/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2010 Sun Microsystems, Inc.
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
package org.openptk.app.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.openptk.connection.ConnectionIF;
import org.openptk.connection.Setup;
import org.openptk.connection.SetupIF;
import org.openptk.exception.AuthenticationException;
import org.openptk.exception.ConnectionException;

public final class PTKEnvironment
{
   /*
    * Environment variables from OS
    *
    * For example in BASH
    *
    *    setenv OPENPTK_CONFIG /var/tmp/openptk.xml
    */
   private static final String OPENPTK_CLIENT = "openptk_client";
   private static final String ENV_PREFIX = "OPENPTK_";

   /*
    * Location of the OpenPTK CLI Property File
    */
   private static final String OPENPTK_DIR = ".openptk";
   private static final String OPENPTK_PROP_FILE = "openptk.properties";
   private static final String OPENPTK_HISTORY = "openptk.history";

   /*
    * DEFAULT Values
    */
   private static String DEFAULT_PROMPT = "> ";
   private static String DEFAULT_TIMINGS = "on";

   /*
    * Check for valid session only if the last check was less than
    * 5 minuts ago, or 60000 ms * 5
    */
   private static long SESSION_CHECK_LIMIT = 60000 * 5;

   public enum Variable
   {
      HISTORY,
      PROMPT,
      TIMINGS
   }

   /*
    * Type of modes that the CLI can be startup up in
    *
    *   CONSOLE - Intactive Shell
    */
   protected enum Mode
   {
      console,
   }
   private String _userHome = null;
   private String _openptkDir = null;
   private String _openptkProps = null;
   private Properties _props = null;
   protected Mode _cliMode = null;
   private ConnectionIF _connection = null;
   private SetupIF _setup = null;
   private String _userId = null;
   private String _sessionId = null;
   private long _lastOperationTime = 0;
   private String [] _contextIds = null;
   private String _contextId = null;
   private String _defaultContextId = null;
   private boolean _showTimings = true;
   private PTKStatistics _stats = null;

