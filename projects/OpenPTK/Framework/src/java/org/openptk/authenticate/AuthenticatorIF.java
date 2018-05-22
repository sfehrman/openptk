/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2008 Sun Microsystems, Inc.
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
package org.openptk.authenticate;

import org.openptk.common.ComponentIF;
import org.openptk.config.Configuration;
import org.openptk.context.ContextIF;
import org.openptk.exception.AuthenticationException;
import org.openptk.session.SessionType;

//===================================================================
public interface AuthenticatorIF extends ComponentIF
//===================================================================
{

   /**
    * Authenticate the Principal.
    * @param principal
    * @return boolean TRUE is successfully authenticated
    * @throws AuthenticationException
    */
   public boolean authenticate(PrincipalIF principal) throws AuthenticationException;

   /**
    * Gets the Authenticator type (enumeration).
    * @return AuthenticatorType enumeration
    */
   public AuthenticatorType getType();

   /**
    * Set the Level (SessionType enumeration).
    * @param sessionType
    */
   public void setLevel(SessionType sessionType);

   /**
    * Get the Level (SessionType enumeration).
    * @return
    */
   public SessionType getLevel();

   /**
    * Set the OpenPTK Configuration object.
    * @param configuration
    */
   public void setConfiguration(Configuration configuration);

   /**
    * Get the OpenPTK Configuration object.
    * @return
    */
   public Configuration getConfiguration();

   /**
    * Set the Context.
    * @param context
    */
   public void setContext(ContextIF context);

   /**
    * Set the Context.
    * @return
    */
   public ContextIF getContext();
}
