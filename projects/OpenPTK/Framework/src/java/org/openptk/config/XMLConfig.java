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
 * addDatum the following below this CDDL HEADER, with the fields enclosed
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
package org.openptk.config;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.openptk.api.DataType;
import org.openptk.api.ElementIF;
import org.openptk.api.Query;
import org.openptk.api.State;
import org.openptk.authenticate.AuthenticatorIF;
import org.openptk.authorize.EnforcerIF;
import org.openptk.authorize.Environment;
import org.openptk.authorize.decider.DeciderIF;
import org.openptk.authorize.policy.PolicyIF;
import org.openptk.authorize.policy.PolicyMode;
import org.openptk.authorize.Effect;
import org.openptk.authorize.TargetIF;
import org.openptk.authorize.TargetType;
import org.openptk.client.BasicClient;
import org.openptk.client.ClientIF;
import org.openptk.common.AssignmentIF;
import org.openptk.common.AttrCategory;
import org.openptk.common.AttrIF;
import org.openptk.common.BasicAssignment;
import org.openptk.common.BasicAttr;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.config.mapper.AttrMapIF;
import org.openptk.config.mapper.Data;
import org.openptk.config.mapper.Datum;
import org.openptk.config.mapper.ExternalAttr;
import org.openptk.config.mapper.ExternalAttrIF;
import org.openptk.config.mapper.ExternalMode;
//import org.openptk.config.mapper.ExternalAttrIF.Mode;
import org.openptk.config.mapper.ExternalModeIF;
import org.openptk.config.mapper.ExternalSubAttr;
import org.openptk.config.mapper.ExternalSubAttrIF;
import org.openptk.config.mapper.Match;
import org.openptk.config.mapper.Processes;
import org.openptk.config.mapper.SubAttributes;
import org.openptk.context.ContextIF;
import org.openptk.context.actions.ActionIF;
import org.openptk.context.actions.ActionMode;
import org.openptk.crypto.CryptoIF;
import org.openptk.crypto.DESCrypto;
import org.openptk.crypto.Encryptor;
import org.openptk.debug.DebugLevel;
import org.openptk.definition.DefinitionAttr;
import org.openptk.definition.DefinitionIF;
import org.openptk.definition.SubjectDefinition;
import org.openptk.definition.functions.ArgumentIF;
import org.openptk.definition.functions.FunctionIF;
import org.openptk.definition.functions.Task;
import org.openptk.definition.functions.TaskArgument;
import org.openptk.definition.functions.TaskIF;
import org.openptk.definition.functions.TaskMode;
import org.openptk.exception.ActionException;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.CryptoException;
import org.openptk.exception.PluginException;
import org.openptk.logging.Logger;
import org.openptk.logging.LoggingIF;
import org.openptk.model.BasicView;
import org.openptk.model.ModelIF;
import org.openptk.model.RelationshipIF;
import org.openptk.model.ViewIF;
import org.openptk.plugin.PluginIF;
import org.openptk.session.SessionType;
import org.openptk.spi.ServiceIF;
import org.openptk.spi.operations.OperationsIF;
import org.openptk.structure.ConverterIF;
import org.openptk.structure.ConverterType;
import org.openptk.structure.StructureIF;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public class XMLConfig extends Config implements ConfigIF, XMLConfigIF
//===================================================================
{

   private static final int VALUE_OPERATION_TIMEOUT = 5000; // milliseconds
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private Document _xmlDocument = null;
   private Element _xmlRoot = null;

   /**
    * @param xmlConfigFile
    * @param configuration
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   public XMLConfig(final String xmlConfigFile, final Properties props, final Configuration configuration) throws ConfigurationException
   //----------------------------------------------------------------
   {
      super();

      LoggingIF log = null;

      if (props != null && !props.isEmpty())
      {
         this.setProperties(props);
      }

      _configuration = configuration;

      this.initParser(xmlConfigFile);
      this.loadDefaults();
      this.loadGlobalProperties();
      this.checkBuildTemp();

      /*
       * Initially get the Loggers and Encryptors definitions so a
       * preferred logger and encryptor can be
       * setup and used during rest of configuration
       */

      this.buildLoggers();
      this.buildCryptos();

      /*
       * Now build remaining OpenPTK components from XML Config file
       */

      this.buildAttrMaps();
      this.buildConverters();
      this.buildDefinitions();
      this.buildConnections();
      this.buildAssociations();
      this.buildAttrGroups();
      this.buildModels();
      this.buildActions();
      this.buildContexts();
      this.buildAuthenticators();
      this.buildClients();
      this.buildPlugins();
      this.buildPolicies();
      this.buildDeciders();
      this.buildEnforcers();

      return;
   }
   //
   //  ===========================
   //  ====  PRIVATE METHODS  ====
   //  ===========================
   //
   //----------------------------------------------------------------

   private void initParser(final String xmlFile) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":initParser(): ";
      String msg = null;
      String xsdFile = SCHEMA_NAME;
      URL xmlURL = null;
      URL xsdURL = null;
      SchemaFactory schemaFactory = null;
      Schema schema = null;
      Validator validator = null;
      DocumentBuilderFactory docBldrFactory = null;
      ClassLoader loader = null;
      DocumentBuilder builder = null;

      if (xmlFile == null || xmlFile.length() < 1)
      {
         throw new ConfigurationException(METHOD_NAME + "xmlFile is null/empty");
      }

      docBldrFactory = DocumentBuilderFactory.newInstance();
      if (docBldrFactory == null)
      {
         throw new ConfigurationException(METHOD_NAME + "DocumentBuilderFactory is null");
      }
      docBldrFactory.setNamespaceAware(true);

      loader = this.getClass().getClassLoader();
      xmlURL = loader.getResource(xmlFile);
      xsdURL = loader.getResource(xsdFile);

      /*
       * Get the configuration file (openptk.xml)
       * Try the classpath first, then try from the filesystem
       */

      if (xmlURL != null)
      {
         System.out.println("OpenPTK: INFO: "
            + "Using Configuration file '" + xmlURL.toExternalForm() + "' from the CLASSPATH");

         this.setProperty(PROP_OPENPTK_CONFIG_FILE, xmlURL.toExternalForm());
         this.setProperty(PROP_OPENPTK_CONFIG_SOURCE, "CLASSPATH");
      }
      else
      {
         try
         {
            if (xmlFile.contains(":/"))
            {
               xmlURL = new URL(xmlFile);
            }
            else
            {
               xmlURL = new URL("file:" + xmlFile);
            }
         }
         catch (MalformedURLException ex)
         {
            msg = "Could not create URL for Configuration file '" + xmlFile + "': " + ex.getMessage();
            throw new ConfigurationException(msg);
         }

         System.out.println("OpenPTK: INFO: "
            + "Using Configuration file '" + xmlURL.toExternalForm() + "' from the file system");

         this.setProperty(PROP_OPENPTK_CONFIG_FILE, xmlURL.toExternalForm());
         this.setProperty(PROP_OPENPTK_CONFIG_SOURCE, "FILESYSTEM");
      }

      /*
       * Get the validation file (openptk.xsd)
       * Try the classpath first, then try from the filesystem
       */

      if (xsdURL != null)
      {
         System.out.println("OpenPTK: INFO: "
            + "Using Validation file '" + xsdURL.toExternalForm() + "' from the CLASSPATH");

         this.setProperty(PROP_OPENPTK_VALIDATE_FILE, xsdURL.toExternalForm());
         this.setProperty(PROP_OPENPTK_VALIDATE_SOURCE, "CLASSPATH");
      }
      else
      {
         try
         {
            if (xsdFile.contains(":/"))
            {
               xsdURL = new URL(xsdFile);
            }
            else
            {
               xsdURL = new URL("file:" + xsdFile);
            }
         }
         catch (MalformedURLException ex)
         {
            msg = "Could not create URL for Validation file '" + xsdFile + "': " + ex.getMessage();
            throw new ConfigurationException(msg);
         }

         System.out.println("OpenPTK: INFO: "
            + "Using Validation file '" + xsdURL.toExternalForm() + "' from the file system");

         this.setProperty(PROP_OPENPTK_VALIDATE_FILE, xsdURL.toExternalForm());
         this.setProperty(PROP_OPENPTK_VALIDATE_SOURCE, "FILESYSTEM");
      }


      /*
       * Get Schema for validation
       * Set Schema on the DocumentBuilderFactory
       */

      schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      if (schemaFactory != null)
      {
         try
         {
            schema = schemaFactory.newSchema(xsdURL);
            docBldrFactory.setSchema(schema);
         }
         catch (SAXException ex)
         {
            msg = "Failed to create schema for Validation, xsdURL='" + xsdURL.toExternalForm() + "', " + ex.getMessage();
            throw new ConfigurationException(msg);
         }
      }
      else
      {
         msg = "SchemaFactory is null, the Configuration file can not be validated.";
         throw new ConfigurationException(msg);
      }

      /*
       * Create a Validator
       */

      validator = schema.newValidator();
      if (validator == null)
      {
         msg = "Schema Validator is null, the Configuration file can not be validated.";
         throw new ConfigurationException(msg);
      }

      /*
       * Create Document, parse it, validate it
       */

      try
      {
         builder = docBldrFactory.newDocumentBuilder();
         _xmlDocument = builder.parse(xmlURL.openStream());
         _xmlRoot = _xmlDocument.getDocumentElement();
         validator.validate(new DOMSource(_xmlDocument));
      }
      catch (ParserConfigurationException e)
      {
         msg = e.getMessage();
      }
      catch (SAXException e)
      {
         msg = e.getMessage();
      }
      catch (IOException e)
      {
         msg = e.getMessage();
      }

      if (msg != null)
      {
         throw new ConfigurationException(msg);
      }

      return;
   }

   //----------------------------------------------------------------
   private void loadDefaults() throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * Load the XML file's "Defaults" element.
       * If it exists, get the "Properties"
       * If there are no Properties, create a new (empty) Properties object
       */
//   <Defaults>
//      <Properties>
//         <Property name="spml1.url"                     value="http://localhost:8080/idm/servlet/rpcrouter2"/>
//         <Property name="spml1.user.name"               value="SPML-Proxy"/>
//         <Property name="spml1.user.password"           value="password"/>
//         <Property name="spml1.user.password.encrypted" value="sdgsdgsdgsdfgse45tseve5y"/>
//         <Property name="timeout.read"                 value="5000"/>
//         <Property name="timeout.write"                value="10000"/>
//         <Property name="basedn"                       value="dc=openptk,dc=org"/>
//         <Property name="debug.level"                  value="4"/>
//      </Properties>
//   </Defaults>

      boolean timestamp = false;
      int iDebug = 0;
      String debugStr = null;
      Element defaultsElement = null;
      DebugLevel[] debugArray = DebugLevel.values();

      defaultsElement = this.getElement(XMLConfig.ELEM_NAME_DEFAULTS);

      if (defaultsElement != null)
      {
         _defaultProps = this.getElementProperties(defaultsElement);
      }

      if (_defaultProps != null)
      {
         //
         // Get the Debug Level
         //

         debugStr = _defaultProps.getProperty(XMLConfig.PROP_DEBUG);
         if (debugStr != null)
         {
            try
            {
               iDebug = Integer.parseInt(debugStr);
            }
            catch (NumberFormatException ex)
            {
               iDebug = 0;
            }
            this.setDebugLevel(debugArray[iDebug]);
         }
         if (this.getDebugLevel() != DebugLevel.NONE)
         {
            this.setDebug(true);
         }

         /*
          * Get the timestamp flag
          */

         timestamp = Boolean.parseBoolean(_defaultProps.getProperty(XMLConfig.PROP_TIMESTAMP));
         this.useTimeStamp(timestamp);

      }

      return;
   }

   //----------------------------------------------------------------
   private void checkBuildTemp() throws ConfigurationException
   //----------------------------------------------------------------
   {
      boolean exist = false;
      boolean created = false;
      String METHOD_NAME = CLASS_NAME + ":checkBuildTemp(): ";
      String temp = null;
      String test = null;
      String file = "_openptk_test_write_";

      temp = this.getProperty(PROP_OPENPTK_TEMP);
      if (temp == null || temp.length() < 1)
      {
         this.handleError(METHOD_NAME
            + "Property '" + PROP_OPENPTK_TEMP + "' is null/empty");
      }

      /*
       * If the directory does not exist ... create it
       * Then test putting a file in it
       */

      exist = (new File(temp)).exists();
      if (!exist)
      {
         created = (new File(temp)).mkdirs();
         if (!created)
         {
            this.handleError(METHOD_NAME
               + "Failed to create directory '" + temp + "'");
         }
      }

      if (temp.endsWith(System.getProperty("file.separator")))
      {
         test = temp + file;
      }
      else
      {
         test = temp + System.getProperty("file.separator") + file;
      }

      try
      {
         created = (new File(test)).createNewFile();
      }
      catch (IOException ex)
      {
         this.handleError(METHOD_NAME
            + "Could not write to '" + PROP_OPENPTK_TEMP + "' directory '"
            + temp + "'");
      }

      if (created)
      {
         (new File(test)).delete();
      }

      return;
   }

   //----------------------------------------------------------------
   private void buildCryptos() throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * The Encryptors Element contains settings to support various aspects
       * of security:
       *
       * - Encryptor define the various way of encrypting Properties and
       * values within the Framework.
       */
