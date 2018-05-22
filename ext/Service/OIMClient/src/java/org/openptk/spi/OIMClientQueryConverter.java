/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
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
package org.openptk.spi;

import java.util.List;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;

import org.openptk.api.Query;
import org.openptk.exception.QueryException;

/**
 *
 * @author Scott Fehrman, Oracle America, Inc.
 */

/*
 * Query examples:
 *
 * SIMPLE:
 *
 * <Query type="EQ" name="lastname" value="Smith"/>
 *
 * COMPLEX:
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
public class OIMClientQueryConverter extends QueryConverter
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public OIMClientQueryConverter()
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   /**
    * @param query
    */
   //----------------------------------------------------------------
   public OIMClientQueryConverter(final Query query)
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
      SearchCriteria criteria = null;

      this.makeBinaryTree(this.getQuery());

      criteria = this.processQuery(this.getQuery());

      return criteria;
   }

   //----------------------------------------------------------------
   private void makeBinaryTree(Query query) throws QueryException
   //----------------------------------------------------------------
   {
      boolean bChanged = false;
      int offset = 0;
      String METHOD_NAME = CLASS_NAME + ":makeBinaryTree(): ";
      Query.Type type = null; // OpenPTK
      List<Query> qlist = null;
      Query newQ = null;

      /*
       * This method makes sure the OpenPTK Query heirarchy is in a
       * Binary Tree format.  This is needed to support the OIMClient
       * SearchCritera mechanism.
       *
       * An OpenPTK Complex Query could look like this:
       *
       * AND +
       *     - OR +
       *          - CONTAINS, attr1, value1
       *          - CONTAINS, attr2, value1
       *          - CONTAINS, attr3, value1
       *     - OR +
       *          - CONTAINS, attr1, value2
       *          - CONTAINS, attr2, value2
       *          - CONTAINS, attr3, value3
       *
       * The OIMClient SearchCritera needs to have the "logic" look like this:
       *
       * AND +
       *     - OR +
       *          - OR +
       *               - CONTAINS, attr1, value1
       *               - CONTAINS, attr2, value1
       *          - CONTAINS, attr3, value1
       *     - OR +
       *          - OR +
       *               - CONTAINS, attr1, value2
       *               - CONTAINS, attr2, value2
       *          - CONTAINS, attr3, value2
       *
       * Basically, a given "complex" operator (OR/AND) MUST have EXACTLY
       * two (2) operands.  OpenPTK Complex Query objects that have more than
       * two sub-queries needs to be re-structured into more nested layers
       * that ONLY contain two sub-queries.  This needs to be done BEFORE the
       * "Query" can be converted to a heirarchy of OIMClient SearchCritera
       */

      if (query == null)
      {
         throw new QueryException(METHOD_NAME + "Query is null");
      }

      type = query.getType();

      /*
       * We only care about Complex Queries (AND and OR)
       */

      if (type == Query.Type.AND || type == Query.Type.OR)
      {
         qlist = query.getQueryList();

         if (qlist == null)
         {
            throw new QueryException(METHOD_NAME + "Complex Query has no sub-Queries");
         }

         if (qlist.size() < 2)
         {
            throw new QueryException(METHOD_NAME + "Complex Query must have at least two sub-Queries.");
         }

         /*
          * walk the sub-queries, if they are complex
          */

         for (Query qsub : qlist)
         {
            if (qsub != null)
            {
               if (qsub.getType() == Query.Type.AND || qsub.getType() == Query.Type.OR)
               {
                  this.makeBinaryTree(qsub);
                  bChanged = true;
               }
            }
         }

         while (qlist.size() > 2)
         {
            /*
             * break the sub-queries into binary trees
             * - use the first two sub queries to make a new query
             *   using the same operator
             * - replace the first sub-query with the new query
             * - remove the second query
             */

            newQ = new Query(type);
            newQ.addQuery(qlist.get(offset));
            newQ.addQuery(qlist.get(offset + 1));

            qlist.set(offset, newQ);
            qlist.remove(offset + 1);

            /*
             * Change the offset, we want to "get" the next two sub-queries.
             * Only increment the offset by one because we removed a sub-query
             * from the list.
             */

            offset++;
            bChanged = true;
         }

         if (bChanged)
         {
            query.setQueryList(qlist);
         }
      }


      return;
   }

   //----------------------------------------------------------------
   private SearchCriteria processQuery(final Query query) throws QueryException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":processQuery(): ";
      String name = null;
      String value = null;
      List<Query> qlist = null;
      Query.Type type = null; // OpenPTK
      Query subquery1 = null; // OpenPTK
      Query subquery2 = null; // OpenPTK
      SearchCriteria criteria = null; // OIMClient
      SearchCriteria subcriteria1 = null; // OIMClient
      SearchCriteria subcriteria2 = null; // OIMClient

      if (query == null)
      {
         throw new QueryException(METHOD_NAME + "Query is null");
      }

      type = query.getType();

      switch (type)
      {
         case EQ:
            name = query.getServiceName();
            value = query.getValue();
            criteria = new SearchCriteria(name, value, SearchCriteria.Operator.EQUAL);
            break;
         case BEGINS:
            name = query.getServiceName();
            value = query.getValue() + "*";
            criteria = new SearchCriteria(name, value, SearchCriteria.Operator.EQUAL);
            break;
         case ENDS:
            name = query.getServiceName();
            value = "*" + query.getValue();
            criteria = new SearchCriteria(name, value, SearchCriteria.Operator.EQUAL);
            break;
         case CONTAINS:
            name = query.getServiceName();
            value = "*" + query.getValue() + "*";
            criteria = new SearchCriteria(name, value, SearchCriteria.Operator.EQUAL);
            break;
         case AND:
         case OR:
            qlist = query.getQueryList();

            if (qlist == null)
            {
               throw new QueryException(METHOD_NAME + "Complex Query has no sub-Queries");
            }

            if (qlist.size() != 2)
            {
               throw new QueryException(METHOD_NAME + "Complex Query must have exactly two sub-Queries.");
            }

            subquery1 = qlist.get(0);
            subquery2 = qlist.get(1);

            subcriteria1 = this.processQuery(subquery1);
            subcriteria2 = this.processQuery(subquery2);

            if (type == Query.Type.AND)
            {
               criteria = new SearchCriteria(subcriteria1, subcriteria2, SearchCriteria.Operator.AND);
            }
            else if (type == Query.Type.OR)
            {
               criteria = new SearchCriteria(subcriteria1, subcriteria2, SearchCriteria.Operator.OR);
            }

            break;
         default:
            throw new QueryException(METHOD_NAME + "Unsupported Query type '"
               + query.getTypeAsString());
      }

      return criteria;
   }
}
