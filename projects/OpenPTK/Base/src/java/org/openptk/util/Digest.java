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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.util;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Generates a digest (SHA) from a String or byte[].
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class Digest
//===================================================================
{
   private static final String PREFIX_DELIM = "::";
   public static final String ALGORITHM_SHA = "SHA";
   public static final String ALGORITHM_DEFAULT = ALGORITHM_SHA;
   public static final String[] ALGORITHM_PREFIXS =
   {
      ALGORITHM_SHA + PREFIX_DELIM
   };

   //----------------------------------------------------------------
   private Digest()
   //----------------------------------------------------------------
   {
      /*
       * Do not allow this class to be instanciated
       */
      throw new AssertionError();
   }

   /**
    * Generate a digest (String) from the byte[].
    *
    * @param bytes byte array used to create the digest
    * @return String the digest
    */
   //----------------------------------------------------------------
   public static synchronized String generate(final byte[] bytes)
   //----------------------------------------------------------------
   {
      String output = null;
      MessageDigest md = null;
      byte[] digest = null;
      BigInteger bi = null;

      if (bytes.length > 0)
      {
         try
         {
            md = MessageDigest.getInstance(ALGORITHM_DEFAULT);
         }
         catch (Exception e)
         {
            md = null;
         }

         if (md != null)
         {
            digest = md.digest(bytes);
            bi = new BigInteger(digest);
            output = md.getAlgorithm() + PREFIX_DELIM + bi.toString(16);
         }
         else
         {
            output = "";
         }
      }
      else
      {
         output = "";
      }

      return output;
   }

   /**
    * Generate a digest (String) from the String.
    *
    * @param str String used to create the digest
    * @return String the digest
    */
   //----------------------------------------------------------------
   public static synchronized String generate(final String str)
   //----------------------------------------------------------------
   {
      String output = null;

      if (str != null && str.length() > 0)
      {
         output = Digest.generate(str.getBytes());
      }

      return output;
   }

   /**
    *
    * @param hashed String containing a hash value
    * @param bytes Byte array that will be used to compare with the hash value
    * @return boolean true if they match
    */
   //----------------------------------------------------------------
   public static synchronized boolean validate(final String hashed, final byte[] bytes)
   //----------------------------------------------------------------
   {
      boolean isValid = false;
      String output = null;

      if (hashed != null && hashed.length() > 0 && bytes.length > 0)
      {
         output = Digest.generate(bytes);
         if (output != null && output.length() > 0)
         {
            /*
             * The "hashed" value may not have a "PREFIX" which would normally be:
             * syntax:  ALGORTHIM + "::"
             * example: "SHA::"
             *
             * If the hashed value does not have a PREFIX ...
             * Remove the PREFIX from the "output" and compare it with the hashed value
             */

            if (hashed.indexOf(ALGORITHM_DEFAULT) == -1)
            {
               output = output.substring(output.indexOf(ALGORITHM_DEFAULT)
                  + ALGORITHM_DEFAULT.length() + PREFIX_DELIM.length());
            }

            if (hashed.equals(output))
            {
               isValid = true;
            }
         }
      }

      return isValid;
   }

   /**
    *
    * @param hashed String containing a hash value
    * @param str String that will be used to compare with the hash value
    * @return boolean true if they match
    */
   //----------------------------------------------------------------
   public static synchronized boolean validate(final String hashed, final String str)
   //----------------------------------------------------------------
   {
      boolean isValid = false;

      if (hashed != null && hashed.length() > 0 && str != null && str.length() > 0)
      {
         isValid = Digest.validate(hashed, str.getBytes());
      }

      return isValid;
   }

   /**
    *
    * @param str String value will be check to see if it is a hashed String
    * @return boolean true if it is a hashed String
    */
   //----------------------------------------------------------------
   public static synchronized boolean isHashed(final String str)
   //----------------------------------------------------------------
   {
      boolean isHashed = false;

      if ( str != null && str.length() > 0)
      {
         for (String prefix : Digest.ALGORITHM_PREFIXS)
         {
            if (str.indexOf(prefix) > -1)
            {
               isHashed = true;
            }
         }
      }

      return isHashed;
   }

   /**
    *
    * @param args (not used)
    */
   //----------------------------------------------------------------
   public static void main(String args[])
   //----------------------------------------------------------------
   {
      boolean isValid = false;
      String text = null;
      String hash = null;

      text = "Hello_World from Project OpenPTK";

      if (text != null && text.length() > 0)
      {
         hash = Digest.generate(text);
         System.out.println("hash='" + hash + "'");

         isValid = Digest.validate(hash, text);
         System.out.println("isValid=" + isValid);

         isValid = Digest.validate("foobar", text);
         System.out.println("isValid=" + isValid);

         System.out.println("Is '"+ hash + "' a 'hashed' value: " + Digest.isHashed(hash));
         System.out.println("Is '"+ text + "' a 'hashed' value: " + Digest.isHashed(text));
      }
      else
      {
         System.err.println("null argument");
      }
      return;
   }
}
