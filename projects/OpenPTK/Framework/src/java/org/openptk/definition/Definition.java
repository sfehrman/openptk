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
package org.openptk.definition;

import org.openptk.common.Category;
import org.openptk.common.Component;

//===================================================================
public abstract class Definition extends Component implements DefinitionIF
//===================================================================
{
   protected static String DEFAULT_DESCRIPTION = "Default Definition";
   protected String _key = null; // which attribute is the primary _key
   protected String _classname = null;  // Attribute holding the subject _classname

   //----------------------------------------------------------------
   public Definition()
   //----------------------------------------------------------------
   {
      super();

      this.setCategory(Category.DEFINITION);

      return;
   }


   /**
    * @param id
    */
   //----------------------------------------------------------------
   public Definition(final String id)
   //----------------------------------------------------------------
   {
      super();

      this.setUniqueId(id);
      this.setCategory(Category.DEFINITION);
      
      return;
   }


   /**
    * @param def
    */
   //----------------------------------------------------------------
   public Definition(final DefinitionIF def)
   //----------------------------------------------------------------
   {
      super(def);

      this.setDefinitionClassName(def.getDefinitionClassName());
      this.setDescription(def.getDescription() + " [copy]");

      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public abstract DefinitionIF copy();
   //----------------------------------------------------------------


   /**
    * @param classname
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setDefinitionClassName(final String classname)
   //----------------------------------------------------------------
   {
      _classname = classname;
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String getDefinitionClassName()
   //----------------------------------------------------------------
   {
      String str = null;

      if ( _classname != null)
      {
         str = new String(_classname); // always return a copy
      }
      
      return str;
   }
}
