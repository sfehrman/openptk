/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2013 Project OpenPTK
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
package org.openptk.config;

/**
 *
 * @author Scott Fehrman
 * @since 2.2.0
 */
//===================================================================
public interface XMLConfigIF
//===================================================================
{

   public static final String ELEM_NAME_ACTION = "Action";
   public static final String ELEM_NAME_ASSIGNMENT = "Assignment";
   public static final String ELEM_NAME_ASSIGNMENTS = "Assignments";
   public static final String ELEM_NAME_ASSOCIATION = "Association";
   public static final String ELEM_NAME_ASSOCIATIONS = "Associations";
   public static final String ELEM_NAME_ATTRGROUP = "AttrGroup";
   public static final String ELEM_NAME_ATTRGROUPS = "AttrGroups";
   public static final String ELEM_NAME_ATTRMAP = "AttrMap";
   public static final String ELEM_NAME_ATTRMAPS = "AttrMaps";
   public static final String ELEM_NAME_ATTRIBUTE = "Attribute";
   public static final String ELEM_NAME_ARGUMENT = "Argument";
   public static final String ELEM_NAME_AUTHENTICATORS = "Authenticators";
   public static final String ELEM_NAME_AUTHENTICATOR = "Authenticator";
   public static final String ELEM_NAME_CLIENTS = "Clients";
   public static final String ELEM_NAME_CLIENT = "Client";
   public static final String ELEM_NAME_CONNECTION = "Connection";
   public static final String ELEM_NAME_CONNECTIONS = "Connections";
   public static final String ELEM_NAME_CONTEXT = "Context";
   public static final String ELEM_NAME_CONTEXTS = "Contexts";
   public static final String ELEM_NAME_CONVERTER = "Converter";
   public static final String ELEM_NAME_CONVERTERS = "Converters";
   public static final String ELEM_NAME_DATUM = "Datum";
   public static final String ELEM_NAME_DATA = "Data";
   public static final String ELEM_NAME_DECIDER = "Decider";
   public static final String ELEM_NAME_DECIDERS = "Deciders";
   public static final String ELEM_NAME_DEFAULTS = "Defaults";
   public static final String ELEM_NAME_DEFINITION = "Definition";
   public static final String ELEM_NAME_DEFINITIONS = "Definitions";
   public static final String ELEM_NAME_DESTINATION = "Destination";
   public static final String ELEM_NAME_ENCRYPTOR = "Encryptor";
   public static final String ELEM_NAME_ENCRYPTORS = "Encryptors";
   public static final String ELEM_NAME_ENFORCER = "Enforcer";
   public static final String ELEM_NAME_ENFORCERS = "Enforcers";
   public static final String ELEM_NAME_FUNCTION = "Function";
   public static final String ELEM_NAME_GLOBAL = "Global";
   public static final String ELEM_NAME_LOGGER = "Logger";
   public static final String ELEM_NAME_LOGGERS = "Loggers";
   public static final String ELEM_NAME_MATCH = "Match";
   public static final String ELEM_NAME_MODE = "Mode";
   public static final String ELEM_NAME_MODEL = "Model";
   public static final String ELEM_NAME_MODELS = "Models";
   public static final String ELEM_NAME_OPERATION = "Operation";
   public static final String ELEM_NAME_OPERATIONS = "Operations";
   public static final String ELEM_NAME_OPERATIONACTIONS = "OperationActions";
   public static final String ELEM_NAME_POLICY = "Policy";
   public static final String ELEM_NAME_POLICIES = "Policies";
   public static final String ELEM_NAME_PLUGIN = "Plugin";
   public static final String ELEM_NAME_PLUGINS = "Plugins";
   public static final String ELEM_NAME_PROCESS = "Process";
   public static final String ELEM_NAME_PROCESSES = "Processes";
   public static final String ELEM_NAME_PROPERTY = "Property";
   public static final String ELEM_NAME_PROPERTIES = "Properties";
   public static final String ELEM_NAME_QUERY = "Query";
   public static final String ELEM_NAME_RELATIONSHIP = "Relationship";
   public static final String ELEM_NAME_RELATIONSHIPS = "Relationships";
   public static final String ELEM_NAME_SECURITY = "Security";
   public static final String ELEM_NAME_SESSION = "Session";
   public static final String ELEM_NAME_SOURCE = "Source";
   public static final String ELEM_NAME_STRUCTURE = "Structure";
   public static final String ELEM_NAME_STRUCTURES = "Structures";
   public static final String ELEM_NAME_SUBATTRIBUTE = "SubAttribute";
   public static final String ELEM_NAME_SUBATTRIBUTES = "SubAttributes";
   public static final String ELEM_NAME_TARGET = "Target";
   public static final String ELEM_NAME_TARGETS = "Targets";
   public static final String ELEM_NAME_TYPE = "Type";
   public static final String ELEM_NAME_TYPES = "Types";
   public static final String ELEM_NAME_VIEW = "View";
   public static final String ELEM_NAME_VIEWS = "Views";
   //
   public static final String ELEM_ATTR_ACCESS = "access";
   public static final String ELEM_ATTR_ASSOCIATION = "association";
   public static final String ELEM_ATTR_ATTRGROUP = "attrgroup";
   public static final String ELEM_ATTR_AUTHENTICATOR_LEVEL = "level";
   public static final String ELEM_ATTR_CLASSNAME = "classname";
   public static final String ELEM_ATTR_CONTEXT = "context";
   public static final String ELEM_ATTR_DATUM = "datum";
   public static final String ELEM_ATTR_DECIDER = "decider";
   public static final String ELEM_ATTR_DEFAULT = "default";
   public static final String ELEM_ATTR_DEFINITION = "definition";
   public static final String ELEM_ATTR_DESCRIPTION = "description";
   public static final String ELEM_ATTR_EFFECT = "effect";
   public static final String ELEM_ATTR_ENABLED = "enabled";
   public static final String ELEM_ATTR_ENCRYPTED = "encrypted";
   public static final String ELEM_ATTR_ENVIRONMENT = "environment";
   public static final String ELEM_ATTR_ID = "id";
   public static final String ELEM_ATTR_NAME = "name";
   public static final String ELEM_ATTR_MAPTO = "mapto";
   public static final String ELEM_ATTR_MAPFROM = "mapfrom";
   public static final String ELEM_ATTR_MODE = "mode";
   public static final String ELEM_ATTR_MULTIVALUED = "multivalued";
   public static final String ELEM_ATTR_READONLY = "readonly";
   public static final String ELEM_ATTR_REQUIRED = "required";
   public static final String ELEM_ATTR_CONNECTION = "connection";
   public static final String ELEM_ATTR_SECRET = "secret";
   public static final String ELEM_ATTR_SERVICENAME = "servicename";
   public static final String ELEM_ATTR_TYPE = "type";
   public static final String ELEM_ATTR_USEEXISTING = "useexisting";
   public static final String ELEM_ATTR_VALUE = "value";
   public static final String ELEM_ATTR_VIRTUAL = "virtual";
   //
   public static final String PROP_AUDIT = "audit";
   public static final String PROP_AUTHENTICATOR_CLASSNAME = "authenticator.classname";
   public static final String PROP_AUTHENTICATOR_CONTEXT = "authenticator.context";
   public static final String PROP_AUTHENTICATOR_DESCRIPTION = "authenticator.description";
   public static final String PROP_DEBUG = "debug.level";
   public static final String PROP_CONTEXT_DEFAULT = "context.default";
   public static final String PROP_CONTEXT_CLASSNAME = "context.classname";
   public static final String PROP_CONTEXT_DESCRIPTION = "context.description";
   public static final String PROP_CRYPTO_CLASSNAME = "crypto.classname";
   public static final String PROP_DECIDER_CLASSNAME = "decider.classname";
   public static final String PROP_DEFINITION_CLASSNAME = "definition.classname";
   public static final String PROP_DEFINITION_DESCRIPTION = "definition.description";
   public static final String PROP_ENFORCER_CLASSNAME = "enforcer.classname";
   public static final String PROP_KEY = "key";
   public static final String PROP_LOGGER = "logger";
   public static final String PROP_LOGGER_CLASSNAME = "logger.classname";
   public static final String PROP_MODEL_CLASSNAME = "model.classname";
   public static final String PROP_MODEL_DESCRIPTION = "model.description";
   public static final String PROP_OPENPTK_CONFIG_FILE = "openptk.config.file";
   public static final String PROP_OPENPTK_CONFIG_SOURCE = "openptk.config.source";
   public static final String PROP_OPENPTK_VALIDATE_FILE = "openptk.validate.file";
   public static final String PROP_OPENPTK_VALIDATE_SOURCE = "openptk.validate.source";
   public static final String PROP_OPERATION_CLASSNAME = "operation.classname";
   public static final String PROP_POLICY_CLASSNAME = "policy.classname";
   public static final String PROP_RELATIONSHIP_CLASSNAME = "relationship.classname";
   public static final String PROP_RELATIONSHIP_DESCRIPTION = "relationship.description";
   public static final String PROP_SEARCH_DEFAULT_ORDER = "search.default.order";
   public static final String PROP_SEARCH_OPERATORS = "search.operators";
   public static final String PROP_SERVICE_CLASSNAME = "service.classname";
   public static final String PROP_SORT = "sort";
   public static final String PROP_TARGET_CLASSNAME = "target.classname";
   public static final String PROP_TIMEOUT = "timeout";
   public static final String PROP_TIMESTAMP = "timestamp";
   //
   public static final String LOGGER_NAME = "openptk";
   public static final String SCHEMA_NAME = "openptk.xsd";
   public static final String DEFAULT_SEARCH_ATTR = "firstname";
}
