/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2008 Sun Microsystems, Inc.
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
package org.openptk.sample.prov.api;

import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.config.Configuration;
import org.openptk.common.Operation;
import org.openptk.definition.SubjectIF;
import org.openptk.exception.ConfigurationException;

//===================================================================
class apiUpdate extends apiTest
//===================================================================
{
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      apiUpdate test = new apiUpdate();
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

//      input.setUniqueId("sfehrman"); // required
      input.setUniqueId("sjohnson"); // required
//      input.setUniqueId("uid=eapi,ou=people,ou=customers,dc=openptk,dc=org"); // REQUIRED

//      input.addAttribute("firstname", "ExampleMod909");
      input.addAttribute("email", "scotty@openptk.org");
//      input.addAttribute("firstname"); // "123-456-7890"

//       IDM User attributes
//        input.addAttribute("roles",new String[]{"Contractor", "Credit"}); // SPML, SPE, JNDI
//        input.addAttribute("roles",""); // SPML, SPE, JNDI
//        input.addAttribute("manager", new Long(13));
//        input.addAttribute("manager", "");
        input.addAttribute("telephone","123-456-7890");
        input.addAttribute("title", "Sr. Engineer");
//        input.addAttribute("organization", "All People");     

//      input.setUniqueId("Users:121"); // required
//       OIM User attributes
//      input.addAttribute("firstname", "ExampleMod92");
//      input.addAttribute("email", "");
//      input.addAttribute("firstname"); // "123-456-7890"

      try
      {
         output = subject.execute(Operation.UPDATE, input);
      }
      catch (Exception ex)
      {
         System.out.println("Exception:  subject.doUpdate(): " + ex.getMessage());
      }

      if (output != null)
      {
         System.out.println("update output: " + output.getStatus());
      }
      return;
   }
}
