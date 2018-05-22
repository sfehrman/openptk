/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010 Oracle America, Inc.
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
package org.openptk.spi.operations;

import java.sql.SQLException;

import oracle.jdbc.pool.OracleDataSource;

import org.openptk.api.State;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.exception.OperationException;

/**
 *
 * @author Scott Fehrman, Oracle America, Inc.
 *
 * Notes:
 *
 * By default, the Oracle database uses case sensitive queries.
 * The OpenPTK Framework needs case in-sensitive queries.
 * This can be changed with the following commands:
 * ALTER SESSION SET NLS_COMP=LINGUISTIC;
 * ALTER SESSION SET NLS_SORT=BINARY_CI;
 * 
 */
//===================================================================
public class OracleJdbcOperations extends JdbcOperations
//===================================================================
{
   private static final String DESCRIPTION = "Java DataBase Connection (Oracle)";
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private OracleDataSource _ods = null;

   //----------------------------------------------------------------
   public OracleJdbcOperations()
   //----------------------------------------------------------------
   {
      super();
      
      this.setDescription(OracleJdbcOperations.DESCRIPTION);
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

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   public synchronized void execute(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      StringBuilder err = new StringBuilder();

      /*
       * Verify the JDBC Connection
       * if it's "null" or "closed" ... get the connection
       * else ... make sure it's "valid"
       */

      Operation oper = null;

      response.setUniqueId(this.CLASS_NAME);

      try
      {
         if (_jdbcConn == null || _jdbcConn.isClosed())
         {
            _ods = new OracleDataSource();
            _ods.setUser(_user);
            _ods.setPassword(_password);
            _ods.setURL(_url);
            _jdbcConn = _ods.getConnection();
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
}
