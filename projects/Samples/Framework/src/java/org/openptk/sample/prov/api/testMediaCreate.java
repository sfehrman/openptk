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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.common.Operation;
import org.openptk.config.Configuration;
import org.openptk.definition.SubjectIF;

//===================================================================
class testMediaCreate extends apiTest
//===================================================================
{
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      testMediaCreate test = new testMediaCreate();

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
      byte[] bytes = null;
      String contextId = "Media-MySQL-JDBC";
      String subjectId = "ja1324";
      String relationshipId = "photo";
      String type = null;
      File file = null;
      InputStream inStream = null;
      Configuration config = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;

      config = new Configuration(this.CONFIG, _props);

      subject = config.getSubject(contextId);

      file = new File("/var/tmp/run-time.png");

      bytes = new byte[(int) file.length()];

      inStream = new FileInputStream(file);
      inStream.read(bytes);

      input = new Input();
      input.setUniqueId(contextId + "-" + subjectId + "-" + relationshipId);
      input.addAttribute("contextid",contextId);
      input.addAttribute("subjectid", subjectId);
      input.addAttribute("relationshipid", relationshipId);
      input.addAttribute("length", (int) file.length());
      input.addAttribute("data", bytes);

      output = subject.execute(Operation.CREATE, input);

      if (output != null)
      {
         System.out.println("create output: " + output.getStatus());
      }
      
      return;
   }
}
