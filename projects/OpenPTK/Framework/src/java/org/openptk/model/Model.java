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
package org.openptk.model;

import java.util.HashMap;
import java.util.Map;

import org.openptk.common.Category;
import org.openptk.common.Component;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Model extends Component implements ModelIF
//===================================================================
{

   private Map<String, RelationshipIF> _relationships = null;
   private Map<String, ViewIF> _views = null;

   //----------------------------------------------------------------
   public Model()
   //----------------------------------------------------------------
   {
      _relationships = new HashMap<String, RelationshipIF>();
      _views = new HashMap<String, ViewIF>();
      this.setCategory(Category.MODEL);
      return;
   }


   /**
    * @param id
    * @param relationship
    */
   //----------------------------------------------------------------
   @Override
   public void setRelationship(final String id, final RelationshipIF relationship)
   //----------------------------------------------------------------
   {
      if (id != null && id.length() > 0 && relationship != null)
      {
         _relationships.put(id, relationship);
      }
      return;
   }


   /**
    * @param id
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public RelationshipIF getRelationship(final String id)
   //----------------------------------------------------------------
   {
      RelationshipIF relationship = null;

      if (id != null && id.length() > 0 && _relationships.containsKey(id))
      {
         relationship = _relationships.get(id);
      }

      return relationship;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public String[] getRelationshipNames()
   //----------------------------------------------------------------
   {
      String[] array = null;

      if (!_relationships.isEmpty())
      {
         array = _relationships.keySet().toArray(new String[_relationships.size()]);
      }
      else
      {
         array = new String[0];
      }

      return array;
   }


   /**
    * @param id
    * @param view
    */
   //----------------------------------------------------------------
   @Override
   public void setView(final String id, final ViewIF view)
   //----------------------------------------------------------------
   {
      if (id != null && id.length() > 0 && view != null)
      {
         _views.put(id, view);
      }

      return;
   }


   /**
    * @param id
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public ViewIF getView(final String id)
   //----------------------------------------------------------------
   {
      ViewIF view = null;

      if (id != null && id.length() > 0 && _views.containsKey(id))
      {
         view = _views.get(id);
      }

      return view;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public String[] getViewNames()
   //----------------------------------------------------------------
   {
      String[] array = null;

      if (!_views.isEmpty())
      {
         array = _views.keySet().toArray(new String[_views.size()]);
      }
      else
      {
         array = new String[0];
      }

      return array;
   }
}
