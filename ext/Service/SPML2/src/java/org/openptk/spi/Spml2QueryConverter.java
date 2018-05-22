/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009-2010 Sun Microsystems, Inc.
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
package org.openptk.spi;

import java.util.List;

import org.openptk.api.Query;
import org.openptk.exception.QueryException;

import org.openspml.v2.profiles.dsml.DSMLProfileException;
import org.openspml.v2.profiles.dsml.DSMLValue;
import org.openspml.v2.profiles.dsml.EqualityMatch;
import org.openspml.v2.profiles.dsml.Filter;
import org.openspml.v2.profiles.dsml.FilterItem;

/*
 * Query examples:
 *
 * <Query type="AND">
 *    <Query type="EQ" name="objectClass" value="Person"/>
 *    <Query type="EQ" name="memberObjectGroup" value="Top:Customer"/>
 * </Query>
 *
 * <Query type="AND">
 *    <Query type="EQ" name="firstname" value="Scott"/>
 *    <Query type="AND">
 *       <Query type="EQ" name="objectClass" value="Person"/>
 *       <Query type="EQ" name="memberObjectGroup" value="Top:Customer"/>
 *    </Query>
 * </Query>
 *
 */
//===================================================================
public class Spml2QueryConverter extends QueryConverter implements QueryConverterIF
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public Spml2QueryConverter()
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   /**
    * @param query
    */
   //----------------------------------------------------------------
   public Spml2QueryConverter(final Query query)
   //----------------------------------------------------------------
   {
      super(query);
      return;
   }

   /**
    * @return
    * @throws QueryException
    */
   //----------------------------------------------------------------
   @Override
   public Object convert() throws QueryException
   //----------------------------------------------------------------
   {
      Filter filter = null; // SPML2

      filter = new Filter(this.processQuery(this.getQuery()));

      return filter;
   }

   //----------------------------------------------------------------
   private FilterItem processQuery(final Query query) throws QueryException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":processQuery()";
      Query.Type type = null;
      List<Query> qlist = null;
      Query subquery1 = null;
      Query subquery2 = null;
      FilterItem term = null;
      FilterItem subterm1 = null;
      FilterItem subterm2 = null;

      if (query != null)
      {
         type = query.getType();

         if (type == Query.Type.EQ)
         {
            try
            {
               term = new EqualityMatch(query.getServiceName(), new DSMLValue(query.getValue()));
            }
            catch (DSMLProfileException dpe)
            {
               throw new QueryException(METHOD_NAME
                  + ": SPML2 EQ term creation failure");
            }
         }

         if (type == Query.Type.AND || type == Query.Type.OR)
         {
            qlist = query.getQueryList();
            if (qlist != null)
            {
               if (qlist.size() == 2)
               {
                  subquery1 = qlist.get(0);
                  subquery2 = qlist.get(1);

                  subterm1 = this.processQuery(subquery1);
                  subterm2 = this.processQuery(subquery2);

                  if (type == Query.Type.AND)
                  {
                     term = new org.openspml.v2.profiles.dsml.And(new FilterItem[]
                        {
                           subterm1, subterm2
                        });
                  }

                  if (type == Query.Type.OR)
                  {
                     term = new org.openspml.v2.profiles.dsml.Or(new FilterItem[]
                        {
                           subterm1, subterm2
                        });
                  }

               }
               else
               {
                  throw new QueryException(METHOD_NAME
                     + ": Complex Query must have exactly two sub-Queries.");
               }
            }
            else
            {
               throw new QueryException(METHOD_NAME
                  + ": Complex Query has no sub-Queries");
            }
         }
      }
      else
      {
         throw new QueryException(METHOD_NAME
            + ": Query is null");
      }

      return term;
   }
}
