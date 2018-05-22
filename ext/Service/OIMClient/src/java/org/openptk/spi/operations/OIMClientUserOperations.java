/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010-2013 Oracle America, Inc.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;

import oracle.iam.identity.exception.NoSuchUserException;
import oracle.iam.identity.exception.SearchKeyNotUniqueException;
import oracle.iam.identity.exception.UserAlreadyExistsException;
import oracle.iam.identity.exception.UserCreateException;
import oracle.iam.identity.exception.UserDeleteException;
import oracle.iam.identity.exception.UserLookupException;
import oracle.iam.identity.exception.UserManagerException;
import oracle.iam.identity.exception.UserModifyException;
import oracle.iam.identity.exception.UserSearchException;
import oracle.iam.identity.exception.ValidationFailedException;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.identity.usermgmt.vo.UserManagerResult;
import oracle.iam.platform.OIMClient;
import oracle.iam.platform.authz.exception.AccessDeniedException;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;
import oracle.iam.selfservice.exception.AuthSelfServiceException;
import oracle.iam.selfservice.uself.uselfmgmt.api.UnauthenticatedSelfService;

import org.openptk.api.State;
import org.openptk.common.AttrIF;
import org.openptk.common.BasicAttr;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.definition.DefinitionIF;
import org.openptk.exception.OperationException;
import org.openptk.util.RandomData;
import org.openptk.util.StringUtil;

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
 *            Response is returned, error = true
 *
 */
