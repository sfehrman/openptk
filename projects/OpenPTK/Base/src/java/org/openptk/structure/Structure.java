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
 * Portions Copyright 2011-2012, Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.openptk.api.State;
import org.openptk.exception.StructureException;

/**
 * The abstract base class for a Structure.
 * Provides core method implementations.
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Structure implements StructureIF
//===================================================================
{

   private boolean _multivalued = false;
   private String CLASS_NAME = this.getClass().getSimpleName();
   private String _name = null;
   private StructureIF _parent = null;
   private StructureType _type = null;
   private List<Object> _values = null;
   private List<StructureIF> _children = null;
   private State _state = State.NEW;
   private Properties _props = null;

   /**
    * Create a new Structure using the provided name.
    * The structure will <b>NOT</b> have a type. This can either contain
    * a value OR contain children.
    *
    * @param name Name of the Structure
    */
   //----------------------------------------------------------------
   public Structure(final String name)
   //----------------------------------------------------------------
   {
      _name = name;
      return;
   }

   /**
    * Create a new Structure using the provided name and value.
    * The structure will be of type STRING and it is single valued.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param value Structure value
    */
   //----------------------------------------------------------------
   public Structure(final String name, final String value)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = StructureType.STRING;

      if (value != null && value.length() > 0)
      {
         try
         {
            this.storeValue(value);
         }
         catch (StructureException ex)
         {
         }
      }

      return;
   }

   /**
    * Create a new Structure using the provided name and List of values.
    * The structure will be of type STRING and it is multi valued.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param values List of values
    */
   //----------------------------------------------------------------
   public Structure(final String name, final List<String> values)
   //----------------------------------------------------------------
   {
      Iterator<String> iter = null;

      _name = name;
      _type = StructureType.STRING;
      _multivalued = true;

      if (values != null && !values.isEmpty())
      {
         iter = values.iterator();
         try
         {
            while (iter.hasNext())
            {
               this.storeValue(iter.next());
            }
         }
         catch (StructureException ex)
         {
         }
      }

      return;
   }

   /**
    * Create a new Structure using the provided name and Array of values.
    * The structure will be of type STRING and it is multi valued.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param values Array of values
    */
   //----------------------------------------------------------------
   public Structure(final String name, final String[] values)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = StructureType.STRING;

      if (values != null && values.length > 0)
      {
         for (String value : values)
         {
            try
            {
               this.storeValue(value);
            }
            catch (StructureException ex)
            {
               // this a contructor, does not add children
               break;
            }
         }
      }

      return;
   }

   /**
    * Create a new Structure using the provided name and value.
    * The structure will be of type INTEGER and it is single valued.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param value Structure integer value
    */
   //----------------------------------------------------------------
   public Structure(final String name, final int value)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = StructureType.INTEGER;

      try
      {
         this.storeValue(new Integer(value));
      }
      catch (StructureException ex)
      {
      }

      return;
   }

   /**
    * Create a new Structure using the provided name and value.
    * The structure will be of type INTEGER and it is single valued.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param value Structure Integer value
    */
   //----------------------------------------------------------------
   public Structure(final String name, final Integer value)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = StructureType.INTEGER;

      if (value != null)
      {
         try
         {
            this.storeValue(value);
         }
         catch (StructureException ex)
         {
         }
      }

      return;
   }

   /**
    * Create a new Structure using the provided name and array of values.
    * The structure will be of type INTEGER and it is multi valued.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param values Array of Integers
    */
   //----------------------------------------------------------------
   @SuppressWarnings("cast")
   public Structure(final String name, final Integer[] values)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = StructureType.INTEGER;

      if (values != null && values.length > 0)
      {
         for (int i = 0; i < values.length; i++)
         {
            try
            {
               this.storeValue((Integer) values[i]);
            }
            catch (StructureException ex)
            {
               break;
            }
         }
      }

      return;
   }

   /**
    * Create a new Structure using the provided name and value.
    * The structure will be of type LONG and it is single valued.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param value Structure long value
    */
   //----------------------------------------------------------------
   public Structure(final String name, final long value)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = StructureType.LONG;

      try
      {
         this.storeValue(new Long(value));
      }
      catch (StructureException ex)
      {
      }

      return;
   }

   /**
    * Create a new Structure using the provided name and value.
    * The structure will be of type LONG and it is single valued.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param value Structure Long value
    */
   //----------------------------------------------------------------
   public Structure(final String name, final Long value)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = StructureType.LONG;

      if (value != null)
      {
         try
         {
            this.storeValue(value);
         }
         catch (StructureException ex)
         {
         }
      }

      return;
   }

   /**
    * Create a new Structure using the provided name and array of values.
    * The structure will be of type LONG and it is multi valued.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param values Array of Longs
    */
   //----------------------------------------------------------------
   @SuppressWarnings("cast")
   public Structure(final String name, final Long[] values)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = StructureType.LONG;

      if (values != null && values.length > 0)
      {
         for (int i = 0; i < values.length; i++)
         {
            try
            {
               this.storeValue((Long) values[i]);
            }
            catch (StructureException ex)
            {
               break;
            }
         }
      }

      return;
   }

   /**
    * Create a new Structure using the provided name and value.
    * The structure will be of type BOOLEAN and it is single valued.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param value boolean
    */
   //----------------------------------------------------------------
   public Structure(final String name, final boolean value)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = StructureType.BOOLEAN;

      try
      {
         this.storeValue(Boolean.valueOf(value));
      }
      catch (StructureException ex)
      {
      }

      return;
   }

   /**
    * Create a new Structure using the provided name and value.
    * The structure will be of type BOOLEAN and it is single valued.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param value Boolean
    */
   //----------------------------------------------------------------
   public Structure(final String name, final Boolean value)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = StructureType.BOOLEAN;

      if (value != null)
      {
         try
         {
            this.storeValue(value);
         }
         catch (StructureException ex)
         {
         }
      }

      return;
   }

   /**
    * Create a new Structure using the provided name and array of values.
    * The structure will be of type BOOLEAN and it is multi valued.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param values Array of Boolean values
    */
   //----------------------------------------------------------------
   public Structure(final String name, final Boolean[] values)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = StructureType.BOOLEAN;

      if (values != null && values.length > 0)
      {
         for (int i = 0; i < values.length; i++)
         {
            try
            {
               this.storeValue(values[i]);
            }
            catch (StructureException ex)
            {
               // this a contructor, does not add children
               break;
            }
         }
      }

      return;
   }

   /**
    * Create a new Structure using the provided name and value.
    * The structure will be of type OBJECT and it is single valued.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param value Object
    */
   //----------------------------------------------------------------
   public Structure(final String name, final Object value)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = StructureType.OBJECT;

      if (value != null)
      {
         try
         {
            this.storeValue(value);
         }
         catch (StructureException ex)
         {
            // this a contructor, does not add children
         }
      }

      return;
   }

   /**
    * Create a new Structure using the provided name and array of values.
    * The structure will be of type OBJECT and it is multi valued.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param values Array of Object values
    */
   //----------------------------------------------------------------
   public Structure(final String name, final Object[] values)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = StructureType.OBJECT;

      if (values != null && values.length > 0)
      {
         for (int i = 0; i < values.length; i++)
         {
            try
            {
               this.storeValue(values[i]);
            }
            catch (StructureException ex)
            {
               // this a contructor, does not add children
               break;
            }
         }
      }

      return;
   }

   /**
    * Create a new Structure using the provided name and Structure.
    * The structure will be of type STRUCTURE.
    * This structure can <b>NOT</b> have children.
    *
    * @param name Name of the Structure
    * @param value The Structure
    */
   //----------------------------------------------------------------
   public Structure(final String name, final StructureIF value)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = StructureType.STRUCTURE;

      if (value != null)
      {
         try
         {
            this.storeValue(value);
         }
         catch (StructureException ex)
         {
            // this a contructor, does not add children
         }
      }

      value.setParent(this);

      return;
   }

   /**
    * Set the Structure name.
    *
    * @param name Structure name
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setName(final String name)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         _name = name;
      }

      return;
   }

   /**
    * Get the Structure name.
    *
    * @return String The Structure name
    */
   //----------------------------------------------------------------
   @Override
   public final String getName()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_name != null)
      {
         str = new String(_name); // always return a copy
      }

      return str;
   }

   /**
    * Set to TRUE if the value will support multiple values
    *
    * @param value boolean to "flag" if value is multi-valued
    * @since 2.2.0
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setMultiValued(boolean value)
   //----------------------------------------------------------------
   {
      _multivalued = value;
      return;
   }

   /**
    * Returns TRUE if configured to support multiple values
    * Will be implicitly set to TRUE if there is more than one value.
    *
    * @return boolean True if supports multiple values
    * @since 2.2.0
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized boolean isMultiValued()
   //----------------------------------------------------------------
   {
      boolean bool = false;

      bool = _multivalued;

      return bool;
   }

   /**
    * Add String to the Structure value.
    * Will throw a StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value type does NOT match the existing type
    * <li>Structure has children
    * </ul>
    *
    * @param value String to add
    * @throws StructureException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addValue(final String value) throws StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      if (value == null)
      {
         throw new StructureException(METHOD_NAME + "value is null");
      }

      if (_type == null)
      {
         _type = StructureType.STRING;
      }
      else if (_type != StructureType.STRING)
      {
         throw new StructureException(METHOD_NAME
            + "Types do not match (" + _type.toString()
            + "), must be STRING, name='" + _name + "' value='" + value + "'");
      }

      this.storeValue(value);

      return;
   }

   /**
    * Add Boolean to the Structure value.
    * Will throw a StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value type does NOT match an existing non-null type
    * <li>Structure has children
    * </ul>
    *
    * @param value Boolean to add
    * @throws StructureException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addValue(final Boolean value) throws StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      if (value == null)
      {
         throw new StructureException(METHOD_NAME + "value is null");
      }

      if (_type == null)
      {
         _type = StructureType.BOOLEAN;
      }
      else if (_type != StructureType.BOOLEAN)
      {
         throw new StructureException(METHOD_NAME
            + "Types do not match (" + _type.toString()
            + "), must be BOOLEAN, name='" + _name + "' value='" + value.toString() + "'");
      }

      this.storeValue(value);

      return;
   }

   /**
    * Add boolean to the Structure value.
    * Will throw a StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value type does NOT match an existing non-null type
    * <li>Structure has children
    * </ul>
    *
    * @param value boolean to add
    * @throws StructureException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addValue(final boolean value) throws StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      if (_type == null)
      {
         _type = StructureType.BOOLEAN;
      }
      else if (_type != StructureType.BOOLEAN)
      {
         throw new StructureException(METHOD_NAME
            + "Types do not match (" + _type.toString()
            + "), must be BOOLEAN, name='" + _name + "' value='" + value + "'");
      }

      this.storeValue(Boolean.valueOf(value));

      return;
   }

   /**
    * Add Integer to the Structure value.
    * Will throw a StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value type does NOT match an existing non-null type
    * <li>Structure has children
    * </ul>
    *
    * @param value Integer to add
    * @throws StructureException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addValue(final Integer value) throws StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      if (value == null)
      {
         throw new StructureException(METHOD_NAME + "value is null");
      }

      if (_type == null)
      {
         _type = StructureType.INTEGER;
      }
      else if (_type != StructureType.INTEGER)
      {
         throw new StructureException(METHOD_NAME
            + "Types do not match (" + _type.toString()
            + "), must be INTEGER, name='" + _name + "' value='" + value.toString() + "'");
      }

      this.storeValue(value);

      return;
   }

   /**
    * Add integer to the Structure value.
    * Will throw a StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value type does NOT match an existing non-null type
    * <li>Structure has children
    * </ul>
    *
    * @param value integer to add
    * @throws StructureException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addValue(final int value) throws StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      if (_type == null)
      {
         _type = StructureType.INTEGER;
      }
      else if (_type != StructureType.INTEGER)
      {
         throw new StructureException(METHOD_NAME
            + "Types do not match (" + _type.toString()
            + "), must be INTEGER, name='" + _name + "' value='" + value + "'");
      }

      this.storeValue(new Integer(value));

      return;
   }

   /**
    * Add Long to the Structure value.
    * Will throw a StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value type does NOT match an existing non-null type
    * <li>Structure has children
    * </ul>
    *
    * @param value Long to add
    * @throws StructureException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addValue(final Long value) throws StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      if (value == null)
      {
         throw new StructureException(METHOD_NAME + "value is null");
      }

      if (_type == null)
      {
         _type = StructureType.LONG;
      }
      else if (_type != StructureType.LONG)
      {
         throw new StructureException(METHOD_NAME
            + "Types do not match (" + _type.toString()
            + "), must be LONG, name='" + _name + "' value='" + value.toString() + "'");
      }

      this.storeValue(value);

      return;
   }

   /**
    * Add long to the Structure value.
    * Will throw a StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value type does NOT match an existing non-null type
    * <li>Structure has children
    * </ul>
    *
    * @param value long to add
    * @throws StructureException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addValue(final long value) throws StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      if (_type == null)
      {
         _type = StructureType.LONG;
      }
      else if (_type != StructureType.LONG)
      {
         throw new StructureException(METHOD_NAME
            + "Types do not match (" + _type.toString()
            + "), must be LONG, name='" + _name + "' value='" + value + "'");
      }

      this.storeValue(new Long(value));

      return;
   }

   /**
    *
    * Add StructureIF as a value.
    * Will throw a StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value type does NOT match an existing type
    * <li>Structure has children
    * </ul>
    *
    * @param value StructureIF to add
    * @throws StructureException
    * @since 2.2.0
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addValue(final StructureIF value) throws StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      if (value == null)
      {
         throw new StructureException("value is null");
      }

      if (_type == null)
      {
         _type = StructureType.STRUCTURE;
      }
      else if (_type != StructureType.STRUCTURE)
      {
         throw new StructureException(METHOD_NAME
            + "Types do not match (" + _type.toString()
            + "), must be STRUCTURE, name='" + _name + "' value='" + value.toString() + "'");
      }

      this.storeValue(value);
      value.setParent(this);

      return;
   }

   /**
    * Add Object to the Structure value.
    * Will throw a StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value type does NOT match an existing non-null type
    * <li>Structure has children
    * </ul>
    *
    * @param value Object to add
    * @throws StructureException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addValue(final Object value) throws StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      if (value == null)
      {
         throw new StructureException("value is null");
      }

      if (_type == null)
      {
         _type = StructureType.OBJECT;
      }
      else if (_type != StructureType.OBJECT)
      {
         throw new StructureException(METHOD_NAME
            + "Types do not match (" + _type.toString()
            + "), must be OBJECT, name='" + _name + "' value='" + value.toString() + "'");
      }

      this.storeValue(value);

      return;
   }

   /**
    * Returns the List of values (Object).
    * An ordered list (order of insertion) of values.
    * Use the <tt>getValueType()</tt> method to determine the type.
    * All values will be of the same type.
    *
    * @return List ordered list of values
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized List<Object> getValues()
   //----------------------------------------------------------------
   {
      List<Object> list = null;

      if (_values != null)
      {
         list = _values;
      }

      return list;
   }

   /**
    * Returns the value (if single valued) or the first value (is multi valued)
    * If there is no value, null will be returned.
    *
    * @return Object the (first) value
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized Object getValue()
   //----------------------------------------------------------------
   {
      Object obj = null;

      if (_values != null && !_values.isEmpty())
      {
         obj = _values.get(0);
      }

      return obj;
   }

   /**
    * Returns a String representation of the (first) value.
    *
    * @return String representation of the (first) value
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getValueAsString()
   //----------------------------------------------------------------
   {
      /*
       * This is a "convience" method that returns a String containing
       * the "String" representation of ONLY the first
       * item in the LinkedList
       */

      Object obj = null;
      String str = null;

      if (_values != null && !_values.isEmpty())
      {
         obj = _values.get(0);
         if (obj != null)
         {
            switch (_type)
            {
               case STRING:
                  str = (String) obj;
                  break;
               case INTEGER:
                  str = ((Integer) obj).toString();
                  break;
               case LONG:
                  str = ((Long) obj).toString();
                  break;
               case BOOLEAN:
                  str = ((Boolean) obj).toString();
                  break;
               case STRUCTURE:
                  str = ((StructureIF) obj).toString();
                  break;
               case OBJECT:
                  str = obj.toString();
                  break;
            }
         }
      }

      return str;
   }

   /**
    * Returns an Array of values (Object).
    * An ordered array (order of insertion) of values.
    * Use the <b>getValueType()</b> method to determine the value real type.
    * All values will be of the same type.
    *
    * @return Object[] ordered array of values
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized Object[] getValuesAsArray()
   //----------------------------------------------------------------
   {
      Object[] array = null;

      if (_values != null)
      {
         array = _values.toArray(new Object[_values.size()]);
      }
      else
      {
         array = new Object[0]; // return empty array, instead of null
      }

      return array;
   }

   /**
    * Returns a String representation of all (List) the values.
    *
    * @return String representation of all (List) the values.
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getValuesAsString()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_values != null)
      {
         str = _values.toString();
      }

      return str;
   }

   /**
    * Returns the Structure Type.
    *
    * @return StructureType Structure Type
    */
   //----------------------------------------------------------------
   @Override
   public final StructureType getValueType()
   //----------------------------------------------------------------
   {
      return _type;
   }

   /**
    * Returns the Structure Parent (if it is a Child).
    * This will be null if it is not a Child.
    *
    * @return StructureIF Parent Structure
    */
   //----------------------------------------------------------------
   @Override
   public final StructureIF getParent()
   //----------------------------------------------------------------
   {
      return _parent;
   }

   /**
    * Set the Structure's parent.
    * <br/>
    * <p>
    * <b>Note:</b> In most situation this method should <b>not</b> be used / needed.
    * The Child/Parent relationship is maintained by the
    * <b>addChild()</b> and <b>setChild()</b> methods.
    * </p>
    *
    * @param struct Parent Structure
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setParent(final StructureIF struct)
   //----------------------------------------------------------------
   {
      _parent = struct;
      return;
   }

   /**
    * Adds the provided Structure as a Child.
    * Will throw a StructureException if:
    * <ul>
    * <li>Child Structure is null
    * <li>Structure contains a value
    * <li>Structure Type is invalid
    * </ul>
    *
    * @param child Child Structure
    * @throws StructureException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addChild(final StructureIF child) throws StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String nameChild = null;

      if (child == null)
      {
         throw new StructureException(METHOD_NAME + "Child Structure is null.");
      }

      nameChild = child.getName();
      if (nameChild == null || nameChild.length() < 1)
      {
         throw new StructureException(METHOD_NAME + "Child Structure MUST have a name");
      }

      if (_values != null)
      {
         /*
          * can not add a child if it has a value
          */
         throw new StructureException(METHOD_NAME
            + "Structure has a value, Can not add child, name='" + _name + "'");
      }

      if (_type == null)
      {
         _type = StructureType.PARENT;
      }
      else if (_type != StructureType.PARENT && _type != StructureType.CONTAINER)
      {
         throw new StructureException(METHOD_NAME
            + "Types do not match, must be PARENT or CONTAINER, name='" + _name + "'");
      }


      if (_children == null)
      {
         _children = new LinkedList<StructureIF>();
      }

      child.setParent(this);
      _children.add(child);

      return;
   }

   /**
    * Sets / Replaces a specific Child with the provided Structure.
    * Will throw an StructureException if:
    * <ul>
    * <li>Child Structure is null
    * <li>Index is out of bounds
    * <li>Structure contains a value
    * <li>Structure Type is invalid
    * </ul>
    *
    * @param index child offset
    * @param child Child Structure
    * @throws StructureException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setChild(final int index, final StructureIF child) throws StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      if (child == null)
      {
         throw new StructureException(METHOD_NAME + "Child Structure is null.");
      }

      if (_values != null)
      {
         /*
          * can not set a child if the structure has a value
          */
         throw new StructureException(METHOD_NAME
            + "Structure has a value, Can not set child, name='" + _name + "'");
      }

      if (_type == null)
      {
         _type = StructureType.PARENT;
      }
      else if (_type != StructureType.PARENT)
      {
         throw new StructureException(METHOD_NAME
            + "Types do not match, must be PARENT, name='" + _name + "'");
      }

      if (_children == null)
      {
         _children = new LinkedList<StructureIF>();
      }

      if (index < 0 || index >= _children.size())
      {
         throw new StructureException(METHOD_NAME
            + "Index '" + index + "' is out of range, name='" + _name + "'");
      }

      child.setParent(this);

      _children.set(index, child);

      return;
   }

   /**
    * Returns TRUE if a Child exists with the specified name.
    *
    * @param name Child Structure name
    * @return boolean TRUE if has children
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized boolean hasChild(final String name)
   //----------------------------------------------------------------
   {
      boolean bool = false;
      Iterator<StructureIF> iter = null;

      if (name != null && name.length() > 0)
      {
         if (_children != null && !_children.isEmpty())
         {
            iter = _children.iterator();
            while (iter.hasNext())
            {
               if (iter.next().getName().equalsIgnoreCase(name))
               {
                  bool = true;
               }
            }
         }
      }

      return bool;
   }

   /**
    * Returns TRUE if the Structure contains one or more Children.
    *
    * @return boolean contains child(ren)
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized boolean hasChildren()
   //----------------------------------------------------------------
   {
      boolean bool = false;

      if (_children != null && !_children.isEmpty())
      {
         bool = true;
      }

      return bool;
   }

   /**
    * Returns a List (of Children) that match the provided name.
    * All of the Child Structures are search, those that match the
    * provided name will be returned in a List.
    *
    * @param name The child name
    * @return List of matching children
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized List<StructureIF> getChildren(final String name)
   //----------------------------------------------------------------
   {
      StructureIF struct = null;
      List<StructureIF> kids = null;
      Iterator<StructureIF> iter = null;

      if (name != null && name.length() > 0)
      {
         if (_children != null && !_children.isEmpty())
         {
            kids = new LinkedList<StructureIF>();
            iter = _children.iterator();
            while (iter.hasNext())
            {
               struct = iter.next();
               if (struct.getName().equalsIgnoreCase(name))
               {
                  kids.add(struct);
               }
            }
         }
      }

      return kids;
   }

   /**
    * Returns an Structure array (of Children) that match the provided name.
    * All of the Child Structures are search, those that match the
    * provided name will be returned in an array.
    *
    * @param name The child name
    * @return StructureIF[] Array of matching children
    */
   //----------------------------------------------------------------
   @Override
   public final StructureIF[] getChildrenAsArray(final String name)
   //----------------------------------------------------------------
   {
      List<StructureIF> kids = null;
      StructureIF[] array = null;

      if (_children != null)
      {
         kids = this.getChildren(name);
         if (kids != null)
         {
            array = kids.toArray(new StructureIF[kids.size()]);
         }
      }

      if (array == null)
      {
         array = new StructureIF[0]; // return empty array, instead of null
      }

      return array;
   }

   /**
    * Returns all Child Structures as a List.
    * The List is ordered (order of insertion).
    *
    * @return List Child Structures.
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized List<StructureIF> getChildren()
   //----------------------------------------------------------------
   {
      List<StructureIF> list = null;

      if (_children != null)
      {
         list = _children;
      }

      return list;
   }

   /**
    * Returns all Child Structures as an Array.
    * The Array is ordered (order of insertion).
    *
    * @return StructureIF[] Child Structures.
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized StructureIF[] getChildrenAsArray()
   //----------------------------------------------------------------
   {
      StructureIF[] array = null;

      if (_children != null)
      {
         array = _children.toArray(new StructureIF[_children.size()]);
      }
      else
      {
         array = new StructureIF[0];
      }

      return array;
   }

   /**
    * Returns a String Array containing the Children's id (name).
    * The Array is ordered (order of insertion).
    *
    * @return String[] Child ids (names)
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String[] getChildrenIds()
   //----------------------------------------------------------------
   {
      String name = null;
      List<String> ids = null;
      Iterator<StructureIF> iter = null;

      if (_children != null)
      {
         iter = _children.iterator();
         ids = new LinkedList<String>();
         while (iter.hasNext())
         {
            name = iter.next().getName();
            if (!ids.contains(name))
            {
               ids.add(name);
            }
         }
      }

      return ids.toArray(new String[ids.size()]);
   }

   /**
    * Returns the specified Child Structure.
    * If the Child does not exist, the returned Structure is null.
    *
    * @param name Child Structure name
    * @return StructureIF the Child
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized StructureIF getChild(final String name)
   //----------------------------------------------------------------
   {
      StructureIF struct = null;
      Iterator<StructureIF> iter = null;

      if (name != null && name.length() > 0)
      {
         if (_children != null && !_children.isEmpty())
         {
            iter = _children.iterator();
            while (iter.hasNext())
            {
               struct = iter.next();
               if (struct.getName().equalsIgnoreCase(name))
               {
                  break;
               }
               else
               {
                  struct = null;
               }
            }
         }
      }

      return struct;
   }

   /**
    * Set the Structure State.
    *
    * @param state A valid State
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setState(final State state)
   //----------------------------------------------------------------
   {
      _state = state;
      return;
   }

   /**
    * Get the Structure's State.
    *
    * @return State the current state
    */
   //----------------------------------------------------------------
   @Override
   public final State getState()
   //----------------------------------------------------------------
   {
      return _state;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized Properties getProperties()
   //----------------------------------------------------------------
   {
      if (_props == null)
      {
         _props = new Properties();
      }

      return _props;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setProperties(Properties props)
   //----------------------------------------------------------------
   {
      if (props != null)
      {
         _props = props;
      }

      return;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized String getProperty(String key)
   //----------------------------------------------------------------
   {
      String value = null;

      if (_props == null)
      {
         _props = new Properties();
      }
      else
      {
         if (_props.containsKey(key))
         {
            value = _props.getProperty(key);
         }
      }

      return value;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setProperty(String key, String value)
   //----------------------------------------------------------------
   {
      if (key != null && key.length() > 0
         && value != null && value.length() > 0)
      {
         if (_props == null)
         {
            _props = new Properties();
         }
         _props.put(key, value);
      }

      return;
   }

   /**
    * Set the Structure type.
    * This method is <b>NOT</b> typically used.
    * The type is automatically set when a value is added to the Structure.
    * If a type is NULL, it can be set to any value.
    * If the type is PARENT or CONTAINER ... type can be toggled
    *
    * @param type StructureType
    * @throws StructureException
    * @since 2.2
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setType(StructureType type) throws StructureException
   //----------------------------------------------------------------
   {
      if (type != null)
      {
         if (_type == null)
         {
            _type = type;
         }
         else if (_type == type)
         {
            // do nothing ... same types
         }
         else if (_type == StructureType.PARENT && type == StructureType.CONTAINER)
         {
            // can switch from PARENT to CONTAINER
            _type = type;
         }
         else if (_type == StructureType.CONTAINER && type == StructureType.PARENT)
         {
            // can switch from CONTAINER to PARENT
            _type = type;
         }
         else
         {
            throw new StructureException("StructureType is already set, type='" + _type.toString() + "'");
         }
      }
      return;
   }

   /**
    * @return String representation of a structure
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String toString()
   //----------------------------------------------------------------
   {
      int len = 0;
      Object[] objs = null;
      Object obj = null;
      StringBuilder buf = new StringBuilder();
      StructureIF[] kids = null;

      if (_name != null && _name.length() > 0)
      {
         buf.append(_name).append("=");
      }

      kids = this.getChildrenAsArray();

      if (kids.length > 0)
      {
         buf.append("{");
         for (int i = 0; i < kids.length; i++)
         {
            if (i > 0)
            {
               buf.append(";");
            }
            buf.append(kids[i].toString());
         }
         buf.append("}");
      }

      if (_multivalued)
      {
         buf.append("[");
      }
      if (_values != null && !_values.isEmpty())
      {
         objs = this.getValuesAsArray();
         len = objs.length;
         for (int i = 0; i < len; i++)
         {
            if (i > 0)
            {
               buf.append(",");
            }
            obj = objs[i];
            switch (_type)
            {
               case STRING:
                  buf.append("\"").append((String) obj).append("\"");
                  break;
               case BOOLEAN:
                  buf.append(Boolean.toString((Boolean) obj));
                  break;
               case INTEGER:
                  buf.append(Integer.toString((Integer) obj));
                  break;
               case LONG:
                  buf.append(Long.toString((Long) obj)).append("L");
                  break;
               case STRUCTURE:
                  buf.append(((StructureIF) obj).toString());
                  break;
               case OBJECT:
                  buf.append("OBJECT::").append(obj.getClass().getSimpleName());
                  break;
               default:
                  break;
            }
         }
      }
      if (_multivalued)
      {
         buf.append("]");
      }

      return buf.toString();
   }

   /*
    * ***************
    * PRIVATE METHODS
    * ***************
    */
   //----------------------------------------------------------------
   private void storeValue(final Object obj) throws StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      /*
       * Can not add a value if there are children
       */

      if (_children == null)
      {
         if (_values == null)
         {
            _values = new LinkedList<Object>();
         }
         _values.add(obj);
      }
      else
      {
         throw new StructureException(METHOD_NAME
            + "Structure has children, Can not add value, name='" + _name + "'");
      }

      if (!_multivalued && _values.size() > 1)
      {
         _multivalued = true;
      }

      return;
   }
}
