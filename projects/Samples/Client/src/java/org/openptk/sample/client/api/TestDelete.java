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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.sample.client.api;

import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.Opcode;
import org.openptk.connection.ConnectionIF;
import org.openptk.connection.Setup;
import org.openptk.connection.SetupIF;
import org.openptk.logging.Logger;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class TestDelete
//===================================================================
{

   /**
    * @param args the command line arguments
    */
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      TestDelete test = new TestDelete();

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
      String userId = null;
      String userPwd = null;
      SetupIF setup = null;
      ConnectionIF connection = null;
      Input input = null;
      Output output = null;

      setup = new Setup("openptk_client");

      userId = "ja1324"; // "ja1324"
      userPwd = "Passw0rd"; // "Passw0rd"
      connection = setup.getConnection(userId, userPwd);

      input = new Input();
      input.setUniqueId("capi");

      output = connection.execute(Opcode.DELETE, input);

      if (output != null)
      {
         System.out.println("Output status: " + output.getStatus());
      }

      connection.close();

      return;
   }
}
