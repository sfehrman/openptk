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

import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.State;
import org.openptk.common.Operation;
import org.openptk.definition.DefinitionIF;
import org.openptk.definition.SubjectIF;
import org.openptk.engine.EngineIF;
import org.openptk.exception.AuthenticationException;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.ProvisionException;
import org.openptk.logging.Logger;

//===================================================================
public class IdPassServiceAuthenticator extends IdPassAuthenticator
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public IdPassServiceAuthenticator()
   //----------------------------------------------------------------
   {
      super();
      _type = AuthenticatorType.IDPASS;
      return;
   }

   /**
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
      String pwdAttrName = null;
      String princId = null;
      String password = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;

      /*
       * Check the Configuration and Principal and Context
       */

      if (_configuration == null)
      {
         this.handleError(METHOD_NAME + "Configuration is null");
      }

      if (principal == null)
      {
         this.handleError(METHOD_NAME + "Principal is null");
      }

      if (_context == null)
      {
         this.handleError(METHOD_NAME + "Context is null");
      }

      if ( _context.getUniqueId() == null)
      {
         this.handleError(METHOD_NAME + "UniqueId (from Context) is null");
      }

      try
      {
         princId = this.getCredentialValue(principal,
            _configuration.getProperty(EngineIF.PROP_AUTH_TOKEN_USER));
         password = this.getCredentialValue(principal,
            _configuration.getProperty(EngineIF.PROP_AUTH_TOKEN_PASSWORD));
      }
      catch (AuthenticationException authEx)
      {
         principal.setState(State.NOTAUTHENTICATED);
         principal.setStatus("client credential validation failed" + authEx.getMessage());
      }

      if (princId != null && princId.length() > 0
         && password != null && password.length() > 0)
      {

         if ( _configuration.isDebug())
         {
            Logger.logInfo(METHOD_NAME + "principal='" + princId +
               "', password='" + this.mask(password) + "'");
         }
         
         try
         {
            subject = _configuration.getSubject(_context.getUniqueId().toString());
         }
         catch (ConfigurationException ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }

         pwdAttrName = _context.getDefinition().getProperty(DefinitionIF.PROP_PASSWORD_ATTR_NAME);

         if (pwdAttrName == null || pwdAttrName.length() < 1)
         {
            this.handleError(METHOD_NAME + "Property '"
               + DefinitionIF.PROP_PASSWORD_ATTR_NAME + "' for Context Definition is null");
         }

         /*
          * Create Input, execute AUTHENTICATE
          */

         input = new Input();
         input.setUniqueId(princId);
         input.addAttribute(pwdAttrName, password);

         try
         {
            output = subject.execute(Operation.AUTHENTICATE, input);
         }
         catch (ProvisionException ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }
         catch (ConfigurationException ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }

         if (output == null)
         {
            this.handleError(METHOD_NAME + "Authenticate Output is null");
         }

         principal.setState(output.getState());
         principal.setStatus(output.getStatus());

         if (principal.getState() == State.AUTHENTICATED)
         {
            isAuthen = true;
            this.setPrincipalUniqueId(princId, principal);
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
