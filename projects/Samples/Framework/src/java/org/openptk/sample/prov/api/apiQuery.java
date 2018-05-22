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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openptk.api.Query;
import org.openptk.exception.QueryException;
import org.openptk.spi.JdbcQueryConverter;
import org.openptk.spi.LdapQueryConverter;
import org.openptk.spi.QueryConverterIF;

//===================================================================
public class apiQuery
//===================================================================
{
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      apiQuery test = new apiQuery();
      try
      {
         test.run();
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
      return;
   }

   //----------------------------------------------------------------
   private void run() throws Exception
   //----------------------------------------------------------------
   {
      String filter = null;
      List<Query> qlist = null;
      Query qEmpty = null;
      Query qParamType = null;
      Query qParamAll = null;
      Query qAnd = null;
      Query qOr = null;
      Query qComplex = null;

      QueryConverterIF converter = null;

      // Query that is empty

      qEmpty = new Query();
      qEmpty.setUseNamespace(true);
      qEmpty.setType(Query.Type.BEGINS);
      qEmpty.setName("firstname");
      qEmpty.setValue("Rob");

      // Query with the "type" specified

      qParamType = new Query(Query.Type.NE);
      qParamType.setName("location");
      qParamType.setValue("Dallas");

      // Query with everything specified

      qParamAll = new Query(Query.Type.CONTAINS, "lastname", "Jone");
      try
      {
         // Query with a single sub query
         qAnd = new Query(Query.Type.AND, qEmpty);
      }
      catch (QueryException ex)
      {
         Logger.getLogger(apiQuery.class.getName()).log(Level.SEVERE, null, ex);
      }
      qAnd.setUseNamespace(true);
      qAnd.addQuery(qParamType);

      // Query with List of Queries

      qlist = new LinkedList<Query>();
      qlist.add(qEmpty);
//      qlist.add(qParamType);
      qlist.add(qParamAll);

      qOr = new Query(Query.Type.OR, qlist);

      // WebJOLT example query

      Query orOne = new Query(Query.Type.OR);
      orOne.addQuery(new Query(Query.Type.CONTAINS, "firstname", "Scott"));
      orOne.addQuery(new Query(Query.Type.CONTAINS, "lastname", "Scott"));
      orOne.addQuery(new Query(Query.Type.NE, "nickname", "Scotty"));

      Query orTwo = new Query(Query.Type.OR);
      orTwo.addQuery(new Query(Query.Type.CONTAINS, "firstname", "Ander"));
      orTwo.addQuery(new Query(Query.Type.CONTAINS, "lastname", "Ander"));
      orTwo.addQuery(new Query(Query.Type.NE, "nickname", "Andy"));

      qComplex = new Query(Query.Type.AND);
      qComplex.addQuery(orOne);
      qComplex.addQuery(orTwo);

      System.out.println("\nqEmpty:\n" + qEmpty.toString() + "qEmpty:\n" + qEmpty.toXML() + "\n");
      System.out.println("\nqParamType:\n" + qParamType.toString() + "qParamType:\n" + qParamType.toXML() + "\n");
      System.out.println("\nqParamAll:\n" + qParamAll.toString() + "qParamAll:\n" + qParamAll.toXML() + "\n");
      System.out.println("\nqAnd:\n" + qAnd.toString() + "qAnd:\n" + qAnd.toXML() + "\n");
      System.out.println("\nqOr:\n" + qOr.toString() + "qOr:\n" + qOr.toXML() + "\n");
      System.out.println("\nqComplex:\n" + qComplex.toString() + "qComplex:\n" + qComplex.toXML() + "\n");

//      converter = new LdapQueryConverter();
      converter = new JdbcQueryConverter();

      converter.setQuery(qEmpty);

      filter = (String) converter.convert();

      System.out.println("\nFilter [qEmpty]: " + filter + "\n");

      converter.setQuery(qAnd);

      filter = (String) converter.convert();

      System.out.println("\nFilter [qAnd]: " + filter + "\n");

      converter.setQuery(qOr);

      filter = (String) converter.convert();

      System.out.println("\nFilter [qOr]: " + filter + "\n");

      converter.setQuery(qComplex);

      filter = (String) converter.convert();

      System.out.println("\nFilter [qComplex]: " + filter + "\n");

   }
}
