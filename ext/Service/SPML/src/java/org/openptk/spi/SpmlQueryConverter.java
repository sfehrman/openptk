/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2010 Sun Microsystems, Inc.
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

import java.util.Iterator;
import java.util.List;

import org.openptk.api.Query;
import org.openptk.exception.QueryException;

import org.openspml.message.Filter;
import org.openspml.message.FilterTerm;

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
public class SpmlQueryConverter extends QueryConverter implements QueryConverterIF
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public SpmlQueryConverter()
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   /**
    * @param query
    */
   //----------------------------------------------------------------
   public SpmlQueryConverter(final Query query)
   //----------------------------------------------------------------
   {
      super(query);
      return;
   }

   /*
    * <dsml:filter>
    *   <dsml:and>
    *     <dsml:equalityMatch name='gn'>
    *       <dsml:value>john</dsml:value>
    *     </dsml:equalityMatch>
    *     <dsml:equalityMatch name='MemberObjectGroups'>
    *       <dsml:value>Top:Customers</dsml:value>
    *     </dsml:equalityMatch>
    *   </dsml:and>
    * </dsml:filter>
    */
   /**
    * @return
    * @throws QueryException
    */
   //----------------------------------------------------------------
   @Override
   public Object convert() throws QueryException
   //----------------------------------------------------------------
   {
      Filter filter = null; // SPML

      filter = new Filter();
      filter.addTerm(this.processQuery(this.getQuery()));

      return filter;
   }

   //----------------------------------------------------------------
   private FilterTerm processQuery(final Query query) throws QueryException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":processQuery()";
      Query.Type type = null;
      List<Query> qlist = null;
      Iterator<Query> iter = null;
      Query subquery = null;
      FilterTerm term = null;
      FilterTerm subterm = null;

      if (query != null)
      {
         type = query.getType();
         term = new FilterTerm();

         switch (type)
         {
            case AND: // Complex
            {
               term.setOperation(FilterTerm.OP_AND);
               break;
            }
            case OR:  // Complex
            {
               term.setOperation(FilterTerm.OP_OR);
               break;
            }
            case EQ: // Simple
            {
               term.setOperation(FilterTerm.OP_EQUAL);
               term.setName(query.getServiceName());
               term.setValue(query.getValue());
               break;
            }
            default:
            {
               break;
            }
         }

         if (type == Query.Type.AND || type == Query.Type.OR)
         {
            qlist = query.getQueryList();
            if (qlist != null)
            {
               if (qlist.size() == 2)
               {
                  iter = qlist.iterator();
                  while (iter.hasNext())
                  {
                     subquery = iter.next();
                     subterm = this.processQuery(subquery);
                     term.addOperand(subterm);
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
