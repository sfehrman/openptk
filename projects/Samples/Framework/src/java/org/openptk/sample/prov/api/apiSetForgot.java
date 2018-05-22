/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007 Sun Microsystems, Inc.
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
package org.openptk.sample.prov.api;

import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.config.Configuration;
import org.openptk.common.Operation;
import org.openptk.definition.SubjectIF;
import org.openptk.exception.ConfigurationException;

//===================================================================
class apiSetForgot extends apiTest
//===================================================================
{
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      apiSetForgot test = new apiSetForgot();
      test.run();
      return;
   }

   //----------------------------------------------------------------
   public void run()
   //----------------------------------------------------------------
   {
      Configuration config = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;

      try
      {
         config = new Configuration(this.CONFIG, _props);
      }
      catch (ConfigurationException ex)
      {
         System.out.println("new Configuration(): " + ex.getMessage());
         return;
      }

      try
      {
         subject = config.getSubject(this.CONTEXT);
      }
      catch (ConfigurationException ex)
      {
         System.out.println("config.getSubject(): " + ex.getMessage());
         return;
      }

      input = new Input();

      input.setUniqueId("sjohnson"); // required

      input.addAttribute("forgottenPasswordQuestions",
         new String[]
         {
            "What is your favorite color?",
            "What is your mother's maiden name?",
            "What is the city of your birth?"
         });

      input.addAttribute("forgottenPasswordAnswers",
         new String[]
         {
            "blue",
            "smith",
            "chicago"
         });

//      input.addAttribute("forgottenPasswordQuestions","Mothers Maden Name");
//
//      input.addAttribute("forgottenPasswordAnswers","Jones");

      try
      {
         output = subject.execute(Operation.UPDATE, input);
      }
      catch (Exception ex)
      {
         System.out.println("subject.doUpdate(): " + ex.getMessage());
      }

      if (output != null)
      {
         System.out.println("update output: " + output.getStatus());
      }
      return;
   }
}
