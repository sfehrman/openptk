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

package org.openptk.app.cli.command;

import org.openptk.connection.ConnectionIF;
import org.openptk.exception.ConnectionException;

/**
 *
 * @author Tery Sigle
 */
public class LogoutCommand extends Command implements CommandIF
{
   /**
    * @throws CommandException
    */
   public void execute() throws CommandException
   {
      try
      {
         ConnectionIF conn = null;

         conn = this.getEnv().getConnection();

         if (conn != null)
         {
            conn.close();
         }
         else
         {
            // Already logged out
            return;
         }
      }
      catch (ConnectionException ex)
      {
         handleError(ex.getMessage());
      }

      this.getEnv().closeConnection();

      _screen.println("Logout Successful");

      return;
   }
}
