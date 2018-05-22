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

import org.openptk.crypto.Encryptor;
import org.openptk.exception.CryptoException;

/**
 *
 * @author Derrick Harcey
 */
//===================================================================
public class ptkadmin
//===================================================================
{
   private static final String HELP = "-help";
   private static final String S_HELP = "-h";
   private static final String ENCRYPT = "-encrypt";
   private static final String S_ENCRYPT = "-e";
   private static final String UUID = "-uuid";
   private static final String S_UUID = "-u";
   private static final String RANDOM = "-randomdata";
   private static final String S_RANDOM = "-r";
   private static final String MESSAGE_HELP =
      "Usage:  ptkadmin -encrypt || -e <string> \n"
      + "        ptkadmin -uuid || -u \n"
      + "        ptkadmin -randomdata || -r <#> \n"
      + "        ptkadmin -help || -h\n";
   private static final String MESSAGE_NOT_VALID = "\nInput is not valid \n";
   private static final String MESSAGE_NOT_PROCESSED = "\nProcessing failed \n";

   /**
    *
    * @param args (not used)
    */
   //----------------------------------------------------------------
   public static void main(String args[])
   //----------------------------------------------------------------
   {
      /**
       * Example Usage:  java -classpath openptk-base.jar org.openptk.util.ptkadmin -e helloworld
       *
       */
      boolean isValid = false;
      boolean isComplete = false;

      isValid = validateArgs(args);

      if (isValid)
      {
         isComplete = execute(args);
      }
      else
      {
         System.out.println(MESSAGE_NOT_VALID);
         printHelpMessage();
         System.exit(1);
      }

      if (!isComplete)
      {
         System.out.println(MESSAGE_NOT_PROCESSED);
         printHelpMessage();
         System.exit(1);
      }

      return;
   }

   //----------------------------------------------------------------
   private static boolean validateArgs(final String[] args)
   //----------------------------------------------------------------
   {
      boolean isValidArgs = false;

      if (args.length == 0)
      {
         return isValidArgs;
      }
      else
      {
         String arg = args[0];

         if (!arg.equals(HELP) && !arg.equals(S_HELP)
            && !arg.equals(ENCRYPT) && !arg.equals(S_ENCRYPT)
            && !arg.equals(RANDOM) && !arg.equals(S_RANDOM)
            && !arg.equals(UUID) && !arg.equals(S_UUID))
         {
            System.out.println(MESSAGE_NOT_VALID);
            return isValidArgs;
         }

         // If the input arg is for help, display help message and end

         if (arg.equals(HELP) || arg.equals(S_HELP))
         {
            printHelpMessage();
            System.exit(1);
         }
         else if (arg.equals(ENCRYPT) || arg.equals(S_ENCRYPT))
         {
            if (args.length != 2)
            {
               return isValidArgs;
            }
            else
            {
               isValidArgs = true;
            }
         }
         else if (arg.equals(RANDOM) || arg.equals(S_RANDOM))
         {
            if (args.length != 2)
            {
               return isValidArgs;
            }
            else
            {
               isValidArgs = true;
            }
         }
         else if (arg.equals(UUID) || arg.equals(S_UUID))
         {
            if (args.length != 1)
            {
               return isValidArgs;
            }
            else
            {
               isValidArgs = true;
            }
         }
      }

      return isValidArgs;
   }

   //----------------------------------------------------------------
   private static void printHelpMessage()
   //----------------------------------------------------------------
   {
      System.out.println(MESSAGE_HELP);
      return;
   }

   //----------------------------------------------------------------
   private static boolean execute(final String[] args)
   //----------------------------------------------------------------
   {
      boolean success = false;
      String clear = null;
      String encrypted = null;
      int len = 0;

      if (args[0].equals(S_ENCRYPT) || args[0].equals(ENCRYPT))
      {
         success = true;

         if (args.length > 1)
         {
            clear = args[1];

            try
            {
               encrypted = Encryptor.encrypt(clear);
            }
            catch (CryptoException ex)
            {
               System.err.println(ex.getMessage());
            }

            if (encrypted != null)
            {
               System.out.print(encrypted + "\n");
            }
         }
      }
      else if (args[0].equals(S_RANDOM) || args[0].equals(RANDOM))
      {
         try
         {
            len = Integer.parseInt(args[1]);
         }
         catch (NumberFormatException ex)
         {
            System.err.println("Argument is not a number");
            len = 0;
         }

         if (len > 0)
         {
            System.out.print(RandomData.getString(len) + "\n");
            success = true;
         }
      }
      else if (args[0].equals(S_UUID) || args[0].equals(UUID))
      {
         System.out.print(UniqueId.getUniqueId() + "\n");
         success = true;
      }
      else if (args[0].equals(HELP) || args[0].equals(S_HELP))
      {
         printHelpMessage();
      }

      return success;
   }
}
