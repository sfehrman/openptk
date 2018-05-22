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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Query;

//===================================================================
public class UserAdmin extends ProvisioningPortlet
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String PORTLET_ATTR_PWDLISTELEMENTS = NOT_SET;
   private String PORTLET_ATTR_PWDLISTSIZE = NOT_SET;
   private String PORTLET_ATTR_USERLISTELEMENTS = NOT_SET;
   private String PORTLET_ATTR_USERLISTSIZE = NOT_SET;
   private String PORTLET_JSP_HELP = NOT_SET;
   private String PORTLET_JSP_VIEW_LIST = NOT_SET;
   private String PORTLET_JSP_VIEW_PASSWORD = NOT_SET;
   private String PORTLET_JSP_VIEW_RESULTS = NOT_SET;
   private String PORTLET_JSP_VIEW_START = NOT_SET;
   private String PORTLET_JSP_VIEW_USERCREATE = NOT_SET;
   private String PORTLET_JSP_VIEW_USERDETAIL = NOT_SET;
   private String PORTLET_JSP_VIEW_USERFIND = NOT_SET;
   private String PORTLET_MSG_ATTR_MISSING = NOT_SET;
   private String PORTLET_MSG_PARAM_NOTFOUND = NOT_SET;
   private String PORTLET_MSG_SEARCHVALUE_NULL = NOT_SET;
   private String PORTLET_CHANGEINFO_ATTRS = "portlet.useradmin.changeinfo.attributes";
   private String PORTLET_DISPLAYINFO_ATTRS = "portlet.useradmin.displayinfo.attributes";
   private String PORTLET_SEARCHINFO_ATTRS = "portlet.useradmin.searchinfo.attributes";
   private String PORTLET_ADDUSER_INPUT_ATTRS = "portlet.useradmin.adduser.input.attributes";
   private String PORTLET_ADDUSER_REQ_ATTRS = "portlet.useradmin.adduser.required.attributes";
   private List<String> listChangeInfoAttrs = null;
   private List<String> listDisplayInfoAttrs = null;
   private List<String> listSearchInfoAttrs = null;
   private List<String> listAddUserInputAttrs = null;
   private List<String> listAddUserReqAttrs = null;

   //----------------------------------------------------------------
   public UserAdmin()
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":processAction(): ";
      String opCode = null;
      PortletState state = null;

      if (this.isDebug())
      {
         this.logAction(METHOD_NAME, request);
      }

      state = this.preprocessSession(request, response);

      state.setPageView(PORTLET_JSP_VIEW_START);
      state.putAttribute(ATTR_NAME_MESSAGE, PORTLET_PROMPT_READY);
      state.putAttribute(ATTR_NAME_STATUS, PORTLET_STATUS_EMPTY);
      state.setError(false);

      opCode = request.getParameter(PORTLET_PARAM_OPCODE);
      if (opCode != null)
      {
         if (opCode.equals(PORTLET_OPCODE_START)) // Start Page
         {
            this.actionStart(request, response, state);
         }
         else if (opCode.equals(PORTLET_OPCODE_MENU))  // Main Menu Page
         {
            this.actionMainMenu(request, response, state);
         }
         else if (opCode.equals(PORTLET_OPCODE_SEARCHUSER))  // Search User Page
         {
            this.actionSearchUser(request, response, state);
         }
         else if (opCode.equals((PORTLET_OPCODE_LISTUSERS))) // List Users Page
         {
            this.actionListUsers(request, response, state);
         }
         else if (opCode.equals(PORTLET_OPCODE_USERDETAIL))  // User Detail Page
         {
            this.actionUserDetail(request, response, state);
         }
         else if (opCode.equals(PORTLET_OPCODE_USERUPDATE))  // User Update Page
         {
            this.actionUserUpdate(request, response, state);
         }
         else if (opCode.equals(PORTLET_OPCODE_ADDUSER))  // Add User Page
         {
            this.actionAddUser(request, response, state);
         }
         else
         {
            this.setStateError(PORTLET_MSG_OPCODE_INVALID, state);
         }
      }
      else
      {
         this.setStateError(PORTLET_MSG_OPCODE_NULL, state);
      }

      response.setPortletMode(PortletMode.VIEW);

      this.postprocessSession(request, response, state);

      return;
   }

   //----------------------------------------------------------------
   @Override
   public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doView(): ";
      String page = null;
      PortletState state = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      state = this.preprocessSession(request, response);

      if (state.getMode() != PortletMode.VIEW)
      {
         state.setMode(PortletMode.VIEW);
      }

      page = state.getPageView();

      if (state.isReset() || page == null || page.length() < 1)
      {
         state.setPageView(PORTLET_JSP_VIEW_START);
         state.putAttribute(ATTR_NAME_MESSAGE, PORTLET_PROMPT_READY);
         state.putAttribute(ATTR_NAME_STATUS, PORTLET_STATUS_EMPTY);
      }

      this.postprocessSession(request, response, state);

      this.dispatchJspPage(request, response, state);

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   //----------------------------------------------------------------
   @Override
   public void doHelp(RenderRequest request, RenderResponse response) throws PortletException, IOException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doHelp(): ";
      String page = null;
      PortletState state = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      state = this.preprocessSession(request, response);

      if (state.getMode() != PortletMode.HELP)
      {
         state.setMode(PortletMode.HELP);
      }

      page = state.getPageHelp();

      if (state.isReset() || page == null || page.length() < 1)
      {
         state.setPageHelp(PORTLET_JSP_HELP);
      }

      this.postprocessSession(request, response, state);

      this.dispatchJspPage(request, response, state);

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   //----------------------------------------------------------------
   @Override
   protected void initLocal() throws PortletException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":initLocal(): ";

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      this.initProperties(CLASS_NAME);
      this.setPortletVariables();

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   //----------------------------------------------------------------
   @Override
   protected void setPortletVariables()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":setPortletVariables(): ";
      String pre = "portlet.useradmin.";
      StringTokenizer strTok = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      PORTLET_ATTR_PWDLISTELEMENTS = _propsPortlet.getProperty("portlet.attr.pwdlistelements");
      PORTLET_ATTR_PWDLISTSIZE = _propsPortlet.getProperty("portlet.attr.pwdlistsize");
      PORTLET_ATTR_USERLISTELEMENTS = _propsPortlet.getProperty("portlet.attr.userlistelements");
      PORTLET_ATTR_USERLISTSIZE = _propsPortlet.getProperty("portlet.attr.userlistsize");
      PORTLET_JSP_HELP = _propsPortlet.getProperty(pre + "jsp.help");
      PORTLET_JSP_VIEW_LIST = _propsPortlet.getProperty(pre + "jsp.view.list");
      PORTLET_JSP_VIEW_PASSWORD = _propsPortlet.getProperty(pre + "jsp.view.password");
      PORTLET_JSP_VIEW_RESULTS = _propsPortlet.getProperty(pre + "jsp.view.results");
      PORTLET_JSP_VIEW_START = _propsPortlet.getProperty(pre + "jsp.view.start");
      PORTLET_JSP_VIEW_USERCREATE = _propsPortlet.getProperty(pre + "jsp.view.usercreate");
      PORTLET_JSP_VIEW_USERDETAIL = _propsPortlet.getProperty(pre + "jsp.view.userdetail");
      PORTLET_JSP_VIEW_USERFIND = _propsPortlet.getProperty(pre + "jsp.view.userfind");
      PORTLET_MSG_ATTR_MISSING = _propsPortlet.getProperty(pre + "msg.attr.missing");
      PORTLET_MSG_PARAM_NOTFOUND = _propsPortlet.getProperty(pre + "msg.param.notfound");
      PORTLET_MSG_SEARCHVALUE_NULL = _propsPortlet.getProperty(pre + "msg.searchvalue.null");
      PORTLET_CHANGEINFO_ATTRS = _propsPortlet.getProperty(pre + "changeinfo.attributes");
      PORTLET_DISPLAYINFO_ATTRS = _propsPortlet.getProperty(pre + "displayinfo.attributes");
      PORTLET_SEARCHINFO_ATTRS = _propsPortlet.getProperty(pre + "searchinfo.attributes");
      PORTLET_ADDUSER_INPUT_ATTRS = _propsPortlet.getProperty(pre + "adduser.input.attributes");
      PORTLET_ADDUSER_REQ_ATTRS = _propsPortlet.getProperty(pre + "adduser.required.attributes");

      /*
       * Attributes that can be shown during search operations
       */

      this.listSearchInfoAttrs = new LinkedList<String>();
      strTok = new StringTokenizer(this.PORTLET_SEARCHINFO_ATTRS, ",");
      while (strTok.hasMoreTokens())
      {
         this.listSearchInfoAttrs.add(strTok.nextToken());
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "PORTLET_SEARCHINFO_ATTRS: " +
            this.listSearchInfoAttrs.toString());
      }

      /*
       * Attributes that can be changed
       */

      this.listChangeInfoAttrs = new LinkedList<String>();
      strTok = new StringTokenizer(this.PORTLET_CHANGEINFO_ATTRS, ",");
      while (strTok.hasMoreTokens())
      {
         this.listChangeInfoAttrs.add(strTok.nextToken());
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "PORTLET_CHANGEINFO_ATTRS: " +
            this.listChangeInfoAttrs.toString());
      }

      /*
       * Attributes that can be displyed on the detail pages
       */

      this.listDisplayInfoAttrs = new LinkedList<String>();
      strTok = new StringTokenizer(this.PORTLET_DISPLAYINFO_ATTRS, ",");
      while (strTok.hasMoreTokens())
      {
         this.listDisplayInfoAttrs.add(strTok.nextToken());
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "PORTLET_DISPLAYINFO_ATTRS: " +
            this.listDisplayInfoAttrs.toString());
      }

      /*
       * Attributes that can be set
       */

      this.listAddUserInputAttrs = new LinkedList<String>();
      strTok = new StringTokenizer(this.PORTLET_ADDUSER_INPUT_ATTRS, ",");
      while (strTok.hasMoreTokens())
      {
         this.listAddUserInputAttrs.add(strTok.nextToken());
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "PORTLET_ADDUSER_INPUT_ATTRS: " +
            listAddUserInputAttrs.toString());
      }

      /*
       * Attributes that are required
       */

      this.listAddUserReqAttrs = new LinkedList<String>();
      strTok = new StringTokenizer(this.PORTLET_ADDUSER_REQ_ATTRS, ",");
      while (strTok.hasMoreTokens())
      {
         this.listAddUserReqAttrs.add(strTok.nextToken());
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "PORTLET_ADDUSER_REQ_ATTRS: " +
            listAddUserReqAttrs.toString());
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private void actionStart(ActionRequest request,
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":actionStart(): ";

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      state.setPageView(PORTLET_JSP_VIEW_START);
      state.setError(false);

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   //----------------------------------------------------------------
   private void actionListUsers(ActionRequest request,
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":actionListUsers(): ";
      String inputCancel = null;
      String searchResultUserCreate = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      inputCancel = request.getParameter(PORTLET_INPUT_CANCEL);

      // was cancel button pressed ? YES if not NULL

      if (inputCancel == null)
      {
         searchResultUserCreate = request.getParameter(PORTLET_INPUT_USERCREATE);

         if (searchResultUserCreate != null)
         {
            state.setPageView(PORTLET_JSP_VIEW_USERCREATE);
         }
      }
      else
      {
         this.actionStart(request, response, state);
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   //----------------------------------------------------------------
   private void actionMainMenu(ActionRequest request,
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":actionMainMenu(): ";
      String mainMenuUserFind = null;
      String mainMenuUserCreate = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      /*
       * available input options:
       *    mainMenuUserFind
       *    mainMenuUserCreate
       */

      mainMenuUserFind = request.getParameter(PORTLET_INPUT_USERFIND);

      if (mainMenuUserFind != null)
      {
         state.setPageView(PORTLET_JSP_VIEW_USERFIND);
      }
      else
      {
         mainMenuUserCreate = request.getParameter(PORTLET_INPUT_USERCREATE);

         if (mainMenuUserCreate != null)
         {
            state.setPageView(PORTLET_JSP_VIEW_USERCREATE);
         }
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   //----------------------------------------------------------------
   private void actionSearchUser(ActionRequest request,
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      boolean bInputError = false;
      String METHOD_NAME = CLASS_NAME + ":actionSearchUser(): ";
      String searchUserFindVal = null;
      String inputCancel = null;
      Query query = null;
      Input input = null;
      List<ElementIF> elements = null;
      PortletSession psession = null;

      psession = request.getPortletSession();

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      /*
       * input: searchUserFindVal:  "Joe", "John Doe", "ja1324"
       */

      inputCancel = request.getParameter(PORTLET_INPUT_CANCEL);

      // was cancel button pressed ? YES if not NULL

      if (inputCancel == null)
      {
         searchUserFindVal = request.getParameter(PORTLET_INPUT_FINDVAL);

         if (searchUserFindVal != null && searchUserFindVal.length() > 0)
         {
            query = new Query(Query.Type.NOOPERATOR, "search", searchUserFindVal);
         }
         else
         {
            this.setStateError(PORTLET_MSG_SEARCHVALUE_NULL, state);
            state.setPageView(PORTLET_JSP_VIEW_USERFIND);
            bInputError = true;
         }

         if (!bInputError)
         {
            input = new Input();

            if (query != null)
            {
               input.setQuery(query);
            }

            for (Iterator<String> iter = this.listSearchInfoAttrs.iterator(); iter.hasNext();)
            {
               input.addAttribute(iter.next());
            }

            elements = this.execSearch(input);

            if (elements.size() > 0)
            {
               psession.setAttribute(
                  PORTLET_ATTR_USERLISTELEMENTS, elements,
                  PortletSession.PORTLET_SCOPE);
               psession.setAttribute(
                  PORTLET_ATTR_USERLISTSIZE, elements.size(),
                  PortletSession.PORTLET_SCOPE);
            }
            else
            {
               psession.setAttribute(
                  PORTLET_ATTR_USERLISTELEMENTS, new ArrayList<ElementIF>(),
                  PortletSession.PORTLET_SCOPE);
               psession.setAttribute(
                  PORTLET_ATTR_USERLISTSIZE, "0",
                  PortletSession.PORTLET_SCOPE);
            }

            state.setPageView(PORTLET_JSP_VIEW_LIST);
         }
      }
      else
      {
         this.actionStart(request, response, state);
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   //----------------------------------------------------------------
   private void actionUserDetail(ActionRequest request,
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":actionUserDetail(): ";
      String inputUniqueId = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      inputUniqueId = request.getParameter(PORTLET_ATTR_UNIQUEID);

      if (inputUniqueId != null)
      {
         this.setPersonParameters(request, inputUniqueId, listDisplayInfoAttrs);
         state.setPageView(PORTLET_JSP_VIEW_USERDETAIL);
      }
      else
      {
         this.setStateError(PORTLET_MSG_UNIQUEID_NULL, state);
         state.setPageView(PORTLET_JSP_VIEW_START);
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   //----------------------------------------------------------------
   private void actionUserUpdate(ActionRequest request,
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":actionUserUpdate(): ";
      String param = null;
      String uniqueId = null;
      String strAttrName = null;
      String strAttrValue = null;
      Iterator<String> iterAttr = null;
      ElementIF result = null;
      Input input = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      /*
       * check to see which button was pushed, choices:
       * Update             INPUT_CHANGEINFO
       * Delete             INPUT_DELETE
       * Cancel             INPUT_CANCEL
       * Reset Password     INPUT_RESETPWD
       */

      uniqueId = request.getParameter(PORTLET_ATTR_UNIQUEID);

      if (uniqueId != null)
      {
         param = request.getParameter(PORTLET_INPUT_CANCEL);
         if (param == null)
         {
            input = new Input();
            input.setUniqueId(uniqueId);

            param = request.getParameter(PORTLET_INPUT_CHANGEINFO);
            if (param == null)
            {
               param = request.getParameter(PORTLET_INPUT_DELETE);
               if (param == null)
               {
                  param = request.getParameter(PORTLET_INPUT_RESETPWD);
                  if (param == null)
                  {
                     // unknown input (error)
                     this.setStateError(PORTLET_MSG_PARAM_NOTFOUND, state);
                     state.setPageView(PORTLET_JSP_VIEW_START);
                  }
                  else // user pressed the RESET PASSWORD button
                  {
                     this.passwordReset(request, input, state);
                  }
               }
               else // user pressed the DELETE button
               {
                  result = this.execDelete(input);
                  state.setPageView(PORTLET_JSP_VIEW_RESULTS);
                  state.putAttribute(ATTR_NAME_MESSAGE, result.getStatus());
               }
            }
            else // user pressed the UPDATE button
            {
               iterAttr = this.listChangeInfoAttrs.iterator();
               while (iterAttr.hasNext())
               {
                  strAttrName = iterAttr.next();
                  if (strAttrName != null)
                  {
                     strAttrValue = null;
                     strAttrValue = request.getParameter(strAttrName);
                     if (strAttrValue == null)
                     {
                        strAttrValue = "";
                     }

                     input.addAttribute(strAttrName, strAttrValue);
                  }
               }

               result = this.execUpdate(input);
               state.putAttribute(ATTR_NAME_MESSAGE, result.getStatus());
               state.setPageView(PORTLET_JSP_VIEW_RESULTS);
            }
         }
         else // user pressed the CANCEL button
         {
            this.actionStart(request, response, state);
         }

      }
      else // null uniqueId
      {
         this.setStateError(PORTLET_MSG_UNIQUEID_NULL, state);
         state.setPageView(PORTLET_JSP_VIEW_START);
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   //----------------------------------------------------------------
   private void actionAddUser(ActionRequest request,
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":actionAddUser(): ";
      String param = null;
      String strAttrName = null;
      String strAttrValue = null;
      boolean bMissingAttr = false;
      Iterator<String> iterAttr = null;
      Input input = null;
      ElementIF result = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      param = request.getParameter(PORTLET_INPUT_CANCEL);

      // was cancel button pressed ? YES if not NULL

      if (param == null)
      {
         input = new Input();

         // read all the input attributes from the request parameters

         iterAttr = this.listAddUserInputAttrs.iterator();

         while (iterAttr.hasNext())
         {
            strAttrName = null;
            strAttrName = iterAttr.next();
            if (strAttrName != null)
            {
               strAttrValue = null;
               strAttrValue = request.getParameter(strAttrName);
               if (strAttrValue == null)
               {
                  strAttrValue = "";
               }

               input.addAttribute(strAttrName, strAttrValue);
            }
         }

         // check for required attributes

         iterAttr = null;
         iterAttr = this.listAddUserReqAttrs.iterator();

         while (iterAttr.hasNext())
         {
            strAttrName = null;
            strAttrName = iterAttr.next();
            if (strAttrName != null)
            {
               strAttrValue = null;
               strAttrValue = input.getAttribute(strAttrName).getValueAsString();
               if (strAttrValue == null)
               {
                  strAttrValue = "";
               }
               if (strAttrValue.length() < 1)
               {
                  bMissingAttr = true;
               }
            }
         }

         if (this.isDebug())
         {
            this.logDebug(METHOD_NAME + "bMissingAttr = " + bMissingAttr);
         }

         if (!bMissingAttr)
         {
            result = this.execCreate(input);
            state.putAttribute(ATTR_NAME_MESSAGE, result.getStatus());
            state.setPageView(PORTLET_JSP_VIEW_RESULTS);
         }
         else
         {
            this.setStateError(PORTLET_MSG_ATTR_MISSING, state);
            state.setPageView(PORTLET_JSP_VIEW_USERCREATE);
         }
      }
      else // Pressed the Cancel button
      {
         this.actionStart(request, response, state);
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   //----------------------------------------------------------------
   private void passwordReset(ActionRequest request,
      Input input,
      PortletState state)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":passwordReset(): ";
      List<ElementIF> results = null;
      PortletSession psession = null;

      psession = request.getPortletSession();

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      results = this.execPasswordReset(input);

      if (results.size() > 0)
      {
         psession.setAttribute(
            PORTLET_ATTR_PWDLISTELEMENTS, results,
            PortletSession.PORTLET_SCOPE);
         psession.setAttribute(
            PORTLET_ATTR_PWDLISTSIZE, results.size(),
            PortletSession.PORTLET_SCOPE);
      }
      else
      {
         psession.setAttribute(
            PORTLET_ATTR_PWDLISTELEMENTS, new ArrayList<ElementIF>(),
            PortletSession.PORTLET_SCOPE);
         psession.setAttribute(
            PORTLET_ATTR_PWDLISTSIZE, "0",
            PortletSession.PORTLET_SCOPE);
      }

      state.setPageView(PORTLET_JSP_VIEW_PASSWORD);

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }
}
