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
package org.openptk.api;

import org.openptk.structure.StructureIF;
import org.openptk.structure.StructureType;

/**
 * The Input object is used "transport" data when an Operation is executed.
 *
 * The Input can contain:
 * <ul>
 * <li>Unique Id
 * <li>Attributes
 * <li>Properties
 * <li>Query
 * </ul>
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class Input extends Element
//===================================================================
{
   private Query _query = null;
   private AttributeIF.Access _access = AttributeIF.Access.PUBLIC;  // public

   /**
    * Creates a new Input object.
    */
   //----------------------------------------------------------------
   public Input()
   //----------------------------------------------------------------
   {
      super();

      this.setDescription("Input");

      return;
   }

   /**
    * Create a new Input that is a copy of the specified Input.
    *
    * @param input an existing Input
    */
   //----------------------------------------------------------------
   public Input(final Input input)
   //----------------------------------------------------------------
   {
      super(input);

      if (input.getQuery() != null)
      {
         _query = new Query(input.getQuery());
      }

      return;
   }

   /**
    * Creates a "deep" copy of the Input.
    *
    * @return Input the new Input (a copy)
    */
   //----------------------------------------------------------------
   @Override
   public Input copy()
   //----------------------------------------------------------------
   {
      return new Input(this);
   }

   /**
    * Set the Input's access level.
    *
    * @param access the access level
    */
   //----------------------------------------------------------------
   public final synchronized void setAccess(final AttributeIF.Access access)
   //----------------------------------------------------------------
   {
      _access = access;
      return;
   }

   /**
    * Set the Input's access level.
    *
    * @param val the access level
    */
   //----------------------------------------------------------------
   public final synchronized void setAccess(final String val)
   //----------------------------------------------------------------
   {
      if (val.equalsIgnoreCase(AttributeIF.Access.PRIVATE.toString()))
      {
         _access = AttributeIF.Access.PRIVATE;
      }
      else
      {
         _access = AttributeIF.Access.PUBLIC;
      }
      return;
   }

   /**
    * Get the Input's access level.
    *
    * @return AttributeIF.Access the access level
    */
   //----------------------------------------------------------------
   public final AttributeIF.Access getAccess()
   //----------------------------------------------------------------
   {
      return _access;
   }

   /**
    * Get the Input's access level, string representation.
    *
    * @return String the access level
    */
   //----------------------------------------------------------------
   public final String getAccessAsString()
   //----------------------------------------------------------------
   {
      return _access.toString();
   }

   /**
    * Get the Input's Query.
    *
    * @return Query the Query
    */
   //----------------------------------------------------------------
   public final Query getQuery()
   //----------------------------------------------------------------
   {
      return _query;
   }

   /**
    * Set the Input's Query.
    *
    * @param query a Query object
    */
   //----------------------------------------------------------------
   public final synchronized void setQuery(final Query query)
   //----------------------------------------------------------------
   {
      _query = query;
      return;
   }

   /**
    * Removes the Input's Query.
    */
   //----------------------------------------------------------------
   public final synchronized void removeQuery()
   //----------------------------------------------------------------
   {
      _query = null;
      return;
   }

   /**
    * Populates the Input with Attributes from the StructureIF data.
    * For each child within the structure, a new Attribute is created
    * using the child-structure's name and value information.
    *
    * @param input a non-null Input object
    * @param struct a non-null SturctureIF object
    */
   //----------------------------------------------------------------
   public static void build(final Input input, final StructureIF struct)
   //----------------------------------------------------------------
   {

      boolean isMulti = false;
      int len = 0;
      Object[] values = null;
      Object[] valObjArray = null;
      Integer[] valIntArray = null;
      Long[] valLongArray = null;
      Boolean[] valBoolArray = null;
      String[] valStrArray = null;
      String name = null;
      String valStr = null;
      AttributeIF attr = null;
      StructureIF structAttr = null;
      StructureIF[] structAttrsArray = null;
      StructureType type = null;

      /*
       * Convert the StructureIF object into an Input object
       *
       * struct format:
       *
       * attributes :
       *       name : value(s)
       *       name : values(s)
       *
       */

      if (struct != null && input != null)
      {
         structAttrsArray = struct.getChildrenAsArray();

         for (int i = 0; i < structAttrsArray.length; i++)
         {
            structAttr = structAttrsArray[i];
            if (structAttr != null)
            {
               name = structAttr.getName();

               if (name != null && name.length() > 0)
               {
                  type = structAttr.getValueType();
                  values = structAttr.getValuesAsArray();
                  if (values != null && values.length > 0)
                  {
                     isMulti = structAttr.isMultiValued();
                     attr = null;
                     switch (type)
                     {
                        case STRING:
                           if (isMulti)
                           {
                              len = values.length;
                              valStrArray = new String[len];
                              for (int j = 0; j < len; j++)
                              {
                                 valStrArray[j] = (String) values[j];
                              }

                              attr = new Attribute(name, valStrArray);
                           }
                           else
                           {
                              valStr = (String) values[0];
                              attr = new Attribute(name, valStr);
                           }
                           break;
                        case INTEGER:
                           if (isMulti)
                           {
                              len = values.length;
                              valIntArray = new Integer[len];
                              for (int j = 0; j < len; j++)
                              {
                                 valIntArray[j] = (Integer) values[j];
                              }

                              attr = new Attribute(name, valIntArray);
                           }
                           else
                           {
                              attr = new Attribute(name, (Integer) values[0]);
                           }
                           break;
                        case LONG:
                           if (isMulti)
                           {
                              len = values.length;
                              valLongArray = new Long[len];
                              for (int j = 0; j < len; j++)
                              {
                                 valLongArray[j] = (Long) values[j];
                              }

                              attr = new Attribute(name, valLongArray);
                           }
                           else
                           {
                              attr = new Attribute(name, (Long) values[0]);
                           }
                           break;
                        case BOOLEAN:
                           if (isMulti)
                           {
                              len = values.length;
                              valBoolArray = new Boolean[len];
                              for (int j = 0; j < len; j++)
                              {
                                 valBoolArray[j] = (Boolean) values[j];
                              }

                              attr = new Attribute(name, valBoolArray);
                           }
                           else
                           {
                              attr = new Attribute(name, (Boolean) values[0]);
                           }
                           break;
                        case OBJECT:
                           if (isMulti)
                           {
                              len = values.length;
                              valObjArray = new Object[len];
                              for (int j = 0; j < len; j++)
                              {
                                 valObjArray[j] = values[j];
                              }

                              attr = new Attribute(name, valObjArray);
                           }
                           else
                           {
                              attr = new Attribute(name, values[0]);
                           }
                           break;
                     }
                  }
                  else // value == null
                  {
                     attr = new Attribute(name);
                  }
                  if (attr != null)
                  {
                     input.addAttribute(attr);
                  }
               }
            }
         }
      }
      return;
   }
}
