/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2008-2009 Sun Microsystems, Inc.
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
package org.openptk.context;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.exception.ProvisionException;
import org.openptk.exception.ServiceException;
import org.openptk.logging.Logger;
import org.openptk.spi.ServiceIF;
import org.openptk.spi.operations.OperationsIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//
//===================================================================
public class TimeoutContext extends Context implements ContextIF
//===================================================================
{
   private String CLASS_NAME = this.getClass().getSimpleName();
   private int VALUE_TIMEOUT_DEFAULT = 10000; // timeout value in milliseconds

   //----------------------------------------------------------------
   public TimeoutContext()
   //----------------------------------------------------------------
   {
      super();
      this.setDescription("Timeout Context");
      return;
   }


   /**
    * @param ctx
    */
   //----------------------------------------------------------------
   public TimeoutContext(final ContextIF ctx)
   //----------------------------------------------------------------
   {
      super(ctx);
      this.setDescription("Timeout Context (copy)");
      return;
   }


   /**
    * @param request
    * @return
    * @throws ProvisionException
    */
   //----------------------------------------------------------------
   @Override
   public synchronized ResponseIF execute(final RequestIF request) throws ProvisionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      int timeout = 0;
      int timeoutDefault = this.VALUE_TIMEOUT_DEFAULT;
      String timeoutStr = null;
      String callerId = null;
      ResponseIF response = null;
      Execute exec = null;
      FutureTask<ResponseIF> task = null;
      Thread thrd = null;
      OperationsIF operation = null;
      Operation oper = Operation.READ;

      if (request == null)
      {
         this.handleError(METHOD_NAME + "Request is null");
      }

      this.checkService();

      callerId = METHOD_NAME + "[" 
         + ( this.getUniqueId() != null ? this.getUniqueId().toString() : "(null)" )
         + "]";

      if (request.isDebug())
      {
         this.debug(request, callerId);
      }

      /*
       * Run PRE Actions
       */

      try
      {
         this.preAction(request);
      }
      catch (ProvisionException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      if (this.isTimeStamp())
      {
         this.setTimeStamp(ComponentIF.EXECUTE_BEGIN);
      }

      oper = request.getOperation();

      /*
       * Get the Service's timeout for the specific Operation
       */

      operation = this.getService().getOperation(oper);

      if (operation != null)
      {
         timeout = this.getService().getTimeout(oper);
      }
      else
      {

         /*
          * Try the "global/default" timeout value for the entire Service
          */

         timeoutStr = this.getService().getProperty("timeout");

         if (timeoutStr == null)
         {
            timeoutStr = "NOT SET";
         }

         try
         {
            timeout = Integer.parseInt(timeoutStr);
         }
         catch (Exception ex)
         {
            Logger.logWarning(METHOD_NAME +
               ": invalid or missing Service [" + request.getOperationAsString() +
               "] timeout value: '" + timeoutStr +
               "', using default value (" + timeoutDefault + " msecs)");
            timeout = timeoutDefault;
         }
      }

      /*
       * Execute BEGIN
       */

      exec = new Execute(this.getService(), request);
      task = new FutureTask<ResponseIF>(exec);
      thrd = new Thread(task);
      thrd.start();

      try
      {
         response = task.get(timeout, TimeUnit.MILLISECONDS); // wait X milliseconds);
      }
      catch (TimeoutException ex)
      {
         this.handleError(METHOD_NAME + "Operation Timed out (" + timeout + " msec.)");
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      /*
       * Execute END
       */

      if (this.isTimeStamp())
      {
         this.setTimeStamp(ComponentIF.EXECUTE_END);
         this.logTime(this.getTimeStamp(ComponentIF.EXECUTE_BEGIN),
            this.getTimeStamp(ComponentIF.EXECUTE_END),
            callerId + " " + request.getOperationAsString());
      }

      if (this.isAudit())
      {
         this.logAudit(response);
      }

      if (this.isSort())
      {
         this.sortResults(response);
      }

      switch (response.getState())
      {
         case SUCCESS:

            /*
             * Run all postActions
             */

            try
            {
               this.postAction(response);
            }
            catch (ProvisionException ex)
            {
               this.handleError(METHOD_NAME + ex.getMessage());
            }
            break;
         case AUTHENTICATED:
         case NOTAUTHENTICATED:
            break;
         case NOTEXIST:
         case INVALID:
         case FAILED:
            Logger.logWarning(response.getStatus());
            break;
         case ERROR:
            this.handleError(METHOD_NAME + response.getStatus());
            break;
         default:
            this.handleError(METHOD_NAME + "Invalid State: "
               + response.getStateAsString() + ", " + response.getStatus());
            break;
      }

      if (response.isDebug())
      {
         debug(response, callerId);
      }

      return response;
   }

   /*
    * PRIVATE INNER CLASS:  used by the FutureTack method to support timeouts
    */
   //
   //===================================================================
   class Execute implements Callable<ResponseIF>
   //===================================================================
   {
      ServiceIF _service = null;
      RequestIF _request = null;
      ResponseIF _response = null;


      /**
       * @param service
       * @param request
       */
      //----------------------------------------------------------------
      public Execute(final ServiceIF service, final RequestIF request)
      //----------------------------------------------------------------
      {
         _service = service;
         _request = request;
         return;
      }


      /**
       * @return
       * @throws ServiceException
       */
      //----------------------------------------------------------------
      @Override
      public ResponseIF call() throws ServiceException
      //----------------------------------------------------------------
      {
         _response = _service.execute(_request);   // DO IT
         return _response;
      }
   }


}

