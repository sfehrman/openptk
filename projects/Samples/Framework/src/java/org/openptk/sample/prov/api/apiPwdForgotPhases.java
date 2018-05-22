/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2011 Project OpenPTK
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
import org.openptk.common.BasicAttr;
import org.openptk.config.Configuration;
import org.openptk.common.Operation;
import org.openptk.definition.DefinitionIF;
import org.openptk.definition.SubjectIF;

//===================================================================
class apiPwdForgotPhases extends apiTest
//===================================================================
{
   private static final String EOL = "\n";

   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      apiPwdForgotPhases test = new apiPwdForgotPhases();

      try
      {
         test.run();
      }
      catch (Exception ex)
      {
         System.out.println(ex.getMessage());
      }

      return;
   }

   //----------------------------------------------------------------
   public void run() throws Exception
   //----------------------------------------------------------------
   {
      String[] forgotQuestions = null;
      String[] forgotValues = null;
      String[] forgotAnswers = null;
      String user = null;
      Configuration config = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;
      AttributeIF attrValues = null;
      AttributeIF attrAnswers = null;

      config = new Configuration(this.CONFIG, _props);

      subject = config.getSubject(this.CONTEXT);

      /*
       * name=forgottenPasswordQuestions,
       * value=[What is your favorite color?,
       *        What is your mother's maiden name?,
       *        What is the city of your birth?]
       *
       */

      forgotQuestions = new String[] // stored in the repository
         {
            "What is your favorite color?",
            "What is your mother's maiden name?",
            "What is the city of your birth?"
         };

      forgotValues = new String[] // stored in the repository
         {
            "blue",
            "smith",
            "st louis"
         };

      forgotAnswers = new String[] // user input
         {
            "blue",
            "smith",
            "St. Louis"
         };

      user = "ja1324";

      /*
       * Set the questions and answers
       */

      input = new Input();
      input.setUniqueId(user);
      input.addAttribute(DefinitionIF.ATTR_PWD_FORGOT_QUESTIONS, forgotQuestions);
      input.addAttribute(DefinitionIF.ATTR_PWD_FORGOT_ANSWERS, forgotValues); // use "Answers" name

      output = subject.execute(Operation.UPDATE, input);

      if (output != null)
      {
         System.out.println("output=" + output.getStatus()
            + ", State=" + output.getStateAsString());
      }
      else
      {
         System.out.println("UPDATE: Output is null");
      }

      /*
       * Read the questions and answers
       */

      input = new Input();
      input.setUniqueId(user);
      input.addAttribute(DefinitionIF.ATTR_PWD_FORGOT_QUESTIONS, forgotQuestions);
      input.addAttribute(DefinitionIF.ATTR_PWD_FORGOT_ANSWERS, forgotAnswers);

      output = subject.execute(Operation.READ, input);

      if (output != null)
      {
         this.printOutput(output);
      }
      else
      {
         System.out.println("READ: Output is null");
      }

      attrAnswers = output.getResults().get(0).getAttribute(DefinitionIF.ATTR_PWD_FORGOT_ANSWERS);

      /*
       * The "data" returned from the READ operation (after the UPDATE) is encrypted
       * The name of the returned attribute is "forgottenPasswordAnswers"
       *
       * The PWDFORGOT operation uses the attribute name "forgottenPasswordAnswers" to
       * represent the end-user input (response to the questions)
       * The "stored" attribute needs to be names "forgottenPasswordValues"
       * We also need to "flag it" as encrypted.
       *
       */
      
      attrValues = new BasicAttr(DefinitionIF.ATTR_PWD_FORGOT_VALUES);
      attrValues.setEncrypted(true);
      if ( attrAnswers.isMultivalued())
      {
         attrValues.setValue((String[])attrAnswers.getValue());
      }
      else
      {
         attrValues.setValue((String)attrAnswers.getValue());
      }

      /*
       * compare the answers (from the read) with the user's values
       */

      input = new Input();
      input.setUniqueId(user);
//      input.addAttribute(DefinitionIF.ATTR_PWD_FORGOT_QUESTIONS, forgotQuestions);
      input.addAttribute(attrValues); // stored
      input.addAttribute(DefinitionIF.ATTR_PWD_FORGOT_ANSWERS, forgotAnswers); // user input

      output = subject.execute(Operation.PWDFORGOT, input);

      if (output != null)
      {
         System.out.println("PWDFORGOT: " + output.getStatus() + ", " + output.getStateAsString());
      }
      else
      {
         System.out.println("PWDFORGOT: Output is null");
      }

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
               System.out.println("\n"
                  + "uniqueId=" + result.getUniqueId().toString() + "\n" + "Attributes:");
               names = result.getAttributeNames();
               for (int i = 0; i < names.length; i++)
               {
                  attr = null;
                  attr = result.getAttribute(names[i]);
                  if (attr != null)
                  {
                     buf.append("name=").append(attr.getName());
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
