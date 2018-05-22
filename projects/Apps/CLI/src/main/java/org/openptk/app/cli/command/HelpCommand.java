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
/**
 *
 * @author Tery Sigle
 */

public class HelpCommand extends Command implements CommandIF{

   private static final String USAGE_OPTIONS =
           "The accepted values for options are:" + EOL;
   private static final String USAGE_UID =
           "  -u <UniqueId>     --  Unique ID of the user";
   private static final String USAGE_RETURN =
           "  -r <attr>[,<attr>,...]  " + EOL +
           "                    --  Attributes to return on SEARCH or READ";
   private static final String USAGE_SET_ATTRIBUTES =
           "  -a <key=val>[,<key=val>,...]  " + EOL +
           "                    --  Key/Value pairs to ADD or UPDATE";
   private static final String USAGE_QUERY =
           "  -q <attr=value>   --  Query for SEARCH (i.e. lastname=Smith)";
   private static final String HELP_USAGE =
           "For information about OpenPTK, visit: http://www.openptk.org/" + EOL +
         EOL +
         "For each command/operation below, type 'help {cmd/op}' to see more info." + EOL +
         EOL +
         "List of all OpenPTK console commands:" + EOL +
         EOL +
           "   login       Open connection to server." + EOL +
           "   logout      Close connection to server." + EOL +
           "   exit        Exit console." + EOL +
           "   help        Display this help." + EOL +
           "   quit        Quit console.  Same as exit." + EOL +
           "   env         Show/Set current environment variables/session." + EOL +
           "   stats       Show/reset statistics of current command line session" + EOL +
           EOL +
           "List of OpenPTK operations:" + EOL +
           EOL +
           "   context     List/Set available contexts." + EOL +
           "   create      Create Context Subject." + EOL +
           "   delete      Delete Context Subject." + EOL +
           "   password    Change current password." + EOL +
           "   read        Read Context Subject." + EOL +
           "   search      Search Context Subjects." + EOL +
           "   update      Update Context Subject." +
           EOL;
   private static final String CONTEXT_USAGE =
           "context [context id]" + EOL +
           EOL +
           "Prints out contexts available if no argument is set.  Otherwise," + EOL +
           "the current context will be set to the value of the 'context id'." + EOL +
           "The context value will also be persisted to the openptk.properties" + EOL +
           "file the same way that variables are persisted." + EOL +
           EOL +
           "Note: If the 'context id' is set to 'default', then the current and" + EOL +
           "      persisted contxt will reflect the default context." + EOL +
           EOL +
           "Example:" + EOL +
           "  context                    # Lists all available contexts" + EOL +
           "  context Employee-Database  # Sets current/future contexts" + EOL +
           "  context default            # Resets current/future contexts to default" + EOL;
   private static final String CREATE_USAGE =
           "create [-u uid] -a attr=value[,attr=value,...]" + EOL +
           EOL +
           "Creates a Subject in the Context." + EOL +
           EOL +
           "Example:" + EOL +
           "  create -u jsmith -a firstname=John,lastname=Smith" + EOL +
           EOL +
           USAGE_OPTIONS + EOL +
           USAGE_UID + EOL +
           USAGE_SET_ATTRIBUTES + EOL;
   private static final String DELETE_USAGE =
           "delete uid" + EOL +
           EOL +
           "Deletes a Subject for the uid from the Context." + EOL +
           EOL +
           "Example:" + EOL +
           "  delete jsmith" + EOL;
   private static final String READ_USAGE =
           "read uid" + EOL +
           EOL +
           "Reads a Subject from the Context based on uid." + EOL +
           EOL +
           "Example:" + EOL +
           "  read jsmith" + EOL;
   private static final String SEARCH_USAGE =
           "search {search string}" + EOL +
           EOL +
           "Searches for Subjects from the Context based on the search string." + EOL +
           EOL +
           "Example:" + EOL +
           "  search john smith" + EOL;
   private static final String UPDATE_USAGE =
           "update uid -a attr=value[,attr=value,...]" + EOL +
           EOL +
           "Updates a Subject in the Context." + EOL +
           EOL +
           "Examples:" + EOL +
           "  1. Following sets users title and email" + EOL +
           "     update jsmith -a title=Engineer,email=jsmith@xyz.com" + EOL +
           EOL +
           "  2. Following empties (i.e. sets to null) users title and sets email" + EOL +
           "     update jsmith -a title=,email=jsmith@xyz.com" + EOL +
           "  " + EOL +
           EOL +
           USAGE_OPTIONS + EOL +
           USAGE_SET_ATTRIBUTES + EOL;
   private static final String STATS_USAGE =
           "stats [reset]" + EOL +
           EOL +
           "Shows statistics of the current command line session." + EOL + "" +
           "The reset option will reset the current command line session statistics" + EOL +
           EOL +
           "The stats show all the statistics from the current console session" + EOL +
           "since the console started, or since stats were reset.  Only statistics" + EOL +
           "from SUCCESSful operations are captured.  Those with failures or" + EOL +
           "errors are not captured.  The reset option will reset the current" + EOL +
           "command line session statistics." + EOL +
           EOL +
           "Columns on the stats table include:" + EOL +
           EOL +
           "  Operation - Type of operations" + EOL +
           "  % Ops     - What percet of total ops make up this operations type" + EOL +
           "  # Ops     - Number of successful operations" + EOL +
           "  Avg       - Average time (in ms) of all ops" + EOL +
           "  Min       - Minimum time (in ms) of all ops" + EOL +
           "  Max       - Maximum time (in ms) of all ops"
           + EOL;


