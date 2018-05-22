/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2010 Sun Microsystems, Inc.
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
package org.openptk.definition;

import org.openptk.common.ComponentIF;

//===================================================================
public interface DefinitionIF extends ComponentIF
//===================================================================
{
   public static final String PROP_PASSWORD_ATTR_NAME = "definition.password";
   public static final String PROP_AUTHENID_ATTR_NAME = "definition.authenid";
   public static final String PROP_CHALLENGE_QUESTIONS_ATTR_NAME = "definition.challenge.questions";
   public static final String PROP_CHALLENGE_ANSWERS_ATTR_NAME = "definition.challenge.answers";
   public static final String PROP_CHALLENGE_VALUES_ATTR_NAME = "definition.challenge.values";
   public static final String ATTR_PWD_FORGOT_QUESTIONS = "forgottenPasswordQuestions";
   public static final String ATTR_PWD_FORGOT_ANSWERS = "forgottenPasswordAnswers";
   public static final String ATTR_PWD_FORGOT_VALUES = "forgottenPasswordValues";

   /**
    * @return
    */
   @Override
   public DefinitionIF copy();

   /**
    * @param classname
    */
   public void setDefinitionClassName(String classname);

   /**
    * @return
    */
   public String getDefinitionClassName();

}