   /**
    * PTKEnvironment constructor is responsible for setting up the environment
    * of variables from a few different sources.  Below is a list of the steps
    * used to setup these variables.
    *
    * 1. Setup DEFAULTS (i.e. PROMPT, HISTORY, TIMINGS file)
    * 2. Read the properties from the OpenPTK properties file.  Normally found
    *    at ~/.openptk/openptk.properties
    * 3. Iterate through each known variable and look for a environment variable
    *    that includes a prefix of OPENPTK_
    * 4. Set certain variables from the command line (i.e. -C <CONTEXT> )
    *
    * So, in order of precedence:
    *
    *    CLI ARGs --> Enviornment Var --> OpenPTK Property file --> DEFAULTS
    *
    * @param args
    * @throws CLIException
    */
   public PTKEnvironment(final String[] args) throws CLIException
   {
      ConnectionIF conn = null;

      /*
       * Setup the:
       *
       *    _userHome     - User's home directory
       *    _openptkDir   - Standard OpenPTK directory under user home
       *    _openptkProps - Standard OpenPTK Property file
       */

      _userHome = System.getProperty("user.home");
      _openptkDir = _userHome + File.separator + OPENPTK_DIR;
      _openptkProps = _openptkDir + File.separator + OPENPTK_PROP_FILE;

      /*
       * Create an empty set of OpenPTK Statistics
       */

      resetStats();
      
      /*
       * STEP 1 -
       *
       * Setup the following default variables:
       *
       *    PROMPT   - Used as the default prompt on the console
       *    HISTORY  - Used as the OpenPTK History file.  Keeps historical
       *               operations that can be recalled with an up arrow on
       *               the commandline.
       *    TIMINGS  - Used to designate whether timings are displayed in the
       *               console.
       */

      try
      {
         setVariable(Variable.PROMPT.toString(), DEFAULT_PROMPT, false);
         setVariable(Variable.HISTORY.toString(), _openptkDir + File.separator + OPENPTK_HISTORY, false);
         setVariable(Variable.TIMINGS.toString(), DEFAULT_TIMINGS, false);
      }
      catch (Exception e)
      {
         handleError(e.getMessage());
      }

      /*
       * STEP 2 -
       *
       * Parse all the properties in the OpenPTK Property file
       */

      this.readPropertyFile();

      try
      {
         /*
          * This will create an OpenPTK Setup object which will read in the
          * OpenPTK Client properties.  These normally consist of:
          *
          *    OpenPTK Server URL
          *    OpenPTK Client ID
          *    OpenPTK Secret
          */

         SetupIF setup = new Setup(OPENPTK_CLIENT);

         this.setSetup(setup);
      }
      catch (ConnectionException e)
      {
         this.handleError(e.getMessage());
      }

      /*
       * STEP 3 -
       *
       * Iterate through all variables and set the environment properties
       */

      for (Variable variable : Variable.values())
      {
         String envValue = System.getenv(ENV_PREFIX + variable.toString());

         if (envValue != null && envValue.length() > 0)
         {
            try
            {
               setVariable(variable, envValue, false);
            }
            catch (Exception e)
            {
               handleError(e.getMessage());
            }
         }
      }

      /*
       * STEP 4 -
       *
       * Parse any OpenPTK CLI arguments
       */

      this.parseCLIArgs(args);

      /*
       * Update the timings flag to reflect what may have been set in the
       * properties files
       */

      this.updateTimingsFlag();
      
      /*
       * Create an anonymous connection, simply to get the list of ContextIDs
       * and the default ContextID
       *
       * If there is a connection exception, probably due to the URL of the
       * server, then emit an error saying unable to connect.  This is fatal
       * and the user needs to resolve to conitune.
       *
       * If there is an AuthenticationConnection, then the server needs to allow
       * for an anonymous connection to allow it to get the context information.
       * However, we don't necessarily need to error out.  It won't be until
       * the user connects/authenticates before they'll get the context
       * information they are looking for.
       */

      try
      {
         conn = _setup.getConnection();

         this.setContextIds(conn.getContextIds());
         
         _defaultContextId = conn.getContextId();
      }
      catch (ConnectionException connEx)
      {
         handleError("Unable to connect to the OpenPTK Server");
      }
      catch (AuthenticationException authEx)
      {
         // OK to continue
         _contextIds = null;
         _defaultContextId = null;
      }

      return;
   }

   /*
    * Creates an OpenPTK connection based on the userid/password presented.
    * This will use the openptk_client.properties variable
    * openptk.connection.uri to attempt an authentication to with the
    * userid/password.
    *
    * Upon an error with the authentication, a few exceptions may be thrown
    * based on an invalid Connection, normally due to an OpenPTK Server not
    * being available (i.e. bad URI) or an real authentication issue with the
    * userid/passsword combination.
    *
    * @param userid - String userid of the user attempting authentication
    * @param password - String password of the user attempting authentication
    * @throws ConnectionException - Normally a bad URI to the OpenPTK Server
    *         or the OpenPTK Server may not be running
    * @thorws AuthenticationException - A bad userid/password combination
    */
   public void createConnection(final String userid, final String password)
      throws ConnectionException, AuthenticationException
   {
      ConnectionIF conn = null;

      String contextId = this.getContextId();

      /*
       * Create the actual connection to the OpenPTK Server using the
       * userid/password sent.  Typically, one of two outcomes may result:
       *
       *    ConnectionException:
       *       Bad ClientID
       *       Bad Secret
       *       Incorrect URL
       *
       *    AuthenticationException:
       *       Incorrect UserID/Password combination
       */

      conn = _setup.getConnection(userid, password);

      setConnection(conn);

      /*
       *
       * If the connection is set, then the authentication was successful.
       * We will now set the userid on the environment.  Then, we will set
       * the variable contextID.  If this is null, then the default context
       * will be used.
       *
       */
      if (conn != null)
      {
         setUserId(userid);

         if (contextId == null)
         {
            contextId = this._defaultContextId;

            this.setContextIds(conn.getContextIds());
         }
         setContextId(contextId);
      }


      return;
   }

