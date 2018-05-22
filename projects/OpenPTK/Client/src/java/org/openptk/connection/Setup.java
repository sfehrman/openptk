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
package org.openptk.connection;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.openptk.exception.AuthenticationException;
import org.openptk.exception.ConnectionException;
import org.openptk.logging.Logger;
import org.openptk.logging.LoggingIF;
import org.openptk.logging.SimpleLogger;

/**
 * <p>
 * Boot-straps the connection to the Server.
 * <br/>
 * A client-side Properties file is used to determine relationship
 * between the server and a client.  The Properties file must contain
 * the following information:
 * </p>
 * <br/>
 * <code>openptk.connection.clientid=_CLIENT_ID_</code><br/>
 * <code>openptk.connection.sharedsecret=_SHARED_SECRET_</code><br/>
 * <code>openptk.connection.uri=http://localhost:8080/openptk</code><br/>
 * <br/>
 * <p>
 * The <b>clientid</b> and <b>sharedsecret</b> are used to establish a
 * trust between the client and the server.
 * <br/>
 * In addition to this data, a userid and password may be used to obtain
 * the "connection".
 * </p>
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class Setup implements SetupIF
//===================================================================
{
   private boolean _debug = false;
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _propertyFile = null;
   private final Properties _props = new Properties();

   /**
    * <p>
    * Create a new Setup object using the specified Property file.
    * <br/>
    * </p>
    * <code>Setup setup = new Setup("openptk_client");</code><br/>
    *
    * @param propertyFile name of the Property file
    * @throws Exception
    */
   //----------------------------------------------------------------
   public Setup(final String propertyFile) throws ConnectionException
   //----------------------------------------------------------------
   {
      super();

      if (propertyFile == null || propertyFile.length() < 1)
      {
         this.handleError("Property file must be set");
      }

      _propertyFile = propertyFile;

      return;
   }

   /**
    * Returns a connection, no user name, no user password.
    * @return ConnectionIF
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public final ConnectionIF getConnection() throws ConnectionException, AuthenticationException
   //----------------------------------------------------------------
   {
      return this.getConnection(null, null);
   }

   /**
    * Returns a connection, only token
    * @param token unique value
    * @return ConnectionIF
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public final ConnectionIF getConnection(final String token) throws ConnectionException, AuthenticationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getConnection(): ";
      ConnectionIF conn = null;

      _props.setProperty(PROP_CONNECTION_TOKEN, (token != null ? token : ""));
      _props.setProperty(PROP_CONNECTION_MODE, CONNECTION_MODE_TOKEN);

      this.initialize();
      this.testURI();
      conn = this.execute();

      return conn;
   }

   /**
    * Returns a connection, authenticates with user name and user password.
    * @param user unique id for the end-user
    * @param password authenticate the end-user
    * @return ConnectionIF
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public final ConnectionIF getConnection(final String user, final String password) throws ConnectionException, AuthenticationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getConnection(): ";
      ConnectionIF conn = null;

      _props.setProperty(PROP_CONNECTION_USER, (user != null ? user : ""));
      _props.setProperty(PROP_CONNECTION_PASSWORD, (password != null ? password : ""));
      _props.setProperty(PROP_CONNECTION_MODE, CONNECTION_MODE_USERPASS);

      this.initialize();
      this.testURI();
      conn = this.execute();

      return conn;
   }

   //----------------------------------------------------------------
   @Override
   public final ConnectionIF useConnection(String sessionId) throws ConnectionException, AuthenticationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":useConnection(): ";
      ConnectionIF conn = null;

      if (sessionId == null || sessionId.length() < 1)
      {
         this.handleError(METHOD_NAME + "SessionId is null/empty");
      }

      _props.setProperty(PROP_CONNECTION_SESSIONID, sessionId);
      _props.setProperty(PROP_CONNECTION_MODE, CONNECTION_MODE_SESSIONID);

      this.initialize();
      conn = this.execute();

      return conn;
   }

   /**
    * @return boolean
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isDebug()
   //----------------------------------------------------------------
   {
      return _debug;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private synchronized ConnectionIF execute() throws ConnectionException, AuthenticationException
   //----------------------------------------------------------------
   {
      int iArgs = 1;
      Object obj = null;
      Object[] args = null;
      Class<?> newClass = null;
      Class<?>[] argTypes = null;
      Constructor<?> constructor = null;
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      ConnectionIF conn = null;
      Exception exception = null;

      args = new Object[iArgs];
      args[0] = _props;

      argTypes = new Class<?>[iArgs];

      for (int i = 0; i < iArgs; i++)
      {
         argTypes[i] = args[i].getClass();
      }

      try
      {
         newClass = Class.forName(SetupIF.CLASSNAME_CONNECTION); // SetupIF.CLASSNAME_CONNECTION
      }
      catch (ClassNotFoundException ex)
      {
         this.handleError(METHOD_NAME + "ClassNotFoundException: " + ex.getMessage());
      }

      try
      {
         constructor = newClass.getConstructor(argTypes);
      }
      catch (NoSuchMethodException ex)
      {
         this.handleError(METHOD_NAME + "NoSuchMethodException: " + ex.getMessage());
      }
      catch (SecurityException ex)
      {
         this.handleError(METHOD_NAME + "SecurityException: " + ex.getMessage());
      }

      try
      {
         obj = constructor.newInstance(args);
      }
      catch (InstantiationException ex)
      {
         this.handleError(METHOD_NAME + "InstantiationException: " + ex.getMessage());
      }
      catch (IllegalAccessException ex)
      {
         this.handleError(METHOD_NAME + "IllegalAccessException: " + ex.getMessage());
      }
      catch (IllegalArgumentException ex)
      {
         this.handleError(METHOD_NAME + "IllegalArgumentException: " + ex.getMessage());
      }
      catch (InvocationTargetException ex)
      {
         exception = (Exception) ex.getTargetException();
         if (exception != null)
         {
            if ( exception instanceof NullPointerException)
            {
               this.handleError(METHOD_NAME + "NullPointerException: " + "'" + ex.getMessage() + "'");
            }
            else if (exception instanceof ConnectionException)
            {
               throw (ConnectionException) exception;
            }
            else if (exception instanceof AuthenticationException)
            {
               throw (AuthenticationException) exception;
            }
            else
            {
               this.handleError(METHOD_NAME + "Unknown target exception: " + exception.getMessage());
            }
         }
         else
         {
            this.handleError(METHOD_NAME + "Target exception is null.");
         }
      }

      conn = (ConnectionIF) obj;

      return conn;
   }

   //----------------------------------------------------------------
   private void initialize() throws ConnectionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":initialize(): ";
      String resName = null;
      String resValue = null;
      String propName = null;
      String propValue = null;
      String logFile = null;
      String logId = null;
      String connectionClassName = SetupIF.CLASSNAME_CONNECTION;
      Enumeration<String> enums = null;
      ResourceBundle resBundle = null;
      LoggingIF logger = null;

      try
      {
         resBundle = ResourceBundle.getBundle(_propertyFile);
      }
      catch (MissingResourceException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      enums = resBundle.getKeys();
      while (enums.hasMoreElements())
      {
         resName = enums.nextElement();
         if (resName != null && resName.length() > 0)
         {
            resValue = resBundle.getString(resName);
            if (resValue != null && resValue.length() > 0)
            {
               _props.setProperty(resName, resValue);
            }
            else
            {
               this.handleError(METHOD_NAME + "Property '" + resName + "' has a null value");
            }
         }
         else
         {
            this.handleError(METHOD_NAME + "Property file '" + _propertyFile + "' has an entry with a null name");
         }
      }

      /*
       * Check for the required Properties
       */

      propName = PROP_CONNECTION_CLIENTID;
      propValue = _props.getProperty(propName);
      if (propValue == null || propValue.length() < 1)
      {
         this.handleError(METHOD_NAME + "Missing property: '" + propName + "' ");
      }

      propName = PROP_CONNECTION_URI;
      propValue = _props.getProperty(propName);
      if (propValue == null || propValue.length() < 1)
      {
         this.handleError(METHOD_NAME + "Missing property: '" + propName + "' ");
      }

      propName = PROP_CONNECTION_SHAREDSECRET;
      propValue = _props.getProperty(propName);
      if (propValue == null || propValue.length() < 1)
      {
         this.handleError(METHOD_NAME + "Missing property: '" + propName + "' ");
      }

      propName = PROP_LOGFILE;
      propValue = _props.getProperty(propName);
      if (propValue == null || propValue.length() < 1)
      {
         this.handleError(METHOD_NAME + "Missing property: '" + propName + "' ");
      }
      else
      {
         logFile = propValue;
         logId = _props.getProperty(PROP_CONNECTION_CLIENTID);
      }

      /*
       * Optional properties
       */

      propName = PROP_DEBUG;
      propValue = _props.getProperty(propName);
      if (propValue != null && propValue.length() > 0)
      {
         _debug = Boolean.parseBoolean(propValue);
      }

      if (connectionClassName == null || connectionClassName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Connection Classname is not set.");
      }

      /*
       * Setup the logger for this Setup and the Connection
       * 
       * Note: Due to the fact that the SimpleLogger controls only a SINGLE
       * Java Logging Facility, we do not want to create another logger.  So,
       * we will first check to see if a SimpleLogger has been created.  If so
       * then do not create one, otherwise proceed.
       */
      
      LoggingIF currLogger = Logger.getLogger();
      
      if (currLogger instanceof SimpleLogger)
      {
         /*
          * Don't add any logger at this point.  Use what's already there.
          */
      }
      else
      {
         logger = new SimpleLogger(logFile);
         Logger.setLogger(logger);
      }

      if (this.isDebug())
      {
         Logger.logInfo(METHOD_NAME + "properties:" + _props);
      }

      return;
   }

   //----------------------------------------------------------------
   private void testURI() throws ConnectionException
   //----------------------------------------------------------------
   {
      URL url = null;
      InputStream is = null;

      try
      {
         url = new URL(_props.getProperty(SetupIF.PROP_CONNECTION_URI));
         is = url.openStream();
         is.close();
      }
      catch (Exception e)
      {
         this.handleError("Could not open connection to: '"
            + _props.getProperty(SetupIF.PROP_CONNECTION_URI) + "'");
      }

      return;
   }

   //----------------------------------------------------------------
   private void handleError(final String msg) throws ConnectionException
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
      throw new ConnectionException(str);
   }
}
