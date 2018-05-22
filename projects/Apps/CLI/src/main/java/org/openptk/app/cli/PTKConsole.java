

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

package org.openptk.app.cli;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jline.ArgumentCompletor;
import jline.ConsoleReader;
import jline.History;
import jline.SimpleCompletor;

import org.openptk.app.cli.command.CommandException;
import org.openptk.app.cli.command.CommandIF;
import org.openptk.app.cli.command.Command;

//===================================================================
public final class PTKConsole
//===================================================================
{

   /*
    * Static Strings to define commonly used defaults
    */
   
   private static final String EOL = "\n";
   private static final Character PASSWORD_MASK = '*';
   private static final String PASSWORD_PROMPT = "Password: ";
   private static final String DEFAULT_PROMPT = "> ";
   private static final String COMMENT = "#";
   
   private PTKEnvironment _ptkEnv = null;
   private ConsoleReader  _reader = null;
   private PrintWriter    _screen = new PrintWriter(System.out, true);
   private String         _prompt = DEFAULT_PROMPT;


   /**
    * @param env
    */
   //----------------------------------------------------------------
   public PTKConsole(PTKEnvironment env)
   //----------------------------------------------------------------
   {
      List<SimpleCompletor> completors = null;
      History history = null;
      
      _ptkEnv = env;

      try
      {
         // Create a new ConsoleReader to run commands
         _reader = new ConsoleReader();

         /*
          * Create and add a set of Completors from the JLINE package.  This
          * will allow the auto completion of commands.
          */
         
         completors = new LinkedList<SimpleCompletor>();
         completors.add(new SimpleCompletor(Command.getCommands()));

         _reader.addCompletor(new ArgumentCompletor(completors));

         /*
          * Setup and use a History object from the JLINE package, that will
          * allow the user to use arrows in the interactive shell to recall
          * previous run commands.
          */
         
         history = new History(new File(env.getVariable(PTKEnvironment.Variable.HISTORY)));
         _reader.setHistory(history);
      }
      catch (IOException e)
      {
         System.err.println("Error starting console: " + e.getMessage());
      }
   }

   /**
    * @param userPrompt
    * @return
    */
    //----------------------------------------------------------------
   private List<String> getInput()
   //----------------------------------------------------------------
   {
      String line = null;
      List<String> cmdArray = null;

      try
      {
         line = _reader.readLine(this.getPrompt());
      }
      catch (IOException e)
      {
         System.err.println("Error while reading line from reader: " + e.getMessage());
      }

      if (line != null)
      {
         // Split the arguments by a space when the space doesn't fall within a
         // pair of double quotes
         cmdArray = Arrays.asList(line.split(" (?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))"));
      }
      
      return cmdArray;
   }

   /**
    * Using the ConsoleReader, prompts the user for a password.  The prompt
    * passed will be used as the string to preceed the question on the console.
    * 
    * @param prompt    prompt used to query user for a password.
    * 
    * @return          password typed in by the user.
    */
   public String getPassword(String prompt)
   {
      String line = null;

      try
      {
         line = _reader.readLine(prompt, PASSWORD_MASK);
      }
      catch (IOException e)
      {
         System.err.println("Error while reading line from reader: " + e.getMessage());
         return null;
      }

      return line;
   }

   /**
    * Helper method to call the getPassword(prompt) method with the default
    * Password prompt.
    * 
    * @return          password typed in by the user.
    */
   public String getPassword()
   {
      return getPassword(PASSWORD_PROMPT);
   }
   
   /**
    * Main method in the PTKConsole that will continue obtaining commands from
    * the command-line and executing those commands with output, until the user
    * exits or quits the console with an exit or quit command.
    */
   protected void run()
   {
      List<String> input = null;
      CommandIF ptkCmd = null;
      boolean done = false;

      /*
       * Print a welcome messag to the PTK Console
       */
      this.printPreamble();

      /*
       * This is the main loop that will continue to accept commands until
       * an EOF is reached (i.e. input == null) or a quit or exit command is
       * entered.
       */
      
      do 
      {
         input = this.getInput();

         /*
          * If the user enteres no command or a line starting with the
          * COMMENT character "#", then ignore it and move onto next line
          * of input.
          */
         if (input != null &&
                 (input.size() > 0) &&
                 !"".equals(input.get(0)) &&
                 !input.get(0).startsWith(COMMENT))
         {
            try
            {
               /*
                * Get the line from the input and create a PTC Command object
                * which will be executed on.
                */
               
               ptkCmd = Command.getCommand(this, input);

               if (ptkCmd != null)
               {
                  ptkCmd.execute();
                  
                  /*
                   * If the command is quit/exit, then close the connection
                   * and set a done flag
                   */
                  
                  if (ptkCmd.getCommand().equals(CommandIF.Command.quit) || ptkCmd.getCommand().equals((CommandIF.Command.exit)))
                  {
                     done = true;
                  }
               }
            }
            catch (CommandException e)
            {
               this.println("Error: " + e.getMessage());
            }
         }
      } while (!done && input != null);

      /*
       * Finally, make sure we close the connection.
       */
      
      this.getEnv().closeConnection();
   }


   /**
    * @param args
    */
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      String[] consoleArgs = {};

      try
      {
         PTKEnvironment env = new PTKEnvironment(consoleArgs);

         PTKConsole console = new PTKConsole(env);

         console.run();
      }
      catch (CLIException e)
      {
         System.err.println("Problem starting console");
      }
   }

   /**
    * Returns the PTKEnvironment associated with this PTKConsole
    * 
    * @return         OpenPTK Environement
    */
   public PTKEnvironment getEnv()
   {
      return _ptkEnv;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   public PrintWriter getScreen()
   //----------------------------------------------------------------
   {
      return _screen;
   }

   /**
    * @param prompt
    */
   //----------------------------------------------------------------
   private void setPrompt(String prompt)
   //----------------------------------------------------------------
   {
      _prompt = prompt;
      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   private String getPrompt()
   //----------------------------------------------------------------
   {
      String inputPrompt = "";
      String userId = null;
      
      userId = this.getEnv().getUserId();
      
      // Prepend the userPrompt with the current userId
      
      if (userId != null)
      {
         inputPrompt = userId;
      }
      
      inputPrompt += _prompt;
      
      return inputPrompt;
   }
   
   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   
   //----------------------------------------------------------------
   private void printPreamble()
   //----------------------------------------------------------------
   {
      println(
         "Welcome to the OpenPTK Console." + EOL +
         "Client Version: 2.2.0" + EOL +
         EOL +
         "Type 'help' for help. Type 'quit' to exit" + EOL);
   }

   //----------------------------------------------------------------
   private void println(String output)
   //----------------------------------------------------------------
   {
      _screen.println(output);
   }
   
}

