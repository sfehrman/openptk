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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import oracle.iam.platform.OIMClient;
import oracle.iam.platform.kernel.ValidationFailedException;
import oracle.iam.request.exception.BulkBeneficiariesAddException;
import oracle.iam.request.exception.BulkEntitiesAddException;
import oracle.iam.request.exception.InvalidRequestDataException;
import oracle.iam.request.exception.InvalidRequestException;
import oracle.iam.request.exception.RequestServiceException;
import oracle.iam.selfservice.exception.UnauthenticatedSelfServiceException;
import oracle.iam.selfservice.uself.uselfmgmt.api.UnauthenticatedSelfService;

import org.openptk.api.State;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.definition.DefinitionIF;
import org.openptk.exception.OperationException;

/**
 *
 * @author Scott Fehrman, Oracle America, Inc.
 */
/*
 * Meaning of Response State:
 *
 * ERROR   : An error with the infrastructure / configuration / connection
 *           An Exception will be thrown
 *
 * SUCCESS : The operation was successful
 *           Response is returned, error = false (default)
 *
 * INVALID : There is something wrong / missing from the input
 *           Response is returned, error = true
 *
 * FAILED  : The operation failed due to some business logic problem
 *           Response is returned, error = true
 *
 * NOTEXIST : The "entry" being referenced was not found
 *           Response is returned, error = true
 *
 */
//===================================================================
public class OIMClientUnauthenOperations extends OIMClientOperations
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String PROP_TEMPLATE_REGISTRATION = "template.registration";
   private OIMClient _oimClientUnauth = null;
   private UnauthenticatedSelfService _unauthSelfService = null;
   private String _template = null;

   //----------------------------------------------------------------
   public OIMClientUnauthenOperations()
   //----------------------------------------------------------------
   {
      super();

      this.setDescription(OIMClientUserOperations.DESCRIPTION);
      OIMClientUserOperations.RESPONSE_DESC = CLASS_NAME + ", Provision Response";

      /*
       * Specify which operations are implemented
       */

      this.setImplemented(Operation.CREATE, true);

      /*
       * Specify which operations are enabled. Can be changed at run-time
       */

      this.setEnabled(Operation.CREATE, true);

      return;
   }

   //----------------------------------------------------------------
   @Override
   @SuppressWarnings("UseOfObsoleteCollectionType") // OIMClient API
   public void startup()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":startup(): ";
      Hashtable<String, String> env = null; // obsolete Java API

      super.startup();

      env = new Hashtable<String, String>(); // obsolete Java API
      env.put(OIMClient.JAVA_NAMING_FACTORY_INITIAL, _factoryInitial);
      env.put(OIMClient.JAVA_NAMING_PROVIDER_URL, _providerURL);

      _oimClientUnauth = new OIMClient(env);

      try
      {
         _template = this.getCheckProp(PROP_TEMPLATE_REGISTRATION);
      }
      catch (OperationException ex)
      {
         this.setState(State.ERROR);
         this.setStatus(ex.getMessage());
      }

      if (!this.isError())
      {
         _unauthSelfService = _oimClientUnauth.getService(UnauthenticatedSelfService.class);
      }

      return;
   }

   //----------------------------------------------------------------
   @Override
   public void shutdown()
   //----------------------------------------------------------------
   {
      _oimClientUnauth = null;

      super.shutdown();

      return;
   }

   //  =============================
   //  ===== PROTECTED METHODS =====
   //  =============================
   //
   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doCreate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      String attrName = null;
      String requestId = null;
      StringBuilder err = new StringBuilder();
      HashMap<String, Object> mapRequest = new HashMap<String, Object>();
      Map<String, Object> mapQnA = new HashMap<String, Object>();
      Map<String, String> mapAttr = new HashMap<String, String>();
      Iterator<String> iter = null;

      try
      {
         this.preCreate(request, mapRequest, mapQnA);
      }
      catch (OperationException ex)
      {
         msg = ex.getMessage();
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      /*
       * Remove the Questions and Answers from the Request Map
       * Add them to the QnA Map
       */

      try
      {
         this.updateQuestionAnswerMap(request, mapRequest, mapQnA);
      }
      catch (OperationException ex)
      {
         msg = ex.getMessage();
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }


      /*
       * initalize the Attribute Name-to-Name Map
       */

      iter = mapRequest.keySet().iterator();
      while (iter.hasNext())
      {
         attrName = iter.next();
         mapAttr.put(attrName, attrName);
      }

      try
      {
         requestId = _unauthSelfService.submitRegistrationRequest(
            mapRequest, mapQnA, _template, mapAttr);
      }
      catch (InvalidRequestException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage();
      }
      catch (InvalidRequestDataException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage();
      }
      catch (BulkBeneficiariesAddException ex)
      {
         response.setState(State.ERROR);
        msg = ex.getMessage();
      }
      catch (BulkEntitiesAddException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage();
      }
      catch (ValidationFailedException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage();
      }
      catch (RequestServiceException ex)
      {
         response.setState(State.ERROR);
         err.append(METHOD_NAME).append(ex.getMessage());
         this.checkException(ex, err);
         this.handleError(err.toString());
      }
      catch (UnauthenticatedSelfServiceException ex)
      {
         response.setState(State.ERROR);
         err.append(METHOD_NAME).append(ex.getMessage());
         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (msg != null)
      {
         response.setStatus(msg);
      }
      else
      {
         response.setUniqueId(requestId);
         response.setState(State.SUCCESS);
         response.setStatus("User Registered, request='" + requestId + "'");
      }

      return;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private void updateQuestionAnswerMap(final RequestIF request,
      final Map<String, Object> mapRequest, final Map<String, Object> mapQnA) throws OperationException
   //----------------------------------------------------------------
   {
      Object obj = null;
      String METHOD_NAME = CLASS_NAME + ":updateQuestionAnswerMap(): ";
      String msg = null;
      String qAttr = null;
      String aAttr = null;
      String[] questions = null;
      String[] answers = null;

      qAttr = request.getSubject().getProperty(DefinitionIF.PROP_CHALLENGE_QUESTIONS_ATTR_NAME);
      if (qAttr != null)
      {
         aAttr = request.getSubject().getProperty(DefinitionIF.PROP_CHALLENGE_ANSWERS_ATTR_NAME);
         if (aAttr != null)
         {
            obj = mapRequest.get(qAttr);
            if (obj instanceof String[])
            {
               questions = (String[]) obj;
               obj = mapRequest.get(aAttr);
               if (obj instanceof String[])
               {
                  answers = (String[]) obj;
                  if (questions.length < 1)
                  {
                     msg = "Must provide at least one question";
                  }
                  else if (questions.length != answers.length)
                  {
                     msg = "The number of questions '" + questions.length
                        + "' do not match the number of answers '" + answers.length
                        + "'";
                  }
                  else
                  {
                     for (int i = 0; i < questions.length; i++)
                     {
                        mapQnA.put(questions[i], answers[i]);
                     }

                     mapRequest.remove(qAttr);
                     mapRequest.remove(aAttr);
                  }
               }
               else
               {
                  msg = "Answers Attribute value must be a String Array";
               }
            }
            else
            {
               msg = "Question Attribute value must be a String Array";
            }
         }
         else
         {
            msg = "Property '" + DefinitionIF.PROP_CHALLENGE_ANSWERS_ATTR_NAME + "' is null";
         }
      }
      else
      {
         msg = "Property '" + DefinitionIF.PROP_CHALLENGE_QUESTIONS_ATTR_NAME + "' is null";
      }

      if (msg != null)
      {
         this.handleError(METHOD_NAME + msg);
      }

      return;
   }
}
