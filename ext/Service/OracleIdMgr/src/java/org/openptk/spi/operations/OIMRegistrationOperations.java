/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010 Oracle America
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
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import Thor.API.Base.tcUtilityOperationsIntf;
import Thor.API.Exceptions.tcAPIException;
import Thor.API.Exceptions.tcChallengeInfoException;
import Thor.API.Exceptions.tcDuplicateSelfRegistrationException;
import Thor.API.Exceptions.tcDuplicateUserException;
import Thor.API.Exceptions.tcInvalidManagerException;
import Thor.API.Exceptions.tcOrganizationNotFoundException;
import Thor.API.Exceptions.tcRequiredDataMissingException;
import Thor.API.tcUtilityFactory;

import com.thortech.xl.util.config.ConfigurationClient;
import com.thortech.xl.util.config.ConfigurationClient.ComplexSetting;

import org.openptk.api.State;
import org.openptk.common.AttrIF;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.definition.DefinitionIF;
import org.openptk.exception.OperationException;

/**
 *
 * @author Scott Fehrman
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
public class OIMRegistrationOperations extends Operations
//===================================================================
{
   public static final String PROP_XLCLIENT_HOME = "xlclient.home";
   public static final String PROP_XLCLIENT_AUTHCONFIG = "xlclient.authconfig";
   public static final String PROP_XLCLIENT_POLICY = "xlclient.policy";
   public static final String PROP_XLCLIENT_PROVIDERURL = "xlclient.providerurl";
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String SERVICE_ATTR_CONFIRMPASSWORD = "ConfirmPassword";
   private static final String DESCRIPTION = "Oracle Identity Manager 9.x API, Registration";
   private ComplexSetting _config = null;
   private Properties _settings = null;
   private tcUtilityFactory _utilFactory = null;
   private tcUtilityOperationsIntf _utilOperations = null;

   //----------------------------------------------------------------
   public OIMRegistrationOperations()
   //----------------------------------------------------------------
   {
      super();

      this.setDescription(OIMRegistrationOperations.DESCRIPTION);
      OIMRegistrationOperations.RESPONSE_DESC = CLASS_NAME + ", Provision Response";
      this.setType(OperationsType.ORACLEIDM);

      /*
       * Specify which operations are implemented
       */

      this.setImplemented(Operation.CREATE, true);
      this.setImplemented(Operation.READ, false);
      this.setImplemented(Operation.UPDATE, false);
      this.setImplemented(Operation.DELETE, false);
      this.setImplemented(Operation.SEARCH, false);
      this.setImplemented(Operation.PWDCHANGE, false);
      this.setImplemented(Operation.PWDRESET, false);

      /*
       * Specify which operations are enabled. Can be changed at run-time
       */

      this.setEnabled(Operation.CREATE, true);
      this.setEnabled(Operation.READ, false);
      this.setEnabled(Operation.UPDATE, false);
      this.setEnabled(Operation.DELETE, false);
      this.setEnabled(Operation.SEARCH, false);
      this.setEnabled(Operation.PWDCHANGE, false);
      this.setEnabled(Operation.PWDRESET, false);

      return;
   }

   //----------------------------------------------------------------
   @Override
   public void startup()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":startup(): ";
      String username = null;
      String password = null;
      String xlhome = null;
      String authconf = null;
      String policy = null;
      String provider = null;
      String msg = null;

      super.startup();

      /*
       * get and validate the properties
       */

      try
      {
         xlhome = this.getCheckProp(PROP_XLCLIENT_HOME);
         authconf = this.getCheckProp(PROP_XLCLIENT_AUTHCONFIG);
         policy = this.getCheckProp(PROP_XLCLIENT_POLICY);
         provider = this.getCheckProp(PROP_XLCLIENT_PROVIDERURL);
         username = this.getCheckProp(OperationsIF.PROP_USER_NAME);
         password = this.getCheckProp(OperationsIF.PROP_USER_PASSWORD);
      }
      catch (OperationException ex)
      {
         this.setError(true);
         this.setState(State.ERROR);
         this.setStatus(ex.getMessage());
      }

      if (!this.isError())
      {
         System.setProperty("XL.HomeDir", xlhome);
         System.setProperty("java.security.auth.login.config", authconf);
         System.setProperty("java.security.policy", policy);
         System.setProperty("java.naming.provider.url", provider);

         //You can set the properties in the java options of the project run configuration. In that case use the options line bellow:
         //-DXL.ExtendedErrorOptions=TRUE -DXL.HomeDir=C:\oracle\oim\9031\JBoss\xlclient -Djava.security.auth.login.config=config\auth.conf -Djava.naming.provider.url=jnp://ten.mydomain.com:1099

         //this is the home dir of OIM design console
//      System.setProperty("XL.HomeDir", XLHomeDir);
//      System.setProperty("java.security.auth.login.config", authConf);
//      System.setProperty("java.security.policy", policy);
//      System.setProperty("java.naming.provider.url", namingProvider);

         /*
          * Initialize the xlClient: get the configuration and the settings
          */

         _config = ConfigurationClient.getComplexSettingByPath("Discovery.CoreServer");

         if (_config == null)
         {
            this.setError(true);
            this.setState(State.ERROR);
            this.setStatus(METHOD_NAME + "ConfigurationClient is null.");
         }
         else
         {
            _settings = _config.getAllSettings();

            if (_settings == null)
            {
               this.setError(true);
               this.setState(State.ERROR);
               this.setStatus(METHOD_NAME + "Settings are null.");
            }
         }
      }
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
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
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
   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doCreate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doCreate(): ";
      String requestId = null;
      String msg = null;
      StringBuilder err = new StringBuilder();
      Map<String, String> userQnA = null;
      Map<String, String> mapAttrs = null;

      mapAttrs = new HashMap<String, String>();

      try
      {
         this.preCreate(request, mapAttrs);
      }
      catch (OperationException ex)
      {
         /*
          * pre-create failed, update response and (re-) throw Exception
          */
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      /*
       * execute the implementation logic for a CREATE
       */

      userQnA = new HashMap<String, String>();

      try
      {
         requestId = tcUtilityFactory.createRegistrationRequest(_settings, mapAttrs, userQnA);
      }
      catch (tcDuplicateSelfRegistrationException ex)
      {
         msg = "Registration User ID '"
            + (request.getUniqueId() != null ? request.getUniqueId().toString() : "(null)")
            + "' already exists.";
      }
      catch (tcRequiredDataMissingException ex)
      {
         msg = "Missing Required Data";
      }
      catch (tcChallengeInfoException ex)
      {
         msg = "Challenge Information Exception";
      }
      catch (tcOrganizationNotFoundException ex)
      {
         msg = "Oragnization Not Found";
      }
      catch (tcInvalidManagerException ex)
      {
         msg = "Invalid Manager";
      }
      catch (tcDuplicateUserException ex)
      {
         msg = "User ID '"
            + (request.getUniqueId() != null ? request.getUniqueId().toString() : "(null)")
            + "' already exists.";
      }
      catch (tcAPIException ex)
      {
         err.append(METHOD_NAME).append("tc API Exception");

         response.setState(State.ERROR);
         response.setStatus(err.toString());

         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (msg != null)
      {
         response.setState(State.ERROR);
         response.setStatus(msg);
      }
      else
      {
         response.setUniqueId(requestId);
         response.setState(State.SUCCESS);
         response.setStatus("User Registered, request='" + requestId + "'");
      }

      /*
       * Post process the response
       */


      this.postCreate();

      return;
   }

   /**
    * @param request
    * @param createRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void preCreate(RequestIF request, Map<String, String> mapAttrs) throws OperationException
   //----------------------------------------------------------------
   {
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":preCreate(): ";
      String err = null;
      String keyName = null;
      String keyValue = null;
      String mapKey = null;
      String serviceName = null;
      String value = null;
      String passwordAttr = null;
      String[] srvcNames = null;
      Map<String, AttrIF> attrMap = null;
      Iterator<String> iter = null;
      AttrIF attr = null;
      ComponentIF subject = null;

      keyName = request.getKey();

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

      keyValue = uid.toString();

      if (keyValue == null || keyValue.length() < 1)
      {
         err = "uniqueId is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      mapAttrs.put(keyName, keyValue);
      request.setUniqueId(keyValue);


      /*
       * Get each PTK Attribute that the Subject has
       * create a new Map entry from the PTK Attribute
       */

//      mapAttrs.put("Users.First Name", "John");
//      mapAttrs.put("Users.Last Name", "Smith");
//      mapAttrs.put("Users.User ID", "jsmith18");
//      mapAttrs.put("Users.Email", "user@company.com");
//      mapAttrs.put("Organizations.Organization Name", "Xellerate Users");
//      mapAttrs.put("Users.Password", "password");
//      mapAttrs.put("ConfirmPassword", "password");

      attrMap = request.getSubject().getAttributes();

      if (attrMap == null || attrMap.isEmpty())
      {
         err = "Subject has no attributes";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      iter = attrMap.keySet().iterator();
      while (iter.hasNext())
      {
         mapKey = iter.next();
         attr = attrMap.get(mapKey);
         if (attr == null)
         {
            err = "Attribute '" + mapKey + "' is null";
            request.setError(true);
            request.setState(State.FAILED);
            request.setStatus(err);
            this.handleError(METHOD_NAME + err);
         }
         serviceName = attr.getServiceName();
         value = attr.getValueAsString();
         mapAttrs.put(serviceName, value);
      }

      /*
       * set the "confirmpassword" attribute
       */

      passwordAttr = request.getSubject().getProperty(DefinitionIF.PROP_PASSWORD_ATTR_NAME);
      attr = request.getSubject().getAttribute(passwordAttr);
      value = attr.getValueAsString();
      mapAttrs.put(SERVICE_ATTR_CONFIRMPASSWORD, value);

      return;
   }

   /**
    * @param response
    * @param createResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postCreate() throws OperationException
   //----------------------------------------------------------------
   {
      return;
   }
}
