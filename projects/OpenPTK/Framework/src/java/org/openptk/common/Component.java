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
package org.openptk.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openptk.api.DataType;
import org.openptk.api.State;
import org.openptk.crypto.Encryptor;
import org.openptk.debug.DebugIF;
import org.openptk.debug.DebugLevel;
import org.openptk.exception.CryptoException;
import org.openptk.logging.Logger;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public class Component implements ComponentIF
//===================================================================
{
   /*
    * This class is thread safe
    * All methods either use the instrinsic locking mechanism (synchronized)
    * or an explicit lock; Properties and Attributes
    * All "get" methods return COPIES (new instances) of the original
    */
   private final Object _lockProps = new Object();
   private final Object _lockAttrs = new Object();
   private State _state = State.NEW;
   private Category _category = Category.GENERIC;
   private DebugLevel _debugLevel = DebugLevel.NONE;
   private boolean _debug = false;
   private boolean _error = false;
   private boolean _tstamp = false;
   private Object _uniqueId = null;
   private String _description = null;
   private String _status = null;
   private String _sortValue = null;
   private Properties _props = null;
   private Map<String, Long> _timeStamps = null;
   private Map<String, AttrIF> _attributes = null;
   private DataType _uidType = DataType.STRING;

   //----------------------------------------------------------------
   public Component()
   //----------------------------------------------------------------
   {
      return;
   }

   /**
    * This constructor creates a new Component, this is MOSTLY a deep copy.
    * The items that are not copied are: TimeStamps
    * @param comp
    */
   //----------------------------------------------------------------
   public Component(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      if (comp != null)
      {
         _state = comp.getState();
         _uniqueId = comp.getUniqueId();
         _uidType = comp.getUniqueIdType();
         _category = comp.getCategory();
         _debugLevel = comp.getDebugLevel();
         _debug = comp.isDebug();
         _error = comp.isError();
         _description = comp.getDescription();
         _status = comp.getStatus();
         _props = comp.getProperties();
         _attributes = comp.getAttributes();
         _tstamp = comp.isTimeStamp();
      }

      return;
   }

   /**
    * Creates a copy!
    * @return ComponentIF
    */
   //----------------------------------------------------------------------
   @Override
   public ComponentIF copy()
   //----------------------------------------------------------------------
   {
      return new Component(this);
   }

   /**
    * Compares two Component objects.
    * This method, compareTo(object), is required to support the
    * Comparable implementation.  It is used by other Objects or
    * Collections that use the sort() method.
    * This method is not directly used.
    * @param comp
    * @return int
    */
   //----------------------------------------------------------------------
   @Override
   public final int compareTo(final ComponentIF comp)
   //----------------------------------------------------------------------
   {
      return (this.getSortValue().compareTo(comp.getSortValue()));
   }

   /**
    * Returns a String representation of the instance.
    * @return String
    */
   //----------------------------------------------------------------
   @Override
   public String toString()
   //----------------------------------------------------------------
   {
      String str = null;
      str = this.getClass().getSimpleName()
         + ": uid='" + (_uniqueId != null ? _uniqueId : "(null)")
         + "', cat='" + _category.toString()
         + "', desc='" + (_description != null ? _description : "(null)")
         + "', status='" + (_status != null ? _status : "(null)")
         + "', state='" + _state.toString()
         + "', error=" + _error
         + ", debug=" + _debug;
      return str;
   }

   /**
    * The value (string) used for sorting.
    * @return String
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getSortValue()
   //----------------------------------------------------------------
   {
      String val = null;

      if (_sortValue != null)
      {
         val = new String(_sortValue); // ALWAYS return a copy!
      }

      return val;
   }

   /**
    * Set the value used for sorting.
    * @param sortValue
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setSortValue(final String sortValue)
   //----------------------------------------------------------------
   {
      _sortValue = sortValue;
   }

   /**
    * Get a property value using the key.
    * @param key
    * @return String
    */
   //----------------------------------------------------------------
   @Override
   public final String getProperty(final String key)
   //----------------------------------------------------------------
   {
      String val = null;
      String encrypted = null;

      if (key != null && key.length() > 0)
      {
         synchronized (_lockProps)
         {
            if (_props != null)
            {
               encrypted = _props.getProperty(key + ".encrypted");
               if (encrypted != null && encrypted.length() > 0)
               {
                  try
                  {
                     val = Encryptor.decrypt(Encryptor.CONFIG, encrypted);
                  }
                  catch (CryptoException ex)
                  {
                     Logger.logError(ex.getMessage());
                  }
               }
               else
               {
                  val = _props.getProperty(key);
               }

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
    * Set a property using the key and value.
    * @param key
    * @param value
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
    * Get all of the Properties.
    * @return Properties
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
    * Set / replace all of the properties.
    * @param props
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
    * Get the number of properties.
    * @return int
    */
   //----------------------------------------------------------------
   @Override
   public final int getPropertiesSize()
   //----------------------------------------------------------------
   {
      int size = 0;

      synchronized (_lockProps)
      {
         if (_props != null)
         {
            size = _props.size();
         }
      }

      return size;
   }

   /**
    * Get an attribute using the key.
    * Note: This returns a copy of the Attribute.
    * @param key
    * @return AttrIF
    */
   //----------------------------------------------------------------
   @Override
   public final AttrIF getAttribute(final String key)
   //----------------------------------------------------------------
   {
      AttrIF attr = null;
      AttrIF val = null;

      if (key != null && key.length() > 0)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes != null)
            {
               /*
                * return a "copy" / new instance of the AttrIF object
                */
               attr = _attributes.get(key);
               if (attr != null)
               {
                  val = attr.copy();
               }
            }
         }
      }

      return val;
   }

   /**
    * Set an Attribute using the key and value.
    * @param key
    * @param value
    */
   //----------------------------------------------------------------
   @Override
   public final void setAttribute(final String key, final AttrIF value)
   //----------------------------------------------------------------
   {
      if (key != null && key.length() > 0 && value != null)
      {
         synchronized (_lockAttrs)
         {
            if (_attributes == null)
            {
               _attributes = new HashMap<String, AttrIF>();
            }
            _attributes.put(key, value);
         }
      }

      return;
   }

   /**
    * Set / replace all the Attributes using the map.
    * @param attr
    */
   //----------------------------------------------------------------
   @Override
   public final void setAttributes(final Map<String, AttrIF> attr)
   //----------------------------------------------------------------
   {
      synchronized (_lockAttrs)
      {
         _attributes = attr;
      }

      return;
   }

   /**
    * Get all the Attributes as a map.
    * @return Map<String, AttrIF>
    */
   //----------------------------------------------------------------
   @Override
   public final Map<String, AttrIF> getAttributes()
   //----------------------------------------------------------------
   {
      Map<String, AttrIF> attrs = null;
      Set<String> set = null;
      AttrIF attr = null;

      synchronized (_lockAttrs)
      {
         if (_attributes != null && !_attributes.isEmpty())
         {
            /*
             * return a "deep copy" / new instance of Map
             */
            attrs = new HashMap<String, AttrIF>();
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
    * Gets all the Attribute names.
    * @return List<String> List of Strings
    */
   //----------------------------------------------------------------
   @Override
   public final List<String> getAttributesNames()
   //----------------------------------------------------------------
   {
      List<String> names = null;
      Set<String> keys = null;

      names = new LinkedList<String>();

      synchronized (_lockAttrs)
      {
         if (_attributes != null)
         {
            keys = _attributes.keySet();
            if (keys != null)
            {
               for (String s : keys)
               {
                  names.add(s);
               }
            }
         }
      }

      return names;
   }

   /**
    * Get the size (quantity) of Attributes.
    * @return int
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
    * Get the description.
    * @return String
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getDescription()
   //----------------------------------------------------------------
   {
      String val = null;

      if (_description != null)
      {
         val = new String(_description); // always return a copy
      }

      return val;
   }

   /**
    * Set the description.
    * @param str
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setDescription(final String str)
   //----------------------------------------------------------------
   {
      _description = str;
      return;
   }

   /**
    * Get the unique id.
    * @return Object
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
    * @return DataType
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
   public final synchronized void setUniqueId(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      if (comp != null)
      {
         _uniqueId = comp.getUniqueId();
         switch (comp.getUniqueIdType())
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
            default:
               _uidType = DataType.OBJECT; // this is NOT a valid type.
               break;
         }
      }
      return;
   }

   /**
    * @return boolean
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized boolean isDebug()
   //----------------------------------------------------------------
   {
      boolean val = _debug;
      return val;
   }

   /**
    * @param debug
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setDebug(final boolean debug)
   //----------------------------------------------------------------
   {
      _debug = debug;
      return;
   }

   /**
    * @return boolean
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized boolean isError()
   //----------------------------------------------------------------
   {
      boolean val = _error;
      return val;
   }

   /**
    * @param error
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
    * @return String
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getStatus()
   //----------------------------------------------------------------
   {
      String val = null;

      if (_status != null)
      {
         val = new String(_status); // always return a copy
      }

      return val;
   }

   /**
    * @param str
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
    * @return State
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized State getState()
   //----------------------------------------------------------------
   {
      State val = null;
      val = _state;
      return val;
   }

   /**
    * @param state
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setState(final State state)
   //----------------------------------------------------------------
   {
      _state = state;

      switch (state)
      {
         case ERROR:
         case INVALID:
         case NOTEXIST:
         case NOTAUTHENTICATED:
         case FAILED:
            this.setError(true);
            break;
         case AUTHENTICATED:
         case SUCCESS:
         case READY:
            this.setError(false);
            break;
      }

      return;
   }

   /**
    * @param state
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setState(final State state, String msg)
   //----------------------------------------------------------------
   {
      this.setState(state);
      this.setStatus(msg);
      return;
   }

   /**
    * @return String
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getStateAsString()
   //----------------------------------------------------------------
   {
      return _state.toString();
   }

   /**
    * @return Category
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized Category getCategory()
   //----------------------------------------------------------------
   {
      Category val = null;
      val = _category;
      return val;
   }

   /**
    * @param category
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setCategory(final Category category)
   //----------------------------------------------------------------
   {
      _category = category;
      return;
   }

   /**
    * @return String
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getCategoryAsString()
   //----------------------------------------------------------------
   {
      return _category.toString();
   }

   /**
    * @return DebugLevel
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized DebugLevel getDebugLevel()
   //----------------------------------------------------------------
   {
      DebugLevel val = null;
      val = _debugLevel;
      return val;
   }

   /**
    * @param debugLevel
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setDebugLevel(final DebugLevel debugLevel)
   //----------------------------------------------------------------
   {
      if (debugLevel == DebugLevel.NONE)
      {
         this.setDebug(false);
      }
      else
      {
         this.setDebug(true);
      }
      _debugLevel = debugLevel;

      return;
   }

   /**
    * @return String
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getDebugLevelAsString()
   //----------------------------------------------------------------
   {
      return _debugLevel.toString();
   }

   /**
    *
    * @return int
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized int getDebugLevelAsInt()
   //----------------------------------------------------------------
   {
      int level = DebugIF.NONE;

      switch (_debugLevel)
      {
         case CONFIG:
            level = DebugIF.CONFIG; // 1
            break;
         case FINE:
            level = DebugIF.FINE; // 2
            break;
         case FINER:
            level = DebugIF.FINER; // 3
            break;
         case FINEST:
            level = DebugIF.FINEST; // 4
            break;
      }

      return level;
   }

   /**
    * @param ts
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void useTimeStamp(final boolean ts)
   //----------------------------------------------------------------
   {
      _tstamp = ts;
      return;
   }

   /**
    * @return boolean
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isTimeStamp()
   //----------------------------------------------------------------
   {
      return _tstamp;
   }

   /**
    * @return Map<String, Long>
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized Map<String, Long> getTimeStamps()
   //----------------------------------------------------------------
   {
      Long val = null;
      Map<String, Long> times = null;
      Set<String> set = null;

      if (_timeStamps != null)
      {
         times = new HashMap<String, Long>();
         set = _timeStamps.keySet();
         for (String s : set)
         {
            val = new Long(_timeStamps.get(s));
            times.put(s, val);
         }
      }

      return times;
   }

   /**
    * @param name
    * @return Long
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized Long getTimeStamp(final String name)
   //----------------------------------------------------------------
   {
      Long stamp = null;

      if (name != null && name.length() > 0)
      {
         if (_timeStamps != null && _timeStamps.containsKey(name))
         {
            stamp = _timeStamps.get(name);
         }
      }
      return stamp;
   }

   /**
    * @param name
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setTimeStamp(final String name)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         if (_timeStamps == null)
         {
            _timeStamps = new HashMap<String, Long>();
         }

         _timeStamps.put(name, new Long(System.currentTimeMillis()));
      }

      return;
   }

   /**
    * @param name
    * @param time
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setTimeStamp(final String name, final Long time)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0)
      {
         if (_timeStamps == null)
         {
            _timeStamps = new HashMap<String, Long>();
         }

         _timeStamps.put(name, time);
      }
   }

   /**
    * @return Set<String>
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized Set<String> getTimeStampNames()
   //----------------------------------------------------------------
   {
      Set<String> set = null;
      if (_timeStamps != null)
      {
         set = _timeStamps.keySet();
      }
      return set;
   }
}
