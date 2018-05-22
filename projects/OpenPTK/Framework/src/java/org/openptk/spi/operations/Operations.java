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
 * Portions Copyright 2011-2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.spi.operations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.openptk.context.ContextIF;
import org.openptk.crypto.Encryptor;
import org.openptk.debug.DebugIF;
import org.openptk.definition.DefinitionIF;
import org.openptk.exception.CryptoException;
import org.openptk.exception.OperationException;
import org.openptk.logging.Logger;
import org.openptk.spi.ServiceIF;
import org.openptk.util.Digest;
import org.openptk.util.RandomData;
import org.openptk.util.StringUtil;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Operations extends Component implements OperationsIF
//===================================================================
{
   private int _default_reset_length = 8;
   private final String CLASS_NAME = this.getClass().getName();
   private Map<Operation, Boolean> _implemented = null;
   private Map<Operation, Boolean> _enabled = null;
   private OperationsType _type = OperationsType.LOOPBACK; // default
   private ServiceIF _service = null;
   protected boolean _emptyRemove = false;
   protected static final int TRANS_FW_SRVC = 0;
   protected static final int TRANS_SRVC_FW = 1;
   protected static String RESPONSE_DESC = "Operations Response";

   //----------------------------------------------------------------
   public Operations()
   //----------------------------------------------------------------
   {
      super();

      this.setCategory(Category.OPERATION);

      Operation[] operArray = null;

      _implemented = new HashMap<Operation, Boolean>();
      _enabled = new HashMap<Operation, Boolean>();

      operArray = Operation.values();

      for (int i = 0; i < operArray.length; i++)
      {
         _implemented.put(operArray[i], false);
         _enabled.put(operArray[i], false);
      }

      return;
   }

   /**
    * @param req
    * @param res
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   public abstract void execute(final RequestIF req, final ResponseIF res) throws OperationException;
   //----------------------------------------------------------------

   /**
    * @param req
    * @param res
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   public void preExecute(final RequestIF req, final ResponseIF res) throws OperationException
   //----------------------------------------------------------------
   {
      return;
   }
   
   /**
    * @param req
    * @param res
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   public void postExecute(final RequestIF req, final ResponseIF res) throws OperationException
   //----------------------------------------------------------------
   {
      return;
   }
   
   //----------------------------------------------------------------
   @Override
   public void startup()
   //----------------------------------------------------------------
   {
      this.setError(false);
      this.setState(State.READY);
      this.setStatus("startup method called");

      /*
       * Set global flags
       */

      String propValue = null;

      /*
       * Get the property that determines if an attribute should be
       * removed if its value is empty
       */

      propValue = this.getProperty(OperationsIF.PROP_ATTRIBUTE_EMPTY_REMOVE);
      if (propValue != null && propValue.length() > 0)
      {
         _emptyRemove = Boolean.parseBoolean(propValue);
      }

      return;
   }

   //----------------------------------------------------------------
   @Override
   public void shutdown()
   //----------------------------------------------------------------
   {
      this.setState(State.STOPPED);
      this.setStatus("shutdown method called");

      return;
   }

   /**
    * @param operation
    * @return boolean
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isImplemented(final Operation operation)
   //----------------------------------------------------------------
   {
      boolean bRet = false;
      bRet = _implemented.get(operation).booleanValue();
      return bRet;
   }

   /**
    * @param operation
    * @param enabled
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setEnabled(final Operation operation, final boolean enabled)
   //----------------------------------------------------------------
   {
      _enabled.put(operation, Boolean.valueOf(enabled));
      return;
   }

   /**
    * @param operation
    * @return boolean
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isEnabled(final Operation operation)
   //----------------------------------------------------------------
   {
      boolean bRet = false;
      bRet = _enabled.get(operation).booleanValue();
      return bRet;
   }

   /**
    * @param type
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setType(final OperationsType type)
   //----------------------------------------------------------------
   {
      _type = type;
      return;
   }

   /**
    * @return OperationsType
    */
   //----------------------------------------------------------------
   @Override
   public final OperationsType getType()
   //----------------------------------------------------------------
   {
      return _type;
   }

   /**
    * @return String
    */
   //----------------------------------------------------------------
   @Override
   public final String getTypeAsString()
   //----------------------------------------------------------------
   {
      return (_type.toString());
   }

   /**
    * @return ServiceIF
    */
   //----------------------------------------------------------------
   @Override
   public final ServiceIF getService()
   //----------------------------------------------------------------
   {
      return _service;
   }

   /**
    * @param service
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setService(final ServiceIF service)
   //----------------------------------------------------------------
   {
      _service = service;
      return;
   }

   //  =============================
   //  ===== PROTECTED METHODS =====
   //  =============================
   //
   /**
    * @param operation
    * @param implemented
    */
   //----------------------------------------------------------------
   protected void setImplemented(final Operation operation, final boolean implemented)
   //----------------------------------------------------------------
   {
      _implemented.put(operation, Boolean.valueOf(implemented));
      return;
   }

   /**
    * @param list
    * @return String[]
    */
   //----------------------------------------------------------------
   protected String[] getArrayFromList(final List<String> list)
   //----------------------------------------------------------------
   {
      int i = 0;
      String[] array = null;
      Iterator<String> iter = null;

      if (list != null)
      {
         array = new String[list.size()];
         iter = list.iterator();
         while (iter.hasNext())
         {
            array[i++] = iter.next();
         }
      }

      return array;
   }

   /**
    * @param query
    */
   //----------------------------------------------------------------
   protected void updateQueryServiceName(final Query query)
   //----------------------------------------------------------------
   {
      /*
       * If the query's serviceName is null is it equal to the
       * name (frameworkName).  Check for sub-queries and process
       * those recursively
       */
      String fwName = null;
      String svcName = null;
      List<Query> list = null;
      Iterator<Query> iter = null;
      Query subq = null;

      if (query != null)
      {
         fwName = query.getName();
         if (fwName != null && fwName.length() > 0)
         {
            svcName = query.getServiceName();
            if (svcName == null || svcName.length() < 1)
            {
               query.setServiceName(fwName);
            }
         }

         list = query.getQueryList();
         if (list != null && list.size() > 0)
         {
            iter = list.iterator();
            while (iter.hasNext())
            {
               subq = iter.next();
               this.updateQueryServiceName(subq);
            }
         }
      }

      return;
   }

   /**
    * @param request
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void checkPasswordAttribute(final RequestIF request) throws OperationException
   //----------------------------------------------------------------
   {
      /*
       * Make sure the "password" attribute exists in the Request and that
       * it has a value.
       * Also, remove ALL other attributes from the Request, should any exist.
       */

      boolean bFoundPwd = false;
      String METHOD_NAME = CLASS_NAME + ":checkPassword(): ";
      String propName = null;
      String attrName = null;
      String pwdValue = null;
      Map<String, AttrIF> mapAttrs = null;
      Iterator<String> iterAttrs = null;
      ComponentIF subject = null; // OpenPTK
      AttrIF attr = null; //         OpenPTK

      if (request == null)
      {
         this.handleError(METHOD_NAME + "Request is null");
      }

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      propName = subject.getProperty(DefinitionIF.PROP_PASSWORD_ATTR_NAME);
      if (propName == null || propName.length() < 1)
      {
         this.handleError(METHOD_NAME
            + "The Property for the Subject Password attribute name was not found: "
            + DefinitionIF.PROP_PASSWORD_ATTR_NAME);
      }

      mapAttrs = subject.getAttributes();
      if (mapAttrs != null && mapAttrs.size() > 0)
      {
         iterAttrs = mapAttrs.keySet().iterator();
         while (iterAttrs.hasNext())
         {
            attrName = iterAttrs.next();
            if (attrName.equalsIgnoreCase(propName))
            {
               attr = mapAttrs.get(attrName);
               pwdValue = (String) attr.getValue();
               if (pwdValue != null && pwdValue.length() > 0)
               {
                  bFoundPwd = true;
               }
               else
               {
                  this.handleError(METHOD_NAME + "The Password attribute is null/empty.");
               }
            }
            else
            {
               mapAttrs.remove(attrName);
            }
         }
      }


      if (!bFoundPwd)
      {
         this.handleError(METHOD_NAME + "The Password Attribute was not found in the Request");
      }
      return;
   }

   /**
    * @param request
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void getPasswordAttribute(final RequestIF request) throws OperationException
   //----------------------------------------------------------------
   {
      /*
       * Create a new Attribute that represents the "password" and add to the request
       * Get the Subject attribute associated with its password.
       * Get the length of the new password, then generate a random password
       * Create a new AttrIF attribute using the "name" and "value"
       * Add it to the Request's Subject.
       */

      int length = 0;
      String METHOD_NAME = CLASS_NAME + ":getPasswordAttribute(): ";
      String resetPwd = null;
      String passwordAttr = null;
      ComponentIF subject = null; // OpenPTK
      AttrIF attr = null; //         OpenPTK

      passwordAttr = request.getSubject().getProperty(DefinitionIF.PROP_PASSWORD_ATTR_NAME);
      if (passwordAttr == null || passwordAttr.length() < 1)
      {
         throw new OperationException(METHOD_NAME
            + "The Property for the Subject Password attribute name was not found: "
            + DefinitionIF.PROP_PASSWORD_ATTR_NAME);
      }

      try
      {
         length = Integer.parseInt(this.getProperty(OperationsIF.PROP_RESET_PASSWORD_LENGTH));
      }
      catch (NumberFormatException ex)
      {
         length = _default_reset_length;
      }

      resetPwd = RandomData.getString(length - 1); // going to prepend "O"

      if (resetPwd != null && resetPwd.length() > 0)
      {
         subject = request.getSubject();

         if (subject != null)
         {
            attr = new BasicAttr(passwordAttr, "O" + resetPwd);
            attr.setServiceName(request.getService().getSrvcName(request.getOperation(), passwordAttr));
            subject.setAttribute(passwordAttr, attr);
         }
      }
      else
      {
         throw new OperationException(METHOD_NAME
            + "The generated password is null");
      }

      return;
   }

   /**
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void getPasswordResult(final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getPasswordResult(): ";
      String passwordAttr = null;
      Component resource = null;
      AttrIF attr = null;

      passwordAttr = response.getRequest().getSubject().getProperty(DefinitionIF.PROP_PASSWORD_ATTR_NAME);
      if (passwordAttr == null && passwordAttr.length() < 1)
      {
         throw new OperationException(METHOD_NAME
            + "The Property for the Password attribute name was not found: "
            + DefinitionIF.PROP_PASSWORD_ATTR_NAME);
      }

      response.setDescription(Operations.RESPONSE_DESC + ": Password Reset");

      if (!response.isError())
      {
         response.setStatus("password reset ");
         response.setState(State.SUCCESS);

         // add a Result (Resource) for the password that was reset

         attr = response.getRequest().getSubject().getAttribute(passwordAttr);

         if (attr != null)
         {
            resource = new Component();
            resource.setUniqueId(response);
            resource.setDescription("Result: Password Reset");
            resource.setDebugLevel(response.getDebugLevel());
            resource.setDebug(response.isDebug());
            resource.setCategory(Category.RESOURCE);
            resource.setAttribute(passwordAttr, attr);
            response.addResult(resource);
         }
      }
      return;
   }

   /**
    * @param key
    * @return String
    */
   //----------------------------------------------------------------
   protected String getValue(final String key)
   //----------------------------------------------------------------
   {
      /*
       * Get a "value" from the Properties, using the key.
       * If it is encrypted, decrypt it before returning it.
       */

      String METHOD_NAME = CLASS_NAME + ":getValue(): ";
      String value = null;
      String encrypted = null;

      if (key != null && key.length() > 0)
      {
         encrypted = this.getProperty(key + ".encrypted");
         if (encrypted != null && encrypted.length() > 0)
         {
            try
            {
               value = this.decrypt(encrypted);
            }
            catch (OperationException ex)
            {
               Logger.logWarning(METHOD_NAME + ex.getMessage());
               value = null;
            }
         }
         else
         {
            value = this.getProperty(key);
         }
      }
      return value;
   }

   /**
    * @param msg
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void handleError(final String msg) throws OperationException
   //----------------------------------------------------------------
   {
      String str = null;

      this.setError(true);
      if (msg == null || msg.length() < 1)
      {
         str = "(null message)";
      }
      else
      {
         str = msg;
      }

      throw new OperationException(str);
   }

   /**
    * @param request
    * @return String[]
    */
   //----------------------------------------------------------------------
   protected String[] getServiceNames(final RequestIF request)
   //----------------------------------------------------------------------
   {
      String[] attrNames = null;
      String name = null;
      List<String> listNames = new LinkedList<String>();
      Map<String, AttrIF> mapAttrs = null;
      Iterator<String> iterNames = null;
      AttrIF attr = null;

      if (request != null)
      {
         mapAttrs = request.getSubject().getAttributes();
         if (mapAttrs != null && !mapAttrs.isEmpty())
         {
            iterNames = mapAttrs.keySet().iterator();
            while (iterNames.hasNext())
            {
               name = iterNames.next();
               attr = mapAttrs.get(name);
               listNames.add(attr.getServiceName());
            }
            attrNames = listNames.toArray(new String[listNames.size()]);
         }
      }

      if (attrNames == null)
      {
         attrNames = new String[0];
      }

      return attrNames;
   }

   /**
    * @param request
    * @param propName
    * @return String
    */
   //----------------------------------------------------------------
   protected String findProperty(final RequestIF request, final String propName)
   //----------------------------------------------------------------
   {
      String propValue = null;

      /*
       * Check the Request first, then "this" object's properties
       */

      if (request != null)
      {
         propValue = request.getProperty(propName);
      }

      if (propValue == null)
      {
         propValue = this.getProperty(propName);
      }

      return propValue;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doPasswordForgot(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      boolean match = false;
      String METHOD_NAME = CLASS_NAME + ":doPasswordForgot(): ";
      String msg = null;
      AttrIF attrForgotValues = null;
      AttrIF attrForgotAnswers = null;

      /*
       * The "values" attribute contains the existing responses to all of the
       * forgotten password questions that the user provided when they were
       * initially provisioned to the user repository.
       */

      attrForgotValues = request.getSubject().getAttribute(DefinitionIF.ATTR_PWD_FORGOT_VALUES);
      if (attrForgotValues == null)
      {
         msg = "Attribute '" + DefinitionIF.ATTR_PWD_FORGOT_VALUES + "' is null";
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

      if (msg != null)
      {
         response.setError(true);
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      if (this.isDebug() && this.getDebugLevelAsInt() >= DebugIF.FINE)
      {
         Logger.logInfo(METHOD_NAME
            + "answers='" + attrForgotAnswers.getValueAsString() + "'"
            + ", values='" + attrForgotValues.getValueAsString() + "'");
      }


      match = this.compareValuesToAnswers(attrForgotValues, attrForgotAnswers);

      if (match == true)
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
    *
    * @param attr
    * @return String[]
    */
   //----------------------------------------------------------------
   protected String[] getAttrValueAsStringArray(final AttrIF attr)
   //----------------------------------------------------------------
   {
      Object obj = null;
      String[] array = null;

      if (attr.getType() == DataType.STRING)
      {
         obj = attr.getValue();
         if (obj != null)
         {
            if (attr.isMultivalued())
            {
               array = (String[]) obj;
            }
            else
            {
               array = new String[1];
               array[0] = (String) obj;
            }
         }
      }

      if (array == null)
      {
         array = new String[0];
      }

      return array;
   }

   //----------------------------------------------------------------
   protected String getCheckProp(String propName) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getCheckProp(): ";
      String propValue = null;
      String msg = null;

      if (propName != null && propName.length() > 0)
      {
         propValue = this.getValue(propName);

         if (propValue == null || propValue.length() < 1)
         {
            msg = "Property '" + propName + "' is null/empty";
         }
      }
      else
      {
         msg = "Property name is null/empty";
      }

      if (msg != null)
      {
         this.setError(true);
         this.setStatus(msg);
         this.setState(State.ERROR);
         this.handleError(METHOD_NAME + msg);
      }

      return propValue;
   }

   //----------------------------------------------------------------
   protected final Object getUniqueIdValueFromRequest(final RequestIF request) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getUniqueIdValueFromRequest(): ";
      Object value = null;
      String err = null;
      ComponentIF subject = null;

      subject = request.getSubject();

      if (subject == null)
      {
         err = "Subject is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      value = subject.getUniqueId();

      if (value == null)
      {
         err = "uniqueId value is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      return value;
   }

   //----------------------------------------------------------------------
   protected void checkException(final Exception ex, StringBuilder buf)
   //----------------------------------------------------------------------
   {
      Throwable cause = null;

      if (buf == null)
      {
         buf = new StringBuilder();
      }
      else
      {
         if (buf.length() > 0)
         {
            buf.append(", ");
         }
      }

      /*
       * Determine if the "state" should be set to FAILED or ERROR
       * Check the Exception first, then check the Cause
       * FAILED States are potentially recoverable
       */

      this.setState(State.ERROR);

      if (ex instanceof java.net.ConnectException)
      {
         this.setState(State.FAILED);
      }
      else if (ex instanceof java.io.IOException)
      {
         this.setState(State.FAILED);
      }
      else if (ex instanceof javax.security.auth.login.LoginException)
      {
         this.setState(State.FAILED);
      }
      else
      {
         cause = ex.getCause();
         if (cause != null)
         {
            if (cause instanceof java.net.ConnectException)
            {
               this.setState(State.FAILED);
            }
            else if ( cause instanceof java.rmi.ConnectException)
            {
               this.setState(State.FAILED);
            }
            else if (cause instanceof java.io.IOException)
            {
               this.setState(State.FAILED);
            }
            else if (ex instanceof javax.security.auth.login.LoginException)
            {
               this.setState(State.FAILED);
            }
            else
            {
               buf.append("un-recognized root cause, setting state to ERROR: ");
            }
            buf.append(cause.getClass().getSimpleName()).append(": ");
            buf.append(cause.getMessage());
         }
         else
         {
            /*
             * Don't know what "caused" the exception
             * set "this" state to ERROR ... critical / unrecoverable
             */
            buf.append("null/empty root cause, exception: ");
            buf.append(ex.getClass().getSimpleName()).append(", ");
         }
      }

      this.setStatus(buf.toString());

      return;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private String decrypt(final String encrypted) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":decrypt(): ";
      String clear = null;
      ContextIF context = null;

      try
      {
         clear = Encryptor.decrypt(Encryptor.CONFIG, encrypted);
      }
      catch (CryptoException ex)
      {
         this.handleError(METHOD_NAME
            + "decrypt error: " + ex.getMessage());
      }

      return clear;
   }

   //----------------------------------------------------------------
   private boolean compareValuesToAnswers(final AttrIF attrValues, final AttrIF attrAnswers)
   //----------------------------------------------------------------
   {
      boolean hashedValues = false;
      boolean areEqual = false;
      int qty = 0;
      int match = 0;
      String value = null;
      String answer = null;
      String[] values = null;
      String[] answers = null;

      values = this.getAttrValueAsStringArray(attrValues);
      answers = this.getAttrValueAsStringArray(attrAnswers);

      /*
       * Check to see if the answers are "encrypted"
       */

      hashedValues = attrValues.isEncrypted();

      /*
       * Check for the same number of inputs and stored answers
       */

      if (values.length >= 1 && answers.length >= 1 && answers.length == values.length)
      {
         /*
          * Make sure all the inputs and stored answers match
          */

         qty = values.length;
         for (int i = 0; i < qty; i++)
         {
            value = values[i];
            answer = answers[i];
            if (value != null && value.length() > 0
               && answer != null && answer.length() > 0)
            {
               /*
                * If the (stored) values are "encrypted", then "encrypt" the answers
                * note: actual encryption is not used.  The "encrypted" flag indicates
                * that the strings are digest (SHA) hashed strings from the actual values.
                * We will create digests from the input answers and then compare the two digests
                */

               if (hashedValues)
               {
                  if (Digest.validate(value, StringUtil.clean(StringUtil.ALPHA_NUM, answer).toLowerCase()))
                  {
                     match++;
                  }
               }
               else
               {
                  if (value.equalsIgnoreCase(answer))
                  {
                     match++;
                  }
               }
            }
         }

         /*
          * Does the "match" value equal "qty" value
          */

         if (match == qty)
         {
            areEqual = true;
         }
      }

      return areEqual;
   }
}
