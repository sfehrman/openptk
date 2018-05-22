/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009-2010 Sun Microsystems, Inc.
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

import org.openptk.api.State;
import org.openptk.engine.EngineIF;
import org.openptk.exception.AuthenticationException;

//===================================================================
public class TrustedClientAuthenticator extends Authenticator
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public TrustedClientAuthenticator()
   //----------------------------------------------------------------
   {
      super();
      _type = AuthenticatorType.TOKEN;
      return;
   }

   /**
    * @param principal
    * @return
    * @throws AuthenticationException
    */
   //----------------------------------------------------------------
   @Override
   public boolean doAuthenticate(final PrincipalIF principal) throws AuthenticationException
   //----------------------------------------------------------------
   {
      boolean isAuthen = false;
      String METHOD_NAME = CLASS_NAME + ":doAuthenticate(): ";
      String princId = null;

      /*
       * Check the Configuration and Context, get a Subject
       */

      if (_configuration == null)
      {
         this.handleError(METHOD_NAME + "Configuration is null");
      }

      try
      {
         princId = this.getCredentialValue(principal,
            _configuration.getProperty(EngineIF.PROP_AUTH_TOKEN_TOKENPARAM));
      }
      catch (AuthenticationException authEx)
      {
         principal.setState(State.NOTAUTHENTICATED);
         principal.setStatus("client credential validation failed" + authEx.getMessage());
      }


      if (princId != null)
      {
         if (this.setPrincipalUniqueId(princId, principal))
         {
            isAuthen = true;
         }
         else
         {
            isAuthen = false;
            principal.setState(State.NOTAUTHENTICATED);
            principal.setStatus("Authentication Failed");
         }
      }
      else
      {
         principal.setState(State.NOTAUTHENTICATED);
         principal.setStatus("Authentication Failed");
      }

      return isAuthen;
   }
}
