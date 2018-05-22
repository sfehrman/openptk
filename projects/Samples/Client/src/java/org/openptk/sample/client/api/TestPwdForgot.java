/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009 Sun Microsystems, Inc.
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
package org.openptk.sample.client.api;

import org.openptk.api.AttributeIF;
import org.openptk.api.DataType;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.Opcode;
import org.openptk.connection.ConnectionIF;
import org.openptk.connection.Setup;
import org.openptk.connection.SetupIF;
import org.openptk.logging.Logger;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class TestPwdForgot
//===================================================================
{

   /**
    * @param args the command line arguments
    */
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      TestPwdForgot test = new TestPwdForgot();

      try
      {
         test.run();
      }
      catch (Exception ex)
      {
         Logger.logError(ex.getMessage());
      }

      return;
   }

   //----------------------------------------------------------------
   private void run() throws Exception
   //----------------------------------------------------------------
   {
      String userId = null;
      String userPwd = null;
      SetupIF setup = null;
      ConnectionIF connection = null;
      Input input = null;
      Output output = null;
      AttributeIF attr = null;

      setup = new Setup("openptk_client");

      userId = "ja1324"; // "ja1324"
      userPwd = "Passw0rd"; // "Passw0rd"
      connection = setup.getConnection(userId, userPwd);

      /*
       * Phase 1: Get the questions
       */

      System.out.println("\nPhase 1: Get the Questions\n");

      input = new Input();
      input.setUniqueId("ja1324");
      input.setProperty(StructureIF.NAME_MODE, StructureIF.NAME_QUESTIONS);

      output = connection.execute(Opcode.PWDFORGOT, input);

      if (output != null)
      {
         attr = this.processOutput(output);
      }

      /*
       * Phase 2: Put the questions and answer
       */

      System.out.println("\nPhase 2: Put the Questions and Answers\n");

      input = new Input();
      input.setUniqueId("ja1324");
      input.setProperty(StructureIF.NAME_MODE, StructureIF.NAME_ANSWERS);
      input.addAttribute(attr);

      /*
       * OIM 11g default questions
       * "What is your favorite color?"
       * "What is your mother's maiden name?"
       * "What is the city of your birth?"
       */
      
      input.addAttribute("forgottenPasswordAnswers",
         new String[]
         {
            "smith",   // Q1: smith
            "chicago", // Q2: chicago
            "1234"     // Q3: 1234
         });

      output = connection.execute(Opcode.PWDFORGOT, input);

      if (output != null)
      {
         attr = this.processOutput(output);
      }

      /*
       * Phase 3: Change the password
       */

      System.out.println("\nPhase 3: Put the new password\n");

      input = new Input();
      input.setUniqueId("ja1324");
      input.setProperty(StructureIF.NAME_MODE, StructureIF.NAME_CHANGE);
      input.addAttribute("password", "Passw0rd");

      output = connection.execute(Opcode.PWDFORGOT, input);

      if (output != null)
      {
         attr = this.processOutput(output);
      }

      connection.close();

      return;
   }

   //----------------------------------------------------------------
   private AttributeIF processOutput(Output output)
   //----------------------------------------------------------------
   {
      Object obj = null;
      boolean bMultivalued = false;
      DataType type = DataType.STRING;
      ElementIF result = null;
      AttributeIF attr = null;
      AttributeIF questions = null;
      String[] names = null;
      String[] values = null;
      StringBuffer buf = new StringBuffer();

      if (output != null)
      {
         System.out.println("read output: " + output.getStatus());
         if (output.getResultsSize() == 1)
         {
            result = output.getResults().get(0);
            if (result != null)
            {
               System.out.println("\n" +
                  "uniqueId=" + result.getUniqueId().toString() + "\n" + "Attributes:");
               names = result.getAttributeNames();
               for (int i = 0; i < names.length; i++)
               {
                  attr = null;
                  attr = result.getAttribute(names[i]);
                  if (attr != null)
                  {
                     if (attr.getName().equalsIgnoreCase("forgottenPasswordQuestions"))
                     {
                        questions = attr;
                     }

                     buf.append("name=" + attr.getName());
                     type = attr.getType();
                     obj = attr.getValue();
                     bMultivalued = attr.isMultivalued();
                     if (obj != null)
                     {
                        buf.append(", value=");
                        switch (type)
                        {
                           case STRING:
                              if (bMultivalued)
                              {
                                 buf.append("[");
                                 values = (String[]) obj;
                                 for (int j = 0; j < values.length; j++)
                                 {
                                    buf.append(values[j]);
                                    if (j < (values.length - 1))
                                    {
                                       buf.append(", ");
                                    }
                                 }
                                 buf.append("]");
                              }
                              else
                              {
                                 buf.append((String) obj);
                              }
                              break;
                           default:
                           {
                              buf.append(attr.getValueAsString());
                              break;
                           }
                        }
                     }
                  }
                  buf.append("\n");
               }
               System.out.println(buf);
            }
         }
      }
      else
      {
         System.out.print("Output is null\n");
      }

      return questions;
   }
}
