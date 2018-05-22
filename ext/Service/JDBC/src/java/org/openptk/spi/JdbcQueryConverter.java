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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.spi;

import java.util.Iterator;
import java.util.List;

import org.openptk.api.Query;
import org.openptk.exception.QueryException;
import org.openptk.spi.operations.JdbcOperations;
import org.openptk.util.StringUtil;

//===================================================================
public class JdbcQueryConverter extends QueryConverter
//===================================================================
{
   public static final String CODE_AND = "AND";
   public static final String CODE_OR = "OR";
   protected String _allowedChars = null;

   //----------------------------------------------------------------
   public JdbcQueryConverter()
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   /**
    *
    * @param query
    */
   //----------------------------------------------------------------
   public JdbcQueryConverter(final Query query)
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
      String str = null;

      /*
       * set the allowed characters for input cleaning
       */

      _allowedChars = this.getProperty(JdbcOperations.PROP_INPUT_ALLOWED_CHARACTERS);
      if (_allowedChars == null || _allowedChars.length() < 1)
      {
         _allowedChars = StringUtil.BASIC_JDBC;
      }

      str = this.process(this.getQuery());

      return str;
   }

   //----------------------------------------------------------------
   private synchronized String process(final Query query) throws QueryException
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();
      String name = null;
      String value = null;
      Query.Type type = Query.Type.AND;
      List<Query> qlist = null;
      Iterator<Query> iter = null;
      Query subq = null;

      type = query.getType();

      if (type == Query.Type.AND || type == Query.Type.OR)  // COMPLEX QUERY
      {
         qlist = query.getQueryList();
         if (qlist != null)
         {
            String op = null;
            iter = qlist.iterator();

            switch (type)
            {
               case AND:
                  op = " " + JdbcQueryConverter.CODE_AND + " ";
                  break;
               case OR:
                  op = " " + JdbcQueryConverter.CODE_OR + " ";
                  break;
               default:
                  throw new QueryException("Unsupported Query Operator " + type.toString());
            }

            int operandCount = 0;

            buf.append("( ");

            /*
             * For complext operators (AND, OR), iterate over each of the
             * operands and apply to the operator.
             */

            while (iter.hasNext())
            {
               subq = null;
               subq = iter.next();
               if (subq != null)
               {
                  // For MYSQL WHERE Clauses, the AND/OR operators are inline.
                  //   i.e.   a AND b AND c ....
                  //
                  // So, do not emit the operator if we are processing the 1st
                  // operand (i.e. operandCount == 0

                  if (operandCount > 0)
                  {
                     buf.append(op);
                  }

                  // Recursively process each operand

                  buf.append(this.process(subq));

                  operandCount++;
               }
            }
            buf.append(" ) ");
         }
      }
      else  // SIMPLE QUERY
      {
         name = query.getServiceName();
         if (name == null || name.length() < 1)
         {
            name = query.getName();
         }

         value = query.getValue();
         if (value != null && value.length() > 0)
         {
            value = StringUtil.clean(_allowedChars, value);
         }

         if (name != null && name.length() > 0
            && value != null && value.length() > 0)
         {
            switch (type)
            {
               case EQ:
               {
                  buf.append(name).append(" = '").append(value).append("'");
                  break;
               }
               case BEGINS:
               {
                  buf.append(name).append(" LIKE '").append(value).append("%'");
                  break;
               }
               case CONTAINS:
               case LIKE:
               {
                  buf.append(name).append(" LIKE '%").append(value).append("%'");
                  break;
               }
               case ENDS:
               {
                  buf.append(name).append(" LIKE '%").append(value).append("'");
                  break;
               }
               case NE:
               {
                  buf.append(name).append(" != '").append(value).append("'");
                  break;
               }
               case GT:
               case GE:
               case LT:
               case LE:
               default:
                  throw new QueryException("Unsupported Query Type: " + type.toString());
            }
         }
      }
      return buf.toString();
   }
}
