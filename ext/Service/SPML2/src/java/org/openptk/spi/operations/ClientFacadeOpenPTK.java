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

import org.openspml.v2.msg.spml.Request;
import org.openspml.v2.msg.spml.Response;
import org.openspml.v2.util.Spml2Exception;
import org.openspml.v2.util.Spml2ExceptionWithResponse;
import org.openspml.v2.transport.RPCRouterMonitor;

import org.openptk.exception.ServiceException;
import org.openptk.openspml.v2.client.OpenPTKSpml2Client;

/**
 *
 * @author Scott Fehrman
 */
//===================================================================
public class ClientFacadeOpenPTK extends ClientFacade
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private OpenPTKSpml2Client _client = null;

   //----------------------------------------------------------------
   public ClientFacadeOpenPTK()
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void init(final String url) throws ServiceException, Spml2Exception
   //----------------------------------------------------------------
   {
      this.init(url);
      this.createClient();
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void init(final String url, final String username, final String password) throws ServiceException, Spml2Exception
   //----------------------------------------------------------------
   {
      this.init(url, username, password);
      this.createClient();
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void init(final String url, final String username, final String password, final String proxyHost, final int proxyPort) throws ServiceException, Spml2Exception
   //----------------------------------------------------------------
   {
      super.init(url, username, password, proxyHost, proxyPort);
      this.createClient();
      return;
   }

   //----------------------------------------------------------------
   public Response send(final Request req) throws ServiceException, Spml2Exception, Spml2ExceptionWithResponse
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":send(Request): ";
      Response response = null;

      if (_client == null)
      {
         this.handleError(METHOD_NAME + "client is null");
      }

      response = _client.send(req);

      return response;
   }

   //----------------------------------------------------------------
   public String sendRequest(final Request req) throws ServiceException, Spml2Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":sendRequest(Request): ";
      String response = null;

      if (_client == null)
      {
         this.handleError(METHOD_NAME + "client is null");
      }

      response = _client.sendRequest(req);

      return response;
   }

   //----------------------------------------------------------------
   public String send(final String xml) throws ServiceException, Spml2Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":send(String): ";
      String response = null;

      if (_client == null)
      {
         this.handleError(METHOD_NAME + "client is null");
      }

      response = _client.send(xml);

      return response;
   }

   //----------------------------------------------------------------
   public void setSOAPAction(final String action) throws ServiceException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":setSOAPAction(): ";

      if (_client == null)
      {
         this.handleError(METHOD_NAME + "client is null");
      }
      
      _client.setSOAPAction(action);

      return;
   }

   //----------------------------------------------------------------
   public void setMonitor(final RPCRouterMonitor m) throws ServiceException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":setMonitor(): ";

      if (_client == null)
      {
         this.handleError(METHOD_NAME + "client is null");
      }

      _client.setMonitor(m);

      return;
   }

   //----------------------------------------------------------------
   public void setTrace(final boolean b) throws ServiceException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":setTrace(): ";

      if (_client == null)
      {
         this.handleError(METHOD_NAME + "client is null");
      }

      _client.setTrace(b);

      return;
   }

   //----------------------------------------------------------------
   public void throwErrors(final Response res) throws Spml2ExceptionWithResponse
   //----------------------------------------------------------------
   {
      _client.throwErrors(res);
      return;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private void createClient() throws Spml2Exception
   //----------------------------------------------------------------
   {
      if (_username != null && _username.length() > 0
         && _password != null && _password.length() > 0)
      {
         if (_proxyHost != null && _proxyHost.length() > 0)
         {
            _client = new OpenPTKSpml2Client(_url, _username, _password, _proxyHost, _proxyPort);
         }
         else
         {
            _client = new OpenPTKSpml2Client(_url, _username, _password);
         }
      }
      else
      {
         _client = new OpenPTKSpml2Client(_url);
      }

      return;
   }
}
