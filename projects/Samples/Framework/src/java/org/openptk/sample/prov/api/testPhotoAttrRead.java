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

import java.io.FileOutputStream;

import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.common.Operation;
import org.openptk.config.Configuration;
import org.openptk.definition.SubjectIF;

//===================================================================
class testPhotoAttrRead extends apiTest
//===================================================================
{

   private static final String ATTR_PHOTO = "photo";

   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      testPhotoAttrRead test = new testPhotoAttrRead();

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
      String contextId = "Employees-OpenDS-JNDI";
      Configuration config = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;

      config = new Configuration(this.CONFIG, _props);

      subject = config.getSubject(contextId);

      input = new Input();
      input.setUniqueId("jphoto");
      input.addAttribute(ATTR_PHOTO);

      output = subject.execute(Operation.READ, input);

      this.processRead(output);

      return;
   }

   //----------------------------------------------------------------
   private void processRead(Output output) throws Exception
   //----------------------------------------------------------------
   {
      byte[] bytes = null;
      Object value = null;
      String fileName = "/Users/sfehrman/Desktop/photo-attr-read.png";
      FileOutputStream fos = new FileOutputStream(fileName);
      ElementIF result = null;
      AttributeIF attr = null;

      if (output == null)
      {
         throw new Exception("Output is null");
      }

      System.out.println("READ status: " + output.getStatus());

      if (output.getResultsSize() != 1)
      {
         throw new Exception("A single record not returned, size=" + output.getResultsSize());
      }

      result = output.getResults().get(0);
      if (result == null)
      {
         throw new Exception("Result Element is null");
      }

      /*
       * Get the data (byte[])
       */

      attr = result.getAttribute(ATTR_PHOTO);
      if (attr == null)
      {
         throw new Exception("Attribute '" + ATTR_PHOTO + "' is null");
      }

      value = attr.getValue();
      if (value == null)
      {
         throw new Exception("Value for '" + ATTR_PHOTO + "' is null");
      }

      if (value instanceof byte[])
      {
         bytes = (byte[]) value;
         fos.write(bytes);
         fos.close();
         System.out.println("File created: " + fileName);
      }

      return;
   }
}
