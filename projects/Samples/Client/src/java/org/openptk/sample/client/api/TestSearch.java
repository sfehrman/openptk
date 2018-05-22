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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.sample.client.api;

import java.util.Iterator;
import java.util.List;

import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.Query;
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
public class TestSearch
//===================================================================
{

   /**
    * @param args the command line arguments
    */
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      TestSearch test = new TestSearch();

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

      setup = new Setup("openptk_client");

      userId = "ja1324"; // "ja1324"
      userPwd = "Passw0rd"; // "Passw0rd"
      connection = setup.getConnection(userId, userPwd);
//      connection = setup.getConnection();

      input = new Input();
      input.setQuery(new Query(Query.Type.NOOPERATOR, StructureIF.NAME_SEARCH, "client")); // "john"
      input.setProperty(StructureIF.NAME_QUANTITY, "100");

      output = connection.execute(Opcode.SEARCH, input);

      if (output != null)
      {
         processOutput(output);
      }

//      connection.close();

      return;
   }

   //----------------------------------------------------------------
   private void processOutput(Output output)
   //----------------------------------------------------------------
   {
      List<ElementIF> list = null;
      Iterator<ElementIF> iter = null;
      ElementIF result = null;
      AttributeIF attr = null;
      String[] names = null;
      StringBuffer buf = new StringBuffer();

      if (output != null)
      {
         System.out.println("search output: " + output.getStatus() + "\n");

         if (output.getResultsSize() > 0)
         {
            list = output.getResults();
            iter = list.iterator();

            while (iter.hasNext())
            {
               result = iter.next();

               if (result != null)
               {
                  buf.append(result.getUniqueId().toString() + ": ");
                  names = result.getAttributeNames();
                  for (int i = 0; i < names.length; i++)
                  {
                     attr = result.getAttribute(names[i]);
                     if (attr != null)
                     {
                        buf.append(attr.getName() + "=" + attr.getValue() + ", ");
                     }
                  }
                  buf.append("\n");
               }
            }
            buf.append("\n");
         }
         else
         {
            System.out.println("no elements returned.");
         }

         System.out.println(buf.toString());
      }
      return;
   }
}
