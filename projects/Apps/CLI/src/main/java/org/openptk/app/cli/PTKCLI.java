/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2010 Sun Microsystems, Inc.
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

package org.openptk.app.cli;

//===================================================================
public class PTKCLI
//===================================================================
{
   private static String EOL = "\n";
   private PTKEnvironment _ptkEnv = null;   // OpenPTK Environment class


   /**
    * @param args
    */
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      PTKCLI cli = new PTKCLI();

      cli.init(args);

      /*
       * Figure out which action to run
       */

      switch (cli._ptkEnv._cliMode)
      {
         case console:
            PTKConsole console = new PTKConsole(cli._ptkEnv);

            console.run();
            break;
         default:
            cli.displayUsage();
      }
   }

   //----------------------------------------------------------------
   private void init(String[] args)
   // Initialize the context
   //----------------------------------------------------------------
   {
      try
      {
         _ptkEnv = new PTKEnvironment(args);
      }
      catch (Exception ex)
      {
         System.err.println(ex.getMessage());
         displayUsage();
      }
      return;
   }

   //----------------------------------------------------------------
   private void displayUsage()
   //----------------------------------------------------------------
   {
      System.err.println(
         EOL +
         "Usage: openptk [action]" + EOL +
         EOL +
         "The accepted values for action are:" + EOL +
         EOL +
         "console   Default - OpenPTK Interactive Console" + EOL);

      System.exit(0);
   }
}
