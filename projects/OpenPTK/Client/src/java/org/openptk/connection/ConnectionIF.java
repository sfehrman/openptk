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
package org.openptk.connection;

import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Opcode;
import org.openptk.api.Output;
import org.openptk.exception.ConnectionException;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public interface ConnectionIF extends ElementIF
//===================================================================
{
   public static final String PARAM_TOKEN = "token";
   public static final String PARAM_USER = "user";
   public static final String PARAM_PASSWORD = "password";
   public static final String PARAM_CLIENTID = "clientid";
   public static final String PARAM_CLIENTCRED = "clientcred";
   public static final String PROP_COOKIE_NAME = "openptk.cookie.name";
   public static final String PROP_URI_CLIENTS = "uri.clients";
   public static final String PROP_URI_CONTEXTS = "uri.contexts";
   public static final String PROP_URI_LOGIN = "uri.login";
   public static final String PROP_URI_LOGOUT = "uri.logout";
   public static final String RESOURCE_SESSION = "session";

   public enum Session
   {
      ID, TYPE, PRINCIPAL, AUTHEN;
   }

   /**
    * Has the Session changed from the initial connection.
    * @return boolean True if the session has changed
    */
   public boolean hasSessionChanged();

   /**
    * Get the "current" Context Id.
    *
    * @return String Id of the current Context
    */
   public String getContextId();

   /**
    * Get all of the available Context Ids.
    *
    * @return String[] Array of available Context Ids
    */
   public String[] getContextIds();

   /**
    * Set the "current" Context Id.
    *
    * @param contextId a valid Context Id
    * @throws Exception
    */
   public void setContextId(String contextId) throws ConnectionException;

   /**
    * Execute the operation (Opcode) on the Server.
    *
    * @param opcode Opcode what operation to execute
    * @param input Input data used by the operation
    * @return Output results of the operation
    * @throws Exception
    */
   public Output execute(Opcode opcode, Input input) throws ConnectionException;

   /**
    * Close the connection to the Server.
    *
    * @throws Exception
    */
   public void close() throws ConnectionException;

   /**
    * Set the debug flag.
    * @param debug should debug data be displayed.
    */
   public void setDebug(boolean debug);

   /**
    * Get the debug flag.
    * @return boolean will the debug data be displayed.
    */
   public boolean isDebug();

   /**
    * Get the String representing a Session data item.
    * @return String Session data
    * @throws ConnectionException
    */
   public String getSessionData(ConnectionIF.Session session) throws ConnectionException;
}
