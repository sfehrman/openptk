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

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openptk.api.DataType;
import org.openptk.api.State;
import org.openptk.debug.DebugLevel;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public interface ComponentIF extends Comparable<ComponentIF>
//===================================================================
{
   public static final String EXECUTE_BEGIN = "execute_begin";
   public static final String EXECUTE_END = "execute_end";
   public static final String SORT_BEGIN = "sort_begin";
   public static final String SORT_END = "sort_end";
   public static final String PROP_OPENPTK_HOME = "openptk.home";
   public static final String PROP_OPENPTK_TEMP = "openptk.temp";

   /**
    * @return ComponentIF
    */
   public ComponentIF copy();

   /**
    * @param key
    * @param value
    */
   public void setAttribute(String key, AttrIF value);

   /**
    * @param attr
    */
   public void setAttributes(Map<String, AttrIF> attr);

   /**
    * @param category
    */
   public void setCategory(Category category);

   /**
    * @param debug
    */
   public void setDebug(boolean debug);

   /**
    * @param debug
    */
   public void setDebugLevel(DebugLevel debug);

   /**
    * @param description
    */
   public void setDescription(String description);

   /**
    * @param error
    */
   public void setError(boolean error);

   /**
    * @param properties
    */
   public void setProperties(Properties properties);

   /**
    * @param key
    * @param property
    */
   public void setProperty(String key, String property);

   /**
    * @param sortValue
    */
   public void setSortValue(String sortValue);

   /**
    * @param state
    */
   public void setState(State state);

   /**
    * @param state
    */
   public void setState(State state, String msg);

   /**
    * @param status
    */
   public void setStatus(String status);

   /**
    * @param name
    */
   public void setTimeStamp(String name);

   /**
    * @param name
    * @param time
    */
   public void setTimeStamp(String name, Long time);

   /**
    * @param value
    */
   public void setUniqueId(String value);

   /**
    *
    * @param value
    */
   public void setUniqueId(Integer value);

   /**
    * 
    * @param value
    */
   public void setUniqueId(Long value);

   /**
    * 
    * @param value
    */
   public void setUniqueId(ComponentIF value);

   /**
    * @return boolean
    */
   public boolean isDebug();

   /**
    * @return boolean
    */
   public boolean isError();

   /**
    * @return int
    */
   public int getAttributesSize();

   /**
    * @return int
    */
   public int getPropertiesSize();

   /**
    * @return Category
    */
   public Category getCategory();

   /**
    * @return DebugLevel
    */
   public DebugLevel getDebugLevel();

   /**
    * @return State
    */
   public State getState();

   /**
    * @param Name
    * @return Long
    */
   public Long getTimeStamp(String Name);

   /**
    * @return String
    */
   public String getCategoryAsString();

   /**
    * @return String
    */
   public String getDebugLevelAsString();

   /**
    * @return int
    */
   public int getDebugLevelAsInt();

   /**
    * @return String
    */
   public String getDescription();

   /**
    * @param key
    * @return String
    */
   public String getProperty(String key);

   /**
    * @return String
    */
   public String getSortValue();

   /**
    * @return String
    */
   public String getStatus();

   /**
    * @return String
    */
   public String getStateAsString();

   /**
    * @return Object
    */

   public Object getUniqueId();

   /**
    * 
    * @return DataType
    */
   public DataType getUniqueIdType();

   /**
    * @return Map<String, AttrIF>
    */
   public Map<String, AttrIF> getAttributes();

   /**
    * @return List<String>
    */
   public List<String> getAttributesNames();

   /**
    * @return Properties
    */
   public Properties getProperties();

   /**
    * @return boolean
    */
   public boolean isTimeStamp();

   /**
    * @param ts
    */
   public void useTimeStamp(boolean ts);

   /**
    * @return Set<String>
    */
   public Set<String> getTimeStampNames();

   /**
    * @return Map<String, Long>
    */
   public Map<String, Long> getTimeStamps();

   /**
    * @param key
    * @return AttrIF
    */
   public AttrIF getAttribute(String key);
}
