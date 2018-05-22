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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.Query;
import org.openptk.api.Opcode;
import org.openptk.app.cli.PTKConsole;
import org.openptk.app.cli.PTKEnvironment;
import org.openptk.connection.ConnectionIF;
import org.openptk.exception.AuthenticationException;
import org.openptk.exception.ConnectionException;
import org.openptk.exception.QueryException;
import org.openptk.util.Timer;

//===================================================================
public abstract class Command implements CommandIF
//===================================================================
{

   private static final String FIELD_DELIMITER = ",";
   private static final char MULTI_DELIMITER = ';';
   private static final String KEY_VALUE_DELIMITER = "=";
   private CommandIF.Command _cmd = null;
   private PTKEnvironment _env = null;
   protected PrintWriter _screen = null;
   private PTKConsole _console = null;
   protected long _commandTiming = -1;

   /**
    * Creates a new instance of Command.
    * @param console
    * @param args
    * @return
    * @throws CommandException
    */
   public static CommandIF getCommand(PTKConsole console, List<String> args)
      throws CommandException
   {
      CommandIF command = null;

      CommandIF.Command cmd = CommandIF.Command.help;

      try
      {
         if (args.size() > 0)
         {
            cmd = CommandIF.Command.valueOf(args.get(0).toLowerCase());
         }
      }
      catch (IllegalArgumentException e)
      {
         handleError("Unknown command/operation - " + args.get(0));
      }

      /*
       * Check to see if we have a valid connection for certain commands
       */

      switch (cmd)
      {
         case create:
         case delete:
         case read:
         case search:
         case update:
         case password:
            if (!console.getEnv().isValidConnection()) {
               console.getEnv().closeConnection();
               handleError("Invalid Connection: Please Login");
            }
            break;
         default:
            break;
      }

      /*
       * Create the Command object depending on the command passed
       */

      switch (cmd)
      {
         case context:
            command = new ContextCommand();
            break;
         case exit:
         case quit:
            command = new QuitCommand();
            break;
         case help:
            command = new HelpCommand();
            break;
         case create:
            command = new CreateCommand();
            break;
         case delete:
            command = new DeleteCommand();
            break;
         case env:
            command = new EnvCommand();
            break;
         case read:
            command = new ReadCommand();
            break;
         case search:
            command = new SearchCommand();
            break;
         case update:
            command = new UpdateCommand();
            break;
         case stats:
            command = new StatsCommand();
            break;
         case login:
            command = new LoginCommand();
            break;
         case password:
            command = new PasswordCommand();
            break;
         case logout:
            command = new LogoutCommand();
            break;
         case show:
         case set:
            handleError("'" + cmd.toString() + "' Command Deprecated.  Replaced by 'env' command.");
            break;
         default:
            handleError("Operation/Command Not Implemented - " + cmd.toString());
            break;
      }

      if (command != null)
      {
         command.setCommand(cmd);
         command.setConsole(console);
         
         try
         {
            command.parseArgs(args);
         }
         catch (NoSuchElementException ex)
         {
            handleError("Invalid Arguments for " + cmd.toString());
         }
      }

      return command;
   }

   /**
    * @param opcode
    * @param input
    * @return
    * @throws Exception
    */
   public Output execute(Opcode opcode, Input input) throws ConnectionException, AuthenticationException
   {
      long elapsedTime = 0;
      ConnectionIF conn = this.getConnection();
      Output output = null;

      if (conn == null)
      {
         throw new AuthenticationException("Please authenticate first: Type 'help login' for more info");
      }

      /*
       * Start the timer for each operation
       */
      Timer.startTimer("CLI");

      output = conn.execute(opcode, input);

      elapsedTime = Timer.stopTimer("CLI");

      this.getEnv().getStats().addStat(opcode, elapsedTime, output.getState());

      _commandTiming = elapsedTime;

      return output;
   }

   /**
    * @param cmd
    */
   public void setCommand(Command cmd)
   {
      _cmd = cmd;
      return;
   }
   
   /**
    * @return
    */
   public PTKEnvironment getEnv()
   {
      return _env;
   }

   /**
    * @param env
    */
   public void setEnv(PTKEnvironment env)
   {
      _env = env;
      return;
   }

   /**
    * @return
    */
   public PTKConsole getConsole()
   {
      return _console;
   }

   /**
    * @param console
    */
   public void setConsole(PTKConsole console)
   {
      _console = console;
      
      this.setEnv(console.getEnv());
      this.setScreen(console.getScreen());
      
      return;
   }

   /**
    * @return
    */
   public Command getCommand()
   {
      return _cmd;
   }

   /**
    * @return
    */
   public ConnectionIF getConnection()
   {
      return _env.getConnection();
   }

   /**
    * @param screen
    */
   public void setScreen(PrintWriter screen)
   {
      _screen = screen;
      return;
   }

   /**
    * @param msg
    * @throws CommandException
    */
   protected static void handleError(String msg) throws CommandException
   {
      throw new CommandException(msg);
   }

   /**
    * @return
    */
   public static String[] getCommands()
   {
      CommandIF.Command[] cmds = CommandIF.Command.values();

      String[] commands = new String[cmds.length];

      int i = 0;

      for (CommandIF.Command cmd : cmds)
      {
         commands[i++] = cmd.toString();
      }

      return commands;
   }

   /*
    * parseId
    *
    * return the dequoted id passed
    * 
    * Example
    *    jsmith
    *    "jsmith"
    *
    * @param arg String argument that will be parsed
    * @return
    */
   public String parseId(String arg)
   {
      return deQuote(arg);
   }


   /*
    *
    * Parse Key & Value
    *
    * Parse key=value pair.  If the value is missing
    * then it will be represented as a null
    *
    * Example:
    *     firstname=john
    *     firstname=
    *
    * @param String key=value pair
    * @return
    */
   public void parseKeyValueIntoMap(String keyValue, Map<String, String> kvMap)
   {
      String key = null;
      String value = null;
      StringTokenizer kv = null;
      
      // For key/value pair, parse the key and value delimited by an equal
      if (keyValue != null)
      {
         kv = new StringTokenizer(keyValue, KEY_VALUE_DELIMITER);
         
         if (kv.hasMoreElements())
         {
            key = deQuote(kv.nextToken());

            // Check to see if there is a value.  If not, then set it to an
            // empty string.  This will result in a null/blank value in the
            // downstream service.
            if (kv.hasMoreElements())
            {
               value = deQuote(kv.nextToken());
            }
            else
            {
               value = null;
            }
            
            kvMap.put(key, value);
         }
      }

      return;
   }

   /*
    *
    * Parse Set Attributes
    *
    * Parse key=value pairs.  If the value is missing
    * then it will pass a null/empty string to basically
    * have the service null out the value.
    *
    * Example:
    *     firstname=john,lastname=doe
    *     firstname=,lastname=doe
    *
    * @param arg list of key=value pairs of arguments, separated by comma
    * @return
    */
   public Map<String, String> parseSetAttributes(String arg)
   {
      String keyValue = null;
      String key = null;
      String value = null;
      Map<String, String> setAttrs = new HashMap<String, String>();
      StringTokenizer kv = null;
      
      // Break apart each key/value pair delimited by a comma
      StringTokenizer attr = new StringTokenizer(arg, FIELD_DELIMITER);

      // For each key/value pair, parse the key and value delimited by an equal
      while (attr.hasMoreTokens())
      {
         keyValue = attr.nextToken();
         
         parseKeyValueIntoMap(keyValue, setAttrs);
      }

      return setAttrs;
   }

   //--------------------------------------
   // QUERY
   //
   // Parse a QUERY on the search request
   //
   // Only support = at this time
   //--------------------------------------

   /**
    * @param arg
    * @return
    * @throws CommandException
    */
   public Query parseQuery(String arg) throws CommandException
   {
      StringTokenizer st = new StringTokenizer(arg, "=");

      Query query = null;

      try
      {
         query = new Query("EQ", st.nextToken(), st.nextToken());
      }
      catch (QueryException ex)
      {
         handleError(ex.getMessage());
      }

      return query;
   }

   /**
    * @param args
    * @throws CommandException
    * @throws NoSuchElementException
    */
   public void parseArgs(List<String> args) throws CommandException, NoSuchElementException
   {
      return;
   }


   /**
    * @param input
    * @param map
    */
   //----------------------------------------------------------------
   public void setAttributesOnInput(Input input, Map<String, String> map)
   //----------------------------------------------------------------
   {
      Iterator<String> iter = null;
      String key = null;
      String value = null;
      String subval = null;
      String token = Character.toString(MULTI_DELIMITER);
      StringTokenizer strtok = null;
      List<String> multiList = null;
      Iterator<String> multiIter = null;
      String[] multiArray = null;

      if (map == null)
      {
         return;
      }

      iter = map.keySet().iterator();

      while (iter.hasNext())
      {
         key = iter.next();
         value = map.get(key);

         /*
          * Check for valid mulit-valued attribute.
          * Look for the MULTI_DELIMITER character within the String
          * Example: Roles="Employee;System Admin;Security"
          *
          * Need to handle "dirty data":
          *   ";;;sub one;;;sub two;;;"
          *   "sub one;;;sub two"
          *   ";;;;sub one"
          *   "sub one;;;;;"
          *   ";;;;;;;"
          *
          * Populate a List of Strings for each sub-value,
          * exclude sub-values that are NULL or zero length
          *
          * After all of the sub-values have been processed,
          * convert the List into a String[] if there is more than one
          * if there is only one, then the first element is set as a String
          *
          * Use .addAttribute() with the key and the String Array / String
          */

         if (value != null && value.contains(token))
         {
            strtok = new StringTokenizer(value, token);
            multiList = new ArrayList<String>();
            for (int i = 0; strtok.hasMoreElements(); i++)
            {
               subval = null;
               subval = strtok.nextToken();
               if (subval != null && subval.length() > 0)
               {
                  multiList.add(subval);
               }
            }
            if (multiList.size() == 1)
            {
               input.addAttribute(key, multiList.get(0));
            }
            else if (multiList.size() > 1)
            {
               multiArray = new String[multiList.size()];
               multiIter = multiList.iterator();
               for (int i = 0; multiIter.hasNext(); i++)
               {
                  multiArray[i] = multiIter.next();
               }
               input.addAttribute(key, multiArray);
            }
         }
         /*
          * Else, if the value is not null, then add it, as a single-value.
          */
         else if (value != null && value.length() > 0)
         {
            input.addAttribute(key, value);
         }
         /*
          * Else, the value is null, or zero length.  Currently, we'll add this
          * as an Attribute which will be processed as a nulling out of the
          * attribute value on the subject.
          * 
          */
         else
         {
            input.addAttribute(key, (String) null);
            // nothing
         }
      }

      return;
   }

   /**
    * Removes double quotes around the string passed in, if they are there.
    *
    * Example:
    *
    *    "John Smith"       ==> John Smith
    *    "John "A" Smith"     ==> John "A" Smith
    *
    * @param String Data to remove double quotes from
    * @param String DeQuotted data
    */
   protected String deQuote(String str)
   {
      if (str != null &&
              str.length() > 1
              && '"' == str.charAt(0)
              && '"' == str.charAt(str.length() - 1)) {
         str = str.substring(1, str.length() - 1);
      }

      return str;
   }

   /**
    * Encloses the string data pseed in with double quotes.
    *
    * Example:
    *
    *    John Smith     ==> "John Smith"
    *
    * @param String Data to place double quotes around
    * @param String Quoated data
    */
   protected String quote(String str)
   {
      return '"' + str + '"';
   }

   public void showTiming()
   {
      if (this.getEnv().showTimings() && _commandTiming > -1)
      {
         _screen.println("Time: " + _commandTiming + " ms" + EOL);
      }
   }
}

