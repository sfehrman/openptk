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
/**
 * @author Scott Fehrman, Project OpenPTK
 */
package org.openptk.spi;

import org.openptk.api.State;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.Response;
import org.openptk.common.ResponseIF;
import org.openptk.exception.OperationException;
import org.openptk.exception.ServiceException;
import org.openptk.logging.Logger;
import org.openptk.spi.operations.OperationsIF;

//===================================================================
public class RestartService extends Service implements ServiceIF
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public RestartService()
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   /**
    * @param request
    * @return ResponseIF
    * @throws ServiceException
    */
   //----------------------------------------------------------------
   @Override
   public ResponseIF execute(final RequestIF request) throws ServiceException
   //----------------------------------------------------------------
   {
      boolean bRetry = false;
      String METHOD_NAME = CLASS_NAME + ":execute() ";
      Operation operation = Operation.READ;

      ResponseIF response = null;
      OperationsIF oper = null;

      this.setError(false);

      operation = request.getOperation();

      response = new Response(request);
      response.setDescription(RESPONSE_DESC);
      response.setDebugLevel(request.getDebugLevel());
      response.setDebug(request.isDebug());

      if (this.validateRequest(request, response))
      {
         oper = this.getOperation(operation);

         if (oper != null)
         {
            if (oper.isImplemented(operation))
            {
               if (oper.isEnabled(operation))
               {
                  do
                  {
                     bRetry = false;

                     /*
                      * Check the STATE of the Operation instance ...
                      * if it's FAILED ...
                      * try to (re) start it
                      * if it's still failed (error = true) after a re-start
                      * throw the ServiceException
                      */

                     if (oper.getState() == State.FAILED)
                     {
                        oper.shutdown();
                        oper.startup();

                        if (oper.isError())
                        {
                           this.handleError(METHOD_NAME
                                   + oper.getClass().getSimpleName()
                                   + " is in the FAILED State: Restart was unsuccessful.");
                        }
                        else
                        {
                           Logger.logWarning(METHOD_NAME
                                   + oper.getClass().getSimpleName()
                                   + " has been restarted.");
                        }
                     }

                     request.addAttempt();
                     response.setState(State.READY);
                     response.setStatus("");

                     try
                     {
                        oper.preExecute(request, response);
                        oper.execute(request, response);
                        oper.postExecute(request, response);
                     }
                     catch (OperationException ex)
                     {
                        if (oper.getState() == State.FAILED && request.getAttempts() < 2)
                        {
                           /*
                            * The operation is in the failed state (might be able to re-start)
                            * and only one attempt to process the "request" was made.
                            * This could be caused by a long idle time and the "connection closed"
                            */

                           Logger.logWarning(METHOD_NAME + ex.getMessage()
                                   + ", state=" + oper.getStateAsString()
                                   + ", attempts=" + request.getAttempts());

                           bRetry = true;
                        }
                        else
                        {
                           /*
                            * Some other type of exception occured, or more than one attempt was made
                            * The Operation (implementation) class should be
                            * setting the Response object's error, state, status
                            * This is typically reached if there's a null pointer
                            * type of error
                            */

                           Logger.logWarning(METHOD_NAME + ex.getMessage()
                                   + ", state=" + oper.getStateAsString()
                                   + ", status='" + oper.getStatus() + "'"
                                   + ", attempts=" + request.getAttempts());

                           /*
                            * Check the response object
                            * If it's state does not indicate a "problem"
                            * Set the state to ERROR 
                            * Set the status to the exception's message
                            */
                           
                           switch (response.getState())
                           {
                              case NEW:
                              case READY:
                              case SUCCESS:
                                 response.setState(State.ERROR);
                                 response.setStatus(ex.getMessage());
                                 break;
                           }
                        }
                     }
                  }
                  while (bRetry);
               }
               else
               {
                  response.setState(State.ERROR);
                  response.setStatus(METHOD_NAME + "Operation " + operation.toString() + " is not enabled");
               }
            }
            else
            {
               response.setState(State.ERROR);
               response.setStatus(METHOD_NAME + "Operation " + operation.toString() + " is not implemented");
            }
         }
         else
         {
            response.setState(State.ERROR);
            response.setStatus(METHOD_NAME + "Operation is not null");
         }
      }

      return response;
   }
}
