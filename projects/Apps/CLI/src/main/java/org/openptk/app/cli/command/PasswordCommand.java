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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.openptk.api.Input;
import org.openptk.api.Opcode;
import org.openptk.api.Output;
import org.openptk.exception.AuthenticationException;
import org.openptk.exception.ConnectionException;

/**
 *
 * @author Tery Sigle
 */
public class PasswordCommand extends Command implements CommandIF
{
   private String _userId = null;
   private String _currentPassword = null;
   private String _newPassword = null;

   /**
    * @throws CommandException
    */
   public void execute() throws CommandException
   {
      _userId = this.getEnv().getUserId();

      try
      {
         this.getEnv().createConnection(_userId, _currentPassword);
      }
      catch (ConnectionException ex)
      {
         handleError("Could not establish a connection to the server");
         return;
      }
      catch (AuthenticationException ex)
      {
         handleError("Invalid userid / password");
         return;
      }

      try
      {
         Input input = null;
         Output output = null;

         input = new Input();

         input.setUniqueId(_userId);
         input.addAttribute("password", _newPassword);

         output = this.getEnv().getConnection().execute(Opcode.PWDCHANGE, input);

         if (output != null)
         {

            switch (output.getState())
            {
               case SUCCESS:
                  _screen.println("Successfully Changed Password!");
                  break;
               case NOTEXIST:
                  _screen.println("Failed: User doesn't exist.");
                  break;
               case FAILED:
                  _screen.println("Failed: Unable to change password");
                  break;
               case ERROR:
                  _screen.println("Failed: CONTEXT '" + this.getEnv().getContextId() + "' not available.");
                  break;
               default:
                  _screen.println("Failed " + output.getState());
            }
         }
         
      }
      catch (ConnectionException ex)
      {
         handleError("Unable to change password");
      }
      
      showTiming();
      
      return;
   }

   /**
    * @param args
    * @throws CommandException
    * @throws NoSuchElementException
    */
   @Override
   public void parseArgs(List<String> args) throws CommandException, NoSuchElementException
   {
      Iterator <String>iter = null;
      String confirmPassword = null;

      _userId = this.getEnv().getUserId();

      if (_userId == null) {
         throw new CommandException("Must login first before changing password.");
      }

      _screen.println("Changing password for " + _userId);

      _currentPassword = this.getConsole().getPassword("Current Password: ");

      _newPassword = this.getConsole().getPassword("    New Password: ");
      confirmPassword = this.getConsole().getPassword("         Confirm: ");

      // Verify that the new password matches the confirmed password
      if (!_newPassword.equals(confirmPassword)) {
         throw new CommandException("New password not confirmed.  Try Again.");
      }

      return;
   }
}
