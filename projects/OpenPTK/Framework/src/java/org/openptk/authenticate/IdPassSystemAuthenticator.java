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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.authenticate;

import org.openptk.api.State;
import org.openptk.engine.EngineIF;
import org.openptk.exception.AuthenticationException;

/**
 *
 * @author Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public class IdPassSystemAuthenticator extends IdPassAuthenticator
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public IdPassSystemAuthenticator()
   //----------------------------------------------------------------
   {
      super();
      _type = AuthenticatorType.IDPASS;
      return;
   }


   /**
    * Authenticate the principal.
    * @param principal
    * @return boolean true if authenticated
    * @throws AuthenticationException
    */
   //----------------------------------------------------------------
   @Override
   public boolean doAuthenticate(final PrincipalIF principal) throws AuthenticationException
   //----------------------------------------------------------------
   {
      boolean isAuthen = false;
      String METHOD_NAME = CLASS_NAME + ":doAuthenticate(): ";
      String userId = null;
      String password = null;

      /*
       * Check the Configuration
       */

      if (_configuration == null)
      {
         this.handleError(METHOD_NAME + "Configuration is null");
      }

      if ( principal == null)
      {
         this.handleError(METHOD_NAME + "Principal is null");
      }
      
      try
      {
         userId = getCredentialValue(principal, _configuration.getProperty(EngineIF.PROP_AUTH_TOKEN_USER));
         password = getCredentialValue(principal, _configuration.getProperty(EngineIF.PROP_AUTH_TOKEN_PASSWORD));
      }
      catch (AuthenticationException authEx)
      {
         principal.setState(State.NOTAUTHENTICATED);
         principal.setStatus("client credential validation failed" + authEx.getMessage());
      }

      if (userId != null && userId.length() > 0
         && password != null && password.length() > 0)
      {
         /*
          * execute AUTHENTICATE
          */

         try
         {
            isAuthen = this.validateUserPass(userId, password);
         }
         catch (Exception ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }

         if (isAuthen)
         {
            principal.setState(State.AUTHENTICATED);
            principal.setStatus("Authentication Successful");
         }
         else
         {
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

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   /**
    * @param userId
    * @param password
    * @return
    */
   //----------------------------------------------------------------
   private Boolean validateUserPass(final String userId, final String password)
   //----------------------------------------------------------------
   {
      Boolean isValid = false;
      String adminuser = null;
      String adminpass = null;

      adminuser = _configuration.getProperty(EngineIF.PROP_SERVER_CONFIG_USERID);
      adminpass = _configuration.getProperty(EngineIF.PROP_SERVER_CONFIG_PASSWORD);

      // Validate the credentials
      if ((userId.equals(adminuser)) && (password.equals(adminpass)))
      {
         isValid = true;
      }
      return isValid;
   }
}
