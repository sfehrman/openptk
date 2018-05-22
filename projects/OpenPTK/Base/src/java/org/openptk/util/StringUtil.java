/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010-2011 Project OpenPTK
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

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Scott Fehrman
 */
//===================================================================
public final class StringUtil
//===================================================================
{
   public static final String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
   public static final String ALPHA_NUM_SPACE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ";
   public static final String BASIC_WEB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz -_:;/%.+?&=,@";
   public static final String BASIC_JDBC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz -_:/@.=,|^";

   //----------------------------------------------------------------
   private StringUtil()
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
      String in = " D/RT\"D\\TH s%20g s'dg sd'fg@#$3<}{>}{__:;";
      String out = null;

      out = StringUtil.clean(StringUtil.ALPHA_NUM_SPACE, in);

      System.out.println(" input: '" + in + "'");
      System.out.println("output: '" + out + "'");
   }

   /**
    * Clean the "input" String so that is only contains the characters
    * specified.
    *
    * @param characters String of characters that are allowed
    * @param in String containing the input data
    * @return output String after cleaning
    */
   //----------------------------------------------------------------
   public static synchronized String clean(final String characters, final String in)
   //----------------------------------------------------------------
   {
      int offset = 0;
      char[] chars = null;
      String out = null;
      StringBuilder buf = new StringBuilder();

      if (characters != null && in != null)
      {
         chars = in.toCharArray();
         for (char c : chars)
         {
            offset = characters.indexOf(c);
            if (offset >= 0)
            {
               buf.append(c);
            }
         }
         out = buf.toString();
      }

      return out;
   }

   /**
    * Converts the String Array to a String.
    * The returned String will begin with the "[" character.
    * Each array item will start and end with a single quote "'".
    * If there is more than one String in the array, a comma "," is placed
    * between items.  The String will end with the "]" character.
    * 
    * @param array
    * @return String
    */
   //----------------------------------------------------------------
   public static synchronized String arrayToString(final String[] array)
   //----------------------------------------------------------------
   {
      String str = null;
      StringBuilder buf = new StringBuilder();

      if (array != null)
      {
         buf.append("[");
         for (int i = 0; i < array.length; i++)
         {
            str = array[i];
            buf.append("'").append(str).append("'");
            if (i < array.length - 1)
            {
               buf.append(",");
            }
         }
         buf.append("]");
      }
      return buf.toString();
   }

   /**
    *
    * Process the String into a String Array
    * Not using String.split because we want to handle "empty/blank" sub-strings
    * The array will not contain empty strings, they will be skipped
    * The returned String Array will always be non-null, may have zero elements.
    *
    * INPUT = "/hello/world//today/is/great" and TOKEN = "/"
    *
    * converts to: [ "hello", "world", "today", "is", "great" ]
    *      NOT to: [ "", "hello", "world", "", "today", "is", "great" ]
    *
    * @param string The String that will be parsed
    * @param token The String that is used to separate / split the string into values
    * @return String[] containing the parsed Strings from the input String
    */
   //----------------------------------------------------------------
   public static synchronized String[] stringToArray(final String string, final String token)
   //----------------------------------------------------------------
   {
      String value = null;
      String[] array = null;
      StringTokenizer tokenizer = null;
      List<String> list = null;

      if (string != null && string.length() > 0 && token != null && token.length() > 0)
      {
         tokenizer = new StringTokenizer(string, token);
         if (tokenizer != null)
         {
            list = new LinkedList<String>();

            while (tokenizer.hasMoreTokens())
            {
               value = tokenizer.nextToken();
               if (value != null && value.length() > 0)
               {
                  list.add(value);
               }
            }

            array = list.toArray(new String[list.size()]);
         }
      }

      if (array == null)
      {
         array = new String[0];
      }

      return array;
   }
}
