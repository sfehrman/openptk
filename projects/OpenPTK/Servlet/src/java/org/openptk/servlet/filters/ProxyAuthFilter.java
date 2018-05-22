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
package org.openptk.servlet.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openptk.connection.ConnectionIF;
import org.openptk.connection.Setup;
import org.openptk.connection.SetupIF;
import org.openptk.logging.Logger;

/**
 *
 * @author Derrick Harcey
 *
 */
//===================================================================
public class ProxyAuthFilter extends BaseFilter
//===================================================================
{
   /**
    *
    * @param request The servlet request we are processing
    * @param result The servlet response we are creating
    * @param chain The filter chain we are processing
    *
    * @exception IOException if an input/output error occurs
    * @exception ServletException if a servlet error occurs
    */
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String TRUE = "true";
   private static final String PARAM_LOGIN = "login";
   private static final String PARAM_LOGOUT = "logout";
   private static final String PARAM_GOTO = "goto";
   private String _cookieName = null;
   private String _cookiePath = null;
   private String _authHTTPHeaderName = null;
   private String _openptkusername = null;
   private String _openptkpassword = null;
   private boolean _debug = false;

   //----------------------------------------------------------------
   public void init(FilterConfig config) throws ServletException
   //----------------------------------------------------------------
   {
      String debug = null;
      /*
       * Get the init parameters.
       *
       * These are located in the web.xml of the application using the
       * ProxyAuthFilter.  An example would look like:
       *
       * <init-param>
       *    <param-name>sessioncookie</param-name>
       *    <param-value>OPENPTKSESSIONID</param-value>
       * </init-param>
       */

      _cookieName = config.getInitParameter("sessioncookie");
      _cookiePath = config.getInitParameter("cookiepath");
      _authHTTPHeaderName = config.getInitParameter("authhttpheadername");
      _openptkusername = config.getInitParameter("openptkusername");
      _openptkpassword = config.getInitParameter("openptkpassword");
      debug = config.getInitParameter("debug");

      if (debug != null && debug.length() > 0)
      {
         _debug = Boolean.parseBoolean(debug);
      }
   }

   //----------------------------------------------------------------
   @Override
   public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException
   //----------------------------------------------------------------
   {
      boolean continueFilter = true;
      String METHOD_NAME = CLASS_NAME + ":doFilter(): ";
      String sessval = null;
      String gotoURI = null;
      String param = null;
      HttpServletRequest hrequest = null;
      HttpServletResponse hresponse = null;
      AuthRequestWrapper requestWrapper = null;
      SetupIF setup = null;
      ConnectionIF connection = null;

      if (!(request instanceof HttpServletRequest))
      {
         // This is not an HTTP request
         throw new ServletException("non-HTTP protocols not allowed!.");
      }

      hrequest = (HttpServletRequest) request;
      hresponse = (HttpServletResponse) response;
      requestWrapper = new AuthRequestWrapper(hrequest, _cookieName);

      param = requestWrapper.getParameter(PARAM_LOGIN);
      if (param != null && param.equals(TRUE))
      {
         continueFilter = this.authenticate(requestWrapper, hresponse, chain);
         if (_debug)
         {
            this.requestInfo(hrequest);
         }
      }
      // only proceed with filter if the authentication was not successful.
      if (!continueFilter)
      {
         return;
      }

      /*
       * If there is a logout=true parameter on the reuqest, then lets set
       * the session to ANON and logout the user here.
       */

      param = requestWrapper.getParameter(PARAM_LOGOUT);
      if (param != null && param.equals(TRUE))
      {
         if (_debug)
         {
            this.requestInfo(hrequest);
         }

         sessval = this.getSessionCookie(requestWrapper);

         /*
          * logout with api:  useconnection, if not null, close connection to logout
          */

         try
         {
            setup = new Setup("openptk_client");

            if (_debug)
            {
               Logger.logInfo(METHOD_NAME + "Setup for Logout Created");
            }

            connection = setup.useConnection(sessval);

            connection.close(); // logout the user

            this.updateSession(null, requestWrapper, hresponse); // clear the users cookie
         }
         catch (Exception ex)
         {
            if (_debug)
            {
               Logger.logWarning(METHOD_NAME + "Authen Logout Exception : "
                  + ex.toString());
            }
            Logger.logWarning(METHOD_NAME
               + "JerseyConnection:connect(user, password): Unauthorized: logout failed");
         }

         gotoURI = request.getParameter(PARAM_GOTO);
         if (gotoURI != null && gotoURI.length() > 0)
         {
            if (_debug)
            {
               Logger.logInfo(METHOD_NAME + "Logout redirect to : " + gotoURI);
            }
            hresponse.sendRedirect(gotoURI);
         }
      }

      chain.doFilter(requestWrapper, hresponse);

      return;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   
   //----------------------------------------------------------------
   @Override
   protected void uniqueInfo(HttpServletRequest hrequest)
   //----------------------------------------------------------------
   {
      return;
   }

   /*
    * =================
    * PRIVATE METHODS
    * =================
    */
   //----------------------------------------------------------------
   private void updateSession(String sessionVal, AuthRequestWrapper hreq,
      HttpServletResponse hresp)
   //----------------------------------------------------------------
   {
      Cookie userCookie = null;

      // sessionVal is null, clear existing cookie, if present
      if ((sessionVal == null) || sessionVal.length() < 1)
      {
         // where is the cookie name supplied in the wrapper
         hreq.setRemoveSessionCookie();
      }
      else  // set cookie with sessionVal
      {
         userCookie = new Cookie(_cookieName, sessionVal);
         userCookie.setPath(_cookiePath);
         hreq.setAddSessionCookie(userCookie);
         hresp.addCookie(userCookie);
      }

      return;
   }

   //----------------------------------------------------------------
   private boolean authenticateUserPass(AuthRequestWrapper requestWrapper,
      HttpServletResponse hresponse, FilterChain chain) throws IOException, ServletException
   //----------------------------------------------------------------
   {
      boolean doNext = true;
      String METHOD_NAME = CLASS_NAME + ":authenticateUserPass(): ";
      String userId = null;
      String password = null;
      String sessionId = null;
      SetupIF setup = null;
      ConnectionIF connection = null;

      // Get Authentication information if present

      userId = requestWrapper.getParameter(_openptkusername);
      password = requestWrapper.getParameter(_openptkpassword);

      // Check for userId and Password Authentication parameters in request

      if ((userId != null) && (password != null)
         && (userId.length() > 0) && (password.length() > 0))
      {
         try
         {
            setup = new Setup("openptk_client");
            if (_debug)
            {
               Logger.logInfo(METHOD_NAME + "Setup for Login Created");
            }

            connection = setup.getConnection(userId, password);

            // sessionType = connection.getSessionData(ConnectionIF.Session.TYPE);
            sessionId = connection.getSessionData(ConnectionIF.Session.ID);

            // Create the new OpenPTK session for the non-anonymous authenticated session
            this.updateSession(sessionId, requestWrapper, hresponse);

            chain.doFilter(requestWrapper, hresponse);
            // return from the doFilter method
            doNext = false;
         }
         catch (Exception ex)
         {
            if (_debug)
            {
               Logger.logWarning(METHOD_NAME + "Authen Exception : " + ex.toString());
            }
            Logger.logWarning(METHOD_NAME + "JerseyConnection:connect(user, password): Unauthorized: login failed");
         }
      }
      // continue on the doFilter method
      return doNext;
   }

   //----------------------------------------------------------------
   private boolean authenticateHTTPHeader(AuthRequestWrapper requestWrapper,
      HttpServletResponse hresponse, FilterChain chain) throws IOException, ServletException
   //----------------------------------------------------------------
   {
      boolean doNext = true;
      String METHOD_NAME = CLASS_NAME + ":authenticateHTTPHeader(): ";
      String userId = null;
      String sessionId = null;
      SetupIF setup = null;
      ConnectionIF connection = null;

      // Get Authentication information if present
      userId = requestWrapper.getHeader(_authHTTPHeaderName);
      if (userId != null && userId.length() > 0)
      {
         try
         {
            setup = new Setup("openptk_client");
            if (_debug)
            {
               Logger.logInfo(METHOD_NAME + "Setup Created");
            }

            connection = setup.getConnection(userId);

            sessionId = connection.getSessionData(ConnectionIF.Session.ID);

            // Create the new OpenPTK session for the non-anonymous authenticated session
            this.updateSession(sessionId, requestWrapper, hresponse);

            chain.doFilter(requestWrapper, hresponse);
            // return from the doFilter method
            doNext = false;
         }
         catch (Exception ex)
         {
            if (_debug)
            {
               Logger.logWarning(METHOD_NAME + "Authen Exception : " + ex.toString());
            }
            Logger.logWarning(METHOD_NAME + "JerseyConnection:connect(user, password): Unauthorized: login failed");
         }
      }
      // continue on to the doFilter method
      return doNext;
   }

   //----------------------------------------------------------------
   private boolean authenticateAnon(AuthRequestWrapper requestWrapper,
      HttpServletResponse hresponse, FilterChain chain) throws IOException, ServletException
   //----------------------------------------------------------------
   {
      boolean doNext = true;
      String METHOD_NAME = CLASS_NAME + ":authenticateAnon(): ";
      String sessionId = null;
      SetupIF setup = null;
      ConnectionIF connection = null;

      try
      {
         setup = new Setup("openptk_client");
         if (_debug)
         {
            Logger.logInfo(METHOD_NAME + "Setup Created");
         }

         connection = setup.getConnection();
         sessionId = connection.getSessionData(ConnectionIF.Session.ID);

         // Create the new OpenPTK session for the non-anonymous authenticated session
         this.updateSession(sessionId, requestWrapper, hresponse);

         chain.doFilter(requestWrapper, hresponse);
         // return from the doFilter method
         doNext = false;
      }
      catch (Exception ex)
      {
         if (_debug)
         {
            Logger.logWarning(METHOD_NAME + "Authen Exception : " + ex.toString());
         }
         Logger.logWarning(METHOD_NAME + "JerseyConnection:connect(user, password): Unauthorized: login failed");
      }

      // continue on to the doFilter method
      return doNext;
   }

   //----------------------------------------------------------------
   private boolean authenticate(AuthRequestWrapper requestWrapper,
      HttpServletResponse hresponse, FilterChain chain) throws IOException, ServletException
   //----------------------------------------------------------------
   {
      boolean doNext = true;

      // Process HTTP Header based authentication for SSO (OpenSSO, etc.) integration
      doNext = authenticateHTTPHeader(requestWrapper, hresponse, chain);

      // only proceed to the next authentication type if the previous was not successful.
      if (doNext)
      {
         doNext = authenticateUserPass(requestWrapper, hresponse, chain);
      }

      // only proceed to the next authentication type if the previous was not successful.
      if (doNext)
      {
         doNext = authenticateAnon(requestWrapper, hresponse, chain);
      }

      return doNext;
   }

   //----------------------------------------------------------------
   private String getSessionCookie(HttpServletRequest requestWrapper)
   //----------------------------------------------------------------
   {
      boolean done = false;
      int i = 0;
      String value = null;
      Cookie cookie = null;
      Cookie[] cookies = null;

      /*
       * Return the cookie value for the cookie with the OpenPTK
       * Cookie Name (found in the openptk.xml configuration)
       */

      /*
       * Get the cookies from the request.  There may be multiple.  Roll
       * through the cookies and find the one with the OpenPTK Cookie Name.
       * Once we find it, return it, else return null;
       */

      cookies = requestWrapper.getCookies();

      if (cookies != null && cookies.length > 0)
      {
         do
         {
            cookie = cookies[i];
            if (cookie != null)
            {
               if (_cookieName.equals(cookie.getName()))
               {
                  /*
                   * Found the cookie we are looking for!!!  Return it!
                   */

                  value = cookie.getValue();
                  done = true;
               }
               i++;
            }
         }
         while (i < cookies.length && !done);
      }

      return value;
   }
}
