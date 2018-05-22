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
package org.openptk.taglib;

import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.openptk.connection.ConnectionIF;
import org.openptk.connection.Setup;
import org.openptk.connection.SetupIF;
import org.openptk.exception.AuthenticationException;
import org.openptk.exception.ConnectionException;

//===================================================================
public class getConnectionTag extends AbstractTag
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private boolean _forceNew = false;
   private String _var = null;
   private String _properties = null;
   private String _user = null;
   private String _password = null;

   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setVar(final String arg)
   //----------------------------------------------------------------
   {
      _var = arg;
      return;
   }

   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setProperties(final String arg)
   //----------------------------------------------------------------
   {
      _properties = arg;
      return;
   }

   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setUser(final String arg)
   //----------------------------------------------------------------
   {
      _user = arg;
      return;
   }

   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setPassword(final String arg)
   //----------------------------------------------------------------
   {
      _password = arg;
      return;
   }

   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setForcenew(final String arg)
   //----------------------------------------------------------------
   {
      _forceNew = Boolean.parseBoolean(arg);
      return;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   /**
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected void process() throws Exception
   //----------------------------------------------------------------
   {
      boolean needConnection = true;
      String METHOD_NAME = CLASS_NAME + ":process(): ";
      String sessionType = null;
      String storedType = null;
      List<String> sessionList = null;
      SetupIF setup = null;     // OpenPTK
      ConnectionIF conn = null; // OpenPTK

      /*
       * check to see if a connection already exists (stored)
       * if not, then create one.  We will also create one if
       * the "forcenew" attribute was set to "true"
       */

      if (!_forceNew)
      {
         try
         {
            conn = this.getConnection(_var);
            needConnection = false;
         }
         catch (Exception ex)
         {
            conn = null;
            needConnection = true;
         }
      }

      /*
       * Validate the "stored" connection
       * - get its "current" type : ANON, USER, SYSTEM
       *
       * Possible conditions:
       *
       * - original (stored) connection is still valid, same TYPE
       *   nothing to do
       *
       * - original (stored) connection is invalid,
       *   a "new" connection was automatically created, with the same TYPE (ANON -> ANON)
       *   store the "new" connection
       *
       * - original (stored) connection is invalid,
       *   a "new" connection was automatically created, with a different TYPE (USER -> ANON)
       *   return an error
       */

      if (conn != null)
      {
         try
         {
            sessionType = conn.getSessionData(ConnectionIF.Session.TYPE);
         }
         catch (ConnectionException ex)
         {
            /*
             * Stored connection is invalid
             */

            this.setConnection(_var, null); // clear it from the "store"
            this.setString(CONNECTION_TYPE, null);
            sessionType = null;
            needConnection = true;
         }

         if (sessionType != null && sessionType.length() > 0)
         {
            storedType = this.getString(CONNECTION_TYPE);
            if (storedType != null && storedType.length() > 0)
            {
               if (sessionType.equalsIgnoreCase(storedType))
               {
                  /*
                   * same (Stored) type and (Session) type
                   * update connection cache, could be a new session id
                   */
                  this.setConnection(_var, conn);
                  this.setString(CONNECTION_TYPE, sessionType);
               }
               else
               {
                  /*
                   * Stored type does not match Session type
                   * maybe went from: USER (Stored) -> ANON (new Session)
                   * clear the "stored" connection and type
                   */
                  this.setConnection(_var, null);
                  this.setString(CONNECTION_TYPE, null);
                  needConnection = true;
               }
            }
            else
            {
               /*
                * Stored type is null
                * clear the stored connection
                */
               this.setConnection(_var, null);
               needConnection = true;
            }
         }
      }

      /*
       * Check for an existing Session (sessionId in the Cookie)
       * Then try to "re-use" the Connection
       */

      if (needConnection)
      {
         setup = new Setup(_properties);

         sessionList = this.getSessionIdFromCookie();
         for (String sessionId : sessionList)
         {
            if (sessionId != null && sessionId.length() > 0 && needConnection)
            {
               try
               {
                  conn = setup.useConnection(sessionId);
                  sessionType = conn.getSessionData(ConnectionIF.Session.TYPE);

                  this.setConnection(_var, conn);
                  this.setString(CONNECTION_TYPE, sessionType);
                  needConnection = false;
               }
               catch (ConnectionException ex)
               {
                  this.setConnection(_var, null);
                  this.setString(CONNECTION_TYPE, null);
                  needConnection = true;
               }
               catch (AuthenticationException ex)
               {
                  this.setConnection(_var, null);
                  this.setString(CONNECTION_TYPE, null);
                  needConnection = true;
               }
            }
            else
            {
               this.setConnection(_var, null);
               this.setString(CONNECTION_TYPE, null);
               needConnection = true;
            }
         }
      }

      if (needConnection)
      {
         /*
          * Get a new Connection
          * Use the provided user and password data
          */

         conn = setup.getConnection(_user, _password);
         sessionType = conn.getSessionData(ConnectionIF.Session.TYPE);

         this.setConnection(_var, conn);
         this.setString(CONNECTION_TYPE, sessionType);
      }

      return;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private List<String> getSessionIdFromCookie()
   //----------------------------------------------------------------
   {
      String name = null;
      String value = null;
      PageContext pageContext = null;
      HttpServletRequest httpRequest = null;
      Cookie[] cookies = null;
      pageContext = (PageContext) this.getJspContext();
      List<String> sessionList = null;

      /*
       * There might be multiple Cookies with the same name
       * Get the value for each Cookie and store it in the List
       */

      sessionList = new LinkedList<String>();

      if (pageContext != null)
      {
         httpRequest = (HttpServletRequest) pageContext.getRequest();
         if (httpRequest != null)
         {
            cookies = httpRequest.getCookies();
            if (cookies != null)
            {
               for (Cookie cookie : cookies)
               {
                  if (cookie != null)
                  {
                     name = cookie.getName();
                     if (name != null && name.length() > 0 && name.equals(COOKIE_NAME))
                     {
                        value = cookie.getValue();
                        if (value != null && value.length() > 0)
                        {
                           sessionList.add(value);
                        }
                     }
                  }
               }
            }
         }
      }

      return sessionList;
   }
}
