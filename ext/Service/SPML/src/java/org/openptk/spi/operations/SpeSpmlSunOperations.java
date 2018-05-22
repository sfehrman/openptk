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

/*
 * The SPE - SPML interface to Identity Manager doesn't offer a native
 * API for changing/reseting the password. This Service extends the 
 * SpmlSunService class and overrides the doPasswordChange and 
 * doPasswordReset methods.  These methods will use the SPML ModifyRequest
 * to update the user's password attribute.
 */
package org.openptk.spi.operations;

import org.openptk.api.State;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.exception.OperationException;

import org.openspml.message.ModifyRequest;
import org.openspml.message.ModifyResponse;
import org.openspml.util.SpmlException;

/**
 *
 * @author Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public class SpeSpmlSunOperations extends SpmlSunOperations
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String DESCRIPTION = "Sun Identity Manager SPML 1.0, Service Provider Edition";

   //----------------------------------------------------------------
   public SpeSpmlSunOperations()
   //----------------------------------------------------------------
   {
      super();
      this.setDescription(SpeSpmlSunOperations.DESCRIPTION);
      SpeSpmlSunOperations.RESPONSE_DESC = CLASS_NAME + ", Provision Response";
      return;
   }

   //  =============================
   //  ===== PROTECTED METHODS =====
   //  =============================
   
   /**
    * @param request
    * @param response
    * @throws OperationException
    * 
    */
   //----------------------------------------------------------------
   @Override
   protected void doPasswordChange(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doPasswordChange(): ";

      ModifyRequest modifyRequest = null;   // SPML
      ModifyResponse modifyResponse = null; // SPML

      this.checkPasswordAttribute(request);

      modifyRequest = new ModifyRequest();

      this.preUpdate(request, modifyRequest);

      if (request.isError())
      {
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
      }
      else
      {
         try
         {
            modifyResponse = (ModifyResponse) this.getClient().request(modifyRequest);
            this.getClient().throwErrors(modifyResponse);
         }
         catch (SpmlException ex)
         {
            this.logXML(METHOD_NAME, modifyRequest, modifyResponse);
            this.handleError(METHOD_NAME + ": " + ex);
         }

         this.postUpdate(response, modifyResponse);

         if (response.getState() == State.SUCCESS)
         {
            response.setStatus("Password Changed");
         }

         this.saveXML(response, modifyRequest, modifyResponse);
      }

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doPasswordReset(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doPasswordReset(): ";

      ModifyRequest modifyRequest = null;   // SPML
      ModifyResponse modifyResponse = null; // SPML

      this.getPasswordAttribute(request);
      this.checkPasswordAttribute(request);

      modifyRequest = new ModifyRequest();

      this.preUpdate(request, modifyRequest);

      if (request.isError())
      {
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
      }
      else
      {
         try
         {
            modifyResponse = (ModifyResponse) this.getClient().request(modifyRequest);
            this.getClient().throwErrors(modifyResponse);
         }
         catch (SpmlException ex)
         {
            this.logXML(METHOD_NAME, modifyRequest, modifyResponse);
            this.handleError(METHOD_NAME + ": " + ex);
         }

         this.postUpdate(response, modifyResponse);

         if (response.getState() == State.SUCCESS)
         {
            /*
             * Update the Response to include the generated Password
             */
            this.getPasswordResult(response);
         }

         this.saveXML(response, modifyRequest, modifyResponse);
      }

      return;
   }
   //  ===========================
   //  ===== PRIVATE METHODS =====
   //  ===========================
}
