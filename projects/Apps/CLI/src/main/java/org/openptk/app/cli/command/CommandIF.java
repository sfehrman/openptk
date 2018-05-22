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

import java.io.PrintWriter;
import java.util.List;
import java.util.NoSuchElementException;

import org.openptk.app.cli.PTKConsole;
import org.openptk.app.cli.PTKEnvironment;

/**
 *
 * @author Terry Sigle
 */
public interface CommandIF
{

   public static final String EOL = "\n";

   public enum Command
   {

      exit,
      help,
      quit,
      set,      // Deprecated.  Replaced by env command
      show,     // Deprecated.  Replaced by env command
      env,
      stats,
      login,
      logout,
      context,
      create,
      delete,
      password,
      read,
      search,
      update,
   }

   /**
    * @throws CommandException
    */
   public void execute() throws CommandException;

   /**
    * @param cmd
    */
   public void setCommand(Command cmd);

   /**
    * @param args
    * @throws CommandException
    * @throws NoSuchElementException
    */
   public void parseArgs(List<String> args) throws CommandException, NoSuchElementException;

   /**
    * @param env
    */
   public void setEnv(PTKEnvironment env);

   /**
    * @param screen
    */
   public void setScreen(PrintWriter screen);

   /**
    * @param console
    */
   public void setConsole(PTKConsole console);

   /**
    * @return
    */
   public Command getCommand();

   /**
    * @return
    */
   public PTKEnvironment getEnv();

   /**
    * @return
    */
   public void showTiming();
}
