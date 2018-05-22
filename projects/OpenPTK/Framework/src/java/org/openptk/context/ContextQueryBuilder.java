/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010 Sun Microsystems, Inc.
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
package org.openptk.context;

import java.util.StringTokenizer;

import org.openptk.api.DataType;
import org.openptk.api.Query;
import org.openptk.api.QueryBuilder;
import org.openptk.common.AttrIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//================================================================
public class ContextQueryBuilder extends QueryBuilder
//================================================================
{
   private ContextIF _context = null;


   /**
    * @param context
    * @throws Exception
    */
   //----------------------------------------------------------------
   public ContextQueryBuilder(final ContextIF context) throws Exception
   //----------------------------------------------------------------
   {
      super();

      if (context != null)
      {
         _context = context;
      }
      else
      {
         throw new Exception("Context is null");
      }
      
      return;
   }


   /**
    * @param search
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Query build(final String search)
   //----------------------------------------------------------------
   {
      int tokQty = 0;
      Boolean bOR = Boolean.valueOf(false);
      Boolean bAND = Boolean.valueOf(false);
      Boolean bCONTAINS = Boolean.valueOf(false);
      String[] srchAttrs = null;
      String[] srchVals = null;
      StringTokenizer tok = null;
      Query query = null;
      Query qor = null;
      Query.Type qType = null;
      AttrIF attr = null;

      /*
       * create a String Array from the search value (user input)
       * search is a space separated String of search values:
       * examples:  "Bob"
       *            "Bob Smith"
       *            "Bob Smith example.com"
       */

      tok = new StringTokenizer(search, " ");
      tokQty = tok.countTokens();
      srchVals = new String[tokQty];
      for (int i = 0; i < tokQty; i++)
      {
         srchVals[i] = tok.nextToken();
      }

      /*
       * Get the (ordered) String Array of attribute names
       */
      attr = _context.getAttribute(ContextIF.ATTRIBUTE_SRCH_ATTRS);
      if (attr != null)
      {
         srchAttrs = (String[]) attr.getValue();
      }
      else
      {
         srchAttrs = new String[1];
         srchAttrs[0] = DEFAULT_SEARCH_ATTR;
      }

      /*
       * Check for the "AND" Attribute, create if null, then save it
       */
      attr = _context.getAttribute(ContextIF.ATTRIBUTE_HAS_AND);

      if (attr != null && attr.getType() == DataType.BOOLEAN)
      {
         bAND = (Boolean) attr.getValue();
      }

      /*
       * Check for the "OR" Attribute, create if null, then save it
       */
      attr = _context.getAttribute(ContextIF.ATTRIBUTE_HAS_OR);

      if (attr != null && attr.getType() == DataType.BOOLEAN)
      {
         bOR = (Boolean) attr.getValue();
      }

      /*
       * Check for the "OR" Attribute, create if null, then save it
       */
      attr = _context.getAttribute(ContextIF.ATTRIBUTE_HAS_CONTAINS);

      if (attr != null && attr.getType() == DataType.BOOLEAN)
      {
         bCONTAINS = (Boolean) attr.getValue();
      }

      /*
       * Determine which algorthm to use
       *
       * Note: if CONTAINS is not supported, then EQ will be used
       *
       * if AND and OR:
       *    Step 1: can use multiple input values
       *    Step 2: each value will be "OR"ed with each of the attributes
       *    Step 3: the "results" of each "OR"ed will be "AND"ed together
       *
       * if OR:
       *    Step 1: can only use one input value (first one)
       *    Step 2: the value will be "OR"ed with each of the attributes
       *
       * else:
       *    Step 1: can only use one input value (first one)
       *    Step 2: the value will be compared with the first attribute
       */
      if (bCONTAINS)
      {
         qType = Query.Type.CONTAINS;
      }
      else
      {
         qType = Query.Type.EQ;
      }

      if (bAND && bOR && srchVals.length > 1)
      {
         query = new Query(Query.Type.AND);
         for (int i = 0; i < srchVals.length; i++)
         {
            qor = new Query(Query.Type.OR);
            for (int j = 0; j < srchAttrs.length; j++)
            {
               qor.addQuery(new Query(qType, srchAttrs[j], srchVals[i]));
            }
            query.addQuery(qor);
         }
      }
      else if (bOR)
      {
         query = new Query(Query.Type.OR);
         for (int i = 0; i < srchAttrs.length; i++)
         {
            query.addQuery(new Query(qType, srchAttrs[i], srchVals[0]));
         }
      }
      else
      {
         query = new Query(qType, srchAttrs[0], srchVals[0]);
      }

      return query;
   }
}
