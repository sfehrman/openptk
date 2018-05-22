/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2011-2012 Project OpenPTK
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

import java.sql.DriverManager;
import java.sql.SQLException;

import org.openptk.api.State;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.exception.OperationException;
import org.openptk.logging.Logger;

/**
 *
 * @author Scott Fehrman, Project OpenPTK
 *
 */
//===================================================================
public class EmbedJdbcOperations extends JdbcOperations
//===================================================================
{

   private static final String DESCRIPTION = "Java DataBase Connection (Embedded)";
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String PROTOCOL = "jdbc:derby:";
   private static final String DERBY_LOG = "derby.log";
   private static final String PROP_PATH_RELATIVE = "path.relative";
   private static final String PROP_DERBY_STREAM_ERROR_FILE = "derby.stream.error.file";

   //----------------------------------------------------------------
   public EmbedJdbcOperations()
   //----------------------------------------------------------------
   {
      super();

      this.setDescription(EmbedJdbcOperations.DESCRIPTION);
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
       * Specify which operations are enabled. Can be changed at run-time
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

   //----------------------------------------------------------------
   @Override
   public void startup()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":startup(): ";
      String home = null;
      String relative = null;
      String temp = null;
      StringBuilder buf = new StringBuilder();
      StringBuilder err = new StringBuilder();

      super.startup();

      if (_jdbcConn == null)
      {

         /*
          * set the embedded derby log directory/file
          */

         temp = this.getProperty(PROP_OPENPTK_TEMP);
         if (temp == null || temp.length() < 1)
         {
            this.setState(State.ERROR);
            this.setStatus(METHOD_NAME + "Property '" + PROP_OPENPTK_TEMP + "' is null/empty");
         }
         else
         {
            if (temp.endsWith("/"))
            {
               temp = temp + DERBY_LOG;
            }
            else
            {
               temp = temp + "/" + DERBY_LOG;
            }

            /*
             * build the connection URL
             */

            buf.append(PROTOCOL);

            home = this.getProperty(PROP_OPENPTK_HOME);
            if (home != null && home.length() > 0)
            {
               buf.append(home);
            }

            relative = this.getProperty(PROP_PATH_RELATIVE);
            if (relative != null && relative.length() > 0)
            {
               if (home != null && !home.endsWith("/"))
               {
                  buf.append("/");
               }

               buf.append(relative);
            }

            _url = buf.toString();

            System.setProperty(PROP_DERBY_STREAM_ERROR_FILE, temp);

            if (this.getDebugLevelAsInt() > 2)
            {
               Logger.logInfo(METHOD_NAME
                       + "url='" + _url + "', user='" + _user + "', "
                       + PROP_DERBY_STREAM_ERROR_FILE + "='" + temp + "', "
                       + "user.dir='" + System.getProperty("user.dir") + "'");
            }

            try
            {
               _jdbcConn = DriverManager.getConnection(_url);
            }
            catch (SQLException ex)
            {
               err.append(METHOD_NAME).append("DriverManager.getConnection error: ");
               err.append("url='").append(_url).append("', ");
               err.append("user='").append(_user).append("', ");
               err.append(PROP_DERBY_STREAM_ERROR_FILE).append("='").append(temp).append("', ");
               err.append("user.dir='").append(System.getProperty("user.dir")).append("', ");
               err.append(ex.getMessage());

               this.checkException(ex, err); // also sets: state, status, error
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   @Override
   public void shutdown()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":shutdown(): ";

      try
      {
         DriverManager.getConnection(PROTOCOL + ";shutdown=true");
      }
      catch (SQLException e)
      {
         /*
          * Per the Derby docs ...
          * A clean shutdown always throws SQL exception XJ015, which can be ignored.
          */
      }

      super.shutdown();

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

      Operation oper = null;

      response.setUniqueId(this.CLASS_NAME);

      /*
       * Verify the JDBC Connection
       * if it's "null" or "closed" ... get the connection
       * else ... make sure it's "valid"
       */

      try
      {
         if (_jdbcConn == null || _jdbcConn.isClosed())
         {
            _jdbcConn = DriverManager.getConnection(_url);
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
