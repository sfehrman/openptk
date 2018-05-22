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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.spi.operations;

import org.openptk.api.State;
import org.openptk.common.ResponseIF;
import org.openptk.exception.OperationException;
import org.openptk.logging.Logger;

import org.openspml.client.LighthouseClient;
import org.openspml.message.AddResponse;
import org.openspml.message.DeleteResponse;
import org.openspml.message.ExtendedResponse;
import org.openspml.message.ModifyResponse;
import org.openspml.message.SearchResponse;

/**
 *
 * @author Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public class SpmlSunOperations extends SpmlOperations
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
    private static final String DESCRIPTION = "Sun Identity Manager SPML 1.0, Lighthouse client";
    private static final String MSG_TIMEOUT = "WSCredentialsTimeoutException";

    //----------------------------------------------------------------
    public SpmlSunOperations()
    //----------------------------------------------------------------
    {
        super();
        this.setDescription(SpmlSunOperations.DESCRIPTION);
        SpmlSunOperations.RESPONSE_DESC = CLASS_NAME + ", Provision Response";
        this.setClient(new LighthouseClient());
        return;
    }

    //----------------------------------------------------------------
    @Override
    public void startup()
    //----------------------------------------------------------------
    {
        String user = null;
        String password = null;

        super.startup();

        user = this.getValue(OperationsIF.PROP_USER_NAME);
        password = this.getValue(OperationsIF.PROP_USER_PASSWORD);

        if (user != null && user.length() > 0)
        {
            if (password != null && password.length() > 0)
            {
                ((LighthouseClient) this.getClient()).setUser(user);
                ((LighthouseClient) this.getClient()).setPassword(password);
            }
            else
            {
                this.setState(State.ERROR);
                this.setStatus("Property " + SpmlSunOperations.PROP_USER_PASSWORD +
                   " is NULL");
            }
        }
        else
        {
            this.setState(State.ERROR);
            this.setStatus("Property " + SpmlSunOperations.PROP_USER_NAME +
               " is NULL");
        }
        
        return;
    }

    //  =============================
    //  ===== PROTECTED METHODS =====
    //  =============================
    //

    /**
     * @param response
     * @param createResponse
     * @throws OperationException
     */
    //----------------------------------------------------------------
   @Override
    protected void postCreate(final ResponseIF response, final AddResponse createResponse) throws OperationException
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
    protected void postRead(final ResponseIF response, final SearchResponse readResponse) throws OperationException
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
    protected void postUpdate(final ResponseIF response, final ModifyResponse modifyResponse) throws OperationException
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
    protected void postDelete(final ResponseIF response, final DeleteResponse deleteResponse) throws OperationException
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
    * @param extResponse
    * @throws OperationException
    */
    //----------------------------------------------------------------
   @Override
    protected void postPasswordChange(final ResponseIF response, final ExtendedResponse extResponse) throws OperationException
    //----------------------------------------------------------------
    {
        this.checkTimeoutCondition(response);
        super.postPasswordChange(response, extResponse);
        return;
    }


   /**
    * @param response
    * @param extResponse
    * @throws OperationException
    */
    //----------------------------------------------------------------
   @Override
    protected void postPasswordReset(final ResponseIF response, final ExtendedResponse extResponse) throws OperationException
    //----------------------------------------------------------------
    {
        this.checkTimeoutCondition(response);
        super.postPasswordReset(response, extResponse);
        return;
    }

    //  ===========================
    //  ===== PRIVATE METHODS =====
    //  ===========================
    //
    //----------------------------------------------------------------
    private void checkTimeoutCondition(final ResponseIF response)
    //----------------------------------------------------------------
    {
        /*
         * The Session for Lighthouse Client timesout when there is
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
                if (value.indexOf(SpmlSunOperations.MSG_TIMEOUT) != -1)
                {
                    response.setError(false);
                    response.setState(State.SUCCESS);
                    response.setStatus("INFO: " + SpmlSunOperations.MSG_TIMEOUT);

                    Logger.logInfo(METHOD_NAME + " " + SpmlSunOperations.MSG_TIMEOUT);
                }
            }
        }

        return;
    }
}
