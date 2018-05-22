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

import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.Query;
import org.openptk.api.QueryBuilderIF;
import org.openptk.api.State;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.common.Operation;
import org.openptk.config.Configuration;
import org.openptk.context.ContextIF;
import org.openptk.context.ContextQueryBuilder;
import org.openptk.crypto.CryptoIF;
import org.openptk.crypto.Encryptor;
import org.openptk.definition.SubjectIF;
import org.openptk.engine.EngineIF;
import org.openptk.exception.AuthenticationException;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.CryptoException;
import org.openptk.exception.ProvisionException;
import org.openptk.logging.Logger;
import org.openptk.session.SessionType;

//===================================================================
public abstract class Authenticator extends Component implements AuthenticatorIF
//===================================================================
{
   protected Configuration _configuration = null;
   protected ContextIF _context = null;
   protected AuthenticatorType _type = null;
   protected SessionType _authLevel = null;
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String MASKED_VALUE = "********";

   //----------------------------------------------------------------
   public Authenticator()
   //----------------------------------------------------------------
   {
      super();

      this.setCategory(Category.AUTHENTICATOR);
      this.setState(State.INVALID);
      this.setStatus("Context is null");

      return;
   }

   /**
    * Authenticate the Principal.
    * @param principal
    * @return boolean TRUE is successfully authenticated
    * @throws AuthenticationException
    */
   //----------------------------------------------------------------
   @Override
   public boolean authenticate(final PrincipalIF principal) throws AuthenticationException
   //----------------------------------------------------------------
   {
      boolean isValid = false;
      boolean isAuthen = false;
      String METHOD_NAME = CLASS_NAME + ":authenticate(): ";
      Object uniqueId = null;

      if (principal == null)
      {
         this.handleError(METHOD_NAME + "Principal is null");
      }

      if (_configuration.isDebug())
      {
         Logger.logInfo(METHOD_NAME + principal.toString());
      }

      principal.setState(State.NOTAUTHENTICATED);

      if (_context != null && _context.getUniqueId() != null)
      {
         principal.setContextId(_context.getUniqueId().toString());
      }

      isValid = this.validateClientCredential(principal);

      if (isValid)
      {
         isAuthen = this.doAuthenticate(principal);
      }

      if (_configuration.isDebug())
      {
         uniqueId = principal.getUniqueId();
         Logger.logInfo(METHOD_NAME + "principal.uniqueId='"
            + (uniqueId != null ? uniqueId.toString() : "(null)") + "', valid=" + isValid);
      }

      return isAuthen;
   }

   /**
    * Authenticate the principal.
    * @param principal
    * @return boolean
    * @throws AuthenticationException
    */
   //----------------------------------------------------------------
   protected abstract boolean doAuthenticate(final PrincipalIF principal) throws AuthenticationException;
   //----------------------------------------------------------------

   /**
    * Gets the Authenticator type (enumeration).
    * @return AuthenticatorType enumeration
    */
   //----------------------------------------------------------------
   @Override
   public final AuthenticatorType getType()
   //----------------------------------------------------------------
   {
      return _type;
   }

   /**
    * Set the Level (SessionType enumeration).
    * @param sessionType
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setLevel(final SessionType sessionType)
   //----------------------------------------------------------------
   {
      _authLevel = sessionType;
      if (sessionType != null)
      {
         this.setState(State.READY);
         this.setStatus("Auth Level is set");
      }
      else
      {
         this.setState(State.INVALID);
         this.setStatus("Auth Level is null");
      }
      return;
   }

   /**
    * Set the Context.
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final SessionType getLevel()
   //----------------------------------------------------------------
   {
      return _authLevel;
   }

   /**
    * Set the OpenPTK Configuration object.
    * @param configuration
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setConfiguration(final Configuration configuration)
   //----------------------------------------------------------------
   {
      _configuration = configuration;
      return;
   }

   /**
    * Get the OpenPTK Configuration object.
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Configuration getConfiguration()
   //----------------------------------------------------------------
   {
      return _configuration;
   }

   /**
    * Set the Context.
    * @param context
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setContext(final ContextIF context)
   //----------------------------------------------------------------
   {
      _context = context;
      if (context != null)
      {
         this.setState(State.READY);
         this.setStatus("Context is set");
      }
      else
      {
         this.setState(State.INVALID);
         this.setStatus("Context is null");
      }
      return;
   }

   /**
    * Set the Context.
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final ContextIF getContext()
   //----------------------------------------------------------------
   {
      return _context;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   /**
    * Get credential value for the specified credential name.
    * @param principal
    * @param credname
    * @return string the credential value.
    * @throws AuthenticationException
    */
   //----------------------------------------------------------------
   @SuppressWarnings("cast")
   protected String getCredentialValue(final PrincipalIF principal, final String credname) throws AuthenticationException
   //----------------------------------------------------------------
   {
      String clientId = null;
      String credvalue = null;
      String METHOD_NAME = CLASS_NAME + ":getCredentialValue(): ";
      CryptoIF crypto = null;

      if (principal == null)
      {
         this.handleError(METHOD_NAME + "Principal is null");
      }

      if (credname == null || credname.length() < 1)
      {
         this.handleError(METHOD_NAME + "credname is null/empty");
      }

      /*
       * Get the ClientId
       */

      clientId = (String) principal.getCredential(_configuration.getProperty(EngineIF.PROP_AUTH_TOKEN_CLIENT));

      if (clientId == null || clientId.length() < 1)
      {
         this.handleError(METHOD_NAME + "clientId is null/empty");
      }

      credvalue = (String) principal.getCredential(credname);

      if (_configuration.isDebug())
      {
         if (credname.equalsIgnoreCase("password"))
         {
            Logger.logInfo(METHOD_NAME + "credname='" + credname + "', "
               + "credvalue='" + this.mask(credvalue) + "', "
               + "clientId='" + clientId + "'");
         }
         else
         {
            Logger.logInfo(METHOD_NAME + "credname='" + credname + "', "
               + "credvalue='" + (credvalue != null ? credvalue : "(null)") + "', "
               + "clientId='" + clientId + "'");
         }
      }

      /*
       * Get the crypto object for this clientId
       */

      try
      {
         crypto = Encryptor.getCrypto(clientId);
      }
      catch (CryptoException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      /*
       * If the crypto is not null, attempt to decrypt it
       */
      if (crypto != null)
      {

         if (credvalue != null && credvalue.length() > 0)
         {
            try
            {
               credvalue = crypto.decrypt(credvalue);
            }
            catch (CryptoException ex)
            {
               credvalue = null;
               this.handleError(METHOD_NAME + "Problem decrypting client credential value: " + ex.getMessage());
            }
         }
         else
         {
            principal.setState(State.NOTAUTHENTICATED);
            principal.setStatus("Authentication Failed");
         }
      }

      return credvalue;
   }

   /**
    * Set the user id inside the principal object.
    * @param userId
    * @param principal
    * @return boolean
    * @throws AuthenticationException
    */
   //----------------------------------------------------------------
   protected boolean setPrincipalUniqueId(final String userId, final PrincipalIF principal) throws AuthenticationException
   //----------------------------------------------------------------
   {
      boolean isValid = false;
      Object obj = null;
      String METHOD_NAME = CLASS_NAME + ":setPrincipalUniqueId(): ";
      String uniqueId = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;
      ElementIF result = null;
      Query query = null;
      QueryBuilderIF qbldr = null;

      if (_context == null)
      {
         this.handleError(METHOD_NAME + "Context is null");
      }

      obj = _context.getUniqueId();
      if (obj == null)
      {
         this.handleError(METHOD_NAME + "UniqueId (from Context) is null");
      }

      if (obj instanceof String)
      {
         uniqueId = (String) obj;
      }
      else
      {
         this.handleError(METHOD_NAME + "UniqueId (from Context) must be a String");
      }

      try
      {
         subject = _configuration.getSubject(uniqueId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      /*
       * Get Default Query, Create Input, execute Search
       */

      try
      {
         qbldr = new ContextQueryBuilder(_context);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + "Unable to create QueryBuilder");
      }
      if (qbldr == null)
      {
         this.handleError(METHOD_NAME + "QueryBuilder is null");
      }

      query = qbldr.build(userId);
      input = new Input();
      input.setQuery(query);

      try
      {
         output = subject.execute(Operation.SEARCH, input);
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
         this.handleError(METHOD_NAME + "Search Output is null, Query='" + query.toString() + "'");
      }

      if (output.getResultsSize() == 1)
      {
         result = output.getResults().get(0);
         if (result != null)
         {
            switch (result.getUniqueIdType())
            {
               case STRING:
                  principal.setUniqueId((String) result.getUniqueId());
                  break;
               case INTEGER:
                  principal.setUniqueId((Integer) result.getUniqueId());
                  break;
               case LONG:
                  principal.setUniqueId((Long) result.getUniqueId());
                  break;
            }
            principal.setState(State.AUTHENTICATED);
            principal.setStatus("Subject uniqueId: '" + userId + "' exists");
            isValid = true;
         }
         else
         {
            principal.setStatus("Search Output Result is null, Query='" + query.toString() + "'");
         }
      }
      else
      {
         principal.setStatus("Search Results not equal to one, Query='" + query.toString() + "'");
      }

      return isValid;
   }

   //----------------------------------------------------------------
   protected String mask(final String value)
   //----------------------------------------------------------------
   {
      String masked = null;

      masked = MASKED_VALUE;

      return (masked);
   }

   /**
    * Handle the provided error message.
    * @param msg
    * @throws AuthenticationException
    */
   //----------------------------------------------------------------
   protected void handleError(final String msg) throws AuthenticationException
   //----------------------------------------------------------------
   {
      String str = null;

      if (msg == null || msg.length() < 1)
      {
         str = "(null message)";
      }
      else
      {
         str = msg;
      }

      throw new AuthenticationException(str);
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   /**
    * Validate the principal's credentials.
    * @param principal
    * @return boolean
    * @throws AuthenticationException
    */
   //----------------------------------------------------------------
   private boolean validateClientCredential(final PrincipalIF principal) throws AuthenticationException
   //----------------------------------------------------------------
   {
      boolean isValid = false;
      String clientId = null;
      String clientCred = null;
      CryptoIF crypto = null;
      String decrypted = null;
      String METHOD_NAME = CLASS_NAME + ":validateClientCredentials(): ";

      if (principal == null)
      {
         this.handleError(METHOD_NAME + "principal is null");
      }

      /*
       * Get the ClientId
       */

      clientId = (String) principal.getCredential(_configuration.getProperty(EngineIF.PROP_AUTH_TOKEN_CLIENT));

      if (clientId == null || clientId.length() < 1)
      {
         this.handleError(METHOD_NAME + "clientId is null/empty");
      }

      if (_configuration.isDebug())
      {
         Logger.logInfo(METHOD_NAME + "clientId='" + clientId + "'");
      }

      /*
       * Get the crypto object for this clientId
       */

      try
      {
         crypto = Encryptor.getCrypto(clientId);
      }
      catch (CryptoException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      /*
       * If the crypto is not null, check the clientCredentials
       */

      if (crypto != null)
      {
         clientCred = (String) principal.getCredential(_configuration.getProperty(EngineIF.PROP_AUTH_TOKEN_CLIENT_CRED));

         if (clientCred != null && clientCred.length() > 0)
         {
            try
            {
               decrypted = crypto.decrypt(clientCred);
            }
            catch (CryptoException ex)
            {
               this.handleError(METHOD_NAME + "CryptoException, validating client: " + ex.getMessage());
            }

            if (decrypted != null && decrypted.length() > 0)
            {
               isValid = true;
            }
         }
         else
         {
            this.handleError(METHOD_NAME + "CryptoException missing client credentials");
         }
      }
      else
      {
         isValid = true;
      }

      return isValid;
   }
}