   public void closeConnection()
   {
      setConnection(null);
      _contextId = null;

      return;
   }

   /**
    * Checks to see if the connection is valid or not.  There may be several
    * reasons for it being invalid:
    *
    *   1. The connection was never set (i.e. connection == null)
    *   2. The connection has timed out (i.e. sessionId has changed)
    *
    * Future reasons might be due to invalid state
    */
   public boolean isValidConnection()
   {
      /*
       * If the cached connection is null or if the sessionId or
       * lastOperationTime isnt set, then return false.
       */

      if (_connection == null
         || _sessionId == null
         || _lastOperationTime == 0)
      {
         return false;
      }

      /*
       * Let's figure out the last time we completed an operation.  If that time
       * was over the SESSION_CHECK_LIMIT, then we need to verify the connection
       * as the server may have terminated it due to timeout
       */

      long currentTime = System.currentTimeMillis();

      if ((currentTime - _lastOperationTime) > SESSION_CHECK_LIMIT)
      {
         String tmpSessionId = "";

         try
         {
            tmpSessionId = _connection.getSessionData(ConnectionIF.Session.ID);
         }
         catch (ConnectionException ex)
         {
            return false;
         }

         if (!_sessionId.equals(tmpSessionId))
         {
            return false;
         }
      }

      /*
       * Lastly, let's change the lastOperationTime to the current time, to
       * restart the clock until the next check.
       */

      _lastOperationTime = currentTime;

      return true;

   }

   /**
    * @param varName
    * @param varValue
    *
    */
   public void setVariable(final String varName, final String varValue, final boolean persist)
   {
      if (_props == null)
      {
         _props = new Properties();
      }

      /*
       * If the value sent is null (i.e. not set), then it's the same as
       * removing the variable from the set of properties.
       */

      if (varValue == null)
      {
         removeVariable(varName);
      }
      else
      {
         _props.setProperty(varName, varValue);
      }

      if (persist)
      {
         this.writePropertyFile();
      }

      this.updateTimingsFlag();

      return;
   }

   private void updateTimingsFlag()
   {
      String timingValue = null;

      timingValue = this.getVariable(PTKEnvironment.Variable.TIMINGS.name());

      if (timingValue == null ||
              "on".equalsIgnoreCase(timingValue))
      {
         setShowTimings(true);
      }
      else
      {
         setShowTimings(false);
      }

      return;
   }

   public boolean showTimings()
   {
      return _showTimings;
   }

   /**
    * @param var
    * @param varValue
    */
   public void setVariable(final Variable var, final String varValue, final boolean persist)
   {
      this.setVariable(var.toString(), varValue, persist);

      return;
   }

   /**
    * @param varName]
    */
   private void removeVariable(final String varName)
   {
      _props.remove(varName);

      return;
   }

   /**
    * @return
    */
   public Enumeration<Object> getVariables()
   {
      return _props.keys();
   }

   /**
    * @param varName
    * @return
    */
   public String getVariable(final String varName)
   {
      return _props.getProperty(varName);
   }

   /**
    * @param var
    * @return
    */
   public String getVariable(final Variable var)
   {
      return this.getVariable(var.toString());
   }

   /**
    * @return
    */
   public ConnectionIF getConnection()
   {
      return _connection;
   }

   /**
    * @param connection
    */
   private void setConnection(final ConnectionIF connection)
   {
      /*
       * Check to see if we have a connection already.  If so, we will
       * close it first
       */

      if (_connection != null)
      {
         try
         {
            _connection.close();
         }
         catch (ConnectionException ex)
         {
         }
      }

      _connection = connection;

      if (connection == null)
      {
         _lastOperationTime = 0;
         _sessionId = null;

         this.setUserId(null);
      }
      else
      {
         _lastOperationTime = System.currentTimeMillis();

         /*
          * Get the defaultContextId in case we ever need to set it back to the
          * default.  By doing this, we won't need to re-authenticate the user.
          */

         _defaultContextId = getConnection().getContextId();

         try
         {
            _sessionId = _connection.getSessionData(ConnectionIF.Session.ID);
         }
         catch (ConnectionException ex)
         {
         }
      }

      return;
   }

