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

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * The ElementIF interface is used by many classes within the package.
 * It defines core functionality and supports
 * storing both Properties and Attributes, each of these can be
 * be used for application specific purposes
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * <br>
 * contributor: Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public interface ElementIF
    //===================================================================
{
    /**
     * Returns a copy of the Element.
     * 
     * @return ElementIF new ElementIF instance
     */
    public ElementIF copy();

    /**
    * Add an Attribute, name only, with no value.
    *
    * @param name of the Attribute
    */
    public void addAttribute(String name);

    /**
     * Add an Attribute with the give name and a single String value.
     *
     * @param name of the Attribute
     * @param value of the Attribute
     */
    public void addAttribute(String name, String value);

    /**
     * Add an Attribute with the give name and multiple String values.
     *
     * @param name of the Attribute
     * @param values String[] with multiple values
     */
    public void addAttribute(String name, String[] values);

    /**
     * Add an Attribute with the give name and boolean value.
     *
     * @param name of the Attribute
     * @param value TRUE/FALSE
     */
    public void addAttribute(String name, boolean value);
    public void addAttribute(String name, Boolean value);
    public void addAttribute(String name, Boolean[] value);

    /**
     * Add an Attribute with the give name and integer value.
     *
     * @param name of the Attribute
     * @param value integer
     */
    public void addAttribute(String name, int value);
    public void addAttribute(String name, Integer value);
    public void addAttribute(String name, Integer[] value);
    public void addAttribute(String name, long value);
    public void addAttribute(String name, Long value);
    public void addAttribute(String name, Long[] value);

    /**
     * Add an Attribute with the give name and Object value.
     *
     * @param name of the Attribute
     * @param value Object
     */
    public void addAttribute(String name, Object value);

    /**
     * Add an Attribute, copying an existing Attribute.
     *
     * @param attr an existing AttributeIF
     */
    public void addAttribute(AttributeIF attr);

    /**
     * Add multiple Attributes, with no value, one for each String.
     *
     * @param names List of Attributes to add
     */
    public void addAttributes(List<String> names);

    /**
     * Set / Replace all Attributes with the new Collection.
     *
     * @param map Collection of new AttributeIF items
     */
    public void setAttributes(Map<String, AttributeIF> map);

    /**
     * Flag to set if there is an Error.
     *
     * @param  error set the error flag
     */
    public void setError(boolean error);

    /**
     * Set the Description, a generic value that can be defined.
     *
     * @param str the description for the Element
     */
    public void setDescription(String str);

    /**
     * Set a property using the key and value.
     *
     * @param key for the property
     * @param value for the property
     */
    public void setProperty(String key, String value);

    /**
     * Set/Replace the properties with the new Properties.
     *
     * @param props new properties object
     */
    public void setProperties(Properties props);

    /**
     * Set the state, see State for valid states.
     *
     * The default value is NEW
     *
     * @param state the State
     */
    public void setState(State state);

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
    public void setStatus(String str);

    /**
     * Set the Unique ID
     *
     * <br>
     * a generic identifier, this should be a unique value.
     *  <b>Note</b>: it's uniqueness IS NOT CHECKED
     *
     * @param value the Elements unique id
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

    public void setUniqueId(ElementIF value);

    /**
     * Sets the name of the attribute which is defined as the unique
     * primary key.
     *
     * @param key the name of the attribute which defines the primary key
     */
    public void setKey(String key);

    /**
     * Flag to determine if there was an Error.
     *
     * @return boolean error TRUE/FALSE
     */
    public boolean isError();

    /**
     * Get the number of Attributes.
     *
     * @return int the number of attributes
     */
    public int getAttributesSize();

    /**
     * Get the number Property key/value pairs.
     *
     * @return int number of properties
     */
    public int getPropertiesSize();

    /**
     * Get the state.
     *
     * @return enum State
     */
    public State getState();

    /**
     * Get the Description
     * a generic value that can be defined by the application.
     *
     * @return String the description of the Element
     */
    public String getDescription();

    /**
     * Get a property value.  Note: this is a "copy" of the value
     *
     * @param key for the property
     * @return String value of the property
     */
    public String getProperty(String key);

    /**
     * Get the String representation for the current state.
     *
     * @return String the current state
     */
    public String getStateAsString();

    /**
     * Get the status
     * <br>
     * a general purpose piece of information that
     * can store arbitrary information, typically used to
     * store application status information.
     *
     * @return String the Elements status
     */
    public String getStatus();

    /**
     * Get the Unique ID, a generic identifier.
     *
     * @return Object the unique id of the ElementIF
     * <br>
     * Could be of type: String, Integer, Long
     */
    public Object getUniqueId();

    /**
     * 
     * @return DataType uniqueid type
     */
    public DataType getUniqueIdType();

    /**
     * Gets all the Attribute names.
     *
     * @return String[] String Array of Attribute names
     */
    public String[] getAttributeNames();

    /**
     * Get all of the Properties.
     *
     * @return Properties all of the properties
     */
    public Properties getProperties();

    /**
     * Get the named Attribute.
     *
     * @param  name the Attributes name
     * @return AttributeIF object
     */
    public AttributeIF getAttribute(String name);

    /**
     * Get all of the Attributes as a Properties object.
     *
     * <br>
     * If an attribute has multiple values, only the first one is used
     *
     * @return Map a map all of the attributes.
     */
    public Map<String, AttributeIF> getAttributes();

    /**
     * Gets the name of the attribute which is defined as the unique
     * primary key.
     *
     * @return String the name of the primary unique key attribute
     */
    public String getKey();

    /**
     * Remove the named Attribute.
     *
     * @param name the Attributes name
     */
    public void removeAttribute(String name);

    /**
     * Remove the named Property
     * @param name of the property
     */
    public void removeProperty(String name);
}
