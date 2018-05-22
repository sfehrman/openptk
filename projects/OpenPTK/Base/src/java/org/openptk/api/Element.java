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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * An <tt>Element</tt> is a foundation class that holds
 * <tt>Properties</tt> and <tt>Attributes</tt>.  It has a unique id,
 * a <tt>Description</tt>, <tt>State</tt> and <tt>Status</tt>
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * <br>
 * contributor: Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public class Element implements ElementIF
//===================================================================
{
   private final Object _lockProps = new Object();
   private final Object _lockAttrs = new Object();
   private static final String DESCRIPTION = "Element";
   private boolean _error = false;
   private State _state = State.NEW;
   private DataType _uidType = DataType.OBJECT;
   private Object _uniqueId = null; // can be: String, Integer, Long
   private String _description = DESCRIPTION;
   private String _status = null;
   private String _key = null;
   private Properties _props = null;
   private Map<String, AttributeIF> _attributes = null;

   /**
    * Creates an empty element, The state is set to NEW.
    */
   //----------------------------------------------------------------
   public Element()
   //----------------------------------------------------------------
   {
      return;
   }

   /**
    * Create an Element using the provided element.
    * All of the information is copied into the new Element
    *
    * @param elem an existing ElementIF object
    */
   //----------------------------------------------------------------
   public Element(final ElementIF elem)
   //----------------------------------------------------------------
   {
      _error = elem.isError();
      _state = elem.getState();
      _uniqueId = elem.getUniqueId();
      _uidType = elem.getUniqueIdType();
      _description = elem.getDescription() + " [copy]";
      _status = elem.getStatus();
      _props = elem.getProperties();
      _attributes = elem.getAttributes();
      _key = elem.getKey();
      return;
   }

   /**
    * Creates a "deep" copy of the Element.
    *
    * @return Element the new Element (a copy)
    */
   //----------------------------------------------------------------
   @Override
   public ElementIF copy()
   //----------------------------------------------------------------
   {
      return new Element(this);
   }

   /**
    * Information about the Element, summarize data into a String.
    *
    * @return String element information
    */
   //----------------------------------------------------------------
   @Override
   public String toString()
   //----------------------------------------------------------------
   {
      String str = null;

      str = "uid='" + (_uniqueId != null ? _uniqueId.toString() : "(null)")
         + "', type='" + _uidType.toString()
         + "', key='" + (_key != null ? _key.toString() : "(null)")
         + "', desc='" + (_description != null ? _description.toString() : "(null)")
         + "', status='" + (_status != null ? _status.toString() : "(null)")
         + "', state='" + this.getStateAsString()
         + "', error=" + this.isError();

      return str;
   }

   /**
    * Returns the Element's State.
    *
    * @return State State enumeration
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
    * Set the State.
    * @param state enumeration value
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
    * Get the String representation for the current state.
    *
    * @return String the current state
    */
   //----------------------------------------------------------------
   @Override
   public final String getStateAsString()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_state != null)
      {
         str = _state.toString();
      }

      return (str);
   }

   /**
    * Set a property using the key and value.
    *
    * @param key for the property
    * @param value for the property
    */
   //----------------------------------------------------------------
   @Override
   public final void setProperty(final String key, final String value)
   //----------------------------------------------------------------
   {
      if (key != null && key.length() > 0
         && value != null && value.length() > 0)
      {
         synchronized (_lockProps)
         {
            if (_props == null)
            {
               _props = new Properties();
            }
            _props.setProperty(key, value);
         }
      }

      return;
   }

   /**
    * Get a property value.  Note: this is a "copy" of the value
    *
    * @param key for the property
    * @return String value of the property
    */
   //----------------------------------------------------------------
   @Override
   public final String getProperty(final String key)
   //----------------------------------------------------------------
   {
      /*
       * NOTICE: Returns a "copy" of the value
       */

      String val = null;

      if (key != null && key.length() > 0)
      {
         synchronized (_lockProps)
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
      }

      return val;
   }

   /**
    * Set/Replace the properties with the new Properties.
    *
    * @param props new properties object
    */
   //----------------------------------------------------------------
   @Override
   public final void setProperties(final Properties props)
   //----------------------------------------------------------------
   {
      synchronized (_lockProps)
      {
         _props = props;
      }
      return;
   }

   /**
    * Get all of the Properties.
    *
    * @return Properties all of the properties
    */
   //----------------------------------------------------------------
   @Override
   public final Properties getProperties()
   //----------------------------------------------------------------
   {
      /*
       * NOTICE: returns a "deep copy" / new instance of Properties
       */

      String key = null;
      String val = null;
      Properties props = null;
      Set<Object> set = null;

      synchronized (_lockProps)
      {
         if (_props != null)
         {
            props = new Properties();
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
      }

      return props;
   }

   /**
    * Get the number Property key/value pairs.
    *
    * @return int number of properties
    */
   //----------------------------------------------------------------
   @Override
   public final int getPropertiesSize()
   //----------------------------------------------------------------
   {
      int size = 0;

      synchronized (_lockProps)
      {
         if (_props != null && !_props.isEmpty())
         {
            size = _props.size();
         }
      }

      return size;
   }

   /**
    * Remove the named Property
    * @param key name of the property
    */
   //----------------------------------------------------------------
   @Override
   public final void removeProperty(String key)
   //----------------------------------------------------------------
   {
      if (key != null && key.length() > 0)
      {
         if (_props.containsKey(key))
         {
            synchronized (_lockProps)
            {
               _props.remove(key);
            }
         }
      }

      return;
   }

   /**
    * Get the Description
    * a generic value that can be defined by the application.
    *
    * @return String the description of the Element
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getDescription()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_description != null)
      {
         str = new String(_description); // always return a copy
      }

      return str;
   }

   /**
    * Set the Description, a generic value that can be defined.
    *
    * @param str the description for the Element
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setDescription(String str)
   //----------------------------------------------------------------
   {
      _description = str;
      return;
   }

   /**
    * Get the Unique ID, a generic identifier.
    *
    * @return String the unique id of the ElementIF
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized Object getUniqueId()
   //----------------------------------------------------------------
   {
      Object obj = null;

      /*
       * always return a copy of the value
       */
      
      if (_uniqueId != null)
      {
         switch (_uidType)
         {
            case STRING:
               obj = new String((String) _uniqueId);
               break;
            case INTEGER:
               obj = new Integer((Integer) _uniqueId);
               break;
            case LONG:
               obj = new Long((Long) _uniqueId);
               break;
            default:
               obj = _uniqueId;
               break;
         }
      }

      return obj;
   }

   /**
    * Get the Unique ID data type
    * @return DataType uniqueid type
    */
   //----------------------------------------------------------------
   @Override
   public final DataType getUniqueIdType()
   //----------------------------------------------------------------
   {
      return _uidType;
   }

   /**
    * Set the Unique ID to the String
    * @param value the Element's unique id
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setUniqueId(final String value)
   //----------------------------------------------------------------
   {
      _uniqueId = value;
      _uidType = DataType.STRING;
      return;
   }

   /**
    * Set the Unique ID to the Integer
    * @param value the Element's unique id
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setUniqueId(final Integer value)
   //----------------------------------------------------------------
   {
      _uniqueId = value;
      _uidType = DataType.INTEGER;
      return;
   }

   /**
    * Set the Unique ID to the Long
    * @param value the Element's unique id
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setUniqueId(final Long value)
   //----------------------------------------------------------------
   {
      _uniqueId = value;
      _uidType = DataType.LONG;
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setUniqueId(final ElementIF elem)
   //----------------------------------------------------------------
   {
      if (elem != null)
      {
         _uniqueId = elem.getUniqueId();
         switch(elem.getUniqueIdType())
         {
            case STRING:
               _uidType = DataType.STRING;
               break;
            case INTEGER:
               _uidType = DataType.INTEGER;
               break;
            case LONG:
               _uidType = DataType.LONG;
               break;
         }
      }
      return;
   }

   /**
    * Flag to determine if there was an Error.
    *
    * @return boolean error TRUE/FALSE
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isError()
   //----------------------------------------------------------------
   {
      boolean bool = false;

      bool = _error;

      return bool;
   }

   /**
    * Flag to set if there is an Error.
    *
    * @param  error set the error flag
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setError(final boolean error)
   //----------------------------------------------------------------
   {
      _error = error;
      return;
   }

   /**
    * Get the status
    * <br>
    * a general purpose piece of information that
    * can store arbitrary information, typically used to
    * store application status information.
    *
    * @return String the Elements status
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getStatus()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_status != null)
      {
         str = new String(_status); // always return a copy
      }

      return str;
   }

   /**
    * Set the status
    *
    * <br>
    * a general purpose piece of information that
    * can be used to store arbitrary information, typically used to
    * store application level status information.
    *
    * @param str the Elements status
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setStatus(final String str)
   //----------------------------------------------------------------
   {
      _status = str;
      return;
   }

   /**
    * Add an Attribute, name only, with no value.
    *
    * @param name of the Attribute
    */
   //----------------------------------------------------------------
   @Override
   public final void addAttribute(final String name)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttributeIF>();
            }
            _attributes.put(name, new Attribute(name));
         }
      }

      return;
   }

   /**
    * Add an Attribute with the give name and a single String value.
    *
    * @param name of the Attribute
    * @param value of the Attribute
    */
   //----------------------------------------------------------------
   @Override
   public final void addAttribute(final String name, final String value)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttributeIF>();
            }
            _attributes.put(name, new Attribute(name, value));
         }
      }

      return;
   }

   /**
    * Add an Attribute with the give name and multiple String values.
    *
    * @param name of the Attribute
    * @param values String[] with multiple values
    */
   //----------------------------------------------------------------
   @Override
   public final void addAttribute(final String name, final String[] values)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttributeIF>();
            }
            _attributes.put(name, new Attribute(name, values));
         }
      }

      return;
   }

   /**
    * Add an Attribute with the give name and boolean value.
    *
    * @param name of the Attribute
    * @param value TRUE/FALSE
    */
   //----------------------------------------------------------------
   @Override
   public final void addAttribute(final String name, final boolean value)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttributeIF>();
            }
            _attributes.put(name, new Attribute(name, value));
         }
      }

      return;
   }

   /**
    * Add an Attribute with the give name and Boolean value.
    *
    * @param name of the Attribute
    * @param value TRUE/FALSE
    */
   //----------------------------------------------------------------
   @Override
   public final void addAttribute(final String name, final Boolean value)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttributeIF>();
            }
            _attributes.put(name, new Attribute(name, value));
         }
      }

      return;
   }

   /**
    * Add an Attribute with the give name and Boolean value.
    *
    * @param name of the Attribute
    * @param value TRUE/FALSE
    */
   //----------------------------------------------------------------
   @Override
   public final void addAttribute(final String name, final Boolean[] value)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttributeIF>();
            }
            _attributes.put(name, new Attribute(name, value));
         }
      }

      return;
   }

   /**
    * Add an Attribute with the give name and integer value.
    *
    * @param name of the Attribute
    * @param value integer
    */
   //----------------------------------------------------------------
   @Override
   public final void addAttribute(final String name, final int value)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttributeIF>();
            }
            _attributes.put(name, new Attribute(name, value));
         }
      }

      return;
   }

   /**
    * Add an Attribute with the give name and Integer value.
    *
    * @param name of the Attribute
    * @param value Integer
    */
   //----------------------------------------------------------------
   @Override
   public final void addAttribute(final String name, final Integer value)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttributeIF>();
            }
            _attributes.put(name, new Attribute(name, value));
         }
      }

      return;
   }

   /**
    * Add an Attribute with the give name and Integer[] value.
    *
    * @param name of the Attribute
    * @param value Integer[]
    */
   //----------------------------------------------------------------
   @Override
   public final void addAttribute(final String name, final Integer[] value)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttributeIF>();
            }
            _attributes.put(name, new Attribute(name, value));
         }
      }

      return;
   }

   /**
    * Add an Attribute with the give name and Long value.
    *
    * @param name of the Attribute
    * @param value Long
    */
   //----------------------------------------------------------------
   @Override
   public final void addAttribute(final String name, final long value)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttributeIF>();
            }
            _attributes.put(name, new Attribute(name, value));
         }
      }

      return;
   }

   /**
    * Add an Attribute with the give name and Long value.
    *
    * @param name of the Attribute
    * @param value Long
    */
   //----------------------------------------------------------------
   @Override
   public final void addAttribute(final String name, final Long value)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttributeIF>();
            }
            _attributes.put(name, new Attribute(name, value));
         }
      }

      return;
   }

   /**
    * Add an Attribute with the give name and Long[] value.
    *
    * @param name of the Attribute
    * @param value Long[]
    */
   //----------------------------------------------------------------
   @Override
   public final void addAttribute(final String name, final Long[] value)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttributeIF>();
            }
            _attributes.put(name, new Attribute(name, value));
         }
      }

      return;
   }

   /**
    * Add an Attribute with the give name and Object value.
    *
    * @param name of the Attribute
    * @param value Object
    */
   //----------------------------------------------------------------
   @Override
   public final void addAttribute(final String name, final Object value)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttributeIF>();
            }
            _attributes.put(name, new Attribute(name, value));
         }
      }

      return;
   }

   /**
    * Add an Attribute, copying an existing Attribute.
    *
    * @param attr an existing AttributeIF
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addAttribute(final AttributeIF attr)
   //----------------------------------------------------------------
   {
      String name = null;

      if (attr != null)
      {
         name = attr.getName();

         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttributeIF>();
            }
            _attributes.put(name, attr);
         }
      }

      return;
   }

   /**
    * Add multiple Attributes, with no value, one for each String.
    *
    * @param names List of Attributes to add
    */
   //----------------------------------------------------------------
   @Override
   public final void addAttributes(final List<String> names)
   //----------------------------------------------------------------
   {
      Iterator<String> iterNames = null;

      if (names != null && !names.isEmpty())
      {
         synchronized (_lockAttrs)
         {
            iterNames = names.iterator();
            while (iterNames.hasNext())
            {
               String name = iterNames.next();
               this.addAttribute(name);
            }
         }
      }

      return;
   }

   /**
    * Set / Replace all Attributes with the new Collection.
    *
    * @param map Collection of new AttributeIF items
    */
   //----------------------------------------------------------------
   @Override
   public final void setAttributes(final Map<String, AttributeIF> map)
   //----------------------------------------------------------------
   {
      if (map != null)
      {
         synchronized (_lockAttrs)
         {
            _attributes = map;
         }
      }

      return;
   }

   /**
    * Get the named Attribute.
    *
    * @param  name the Attributes name
    * @return AttributeIF object
    */
   //----------------------------------------------------------------
   @Override
   public final AttributeIF getAttribute(final String name)
   //----------------------------------------------------------------
   {
      /*
       * NOTICE: This will return a "copy" of the Attribute
       */
      AttributeIF attr = null;

      if (name != null && name.length() > 0 && _attributes != null
         && _attributes.containsKey(name))
      {
         attr = _attributes.get(name).copy();
      }

      return attr;
   }

   /**
    * Gets all the Attribute names.
    *
    * @return String[] String Array of Attribute names
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getAttributeNames()
   //----------------------------------------------------------------
   {
      String[] names = null;

      if (_attributes != null && !_attributes.isEmpty())
      {
         synchronized (_lockAttrs)
         {
            names = _attributes.keySet().toArray(new String[_attributes.size()]);
         }
      }
      else
      {
         names = new String[0];
      }

      return names;
   }

   /**
    * Get all of the Attributes as a Properties object.
    *
    * <br>
    * If an attribute has multiple values, only the first one is used
    *
    * @return Map a map all of the attributes.
    */
   //----------------------------------------------------------------
   @Override
   public final Map<String, AttributeIF> getAttributes()
   //----------------------------------------------------------------
   {
      /*
       * NOTICE: return a "deep copy" / new instance of Map
       */

      Map<String, AttributeIF> attrs = null;
      Set<String> set = null;
      AttributeIF attr = null;

      synchronized (_lockAttrs)
      {
         if (_attributes != null && !_attributes.isEmpty())
         {
            attrs = new HashMap<String, AttributeIF>();
            set = _attributes.keySet();
            for (String s : set)
            {
               attr = _attributes.get(s).copy();
               attrs.put(s, attr);
            }
         }
      }

      return attrs;
   }

   /**
    * Get the number of Attributes.
    *
    * @return int the number of attributes
    */
   //----------------------------------------------------------------
   @Override
   public final int getAttributesSize()
   //----------------------------------------------------------------
   {
      int size = 0;

      synchronized (_lockAttrs)
      {
         if (_attributes != null && !_attributes.isEmpty())
         {
            size = _attributes.size();
         }
      }
      return size;
   }

   /**
    * Remove the named Attribute.
    *
    * @param name the Attributes name
    */
   //----------------------------------------------------------------
   @Override
   public final void removeAttribute(final String name)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes != null && _attributes.containsKey(name))
            {
               _attributes.remove(name);
            }
         }
      }

      return;
   }

   /**
    * Sets the name of the attribute which is defined as the unique
    * primary key.
    *
    * @param key the name of the attribute which defines the primary key
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setKey(final String key)
   //----------------------------------------------------------------
   {
      _key = key;
      return;
   }

   /**
    * Gets the name of the attribute which is defined as the unique
    * primary key.
    *
    * @return String the name of the primary unique key attribute
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getKey()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_key != null)
      {
         str = new String(_key); // always return a copy
      }

      return str;
   }
}
