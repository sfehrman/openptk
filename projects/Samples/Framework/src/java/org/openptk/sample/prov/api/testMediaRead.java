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
package org.openptk.sample.prov.api;

import java.io.FileOutputStream;

import org.openptk.api.AttributeIF;
import org.openptk.api.DataType;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.common.Operation;
import org.openptk.config.Configuration;
import org.openptk.definition.SubjectIF;
import org.openptk.util.Digest;

//===================================================================
class testMediaRead extends apiTest
//===================================================================
{
   private static final String ATTR_LENGTH = "length";
   private static final String ATTR_DATA = "data";
   private static final String ATTR_MODIFIED = "modified";
   private static final String ATTR_DIGEST = "digest";

   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      testMediaRead test = new testMediaRead();

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
      String contextId = "Media-Embed-JDBC";
      String subjectId = "ja1324";
      String relationshipId = "photo";
      Configuration config = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;

      config = new Configuration(this.CONFIG, _props);

      subject = config.getSubject(contextId);

      input = new Input();
      input.setUniqueId(contextId + "-" + subjectId + "-" + relationshipId);
      input.addAttribute(ATTR_LENGTH);
      input.addAttribute(ATTR_DATA);
      input.addAttribute(ATTR_MODIFIED);
      input.addAttribute(ATTR_DIGEST);

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
      String fileName = "/var/tmp/photo-in-mysql-read.png";
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

      attr = result.getAttribute(ATTR_DATA);
      if (attr == null)
      {
         throw new Exception("Attribute '" + ATTR_DATA + "' is null");
      }

      value = attr.getValue();
      if (value == null)
      {
         throw new Exception("Value for '" + ATTR_DATA + "' is null");
      }

      if (value instanceof byte[])
      {
         bytes = (byte[]) value;
         fos.write(bytes);
         fos.close();
         System.out.println("File created: " + fileName);
      }

      /*
       * Get the size
       */

      attr = result.getAttribute(ATTR_LENGTH);
      if (attr == null)
      {
         throw new Exception("Attribute '" + ATTR_LENGTH + "' is null");
      }

      value = attr.getValue();
      if (value == null)
      {
         throw new Exception("Value for '" + ATTR_LENGTH + "' is null");
      }

      if (attr.getType() == DataType.INTEGER)
      {
         if (bytes.length == ((Integer) value).intValue())
         {
            System.out.println("File length matches: " + bytes.length + " bytes");
         }
         else
         {
            throw new Exception("byte[] length does not match length attribute");
         }
      }
      else
      {
         throw new Exception("Attribute '" + ATTR_LENGTH + "' is not an integer");
      }

      /*
       * Get the digest
       */

      attr = result.getAttribute(ATTR_DIGEST);
      if (attr == null)
      {
         throw new Exception("Attribute '" + ATTR_DIGEST + "' is null");
      }

      value = attr.getValue();
      if (value == null)
      {
         throw new Exception("Value for '" + ATTR_DIGEST + "' is null");
      }

      if (attr.getType() == DataType.STRING)
      {
         if (Digest.validate((String) value, bytes))
         {
            System.out.println("Digest matches: '" + (String) value + "'");
         }
         else
         {
            throw new Exception("digest does NOT match");
         }
      }
      else
      {
         throw new Exception("Attribute '" + ATTR_DIGEST + "' is not a string");
      }


      return;
   }
}
