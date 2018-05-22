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

import org.openptk.api.Opcode;
import org.openptk.authenticate.AuthenticatorIF;
import org.openptk.authorize.EnforcerIF;
import org.openptk.authorize.decider.DeciderIF;
import org.openptk.authorize.decider.DeciderManager;
import org.openptk.authorize.policy.PolicyIF;
import org.openptk.client.ClientIF;
import org.openptk.common.ComponentIF;
import org.openptk.config.mapper.AttrMapIF;
import org.openptk.context.ContextIF;
import org.openptk.context.actions.ActionIF;
import org.openptk.definition.SubjectIF;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.ConverterException;
import org.openptk.model.ModelIF;
import org.openptk.plugin.PluginIF;
import org.openptk.representation.RepresentationType;
import org.openptk.session.SessionIF;
import org.openptk.structure.ConverterIF;
import org.openptk.structure.ConverterType;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public interface EngineIF extends ComponentIF
//===================================================================
{
   public static final String OPENPTK_VERSION = "2.2.0";
   public static final String ATTR_SERVLET_CONTEXT_ENGINE = "OpenPTK_Engine";
   public static final String PROP_AUTH_TOKEN_CLIENT = "auth.token.name.clientid";
   public static final String PROP_AUTH_TOKEN_CLIENT_CRED = "auth.token.name.clientcred";
   public static final String PROP_AUTH_TOKEN_TOKENPARAM = "auth.token.name.tokenparam";
   public static final String PROP_AUTH_TOKEN_PASSWORD = "auth.token.name.password";
   public static final String PROP_AUTH_TOKEN_USER = "auth.token.name.user";
   public static final String PROP_ENGINE_SESSION_KILL = "engine.session.kill";
   public static final String PROP_ENGINE_SESSION_TTL = "engine.session.ttl";
   public static final String PROP_ENGINE_SESSION_CACHE_TTL = "engine.session.cache.ttl";
   public static final String PROP_HTTP_SESSION_COOKIE_UNIQUEID = "http.session.cookie.uniqueid";
   public static final String PROP_HTTP_SESSION_COOKIE_HTTPONLY = "http.session.cookie.httponly";
   public static final String PROP_SEARCH_RESULTS_QUANTITY = "search.results.quantity";
   public static final String PROP_SEARCH_ATTRIBUTE_DEFAULT = "search.attribute.default";
   public static final String PROP_SECURITY_ENFORCER_ENGINE = "security.enforcer.engine";
   public static final String PROP_SECURITY_ENFORCER_SERVLET = "security.enforcer.servlet";
   public static final String PROP_SERVER_CONFIG_USERID = "server.config.userid";
   public static final String PROP_SERVER_CONFIG_PASSWORD = "server.config.password";
   public static final String PROP_SERVER_COOKIEPATH = "server.cookiepath";
   public static final String PROP_SERVER_DEFAULTCLIENT = "server.defaultclient";
   public static final String PROP_SERVER_INFO = "server.info";
   public static final String PROP_INPUT_ALLOWED_CHARACTERS = "allowed.characters.web";
   public static final String MSG_ENGINE_STARTUP_SUCCESS = "The OpenPTK Server is ready.";
   public static final String MSG_ENGINE_NULL = "Engine not found in context";
   public static final String MSG_ENGINE_NOT_VALID = "Object in context is not a valid Engine object";
   public static final String MSG_ENGINE_PROPERTY_NOT_AVAILABLE = "Required configuration property no found";
   public static final String MSG_SERVLETCONTEXT_NULL = "ServletContext is null";
   public static final String MSG_SERVLETCONFIG_NULL = "ServletConfig is null";

   /**
    * @param action
    */
   public void action(EngineAction action);

   /**
    * @return
    */
   public String[] getContextNames();

   /**
    * @param id
    * @return
    * @throws ConfigurationException
    */
   public ContextIF getContext(String id) throws ConfigurationException;

   /**
    * @return
    */
   public String[] getAuthenticatorNames();

   /**
    * @param id
    * @return
    * @throws ConfigurationException
    */
   public AuthenticatorIF getAuthenticator(String id) throws ConfigurationException;

   /**
    * @return
    */
   public String[] getClientNames();

   /**
    * @param id
    * @return
    * @throws ConfigurationException
    */
   public ClientIF getClient(String id) throws ConfigurationException;

   /**
    * @return
    */
   public String[] getModelNames();

   /**
    * @param id
    * @return
    * @throws ConfigurationException
    */
   public ModelIF getModel(String id) throws ConfigurationException;

   /**
    * @param ctx
    * @return
    * @throws ConfigurationException
    */
   public SubjectIF getSubject(String ctx) throws ConfigurationException;

   /**
    * @param id
    * @return
    */
   public boolean containsSession(String id);

   /**
    * @return
    */
   public String[] getSessionIds();

   /**
    * Returns the Session related to the id.
    * Note: This is a copy of the stored Session.  If changes are made, the
    * updated Session will need to be put back with setSession()
    * @param id
    * @return
    */
   public SessionIF getSession(String id);

   /**
    * @param id
    * @param session
    */
   public void setSession(String id, SessionIF session);

   /**
    * @param id
    * @param session
    * @param index
    */
   public void setSession(String id, SessionIF session, String index);

   /**
    * @param id
    */
   public void removeSession(String id);

   /**
    * @param type
    * @param struct
    * @return
    * @throws ConverterException
    */
   public String encode(ConverterType type, StructureIF struct) throws ConverterException;

   /**
    * @param type
    * @param value
    * @return
    * @throws ConverterException
    */
   public StructureIF decode(ConverterType type, String value) throws ConverterException;

   /**
    * @param opcode
    * @param type
    * @param input
    * @return
    * @throws Exception
    */
   public StructureIF execute(Opcode opcode, SessionIF session, RepresentationType type, StructureIF input) throws Exception;

   /**
    * @param str
    */
   public void logError(String str);

   /**
    * @param session
    * @param str
    */
   public void logError(SessionIF session, String str);

   /**
    * @param str
    */
   public void logInfo(String str);

   /**
    * @param session
    * @param str
    */
   public void logInfo(SessionIF session, String str);

   /**
    * @param str
    */
   public void logWarning(String str);

   /**
    * @param session
    * @param str
    */
   public void logWarning(SessionIF session, String str);

   /**
    * @param obj
    * @param callerId
    */
   public void logData(Object obj, String callerId);

   /**
    * @param contextId
    * @param opcode
    * @return
    */
   public Stats getStats(String contextId, Opcode opcode);

   /**
    * @param type
    * @param converter
    */
   public void setConverter(ConverterType type, ConverterIF converter);

   /**
    * @param type
    * @return
    */
   public ConverterIF getConverter(ConverterType type);

   /**
    * @return
    */
   public ConverterType[] getConverterTypes();

   /**
    * @return
    */
   public String[] getPluginNames();

   /**
    * @param id
    * @return
    * @throws ConfigurationException
    */
   public PluginIF getPlugin(String id) throws ConfigurationException;

   /**
    * @return
    */
   public String[] getActionNames();

   /**
    * @param id
    * @return
    * @throws ConfigurationException
    */
   public ActionIF getAction(String id) throws ConfigurationException;

   /**
    *
    * @return DeciderManager
    */
   public DeciderManager getDeciderManager();
   
   /**
    * 
    * @return 
    */
   public String[] getDeciderNames();
   
   /**
    * 
    * @param id
    * @return
    * @throws ConfigurationExecption 
    */
   public DeciderIF getDecider(String id) throws ConfigurationException;
   
   /**
    * 
    * @return 
    */
   public String[] getEnforcerNames();
   
   /**
    * 
    * @param id
    * @return
    * @throws ConfigurationException 
    */
   public EnforcerIF getEnforcer(String id) throws ConfigurationException;
   
   /**
    * 
    * @return 
    */
   public String[] getPolicyNames();
   
   /**
    * 
    * @param id
    * @return
    * @throws ConfigurationException 
    */
   public PolicyIF getPolicy(String id) throws ConfigurationException;
   
   /**
    * 
    * @return 
    */
   public String[] getAttrMapNames();
   
   /**
    * 
    * @param id
    * @return
    * @throws ConfigurationException 
    */
   public AttrMapIF getAttrMap(String id) throws ConfigurationException;
}
