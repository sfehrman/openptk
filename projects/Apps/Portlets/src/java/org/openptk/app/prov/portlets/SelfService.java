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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.openptk.api.Input;
import org.openptk.api.Output;

//===================================================================
public class SelfService extends ProvisioningPortlet
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String PORTLET_JSP_HELP = NOT_SET;
   private String PORTLET_JSP_VIEW_START = NOT_SET;
   private String PORTLET_JSP_VIEW_CHANGEPWD = NOT_SET;
   private String PORTLET_JSP_VIEW_CHANGEINFO = NOT_SET;
   private String PORTLET_JSP_VIEW_RESULTS = NOT_SET;
   private String CHANGEINFO_ATTRS = "portlet.selfservice.changeinfo.attributes";
   private List<String> listChangeInfoAttrs = null;

   //----------------------------------------------------------------
   public SelfService()
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void processAction(ActionRequest request, ActionResponse response)
      throws PortletException, IOException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":processAction(): ";
      String opCode = null;
      String openptkId =null;
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
      
      openptkId = state.getPtkUserId();

      if (openptkId != null)
      {
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
            else if (opCode.equals(PORTLET_OPCODE_CHANGEPWD)) // Change Pwd Page
            {
               this.actionChangePassword(request, response, state);
            }
            else if (opCode.equals(PORTLET_OPCODE_CHANGEINFO)) // Change Info Page
            {
               this.actionChangeInfo(request, response, state);
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
      }
      else
      {
         this.setStateError(PORTLET_MSG_USER_NULL, state);
      }

      response.setPortletMode(PortletMode.VIEW);

      this.postprocessSession(request, response, state);

      return;
   }

   //----------------------------------------------------------------
   @Override
   public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException
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
   public void doHelp(RenderRequest request, RenderResponse response)
      throws PortletException, IOException
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
      String pre = "portlet.selfservice.";
      StringTokenizer strTok = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      PORTLET_JSP_HELP = _propsPortlet.getProperty(pre + "jsp.help");
      PORTLET_JSP_VIEW_START = _propsPortlet.getProperty(pre + "jsp.view.start");
      PORTLET_JSP_VIEW_CHANGEPWD = _propsPortlet.getProperty(pre + "jsp.view.changepwd");
      PORTLET_JSP_VIEW_CHANGEINFO = _propsPortlet.getProperty(pre + "jsp.view.changeinfo");
      PORTLET_JSP_VIEW_RESULTS = _propsPortlet.getProperty(pre + "jsp.view.results");

      listChangeInfoAttrs = new LinkedList<String>();
      strTok = new StringTokenizer(_propsPortlet.getProperty(CHANGEINFO_ATTRS), ",");
      while (strTok.hasMoreTokens())
      {
         listChangeInfoAttrs.add(strTok.nextToken());
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
         this.logAction(METHOD_NAME, request);
      }

      state.setPageView(PORTLET_JSP_VIEW_START);
      state.setError(false);

      return;
   }

   //----------------------------------------------------------------
   private void actionMainMenu(ActionRequest request,
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":actionMainMenu(): ";
      String actionMainMenuChangePwd = null;
      String actionMainMenuChangeInfo = null;

      /*
       * available input options:
       *    actionMainMenuChangePwd
       *    actionMainMenuChangeInfo
       */

      if (this.isDebug())
      {
         this.logAction(METHOD_NAME, request);
      }

      actionMainMenuChangePwd = request.getParameter(PORTLET_INPUT_CHANGEPWD);

      if (actionMainMenuChangePwd != null)
      {
         state.setPageView(PORTLET_JSP_VIEW_CHANGEPWD);
      }
      else
      {
         actionMainMenuChangeInfo = request.getParameter(PORTLET_INPUT_CHANGEINFO);

         if (actionMainMenuChangeInfo != null)
         {
            state.setPageView(PORTLET_JSP_VIEW_CHANGEINFO);
            this.setPersonParameters(request, state.getPtkUserId(), listChangeInfoAttrs);
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void actionChangePassword(ActionRequest request, 
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":actionChangePassword(): ";
      String inputCancel = null;
      String newPwd = null;
      String confirmPwd = null;
      Input input = null;
      Output output = null;

      if (this.isDebug())
      {
         this.logAction(METHOD_NAME, request);
      }

      inputCancel = request.getParameter(PORTLET_INPUT_CANCEL);

      // was cancel button pressed ? YES if not NULL

      if (inputCancel == null)
      {
         newPwd = request.getParameter(PORTLET_INPUT_NEWPWD);
         confirmPwd = request.getParameter(PORTLET_INPUT_CONFIRMPWD);

         if (newPwd == null)
         {
            newPwd = "";
         }
         if (confirmPwd == null)
         {
            confirmPwd = "";
         }

         if (newPwd.length() < 1 || confirmPwd.length() < 1)
         {
            this.setStateError(PORTLET_MSG_PWD_NULL, state);
            state.setPageView(PORTLET_JSP_VIEW_CHANGEPWD);
         }
         else
         {
            if (!newPwd.equals(confirmPwd))
            {
               this.setStateError(PORTLET_MSG_PWD_MISMATCH, state);
               state.setPageView(PORTLET_JSP_VIEW_CHANGEPWD);
            }
            else
            {
               input = new Input();

               input.setUniqueId(state.getPtkUserId());
               input.addAttribute(PORTLET_ATTR_PASSWORD, newPwd);

               output = this.execPasswordChange(input);

               state.setPageView(this.PORTLET_JSP_VIEW_RESULTS);

               if (this.isDebug())
               {
                  this.logDebug(METHOD_NAME + output.getStatus());
               }
            }
         }
      }
      else
      {
         this.actionStart(request, response, state);
      }

      return;
   }

   //----------------------------------------------------------------
   private void actionChangeInfo(ActionRequest request, 
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":actionChangeInfo(): ";
      String inputCancel = null;
      String strAttrName = null;
      String strAttrValue = null;
      Iterator<String> iterAttr = null;
      Input input = null;
      Output output = null;

      if (this.isDebug())
      {
         this.logAction(METHOD_NAME, request);
      }

      inputCancel = request.getParameter(PORTLET_INPUT_CANCEL);

      // was cancel button pressed ? YES if not NULL

      if (inputCancel == null)
      {
         input = new Input();
         input.setUniqueId(state.getPtkUserId());

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

         output = this.execUpdate(input);

         state.setPageView(PORTLET_JSP_VIEW_RESULTS);
         this.logDebug(METHOD_NAME + output.getStatus());
      }
      else
      {
         this.actionStart(request, response, state);
      }

      return;
   }
}
