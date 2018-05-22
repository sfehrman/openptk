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
import java.util.LinkedList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.openptk.api.Attribute;
import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.State;
import org.openptk.structure.StructureIF;

//===================================================================
public class ForgotPassword extends ProvisioningPortlet
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String PORTLET_ATTR_ANSWERSINPUT = NOT_SET;
   private String PORTLET_ATTR_FORGOTANSWERS = NOT_SET;
   private String PORTLET_ATTR_FORGOTQUESTIONS = NOT_SET;
   private String PORTLET_ATTR_QUESTIONSIZE = NOT_SET;
   private String PORTLET_ATTR_QUESTIONSTRINGS = NOT_SET;
   private String PORTLET_JSP_HELP = NOT_SET;
   private String PORTLET_JSP_VIEW_CHANGEPWD = NOT_SET;
   private String PORTLET_JSP_VIEW_QUESTIONS = NOT_SET;
   private String PORTLET_JSP_VIEW_RESULTS = NOT_SET;
   private String PORTLET_JSP_VIEW_START = NOT_SET;
   private String PORTLET_MSG_NOQUESTIONS = NOT_SET;
   private String PORTLET_MSG_WRONGANSWERS = NOT_SET;
   private List<String> _forgotPwdQuestions = new LinkedList<String>();

   //----------------------------------------------------------------
   public ForgotPassword()
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
         else if (opCode.equals(PORTLET_OPCODE_FORGOTPWD))  // show questions
         {
            this.actionForgotPwd(request, response, state);
         }
         else if (opCode.equals(PORTLET_OPCODE_ANSWERS))  // process answers
         {
            this.actionValidateAnswers(request, response, state);
         }
         else if (opCode.equals(PORTLET_OPCODE_CHANGEPWD)) // Change Pwd Page
         {
            this.actionChangePassword(request, response, state);
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
      String pre = "portlet.forgotpassword.";

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      PORTLET_ATTR_ANSWERSINPUT = _propsPortlet.getProperty("portlet.attr.answersinput");
      PORTLET_ATTR_FORGOTANSWERS = _propsPortlet.getProperty("portlet.attr.forgotanswers");
      PORTLET_ATTR_FORGOTQUESTIONS = _propsPortlet.getProperty("portlet.attr.forgotquestions");
      PORTLET_ATTR_QUESTIONSIZE = _propsPortlet.getProperty("portlet.attr.questionsize");
      PORTLET_ATTR_QUESTIONSTRINGS = _propsPortlet.getProperty("portlet.attr.questionstrings");
      PORTLET_JSP_HELP = _propsPortlet.getProperty(pre + "jsp.help");
      PORTLET_JSP_VIEW_CHANGEPWD = _propsPortlet.getProperty(pre + "jsp.view.changepwd");
      PORTLET_JSP_VIEW_QUESTIONS = _propsPortlet.getProperty(pre + "jsp.view.questions");
      PORTLET_JSP_VIEW_RESULTS = _propsPortlet.getProperty(pre + "jsp.view.results");
      PORTLET_JSP_VIEW_START = _propsPortlet.getProperty(pre + "jsp.view.start");
      PORTLET_MSG_NOQUESTIONS = _propsPortlet.getProperty(pre + "msg.noquestions");
      PORTLET_MSG_WRONGANSWERS = _propsPortlet.getProperty(pre + "msg.wronganswers");

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
   private void actionForgotPwd(ActionRequest request,
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      boolean bFoundUser = false;
      String METHOD_NAME = CLASS_NAME + ":actionForgotPwd(): ";
      String uniqueId = null;
      String attrName = null;
      String attrValue = null;
      PortletSession psession = null;
      Input input = null;      // OpenPTK
      Output output = null;    // OpenPTK
      ElementIF result = null; // OpenPTK
      AttributeIF attr = null; // OpenPTK

      psession = request.getPortletSession();

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      uniqueId = request.getParameter(PORTLET_ATTR_UNIQUEID);

      if (uniqueId != null && uniqueId.length() > 0)
      {
         input = new Input();
         input.setUniqueId(uniqueId);

         /*
          * Validation Phase 0
          * Do a "read" first, get the first and last name
          * validates that this is uniqueid does exist.
          * We also want to display the first and last name
          */

         result = this.execRead(input);
         if (result != null)
         {
            if (!result.isError())
            {
               /*
                * uniqueid
                */

               attrName = PORTLET_ATTR_UNIQUEID;
               attrValue = uniqueId;
               psession.setAttribute(attrName, attrValue, PortletSession.PORTLET_SCOPE);

               /*
                * firstname
                */

               attrName = PORTLET_ATTR_FIRSTNAME;
               attr = result.getAttribute(attrName);
               if (attr != null)
               {
                  attrValue = attr.getValueAsString();
               }
               else
               {
                  attrValue = "";
               }
               psession.setAttribute(attrName, attrValue, PortletSession.PORTLET_SCOPE);
               state.putAttribute(attrName, attrValue);

               /*
                * lastname
                */

               attrName = PORTLET_ATTR_LASTNAME;
               attr = result.getAttribute(attrName);
               if (attr != null)
               {
                  attrValue = attr.getValueAsString();
               }
               else
               {
                  attrValue = "";
               }
               psession.setAttribute(attrName, attrValue, PortletSession.PORTLET_SCOPE);
               state.putAttribute(attrName, attrValue);

               bFoundUser = true;
            }
            else
            {
               this.setStateError("Account Id not found: " + uniqueId, state);
            }
         }
         else
         {
            this.setStateError(PORTLET_MSG_RESULT_NULL, state);
         }

         /*
          * Validation Phase 1 
          * Get the questions related to the user
          */

         input.setProperty(StructureIF.NAME_MODE, StructureIF.NAME_QUESTIONS);

         output = this.execPasswordForgot(input);

         if (output != null)
         {
            if (output.getResultsSize() == 1)
            {
               result = output.getResults().get(0);
            }

            if (result != null)
            {
               if (!result.isError())
               {
                  /*
                   * we need to extract the List of questions from the person's
                   * (ElementIF) attributes.  The questions are stored in a
                   * multi-valued attribute.  We need a List<String> for the JSP
                   */

                  this.getForgotQuestionsAsList(result);

                  if (_forgotPwdQuestions.size() > 0)
                  {
                     attrName = PORTLET_ATTR_QUESTIONSIZE;
                     psession.setAttribute(attrName,
                        Integer.toString(_forgotPwdQuestions.size()),
                        PortletSession.PORTLET_SCOPE);

                     attrName = PORTLET_ATTR_QUESTIONSTRINGS;
                     psession.setAttribute(attrName, _forgotPwdQuestions,
                        PortletSession.PORTLET_SCOPE);
                  }
                  else
                  {
                     this.setStateError(PORTLET_MSG_NOQUESTIONS, state);
                  }

                  if (this.isDebug())
                  {
                     this.logDebug(METHOD_NAME + "bFoundUser=" + bFoundUser +
                        ", " + this.PORTLET_ATTR_QUESTIONSTRINGS + "=" +
                        _forgotPwdQuestions.toString());
                  }
               }
               else
               {
                  this.setStateError("Account Id not found: " + uniqueId, state);
               }
            }
            else
            {
               this.setStateError(PORTLET_MSG_RESULT_NULL, state);
            }
         }
         else
         {
            this.setStateError(PORTLET_MSG_OUTPUT_NULL + ": " + uniqueId, state);
         }
      }
      else
      {
         this.setStateError(PORTLET_MSG_UNIQUEID_NULL, state);
      }

      if (bFoundUser)
      {
         state.setPageView(PORTLET_JSP_VIEW_QUESTIONS);
      }
      else
      {
         state.setPageView(PORTLET_JSP_VIEW_START);
      }

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "EXIT");
      }

      return;
   }

   //----------------------------------------------------------------
   private void actionValidateAnswers(ActionRequest request,
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      boolean bDone = false;
      int iAnswerCnt = 0;
      String METHOD_NAME = CLASS_NAME + ":actionValidateAnswers(): ";
      String inputCancel = null;
      String answerKey = null;
      String answerVal = null;
      String uniqueId = null;
      String[] questions = null;
      String[] answers = null;
      List<String> listAnswers = null;
      Input input = null;               // OpenPTK
      Output output = null;             // OpenPTK
      AttributeIF attrQuestions = null; // OpenPTK
      AttributeIF attrAnswers = null;   // OpenPTK

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      inputCancel = request.getParameter(PORTLET_INPUT_CANCEL);

      // was cancel button pressed ? YES if not NULL

      if (inputCancel == null)
      {
         uniqueId = request.getParameter(PORTLET_ATTR_UNIQUEID);

         if (uniqueId != null && uniqueId.length() > 0)
         {
            questions = _forgotPwdQuestions.toArray(new String[_forgotPwdQuestions.size()]);
            attrQuestions = new Attribute(PORTLET_ATTR_FORGOTQUESTIONS, questions);

            /*
             * The Portlet request saved the users input (answers) for each
             * question as a unique parameter in the request.
             *
             * answersinput.0 = Chicago
             * answersinput.1 = Smith
             * answersinput.2 = Spot
             *
             * Get each request parameter and save it as a Property in the
             * Element using only the integer as the prop name
             */

            listAnswers = new LinkedList<String>();
            while (!bDone)
            {
               answerKey = PORTLET_ATTR_ANSWERSINPUT + "." + iAnswerCnt;
               answerVal = null;
               answerVal = request.getParameter(answerKey);

               if (answerVal != null)
               {
                  listAnswers.add(answerVal.trim());
                  iAnswerCnt++;
               }
               else
               {
                  bDone = true;
               }
            }

            answers = listAnswers.toArray(new String[listAnswers.size()]);
            attrAnswers = new Attribute(PORTLET_ATTR_FORGOTANSWERS, answers);

            input = new Input();
            input.setUniqueId(uniqueId);
            input.setProperty(StructureIF.NAME_MODE, StructureIF.NAME_ANSWERS);
            input.addAttribute(attrQuestions);
            input.addAttribute(attrAnswers);

            output = this.execPasswordForgot(input);

            if (output != null)
            {
               if (output.getState() == State.SUCCESS)
               {
                  state.setPageView(PORTLET_JSP_VIEW_CHANGEPWD);
               }
               else
               {
                  this.setStateError(PORTLET_MSG_WRONGANSWERS, state);
                  state.setPageView(PORTLET_JSP_VIEW_QUESTIONS);
               }
            }
            else
            {
               this.setStateError(PORTLET_MSG_OUTPUT_NULL, state);
            }
         }
         else
         {
            this.setStateError(PORTLET_MSG_UNIQUEID_NULL, state);
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
   private void actionChangePassword(ActionRequest request,
      ActionResponse response,
      PortletState state)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":actionChangePassword(): ";
      String inputCancel = null;
      String newPwd = null;
      String confirmPwd = null;
      String uniqueId = null;
      Input input = null;   // OpenPTK
      Output output = null; // OpenPTK

      if (this.isDebug())
      {
         this.logDebug(METHOD_NAME + "ENTER");
      }

      inputCancel = request.getParameter(PORTLET_INPUT_CANCEL);

      // was cancel button pressed ? YES if not NULL

      if (inputCancel == null)
      {
         uniqueId = request.getParameter(PORTLET_ATTR_UNIQUEID);

         if (uniqueId == null)
         {
            uniqueId = "";
         }

         if (uniqueId.length() > 0)
         {

            newPwd = request.getParameter(PORTLET_INPUT_NEWPWD);
            confirmPwd = request.getParameter(PORTLET_INPUT_CONFIRMPWD);

            if (this.isDebug())
            {
               this.logDebug(METHOD_NAME + ": " +
                  PORTLET_ATTR_UNIQUEID + "=" + uniqueId + ", " +
                  PORTLET_INPUT_NEWPWD + "=" + newPwd + ", " +
                  PORTLET_INPUT_CONFIRMPWD + "=" + confirmPwd);
            }

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

                  input.setUniqueId(uniqueId);
                  input.setProperty(StructureIF.NAME_MODE, StructureIF.NAME_CHANGE);
                  input.addAttribute(PORTLET_ATTR_PASSWORD, newPwd);

                  output = this.execPasswordForgot(input);

                  state.setPageView(PORTLET_JSP_VIEW_RESULTS);

                  if (this.isDebug())
                  {
                     this.logDebug(METHOD_NAME + output.getStatus());
                  }
               }
            }
         }
         else
         {
            this.setStateError(PORTLET_MSG_UNIQUEID_NULL, state);
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
   private void getForgotQuestionsAsList(ElementIF elem)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getForgotQuestionsAsList(): ";
      boolean bMultivalued = false;
      AttributeIF attr = null;
      String[] strArray = null;

      attr = elem.getAttribute(PORTLET_ATTR_FORGOTQUESTIONS);

      _forgotPwdQuestions.clear();

      if (attr != null)
      {
         bMultivalued = attr.isMultivalued();
         switch (attr.getType())
         {
            case STRING:
               if (bMultivalued)
               {
                  strArray = (String[]) attr.getValue();
                  for (int i = 0; i < strArray.length; i++)
                  {
                     _forgotPwdQuestions.add(strArray[i]);
                  }
               }
               else
               {
                  _forgotPwdQuestions.add((String) attr.getValue());
               }
               break;
            default:
               break;
         }
      }
      else
      {
         this.logError(METHOD_NAME + "Attribute '" + PORTLET_ATTR_FORGOTQUESTIONS +
            "' is null.");
      }

      return;
   }
}