   private void setShowTimings(boolean showTimings)
   {
      _showTimings = showTimings;
   }

   /**
    * @param setup
    */
   private void setSetup(final SetupIF setup)
   {
      _setup = setup;
   }

   /**
    * @return
    */
   public String getUserId()
   {
      return _userId;
   }

   /*
    * getStats
    * 
    * Return the current set of console statistics
    * 
    * @return PTKStatistics
    */
   public PTKStatistics getStats()
   {
      return _stats;
   }

   /*
    * reset Stats
    *
    * Creates a new set of console statistics and returns that set
    *
    * @return PTKStatistics
    */
   public PTKStatistics resetStats()
   {
      _stats = new PTKStatistics();

      return _stats;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   /**
    * @param userId
    */
   private void setUserId(final String userId)
   {
      _userId = userId;
      return;
   }

   /**
    * @param msg
    * @throws CLIException
    */
   private void handleError(final String msg) throws CLIException
   {
      throw new CLIException(msg);
   }

   private void readPropertyFile()
   {
      boolean exists = false;

      exists = (new File(_openptkDir)).exists();

      if (!exists)
      {
         exists = (new File(_openptkDir)).mkdir();

         if (!exists)
         {
            return;
         }
      }

      exists = (new File(_openptkProps)).exists();

      if (exists)
      {
         try
         {
            _props.load(new FileInputStream(_openptkProps));
         }
         catch (IOException ex)
         {
            this.noop();
         }
      }
      else
      {
         writePropertyFile();
      }

      return;
   }

   public void writePropertyFile()
   {
      try
      {
         _props.store(new FileOutputStream(_openptkProps), null);
      }
      catch (IOException ex)
      {
         this.noop();
      }

      return;
   }

   //----------------------------------------------------------------
   private void parseCLIArgs(final String[] args) throws CLIException
   //----------------------------------------------------------------
   {
      /*
       * If the arguments passed are either null or a length of 0, then 
       * the console was invoked.
       */

      if (args == null || args.length < 1)
      {
         _cliMode = Mode.console;
      }
      else
      {

         /*
          * Get the mode the user wants to run
          *
          * Examples:
          *    ptk         // defalults to console
          *    ptk console
          */

         try
         {
            _cliMode = Mode.valueOf(args[0].toLowerCase());
         }
         catch (IllegalArgumentException e)
         {
            this.handleError("Unknown command - " + args[0]);
         }

         /*
          * Parse remaining arguments
          * 
          * Currently, there are no options defined for the OpenPTK CLI tool
          */

      }
      return;
   }

   /**
    * @param contextId
    *
    */
   public void setContextId(final String contextId) throws ConnectionException
   {
      String newContextId = null;

      _contextId = _defaultContextId;

      /*
       * If the contextId that we are trying to set is null, then, we need to
       * use the defaultContextId that we obtained when we created the
       * connection
       */

      if (contextId != null)
      {
         _contextId = contextId;
      }
      if (_connection != null)
      {
         try
         {
            _connection.setContextId(_contextId);
         }
         catch (ConnectionException e)
         {
            newContextId = _contextId;
            closeConnection();
            throw new ConnectionException("Specified context '" + newContextId + "' is not valid");
         }
      }
      else
      {
         _contextId = null;
      }

      return;
   }

   /**
    * @param contextId
    *
    */
   public String getContextId()
   {
      return _contextId;
   }

   /**
    * @param contextIds
    *
    */
   public void setContextIds(String [] contextIds)
   {
      _contextIds = contextIds;
   }

   /**
    * @param contextId
    *
    */
   public String [] getContextIds()
   {
      return _contextIds;
   }


   /**
    * @param contextId
    *
    */
   public String getDefaultContextId()
   {
      return _defaultContextId;
   }

   private void noop()
   {
      return;
   }
}