//   <Encryptors>
//      <Encryptor>
//         ...
//      </Encryptor>
//   </Encryptors>

      String METHOD_NAME = CLASS_NAME + ":buildCryptos(): ";
      String id = null;
      String classname = null;
      Properties encryptionProps = null;
      NodeList encryptionNL = null;
      CryptoIF crypto = null;
      Element encryptorsElement = null;
      Element encryptorElement = null;

      encryptorsElement = this.getElement(XMLConfig.ELEM_NAME_ENCRYPTORS);
      if (encryptorsElement == null)
      {
         this.handleError(METHOD_NAME + "Element '"
            + XMLConfig.ELEM_NAME_SECURITY + "' is null");
      }

      if (encryptorsElement != null)
      {
         encryptionNL = encryptorsElement.getElementsByTagName(XMLConfig.ELEM_NAME_ENCRYPTOR);

         for (int i = 0; i < encryptionNL.getLength(); i++)
         {
            encryptorElement = (Element) encryptionNL.item(i);
            id = encryptorElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
            encryptionProps = this.getElementProperties(encryptorElement);

            if (id != null && encryptionProps != null)
            {
               classname = encryptionProps.getProperty(XMLConfig.PROP_CRYPTO_CLASSNAME);
               if (classname != null && classname.length() > 0)
               {
                  crypto = this.createCrypto(id, classname);
                  if (crypto != null)
                  {
                     if (_encryptors.containsKey(id))
                     {
                        this.handleError(METHOD_NAME + "Encryptor '" + id
                           + "' already exists, id must be unique");
                     }
                     _encryptors.put(id, crypto);
                  }
               }
            }
         }
      }

      _encryptorName = encryptorsElement.getAttribute(XMLConfig.ELEM_ATTR_DEFAULT);
      _encryptorName = this.checkSubstitution(_encryptorName);

      if (_encryptorName == null || !(_encryptors.containsKey(_encryptorName)))
      {
         Logger.logError("Unable to initialize encryptor '" + _encryptorName + "'. Using default");
      }
      else
      {
         /*
          * Setup/Override the main global encryptor that will be used.
          *
          */

         crypto = _encryptors.get(_encryptorName);

         /*
          * Next, do the switch and emit a message to the new logger that it
          * is now initizlized.
          */

         try
         {
            Encryptor.setCrypto(Encryptor.CONFIG, crypto);
         }
         catch (CryptoException ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }

         Logger.logInfo("Encryptor Initialized ('" + _encryptorName + "')");
      }

      return;
   }

   //----------------------------------------------------------------
   private void buildDeciders() throws ConfigurationException
   //----------------------------------------------------------------
   {
//   <Security>
//      <Deciders>
//         <Decider id="internal">
//            <Properties>
//               <Property name="decider.classname" value="org.openptk.authorize.decider.BasicDecider"/>
//            </Properties>
//         </Decider>
//      </Deciders>
//   </Security>

      String METHOD_NAME = CLASS_NAME + ":buildDeciders(): ";
      String id = null;
      String classname = null;
      Properties decidersProps = null;
      Properties deciderProps = null;
      Properties mergeProps = null;
      NodeList deciderNL = null;
      Element securityElement = null;
      Element decidersElement = null;
      Element deciderElement = null;
      DeciderIF decider = null;

      securityElement = this.getElement(XMLConfig.ELEM_NAME_SECURITY);
      if (securityElement == null)
      {
         this.handleError(METHOD_NAME + "Element '"
            + XMLConfig.ELEM_NAME_SECURITY + "' is null");
      }

      decidersElement = this.getElement(securityElement, XMLConfig.ELEM_NAME_DECIDERS);

      if (decidersElement != null)
      {
         decidersProps = this.getElementProperties(decidersElement);

         deciderNL = decidersElement.getElementsByTagName(XMLConfig.ELEM_NAME_DECIDER);
         if (deciderNL != null)
         {
            for (int i = 0; i < deciderNL.getLength(); i++)
            {
               deciderElement = (Element) deciderNL.item(i);
               if (deciderElement != null)
               {
                  id = deciderElement.getAttribute(XMLConfig.ELEM_ATTR_ID);

                  if (id == null || id.length() < 1)
                  {
                     this.handleError(METHOD_NAME + "Decider number '"
                        + i + "' has an empty id.");
                  }

                  deciderProps = this.getElementProperties(deciderElement);
                  mergeProps = this.combineProperties(decidersProps, deciderProps);
                  classname = mergeProps.getProperty(XMLConfig.PROP_DECIDER_CLASSNAME);

                  if (classname != null && classname.length() > 0)
                  {
                     decider = this.createDecider(id, classname);
                  }
                  else
                  {
                     this.handleError(METHOD_NAME + "Policy '"
                        + id + "' has an empty classname.");
                  }

                  decider.setProperties(mergeProps);

                  if (_deciders.containsKey(id))
                  {
                     this.handleError(METHOD_NAME + "Decider '" + id
                        + "' already exists, id must be unique");
                  }
                  _deciders.put(id, decider);
               }
            }
         }
      }


      return;
   }

   //----------------------------------------------------------------
   private void buildEnforcers() throws ConfigurationException
   //----------------------------------------------------------------
   {
//       <Enforcers>
//         <Properties>
//            <Property name="enforcer.classname" value="org.openptk.authorize.BasicEnforcer"/>
//         </Properties>
//         <Enforcer id="webfilter" environment="SERVLET" decider="internal" />
//         <Enforcer id="operation" environment="ENGINE"  decider="internal" />
//      </Enforcers>

      String METHOD_NAME = CLASS_NAME + ":buildEnforcers(): ";
      String id = null;
      String classname = null;
      String envName = null;
      String deciderId = null;
      Properties enforcersProps = null;
      Properties enforcerProps = null;
      Properties mergeProps = null;
      NodeList enforcerNL = null;
      Element securityElement = null;
      Element enforcersElement = null;
      Element enforcerElement = null;
      Environment env = null;
      Environment[] envArray = null;
      EnforcerIF enforcer = null;

      securityElement = this.getElement(XMLConfig.ELEM_NAME_SECURITY);
      if (securityElement == null)
      {
         this.handleError(METHOD_NAME + "Element '"
            + XMLConfig.ELEM_NAME_SECURITY + "' is null");
      }

      envArray = Environment.values();

      enforcersElement = this.getElement(securityElement, XMLConfig.ELEM_NAME_ENFORCERS);

      if (enforcersElement != null)
      {
         enforcersProps = this.getElementProperties(enforcersElement);

         enforcerNL = enforcersElement.getElementsByTagName(XMLConfig.ELEM_NAME_ENFORCER);
         if (enforcerNL != null)
         {
            for (int i = 0; i < enforcerNL.getLength(); i++)
            {
               enforcerElement = (Element) enforcerNL.item(i);
               if (enforcerElement != null)
               {
                  id = enforcerElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
                  if (id == null || id.length() < 1)
                  {
                     this.handleError(METHOD_NAME + "Enforcer number '"
                        + i + "' has an empty id.");
                  }

                  envName = enforcerElement.getAttribute(XMLConfig.ELEM_ATTR_ENVIRONMENT);
                  if (envName == null || envName.length() < 1)
                  {
                     this.handleError(METHOD_NAME + "Enforcer '"
                        + id + "' has an empty environment.");
                  }

                  env = null;
                  for (int j = 0; j < envArray.length; j++)
                  {
                     if (envName.equalsIgnoreCase(envArray[j].toString()))
                     {
                        env = envArray[j];
                        break;
                     }
                  }
                  if (env == null)
                  {
                     this.handleError(METHOD_NAME + "Enforcer '"
                        + id + "' has an invalid environment: '" + envName + "'.");
                  }

                  deciderId = enforcerElement.getAttribute(XMLConfig.ELEM_ATTR_DECIDER);
                  if (deciderId == null || deciderId.length() < 1)
                  {
                     this.handleError(METHOD_NAME + "Enforcer '"
                        + id + "' has an empty decider.");
                  }
                  if (!_deciders.containsKey(deciderId))
                  {
                     this.handleError(METHOD_NAME + "Enforcer '"
                        + id + "' has an invalid decider: '" + deciderId + "'.");
                  }

                  enforcerProps = this.getElementProperties(enforcerElement);
                  mergeProps = this.combineProperties(enforcersProps, enforcerProps);
                  classname = mergeProps.getProperty(XMLConfig.PROP_ENFORCER_CLASSNAME);

                  if (classname != null && classname.length() > 0)
                  {
                     enforcer = this.createEnforcer(id, classname, deciderId, env);
                  }
                  else
                  {
                     this.handleError(METHOD_NAME + "Enforcer '"
                        + id + "' has an empty classname.");
                  }

                  enforcer.setProperties(mergeProps);

                  if (_enforcers.containsKey(id))
                  {
                     this.handleError(METHOD_NAME + "Enforcer '" + id
                        + "' already exists, id must be unique");
                  }
                  _enforcers.put(id, enforcer);
               }
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void buildPolicies() throws ConfigurationException
   //----------------------------------------------------------------
   {
//       <Policies>
//          <Properties>
//            <Property name="policy.classname" value="org.openptk.authorize.policy.BasicPolicy"/>
//          </Properties>
//          <Policy id="UserOps" environment="ENGINE" mode="inbound" type="allow">
//            <Properties>
//               <Property name="policy.description" value="End-User Self Service"/>
//            </Properties>
//            <Session>
//               <Types>
//                  <Type id="USER"/>
//               </Types>
//            </Session>
//            <Resources>
//               <Resource id="update" uri="/resources/contexts/${session.principal.contextid}/subjects/${session.principal.uniqueid}">
//                  <Operations>
//                     <Operation id="UPDATE"/>
//                  </Operations>
//               </Resource>
//               <Resource id="pwdchange" uri="/resources/contexts/${session.principal.contextid}/subjects/${session.principal.uniqueid}/password/change">
//                  <Operations>
//                     <Operation id="UPDATE"/>
//                  </Operations>
//               </Resource>
//            </Resources>
//         </Policy>
//         <Policy id="DenyPortletClient" environment="ENGINE" mode="inbound" type="deny">
//            <Properties>
//               <Property name="policy.description" value="Block all request from the client"/>
//            </Properties>
//            <Session>
//               <Clients>
//                  <Client id="portlet"/>
//               </Clients>
//            </Session>
//            <Resources>
//               <Resource id="all" uri="*">
//                  <Operations>
//                     <Operation id="CREATE"/>
//                     <Operation id="READ"/>
//                     <Operation id="UPDATE"/>
//                     <Operation id="DELETE"/>
//                     <Operation id="SEARCH"/>
//                  </Operations>
//               </Resource>
//            </Resources>
//         </Policy>
//      </Policies>

      String METHOD_NAME = CLASS_NAME + ":buildPolicies(): ";
      String policyId = null;
      String sessionTypeName = null;
      String clientId = null;
      String targetId = null;
      String targetTypeName = null;
      String targetValue = null;
      String operationId = null;
      String classname = null;
      String envName = null;
      String modeName = null;
      String effectName = null;
      Properties policiesProps = null;
      Properties policyProps = null;
      Properties mergeProps = null;
      NodeList policyNL = null;
      NodeList typeNL = null;
      NodeList clientNL = null;
      NodeList targetNL = null;
      NodeList operationNL = null;
      Element securityElement = null;
      Element policiesElement = null;
      Element policyElement = null;
      Element sessionElement = null;
      Element typesElement = null;
      Element typeElement = null;
      Element clientsElement = null;
      Element clientElement = null;
      Element targetsElement = null;
      Element targetElement = null;
      Element operationsElement = null;
      Element operationElement = null;
      Environment env = null;
      Environment[] envArray = null;
      PolicyMode mode = null;
      PolicyMode[] modeArray = null;
      Effect effect = null;
      Effect[] effectArray = null;
      PolicyIF policy = null;
      SessionType[] sessionArray = null;
      SessionType sessionType = null;
      TargetIF target = null;
      TargetType[] targetArray = null;
      TargetType targetType = null;
      Operation[] operationArray = null;
      Operation operation = null;

      securityElement = this.getElement(XMLConfig.ELEM_NAME_SECURITY);
      if (securityElement == null)
      {
         this.handleError(METHOD_NAME + "Element '"
            + XMLConfig.ELEM_NAME_SECURITY + "' is null");
      }

      envArray = Environment.values();
      modeArray = PolicyMode.values();
      effectArray = Effect.values();
      sessionArray = SessionType.values();
      targetArray = TargetType.values();
      operationArray = Operation.values();

      policiesElement = this.getElement(securityElement, XMLConfig.ELEM_NAME_POLICIES);

      if (policiesElement != null)
      {
         policiesProps = this.getElementProperties(policiesElement);
         policyNL = policiesElement.getElementsByTagName(XMLConfig.ELEM_NAME_POLICY);
         if (policyNL != null)
         {
            for (int i = 0; i < policyNL.getLength(); i++)
            {
               policyElement = (Element) policyNL.item(i);
               if (policyElement != null)
               {
                  // attribute: id

                  policyId = policyElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
                  if (policyId == null || policyId.length() < 1)
                  {
                     this.handleError(METHOD_NAME + "Policy number '"
                        + i + "' has an empty id.");
                  }

                  // attribute: environment

                  envName = policyElement.getAttribute(XMLConfig.ELEM_ATTR_ENVIRONMENT);
                  if (envName == null || envName.length() < 1)
                  {
                     this.handleError(METHOD_NAME + "Policy '"
                        + policyId + "' has an empty environment.");
                  }

                  env = null;
                  for (int j = 0; j < envArray.length; j++)
                  {
                     if (envName.equalsIgnoreCase(envArray[j].toString()))
                     {
                        env = envArray[j];
                        break;
                     }
                  }
                  if (env == null)
                  {
                     this.handleError(METHOD_NAME + "Policy '"
                        + policyId + "' has an invalid environment: '" + envName + "'.");
                  }

                  // attribute: mode

                  modeName = policyElement.getAttribute(XMLConfig.ELEM_ATTR_MODE);
                  if (modeName == null || modeName.length() < 1)
                  {
                     this.handleError(METHOD_NAME + "Policy '"
                        + policyId + "' has an empty mode.");
                  }

                  mode = null;
                  for (int j = 0; j < modeArray.length; j++)
                  {
                     if (modeName.equalsIgnoreCase(modeArray[j].toString()))
                     {
                        mode = modeArray[j];
                        break;
                     }
                  }
                  if (mode == null)
                  {
                     this.handleError(METHOD_NAME + "Policy '"
                        + policyId + "' has an invalid mode: '" + modeName + "'.");
                  }

                  // attribute: type

                  effectName = policyElement.getAttribute(XMLConfig.ELEM_ATTR_EFFECT);
                  if (effectName == null || effectName.length() < 1)
                  {
                     this.handleError(METHOD_NAME + "Policy '"
                        + policyId + "' has an empty effect.");
                  }

                  effect = null;
                  for (int j = 0; j < effectArray.length; j++)
                  {
                     if (effectName.equalsIgnoreCase(effectArray[j].toString()))
                     {
                        effect = effectArray[j];
                        break;
                     }
                  }
                  if (effect == null)
                  {
                     this.handleError(METHOD_NAME + "Policy '"
                        + policyId + "' has an invalid effect: '" + effectName + "'.");
                  }

                  // properties

                  policyProps = this.getElementProperties(policyElement);
                  mergeProps = this.combineProperties(policiesProps, policyProps);
                  classname = mergeProps.getProperty(XMLConfig.PROP_POLICY_CLASSNAME);

                  if (classname != null && classname.length() > 0)
                  {
                     policy = this.createPolicy(policyId, classname, env, mode, effect);
                  }
                  else
                  {
                     this.handleError(METHOD_NAME + "Policy '"
                        + policyId + "' has an empty classname.");
                  }

                  policy.setProperties(mergeProps);

                  // sub-element: Session

                  sessionElement = this.getElement(policyElement, XMLConfig.ELEM_NAME_SESSION);
                  if (sessionElement != null)
                  {
                     // sub-element: Types

                     typesElement = this.getElement(sessionElement, XMLConfig.ELEM_NAME_TYPES);
                     if (typesElement != null)
                     {
                        typeNL = typesElement.getElementsByTagName(XMLConfig.ELEM_NAME_TYPE);
                        if (typeNL != null)
                        {
                           for (int j = 0; j < typeNL.getLength(); j++)
                           {
                              typeElement = (Element) typeNL.item(j);
                              if (typeElement != null)
                              {
                                 sessionTypeName = typeElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
                                 if (sessionTypeName == null || sessionTypeName.length() < 1)
                                 {
                                    this.handleError(METHOD_NAME + "Policy '"
                                       + policyId + "' has an empty Session Type.");
                                 }

                                 sessionType = null;
                                 for (int k = 0; k < sessionArray.length; k++)
                                 {
                                    if (sessionTypeName.equalsIgnoreCase(sessionArray[k].toString()))
                                    {
                                       sessionType = sessionArray[k];
                                       break;
                                    }
                                 }
                                 if (sessionType == null)
                                 {
                                    this.handleError(METHOD_NAME + "Policy '"
                                       + policyId + "' has an invalid Session Type '" + sessionTypeName + "'.");
                                 }

                                 policy.addSessionType(sessionType);
                              }
                           }
                        }
                     }

                     // sub-element: Clients

                     clientsElement = this.getElement(sessionElement, XMLConfig.ELEM_NAME_CLIENTS);
                     if (clientsElement != null)
                     {
                        clientNL = clientsElement.getElementsByTagName(XMLConfig.ELEM_NAME_CLIENT);
                        if (clientNL != null)
                        {
                           for (int j = 0; j < clientNL.getLength(); j++)
                           {
                              clientElement = (Element) clientNL.item(j);
                              if (clientElement != null)
                              {
                                 clientId = clientElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
                                 if (clientId == null || clientId.length() < 1)
                                 {
                                    this.handleError(METHOD_NAME + "Policy '"
                                       + policyId + "' has an empty Session Client Id.");
                                 }

                                 if (!_clients.containsKey(clientId))
                                 {
                                    this.handleError(METHOD_NAME + "Policy '"
                                       + policyId + "' has an invalid Session Client: '" + clientId + "'.");
                                 }

                                 policy.addClientId(clientId);
                              }
                           }
                        }
                     }
                  }

                  // sub-element: Targets

                  classname = _defaultProps.getProperty(XMLConfig.PROP_TARGET_CLASSNAME);
                  if (classname == null || classname.length() < 1)
                  {
                     this.handleError(METHOD_NAME + "Defaults Property '"
                        + XMLConfig.PROP_TARGET_CLASSNAME + "' is null.");
                  }

                  targetsElement = this.getElement(policyElement, XMLConfig.ELEM_NAME_TARGETS);
                  if (targetsElement != null)
                  {
                     targetNL = targetsElement.getElementsByTagName(XMLConfig.ELEM_NAME_TARGET);
                     if (targetNL != null)
                     {
                        for (int j = 0; j < targetNL.getLength(); j++)
                        {
                           targetElement = (Element) targetNL.item(j);
                           if (targetElement != null)
                           {
                              targetId = targetElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
                              if (targetId == null || targetId.length() < 1)
                              {
                                 this.handleError(METHOD_NAME + "Policy '"
                                    + policyId + "' has an empty Target Id.");
                              }

                              targetTypeName = targetElement.getAttribute(XMLConfig.ELEM_ATTR_TYPE);
                              if (targetTypeName == null || targetTypeName.length() < 1)
                              {
                                 this.handleError(METHOD_NAME + "Policy '"
                                    + policyId + "' has an empty Target Type.");
                              }

                              targetType = null;
                              for (int k = 0; k < targetArray.length; k++)
                              {
                                 if (targetTypeName.equalsIgnoreCase(targetArray[k].toString()))
                                 {
                                    targetType = targetArray[k];
                                    break;
                                 }
                              }
                              if (targetType == null)
                              {
                                 this.handleError(METHOD_NAME + "Policy '"
                                    + policyId + "' has an invalid Target Type '" + targetTypeName + "'.");
                              }

                              targetValue = targetElement.getAttribute(XMLConfig.ELEM_ATTR_VALUE);
                              if (targetValue == null || targetValue.length() < 1)
                              {
                                 this.handleError(METHOD_NAME + "Policy '"
                                    + policyId + "' has an empty Target Value.");
                              }

                              target = this.createTarget(targetId, classname, targetType, targetValue);

                              // addDatum the Operations for the target

                              operationsElement = this.getElement(targetElement, XMLConfig.ELEM_NAME_OPERATIONS);
                              if (operationsElement != null)
                              {
                                 operationNL = operationsElement.getElementsByTagName(XMLConfig.ELEM_NAME_OPERATION);
                                 if (operationNL != null)
                                 {
                                    for (int k = 0; k < operationNL.getLength(); k++)
                                    {
                                       operationElement = (Element) operationNL.item(k);
                                       if (operationElement != null)
                                       {
                                          operationId = operationElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
                                          if (operationId == null || operationId.length() < 1)
                                          {
                                             this.handleError(METHOD_NAME + "Policy '"
                                                + policyId + "', Target '" + targetId
                                                + ", has an empty Operation Id (" + k + ").");
                                          }
                                          operation = null;
                                          for (int l = 0; l < operationArray.length; l++)
                                          {
                                             if (operationId.equalsIgnoreCase(operationArray[l].toString()))
                                             {
                                                operation = operationArray[l];
                                                break;
                                             }
                                          }
                                          if (operation == null)
                                          {
                                             this.handleError(METHOD_NAME + "Policy '"
                                                + policyId + "', Target '" + targetId
                                                + "', has an invalid Operation '" + operationId + "'.");
                                          }

                                          target.addOperation(operation);
                                       }
                                    }
                                 }
                              }

                              if (policy.getTargets().containsKey(targetId))
                              {
                                 this.handleError(METHOD_NAME + "Policy '"
                                    + policyId + "', Target '" + targetId
                                    + "', target already exists, id must be unique.");
                              }

                              policy.addTarget(targetId, target);
                           }
                        }
                     }
                  }

                  if (_policies.containsKey(policyId))
                  {
                     this.handleError(METHOD_NAME + "Policy '" + policyId
                        + "', policy already exists, id must be unique.");
                  }

                  policy.setState(State.READY);
                  _policies.put(policyId, policy);
               }
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void buildAuthenticators() throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * The Security Element contains settings to support various aspects
       * of security:
       *
       * - Authenticators define the various means of authenticating
       * clients
       */
//   <Security>
//      <Authenticators>
//         <Authentcator id="OpenDS" level="USER">
//            ...
//         </Authenticator>
//         ...
//      </Authenticators>
//   </Security>

      boolean isValidAuthLevel = false;
      String METHOD_NAME = CLASS_NAME + ":buildAuthenticators(): ";
      String id = null;
      String contextName = null;
      String sAuthLevel = null;
      SessionType level = null;
      String classname = null;
      Properties authenticatorsProps = null;
      Properties authenticatorProps = null;
      Properties mergeProps = null;
      Element authenticatorElement = null;
      NodeList authNL = null;
      AuthenticatorIF authenticator = null;
      Element securityElement = null;
      Element authenticatorsElement = null;

      securityElement = this.getElement(XMLConfig.ELEM_NAME_SECURITY);

      /*
       * Create Map of Authenticators
       */

      _authenticators = new HashMap<String, AuthenticatorIF>();

      authenticatorsElement = this.getElement(securityElement, XMLConfig.ELEM_NAME_AUTHENTICATORS);

      if (authenticatorsElement != null)
      {
         authenticatorsProps = this.getElementProperties(authenticatorsElement);

         authNL = authenticatorsElement.getElementsByTagName(XMLConfig.ELEM_NAME_AUTHENTICATOR);

         for (int i = 0; i < authNL.getLength(); i++)
         {
            isValidAuthLevel = false;
            authenticatorElement = (Element) authNL.item(i);
            id = authenticatorElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
            authenticatorProps = this.getElementProperties(authenticatorElement);

            sAuthLevel = authenticatorElement.getAttribute(XMLConfig.ELEM_ATTR_AUTHENTICATOR_LEVEL);
            //An auth Level is required and must be valid based on SessionType Enum
            if (sAuthLevel != null && sAuthLevel.length() > 0)
            {

               for (SessionType st : SessionType.values())
               {

                  if (sAuthLevel.equalsIgnoreCase(st.toString()))
                  {
                     level = st;
                     isValidAuthLevel = true;
                  }
               }

               if (!isValidAuthLevel)
               {
                  this.handleError(METHOD_NAME + "Authenticator '"
                     + id + "' has an invalid level '"
                     + sAuthLevel + "'");
               }
            }
            else
            {
               this.handleError(METHOD_NAME + "Authenticator '"
                  + id + "' is missing a required authenticator.level '");
            }

            mergeProps = this.combineProperties(authenticatorsProps, authenticatorProps);

            if (id != null && mergeProps != null)
            {
               classname = mergeProps.getProperty(XMLConfig.PROP_AUTHENTICATOR_CLASSNAME);

               contextName = mergeProps.getProperty(XMLConfig.ELEM_ATTR_AUTHENTICATOR_LEVEL);

               if (contextName != null && contextName.length() > 0)
               {
                  if (!_contexts.containsKey(contextName))
                  {
                     this.handleError(METHOD_NAME + "Authenticator '"
                        + id + "' has an invalid context '"
                        + contextName + "'");
                  }
               }

               if (classname != null && classname.length() > 0)
               {
                  authenticator = this.createAuthenticator(id, classname, level, mergeProps);

                  if (authenticator != null)
                  {
                     if (_authenticators.containsKey(id))
                     {
                        this.handleError(METHOD_NAME + "Authenticator '" + id
                           + "' already exists, id must be unique");
                     }

                     _authenticators.put(id, authenticator);
                  }
               }
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void buildClients() throws ConfigurationException
   //----------------------------------------------------------------
   {
//   <Clients>
//      <Properties>
//         <Property name="cookie.name" value="${cookie.name}"/>
//      </Properties>
//      <Client id="apitest" secret="McP7NoBoPTPHrJZLfXsnDEod" authenticator="Employees-IdPass-JDBC">
//         <Authenticators>
//            <Authenticator id="Employees-IdPass-JDBC"/>
//            <Authenticator id="OpenPTK-config"/>
//         </Authenticators>
//         <Contexts default="Employees-MySQL-JDBC" >
//            <Context id="Employees-MySQL-JDBC" />
//            <Context id="Employees-OpenDS-JNDI" />
//         </Contexts>
//      </Client>
//   </Clients>

      boolean isDefaultDefined = false;
      int clientsLen = 0;
      int contextsLen = 0;
      int authensLen = 0;
      String METHOD_NAME = CLASS_NAME + ":buildClients(): ";
      String clientId = null;
      String authenId = null;
      String contextId = null;
      String defCtxId = null;
      String clientSecret = null;
      Properties clientsProps = null;
      Properties clientProps = null;
      Properties converterProps = null;
      Properties multivalueProps = null;
      Element clientsElement = null;     // DOM
      Element clientElement = null;      // DOM
      Element contextsElement = null;    // DOM
      Element contextElement = null;     // DOM
      Element authenElement = null;      // DOM
      Element authensElement = null;     // DOM
      NodeList clientsNL = null;         // DOM
      NodeList contextsNL = null;        // DOM
      NodeList authensNL = null;         // DOM
      ClientIF client = null;            // OpenPTK
      CryptoIF crypto = null;            // OpenPTK
      ConverterIF converter = null;      // OpenPTK
      ElementIF converterElement = null; // OpenPTK

      clientsElement = this.getElement(XMLConfig.ELEM_NAME_CLIENTS);

      clientsProps = this.getElementProperties(clientsElement);

      clientsNL = clientsElement.getElementsByTagName(XMLConfig.ELEM_NAME_CLIENT);
      clientsLen = clientsNL.getLength();

      /*
       * get the converter/multivalue properties
       * addDatum a new property to the multivalue Properties
       * <converterType>.<StructureIF.NAME_MULTIVALUE>.<structInfoId> = <StructureIF.NAME_VALUE>
       */

      multivalueProps = new Properties();

      for (ConverterType convType : _converters.keySet())
      {
         converter = _converters.get(convType);
         if (converter != null)
         {
            for (String structInfoId : converter.getStructInfoIds())
            {
               converterElement = converter.getStructInfo(structInfoId);
               if (converterElement != null)
               {
                  converterProps = converterElement.getProperties();
                  if (converterProps != null)
                  {
                     if (Boolean.parseBoolean(converterProps.getProperty(StructureIF.NAME_MULTIVALUE)))
                     {
                        multivalueProps.setProperty(
                           convType.toString().toLowerCase() + "." + StructureIF.NAME_MULTIVALUE + "." + structInfoId.toLowerCase(),
                           StructureIF.NAME_VALUE);
                     }
                  }
               }
            }
         }
      }

      /*
       * process each client
       */

      for (int i = 0; i < clientsLen; i++)
      {
         isDefaultDefined = false;
         clientElement = (Element) clientsNL.item(i);

         clientProps = this.getElementProperties(clientElement);

         clientId = clientElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
         clientSecret = clientElement.getAttribute(XMLConfig.ELEM_ATTR_SECRET);

         if (clientId != null && clientId.length() > 0)
         {
            /*
             * does it already exist ... there can be only one (for a given name)
             */

            if (_clients.containsKey(clientId))
            {
               this.handleError(METHOD_NAME + "Client '" + clientId
                  + "' already exists, id must be unique");
            }

            if (clientSecret != null && clientSecret.length() > 0)
            {
               client = new BasicClient(clientId, clientSecret);

               try
               {
                  crypto = new DESCrypto(clientSecret);
                  Encryptor.setCrypto(clientId, crypto);
               }
               catch (CryptoException ex)
               {
                  this.handleError(METHOD_NAME + "Problem creating crypto for clientId = " + clientId);
               }
            }
            else
            {
               client = new BasicClient(clientId);

               /*
                * We won't set a crypto for no secret value. It will
                * come across as clear text
                */
            }
         }
         else
         {
            this.handleError(METHOD_NAME + "A Client has a null id");
         }

         client.setProperties(this.combineProperties(this.combineProperties(clientsProps, clientProps), multivalueProps));

         /*
          * Get the "Authenticator" information
          */

         authensElement = this.getElement(clientElement, XMLConfig.ELEM_NAME_AUTHENTICATORS);
         authensNL = authensElement.getElementsByTagName(XMLConfig.ELEM_NAME_AUTHENTICATOR);
         authensLen = authensNL.getLength();

         for (int j = 0; j < authensLen; j++)
         {
            authenElement = (Element) authensNL.item(j);
            authenId = authenElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
            if (authenId != null && authenId.length() > 0)
            {
               if (_authenticators.containsKey(authenId))
               {
                  client.addAuthenticatorId(authenId);
               }
               else
               {
                  this.handleError(METHOD_NAME + "Client '" + clientId
                     + "' has an invalid Authenticator '" + authenId + "'");
               }
            }
            else
            {
               this.handleError(METHOD_NAME + "Client '" + clientId
                  + "' has an Authenticator with a null id");
            }
         }

         /*
          * Get the "Context" information
          */

         contextsElement = this.getElement(clientElement, XMLConfig.ELEM_NAME_CONTEXTS);
         contextsNL = contextsElement.getElementsByTagName(XMLConfig.ELEM_NAME_CONTEXT);
         contextsLen = contextsNL.getLength();

         defCtxId = contextsElement.getAttribute(XMLConfig.ELEM_ATTR_DEFAULT);
         if (defCtxId != null && defCtxId.length() > 0 && _contexts.containsKey(defCtxId))
         {
            client.setDefaultContextId(defCtxId);
         }
         else
         {
            this.handleError(METHOD_NAME + "Client '" + clientId
               + "' has a empty or invalid default context");
         }

         for (int j = 0; j < contextsLen; j++)
         {
            contextElement = (Element) contextsNL.item(j);

            contextId = contextElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
            if (contextId != null && contextId.length() > 0)
            {
               if (_contexts.containsKey(contextId))
               {
                  client.addContextId(contextId);
               }
               else
               {
                  this.handleError(METHOD_NAME + "Client '" + clientId
                     + "' has an invalid Context '" + contextId + "'");
               }
            }
            else
            {
               this.handleError(METHOD_NAME + "Client '" + clientId
                  + "' has a Context with an null id");
            }

            if (contextId.equals(defCtxId))
            {
               isDefaultDefined = true;
            }
         }

         if (!isDefaultDefined)
         {
            this.handleError(METHOD_NAME + "Client '" + clientId
               + "', has a default Context '" + defCtxId
               + "' that is not one of the included Contexts");
         }

         /*
          * include converter/multivalue properties
          */







         _clients.put(clientId, client);

      }

      return;
   }

   //----------------------------------------------------------------
   private void buildLoggers() throws ConfigurationException
   //----------------------------------------------------------------
   {
//   <Loggers default="UnixLogFile">
//      <Logger id="UnixLogFile">
//         <Properties>
//            <Property name="classname" value="org.openptk.provision.logging.AtomicLogger"/>
//            <Property name="file"      value="/var/tmp/openptk.log"/>
//         </Properties>
//      </Logger>
//   </Loggers>

      String METHOD_NAME = CLASS_NAME + ":buildLoggers(): ";

      String loggerId = null;
      LoggingIF logger = null;
      Element loggersElement = null;
      Element loggerElement = null;
      NodeList loggersNL = null;

      /*
       * The "loggerName" is set in the buildContexts() method
       * It is a Property of the "Contexts" Element
       */

      loggersElement = this.getElement(XMLConfig.ELEM_NAME_LOGGERS);

      loggersNL = loggersElement.getElementsByTagName(XMLConfig.ELEM_NAME_LOGGER);

      for (int j = 0; j < loggersNL.getLength(); j++)
      {
         loggerElement = (Element) loggersNL.item(j);

         loggerId = loggerElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
         logger = this.getLogger(loggerId);

         if (logger != null)
         {
            _loggers.put(loggerId, logger);
         }
      }

      _loggerName = loggersElement.getAttribute(XMLConfig.ELEM_ATTR_DEFAULT);
      _loggerName = this.checkSubstitution(_loggerName);

      if (_loggerName == null || !(_loggers.containsKey(_loggerName)))
      {
         Logger.logError("Unable to initialize logger '" + _loggerName + "'. Using default");
      }
      else
      {
         /*
          * Setup/Override the main global logger that will be used.
          *
          */

         logger = this.getLoggers().get(_loggerName);

         /*
          * First, emit a message to the old Logger (most likely SysoutLogger)
          * that we are about to switch over.
          */

         Logger.logInfo("OpenPTK Switching over to new Logger ('" + _loggerName + "')");

         /*
          * Next, do the switch and emit a message to the new logger that it
          * is now initizlized.
          */

         Logger.setLogger(logger);

         Logger.logInfo("OpenPTK Logger Initialized ('" + _loggerName + "')");
      }

      return;

   }

   //----------------------------------------------------------------
   private void buildDefinitions() throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * Build all the Definition Elements within Definitions Section
       */
//   <Definitions>
//      <Definition id="Person">
//      ...
//      </Definition>
//      <Definition id="Role">
//      ...
//      </Defintition>
//   </Definitions>

      int listlen = 0;
      String METHOD_NAME = CLASS_NAME + ":buildDefinitions(): ";
      String definitionId = null;
      Element elements = null; //        DOM
      Element element = null; //         DOM
      NodeList nodelist = null; //       DOM
      DefinitionIF definition = null; // OpenPTK

      elements = getElement(XMLConfig.ELEM_NAME_DEFINITIONS);

      if (elements != null)
      {
         nodelist = elements.getElementsByTagName(XMLConfig.ELEM_NAME_DEFINITION);

         if (nodelist != null)
         {
            listlen = nodelist.getLength();
            for (int j = 0; j < listlen; j++)
            {
               element = (Element) nodelist.item(j);

               definitionId = element.getAttribute(XMLConfig.ELEM_ATTR_ID);
               definition = this.getDefinition(element);
               if (definition != null)
               {
                  if (_definitions.containsKey(definitionId))
                  {
                     this.handleError(METHOD_NAME + "Definition '" + definitionId
                        + "' already exists, id must be unique");
                  }
                  _definitions.put(definitionId, definition);
               }
            }
         }
         else
         {
            this.handleError(METHOD_NAME + "Definitions NodeList is null");
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "The Definitions section was not found");
      }

      return;

   }

   //----------------------------------------------------------------
   private void buildConnections() throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * Build all the Connection Elements within Services Section
       */
//   <Connections>
//      <Properties>
//         <Property name="service.classname" value="org.openptk.provision.spi.BasicService"/>
//      </Properties>
//      <Connection id="SunIdMgr">
//         ...
//      </Connection>
//      ...
//   </Conections>

      int listlen = 0;
      String METHOD_NAME = CLASS_NAME + ":buildConnections(): ";
      String connId = null;
      String srvcClassname = null;
      Properties servicesProps = null;
      Properties connProps = null;
      Element elements = null; //    DOM
      Element element = null; //     DOM
      NodeList nodelist = null; //   DOM


      elements = this.getElement(XMLConfig.ELEM_NAME_CONNECTIONS);

      if (elements != null)
      {
         servicesProps = this.getElementProperties(elements);
         srvcClassname = servicesProps.getProperty(XMLConfig.PROP_SERVICE_CLASSNAME);

         /*
          * Get the serviceClassName
          * The Service is the class that manages the Operations/Connections
          * for the Contexts.
          * Only one Service is used for the entire configuration.
          */

         if (srvcClassname != null && srvcClassname.length() > 0)
         {
            _serviceClassName = srvcClassname;
         }
         else
         {
            this.handleError(METHOD_NAME
               + "Property '" + XMLConfig.PROP_SERVICE_CLASSNAME + "' is not set for Services");
         }

         /*
          * Get all of the Connection's Properties
          */

         nodelist = elements.getElementsByTagName(XMLConfig.ELEM_NAME_CONNECTION);

         listlen = nodelist.getLength();
         for (int j = 0; j < listlen; j++)
         {
            element = (Element) nodelist.item(j);
            if (element != null)
            {
               connProps = this.getElementProperties(element);
               connId = element.getAttribute(XMLConfig.ELEM_ATTR_ID);

               _connectionprops.put(connId, connProps);
            }
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "No Services were found");
      }

      return;
   }

   //----------------------------------------------------------------
   private void buildAssociations() throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * Associations is a container Element that can hold zero or more
       * unique Association sub-Elements.
       */
//   <Associations>
//      <Association id="SPML">
//      </Association>
//      ...
//      <Association id="JDBC">
//      </Association>
//   </Associations>

      int listlen = 0;
      String METHOD_NAME = CLASS_NAME + ":buildAssociations(): ";
      String elementId = null;
      Element elements = null; //  DOM
      Element element = null; //   DOM
      NodeList nodelist = null; // DOM
      ComponentIF comp = null; //  OpenPTK

      elements = this.getElement(XMLConfig.ELEM_NAME_ASSOCIATIONS);

      if (elements != null)
      {
         nodelist = elements.getElementsByTagName(XMLConfig.ELEM_NAME_ASSOCIATION);

         listlen = nodelist.getLength();
         for (int i = 0; i < listlen; i++)
         {
            element = (Element) nodelist.item(i);
            if (element != null)
            {
               elementId = element.getAttribute(XMLConfig.ELEM_ATTR_ID);

               comp = new Component();
               comp.setUniqueId(elementId);
               comp.setCategory(Category.ASSOCIATION);
               comp.setDebug(this.isDebug());
               comp.setDebugLevel(this.getDebugLevel());

               this.parseAndSetAssociationAttributes(element, comp);

               if (!comp.isError())
               {
                  comp.setState(State.READY);

                  if (_associations.containsKey(elementId))
                  {
                     this.handleError(METHOD_NAME + "Association '" + elementId
                        + "' already exists, id must be unique");
                  }
                  _associations.put(elementId, comp);
               }
            }
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private void buildAttrGroups() throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * AttrGroups is a container Element that can hole zero or more
       * unique AttrGroup sub-Elements
       */

//   <AttrGroups>
//      <AttrGroup id="person-create">
//         ...
//      </AttrGroup>
//      ...
//   </AttrGroups>

      int listlen = 0;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String elementId = null;
      ComponentIF comp = null; //  OpenPTK
      Element elements = null; //  DOM
      Element element = null; //   DOM
      NodeList nodelist = null; // DOM

      elements = this.getElement(XMLConfig.ELEM_NAME_ATTRGROUPS);

      if (elements != null)
      {
         nodelist = elements.getElementsByTagName(XMLConfig.ELEM_NAME_ATTRGROUP);
         if (nodelist != null)
         {
            listlen = nodelist.getLength();
            for (int i = 0; i < listlen; i++)
            {
               element = (Element) nodelist.item(i);
               if (element != null)
               {
                  elementId = null;
                  comp = new Component();
                  elementId = element.getAttribute(XMLConfig.ELEM_ATTR_ID);

                  comp.setUniqueId(elementId);
                  comp.setCategory(Category.ATTRGROUP);
                  this.parseAndSetAttrGroup(element, comp);

                  if (!comp.isError())
                  {
                     if (_attrgroups.containsKey(elementId))
                     {
                        this.handleError(METHOD_NAME + "AttrGroup '" + elementId
                           + "' already exists, id must be unique");
                     }
                     _attrgroups.put(elementId, comp);
                  }
               }
            }
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private void buildAttrMaps() throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * AttrMaps support the mapping of external data schemas
       * to internal data schemas which must map to a Defintions set
       * of Attributes, associate to a Context.
       * The initial requirement for Attribute Mapping is to support
       * external hierarchical data and normalize it into a flat
       * name / value Attribute model used internally by the Framework.
       * This mapping feature can be leveraged by an input (Representation)
       * or output (Service) mechanism.
       */

//   <AttrMaps>
//      <AttrMap id="scim-user-1.0" classname="org.openptk.config.attribute.BasicAttrMap">
//         ...
//      </AttrMap>
//      ...
//   </AttrMaps>

      int listlen = 0;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String elementId = null;
      String className = null;
      Element elements = null; //  DOM
      Element element = null; //   DOM
      NodeList nodelist = null; // DOM
      AttrMapIF attrMap = null; //  OpenPTK

      elements = this.getElement(XMLConfig.ELEM_NAME_ATTRMAPS);
      if (elements != null)
      {
         nodelist = elements.getElementsByTagName(XMLConfig.ELEM_NAME_ATTRMAP);
         if (nodelist != null)
         {
            listlen = nodelist.getLength();
            for (int i = 0; i < listlen; i++)
            {
               element = (Element) nodelist.item(i);
               if (element != null)
               {
                  elementId = element.getAttribute(XMLConfig.ELEM_ATTR_ID);
                  className = element.getAttribute(XMLConfig.ELEM_ATTR_CLASSNAME);
                  if (elementId != null && elementId.length() > 0
                     && className != null && className.length() > 0)
                  {
                     try
                     {
                        attrMap = (AttrMapIF) this.instantiateClass(className);
                     }
                     catch (ConfigurationException ex)
                     {
                        this.handleError(METHOD_NAME + ex.getMessage());
                     }

                     attrMap.setUniqueId(elementId);

                     this.parseAndSetAttrMap(element, attrMap);

                     if (!attrMap.isError())
                     {
                        if (_attrMaps.containsKey(elementId))
                        {
                           this.handleError(METHOD_NAME + "AttrMap '" + elementId
                              + "' already exists, id must be unique");
                        }
                        _attrMaps.put(elementId, attrMap);
                     }
                  }
                  else
                  {
                     this.handleError(METHOD_NAME + "XML Element Attributes 'id' and 'classname' MUST be set.");
                  }
               }
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void buildConverters() throws ConfigurationException
   //----------------------------------------------------------------
   {
//   <Converters>
//      <Converter type="json" classname="org.openptk.structure.JsonConverter"/>
//      <Converter type="xml" classname="org.openptk.structure.XmlConverter">
//         <Structures>
//            <Structure id="forgottenPasswordAnswers">
//               <Properties>
//                  <Property name="multivalue" value="true"/>
//               </Properties>
//            </Structure>
//            <Structure id="forgottenPasswordQuestions">
//               <Properties>
//                  <Property name="multivalue" value="true"/>
//               </Properties>
//            </Structure>
//            <Structure id="roles">
//               <Properties>
//                  <Property name="multivalue" value="true"/>
//               </Properties>
//            </Structure>
//            <Structure id="contexts">
//               <Properties>
//                  <Property name="multivalue" value="true"/>
//               </Properties>
//            </Structure>
//         </Structures>
//      </Converter>
//      <Converter type="html" classname="org.openptk.structure.HtmlConverter"/>
//      <Converter type="plain" classname="org.openptk.structure.PlainConverter"/>
//   </Converters>

      int lenConvs = 0;
      int lenStructs = 0;
      String METHOD_NAME = CLASS_NAME + ":buildConverters(): ";
      String typeName = null;
      String className = null;
      String structId = null;
      Element elemConvs = null; //  DOM
      Element elemConv = null;
      Element elemStructs = null;
      Element elemStruct = null;
      NodeList nlConvs = null; // DOM
      NodeList nlStructs = null;
      Properties propStruct = null;
      ElementIF elem = null;
      ConverterType type = null;
      ConverterType[] types = null;
      ConverterIF converter = null;
      Map<String, ConverterType> typeNames = null;


      types = ConverterType.values();
      typeNames = new HashMap<String, ConverterType>();
      for (int i = 0; i < types.length; i++)
      {
         typeNames.put(types[i].toString().toLowerCase(), types[i]);
      }

      elemConvs = this.getElement(XMLConfig.ELEM_NAME_CONVERTERS);
      if (elemConvs != null)
      {
         nlConvs = elemConvs.getElementsByTagName(XMLConfig.ELEM_NAME_CONVERTER);
         lenConvs = nlConvs.getLength();
         for (int i = 0; i < lenConvs; i++)
         {
            elemConv = (Element) nlConvs.item(i);
            if (elemConv != null)
            {
               /*
                * Get the Type
                */

               typeName = null;
               typeName = elemConv.getAttribute(XMLConfig.ELEM_ATTR_TYPE);
               if (typeName != null && typeName.length() > 0)
               {
                  if (typeNames.keySet().contains(typeName.toLowerCase()))
                  {
                     type = typeNames.get(typeName.toLowerCase());
                  }
                  else
                  {
                     this.handleError(METHOD_NAME + "Converter has invalid type '" + typeName);
                  }
               }
               else
               {
                  this.handleError(METHOD_NAME + "A Converter has a null type");
               }

               /*
                * Get the Classname
                */

               className = null;
               className = elemConv.getAttribute(XMLConfig.ELEM_ATTR_CLASSNAME);
               if (className != null && className.length() > 0)
               {
                  converter = (ConverterIF) this.instantiateClass(className);
               }
               else
               {
                  this.handleError(METHOD_NAME + "Converter type '" + typeName
                     + "' has a null classname");
               }

               /*
                * Get Structure information, if it has any
                */

               elemStructs = this.getElement(elemConv, XMLConfig.ELEM_NAME_STRUCTURES);
               if (elemStructs != null)
               {
                  nlStructs = elemStructs.getElementsByTagName(XMLConfig.ELEM_NAME_STRUCTURE);
                  lenStructs = nlStructs.getLength();
                  for (int j = 0; j < lenStructs; j++)
                  {
                     elemStruct = (Element) nlStructs.item(j);
                     if (elemStruct != null)
                     {
                        structId = null;
                        structId = elemStruct.getAttribute(XMLConfig.ELEM_ATTR_ID);
                        if (structId != null && structId.length() > 0)
                        {
                           elem = new org.openptk.api.Element();
                           elem.setUniqueId(structId);

                           propStruct = this.getElementProperties(elemStruct);
                           if (propStruct != null)
                           {
                              elem.setProperties(propStruct);
                           }
                           converter.setStructInfo(structId, elem);
                        }
                     }
                  }
               }
               if (_converters.containsKey(type))
               {
                  this.handleError(METHOD_NAME + "Converter '" + type
                     + "' already exists, duplicates are not allowed");
               }
               _converters.put(type, converter);
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void buildModels() throws ConfigurationException
   //----------------------------------------------------------------
   {
//   <Models>
//      <Properties>
//         <Property name="model.classname" value="org.openptk.model.PersonModel"/>
//      </Properties>
//      <Model id="Employee">
//         <Relationships>
//            <Relationship id="directReports">
//               <Properties>
//                  <Property name="relationship.classname" value="org.openptk.model.ChildrenRelationship"/>
//               </Properties>
//               <Query type="EQ" name="dn" value="${manager}"/>
//            </Relationship>
//            ...
//         </Relationships>
//         <Views>
//            <View id="extended">
//               <Relationships>
//                  <Relationship id="reportsTo"/>
//                  <Relationship id="peers"/>
//                  <Relationship id="directReports"/>
//               </Relationships>
//            </View>
//         </Views>
//      </Model>
//      ...
//   </Models>

      int listlen = 0;
      String METHOD_NAME = CLASS_NAME + ":buildModels(): ";
      String modelId = null;
      String classname = null;
      Properties elementsProps = null;
      Properties elementProps = null;
      Properties mergeProps = null;
      Element elements = null; //  DOM
      Element element = null; // DOM
      NodeList nodelist = null; // DOM
      ModelIF model = null;

      elements = this.getElement(XMLConfig.ELEM_NAME_MODELS);

      if (elements != null)
      {
         elementsProps = this.getElementProperties(elements);

         nodelist = elements.getElementsByTagName(XMLConfig.ELEM_NAME_MODEL);

         listlen = nodelist.getLength();
         for (int i = 0; i < listlen; i++)
         {
            element = (Element) nodelist.item(i);
            modelId = element.getAttribute(XMLConfig.ELEM_ATTR_ID);
            elementProps = this.getElementProperties(element);

            mergeProps = this.combineProperties(elementsProps, elementProps);

            if (modelId != null && mergeProps != null)
            {
               classname = mergeProps.getProperty(XMLConfig.PROP_MODEL_CLASSNAME);
               if (classname != null && classname.length() > 0)
               {
                  model = this.createModel(element, modelId, classname, mergeProps);

                  if (model != null)
                  {
                     if (_models.containsKey(modelId))
                     {
                        this.handleError(METHOD_NAME + "Model '" + modelId
                           + "' already exists, id must be unique");
                     }
                     _models.put(modelId, model);
                  }
               }
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void buildContexts() throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * Contexts is a container Element that can hole one or more
       * unique Context sub-Elements. The Contexts container Element
       * can have a Properties sub-element. These Properties will be
       * provided to each Context sub-Element.
       */
//   <Contexts>
//      <Properties>
//         ...
//      </Properties>
//      <Context id="Person-SunIdm-SPML" ... >
//       ...
//      </Context>
//      ...
//   </Contexts>

      boolean audit = false;
      int listlen = 0;
      String METHOD_NAME = CLASS_NAME + ":buildContexts(): ";
      String contextId = null;
      Properties contextsProps = null;
      NodeList nodelist = null;
      Element ctxElements = null;
      Element ctxElement = null;

      ContextIF context = null;

      /*
       * initialize
       */

      _mapCtxRelCtx = new HashMap<String, Map<String, String>>();

      ctxElements = this.getElement(XMLConfig.ELEM_NAME_CONTEXTS);

      if (ctxElements != null)
      {
         /*
          * Get the Properties that will be pushed to each Context
          */

         contextsProps = this.combineProperties(this.getProperties(),
            this.getElementProperties(ctxElements));

         /*
          * Get the audit flag
          */

         audit = Boolean.parseBoolean(contextsProps.getProperty(XMLConfig.PROP_AUDIT));

         /*
          * Get the name of the default context
          */

         _defaultCtxName = contextsProps.getProperty(XMLConfig.PROP_CONTEXT_DEFAULT);

         /*
          * Process each defined Context
          */

         nodelist = ctxElements.getElementsByTagName(XMLConfig.ELEM_NAME_CONTEXT);
         listlen = nodelist.getLength();
         for (int j = 0; j < listlen; j++)
         {
            ctxElement = (Element) nodelist.item(j);
            if (ctxElement != null)
            {
               contextId = ctxElement.getAttribute(XMLConfig.ELEM_ATTR_ID);

               // check to see if this context already exists

               if (_contexts.containsKey(contextId))
               {
                  this.handleError(METHOD_NAME + "Context: '" + contextId
                     + "' already defined");
               }

               context = this.createContext(ctxElement, contextsProps);

               if (context != null)
               {
                  context.setConfiguration(_configuration);
                  context.setAudit(audit);

                  if (_contexts.containsKey(contextId))
                  {
                     this.handleError(METHOD_NAME + "Context '" + contextId
                        + "' already exists, id must be unique");
                  }
                  _contexts.put(contextId, context);
               }
               else
               {
                  this.handleError(METHOD_NAME + "Context '" + contextId
                     + "' is null");
               }
            }
         }
         this.postProcessContexts();
      }
      else
      {
         this.handleError(METHOD_NAME + "There are no Contexts");
      }

      return;
   }

   //----------------------------------------------------------------
   private void buildPlugins() throws ConfigurationException
   //----------------------------------------------------------------
   {
//   <Plugins>
//      <Plugin id="mimeutil" enabled="true" classname="org.openptk.plugin.mimeutil.MimeUtilPlugin">
//         <Properties>
//            <Property name="detector" value="MagicMimeMimeDetector"/>
//            <Property name="input.attribute.document" value="document"/>
//            <Property name="output.attribute.mime" value="mime"/>
//         </Properties>
//      </Plugin>
//   </Plugins>

      int pluginsLen = 0;
      String METHOD_NAME = CLASS_NAME + ":buildPlugins(): ";
      String pluginId = null;
      Element pluginsElement = null;
      Element pluginElement = null;
      NodeList clientsNL = null;
      PluginIF plugin = null;

      pluginsElement = this.getElement(XMLConfig.ELEM_NAME_PLUGINS);

      if (pluginsElement == null)
      {
         this.handleError(METHOD_NAME + "Plugins section was not found");
      }

      clientsNL = pluginsElement.getElementsByTagName(XMLConfig.ELEM_NAME_PLUGIN);
      pluginsLen = clientsNL.getLength();

      for (int i = 0; i < pluginsLen; i++)
      {
         pluginElement = (Element) clientsNL.item(i);
         if (pluginElement != null)
         {
            pluginId = pluginElement.getAttribute(XMLConfig.ELEM_ATTR_ID);

            plugin = this.createPlugin(pluginElement);

            if (plugin != null)
            {
               if (_plugins.containsKey(pluginId))
               {
                  this.handleError(METHOD_NAME + "Plugin '" + pluginId
                     + "' already exists, id must be unique");
               }
               _plugins.put(pluginId, plugin);
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void buildActions() throws ConfigurationException
   //----------------------------------------------------------------
   {
//   <Actions>
//      <Action id="ifexists" classname="org.openptk.definitions.actions.UseUpdateIfExists">
//         <Properties>
//            <Property name="actions.description" value="Change to Update if entry exists"/>
//         </Properties>
//      </Action>
//   </Actions>

      int actionsLen = 0;
      String METHOD_NAME = CLASS_NAME + ":buildActions(): ";
      String actionId = null;
      Element actionsElement = null;
      Element actionElement = null;
      NodeList actionsNL = null;
      ActionIF action = null;

      actionsElement = this.getElement(XMLConfig.ELEM_NAME_OPERATIONACTIONS);

      if (actionsElement == null)
      {
         this.handleError(METHOD_NAME + "Actions section was not found");
      }

      actionsNL = actionsElement.getElementsByTagName(XMLConfig.ELEM_NAME_ACTION);
      actionsLen = actionsNL.getLength();

      for (int i = 0; i < actionsLen; i++)
      {
         actionElement = (Element) actionsNL.item(i);
         if (actionElement != null)
         {
            actionId = actionElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
            action = this.createAction(actionElement);

            if (action != null)
            {
               /*
                * Check map to see if one already exists, must have unique id
                */

               if (_actions.containsKey(actionId))
               {
                  this.handleError(METHOD_NAME + "Action '" + actionId
                     + "', already exists, id must be unique");
               }

               _actions.put(actionId, action);
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void postProcessContexts() throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * Look the Context "map" of Relationship <-> Context "maps"
       * If a Relationship, for a given Context, has a Context ...
       * then validate the Context and set it in the Relationship
       */

      String METHOD_NAME = CLASS_NAME + ":postProcessContexts(): ";
      String relCtxId = null;
      Map<String, String> mapRelCtx = null;
      ContextIF context = null;
      ContextIF ctx = null;
      RelationshipIF relationship = null;

      if (_mapCtxRelCtx != null && !_mapCtxRelCtx.isEmpty())
      {
         for (String contextId : _mapCtxRelCtx.keySet())
         {

            if (contextId != null && contextId.length() > 0 && _contexts.containsKey(contextId))
            {
               context = _contexts.get(contextId);
               mapRelCtx = _mapCtxRelCtx.get(contextId);

               if (mapRelCtx != null && !mapRelCtx.isEmpty())
               {
                  for (String relId : mapRelCtx.keySet())
                  {
                     if (relId != null && relId.length() > 0 && context.hasRelationship(relId))
                     {
                        relationship = context.getRelationship(relId);
                        relCtxId = null;
                        relCtxId = mapRelCtx.get(relId);

                        /*
                         * If the ctxId is null .. then the "original" (self) Context is used
                         * Nothing to do.
                         * If it's not null .. make sure it's a valid "enabled" Context
                         */

                        if (relCtxId != null && relCtxId.length() > 0)
                        {
                           if (_contexts.containsKey(relCtxId))
                           {
                              ctx = null;
                              ctx = _contexts.get(relCtxId);

                              if (ctx != null)
                              {

                                 /*
                                  * Update the Context/Relationship with the specified Context
                                  */

                                 relationship.setContext(ctx);
                              }
                           }
                           else
                           {
                              this.handleError(METHOD_NAME
                                 + "Context '" + contextId + "' has a Model/Relationship '" + relId
                                 + "', with an invalid external Context '" + relCtxId + "'");
                           }
                        }
                     }
                     else
                     {
                        this.handleError(METHOD_NAME + "Context '" + contextId
                           + "' has an invalid Relationship '" + relId + "'");
                     }
                  }
               }
            }
            else
            {
               this.handleError(METHOD_NAME + "Context '" + contextId + "' does not exist");
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private ContextIF createContext(final Element element, final Properties contextsProps) throws ConfigurationException
   //----------------------------------------------------------------
   {
//      <Context id="Person-OpenDS-JNDI" definition="Person" connection="OpenDS" association="JNDI">
//         <Properties>
//            <Property name="context.description" value="Person to OpenDS using JNDI"/>
//            <Property name="context.classname"   value="org.openptk.provision.spi.operations.JndiOperations"/>
//            ...
//         </Properties>
//         <Model id="Employee">
//            <Relationships>
//               <Relationship id="location" context="Locations-OpenDS-JNDI"/>
//            </Relationships>
//         </Model>
//         <Query type="EQ" name="objectClass" value="inetOrgPerson"/>
//         <Operations>
//            <Operation id="create">
//               ...
//            </Operation>
//            <Operation id="read">
//               ...
//            </Operation>
//            <Operation id="update">
//               ...
//            </Operation>
//            <Operation id="delete">
//            </Operation>
//            <Operation id="search">
//               ...
//         </Operations>
//         <Assignments>
//            <Assignment id="http-hdr-attrmap" description="HTTP Header for SCIM map">
//               <Source      type="http_header" name="X-OPENPTK-ATTRMAP" value="SCIM-USER-1.0"/>
//               <Destination type="attr_map"    name="scim.user-1.0-consumer"/>
//            </Assignment>
//            <Assignment id="http-hdr-alias-url" description="re-write URL for responses">
//               <Source      type="http_header" name="X-OPENPTK-ALIAS-URL"/>
//               <Destination type="property"    name="response.url"/>
//            </Assignment>
//         </Assignments>
//      </Context>
      boolean enabled = false;
      String METHOD_NAME = CLASS_NAME + ":getContext(): ";
      String contextId = null;
      String contextDesc = null;
      String contextClassname = null;
      String definitionName = null;
      String modelId = null;
      String relId = null;
      String relCtx = null;
      String securityEncryption = null;
      String[] relIds = null;
      Map<String, String> mapRelCtx = null;
      Properties ctxProps = null;
      Properties mergeProps = null;
      Properties relProps = null;
      Element modelElement = null; //    DOM
      Element queryElement = null; //    DOM
      Element relElement = null; //      DOM
      Element relElements = null;  //    DOM
      NodeList relList = null;     //    DOM
      ContextIF context = null; //       OpenPTK
      DefinitionIF definition = null; // OpenPTK
      ServiceIF service = null; //       OpenPTK
      Query query = null; //             OpenPTK
      Query relQuery = null; //          OpenPTK
      ModelIF model = null; //           OpenPTK
      RelationshipIF relModel = null;
      RelationshipIF relContext = null;

      contextId = element.getAttribute(XMLConfig.ELEM_ATTR_ID);

      enabled = Boolean.valueOf(element.getAttribute(XMLConfig.ELEM_ATTR_ENABLED));

      /*
       * Get the properties for the specific Context and merge them together
       */

      ctxProps = this.getElementProperties(element);
      mergeProps = this.combineProperties(contextsProps, ctxProps);

      /*
       * Get the Context classname and instanciate the class
       */

      contextClassname = mergeProps.getProperty(XMLConfig.PROP_CONTEXT_CLASSNAME);

      if (contextClassname != null && contextClassname.length() > 0)
      {
         context = (ContextIF) this.instantiateClass(contextClassname);
      }
      else
      {
         Logger.logWarning(METHOD_NAME + " Context '" + contextId
            + "' will not be created, The classname is not set");
      }

      if (context != null)
      {
         context.setUniqueId(contextId);
         context.setCategory(Category.CONTEXT);
         context.setDebugLevel(this.getDebugLevel());
         context.setDebug(this.isDebug());
         context.setProperties(mergeProps);
         context.useTimeStamp(this.isTimeStamp());

         contextDesc = mergeProps.getProperty(XMLConfig.PROP_CONTEXT_DESCRIPTION);
         if (contextDesc != null && contextDesc.length() > 0)
         {
            context.setDescription(contextDesc);
         }

         if (enabled)
         {
            context.setState(State.READY);
         }
         else
         {
            context.setState(State.DISABLED);
         }

         /*
          * Get the Definition (apply the Config's data, as needed)
          */

         definitionName = element.getAttribute(XMLConfig.ELEM_ATTR_DEFINITION);

         if (definitionName != null && definitionName.length() > 0)
         {
            if (_definitions.containsKey(definitionName))
            {
               definition = _definitions.get(definitionName);
               context.setDefinition(definition);
            }
            else
            {
               this.handleError(METHOD_NAME + "Context '" + contextId
                  + "' Definition Property is empty/null");
            }
         }
         else
         {
            this.handleError(METHOD_NAME + "Context '" + contextId
               + "' does not have a Definition Property");
         }

         /*
          * Get the Model, if it is set
          * Then get all the Relationships associated with the Model
          * We want a new instance (copy) of each Relationship for each Context
          */

         modelElement = this.getElement(element, XMLConfig.ELEM_NAME_MODEL);
         if (modelElement != null)
         {
            modelId = modelElement.getAttribute(XMLConfig.ELEM_ATTR_ID);

            if (modelId != null && modelId.length() > 0)
            {
               if (_models.containsKey(modelId))
               {
                  model = _models.get(modelId);
                  context.setModel(model);

                  /*
                   * Copy all the Model Relationships into the Context
                   */

                  mapRelCtx = new HashMap<String, String>();

                  relIds = model.getRelationshipNames();
                  if (relIds != null && relIds.length > 0)
                  {
                     for (int i = 0; i < relIds.length; i++)
                     {
                        relId = relIds[i];
                        relModel = model.getRelationship(relId);
                        try
                        {
                           relContext = (RelationshipIF) relModel.getClass().newInstance();
                        }
                        catch (Exception ex)
                        {
                           this.handleError(METHOD_NAME
                              + "Failed to instanciate Relationship '"
                              + relIds[i].toString() + "': " + ex.getMessage());
                        }
                        relQuery = relModel.getQuery();
                        if (relQuery != null)
                        {
                           relContext.setQuery(relQuery);
                        }
                        relProps = relModel.getProperties();
                        if (relProps != null)
                        {
                           relContext.setProperties(relProps);
                        }
                        relContext.setContext(context);
                        context.setRelationship(relIds[i], relContext);

                        mapRelCtx.put(relId, null);
                     }
                  }

                  /*
                   * Check to see if any of the Relationships have set a Context
                   */

                  relElements = this.getElement(modelElement, XMLConfig.ELEM_NAME_RELATIONSHIPS);
                  if (relElements != null)
                  {
                     relList = relElements.getElementsByTagName(XMLConfig.ELEM_NAME_RELATIONSHIP);
                     for (int i = 0; i < relList.getLength(); i++)
                     {
                        relElement = (Element) relList.item(i);
                        relId = relElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
                        if (relId != null && relId.length() > 0)
                        {
                           if (context.hasRelationship(relId))
                           {
                              /*
                               * If the Relationship has a Context we need to save it
                               * in the "map", we can't validate / set the Context becuase
                               * is might not have been created yet (chick-n-egg) problem
                               * We will have to post process the Relationships in the Contexts
                               * using the map that is built
                               */

                              relCtx = relElement.getAttribute(XMLConfig.ELEM_ATTR_CONTEXT);
                              if (relCtx != null && relCtx.length() > 0)
                              {
                                 mapRelCtx.put(relId, relCtx);
                              }
                           }
                           else
                           {
                              this.handleError(METHOD_NAME
                                 + "Model '" + modelId + "' has an invalid Relationship '"
                                 + relId + "' for Context '" + contextId + "'");
                           }
                        }
                     }
                  }

                  _mapCtxRelCtx.put(contextId, mapRelCtx);
               }
               else
               {
                  this.handleError(METHOD_NAME
                     + "Model '" + modelId + "' is not valid for Context '"
                     + contextId + "'");
               }
            }
         }

         /*
          * Instanciate a Service object for the Context
          * The serviceClassname was set in the buildServices/getService section
          * The serviceClassname is global for all Contexts / Operations
          */

         if (_serviceClassName != null && _serviceClassName.length() > 0)
         {
            service = (ServiceIF) this.instantiateClass(_serviceClassName);
         }
         else
         {
            this.handleError(METHOD_NAME + "Service classname is null");
         }

         service.setState(State.READY);
         service.setDebugLevel(this.getDebugLevel());
         service.setDebug(this.isDebug());
         service.useTimeStamp(this.isTimeStamp());
         service.setContext(context);
         service.setDescription("Service for Context: " + context.getDescription());

         context.setService(service);

         /*
          * Get the Query (if it has one)
          */

         queryElement = this.getElement(element, XMLConfig.ELEM_NAME_QUERY);

         if (queryElement != null)
         {
            query = this.getQuery(queryElement);
            if (query != null)
            {
               context.setQuery(query);
            }
         }

         /*
          * Get the Operations
          */

         if (context.getState() != State.DISABLED)
         {
            this.parseAndSetOperations(element, context);
         }

         /*
          * Get the Assignments for this context
          */

         this.parseAndSetAssignments(element, context);

         /*
          * Get the Attributes for this context
          */

         this.getContextAttributes(context);
      }
      else
      {
         this.handleError(METHOD_NAME + "Context is NULL");
      }

      return context;
   }

   //----------------------------------------------------------------
   private LoggingIF getLogger(final String loggerName) throws ConfigurationException
   //----------------------------------------------------------------
   {
//   <Loggers>
//      <Logger id="UnixLogFile">
//         <Properties>
//            <Property name="logger.classname" value="org.openptk.provision.logging.AtomicLogger"/>
//            <Property name="file"      value="openptk.log"/>
//         </Properties>
//      </Logger>
//   </Loggers>

      LoggingIF log = null;
      Element logElement = null;
      Properties logProperties = null;
      String logClassname = null;
      String logDir = null;
      String logFile = null;
      Object[] logArguments = new Object[1];

      logElement = this.getElementById(XMLConfig.ELEM_NAME_LOGGER, loggerName);

      // Get properties for the logger

      logProperties = this.getElementProperties(logElement);

      logClassname = logProperties.getProperty(XMLConfig.PROP_LOGGER_CLASSNAME);

      logDir = this.getProperty(PROP_OPENPTK_TEMP);

      if (logDir.endsWith(System.getProperty("file.separator")))
      {
         logFile = logDir + logProperties.getProperty("file");
      }
      else
      {
         logFile = logDir + System.getProperty("file.separator") + logProperties.getProperty("file");
      }

      logArguments[0] = logFile;

      try
      {
         // Create an instance of the logging class

         log = (LoggingIF) instantiateClass(logClassname, logArguments);
      }
      catch (Exception ex)
      {
         throw new ConfigurationException(ex);
      }

      /*
       * Save the log object "locally" so that "this" class can log to it
       */

      return log;
   }

   //----------------------------------------------------------------
   private DefinitionIF getDefinition(final Element definitionElement) throws ConfigurationException
   //----------------------------------------------------------------
   {
//      <Definition id="Person">
//         <Properties>
//            <Property name="definition.classname" value="org.openptk.provision.api.Person"/>
//            <Property name="definition.key"       value="uniqueid"/>
//            <Property name="definition.authenid"  value="id,email"/>
//            <Property name="definition.password"  value="password"/>
//         </Properties>
//         <Attributes>
//            ...
//         </Attributes>
//      </Definition>

      String METHOD_NAME = CLASS_NAME + ":getDefinition() ";
      String id = null;
      String desc = null;
      String classname = null;
      Properties definitionProps = null;
      DefinitionIF definition = null;

      id = definitionElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
      if (id != null)
      {
         definition = new SubjectDefinition();
         definition.setUniqueId(id);

         definitionProps = this.getElementProperties(definitionElement);
         definition.setProperties(definitionProps);

         classname = definition.getProperty(XMLConfig.PROP_DEFINITION_CLASSNAME);
         if (classname != null && classname.length() > 1)
         {
            definition.setDefinitionClassName(classname);
            this.parseAndSetDefinitionAttributes(definitionElement, definition);
         }
         else
         {
            this.handleError(METHOD_NAME + "Definition Property '"
               + XMLConfig.PROP_DEFINITION_CLASSNAME + "' is not set for (" + id + ")");
         }

         desc = definition.getProperty(XMLConfig.PROP_DEFINITION_DESCRIPTION);
         if (desc != null && desc.length() > 1)
         {
            definition.setDescription(desc);
         }
         else
         {
            definition.setDescription(classname);
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "Definition has no 'id'");
      }

      definition.setDebugLevel(this.getDebugLevel());
      definition.setDebug(this.isDebug());
      definition.useTimeStamp(this.isTimeStamp());
      definition.setState(State.READY);

      return definition;

   }

   //----------------------------------------------------------------
   private CryptoIF createCrypto(final String id, final String classname) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":createCrypto(): ";
      CryptoIF crypto = null;

      try
      {
         crypto = (CryptoIF) this.instantiateClass(classname);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage()
            + ": id='" + id + "', classname='" + classname + "'");
      }

      if (crypto != null)
      {
         crypto.setId(id);
      }

      return crypto;
   }

   //----------------------------------------------------------------
   private ModelIF createModel(final Element modelElement, final String modelId, final String classname, final Properties props) throws ConfigurationException
   //----------------------------------------------------------------
   {
      int relLen = 0;
      int viewLen = 0;
      String METHOD_NAME = CLASS_NAME + ":createModel(): ";
      String desc = null;
      String relId = null;
      String relClassname = null;
      String viewId = null;
      String[] relIds = null;
      List<String> relNames = null;
      Properties relsProps = null;
      Properties relProps = null;
      Properties mergeProps = null;
      Element relElement = null;   // DOM
      Element relElements = null;  // DOM
      Element viewElement = null;  // DOM
      Element viewElements = null; // DOM
      NodeList relList = null;     // DOM
      NodeList viewList = null;    // DOM
      ModelIF model = null;
      RelationshipIF relationship = null;
      ViewIF view = null;

      try
      {
         model = (ModelIF) this.instantiateClass(classname);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage()
            + ": id='" + modelId + "', classname='" + classname + "'");
      }

      if (model != null)
      {
         model.setUniqueId(modelId);
         model.setDebug(this.isDebug());
         model.useTimeStamp(this.isTimeStamp());
         model.setProperties(props);
         desc = props.getProperty(XMLConfig.PROP_MODEL_DESCRIPTION);
         if (desc != null && desc.length() > 0)
         {
            model.setDescription(desc);
         }

         /*
          * Get the Relationship(s)
          */

         relElements = this.getElement(modelElement, XMLConfig.ELEM_NAME_RELATIONSHIPS);
         if (relElements != null)
         {
            relsProps = this.getElementProperties(relElements);
            relList = relElements.getElementsByTagName(XMLConfig.ELEM_NAME_RELATIONSHIP);
            relLen = relList.getLength();
            for (int i = 0; i < relLen; i++)
            {
               relElement = (Element) relList.item(i);
               relId = relElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
               relProps = this.getElementProperties(relElement);
               mergeProps = this.combineProperties(relsProps, relProps);

               if (relId != null && mergeProps != null)
               {
                  relClassname = mergeProps.getProperty(XMLConfig.PROP_RELATIONSHIP_CLASSNAME);
                  if (relClassname != null && relClassname.length() > 0)
                  {
                     relationship = this.createRelationship(relElement, relId, relClassname, mergeProps);

                     if (relationship != null)
                     {
                        model.setRelationship(relId, relationship);
                     }
                  }
               }
            }
         }

         /*
          * Get the View(s)
          */

         relIds = model.getRelationshipNames();
         relNames = new LinkedList<String>();
         for (int i = 0; i < relIds.length; i++)
         {
            relNames.add(relIds[i]);
         }

         viewElements = this.getElement(modelElement, XMLConfig.ELEM_NAME_VIEWS);
         if (viewElements != null)
         {
            viewList = viewElements.getElementsByTagName(XMLConfig.ELEM_NAME_VIEW);
            viewLen = viewList.getLength();
            for (int j = 0; j < viewLen; j++)
            {
               viewElement = (Element) viewList.item(j);
               viewId = viewElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
               if (viewId != null && viewId.length() > 0)
               {
                  view = new BasicView(viewId);
                  relElements = this.getElement(viewElement, XMLConfig.ELEM_NAME_RELATIONSHIPS);
                  if (relElements != null)
                  {
                     relList = relElements.getElementsByTagName(XMLConfig.ELEM_NAME_RELATIONSHIP);
                     relLen = relList.getLength();
                     for (int i = 0; i < relLen; i++)
                     {
                        relElement = (Element) relList.item(i);
                        relId = relElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
                        if (relId != null && relId.length() > 0)
                        {
                           if (relNames.contains(relId))
                           {
                              view.addRelationshipId(relId);
                           }
                           else
                           {
                              this.handleError(METHOD_NAME
                                 + "Model='" + modelId + "', View='" + viewId
                                 + "', Invalid Relationship: '" + relId + "'");
                           }
                        }
                     }
                  }
                  model.setView(viewId, view);
               }
            }
         }
      }

      return model;
   }

   //----------------------------------------------------------------
   private RelationshipIF createRelationship(final Element relElem, final String relId, final String classname, final Properties props) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":createRelationship(): ";
      String desc = null;
      RelationshipIF relationship = null;
      Query query = null;
      Element queryElem = null;

      try
      {
         relationship = (RelationshipIF) this.instantiateClass(classname);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage()
            + ": id='" + relId + "', classname='" + classname + "'");
      }

      if (relationship != null)
      {
         relationship.setUniqueId(relId);
         relationship.setDebug(this.isDebug());
         relationship.useTimeStamp(this.isTimeStamp());
         relationship.setProperties(props);
         desc = props.getProperty(XMLConfig.PROP_RELATIONSHIP_DESCRIPTION);
         if (desc != null && desc.length() > 0)
         {
            relationship.setDescription(desc);
         }

         /*
          * Get the Query, if it exists.
          */

         if (relElem != null)
         {
            queryElem = this.getElement(relElem, XMLConfig.ELEM_NAME_QUERY);
            if (queryElem != null)
            {
               query = this.getQuery(queryElem);
               if (query != null)
               {
                  query.setInternal(true);
                  relationship.setQuery(query);
               }
            }
         }
      }

      return relationship;
   }

   //----------------------------------------------------------------
   private AuthenticatorIF createAuthenticator(final String id, final String classname, final SessionType level, final Properties props) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":createAuthenticator(): ";
      String desc = null;
      String contextName = null;
      AuthenticatorIF authenticator = null;

      try
      {
         authenticator = (AuthenticatorIF) this.instantiateClass(classname);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage()
            + ": id='" + id + "', classname='" + classname + "'");
      }

      if (authenticator != null)
      {
         authenticator.setUniqueId(id);
         authenticator.setDebug(this.isDebug());
         authenticator.useTimeStamp(this.isTimeStamp());
         authenticator.setConfiguration(_configuration);
         contextName = props.getProperty(XMLConfig.PROP_AUTHENTICATOR_CONTEXT);
         if (contextName != null && contextName.length() > 0)
         {
            authenticator.setContext(_contexts.get(contextName));
         }

         authenticator.setProperties(props);
         desc = props.getProperty(XMLConfig.PROP_AUTHENTICATOR_DESCRIPTION);
         if (desc != null && desc.length() > 0)
         {
            authenticator.setDescription(desc);
         }
         if (level != null)
         {
            authenticator.setLevel(level);
         }
      }

      return authenticator;
   }

   //----------------------------------------------------------------
   private DeciderIF createDecider(final String id, final String classname) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":createDecider(): ";
      DeciderIF decider = null;

      try
      {
         decider = (DeciderIF) this.instantiateClass(classname);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage()
            + ": id='" + id + "', classname='" + classname + "'");
      }

      if (decider != null)
      {
         decider.setUniqueId(id);
         decider.setDebug(this.isDebug());
         decider.setDebugLevel(this.getDebugLevel());
         decider.useTimeStamp(this.isTimeStamp());
      }
      else
      {
         this.handleError(METHOD_NAME + "Decider '"
            + classname + "' is null.");
      }

      return decider;
   }

   //----------------------------------------------------------------
   private EnforcerIF createEnforcer(final String id, final String classname,
      final String deciderId, final Environment env) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":createEnforcer(): ";
      EnforcerIF enforcer = null;

      try
      {
         enforcer = (EnforcerIF) this.instantiateClass(classname);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage()
            + ": id='" + id + "', classname='" + classname + "'");
      }

      if (enforcer != null)
      {
         enforcer.setUniqueId(id);
         enforcer.setDeciderId(deciderId);
         enforcer.setEnvironment(env);
         enforcer.setDebug(this.isDebug());
         enforcer.useTimeStamp(this.isTimeStamp());
      }
      else
      {
         this.handleError(METHOD_NAME + "Enforcer '"
            + classname + "' is null.");
      }

      return enforcer;
   }

   //----------------------------------------------------------------
   private PolicyIF createPolicy(final String id, final String classname, final Environment env,
      final PolicyMode mode, final Effect effect) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":createPolicy(): ";
      PolicyIF policy = null;

      try
      {
         policy = (PolicyIF) this.instantiateClass(classname);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage()
            + ": id='" + id + "', classname='" + classname + "'");
      }

      if (policy != null)
      {
         policy.setUniqueId(id);
         policy.setEnvironment(env);
         policy.setMode(mode);
         policy.setEffect(effect);
         policy.setDebug(this.isDebug());
         policy.useTimeStamp(this.isTimeStamp());
      }
      else
      {
         this.handleError(METHOD_NAME + "Policy '"
            + classname + "' is null.");
      }

      return policy;
   }

   //----------------------------------------------------------------
   private TargetIF createTarget(final String id, final String classname,
      final TargetType type, final String value) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":createTarget(): ";
      TargetIF target = null;

      try
      {
         target = (TargetIF) this.instantiateClass(classname);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage()
            + ": id='" + id + "', classname='" + classname + "'");
      }

      if (target != null)
      {
         target.setUniqueId(id);
         target.setType(type);
         target.setValue(value);
         target.setDebug(this.isDebug());
         target.useTimeStamp(this.isTimeStamp());
      }
      else
      {
         this.handleError(METHOD_NAME + "Target '" + classname + "' is null.");
      }

      return target;

   }

   //----------------------------------------------------------------
   private PluginIF createPlugin(final Element element) throws ConfigurationException
   //----------------------------------------------------------------
   {
      boolean enabled = false;
      String METHOD_NAME = CLASS_NAME + ":createPlugin(): ";
      PluginIF plugin = null;
      String pluginId = null;
      String classname = null;
      Properties props = null;

      pluginId = element.getAttribute(XMLConfig.ELEM_ATTR_ID);
      enabled = Boolean.valueOf(element.getAttribute(XMLConfig.ELEM_ATTR_ENABLED));
      classname = element.getAttribute(XMLConfig.ELEM_ATTR_CLASSNAME);

      if (pluginId == null || pluginId.length() < 1)
      {
         this.handleError(METHOD_NAME + "Plugin has a null/empty id");
      }

      if (classname == null || classname.length() < 1)
      {
         this.handleError(METHOD_NAME + "Plugin '" + pluginId + "' has a null classname");
      }

      try
      {
         plugin = (PluginIF) this.instantiateClass(classname);
      }
      catch (ConfigurationException ex)
      {
         plugin = null;
         Logger.logWarning(METHOD_NAME + "Plugin '" + pluginId
            + "' was not loaded, " + ex.getMessage());
      }

      if (plugin != null)
      {
         props = this.getElementProperties(element);
         plugin.setProperties(this.combineProperties(this.getProperties(), props));
         if (enabled)
         {
            try
            {
               plugin.startup();
               plugin.setState(State.READY);
               plugin.setUniqueId(pluginId);
               plugin.setDebug(this.isDebug());
               plugin.useTimeStamp(this.isTimeStamp());
            }
            catch (PluginException ex)
            {
               plugin.setState(State.DISABLED);
               Logger.logWarning(METHOD_NAME + "Plugin '" + pluginId
                  + "' is DISABLED, startup failed, " + ex.getMessage());
            }
         }
         else
         {
            plugin.setState(State.DISABLED);
         }
      }

      return plugin;
   }

   //----------------------------------------------------------------
   private ActionIF createAction(final Element element) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":createAction(): ";
      String actionId = null;
      String classname = null;
      String desc = null;
      ActionIF action = null;
      Properties props = null;

      actionId = element.getAttribute(XMLConfig.ELEM_ATTR_ID);
      classname = element.getAttribute(XMLConfig.ELEM_ATTR_CLASSNAME);

      if (actionId == null || actionId.length() < 1)
      {
         this.handleError(METHOD_NAME + "Action has a null/empty id");
      }

      if (classname == null || classname.length() < 1)
      {
         this.handleError(METHOD_NAME + "Action '" + actionId + "' has a null classname");
      }

      try
      {
         action = (ActionIF) this.instantiateClass(classname);
      }
      catch (ConfigurationException ex)
      {
         action = null;
         Logger.logWarning(METHOD_NAME + "Action '" + actionId
            + "' was not loaded, " + ex.getMessage());
      }

      if (action != null)
      {
         props = this.getElementProperties(element);
         desc = props.getProperty("action.description");
         action.setProperties(props);
         action.setUniqueId(actionId);
         action.setDebug(this.isDebug());
         action.useTimeStamp(this.isTimeStamp());
      }

      return action;
   }

   //----------------------------------------------------------------
   private Element getElement(final String elementName)
   //----------------------------------------------------------------
   {
      // Helper method to return the first element for the XML element passed.

      NodeList nl = _xmlRoot.getElementsByTagName(elementName);
      return (Element) nl.item(0);
   }

   //----------------------------------------------------------------
   private Element getElement(final Element element, final String elementName)
   //----------------------------------------------------------------
   {
      // Helper method to return the first element within element named by
      // elementName passed.

      Element subElem = null;
      NodeList nl = null;

      nl = element.getElementsByTagName(elementName);

      if (nl != null)
      {
         subElem = (Element) nl.item(0);
      }

      return subElem;
   }

   //----------------------------------------------------------------
   private Element getElementById(final String elementName, final String elementId)
   //----------------------------------------------------------------
   {
      // Helper method to return the element for the XML element
      // using the elementId passed.

      int listlen = 0;
      NodeList nl = _xmlRoot.getElementsByTagName(elementName);
      Element subElem = null;

      listlen = nl.getLength();
      for (int i = 0; i < listlen; i++)
      {
         subElem = (Element) nl.item(i);

         if (elementId.equalsIgnoreCase(subElem.getAttribute(XMLConfig.ELEM_ATTR_ID)))
         {
            return subElem;
         }
      }
      return null;
   }

   //----------------------------------------------------------------
   private Properties getElementProperties(final Element element)
   //----------------------------------------------------------------
   {
      /*
       * Helper method to return the properies for the XML element
       * Check the values to see if they reference a Default Property
       * look for the "%{def_prop_name}" syntax and then substitue
       */

      int listlen = 0;
      Properties properties = null;
      NodeList propNL = null;
      Element elemProp = null;
      Element elemProperties = null;
      String propName = null;
      String propValue = null;

      properties = new Properties();

      elemProperties = this.getElement(element, XMLConfig.ELEM_NAME_PROPERTIES);

      if (elemProperties != null)
      {
         propNL = elemProperties.getElementsByTagName(XMLConfig.ELEM_NAME_PROPERTY);

         if (propNL != null)
         {
            listlen = propNL.getLength();
            for (int j = 0; j < listlen; j++)
            {
               propName = null;
               propValue = null;
               elemProp = (Element) propNL.item(j);

               propName = elemProp.getAttribute(XMLConfig.ELEM_ATTR_NAME);
               if (propName != null && propName.length() > 0)
               {
                  propValue = elemProp.getAttribute(XMLConfig.ELEM_ATTR_VALUE);
                  if (propValue != null && propValue.length() > 0)
                  {
                     properties.put(propName, this.checkSubstitution(propValue));
                  }
               }
            }
         }
      }

      return properties;
   }

   //----------------------------------------------------------------
   private String checkSubstitution(final String str)
   //----------------------------------------------------------------
   {
      /*
       * Check the string to see if it contains the "%{def_prop_name}"
       * syntax and replace it with the Default Properites value
       */
      boolean bDone = false;
      int iBegin = -1;
      int iEnd = -1;
      String tmp = null;
      String propName = null;
      String propValue = null;
      StringBuffer buf = null;

      if (str == null)
      {
         return str;
      }

      tmp = new String(str); // make a copy
      buf = new StringBuffer();

      /*
       * 1 2
       * 0123456789012345678901
       * ----------------------
       * %{spml.url}
       *
       * HELLO %{world} to %{x}%{y}
       * to %{x}%{y}
       * %{y}
       */

      while (!bDone)
      {
         iBegin = tmp.indexOf("%{");
         if (iBegin >= 0)
         {
            propName = null;
            propValue = null;

            if (iBegin > 0)
            {
               buf.append(tmp.substring(0, iBegin));
               tmp = tmp.substring(iBegin);
            }
            else
            {
               iBegin += 2;
               iEnd = tmp.indexOf("}");
               if (iEnd > iBegin)
               {
                  propName = tmp.substring(iBegin, iEnd);
                  if (propName != null && propName.length() > 0)
                  {
                     propValue = _defaultProps.getProperty(propName);
                     if (propValue != null && propValue.length() > 0)
                     {
                        buf.append(propValue);
                     }
                  }
                  if (tmp.length() > iEnd + 1)
                  {
                     tmp = tmp.substring(iEnd + 1);
                  }
                  else
                  {
                     bDone = true;
                  }
               }
            }
         }
         else
         {
            buf.append(tmp);
            bDone = true;
         }
      }

      return buf.toString();
   }

   //----------------------------------------------------------------
   private void parseAndSetTaskArguments(final Element element, final TaskIF task)
   //----------------------------------------------------------------
   {
// Helper method to return the arguments for the XML element
//                     <Arguments>
//                        <Argument name="maxlength" type="literal"   value="8"/>
//                        <Argument name="first"     type="attribute" value="firstname"/>
//                        <Argument name="last"      type="attribute" value="lastname"/>
//                     </Arguments>

      int listLen = 0;
      NodeList argumentNL = null;
      Element argumentElement = null;
      String argName = null;
      String argType = null;
      String argValue = null;
      String argRequired = null;
      ArgumentIF argument = null;

      argumentNL = element.getElementsByTagName(XMLConfig.ELEM_NAME_ARGUMENT);

      listLen = argumentNL.getLength();
      for (int j = 0; j < listLen; j++)
      {
         argumentElement = (Element) argumentNL.item(j);
         if (argumentElement != null)
         {
            argName = argumentElement.getAttribute(XMLConfig.ELEM_ATTR_NAME);
            argType = argumentElement.getAttribute(XMLConfig.ELEM_ATTR_TYPE);
            argValue = argumentElement.getAttribute(XMLConfig.ELEM_ATTR_VALUE);
            argRequired = argumentElement.getAttribute(XMLConfig.ELEM_ATTR_REQUIRED);

            if (argName != null && argType != null && argValue != null)
            {
               argument = new TaskArgument(argName, argType, this.checkSubstitution(argValue));
               if (argRequired != null && argRequired.equalsIgnoreCase("true"))
               {
                  argument.setRequired(true);
               }
               task.addArgument(argument);
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void parseAndSetTaskOperations(final AttrIF attr, final Element element, final TaskIF task) throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * For the given Task (Function) process all the assigned Operations
       * For each Operation, get the Operation value
       * Use the attr.setTask() method to assign the Task to the Attribute
       * for all of the Operations.
       */
//                     <Operations>
//                        <Operation type="create" />
//                        <Operation type="update" />
//                        <Operation type="search" mode="TOSERVICE" />
//                     </Operations>

      NodeList operNL = null;
      Element operElement = null;
      String operType = null;
      String operMode = null;
      Operation[] operArray = Operation.values();
      TaskMode[] modeArray = TaskMode.values();
      TaskMode mode = null;

      operNL = element.getElementsByTagName(XMLConfig.ELEM_NAME_OPERATION);

      for (int j = 0; j < operNL.getLength(); j++)
      {
         operElement = (Element) operNL.item(j);
         if (operElement != null)
         {
            operType = operElement.getAttribute(XMLConfig.ELEM_ATTR_TYPE);
            if (operType != null && operType.length() > 0)
            {
               /*
                * Check for "mode" else use "AUTOMATIC"
                */

               mode = TaskMode.AUTOMATIC;
               operMode = operElement.getAttribute(XMLConfig.ELEM_ATTR_MODE);
               if (operMode != null && operMode.length() > 0)
               {
                  for (int i = 0; i < modeArray.length; i++)
                  {
                     if (operMode.equalsIgnoreCase(modeArray[i].toString()))
                     {
                        mode = modeArray[i];
                     }
                  }
               }

               /*
                * Set the Operation in the Task
                * Add the Task to the Attr
                */

               for (int i = 0; i < operArray.length; i++)
               {
                  if (operType.equalsIgnoreCase(operArray[i].toString()))
                  {
                     task.setOperationMode(operArray[i], mode);
                     attr.addTask(operArray[i], task);
                     break;
                  }
               }
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void parseAndSetDefinitionAttributes(final Element element, final DefinitionIF definition) throws ConfigurationException
   //----------------------------------------------------------------
   {
//         <Attributes>
//            <Attribute id="uniqueid" type="Long" required="true">
//               ...
//            </Attribute>
//            <Attribute id="foo" multivalued="..." readonly="..."/>
//            ...
//         </Attributes>

      int listlen = 0;
      String METHOD_NAME = CLASS_NAME + ": parseAndSetDefinitionAttributes(): ";
      String attrId = null;
      String arg = null;
      Properties attrProps = null;
      NodeList attrNL = null; //          DOM
      Element attributeElement = null; // DOM
      AttrIF attrDef = null; //      OpenPTK
      AttrIF attrExist = null; // OpenPTK

      attrNL = element.getElementsByTagName(XMLConfig.ELEM_NAME_ATTRIBUTE);
      listlen = attrNL.getLength();

      for (int j = 0; j < listlen; j++)
      {
         attributeElement = (Element) attrNL.item(j);
         if (attributeElement != null)
         {
            attrId = attributeElement.getAttribute(XMLConfig.ELEM_ATTR_ID);

            /*
             * Get the Attribute's XML element arguments, here are the defaults:
             * type = String
             * access = public
             * readonly = false
             * required = false
             * encrypted = false
             * multivalued = false
             */

            arg = attributeElement.getAttribute(XMLConfig.ELEM_ATTR_TYPE);
            if (arg != null && arg.length() > 0)
            {
               attrDef = new DefinitionAttr(attrId, arg);
            }
            else
            {
               attrDef = new DefinitionAttr(attrId);
            }

            attrDef.setCategory(AttrCategory.DEFINITION);

            arg = attributeElement.getAttribute(XMLConfig.ELEM_ATTR_ACCESS);
            if (arg != null && arg.length() > 0)
            {
               attrDef.setAccess(arg);
            }

            arg = attributeElement.getAttribute(XMLConfig.ELEM_ATTR_REQUIRED);
            if (arg != null && arg.length() > 0)
            {
               attrDef.setRequired(Boolean.parseBoolean(arg));
            }

            arg = attributeElement.getAttribute(XMLConfig.ELEM_ATTR_READONLY);
            if (arg != null && arg.length() > 0)
            {
               attrDef.setReadOnly(Boolean.parseBoolean(arg));
            }

            arg = attributeElement.getAttribute(XMLConfig.ELEM_ATTR_MULTIVALUED);
            if (arg != null && arg.length() > 0)
            {
               attrDef.setAllowMultivalue(Boolean.parseBoolean(arg));
            }

            arg = attributeElement.getAttribute(XMLConfig.ELEM_ATTR_ENCRYPTED);
            if (arg != null && arg.length() > 0)
            {
               attrDef.setEncrypted(Boolean.parseBoolean(arg));
            }

            arg = attributeElement.getAttribute(XMLConfig.ELEM_ATTR_VIRTUAL);
            if (arg != null && arg.length() > 0)
            {
               attrDef.setVirtual(Boolean.parseBoolean(arg));
            }

            this.parseAndSetAttributeFunctions(attributeElement, attrDef);

            attrProps = this.getElementProperties(attributeElement);
            if (attrProps != null && attrProps.size() > 0)
            {
               attrDef.setProperties(attrProps);
            }

            /*
             * Add the Attribute to the Definition
             * Check to if it already exists.
             * If it does ... log a WARNING
             */

            attrExist = definition.getAttribute(attrId);
            if (attrExist != null)
            {
               Logger.logWarning(METHOD_NAME + "Definition: '"
                  + (definition.getUniqueId() != null ? definition.getUniqueId().toString() : "(null)")
                  + "' has a duplicate Attribute: '" + attrId
                  + "', using the last one.");
            }

            definition.setAttribute(attrId, attrDef);
         }
      }
   }

   //----------------------------------------------------------------
   private void parseAndSetOperations(final Element ctxElement, final ContextIF context) throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * Process all of the Operation Elements within a given Context.
       * Each Operation can have Properties, Actions and a Query.
       * Each Operation will inherit the Context Properties and Query, then
       * the Operation will be checked for it's own Properties and Query.
       *
       * If the Operation has Properties, they will be combined with the Context
       * Properties.
       * If the Operation has a Query, then it will be used instead of the
       * Context Query.
       *
       * The Service, Association and AttrGroup will be processed
       *
       * The Operation will be added to the Service (defined in the Context).
       */
//         <Operations>
//            <Operation id="create" attrgroup="person-create">
//               <Properties>
//                  <Property name="operation.classname" value="org.openptk.provision.spi.operations.SpmlSunOperations"/>
//                  <Property name="key"         value="${uniqueid}"/>
//                  <Property name="objectclass" value="user"/>
//               </Properties>
//               <Query type="AND">
//                  <Query type="EQ" name="MemberObjectGroups" value="All People"/>
//                  <Query type="EQ" name="objectclass" value="user"/>
//               </Query>
//               <Actions>
//                  <Action id="ifexists" type="pre"/>
//               </Actions>
//            </Operation>
//            <Operation id="read" attrgroup="person-read" association="JNDI" connection="OpenDS">
//               <Properties>
//                  <Property name="operation.classname" value="org.openptk.provision.spi.operations.JndiOperations"/>
//                  <Property name="key"         value="uid=${uniqueid},ou=People,%{basedn}"/>
//                  <Property name="rdn"         value="uid=${uniqueid}"/>
//                  <Property name="basedn"      value="ou=People,%{basedn}"/>
//                  <Property name="objectclass" value="inetOrgPerson"/>
//                  <Property name="timeout"     value="%{timeout.read}"/>
//               </Properties>
//               <Query type="EQ" name="objectClass" value="inetOrgPerson"/>
//            </Operation>
//            ...
//         <Operations>

      int timeout = XMLConfig.VALUE_OPERATION_TIMEOUT;
      int listlen = 0;
      String METHOD_NAME = CLASS_NAME + ":parseAndSetOperations() ";
      String key = null;
      String operClassname = null;
      String contextId = null;
      String operId = null;
      String connectionName = null;
      String associationName = null;
      String attrgroupName = null;
      String sortProp = null;
      String sortAttr = null;
      String msg = null;
      StringTokenizer strTok = null;
      Properties ctxProps = null;
      Properties connProps = null;
      Properties operProps = null;
      Properties mergeProps = null;
      List<String> sortList = null;
      Element operElement = null; //             DOM
      Element queryElement = null; //            DOM
      NodeList operNL = null; //                 DOM
      ComponentIF association = null; // OpenPTK
      ComponentIF attrgroup = null; //   OpenPTK
      ServiceIF service = null; //       OpenPTK
      Operation oper = null; //          OpenPTK
      Operation[] operArray = null; //   OpenPTK
      Query query = null; //             OpenPTK
      OperationsIF operation = null; //  OpenPTK
      DefinitionIF definition = null; // OpenPTK

      operArray = Operation.values();
      contextId = context.getUniqueId().toString();
      ctxProps = context.getProperties();
      service = context.getService();
      definition = context.getDefinition();

      operNL = ctxElement.getElementsByTagName(XMLConfig.ELEM_NAME_OPERATION);
      listlen = operNL.getLength();

      /*
       * Process each declared operation
       */

      for (int i = 0; i < listlen; i++)
      {
         oper = null;
         operElement = null;
         operId = null;
         operProps = null;
         query = null;
         operElement = (Element) operNL.item(i);
         if (operElement != null)
         {
            operProps = this.getElementProperties(operElement);

            operId = operElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
            if (operId != null && operId.length() > 0)
            {
               /*
                * Match the operId to a Operation
                * CREATE, READ, UPDATE, DELETE, SEARCH, etc.
                */

               for (int j = 0; j < operArray.length; j++)
               {
                  if (operArray[j].toString().equalsIgnoreCase(operId))
                  {
                     oper = operArray[j];
                     break;
                  }
               }
               if (oper == null)
               {
                  this.handleError(METHOD_NAME
                     + "A valid Operation was not found for id='" + operId
                     + "' in Context '" + contextId + "'");
               }

               /*
                * Get the Connection related to the Operation
                * Check the "Operation" element first, then check the "Context"
                */

               connectionName = operElement.getAttribute(XMLConfig.ELEM_ATTR_CONNECTION);

               if (connectionName == null || connectionName.length() < 1)
               {
                  connectionName = ctxElement.getAttribute(XMLConfig.ELEM_ATTR_CONNECTION);
               }

               if (connectionName != null && connectionName.length() > 0)
               {
                  if (_connectionprops.containsKey(connectionName))
                  {
                     connProps = _connectionprops.get(connectionName);
                  }
                  else
                  {
                     this.handleError(METHOD_NAME + "Context '"
                        + contextId + "', Operation=" + oper.toString()
                        + ", Connection '" + connectionName + "' was not found");
                  }
               }
               else
               {
                  this.handleError(METHOD_NAME + "Context '"
                     + contextId + "', Operation=" + oper.toString()
                     + ", does not have a Connection");
               }

               /*
                * Get the Association
                * Check the "operation" element first, then check the "context"
                */

               associationName = operElement.getAttribute(XMLConfig.ELEM_ATTR_ASSOCIATION);

               if (associationName == null || associationName.length() < 1)
               {
                  associationName = ctxElement.getAttribute(XMLConfig.ELEM_ATTR_ASSOCIATION);
               }

               if (associationName != null && associationName.length() > 0)
               {
                  if (_associations.containsKey(associationName))
                  {
                     association = _associations.get(associationName);
                     try
                     {
                        this.checkDefinitionAssociation(definition.getAttributes(), association.getAttributes());
                     }
                     catch (ConfigurationException ex)
                     {
                        this.handleError(METHOD_NAME + "Association Attribute not found in Definition"
                           + ": Context='" + contextId
                           + "', Operation='" + oper.toString()
                           + "', Association='" + associationName
                           + "', Definition='"
                           + (definition.getUniqueId() != null ? definition.getUniqueId().toString() : "(null)")
                           + "', " + ex.getMessage());
                     }
                     service.setAssociation(oper, association);
                  }
                  else
                  {
                     this.handleError(METHOD_NAME + "Context '" + contextId
                        + "', Operation=" + oper.toString()
                        + ", Association is empty/null");
                  }
               }
               else
               {
                  this.handleError(METHOD_NAME + "Context '" + contextId
                     + "', Operation=" + oper.toString()
                     + ", does not have an Association");
               }

               /*
                * Combine the Context -> Connection -> Operation Properties
                */

               if (connProps != null)
               {
                  mergeProps = this.combineProperties(connProps, ctxProps); // ctxProps, connProps
               }
               else
               {
                  mergeProps = ctxProps;
               }

               if (operProps != null)
               {
                  mergeProps = this.combineProperties(mergeProps, operProps);
               }

               /*
                * Set the Operation properties in the Service (oper specific)
                */

               service.setOperProps(oper, mergeProps);

               /*
                * Get the key for the service, for the specific operation
                */

               key = mergeProps.getProperty(XMLConfig.PROP_KEY);

               if (key == null || key.length() < 1)
               {
                  this.handleError(METHOD_NAME
                     + " Context='" + contextId + "', Operation='" + operId
                     + "', Key is null");
               }

               service.setKey(oper, key);

               /*
                * Get the Operation classname and instanciate the OperationIF
                * Follow this order for obtaining the classname:
                * 1: Check the Operation Properties (operProps)
                * 2: Check the Context Properties (ctxProps)
                */

               operClassname = mergeProps.getProperty(XMLConfig.PROP_OPERATION_CLASSNAME);

               if (operClassname == null || operClassname.length() < 1)
               {
                  this.handleError(METHOD_NAME
                     + "Operation '" + operId + "' for Context '" + contextId
                     + "' does not have a classname");
               }

               /*
                * Check the "operation" cache (Map in the Context) before
                * instantiating a new one, the classname is the key in the cache
                */

               if (context.hasOperation(operClassname))
               {
                  operation = context.getOperation(operClassname);
               }
               else
               {
                  try
                  {
                     operation = (OperationsIF) instantiateClass(operClassname);
                  }
                  catch (ConfigurationException ex)
                  {
                     operation = null;
                     msg = "Disabling Context:"
                        + " Context='" + contextId + "', Operation='" + operId
                        + "' " + ex.getMessage();
                     Logger.logWarning(METHOD_NAME + msg);
                     context.setStatus(msg);
                     context.setState(State.DISABLED);
                     context.setError(true);
                  }

                  if (operation != null)
                  {
                     operation.setDebug(this.isDebug());
                     operation.setDebugLevel(this.getDebugLevel());
                     operation.setProperties(mergeProps);
                     operation.setService(service);

                     /*
                      * Run the Operations "startup" method, check for errors
                      * Only if the Context State = READY
                      */

                     if (context.getState() == State.READY)
                     {
                        operation.startup();
                     }

                     if (operation.isError())
                     {
                        switch (operation.getState())
                        {
                           case ERROR:
                              Logger.logError(operation.getStatus());
                              break;
                           case FAILED:
                              Logger.logWarning(operation.getStatus());
                              break;
                           default:
                              Logger.logError(operation.getStatus());
                              break;
                        }

                        context.setError(true);
                        context.setStatus(operation.getStatus());
                        context.setState(operation.getState());
                     }

                     /*
                      * Put the Operation in the Context's "cache"
                      */

                     context.setOperation(operClassname, operation);
                  }

               } // END if: had to instanciate the "operation" object

               /*
                * Get the AttrGroup
                */

               attrgroupName = operElement.getAttribute(XMLConfig.ELEM_ATTR_ATTRGROUP);
               if (attrgroupName != null && attrgroupName.length() > 0)
               {
                  if (_attrgroups.containsKey(attrgroupName))
                  {
                     attrgroup = _attrgroups.get(attrgroupName);
                     service.setAttrGroup(oper, attrgroup);
                  }
                  else
                  {
                     this.handleError(METHOD_NAME + "Context '" + contextId
                        + "' for Operation '" + operId
                        + "' AttrGroup Property is empty/null");
                  }
               }
               else
               {
                  this.handleError(METHOD_NAME + "Context '" + contextId
                     + "' for Operation '" + operId
                     + "' does not have an AttrGroup Property");
               }

               /*
                * Set the service's timeouts for the operation
                */

               try
               {
                  timeout = Integer.parseInt(mergeProps.getProperty(XMLConfig.PROP_TIMEOUT));
               }
               catch (NumberFormatException ex)
               {
                  timeout = XMLConfig.VALUE_OPERATION_TIMEOUT;
               }

               service.setTimeout(oper, timeout);

               /*
                * Get the Query (if it has one), then check the Context for one
                */

               queryElement = getElement(operElement, XMLConfig.ELEM_NAME_QUERY);
               if (queryElement != null)
               {
                  query = this.getQuery(queryElement);
               }
               if (query == null)
               {
                  query = context.getQuery();
               }

               if (query != null)
               {
                  service.setQuery(oper, query);
               }

               /*
                * Check for the "sort" property and set it if it exists
                */

               sortProp = mergeProps.getProperty(XMLConfig.PROP_SORT);

               if (sortProp != null && sortProp.length() > 0)
               {
                  strTok = new StringTokenizer(sortProp, ",");
                  sortList = new LinkedList<String>();
                  while (strTok.hasMoreTokens())
                  {
                     sortAttr = strTok.nextToken();
                     if (sortAttr != null)
                     {
                        sortList.add(sortAttr);
                     }
                  }
                  service.setSortAttributes(sortList);
                  context.setSort(true);
               }

               /*
                * Build the Attributes for the service
                */

               this.setServiceAttributes(oper, service, definition);

               /*
                * Add the Operation to the Service
                */

               service.setOperation(oper, operation);

               /*
                * Check for Actions
                */

               this.parseAndSetOperActions(oper, operElement, context);
            }
            else
            {
               this.handleError(METHOD_NAME + "An Operation id is NULL");
            }
         }
         else
         {
            this.handleError(METHOD_NAME + "Operation is NULL");
         }
      } // END for(;;)

      return;
   }

   //----------------------------------------------------------------
   private void parseAndSetAssignments(final Element ctxElement, final ContextIF context) throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * Process all the Assignment elements in a given Context
       */
//         <Assignments>
//            <Assignment id="http-hdr-attrmap" description="HTTP Header for SCIM map">
//               <Source      type="http-header" name="X-OPENPTK-ATTRMAP" value="SCIM-USER-1.0"/>
//               <Destination type="attrmap"     name="scim.user-1.0-consumer"/>
//            </Assignment>
//            <Assignment id="http-hdr-alias-url" description="re-write URL for responses">
//               <Source      type="http-header" name="X-OPENPTK-ALIAS-URL"/>
//               <Destination type="property"    name="response.url"/>
//            </Assignment>
//         </Assignments>

      int asglen = 0;
      int srclen = 0;
      int dstlen = 0;
      String METHOD_NAME = CLASS_NAME + ":parseAndSetAssignments() ";
      String asgId = null;
      String asgDesc = null;
      String type = null;
      String name = null;
      String value = null;
      NodeList asgNL = null; //     DOM
      NodeList srcNL = null; //     DOM
      NodeList dstNL = null; //     DOM
      Element asgElement = null; // DOM
      Element srcElement = null; // DOM
      Element dstElement = null; // DOM
      ComponentIF srcComp = null; //     OpenPTK
      ComponentIF dstComp = null; //     OpenPTK
      AssignmentIF assignment = null; // OpenPTK

      asgNL = ctxElement.getElementsByTagName(XMLConfig.ELEM_NAME_ASSIGNMENT);
      asglen = asgNL.getLength();

      /*
       * process each assignment
       */

      for (int i = 0; i < asglen; i++)
      {
         asgElement = (Element) asgNL.item(i);
         if (asgElement != null)
         {
            asgId = asgElement.getAttribute(XMLConfig.ELEM_ATTR_ID);

            if (asgId == null || asgId.length() < 1)
            {
               this.handleError(METHOD_NAME
                  + "Context='" + context.getUniqueId().toString()
                  + "', Assignment does not have an 'id' Attribute");
            }
            if (context.getAssignment(asgId) != null)
            {
               this.handleError(METHOD_NAME
                  + "Context='" + context.getUniqueId().toString()
                  + "', Assignment 'id' must be unique, id='" + asgId + "' already exists.");
            }

            asgDesc = asgElement.getAttribute(XMLConfig.ELEM_ATTR_DESCRIPTION);

            assignment = null;
            /*
             * process the source 
             */

            srcNL = asgElement.getElementsByTagName(XMLConfig.ELEM_NAME_SOURCE);
            srclen = srcNL.getLength();

            for (int j = 0; j < srclen; j++)
            {
               type = name = value = null;
               srcComp = null;
               srcElement = (Element) srcNL.item(j);
               if (srcElement != null)
               {
                  srcComp = new Component();

                  type = srcElement.getAttribute(XMLConfig.ELEM_ATTR_TYPE);
                  if (type != null && type.length() > 0)
                  {
                     for (AssignmentIF.Type asgType : _assignmentTypes)
                     {
                        if (type.equalsIgnoreCase(asgType.toString()))
                        {
                           srcComp.setState(State.VALID);
                           break;
                        }
                     }
                     if (srcComp.getState() == State.VALID)
                     {
                        srcComp.setProperty(StructureIF.NAME_TYPE, type);
                     }
                     else
                     {
                        this.handleError(METHOD_NAME
                           + "Context='" + context.getUniqueId().toString()
                           + "', Assignment='" + asgId
                           + "', Source has an invalid type: '" + type + "'");
                     }
                  }
                  else
                  {
                     this.handleError(METHOD_NAME
                        + "Context='" + context.getUniqueId().toString()
                        + "', Assignment='" + asgId
                        + "', Source has a null/empty type");
                  }

                  name = srcElement.getAttribute(XMLConfig.ELEM_ATTR_NAME);
                  if (name != null && name.length() > 0)
                  {
                     srcComp.setProperty(StructureIF.NAME_NAME, name);
                  }
                  else
                  {
                     this.handleError(METHOD_NAME
                        + "Context='" + context.getUniqueId().toString()
                        + "', Assignment='" + asgId
                        + "', Source has a null/empty name");
                  }

                  value = srcElement.getAttribute(XMLConfig.ELEM_ATTR_VALUE);
                  if (value != null && value.length() > 0)
                  {
                     srcComp.setProperty(StructureIF.NAME_VALUE, value);
                  }
               }
               else
               {
                  this.handleError(METHOD_NAME
                     + "Context='" + context.getUniqueId().toString()
                     + "', Assignment='" + asgId
                     + "', Source is NULL");
               }
            }

            /*
             * process the destination
             */

            dstNL = asgElement.getElementsByTagName(XMLConfig.ELEM_NAME_DESTINATION);
            dstlen = dstNL.getLength();

            for (int j = 0; j < dstlen; j++)
            {
               type = name = value = null;
               dstComp = null;
               dstElement = (Element) dstNL.item(j);
               if (dstElement != null)
               {
                  dstComp = new Component();

                  type = dstElement.getAttribute(XMLConfig.ELEM_ATTR_TYPE);
                  if (type != null && type.length() > 0)
                  {
                     for (AssignmentIF.Type asgType : _assignmentTypes)
                     {
                        if (type.equalsIgnoreCase(asgType.toString()))
                        {
                           dstComp.setState(State.VALID);
                           break;
                        }
                     }
                     if (dstComp.getState() == State.VALID)
                     {
                        dstComp.setProperty(StructureIF.NAME_TYPE, type);
                     }
                     else
                     {
                        this.handleError(METHOD_NAME
                           + "Context='" + context.getUniqueId().toString()
                           + "', Assignment='" + asgId
                           + "', Destination has an invalid type: '" + type + "'");
                     }
                  }
                  else
                  {
                     this.handleError(METHOD_NAME
                        + "Context='" + context.getUniqueId().toString()
                        + "', Assignment='" + asgId
                        + "', Destination has a null/empty type");
                  }

                  name = dstElement.getAttribute(XMLConfig.ELEM_ATTR_NAME);
                  if (name != null && name.length() > 0)
                  {
                     dstComp.setProperty(StructureIF.NAME_NAME, name);
                  }
                  else
                  {
                     this.handleError(METHOD_NAME
                        + "Context='" + context.getUniqueId().toString()
                        + "', Assignment='" + asgId
                        + "', Destination has a null/empty name");
                  }

                  value = dstElement.getAttribute(XMLConfig.ELEM_ATTR_VALUE);
                  if (value != null && value.length() > 0)
                  {
                     dstComp.setProperty(StructureIF.NAME_VALUE, value);
                  }
               }
               else
               {
                  this.handleError(METHOD_NAME
                     + "Context='" + context.getUniqueId().toString()
                     + "', Assignment='" + asgId
                     + "', Destination is NULL");
               }
            }

            assignment = new BasicAssignment(srcComp, dstComp);
            assignment.setUniqueId(asgId);

            if (asgDesc != null && asgDesc.length() > 0)
            {
               assignment.setDescription(asgDesc);
            }

            context.setAssignment(asgId, assignment);
         }
         else
         {
            this.handleError(METHOD_NAME
               + "Context='" + context.getUniqueId().toString()
               + "', Assignment is NULL");
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void parseAndSetOperActions(final Operation oper, final Element operElement, final ContextIF context) throws ConfigurationException
   //----------------------------------------------------------------
   {
//            <Operation id="create" attrgroup="person-create">
//               ...
//               <Actions>
//                  <Action id="ifexists" type="pre"/>
//               </Actions>
//            </Operation>

      int listlen = 0;
      String METHOD_NAME = CLASS_NAME + ":parseAndSetOperActions() ";
      String actionId = null;
      String actionMode = null;
      NodeList nodelist = null;
      Element actionElement = null;
      ActionIF action = null;
      ActionMode mode = ActionMode.NONE;

      nodelist = operElement.getElementsByTagName(XMLConfig.ELEM_NAME_ACTION);
      listlen = nodelist.getLength();

      for (int i = 0; i < listlen; i++)
      {
         actionElement = (Element) nodelist.item(i);

         actionId = actionElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
         if (actionId != null && actionId.length() > 0)
         {
            /*
             * Get the Action from the pre-loaded Map
             * Get a new instance of it
             * Copy over the Properties
             */

            if (_actions.containsKey(actionId))
            {
               try
               {
                  action = (ActionIF) _actions.get(actionId).getClass().newInstance();
               }
               catch (InstantiationException ex)
               {
                  this.handleError(METHOD_NAME + "Could not copy Action '"
                     + actionId
                     + "', Context='"
                     + (context.getUniqueId() != null ? context.getUniqueId().toString() : "(null)")
                     + "', Operation='"
                     + oper.toString() + "', " + ex.getMessage());
               }
               catch (IllegalAccessException ex)
               {
                  this.handleError(METHOD_NAME + "Could not copy Action '"
                     + actionId
                     + "', Context='"
                     + (context.getUniqueId() != null ? context.getUniqueId().toString() : "(null)")
                     + "', Operation='"
                     + oper.toString() + "', " + ex.getMessage());
               }
            }
            else
            {
               this.handleError(METHOD_NAME + "Action '" + actionId
                  + "' does not exist, Context='"
                  + (context.getUniqueId() != null ? context.getUniqueId().toString() : "(null)")
                  + "', Operation='" + oper.toString() + "'");
            }

            /*
             * Get the mode
             */

            actionMode = actionElement.getAttribute(XMLConfig.ELEM_ATTR_MODE);
            if (actionMode != null && actionMode.length() > 0)
            {
               if (actionMode.equalsIgnoreCase(ActionMode.PRE.toString()))
               {
                  mode = ActionMode.PRE;
               }
               else if (actionMode.equalsIgnoreCase(ActionMode.POST.toString()))
               {
                  mode = ActionMode.POST;
               }
               else if (actionMode.equalsIgnoreCase(ActionMode.BOTH.toString()))
               {
                  mode = ActionMode.BOTH;
               }
               else if (actionMode.equalsIgnoreCase(ActionMode.NONE.toString()))
               {
                  // do nothing, essentially disabling the action
               }
               else
               {
                  this.handleError(METHOD_NAME + "Action '" + actionId
                     + "' has an invalid mode '" + actionMode
                     + "', Context ='"
                     + (context.getUniqueId() != null ? context.getUniqueId().toString() : "(null)")
                     + "', Operation='" + oper.toString() + "'");
               }
            }
            else
            {
               this.handleError(METHOD_NAME + "Context '"
                  + (context.getUniqueId() != null ? context.getUniqueId().toString() : "(null)")
                  + "', Operation '" + oper.toString() + "' has an Action with a null 'type'");
            }

            action.setMode(mode);

            /*
             * Get template action Properties, oper Properties, combine them
             */

            action.setProperties(this.combineProperties(
               _actions.get(actionId).getProperties(),
               this.getElementProperties(actionElement)));

            action.setUniqueId(actionId);

            try
            {
               action.startup();
            }
            catch (ActionException ex)
            {
               this.handleError(METHOD_NAME + ex.getMessage());
            }

            action.setContext(context);

            /*
             * Add the Action to the Context
             */

            context.addAction(oper, action);
         }
         else
         {
            this.handleError(METHOD_NAME + "Context '"
               + (context.getUniqueId() != null ? context.getUniqueId().toString() : "(null)")
               + "', Operation '" + oper.toString() + "' has an Action with a null 'id'");
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void parseAndSetAttrGroup(final Element elements, final ComponentIF comp)
   //----------------------------------------------------------------
   {
      /*
       * Process each Attribute and addDatum it to the Component
       * These Attributes, for a given AttrGroup, are used to "group"
       * together attributes that can be referenced by an Operation.
       * The Attribute "id" is the OpenPTK attribute name.
       * When an Operation references an AttrGroup, it can only perform
       * it's processing with the Attributes defined within the AttrGroup
       */

//      <AttrGroup id="person-create">
//         <Attributes>
//            <Attribute id="firstname"/>
//            <Attribute id="lastname"/>
//            <Attribute id="email"/>
//            <Attribute id="fullname"/>
//            <Attribute id="roles"/>
//            <Attribute id="manager"/>
//            <Attribute id="title"/>
//            <Attribute id="resources"/>
//            <Attribute id="telephone"/>
//            <Attribute id="organization"/>
//         </Attributes>
//      </AttrGroup>

      int listlen = 0;
      NodeList nodelist = null;
      Element element = null;
      String id = null;
      AttrIF attr = null;  // OpenPTK

      nodelist = elements.getElementsByTagName(XMLConfig.ELEM_NAME_ATTRIBUTE);
      if (nodelist != null)
      {
         listlen = nodelist.getLength();

         for (int i = 0; i < listlen; i++)
         {
            element = (Element) nodelist.item(i);
            if (element != null)
            {
               id = element.getAttribute(XMLConfig.ELEM_ATTR_ID);
               attr = new BasicAttr(id);
               comp.setAttribute(id, attr);
            }
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private void parseAndSetAttrMap(final Element attrmapElement, final AttrMapIF attrMap) throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * Parse each Attribute in the given AttrMap
       */
//      <AttrMap id="scim-user-1.0" classname="org.openptk.config.attribute.BasicAttrMap">
//         <Attributes>
//            <Attribute ... >
//               <Mode>
//                  <Processes>
//                  </Processes>
//                  <SubAttributes>
//                  </SubAttributes>
//                  <Data>
//                  </Data>
//               </Mode>
//            </Attribute>
//         </Attributes>
//      </AttrMap>

      int listlen = 0;
      int modelen = 0;
      int childlen = 0;
      boolean required = false;
      boolean multivalued = false;
      boolean isIgnore = false;
      boolean hasProcesses = false;
      boolean hasSubAttributes = false;
      boolean hasData = false;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String attrId = null;
      String modeId = null;
      String mapto = null;
      String dataType = null;
      String childName = null;
      String dataId = null;
      Node modeNode = null;
      Node childNode = null;
      NodeList attrNodeList = null;
      NodeList childNodeList = null;
      NodeList modeList = null;
      NodeList processList = null;
      NodeList subattrList = null;
      NodeList datumList = null;
      NodeList matchList = null;
      Element attrElement = null;
      Element modeElement = null;
      Element childElement = null;
      ExternalAttrIF extAttr = null; //       OpenPTK
      ExternalAttrIF.Mode attrMode = null; // OpenPTK 
      ExternalModeIF extMode = null; // OpenPTK
      Processes processes = null; //    OpenPTK
      SubAttributes subAttrs = null; // OpenPTK
      Data data = null; //              OpenPTK

      attrNodeList = attrmapElement.getElementsByTagName(ELEM_NAME_ATTRIBUTE);
      if (attrNodeList != null)
      {
         listlen = attrNodeList.getLength();
         for (int i = 0; i < listlen; i++)
         {
            attrElement = (Element) attrNodeList.item(i);
            if (attrElement != null)
            {
               attrId = attrElement.getAttribute(ELEM_ATTR_ID); // required
               if (attrId != null && attrId.length() > 0)
               {
                  extAttr = new ExternalAttr(attrId);

                  required = false;
                  required = Boolean.parseBoolean(attrElement.getAttribute(ELEM_ATTR_REQUIRED)); // true / false (default)
                  if (required)
                  {
                     extAttr.setRequired(true);
                  }

                  mapto = attrElement.getAttribute(ELEM_ATTR_MAPTO); // optional
                  if (mapto != null && mapto.length() > 0)
                  {
                     extAttr.setMapTo(mapto);
                  }

                  multivalued = false;
                  multivalued = Boolean.parseBoolean(attrElement.getAttribute(ELEM_ATTR_MULTIVALUED)); // true / false (default)
                  if (multivalued)
                  {
                     extAttr.setAllowMultivalue(true);
                  }

                  dataType = attrElement.getAttribute(ELEM_ATTR_TYPE); // optional, string (default)

                  if (dataType != null && dataType.length() > 0)
                  {
                     extAttr.setType(this.getDataTypeFromString(dataType));
                  }

                  /*
                   * Process both of the possible "modes": INBOUND, OUTBOUND
                   * If both are set, the mode will be BOTH
                   * If neither is set, the mode will be IGNORE
                   * 
                   * NOTE: If an IGNORE mode is found for a given attribute,
                   * it will negate all other modes
                   */

                  isIgnore = false;
                  modeList = attrElement.getElementsByTagName(ELEM_NAME_MODE);
                  if (modeList != null && modeList.getLength() > 0)
                  {
                     modelen = modeList.getLength();
                     for (int j = 0; j < modelen; j++)
                     {
                        modeNode = modeList.item(j);
                        if (modeNode != null)
                        {
                           modeElement = (Element) modeNode;
                           modeId = modeElement.getAttribute(ELEM_ATTR_ID); // required
                           if (modeId != null && modeId.length() > 0)
                           {
                              attrMode = this.getAttrModeFromString(modeId);
                              if (attrMode == null)
                              {
                                 this.handleError(METHOD_NAME + "AttrMap '" + attrMap.getUniqueId() + "', "
                                    + "Attribute '" + attrId + "', "
                                    + "has an invalid mode: '" + modeId + "'");
                              }

                              if (attrMode == ExternalAttrIF.Mode.IGNORE)
                              {
                                 isIgnore = true;
                              }

                              if (isIgnore)
                              {
                                 /*
                                  * set Inbound and OutBound to null 
                                  * (could have been previously set)
                                  */

                                 extAttr.setMode(ExternalAttrIF.Mode.IGNORE);
                                 extAttr.setInbound(null);
                                 extAttr.setOutbound(null);
                              }
                              else
                              {
                                 extMode = new ExternalMode(attrMode); // default mode = BOTH

                                 /* 
                                  * mode is either "inbound" or "outbound" or "both"
                                  * process the child nodes which could be:
                                  * Processes, SubAttributes, Data
                                  */

                                 hasProcesses = false;
                                 hasSubAttributes = false;
                                 hasData = false;

                                 childNodeList = modeElement.getChildNodes();
                                 if (childNodeList != null)
                                 {
                                    childlen = childNodeList.getLength();
                                    for (int k = 0; k < childlen; k++)
                                    {
                                       childNode = childNodeList.item(k);
                                       if (childNode != null && childNode.getNodeType() == Node.ELEMENT_NODE)
                                       {
                                          childElement = (Element) childNode;
                                          childName = childElement.getNodeName();
                                          if (childName != null && childName.length() > 0)
                                          {
                                             if (childName.equalsIgnoreCase(ELEM_NAME_PROCESSES))
                                             {
                                                /*
                                                 * Processes
                                                 */
                                                processList = null;
                                                processList = childElement.getElementsByTagName(ELEM_NAME_PROCESS);
                                                if (processList != null && processList.getLength() > 0)
                                                {
                                                   if (!hasSubAttributes && !hasData)
                                                   {
                                                      processes = new Processes();
                                                      this.parseAndSetAttrMapProcesses(processList, processes);
                                                      extMode.setProcesses(processes);
                                                      extMode.setKind(ExternalModeIF.Kind.PROCESS);
                                                      hasProcesses = true;
                                                   }
                                                   else
                                                   {
                                                      this.handleError(METHOD_NAME + "AttrMap '" + attrMap.getUniqueId() + "', "
                                                         + "Attribute '" + attrId + "', Mode '" + modeId + "', "
                                                         + "can not have Processes, already contains either: SubAttributes or Data");
                                                   }
                                                }

                                             }
                                             else if (childName.equalsIgnoreCase(ELEM_NAME_SUBATTRIBUTES))
                                             {
                                                /*
                                                 * SubAttributes
                                                 */
                                                subattrList = null;
                                                subattrList = childElement.getElementsByTagName(ELEM_NAME_SUBATTRIBUTE);
                                                if (subattrList != null && subattrList.getLength() > 0)
                                                {
                                                   if (!hasProcesses && !hasData)
                                                   {
                                                      subAttrs = new SubAttributes();
                                                      this.parseAndSetAttrMapSubAttributes(subattrList, subAttrs);
                                                      extMode.setSubAttributes(subAttrs);
                                                      extMode.setKind(ExternalModeIF.Kind.SUBATTR);
                                                      hasSubAttributes = true;
                                                   }
                                                   else
                                                   {
                                                      this.handleError(METHOD_NAME + "AttrMap '" + attrMap.getUniqueId() + "', "
                                                         + "Attribute '" + attrId + "', Mode '" + modeId + "', "
                                                         + "' can not have SubAttributes, already contains either: Processes or Data");
                                                   }
                                                }
                                             }
                                             else if (childName.equalsIgnoreCase(ELEM_NAME_DATA))
                                             {
                                                /*
                                                 * Data
                                                 */
                                                dataId = null;
                                                dataId = childElement.getAttribute(ELEM_ATTR_ID);
                                                if (dataId == null || dataId.length() < 1)
                                                {
                                                   this.handleError(METHOD_NAME + "Attribute '" + attrId
                                                      + "' has a Data Element that is missing the 'id' Attribute");
                                                }
                                                datumList = null;
                                                matchList = null;
                                                datumList = childElement.getElementsByTagName(ELEM_NAME_DATUM);
                                                matchList = childElement.getElementsByTagName(ELEM_NAME_MATCH);
                                                if (datumList != null && datumList.getLength() > 0)
                                                {
                                                   if (!hasProcesses && !hasSubAttributes)
                                                   {
                                                      data = new Data(dataId);
                                                      this.parseAndSetAttrMapData(datumList, matchList, data);
                                                      extMode.setData(data);
                                                      extMode.setKind(ExternalModeIF.Kind.DATA);
                                                      hasData = true;
                                                   }
                                                   else
                                                   {
                                                      this.handleError(METHOD_NAME + "AttrMap '" + attrMap.getUniqueId() + "', "
                                                         + "Attribute '" + attrId + "', Mode '" + modeId + "', "
                                                         + "' can not have Data, already contains either: Processes or SubAttributes");
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }

                                 switch (attrMode)
                                 {
                                    case INBOUND:
                                       extAttr.setMode(ExternalAttrIF.Mode.INBOUND);
                                       extAttr.setInbound(extMode);
                                       extAttr.setOutbound(null);
                                       break;
                                    case OUTBOUND:
                                       extAttr.setMode(ExternalAttrIF.Mode.OUTBOUND);
                                       extAttr.setInbound(null);
                                       extAttr.setOutbound(extMode);
                                       break;
                                    case BOTH:
                                       extAttr.setMode(ExternalAttrIF.Mode.BOTH);
                                       extAttr.setInbound(extMode);
                                       extAttr.setOutbound(extMode);
                                       break;
                                 }

                              }
                           }
                           else
                           {
                              this.handleError(METHOD_NAME + "Mode has a null id, Attribute='" + attrId + "'");
                           }
                        }
                     }
                  }
                  else
                  {
                     this.handleError(METHOD_NAME + "AttrMap '" + attrMap.getUniqueId() + "', "
                        + "Attribute '" + attrId + "', "
                        + "Must define a mode: inbound, outbound, both, ignore'");
                  }

                  attrMap.setAttribute(extAttr);
               }
               else
               {
                  this.handleError(METHOD_NAME + "AttrMap '" + attrMap.getUniqueId() + "', "
                     + "Attribute has a null id");
               }
            }
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private void parseAndSetAttrMapProcesses(final NodeList nodelist, final Processes processes)
   //----------------------------------------------------------------
   {
//                  <Processes>
//                     <Process value="urn:scim:schemas:core:1.0"/>
//                  </Processes>

      int listlen = 0;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String value = null;
      Element element = null;
      ExternalAttrIF.Mode mode = null;

      listlen = nodelist.getLength();
      for (int i = 0; i < listlen; i++)
      {
         element = (Element) nodelist.item(i);
         if (element != null)
         {
            value = element.getAttribute(ELEM_ATTR_VALUE);
            if (value != null && value.length() > 0)
            {
               processes.setValue(value);
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void parseAndSetAttrMapSubAttributes(final NodeList nodelist, final SubAttributes subattrs) throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * The nodelist should contain a list of <SubAttribute> Elements
       */
//               <SubAttributes>
//                  <SubAttribute id="formatted">
//                     <Processes>
//                        <Process mode="producer" value="${firstname} ${lastname}"/>
//                     </Processes>
//                  </SubAttribute>
//                  <SubAttribute id="familyName" mapto="lastname" required="true"/>
//                  <SubAttribute id="givenName" mapto="firstname" required="true"/>
//                  <SubAttribute id="middleName"/>
//                  <SubAttribute id="honorificPrefix" mapto="title"/>
//                  <SubAttribute id="honorificSuffix" mode="ignore"/>
//               </SubAttributes>

      boolean required = false;
      boolean multivalued = false;
      int listlen = 0;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String id = null;
      String mapto = null;
      String type = null;
      String dataId = null;
      Element element = null;
      Element dataElement = null;
      NodeList nodeList = null;
      NodeList datumList = null;
      NodeList matchList = null;
      ExternalSubAttrIF subattr = null; // OpenPTK
      Processes processes = null; // OpenPTK
      Data data = null; // OpenPTK

      listlen = nodelist.getLength();
      for (int i = 0; i < listlen; i++)
      {
         element = (Element) nodelist.item(i);
         if (element != null)
         {
            id = element.getAttribute(ELEM_ATTR_ID); // required
            if (id != null && id.length() > 0)
            {
               subattr = new ExternalSubAttr(id);

               required = false;
               required = Boolean.parseBoolean(element.getAttribute(ELEM_ATTR_REQUIRED)); // true / false (default)
               if (required)
               {
                  subattr.setRequired(true);
               }

               mapto = element.getAttribute(ELEM_ATTR_MAPTO); // optional ... default to "id"
               if (mapto != null && mapto.length() > 0)
               {
                  subattr.setMapTo(mapto);
               }
               else
               {
                  subattr.setMapTo(id);
               }

               multivalued = false;
               multivalued = Boolean.parseBoolean(element.getAttribute(ELEM_ATTR_MULTIVALUED)); // true / false (default)
               if (multivalued)
               {
                  subattr.setAllowMultivalue(true);
               }

               type = element.getAttribute(ELEM_ATTR_TYPE); // optional
               // string (default)
               if (type != null && type.length() > 0)
               {
                  subattr.setType(this.getDataTypeFromString(type));
               }

               /*
                * Processes
                */

               processes = null;
               nodeList = element.getElementsByTagName(ELEM_NAME_PROCESS);
               if (nodeList != null && nodeList.getLength() > 0)
               {
                  processes = new Processes();
                  this.parseAndSetAttrMapProcesses(nodeList, processes);
                  subattr.setProcesses(processes);
               }

               /*
                * Data
                */

               nodeList = null;
               nodeList = element.getElementsByTagName(ELEM_NAME_DATA);
               if (nodeList != null && nodeList.getLength() > 0)
               {
                  dataElement = (Element) nodeList.item(0);
                  if (dataElement != null)
                  {
                     dataId = null;
                     dataId = dataElement.getAttribute(ELEM_ATTR_ID);
                     if (dataId == null || dataId.length() < 1)
                     {
                        this.handleError(METHOD_NAME + "SubAttribute '" + id
                           + "' has a Data Element that is missing the 'id' Attribute");
                     }

                     datumList = null;
                     matchList = null;
                     datumList = element.getElementsByTagName(ELEM_NAME_DATUM);
                     matchList = element.getElementsByTagName(ELEM_NAME_MATCH);
                     if (datumList != null && datumList.getLength() > 0)
                     {
                        data = new Data(dataId);
                        this.parseAndSetAttrMapData(datumList, matchList, data);
                        subattr.setData(data);
                     }
                  }
               }
               subattrs.add(id, subattr);
            }
            else
            {
               this.handleError(METHOD_NAME + "SubAttribute has a null id");
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void parseAndSetAttrMapData(final NodeList datalist, final NodeList matchlist, final Data data) throws ConfigurationException
   //----------------------------------------------------------------
   {

      int listlen = 0;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String idElement = null;
      String idDatum = null;
      String value = null;
      String mapfrom = null;
      String mapto = null;
      Set<String> datumIds = null;
      Element element = null;
      NodeList processList = null;
      Datum datum = null; // OpenPTK
      Match match = null; // OpenPTK
      Processes processes = null; // OpenPTK

      /*
       * Process all the Datum Elements, within the Data
       * Each Datum element can contain Process elements
       */

      listlen = datalist.getLength();
      for (int i = 0; i < listlen; i++)
      {
         element = (Element) datalist.item(i);
         if (element != null)
         {
            idElement = element.getAttribute(ELEM_ATTR_ID); // required
            if (idElement != null && idElement.length() > 0)
            {
               datum = new Datum(idElement);

               mapto = element.getAttribute(ELEM_ATTR_MAPTO); // optional
               if (mapto != null && mapto.length() > 0)
               {
                  datum.setMapTo(mapto);
               }

               /*
                * Processes
                */

               processes = null;
               processList = element.getElementsByTagName(ELEM_NAME_PROCESS);
               if (processList != null && processList.getLength() > 0)
               {
                  processes = new Processes();
                  this.parseAndSetAttrMapProcesses(processList, processes);
                  datum.setProcesses(processes);
               }

               /*
                * Add Datum to the Data
                */

               data.addDatum(datum);
            }
            else
            {
               this.handleError(METHOD_NAME + "Datum has a null id");
            }
         }
      }

      /*
       * Process all the Match Elements, within the Data
       */

      datumIds = data.getDatumIds();

      listlen = 0;
      listlen = matchlist.getLength();
      for (int i = 0; i < listlen; i++)
      {
         element = (Element) matchlist.item(i);
         if (element != null)
         {
            idElement = element.getAttribute(ELEM_ATTR_ID); // required
            if (idElement != null && idElement.length() > 0)
            {
               match = new Match(idElement);

               idDatum = element.getAttribute(ELEM_ATTR_DATUM); // required
               if (idDatum != null && idDatum.length() > 0)
               {
                  if (!datumIds.contains(idDatum))
                  {
                     this.handleError(METHOD_NAME + "Match '" + idElement
                        + "', datum value '" + idDatum + "' is not valid / does not exist");
                  }

                  match.setDatumId(idDatum);

                  value = element.getAttribute(ELEM_ATTR_VALUE); // required
                  if (value != null && value.length() > 0)
                  {
                     match.setValue(value);

                     mapto = element.getAttribute(ELEM_ATTR_MAPTO); // required
                     if (mapto != null && mapto.length() > 0)
                     {
                        match.setMapTo(mapto);

                        mapfrom = element.getAttribute(ELEM_ATTR_MAPFROM); // optional
                        if (mapfrom != null && mapfrom.length() > 0)
                        {
                           if (!datumIds.contains(mapfrom))
                           {
                              this.handleError(METHOD_NAME + "Match '" + idElement
                                 + "', mapfrom value '" + idDatum + "' is not a valid Datum / does not exist");
                           }
                           match.setMapFrom(mapfrom);
                        }

                        /*
                         * Add Match to Data
                         */

                        data.addMatch(match);
                     }
                     else
                     {
                        this.handleError(METHOD_NAME + "Match '" + idElement
                           + "' has a missing/null '" + ELEM_ATTR_MAPTO + "' attribute");
                     }
                  }
                  else
                  {
                     this.handleError(METHOD_NAME + "Match '" + idElement
                        + "' has a missing/null '" + ELEM_ATTR_VALUE + "' attribute");
                  }
               }
               else
               {
                  this.handleError(METHOD_NAME + "Match '" + idElement
                     + "' has a missing/null '" + ELEM_ATTR_DATUM + "' attribute");
               }
            }
            else
            {
               this.handleError(METHOD_NAME + "Match has a missing/null 'id' attribute");
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void parseAndSetAssociationAttributes(final Element elements, final ComponentIF comp) throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * Process each Attribute and addDatum it to the Component ("the Association")
       * These Attributes, for a given Association, are used to Map
       * OpenPTK Attribute names to Operation specific names.
       * Associations are only needed when the OpenPTK Attribute Names
       * and the Operation Names are not the same. Attribute Names that
       * are not listed in an Association will be the same for both the
       * OpenPTK and Operation Names (as defined by the OpenPTK name).
       */

//      <Association id="JNDI">
//         <Attributes>
//            <Attribute id="uniqueid"  servicename="uid"/>
//            <Attribute id="firstname" servicename="givenName"/>
//            <Attribute id="lastname"  servicename="sn"/>
//            <Attribute id="fullname"  servicename="cn" required="true"/>
//            <Attribute id="email"     servicename="mail"/>
//         </Attributes>
//      </Association>

      int listlen = 0;
      NodeList nodelist = null;
      Element element = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String arg = null;
      String id = null;
      String serviceName = null;
      AttrIF attr = null; // OpenPTK

      nodelist = elements.getElementsByTagName(XMLConfig.ELEM_NAME_ATTRIBUTE);
      listlen = nodelist.getLength();

      for (int j = 0; j < listlen; j++)
      {
         element = (Element) nodelist.item(j);

         id = element.getAttribute(XMLConfig.ELEM_ATTR_ID);

         /*
          * Create an Attr and addDatum it to the Component
          */

         arg = element.getAttribute(XMLConfig.ELEM_ATTR_TYPE);
         if (arg != null && arg.length() > 0)
         {
            attr = new DefinitionAttr(id, arg);
         }
         else
         {
            attr = new DefinitionAttr(id);
         }

         attr.setCategory(AttrCategory.DEFINITION);

         serviceName = element.getAttribute(XMLConfig.ELEM_ATTR_SERVICENAME);
         if (serviceName != null && serviceName.length() > 0)
         {
            attr.setServiceName(serviceName);
         }
         else
         {
            attr.setServiceName(id);
         }

         /*
          * Get the Attribute's XML element arguments, here are the defaults:
          * type = String
          * access = public
          * readonly = false
          * required = false
          * encrypted = false
          * multivalued = false
          * virtual = false
          */

         arg = element.getAttribute(XMLConfig.ELEM_ATTR_ACCESS);
         if (arg != null && arg.length() > 0)
         {
            attr.setAccess(arg);
         }

         arg = element.getAttribute(XMLConfig.ELEM_ATTR_REQUIRED);
         if (arg != null && arg.length() > 0)
         {
            attr.setRequired(Boolean.parseBoolean(arg));
         }

         arg = element.getAttribute(XMLConfig.ELEM_ATTR_READONLY);
         if (arg != null && arg.length() > 0)
         {
            attr.setReadOnly(Boolean.parseBoolean(arg));
         }

         arg = element.getAttribute(XMLConfig.ELEM_ATTR_MULTIVALUED);
         if (arg != null && arg.length() > 0)
         {
            attr.setAllowMultivalue(Boolean.parseBoolean(arg));
         }

         arg = element.getAttribute(XMLConfig.ELEM_ATTR_ENCRYPTED);
         if (arg != null && arg.length() > 0)
         {
            attr.setEncrypted(Boolean.parseBoolean(arg));
         }

         arg = element.getAttribute(XMLConfig.ELEM_ATTR_VIRTUAL);
         if (arg != null && arg.length() > 0)
         {
            attr.setVirtual(Boolean.parseBoolean(arg));
         }

         this.parseAndSetAttributeFunctions(element, attr);

         comp.setAttribute(id, attr);
      }
      return;
   }

   //----------------------------------------------------------------
   private void parseAndSetAttributeFunctions(final Element element, final AttrIF attr) throws ConfigurationException
   //----------------------------------------------------------------
   {
//               <Functions>
//                  <Function id="FirstInitialLastName" classname="org.openptk.provision.definition.functions.FirstInitialLastname">
//                     <Properties>
//                        <Property name="useexisting" value="true"/>
//                     </Properties>
//                     <Arguments>
//                        <Argument name="maxlength" arg="literal"   value="8"/>
//                        <Argument name="first"     arg="attribute" value="firstname"/>
//                        <Argument name="last"      arg="attribute" value="lastname"/>
//                     </Arguments>
//                     <Operations>
//                        <Operation type="create"/>
//                     </Operations>
//                  </Function>
//                  <Function id="lengthcheck" mode="toService" classname="org.openptk.provision.definition.functions.Length">
//                     <Arguments>
//                        <Argument name="maxlength" arg="literal"   value="8"/>
//                     </Arguments>
//                     <Operations>
//                        <Operation type="create"/>
//                     </Operations>
//                  </Function>
//               </Functions>

      int listLen = 0;
      boolean useExisting = false;
      String attrName = null;
      String functionClassname = null;
      String existing = null;
      String functionId = null;
      NodeList functionNL = null; //     XML
      Element functionElement = null; // XML
      TaskIF task = null; //             OpenPTK
      FunctionIF function = null;

      attrName = attr.getName();

      functionNL = element.getElementsByTagName(XMLConfig.ELEM_NAME_FUNCTION);

      listLen = functionNL.getLength();
      for (int j = 0; j < listLen; j++)
      {
         functionElement = (Element) functionNL.item(j);
         if (functionElement != null)
         {
            functionId = functionElement.getAttribute(XMLConfig.ELEM_ATTR_ID);
            useExisting = false;

            /*
             * Get the classname, make sure it can instanciated
             */

            functionClassname = functionElement.getAttribute(XMLConfig.ELEM_ATTR_CLASSNAME);

            if (functionClassname != null && functionClassname.length() > 0)
            {
               /*
                * See if the function has already been created
                */

               function = _configuration.getFunction(functionClassname);
               if (function == null)
               {
                  try
                  {
                     function = (FunctionIF) this.instantiateClass(functionClassname);
                  }
                  catch (ConfigurationException ex)
                  {
                     this.handleError("Classname error for Function '"
                        + functionId + "' related to Attribute '"
                        + attrName + "', " + ex.getMessage());
                  }

                  if (function != null)
                  {
                     _configuration.setFunction(functionClassname, function);
                  }
               }
            }
            else
            {
               this.handleError("Missing classname for Function '"
                  + functionId + "' related to Attribute '"
                  + attrName + "'");
            }

            /*
             * Get the UseExisting flag
             */

            existing = functionElement.getAttribute(XMLConfig.ELEM_ATTR_USEEXISTING);
            if (existing != null && existing.length() > 0)
            {
               useExisting = Boolean.parseBoolean(existing);
            }

            /*
             * Create the Task for the Function
             */

            task = new Task(functionId, functionClassname);
            task.setUseExisting(useExisting);

            /*
             * Set the Arguments for the Task
             */

            this.parseAndSetTaskArguments(functionElement, task);

            /*
             * Set the Operations for the Task
             */

            this.parseAndSetTaskOperations(attr, functionElement, task);
         }
         else
         {
            this.handleError("A Definition has a Function Element that is NULL");
         }
      } // end of the for loop
      return;
   }

   //----------------------------------------------------------------
   private void setServiceAttributes(final Operation operation, final ServiceIF service, final DefinitionIF definition) throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * For each attribute in the AttrGroup,
       * get the attribute name.
       * Check the Association for an attribute with the same name.
       * If an association is used, update the Fw2Srvc and Srvc2Fw maps.
       * If there is no Association attribute, use the AttrGroup attr name
       * for the "servicename"
       *
       * Next, make sure that each attribute is valid for the Definition
       * used by the Context
       *
       */

      String METHOD_NAME = CLASS_NAME + ":setServiceAttributes() ";
      String FwName = null;
      String SrvcName = null;

      Set<String> keys = null;
      Map<String, String> fw2SrvcMap = null;
      Map<String, String> srvc2FwMap = null;
      Map<String, AttrIF> srvcAttrsMap = null;
      Map<String, AttrIF> attrgroupMap = null;
      Map<String, AttrIF> assocMap = null;
      Map<String, AttrIF> defAttrMap = null;
      Collection<AttrIF> assocColl = null;

      AttrIF defAttr = null;
      AttrIF assocAttr = null;
      DefinitionIF def = null;
      ComponentIF comp = null;

      srvcAttrsMap = new HashMap<String, AttrIF>();
      fw2SrvcMap = new HashMap<String, String>();
      srvc2FwMap = new HashMap<String, String>();

      /*
       * Make a copy of the Context's Definition. We DO NOT want to modify
       * Attribute data that might be specific to a given Operation.
       * For example: We may addDatum an Attribute from the Association or
       * an Attribute may be updated based on the Association
       */

      def = definition.copy(); // COPY IT

      if (def != null)
      {
         defAttrMap = def.getAttributes();
      }
      else
      {
         this.handleError(METHOD_NAME + ": The Context's Definition Attribute Map is null");
      }

      /*
       * Process each Attribute in the Association
       * merge with the Definition Attribute
       */

      assocMap = service.getAssociation(operation).getAttributes(); // REMEMBER: IT'S A COPY
      if (assocMap != null && !assocMap.isEmpty())
      {
         keys = assocMap.keySet();
         for (String attrName : keys)
         {
            if (attrName != null && attrName.length() > 0)
            {
               assocAttr = assocMap.get(attrName);
               if (assocAttr != null)
               {
                  defAttr = defAttrMap.get(attrName);
                  if (defAttr != null)
                  {
                     /*
                      * Add the Association data to the Definition data
                      * The Association data can replace/add-to Definition data
                      * Replace the Association Attribute with the new
                      * merged Attribute
                      */

                     this.combineAttributes(defAttr, assocAttr);
                     assocMap.put(attrName, defAttr);

                     /*
                      * The assocMap/assocAttr were copied from the
                      * Association (Component) when the ".getAttributes()"
                      * method was called. We need to "set" the updated
                      * Attr back into the Association
                      */

                     service.getAssociation(operation).setAttribute(attrName, defAttr);
                  }
               }
            }
         }
      }

      /*
       * Process each Attribute in the AttrGroup
       */

      attrgroupMap = service.getAttrGroup(operation).getAttributes(); // REMEMBER: IT'S A COPY
      if (attrgroupMap != null && !attrgroupMap.isEmpty())
      {
         keys = attrgroupMap.keySet();
         for (String attrName : keys)
         {
            assocAttr = null;

            /*
             * Get the attribute from the Association
             */

            if (attrName != null && attrName.length() > 0)
            {
               assocAttr = assocMap.get(attrName);
               if (assocAttr != null)
               {

                  /*
                   * Add the Association attribute to the
                   * "new" Service Attribute Map (Service Operation Attributes)
                   */

                  srvcAttrsMap.put(attrName, assocAttr);
               }
            }
         }
      }

      comp = new Component();
      comp.setAttributes(srvcAttrsMap);

      /*
       * Add the Attribute and Name Maps to the Service for the specific
       * operation code
       */

      service.setOperAttr(operation, comp);

      /*
       * Create the Framework 2 Service / Service 2 Framework Maps
       * Use the Association Attributes
       */

      assocColl = assocMap.values();

      for (AttrIF associationAttr : assocColl)
      {
         FwName = null;
         SrvcName = null;
         if (associationAttr != null)
         {
            FwName = associationAttr.getFrameworkName();
            SrvcName = associationAttr.getServiceName();

            /*
             * If the name used in the serive is not set, then use the
             * framework name. Not set, implies that they are equal.
             */

            if (SrvcName == null)
            {
               SrvcName = FwName;
            }

            fw2SrvcMap.put(FwName, SrvcName);
            srvc2FwMap.put(SrvcName, FwName);
         }
      }

      /*
       * Add the "key" to the maps, it's probably not listed in the
       * AttrGroup so it needs to be explicitly added
       */

      FwName = service.getKey(operation);
      assocAttr = assocMap.get(FwName);
      if (assocAttr != null)
      {
         SrvcName = assocAttr.getServiceName();
      }
      else
      {
         SrvcName = FwName;
      }

      fw2SrvcMap.put(FwName, SrvcName);
      srvc2FwMap.put(SrvcName, FwName);

      /*
       * Set the service's map for this operation
       */

      service.setFw2SrvcNames(operation, fw2SrvcMap);
      service.setSrvc2FwNames(operation, srvc2FwMap);

      return;
   }

   //----------------------------------------------------------------
   private void getContextAttributes(final ContextIF context)
   //----------------------------------------------------------------
   {
      int tokQty = 0;
      Boolean bOR = Boolean.valueOf(false);
      Boolean bAND = Boolean.valueOf(false);
      Boolean bCONTAINS = Boolean.valueOf(false);
      String srchOrder = null;
      String srchOperators = null;
      String[] srchAttrs = null;
      StringTokenizer tok = null;
      AttrIF attr = null;

      /*
       * attribute (ordered) names, used by the searching processes
       */

      srchOrder = context.getProperty(PROP_SEARCH_DEFAULT_ORDER);

      if (srchOrder != null)
      {
         tok = new StringTokenizer(srchOrder, ",");
         tokQty = tok.countTokens();
         srchAttrs = new String[tokQty];
         for (int i = 0; i < tokQty; i++)
         {
            srchAttrs[i] = tok.nextToken().trim();
         }
      }
      else
      {
         srchAttrs = new String[1];
         srchAttrs[0] = DEFAULT_SEARCH_ATTR;
      }

      attr = new BasicAttr(ContextIF.ATTRIBUTE_SRCH_ATTRS, srchAttrs);
      context.setAttribute(ContextIF.ATTRIBUTE_SRCH_ATTRS, attr);

      /*
       * search operators: AND, OR, CONTAINS
       */

      srchOperators = context.getProperty(PROP_SEARCH_OPERATORS);
      if (srchOperators != null && srchOperators.length() > 0)
      {
         if (srchOperators.indexOf(Query.Type.AND.toString()) > -1)
         {
            bAND = Boolean.valueOf(true);
         }
         attr = new BasicAttr(ContextIF.ATTRIBUTE_HAS_AND, bAND);
         context.setAttribute(ContextIF.ATTRIBUTE_HAS_AND, attr);

         if (srchOperators.indexOf(Query.Type.OR.toString()) > -1)
         {
            bOR = Boolean.valueOf(true);
         }
         attr = new BasicAttr(ContextIF.ATTRIBUTE_HAS_OR, bOR);
         context.setAttribute(ContextIF.ATTRIBUTE_HAS_OR, attr);

         if (srchOperators.indexOf(Query.Type.CONTAINS.toString()) > -1)
         {
            bCONTAINS = Boolean.valueOf(true);
         }
         attr = new BasicAttr(ContextIF.ATTRIBUTE_HAS_CONTAINS, bCONTAINS);
         context.setAttribute(ContextIF.ATTRIBUTE_HAS_CONTAINS, attr);
      }

      return;
   }

   //----------------------------------------------------------------
   private Object instantiateClass(final String classname) throws ConfigurationException
   //----------------------------------------------------------------
   {
      return this.instantiateClass(classname, null);
   }

   //----------------------------------------------------------------
   private Object instantiateClass(final String classname, final Object[] arguments) throws ConfigurationException
   //----------------------------------------------------------------
   {

      String METHOD_NAME = CLASS_NAME + ":instantiateClass() ";
      String strError = null;
      Object newObject = null;
      Class<?> newClass = null;
      Class<?>[] argumentTypes = null;
      Constructor<?> constructor = null;

      // Instantiate the the classname
      if (classname != null)
      {
         if (classname.length() > 0)
         {
            try
            {
               if (arguments == null)
               {
                  newObject = Class.forName(classname).newInstance();
               }
               else
               {
                  newClass = Class.forName(classname);

                  argumentTypes = new Class<?>[arguments.length];

                  for (int i = 0; i < arguments.length; i++)
                  {
                     argumentTypes[i] = arguments[i].getClass();
                  }

                  constructor = newClass.getConstructor(argumentTypes);

                  newObject = constructor.newInstance(arguments);
               }
            }
            catch (NoClassDefFoundError er)
            {
               strError = "No Class Definition found (" + classname + ") : " + er.getMessage();
            }
            catch (InstantiationException ex)
            {
               strError = "Instantiation problem (" + classname + ") : " + ex.getMessage();
            }
            catch (IllegalAccessException ex)
            {
               strError = "Access problem (" + classname + ") : " + ex.getMessage();
            }
            catch (ClassNotFoundException ex)
            {
               strError = "Class not found (" + classname + ") : " + ex.getMessage();
            }
            catch (NoSuchMethodException er)
            {
               strError = "No Constructor Method Found for Arguments (" + classname + ") : " + er.getMessage();
            }
            catch (InvocationTargetException er)
            {
               strError = "Unable to call Constructor Method Found for Arguments (" + classname + ") : " + er.getMessage();
            }
         }
         else
         {
            strError = "Classname is not set (length=0)";
         }
      }
      else
      {
         strError = "Classname is null";
      }

      if (strError != null)
      {
         this.handleError(METHOD_NAME + strError);
      }

      return newObject;
   }

   //----------------------------------------------------------------
   private Query getQuery(final Element queryElement) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = this.CLASS_NAME + ":getQuery() ";
      boolean bFound = false;
      Query.Type type = null;
      String strType = null;
      String queryName = null;
      String queryValue = null;
      Element subElement = null; // w3c.dom
      NodeList queryNL = null;   // w3c.dom
      Node node = null;          // w3c.dom
      Query query = null;
      Query subquery = null;
      Query.Type[] typeArray = null;

      typeArray = Query.Type.values();

      query = new Query();

      strType = queryElement.getAttribute(XMLConfig.ELEM_ATTR_TYPE);

      for (int i = 0; i < typeArray.length; i++)
      {
         if (strType.equalsIgnoreCase(typeArray[i].toString()))
         {
            bFound = true;
            type = typeArray[i];
         }
      }

      if (!bFound)
      {
         this.handleError(METHOD_NAME + "Invalid Query Type/Code '" + strType + "'");
      }

      query.setType(type);

      switch (type)
      {
         case NOOPERATOR:
            break;
         case AND:
         case OR:
         {
            /*
             * Complex Query Examples:
             *
             * <Query type="AND">
             * <Query type="EQ" name="memberObjectGroup" value="Top:Customer"/>
             * <Query type="EQ" name="objectClass" value="Person"/>
             * <Query type="NE" name="org" value="corporate"/>
             * </Query>
             *
             * <Query type="OR">
             * <Query type="AND">
             * <Query type="EQ" name="MemberObjectGroups" value="All People" />
             * <Query type="NE" name="org" value="Research"/>
             * </Query>
             * <Query type="AND">
             * <Query type="EQ" name="objectclass" value="user" />
             * <Query type="NE" name="org" value="Research"/>
             * </Query>
             * </Query>
             */

            queryNL = queryElement.getChildNodes();

            for (int j = 0; j < queryNL.getLength(); j++)
            {
               node = queryNL.item(j);
               if (node != null && node.getNodeType() == Node.ELEMENT_NODE)
               {
                  subElement = (Element) node;
                  if (subElement != null)
                  {
                     subquery = this.getQuery(subElement);
                     if (subquery != null)
                     {
                        query.addQuery(subquery);
                     }
                     else
                     {
                        this.handleError(METHOD_NAME + "Complex Query's sub-query is null.");
                     }
                  }
                  else
                  {
                     this.handleError(METHOD_NAME + "Complex Query sub-element is null.");
                  }
               }
            }
         }
         break;
         default:
         {
            /*
             * Simple Query:
             *
             * <Query type="EQ" name="memberObjectGroup" value="Top:Customer"/>
             */
            queryName = queryElement.getAttribute(XMLConfig.ELEM_ATTR_NAME);
            if (queryName != null && queryName.length() > 0)
            {
               query.setName(queryName);
            }
            else
            {
               this.handleError(METHOD_NAME + "Query attribute 'name' is not set.");
            }

            queryValue = queryElement.getAttribute(XMLConfig.ELEM_ATTR_VALUE);
            if (queryValue != null && queryValue.length() > 0)
            {
               query.setValue(queryValue);
            }
            else
            {
               this.handleError(METHOD_NAME + "Query attribute 'value' is not set.");
            }
         }
         break;
      }

      return query;
   }

   //----------------------------------------------------------------
   private void loadGlobalProperties()
   //----------------------------------------------------------------
   {
      /*
       * Load the XML file's "Global" element.
       * If it exists, get the "Properties"
       * If there are no Properties, create a new (empty) Properties object
       */
      //   <Global>
      //      <Properties>
      //         <Property name="auth.token.httpheader"        value="openptkid" />
      //         <Property name="http.session.cookie.uniqueid" value="OPENPTKSESSIONID" />
      //         <Property name="http.session.cookie.httponly" value="true" />
      //         <Property name="engine.session.ttl"           value="1800000" />
      //         <Property name="engine.session.cache.ttl"     value="120000" />
      //         <Property name="search.results.quantity"      value="10" />
      //         <Property name="search.attribute.default"     value="firstname" />
      //      </Properties>
      //   </Global>

      int iDebug = 0;
      String debugStr = null;
      Element globalElement = null;
      DebugLevel[] debugArray = DebugLevel.values();

      globalElement = getElement(XMLConfig.ELEM_NAME_GLOBAL);

      if (globalElement != null)
      {
         this.setProperties(this.combineProperties(this.getProperties(),
            this.getElementProperties(globalElement)));
      }

      if (this.getProperties() == null)
      {
         this.setProperties(new Properties());
      }
      else
      {
         //
         // Get the Debug Level
         //

         debugStr = this.getProperty(XMLConfig.PROP_DEBUG);
         if (debugStr != null)
         {
            try
            {
               iDebug = Integer.parseInt(debugStr);
            }
            catch (NumberFormatException ex)
            {
               iDebug = 0;
            }
            this.setDebugLevel(debugArray[iDebug]);
         }
         if (this.getDebugLevel() != DebugLevel.NONE)
         {
            this.setDebug(true);
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private Properties combineProperties(final Properties first, final Properties second)
   //----------------------------------------------------------------
   {
      String key = null;
      String value = null;
      Properties merge = null;
      Set<Object> set = null;
      Iterator<Object> iter = null;

      /*
       * The second properties are added to the first properties
       * If the second has the same name as the first, it is replaced
       */

      merge = new Properties();

      if (first != null && !first.isEmpty())
      {
         set = first.keySet();
         iter = set.iterator();
         while (iter.hasNext())
         {
            key = (String) iter.next();
            value = first.getProperty(key);
            if (key != null && value != null)
            {
               merge.put(key, value);
            }
         }
      }

      if (second != null && !second.isEmpty())
      {
         set = second.keySet();
         iter = set.iterator();
         while (iter.hasNext())
         {
            key = (String) iter.next();
            value = second.getProperty(key);
            if (key != null && value != null)
            {
               merge.put(key, value);
            }
         }
      }

      return merge;
   }

   //----------------------------------------------------------------
   private void combineAttributes(final AttrIF first, final AttrIF second)
   //----------------------------------------------------------------
   {
      /*
       * This method will merge data from the "second" Attr
       * into the "first" Attr.
       * example: the "second" is from an Association and the
       * first is from a Definition. The Association "settings"
       * will replace the Definition "settings"
       */
      String SrvcName = null;
      Properties firstProps = null;
      Properties secondProps = null;
      Map<String, TaskIF> firstTaskMap = null;
      Map<String, TaskIF> secondTaskMap = null;
      Operation oper = null;
      TaskIF task = null;

      if (first != null && second != null)
      {

         /*
          * If the second has a "Service Name" apply to the first
          */

         SrvcName = second.getServiceName();

         if (SrvcName != null && SrvcName.length() > 0)
         {
            first.setServiceName(SrvcName);
         }
         else
         {
            first.setServiceName(first.getFrameworkName());
         }

         /*
          * Only "apply" the value only if it's true (defaults are "false")
          */
         if (second.isRequired())
         {
            first.setRequired(second.isRequired());
         }

         if (second.isReadOnly())
         {
            first.setReadOnly(second.isReadOnly());
         }

         if (second.isEncrypted())
         {
            first.setEncrypted(second.isEncrypted());
         }

         if (second.isVirtual())
         {
            first.setVirtual(second.isVirtual());
         }

         if (second.allowMultivalue())
         {
            first.setAllowMultivalue(second.allowMultivalue());
         }

         /*
          * Merge the second Properties with the first
          */

         secondProps = second.getProperties();
         if (secondProps != null && secondProps.size() > 0)
         {
            firstProps = first.getProperties();
            if (firstProps != null && firstProps.size() > 0)
            {
               first.setProperties(this.combineProperties(firstProps, secondProps));
            }
            else
            {
               first.setProperties(secondProps);
            }
         }

         /*
          * If the second Attribute has one or more Functions,
          * merge them.
          *
          * Rules:
          *
          * Check all the Operations for specific functions
          *
          * If a Function in the 2nd Attr has the same "id" as a Function
          * in the 1st ... then the Function from the 2nd Attr will REPLACE
          * the Function in the 1st ... else the Function from the 2nd Attr
          * will be appended to any existing Functions in the 1st Attr
          *
          */
         for (int i = 0; i < _operations.length; i++)
         {
            oper = _operations[i];
            secondTaskMap = second.getTasks(oper);
            if (secondTaskMap != null)
            {
               firstTaskMap = first.getTasks(oper);
               if (firstTaskMap != null)
               {
                  for (String taskId : secondTaskMap.keySet())
                  {
                     if (taskId != null && taskId.length() > 0)
                     {
                        task = secondTaskMap.get(taskId);
                        if (task != null)
                        {
                           /*
                            * A valid task was found in the "second" attribute
                            * If the "first" attribute has a task with the
                            * same "id" then it will be replaced. Else,
                            * the task will be "added" (to the end)
                            */

                           first.addTask(oper, task);
                        }
                     }
                  }
               }
               else
               {
                  /*
                   * first Attr has no Task(s), but the second one does ...
                   * set the first Attr to use the second Attr's Task(s)
                   */
                  first.setTasks(oper, secondTaskMap);
               }
            }
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private void checkDefinitionAssociation(final Map<String, AttrIF> definitions, final Map<String, AttrIF> associations) throws ConfigurationException
   //----------------------------------------------------------------
   {
      /*
       * Check the Association Attributes to make sure they are all valid
       * within the Defintion
       */

      String METHOD_NAME = this.CLASS_NAME + "checkDefinitionAssociation(): ";

      for (String name : associations.keySet())
      {
         if (!definitions.containsKey(name))
         {
            throw new ConfigurationException(METHOD_NAME + "Attribute='" + name + "'");
         }

      }

      return;
   }

   //----------------------------------------------------------------
   private ExternalAttrIF.Mode getAttrModeFromString(String str)
   //----------------------------------------------------------------
   {
      ExternalAttrIF.Mode attrMode = null;

      if (str.equalsIgnoreCase(ExternalAttrIF.Mode.IGNORE.toString()))
      {
         attrMode = ExternalAttrIF.Mode.IGNORE;
      }
      else if (str.equalsIgnoreCase(ExternalAttrIF.Mode.OUTBOUND.toString()))
      {
         attrMode = ExternalAttrIF.Mode.OUTBOUND;
      }
      else if (str.equalsIgnoreCase(ExternalAttrIF.Mode.INBOUND.toString()))
      {
         attrMode = ExternalAttrIF.Mode.INBOUND;
      }
      else if (str.equalsIgnoreCase(ExternalAttrIF.Mode.BOTH.toString()))
      {
         attrMode = ExternalAttrIF.Mode.BOTH;
      }

      return attrMode;
   }

   //----------------------------------------------------------------
   private DataType getDataTypeFromString(String str)
   //----------------------------------------------------------------
   {
      DataType type = null;

      if (str.equalsIgnoreCase(DataType.STRING.toString()))
      {
         type = DataType.STRING;
      }
      else if (str.equalsIgnoreCase(DataType.BOOLEAN.toString()))
      {
         type = DataType.BOOLEAN;
      }
      else if (str.equalsIgnoreCase(DataType.INTEGER.toString()))
      {
         type = DataType.INTEGER;
      }
      else if (str.equalsIgnoreCase(DataType.LONG.toString()))
      {
         type = DataType.LONG;
      }
      else if (str.equalsIgnoreCase(DataType.OBJECT.toString()))
      {
         type = DataType.OBJECT;
      }
      else
      {
         type = DataType.STRING;
      }

      return type;
   }
}
