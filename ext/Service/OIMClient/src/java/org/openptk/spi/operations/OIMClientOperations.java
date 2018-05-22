/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010-2011 Oracle America, Inc.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;

import oracle.iam.identity.exception.NoSuchUserException;
import oracle.iam.identity.exception.RoleSearchException;
import oracle.iam.identity.exception.UserLookupException;
import oracle.iam.identity.rolemgmt.api.RoleManager;
import oracle.iam.identity.rolemgmt.api.RoleManagerConstants;
import oracle.iam.identity.rolemgmt.vo.Role;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.identity.usermgmt.vo.UserManagerResult;
import oracle.iam.platform.OIMClient;
import oracle.iam.platform.authz.exception.AccessDeniedException;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;
import oracle.iam.selfservice.exception.AuthSelfServiceException;
import oracle.iam.selfservice.uself.uselfmgmt.api.UnauthenticatedSelfService;

import org.openptk.api.DataType;
import org.openptk.api.Query;
import org.openptk.api.State;
import org.openptk.common.AttrIF;
import org.openptk.common.BasicAttr;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.exception.OperationException;
import org.openptk.exception.QueryException;
import org.openptk.logging.Logger;
import org.openptk.spi.OIMClientQueryConverter;
import org.openptk.spi.QueryConverterIF;

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
public abstract class OIMClientOperations extends Operations
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String NOT_IMPLEMENTED = "Operation not Implemented";
   private static final String PROP_CLIENT_AUTHCONFIG = "client.authconfig";
   private static final String PROP_CLIENT_PROVIDERURL = "client.providerurl";
   protected static final String PROP_FORGOT_CHALLENGE_PREFIX = "forgot.challenge.";
   protected static final String USER_STATUS_ACTIVE = "Active";
   protected static final String USER_STATUS_DELETED = "Deleted";
   protected static final String DESCRIPTION = "Oracle Identity Manager OIMClient API, 11gR1";
   protected static final String RESULT_STATUS_COMPLETED = "COMPLETED";
   protected static final String ATTR_PASSWORD_NAME = "usr_password";
   protected static final String ATTR_ACCOUNTKEY_NAME = "act_key";
   protected static final String ATTR_STATUS_NAME = "Status";
   protected static final Long ATTR_ACCOUNTKEY_VALUE = new Long(1);
   protected String _factoryInitial = null;
   protected String _providerURL = null;
   protected List<String> _defaultChallengeQuestions = null;
   protected OIMClient _oimClientProxy = null;
   protected UserManager _userMgr = null;
   protected RoleManager _roleMgr = null;

   //----------------------------------------------------------------
   public OIMClientOperations()
   //----------------------------------------------------------------
   {
      super();

      this.setType(OperationsType.ORACLEIDM);

      return;
   }

   //----------------------------------------------------------------
   @Override
   @SuppressWarnings("UseOfObsoleteCollectionType") // OIMClient API
   public void startup()
   //----------------------------------------------------------------
   {
      int iQ = 0;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String authconf = null;
      String question = null;
      String username = null;
      String password = null;
      StringBuilder err = new StringBuilder();
      Hashtable<String, String> env = null; // obsolete Java API

      super.startup();

      try
      {
         _providerURL = this.getCheckProp(PROP_CLIENT_PROVIDERURL);
         authconf = this.getCheckProp(PROP_CLIENT_AUTHCONFIG);
         username = this.getCheckProp(OperationsIF.PROP_USER_NAME);
         password = this.getCheckProp(OperationsIF.PROP_USER_PASSWORD);
      }
      catch (OperationException ex)
      {
         this.setState(State.ERROR);
         this.setStatus(ex.getMessage());
      }

      if (!this.isError())
      {
         _factoryInitial = "weblogic.jndi.WLInitialContextFactory";

         /*
          * Tell the JVM where the authenication login configuration file is
          */

         System.setProperty("java.security.auth.login.config", authconf);

         /*
          * Create an instance of the OIMClient class, login, then get
          * a UserManger (used by most of the Operations)
          */

         env = new Hashtable<String, String>(); // obsolete Java API

         env.put(OIMClient.JAVA_NAMING_FACTORY_INITIAL, _factoryInitial);
         env.put(OIMClient.JAVA_NAMING_PROVIDER_URL, _providerURL);

         _oimClientProxy = new OIMClient(env);

         /*
          * Login as the priv. proxy user
          */

         try
         {
            _oimClientProxy.login(username, password.toCharArray());
         }
         catch (LoginException ex)
         {
            err.append(METHOD_NAME).append(ex.getMessage());
            err.append(": user.dir='").append(System.getProperty("user.dir"));
            err.append("'");

            this.checkException(ex, err); // sets:  state, status, error=true
         }

         if (!this.isError())
         {
            _userMgr = _oimClientProxy.getService(UserManager.class);
            _roleMgr = _oimClientProxy.getService(RoleManager.class);

            /*
             * Load the default forgotten password challenge questions
             */

            _defaultChallengeQuestions = new LinkedList<String>();

            do
            {
               question = this.getValue(PROP_FORGOT_CHALLENGE_PREFIX + iQ++);
               if (question != null)
               {
                  _defaultChallengeQuestions.add(question);
               }
            }
            while (question != null);
         }
      }
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void shutdown()
   //----------------------------------------------------------------
   {
      _oimClientProxy = null;

      super.shutdown();

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   public synchronized void execute(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      Operation oper = null;

      if (request == null)
      {
         this.handleError(METHOD_NAME + "Request is null");
      }

      if (response == null)
      {
         this.handleError(METHOD_NAME + "Response is null");
      }

      response.setUniqueId(CLASS_NAME);

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
            this.handleError(METHOD_NAME + "Unimplemented Operation: "
               + oper.toString());
      }
      return;
   }

   //  =============================
   //  ===== PROTECTED METHODS =====
   //  =============================
   //
   //----------------------------------------------------------------
   protected void doCreate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      throw new OperationException(NOT_IMPLEMENTED);
   }

   //----------------------------------------------------------------
   protected void doRead(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      throw new OperationException(NOT_IMPLEMENTED);
   }

   //----------------------------------------------------------------
   protected void doUpdate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      throw new OperationException(NOT_IMPLEMENTED);
   }

   //----------------------------------------------------------------
   protected void doDelete(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      throw new OperationException(NOT_IMPLEMENTED);
   }

   //----------------------------------------------------------------
   protected void doSearch(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      throw new OperationException(NOT_IMPLEMENTED);
   }

   //----------------------------------------------------------------
   protected void doPasswordChange(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      throw new OperationException(NOT_IMPLEMENTED);
   }

   //----------------------------------------------------------------
   protected void doPasswordReset(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      throw new OperationException(NOT_IMPLEMENTED);
   }

   //----------------------------------------------------------------
   @Override
   protected void doPasswordForgot(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      throw new OperationException(NOT_IMPLEMENTED);
   }

   //----------------------------------------------------------------
   protected void doAuthenticate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      throw new OperationException(NOT_IMPLEMENTED);
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   /**
    * @param request
    * @param mapRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void preCreate(final RequestIF request, final HashMap<String, Object> mapRequest,
      final Map<String, Object> mapQnA) throws OperationException
   //----------------------------------------------------------------
   {
      boolean bValidId = false;
      int iCnt = 1;
      int iMax = 999; // safety valve
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String err = null;
      String keyName = null;
      String keyValue = null;
      String baseValue = null;
      User user = null;

      /*
       * Get the unique id  (key) and it's value
       */

      keyName = request.getKey();
      keyValue = this.getUniqueIdValueFromRequest(request).toString();

      /*
       * Check the value ...
       *
       * It exists:
       *    Is "Active":
       *       Error = "User already exists"
       *    Else:
       *       Add a number to the end of the uniqueId
       *       Try again
       * Else:
       *    Use the uniqueId
       */

      baseValue = keyValue;

      while (!bValidId)
      {
         if (iCnt >= iMax)
         {
            this.handleError(METHOD_NAME + "Unique ID counter has reached it's max.");
         }

         user = this.getUser(keyValue);
         if (user != null)
         {
            if (user.getStatus().equalsIgnoreCase(USER_STATUS_ACTIVE))
            {
               err = "User '" + keyValue + "' already exists";
               request.setState(State.INVALID);
               request.setStatus(err);
               this.handleError(METHOD_NAME + err);
            }
            else
            {
               keyValue = baseValue + iCnt++;
            }
         }
         else
         {
            bValidId = true;
         }
      }

      mapRequest.put(keyName, keyValue);
      request.setUniqueId(keyValue);

      /*
       * Get each PTK Attribute that the Subject has
       * create a new Map entry from the PTK Attribute
       */

      this.updateMapFromRequest(request, mapRequest);

      return;
   }

   //----------------------------------------------------------------
   protected final void checkResult(final ResponseIF response, final UserManagerResult result) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":checkResult(): ";
      String msg = null;

      /*
       * check the result object
       */

      if (result == null)
      {
         msg = "UserManagerResult is null";
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      return;
   }

   //----------------------------------------------------------------
   protected final void updateMapFromRequest(final RequestIF request, final HashMap<String, Object> objMap) throws OperationException
   //----------------------------------------------------------------
   {
      Object value = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String err = null;
      String serviceName = null;
      Map<String, AttrIF> attrMap = null;
      AttrIF attr = null;
      DataType type = null;

      attrMap = request.getSubject().getAttributes();

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
         type = attr.getType();
         serviceName = attr.getServiceName();
         value = attr.getValue();
         objMap.put(serviceName, value);
      }

      return;
   }

   //----------------------------------------------------------------
   protected final ComponentIF getComponentFromUser(final RequestIF request, final User user) throws OperationException
   //----------------------------------------------------------------
   {
      Object value = null;
      Long longValue = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String status = null;
      String attrFwName = null;
      String strValue = null;
      String srvcKey = null;
      HashMap<String, Object> mapAttrs = null;
      ComponentIF subject = null;    // OpenPTK
      AttrIF ptkAttr = null;         // OpenPTK
      DataType attrType = null; // OpenPTK

      /*
       * Create a OpenPTK Component object from the OIMClient User object
       */

      status = user.getStatus();

      mapAttrs = user.getAttributes();
      if (mapAttrs == null || mapAttrs.isEmpty())
      {
         this.handleError(METHOD_NAME + "OIMClient User is null");
      }

      srvcKey = request.getKey();  // getKey() returns the Service Name
      if (srvcKey == null || srvcKey.length() < 1)
      {
         this.handleError(METHOD_NAME + "Request Key is NULL");
      }

      /*
       * Create the OpenPTK "subject", which will hold the OIMClient "user" data
       */

      subject = new Component();
      subject.setCategory(Category.SUBJECT);
      subject.setDescription("OIM Client User");

      for (String attrSrvcName : mapAttrs.keySet())
      {
         attrFwName = request.getService().getFwName(request.getOperation(), attrSrvcName);
         value = mapAttrs.get(attrSrvcName);

         if (attrSrvcName.equals(srvcKey))
         {
            subject.setUniqueId((String) value);
         }

         if (value != null)
         {
            if (value instanceof String)
            {
               strValue = (String) value;
               ptkAttr = new BasicAttr(attrFwName, strValue);
            }
            else if (value instanceof Long)
            {
               longValue = (Long) value;
               ptkAttr = new BasicAttr(attrFwName, longValue);
            }
            else
            {
               this.handleError(METHOD_NAME + "User has an unsupportted attribute '"
                  + value.getClass().getName() + "'");
            }
         }
         else
         {
            ptkAttr = new BasicAttr(attrFwName);
         }
         ptkAttr.setServiceName(attrSrvcName);
         subject.setAttribute(attrFwName, ptkAttr);
      }

      subject.setDebugLevel(request.getDebugLevel());
      subject.setDebug(request.isDebug());
      subject.setState(State.READY);
      subject.setStatus("Has " + mapAttrs.size() + " attributes, status='"
         + status + "'");

      return subject;
   }

   //----------------------------------------------------------------
   protected final SearchCriteria getSearchCriteria(final RequestIF request) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String err = null;
      SearchCriteria criteria = null;
      Query theQuery = null;              // OpenPTK
      Query srvcQuery = null;             // OpenPTK
      Query userQuery = null;             // OpenTPK
      QueryConverterIF qConverter = null; // OpenTPK

      srvcQuery = request.getService().getQuery(request.getOperation());
      userQuery = request.getQuery();

      /*
       * if the Operation Query is set
       * set its "serviceName" equal to the "frameworkName"
       * the "frameworkName is used by the QueryConverter
       */

      if (srvcQuery != null)
      {
         srvcQuery.setServiceName(srvcQuery.getName());
      }

      /*
       * if there's a Operation Query and a User Query
       * create a new Query and "AND" them together
       * else use either the User or Operation Query
       */

      if (srvcQuery != null && userQuery != null)
      {
         theQuery = new Query(Query.Type.AND);

         theQuery.addQuery(userQuery);
         theQuery.addQuery(srvcQuery);
      }
      else if (srvcQuery != null)
      {
         theQuery = srvcQuery;
      }
      else if (userQuery != null)
      {
         theQuery = userQuery;
      }

      /*
       * A query is required
       */

      if (theQuery == null)
      {
         theQuery = new Query(Query.Type.EQ, request.getKey(), "*");
         theQuery.setServiceName(request.getKey());
      }

      qConverter = new OIMClientQueryConverter(theQuery);
      try
      {
         criteria = (SearchCriteria) qConverter.convert();
      }
      catch (QueryException ex)
      {
         err = ex.getMessage();
         request.setState(State.ERROR);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      return criteria;
   }

   //----------------------------------------------------------------
   protected final String[] getUserChallengeQuestions(final String uniqueId) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      String[] questions = null;
      UnauthenticatedSelfService unauthSelfService = null;

      unauthSelfService = _oimClientProxy.getService(UnauthenticatedSelfService.class);

      try
      {
         questions = unauthSelfService.getChallengeQuestions(uniqueId);
      }
      catch (AuthSelfServiceException ex)
      {
         questions = null;
         msg = METHOD_NAME + "Could not get Challenge questions for '" + uniqueId
            + "', using Service defaults (" + ex.getMessage() + ")";
         Logger.logWarning(msg);
      }

      /*
       * If we could not get the users questions ... use the defaults
       */

      if (questions == null)
      {
         questions = _defaultChallengeQuestions.toArray(new String[_defaultChallengeQuestions.size()]);
      }

      return questions;
   }

   //----------------------------------------------------------------
   protected final String getUserKeyFromUserLogin(final String userLogin) throws OperationException
   //----------------------------------------------------------------
   {
      boolean isUserLogin = true;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      StringBuilder err = new StringBuilder();
      String userKey = null;
      User user = null;
      Set<String> attrNames = null;

      if (userLogin == null || userLogin.length() < 1)
      {
         this.handleError(METHOD_NAME + "User Login is null");
      }

      try
      {
         user = _userMgr.getDetails(userLogin, attrNames, isUserLogin);
      }
      catch (AccessDeniedException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());
         this.handleError(err.toString());
      }
      catch (NoSuchUserException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());
         this.handleError(err.toString());
      }
      catch (UserLookupException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());
         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (user == null)
      {
         this.handleError(METHOD_NAME + "User object is null");
      }

      userKey = user.getId();

      return userKey;
   }

   //----------------------------------------------------------------
   protected final String getUserLoginFromUserKey(final String userKey) throws OperationException
   //----------------------------------------------------------------
   {
      boolean isUserLogin = false;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String userLogin = null;
      StringBuilder err = new StringBuilder();
      Set<String> attrNames = null;
      User user = null;        // OIMClient API

      if (userKey == null || userKey.length() < 1)
      {
         this.handleError(METHOD_NAME + "User Key is null");
      }

      try
      {
         user = _userMgr.getDetails(userKey, attrNames, isUserLogin);
      }
      catch (AccessDeniedException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());
         this.handleError(err.toString());
      }
      catch (NoSuchUserException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());
         this.handleError(err.toString());
      }
      catch (UserLookupException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());
         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (user == null)
      {
         this.handleError(METHOD_NAME + "User object is null");
      }

      userLogin = user.getLogin();

      if (userLogin == null || userLogin.length() < 1)
      {
         this.handleError(METHOD_NAME + "User Login is empty / null");
      }

      return userLogin;
   }

   //----------------------------------------------------------------
   protected String getRoleKeyFromRoleName(final String roleName) throws OperationException
   //----------------------------------------------------------------
   {
      Object attrValue = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String roleKey = null;
      StringBuilder err = new StringBuilder();
      Role role = null;               // OIM Client API
      List<Role> roles = null;        // OIM Client API
      SearchCriteria criteria = null; // OIM Clinet API

      /*
       * Use the "roleName" to obtain the internal "roleKey"
       */

      if (roleName == null || roleName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Role Name is empty/null");
      }

      criteria = new SearchCriteria(RoleManagerConstants.ROLE_NAME, roleName, SearchCriteria.Operator.EQUAL);

      try
      {
         roles = _roleMgr.search(criteria, null, null);
      }
      catch (AccessDeniedException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());
         this.handleError(err.toString());
      }
      catch (RoleSearchException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());
         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (roles == null || roles.isEmpty())
      {
         this.handleError(METHOD_NAME + "A Role was not found for name '" + roleName + "'");
      }

      role = roles.get(0);
      if (role == null)
      {
         this.handleError(METHOD_NAME + "Role '" + roleName + "' is null");
      }

      attrValue = role.getAttribute(RoleManagerConstants.ROLE_KEY);
      if (attrValue == null)
      {
         this.handleError(METHOD_NAME + "Role '" + roleName
            + "' has a null value for attrbiute '" + RoleManagerConstants.ROLE_KEY + "'");
      }

      roleKey = (String) attrValue;

      return roleKey;
   }

   //----------------------------------------------------------------
   protected String getRoleNameFromRoleKey(final String roleKey) throws OperationException
   //----------------------------------------------------------------
   {
      Object attrValue = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String roleName = null;
      StringBuilder err = new StringBuilder();
      SearchCriteria criteria = null; // OIM Clinet API
      Role role = null;               // OIM Client API
      List<Role> roles = null;        // OIM Client API

      /*
       * Use the internal "roleKey" to obtain the "roleName"
       */

      if (roleKey != null && roleKey.length() > 0)
      {
         criteria = new SearchCriteria(RoleManagerConstants.ROLE_KEY, roleKey, SearchCriteria.Operator.EQUAL);

         try
         {
            roles = _roleMgr.search(criteria, null, null);
         }
         catch (AccessDeniedException ex)
         {
            err.append(METHOD_NAME).append(ex.getMessage());
            this.handleError(err.toString());
         }
         catch (RoleSearchException ex)
         {
            err.append(METHOD_NAME).append(ex.getMessage());
            this.checkException(ex, err);
            this.handleError(err.toString());
         }

         if (roles != null && !roles.isEmpty())
         {
            role = roles.get(0);
            if (role != null)
            {
               attrValue = role.getAttribute(RoleManagerConstants.ROLE_NAME);
               if (attrValue != null)
               {
                  roleName = (String) attrValue;
               }
            }
         }
      }

      if (roleName == null)
      {
         roleName = "(Name not found for '" + (roleKey != null ? roleKey : "null") + "')";
      }

      return roleName;
   }
   /*
    * =========================
    * ==== PRIVATE METHODS ====
    * =========================
    */

   //----------------------------------------------------------------
   private User getUser(final String uniqueId) throws OperationException
   //----------------------------------------------------------------
   {
      boolean isUserLogin = true;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      StringBuilder err = new StringBuilder();
      Set<String> attrNames = new HashSet<String>();
      User user = null;

      try
      {
         user = _userMgr.getDetails(uniqueId, attrNames, isUserLogin);
      }
      catch (AccessDeniedException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());
         this.handleError(err.toString());
      }
      catch (NoSuchUserException ex)
      {
         user = null;
      }
      catch (UserLookupException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());
         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      return user;
   }
}
