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

import java.util.List;
import java.util.Map;

import org.openptk.exception.StructureException;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;
import org.openptk.structure.StructureType;

/**
 * This class provides an implementation of the AttrMapIF interface to enable
 * the OpenPTK server (RESTful Web Service) to "consume" external attributes
 * as part of a data (JSON) payload from CREATE and UPDATE operations. This
 * class will also convert output data from READ and SEARCH operations
 * to the external attribute format.
 *
 * @author Scott Fehrman
 *
 * @since 2.2.0
 */
//===================================================================
public class ConsumerAttrMap extends AttrMap
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public ConsumerAttrMap()
   //----------------------------------------------------------------
   {
      super();
      return;
   }
   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   //----------------------------------------------------------------

   @Override
   protected StructureIF getFrameworkStructure(final StructureIF structExternal, final Map<String, Boolean> mapRequired) throws StructureException
   //----------------------------------------------------------------
   {
      /*
       * This is a recursive method
       * It converts the external Structure (argument) to a framework Strucuture
       * and returns it.
       * This is considered to be INBOUND
       */

      boolean isMultiValued = false;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String extName = null;
      String fwName = null;
      StructureIF structFramework = null;
      StructureType valueType = null;
      ExternalAttrIF extAttr = null;
//      ExternalModeIF extMode = null;
      ExternalAttrIF.Mode attrMode = null;
      List<StructureIF> children = null;

      if (structExternal == null)
      {
         this.handleError(METHOD_NAME + "Structure for External is null");
      }

      /*
       * get the name of the external structure
       * use the name to obtain the ExternalAttrIF definition object
       * 
       * The ExternalAttrIF object determines how the external structure
       * is "mapped" to a framework (internal) structure
       */

      extName = structExternal.getName();
      if (extName == null || extName.length() < 1)
      {
         this.handleError(METHOD_NAME + "External Structure does not have a name");
      }

      if (!_attributes.containsKey(extName))
      {
         this.handleError(METHOD_NAME + "External Struture '" + extName
            + "' does not exist in the attribute map.");
      }

      extAttr = _attributes.get(extName);
      if (extAttr == null)
      {
         this.handleError(METHOD_NAME + "External Attribute '" + extName
            + "' is null");
      }

      /*
       * only process the "INBOUND" or "BOTH" mode attributes, skip "OUTBOUND" and "IGNORE"
       */
      
      attrMode = extAttr.getMode();
      if (attrMode == ExternalAttrIF.Mode.INBOUND || attrMode == ExternalAttrIF.Mode.BOTH)
      {
         fwName = extAttr.getMapTo();
         if (fwName == null || fwName.length() < 1)
         {
            fwName = extName;
         }

         /*
          * A given structure will fall into one of two "categories" 
          * a Collection (PARENT, CONTAINER) or,
          * a Element (STRING, INTEGER, LONG, BOOLEAN, OBJECT)
          */

         switch (structExternal.getValueType())
         {
            case PARENT:
            case CONTAINER:
               children = structExternal.getChildren();
               if (children != null && children.size() > 0)
               {
                  structFramework = new BasicStructure(fwName);
                  for (StructureIF child : children)
                  {
                     
                  }
               }
               break;
            default:
               break;
         }








         isMultiValued = structExternal.isMultiValued();
         valueType = structExternal.getValueType();

         if (isMultiValued)
         {
            switch (valueType)
            {
               case STRING:
                  structFramework = new BasicStructure(fwName, (String[]) structExternal.getValue());
                  break;
               case INTEGER:
                  structFramework = new BasicStructure(fwName, (Integer[]) structExternal.getValue());
                  break;
               case LONG:
                  structFramework = new BasicStructure(fwName, (Long[]) structExternal.getValue());
                  break;
               case BOOLEAN:
                  structFramework = new BasicStructure(fwName, (Boolean[]) structExternal.getValue());
                  break;
               case OBJECT:
                  structFramework = new BasicStructure(fwName, (Object[]) structExternal.getValue());
                  break;
            }
         }
         else
         {
            switch (valueType)
            {
               case STRING:
                  structFramework = new BasicStructure(fwName, (String) structExternal.getValue());
                  break;
               case INTEGER:
                  structFramework = new BasicStructure(fwName, (Integer) structExternal.getValue());
                  break;
               case LONG:
                  structFramework = new BasicStructure(fwName, (Long) structExternal.getValue());
                  break;
               case BOOLEAN:
                  structFramework = new BasicStructure(fwName, (Boolean) structExternal.getValue());
                  break;
               case OBJECT:
                  structFramework = new BasicStructure(fwName, (Object) structExternal.getValue());
                  break;
            }
         }
      }

      return structFramework;
   }

   //----------------------------------------------------------------
   @Override
   protected StructureIF getExternalStructure(final StructureIF structFramework) throws StructureException
   //----------------------------------------------------------------
   {
      boolean isMultiValued = false;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String fwName = null;
      String extName = null;
      StructureIF structExternal = null;
      StructureType valueType = null;
      ExternalAttrIF extAttr = null;
      ExternalAttrIF.Mode attrMode = null;

      if (structFramework == null)
      {
         this.handleError(METHOD_NAME + "Structure for Framework Attribute is null");
      }

      fwName = structFramework.getName();
      if (fwName == null || fwName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Framework Structure does not have a name");
      }

      if (!_indexFwKey.containsKey(fwName))
      {
         this.handleError(METHOD_NAME + "Framework Attribute '" + fwName
            + "' does not exist in the attribute map.");
      }

      extName = _indexFwKey.get(fwName);
      if (extName == null || extName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Framework Attribute '" + fwName
            + "' has a null/empty External Name");
      }

      extAttr = _attributes.get(extName);
      if (extAttr == null)
      {
         this.handleError(METHOD_NAME + "Framework Attribute '" + fwName
            + "' is null");
      }

      /*
       * only process the "consumer" mode attributes, skip "OUTBOUND" and "IGNORE"
       */

      attrMode = extAttr.getMode();
      if (attrMode == ExternalAttrIF.Mode.INBOUND || attrMode == ExternalAttrIF.Mode.BOTH)
      {
         isMultiValued = structFramework.isMultiValued();
         valueType = structFramework.getValueType();

         if (isMultiValued)
         {
            switch (valueType)
            {
               case STRING:
                  structExternal = new BasicStructure(extName, (String[]) structFramework.getValue());
                  break;
               case INTEGER:
                  structExternal = new BasicStructure(extName, (Integer[]) structFramework.getValue());
                  break;
               case LONG:
                  structExternal = new BasicStructure(extName, (Long[]) structFramework.getValue());
                  break;
               case BOOLEAN:
                  structExternal = new BasicStructure(extName, (Boolean[]) structFramework.getValue());
                  break;
               case OBJECT:
                  structExternal = new BasicStructure(extName, (Object[]) structFramework.getValue());
                  break;
            }
         }
         else
         {
            switch (valueType)
            {
               case STRING:
                  structExternal = new BasicStructure(extName, (String) structFramework.getValue());
                  break;
               case INTEGER:
                  structExternal = new BasicStructure(extName, (Integer) structFramework.getValue());
                  break;
               case LONG:
                  structExternal = new BasicStructure(extName, (Long) structFramework.getValue());
                  break;
               case BOOLEAN:
                  structExternal = new BasicStructure(extName, (Boolean) structFramework.getValue());
                  break;
               case OBJECT:
                  structExternal = new BasicStructure(extName, (Object) structFramework.getValue());
                  break;
            }
         }
      }

      return structExternal;
   }
}
