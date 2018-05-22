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

import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.openptk.common.Component;
import org.openptk.logging.Logger;

/**
 *
 * @author Derrick Harcey, Sun Microsystems, Inc.
 *
 */
//===================================================================
public abstract class BaseFilter extends Component implements Filter
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String MASKED_VALUE = "********";
   private static final String NULL = "";

   //----------------------------------------------------------------
   @Override
   public void init(FilterConfig config) throws ServletException
   //----------------------------------------------------------------
   {
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void destroy()
   //----------------------------------------------------------------
   {
      return;
   }
   
   abstract protected void uniqueInfo(HttpServletRequest hrequest);

   //----------------------------------------------------------------
   @SuppressWarnings("rawtypes")
   protected void requestInfo(HttpServletRequest hrequest)
   //----------------------------------------------------------------
   {
      Object obj = null;
      int i = 0;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String name = null;
      String value = null;
      StringBuffer buf = null;
      Cookie[] cookies = null;
      Enumeration enumObj = null;

      buf = new StringBuffer();

      buf.append(METHOD_NAME);

      name = hrequest.getMethod();
      buf.append((name != null ? name : "(method)")).append(" ");

      name = hrequest.getProtocol();
      buf.append("protocol='").append((name != null ? name : NULL)).append("' ");

      buf.append("secure='").append(hrequest.isSecure()).append("' ");

      name = hrequest.getCharacterEncoding();
      buf.append("characterEncoding='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getScheme();
      buf.append("scheme='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getServerName();
      buf.append("serverName='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getLocalAddr();
      buf.append("localAddr='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getLocalName();
      buf.append("localName='").append((name != null ? name : NULL)).append("' ");

      i = hrequest.getLocalPort();
      name = Integer.toString(i);
      buf.append("localPort='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getAuthType();
      buf.append("authType='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getContentType();
      buf.append("contentType='").append((name != null ? name : NULL)).append("' ");

      i = hrequest.getContentLength();
      name = Integer.toString(i);
      buf.append("contentLength='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getContextPath();
      buf.append("contextPath='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getRequestURI();
      buf.append("requestURI='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getRequestURL().toString();
      buf.append("requestURL='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getServletPath();
      buf.append("servletPath='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getPathInfo();
      buf.append("pathInfo='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getPathTranslated();
      buf.append("pathTranslated='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getQueryString();
      buf.append("queryString='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getRemoteHost();
      buf.append("remoteHost='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getRemoteAddr();
      buf.append("remoteAddr='").append((name != null ? name : NULL)).append("' ");

      i = hrequest.getRemotePort();
      name = Integer.toString(i);
      buf.append("remotePort='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getRemoteUser();
      buf.append("remoteUser='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getRequestedSessionId();
      buf.append("requestedSessionId='").append((name != null ? name : NULL)).append("' ");

      name = hrequest.getSession().getId();
      buf.append("sessionId='").append((name != null ? name : NULL)).append("' ");

      buf.append("cookies=[ ");
      cookies = hrequest.getCookies();
      if (cookies != null && cookies.length > 0)
      {
         for (Cookie cookie : cookies)
         {
            name = cookie.getName();
            if (name != null && name.length() > 0)
            {
               value = cookie.getValue();
               buf.append(name).append("='").append((value != null ? value : NULL)).append("' ");
            }
         }
      }
      buf.append("] ");

      buf.append("attributes=[ ");
      enumObj = hrequest.getSession().getAttributeNames();
      if (enumObj != null)
      {
         while (enumObj.hasMoreElements())
         {
            name = (String) enumObj.nextElement();
            if (name != null && name.length() > 0)
            {
               obj = hrequest.getSession().getAttribute(name);
               if (obj instanceof String)
               {
                  value = (String) obj;
                  if (value != null && value.length() > 0
                          && name.equalsIgnoreCase("password"))
                  {
                     value = this.mask(value);
                  }
                  buf.append(name).append("='").append((value != null ? value : NULL)).append("' ");
               }
               else if (obj instanceof Integer)
               {
                  value = ((Integer) obj).toString();
                  buf.append("(Integer::").append(name).append(")='").append((value != null ? value : NULL)).append("' ");
               }
               else if (obj instanceof Long)
               {
                  value = ((Long) obj).toString();
                  buf.append("(Long::").append(name).append(")='").append((value != null ? value : NULL)).append("' ");
               }
               else
               {
                  buf.append("(Class::").append(obj.getClass().getName()).append(") ");
               }
            }
         }
      }
      buf.append("] ");

      buf.append("headers=[ ");
      enumObj = hrequest.getHeaderNames();
      if (enumObj != null)
      {
         while (enumObj.hasMoreElements())
         {
            name = (String) enumObj.nextElement();
            if (name != null && name.length() > 0)
            {
               value = hrequest.getHeader(name);
               if (value != null && value.length() > 0
                       && name.equalsIgnoreCase("password"))
               {
                  value = this.mask(value);
               }
               buf.append(name).append("='").append((value != null ? value : NULL)).append("' ");
            }
         }
      }
      buf.append("] ");

      buf.append("parameters=[ ");
      enumObj = hrequest.getParameterNames();
      if (enumObj != null)
      {
         while (enumObj.hasMoreElements())
         {
            name = (String) enumObj.nextElement();
            if (name != null && name.length() > 0)
            {
               value = hrequest.getParameter(name);
               if (value != null && value.length() > 0
                       && name.equalsIgnoreCase("password"))
               {
                  value = this.mask(value);
               }
               buf.append(name).append("='").append((value != null ? value : NULL)).append("' ");
            }
         }
      }
      buf.append("] ");

      Logger.logInfo(buf.toString());
      
      this.uniqueInfo(hrequest);

      return;
   }

   //----------------------------------------------------------------
   protected String mask(final String value)
   //----------------------------------------------------------------
   {
      String masked = null;

      masked = MASKED_VALUE;

      return (masked);
   }

   //----------------------------------------------------------------
   protected void handleError(String msg) throws ServletException
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

      Logger.logError(msg);
      throw new ServletException(msg);
   }
}
