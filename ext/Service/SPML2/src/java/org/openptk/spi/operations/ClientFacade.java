/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010 Project OpenPTK
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
package org.openptk.spi.operations;

import java.net.MalformedURLException;
import java.net.URL;

import org.openspml.v2.msg.spml.Request;
import org.openspml.v2.msg.spml.Response;
import org.openspml.v2.transport.RPCRouterMonitor;
import org.openspml.v2.util.Spml2Exception;
import org.openspml.v2.util.Spml2ExceptionWithResponse;

import org.openptk.exception.ServiceException;

/**
 *
 * @author Scott Fehrman
 */
//===================================================================
public abstract class ClientFacade implements ClientFacadeIF
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   protected static final int DEF_PROXY_PORT = 8080;
   protected String _url = null;
   protected String _username = null;
   protected String _password = null;
   protected String _action = null;
   protected String _proxyHost = null;
   protected int _proxyPort = DEF_PROXY_PORT;

   //----------------------------------------------------------------
   public ClientFacade()
   //----------------------------------------------------------------
   {
      return;
   }

   public abstract Response send(Request req) throws ServiceException, Spml2Exception, Spml2ExceptionWithResponse;

   public abstract String sendRequest(Request req) throws ServiceException, Spml2Exception;

   public abstract String send(String xml) throws ServiceException, Spml2Exception;

   public abstract void setSOAPAction(String action) throws ServiceException;

   public abstract void setMonitor(RPCRouterMonitor m) throws ServiceException;

   public abstract void setTrace(boolean b) throws ServiceException;

   public abstract void throwErrors(Response res) throws Spml2ExceptionWithResponse;

   //----------------------------------------------------------------
   public void init(final String url) throws ServiceException, Spml2Exception
   //----------------------------------------------------------------
   {
      this.init(url, null, null, null, DEF_PROXY_PORT);
      return;
   }

   //----------------------------------------------------------------
   public void init(final String url, final String username, final String password) throws ServiceException, Spml2Exception
   //----------------------------------------------------------------
   {
      this.init(url, username, password, null, DEF_PROXY_PORT);
      return;
   }

   //----------------------------------------------------------------
   public void init(final String url, final String username, final String password, final String proxyHost, final int proxyPort) throws ServiceException, Spml2Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":init(): ";
      URL testurl = null;

      /*
       * Test the url to make sure it's valid, try to create a real URL object
       */

      if (url == null || url.length() < 1)
      {
         this.handleError(METHOD_NAME + "url is null");
      }

      try
      {
         testurl = new URL(url);
      }
      catch (MalformedURLException e)
      {
         this.handleError(METHOD_NAME + e.getMessage());
      }

      _url = url;

      /*
       * Check the user and host data
       */

      if (username != null && username.length() > 0)
      {
         _username = username;
      }

      if (password != null && password.length() > 0)
      {
         _password = password;
      }

      if (proxyHost != null && proxyHost.length() > 0)
      {
         _proxyHost = proxyHost;
      }

      if (proxyPort > 1024) // assume its an unprivileaged port
      {
         _proxyPort = proxyPort;
      }

      return;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   //----------------------------------------------------------------
   protected final void handleError(final String msg) throws ServiceException
   //----------------------------------------------------------------
   {
      String err = null;
      if (msg == null || msg.length() < 1)
      {
         err = "(null message)";
      }
      else
      {
         err = msg;
      }
      throw new ServiceException(err);
   }
}
