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
 * Portions Copyright 2011-2013 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.structure;

import java.util.List;
import java.util.Properties;

import org.openptk.api.State;
import org.openptk.exception.StructureException;

/**
 * The <b>Structure</b> (interface and classes) is a generic class that supports
 * two main features.
 * <ol>
 * <li>
 * A <b>name</b> and typed <b>value</b>. The value can be single or
 * multi-valued. Supported value types:
 * <ul>
 * <li>BOOLEAN</li><li>NUMBER</li><li>STRING</li><li>OBJECT</li>
 * </ul>
 * </li>
 * <li>
 * Can contain one or more <b>child</b> Structures.
 * </li>
 * </ol>
 * A given Structure can contain either a <b>name/value</b> or have <b>children</b>,
 * it can NOT contain both.
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public interface StructureIF
//===================================================================
{

   public static final String NAME_ACCESSED = "accessed";
   public static final String NAME_ACTION = "action";
   public static final String NAME_ACTIONID = "actionid";
   public static final String NAME_ACTIONS = "actions";
   public static final String NAME_ALLOWMULTI = "allowMultiValue";
   public static final String NAME_ANCESTORS = "ancestors";
   public static final String NAME_ANSWERS = "answers";
   public static final String NAME_ASSIGNMENT = "assignment";
   public static final String NAME_ASSIGNMENTS = "assignments";
   public static final String NAME_ATTRIBUTE = "attribute";
   public static final String NAME_ATTRIBUTES = "attributes";
   public static final String NAME_ATTRMAP = "attrmap";
   public static final String NAME_ATTRMAPID = "attrmapid";
   public static final String NAME_ATTRMAPS = "attrmaps";
   public static final String NAME_AUTHENTICATOR = "authenticator";
   public static final String NAME_AUTHENTICATORID = "authneticatorid";
   public static final String NAME_AUTHENTICATORS = "authenticators";
   public static final String NAME_AVERAGE = "average";
   public static final String NAME_BODY = "body";
   public static final String NAME_CACHE = "cache";
   public static final String NAME_CACHEID = "cacheid";
   public static final String NAME_CACHES = "caches";
   public static final String NAME_CATEGORY = "category";
   public static final String NAME_CHANGE = "change";
   public static final String NAME_CHILDREN = "children";
   public static final String NAME_CLASS = "class";
   public static final String NAME_CLASSNAME = "classname";
   public static final String NAME_CLIENT = "client";
   public static final String NAME_CLIENTID = "clientid";
   public static final String NAME_CLIENTS = "clients";
   public static final String NAME_CONTEXT = "context";
   public static final String NAME_CONTEXTID = "contextid";
   public static final String NAME_CONTEXTS = "contexts";
   public static final String NAME_CONVERTER = "converter";
   public static final String NAME_CONVERTERID = "converterid";
   public static final String NAME_CONVERTERS = "converters";
   public static final String NAME_COUNT = "count";
   public static final String NAME_CREATED = "created";
   public static final String NAME_CRYPTO = "crypto";
   public static final String NAME_DATA = "data";
   public static final String NAME_DATUM = "datum";
   public static final String NAME_DEBUGLEVEL = "debuglevel";
   public static final String NAME_DECIDER = "decider";
   public static final String NAME_DECIDERID = "deciderid";
   public static final String NAME_DECIDERS = "deciders";
   public static final String NAME_DEFAULT = "defaultContext";
   public static final String NAME_DEFINITION = "definition";
   public static final String NAME_DESCRIPTION = "description";
   public static final String NAME_DESTINATION = "destination";
   public static final String NAME_DIGEST = "digest";
   public static final String NAME_DOCUMENT = "document";
   public static final String NAME_EFFECT = "effect";
   public static final String NAME_ENCRYPTED = "encrypted";
   public static final String NAME_ENFORCER = "enforcer";
   public static final String NAME_ENFORCERID = "enforcerid";
   public static final String NAME_ENFORCERS = "enforcers";
   public static final String NAME_ENGINE = "engine";
   public static final String NAME_ENVIRONMENT = "environment";
   public static final String NAME_ENVIRONMENTS = "environments";
   public static final String NAME_ERROR = "error";
   public static final String NAME_FROM = "from";
   public static final String NAME_ID = "id";
   public static final String NAME_INDEX = "index";
   public static final String NAME_KIND = "kind"; // 2.2.0
   public static final String NAME_LENGTH = "length";
   public static final String NAME_LEVEL = "level";
   public static final String NAME_LIBRARY = "library";
   public static final String NAME_MAPTO = "mapto";
   public static final String NAME_MAPFROM = "mapfrom"; // 2.2.0
   public static final String NAME_MATCH = "match"; // 2.2.0
   public static final String NAME_MAXIMUM = "maximum";
   public static final String NAME_MEDIA = "media";
   public static final String NAME_META = "meta";
   public static final String NAME_MIMETYPE = "mimetype";
   public static final String NAME_MINIMUM = "minimum";
   public static final String NAME_MODE = "mode";
   public static final String NAME_MODELID = "modeid";
   public static final String NAME_MODEL = "model";
   public static final String NAME_MODELS = "models";
   public static final String NAME_MODIFIED = "modified";
   public static final String NAME_MULTIVALUE = "multivalue";
   public static final String NAME_NAME = "name";
   public static final String NAME_OFFSET = "offset";
   public static final String NAME_OPERATION = "operation";
   public static final String NAME_OPERATIONS = "operations";
   public static final String NAME_PARAMPATH = "parampath";
   public static final String NAME_PARAMQUERY = "paramquery";
   public static final String NAME_PASSWORD = "password";
   public static final String NAME_PHASE = "phase";
   public static final String NAME_PLUGIN = "plugin";
   public static final String NAME_PLUGINID = "pluginid";
   public static final String NAME_PLUGINS = "plugins";
   public static final String NAME_POLICY = "policy";
   public static final String NAME_POLICYID = "policyid";
   public static final String NAME_POLICIES = "policies";
   public static final String NAME_PRINCIPAL = "principal";
   public static final String NAME_PROCESS = "process";
   public static final String NAME_PROCESSES = "processes";
   public static final String NAME_PROPERTIES = "properties";
   public static final String NAME_PROPERTY = "property";
   public static final String NAME_QUERY = "query";
   public static final String NAME_QUESTIONS = "questions";
   public static final String NAME_QUANTITY = "quantity";
   public static final String NAME_READONLY = "readonly";
   public static final String NAME_REQUEST = "request";
   public static final String NAME_REQUIRED = "required";
   public static final String NAME_RELATIONSHIP = "relationship";
   public static final String NAME_RELATIONSHIPID = "relationshipid";
   public static final String NAME_RELATIONSHIPS = "relationships";
   public static final String NAME_RESOURCE = "resource";
   public static final String NAME_RESOURCEID = "resourceid";
   public static final String NAME_RESOURCES = "resources";
   public static final String NAME_RESPONSE = "response";
   public static final String NAME_RESULTS = "results";
   public static final String NAME_SEARCH = "search";
   public static final String NAME_SECRET = "secret";
   public static final String NAME_SERVICE = "service";
   public static final String NAME_SESSION = "session";
   public static final String NAME_SESSIONID = "sessionid";
   public static final String NAME_SESSIONINFO = "sessioninfo";
   public static final String NAME_SESSIONS = "sessions";
   public static final String NAME_SIBLINGS = "siblings";
   public static final String NAME_SORT = "sort";
   public static final String NAME_SOURCE = "source";
   public static final String NAME_STATE = "state";
   public static final String NAME_STAT = "statistic";
   public static final String NAME_STATID = "statisticid";
   public static final String NAME_STATS = "statistics";
   public static final String NAME_STATUS = "status";
   public static final String NAME_STRUCTURE = "structure";
   public static final String NAME_STRUCTURES = "structures";
   public static final String NAME_SUBATTR = "subattribute";
   public static final String NAME_SUBATTRS = "subattributes";
   public static final String NAME_SUBJECT = "subject";
   public static final String NAME_SUBJECTID = "subjectid";
   public static final String NAME_SUBJECTS = "subjects";
   public static final String NAME_TARGET = "target";
   public static final String NAME_TARGETID = "targetid";
   public static final String NAME_TARGETS = "targets";
   public static final String NAME_TEMPLATE = "template";
   public static final String NAME_TO = "to";
   public static final String NAME_TYPE = "type";
   public static final String NAME_TYPES = "types";
   public static final String NAME_UNIQUEID = "uniqueid";
   public static final String NAME_UPDATED = "updated";
   public static final String NAME_URI = "uri";
   public static final String NAME_VALUE = "value";
   public static final String NAME_VALUES = "values";
   public static final String NAME_VERSION = "version";
   public static final String NAME_VIEW = "view";
   public static final String NAME_VIEWID = "viewid";
   public static final String NAME_VIEWS = "views";
   public static final String NAME_VIRTUAL = "virtual";

   /**
    * Set the Structure's name.
    *
    * @param name Structure's name
    */
   public void setName(String name);

   /**
    * Get the Structure's name.
    *
    * @return String The Structure's name
    */
   public String getName();

   /**
    * Add String to the Structure's value.
    * Will throw an StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value's type does NOT match an existing non-null type
    * <li>Structure has children
    * </ul>
    *
    * @param value String to add
    * @throws StructureException
    */
   public void addValue(String value) throws StructureException;

   /**
    * Add Boolean to the Structure's value.
    * Will throw an StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value's type does NOT match an existing non-null type
    * <li>Structure has children
    * </ul>
    *
    * @param value Boolean to add
    * @throws StructureException
    */
   public void addValue(Boolean value) throws StructureException;

   /**
    * Add boolean to the Structure's value.
    * Will throw an StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value's type does NOT match an existing non-null type
    * <li>Structure has children
    * </ul>
    *
    * @param value boolean to add
    * @throws StructureException
    */
   public void addValue(boolean value) throws StructureException;

   /**
    * Add Integer to the Structure's value.
    * Will throw an StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value's type does NOT match an existing non-null type
    * <li>Structure has children
    * </ul>
    *
    * @param value Integer to add
    * @throws StructureException
    */
   public void addValue(Integer value) throws StructureException;

   /**
    * Add integer to the Structure's value.
    * Will throw an StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value's type does NOT match an existing non-null type
    * <li>Structure has children
    * </ul>
    *
    * @param value integer to add
    * @throws StructureException
    */
   public void addValue(int value) throws StructureException;

   /**
    * Add Long as a value.
    * Will throw an StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value type does NOT match an existing type
    * <li>Structure has children
    * </ul>
    *
    * @param value Long to add
    * @throws StructureException
    */
   public void addValue(final Long value) throws StructureException;

   /**
    * Add long as a value.
    * Will throw an StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value type does NOT match an existing type
    * <li>Structure has children
    * </ul>
    *
    * @param value long to add
    * @throws StructureException
    */
   public void addValue(final long value) throws StructureException;

   /**
    *
    * Add StructureIF as a value.
    * Will throw an StructureException if:
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
   public void addValue(final StructureIF value) throws StructureException;

   /**
    * Add Object as a value.
    * Will throw an StructureException if:
    * <ul>
    * <li>Value is null
    * <li>Value's type does NOT match an existing non-null type
    * <li>Structure has children
    * </ul>
    *
    * @param value Object to add
    * @throws StructureException
    */
   public void addValue(Object value) throws StructureException;

   /**
    * Set to TRUE if the value will support multiple values
    *
    * @param value boolean to "flag" if value is multi-valued
    * @since 2.2.0
    */
   public void setMultiValued(boolean value);

   /**
    * Returns TRUE if configured to support multiple values
    *
    * @return boolean True if supports multiple values
    * @since 2.2.0
    */
   public boolean isMultiValued();

   /**
    * Returns the value (if single valued) or the first value (is multi valued)
    * If there is no value, null will be returned.
    *
    * @return Object the (first) value
    */
   public Object getValue();

   /**
    * Returns a String representation of the (first) value.
    *
    * @return String representation of the (first) value
    */
   public String getValueAsString();

   /**
    * Returns the List of values (Object).
    * An ordered list (order of insertion) of values.
    * Use the <b>getValueType()</b> method to determine the value's real type.
    * All values will be of the same type.
    *
    * @return List ordered list of values
    */
   public List<Object> getValues();

   /**
    * Returns an Array of values (Object).
    * An ordered array (order of insertion) of values.
    * Use the <b>getValueType()</b> method to determine the value's real type.
    * All values will be of the same type.
    *
    * @return Object[] ordered array of values
    */
   public Object[] getValuesAsArray();

   /**
    * Returns the Structure's Type.
    *
    * @return StructureType Structure's Type
    */
   public StructureType getValueType();

   /**
    * Returns a String representation of all (List) the values.
    *
    * @return String representation of all (List) the values.
    */
   public String getValuesAsString();

   /**
    * Adds the provided Structure as a Child.
    * Will throw an StructureException if:
    * <ul>
    * <li>Child Structure is null
    * <li>Structure contains a value
    * <li>Structure Type is invalid
    * </ul>
    *
    * @param struct Child Structure
    * @throws StructureException
    */
   public void addChild(StructureIF struct) throws StructureException;

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
   public void setChild(int index, StructureIF child) throws StructureException;

   /**
    * Returns TRUE if a Child exists with the specified name.
    *
    * @param name Child Structure name
    * @return boolean TRUE if has children
    */
   public boolean hasChild(String name);

   /**
    * Returns the specified Child Structure.
    * If the Child does not exist, the returned Structure is null.
    *
    * @param name Child Structure's name
    * @return StructureIF the Child
    */
   public StructureIF getChild(String name);

   /**
    * Returns a List (of Children) that match the provided name.
    * All of the Child Structures are search, those that match the
    * provided name will be returned in a List.
    *
    * @param name The child name
    * @return List of matching children
    */
   public List<StructureIF> getChildren(String name);

   /**
    * Returns an Structure array (of Children) that match the provided name.
    * All of the Child Structures are search, those that match the
    * provided name will be returned in an array.
    *
    * @param name The child name
    * @return StructureIF[] Array of matching children
    */
   public StructureIF[] getChildrenAsArray(String name);

   /**
    * Returns all Child Structures as a List.
    * The List is ordered (order of insertion).
    *
    * @return List Child Structures.
    */
   public List<StructureIF> getChildren();

   /**
    * Returns all Child Structures as an Array.
    * The Array is ordered (order of insertion).
    *
    * @return StructureIF[] Child Structures.
    */
   public StructureIF[] getChildrenAsArray();

   /**
    * Returns a String Array containing the Childrens id (name).
    * The Array is ordered (order of insertion).
    *
    * @return String[] Child ids (names)
    */
   public String[] getChildrenIds();

   /**
    * Returns TRUE if the Structure contains one or more Children.
    *
    * @return boolean contains child(ren)
    */
   public boolean hasChildren();

   /**
    * Returns the Structure's Parent (if it is a Child).
    * This will be null if it is not a Child.
    *
    * @return StructureIF Parent Structure
    */
   public StructureIF getParent();

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
   public void setParent(StructureIF struct);

   /**
    * Set the Structure's State.
    *
    * @param state A valid State
    */
   public void setState(State state);

   /**
    * Get the Structure's State.
    *
    * @return State the current state
    */
   public State getState();

   /**
    * Get the Properties object, containing all the properties
    *
    * @return Properties
    */
   public Properties getProperties();

   /**
    * Add the Property to the Properties
    *
    * @param props a Property object
    */
   public void setProperties(Properties props);

   /**
    * Get the Property value
    *
    * @param key String for the property name
    * @return String
    */
   public String getProperty(String key);

   /**
    * Add the Property using the String name and String value
    *
    * @param key
    * @param value
    */
   public void setProperty(String key, String value);

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
   public void setType(StructureType type) throws StructureException;
}
