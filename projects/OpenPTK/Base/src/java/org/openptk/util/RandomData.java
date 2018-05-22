/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009-2010 Sun Microsystems, Inc.
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * trunk/openptk/resource/legal-notices/OpenPTK.LICENSE
 * or https://openptk.dev.java.net/OpenPTK.LICENSE.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the reference to
 * trunk/openptk/resource/legal-notices/OpenPTK.LICENSE. If applicable,
 * add the following below this CDDL HEADER, with the fields enclosed
 * by brackets "[]" replaced with your own identifying information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.util;

import java.util.Properties;
import java.util.Random;

/**
 * Methods that generate random data.
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class RandomData
//===================================================================
{
   public static final int DEFAULT_SIZE = 8;
   public static final String PROP_SIZE = "size";
   public static final String PROP_TYPE = "type";
   public static final String PROP_CASE = "case";
   public static final String PROP_CHAR_SET = "charset";
   public static final String CASE_UPPER = "upper";
   public static final String CASE_LOWER = "lower";
   public static final String CASE_BOTH = "both";
   public static final String TYPE_ALPHA = "alpha";
   public static final String TYPE_NUMERIC = "numeric";
   public static final String TYPE_BOTH = "both";
   public static final String DEFAULT_CASE = "both";
   public static final String DEFAULT_TYPE = "both";

   //----------------------------------------------------------------
   private RandomData()
   //----------------------------------------------------------------
   {
      /*
       * Do not allow this class to be instanciated
       */
      throw new AssertionError();
   }

   /**
    *
    * @param args (not used)
    */
   //----------------------------------------------------------------
   public static void main(String args[])
   //----------------------------------------------------------------
   {
      int len = 0;

      if (args.length == 1)
      {
         try
         {
            len = Integer.parseInt(args[0]);
         }
         catch (NumberFormatException ex)
         {
            System.err.println("Argument is not a number");
            len = 0;
         }

         if (len > 0)
         {
            System.out.println(RandomData.getString(len));
         }
      }
      else
      {
         System.err.println("Must specify the number of characters");
      }

      return;
   }

   /**
    * Generate a random String of the specified length.
    *
    * @param length int length of the random String
    * @return String generated random data
    */
   //----------------------------------------------------------------
   public static synchronized String getString(final int length)
   //----------------------------------------------------------------
   {
      Properties props = new Properties();

      props.put(PROP_SIZE, new Integer(length));

      return getString(props);
   }

   /**
    * Generate a random String based on the specified Properties.
    * 
    * @param props Properties random data specifications
    * @return String generated random data
    */
   //----------------------------------------------------------------
   public static synchronized String getString(final Properties props)
   //----------------------------------------------------------------
   {
      /*
       * Generate a new random String that has the specified length
       * The available characters are "a-z","A-Z", and "0-9" = 62
       * A String should have at least one of each "type"
       * Using a random number, from 0-61, pick characters and add to buffer
       * If buffer contains all of the "types" then return it, else try again
       * For safety reasons, only check so many times for all "types"
       */

      boolean bReqUpper = false;
      boolean bReqLower = false;
      boolean bReqNumber = false;
      boolean bHasUpper = false;
      boolean bHasLower = false;
      boolean bHasNumber = false;
      int length = DEFAULT_SIZE;
      int offset = 0;
      int limit = 16;
      int tries = 0;
      String propCase = DEFAULT_CASE;
      String propType = DEFAULT_TYPE;
      String pwd = null;
      StringBuilder buf = null;
      String lowerString = "abcdefghijklmnopqrstuvwxyz";
      String upperString = lowerString.toUpperCase();
      String numericString = "0123456789";
      String candidateString = "";
      char[] chars =
      {
      };

      Random random = null;

      /*
       * Determine the type of random data being requested
       */

      if (props != null)
      {
         if (props.containsKey(PROP_SIZE))
         {
            length = ((Integer) props.get(PROP_SIZE)).intValue();
         }

         if (props.containsKey(PROP_TYPE))
         {
            propType = props.getProperty(PROP_TYPE);
         }

         if (props.containsKey(PROP_CASE))
         {
            propCase = props.getProperty(PROP_CASE);
         }

         /*
          * If a character set is passed (i.e. set = "abcde"), then set the
          * possible characters generated to that set.  Otherwise, create the
          * set based on what type of data is requested.
          */

         if (props.containsKey(PROP_CHAR_SET))
         {
            chars = props.getProperty(PROP_CHAR_SET).toCharArray();
         }
         else
         {
            if (TYPE_NUMERIC.equalsIgnoreCase(propType)
               || TYPE_BOTH.equalsIgnoreCase(propType))
            {
               bReqNumber = true;
               candidateString += numericString;
            }

            if (TYPE_ALPHA.equalsIgnoreCase(propType)
               || TYPE_BOTH.equalsIgnoreCase(propType))
            {
               if (CASE_UPPER.equalsIgnoreCase(propCase))
               {
                  bReqUpper = true;
                  candidateString += upperString;
               }
               else if (CASE_LOWER.equalsIgnoreCase(propCase))
               {
                  bReqLower = true;
                  candidateString += lowerString;
               }
               else if (CASE_BOTH.equalsIgnoreCase(propCase))
               {
                  bReqUpper = true;
                  bReqLower = true;
                  candidateString += lowerString + upperString;
               }
            }

            chars = candidateString.toCharArray();
         }

      }

      if (length > 0)
      {
         do
         {
            /*
             * reset and try to generate a "good" String
             *
             * The flags below will first define if a number, lower or uppercase
             * character is required at all.
             */

            tries++;
            bHasUpper = !bReqUpper;
            bHasLower = !bReqLower;
            bHasNumber = !bReqNumber;
            buf = new StringBuilder();
            random = new Random();

            for (int i = 0; i < length; i++)
            {
               offset = random.nextInt(chars.length);
               buf.append(chars[offset]);

               if (!bHasLower && lowerString.indexOf(chars[offset]) != -1)
               {
                  bHasLower = true;
               }
               else if (!bHasUpper && upperString.indexOf(chars[offset]) != -1)
               {
                  bHasUpper = true;
               }
               else if (!bHasNumber && numericString.indexOf(chars[offset]) != -1)
               {
                  bHasNumber = true;
               }
            }

            /*
             * Make sure we don't "try forever" ... stop after limit, use last
             */

            if (tries >= limit)
            {
               bReqUpper = false;
               bReqLower = false;
               bReqNumber = false;
            }
         }
         while (!(bHasNumber && bHasUpper && bHasLower));

         pwd = buf.toString();
      }
      else
      {
         pwd = "";
      }

      return pwd;
   }
}
