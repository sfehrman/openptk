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
 * Portions Copyright 2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.api;

import java.util.Properties;
import java.util.Set;

/**
 *
 * The Attribute is used to store a related name / value and meta-data.
 *
 * An Attribute can be one of the following DataType enumerations,
 * if not specified the default is STRING:
 *
 * <ul>
 * <li>STRING</li>
 * <li>INTEGER</li>
 * <li>BOOLEAN</li>
 * <li>OBJECT</li>
 * <li>LONG</li>
 * </ul>
 *
 * An Attribute has a number of boolean flags, they are all FALSE by default:
 *
 * <ul>
 * <li>isRequired</li>
 * <li>isEncrypted</li>
 * <li>isMultivalued</li>
 * <li>allowMultivalue</li>
 * <li>isReadOnly</li>
 * <li>isVirtual</li>
 * </ul>
 *
 * If the value is an Array (of the type) it is designated as being multi-valued
 * Else the value is designed as not being multi-valued (default)
 *
 * The default <b>State</b> of an Attribute is NEW
 *
 * An Attribute can also have name/value Properties
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * <br>
 * contributor: Derrick Harcey, Sun Microsystems, Inc.
 *
 */
//===================================================================
public class Attribute implements AttributeIF
//===================================================================
{
   private boolean _required = false;
   private boolean _encrypted = false;
   private boolean _isMultivalued = false;
   private boolean _allowMultivalue = false;
   private boolean _isReadOnly = false;
   private boolean _isVirtual = false;
   private String _name = null;
   private Object _value = null;
   private Properties _props = null;
   private DataType _type = DataType.STRING;
   private Access _access = Access.PUBLIC;
   private State _state = State.NEW;

   /**
    * Creates a new Attribute from an existing Attribute.
    *
    * The content of the provided Attribute is copied into the new Attribute
    * The new Attribute is a "deep" copy of the referenced Attribute
    *
    * @param attribute an existing Attribute
    */
   //----------------------------------------------------------------
   public Attribute(final AttributeIF attribute)
   //----------------------------------------------------------------
   {
      _required = attribute.isRequired();
      _encrypted = attribute.isEncrypted();
      _allowMultivalue = attribute.allowMultivalue();
      _isMultivalued = attribute.isMultivalued();
      _type = attribute.getType();
      _name = attribute.getName();
      _value = attribute.getValue();
      _access = attribute.getAccess();
      _state = attribute.getState();
      _isReadOnly = attribute.isReadOnly();
      _isVirtual = attribute.isVirtual();
      _props = attribute.getProperties();

      return;
   }

   /** 
    * Creates a new Attribute using the provided name.
    *
    * The DataType will be set to STRING.
    * The value will be NULL.
    *
    * @param name the Attributes name
    */
   //----------------------------------------------------------------
   public Attribute(final String name)
   //----------------------------------------------------------------
   {
      _name = name;
      return;
   }

   /**
    * Creates a new Attribute using the provided name and value.
    *
    * The DataType will be set to OBJECT, this can not be multi-valued.
    *
    * @param name the Attributes name
    * @param value the Attributes value, Java Object
    */
   //----------------------------------------------------------------
   public Attribute(final String name, final Object value)
   //----------------------------------------------------------------
   {
      _name = name;
      this.setValue(value);
      return;
   }

   /**
    * Creates a new Attribute using the provided name and value.
    *
    * The DataType will be set to OBJECT, this will be multivalued.
    *
    * @param name the Attributes name
    * @param value the Attributes value, Java Object[]
    */
   //----------------------------------------------------------------
   public Attribute(final String name, final Object[] value)
   //----------------------------------------------------------------
   {
      _name = name;
      this.setValue(value);
      return;
   }

   /**
    * Creates a new Attribute using the provided name.
    *
    * The DataType will be set to BOOLEAN.
    * The value will be set using the provided value.
    *
    * @param  name the Attributes name
    * @param  value the Attributes value [ true | false ]
    */
   //----------------------------------------------------------------
   public Attribute(final String name, final boolean value)
   //----------------------------------------------------------------
   {
      _name = name;
      this.setValue(value);
      return;
   }

   /**
    * Creates a new Attribute using the provided name.
    *
    * The DataType will be set to BOOLEAN, this will be multivalued.
    * The value will be set using the provided value.
    *
    * @param  name the Attributes name
    * @param  value the Attributes value, Boolean[]
    */
   //----------------------------------------------------------------
   public Attribute(final String name, final boolean[] value)
   //----------------------------------------------------------------
   {
      _name = name;
      this.setValue(value);
      return;
   }

   /**
    * Creates a new Attribute using the provided name.
    *
    * The DataType will be set to BOOLEAN.
    * The value will be set using the provided value.
    *
    * @param  name the Attributes name
    * @param  value the Attributes value, Integer [ true | false ]
    */
   //----------------------------------------------------------------
   public Attribute(final String name, final Boolean value)
   //----------------------------------------------------------------
   {
      _name = name;
      this.setValue(value);
      return;
   }

   /**
    * Creates a new Attribute using the provided name.
    *
    * The DataType will be set to BOOLEAN, this will be multivalued
    * The value will be set using the provided value.
    *
    * @param  name the Attributes name
    * @param  value the Attributes value, Boolean[]
    */
   //----------------------------------------------------------------
   public Attribute(final String name, final Boolean[] value)
   //----------------------------------------------------------------
   {
      _name = name;
      this.setValue(value);
      return;
   }

   /**
    * Creates a new Attribute using the provided name.
    *
    * The DataType will be set to INTEGER.
    * The value will be set using the provided value.
    *
    * @param  name the Attributes name
    * @param  value the Attributes value, Integer
    */
   //----------------------------------------------------------------
   public Attribute(final String name, final int value)
   //----------------------------------------------------------------
   {
      _name = name;
      this.setValue(value);
      return;
   }

   /**
    * Creates a new Attribute using the provided name.
    *
    * The DataType will be set to INTEGER.
    * The value will be set using the provided value, this will be multivalued
    *
    * @param  name the Attributes name
    * @param  value the Attributes value, Integer[]
    */
   //----------------------------------------------------------------
   public Attribute(final String name, final int[] value)
   //----------------------------------------------------------------
   {
      _name = name;
      this.setValue(value);
      return;
   }

   /**
    * Creates a new Attribute using the provided name.
    *
    * The DataType will be set to INTEGER.
    * The value will be set using the provided value.
    *
    * @param  name the Attributes name
    * @param  value the Attributes value, Integer
    */
   //----------------------------------------------------------------
   public Attribute(final String name, final Integer value)
   //----------------------------------------------------------------
   {
      _name = name;
      this.setValue(value);
      return;
   }

   /**
    * Creates a new Attribute using the provided name.
    *
    * The DataType will be set to INTEGER.
    * The value will be set using the provided value, this will be multivalued
    *
    * @param  name the Attributes name
    * @param  value the Attributes value, Integer[]
    */
   //----------------------------------------------------------------
   public Attribute(final String name, final Integer[] value)
   //----------------------------------------------------------------
   {
      _name = name;
      this.setValue(value);
      return;
   }

   /**
    * Creates a new Attribute using the provided name.
    *
    * The DataType will be set to LONG.
    * The value will be set using the provided value.
    *
    * @param  name the Attributes name
    * @param  value the Attributes value, Long
    */
   //----------------------------------------------------------------
   public Attribute(final String name, final Long value)
   //----------------------------------------------------------------
   {
      _name = name;
      this.setValue(value);
      return;
   }

   /**
    * Creates a new Attribute using the provided name.
    *
    * The DataType will be set to LONG.
    * The value will be set using the provided value, this will be multivalued
    *
    * @param  name the Attributes name
    * @param  value the Attributes value, Long[]
    */
   //----------------------------------------------------------------
   public Attribute(final String name, final Long[] value)
   //----------------------------------------------------------------
   {
      _name = name;
      this.setValue(value);
      return;
   }

   /**
    * Creates a new Attribute using the provided name.
    *
    * The DataType will be set to STRING.
    * The value will be set using the provided value.
    *
    * @param  name the Attributes name
    * @param  value the Attributes value, String
    */
   //----------------------------------------------------------------
   public Attribute(final String name, final String value)
   //----------------------------------------------------------------
   {
      _name = name;
      this.setValue(value);
      return;
   }

   /**
    * Creates a new Attribute using the provided name.
    *
    * The DataType will be set to STRING.
    * The value will be set using the provided value, this will be multivalued
    *
    * @param name the Attributes name
    * @param value the Attributes value, String[]
    */
   //----------------------------------------------------------------
   public Attribute(final String name, final String[] value)
   //----------------------------------------------------------------
   {
      _name = name;
      this.setValue(value);
      return;
   }

   /**
    * Returns a "deep" copy of the Attribute.
    *
    * The new Attribute will have a copy of the name, value, meta-data
    * and Properties from the referenced Attribute.
    *
    * @return AttributeIF a copy of the Attribute
    */
   //----------------------------------------------------------------
   @Override
   public AttributeIF copy()
   //----------------------------------------------------------------
   {
      return new Attribute(this);
   }

   /**
    * Return TRUE if the Attribute allows multivalues.
    *
    * @return boolean are multivalues allowed
    */
   //----------------------------------------------------------------
   @Override
   public final boolean allowMultivalue()
   //----------------------------------------------------------------
   {
      boolean val = false;
      val = _allowMultivalue;
      return val;
   }

   /**
    * Returns TRUE if the value is encrypted.
    *
    * @return boolean is the value encrypted
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isEncrypted()
   //----------------------------------------------------------------
   {
      boolean val = false;
      val = _encrypted;
      return val;
   }

   /**
    * Return TRUE if the actual value is contains muliple values.
    *
    * @return boolean is it multi-valued
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isMultivalued()
   //----------------------------------------------------------------
   {
      boolean val = false;
      val = _isMultivalued;
      return val;
   }

   /**
    * Set the flag to define if the Attribute allows multivalues.
    *
    * @param value
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setAllowMultivalue(final boolean value)
   //----------------------------------------------------------------
   {
      _allowMultivalue = value;
      return;
   }

   /**
    * Set the flag to define if the Attribute value is encrypted.
    *
    * @param bEncrypted
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setEncrypted(final boolean bEncrypted)
   //----------------------------------------------------------------
   {
      _encrypted = bEncrypted;
      return;
   }

   /**
    * Set the Attributes type (enum).
    * Notice:  The type is automatically set based on the
    * Attributes value.  If the type does not match the value, there
    * will be unpredictable behavior.
    *
    * @param type DataType
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setType(final DataType type)
   //----------------------------------------------------------------
   {
      if (type != null)
      {
         _type = type;
      }

      return;
   }

   /**
    * Set the Attributes value to the single Object.
    * The Type will be automatically set.
    *
    * @param value Object
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final Object value)
   //----------------------------------------------------------------
   {
      this.storeValue(value, DataType.OBJECT, false);
      return;
   }

   /**
    * Set the Attributes value to the Object array.
    * The Type will be automatically set.
    *
    * @param value Object[]
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final Object[] value)
   //----------------------------------------------------------------
   {
      this.storeValue(value, DataType.OBJECT, true);
      return;
   }

   /**
    * Set the Attributes value to a single Boolean.
    * The Type will be automatically set.
    *
    * @param  value boolean
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final boolean value)
   //----------------------------------------------------------------
   {
      this.storeValue(Boolean.valueOf(value), DataType.BOOLEAN, false);
      return;
   }

   /**
    * Set the Attributes value to a Boolean array.
    * The Type will be automatically set.
    *
    * @param  value boolean[]
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final boolean[] value)
   //----------------------------------------------------------------
   {
      Boolean[] iArray = null;
      if (value != null && value.length > 0)
      {
         iArray = new Boolean[value.length];
         for (int i = 0; i < value.length; i++)
         {
            iArray[i] = Boolean.valueOf(value[i]);
         }
      }
      this.storeValue(iArray, DataType.BOOLEAN, true);
      return;
   }

   /**
    * Set the Attributes value to a single Boolean.
    * The Type will be automatically set.
    *
    * @param  value Boolean
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final Boolean value)
   //----------------------------------------------------------------
   {
      this.storeValue(value, DataType.BOOLEAN, false);
      return;
   }

   /**
    * Set the Attributes value to a Boolean array.
    * The Type will be automatically set.
    *
    * @param  value Boolean[]
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final Boolean[] value)
   //----------------------------------------------------------------
   {
      this.storeValue(value, DataType.BOOLEAN, true);
      return;
   }

   /**
    * Set the Attributes value to a single Integer.
    * The Type will be automatically set.
    *
    * @param  value int
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final int value)
   //----------------------------------------------------------------
   {
      this.storeValue(Integer.valueOf(value), DataType.INTEGER, false);
      return;
   }

   /**
    * Set the Attributes value to a Integer array.
    * The Type will be automatically set.
    *
    * @param value int[]
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final int[] value)
   //----------------------------------------------------------------
   {
      Integer[] iArray = null;
      if (value != null && value.length > 0)
      {
         iArray = new Integer[value.length];
         for (int i = 0; i < value.length; i++)
         {
            iArray[i] = Integer.valueOf(value[i]);
         }
      }
      this.storeValue(iArray, DataType.INTEGER, true);
      return;
   }

   /**
    * Set the Attributes value to a single Integer.
    * The Type will be automatically set.
    *
    * @param  value Integer
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final Integer value)
   //----------------------------------------------------------------
   {
      this.storeValue(value, DataType.INTEGER, false);
      return;
   }

   /**
    * Set the Attributes value to a Integer array.
    * The Type will be automatically set.
    *
    * @param value Integer[]
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final Integer[] value)
   //----------------------------------------------------------------
   {
      this.storeValue(value, DataType.INTEGER, true);
      return;
   }

   /**
    * Set the Attributes value to a single Long.
    * The Type will be automatically set.
    *
    * @param  value Long
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final Long value)
   //----------------------------------------------------------------
   {
      this.storeValue(value, DataType.LONG, false);
      return;
   }

   /**
    * Set the Attributes value to a Long array.
    * The Type will be automatically set.
    *
    * @param value Long[]
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final Long[] value)
   //----------------------------------------------------------------
   {
      this.storeValue(value, DataType.LONG, true);
      return;
   }

   /**
    * Set the Attributes value to a single String.
    * The Type will be automatically set.
    *
    * @param  value String
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final String value)
   //----------------------------------------------------------------
   {
      this.storeValue(value, DataType.STRING, false);
      return;
   }

   /**
    * Set the Attributes value to a String array.
    * The Type will be automatically set.
    *
    * @param value String[]
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final String[] value)
   //----------------------------------------------------------------
   {
      this.storeValue(value, DataType.STRING, true);
      return;
   }

   /**
    * Get the Attributes value.
    *
    * @return Object the Attributes value
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized Object getValue()
   //----------------------------------------------------------------
   {
      Object o = null;
      o = _value;
      return o;
   }

   /**
    * Get the Attributes name.
    *
    * @return String the Attributes name
    */
   //----------------------------------------------------------------
   @Override
   public final String getName()
   //----------------------------------------------------------------
   {
      String val = null;

      if (_name != null)
      {
         val = new String(_name); // always return a copy
      }

      return val;
   }

   /**
    * Get the Attributes type.
    *
    * @return DataType the Attributes type
    */
   //----------------------------------------------------------------
   @Override
   public final DataType getType()
   //----------------------------------------------------------------
   {
      DataType type = null;
      type = _type;
      return type;
   }

   /**
    * Get the Attributes type, as a String.
    *
    * @return String the String representation of the type
    */
   //----------------------------------------------------------------
   @Override
   public final String getTypeAsString()
   //----------------------------------------------------------------
   {
      String val = null;

      if (_type != null)
      {
         val = _type.toString();
      }

      return val;
   }

   /**
    * Set the Attributes required flag.
    *
    * @param  bool flag to define if the Attribute is required
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setRequired(final boolean bool)
   //----------------------------------------------------------------
   {
      _required = bool;
      return;
   }

   /**
    * Get the Attributes required flag.
    *
    * @return boolean the required flag
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isRequired()
   //----------------------------------------------------------------
   {
      boolean val = false;
      val = _required;
      return val;
   }

   /**
    * Set the Attributes read only flag.
    *
    * @param  value flag to define if the Attribute is read only
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setReadOnly(final boolean value)
   //----------------------------------------------------------------
   {
      _isReadOnly = value;
      return;
   }

   /**
    * Get the Attributes read only flag.
    *
    * @return boolean the read only flag
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isReadOnly()
   //----------------------------------------------------------------
   {
      boolean val = false;
      val = _isReadOnly;
      return val;
   }

   /**
    * Set the Attributes virtual flag.
    *
    * @param  bool flag to define if the Attribute is virtual
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setVirtual(final boolean bool)
   //----------------------------------------------------------------
   {
      _isVirtual = bool;
      _isReadOnly = true;
      return;
   }

   /**
    * Get the Attributes virtual flag.
    *
    * @return boolean the virtual flag
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isVirtual()
   //----------------------------------------------------------------
   {
      boolean val = false;
      val = _isVirtual;
      return val;
   }

   /**
    * Get the attributes value as a String, regardless of it's internal type.
    * String[] is enclosed with brackets and values are comma delimited
    * Integer and Booleans use their toString() value.
    *
    * @return String a String representation of the internal value
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getValueAsString()
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();
      String[] strArray = null;

      if (_value != null)
      {
         switch (_type)
         {
            case STRING:
            {
               if (this.isMultivalued())
               {
                  strArray = (String[]) _value;
                  buf.append("[");
                  for (int i = 0; i < strArray.length; i++)
                  {
                     buf.append(strArray[i]);
                     if (i < (strArray.length - 1))
                     {
                        buf.append(",");
                     }
                  }
                  buf.append("]");
               }
               else
               {
                  buf.append((String) _value);
               }
               break;
            }
            default:
            {
               buf.append(_value.toString());
               break;
            }
         }
      }

      return buf.toString();
   }

   /**
    * Set the Attributes Access level.
    *
    * @param access Access level
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setAccess(final Access access)
   //----------------------------------------------------------------
   {
      if (access != null)
      {
         _access = access;
      }

      return;
   }

   /**
    * Set the Attribute Access level.
    *
    * @param access String representation of the level
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setAccess(final String access)
   //----------------------------------------------------------------
   {

      if (access != null && access.length() > 0)
      {
         if (access.equalsIgnoreCase(Access.PRIVATE.toString()))
         {
            _access = Access.PRIVATE;
         }
         else if (access.equalsIgnoreCase(Access.PROTECTED.toString()))
         {
            _access = Access.PROTECTED;
         }
         else if (access.equalsIgnoreCase(Access.PUBLIC.toString()))
         {
            _access = Access.PUBLIC;
         }
      }

      return;
   }

   /**
    * Get the Attributes Access level.
    *
    * @return Access the Access level
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized Access getAccess()
   //----------------------------------------------------------------
   {
      Access access = null;

      if (_access != null)
      {
         access = _access;
      }

      return access;
   }

   /**
    * Get the String representation of the Attributes Access level.
    *
    * @return val String representation of Access level
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getAccessAsString()
   //----------------------------------------------------------------
   {
      String val = null;

      if (_access != null)
      {
         val = _access.toString();
      }

      return val;
   }

   /**
    * Set the Attributes State.
    *
    * @param state State
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setState(final State state)
   //----------------------------------------------------------------
   {
      if (state != null)
      {
         _state = state;
      }

      return;
   }

   /**
    * Get the Attributes State.
    *
    * @return state State for the Attribute
    */
   //----------------------------------------------------------------
   @Override
   public final State getState()
   //----------------------------------------------------------------
   {
      State state = null;

      if (_state != null)
      {
         state = _state;
      }

      return state;
   }

   /**
    * Add the key / value as a Property to the Attribute.
    *
    * @param key the name of the Property
    * @param value the value of the Property
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setProperty(final String key, final String value)
   //----------------------------------------------------------------
   {
      if (key != null && key.length() > 0
         && value != null && value.length() > 0)
      {
         if (_props == null)
         {
            _props = new Properties();
         }
         _props.setProperty(key, value);
      }
      return;
   }

   /**
    * Get the Property value from the Attribute.
    * If the property does not exist, a null value is returned.
    * Note: the returned value (String) is a copy of the internal value.
    *
    * @param key the name of the Property
    * @return val the value of the Property
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getProperty(final String key)
   //----------------------------------------------------------------
   {
      String val = null;

      if (key != null && key.length() > 0)
      {
         if (_props != null)
         {
            val = _props.getProperty(key);
            if (val != null)
            {
               val = new String(val); // always return a copy
            }
         }
      }

      return val;
   }

   /**
    * Sets / Replaces the Attributes Properties.
    *
    * @param props a Properties object
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setProperties(final Properties props)
   //----------------------------------------------------------------
   {
      _props = props;
      return;
   }

   /**
    * Returns the Properties for the Attribute.
    * Notice: The returned Properties is a "deep copy",
    * not a reference to the internal Properties.
    *
    * @return Properties the current Attribute properties
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized Properties getProperties()
   //----------------------------------------------------------------
   {
      String key = null;
      String val = null;
      Properties props = null;
      Set<Object> set = null;

      props = new Properties();

      if (_props != null && !_props.isEmpty())
      {
         set = _props.keySet();
         for (Object o : set)
         {
            key = (String) o;
            val = _props.getProperty(key);
            if (val != null)
            {
               val = new String(val); // always return a copy
            }
            props.setProperty(key, val);
         }
      }

      return props;
   }

   /**
    * Returns the quantity of Properties.
    *
    * @return int quantity of Properties
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized int getPropertiesSize()
   //----------------------------------------------------------------
   {
      int size = 0;

      if (_props != null)
      {
         size = _props.size();
      }

      return size;
   }

   /**
    * Get a String that represents the Attribute.
    *
    * The output will contain the name, type and value information
    * 
    * @return String a string that describes the Attribute.
    */
   //----------------------------------------------------------------
   @Override
   public String toString()
   //----------------------------------------------------------------
   {
      String str = null;

      str =
         "name=" + _name
         + ", type=" + this.getTypeAsString()
         + ",value='" + this.getValueAsString()
         + "'";

      return str;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   /**
    * Set the flag for a value, is it multivalued.
    *
    * @param bool boolean is it multivalued.
    */
   //----------------------------------------------------------------
   protected synchronized void setMultiValued(final Boolean bool)
   //----------------------------------------------------------------
   {
      _isMultivalued = bool;
      return;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //
   //----------------------------------------------------------------
   private void storeValue(final Object value, final DataType type, final boolean multivalued)
   //----------------------------------------------------------------
   {

      if (type == DataType.OBJECT)
      {
         /*
          * Test to see if it might be a known object.
          */

         if (value instanceof String)
         {
            _type = DataType.STRING;
         }
         else if (value instanceof Integer)
         {
            _type = DataType.INTEGER;
         }
         else if (value instanceof Long)
         {
            _type = DataType.LONG;
         }
         else
         {
            _type = DataType.OBJECT;
         }
      }
      else
      {
         _type = type;
      }

      _value = value;
      _isMultivalued = multivalued;

      return;
   }
}
