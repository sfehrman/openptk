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

import org.openptk.api.DataType;
import org.openptk.common.Attr;
import org.openptk.common.AttrIF;

//
//===================================================================
public class DefinitionAttr extends Attr implements AttrIF
//===================================================================
{


   /**
    * @param attr
    */
   //----------------------------------------------------------------
   public DefinitionAttr(final AttrIF attr)
   //----------------------------------------------------------------
   {
      super(attr);
      return;
   }


   /**
    * @param id
    */
   //----------------------------------------------------------------
   public DefinitionAttr(final String id)
   //----------------------------------------------------------------
   {
      super(id);
   }


   /**
    * @param id
    * @param type
    */
   //----------------------------------------------------------------
   public DefinitionAttr(final String id, final String type)
   //----------------------------------------------------------------
   {
      super(id); // default:  DataType = String

      if (type != null)
      {
         if (type.equalsIgnoreCase(DataType.OBJECT.toString()))
         {
            super.setValue(new Object());
         }
         else if (type.equalsIgnoreCase(DataType.BOOLEAN.toString()))
         {
            super.setValue(true);
         }
         else if (type.equalsIgnoreCase(DataType.INTEGER.toString()))
         {
            super.setValue(new Integer(0));
         }
         else if ( type.equalsIgnoreCase(DataType.LONG.toString()))
         {
            super.setValue(new Long(0));
         }
      }
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public AttrIF copy()
   //----------------------------------------------------------------
   {
      return new DefinitionAttr(this);
   }

}
