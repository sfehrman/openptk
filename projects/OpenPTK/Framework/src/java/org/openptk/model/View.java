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

import java.util.LinkedList;
import java.util.List;

import org.openptk.common.Category;
import org.openptk.common.Component;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class View extends Component implements ViewIF
//===================================================================
{

   private List<String> _relationshipIds = null;

   //----------------------------------------------------------------
   public View()
   //----------------------------------------------------------------
   {
      _relationshipIds = new LinkedList<String>();
      this.setCategory(Category.VIEW);
      return;
   }


   /**
    * @param id
    */
   //----------------------------------------------------------------
   public View(final String id)
   //----------------------------------------------------------------
   {
      this.setUniqueId(id);
      _relationshipIds = new LinkedList<String>();
      this.setCategory(Category.VIEW);
      return;
   }


   /**
    * @param id
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addRelationshipId(final String id)
   //----------------------------------------------------------------
   {
      if (id != null && id.length() > 0)
      {
         _relationshipIds.add(id);
      }
      
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getRelationshipIds()
   //----------------------------------------------------------------
   {
      String[] array = null;

      if (!_relationshipIds.isEmpty())
      {
         array = _relationshipIds.toArray(new String[_relationshipIds.size()]);
      }
      else
      {
         array = new String[0];
      }

      return array;
   }
}
