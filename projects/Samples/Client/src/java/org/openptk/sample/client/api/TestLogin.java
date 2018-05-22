/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009-2011 Sun Microsystems, Inc.
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
package org.openptk.sample.client.api;

import org.openptk.connection.ConnectionIF;
import org.openptk.connection.Setup;
import org.openptk.connection.SetupIF;
import org.openptk.logging.Logger;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class TestLogin extends TestClient
//===================================================================
{
   /**
    * @param args the command line arguments
    */
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      TestLogin test = new TestLogin();

      try
      {
         test.run();
      }
      catch (Exception ex)
      {
         Logger.logError(ex.getMessage());
      }

      return;
   }

   //----------------------------------------------------------------
   private void run() throws Exception
   //----------------------------------------------------------------
   {
      String token = null;
      String userId = null;
      String userPwd = null;
      SetupIF setup = null;
      ConnectionIF connection = null;

      setup = new Setup("openptk_client");

      userId = "ja1324";
      userPwd = "Passw0rd";

      System.out.println("======================================================");
      System.out.println("LOGGING IN: " + userId + "/" + userPwd);
      System.out.println("======================================================\n");

      connection = setup.getConnection(userId, userPwd);

      System.out.println(printConnectionInfo(connection));
      System.out.println(printContextInfo(connection));
      System.out.println(printSessionInfo(connection));

      System.out.println("======================================================");
      System.out.println("User Pass Auth - LOGGING OUT: ");
      System.out.println("======================================================\n");
      connection.close();

//      // Test the user pass authenticator (with the system user)
//      userId = "openptkconfig";
//      userPwd = "password";
//
//      System.out.println("======================================================");
//      System.out.println("LOGGING IN: " + userId + "/" + userPwd);
//      System.out.println("======================================================\n");
//
//      connection = null;
//      connection = setup.getConnection(userId, userPwd);
//
//      System.out.println(printConnectionInfo(connection));
//      System.out.println(printContextInfo(connection));
//      System.out.println(printSessionInfo(connection));
//
//      System.out.println("======================================================");
//      System.out.println("User Pass Auth - LOGGING OUT: ");
//      System.out.println("======================================================\n");
//      connection.close();

//      // Test the token authenticator (used when deployed in a trusted environment, like portlets)
//      token = "ja1324";
//
//      System.out.println("======================================================");
//      System.out.println("Token Auth - LOGGING IN: " + token );
//      System.out.println("======================================================\n");
//
//      connection = null;
//      connection = setup.getConnection(token);
//
//      System.out.println(printConnectionInfo(connection));
//      System.out.println(printContextInfo(connection));
//      System.out.println(printSessionInfo(connection));
//
//      System.out.println("======================================================");
//      System.out.println("LOGGING OUT: ");
//      System.out.println("======================================================\n");
//      connection.close();
/*
      // Test the anonymous authenticator

      System.out.println("======================================================");
      System.out.println("Anon Auth - LOGGING IN " );
      System.out.println("======================================================\n");

      connection = null;
      connection = setup.getConnection();

      System.out.println(printConnectionInfo(connection));
      System.out.println(printContextInfo(connection));
      System.out.println(printSessionInfo(connection));

      System.out.println("======================================================");
      System.out.println("LOGGING OUT: ");
      System.out.println("======================================================\n");
      connection.close();
*/
      
      return;
   }
}