//===================================================================
public class OIMClientUserOperations extends OIMClientOperations
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public OIMClientUserOperations()
   //----------------------------------------------------------------
   {
      super();

      this.setDescription(OIMClientUserOperations.DESCRIPTION);
      OIMClientUserOperations.RESPONSE_DESC = CLASS_NAME + ", Provision Response";

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
      StringBuilder err = new StringBuilder();
      HashMap<String, Object> mapRequest = new HashMap<String, Object>();
      Map<String, Object> mapQnA = new HashMap<String, Object>();
      User user = null;
      UserManagerResult result = null;

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
       * Add mandatory attribute(s) for create
       */

      mapRequest.put(ATTR_ACCOUNTKEY_NAME, ATTR_ACCOUNTKEY_VALUE);

      /*
       * execute the implementation logic for a CREATE
       */

      user = new User((String) mapRequest.get(request.getKey()), mapRequest);

      try
      {
         result = _userMgr.create(user);
      }
      catch (ValidationFailedException ex)
      {
         response.setState(State.INVALID);
         msg = ex.getMessage();
      }
      catch (AccessDeniedException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage();
      }
      catch (UserAlreadyExistsException ex)
      {
         response.setState(State.INVALID);
         msg = ex.getMessage();
      }
      catch (UserCreateException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());

         response.setState(State.ERROR);
         response.setStatus(err.toString());

         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (msg != null)
      {
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      /*
       * Post process the response
       */

      response.setUniqueId(user.getLogin());
      this.postCreate(response, result);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doRead(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      boolean isUserLogin = true;
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      String uniqueId = null;
      String qAttr = null;
      String aAttr = null; // Forgotten Password Challenge answers 
      String[] questions = null;
      Set<String> attrNames = new HashSet<String>();
      StringBuilder err = new StringBuilder();
      User user = null; //           OIMClient
      AttrIF attr = null; //         OpenPTK
      ComponentIF subject = null; // OpenPTK

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      uid = subject.getUniqueId();
      if (uid == null)
      {
         this.handleError(METHOD_NAME + "UniqueId (from Subject) is null");
      }

      /*
       * Check if the "read" is for the forgotten password challenge questions and answers
       */

      qAttr = subject.getProperty(DefinitionIF.PROP_CHALLENGE_QUESTIONS_ATTR_NAME);
      if (qAttr == null || qAttr.length() < 1)
      {
         msg = "Definition Property '" + DefinitionIF.PROP_CHALLENGE_QUESTIONS_ATTR_NAME
            + "' is null or not set.";
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      aAttr = subject.getProperty(DefinitionIF.PROP_CHALLENGE_ANSWERS_ATTR_NAME);
      if (aAttr == null || aAttr.length() < 1)
      {
         msg = "Definition Property '" + DefinitionIF.PROP_CHALLENGE_ANSWERS_ATTR_NAME
            + "' is null or not set.";
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      try
      {
         this.preRead(request, attrNames);
      }
      catch (OperationException ex)
      {
         msg = ex.getMessage();
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      /*
       * Check the collection of attribute names for the "questions" attribute.
       * Remove it from the collection
       * Make the "special" API call to get the questions.
       */

      uniqueId = uid.toString();

      if (attrNames.contains(qAttr))
      {
         attrNames.remove(qAttr);

         try
         {
            questions = this.getUserChallengeQuestions(uniqueId);
         }
         catch (OperationException ex)
         {
            msg = ex.getMessage();
            response.setState(State.ERROR);
            response.setStatus(msg);
            this.handleError(METHOD_NAME + msg);
         }
      }
      
      if (attrNames.contains(aAttr))
      {
         attrNames.remove(aAttr);
      }

      /*
       * execute the implementation logic for a READ
       */

      try
      {
         user = _userMgr.getDetails(uniqueId, attrNames, isUserLogin);
      }
      catch (AccessDeniedException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage() + "uniqueId='" + uniqueId + "'";
      }
      catch (NoSuchUserException ex)
      {
         response.setState(State.NOTEXIST);
         msg = ex.getMessage() + ", " + State.NOTEXIST.toString() + ", uniqueId='" + uniqueId + "'";
      }
      catch (UserLookupException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());

         response.setState(State.ERROR);
         response.setStatus(err.toString());

         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (msg != null)
      {
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      this.postRead(response, user, questions);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doUpdate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      StringBuilder err = new StringBuilder();
      HashMap<String, Object> objMap = new HashMap<String, Object>();
      User user = null;
      UserManagerResult result = null;

      this.checkExist(request, response);

      try
      {
         this.preUpdate(request, objMap);
      }
      catch (OperationException ex)
      {
         msg = ex.getMessage();
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      user = new User(request.getUniqueId().toString(), objMap);

      try
      {
         result = _userMgr.modify(request.getKey(), request.getUniqueId().toString(), user);
      }
      catch (ValidationFailedException ex)
      {
         response.setState(State.INVALID);
         msg = ex.getMessage();
      }
      catch (NoSuchUserException ex)
      {
         response.setState(State.NOTEXIST);
         msg = ex.getMessage();
      }
      catch (SearchKeyNotUniqueException ex)
      {
         response.setState(State.INVALID);
         msg = ex.getMessage();
      }
      catch (AccessDeniedException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage();
      }
      catch (UserModifyException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());

         response.setState(State.ERROR);
         response.setStatus(err.toString());

         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (msg != null)
      {
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      /*
       * Post process the response
       */

      response.setUniqueId(user.getLogin());
      this.postUpdate(response, result);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doDelete(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      StringBuilder err = new StringBuilder();
      UserManagerResult result = null;

      this.checkExist(request, response);

      try
      {
         this.preDelete(request);
      }
      catch (OperationException ex)
      {
         msg = ex.getMessage();
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      try
      {
         result = _userMgr.delete(request.getKey(), request.getUniqueId().toString());
      }
      catch (NoSuchUserException ex)
      {
         response.setState(State.NOTEXIST);
         msg = ex.getMessage();
      }
      catch (SearchKeyNotUniqueException ex)
      {
         response.setState(State.INVALID);
         msg = ex.getMessage();
      }
      catch (ValidationFailedException ex)
      {
         response.setState(State.INVALID);
         msg = ex.getMessage();
      }
      catch (AccessDeniedException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage();
      }
      catch (UserDeleteException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());

         response.setState(State.ERROR);
         response.setStatus(err.toString());

         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (msg != null)
      {
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      this.postDelete(response, result);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doSearch(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      StringBuilder err = new StringBuilder();
      Set<String> attrNames = new HashSet<String>();
      SearchCriteria criteria = null;
      HashMap<String, Object> mapParams = null;
      List<User> list = null;

      try
      {
         criteria = this.preSearch(request, attrNames);
      }
      catch (OperationException ex)
      {
         response.setState(request.getState());
         response.setStatus(request.getStatus());
         this.handleError(METHOD_NAME + request.getStatus());
      }

      mapParams = new HashMap<String, Object>();
      mapParams.put("STARTROW", 0);
      mapParams.put("ENDROW", -1);

      try
      {
         list = _userMgr.search(criteria, attrNames, mapParams);
      }
      catch (AccessDeniedException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage();
      }
      catch (UserSearchException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());

         response.setState(State.ERROR);
         response.setStatus(err.toString());

         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (msg != null)
      {
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      this.postSearch(response, list);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doPasswordChange(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      boolean isUserLogin = true;
      boolean setPasswordResetFlag = false;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      String name = null;
      String password = null;
      StringBuilder err = new StringBuilder();
      HashMap<String, Object> objMap = new HashMap<String, Object>();
      Locale locale = null;

      this.checkExist(request, response);

      try
      {
         this.preUpdate(request, objMap);
      }
      catch (OperationException ex)
      {
         msg = ex.getMessage();
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      if (objMap.containsKey(ATTR_PASSWORD_NAME))
      {
         password = (String) objMap.get(ATTR_PASSWORD_NAME);
         if (password == null || password.length() < 1)
         {
            msg = "Password Attribute '" + ATTR_PASSWORD_NAME + "' is null.";
            response.setState(State.INVALID);
            response.setStatus(msg);
            this.handleError(METHOD_NAME + msg);
         }
      }
      else
      {
         msg = "Password Attribute '" + ATTR_PASSWORD_NAME + "' was not found.";
         response.setState(State.INVALID);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      try
      {
         _userMgr.changePassword(request.getUniqueId().toString(),
            password.toCharArray(), isUserLogin, locale, setPasswordResetFlag);
      }
      catch (NoSuchUserException ex)
      {
         response.setState(State.NOTEXIST);
         msg = ex.getMessage();
      }
      catch (AccessDeniedException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage();
      }
      catch (UserManagerException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());

         response.setState(State.ERROR);
         response.setStatus(err.toString());

         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (msg != null)
      {
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      response.setState(State.SUCCESS);
      response.setStatus("Password Changed");

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
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      this.checkExist(request, response);

      this.getPasswordAttribute(request);

      this.doPasswordChange(request, response);

      this.getPasswordResult(response);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doPasswordForgot(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      boolean wasReset = false;
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      String uniqueId = null;
      String resetPwd = null;
      StringBuilder err = new StringBuilder();
      Map<String, Object> mapQA = new HashMap<String, Object>();
      UnauthenticatedSelfService unauthSelfService = null;
      ComponentIF subject = null;

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      uid = subject.getUniqueId();
      if (uid == null)
      {
         this.handleError(METHOD_NAME + "UniqueId (from Subject) is null");
      }

      uniqueId = uid.toString();
      if (uniqueId == null || uniqueId.length() < 1)
      {
         msg = "uniqueId is null";
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      try
      {
         this.prePasswordForgot(request, mapQA);
      }
      catch (OperationException ex)
      {
         msg = ex.getMessage();
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      unauthSelfService = _oimClientProxy.getService(UnauthenticatedSelfService.class);

      resetPwd = "O" + RandomData.getString(15);

      try
      {
         wasReset = unauthSelfService.resetPassword(uniqueId, mapQA, resetPwd.toCharArray());
      }
      catch (AuthSelfServiceException ex)
      {
         err.append(METHOD_NAME).append("Password Forgot Error: ").append(ex.getMessage());

         response.setState(State.ERROR);
         response.setStatus(err.toString());

         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (wasReset == true)
      {
         response.setState(State.AUTHENTICATED);
         response.setStatus("User verified, response to Questions match stored values.");
      }
      else
      {
         response.setState(State.NOTAUTHENTICATED);
         response.setStatus("User NOT verified, response to Questions DO NOT match stored values.");
      }

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @SuppressWarnings("UseOfObsoleteCollectionType") // OIMClient API
   @Override
   protected void doAuthenticate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      String username = null;
      String password = null;
      String pwdAttrName = null;
      StringBuilder err = new StringBuilder();
      Hashtable<String, String> userEnv = null; // obsolete Java API
      OIMClient userClient = null; // OIM11g
      AttrIF attr = null; // OpenPTK
      ComponentIF subject = null; // OpenPTK

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      uid = subject.getUniqueId();
      if (uid == null)
      {
         this.handleError(METHOD_NAME + "UniqueId (from Subject) is null");
      }

      response.setState(State.NOTAUTHENTICATED);

      userEnv = new Hashtable<String, String>(); // obsolete Java API

      userEnv.put(OIMClient.JAVA_NAMING_FACTORY_INITIAL, "weblogic.jndi.WLInitialContextFactory");
      userEnv.put(OIMClient.JAVA_NAMING_PROVIDER_URL, _providerURL);

      userClient = new OIMClient(userEnv);

      username = uid.toString();
      if (username == null || username.length() < 1)
      {
         msg = "username is not set";
      }
      else
      {
         pwdAttrName = request.getSubject().getProperty(DefinitionIF.PROP_PASSWORD_ATTR_NAME);
         if (pwdAttrName == null || pwdAttrName.length() < 1)
         {
            msg = "The Property for the Password Attribute is null: '"
               + DefinitionIF.PROP_PASSWORD_ATTR_NAME + "'";
         }
         else
         {
            attr = request.getSubject().getAttribute(pwdAttrName);
            if (attr == null)
            {
               msg = "The '" + pwdAttrName + "' attribute does not exist";

            }
            else
            {
               password = attr.getValueAsString();
               if (password == null || password.length() < 1)
               {
                  msg = "The '" + pwdAttrName + "' attribute is empty";
               }
            }
         }
      }

      if (msg != null)
      {
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      try
      {
         userClient.login(username, password.toCharArray());
      }
      catch (LoginException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());

         response.setState(State.ERROR);
         response.setStatus(err.toString());

         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      response.setState(State.AUTHENTICATED);
      response.setStatus("User '" + username + "' has been verified");

      return;
   }

   /**
    * @param request
    * @param attrNames
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void preRead(final RequestIF request, final Set<String> attrNames) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String err = null;
      String keyName = null;
      String keyValue = null;
      String serviceName = null;
      Map<String, AttrIF> attrMap = null;
      AttrIF attr = null;
      ComponentIF subject = null;

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      keyName = request.getKey();
      keyValue = this.getUniqueIdValueFromRequest(request).toString(); // check the value

      request.setUniqueId(keyValue);

      attrNames.add(keyName);

      attrMap = subject.getAttributes();

      if (attrMap == null || attrMap.isEmpty())
      {
         err = "Subject has no attributes";
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      for (String mapKey : attrMap.keySet())
      {
         attr = attrMap.get(mapKey);
         if (attr == null)
         {
            err = "Attribute '" + mapKey + "' is null";
            request.setState(State.FAILED);
            request.setStatus(err);
            this.handleError(METHOD_NAME + err);
         }
         serviceName = attr.getServiceName();
         attrNames.add(serviceName);
      }

      /*
       * Make sure the attribute set contains status
       */

      attrNames.add(ATTR_STATUS_NAME);

      return;
   }

   /**
    * @param request
    * @param objMap
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void preUpdate(final RequestIF request, final HashMap<String, Object> objMap) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String err = null;
      String keyName = null;
      String keyValue = null;

      /*
       * Check the unique id
       */

      keyName = request.getKey();
      keyValue = this.getUniqueIdValueFromRequest(request).toString();

      request.setUniqueId(keyValue);

      /*
       * Get each PTK Attribute that the Subject has
       * create a new Map entry from the PTK Attribute
       */

      this.updateMapFromRequest(request, objMap);

      return;
   }

   /**
    * @param request
    * @param objMap
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void preDelete(final RequestIF request) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      /*
       * Check the unique id
       */

      request.setUniqueId(this.getUniqueIdValueFromRequest(request).toString());

      return;
   }

   /**
    * @param request
    * @param objMap
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected SearchCriteria preSearch(final RequestIF request, final Set<String> attrNames) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String err = null;
      String serviceName = null;
      SearchCriteria criteria = null;
      Map<String, AttrIF> attrMap = null;
      AttrIF attr = null;

      /*
       * Set the attribute names from the Request
       */

      attrNames.add(request.getKey());

      attrMap = request.getSubject().getAttributes();

      if (attrMap == null || attrMap.isEmpty())
      {
         err = "Subject has no attributes";

         request.setState(State.INVALID);
         request.setStatus(err);

         this.handleError(METHOD_NAME + err);
      }

      for (String mapKey : attrMap.keySet())
      {
         attr = attrMap.get(mapKey);
         if (attr == null)
         {
            err = "Attribute '" + mapKey + "' is null";

            request.setState(State.INVALID);
            request.setStatus(err);

            this.handleError(METHOD_NAME + err);
         }
         serviceName = attr.getServiceName();
         attrNames.add(serviceName);
      }

      /*
       * Make sure the attribute set contains status
       */

      attrNames.add(ATTR_STATUS_NAME);

      /*
       * Create the search criteria
       */

      criteria = this.getSearchCriteria(request);

      return criteria;
   }

   /**
    *
    * @param request
    * @param mapQA
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void prePasswordForgot(final RequestIF request, final Map<String, Object> mapQA) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      String[] forgotQuestions = null;
      String[] forgotAnswers = null;
      AttrIF attrForgotQuestions = null;
      AttrIF attrForgotAnswers = null;

      /*
       * The "questions" attribute contains the pre-defined questions that the
       * user needs to provide answers to.
       */

      attrForgotQuestions = request.getSubject().getAttribute(DefinitionIF.ATTR_PWD_FORGOT_QUESTIONS);
      if (attrForgotQuestions == null)
      {
         msg = "Attribute '" + DefinitionIF.ATTR_PWD_FORGOT_QUESTIONS + "' is null";
      }

      /*
       * The "answers" attribute contains the actual end-user input/response to the
       * forgotten password questions.
       */

      attrForgotAnswers = request.getSubject().getAttribute(DefinitionIF.ATTR_PWD_FORGOT_ANSWERS);
      if (attrForgotAnswers == null)
      {
         msg = "Attribute '" + DefinitionIF.ATTR_PWD_FORGOT_ANSWERS + "' is null";
      }

      forgotQuestions = this.getAttrValueAsStringArray(attrForgotQuestions);
      forgotAnswers = this.getAttrValueAsStringArray(attrForgotAnswers);

      if (forgotQuestions.length < 1)
      {
         msg = "There are no questions";
      }

      if (forgotAnswers.length < 1)
      {
         msg = "There are no answers";
      }

      if (forgotQuestions.length != forgotAnswers.length)
      {
         msg = "Qty of questions (" + forgotQuestions.length
            + ") / answers (" + forgotAnswers.length + ") do not match";
         if (this.isDebug())
         {
            msg += ", Questions=" + StringUtil.arrayToString(forgotQuestions)
               + ", Answers=" + StringUtil.arrayToString(forgotAnswers);
         }
      }

      if (msg != null)
      {
         this.handleError(METHOD_NAME + msg);
      }

      for (int i = 0; i < forgotAnswers.length; i++)
      {
         mapQA.put(forgotQuestions[i], forgotAnswers[i]);
      }

      return;
   }

   /**
    * @param response
    * @param result
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postCreate(final ResponseIF response, final UserManagerResult result) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String status = null;

      this.checkResult(response, result);

      status = result.getStatus();

      if (status.equalsIgnoreCase(RESULT_STATUS_COMPLETED))
      {
         response.setState(State.SUCCESS);
         response.setStatus("Entry Created");
         response.setProperty("entityid", result.getEntityId());
      }
      else
      {
         response.setState(State.ERROR);
         response.setStatus("Unknown result status: '" + status);
      }

      return;
   }

   /**
    * @param response
    * @param user
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postRead(final ResponseIF response, final User user, final String[] questions) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      String qAttr = null;
      ComponentIF subject = null; // OpenPTK
      AttrIF ptkAttr = null;    // OpenPTK

      if (user == null)
      {
         msg = "User is null";
         response.setResults(new ArrayList<ComponentIF>());
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      if (user.getStatus().equalsIgnoreCase(USER_STATUS_DELETED))
      {
         msg = "User does not exist (Deleted)";
         response.setResults(new ArrayList<ComponentIF>());
         response.setState(State.NOTEXIST);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      response.setUniqueId(user.getLogin());

      try
      {
         subject = this.getComponentFromUser(response.getRequest(), user);
      }
      catch (OperationException ex)
      {
         msg = ex.getMessage();
      }

      if (msg != null)
      {
         response.setState(State.ERROR);
         response.setStatus(msg);
      }
      else
      {
         if (questions != null && questions.length > 0)
         {
            qAttr = response.getRequest().getSubject().getProperty(DefinitionIF.PROP_CHALLENGE_QUESTIONS_ATTR_NAME);
            ptkAttr = new BasicAttr(qAttr, questions);
            subject.setAttribute(qAttr, ptkAttr);
         }
         response.setStatus("Entry found");
         response.addResult(subject);
         response.setState(State.SUCCESS);
      }

      return;
   }

   /**
    * @param response
    * @param result
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postUpdate(final ResponseIF response, final UserManagerResult result) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String status = null;

      this.checkResult(response, result);

      status = result.getStatus();

      if (status.equalsIgnoreCase(RESULT_STATUS_COMPLETED))
      {
         response.setState(State.SUCCESS);
         response.setStatus("Entry Updated");
         response.setProperty("entityid", result.getEntityId());
      }
      else
      {
         response.setState(State.ERROR);
         response.setStatus("Unknown result status: '" + status);
      }

      return;
   }

   /**
    * @param response
    * @param result
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postDelete(final ResponseIF response, final UserManagerResult result) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String status = null;

      this.checkResult(response, result);

      status = result.getStatus();

      if (status.equalsIgnoreCase(RESULT_STATUS_COMPLETED))
      {
         response.setState(State.SUCCESS);
         response.setStatus("Entry Deleted");
      }
      else
      {
         response.setState(State.ERROR);
         response.setStatus("Unknown result status: '" + status);
      }

      return;
   }

   /**
    * 
    * @param response
    * @param users
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postSearch(final ResponseIF response, final List<User> users) throws OperationException
   //----------------------------------------------------------------
   {
      int iItems = 0;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      ComponentIF subject = null; // OpenPTK

      response.setDescription(RESPONSE_DESC + ": Search");

      if (users == null)
      {
         msg = "List (of Users) is NULL";
         response.setState(State.FAILED);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      if (users.isEmpty())
      {
         msg = "Nothing was found";
         response.setStatus(msg);
         response.setState(State.SUCCESS);
         response.setDescription(RESPONSE_DESC + ": Search, " + msg);
      }
      else
      {
         iItems = users.size();
         for (User user : users)
         {
            subject = this.getComponentFromUser(response.getRequest(), user);

            if (subject == null)
            {
               msg = "OIMClient Result had a null subject";
               response.setState(State.FAILED);
               response.setStatus(msg);
               this.handleError(METHOD_NAME + msg);
            }

            subject.setDebugLevel(response.getDebugLevel());
            subject.setDebug(response.isDebug());
            response.addResult(subject);
         }

         response.setStatus("" + iItems + " entries returned");
         response.setState(State.SUCCESS);
         response.setDescription(RESPONSE_DESC + ": Search, " + iItems + " items");
      }

      return;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private void checkExist(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      boolean isUserLogin = true;
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String userId = null;
      String msg = null;
      Set<String> attrNames = new HashSet<String>();
      User user = null;
      ComponentIF subject = null;

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      uid = subject.getUniqueId();
      if (uid == null)
      {
         this.handleError(METHOD_NAME + "UniqueId (from Subject) is null");
      }

      userId = uid.toString();

      try
      {
         user = _userMgr.getDetails(userId, attrNames, isUserLogin);
      }
      catch (AccessDeniedException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage();
      }
      catch (NoSuchUserException ex)
      {
         response.setState(State.NOTEXIST);
         msg = "User '" + userId + "' does not exist.";
      }
      catch (UserLookupException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage();
      }

      if (user == null)
      {
         response.setState(State.NOTEXIST);
         msg = "User '" + userId + "' does not exist (null)";
      }

      if (user.getStatus().equalsIgnoreCase(USER_STATUS_DELETED))
      {
         response.setResults(new ArrayList<ComponentIF>());
         msg = "User '" + userId + "' does not exist (Deleted)";
         response.setState(State.NOTEXIST);
      }

      if (msg != null)
      {
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      return;
   }
}