   private static final String ENV_USAGE =
           "env [name[=value]]" + EOL +
           EOL +
           "Shows and sets current environment variables and session information." + EOL +
           "An optional name/value pair (i.e. name=value) can be an optional argument" + EOL +
           "that will set that name as a vaiable with the value into both the current" + EOL +
           "session as well as persisted to the properties file.  Only a name is passed" + EOL +
           "with no value, then that name will be removed from the current session" + EOL +
           "as wekk as persisted to the properties file." + EOL +
           EOL +
           "Example:" + EOL +
           "  env                  # Prints all Environment variables and session Info" + EOL +
           "  env PRODUCT=OpenPTK  # Sets and persists a Variable 'PRODUCT' with value 'OpenPTK'" + EOL +
           "  env PRODUCT          # Removes the variable 'PRODUCT'" + EOL;
   private static final String PASSWORD_USAGE =
           "password" + EOL +
           EOL +
           "Changes the current users (requires user to login first) password." + EOL +
           "User will be asked to enter current password and then new password" + EOL +
           "along with a confirmation of the new password" + EOL +
           EOL +
           "Example:" + EOL +
           "  password" + EOL;
   private static final String LOGIN_USAGE =
           "login userid" + EOL +
           EOL +
           "Logs user into current context with userid.  User will be required to" + EOL +
           "provide a valid password as a response" + EOL +
           EOL +
           "Example:" + EOL +
           "  login jsmith" + EOL;
   private static final String LOGOUT_USAGE =
           "logout" + EOL +
           EOL +
           "Logs the user out of the current session" + EOL +
           EOL +
           "Example:" + EOL +
           "  logout" + EOL;


   CommandIF.Command _helpCmd = Command.help;

   /**
    * @throws CommandException
    */
   public void execute() throws CommandException
   {

      switch (_helpCmd)
      {
         case context:
            _screen.println(CONTEXT_USAGE);
            break;
         case create:
            _screen.println(CREATE_USAGE);
            break;
         case delete:
            _screen.println(DELETE_USAGE);
            break;
         case login:
            _screen.println(LOGIN_USAGE);
            break;
         case logout:
            _screen.println(LOGOUT_USAGE);
            break;
         case password:
            _screen.println(PASSWORD_USAGE);
            break;
         case read:
            _screen.println(READ_USAGE);
            break;
         case search:
            _screen.println(SEARCH_USAGE);
            break;
         case update:
            _screen.println(UPDATE_USAGE);
            break;
         case stats:
            _screen.println(STATS_USAGE);
            break;
         case env:
            _screen.println(ENV_USAGE);
            break;
         case help:
         default:
            _screen.println(HELP_USAGE);
            break;
      }
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
      String arg = null;

      // If no args are passed, then siimply return
      if (args == null)
      {
         return;
      }

      iter = args.iterator();

      iter.next(); // Pop off the command

      if (iter.hasNext())
      {
         arg = iter.next();

         try
         {
            _helpCmd = CommandIF.Command.valueOf(arg.toLowerCase());

         }
         catch (IllegalArgumentException e)
         {
            handleError("Unknown command/operation - " + arg);
         }
      }
   }
}
