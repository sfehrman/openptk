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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.definition.functions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.openptk.common.AttrCategory;
import org.openptk.common.AttrIF;
import org.openptk.common.BasicAttr;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.exception.FunctionException;

//===================================================================
public class ForgottenPassword extends Function implements FunctionIF
//===================================================================
{
   private static final int MIN_LENGTH = 3;
   private static final String ARGUMENT_DATA = "data";
   private static final String ARGUMENT_ANSWERS = "answers";
   private static final String ARGUMENT_QUESTIONS = "questions";
   private static final String ARGUMENT_DELIM_INNER = "innerdelimiter";
   private static final String ARGUMENT_DELIM_OUTER = "outerdelimiter";
   private static final String ARGUMENT_MIN_LENGTH = "minlength";
   private static final String ARGUMENT_DEFAULT = "default";
   private String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param context
    * @param key
    * @param mode
    * @param oper
    * @param arguments
    * @param attributes
    * @throws FunctionException
    */
   //----------------------------------------------------------------
   @Override
   public synchronized void execute(ContextIF context, String key, TaskMode mode,
      Operation oper, List<ArgumentIF> arguments, Map<String, AttrIF> attributes)
      throws FunctionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      AttrIF attribute = null;

      //Get the Attribute we are transforming

      if (key != null)
      {
         attribute = attributes.get(key);
      }
      else
      {
         this.handleError(METHOD_NAME + "Key is Null");
      }

      /*
       * Get the execute object and associated arguments for this function
       */

      if (attribute != null)
      {
         if (arguments == null)
         {
            this.handleError(METHOD_NAME + "No Function arguments");
         }

         switch (oper)
         {
            case READ:
            {
               if (mode == TaskMode.TOFRAMEWORK)
               {
                  this.decodeData(key, attributes, arguments);
               }
               break;
            }
            case CREATE:
            case UPDATE:
            {
               this.encodeData(key, attributes, arguments);
               break;
            }
            default:
            {
               this.handleError(METHOD_NAME + "Unsupported Operation code='" + oper.toString() + "'");
            }
         }
      }
      return;
   }

   /*
    * -----------------
    * PROTECTED METHODS
    * -----------------
    */
   //----------------------------------------------------------------
   protected void encodeData(final String key,
      final Map<String, AttrIF> attributes, final List<ArgumentIF> arguments) throws FunctionException
   //----------------------------------------------------------------
   {
      /*
       * Operation = Create, Update
       * Mode = toService
       *
       * Create an encoded string that contains a multivalued set of key/value pairs
       *
       * The multivalued "questions" and "answers" attributes will be processed
       * to create the encoded String.  The "innerdelimiter" character will be used
       * to separate the key / value pairs.  Each key/value pair will be
       * separated by the outerdelimiter.
       *
       * The encoded String will be added to the Request using the "data"
       * attribute name.
       *
       * The "questions" and/or "answers" attributes will be removed from the
       * Request.
       *
       * The following arguments are used by this method:
       * Name           Type    Value
       * -----------------------------
       * questions      attribute  attribute containing the questions
       * answers        attribute  attribute containing the answers
       * data           attribute  attribute that will contain the information
       * innerdelimiter literal    character use to separate the key/value
       * outerdelimiter literal    character use to separate the pairs
       * minlength      literal    min length for the answers
       */


      int iMinLength = 0;

      String METHOD_NAME = CLASS_NAME + ":doUpdate(): ";
      String argName = null;
      String data = null;
      String strQuestionsAttrName = null;
      String strAnswersAttrName = null;
      String strInnerDelimChar = null;
      String strOuterDelimChar = null;

      String[] strQuestionsValue = null;
      String[] strAnswersValue = null;

      StringBuilder buf = new StringBuilder();

      AttrIF attrQuestions = null;
      AttrIF attrAnswers = null;
      AttrIF attrData = null;
      ArgumentIF argument = null;

      Iterator<ArgumentIF> iter = null;

      iter = arguments.iterator();
      while (iter.hasNext())
      {
         argument = iter.next();
         if (argument != null)
         {
            argName = argument.getName();

            if (argName.equalsIgnoreCase(ForgottenPassword.ARGUMENT_QUESTIONS))
            {
               strQuestionsAttrName = argument.getValue();
            }
            else if (argName.equalsIgnoreCase(ForgottenPassword.ARGUMENT_ANSWERS))
            {
               strAnswersAttrName = argument.getValue();
            }
            else if (argName.equalsIgnoreCase(ForgottenPassword.ARGUMENT_DATA))
            {
               data = argument.getValue();
            }
            else if (argName.equalsIgnoreCase(ForgottenPassword.ARGUMENT_DELIM_INNER))
            {
               strInnerDelimChar = argument.getValue();
            }
            else if (argName.equalsIgnoreCase(ForgottenPassword.ARGUMENT_DELIM_OUTER))
            {
               strOuterDelimChar = argument.getValue();
            }
            else if (argName.equalsIgnoreCase(ForgottenPassword.ARGUMENT_MIN_LENGTH))
            {
               try
               {
                  iMinLength = Integer.parseInt(argument.getValue());
               }
               catch (NumberFormatException ex)
               {
                  iMinLength = ForgottenPassword.MIN_LENGTH;
               }
            }
         }
      }

      /*
       * Check the required values
       */

      if (strQuestionsAttrName == null || strQuestionsAttrName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Argument '"
            + ForgottenPassword.ARGUMENT_QUESTIONS
            + "' not found for Attribute '" + key + "'");
      }
      if (strAnswersAttrName == null || strAnswersAttrName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Argument '"
            + ForgottenPassword.ARGUMENT_ANSWERS
            + "' not found for Attribute '" + key + "'");
      }
      if (data == null || data.length() < 1)
      {
         this.handleError(METHOD_NAME + "Argument '"
            + ForgottenPassword.ARGUMENT_DATA
            + "' not found for Attribute '" + key + "'");
      }
      if (strInnerDelimChar == null || strInnerDelimChar.length() < 1)
      {
         this.handleError(METHOD_NAME + "Argument '"
            + ForgottenPassword.ARGUMENT_DELIM_INNER
            + "' not found for Attribute '" + key + "'");
      }
      if (strOuterDelimChar == null || strOuterDelimChar.length() < 1)
      {
         this.handleError(METHOD_NAME + "Argument '"
            + ForgottenPassword.ARGUMENT_DELIM_OUTER
            + "' not found for Attribute '" + key + "'");
      }

      /*
       * See if the "data" attribute already exists
       * It may have been created by the "other" attribute (question/answer)
       */

      if (attributes.containsKey(data))
      {
         attrData = attributes.get(data);
      }
      if (attrData != null)
      {
         /*
          * Get the questions and answers (multi-valued) make sure they
          * have the same number of values
          */

         attrQuestions = attributes.get(strQuestionsAttrName);
         if (attrQuestions != null)
         {
            attrAnswers = attributes.get(strAnswersAttrName);
            if (attrAnswers != null)
            {

               if (attrQuestions.isMultivalued())
               {
                  strQuestionsValue = (String[]) attrQuestions.getValue();
               }
               else
               {
                  strQuestionsValue = new String[1];
                  strQuestionsValue[0] = (String) attrQuestions.getValue();
               }

               if (attrAnswers.isMultivalued())
               {
                  strAnswersValue = (String[]) attrAnswers.getValue();
               }
               else
               {
                  strAnswersValue = new String[1];
                  strAnswersValue[0] = (String) attrAnswers.getValue();
               }


               if (strQuestionsValue.length < 1)
               {
                  this.handleError(METHOD_NAME + "No questions were provided");
               }
               if (strAnswersValue.length < 1)
               {
                  this.handleError(METHOD_NAME + "No answers were provided");
               }
               if (strQuestionsValue.length != strAnswersValue.length)
               {
                  this.handleError(METHOD_NAME + "questions/answers qty do not match: "
                     + "Questions=" + strQuestionsValue.length
                     + ", Answers=" + strAnswersValue.length);
               }

               /*
                * Do all of the answers have min length
                */

               for (int i = 0; i < strAnswersValue.length; i++)
               {
                  if (strAnswersValue[i].length() < iMinLength)
                  {
                     this.handleError(METHOD_NAME + "Question '"
                        + strQuestionsValue[i] + "' has an answer that is too short: '"
                        + strAnswersValue[i] + "'");
                  }
               }
            }
            else
            {
               this.handleError(METHOD_NAME + "Required Attribute '"
                  + strAnswersAttrName + "' is NULL");
            }
         }
         else
         {
            this.handleError(METHOD_NAME + "Required Attribute '"
               + strQuestionsAttrName + "' is NULL");
         }

         /*
          * Build the delimited String
          */

         for (int i = 0; i < strAnswersValue.length; i++)
         {
            if (i != 0) // add an outer delimiter
            {
               buf.append(strOuterDelimChar);
            }
            buf.append(strQuestionsValue[i]).append(strInnerDelimChar).append(strAnswersValue[i]);
         }

         /*
          * Create / set the "data" attribute
          */

         this.include(data, buf.toString(), attributes);
      }
      return;
   }

   //----------------------------------------------------------------
   protected void decodeData(final String key,
      final Map<String, AttrIF> attributes, final List<ArgumentIF> arguments) throws FunctionException
   //----------------------------------------------------------------
   {
      /*
       * Operation = Read
       * Mode = toFramework
       *
       * Convert the String that comes from the Service.
       * The String is an encoded set of key/value pairs
       * The "outerdelimiter" separates each pair
       * The "innerdelimiter" separates the key and value
       * Create a multi-value String for both the "answers" and "quesitons"
       * and apply the value(s) to "this" Attribute, defined by the provided "key"
       *
       * Remove the "data" (this) Attribute from the Response
       *
       * The following arguments are used by this method:
       * Name           Type    Value
       * -----------------------------
       * data           attribute  attribute name that has the raw encoded data
       * questions      attribute  attribute name for the Questions
       * answers        attribute  attribute name for the Answers
       * innerdelimiter literal  character use to separate the key/value
       * outerdelimiter literal  character use to separate the pairs
       * default        literal  comma delimited string of default questions
       */

      boolean hasQandA = false;
      boolean hasData = true;
      int i = 0;
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      String argName = null;
      String dataAttrName = null;
      String dataAttrValue = null;
      String questionsAttrName = null;
      String answersAttrName = null;
      String innerdelimiter = null;
      String outerdelimiter = null;
      String defaultQuestions = null;
      String token = null;
      String sKVpair = "";
      String sKey = "";
      String sVal = "";
      String[] questionsVal = null;
      String[] answersVal = null;
      StringTokenizer stOuter = null;
      StringTokenizer stInner = null;
      StringTokenizer stDef = null;
      AttrIF dataAttr = null;
      AttrIF questionAttr = null;
      AttrIF answerAttr = null;
      ArgumentIF argument = null;
      Iterator<ArgumentIF> iter = null;

      iter = arguments.iterator();
      while (iter.hasNext())
      {
         argument = iter.next();
         if (argument != null)
         {

            argName = argument.getName();

            /*
             * Get the following arguments:
             * data, questions, answers, inner, outer, default
             */

            if (argName.equalsIgnoreCase(ForgottenPassword.ARGUMENT_DATA))
            {
               dataAttrName = argument.getValue();
            }
            else if (argName.equalsIgnoreCase(ForgottenPassword.ARGUMENT_QUESTIONS))
            {
               questionsAttrName = argument.getValue();
            }
            else if (argName.equalsIgnoreCase(ForgottenPassword.ARGUMENT_ANSWERS))
            {
               answersAttrName = argument.getValue();
            }
            else if (argName.equalsIgnoreCase(ForgottenPassword.ARGUMENT_DELIM_INNER))
            {
               innerdelimiter = argument.getValue();
            }
            else if (argName.equalsIgnoreCase(ForgottenPassword.ARGUMENT_DELIM_OUTER))
            {
               outerdelimiter = argument.getValue();
            }
            else if (argName.equalsIgnoreCase(ForgottenPassword.ARGUMENT_DEFAULT))
            {
               defaultQuestions = argument.getValue();
            }
         }
      }

      /*
       * Check the required values ... obtained from the Argumenets
       */

      if (dataAttrName == null || dataAttrName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Argument '"
            + ForgottenPassword.ARGUMENT_DATA
            + "' not found for Attribute '" + key + "'");
      }

      if (questionsAttrName == null || questionsAttrName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Argument '"
            + ForgottenPassword.ARGUMENT_QUESTIONS
            + "' not found for Attribute '" + key + "'");
      }

      if (answersAttrName == null || answersAttrName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Argument '"
            + ForgottenPassword.ARGUMENT_ANSWERS
            + "' not found for Attribute '" + key + "'");
      }

      if (innerdelimiter == null || innerdelimiter.length() < 1)
      {
         this.handleError(METHOD_NAME + "Argument '"
            + ForgottenPassword.ARGUMENT_DELIM_INNER
            + "' not found for Attribute '" + key + "'");
      }

      if (outerdelimiter == null || outerdelimiter.length() < 1)
      {
         this.handleError(METHOD_NAME + "Argument '"
            + ForgottenPassword.ARGUMENT_DELIM_OUTER
            + "' not found for Attribute '" + key + "'");
      }

      if (key.equalsIgnoreCase(questionsAttrName))
      {
         /*
          * Only applies to the questions
          */

         if (defaultQuestions == null || defaultQuestions.length() < 1)
         {
            this.handleError(METHOD_NAME + "Argument '"
               + ForgottenPassword.ARGUMENT_DEFAULT
               + "' not found for Attribute '" + key + "'");
         }
      }

      /*
       * See if the "questions" and "answers" are already in the attributes
       * This function can get run for both the "questions" and "answers"
       * attribute.  The first one the runs should populate BOTH of these
       * attr/values.  If both of them exist, we will skip the processing
       */

      if (attributes.containsKey(questionsAttrName) && attributes.containsKey(answersAttrName))
      {
         questionAttr = attributes.get(questionsAttrName);
         answerAttr = attributes.get(answersAttrName);
         if (questionAttr != null && answerAttr != null)
         {
            if (questionAttr.getValue() != null && answerAttr.getValue() != null)
            {
               hasQandA = true;
            }
         }
      }

      if (!hasQandA)
      {
         /*
          * Check to see if the "data" exists ... maybe the Service doesn't
          * have a value for the "forgotData"
          */

         if (attributes.containsKey(dataAttrName))
         {
            dataAttr = attributes.get(dataAttrName);
            dataAttrValue = (String) dataAttr.getValue();

            if (dataAttrValue != null && dataAttrValue.length() > 2)
            {
               /*
                * split the delimited string into 2 attributes
                * of type string array
                *
                * Create the string arrays for the new questions and answers Attributes
                */

               stOuter = new StringTokenizer(dataAttrValue, outerdelimiter);

               if (stOuter.countTokens() > 0)
               {
                  questionsVal = new String[stOuter.countTokens()];
                  answersVal = new String[stOuter.countTokens()];

                  while (stOuter.hasMoreTokens())
                  {
                     sKVpair = stOuter.nextToken();
                     stInner = new StringTokenizer(sKVpair, innerdelimiter);
                     if (stInner.hasMoreTokens())
                     {
                        sKey = stInner.nextToken();
                        if (sKey.length() > 0)
                        {
                           // Add the sKey to questions StringArray
                           questionsVal[i] = sKey;
                           if (stInner.hasMoreTokens())
                           {
                              sVal = stInner.nextToken();
                              if (sVal.length() > 0)
                              {
                                 // Add the sVal to answers StringArray
                                 answersVal[i] = sVal;
                              }
                           }
                        }
                     }
                     i++;
                  }
               }
            }
            else
            {
               hasData = false;
            }
         }
         else
         {
            hasData = false;
         }

         if (!hasData && key.equalsIgnoreCase(questionsAttrName))
         {
            /*
             * There is no data (questions) and it's the "answers"
             * Get the default questions and create the questions/answers
             */

            stDef = new StringTokenizer(defaultQuestions, ",");

            if (stDef.countTokens() > 0)
            {
               questionsVal = new String[stDef.countTokens()];
               answersVal = new String[stDef.countTokens()];

               while (stDef.hasMoreTokens())
               {
                  token = stDef.nextToken();
                  questionsVal[i] = token;
                  answersVal[i] = "";
                  i++;
               }
            }
         }

         /*
          * include the "questions" and "answers" in the attributes
          */

         this.include(questionsAttrName, questionsVal, attributes);
         this.include(answersAttrName, answersVal, attributes);
      }
      return;
   }

   /*
    * ---------------
    * PRIVATE METHODS
    * ---------------
    */
   //----------------------------------------------------------------
   private void include(final String name, final String value, final Map<String, AttrIF> attrMap)
   //----------------------------------------------------------------
   {
      AttrIF attr = null;

      if (attrMap.containsKey(name))
      {
         attrMap.get(name).setValue(value);
      }
      else
      {
         attr = new BasicAttr(name);
         attr.setCategory(AttrCategory.FRAMEWORK);
         attr.setValue(value);
         attrMap.put(name, attr);
      }
      return;
   }

   //----------------------------------------------------------------
   private void include(final String name, final String[] value, final Map<String, AttrIF> attrMap)
   //----------------------------------------------------------------
   {
      AttrIF attr = null;

      if (attrMap.containsKey(name))
      {
         attrMap.get(name).setValue(value);
      }
      else
      {
         attr = new BasicAttr(name);
         attr.setCategory(AttrCategory.FRAMEWORK);
         attr.setValue(value);
         attrMap.put(name, attr);
      }
      return;
   }
}
