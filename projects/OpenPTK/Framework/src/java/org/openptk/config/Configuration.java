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
 * Portions Copyright 2011-2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openptk.authenticate.AuthenticatorIF;
import org.openptk.authenticate.PrincipalIF;
import org.openptk.authorize.EnforcerIF;
import org.openptk.authorize.decider.DeciderIF;
import org.openptk.authorize.decider.DeciderManager;
import org.openptk.authorize.policy.PolicyIF;
import org.openptk.client.ClientIF;
import org.openptk.config.mapper.AttrMapIF;
import org.openptk.context.ContextIF;
import org.openptk.context.actions.ActionIF;
import org.openptk.crypto.Encryptor;
import org.openptk.debug.DebugLevel;
import org.openptk.debug.Debugger;
import org.openptk.definition.DefinitionIF;
import org.openptk.definition.SubjectIF;
import org.openptk.definition.functions.FunctionIF;
import org.openptk.logging.Logger;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.CryptoException;
import org.openptk.model.ModelIF;
import org.openptk.plugin.PluginIF;
import org.openptk.session.SessionIF;
import org.openptk.structure.ConverterIF;
import org.openptk.structure.ConverterType;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
/**
 * The Configuration class is the starting point for the OpenPTK
 * This class leverages a XML file which defines the OpenPTK environment
 * The XML file (default=openptk.xml) must conform to the PTK.xsd definition
 * The XML config file defines available Contexts. A given Context will have
 * an associated Subject and Service.
 */
