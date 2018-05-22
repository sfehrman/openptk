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
import org.openspml.v2.transport.RPCRouterMonitor;
import org.openspml.v2.util.Spml2Exception;
import org.openspml.v2.util.Spml2ExceptionWithResponse;

import org.openptk.exception.ServiceException;

/**
 *
 * @author Scott Fehrman
 */
//===================================================================
public interface ClientFacadeIF
//===================================================================
{
   public void init(String url) throws ServiceException, Spml2Exception;

   public void init(String url, String username, String password) throws ServiceException, Spml2Exception;

   public void init(String url, String username, String password, String proxyHost, int proxyPort) throws ServiceException, Spml2Exception;

   public Response send(Request req) throws ServiceException, Spml2Exception, Spml2ExceptionWithResponse;

   public String sendRequest(Request req) throws ServiceException, Spml2Exception;

   public String send(String xml) throws ServiceException, Spml2Exception;

   public void setMonitor(RPCRouterMonitor m) throws ServiceException;

   public void setTrace(boolean b) throws ServiceException;

   public void setSOAPAction(String action) throws ServiceException;

   public void throwErrors(Response res) throws Spml2ExceptionWithResponse;
}
