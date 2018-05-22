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

import org.openspml.v2.msg.pass.ResetPasswordResponse;
import org.openspml.v2.msg.pass.SetPasswordResponse;
import org.openspml.v2.msg.spml.AddResponse;
import org.openspml.v2.msg.spml.DeleteResponse;
import org.openspml.v2.msg.spml.LookupResponse;
import org.openspml.v2.msg.spml.ModifyResponse;
import org.openspml.v2.msg.spmlsearch.SearchResponse;

import org.openptk.api.State;
import org.openptk.common.ResponseIF;
import org.openptk.exception.OperationException;
import org.openptk.logging.Logger;

/**
 *
 * @author Scott Fehrman
 */
//===================================================================
public class Spml2WavesetOperations extends Spml2Operations
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String DESCRIPTION = "Waveset Identity Manager (SPML) 2.0";
   private static final String MSG_TIMEOUT = "SPML2CredentialsTimeoutException";

   //----------------------------------------------------------------
   public Spml2WavesetOperations()
   //----------------------------------------------------------------
   {
      super();

      this.setDescription(Spml2WavesetOperations.DESCRIPTION);
      Spml2WavesetOperations.RESPONSE_DESC = CLASS_NAME + ", Provision Response";

      return;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   /**
    * @param response
    * @param createResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void postCreate(ResponseIF response, AddResponse createResponse) throws OperationException
   //----------------------------------------------------------------
   {
      this.checkTimeoutCondition(response);
      super.postCreate(response, createResponse);
      return;
   }

   /**
    * @param response
    * @param readResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void postRead(ResponseIF response, LookupResponse readResponse) throws OperationException
   //----------------------------------------------------------------
   {
      this.checkTimeoutCondition(response);
      super.postRead(response, readResponse);
      return;
   }

   /**
    * @param response
    * @param modifyResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void postUpdate(ResponseIF response, ModifyResponse modifyResponse) throws OperationException
   //----------------------------------------------------------------
   {
      this.checkTimeoutCondition(response);
      super.postUpdate(response, modifyResponse);
      return;
   }

   /**
    * @param response
    * @param deleteResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void postDelete(ResponseIF response, DeleteResponse deleteResponse) throws OperationException
   //----------------------------------------------------------------
   {
      this.checkTimeoutCondition(response);
      super.postDelete(response, deleteResponse);
      return;
   }

   /**
    * @param response
    * @param searchResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void postSearch(ResponseIF response, SearchResponse searchResponse) throws OperationException
   //----------------------------------------------------------------
   {
      this.checkTimeoutCondition(response);
      super.postSearch(response, searchResponse);
      return;
   }

   /**
    * @param response
    * @param setPwdResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void postPasswordChange(ResponseIF response, SetPasswordResponse setPwdResponse) throws OperationException
   //----------------------------------------------------------------
   {
      this.checkTimeoutCondition(response);
      super.postPasswordChange(response, setPwdResponse);
      return;
   }

   /**
    * @param response
    * @param resetPwdResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void postPasswordReset(ResponseIF response, ResetPasswordResponse resetPwdResponse) throws OperationException
   //----------------------------------------------------------------
   {
      this.checkTimeoutCondition(response);
      super.postPasswordReset(response, resetPwdResponse);
      return;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private void checkTimeoutCondition(ResponseIF response)
   //----------------------------------------------------------------
   {
      /*
       * The Session for SPML2 Client timesout when there is
       * five (5) minutes of inactivity.
       *
       * If a request is made after this timeout a few things happen
       * - An error / exception is generated
       * - A new session is created using the userid/password
       * - The operation is preformed with the new session
       *
       * The problem is that we get a FALSE NEGATIVE:
       * - The exception is caught and the Response error flag is
       *   set along with the Repsonse's State and Status (Exception message)
       * - Any values related to the operation are still returned using
       *   the new Session
       *
       * We need to check the "response" object's Error value.
       * If the error == "true" we need to evalute its Status.
       * Check the Status for value related to the "timeout".
       * If it is related to the "timeout" then clear the Error, State, Status
       * Record the timeout event in the Log as an "INFO" message
       */

      String METHOD_NAME = CLASS_NAME + ": checkTimeoutCondition():";
      String value = null;

      if (response.isError())
      {
         value = response.getStatus();
         if (value != null && value.length() > 0)
         {
            if (value.indexOf(Spml2WavesetOperations.MSG_TIMEOUT) != -1)
            {
               response.setError(false);
               response.setState(State.SUCCESS);
               response.setStatus("INFO: " + Spml2WavesetOperations.MSG_TIMEOUT);

               Logger.logInfo(METHOD_NAME + " " + Spml2WavesetOperations.MSG_TIMEOUT);
            }
         }
      }

      return;
   }
}
