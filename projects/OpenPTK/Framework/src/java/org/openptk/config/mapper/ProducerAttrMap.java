/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2012-2013 Project OpenPTK
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
package org.openptk.config.mapper;

import java.util.Map;
import org.openptk.exception.StructureException;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman
 * 
 * @since 2.2.0
 */
//===================================================================
public class ProducerAttrMap extends AttrMap
//===================================================================
{

   //----------------------------------------------------------------
   public ProducerAttrMap()
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   //----------------------------------------------------------------
   @Override
   protected StructureIF getFrameworkStructure(final StructureIF structExtAttr, final Map<String, Boolean> mapRequired) throws StructureException
   //----------------------------------------------------------------
   {
      StructureIF structFwAttr = null;
      
      // TODO: implement
      
      structFwAttr = structExtAttr;
      
      return structFwAttr;
   }

   //----------------------------------------------------------------
   @Override
   protected StructureIF getExternalStructure(final StructureIF structFwAttr) throws StructureException
   //----------------------------------------------------------------
   {
      StructureIF structExtAttr = null;

      // TODO: implement

      structExtAttr = structFwAttr;

      return structExtAttr;
   }
}
