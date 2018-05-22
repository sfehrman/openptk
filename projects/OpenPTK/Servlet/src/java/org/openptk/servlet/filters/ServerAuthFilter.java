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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.servlet.filters;

import java.io.IOException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openptk.api.Constants;
import org.openptk.api.State;
import org.openptk.authenticate.AuthenticatorIF;
import org.openptk.authenticate.AuthenticatorType;
import org.openptk.authenticate.BasicCredentials;
import org.openptk.authenticate.BasicPrincipal;
import org.openptk.authenticate.PrincipalIF;
import org.openptk.authorize.UrlTarget;
import org.openptk.authorize.TargetIF;
import org.openptk.authorize.decider.DeciderManager;
import org.openptk.authorize.decider.DeciderIF;
import org.openptk.common.ComponentIF;
import org.openptk.engine.EngineIF;
import org.openptk.exception.AuthenticationException;
import org.openptk.exception.AuthorizationException;
import org.openptk.exception.ConfigurationException;
import org.openptk.logging.Logger;
import org.openptk.session.BasicSession;
import org.openptk.session.SessionIF;
import org.openptk.session.SessionType;

/**
 *
 * @author Derrick Harcey, Sun Microsystems, Inc.
 *
 */
//===================================================================
public class ServerAuthFilter extends BaseFilter
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String PATH_RESOURCES = "/" + Constants.RESOURCES;
   private static final String PATH_LOGIN = "/" + Constants.LOGIN;
   private static final String PATH_LOGOUT = "/" + Constants.LOGOUT;
   private static final String PARAM_GOTO = Constants.GOTO;
   private String _cookieName = null;
   private String _cookiePath = null;
   private String _defaultClient = null;
   private String _httpParamClientId = null;
   private String _httpParamClientCred = null;
   private String _httpParamPassword = null;
   private String _httpParamUser = null;
   private String _httpHeaderToken = null;
   private ServletContext _ctx = null;
   private EngineIF _engine = null;   // OpenPTK
   private DeciderIF _decider = null; // OpenPTK

   private enum Mode
   {

      NOTHING, REMOVE, CREATE
   };

   //----------------------------------------------------------------
   @Override
   public void init(FilterConfig config) throws ServletException
   //----------------------------------------------------------------
   {
      Object obj = null;
      String METHOD_NAME = CLASS_NAME + ":init(FilterConfig): ";
      String propName = null;
      String propValue = null;
      String enforcerId = null;
      DeciderManager dm = null;

      _ctx = config.getServletContext();
      obj = _ctx.getAttribute(EngineIF.ATTR_SERVLET_CONTEXT_ENGINE);

      if (obj == null)
      {
         this.handleError(METHOD_NAME
                 + EngineIF.MSG_ENGINE_NOT_VALID + ", "
                 + EngineIF.ATTR_SERVLET_CONTEXT_ENGINE);
      }

      if (obj instanceof EngineIF)
      {
         //Get the OpenPTK Server engine
         _engine = (EngineIF) obj;
      }
      else
      {
         //The OpenPTK Server engine is invalid
         this.handleError(METHOD_NAME
                 + EngineIF.MSG_ENGINE_NOT_VALID + ", "
                 + EngineIF.ATTR_SERVLET_CONTEXT_ENGINE
                 + "Servlet Container Log may have more related error messages");
      }

      // Get init parameters

      propName = EngineIF.PROP_HTTP_SESSION_COOKIE_UNIQUEID;
      propValue = _engine.getProperty(propName);
      if (propValue != null && propValue.length() > 0)
      {
         _cookieName = propValue;
      }
      else
      {
         this.handleError(METHOD_NAME
                 + EngineIF.MSG_ENGINE_PROPERTY_NOT_AVAILABLE + ", '"
                 + propName + "' not found, required");
      }

      propName = EngineIF.PROP_AUTH_TOKEN_TOKENPARAM;
      propValue = _engine.getProperty(propName);
      if (propValue != null && propValue.length() > 0)
      {
         _httpHeaderToken = propValue;
      }
      else
      {
         Logger.logInfo(METHOD_NAME
                 + EngineIF.MSG_ENGINE_PROPERTY_NOT_AVAILABLE + ", '"
                 + propName + "' not found");
      }

      propName = EngineIF.PROP_SERVER_COOKIEPATH;
      propValue = _engine.getProperty(propName);
      if (propValue != null && propValue.length() > 0)
      {
         _cookiePath = propValue;
      }
      else
      {
         this.handleError(METHOD_NAME
                 + EngineIF.MSG_ENGINE_PROPERTY_NOT_AVAILABLE + ", '"
                 + propName + "' not found, required");
      }

      propName = EngineIF.PROP_SERVER_DEFAULTCLIENT;
      propValue = _engine.getProperty(propName);
      if (propValue != null && propValue.length() > 0)
      {
         _defaultClient = propValue;
      }
      else
      {
         Logger.logInfo(METHOD_NAME
                 + EngineIF.MSG_ENGINE_PROPERTY_NOT_AVAILABLE + ", '"
                 + propName + "' not found");
      }

      propName = EngineIF.PROP_AUTH_TOKEN_CLIENT;
      propValue = _engine.getProperty(propName);
      if (propValue != null && propValue.length() > 0)
      {
         _httpParamClientId = propValue;
      }
      else
      {
         Logger.logInfo(METHOD_NAME
                 + EngineIF.MSG_ENGINE_PROPERTY_NOT_AVAILABLE + ", '"
                 + propName + "' not found");
      }

      propName = EngineIF.PROP_AUTH_TOKEN_CLIENT_CRED;
      propValue = _engine.getProperty(propName);
      if (propValue != null && propValue.length() > 0)
      {
         _httpParamClientCred = propValue;
      }
      else
      {
         Logger.logInfo(METHOD_NAME
                 + EngineIF.MSG_ENGINE_PROPERTY_NOT_AVAILABLE + ", '"
                 + propName + "' not found");
      }

      propName = EngineIF.PROP_AUTH_TOKEN_PASSWORD;
      propValue = _engine.getProperty(propName);
      if (propValue != null && propValue.length() > 0)
      {
         _httpParamPassword = propValue;
      }
      else
      {
         Logger.logInfo(METHOD_NAME
                 + EngineIF.MSG_ENGINE_PROPERTY_NOT_AVAILABLE + ", '"
                 + propName + "' not found");
      }

      propName = EngineIF.PROP_AUTH_TOKEN_USER;
      propValue = _engine.getProperty(propName);
      if (propValue != null && propValue.length() > 0)
      {
         _httpParamUser = propValue;
      }
      else
      {
         Logger.logInfo(METHOD_NAME
                 + EngineIF.MSG_ENGINE_PROPERTY_NOT_AVAILABLE + ", '"
                 + propName + "' not found");
      }

      propName = EngineIF.PROP_SECURITY_ENFORCER_SERVLET;
      propValue = _engine.getProperty(propName);
      if (propValue != null && propValue.length() > 0)
      {
         enforcerId = propValue;
      }
      else
      {
         this.handleError(METHOD_NAME
                 + EngineIF.MSG_ENGINE_PROPERTY_NOT_AVAILABLE + ", '"
                 + propName + "' not found, required");
      }

      dm = _engine.getDeciderManager();
      if (dm == null)
      {
         this.handleError(METHOD_NAME + "DeciderManager is null");
      }

      try
      {
         _decider = dm.getDecider(enforcerId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + "Failed to get Decider: " + ex.getMessage());
      }
      if (_decider == null)
      {
         this.handleError(METHOD_NAME + "Decider is null");
      }

      this.setDebug(_engine.isDebug());
      this.setDebugLevel(_engine.getDebugLevel());

      return;
   }

   //----------------------------------------------------------------
   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
           throws IOException, ServletException
   //----------------------------------------------------------------
   {
      boolean isAuthenticated = false;
      boolean doAuthen = true;
      String METHOD_NAME = CLASS_NAME + ":doFilter(): ";
      String gotoURI = null;
      String sessionId = null;
      String requestURI = null;
      String contextPath = null;
      String[] notEnforcedURIs = null;
      List<String> sessionList = null;
      Map<String, Object> authInfo = null;
      Action cookieAction = null;
      HttpServletRequest hrequest = null;  // Servlet
      HttpServletResponse hresponse = null; // Servlet
      AuthRequestWrapper requestWrapper = null; // Servlet
      Cookie cookie = null; // Servlet
      SessionIF session = null;      // OpenPTK
      ComponentIF authorized = null; // OpenPTK
      TargetIF target = null;        // OpenPTK

      if (request == null)
      {
         this.handleError("ServletRequest is null");
      }

      if (response == null)
      {
         this.handleError("ServletResponse is null");
      }

      if (!(request instanceof HttpServletRequest))
      {
         this.handleError("non-HTTP protocols not allowed!.");
      }

      if (_engine == null)
      {
         this.handleError("Engine is null, check configuration file.");
      }

      hrequest = (HttpServletRequest) request;
      hresponse = (HttpServletResponse) response;

      if (this.getDebugLevelAsInt() > 2)
      {
         requestWrapper = new AuthRequestWrapper(hrequest, _cookieName, _engine.isDebug());
      }
      else
      {
         requestWrapper = new AuthRequestWrapper(hrequest, _cookieName);
      }

      cookieAction = new Action();

      if (_engine.isDebug())
      {
         this.requestInfo(hrequest);
      }

      /*
       * Check for existing session, Get the OpenPTK SessionId from the HTTP Cookie
       */

      sessionList = this.getSessionListFromCookies(hrequest.getCookies());
      switch (sessionList.size())
      {
         case 0: // No sessions
            sessionId = null;
            break;
         case 1: // Only one
            sessionId = sessionList.get(0);
            break;
         default: // More than one, not normal
            Logger.logWarning(METHOD_NAME + "Found " + sessionList.size()
                    + " sessions ... there can be only one (using last one)");
            sessionId = sessionList.get(sessionList.size() - 1);
            break;
      }

      requestURI = hrequest.getRequestURI();
      contextPath = hrequest.getContextPath();

      /*
       * Check for /login request
       */

      if (requestURI.equals(contextPath + PATH_LOGIN))
      {
         /*
          * Get authentication parameters from the HTTP Request
          * store parameters in a hashmap
          */

         authInfo = this.getLoginParameters(hrequest);

         /*
          * Do not attempt to authenticate if no clientId is present
          */

         if (!authInfo.containsKey(Constants.CLIENTID))
         {
            doAuthen = false;
         }

         /*
          * do authentication if necessary
          */

         if (doAuthen)
         {
            isAuthenticated = this.authenticate(authInfo, sessionList, cookieAction);

            if (_engine.isDebug())
            {
               Logger.logInfo(METHOD_NAME + "Is Authenticated: " + isAuthenticated);
            }

            if (!isAuthenticated)
            {
               /*
                * Explicit login attempt failed, send HTTP error code back.
                */

               Logger.logWarning(METHOD_NAME
                       + "Explicit login attempt failed, sent HTTP error code back");

               gotoURI = request.getParameter(PARAM_GOTO);

               if (gotoURI != null)
               {
                  if (gotoURI.length() > 0)
                  {
                     hresponse.sendRedirect(gotoURI + "?login=failed");
                  }
               }
               else
               {
                  hresponse.sendError(hresponse.SC_UNAUTHORIZED, "Login Failed!");
               }

               return;
            }
         }
         else
         {
            /*
             * Explicit login attempt not performed, send HTTP error code back.
             */

            gotoURI = request.getParameter(PARAM_GOTO);

            if (gotoURI != null)
            {
               if (gotoURI.length() > 0)
               {
                  hresponse.sendRedirect(gotoURI + "?login=failed");
               }
            }
            else
            {
               hresponse.sendError(hresponse.SC_UNAUTHORIZED, "Login Failed!");
            }

            return;
         }

      }

      /*
       * only check the default authenticator if
       * the existing session is invalid (used for global anonymous access)
       */

      if (!isAuthenticated && !_engine.containsSession(sessionId))
      {
         authInfo = this.getLoginParameters(hrequest);
         authInfo.put(Constants.CLIENTID, _defaultClient);

         isAuthenticated = this.authenticate(authInfo, sessionList, cookieAction);
      }
      else
      {
         isAuthenticated = true;
      }

      if (!isAuthenticated)
      {
         /*
          * This string array contains the URI's which will not
          * require a session to be present to access
          */

         notEnforcedURIs = new String[]
         {
            contextPath + PATH_LOGIN,
            contextPath + PATH_LOGOUT
         };

         if (!(Arrays.asList(notEnforcedURIs).contains(requestURI)))
         {
            /*
             * Authenticators failed, and anonymous authentication not allowed
             * Redirect to login error page
             */

            hresponse.sendRedirect(contextPath + PATH_LOGIN);
         }
      }
      else if (authInfo != null)
      {
         if (authInfo.containsKey(Constants.SESSIONID))
         {
            sessionId = (String) authInfo.get(Constants.SESSIONID);
         }
      }

      /*
       * Check for /logout request
       */

      if (requestURI.equals(contextPath + PATH_LOGOUT))
      {
         /*
          * Invalidate the existing session.
          * Action = remove cookie
          */

         this.invalidateSessions(sessionList);
         cookieAction.setMode(Mode.REMOVE);

         /*
          * Clear the existing session value and
          * set the session type to lowest possible value (anon)\
          */

         isAuthenticated = false;
      }

      /*
       *  /resources request
       */

      if (requestURI.substring(contextPath.length()).startsWith(PATH_RESOURCES))
      {
         // Get Session
         session = _engine.getSession(sessionId);

         if (session != null)
         {
            try
            {
               target = new UrlTarget(hrequest.getRequestURL().toString());
            }
            catch (AuthorizationException ex)
            {
               Logger.logError(METHOD_NAME
                       + "Target Exception: " + ex.getMessage()
                       + ", SessionId='" + sessionId + "'");
               hresponse.sendError(hresponse.SC_UNAUTHORIZED, "Request Failed!");
            }

            if (target.getState() != State.ERROR)
            {
               try
               {
                  // Perform Authorization
                  authorized = _decider.check(session, target);
               }
               catch (AuthorizationException ex)
               {
                  Logger.logError(METHOD_NAME
                          + "Decider Check Exception: " + ex.getMessage()
                          + ", SessionId='" + sessionId + "'");
                  hresponse.sendError(hresponse.SC_UNAUTHORIZED, "Request Failed!");
               }

               if (authorized.getState() != State.ALLOWED)
               {
                  // Not authorized, return a 401
                  hresponse.sendError(hresponse.SC_UNAUTHORIZED, "Request Denied!");
                  _engine.logWarning(session, METHOD_NAME + "401: Request Denied:"
                          + " State: " + authorized.getStateAsString()
                          + ", Status: '" + authorized.getStatus() + "'");
               }
            }
            else
            {
               // Not authorized, return a 401
               hresponse.sendError(hresponse.SC_UNAUTHORIZED, "Request Invalid!");
            }
         }
         else
         {
            Logger.logWarning(METHOD_NAME
                    + "Session '" + sessionId + "' does not exist, can not process request");
            hresponse.sendError(hresponse.SC_UNAUTHORIZED, "Request Denied!");
         }
      }

      switch (cookieAction.getMode())
      {
         case NOTHING:
            break;
         case CREATE:
            cookie = new Cookie(_cookieName, sessionId);
            cookie.setPath(_cookiePath);
            cookie.setMaxAge(-1); // do not persist cookie when browser is closed
            requestWrapper.setAddSessionCookie(cookie);
            hresponse.addCookie(cookie);
            break;
         case REMOVE:
            cookie = new Cookie(_cookieName, sessionId);
            cookie.setPath(_cookiePath);
            cookie.setMaxAge(0); // browser should remove the cookie
            requestWrapper.setRemoveSessionCookie();
            hresponse.addCookie(cookie);
            break;
      }

      if (_engine.isDebug())
      {
         Logger.logInfo(METHOD_NAME + "Cookie action [" + cookieAction.getMode().toString()
                 + "] " + _cookieName + "=" + (sessionId != null ? sessionId : ""));
      }

      chain.doFilter(requestWrapper, hresponse);

      return;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   
   @Override
   protected void uniqueInfo(HttpServletRequest hrequest)
   {
      return;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private Map<String, Object> getLoginParameters(HttpServletRequest hrequest)
   //----------------------------------------------------------------
   {
      String value = null;
      Map<String, Object> loginParams = null;


      loginParams = new HashMap<String, Object>();

      value = hrequest.getParameter(_httpParamUser);
      if (value != null && value.length() > 0)
      {
         loginParams.put(Constants.USER, value);
      }

      value = null;
      value = hrequest.getParameter(_httpParamPassword);
      if (value != null && value.length() > 0)
      {
         loginParams.put(Constants.PASSWORD, value);
      }

      value = null;
      value = hrequest.getParameter(_httpParamClientId);
      if (value != null && value.length() > 0)
      {
         loginParams.put(Constants.CLIENTID, value);
      }

      value = null;
      value = hrequest.getParameter(_httpParamClientCred);
      if (value != null && value.length() > 0)
      {
         loginParams.put(Constants.CLIENTCRED, value);
      }

      value = null;
      value = hrequest.getParameter(_httpHeaderToken);
      if (value != null && value.length() > 0)
      {
         loginParams.put(Constants.TOKEN, value);
      }

      return loginParams;
   }

   //----------------------------------------------------------------
   private synchronized boolean authenticate(Map<String, Object> authInfo,
           List<String> sessionList, Action cookieAction) throws ServletException
   //----------------------------------------------------------------
   {
      boolean isAuthenticated = false;
      boolean done = false;
      String[] auths = null;
      String METHOD_NAME = CLASS_NAME + ":authenticate(): ";
      String authenticatorName = null;
      String userId = null;
      String password = null;
      String token = null;
      String clientId = null;
      String sessionVal = null;
      StringBuilder buf = new StringBuilder();
      List<String> authsList = null;
      SessionType authLevel = null;         // OpenPTK
      BasicCredentials creds = null;        // OpenPTK
      AuthenticatorIF authenticator = null; // OpenPTK
      PrincipalIF principal = null;         // OpenPTK

      userId = (String) authInfo.get(Constants.USER);
      password = (String) authInfo.get(Constants.PASSWORD);
      token = (String) authInfo.get(Constants.TOKEN);
      clientId = (String) authInfo.get(Constants.CLIENTID);

      if (clientId == null || clientId.length() < 1)
      {
         this.handleError(METHOD_NAME + "ClientId is null");
      }

      try
      {
         auths = _engine.getClient(clientId).getAuthenticatorIds();
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      authsList = Arrays.asList(auths);

      if (_engine.isDebug())
      {
         buf.append("Authenticators=");
         if (authsList != null)
         {
            buf.append(authsList.toString());
         }
         else
         {
            buf.append("(null)");
         }
         Logger.logInfo(METHOD_NAME + buf.toString());
      }

      /*
       * Perform authentication
       * Process each Authenticator, until one is successful
       */

      if (authsList != null && !authsList.isEmpty())
      {
         Iterator<String> iterator = authsList.iterator();
         while (iterator.hasNext() && !done)
         {
            authenticatorName = iterator.next();
            isAuthenticated = false;

            try
            {
               authenticator = _engine.getAuthenticator(authenticatorName);
            }
            catch (ConfigurationException ex)
            {
               this.handleError(METHOD_NAME + ex.getMessage());
            }

            if (authenticator != null)
            {
               authLevel = authenticator.getLevel();
               creds = new BasicCredentials(authInfo);
               principal = new BasicPrincipal(creds);

               if (_engine.isDebug())
               {
                  Logger.logInfo(METHOD_NAME + "Authenticator=" + authenticatorName + ":"
                          + authLevel + ":" + authenticator.getType().toString());
               }

               if (authenticator.getType() == AuthenticatorType.IDPASS)
               {
                  /*
                   * Check for userId and Password
                   * Authentication parameters in request
                   */
                  if (userId != null && password != null
                          && userId.length() > 0 && password.length() > 0)
                  {
                     isAuthenticated = this.doAuthenticate(authenticator, principal);
                     if (isAuthenticated)
                     {
                        done = true;
                     }
                  }
               }
               else if (authenticator.getType() == AuthenticatorType.TOKEN)
               {
                  if (token != null && token.length() > 0)
                  {
                     isAuthenticated = this.doAuthenticate(authenticator, principal);
                     if (isAuthenticated)
                     {
                        done = true;
                     }
                  }
               }
               else if (authenticator.getType() == AuthenticatorType.ANON)
               {
                  if (userId == null && password == null && token == null)
                  {
                     isAuthenticated = this.doAuthenticate(authenticator, principal);
                     if (isAuthenticated)
                     {
                        done = true;
                     }
                  }
               }
            }
            else
            {
               Logger.logWarning(METHOD_NAME + "Authenticator '"
                       + authenticatorName + "' is null, skipping it");
            }
         }

         /*
          * If authentication was successful, create session id and update Session
          */

         if (isAuthenticated)
         {
            sessionVal = org.openptk.util.UniqueId.getUniqueId();

            if (authLevel != null)
            {
               this.invalidateSessions(sessionList);
               this.updateSession(sessionVal, clientId, principal, authLevel, cookieAction);
               authInfo.put(Constants.SESSIONID, sessionVal);
            }
            else
            {
               Logger.logError(METHOD_NAME + "Authenticator '"
                       + authenticatorName + "' has a null authLevel");
            }
         }
      }
      else
      {
         Logger.logError(METHOD_NAME
                 + "Client '" + clientId + "' does not have any authenticators");
      }

      return isAuthenticated;
   }

   //----------------------------------------------------------------
   private List<String> getSessionListFromCookies(Cookie[] cookies)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getSessionListFromCookies(): ";
      String name = null;
      String value = null;
      String msg = null;
      List<String> sessionList = null;
      SessionIF session = null; // OpenPTK

      /*
       * It's possible that there could be more than one Cookie with the same name
       * Get each one and see if the value (sessionId) exists in the Engine
       * If it's valid, add the sessionId to the List that is returned
       */

      sessionList = new LinkedList<String>();

      if (cookies != null && cookies.length > 0)
      {
         for (Cookie cookie : cookies)
         {
            if (cookie != null)
            {
               name = cookie.getName();
               if (name != null && name.equals(_cookieName))
               {
                  value = cookie.getValue();
                  if (value != null && value.length() > 0)
                  {
                     if (_engine.containsSession(value))
                     {
                        /*
                         * get the session, from the Engine
                         * check to see if it is valid,
                         * if not, remove it from the engine
                         * DO NOT include it in the returned List
                         */

                        session = _engine.getSession(value);
                        if (session != null)
                        {
                           if (session.isExpired())
                           {
                              _engine.removeSession(value);
                              msg = "Expired session: " + value + ", NOT added to list";
                           }
                           else
                           {
                              sessionList.add(value);
                              msg = "Valid Session: " + value + ", added to list";
                           }
                        }
                        else
                        {
                           msg = "Null session: " + value + ", NOT added to list";
                        }
                     }
                     else
                     {
                        msg = "Missing session: " + value + ", NOT added to list";
                     }
                  }
                  if (_engine.isDebug())
                  {
                     Logger.logInfo(METHOD_NAME + msg);
                  }
               }
            }
         }
      }

      return sessionList;
   }

   //----------------------------------------------------------------
   private void updateSession(String sessionId, String clientId,
           PrincipalIF principal, SessionType authLevel, Action cookieAction)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":updateSession(): ";
      SessionIF session = null;

      principal.setCredentials(null);

      if (sessionId != null && sessionId.length() > 0)
      {

         //create the Engine Session if it does not already exist

         if (!_engine.containsSession(sessionId))
         {
            switch (authLevel)
            {
               case SYSTEM:
                  session = new BasicSession(_engine, SessionType.SYSTEM, sessionId);
                  break;
               case USER:
                  session = new BasicSession(_engine, SessionType.USER, sessionId, principal);
                  break;
               default: // includes ANON
                  session = new BasicSession(_engine, SessionType.ANON, sessionId);
                  break;
            }

            /*
             * Add the clientId to the session,
             * have to "re-set" it back into the Engine
             */

            session.setClientId(clientId);
            _engine.setSession(sessionId, session);
         }

         cookieAction.setMode(Mode.CREATE);
      }
      else
      {
         Logger.logError(METHOD_NAME + "Session value is null/empty");
      }

      return;
   }

   //----------------------------------------------------------------
   private void invalidateSessions(List<String> sessionList)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":invalidateSession(): ";

      /*
       * If any of the sessionId's in the List exist in the Engine, Remove them
       */

      if (sessionList != null && sessionList.size() > 0)
      {
         for (String sessionId : sessionList)
         {
            if (sessionId != null && sessionId.length() > 0)
            {
               if (_engine.containsSession(sessionId))
               {
                  _engine.removeSession(sessionId);
               }
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private Boolean doAuthenticate(AuthenticatorIF authenticator, PrincipalIF principal)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doAuthenticate(): ";
      boolean isAuthen = false;

      try
      {
         //Perform authentication
         isAuthen = authenticator.authenticate(principal);
      }
      catch (AuthenticationException ex)
      {
         //logs an authentication exception
         Logger.logError(METHOD_NAME + ex.getMessage());
      }

      return isAuthen;
   }

   //================================================================
   private class Action
   //================================================================
   {

      private Mode mode = Mode.NOTHING;

      private Mode getMode()
      {
         return this.mode;
      }

      private void setMode(Mode mode)
      {
         this.mode = mode;
      }
   }
}
