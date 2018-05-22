/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2009 Sun Microsystems, Inc.
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
package org.openptk.app.prov.portlets;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.openptk.api.AttributeIF;
import org.openptk.api.Element;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.State;
import org.openptk.api.Opcode;
import org.openptk.connection.ConnectionIF;
import org.openptk.connection.Setup;
import org.openptk.connection.SetupIF;

//===================================================================
public abstract class ProvisioningPortlet extends GenericPortlet
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private final String DESCRIPTION = "ProvisionPortlet [Abstract]";
   private final String RESOURCE_BUNDLE_NAME = "openptk_client";
   protected final String NOT_SET = "(not set)";
   protected final String ATTR_NAME_MESSAGE = "message";
   protected final String ATTR_NAME_STATUS = "status";
   protected static final String PORTALUSER_UNIQUEID = "portaluser.uniqueid";
   protected String ELEMENT_ATTR_NULL = NOT_SET;
   protected String OPENPTKID_CLASSNAME = null;
   protected String OUTPUT_ELEMENT_NULL = NOT_SET;
   protected String OUTPUT_RESULT_NOTSINGLE = NOT_SET;
   protected String PORTLET_ATTR_FIRSTNAME = NOT_SET;
   protected String PORTLET_ATTR_LASTNAME = NOT_SET;
   protected String PORTLET_ATTR_MESSAGE = NOT_SET;
   protected String PORTLET_ATTR_PASSWORD = NOT_SET;
   protected String PORTLET_ATTR_PHONEWORK = NOT_SET;
   protected String PORTLET_ATTR_STATUS = NOT_SET;
   protected String PORTLET_ATTR_TITLE = NOT_SET;
   protected String PORTLET_ATTR_UNIQUEID = NOT_SET;
   protected String PORTLET_INPUT_CANCEL = NOT_SET;
   protected String PORTLET_INPUT_CHANGEINFO = NOT_SET;
   protected String PORTLET_INPUT_CHANGEPWD = NOT_SET;
   protected String PORTLET_INPUT_CONFIRMPWD = NOT_SET;
   protected String PORTLET_INPUT_DELETE = NOT_SET;
   protected String PORTLET_INPUT_FINDVAL = NOT_SET;
   protected String PORTLET_INPUT_NEWPWD = NOT_SET;
   protected String PORTLET_INPUT_NONE = NOT_SET;
   protected String PORTLET_INPUT_RESETPWD = NOT_SET;
   protected String PORTLET_INPUT_USERCREATE = NOT_SET;
   protected String PORTLET_INPUT_USERFIND = NOT_SET;
   protected String PORTLET_MSG_OPCODE_INVALID = NOT_SET;
   protected String PORTLET_MSG_OPCODE_NULL = NOT_SET;
   protected String PORTLET_MSG_OUTPUT_ERROR = NOT_SET;
   protected String PORTLET_MSG_OUTPUT_NULL = NOT_SET;
   protected String PORTLET_MSG_PWD_MISMATCH = NOT_SET;
   protected String PORTLET_MSG_PWD_NULL = NOT_SET;
   protected String PORTLET_MSG_RESULT_NULL = NOT_SET;
   protected String PORTLET_MSG_SESSION_NULL = NOT_SET;
   protected String PORTLET_MSG_USER_NULL = NOT_SET;
   protected String PORTLET_MSG_UNIQUEID_NULL = NOT_SET;
   protected String PORTLET_OPCODE_ADDUSER = NOT_SET;
   protected String PORTLET_OPCODE_ANSWERS = NOT_SET;
   protected String PORTLET_OPCODE_CHANGEINFO = NOT_SET;
   protected String PORTLET_OPCODE_CHANGEPWD = NOT_SET;
   protected String PORTLET_OPCODE_FORGOTPWD = NOT_SET;
   protected String PORTLET_OPCODE_LISTUSERS = NOT_SET;
   protected String PORTLET_OPCODE_MENU = NOT_SET;
   protected String PORTLET_OPCODE_REGISTRATION = NOT_SET;
   protected String PORTLET_OPCODE_REGISTRATIONRESULTS = NOT_SET;
   protected String PORTLET_OPCODE_SEARCHUSER = NOT_SET;
   protected String PORTLET_OPCODE_START = NOT_SET;
   protected String PORTLET_OPCODE_USERDETAIL = NOT_SET;
   protected String PORTLET_OPCODE_USERUPDATE = NOT_SET;
   protected String PORTLET_PARAM_OPCODE = NOT_SET;
   protected String PORTLET_PROMPT_READY = NOT_SET;
   protected String PORTLET_STATUS_ERROR = NOT_SET;
   protected String PORTLET_STATUS_EMPTY = NOT_SET;
   protected String PORTLET_TITLE = NOT_SET;
   protected Properties _propsPortlet = null;
   protected Properties _propsUser = null;
   protected PortalUserIF _portalUser = null;
   protected Map<String, PortletState> _mapPortletData = null;
   protected PortletContext _portletCtx = null;
   protected Logger _logger = null;
   private SetupIF _setup = null;
   private ConnectionIF _connection = null;
   private boolean _debug = false;
   private boolean _audit = false;

   //----------------------------------------------------------------
   public ProvisioningPortlet()
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void init(PortletConfig portletconfig) throws PortletException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":init(): ";

      super.init(portletconfig);

      _portletCtx = portletconfig.getPortletContext();

      _logger = Logger.getLogger(ProvisioningPortlet.class.getSimpleName());

      /*
       * setup the PTK infrastructure
       */

      try
      {
         _setup = new Setup(RESOURCE_BUNDLE_NAME);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      /*
       * run the "sub-class's" local initialization
       * this set's all the Properties
       */

      this.initLocal();

      /*
       * if defined, instanciate the openptkid.classname
       */

      if (OPENPTKID_CLASSNAME != null && OPENPTKID_CLASSNAME.length() > 0)
      {
         try
         {
            _portalUser = (PortalUserIF) Class.forName(OPENPTKID_CLASSNAME).newInstance();
         }
         catch (InstantiationException ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }
         catch (IllegalAccessException ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }
         catch (ClassNotFoundException ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }
      }

      /*
       * set the initial values
       * The HashMap will Store each Portlet's State, using the Portal's Id
       */

      _mapPortletData = new HashMap<String, PortletState>();

      return;
   }

   //----------------------------------------------------------------
   @Override
   public String getTitle(RenderRequest request)
   //----------------------------------------------------------------
   {
      String title = null;
      title = PORTLET_TITLE;
      return title;
   }

   //----------------------------------------------------------------
   @Override
   public void processAction(ActionRequest request, ActionResponse response)
      throws PortletException, IOException
   //----------------------------------------------------------------
   {
      /*
       * needs to be overloaded by sub-class
       */
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException
   //----------------------------------------------------------------
   {
      /*
       * needs to be overloaded by sub-class
       */
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void doEdit(RenderRequest request, RenderResponse response)
      throws PortletException, IOException
   //----------------------------------------------------------------
   {
      /*
       * may be overloaded by sub-class
       */
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void doHelp(RenderRequest request, RenderResponse response)
      throws PortletException, IOException
   //----------------------------------------------------------------
   {
      /*
       * may be overloaded by sub-class
       */
      return;
   }

   //----------------------------------------------------------------
   public void setDebug(boolean debug)
   //----------------------------------------------------------------
   {
      _debug = debug;
      return;
   }

   //----------------------------------------------------------------
   public boolean isDebug()
   //----------------------------------------------------------------
   {
      return _debug;
   }

   //----------------------------------------------------------------
   public void setAudit(boolean audit)
   //----------------------------------------------------------------
   {
      _audit = audit;
      return;
   }

   //----------------------------------------------------------------
   public boolean isAudit()
   //----------------------------------------------------------------
   {
      return _audit;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   //----------------------------------------------------------------
   protected void initProperties(String propName) throws PortletException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":initProperties(): ";
      String key = null;
      String val = null;
      Enumeration<String> keys = null;
      ResourceBundle res = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      _propsPortlet = new Properties();
      _propsUser = new Properties();

      try
      {
         res = ResourceBundle.getBundle(propName);
      }
      catch (NullPointerException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }
      catch (MissingResourceException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      keys = res.getKeys();

      while (keys.hasMoreElements())
      {
         key = keys.nextElement();
         val = res.getString(key);
         _propsPortlet.setProperty(key, val);
      }
      this.setBaseVariables();

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   //----------------------------------------------------------------
   protected void setPortletVariables()
   //----------------------------------------------------------------
   {
      /*
       * Application specific Portlets (that extend this class)
       * should implement this method to initialize application
       * specific variables.
       */

      return;
   }

   //----------------------------------------------------------------
   protected void initLocal() throws PortletException
   //----------------------------------------------------------------
   {
      /*
       * this is intended to be used by sub-classes that want something
       * to be initialized once via the Portlet init() mechanism
       * sub-classes can over-load this method
       */

      return;
   }

   //----------------------------------------------------------------
   protected void logError(String msg)
   //----------------------------------------------------------------
   {
      _logger.log(Level.SEVERE, msg);
      return;
   }

   //----------------------------------------------------------------
   protected void logInfo(String msg)
   //----------------------------------------------------------------
   {
      _logger.log(Level.INFO, msg);
      return;
   }

   //----------------------------------------------------------------
   protected void logDebug(String msg)
   //----------------------------------------------------------------
   {
      _logger.log(Level.FINEST, msg);
      return;
   }

   //----------------------------------------------------------------
   protected void setStateError(String msg, PortletState state)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":setStateError(): ";

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + msg);
      }

      state.setError(true);
      state.putAttribute(ATTR_NAME_MESSAGE, msg);
      state.putAttribute(ATTR_NAME_STATUS, PORTLET_STATUS_ERROR);

      return;
   }

   //----------------------------------------------------------------
   protected void dispatchJspPage(RenderRequest request,
      RenderResponse response,
      PortletState state)
      throws PortletException, IOException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":dispatchJspPage(): ";
      String page = null;
      String retStatus = null;
      String retMessage = null;

      retMessage = state.getAttribute(ATTR_NAME_MESSAGE);
      retStatus = state.getAttribute(ATTR_NAME_STATUS);

      request.setAttribute(PORTLET_ATTR_MESSAGE, retMessage);
      request.setAttribute(PORTLET_ATTR_STATUS, retStatus);

      if (state.getMode() == PortletMode.VIEW)
      {
         page = state.getPageView();
      }
      else if (state.getMode() == PortletMode.HELP)
      {
         page = state.getPageHelp();
      }
      else if (state.getMode() == PortletMode.EDIT)
      {
         page = state.getPageEdit();
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + ": " +
            PORTLET_ATTR_STATUS + "='" + retStatus + "', " +
            PORTLET_ATTR_MESSAGE + "='" + retMessage + "', " +
            "mode='" + state.getModeAsString() + "', " +
            "page='" + page + "'");
      }

      response.setContentType(request.getResponseContentType());

      PortletRequestDispatcher dispatcher =
         this.getPortletContext().getRequestDispatcher(page);

      dispatcher.include(request, response);

      return;
   }

   //----------------------------------------------------------------
   protected Output execCreate(Input input)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execCreate(): ";
      String strErr = null;
      Output output = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + ", " + input.toString());
      }

      try
      {
         output = _connection.execute(Opcode.CREATE, input);
      }
      catch (Exception ex)
      {
         strErr = "Exception: " + ex.getMessage();
         output = new Output();
         output.setState(State.ERROR);
      }

      if (output != null)
      {
         if (output.isError())
         {
            strErr = PORTLET_MSG_OUTPUT_ERROR + ": " + output.getStatus();
         }
      }
      else
      {
         strErr = PORTLET_MSG_OUTPUT_NULL;
//         output.setState(State.NULL);
      }

      if (strErr != null)
      {
         output.setStatus(strErr);
         output.setError(true);
         this.logError(strErr);
      }

      return output;
   }

   //----------------------------------------------------------------
   protected ElementIF execRead(Input input)
   //----------------------------------------------------------------
   {
      int resultSize = 0;
      String METHOD_NAME = CLASS_NAME + ":execRead(): ";
      String strErr = null;
      Output output = null;
      ElementIF elem = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + ", " + input.toString());
      }

      try
      {
         output = _connection.execute(Opcode.READ, input);
      }
      catch (Exception ex)
      {
         strErr = "Exception: " + ex.getMessage();
      }

      if (output != null)
      {
         if (!output.isError())
         {
            resultSize = output.getResultsSize();
            if (resultSize == 1)
            {
               elem = output.getResults().get(0);
               if (elem == null)
               {
                  strErr = OUTPUT_ELEMENT_NULL;
               }
            }
            else
            {
               strErr = OUTPUT_RESULT_NOTSINGLE + ": size=" + resultSize;
            }
         }
         else if (strErr == null)
         {
            strErr = PORTLET_MSG_OUTPUT_ERROR + ": " + output.getStatus();
         }
      }
      else if (strErr == null)
      {
         strErr = PORTLET_MSG_OUTPUT_NULL;
      }

      if (elem == null)
      {
         elem = new Element();
         elem.setState(State.NULL);
         elem.setStatus(strErr);
         elem.setError(true);
      }

      if (strErr != null)
      {
         this.logError(strErr);
      }

      return elem;
   }

   //----------------------------------------------------------------
   protected Output execUpdate(Input input)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execUpdate(): ";
      String strErr = null;
      Output output = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + ", " + input.toString());
      }

      try
      {
         output = _connection.execute(Opcode.UPDATE, input);
      }
      catch (Exception ex)
      {
         strErr = "Exception: " + ex.getMessage();
         output = new Output();
         output.setState(State.ERROR);
      }

      if (output != null)
      {
         if (output.isError())
         {
            strErr = PORTLET_MSG_OUTPUT_ERROR + ": " + output.getStatus();
            output.setState(State.ERROR);
         }
      }
      else
      {
         strErr = PORTLET_MSG_OUTPUT_NULL;
//         output.setState(State.NULL);
      }

      if (strErr != null)
      {
         output.setStatus(strErr);
         output.setError(true);
         this.logError(strErr);
      }

      return output;
   }

   //----------------------------------------------------------------
   protected ElementIF execDelete(Input input)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execDelete(): ";
      String strErr = null;
      Output output = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + ", " + input.toString());
      }

      try
      {
         output = _connection.execute(Opcode.DELETE, input);
      }
      catch (Exception ex)
      {
         strErr = "Exception: " + ex.getMessage();
         output = new Output();
         output.setState(State.ERROR);
      }

      if (output != null)
      {
         if (output.isError())
         {
            strErr = PORTLET_MSG_OUTPUT_ERROR + ": " + output.getStatus();
            output.setState(State.ERROR);
         }
      }
      else
      {
         strErr = PORTLET_MSG_OUTPUT_NULL;
//         output.setState(State.NULL);
      }

      if (strErr != null)
      {
         output.setStatus(strErr);
         output.setError(true);
         this.logError(strErr);
      }

      return output;
   }

   //----------------------------------------------------------------
   protected List<ElementIF> execSearch(Input input)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execSearch(): ";
      String strErr = null;
      Output output = null;
      List<ElementIF> elemList = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + ", " + input.toString());
      }

      try
      {
         output = _connection.execute(Opcode.SEARCH, input);
      }
      catch (Exception ex)
      {
         strErr = "Exception: " + ex.getMessage();
      }

      if (output != null)
      {
         if (!output.isError())
         {
            elemList = output.getResults();
         }
         else
         {
            strErr = PORTLET_MSG_OUTPUT_ERROR + ": " + output.getStatus();
         }
      }
      else
      {
         strErr = PORTLET_MSG_OUTPUT_NULL;
      }

      if (elemList == null)
      {
         elemList = new LinkedList<ElementIF>();
      }
      if (strErr != null)
      {
         this.logError(strErr);
      }

      return elemList;
   }

   //----------------------------------------------------------------
   protected Output execPasswordChange(Input input)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execPasswordChange(): ";
      String strErr = null;
      Output output = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + ", " + input.toString());
      }

      try
      {
         output = _connection.execute(Opcode.PWDCHANGE, input);
      }
      catch (Exception ex)
      {
         strErr = "Exception: " + ex.getMessage();
         output = new Output();
         output.setState(State.ERROR);
      }

      if (output != null)
      {
         if (output.isError())
         {
            strErr = PORTLET_MSG_OUTPUT_ERROR + ": " + output.getStatus();
            output.setState(State.ERROR);
         }
      }
      else
      {
         strErr = PORTLET_MSG_OUTPUT_NULL;
//         output.setState(State.NULL);
      }

      if (strErr != null)
      {
         output.setStatus(strErr);
         output.setError(true);
         this.logError(strErr);
      }

      return output;
   }

   //----------------------------------------------------------------
   protected List<ElementIF> execPasswordReset(Input input)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execPasswordReset(): ";
      String strErr = null;
      Output output = null;
      List<ElementIF> elemList = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + ", " + input.toString());
      }

      try
      {
         output = _connection.execute(Opcode.PWDRESET, input);
      }
      catch (Exception ex)
      {
         strErr = "Exception: " + ex.getMessage();
      }

      if (output != null)
      {
         if (!output.isError())
         {
            elemList = output.getResults();
         }
         else
         {
            strErr = PORTLET_MSG_OUTPUT_ERROR + ": " + output.getStatus();
         }
      }
      else
      {
         strErr = PORTLET_MSG_OUTPUT_NULL;
      }

      if (elemList == null)
      {
         elemList = new LinkedList<ElementIF>();
      }
      if (strErr != null)
      {
         this.logError(strErr);
      }

      return elemList;
   }

   //----------------------------------------------------------------
   protected Output execPasswordForgot(Input input)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execPasswordForgot(): ";
      String strErr = null;
      Output output = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + ", " + input.toString());
      }

      try
      {
         output = _connection.execute(Opcode.PWDFORGOT, input);
      }
      catch (Exception ex)
      {
         strErr = "Exception: " + ex.getMessage();
         output = new Output();
         output.setState(State.ERROR);
      }

      if (output != null)
      {
         if (output.isError())
         {
            strErr = PORTLET_MSG_OUTPUT_ERROR + ": " + output.getStatus();
            output.setState(State.ERROR);
         }
      }
      else
      {
         strErr = PORTLET_MSG_OUTPUT_NULL;
      }

      if (strErr != null)
      {
         output.setStatus(strErr);
         output.setError(true);
         this.logError(strErr);
      }

      return output;
   }

   //----------------------------------------------------------------
   @SuppressWarnings("rawtypes")
   protected void logAction(String callerId, ActionRequest request)
   //----------------------------------------------------------------
   {
      Enumeration paramEnum = null;
      String paramKey = null;
      String paramVal = null;
      StringBuilder strBuf = new StringBuilder();

      strBuf.append(callerId).append(": ");

      paramEnum = request.getParameterNames();
      strBuf.append("Parameters:[");

      while (paramEnum.hasMoreElements())
      {
         paramKey = paramVal = null;
         paramKey = (String) paramEnum.nextElement();
         if (paramKey != null)
         {
            paramVal = request.getParameter(paramKey);
            if (paramVal == null)
            {
               paramVal = "''";
            }
         }
         else
         {
            paramKey = paramVal = "''";
         }
         strBuf.append(paramKey).append("=").append(paramVal).append(",");
      }
      strBuf.append("]");

      this.logInfo(strBuf.toString());

      return;
   }

   //----------------------------------------------------------------
   protected void setPersonParameters(ActionRequest request, String uniqueId, List<String> listAttrs)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":setPersonParameters(): ";
      Object obj = null;
      String attrName = null;
      String attrValue = null;
      Input input = null;
      ElementIF elem = null;
      AttributeIF attr = null;
      PortletSession psession = null;

      /*
       * request: the portlet request, this will be updated with data
       * uniqueId: the identifier
       * listAttr: a List of Strings that contains attribute names
       *
       * Perform a "read" operation for the uniqueId and ask for the attributes
       * that are defined in the List.
       *
       * When the information is returned, "set" the uniqueid and
       * attributes in the provided Portlet request
       */

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "User='" + uniqueId +
            "' listAttrs[" + listAttrs.size() + "]=" + listAttrs.toString());
      }

      input = new Input();
      input.setUniqueId(uniqueId);

      for (int i = 0; i < listAttrs.size(); i++)
      {
         input.addAttribute(listAttrs.get(i));
      }

      elem = this.execRead(input);

      if (!elem.isError())
      {
         psession = request.getPortletSession();
         for (int i = 0; i < listAttrs.size(); i++)
         {
            attrName = listAttrs.get(i);
            attrValue = null;
            attr = elem.getAttribute(attrName);
            if (attr != null)
            {
               obj = attr.getValue();
               if (obj == null)
               {
                  attrValue = "";
               }
               else
               {
                  attrValue = attr.getValueAsString();
                  if (attrValue == null || attrValue.length() < 1)
                  {
                     attrValue = "";
                  }
                  else
                  {
                     if (attrValue.equalsIgnoreCase("null"))
                     {
                        attrValue = "";
                     }
                  }
               }
            }
            else
            {
               attrValue = "";
            }
            psession.setAttribute(attrName, attrValue, PortletSession.PORTLET_SCOPE);
         }
         psession.setAttribute(PORTLET_ATTR_UNIQUEID, uniqueId, PortletSession.PORTLET_SCOPE);
      }

      return;
   }

   //----------------------------------------------------------------
   protected PortletState preprocessSession(RenderRequest request,
      RenderResponse response)
   //----------------------------------------------------------------
   {
      /*
       *  Called from a doView(), usually from the beginning
       */
      String METHOD_NAME = CLASS_NAME + ":preprocessSession(Render): ";
      String user = null;
      PortletSession session = null;
      PortletState state = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      session = request.getPortletSession();
      user = request.getRemoteUser();
      state = this.getState(session, user);

      try
      {
         this.getConnection(user);
      }
      catch (PortletException ex)
      {
         this.logError(METHOD_NAME + ex.getMessage());
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT, state='" + state.toString() + "'");
      }

      return state;
   }

   //----------------------------------------------------------------
   protected void postprocessSession(RenderRequest request,
      RenderResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      /*
       *  Called from a doView(), usually from the end
       */

      String METHOD_NAME = CLASS_NAME + ":postprocessSession(Render): ";

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      if (state != null)
      {
         if (state.isModified())
         {
            state.setModified(false);
            _mapPortletData.put(state.getId(), state);
         }
      }
      else
      {
         this.logDebug(METHOD_NAME + "ERROR: PortletState is null");
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   //----------------------------------------------------------------
   protected PortletState preprocessSession(ActionRequest request,
      ActionResponse response)
   //----------------------------------------------------------------
   {
      /*
       * Called from a processAction method(), usually from the beginning
       */

      String METHOD_NAME = CLASS_NAME + ":preprocessSession(Action): ";
      String user = null;
      PortletSession session = null;
      PortletState state = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      session = request.getPortletSession();
      user = request.getRemoteUser();
      state = this.getState(session, user);

      try
      {
         this.getConnection(user);
      }
      catch (PortletException ex)
      {
         this.logError(METHOD_NAME + ex.getMessage());
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return state;
   }

   //----------------------------------------------------------------
   protected void postprocessSession(ActionRequest request,
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      /*
       * Called from a processAction method(), usually from the end
       */

      String METHOD_NAME = CLASS_NAME + ":postprocessSession(Action): ";

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      if (state != null)
      {
         if (state.isModified())
         {
            state.setModified(false);
            _mapPortletData.put(state.getId(), state);
         }
      }
      else
      {
         this.logDebug(METHOD_NAME + "ERROR: PortletState is null");
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   //----------------------------------------------------------------
   protected void handleError(String msg) throws PortletException
   //----------------------------------------------------------------
   {
      throw new PortletException(msg);
   }

   //----------------------------------------------------------------
   private void getConnection(String user) throws PortletException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getConnection(): ";

      if (_connection == null)
      {
         if (_setup != null)
         {
            try
            {
               _connection = _setup.getConnection(user);
            }
            catch (Exception ex)
            {
               this.handleError(METHOD_NAME + ex.getMessage());
            }
         }
         else
         {
            this.handleError(METHOD_NAME + "Setup is null.");
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private PortletState getState(PortletSession session, String uniqueId)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getState(): ";
      String portletId = null;
      String portalUserId = null;
      String ptkUserId = null;
      boolean bNeedState = false;
      boolean bNeedUpdate = false;
      PortletState state = null;
      PortletState storedState = null;

      if (session != null)
      {
         portletId = session.getId();

         if (this.isDebug())
         {
            this.logDebug(METHOD_NAME + "ENTER: portletId='" + portletId + "'");
         }

         if (_mapPortletData.containsKey(portletId))
         {
            /*
             * Need logic to determine if existing session is old / obsolete
             * should it be removed?
             */
            storedState = _mapPortletData.get(portletId);
            if (storedState == null)
            {
               bNeedState = true;
            }
            else
            {
               state = storedState;
               /*
                * if "reset" is TRUE, change to FALSE and update state in map
                */
               if (state.isReset())
               {
                  state.setReset(false);
                  bNeedUpdate = true;
               }
            }
         }
         else
         {
            bNeedState = true;
         }

         if (bNeedState)
         {
            /*
             * Does not exist, create it, save it
             */
            state = new PortletState(portletId);
            bNeedUpdate = true;

         }

         portalUserId = state.getPortalUserId();

         if (uniqueId != null && uniqueId.length() > 0)
         {
            if (portalUserId == null)
            {
               portalUserId = "";
            }

            if (!uniqueId.equals(portalUserId))
            {
               portalUserId = uniqueId;
               if (_portalUser != null)
               {
                  _propsUser.setProperty(ProvisioningPortlet.PORTALUSER_UNIQUEID, portalUserId);
                  ptkUserId = _portalUser.getId(_propsUser);

                  state.setPortalUserId(portalUserId);
                  state.setPtkUserId(ptkUserId);

                  bNeedUpdate = true;
               }
            }
         }

         if (bNeedUpdate)
         {
            _mapPortletData.put(portletId, state);
         }

         if (this.isDebug())
         {
            this.logDebug(METHOD_NAME + "EXIT:" +
               " PortletSession.getId()='" + portletId +
               "', state='" + state.toString() +
               "', mapSessionData='" + _mapPortletData.toString() +
               "', reset=" + state.isReset());
         }
      }
      else
      {
         this.logError(METHOD_NAME + "PortletSession is null");
      }
      return state;
   }

//   //----------------------------------------------------------------
//   private void setUserId(String uniqueId, PortletState state)
//   //----------------------------------------------------------------
//   {
//      String METHOD_NAME = CLASS_NAME + ":setUserId(): ";
//      String portalUserId = null;
//      String ptkUserId = null;
//      boolean bUpdate = false;
//
//      if (state != null)
//      {
//         portalUserId = state.getPortalUserId();
//         ptkUserId = state.getPtkUserId();
//
//         if (this.isDebug())
//         {
//            this.logDebug(METHOD_NAME + "ENTER: uniqueId='" + uniqueId +
//               "', portalUserId='" + portalUserId +
//               "', ptkUserId='" + ptkUserId +
//               "'");
//         }
//
//         if (uniqueId != null && uniqueId.length() > 0)
//         {
//            if (portalUserId == null)
//            {
//               portalUserId = "";
//            }
//
//            if (!uniqueId.equals(portalUserId))
//            {
//               portalUserId = uniqueId;
//               if (_portalUser != null)
//               {
//                  bUpdate = true;
//                  _propsUser.setProperty(ProvisioningPortlet.PORTALUSER_UNIQUEID, portalUserId);
//                  ptkUserId = _portalUser.getId(_propsUser);
//               }
//            }
//         }
//
//         if (bUpdate)
//         {
//            state.setPortalUserId(portalUserId);
//            state.setPtkUserId(ptkUserId);
//         }
//
//         if (this.isDebug())
//         {
//            this.logDebug(METHOD_NAME + "EXIT: uniqueId='" + uniqueId +
//               "', portalUserId='" + portalUserId +
//               "', ptkUserId='" + ptkUserId +
//               "'");
//         }
//      }
//      else
//      {
//         this.logError(METHOD_NAME + "PortletState is null");
//      }
//
//      return;
//   }
//   //----------------------------------------------------------------
//   private void initOpenPTK() throws PortletException
//   //----------------------------------------------------------------
//   {
//      String METHOD_NAME = CLASS_NAME + ":initOpenPTK(): ";
//      SetupIF setup = null;
//
//      this.logDebug(METHOD_NAME + "ENTER");
//
//      try
//      {
//         setup = new Setup(RESOURCE_BUNDLE_NAME);
//      }
//      catch (Exception ex)
//      {
//         throw new PortletException(METHOD_NAME + "Setup Error: " + ex.getMessage());
//      }
//
//      if (setup != null)
//      {
//         try
//         {
//            _connection = setup.getConnection();
//         }
//         catch (Exception ex)
//         {
//            throw new PortletException(METHOD_NAME + "getConnection() Error: " + ex.getMessage());
//         }
//      }
//      else
//      {
//         throw new PortletException(METHOD_NAME + "Resource Bundle is null.");
//      }
//
//      this.logDebug(METHOD_NAME + "EXIT");
//
//      return;
//   }
   //----------------------------------------------------------------
   private void setBaseVariables()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":setBaseVariables(): ";
      String pre = "portlet.";

      this.setDebug(Boolean.parseBoolean(_propsPortlet.getProperty(pre + "debug")));
      this.logInfo("debug=" + this.isDebug());

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      OPENPTKID_CLASSNAME = _propsPortlet.getProperty("openptkid.classname");
      PORTLET_ATTR_FIRSTNAME = _propsPortlet.getProperty(pre + "attr.firstname");
      PORTLET_ATTR_LASTNAME = _propsPortlet.getProperty(pre + "attr.lastname");
      PORTLET_ATTR_MESSAGE = _propsPortlet.getProperty(pre + "attr.message");
      PORTLET_ATTR_PASSWORD = _propsPortlet.getProperty(pre + "attr.password");
      PORTLET_ATTR_PHONEWORK = _propsPortlet.getProperty(pre + "attr.phonework");
      PORTLET_ATTR_STATUS = _propsPortlet.getProperty(pre + "attr.status");
      PORTLET_ATTR_TITLE = _propsPortlet.getProperty(pre + "attr.title");
      PORTLET_ATTR_UNIQUEID = _propsPortlet.getProperty(pre + "attr.uniqueid");
      PORTLET_INPUT_CANCEL = _propsPortlet.getProperty(pre + "input.cancel");
      PORTLET_INPUT_CHANGEINFO = _propsPortlet.getProperty(pre + "input.changeinfo");
      PORTLET_INPUT_CHANGEPWD = _propsPortlet.getProperty(pre + "input.changepwd");
      PORTLET_INPUT_CONFIRMPWD = _propsPortlet.getProperty(pre + "input.confirmpwd");
      PORTLET_INPUT_DELETE = _propsPortlet.getProperty(pre + "input.delete");
      PORTLET_INPUT_FINDVAL = _propsPortlet.getProperty(pre + "input.findval");
      PORTLET_INPUT_NEWPWD = _propsPortlet.getProperty(pre + "input.newpwd");
      PORTLET_INPUT_NONE = _propsPortlet.getProperty(pre + "input.none");
      PORTLET_INPUT_RESETPWD = _propsPortlet.getProperty(pre + "input.resetpwd");
      PORTLET_INPUT_USERCREATE = _propsPortlet.getProperty(pre + "input.usercreate");
      PORTLET_INPUT_USERFIND = _propsPortlet.getProperty(pre + "input.userfind");
      PORTLET_MSG_OPCODE_INVALID = _propsPortlet.getProperty(pre + "msg.opcode.invalid");
      PORTLET_MSG_OPCODE_NULL = _propsPortlet.getProperty(pre + "msg.opcode.null");
      PORTLET_MSG_OUTPUT_ERROR = _propsPortlet.getProperty(pre + "msg.output.error");
      PORTLET_MSG_OUTPUT_NULL = _propsPortlet.getProperty(pre + "msg.output.null");
      PORTLET_MSG_PWD_MISMATCH = _propsPortlet.getProperty(pre + "msg.pwd.mismatch");
      PORTLET_MSG_PWD_NULL = _propsPortlet.getProperty(pre + "msg.pwd.null");
      PORTLET_MSG_RESULT_NULL = _propsPortlet.getProperty(pre + "msg.result.null");
      PORTLET_MSG_SESSION_NULL = _propsPortlet.getProperty(pre + "msg.session.null");
      PORTLET_MSG_USER_NULL = _propsPortlet.getProperty(pre + "msg.user.null");
      PORTLET_MSG_UNIQUEID_NULL = _propsPortlet.getProperty(pre + "msg.uniqueid.null");
      PORTLET_OPCODE_ADDUSER = _propsPortlet.getProperty(pre + "opcode.adduser");
      PORTLET_OPCODE_ANSWERS = _propsPortlet.getProperty(pre + "opcode.answers");
      PORTLET_OPCODE_CHANGEINFO = _propsPortlet.getProperty(pre + "opcode.changeinfo");
      PORTLET_OPCODE_CHANGEPWD = _propsPortlet.getProperty(pre + "opcode.changepwd");
      PORTLET_OPCODE_FORGOTPWD = _propsPortlet.getProperty(pre + "opcode.forgotpwd");
      PORTLET_OPCODE_MENU = _propsPortlet.getProperty(pre + "opcode.menu");
      PORTLET_OPCODE_LISTUSERS = _propsPortlet.getProperty(pre + "opcode.listusers");
      PORTLET_OPCODE_SEARCHUSER = _propsPortlet.getProperty(pre + "opcode.searchuser");
      PORTLET_OPCODE_START = _propsPortlet.getProperty(pre + "opcode.start");
      PORTLET_OPCODE_REGISTRATION = _propsPortlet.getProperty(pre + "opcode.registration");
      PORTLET_OPCODE_REGISTRATIONRESULTS = _propsPortlet.getProperty(pre + "opcode.registration.results");
      PORTLET_OPCODE_USERDETAIL = _propsPortlet.getProperty(pre + "opcode.userdetail");
      PORTLET_OPCODE_USERUPDATE = _propsPortlet.getProperty(pre + "opcode.userupdate");
      PORTLET_PARAM_OPCODE = _propsPortlet.getProperty(pre + "param.opcode");
      PORTLET_PROMPT_READY = _propsPortlet.getProperty(pre + "prompt.ready");
      PORTLET_STATUS_EMPTY = _propsPortlet.getProperty(pre + "status.empty");
      PORTLET_STATUS_ERROR = _propsPortlet.getProperty(pre + "status.error");
      PORTLET_TITLE = _propsPortlet.getProperty(pre + "title");

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }
}
