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
import org.openptk.exception.AuthenticationException;

import org.openptk.exception.ConnectionException;
import org.openptk.util.Timer;

/**
 *
 * @author Tery Sigle
 */
public class LoginCommand extends Command implements CommandIF
{
   private String _userId = null;
   private String _password = null;

   /**
    * @throws CommandException
    */
   public void execute() throws CommandException
   {
      try
      {
         /*
         * Start the timer before the login attempt
         */
         Timer.startTimer("Login");

         getEnv().closeConnection();
         
         getEnv().createConnection(_userId, _password);

         _screen.println("Login Successful");
         _screen.println("");
         _screen.println("Using Context: " + this.getEnv().getContextId());
      }
      catch (ConnectionException ex)
      {
         handleError("Could not establish a connection to the server (" + ex.getMessage() + ")");
      }
      catch (AuthenticationException ex)
      {
         handleError("Invalid userid / password");
      }
      finally
      {
         _commandTiming = Timer.stopTimer("Login");
         showTiming();
      }

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
      Iterator<String> iter = null;

      iter = args.iterator();

      iter.next(); // Pop off the command

      // Iterate through each of the command arguments
      if (iter.hasNext())
      {
         _userId = iter.next();

         if (_userId != null && _userId.length() > 0)
         {
            _password = this.getConsole().getPassword();
         }
      }
      return;
   }
}
