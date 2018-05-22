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
 * Portions Copyright 2012, Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.structure;

import java.util.List;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class BasicStructure extends Structure
//===================================================================
{
   /**
    * Create a new BasicStructure using the provided name.
    * The structure will NOT have a type.  This can either contain
    * a value OR contain children, NOT BOTH.
    *
    * @param name Name of the Structure
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name)
   //----------------------------------------------------------------
   {
      super(name);
      return;
   }

   /**
    * Create a new BasicStructure using the provided name and value.
    * The structure will be of type STRING and it is single valued.
    * This structure can not have any children.
    *
    * @param name the name of the Structure
    * @param value the String value
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name, final String value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   /**
    * Create a new BasicStructure using the provided name and List of values.
    * The structure will be of type STRING and it is multi valued.
    * This structure can not have any children.
    *
    * @param name Name of the Structure
    * @param values List of values
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name, final List<String> values)
   //----------------------------------------------------------------
   {
      super(name, values);
      return;
   }

   /**
    * Create a new BasicStructure using the provided name and Array of values.
    * The structure will be of type STRING and it is multi valued.
    * This structure can not have any children.
    *
    * @param name Name of the Structure
    * @param values Array of values
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name, final String[] values)
   //----------------------------------------------------------------
   {
      super(name, values);
      return;
   }

   /**
    * Create a new BasicStructure using the provided name and value.
    * The structure will be of type INTEGER and it is single valued.
    * This structure can not have any children.
    *
    * @param name Name of the Structure
    * @param value Strucutre's int value
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name, final int value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   /**
    * Create a new BasicStructure using the provided name and value.
    * The structure will be of type INTEGER and it is single valued.
    * This structure can not have any children.
    *
    * @param name Name of the Structure
    * @param value Strucutre's Integer value
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name, final Integer value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   /**
    * Create a new BasicStructure using the provided name and array of values.
    * The structure will be of type INTEGER and it is multi valued.
    * This structure can not have any children.
    *
    * @param name Name of the Structure
    * @param values Array of Integers
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name, final Integer[] values)
   //----------------------------------------------------------------
   {
      super(name, values);
      return;
   }

   /**
    * Create a new BasicStructure using the provided name and value.
    * The structure will be of type LONG and it is single valued.
    * This structure can not have any children.
    *
    * @param name Name of the Structure
    * @param value Strucutre's Long value
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name, final Long value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   /**
    * Create a new BasicStructure using the provided name and array of values.
    * The structure will be of type LONG and it is multi valued.
    * This structure can not have any children.
    *
    * @param name Name of the Structure
    * @param values Array of Longs
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name, final Long[] values)
   //----------------------------------------------------------------
   {
      super(name, values);
      return;
   }

   /**
    * Create a new BasicStructure using the provided name and value.
    * The structure will be of type BOOLEAN and it is single valued.
    * This structure can not have any children.
    *
    * @param name Name of the Structure
    * @param value boolean
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name, final boolean value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   /**
    * Create a new BasicStructure using the provided name and value.
    * The structure will be of type BOOLEAN and it is single valued.
    * This structure can not have any children.
    *
    * @param name Name of the Structure
    * @param value Boolean
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name, final Boolean value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   /**
    * Create a new BasicStructure using the provided name and array of values.
    * The structure will be of type BOOLEAN and it is multi valued.
    * This structure can not have any children.
    *
    * @param name Name of the Structure
    * @param values Array of Boolean values
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name, final Boolean[] values)
   //----------------------------------------------------------------
   {
      super(name, values);
      return;
   }

   /**
    * Create a new BasicStructure using the provided name and value.
    * The structure will be of type OBJECT and it is single valued.
    * This structure can not have any children.
    *
    * @param name Name of the Structure
    * @param value Object
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name, final Object value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   /**
    * Create a new BasicStructure using the provided name and array of values.
    * The structure will be of type OBJECT and it is multi valued.
    * This structure can not have any children.
    *
    * @param name Name of the Structure
    * @param values Array of Object values
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name, final Object[] values)
   //----------------------------------------------------------------
   {
      super(name, values);
      return;
   }

   /**
    * Create a new BasicStructure using the provide name and Structure.
    * The structure will be of type STRUCTURE.
    *
    * @param name Name of the Structure
    * @param struct The child Structure
    */
   //----------------------------------------------------------------
   public BasicStructure(final String name, final StructureIF struct)
   //----------------------------------------------------------------
   {
      super(name, struct);
      return;
   }
}
