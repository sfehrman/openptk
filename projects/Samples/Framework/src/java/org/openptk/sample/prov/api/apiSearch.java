/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2010 Sun Microsystems, Inc.
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

import java.util.Iterator;
import java.util.List;

import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.Query;
import org.openptk.config.Configuration;
import org.openptk.common.Operation;
import org.openptk.definition.SubjectIF;
import org.openptk.exception.ConfigurationException;

//===================================================================
class apiSearch extends apiTest
//===================================================================
{
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      apiSearch test = new apiSearch();
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
      Query query = null;
      Query query2 = null;
      Query query3 = null;
      Query query4 = null;

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

     query = new Query(Query.Type.CONTAINS, "firstname", "john");
//     query2 = new Query(Query.Type.EQ, "objectclass", "speperson");
//     query3 = new Query(Query.Type.AND);

//      query3.addQuery(query);
//      query3.addQuery(query2);

      query = new Query(Query.Type.AND);

      query2 = new Query(Query.Type.OR);
      query2.addQuery(new Query(Query.Type.CONTAINS, "firstname", "John"));
      query2.addQuery(new Query(Query.Type.CONTAINS, "lastname", "John"));
      query2.addQuery(new Query(Query.Type.CONTAINS, "uniqueid", "John"));
      query2.addQuery(new Query(Query.Type.CONTAINS, "email", "John"));

      query3 = new Query(Query.Type.OR);
      query3.addQuery(new Query(Query.Type.CONTAINS, "firstname", "Mc"));
      query3.addQuery(new Query(Query.Type.CONTAINS, "lastname", "Mc"));
      query3.addQuery(new Query(Query.Type.CONTAINS, "uniqueid", "Mc"));
      query3.addQuery(new Query(Query.Type.CONTAINS, "email", "Mc"));

      query.addQuery(query2);
      query.addQuery(query3);

//      query2 = new Query(Query.Type.OR);
//      query2.addQuery(query);
//      query = new Query(Query.Type.EQ, "firstname", "Scott");
//      query = new Query(Query.Type.EQ, "firstname", "randy"); 
//      query = new Query(Query.Type.BEGINS, "firstname", "D");
//      query = new Query(Query.Type.CONTAINS, "firstname", "amp"); 



      input = new Input();

      input.addAttribute("firstname");
      input.addAttribute("lastname");
      input.addAttribute("email");
      //input.addAttribute("fullname");
//      input.addAttribute("telephone");
//      input.addAttribute("title");
//      input.addAttribute("roles");
//      input.addAttribute("organization");
//      input.addAttribute("org");
//      input.addAttribute("access");
//      input.addAttribute("accountnumber");
//      input.addAttribute("lastcommafirst");

      /*  For SPML SUN SPE testing, an additional query MUST be supplied
      
      try
      {
      output = subject.doSearch(input);
      }
      catch (ProvisionException ex)
      {
      System.out.println("subject.doSearch(): " + ex.getMessage());
      return;
      }
      
      this.processOutput(output);
       */

      input.setQuery(query2);

      try
      {
         output = subject.execute(Operation.SEARCH, input);
      }
      catch (Exception ex)
      {
         System.out.println("EXCEPTION: " + ex.getMessage());
         return;
      }

      this.processOutput(output);

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
         System.out.println("State=" + output.getStateAsString() + ", Status='"
            + output.getStatus() + "'\n");

         if (output.getResultsSize() > 0)
         {
            list = output.getResults();
            iter = list.iterator();

            while (iter.hasNext())
            {
               result = iter.next();

               if (result != null)
               {
                  buf.append(result.getUniqueId().toString()).append(": ");
                  names = result.getAttributeNames();
                  for (int i = 0; i < names.length; i++)
                  {
                     attr = result.getAttribute(names[i]);
                     if (attr != null)
                     {
                        buf.append(attr.getName()).append("=").append(attr.getValueAsString()).append(", ");
                     }
                  }
                  buf.append("\n");
               }
            }
            buf.append("\n");
            System.out.println(buf);
         }
         else
         {
            System.out.println("no elements returned");
         }
      }
      return;
   }
}
