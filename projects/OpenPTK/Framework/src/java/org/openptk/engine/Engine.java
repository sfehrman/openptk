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
package org.openptk.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openptk.api.Opcode;
import org.openptk.api.State;
import org.openptk.authenticate.AuthenticatorIF;
import org.openptk.authorize.EnforcerIF;
import org.openptk.authorize.decider.DeciderIF;
import org.openptk.authorize.decider.DeciderManager;
import org.openptk.authorize.policy.PolicyIF;
import org.openptk.client.ClientIF;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.config.Configuration;
import org.openptk.config.mapper.AttrMapIF;
import org.openptk.context.ContextIF;
import org.openptk.context.actions.ActionIF;
import org.openptk.definition.SubjectIF;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.ConverterException;
import org.openptk.model.ModelIF;
import org.openptk.plugin.PluginIF;
import org.openptk.representation.ActionRepresentation;
import org.openptk.representation.AttrMapRepresentation;
import org.openptk.representation.AuthenticatorRepresentation;
import org.openptk.representation.CacheRepresentation;
import org.openptk.representation.ClientRepresentation;
import org.openptk.representation.ContextRepresentation;
import org.openptk.representation.ConverterRepresentation;
import org.openptk.representation.DeciderRepresentation;
import org.openptk.representation.EnforcerRepresentation;
import org.openptk.representation.EngineRepresentation;
import org.openptk.representation.ModelRepresentation;
import org.openptk.representation.PluginRepresentation;
import org.openptk.representation.PolicyRepresentation;
import org.openptk.representation.RelationshipRepresentation;
import org.openptk.representation.RepresentationIF;
import org.openptk.representation.RepresentationType;
import org.openptk.representation.SessionInfoRepresentation;
import org.openptk.representation.SessionRepresentation;
import org.openptk.representation.StatRepresentation;
import org.openptk.representation.SubjectRepresentation;
import org.openptk.representation.ViewRepresentation;
import org.openptk.session.ScanningSessionManager;
import org.openptk.session.SessionIF;
import org.openptk.session.SessionManagerIF;
import org.openptk.structure.ConverterIF;
import org.openptk.structure.ConverterType;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Engine extends Component implements EngineIF
//===================================================================
{

   private final Object _lockIndexes = new Object();
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _configFileName = null;
   private Configuration _configuration = null;
   private SessionManagerIF _sessionMgr = null;
   private Map<String, String> _indexToSessionId = null;
   private Map<String, String> _indexFromSessionId = null;
   private Map<ConverterType, ConverterIF> _converters = null;
   private Map<RepresentationType, RepresentationIF> _representations = null;
   private Map<String, Instrument> _instruments = null;

   /**
    * @param configFile
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   public Engine(final String configFile, final Properties props) throws ConfigurationException
   //----------------------------------------------------------------
   {
      this.setCategory(Category.ENGINE);
      this.setDescription("Abstract Engine");

      if (configFile != null && configFile.length() > 0)
      {
         _configFileName = configFile;
         _configuration = new Configuration(_configFileName, props);
      }
      else
      {
         throw new ConfigurationException(CLASS_NAME
                 + ": The name of the Config file is not set");
      }

      this.setDebug(_configuration.isDebug());
      this.setDebugLevel(_configuration.getDebugLevel());
      this.setProperties(_configuration.getProperties());

      this.start();

      this.setTimeStamp(SessionIF.TIMESTAMP_CREATED);

      return;
   }

   /**
    * @param action
    */
   //----------------------------------------------------------------
   @Override
   public synchronized void action(final EngineAction action)
   //----------------------------------------------------------------
   {
      switch (action)
      {
         case START:
         {
            this.start();
            break;
         }
         case STOP:
         {
            this.stop();
            break;
         }
         case RESTART:
         {
            this.stop();
            this.start();
            break;
         }
      }
      return;
   }

   /**
    * @param opcode
    * @param type
    * @param structIn
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public StructureIF execute(final Opcode opcode, final SessionIF session, final RepresentationType type, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      Long time = 0L;
      StructureIF structParamPath = null;
      StructureIF structCtx = null;
      StructureIF structOut = null;
      RepresentationIF rep = null;

      if (structIn == null)
      {
         this.handleError("Input Structure is null.");
      }

      if (type == null)
      {
         this.handleError("Representation Type is null.");
      }

      if (_representations.containsKey(type))
      {
         rep = _representations.get(type);

         if (rep == null)
         {
            this.handleError(METHOD_NAME + "Representation is null for type '"
                    + type.toString() + "'");
         }

         switch (type)
         {
            case SUBJECT:
               structParamPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
               if (structParamPath == null)
               {
                  this.handleError(METHOD_NAME + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
               }

               this.setTimeStamp(ComponentIF.EXECUTE_BEGIN);
               try
               {
                  structOut = rep.execute(opcode, session, structIn);
               }
               catch (Exception ex)
               {
                  this.handleError(METHOD_NAME + "rep.execute("
                          + opcode.toString() + ",struct): " + ex.getMessage());
               }
               this.setTimeStamp(ComponentIF.EXECUTE_END);

               structCtx = structParamPath.getChild(StructureIF.NAME_CONTEXTID);
               if (structCtx == null)
               {
                  this.handleError(METHOD_NAME + "Structure '" + StructureIF.NAME_CONTEXTID + "' is null");
               }
               time = this.getTimeStamp(ComponentIF.EXECUTE_END) - this.getTimeStamp(ComponentIF.EXECUTE_BEGIN);
               this.updateStats(structCtx, opcode, time);

               break;
            default:
               try
               {
                  structOut = rep.execute(opcode, session, structIn);
               }
               catch (Exception ex)
               {
                  this.handleError(METHOD_NAME + "rep.execute(): " + ex.getMessage());
               }
               break;
         }
      }
      else
      {
         this.handleError("RepresentationType '" + type.toString() + "' is not configured.");
      }

      return structOut;
   }

   /**
    * @param id
    * @return
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   @Override
   public final ContextIF getContext(final String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      return _configuration.getContext(id);
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getContextNames()
   //----------------------------------------------------------------
   {
      return _configuration.getContextNames();
   }

   /**
    * @param id
    * @return
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   @Override
   public final AuthenticatorIF getAuthenticator(final String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      return _configuration.getAuthenticator(id);
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getAuthenticatorNames()
   //----------------------------------------------------------------
   {
      return _configuration.getAuthenticatorNames();
   }

   /**
    * @param id
    * @return
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   @Override
   public final ClientIF getClient(final String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      return _configuration.getClient(id);
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getClientNames()
   //----------------------------------------------------------------
   {
      return _configuration.getClientNames();
   }

   /**
    * @param id
    * @return
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   @Override
   public final ModelIF getModel(final String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      return _configuration.getModel(id);
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getModelNames()
   //----------------------------------------------------------------
   {
      return _configuration.getModelNames();
   }

   /**
    * @param id
    * @return
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   @Override
   public final PluginIF getPlugin(final String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      return _configuration.getPlugin(id);
   }

   /**
    * @param id
    * @return
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   @Override
   public final ActionIF getAction(final String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      return _configuration.getAction(id);
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getPluginNames()
   //----------------------------------------------------------------
   {
      return _configuration.getPluginIds();
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getActionNames()
   //----------------------------------------------------------------
   {
      return _configuration.getActionIds();
   }

   /**
    * @param ctx
    * @return
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   @Override
   public final SubjectIF getSubject(final String ctx) throws ConfigurationException
   //----------------------------------------------------------------
   {
      return _configuration.getSubject(ctx);
   }

   /**
    * @param id
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final boolean containsSession(final String id)
   //----------------------------------------------------------------
   {
      boolean found = false;

      if (id != null && id.length() > 0)
      {
         found = _sessionMgr.contains(id);
      }

      return found;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getSessionIds()
   //----------------------------------------------------------------
   {
      String[] ids = null;

      ids = _sessionMgr.getIds();

      return ids;
   }

   /**
    * Returns the Session related to the id.
    * Note: This is a copy of the stored Session. If changes are made, the
    * updated Session will need to be put back with setSession()
    *
    * @param id
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final SessionIF getSession(final String id)
   //----------------------------------------------------------------
   {
      String sessionId = null;
      SessionIF session = null;

      /*
       * The "id" may be either the actual SessionId (guid) or
       * it can be an alternate index value (maybe jSessionId)
       * Assume it's the actual Session Id first
       */

      if (id != null && id.length() > 0)
      {
         if (_sessionMgr.contains(id))
         {
            sessionId = id;
         }
         else
         {
            if (_indexToSessionId.containsKey(id))
            {
               sessionId = _indexToSessionId.get(id);
            }
         }

         if (sessionId != null && sessionId.length() > 0)
         {
            session = _sessionMgr.get(sessionId);
            if (session != null)
            {
               if (session.getState() != State.VALID)
               {
                  this.removeSession(sessionId);
                  session = null;
               }
            }
            else
            {
               this.removeSession(sessionId);
            }
         }
      }

      return session;
   }

   /**
    * @param id
    * @param session
    */
   //----------------------------------------------------------------
   @Override
   public final void setSession(final String id, final SessionIF session)
   //----------------------------------------------------------------
   {
      if (id != null && id.length() > 0 && session != null)
      {
         _sessionMgr.set(id, session);
      }

      return;
   }

   /**
    * @param id
    * @param session
    * @param index
    */
   //----------------------------------------------------------------
   @Override
   public final void setSession(final String id, final SessionIF session, final String index)
   //----------------------------------------------------------------
   {
      if (id != null && id.length() > 0 && session != null
              && index != null && index.length() > 0)
      {
         session.setProperty(StructureIF.NAME_INDEX, index);

         _sessionMgr.set(id, session);

         synchronized (_lockIndexes)
         {
            _indexToSessionId.put(index, id);
            _indexFromSessionId.put(id, index);
         }
      }
      return;
   }

   /**
    * @param id
    */
   //----------------------------------------------------------------
   @Override
   public final void removeSession(final String id)
   //----------------------------------------------------------------
   {
      String index = null;

      if (id != null && id.length() > 0)
      {
         _sessionMgr.remove(id);
      }

      if (_indexFromSessionId.containsKey(id))
      {
         synchronized (_lockIndexes)
         {
            index = _indexFromSessionId.get(id);
            if (index != null && index.length() > 0)
            {
               if (_indexToSessionId.containsKey(index))
               {
                  _indexToSessionId.remove(index);
               }
            }
            _indexFromSessionId.remove(id);
         }
      }

      return;
   }

   /**
    * @param type
    * @param struct
    * @return
    * @throws ConverterException
    */
   //----------------------------------------------------------------
   @Override
   public final String encode(final ConverterType type, final StructureIF struct) throws ConverterException
   //----------------------------------------------------------------
   {
      return _converters.get(type).encode(struct);
   }

   /**
    * @param type
    * @param value
    * @return
    * @throws ConverterException
    */
   //----------------------------------------------------------------
   @Override
   public final StructureIF decode(final ConverterType type, final String value) throws ConverterException
   //----------------------------------------------------------------
   {
      return _converters.get(type).decode(value);
   }

   /**
    * @param str
    */
   //----------------------------------------------------------------
   @Override
   public final void logError(final String str)
   //----------------------------------------------------------------
   {
      _configuration.logError(str);
      return;
   }

   /**
    * @param session
    * @param str
    */
   //----------------------------------------------------------------
   @Override
   public final void logError(final SessionIF session, final String str)
   //----------------------------------------------------------------
   {
      _configuration.logError(session, str);
      return;
   }

   /**
    * @param str
    */
   //----------------------------------------------------------------
   @Override
   public final void logInfo(final String str)
   //----------------------------------------------------------------
   {
      _configuration.logInfo(str);
      return;
   }

   /**
    * @param session
    * @param str
    */
   //----------------------------------------------------------------
   @Override
   public final void logInfo(final SessionIF session, final String str)
   //----------------------------------------------------------------
   {
      _configuration.logInfo(session, str);
      return;
   }

   /**
    * @param str
    */
   //----------------------------------------------------------------
   @Override
   public final void logWarning(final String str)
   //----------------------------------------------------------------
   {
      _configuration.logWarning(str);
      return;
   }

   /**
    * @param session
    * @param str
    */
   //----------------------------------------------------------------
   @Override
   public final void logWarning(final SessionIF session, final String str)
   //----------------------------------------------------------------
   {
      _configuration.logWarning(session, str);
      return;
   }

   /**
    * @param obj
    * @param callerId
    */
   //----------------------------------------------------------------
   @Override
   public final void logData(final Object obj, final String callerId)
   //----------------------------------------------------------------
   {
      _configuration.logData(obj, callerId);
      return;
   }

   /**
    * @param contextId
    * @param oper
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Stats getStats(final String contextId, final Opcode oper)
   //----------------------------------------------------------------
   {
      Instrument inst = null;
      Stats stats = null;

      inst = _instruments.get(contextId);
      if (inst != null)
      {
         stats = inst.getStats(oper);
      }

      return stats;
   }

   /**
    * @param type
    * @param converter
    */
   //----------------------------------------------------------------
   @Override
   public final void setConverter(final ConverterType type, final ConverterIF converter)
   //----------------------------------------------------------------
   {
      if (type != null && converter != null)
      {
         _converters.put(type, converter);
      }
      return;
   }

   /**
    * @param type
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final ConverterIF getConverter(final ConverterType type)
   //----------------------------------------------------------------
   {
      ConverterIF converter = null;
      if (type != null && _converters.containsKey(type))
      {
         converter = _converters.get(type);
      }
      return converter;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final ConverterType[] getConverterTypes()
   //----------------------------------------------------------------
   {
      return _converters.keySet().toArray(new ConverterType[_converters.size()]);
   }

   /**
    * Get the DeciderManager, from the Configuration
    *
    * @return DeciderManager
    */
   //----------------------------------------------------------------
   @Override
   public final DeciderManager getDeciderManager()
   //----------------------------------------------------------------
   {
      return _configuration.getDeciderManager();
   }

   /**
    *
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getDeciderNames()
   //----------------------------------------------------------------
   {
      return _configuration.getDeciderIds();
   }

   /**
    *
    * @param id
    * @return
    * @throws ConfigurationExecption
    */
   //----------------------------------------------------------------
   @Override
   public final DeciderIF getDecider(String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      return _configuration.getDecider(id);
   }

   /**
    * 
    * @return 
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getEnforcerNames()
   //----------------------------------------------------------------
   {
      return _configuration.getEnforcerIds();
   }
   
   /**
    * 
    * @param id
    * @return
    * @throws ConfigurationException 
    */
   //----------------------------------------------------------------
   @Override
   public final EnforcerIF getEnforcer(String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      return _configuration.getEnforcer(id);
   }
   
   /**
    * 
    * @return 
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getPolicyNames()
   //----------------------------------------------------------------
   {
      return _configuration.getPolicyIds();
   }
   
   /**
    * 
    * @param id
    * @return
    * @throws ConfigurationException 
    */
   //----------------------------------------------------------------
   @Override
   public final PolicyIF getPolicy(String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      return _configuration.getPolicy(id);
   }
   
   /**
    * 
    * @return 
    */
   //----------------------------------------------------------------
   @Override
   public String[] getAttrMapNames()
   //----------------------------------------------------------------
   {
      return _configuration.getAttrMapIds();
   }
   
   /**
    * 
    * @param id
    * @return
    * @throws ConfigurationException 
    */
   //----------------------------------------------------------------
   @Override
   public AttrMapIF getAttrMap(String id) throws ConfigurationException
   //----------------------------------------------------------------
   {
      return _configuration.getAttrMap(id);
   }
   
   /*
    * *****************
    * PROTECTED METHODS
    * *****************
    */
   /**
    * @param msg
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   protected void exceptionConfiguration(final String msg) throws ConfigurationException
   //----------------------------------------------------------------
   {
      this.logError(msg);
      throw new ConfigurationException(msg);
   }

   /*
    * ***************
    * PRIVATE METHODS
    * ***************
    */
   //----------------------------------------------------------------
   private void updateStats(final StructureIF structCtx, final Opcode oper, final Long time)
   //----------------------------------------------------------------
   {
      String contextId = null;
      Stats stats = null;
      Instrument inst = null;

      if (structCtx != null)
      {
         contextId = structCtx.getValueAsString();
         if (contextId != null && contextId.length() > 0)
         {
            inst = _instruments.get(contextId);
            if (inst != null)
            {
               stats = inst.getStats(oper);
               stats.add(time);
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void start()
   //----------------------------------------------------------------
   {
      String[] contextNames = null;

      _sessionMgr = new ScanningSessionManager(this);
      _sessionMgr.startup();

      _indexToSessionId = new HashMap<String, String>();
      _indexFromSessionId = new HashMap<String, String>();
      _representations = new HashMap<RepresentationType, RepresentationIF>();
      _instruments = new HashMap<String, Instrument>();

      _representations.put(RepresentationType.ACTION, new ActionRepresentation(this));
      _representations.put(RepresentationType.ATTRMAP, new AttrMapRepresentation(this));
      _representations.put(RepresentationType.AUTHENTICATOR, new AuthenticatorRepresentation(this));
      _representations.put(RepresentationType.CACHE, new CacheRepresentation(this));
      _representations.put(RepresentationType.CLIENT, new ClientRepresentation(this));
      _representations.put(RepresentationType.CONTEXT, new ContextRepresentation(this));
      _representations.put(RepresentationType.CONVERTER, new ConverterRepresentation(this));
      _representations.put(RepresentationType.DECIDER, new DeciderRepresentation(this));
      _representations.put(RepresentationType.ENFORCER, new EnforcerRepresentation(this));
      _representations.put(RepresentationType.ENGINE, new EngineRepresentation(this));
      _representations.put(RepresentationType.MODEL, new ModelRepresentation(this));
      _representations.put(RepresentationType.PLUGIN, new PluginRepresentation(this));
      _representations.put(RepresentationType.POLICY, new PolicyRepresentation(this));
      _representations.put(RepresentationType.RELATIONSHIP, new RelationshipRepresentation(this));
      _representations.put(RepresentationType.SESSION, new SessionRepresentation(this));
      _representations.put(RepresentationType.SESSIONINFO, new SessionInfoRepresentation(this));
      _representations.put(RepresentationType.STAT, new StatRepresentation(this));
      _representations.put(RepresentationType.SUBJECT, new SubjectRepresentation(this));
      _representations.put(RepresentationType.VIEW, new ViewRepresentation(this));

      contextNames = _configuration.getContextNames();
      if (contextNames != null && contextNames.length > 0)
      {
         for (int i = 0; i < contextNames.length; i++)
         {
            _instruments.put(contextNames[i], new Instrument(contextNames[i]));
         }
      }

      _converters = _configuration.getConverters();

      this.setState(State.RUNNING);

      return;
   }

   //----------------------------------------------------------------
   private void stop()
   //----------------------------------------------------------------
   {
      _converters = null;
      _representations = null;
      _instruments = null;

      _sessionMgr.shutdown();

      this.setState(State.STOPPED);

      return;
   }

   //----------------------------------------------------------------
   private void handleError(final String msg) throws Exception
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
      throw new Exception(str);
   }

   /*
    * PRIVATE INNER CLASSES
    */
   //===================================================================
   private class Instrument
   //===================================================================
   {

      private String name = null;
      private Map<Opcode, Stats> stats = null;

      //-------------------------------------------------------------
      Instrument(final String name)
      //-------------------------------------------------------------
      {
         Opcode[] opcodes = null;
         this.name = name;
         this.stats = new HashMap<Opcode, Stats>();

         opcodes = Opcode.values();
         for (int i = 0; i < opcodes.length; i++)
         {
            this.stats.put(opcodes[i], new Stats());
         }
      }

      //-------------------------------------------------------------
      private Stats getStats(final Opcode oper)
      //-------------------------------------------------------------
      {
         return this.stats.get(oper);
      }
   }
}
