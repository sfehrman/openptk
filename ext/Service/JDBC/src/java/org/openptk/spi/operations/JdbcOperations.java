/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2008-2010 Sun Microsystems, Inc.
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
package org.openptk.spi.operations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openptk.api.DataType;
import org.openptk.api.Query;
import org.openptk.api.State;
import org.openptk.common.AttrIF;
import org.openptk.common.BasicAttr;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.definition.DefinitionIF;
import org.openptk.exception.OperationException;
import org.openptk.exception.QueryException;
import org.openptk.logging.Logger;
import org.openptk.spi.JdbcQueryConverter;
import org.openptk.spi.QueryConverterIF;
import org.openptk.util.StringUtil;

/**
 *
 * @author Terry Sigle
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 *
 * This class is used for MySQL
 *
 */
//===================================================================
public class JdbcOperations extends Operations
// ===================================================================
{

   private static final String DESCRIPTION = "Java DataBase Connection (JDBC)";
   private static String VALUE_UNSUPPORTED_TYPE = "__UNSUPPORTED_TYPE__";
   private final String CLASS_NAME = this.getClass().getSimpleName();
   protected int _timeout = 0;  // conenction isValid() timeout
   protected String _user = null;
   protected String _password = null;
   protected String _url = null;
   protected String _driver = null;
   protected String _table = null;
   protected String _allowedChars = null;
   protected Connection _jdbcConn = null;
   protected static int DEFAULT_TIMEOUT = 1;
   protected static String PROP_CONNECTION_TABLE = "connection.table";
   protected static String PROP_CONNECTION_TIMEOUT = "connection.timeout";
   protected static String DEFAULT_USERNAME = "openptk";
   protected static String DEFAULT_PASSWORD = "openptk";
   public static final String PROP_INPUT_ALLOWED_CHARACTERS = "allowed.characters.jdbc";

   //----------------------------------------------------------------
   public JdbcOperations()
   //----------------------------------------------------------------
   {
      super();

      this.setDescription(JdbcOperations.DESCRIPTION);
      this.setType(OperationsType.JDBC);

      /*
       * Specify which operations are implemented
       */

      this.setImplemented(Operation.CREATE, true);
      this.setImplemented(Operation.READ, true);
      this.setImplemented(Operation.UPDATE, true);
      this.setImplemented(Operation.DELETE, true);
      this.setImplemented(Operation.SEARCH, true);
      this.setImplemented(Operation.PWDCHANGE, true);
      this.setImplemented(Operation.PWDRESET, true);
      this.setImplemented(Operation.PWDFORGOT, true);
      this.setImplemented(Operation.AUTHENTICATE, true);

      /*
       * Specify which operations are enabled (by default) Can be changed at
       * run-time
       */

      this.setEnabled(Operation.CREATE, true);
      this.setEnabled(Operation.READ, true);
      this.setEnabled(Operation.UPDATE, true);
      this.setEnabled(Operation.DELETE, true);
      this.setEnabled(Operation.SEARCH, true);
      this.setEnabled(Operation.PWDCHANGE, true);
      this.setEnabled(Operation.PWDRESET, true);
      this.setEnabled(Operation.PWDFORGOT, true);
      this.setEnabled(Operation.AUTHENTICATE, true);

      /*
       * set the allowed characters for input cleaning
       */

      _allowedChars = this.getProperty(JdbcOperations.PROP_INPUT_ALLOWED_CHARACTERS);
      if (_allowedChars == null || _allowedChars.length() < 1)
      {
         _allowedChars = StringUtil.BASIC_JDBC;
      }

      return;
   }

   //----------------------------------------------------------------
   @Override
   public void startup()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":startup(): ";

      super.startup();

      try
      {
         _timeout = Integer.parseInt(this.getProperty(PROP_CONNECTION_TIMEOUT));
      }
      catch (NumberFormatException ex)
      {
         _timeout = DEFAULT_TIMEOUT;
      }

      _table = this.getProperty(PROP_CONNECTION_TABLE);
      if (_table == null || _table.length() < 1)
      {
         this.setState(State.ERROR);
         this.setStatus(METHOD_NAME + "Property '" + PROP_CONNECTION_TABLE
                 + "' is null");
      }
      else
      {
         _user = this.getValue(OperationsIF.PROP_USER_NAME);
         if (_user == null || _user.length() < 1)
         {
            this.setState(State.ERROR);
            this.setStatus(METHOD_NAME + "Value '" + OperationsIF.PROP_USER_NAME
                    + "' is null");
         }
         else
         {
            _password = this.getValue(OperationsIF.PROP_USER_PASSWORD);
            if (_password == null || _password.length() < 1)
            {
               this.setState(State.ERROR);
               this.setStatus(METHOD_NAME + "Value '" + OperationsIF.PROP_USER_PASSWORD
                       + "' is null");
            }
            else
            {
               _url = this.getProperty(OperationsIF.PROP_URL);
               if (_url == null || _url.length() < 1)
               {
                  this.setState(State.ERROR);
                  this.setStatus(METHOD_NAME + "Property '" + Operations.PROP_URL
                          + "' is null");
               }
               else
               {
                  _driver = this.getProperty(OperationsIF.PROP_DRIVER);
                  if (_driver == null || _driver.length() < 1)
                  {
                     this.setState(State.ERROR);
                     this.setStatus(METHOD_NAME + "Property '" + Operations.PROP_DRIVER
                             + "' is null");
                  }
                  else
                  {
                     try
                     {
                        Class.forName(_driver);
                     }
                     catch (ClassNotFoundException e)
                     {
                        this.setState(State.ERROR);
                        this.setStatus(METHOD_NAME + "Unable to load SQL Driver: " + _driver);
                     }
                  }
               }
            }
         }
      }

      return;
   }

   // ----------------------------------------------------------------
   @Override
   public void shutdown()
   // ----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":shutdown(): ";
      Throwable cause = null;

      if (_jdbcConn != null)
      {
         try
         {
            _jdbcConn.close();
         }
         catch (SQLException e)
         {
            cause = e.getCause();

            if (cause == null)
            {
               Logger.logError(METHOD_NAME + e.getMessage());
            }
            else
            {
               Logger.logError(METHOD_NAME + e.getMessage() + ", " + cause.getMessage());
            }
         }
      }

      super.shutdown();

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   // ----------------------------------------------------------------
   @Override
   public synchronized void execute(final RequestIF request, final ResponseIF response) throws OperationException
   // ----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      StringBuilder err = new StringBuilder();

      if (request == null)
      {
         this.handleError(METHOD_NAME + "Request is null");
      }

      if (response == null)
      {
         this.handleError(METHOD_NAME + "Response is null");
      }

      Operation oper = null;

      this.setState(State.READY);

      response.setUniqueId(CLASS_NAME);

      /*
       * Verify the JDBC Connection
       * if it's "null" or "closed" ... get the connection
       * else ... make sure it's "valid"
       */

      try
      {
         if (_jdbcConn == null || _jdbcConn.isClosed())
         {
            _jdbcConn = DriverManager.getConnection(_url, _user, _password);
         }
         else
         {
            if (!_jdbcConn.isValid(_timeout))
            {
               err.append(METHOD_NAME).append("Connection is not valid: ");
               err.append("timeout=").append(_timeout).append(": ");
               err.append(_url).append(", user='").append(_user).append("'");

               response.setState(State.FAILED);
               response.setStatus(err.toString());

               this.setState(State.FAILED);
               this.setStatus(err.toString());

               this.handleError(err.toString());
            }
         }
      }
      catch (SQLException ex)
      {
         err.append(METHOD_NAME).append("Connection error: ");
         err.append(_url).append(", user='").append(_user).append("', ");
         err.append(ex.getMessage());

         response.setState(State.ERROR);
         response.setStatus(err.toString());

         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      oper = request.getOperation();

      switch (oper)
      {
         case CREATE:
            this.doCreate(request, response);
            break;
         case READ:
            this.doRead(request, response);
            break;
         case UPDATE:
            this.doUpdate(request, response);
            break;
         case DELETE:
            this.doDelete(request, response);
            break;
         case SEARCH:
            this.doSearch(request, response);
            break;
         case PWDCHANGE:
            this.doPasswordChange(request, response);
            break;
         case PWDRESET:
            this.doPasswordReset(request, response);
            break;
         case PWDFORGOT:
            this.doPasswordForgot(request, response);
            break;
         case AUTHENTICATE:
            this.doAuthenticate(request, response);
            break;
         default:
            throw new OperationException(METHOD_NAME + "Unimplemented Operation: " + oper.toString());
      }

      return;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   /**
    * @throws Throwable
    */
   // ----------------------------------------------------------------
   @Override
   protected void finalize() throws Throwable
   // ----------------------------------------------------------------
   {
      super.finalize();
      _jdbcConn.close();
      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doCreate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doCreate(): ";
      StringBuilder err = new StringBuilder();
      ComponentIF subject = null;
      PreparedStatement pstmt = null;

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      response.setDescription(JdbcOperations.DESCRIPTION + ": Create");

      pstmt = this.getPreparedStatement(request);

      if (pstmt == null)
      {
         this.handleError(METHOD_NAME + "PreparedStatement is null");
      }

      try
      {
         pstmt.executeUpdate();
      }
      catch (SQLException ex)
      {
         err.append(ex.getErrorCode()).append(", ").append(ex.getMessage());
         err.append(", statement='").append(pstmt.toString()).append("'");

         response.setState(State.ERROR);
         response.setStatus(METHOD_NAME + err.toString());

         this.checkException(ex, err);
         this.handleError(METHOD_NAME + err.toString());
      }

      if (!response.isError())
      {
         response.setState(State.SUCCESS);
         response.setStatus("Entry created");
         response.setUniqueId(subject);
      }

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doRead(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      this.doJdbcSelect(request, response, Operation.READ);
      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doSearch(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      this.doJdbcSelect(request, response, Operation.SEARCH);
      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doUpdate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      int returnCode = 0;
      String METHOD_NAME = CLASS_NAME + ":doUpdate(): ";
      StringBuilder err = new StringBuilder();
      PreparedStatement pstmt = null;

      response.setDescription(JdbcOperations.DESCRIPTION + ": Update");

      pstmt = this.getPreparedStatement(request);

      if (pstmt == null)
      {
         this.handleError(METHOD_NAME + "PreparedStatement is null");
      }

      try
      {
         returnCode = pstmt.executeUpdate();
      }
      catch (SQLException ex)
      {
         err.append(ex.getErrorCode()).append(", ").append(ex.getMessage());
         err.append(", statement='").append(pstmt.toString()).append("'");

         response.setState(State.ERROR);
         response.setStatus(METHOD_NAME + err.toString());

         this.checkException(ex, err);
         this.handleError(METHOD_NAME + err.toString());
      }

      if (!response.isError() && returnCode != 1)
      {
         err.append("statement='").append(pstmt.toString()).append("'");
         response.setState(State.ERROR);
      }

      if (response.isError())
      {
         response.setState(State.NOTEXIST);
         response.setStatus(METHOD_NAME + err.toString());
      }
      else
      {
         response.setState(State.SUCCESS);
         response.setStatus("Entry updated");
      }

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doDelete(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      int returnCode = 0;
      String METHOD_NAME = CLASS_NAME + ":doDelete(): ";
      String keyName = null;
      String value = null;
      Object keyValue = null;
      DataType keyType = null;
      StringBuilder err = new StringBuilder();
      StringBuilder buf = new StringBuilder();
      ComponentIF subject = null;
      Statement stmt = null;

      response.setDescription(JdbcOperations.DESCRIPTION + ": Delete");

      // Get the key value that's being deleted.
      // Construct the where clause such that the delete will delete the
      // row with the specific key.
      //
      // Note: This also assumes that all deletes will be based on the unique
      // key value. A future enhancement might be to allow for a query to
      // specify the row to delete.

      buf.append("DELETE FROM ").append(_table);

      keyName = request.getKey();
      if (keyName == null || keyName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Key name is null");
      }

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      keyValue = subject.getUniqueId();
      if (keyValue == null)
      {
         this.handleError(METHOD_NAME + "Value for key '" + keyName + "' is null");
      }

      value = StringUtil.clean(_allowedChars, keyValue.toString());

      keyType = subject.getUniqueIdType();

      switch (keyType)
      {
         case STRING:
            buf.append(" WHERE ").append(keyName).append("='").append(value).append("'");
            break;
         case INTEGER:
         case LONG:
            buf.append(" WHERE ").append(keyName).append("=").append(value);
            break;
      }

      response.setUniqueId(subject);

      try
      {
         stmt = _jdbcConn.createStatement();
         returnCode = stmt.executeUpdate(buf.toString());
      }
      catch (SQLException ex)
      {
         err.append(ex.getErrorCode()).append(", ").append(ex.getMessage());
         err.append(", ").append(buf.toString());

         response.setState(State.ERROR);
         response.setStatus(METHOD_NAME + err.toString());

         this.checkException(ex, err);
         this.handleError(METHOD_NAME + err.toString());
      }

      if (!response.isError() && returnCode == 0)
      {
         err.append(buf);
         response.setState(State.ERROR);
      }

      if (response.isError())
      {
         response.setState(State.NOTEXIST);
         response.setStatus(METHOD_NAME + err.toString());
      }
      else
      {
         response.setState(State.SUCCESS);
         response.setStatus("Entry deleted");
      }

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doPasswordChange(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      this.checkPasswordAttribute(request);
      this.doUpdate(request, response);

      response.setDescription(JdbcOperations.DESCRIPTION + ": Password Change");

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doPasswordReset(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      this.getPasswordAttribute(request);
      this.checkPasswordAttribute(request);
      this.doUpdate(request, response);
      this.getPasswordResult(response);

      response.setDescription(JdbcOperations.DESCRIPTION + ": Password Reset");

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doAuthenticate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      boolean bAuthen = false;
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":doAuthenticate(): ";
      String uniqueIdName = null;
      String uniqueIdValue = null;
      String pwdAttrName = null;
      String pwdAttrValue = null;
      String clean = null;
      String userAttrWhereClause = "";
      String[] userAttrNames = null;
      StringBuilder authQuery = null;
      StringBuilder err = new StringBuilder();
      Statement stmt = null; // JDBC
      ResultSet rs = null; //   JDBC
      ComponentIF subject = null; // OpenPTK
      AttrIF pwdAttr = null; // OpenPTK

      /*
       * The response object will have one of the following states:
       * ERROR = something failed before trying to authenticate
       * FAILED = the JDBC query threw an exception
       * SUCCESS = the JDBC query was successful
       */

      response.setDescription(JdbcOperations.DESCRIPTION + ": Authenticate");

      uniqueIdName = request.getSubject().getProperty(DefinitionIF.PROP_AUTHENID_ATTR_NAME);

      if (uniqueIdName == null)
      {
         uniqueIdName = request.getKey();
      }

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      uid = subject.getUniqueId();
      if (uid == null)
      {
         this.handleError(METHOD_NAME + "Uid (from Subject) is null");
      }

      uniqueIdValue = uid.toString();

      if (uniqueIdName != null && uniqueIdName.length() > 0
              && uniqueIdValue != null && uniqueIdValue.length() > 0)
      {
         response.setUniqueId(uniqueIdValue);

         pwdAttrName = subject.getProperty(DefinitionIF.PROP_PASSWORD_ATTR_NAME);

         if (pwdAttrName != null && pwdAttrName.length() > 0)
         {
            pwdAttr = subject.getAttribute(pwdAttrName);
            if (pwdAttr != null)
            {
               pwdAttrValue = pwdAttr.getValueAsString();
               if (pwdAttrValue != null && pwdAttrValue.length() > 0)
               {
                  /*
                   * Get an array of the userAttrNames passed. In some cases,
                   * multiple attriutes might be supplied so each attribute will
                   * be checked for the attribute value
                   */

                  userAttrNames = uniqueIdName.split(",");

                  for (int i = 0; i < userAttrNames.length; i++)
                  {
                     if (i > 0)
                     {
                        userAttrWhereClause += " OR ";
                     }

                     userAttrWhereClause += userAttrNames[i] + " = '"
                             + StringUtil.clean(_allowedChars, uniqueIdValue) + "' ";
                  }

                  /*
                   * Create the query to test the authentication. Currently
                   * this will perform a select with the where clause testing
                   * both the id and password being sent. An example query
                   * would look like:
                   * SELECT 'PASS' FROM employees WHERE id = 'mz30500' AND password = 'Passw0rd'
                   */

                  authQuery = new StringBuilder();
                  authQuery.append(" SELECT 'PASS' ");
                  authQuery.append(" FROM ").append(_table);
                  authQuery.append(" WHERE ( ").append(userAttrWhereClause).append(" ) ");
                  authQuery.append(" AND ").append(pwdAttrName).append(" = '").append(pwdAttrValue).append("'");

                  try
                  {
                     stmt = _jdbcConn.createStatement();
                     rs = stmt.executeQuery(authQuery.toString());

                     if (rs == null)
                     {
                        err.append("result set is null");
                     }
                     else
                     {
                        /*
                         * If the password was "correct" then the SELECT will
                         * return at least one row (in the result set).
                         * If the password is "wrong" then the SELECT not
                         * return any rows.
                         */

                        while (rs.next())
                        {
                           bAuthen = true;
                        }

                        if (bAuthen)
                        {
                           response.setState(State.AUTHENTICATED);
                           response.setStatus("Authenticated: " + uniqueIdValue);
                        }
                        else
                        {
                           response.setState(State.NOTAUTHENTICATED);
                           response.setStatus("Not Authenticated: Invalid Credentials");
                        }
                     }
                  }
                  catch (SQLException ex)
                  {
                     err.append(ex.getErrorCode()).append(", ");
                     err.append(ex.getMessage()).append(", JDBC SQL Authentication Failed");
                     this.checkException(ex, err);
                  }
               }
               else
               {
                  err.append("The '").append(pwdAttrName).append("' attribute is empty");
               }
            }
            else
            {
               err.append("The '").append(pwdAttrName).append("' attribute does not exist");
            }
         }
         else
         {
            err.append("The Context/Operation Property for the Password Attribute is null: '"
                    + DefinitionIF.PROP_PASSWORD_ATTR_NAME + "'");
         }
      }
      else
      {
         err.append("Authentication ID is NULL");
      }

      if (err.length() > 0)
      {
         response.setState(State.FAILED);
         response.setStatus(METHOD_NAME + err.toString());
      }

      return;
   }

   /*
    *****************
    * PRIVATE METHODS
    *****************
    */
   //----------------------------------------------------------------
   private void doJdbcSelect(final RequestIF request, final ResponseIF response,
           final Operation operation) throws OperationException
   //----------------------------------------------------------------
   {
      int sizeList = 0;
      Object keyValue = null;
      String METHOD_NAME = CLASS_NAME + ":doJdbcSelect(): ";
      String fwName = null;
      String srvcName = null;
      String keySrvc = null;
      String searchAttrs = "";
      String whereClause = null;
      String selectQuery = null;
      StringBuilder err = new StringBuilder();
      List<String> attrNames = null;
      List<ComponentIF> ptkResultsList = null; // OpenPTK
      Map<String, AttrIF> map = null;
      Statement stmt = null; // JDBC
      ResultSet rs = null; // JDBC
      ComponentIF subject = null; // OpenPTK
      DataType keyType = null; // OpenPTK

      // Get the Operation key and sevice name

      keySrvc = request.getKey();

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      /*
       * Get the "map" of all the Attribute objects for this "request"
       */

      map = subject.getAttributes();

      /*
       * Need to build the where clause (for the unique search) from the
       * uniqueId and the name of the "key" attribute from the PTK Definition
       *
       * It is assumed that the Primary Key of the table is equal to:
       * subjectDef's "key" and the request uniqueId ... WHERE CLAUSE = keyName
       * + "=" + uniquedId
       */

      switch (operation)
      {
         case READ:
            /*
             * Get the key to build out the where clause
             * The clause is different between String and Integer/Long
             */
            response.setDescription(JdbcOperations.DESCRIPTION + ": Read");
            keyType = subject.getUniqueIdType();
            keyValue = subject.getUniqueId();
            if (keyValue != null && keyValue.toString().length() > 0)
            {
               if (keySrvc != null && keySrvc.length() > 0)
               {
                  switch (keyType)
                  {
                     case STRING:
                        whereClause = keySrvc + "=" + "'"
                                + StringUtil.clean(_allowedChars, keyValue.toString())
                                + "'";
                        break;
                     case INTEGER:
                     case LONG:
                        whereClause = keySrvc + "=" + keyValue.toString();
                        break;
                  }

                  response.setUniqueId(subject);
               }
               else
               {
                  err.append(METHOD_NAME).append("Unique Id attribute name is not set");
               }
            }
            else
            {
               err.append(METHOD_NAME).append("UniqueId value is not set");
            }
            break;
         case SEARCH:
            response.setDescription(JdbcOperations.DESCRIPTION + ": Search");
            response.setStatus("Results Search");
            try
            {
               whereClause = this.getJdbcSearch(request);
            }
            catch (OperationException e)
            {
               err.append(METHOD_NAME).append(e.getMessage());
            }
            break;
      }

      // Get a list of the attributes being reqeusted of the Subject
      // If this list returns anything, then build out the select clause
      // otherwiese, select *, which will return all columns from the
      // table name.

      attrNames = subject.getAttributesNames();

      if (attrNames != null && !attrNames.isEmpty())
      {
         if (keySrvc != null && keySrvc.length() > 0)
         {
            searchAttrs += keySrvc;
         }

         searchAttrs += ", ";
         // Only return the colums being asked for (plus the key)
         sizeList = attrNames.size();
         for (int i = 0; i < sizeList; i++)
         {
            if (i > 0)
            {
               searchAttrs += ", ";
            }
            fwName = attrNames.get(i);
            if (map.containsKey(fwName))
            {
               srvcName = map.get(fwName).getServiceName();
               if (srvcName != null && srvcName.length() > 0)
               {
                  searchAttrs += srvcName;
               }
               else
               {
                  searchAttrs += fwName;
               }
            }
         }
      }
      else
      {
         // Return only the (primary) key
         if (keySrvc != null && keySrvc.length() > 0)
         {
            searchAttrs = keySrvc;
         }
      }

      /*
       * Safety check, make sure there's at least one attribute to return
       */

      if (searchAttrs == null || searchAttrs.length() < 1)
      {
         err.append(METHOD_NAME).append("Operation ").append(operation.toString());
         err.append(" requires at least one attribute to return.");
      }
      else
      {
         // Construct the SELECT queiry. Note that the current implementation
         // was built and tested against MySQL. Modifications will potentially
         // be required for other SQL vendors.

         selectQuery = "SELECT " + searchAttrs + " FROM " + _table;

         if (whereClause != null)
         {
            selectQuery += " WHERE " + whereClause;
         }
      }

      if (this.isDebug())
      {
         Logger.logInfo(METHOD_NAME + selectQuery);
      }

      if (err.length() == 0)
      {
         try
         {
            stmt = _jdbcConn.createStatement();
            rs = stmt.executeQuery(selectQuery);
         }
         catch (SQLException ex)
         {
            err.append(METHOD_NAME).append("Operation ").append(operation.toString());
            err.append(ex.getErrorCode()).append(", ");
            err.append(ex.getMessage()).append(", Select failed");

            response.setState(State.ERROR);
            response.setStatus(METHOD_NAME + err.toString());

            this.checkException(ex, err);
            this.handleError(METHOD_NAME + err.toString());
         }
      }

      if (err.length() > 0)
      {
         response.setState(State.FAILED);
         response.setStatus(err.toString());
      }
      else
      {
         ptkResultsList = this.getPtkResults(request, rs);

         response.setResults(ptkResultsList);

         switch (operation)
         {
            case READ:
               if (ptkResultsList.isEmpty())
               {
                  response.setState(State.NOTEXIST);
                  response.setStatus("Entry does not exist");
               }
               else
               {
                  response.setState(State.SUCCESS);
                  response.setStatus("Entry found");
               }
               break;
            case SEARCH:
               if (ptkResultsList.isEmpty())
               {
                  response.setState(State.SUCCESS);
                  response.setStatus("Nothing was found");
               }
               else
               {
                  response.setState(State.SUCCESS);
                  response.setStatus("Entries found: " + ptkResultsList.size());
               }
               break;
         }
      }

      return;
   }

   //----------------------------------------------------------------------
   private List<ComponentIF> getPtkResults(final RequestIF request, final ResultSet resultSet) throws OperationException
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getPtkResults(): ";
      List<ComponentIF> ptkResultsList = null; //    OpenPTK
      Component ptkComponent = null; //              OpenPTK
      ResultSetMetaData resultSetMetaData = null; // JDBC

      ptkResultsList = new LinkedList<ComponentIF>();

      if (resultSet != null)
      {
         try
         {
            resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next())
            {
               ptkComponent = this.getPtkComponent(request, resultSet,
                       resultSetMetaData);
               ptkResultsList.add(ptkComponent);
            }
         }
         catch (SQLException ex)
         {
            ptkComponent = new Component();
            ptkComponent.setState(State.ERROR);
            ptkComponent.setStatus(METHOD_NAME
                    + ex.getErrorCode() + ", " + ex.getMessage());

            ptkResultsList.add(ptkComponent);
         }
      }
      else
      {
         ptkComponent = new Component();
         ptkComponent.setError(true);
         ptkComponent.setState(State.NULL);
         ptkComponent.setStatus(METHOD_NAME + "Result Set is NULL");

         ptkResultsList.add(ptkComponent);
      }

      return ptkResultsList;
   }

   //----------------------------------------------------------------------
   private Component getPtkComponent(final RequestIF request, final ResultSet rs, final ResultSetMetaData md) throws OperationException
   //----------------------------------------------------------------------
   {
      Object objValue = null;
      boolean hasUid = false;
      boolean bValue = false;
      byte[] bArray = null;
      int srvcType = 0;
      int iColumns = 0;
      int iValue = 0;
      long lValue = 0L;
      String METHOD_NAME = CLASS_NAME + ":getPtkComponent(): ";
      String keySrvc = null;
      String fwName = null;
      String srvcName = null;
      String attrValue = null;
      String strValue = null;
      Component ptkComp = null; // OpenPTK
      BasicAttr ptkAttr = null; // OpenPTK

      ptkComp = new Component();
      keySrvc = request.getKey();

      if (rs != null && md != null)
      {
         try
         {
            iColumns = md.getColumnCount();
         }
         catch (SQLException ex)
         {
            this.handleError(METHOD_NAME
                    + ex.getErrorCode() + ", " + ex.getMessage());
         }

         /*
          * Enumerate through all the column NAMES (ids)
          */
         for (int i = 1; i <= iColumns; i++)
         {
            strValue = null;
            ptkAttr = null;
            fwName = null;

            try
            {
               srvcType = md.getColumnType(i);
               srvcName = md.getColumnName(i);
            }
            catch (SQLException ex)
            {
               this.handleError(METHOD_NAME
                       + ex.getErrorCode() + ", " + ex.getMessage());
            }

            if (srvcName != null && srvcName.length() > 0)
            {
               srvcName = srvcName.toLowerCase();
               fwName = request.getService().getFwName(request.getOperation(), srvcName);

               switch (srvcType)
               {
                  case Types.VARCHAR: // String
                  case Types.LONGVARCHAR: // String
                  case Types.CHAR: // String
                     try
                     {
                        strValue = rs.getString(i);
                     }
                     catch (SQLException ex)
                     {
                        this.handleError(METHOD_NAME + ex.getErrorCode() + ", " + ex.getMessage());
                     }

                     /*
                      * Check to see if this is the "primary key" / "uniqueid"
                      * else process as an attribute
                      */

                     if (srvcName.equalsIgnoreCase(keySrvc))
                     {
                        ptkComp.setUniqueId(strValue);
                        hasUid = true;
                     }
                     else
                     {
                        if (strValue != null && strValue.length() > 0)
                        {
                           ptkAttr = new BasicAttr(fwName, strValue);
                           ptkAttr.setServiceName(srvcName);
                           ptkComp.setAttribute(fwName, ptkAttr);
                        }
                     }
                     break;
                  case Types.INTEGER: // int
                     try
                     {
                        iValue = rs.getInt(i);
                     }
                     catch (SQLException ex)
                     {
                        this.handleError(METHOD_NAME + ex.getErrorCode() + ", " + ex.getMessage());
                     }
                     if (srvcName.equalsIgnoreCase(keySrvc))
                     {
                        ptkComp.setUniqueId(new Integer(iValue));
                        hasUid = true;
                     }
                     else
                     {
                        ptkAttr = new BasicAttr(fwName, iValue);
                        ptkAttr.setServiceName(srvcName);
                        ptkComp.setAttribute(fwName, ptkAttr);
                     }
                     break;
                  case Types.BIGINT: //  long
                  case Types.NUMERIC: // map to Oracle NUMERIC(38,0) type
                     try
                     {
                        lValue = rs.getLong(i);
                     }
                     catch (SQLException ex)
                     {
                        this.handleError(METHOD_NAME + ex.getErrorCode() + ", " + ex.getMessage());
                     }
                     if (srvcName.equalsIgnoreCase(keySrvc))
                     {
                        ptkComp.setUniqueId(new Long(lValue));
                        hasUid = true;
                     }
                     else
                     {
                        ptkAttr = new BasicAttr(fwName, lValue);
                        ptkAttr.setServiceName(srvcName);
                        ptkComp.setAttribute(fwName, ptkAttr);
                     }
                     break;
                  case Types.BIT: // boolean
                     try
                     {
                        bValue = rs.getBoolean(i);
                     }
                     catch (SQLException ex)
                     {
                        this.handleError(METHOD_NAME
                                + ex.getErrorCode() + ", " + ex.getMessage());
                     }
                     ptkAttr = new BasicAttr(fwName, bValue);
                     ptkAttr.setServiceName(srvcName);
                     ptkComp.setAttribute(fwName, ptkAttr);
                     break;
                  case Types.BINARY: //        byte[]
                  case Types.VARBINARY: //     byte[]
                  case Types.LONGVARBINARY: // byte[]
                  case Types.BLOB: //          java.sql.Blob
                     try
                     {
                        bArray = rs.getBytes(i);
                     }
                     catch (SQLException ex)
                     {
                        this.handleError(METHOD_NAME
                                + ex.getErrorCode() + ", " + ex.getMessage());
                     }
                     if (bArray != null && bArray.length > 0)
                     {
                        objValue = bArray;
                        ptkAttr = new BasicAttr(fwName, objValue);
                        ptkAttr.setServiceName(srvcName);
                        ptkComp.setAttribute(fwName, ptkAttr);
                     }
                     break;
                  case Types.DECIMAL: //   java.math.BigDecimal
                  case Types.TINYINT: //   byte
                  case Types.SMALLINT: //  short
                  case Types.REAL: //      float
                  case Types.FLOAT: //     double
                  case Types.DOUBLE: //    double
                  case Types.DATE: //      java.sql.Date
                  case Types.TIME: //      java.sql.Time
                  case Types.TIMESTAMP: // java.sql.Timestamp
                  case Types.CLOB: //      java.sql.Clob
                  case Types.STRUCT: //    java.sql.Struct
                  case Types.REF: //       java.sql.Ref
                  case Types.ARRAY: //     java.sql.Array
                  default:
                     attrValue = JdbcOperations.VALUE_UNSUPPORTED_TYPE;
                     this.handleError(METHOD_NAME + "Attribute '" + srvcName
                             + "' has an unsupported Type: " + srvcType);
               }
            }
            else
            {
               this.handleError(METHOD_NAME + "There is a null Column/Service Name");
            }
         }
      }
      else
      {
         ptkComp.setState(State.FAILED);
         ptkComp.setStatus(METHOD_NAME + "Result Set and/or MetaData are NULL");
      }

      return ptkComp;
   }

   //----------------------------------------------------------------------
   private String getJdbcSearch(final RequestIF request) throws OperationException
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getJdbcSearch(): ";
      String srch = null;
      Query requestQuery = null;
      QueryConverterIF qConverter = null;

      requestQuery = request.getQuery();

      if (requestQuery != null)
      {
         qConverter = new JdbcQueryConverter(requestQuery);
         qConverter.setProperties(this.getProperties());

         try
         {
            srch = (String) qConverter.convert();
         }
         catch (QueryException ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }
      }

      return srch;
   }

   //----------------------------------------------------------------------
   private PreparedStatement getPreparedStatement(final RequestIF request) throws OperationException
   //----------------------------------------------------------------------
   {
      Object value = null;
      String METHOD_NAME = CLASS_NAME + ":getPreparedStatement(): ";
      boolean hasKey = false;
      int attrQty = 0;
      int paramCnt = 1;
      Object keyValue = null;
      String keyName = null;
      String strValue = null;
      String[] srvcNames = null;
      StringBuilder buf = new StringBuilder();
      PreparedStatement pstmt = null; //        JDBC
      Map<String, AttrIF> ptkAttrMap = null; // OpenPTK
      Iterator<AttrIF> ptkAttrIter = null; //   OpenPTK
      AttrIF ptkAttr = null; //                 OpenPTK
      ComponentIF subject = null; //            OpenPTK
      Operation operation = null; //            OpenPTK
      DataType keyType = null; //               OpenPTK

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      keyName = request.getKey();
      keyValue = subject.getUniqueId();
      keyType = subject.getUniqueIdType();
      srvcNames = this.getServiceNames(request);
      attrQty = srvcNames.length;

      operation = request.getOperation();

      /*
       * Build the prepared statement based on the Operation
       */

      switch (operation)
      {
         case CREATE:
            buf.append("INSERT INTO ").append(_table).append(" (");

            /*
             * Check the uniqueId to see if it is set, then include it
             */

            if (keyName != null && keyName.length() > 0
                    && keyValue != null && keyValue.toString().length() > 0)
            {
               hasKey = true;
               buf.append(keyName);
               if (attrQty > 0)
               {
                  buf.append(",");
               }
            }

            /*
             * Add the attribute names
             */

            for (int i = 0; i < attrQty; i++)
            {
               buf.append(srvcNames[i]);
               if (i < (attrQty - 1))
               {
                  buf.append(",");
               }
            }
            buf.append(") VALUES(");

            /*
             * Add the param "place holders" (question marks)
             */

            if (hasKey)
            {
               buf.append("?");
               if (attrQty > 0)
               {
                  buf.append(",");
               }
            }

            for (int i = 0; i < attrQty; i++)
            {
               buf.append("?");
               if (i < (attrQty - 1))
               {
                  buf.append(",");
               }
            }

            buf.append(")");

            break;
         case UPDATE:
         case PWDCHANGE:
         case PWDRESET:
            buf.append("UPDATE ").append(_table).append(" SET ");

            /*
             * Add the attribute names
             */

            for (int i = 0; i < attrQty; i++)
            {
               buf.append(srvcNames[i]).append(" = ?");
               if (i < (attrQty - 1))
               {
                  buf.append(", ");
               }
            }

            /*
             * Add the "where" clause
             */

            if (keyName != null && keyName.length() > 0
                    && keyValue != null && keyValue.toString().length() > 0)
            {
               strValue = StringUtil.clean(_allowedChars, keyValue.toString());
               switch (keyType)
               {
                  case STRING:
                     buf.append(" WHERE ").append(keyName).append(" = '").append(strValue).append("'");
                     break;
                  case INTEGER:
                  case LONG:
                     buf.append(" WHERE ").append(keyName).append(" = ").append(strValue);
                     break;
               }
            }
            else
            {
               this.handleError(METHOD_NAME + "Key name and/or value are null");
            }

            break;
      }

      /*
       * Create the prepared statement from the Connection
       */
      
      if (this.isDebug())
      {
         Logger.logInfo(METHOD_NAME + "statement=\"" + buf.toString() + "\"");
      }

      try
      {
         pstmt = _jdbcConn.prepareStatement(buf.toString());
      }
      catch (SQLException ex)
      {
         this.handleError(METHOD_NAME
                 + ex.getErrorCode() + ", " + ex.getMessage());
      }

      /*
       * Add the values to the prepared statement
       */

      if (hasKey)
      {
         try
         {
            switch (keyType)
            {
               case STRING:
                  pstmt.setString(paramCnt, StringUtil.clean(_allowedChars, keyValue.toString()));
                  break;
               case INTEGER:
                  pstmt.setInt(paramCnt, ((Integer) keyValue).intValue());
                  break;
               case LONG:
                  pstmt.setLong(paramCnt, ((Long) keyValue).intValue());
                  break;
            }
         }
         catch (SQLException ex)
         {
            this.handleError(METHOD_NAME
                    + ex.getErrorCode() + ", " + ex.getMessage()
                    + ", Name='" + keyName + "'");
         }
         paramCnt++;
      }

      ptkAttrMap = request.getSubject().getAttributes();

      if (ptkAttrMap == null)
      {
         this.handleError(METHOD_NAME + "Subject Attribute Map is null");
      }

      ptkAttrIter = ptkAttrMap.values().iterator();

      while (ptkAttrIter.hasNext())
      {
         ptkAttr = ptkAttrIter.next();
         if (ptkAttr == null)
         {
            this.handleError(METHOD_NAME + "Attribute [" + paramCnt + "] is null");
         }

         if (ptkAttr.isMultivalued())
         {
            this.handleError(METHOD_NAME
                    + "Multivalued attributes are not supported: " + ptkAttr.getName());
         }

         value = ptkAttr.getValue();

         switch (ptkAttr.getType())
         {
            case STRING:
               if (value == null)
               {
                  value = "";
               }
               try
               {
                  if (_emptyRemove == true && ((String) value).length() == 0)
                  {
                     pstmt.setString(paramCnt, null);
                  }
                  else
                  {
                     pstmt.setString(paramCnt, StringUtil.clean(_allowedChars, value.toString()));
                  }
               }
               catch (SQLException ex)
               {
                  this.handleError(METHOD_NAME
                          + ex.getErrorCode() + ", " + ex.getMessage()
                          + ", Name='" + ptkAttr.getName() + "'");
               }
               break;
            case INTEGER:
               if (value == null)
               {
                  this.handleError(METHOD_NAME
                          + "Integer Attribute '" + ptkAttr.getName()
                          + "' has a null value");
               }
               try
               {
                  pstmt.setInt(paramCnt, ((Integer) value).intValue());
               }
               catch (SQLException ex)
               {
                  this.handleError(METHOD_NAME
                          + ex.getErrorCode() + ", " + ex.getMessage()
                          + ", Name='" + ptkAttr.getName() + "'");
               }
               break;
            case LONG:
               if (value == null)
               {
                  this.handleError(METHOD_NAME
                          + "Long Attribute '" + ptkAttr.getName()
                          + "' has a null value");
               }
               try
               {
                  pstmt.setLong(paramCnt, ((Long) value).longValue());
               }
               catch (SQLException ex)
               {
                  this.handleError(METHOD_NAME
                          + ex.getErrorCode() + ", " + ex.getMessage()
                          + ", Name='" + ptkAttr.getName() + "'");
               }
               break;
            case BOOLEAN:
               if (value == null)
               {
                  this.handleError(METHOD_NAME + "Boolean Attribute '" + ptkAttr.getName()
                          + "' has a null value");
               }
               try
               {
                  pstmt.setBoolean(paramCnt, ((Boolean) value).booleanValue());
               }
               catch (SQLException ex)
               {
                  this.handleError(METHOD_NAME
                          + ex.getErrorCode() + ", " + ex.getMessage()
                          + ", Name='" + ptkAttr.getName() + "'");
               }
               break;
            case OBJECT:
               if (value instanceof byte[])
               {
                  try
                  {
                     pstmt.setBytes(paramCnt, (byte[]) value);
                  }
                  catch (SQLException ex)
                  {
                     this.handleError(METHOD_NAME
                             + ex.getErrorCode() + ", " + ex.getMessage()
                             + ", Name='" + ptkAttr.getName() + "'");
                  }
               }
               else
               {
                  this.handleError(METHOD_NAME + "Attribute '" + ptkAttr.getName()
                          + "' has an unsupported Object type '" + value.getClass().getSimpleName() + "'");
               }
               break;
         }
         paramCnt++;
      }

      return pstmt;
   }
}
