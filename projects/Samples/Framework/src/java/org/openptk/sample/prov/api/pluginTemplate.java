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
class pluginTemplate extends apiTest
//===================================================================
{
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      pluginTemplate test = new pluginTemplate();

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
      Configuration config = null;
      PluginIF plugin = null;
      StructureIF structIn = null;
      StructureIF structAttrs = null;
      StructureIF structOut = null;

      config = new Configuration(this.CONFIG, _props);

      plugin = config.getPlugin("template");

      if (plugin == null)
      {
         throw new Exception("plugin is null");
      }
      
      structIn = new BasicStructure(StructureIF.NAME_REQUEST);
      structIn.addChild(new BasicStructure(StructureIF.NAME_DOCUMENT, "EmailTemplate-AccountCreated"));
      structAttrs = new BasicStructure(StructureIF.NAME_ATTRIBUTES);
      structAttrs.addChild(new BasicStructure("uniqueid", "fbar"));
      structAttrs.addChild(new BasicStructure("firstname", "Foo"));
      structAttrs.addChild(new BasicStructure("lastname", "Bar"));
      structAttrs.addChild(new BasicStructure("email", "foo.bar@openptk.org"));
      structIn.addChild(structAttrs);

      structOut = plugin.execute(structIn);

      System.out.println("structOut: " + structOut.getState().toString() + ", " + structOut.toString());

      return;
   }
}
