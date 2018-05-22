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
package org.openptk.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openptk.authenticate.AuthenticatorIF;
import org.openptk.authorize.EnforcerIF;
import org.openptk.authorize.decider.DeciderIF;
import org.openptk.authorize.policy.PolicyIF;
import org.openptk.client.ClientIF;
import org.openptk.common.AssignmentIF;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.config.mapper.AttrMapIF;
import org.openptk.context.ContextIF;
import org.openptk.context.actions.ActionIF;
import org.openptk.crypto.CryptoIF;
import org.openptk.definition.DefinitionIF;
import org.openptk.exception.ConfigurationException;
import org.openptk.logging.LoggingIF;
import org.openptk.model.ModelIF;
import org.openptk.plugin.PluginIF;
import org.openptk.structure.ConverterIF;
import org.openptk.structure.ConverterType;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Config extends Component implements ConfigIF
//===================================================================
{

   protected String _defaultCtxName = null;
   protected String _loggerName = null;
   protected String _encryptorName = null;
   protected String _serviceClassName = null;
   protected Properties _defaultProps = null;
   protected Configuration _configuration = null;
   protected Operation[] _operations = Operation.values();
   protected AssignmentIF.Type[] _assignmentTypes = AssignmentIF.Type.values();
   protected Map<String, CryptoIF> _encryptors = null;
   protected Map<String, AuthenticatorIF> _authenticators = null;
   protected Map<String, ClientIF> _clients = null;
   protected Map<String, ContextIF> _contexts = null;
   protected Map<String, DefinitionIF> _definitions = null;
   protected Map<String, Properties> _connectionprops = null;
   protected Map<String, LoggingIF> _loggers = null;
   protected Map<String, ComponentIF> _associations = null;
   protected Map<String, ComponentIF> _attrgroups = null;
   protected Map<String, ModelIF> _models = null;
   protected Map<String, Map<String, String>> _mapCtxRelCtx = null;
   protected Map<ConverterType, ConverterIF> _converters = null;
   protected Map<String, PluginIF> _plugins = null;
   protected Map<String, ActionIF> _actions = null;
   protected Map<String, DeciderIF> _deciders = null;
   protected Map<String, EnforcerIF> _enforcers = null;
   protected Map<String, PolicyIF> _policies = null;
   protected Map<String, AttrMapIF> _attrMaps = null;

   //----------------------------------------------------------------
   public Config()
   //----------------------------------------------------------------
   {
      super();

      _encryptors = new HashMap<String, CryptoIF>();
      _authenticators = new HashMap<String, AuthenticatorIF>();
      _clients = new HashMap<String, ClientIF>();
      _contexts = new HashMap<String, ContextIF>();
      _definitions = new HashMap<String, DefinitionIF>();
      _connectionprops = new HashMap<String, Properties>();
      _loggers = new HashMap<String, LoggingIF>();
      _associations = new HashMap<String, ComponentIF>();
      _attrgroups = new HashMap<String, ComponentIF>();
      _models = new HashMap<String, ModelIF>();
      _converters = new HashMap<ConverterType, ConverterIF>();
      _plugins = new HashMap<String, PluginIF>();
      _actions = new HashMap<String, ActionIF>();
      _deciders = new HashMap<String, DeciderIF>();
      _enforcers = new HashMap<String, EnforcerIF>();
      _policies = new HashMap<String, PolicyIF>();
      _attrMaps = new HashMap<String, AttrMapIF>();

      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String getDefaultContextName()
   //----------------------------------------------------------------
   {
      return _defaultCtxName;
   }

   /**
    * @param name
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final ContextIF getContext(final String name)
   //----------------------------------------------------------------
   {
      ContextIF ctx = null;

      if (_contexts.containsKey(name))
      {
         ctx = _contexts.get(name);
      }

      return ctx;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Map<String, ContextIF> getContexts()
   //----------------------------------------------------------------
   {
      return _contexts;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getContextNames()
   //----------------------------------------------------------------
   {
      return _contexts.keySet().toArray(new String[0]);
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String getLoggerName()
   //----------------------------------------------------------------
   {
      return _loggerName;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Map<String, LoggingIF> getLoggers()
   //----------------------------------------------------------------
   {
      return _loggers;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Map<String, AuthenticatorIF> getAuthenticators()
   //----------------------------------------------------------------
   {
      return _authenticators;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Map<String, ClientIF> getClients()
   //----------------------------------------------------------------
   {
      return _clients;
   }

   //----------------------------------------------------------------
   @Override
   public final Map<String, PolicyIF> getPolicies()
   //----------------------------------------------------------------
   {
      return _policies;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Map<String, DeciderIF> getDeciders()
   //----------------------------------------------------------------
   {
      return _deciders;
   }

   /**
    *
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Map<String, EnforcerIF> getEnforcers()
   //----------------------------------------------------------------
   {
      return _enforcers;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Map<String, ModelIF> getModels()
   //----------------------------------------------------------------
   {
      return _models;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Map<ConverterType, ConverterIF> getConverters()
   //----------------------------------------------------------------
   {
      return _converters;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Map<String, PluginIF> getPlugins()
   //----------------------------------------------------------------
   {
      return _plugins;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Map<String, ActionIF> getActions()
   //----------------------------------------------------------------
   {
      return _actions;
   }

   //----------------------------------------------------------------
   @Override
   public Map<String, AttrMapIF> getAttrMaps()
   //----------------------------------------------------------------
   {
      return _attrMaps;
   }

   /**
    * @param msg
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   protected void handleError(final String msg) throws ConfigurationException
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
}
