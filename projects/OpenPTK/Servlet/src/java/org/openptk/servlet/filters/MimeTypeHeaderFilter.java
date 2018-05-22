/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2012 Project OpenPTK
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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.servlet.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.openptk.debug.DebugLevel;
import org.openptk.logging.Logger;

/**
 *
 * @author Scott Fehrman, Project OpenPTK
 */
//===================================================================
public class MimeTypeHeaderFilter extends BaseFilter
//===================================================================
{

   private static final String PARAM_MODE = "mode";
   private static final String PARAM_DEBUG = "debug";
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private FilterConfig _config = null;

   //----------------------------------------------------------------
   @Override
   public void init(FilterConfig config) throws ServletException
   //----------------------------------------------------------------
   {
      _config = config;
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException, ServletException
   //----------------------------------------------------------------
   {
      int debugLevel = 0;
      String strMode = null;
      String strDebug = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      MimeTypeHeaderRequestWrapper wrapperRequest = null;
      MimeTypeHeaderRequestWrapper.Mode mode = MimeTypeHeaderRequestWrapper.Mode.DISABLED;
      MimeTypeHeaderRequestWrapper.Mode[] modes = null;

      strMode = _config.getInitParameter(PARAM_MODE);
      if (strMode != null && strMode.length() > 0)
      {
         modes = MimeTypeHeaderRequestWrapper.Mode.values();
         for (MimeTypeHeaderRequestWrapper.Mode m : modes)
         {
            if (m.toString().equalsIgnoreCase(strMode))
            {
               mode = m;
            }
         }
      }

      strDebug = _config.getInitParameter(PARAM_DEBUG);
      if (strDebug != null && strDebug.length() > 0)
      {
         try
         {
            debugLevel = Integer.parseInt(strDebug);
         }
         catch (NumberFormatException ex)
         {
            debugLevel = 0;
         }

         if (debugLevel < 0 || debugLevel > 4)
         {
            debugLevel = 0;
         }

         switch (debugLevel)
         {
            case 1:
               this.setDebugLevel(DebugLevel.CONFIG);
               break;
            case 2:
               this.setDebugLevel(DebugLevel.FINE);
               break;
            case 3:
               this.setDebugLevel(DebugLevel.FINER);
               break;
            case 4:
               this.setDebugLevel(DebugLevel.FINEST);
               break;
         }
      }

      Logger.logInfo(METHOD_NAME + "mode=" + mode.toString() + ", debugLevel=" + debugLevel);

      if (this.isDebug())
      {
         this.requestInfo((HttpServletRequest) servletRequest);
      }

      if (mode != MimeTypeHeaderRequestWrapper.Mode.DISABLED
              && servletRequest instanceof HttpServletRequest)
      {
         wrapperRequest = new MimeTypeHeaderRequestWrapper((HttpServletRequest) servletRequest, mode, debugLevel);
         chain.doFilter(wrapperRequest, response);
      }
      else
      {
         chain.doFilter(servletRequest, response);
      }

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
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String str = null;
      StringBuilder bldr = new StringBuilder();

      bldr.append(METHOD_NAME);

      if (this.getDebugLevelAsInt() > 2)
      {
         bldr.append("body='");

         if (hrequest instanceof MimeTypeHeaderRequestWrapper)
         {
            str = ((MimeTypeHeaderRequestWrapper) hrequest).getBody();
            if (str != null && str.length() > 0)
            {
               bldr.append(str);
            }
         }
         bldr.append("'");
         bldr.append(", length=").append(hrequest.getContentLength());
      }

      Logger.logInfo(bldr.toString());

      return;
   }
}