//
//===================================================================
public class Configuration
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String MSG_NAME = "Messages";
   private static final String LOGGER_NAME = "Configuration";
   private static final String LOG_MSG_NULL = "(Null Message)";
   private static final String LOG_MSG_NO_SESSION = "[No Session]";
   private boolean _bDebug = false;
   private boolean _bDefCtx = false;
   private DebugLevel _debugLevel = DebugLevel.NONE;
   private String _defaultCtxName = null;
   private String _currentCtxName = null;
   private Map<String, ContextIF> _contexts = null;
   private Map<String, FunctionIF> _functions = null;
   private Map<String, AuthenticatorIF> _authenticators = null;
   private Map<String, ClientIF> _clients = null;
   private Map<String, ModelIF> _models = null;
   private Map<ConverterType, ConverterIF> _converters = null;
   private Map<String, PluginIF> _plugins = null;
   private Map<String, ActionIF> _actions = null;
   private Map<String, PolicyIF> _policies = null;
   private Map<String, DeciderIF> _deciders = null;
   private Map<String, EnforcerIF> _enforcers = null;
   private Map<String, AttrMapIF> _attrmaps = null;
   private DeciderManager _deciderMgr = null;
   private Properties _props = null;
   private Properties _messages = null;
   private Debugger _debugger = null;
   //
   // debugLevel   ComponentIF.Debug
   // 0            NONE
   // 1            CONFIG
   // 2            FINE
   // 3            FINER
   // 4            FINEST
   //

   /**
    * Create a new Configuration object.
    * The specific configFile will be used to create Contexts, Subjects and Services
    *
    * @param configFile the XML configuration file
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   public Configuration(final String configFile, Properties props) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":Configuration(String): ";
      String msg = null;
      ConfigIF config = null;

      _debugger = new Debugger();
      _functions = new HashMap<String, FunctionIF>();

      /*
       * load all the Messages from the Property file
       */

      try
      {
         this.initMessages(Configuration.MSG_NAME);
      }
      catch (ConfigurationException ex)
      {
         msg = METHOD_NAME + "initMessages: " + ex.getMessage();
         this.handleError(msg);
      }

      /*
       * config represents an .xml file containing all configuration information.
       */

      try
      {
         config = new XMLConfig(configFile, props, this);
      }
      catch (ConfigurationException ex)
      {
         msg = METHOD_NAME + "new XMLConfig(): " + ex.getMessage();
         this.handleError(msg);
      }

      _contexts = config.getContexts();
      _defaultCtxName = config.getDefaultContextName();
      _authenticators = config.getAuthenticators();
      _policies = config.getPolicies();
      _deciders = config.getDeciders();
      _enforcers = config.getEnforcers();
      _clients = config.getClients();
      _models = config.getModels();
      _props = config.getProperties();
      _converters = config.getConverters();
      _plugins = config.getPlugins();
      _actions = config.getActions();
      _attrmaps = config.getAttrMaps();
      _debugLevel = config.getDebugLevel();
      _bDebug = config.isDebug();

      _currentCtxName = _defaultCtxName;

      this.checkDefaultContext();

      if (_debugLevel == DebugLevel.FINEST)
      {
         this.logData(this, CLASS_NAME);
      }

      _deciderMgr = new DeciderManager(_enforcers, _deciders, _policies);

      return;
   }

   /**
    * Is the Configuration's debug mode turned on.
    * This is true when the debug level, in the XML file is greater than 0
    *
    * @return boolean is debugging level turned on
    */
   //----------------------------------------------------------------
   public final boolean isDebug()
   //----------------------------------------------------------------
   {
      return _bDebug;
   }

   /**
    * The debug level as defined in the XML file.
    * 0 = off, 1,2,3,4 are the different levels of detail.
    *
    * @return integer the debug level
    */
   //----------------------------------------------------------------
   public final DebugLevel getDebugLevel()
   //----------------------------------------------------------------
   {
      return _debugLevel;
   }

   /**
    * Does the Configuration have a valid Default Context.
    *
    * @return boolean is there a default Context
    */
   //----------------------------------------------------------------
   public final boolean hasDefaultContext()
   //----------------------------------------------------------------
   {
      return _bDefCtx;
   }

   /**
    * Get a Message (property).
    *
    * @param key the messages name
    * @return String the messages value
    */
   //----------------------------------------------------------------
   public final String getMessage(final String key)
   //----------------------------------------------------------------
   {
      String ret = null;

      ret = _messages.getProperty(key);
      if (ret == null)
      {
         ret = "ERROR: Message: " + key + " was not found.";
      }

      return ret;
   }

   /**
    * Get all of the Messages.
    *
    * @return Properties contains all of the messages
    */
   //----------------------------------------------------------------
   public final Properties getMessages()
   //----------------------------------------------------------------
   {
      return _messages;
   }

   /**
    * Get the specified Context.
    *
    * @param name the name of the Context, as defined in the XML file
    * @return ContextIF the context's interface
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   public final ContextIF getContext(final String name) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getContext(String): ";
      String msg = null;
      ContextIF ctx = null;

      if (_contexts != null)
      {
         if (_contexts.containsKey(name))
         {
            ctx = _contexts.get(name);
         }
         else
         {
            msg = METHOD_NAME
                    + this.getMessage("configuration.context.notexist")
                    + ": '" + name + "'";
            this.handleError(msg);
         }
      }
      else
      {
         msg = METHOD_NAME + this.getMessage("configuration.contexts.null");
         this.handleError(msg);
      }

      return ctx;
   }

   /**
    * Get the specified Authenticator.
    *
    * @param name the name of the Authenticator, as defined in the XML file
    * @return AuthenticatorIF the authenticator's interface
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   public final AuthenticatorIF getAuthenticator(final String name) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getAuthenticator(String): ";
      String msg = null;
      AuthenticatorIF authen = null;

      if (_authenticators != null)
      {
         if (_authenticators.containsKey(name))
         {
            authen = _authenticators.get(name);
         }
         else
         {
            msg = METHOD_NAME
                    + this.getMessage("configuration.authenticator.notexist")
                    + ": '" + name + "'";
            this.handleError(msg);
         }
      }
      else
      {
         msg = METHOD_NAME + this.getMessage("configuration.authenticators.null");
         this.handleError(msg);
      }

      return authen;
   }

   /**
    * Gets a String Array of the Authenticator Names.
    *
    * @return String Array
    */
   //----------------------------------------------------------------
   public final String[] getAuthenticatorNames()
   //----------------------------------------------------------------
   {
      String[] array = null;

      if (_authenticators != null && !_authenticators.isEmpty())
      {
         array = _authenticators.keySet().toArray(new String[_authenticators.size()]);
      }
      else
      {
         array = new String[0];
      }
      return array;
   }

   /**
    * Get the specified Client.
    *
    * @param name the name of the Client, as defined in the XML file
    * @return ClientIF the client's interface
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   public final ClientIF getClient(final String name) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getClient(String): ";
      String msg = null;
      ClientIF client = null;

      if (_clients != null)
      {
         if (_clients.containsKey(name))
         {
            client = _clients.get(name);
         }
         else
         {
            msg = METHOD_NAME
                    + this.getMessage("configuration.client.notexist")
                    + ": '" + name + "'";
            this.handleError(msg);
         }
      }
      else
      {
         msg = METHOD_NAME + this.getMessage("configuration.client.null");
         this.handleError(msg);
      }

      return client;
   }

   /**
    * Gets a String Array of the Client Names.
    *
    * @return String Array
    */
   //----------------------------------------------------------------
   public final String[] getClientNames()
   //----------------------------------------------------------------
   {
      String[] array = null;

      if (_clients != null && !_clients.isEmpty())
      {
         array = _clients.keySet().toArray(new String[_clients.size()]);
      }
      else
      {
         array = new String[0];
      }
      return array;
   }

   /**
    * Get the specified Client.
    *
    * @param name the name of the Client, as defined in the XML file
    * @return ClientIF the client's interface
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   public final ModelIF getModel(final String name) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getModel(String): ";
      String msg = null;
      ModelIF model = null;

      if (_models != null)
      {
         if (_models.containsKey(name))
         {
            model = _models.get(name);
         }
         else
         {
            msg = METHOD_NAME
                    + this.getMessage("configuration.model.notexist")
                    + ": '" + name + "'";
            this.handleError(msg);
         }
      }
      else
      {
         msg = METHOD_NAME + this.getMessage("configuration.models.null");
         this.handleError(msg);
      }

      return model;
   }

   /**
    * Gets a String Array of the Model Names.
    *
    * @return String Array
    */
   //----------------------------------------------------------------
   public final String[] getModelNames()
   //----------------------------------------------------------------
   {
      String[] array = null;

      if (_models != null && !_models.isEmpty())
      {
         array = _models.keySet().toArray(new String[_models.size()]);
      }
      else
      {
         array = new String[0];
      }
      return array;
   }

   /**
    * Gets a String Array of the Context Names.
    *
    * @return String Array
    */
   //----------------------------------------------------------------
   public final String[] getContextNames()
   //----------------------------------------------------------------
   {
      String[] array = null;

      if (_contexts != null && !_contexts.isEmpty())
      {
         array = _contexts.keySet().toArray(new String[0]);
      }
      else
      {
         array = new String[0];
      }
      return array;
   }

   /**
    * Set the current Context's name.
    * This will allow the client to set the current contect to a value different
    * than the default context. If no context is set, the default one from
    * the cofiguration file will be set.
    *
    * @param contextName
    */
   //----------------------------------------------------------------
   public final void setContextName(final String contextName)
   //----------------------------------------------------------------
   {
      _currentCtxName = contextName;
      return;
   }

   /**
    * Get the current Context's name
    * Get the name of the current Context. If this hasn't been set
    * by the client, it will be defaulted from the default context in the XML
    * configuration file.
    *
    * @return String the name of the current Context
    */
   //----------------------------------------------------------------
   public final String getContextName()
   //----------------------------------------------------------------
   {
      return _currentCtxName;
   }

   /**
    * Get the default Context's name
    * Get the name of the default Context. NOTICE: Having a default
    * Context name does not imply the actual default context exists.
    *
    * @return String the name of the default Context
    */
   //----------------------------------------------------------------
   public final String getDefaultContextName()
   //----------------------------------------------------------------
   {
      return _defaultCtxName;
   }

   /**
    * Get all of the defined Contexts as a Map
    * The Maps key is the name of the Context and the Maps value is
    * the Context. This is primarily used for debugging.
    *
    * @return Map all the Contexts
    */
   //----------------------------------------------------------------
   public final Map<String, ContextIF> getContexts()
   //----------------------------------------------------------------
   {
      return _contexts;
   }

   /**
    * Get all of the defined Authenticators as a Map
    * The Maps key is the name of the Authenticator and the Maps value is
    * the Authenticator. This is primarily used for debugging.
    *
    * @return Map all the Authenticators
    */
   //----------------------------------------------------------------
   public final Map<String, AuthenticatorIF> getAuthenticators()
   //----------------------------------------------------------------
   {
      return _authenticators;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   public final String[] getPluginIds()
   //----------------------------------------------------------------
   {
      return _plugins.keySet().toArray(new String[_plugins.size()]);
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   public final Map<String, PluginIF> getPlugins()
   //----------------------------------------------------------------
   {
      return _plugins;
   }
   
   /**
    * @param id
    * @return
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   public final PluginIF getPlugin(final String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      if (id == null || id.length() < 1)
      {
         this.handleError("Plugin id is null");
      }

      if (!_plugins.containsKey(id))
      {
         this.handleError("Plugin id '" + id + "' does not exist");
      }

      return _plugins.get(id);
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   public final String[] getAttrMapIds()
   //----------------------------------------------------------------
   {
      return _attrmaps.keySet().toArray(new String[_attrmaps.size()]);
   }
   
   /**
    * 
    * @return 
    */
   //----------------------------------------------------------------
   public final Map<String, AttrMapIF> getAttrMaps()
   //----------------------------------------------------------------
   {
      return _attrmaps;
   }
   
   /**
    * 
    * @param id
    * @return
    * @throws ConfigurationException 
    */
   //----------------------------------------------------------------
   public final AttrMapIF getAttrMap(final String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      if ( id == null || id.length() < 1)
      {
         this.handleError("AttrMap id is null");
      }
      
      if (!_attrmaps.containsKey(id))
      {
         this.handleError("AttrMap id '" + id + "' does not exist");
      }
      
      return _attrmaps.get(id);
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   public final Map<String, ActionIF> getActions()
   //----------------------------------------------------------------
   {
      return _actions;
   }

   /**
    * @param id
    * @return
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   public final ActionIF getAction(final String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      if (id == null || id.length() < 1)
      {
         this.handleError("Action Id is null");
      }

      if (!_actions.containsKey(id))
      {
         this.handleError("Action id '" + id + "' does not exist");
      }

      return _actions.get(id);
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   public final String[] getActionIds()
   //----------------------------------------------------------------
   {
      return _actions.keySet().toArray(new String[_actions.size()]);
   }

   /**
    *
    * @return
    */
   //----------------------------------------------------------------
   public final Map<String, DeciderIF> getDeciders()
   //----------------------------------------------------------------
   {
      return _deciders;
   }
   
   /**
    * 
    * @param id
    * @return
    * @throws ConfigurationException 
    */
   //----------------------------------------------------------------
   public final DeciderIF getDecider(final String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      if ( id == null || id.length() < 1)
      {
         this.handleError("Decider id is empty/null");
      }
      
      if ( !_deciders.containsKey(id))
      {
         this.handleError("Decider id '" + id + "' does not exist");
      }
      
      return _deciders.get(id);
   }
   
   /**
    * 
    * @return 
    */
   //----------------------------------------------------------------
   public final String[] getDeciderIds()
   //----------------------------------------------------------------
   {
      return _deciders.keySet().toArray(new String[_deciders.size()]);
   }

   /**
    * 
    * @return 
    */
   //----------------------------------------------------------------
   public final Map<String, EnforcerIF> getEnforcers()
   //----------------------------------------------------------------
   {
      return _enforcers;
   }
   
   /**
    * 
    * @param id
    * @return
    * @throws ConfigurationException 
    */
   //----------------------------------------------------------------
   public final EnforcerIF getEnforcer(final String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      if ( id == null || id.length() < 1)
      {
         this.handleError("Enforcer id is empty/null");
      }
      
      if ( !_enforcers.containsKey(id))
      {
         this.handleError("Enforcer id '" + id + "' does not exist");
      }
      
      return _enforcers.get(id);      
   }
   
   /**
    * 
    * @return 
    */
   //----------------------------------------------------------------
   public final String[] getEnforcerIds()
   //----------------------------------------------------------------
   {
      return _enforcers.keySet().toArray(new String[_enforcers.size()]);
   }
   
   /**
    * 
    * @return 
    */
   //----------------------------------------------------------------
   public final Map<String, PolicyIF> getPolicies()
   //----------------------------------------------------------------
   {
      return _policies;
   }
   
   /**
    * 
    * @param id
    * @return
    * @throws ConfigurationException 
    */
   //----------------------------------------------------------------
   public final PolicyIF getPolicy(String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      if ( id == null || id.length() < 1)
      {
         this.handleError("Policy id is empty/null");
      }
      
      if ( !_policies.containsKey(id))
      {
         this.handleError("Policy id '" + id + "' does not exist");
      }
      
      return _policies.get(id);      
   }
   
   /**
    * 
    * @return 
    */
   //----------------------------------------------------------------
   public final String[] getPolicyIds()
   //----------------------------------------------------------------
   {
      return _policies.keySet().toArray(new String[_policies.size()]);
   }
   /**
    * Log information about the object.
    *
    * @param obj a OpenPTK framework object
    * @param callerId an identifier that is added to the data
    */
   //----------------------------------------------------------------
   public final void logData(final Object obj, final String callerId)
   //----------------------------------------------------------------
   {
      if (_debugger == null)
      {
         _debugger = new Debugger();
      }
      _debugger.logData(obj, callerId);

      return;
   }

   /**
    * Get information about the object.
    *
    * @param obj a OpenPTK framework object
    * @param callerId an identifier that is added to the data
    * @return String the object's data
    */
   //----------------------------------------------------------------
   public final String getData(final Object obj, final String callerId)
   //----------------------------------------------------------------
   {
      if (_debugger == null)
      {
         _debugger = new Debugger();
      }

      return _debugger.getData(obj, callerId);
   }

   /**
    * Write the String to the log, type is set to INFO.
    *
    * @param str value that will be written to the log
    */
   //----------------------------------------------------------------
   public final void logInfo(final String str)
   //----------------------------------------------------------------
   {
      this.logInfo(null, str);
      return;
   }

   /**
    * Write the String to the log, include the Session data, type is set to INFO.
    *
    * @param session user - context information
    * @param str value that will be written to the log
    */
   //----------------------------------------------------------------
   public final void logInfo(SessionIF session, String str)
   //----------------------------------------------------------------
   {
      String msg = null;
      StringBuffer buf = null;

      if (str == null || str.length() < 1)
      {
         str = LOG_MSG_NULL;
      }

      if (session != null)
      {
         msg = this.getSessionData(session) + " " + str;
      }
      else
      {
         msg = LOG_MSG_NO_SESSION + " " + str;
      }

      Logger.logInfo(msg);

      return;
   }

   /**
    * Write the String to the log, type is set to WARNING.
    *
    * @param str value that will be written to the log
    */
   //----------------------------------------------------------------
   public final void logWarning(final String str)
   //----------------------------------------------------------------
   {
      this.logWarning(null, str);
      return;
   }

   /**
    * Write the String to the log, include the Session data, type is set to WARNING.
    *
    * @param session user - context information
    * @param str value that will be written to the log
    */
   //----------------------------------------------------------------
   public final void logWarning(SessionIF session, String str)
   //----------------------------------------------------------------
   {
      String msg = null;
      StringBuffer buf = null;

      if (str == null || str.length() < 1)
      {
         str = LOG_MSG_NULL;
      }

      if (session != null)
      {
         msg = this.getSessionData(session) + " " + str;
      }
      else
      {
         msg = LOG_MSG_NO_SESSION + " " + str;
      }

      Logger.logWarning(msg);

      return;
   }

   /**
    * Write the String to the log, type is set to ERROR.
    *
    * @param str value that will be written to the log
    */
   //----------------------------------------------------------------
   public final void logError(final String str)
   //----------------------------------------------------------------
   {
      this.logError(null, str);
      return;
   }

   /**
    * Write the String to the log, include the Session data, type is set to ERROR.
    *
    * @param session user - context information
    * @param str value that will be written to the log
    */
   //----------------------------------------------------------------
   public final void logError(SessionIF session, String str)
   //----------------------------------------------------------------
   {
      String msg = null;
      StringBuffer buf = null;

      if (str == null || str.length() < 1)
      {
         str = LOG_MSG_NULL;
      }

      if (session != null)
      {
         msg = this.getSessionData(session) + " " + str;
      }
      else
      {
         msg = LOG_MSG_NO_SESSION + " " + str;
      }

      Logger.logError(msg);

      return;
   }

   /**
    * Get all of the defined Authenticators as a Map
    * The Maps key is the name of the Authenticator and the Maps value is
    * the Authenticator. This is primarily used for debugging.
    *
    * @return Map all the Authenticators
    */
   //----------------------------------------------------------------
   public final Properties getProperties()
   //----------------------------------------------------------------
   {
      return _props;
   }

   /**
    * Get the Subject from the default Context.
    *
    * @return SubjectIF the Subject interface
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   public final SubjectIF getSubject() throws ConfigurationException
   //----------------------------------------------------------------
   {
      return this.getSubject(this.getContextName());
   }

   /**
    * Get the specified Subject from the Context.
    *
    * @param contextName the name of the Context
    * @return SubjectIF the Subject interface
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   public final synchronized SubjectIF getSubject(final String contextName) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getSubject(): ";
      String msg = null;
      String className = null;
      SubjectIF subject = null;
      ContextIF ctx = null;
      DefinitionIF def = null;

      ctx = this.getContext(contextName);
      if (ctx == null)
      {
         msg = METHOD_NAME + this.getMessage("configuration.context.notexist")
                 + ", contextName='" + contextName + "'";
         this.handleError(msg);
      }

      /*
       * Check the Context's state:
       *
       * READY: everything is fine ... proceed
       * FAILED: something went wrong ... log message proceed (maybe recoverable)
       * ERROR: critcal / unrecoverable ... stop, throw exception
       * default: unsupported STATE ... stop, throw exception
       */

      switch (ctx.getState())
      {
         case READY:
            break;
         case FAILED:
            msg = METHOD_NAME + this.getMessage("configuration.context.state.failed")
                    + ", status='" + ctx.getStatus() + "', contextName='" + contextName + "'";
            this.logWarning(msg);
            break;
         case ERROR:
            msg = METHOD_NAME + this.getMessage("configuration.context.state.error")
                    + ", status='" + ctx.getStatus() + "', contextName='" + contextName + "'";
            this.handleError(msg);
            break;
         case DISABLED:
            msg = METHOD_NAME + this.getMessage("configuration.context.state.disabled")
                    + ", status='" + ctx.getStatus() + "', contextName='" + contextName + "'";
            this.handleError(msg);
            break;
         default:
            msg = METHOD_NAME + this.getMessage("configuration.context.state.unsupported")
                    + ", state='" + ctx.getState() + "', contextName='" + contextName + "'";
            this.handleError(msg);
            break;
      }

      def = ctx.getDefinition();
      if (def == null)
      {
         msg = METHOD_NAME + this.getMessage("subject.definition.null");
         this.handleError(msg);
      }

      className = ctx.getDefinition().getDefinitionClassName();
      if (className == null || className.length() < 1)
      {
         msg = METHOD_NAME + this.getMessage("subject.definition.classnotset")
                 + ", context='" + contextName + "'";
         this.handleError(msg);
      }

      try
      {
         subject = (SubjectIF) Class.forName(className).newInstance();
      }
      catch (Exception ex)
      {
         msg = METHOD_NAME + "classname='" + className + "': " + ex.getMessage();
         this.handleError(msg);
      }

      subject.initialize(this, contextName);

      return subject;
   }

   /**
    * @param name
    * @param function
    */
   //----------------------------------------------------------------
   public final void setFunction(final String name, final FunctionIF function)
   //----------------------------------------------------------------
   {
      _functions.put(name, function);
      return;
   }

   /**
    * @param functions
    */
   //----------------------------------------------------------------
   public final void setFunctions(final Map<String, FunctionIF> functions)
   //----------------------------------------------------------------
   {
      _functions = functions;
      return;
   }

   /**
    * @param name
    * @return
    */
   //----------------------------------------------------------------
   public final FunctionIF getFunction(final String name)
   //----------------------------------------------------------------
   {
      FunctionIF function = null;

      if (_functions.containsKey(name))
      {
         function = _functions.get(name);
      }

      return function;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   public final Map<String, FunctionIF> getFunctions()
   //----------------------------------------------------------------
   {
      return _functions;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   public final Map<ConverterType, ConverterIF> getConverters()
   //----------------------------------------------------------------
   {
      return _converters;
   }

   //----------------------------------------------------------------
   public final DeciderManager getDeciderManager()
   //----------------------------------------------------------------
   {
      return _deciderMgr;
   }

   /**
    * Get a property value using the key.
    *
    * @param key
    * @return
    */
   //----------------------------------------------------------------
   public final String getProperty(final String key)
   //----------------------------------------------------------------
   {
      String val = null;
      String encrypted = null;

      if (key != null && key.length() > 0)
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

      return val;
   }

   /**
    * Set a property using the key and value.
    *
    * @param key
    * @param value
    */
   //----------------------------------------------------------------
   public final void setProperty(final String key, final String value)
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

   /*
    * =========================
    * ==== PRIVATE METHODS ====
    * =========================
    */
   //----------------------------------------------------------------
   private void checkDefaultContext()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":checkDefaultContext(): ";
      StringBuffer buf = null;
      Map<String, ContextIF> mapCtxs = null;
      ContextIF ctx = null;

      buf = new StringBuffer();

      /*
       * If the default context is NULL, send a warning and list
       * the available contexts
       */

      try
      {
         ctx = this.getContext(this.getDefaultContextName());
      }
      catch (Exception ex)
      {
         ctx = null;
      }

      if (ctx == null)
      {
         mapCtxs = this.getContexts();

         for (String ctxName : mapCtxs.keySet())
         {
            try
            {
               ctx = this.getContext(ctxName);
            }
            catch (Exception ex)
            {
               ctx = null;
            }
            if (ctx != null)
            {
               if (buf.length() > 0)
               {
                  buf.append(", ");
               }
               buf.append(ctxName);
            }
         }
         this.logWarning(METHOD_NAME + "Default Context '"
                 + this.getDefaultContextName()
                 + "' does not exist, available Contexts: '" + buf.toString() + "'");
      }
      else
      {
         _bDefCtx = true;
      }

      return;
   }

   //----------------------------------------------------------------
   private void initMessages(final String msgName) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":initMessages(String): ";
      String msg = null;
      ResBundleSettings settings = null;

      if (msgName == null || msgName.length() < 1)
      {
         this.handleError(METHOD_NAME + "msgName is null");
      }

      try
      {
         settings = new ResBundleSettings(msgName);
      }
      catch (Exception ex)
      {
         msg = METHOD_NAME + ex.getMessage() + ", msgName='" + msgName + "'";
         this.handleError(msg);
      }

      _messages = settings.getProperties();

      return;
   }

   //----------------------------------------------------------------
   private void handleError(final String msg) throws ConfigurationException
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
      throw new ConfigurationException(str);
   }

   //----------------------------------------------------------------
   private synchronized String getSessionData(SessionIF session)
   //----------------------------------------------------------------
   {
      String type = null;
      String clientid = null;
      String uniqueid = null;
      String contextid = null;

      StringBuilder buf = new StringBuilder();
      PrincipalIF principal = null;

      buf.append("[");

      if (session != null)
      {
         type = session.getType().toString();
         buf.append(type);

         clientid = session.getClientId();
         if (clientid != null)
         {
            buf.append(":").append(clientid);
         }

         principal = session.getPrincipal();
         if (principal != null && principal.getUniqueId() != null)
         {
            uniqueid = principal.getUniqueId().toString();
            if (uniqueid != null)
            {
               buf.append(":").append(uniqueid);
            }

            contextid = principal.getContextId();
            if (contextid != null)
            {
               buf.append(":").append(contextid);
            }
         }
      }
      else
      {
         buf.append("null_session");
      }

      buf.append("]");

      return buf.toString();
   }
}
