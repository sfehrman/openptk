/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2011 Project OpenPTK
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

//===================================================================
public class LdapQueryConverter extends QueryConverter
//===================================================================
{

   //----------------------------------------------------------------
   public LdapQueryConverter()
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   /**
    * @param query
    */
   //----------------------------------------------------------------
   public LdapQueryConverter(Query query)
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

      str = "(" + this.process(this.getQuery()) + ")";

      return str;
   }

   //----------------------------------------------------------------
   private synchronized String process(final Query query)
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();
      String name = null;
      Query.Type type = Query.Type.AND;
      List<Query> qlist = null;

      type = query.getType();

      if (type == Query.Type.AND || type == Query.Type.OR)  // COMPLEX QUERY
      {
         qlist = query.getQueryList();

         if (qlist != null)
         {

            if (type == Query.Type.AND)
            {
               buf.append("&");
            }
            else
            {
               buf.append("|");
            }

            for ( Query subq : qlist)
            {
               if ( subq != null )
               {
                  buf.append("(");
                  buf.append(this.process(subq));
                  buf.append(")");
               }
            }
         }
      }
      else  // SIMPLE QUERY
      {
         name = null;
         name = query.getServiceName();
         if (name == null || name.length() < 1)
         {
            name = query.getName();
         }
         switch (type)
         {
            case BEGINS:
            {
               buf.append(name).append("=").append(query.getValue()).append("*");
               break;
            }
            case CONTAINS:
            {
               buf.append(name).append("=*").append(query.getValue()).append("*");
               break;
            }
            case ENDS:
            {
               buf.append(name).append("=*").append(query.getValue());
               break;
            }
            case EQ:
            {
               buf.append(name).append("=").append(query.getValue());
               break;
            }
            case GT:
            {
               break;
            }
            case GE:
            {
               break;
            }
            case LT:
            {
               break;
            }
            case LE:
            {
               break;
            }
            case NE:
            {
               buf.append("!(").append(name).append("=").append(query.getValue()).append(")");
               break;
            }
            case LIKE:
            {
               buf.append(name).append("~=").append(query.getValue());
               break;
            }
            default:
               break;
         }
      }

      return buf.toString();
   }
}
