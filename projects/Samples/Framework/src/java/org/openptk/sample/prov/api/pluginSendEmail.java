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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.sample.prov.api;

import org.openptk.config.Configuration;
import org.openptk.plugin.PluginIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

//===================================================================
class pluginSendEmail extends apiTest
//===================================================================
{
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      pluginSendEmail test = new pluginSendEmail();

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
      StringBuilder body = new StringBuilder();
      String[] to = {"sfehrman@identric.com","scott.fehrman@example.com"};
      Configuration config = null;
      PluginIF plugin = null;
      StructureIF structIn = null;
      StructureIF structOut = null;

      config = new Configuration(this.CONFIG, _props);

      plugin = config.getPlugin("sendemail");

      if (plugin == null)
      {
         throw new Exception("plugin is null");
      }
      
      body.append("<b>Hello</b>" +
         "<p>Welcome to Project OpenPTK<br>" +
         "Thank you for registering</p>" +
         "<hr><h2>http://openptk.dev.java.net</h2>" +
         "<br><p><i>This is only a test</i></p>");

      structIn = new BasicStructure("email");
      structIn.addChild(new BasicStructure(StructureIF.NAME_TO, to));
      structIn.addChild(new BasicStructure(StructureIF.NAME_FROM, "donotreply@openptk.org"));
      structIn.addChild(new BasicStructure(StructureIF.NAME_SUBJECT, "Testing Send Email Plugin"));
      structIn.addChild(new BasicStructure(StructureIF.NAME_BODY, body.toString()));

      structOut = plugin.execute(structIn);

      System.out.println("structOut: " + structOut.getState().toString() + ", " + structOut.toString());

      return;
   }
}
