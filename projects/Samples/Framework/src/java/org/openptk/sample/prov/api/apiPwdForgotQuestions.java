/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010 Oracle America, Inc.
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

import org.openptk.api.AttributeIF;
import org.openptk.api.DataType;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.config.Configuration;
import org.openptk.common.Operation;
import org.openptk.definition.SubjectIF;
import org.openptk.exception.ConfigurationException;

//===================================================================
class apiPwdForgotQuestions extends apiTest
//===================================================================
{
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      apiPwdForgotQuestions test = new apiPwdForgotQuestions();

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
      AttributeIF attr = null;

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

//      input.setUniqueId("sfehrman"); // REQUIRED
//      input.setUniqueId("eapi4"); // REQUIRED
      input.setUniqueId("sjohnson"); // REQUIRED
      input.addAttribute("firstname");
      input.addAttribute("lastname");
      input.addAttribute("forgottenPasswordQuestions");

      try
      {
         output = subject.execute(Operation.READ, input);
      }
      catch (Exception ex)
      {
         System.out.println("subject.doRead(): " + ex.getMessage());
      }

      this.printOutput(output);

      return;
   }

   //----------------------------------------------------------------
   private void printOutput(Output output)
   //----------------------------------------------------------------
   {
      Object obj = null;
      boolean bMultivalued = false;
      DataType type = DataType.STRING;
      ElementIF result = null;
      AttributeIF attr = null;
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
      return;
   }
}
