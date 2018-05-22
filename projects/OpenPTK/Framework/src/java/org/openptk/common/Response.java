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
package org.openptk.common;

import java.util.ArrayList;
import java.util.List;

//===================================================================
public class Response extends Component implements ResponseIF
//===================================================================
{
   private RequestIF _request = null;
   private List<ComponentIF> _results = null;

   //----------------------------------------------------------------
   public Response()
   //----------------------------------------------------------------
   {
      this.setCategory(Category.RESPONSE);
      return;
   }

   /**
    * @param request
    */
   //----------------------------------------------------------------
   public Response(final RequestIF request)
   //----------------------------------------------------------------
   {
      this.setCategory(Category.RESPONSE);
      _request = request;
      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final RequestIF getRequest()
   //----------------------------------------------------------------
   {
      return _request;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final List<ComponentIF> getResults()
   //----------------------------------------------------------------
   {
      return _results;
   }

   /**
    * @param results
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setResults(final List<ComponentIF> results)
   //----------------------------------------------------------------
   {
      _results = results;
      return;
   }

   /**
    * @param comp
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addResult(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      if (_results == null)
      {
         _results = new ArrayList<ComponentIF>();
      }
      _results.add(comp);
      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized int getResultsSize()
   //----------------------------------------------------------------
   {
      int size = 0;
      if (_results != null)
      {
         size = _results.size();
      }
      return size;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public String toString()
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();

      buf.append("[");
      if (_results != null)
      {
         for (ComponentIF comp : this.getResults())
         {
            buf.append("{").append(comp.toString()).append("},");
         }
      }
      else
      {
         buf.append("NULL");
      }
      buf.append("]");

      return buf.toString();
   }
}
