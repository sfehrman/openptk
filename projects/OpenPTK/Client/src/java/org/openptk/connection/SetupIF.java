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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.connection;

import org.openptk.exception.AuthenticationException;
import org.openptk.exception.ConnectionException;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public interface SetupIF
//===================================================================
{
   public static final String CLASSNAME_CONNECTION = "org.openptk.connection.jersey.JerseyConnection";
   public static final String CLASSNAME_CONVERTER = "org.openptk.structure.XmlConverter";
   public static final String NAMESPACE = "openptk";
   public static final String PKG_CONNECTION = NAMESPACE + ".connection";
   public static final String PKG_CONVERTER = NAMESPACE + ".converter";
   public static final String PROP_DEBUG = NAMESPACE + ".debug";
   public static final String PROP_LOGFILE = NAMESPACE + ".logfile";
   public static final String PROP_CONNECTION_CLIENTID = PKG_CONNECTION + ".clientid";
   public static final String PROP_CONNECTION_SHAREDSECRET = PKG_CONNECTION + ".sharedsecret";
   public static final String PROP_CONNECTION_URI = PKG_CONNECTION + ".uri";
   public static final String PROP_CONNECTION_MODE = PKG_CONNECTION + ".mode";
   public static final String PROP_CONNECTION_TOKEN = PKG_CONNECTION + ".token";
   public static final String PROP_CONNECTION_USER = PKG_CONNECTION + ".user";
   public static final String PROP_CONNECTION_PASSWORD = PKG_CONNECTION + ".password";
   public static final String PROP_CONNECTION_SESSIONID = PKG_CONNECTION + ".sessionid";
   public static final String CONNECTION_MODE_USERPASS = "userpass";
   public static final String CONNECTION_MODE_TOKEN = "token";   
   public static final String CONNECTION_MODE_SESSIONID = "sessionid";
   public static final String SESSION_ID = "OPENPTKSESSIONID";
   public static final String URI_RESOURCES = "resources";
   public static final String URI_LOGIN = "login";
   public static final String URI_LOGOUT = "logout";

   /**
    * <p>
    * Get a connection to the Server anonymously.
    * <br/>
    * </p>
    * <code>ConnectionIF conn = setup.getConnection();</code><br/>
    * 
    * @return ConnectionIF a connection
    * @throws Exception
    */
   public ConnectionIF getConnection() throws ConnectionException, AuthenticationException;

   /**
    * <p>
    * Get a connection to the Server, using end-user id.
    * <br/>
    * </p>
    * <code>ConnectionIF conn = setup.getConnection("jdoe");</code><br/>
    *
    * @param user String end-user id
    * @return ConnectionIF a connection
    * @throws Exception
    */
   public ConnectionIF getConnection(String user) throws ConnectionException, AuthenticationException;

   /**
    * <p>
    * Get a connection to the Server, using end-user name and password.
    * <br/>
    * </p>
    * <code>ConnectionIF conn = setup.getConnection("jdoe","password");</code><br/>
    * 
    * @param user String end-user name
    * @param password String end-user password
    * @return ConnectionIF a connection
    * @throws Exception
    */
   public ConnectionIF getConnection(String user, String password) throws ConnectionException, AuthenticationException;

   /**
    * 
    * @param sessionId String a current and valid session id
    * @return ConnectionIF a connection
    * @throws ConnectionException
    * @throws AuthenticationException
    */
   public ConnectionIF useConnection(String sessionId) throws ConnectionException, AuthenticationException;

   /**
    * Get the debug flag.
    * @return boolean display debug data
    */
   public boolean isDebug();
}
