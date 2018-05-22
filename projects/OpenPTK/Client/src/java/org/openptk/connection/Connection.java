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
 * Portions Copyright 2011-2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.connection;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Properties;

import org.openptk.api.Attribute;
import org.openptk.api.AttributeIF;
import org.openptk.api.DataType;
import org.openptk.api.Element;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Opcode;
import org.openptk.api.Output;
import org.openptk.api.Query;
import org.openptk.api.State;
import org.openptk.crypto.CryptoIF;
import org.openptk.crypto.DESCrypto;
import org.openptk.crypto.Encryptor;
import org.openptk.exception.ConnectionException;
import org.openptk.exception.ConverterException;
import org.openptk.exception.CryptoException;
import org.openptk.exception.StructureException;
import org.openptk.logging.Logger;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.ConverterIF;
import org.openptk.structure.StructureIF;
import org.openptk.structure.StructureType;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Connection extends Element implements ConnectionIF
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private final static String ENCODING_SCHEME = "UTF-8";
   private boolean _debug = false;
   private boolean _sessionChanged = false;
   private String _sessionId = null;
   private String _logId = null;
   protected String _uriClients = null;
   protected String _uriContexts = null;
   protected String _uriSubjects = null;
   protected String _uriLogin = null;
   protected String _uriLogout = null;
   protected String _uriSessionInfo = null;
   protected ConverterIF _converter = null;

   /**
    * Create a new instance.
    *
    * @throws Exception
    */
   //----------------------------------------------------------------
   public Connection(Properties props) throws ConnectionException
   //----------------------------------------------------------------
   {
      super();

      if (props == null)
      {
         this.handleError(CLASS_NAME + ":constructor(): Properties argument is null");
      }

      this.setProperties(props);
      this.init();

      return;
   }

   /**
    * Close the open connection. The Session will be removed.
    *
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public void close() throws ConnectionException
   //----------------------------------------------------------------
   {
      this.setState(State.DESTROYED);
      this.setStatus("closed");
      this.setProperties(new Properties());
      this.setAttributes(new HashMap<String, AttributeIF>());

      _converter = null;

      return;
   }

   /**
    * Execute the operation (Opcode) on the Server.
    *
    * @param opcode Opcode what operation to execute
    * @param input Input data used by the operation
    * @return Output results of the operation
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized Output execute(final Opcode opcode, final Input input) throws ConnectionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      Output output = null;

      if (input == null)
      {
         this.handleError(METHOD_NAME + "Input is null.");
      }

      if (this.getState() == State.DESTROYED)
      {
         this.handleError(METHOD_NAME + "Connection has been closed");
      }

      if (this.isDebug())
      {
         this.logInfo(METHOD_NAME + "opcode=" + opcode.toString());
      }

      try
      {
         switch (opcode)
         {
            case CREATE:
               output = doCreate(input);
               break;
            case READ:
               output = doRead(input);
               break;
            case UPDATE:
               output = doUpdate(input);
               break;
            case DELETE:
               output = doDelete(input);
               break;
            case SEARCH:
               output = doSearch(input);
               break;
            case PWDCHANGE:
               output = doPwdChange(input);
               break;
            case PWDRESET:
               output = doPwdReset(input);
               break;
            case PWDFORGOT:
               output = doPwdForgot(input);
               break;
         }
      }
      catch (StructureException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      return output;
   }

   /**
    * Gets the id of the current Context.
    *
    * @return String context id
    */
   //----------------------------------------------------------------
   @Override
   public final String getContextId()
   //----------------------------------------------------------------
   {
      String contextId = null;
      AttributeIF attr = null;

      attr = this.getAttribute(StructureIF.NAME_CONTEXT);

      if (attr != null)
      {
         contextId = attr.getValueAsString();
      }

      return contextId;
   }

   /**
    * Get all of the available Context Ids.
    *
    * @return String[] Array of available Context Ids
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getContextIds()
   //----------------------------------------------------------------
   {
      Object value = null;
      String[] contextIds = null;
      AttributeIF attr = null;

      attr = this.getAttribute(StructureIF.NAME_CONTEXTS);

      if (attr != null)
      {
         value = attr.getValue();
         if (attr.isMultivalued())
         {
            contextIds = (String[]) value;
         }
         else
         {
            contextIds = new String[1];
            contextIds[0] = (String) value;
         }
      }

      return contextIds;
   }

   /**
    * Set the "current" Context Id.
    *
    * When a connection created, the default context as defined
    * by the server is set as the the "current" context. The client application
    * can make a change and change the "current" context at anytime. This is
    * the method that will make that change.
    *
    * @param contextId a valid Context Id
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setContextId(final String contextId) throws ConnectionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      boolean foundContext = false;
      String[] contextIds = null;
      AttributeIF attribute = null;

      /*
       * Check to see if the current connection has already been closed.
       */

      if (this.getState() == State.DESTROYED)
      {
         this.handleError(METHOD_NAME + "Connection has been closed");
      }

      /*
       * Validate that we were passed a non-null, non-zero length contextId.
       */

      if (contextId == null || contextId.length() < 1)
      {
         this.handleError(METHOD_NAME + "contextId is null");
      }

      /*
       * Get the list of valid contexts, as defined by the server, that we
       * will use to match the contextId passed to validate that it's a valid
       * context.
       *
       * If the list of contextIds is null or zero-length, then error off.
       */

      contextIds = this.getContextIds();

      if (contextIds == null || contextIds.length < 1)
      {
         this.handleError(METHOD_NAME + "no available contexts");
      }

      for (String currContext : contextIds)
      {
         if (currContext.equals(contextId))
         {
            foundContext = true;
         }
      }

      /*
       * Great, we found a the context we are setting in the list of available
       * contexts. In this case, we will create an Attribute (context) with
       * this new contextId as the value and add it to our Conection.
       *
       * If we didn't find it, then the contextId is invalid and an error
       * will occur.
       */

      if (foundContext)
      {
         attribute = this.getAttribute(StructureIF.NAME_CONTEXT);

         if (attribute == null)
         {
            attribute = new Attribute(StructureIF.NAME_CONTEXT);

         }

         attribute.setValue(contextId);
         this.addAttribute(attribute);

      }
      else
      {
         this.handleError(METHOD_NAME + "Specified context '" + contextId
            + "' is not valid");
      }

      /*
       * We are changing the contextId, so we'll also change the base
       * _uriSubjects to the proper URL ending in Subject.
       */
      _uriSubjects = _uriContexts + "/" + contextId + "/"
         + StructureIF.NAME_SUBJECTS;

      return;
   }

   /**
    * Get the debug flag.
    *
    * @return boolean will the debug data be displayed.
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isDebug()
   //----------------------------------------------------------------
   {
      return _debug;
   }

   /**
    * Has the Session changed from the initial connection.
    *
    * @return boolean True if the session has changed
    */
   //----------------------------------------------------------------
   @Override
   public final boolean hasSessionChanged()
   //----------------------------------------------------------------
   {
      return _sessionChanged;
   }

   /**
    * Set the debug flag.
    *
    * @param debug should debug data be displayed.
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
    * Get the String representing a Session data item.
    *
    * @return String Session data
    * @throws ConnectionException
    */
   //----------------------------------------------------------------
   @Override
   public abstract String getSessionData(ConnectionIF.Session session) throws ConnectionException;
   //----------------------------------------------------------------

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   /**
    * Get the Session Id. Will be null if connection is closed.
    *
    * @return String the Session Id
    */
   //----------------------------------------------------------------
   protected String getSessionId()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_sessionId != null)
      {
         str = new String(_sessionId); // make a copy
      }

      return str;
   }

   /**
    * Execute CREATE operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected Output doCreate(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      throw new ConnectionException(METHOD_NAME + "Operation Not Implemented");
   }

   /**
    * Execute READ operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected Output doRead(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      throw new ConnectionException(METHOD_NAME + "Operation Not Implemented");
   }

   /**
    * Execute UPDATE operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected Output doUpdate(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      throw new ConnectionException(METHOD_NAME + "Operation Not Implemented");
   }

   /**
    * Execute DELETE operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected Output doDelete(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      throw new ConnectionException(METHOD_NAME + "Operation Not Implemented");
   }

   /**
    * Execute SEARCH operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected Output doSearch(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      throw new ConnectionException(METHOD_NAME + "Operation Not Implemented");
   }

   /**
    * Execute PWDCHANGE operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected Output doPwdChange(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      throw new ConnectionException(METHOD_NAME + "Operation Not Implemented");
   }

   /**
    * Execute PWDRESET operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected Output doPwdReset(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      throw new ConnectionException(METHOD_NAME + "Operation Not Implemented");
   }

   /**
    * Execute PWDFORGOT operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected Output doPwdForgot(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      throw new ConnectionException(METHOD_NAME + "Operation Not Implemented");
   }

   /**
    * Process the Client Structure that comes from the server. In this case the
    * types of items that are setup as ConnectionAttributes include:
    *
    * Example:
    * UniqueID JerseyConnection
    * Context Employees-MySQL-JDBC
    * Contexts Employees-MySQL-JDBC, Employees-UnboundID-LDAP
    *
    * This method is generally called by the init method of a child Connection
    * class.
    *
    * @param structClient
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected void processClientStructure(final StructureIF structClient) throws ConnectionException
   //----------------------------------------------------------------
   {
      Object values[] = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String name = null;
      String propName = null;
      String propPrefix = null;
      String elemId = null;
      Object uniqueId = null;
      String value = null;
      String[] strArray = null;
      String structName = null;
      AttributeIF attr = null;
      StructureIF struct = null;
      StructureIF structProp = null;
      StructureType type = null;
      ElementIF elem = null;

      if (structClient == null)
      {
         this.handleError(METHOD_NAME + "Client Structure is null");
      }

      /*
       * Get the uniqueid from the Client
       * Use the value to set the uniqueId of the connection (this)
       */

      uniqueId = this.getValueFromStructure(structClient, StructureIF.NAME_UNIQUEID, null);

      structName = StructureIF.NAME_UNIQUEID;

      struct = structClient.getChild(structName);

      type = struct.getValueType();

      switch (type)
      {
         case STRING:
            if (uniqueId.toString().length() < 1)
            {
               this.handleError(METHOD_NAME + "UniqueId is empty");
            }
            this.setUniqueId((String) uniqueId);
            break;
         case INTEGER:
            this.setUniqueId((Integer) uniqueId);
            break;
         case LONG:
            this.setUniqueId((Long) uniqueId);
            break;
         default:
            this.handleError(METHOD_NAME + "UniqueId is a '"
               + type.toString() + "', which is not supported");
            break;
      }

      /*
       * Get the "available" Contexts
       * Save them as an Attribute within the connection (this)
       */

      values = this.getValuesFromStructure(structClient, StructureIF.NAME_CONTEXTS, StructureType.STRING);

      attr = new Attribute(StructureIF.NAME_CONTEXTS);

      strArray = new String[values.length];
      for (int i = 0; i < values.length; i++)
      {
         value = (String) values[i];
         if (value == null || value.length() < 1)
         {
            this.handleError(METHOD_NAME + "Structure '"
               + structName + "' has an empty value");
         }
         strArray[i] = value;
      }
      attr.setValue(strArray);

      this.addAttribute(attr);


      /*
       * Get the default Context.
       *
       * Set the "current" Context of this connection
       */

      value = (String) this.getValueFromStructure(structClient, StructureIF.NAME_DEFAULT, StructureType.STRING);

      this.setContextId(value);

      /*
       * Get the Properties, each one must have a name and value
       * Add/Set each property within the connection (this)
       */

      structName = StructureIF.NAME_PROPERTIES;

      struct = structClient.getChild(structName);
      if (struct != null)
      {
         strArray = struct.getChildrenIds();
         if (strArray != null && strArray.length > 0)
         {
            for (int i = 0; i < strArray.length; i++)
            {
               name = strArray[i];
               if (name != null && name.length() > 0)
               {
                  structProp = struct.getChild(name);
                  if (structProp != null)
                  {
                     value = structProp.getValueAsString();
                     if (value != null && value.length() > 0)
                     {
                        this.setProperty(name, value);
                     }
                  }
               }
            }
         }
      }

      /*
       * Update the converter with Structure Info
       * indicate which attributes should be treated as multivalue
       * needed for the "xml" converter
       * format of the properties:
       * syntax: converterType."mutlivalue".structureId="value"
       * example: xml.multivalue.roles=value
       */

      propPrefix = _converter.getType().toString().toLowerCase() + "." 
         + StructureIF.NAME_MULTIVALUE + ".";

      for (Object obj : this.getProperties().keySet())
      {
         if (obj != null && obj instanceof String)
         {
            propName = (String) obj;
            if (propName != null && propName.startsWith(propPrefix))
            {
               elemId = propName.substring(propPrefix.length());
               elem = new Element();
               elem.setUniqueId(elemId);
               elem.setProperty(StructureIF.NAME_MULTIVALUE, "true");
               _converter.setStructInfo(elemId, elem);
            }
         }
      }

      return;
   }

   /**
    * Convert the "encoded" string to a StructureIF object.
    *
    * @param str
    * @return StructureIF object
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final StructureIF decode(final String str) throws ConnectionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      StructureIF structOut = null;

      try
      {
         structOut = _converter.decode(str);
      }
      catch (ConverterException ex)
      {
         this.handleError(METHOD_NAME + "_converter.decode(str): " + ex.getMessage());
      }

      return structOut;
   }

   /**
    * Convert the StructureIF object to an "encoded" string.
    *
    * @param struct
    * @return String encoded data that represents the StructureIF
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final String encode(final StructureIF struct) throws ConnectionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String output = null;

      try
      {
         output = _converter.encode(struct);
      }
      catch (ConverterException ex)
      {
         this.handleError(METHOD_NAME + "_converter.encode(str): ");
      }

      return output;
   }

   /**
    * URL Encode a string. Typically this is used to encode strings that
    * will be embedded in a URI/URL bound for the server.
    *
    * @param str String Data to be encoded
    * @return String URL encoded data that represents the String
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final String urlEncode(final String str) throws ConnectionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String output = null;

      try
      {
         output = URLEncoder.encode(str, ENCODING_SCHEME);
      }
      catch (UnsupportedEncodingException ex)
      {
         throw new ConnectionException(METHOD_NAME + "Invalid Encoding Scheme: " + ENCODING_SCHEME);
      }

      return output;
   }

   /**
    * URL Decode a string. Typically this is used to decode strings that
    * have come back in a URI/URL from the server.
    *
    * @param str String Data to be decoded
    * @return String URL decoded data that represents the String
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final String urlDecode(final String str) throws ConnectionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String output = null;

      try
      {
         output = URLDecoder.decode(str, ENCODING_SCHEME);
      }
      catch (UnsupportedEncodingException ex)
      {
         throw new ConnectionException(METHOD_NAME + "Invalid Decoding Scheme: " + ENCODING_SCHEME);
      }

      return output;
   }

   /**
    * Create a StructureIF object from the Input object.
    *
    * @param opcode
    * @param input
    * @return StructureIF
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected StructureIF getStructureFromInput(final Opcode opcode, final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      boolean checkUniqueId = false;
      boolean checkAttrs = false;
      boolean checkQuery = false;
      Object uniqueId = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String search = null;
      String[] attrNames = null;
      DataType uidType = null;
      Query query = null;
      AttributeIF attr = null;
      StructureIF structSubject = null;
      StructureIF structAttrs = null;

      if (input == null)
      {
         this.handleError(METHOD_NAME + "Input is null");
      }

      structSubject = new BasicStructure(StructureIF.NAME_SUBJECT);

      /*
       * Determine what to "check for" based on the Opcode
       */

      switch (opcode)
      {
         case CREATE:
            checkUniqueId = true;
            checkAttrs = true;
            break;
         case READ:
            checkUniqueId = true;
            break;
         case UPDATE:
            checkUniqueId = true;
            checkAttrs = true;
            break;
         case DELETE:
            checkUniqueId = true;
            break;
         case SEARCH:
            checkQuery = true;
            break;
         case PWDCHANGE:
            checkUniqueId = true;
            checkAttrs = true;
            break;
         case PWDRESET:
            checkUniqueId = true;
            break;
         case PWDFORGOT:
            checkAttrs = true;
            checkUniqueId = true;
            break;
      }

      if (checkUniqueId)
      {
         uniqueId = input.getUniqueId();
         if (uniqueId != null)
         {
            uidType = input.getUniqueIdType();
            switch (uidType)
            {
               case STRING:
                  structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (String) uniqueId));
                  break;
               case INTEGER:
                  structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Integer) uniqueId));
                  break;
               case LONG:
                  structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Long) uniqueId));
                  break;
            }
         }
      }

      if (checkQuery)
      {
         query = input.getQuery();
         if (query != null && query.getType() == Query.Type.NOOPERATOR)
         {
            if (query.getName().equalsIgnoreCase(StructureIF.NAME_SEARCH))
            {
               search = query.getValue();
               if (search != null && search.length() > 0)
               {
                  structSubject.addChild(new BasicStructure(StructureIF.NAME_SEARCH, search));
               }
            }
         }
      }

      if (checkAttrs)
      {
         attrNames = input.getAttributeNames();
         if (attrNames != null && attrNames.length > 0)
         {
            structAttrs = new BasicStructure(StructureIF.NAME_ATTRIBUTES);
            structSubject.addChild(structAttrs);

            for (int i = 0; i < attrNames.length; i++)
            {
               attr = input.getAttribute(attrNames[i]);
               if (attr != null)
               {
                  structAttrs.addChild(this.getStructureFromAttribute(attr));
               }
            }
         }
      }

      return structSubject;
   }

   /**
    * Create a StructureIF object from the AttributeIF object.
    *
    * @param attr
    * @return StructureIF
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected StructureIF getStructureFromAttribute(final AttributeIF attr) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      Object value = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String name = null;
      DataType type = null;
      StructureIF struct = null;

      name = attr.getName();
      if (name != null && name.length() > 0)
      {
         value = attr.getValue();
         if (value != null)
         {
            type = attr.getType();
            if (attr.isMultivalued())
            {
               switch (type)
               {
                  case STRING:
                     struct = new BasicStructure(name, (String[]) value);
                     break;
                  case INTEGER:
                     struct = new BasicStructure(name, (Integer[]) value);
                     break;
                  case LONG:
                     struct = new BasicStructure(name, (Long[]) value);
                     break;
                  case BOOLEAN:
                     struct = new BasicStructure(name, (Boolean[]) value);
                     break;
               }
            }
            else
            {
               switch (type)
               {
                  case STRING:
                     struct = new BasicStructure(name, (String) value);
                     break;
                  case INTEGER:
                     struct = new BasicStructure(name, (Integer) value);
                     break;
                  case LONG:
                     struct = new BasicStructure(name, (Long) value);
                     break;
                  case BOOLEAN:
                     struct = new BasicStructure(name, (Boolean) value);
                     break;
               }
            }
         }
         else
         {
            struct = new BasicStructure(name);
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "Attribute name is null");
      }

      return struct;
   }

   /**
    * Update the StructureIF from the Read response (Structure).
    *
    * @param output
    * @param structResponse
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected void updateOutputFromReadStructure(final Output output, final StructureIF structResponse) throws ConnectionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      ElementIF result = null;
      StructureIF structSubject = null;

      /*
       * Schema:
       * response:
       * uri: (string)
       * subject:
       * uniqueid: (string)
       * attributes:
       * name: value: [string | number | boolean]
       * ...
       */

      if (output == null)
      {
         this.handleError(METHOD_NAME + "Output is null");
      }

      if (structResponse == null)
      {
         this.handleError(METHOD_NAME + "Response Structure is null");
      }

      structSubject = structResponse.getChild(StructureIF.NAME_SUBJECT);

      if (structSubject == null)
      {
         this.handleError(METHOD_NAME + "Subject Structure is null");
      }

      result = this.getResultFromStructure(structSubject);
      output.addResult(result);

      return;
   }

   /**
    * Update Output from the Search response (Structure).
    *
    * @param output
    * @param structResponse
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected void updateOutputFromSearchStructure(final Output output, final StructureIF structResponse) throws ConnectionException
   //----------------------------------------------------------------
   {
      // Object[] values = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String length = null;
      String offset = null;
      String quantity = null;
      ElementIF result = null;
      StructureIF structResults = null;
      StructureIF[] structArraySubjects = null;
      StructureIF structSubject = null;
      StructureIF struct = null;

      /*
       * Schema:
       * response: structure
       * uri: string
       * length: number
       * offset: number
       * quantity: number
       * results: structure
       * subject:
       * uri:
       * uniqueid:
       * attributes:
       * name: value:
       * ...
       * subject:
       * ...
       */

      if (output == null)
      {
         this.handleError(METHOD_NAME + "Output is null");
      }

      if (structResponse == null)
      {
         this.handleError(METHOD_NAME + "Response Structure is null");
      }

      struct = structResponse.getChild(StructureIF.NAME_LENGTH);
      if (struct != null)
      {
         length = struct.getValueAsString();
         output.setProperty(StructureIF.NAME_LENGTH, length);
      }

      struct = structResponse.getChild(StructureIF.NAME_OFFSET);
      if (struct != null)
      {
         offset = struct.getValueAsString();
         output.setProperty(StructureIF.NAME_OFFSET, offset);
      }

      struct = structResponse.getChild(StructureIF.NAME_QUANTITY);
      if (struct != null)
      {
         quantity = struct.getValueAsString();
         output.setProperty(StructureIF.NAME_QUANTITY, quantity);
      }

      output.setStatus(quantity + " of " + length + ", offset=" + offset);

      structResults = structResponse.getChild(StructureIF.NAME_RESULTS);
      if (structResults == null)
      {
         this.handleError(METHOD_NAME + "Results are null");
      }

      structArraySubjects = structResults.getChildrenAsArray();
      if (structArraySubjects != null && structArraySubjects.length > 0)
      {
         for (int i = 0; i < structArraySubjects.length; i++)
         {
            structSubject = structArraySubjects[i];
            result = this.getResultFromStructure(structSubject);
            output.addResult(result);
         }
      }

      return;
   }

   /**
    * Handle an error condition.
    *
    * @param msg
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final void handleError(final String msg) throws ConnectionException
   //----------------------------------------------------------------
   {
      String str = null;

      if (msg == null || msg.length() < 1)
      {
         str = "(null message)";
      }
      else
      {
         str = msg;
      }

      throw new ConnectionException(str);
   }

   /**
    * Encrypt the provided String.
    *
    * @param value clear text value.
    * @return String encrypted value.
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final String encrypt(final String value) throws ConnectionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String encrypted = null;

      /*
       * Encrypt string using internal "phrase"
       */

      if (value != null && value.length() > 0)
      {
         try
         {
            encrypted = Encryptor.encrypt(Encryptor.NETWORK, value);
         }
         catch (CryptoException ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }
      }

      return encrypted;
   }

   //----------------------------------------------------------------
   protected synchronized void setSessionId(final String str)
   //----------------------------------------------------------------
   {
      _sessionId = str;
      return;
   }

   //----------------------------------------------------------------
   protected synchronized void setSessionChanged(final boolean bool)
   //----------------------------------------------------------------
   {
      _sessionChanged = bool;
      return;
   }

   //----------------------------------------------------------------
   protected void logInfo(final String str)
   //----------------------------------------------------------------
   {
      Logger.logInfo(_logId, str);
      return;
   }

   //----------------------------------------------------------------
   protected void logWarning(final String str)
   //----------------------------------------------------------------
   {
      Logger.logWarning(_logId, str);
      return;
   }

   //----------------------------------------------------------------
   protected void logError(final String str)
   //----------------------------------------------------------------
   {
      Logger.logError(_logId, str);
      return;
   }

   //----------------------------------------------------------------
   protected void debug(final String str)
   //----------------------------------------------------------------
   {
      if (this.isDebug())
      {
         Logger.logInfo(_logId, str);
      }
      return;
   }
   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------

   private ElementIF getResultFromStructure(final StructureIF structSubject) throws ConnectionException
   //----------------------------------------------------------------
   {
      Object[] values = null;
      Boolean[] bools = null;
      Integer[] nums = null;
      Long[] longs = null;
      String[] strs = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String name = null;
      Object uniqueId = null;
      ElementIF result = null;
      AttributeIF attr = null;
      StructureIF structId = null;
      StructureIF structAttrs = null;
      StructureIF[] structArrayAttrs = null;
      StructureIF structAttr = null;
      StructureType type = null;

      result = new Element();

      /*
       * get - set uniqueid
       */

      structId = structSubject.getChild(StructureIF.NAME_UNIQUEID);
      if (structId == null)
      {
         this.handleError(METHOD_NAME + "Required Structure is missing: '"
            + StructureIF.NAME_UNIQUEID + "'");
      }

      uniqueId = structId.getValue();

      if (uniqueId == null)
      {
         this.handleError(METHOD_NAME + "UniqueId is null");
      }

      type = structId.getValueType();

      switch (type)
      {
         case STRING:
            if (uniqueId.toString().length() < 1)
            {
               this.handleError(METHOD_NAME + "UniqueId is empty");
            }
            result.setUniqueId((String) uniqueId);
            break;
         case INTEGER:
            result.setUniqueId((Integer) uniqueId);
            break;
         case LONG:
            result.setUniqueId((Long) uniqueId);
            break;
         default:
            this.handleError(METHOD_NAME + "UniqueId is a '"
               + type.toString() + "', which is not supported");
            break;
      }

      /*
       * get - set attributes
       */

      structAttrs = structSubject.getChild(StructureIF.NAME_ATTRIBUTES);
      if (structAttrs != null)
      {
         structArrayAttrs = structAttrs.getChildrenAsArray();
         if (structArrayAttrs != null && structArrayAttrs.length > 0)
         {
            for (int i = 0; i < structArrayAttrs.length; i++)
            {
               structAttr = structArrayAttrs[i];

               /*
                * get the name, REQUIRED
                */

               name = structAttr.getName();
               if (name == null || name.length() < 1)
               {
                  handleError(METHOD_NAME + "Attribute name is null or empty");
               }

               attr = new Attribute(name);

               /*
                * get the value(s), OPTIONAL
                * The actual "value" or the "type" of the
                * structValue may be null
                */

               type = structAttr.getValueType();
               values = structAttr.getValuesAsArray();
               if (type != null && values != null && values.length > 0)
               {
                  if (structAttr.isMultiValued())
                  {
                     switch (type)
                     {
                        case STRING:
                           strs = new String[values.length];
                           for (int j = 0; j < values.length; j++)
                           {
                              strs[j] = (String) values[j];
                           }
                           attr.setValue(strs);
                           break;
                        case INTEGER:
                           nums = new Integer[values.length];
                           for (int j = 0; j < values.length; j++)
                           {
                              nums[j] = (Integer) values[j];
                           }
                           attr.setValue(nums);
                           break;
                        case LONG:
                           longs = new Long[values.length];
                           for (int j = 0; j < values.length; j++)
                           {
                              longs[j] = (Long) values[j];
                           }
                           attr.setValue(longs);
                           break;
                        case BOOLEAN:
                           bools = new Boolean[values.length];
                           for (int j = 0; j < values.length; j++)
                           {
                              bools[j] = (Boolean) values[j];
                           }
                           attr.setValue(bools);
                           break;
                     }
                  }
                  else
                  {
                     switch (type)
                     {
                        case STRING:
                           attr.setValue((String) values[0]);
                           break;
                        case INTEGER:
                           attr.setValue((Integer) values[0]);
                           break;
                        case LONG:
                           attr.setValue((Long) values[0]);
                           break;
                        case BOOLEAN:
                           attr.setValue((Boolean) values[0]);
                           break;
                     }
                  }
               }
               result.addAttribute(attr);
            }
         }
      }

      return result;
   }

   //----------------------------------------------------------------
   private void init() throws ConnectionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String propValue = null;
      String secret = null;
      CryptoIF crypto = null;

      /*
       * create the converter
       */

      try
      {
         _converter = (ConverterIF) Class.forName(SetupIF.CLASSNAME_CONVERTER).newInstance();
      }
      catch (ClassNotFoundException ex)
      {
         this.handleError(METHOD_NAME + SetupIF.CLASSNAME_CONVERTER
            + " Not Found: " + ex.getMessage());
      }
      catch (IllegalAccessException ex)
      {
         this.handleError(METHOD_NAME + SetupIF.CLASSNAME_CONVERTER
            + " Illegal Access: " + ex.getMessage());
      }
      catch (InstantiationException ex)
      {
         this.handleError(METHOD_NAME + SetupIF.CLASSNAME_CONVERTER
            + " Instantiation Problem: " + ex.getMessage());
      }

      /*
       * Initialize the crypto objects
       */

      secret = this.getProperty(SetupIF.PROP_CONNECTION_SHAREDSECRET);
      if (secret == null || secret.length() < 1)
      {
         this.handleError(METHOD_NAME + "Property '"
            + SetupIF.PROP_CONNECTION_SHAREDSECRET + "' is empty/null");
      }

      /*
       * NOTE: The Connection object (this object) will create a NETWORK
       * crypto since the passphrase is based on the secret identified in
       * the configuration. The other end of this connection (the Server)
       * will be handled by the Authenticator.
       */

      try
      {
         crypto = new DESCrypto(secret);
         Encryptor.setCrypto(Encryptor.NETWORK, crypto);
      }
      catch (CryptoException ex)
      {
         this.handleError(METHOD_NAME + "_crypto: " + ex.getMessage());
      }

      /*
       * remove the shared secret from the properties, no longer needed
       */

      this.removeProperty(SetupIF.PROP_CONNECTION_SHAREDSECRET);

      /*
       * Set global uri variables from the properties
       */

      _uriClients = this.getProperty(SetupIF.PROP_CONNECTION_URI)
         + "/" + SetupIF.URI_RESOURCES + "/" + StructureIF.NAME_CLIENTS;

      _uriContexts = this.getProperty(SetupIF.PROP_CONNECTION_URI)
         + "/" + SetupIF.URI_RESOURCES + "/" + StructureIF.NAME_CONTEXTS;

      _uriLogin = this.getProperty(SetupIF.PROP_CONNECTION_URI)
         + "/" + SetupIF.URI_LOGIN;

      _uriLogout = this.getProperty(SetupIF.PROP_CONNECTION_URI)
         + "/" + SetupIF.URI_LOGOUT;

      _uriSessionInfo = this.getProperty(SetupIF.PROP_CONNECTION_URI)
         + "/" + SetupIF.URI_RESOURCES + "/" + StructureIF.NAME_SESSIONINFO;

      /*
       * set debug
       */

      propValue = this.getProperty(SetupIF.PROP_DEBUG);
      if (propValue != null && propValue.length() > 0)
      {
         _debug = Boolean.parseBoolean(propValue);
      }

      /*
       * get the logIds from the properties
       */

      _logId = this.getProperty(SetupIF.PROP_CONNECTION_CLIENTID);

      if (_logId == null || _logId.length() < 1)
      {
         this.handleError(METHOD_NAME + "_logId is null/empty");
      }

      return;
   }

   //----------------------------------------------------------------
   private Object[] getValuesFromStructure(StructureIF parentStruct, String key, StructureType structType) throws ConnectionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      StructureIF struct = null;
      Object values[] = null;

      /*
       * Validate that the child structure with the key isn't null
       */

      struct = parentStruct.getChild(key);
      if (struct == null)
      {
         this.handleError(METHOD_NAME + "Structure '"
            + key + "' is null");
      }

      /*
       * Get the values from the child structure
       */

      values = struct.getValuesAsArray();
      if (values == null || values.length < 1)
      {
         this.handleError(METHOD_NAME + "Structure '"
            + key + "' has no value");
      }


      if (structType != null && struct.getValueType() != structType)
      {
         this.handleError(METHOD_NAME + "Structure '"
            + key + "' must be a " + structType.name() + ", type='"
            + struct.getValueType().toString() + "'");
      }

      return values;
   }

   //----------------------------------------------------------------
   private Object getValueFromStructure(StructureIF parentStruct, String key, StructureType structType) throws ConnectionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      Object values[] = null;
      Object value = null;

      /*
       * First, get the full list of Values from the Structure. Since we
       * are in the getValue.... method, there should be 1 and only 1.
       */
      values = this.getValuesFromStructure(parentStruct, key, structType);

      if (values == null)
      {
         this.handleError(METHOD_NAME + "Structure '"
            + key + "' has no value.");
      }

      if (values.length != 1)
      {
         this.handleError(METHOD_NAME + "Structure '"
            + key + "' has " + values.length + " value.  Should only have 1.");
      }

      value = values[0];

      return value;
   }
}
