/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2008-2010 Sun Microsystems, Inc.
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

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * <br>
 * contributor: Derrick Harcey, Sun Microsystems, Inc.
 */
//
//===================================================================
public interface AttributeIF
//===================================================================
{

   public static enum Access
   {

      PUBLIC,
      PROTECTED,
      PRIVATE;
   }

   /**
    * Returns a copy of the instance.
    *
    * @return AttributeIF copy of the Attribute instance
    */
   public AttributeIF copy();

   /**
    * Return TRUE if the Attribute allows multivalues.
    *
    * @return boolean are multivalues allowed
    */
   public boolean allowMultivalue();

   /**
    * Return TRUE if the actual value is contains muliple values.
    *
    * @return boolean is it multi-valued
    */
   public boolean isMultivalued();

   /**
    * Get the Attributes required flag.
    *
    * @return boolean the required flag
    */
   public boolean isRequired();

   /**
    * Returns TRUE if the value is encrypted.
    *
    * @return boolean is the value encrypted
    */
   public boolean isEncrypted();

   /**
    * Get the Attributes read only flag.
    *
    * @return boolean the read only flag
    */
   public boolean isReadOnly();

   /**
    * Get the Attributes virtual flag.
    *
    * @return boolean the virtual flag
    */
   public boolean isVirtual();

   /**
    * Get the Attributes Access level.
    *
    * @return Access the Access level
    */
   public Access getAccess();

   /**
    * Get the String representation of the Attributes Access level.
    *
    * @return val String representation of Access level
    */
   public String getAccessAsString();

   /**
    * Get the attributes value as a String, regardless of it's internal type.
    * String[] is enclosed with brackets and values are comma delimited
    * Integer, Long and Booleans use their toString() value.
    *
    * @return String a String representation of the internal value
    */
   public String getValueAsString();

   /**
    * Get the Attributes name.
    *
    * @return String the Attributes name
    */
   public String getName();

   /**
    * Get the Attributes State.
    *
    * @return state State for the Attribute
    */
   public State getState();

   /**
    * Get the Attributes type.
    *
    * @return DataType the Attributes type
    */
   public DataType getType();

   /**
    * Get the Attributes type, as a String.
    *
    * @return String the String representation of the type
    */
   public String getTypeAsString();

   /**
    * Get the Property value from the Attribute.
    * If the property does not exist, a null value is returned.
    * Note: the returned value (String) is a copy of the internal value.
    *
    * @param key the name of the Property
    * @return String the value of the Property
    */
   public String getProperty(String key);

   /**
    * Returns the Properties for the Attribute.
    * Notice: The returned Properties is a "deep copy",
    * not a reference to the internal Properties.
    *
    * @return Properties the current Attribute properties
    */
   public Properties getProperties();

   /**
    * Returns the quantity of Properties.
    *
    * @return int quantity of Properties
    */
   public int getPropertiesSize();

   /**
    * Get the Attributes value.
    *
    * @return Object the Attributes value
    */
   public Object getValue();

   /**
    * Set the Attributes Access level.
    *
    * @param access Access level
    */
   public void setAccess(Access access);

   /**
    * Set the Attribute Access level.
    *
    * @param access String representation of the level
    */
   public void setAccess(String access);

   /**
    * Set the flag to define if the Attribute allows multivalues.
    *
    * @param value
    */
   public void setAllowMultivalue(boolean value);

   /**
    * Set the Attributes read only flag.
    *
    * @param value flag to define if the Attribute is read only
    */
   public void setReadOnly(boolean value);

   /**
    * Add the key / value as a Property to the Attribute.
    *
    * @param key the name of the Property
    * @param value the value of the Property
    */
   public void setProperty(String key, String value);

   /**
    * Sets / Replaces the Attributes Properties.
    *
    * @param props a Properties object
    */
   public void setProperties(Properties props);

   /**
    * Set the Attributes State.
    *
    * @param state State
    */
   public void setState(State state);

   /**
    * Set the Attributes type (enum).
    * Notice: The type is automatically set based on the
    * Attributes value. If the type does not match the value, there
    * will be unpredictable behavior.
    *
    * @param type DataType
    */
   public void setType(DataType type);

   /**
    * Set the Attributes required flag.
    *
    * @param bool flag to define if the Attribute is required
    */
   public void setRequired(boolean bool);

   /**
    * Set the flag to define if the Attribute value is encrypted.
    *
    * @param bEncrypted
    */
   public void setEncrypted(boolean bEncrypted);

   /**
    * Set the Attributes virtual flag.
    *
    * @param bool flag to define if the Attribute is virtual
    */
   public void setVirtual(boolean bool);

   /**
    * Set the Attributes value to the single Object.
    * The Type will be automatically set.
    *
    * @param value Object
    */
   public void setValue(Object value);

   /**
    * Set the Attributes value to the Object array.
    * The Type will be automatically set.
    *
    * @param value Object[]
    */
   public void setValue(Object[] value);

   /**
    * Set the Attributes value to a single Boolean.
    * The Type will be automatically set.
    *
    * @param value boolean
    */
   public void setValue(boolean value);

   /**
    * Set the Attributes value to a Boolean array.
    * The Type will be automatically set.
    *
    * @param value boolean[]
    */
   public void setValue(boolean[] value);

   /**
    * Set the Attributes value to a single Boolean.
    * The Type will be automatically set.
    *
    * @param value Boolean
    */
   public void setValue(Boolean value);

   /**
    * Set the Attributes value to a Boolean array.
    * The Type will be automatically set.
    *
    * @param value Boolean[]
    */
   public void setValue(Boolean[] value);

   /**
    * Set the Attributes value to a single Integer.
    * The Type will be automatically set.
    *
    * @param value int
    */
   public void setValue(int value);

   /**
    * Set the Attributes value to a Integer array.
    * The Type will be automatically set.
    *
    * @param value int[]
    */
   public void setValue(int[] value);

   /**
    * Set the Attributes value to a single Integer.
    * The Type will be automatically set.
    *
    * @param value Integer
    */
   public void setValue(Integer value);

   /**
    * Set the Attributes value to a Integer array.
    * The Type will be automatically set.
    *
    * @param value Integer[]
    */
   public void setValue(Integer[] value);

   /**
    * Set the Attributes value to a single Long.
    * The Type will be automatically set.
    *
    * @param value Long
    */
   public void setValue(Long value);

   /**
    * Set the Attributes value to a Long array.
    * The Type will be automatically set.
    *
    * @param value Long[]
    */
   public void setValue(Long[] value);

   /**
    * Set the Attributes value to a single String.
    * The Type will be automatically set.
    *
    * @param value String
    */
   public void setValue(String value);

   /**
    * Set the Attributes value to a String array.
    * The Type will be automatically set.
    *
    * @param value String[]
    */
   public void setValue(String[] value);
}
