/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009 Sun Microsystems, Inc.
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
package org.openptk.sample.prov.api;

import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openptk.authenticate.AuthenticatorIF;
import org.openptk.authenticate.BasicPrincipal;
import org.openptk.authenticate.BasicCredentials;
import org.openptk.authenticate.PrincipalIF;
import org.openptk.config.Configuration;
import org.openptk.crypto.DESCrypto;
import org.openptk.exception.AuthenticationException;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.CryptoException;

//===================================================================
class testTokenAuthenticator
//===================================================================
{

   private String CONFIG = "openptk.xml";
   private String CLIENTID_TOKEN_NAME = "clientid"; //
   private String CLIENTID = "apitest"; //
   private String CLIENTCRED_TOKEN_NAME = "clientcred"; //
   private String CLIENTCRED = "FOO - THIS IS A TEST SEED STRING"; //
   private String CLIENTSECRET = "McP7NoBoPTPHrJZLfXsnDEod"; //McP7NoBoPTPHrJZLfXsnDEod  
   private String AUTHENTICATOR = "Employees-Token-LDAP"; // from CONFIG - Authenticator  
   private String UNIQUEID_TOKEN_NAME = "token"; // from Global properties
   private String UNIQUEID = "ja1324"; // valid = eapi


   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      testTokenAuthenticator test = new testTokenAuthenticator();

      try
      {
         test.run();
      }
      catch (Exception ex)
      {
         Logger.getLogger(testTokenAuthenticator.class.getName()).log(Level.SEVERE, null, ex);
      }

      return;
   }

   //----------------------------------------------------------------
   public void run() throws Exception
   //----------------------------------------------------------------
   {
      boolean isAuthen = false;
      Configuration config = null;
      AuthenticatorIF authenticator = null;
      PrincipalIF principal = null;
      BasicCredentials creds = null;
      HashMap<String, Object> authInfo;
      DESCrypto crypto = null;

      try
      {
         config = new Configuration(this.CONFIG, new Properties());
      }
      catch (ConfigurationException ex)
      {
         throw new Exception("Configuration(): " + ex.getMessage());
      }

      authenticator = config.getAuthenticator(AUTHENTICATOR);
      if (authenticator == null)
      {
         throw new Exception("Authenticator is null '" + AUTHENTICATOR + "'");
      }

      authInfo = new HashMap<String, Object>();
      authInfo.put(CLIENTID_TOKEN_NAME, CLIENTID);

      try
      {
         crypto = new DESCrypto(CLIENTSECRET);
         authInfo.put(UNIQUEID_TOKEN_NAME, crypto.encrypt(UNIQUEID));
         authInfo.put(CLIENTCRED_TOKEN_NAME, crypto.encrypt(CLIENTCRED));
      }
      catch (CryptoException ex)
      {
         System.out.println("\nEncryption of client creds problem.");
      }

      creds = new BasicCredentials(authInfo);
      principal = new BasicPrincipal(creds);
      principal.setUniqueId(UNIQUEID);

      try
      {
         isAuthen = authenticator.authenticate(principal);
      }
      catch (AuthenticationException authEx)
      {
         System.out.println("\nAuthentication exception:  " + authEx.getMessage());
      }

      System.out.println("\n" +
              "isAuthen: " + isAuthen + "\n" +
              "uniqueId: " + principal.getUniqueId().toString() + "\n" +
              "   state: " + principal.getState().toString() + "\n" +
              "  status: " + principal.getStatus() + "\n");

      return;
   }
}
