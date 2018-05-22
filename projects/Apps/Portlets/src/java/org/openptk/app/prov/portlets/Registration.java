/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
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
package org.openptk.app.prov.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 *
 * @author Derrick Harcey, Sun Microsystems, Inc.
 *
 */
//===================================================================
public class Registration extends ProvisioningPortlet
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String PORTLET_JSP_HELP = NOT_SET;
   private String PORTLET_JSP_VIEW_REGISTRATION = NOT_SET;
   private String PORTLET_JSP_VIEW_RESULTS = NOT_SET;
   private String PORTLET_JSP_VIEW_START = NOT_SET;

   //----------------------------------------------------------------
   public Registration()
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
         else if (opCode.equals(PORTLET_OPCODE_REGISTRATION))  // register
         {
            this.actionRegistration(request, response, state);
         }
         else if (opCode.equals(PORTLET_OPCODE_REGISTRATIONRESULTS))  // register
         {
            this.actionRegistrationResults(request, response, state);
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
      state.putAttribute(ATTR_NAME_MESSAGE, PORTLET_PROMPT_READY);

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

      this.initProperties(CLASS_NAME);
      this.setPortletVariables();

      return;
   }

   //----------------------------------------------------------------
   @Override
   protected void setPortletVariables()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":setPortletVariables(): ";
      String pre = "portlet.registration.";

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }


      PORTLET_JSP_HELP = _propsPortlet.getProperty(pre + "jsp.help");
      PORTLET_JSP_VIEW_REGISTRATION = _propsPortlet.getProperty(pre + "jsp.view.register");
      PORTLET_JSP_VIEW_RESULTS = _propsPortlet.getProperty(pre + "jsp.view.results");
      PORTLET_JSP_VIEW_START = _propsPortlet.getProperty(pre + "jsp.view.start");


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
      PortletSession psession = null;

      psession = request.getPortletSession();

      if (this.isDebug())
      {
         this.logAction(METHOD_NAME, request);
      }

      // Ensure fields are null
      psession.setAttribute("username", "", PortletSession.PORTLET_SCOPE);
      psession.setAttribute("firstname", "", PortletSession.PORTLET_SCOPE);
      psession.setAttribute("middlename", "", PortletSession.PORTLET_SCOPE);
      psession.setAttribute("lastname", "", PortletSession.PORTLET_SCOPE);
      psession.setAttribute("email", "", PortletSession.PORTLET_SCOPE);
      psession.setAttribute("password", "", PortletSession.PORTLET_SCOPE);
      psession.setAttribute("acceptterms", "", PortletSession.PORTLET_SCOPE);

      state.setPageView(PORTLET_JSP_VIEW_START);
      state.setError(false);

      return;
   }

   //----------------------------------------------------------------
   private void actionRegistration(ActionRequest request,
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":actionValidateAnswers(): ";
      String inputCancel = null;

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      inputCancel = request.getParameter(PORTLET_INPUT_CANCEL);

      // was cancel button pressed ? YES if not NULL

      if (inputCancel == null)
      {

         state.setPageView(PORTLET_JSP_VIEW_REGISTRATION);
         state.setError(false);

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
   private void actionRegistrationResults(ActionRequest request,
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":actionValidateAnswers(): ";
      String inputCancel = null;
      String username = null;
      String firstname = null;
      String middlename = null;
      String lastname = null;
      String email = null;
      String password = null;
      String acceptterms = null;
      PortletSession psession = null;

      psession = request.getPortletSession();

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      username = request.getParameter("username");
      psession.setAttribute("username", username, PortletSession.PORTLET_SCOPE);

      firstname = request.getParameter("firstname");
      psession.setAttribute("firstname", firstname, PortletSession.PORTLET_SCOPE);

      middlename = request.getParameter("middlename");
      psession.setAttribute("middlename", middlename, PortletSession.PORTLET_SCOPE);

      lastname = request.getParameter("lastname");
      psession.setAttribute("lastname", lastname, PortletSession.PORTLET_SCOPE);

      email = request.getParameter("email");
      psession.setAttribute("email", email, PortletSession.PORTLET_SCOPE);

      password = request.getParameter("password");
      psession.setAttribute("password", password, PortletSession.PORTLET_SCOPE);

      acceptterms = request.getParameter("acceptterms");
      psession.setAttribute("acceptterms", acceptterms, PortletSession.PORTLET_SCOPE);

      inputCancel = request.getParameter(PORTLET_INPUT_CANCEL);

      // was cancel button pressed ? YES if not NULL
      if (inputCancel == null)
      {
//         if ((email == null) || (email.length() < 1)||(firstname == null) || (firstname.length() < 1)||(lastname == null) || (lastname.length() < 1)|| (acceptterms != "on"))
         if ((email == null) || (email.length() < 1) || (firstname == null) || (firstname.length() < 1) || (lastname == null) || (lastname.length() < 1)|| (acceptterms == null))
         {
            state.setPageView(PORTLET_JSP_VIEW_REGISTRATION);
            state.setError(true);
            state.putAttribute(ATTR_NAME_MESSAGE, "Please complete all fields!");
            state.putAttribute(ATTR_NAME_STATUS, PORTLET_STATUS_ERROR);
            psession.setAttribute("resubmit", "true", PortletSession.PORTLET_SCOPE);
         }
         else
         {
            state.setPageView(PORTLET_JSP_VIEW_RESULTS);
            state.setError(false);
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
}
