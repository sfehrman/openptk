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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.openptk.logging.Logger;

/**
 *
 * @author Derrick Harcey, Sun Microsystems, Inc.
 *
 */
//===================================================================
public class AuthRequestWrapper extends HttpServletRequestWrapper
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static boolean _debug = false;
   private static final String NULL = "(NULL)";
   private static final String COOKIE_HEADER_ATTR = "cookie";
   private Cookie _sessionCookie = null;
   private Boolean _removeSessionCookie = false;
   private String _cookieName = null;

   //----------------------------------------------------------------
   AuthRequestWrapper(HttpServletRequest req, String cookieName)
   //----------------------------------------------------------------
   {
      super(req);
      _cookieName = cookieName;
      return;
   }

   //----------------------------------------------------------------
   AuthRequestWrapper(HttpServletRequest req, String cookieName, boolean debug)
   //----------------------------------------------------------------
   {
      super(req);
      _cookieName = cookieName;
      _debug = debug;
      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public Cookie[] getCookies()
   //----------------------------------------------------------------
   {
      boolean addCookie = true;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String cookieName = null;
      String dbgStr = null;
      StringBuffer buf = null;
      Cookie dbgCookie = null;
      List<Cookie> newCookies = null;
      Cookie[] originalCookies = null;
      Cookie[] finalCookies = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>:");
      }

      originalCookies = super.getCookies();
      newCookies = new ArrayList<Cookie>();

      if (originalCookies != null && originalCookies.length > 0)
      {
         for (Cookie cookie : originalCookies)
         {
            if (cookie != null)
            {
               cookieName = cookie.getName();

               /*
                * If a cookie was found, with the same name as "OPENPTKSESSIONID" ...
                *
                * If the cookie is "flagged" to be removed (_removeSessionCookie = true)
                * Do not add it to the "new" Cookie array (do nothing)
                *
                * If there is a valid new cookie (not null)
                * Add it to the new Cookie array
                * Else
                * Copy the existing OPENPTKSESSIONID Cookie to the new array
                *
                * Else, copy the cookie to the new Cookie array
                */

               if (cookieName.equalsIgnoreCase(_cookieName))
               {
                  if (_removeSessionCookie)
                  {
                     addCookie = false;
                  }
                  else
                  {
                     if (_sessionCookie == null)
                     {
                        addCookie = false;
                        newCookies.add(cookie);
                     }
                  }
               }
               else
               {
                  newCookies.add(cookie);
               }
            }
         }
      }

      if (addCookie && _sessionCookie != null)
      {
         newCookies.add(_sessionCookie);
      }

      finalCookies = new Cookie[newCookies.size()];
      finalCookies = newCookies.toArray(finalCookies);

      if (_debug)
      {
         buf = new StringBuffer();
         buf.append("[");
         for (int i = 0; i < finalCookies.length; i++)
         {
            if (i > 0)
            {
               buf.append(", ");
            }
            dbgCookie = finalCookies[i];
            if (dbgCookie != null)
            {
               dbgStr = dbgCookie.getName();
               if (dbgStr != null)
               {
                  buf.append(dbgStr).append("=");
                  dbgStr = dbgCookie.getValue();
                  if (dbgStr != null)
                  {
                     buf.append(dbgStr);
                  }
               }
            }
         }
         buf.append("]");
         Logger.logInfo(METHOD_NAME + "<<<: Cookie[]: " + buf.toString());
      }

      return finalCookies;
   }

   /**
    * @param name
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public String getHeader(String name)
   //----------------------------------------------------------------
   {
      boolean needSeparator = false;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String cookieName = null;
      String cookieValue = null;
      String value = null;
      StringBuffer buf = null;
      Cookie[] cookies = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>: name='" + (name != null ? name : NULL) + "'");
      }

      /*
       * If the header name matches the "cookie" name ...
       * - Get the current "Cookie" via this.getCookies()
       * - Create a String containing the value
       * Else ...
       * - get the parent value
       *
       */
      if (name != null && name.equals(COOKIE_HEADER_ATTR))
      {
         cookies = this.getCookies();
         if (cookies != null)
         {
            buf = new StringBuffer();
            for (Cookie cookie : cookies)
            {
               if (needSeparator)
               {
                  buf.append(";");
               }
               cookieName = cookie.getName();
               if (cookieName != null && cookieName.length() > 0)
               {
                  buf.append(cookieName).append("=");
                  cookieValue = cookie.getValue();
                  if (cookieValue != null && cookieValue.length() > 0)
                  {
                     buf.append(cookieValue);
                  }
                  needSeparator = true;
               }
            }

            value = buf.toString();
         }
      }
      else
      {
         value = super.getHeader(name);
      }

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>: String: '" + (value != null ? value : NULL) + "'");
      }

      return value;
   }

   /**
    * @param key
    * @return
    */
   //----------------------------------------------------------------
   @Override
   @SuppressWarnings(
   {
      "unchecked", "UseOfObsoleteCollectionType"
   })
   public Enumeration<String> getHeaders(String name)
   //----------------------------------------------------------------
   {
      boolean needSeparator = false;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String cookieName = null;
      String cookieValue = null;
      Cookie[] cookies = null;
      Enumeration<String> enumHdrValues = null;
      Enumeration<String> enumDebug = null;
      Vector<String> cookieVals = null;
      List<String> output = null;
      StringBuffer buf = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>: name='" + (name != null ? name : NULL) + "'");
      }

      /*
       * If the header name matches the "cookie" name ...
       * - Create a new Vector to store values
       * - Get the current "Cookie" via this.getCookies(), add to Vector
       * - Create an Enumeration of the Vector
       * Else ...
       * - get the parent value
       *
       */
      if (name != null && name.equals(COOKIE_HEADER_ATTR))
      {
         cookieVals = new Vector<String>();
         cookies = this.getCookies();
         if (cookies != null)
         {
            buf = new StringBuffer();
            for (Cookie cookie : cookies)
            {
               if (cookie != null)
               {
                  if (needSeparator)
                  {
                     buf.append(";");
                  }
                  cookieName = cookie.getName();
                  if (cookieName != null && cookieName.length() > 0)
                  {
                     buf.append(cookieName).append("=");
                     cookieValue = cookie.getValue();
                     if (cookieValue != null && cookieValue.length() > 0)
                     {
                        buf.append(cookieValue);
                     }
                     needSeparator = true;
                  }
               }
            }
            cookieVals.add(buf.toString());
         }
         enumHdrValues = cookieVals.elements();
         if (_debug)
         {
            enumDebug = cookieVals.elements();
         }
      }
      else
      {
         enumHdrValues = super.getHeaders(name);
         if (_debug)
         {
            enumDebug = super.getHeaders(name);
         }
      }

      if (_debug)
      {
         output = Collections.list(enumDebug);
         Logger.logInfo(METHOD_NAME + "<<<: Enumeration<String>: " + (output != null ? output : NULL));
      }

      return enumHdrValues;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   @SuppressWarnings(
   {
      "unchecked", "UseOfObsoleteCollectionType"
   })
   public Enumeration<String> getHeaderNames()
   //----------------------------------------------------------------
   {
      boolean containsCookies = false;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String headerName = null;
      List<String> output = null;
      Enumeration<String> enumHdrNames = null;
      Enumeration<String> enumDebug = null;
      Vector<String> vectHdrNames = new Vector<String>();

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>:");
      }

      enumHdrNames = super.getHeaderNames();

      while (enumHdrNames.hasMoreElements())
      {
         headerName = enumHdrNames.nextElement();
         if (headerName.equals(COOKIE_HEADER_ATTR))
         {
            containsCookies = true;
         }
         vectHdrNames.add(headerName);
      }

      if (!containsCookies)
      {
         vectHdrNames.add(COOKIE_HEADER_ATTR);
      }

      enumHdrNames = vectHdrNames.elements();
      if (_debug)
      {
         enumDebug = vectHdrNames.elements();
      }

      if (_debug)
      {
         output = Collections.list(enumDebug);
         Logger.logInfo(METHOD_NAME + "<<<: Enumeration<String>: " + (output != null ? output : NULL));
      }

      return enumHdrNames;
   }

   /**
    *
    * @param cookie
    */
   //----------------------------------------------------------------
   public void setAddSessionCookie(Cookie cookie)
   //----------------------------------------------------------------
   {
      // The cookie that is to be injected should always
      // specify a cookie path to ensure that multiple
      // cookies do not get set by the wrapper.

      _sessionCookie = cookie;
      return;
   }

   //----------------------------------------------------------------
   public void setRemoveSessionCookie()
   //----------------------------------------------------------------
   {
      // The cookie that is to be injected should always
      // specify a cookie path to ensure that multiple
      // cookies do not get set by the wrapper.

      _removeSessionCookie = true;
      return;
   }
}
