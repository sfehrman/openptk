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
class apiRead extends apiTest
//===================================================================
{
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      apiRead test = new apiRead();

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

//      input.setUniqueId("ja1324"); // REQUIRED
//      input.setUniqueId("eapi4"); // REQUIRED
//      input.setUniqueId("jhenry2"); // REQUIRED
//      input.setUniqueId("aa10127"); // REQUIRED
      input.setUniqueId("sjohnson"); // REQUIRED
//      input.setUniqueId("uid=eapi,ou=people,ou=customers,dc=openptk,dc=org"); // REQUIRED (LDAP)
//      input.addAttribute("firstname");
//      input.addAttribute("lastname");
//      input.addAttribute("telephone");
//      input.addAttribute("fullname");
//      input.addAttribute("telephone");
//      input.addAttribute("email");
//      input.addAttribute("roles");
//      input.addAttribute("manager");
//      input.addAttribute("title");
//      input.addAttribute("organization");
//      input.addAttribute("org");
//      input.addAttribute("access");
//      input.addAttribute("accountnumber");

      try
      {
         output = subject.execute(Operation.READ, input);
      }
      catch (Exception ex)
      {
         System.out.println("subject.doRead(): " + ex.getMessage());
      }

//      System.out.println(config.getData( (ElementIF)input ,"apiRead"));
//      System.out.println(config.getData( (ElementIF)output ,"apiRead"));

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
