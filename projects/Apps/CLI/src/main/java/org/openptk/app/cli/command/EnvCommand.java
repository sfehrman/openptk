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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.openptk.app.cli.PTKEnvironment;

import org.openptk.app.cli.writer.CLIWriterIF;
import org.openptk.app.cli.writer.ColumnWriter;
import org.openptk.connection.ConnectionIF;
import org.openptk.exception.ConnectionException;

/**
 *
 * @author Terry Sigle
 */
public class EnvCommand extends Command implements CommandIF
{
   private Map<String, String> _nameValue = null;

   /**
    * @throws CommandException
    */
   public void execute() throws CommandException
   {
      if (_nameValue != null)
      {
         setEnvionmentVariable();
      }

      printEnvironmentInfo();

      printSessionInfo();
      
      return;
   }

   /**
    * @throws CommandException
    */
   private void setEnvionmentVariable() throws CommandException
   {
      String variable = null;
      String value = null;
      Iterator<String> iter = null;

      // Set the variable to the value.  If the value is null, then this
      // method will remove it from the set of properties.

      iter = _nameValue.keySet().iterator();

      while (iter.hasNext())
      {
         variable = iter.next();
         value = _nameValue.get(variable);


         try
         {
            getEnv().setVariable(variable, value, true);
         }
         catch (Exception e)
         {
            throw new CommandException(e.getMessage());
         }
      }

      return;
   }

   /**
    * @throws CommandException
    */
   private void printEnvironmentInfo() throws CommandException
   {
      String key = null;
      List<String> data = new ArrayList<String>();
      List<String> row = null;

      // Show the settings in the current environment
      CLIWriterIF cliWriter = new ColumnWriter(_screen);
      Enumeration<Object> variables = null;

      _screen.println("");
      _screen.println("Environment Information");

      // Create the Header
      data.add("Variable");
      data.add("Value");
      cliWriter.setMetaData(data);

      variables = getEnv().getVariables();

      while (variables.hasMoreElements())
      {
         key = (String) variables.nextElement();
         row = new ArrayList<String>();
         row.add(key);
         row.add(getEnv().getVariable(key));

         cliWriter.addRow(row);
      }

      cliWriter.write(false);

      return;
   }


   /**
    * @throws CommandException
    */
   private void printSessionInfo() throws CommandException
   {
      String key = null;
      List<String> data = new ArrayList<String>();
      String contextDetails = "";

      // Show the settings in the current environment
      CLIWriterIF cliWriter = new ColumnWriter(_screen);

      _screen.println("");
      _screen.println("Session Information");

      // Create the Header
      data.add("Variable");
      data.add("Value");
      cliWriter.setMetaData(data);

      String userId = getEnv().getUserId();

      if (userId == null)
      {
         userId = "";
      }

      data = new ArrayList<String>();
      data.add("UserId");
      data.add(userId);
      cliWriter.addRow(data);

      ConnectionIF conn = getEnv().getConnection();

      String sessionId = "";
      String sessionType = "";
      String contextId = "";

      try {
         if (conn != null)
         {
            contextId = conn.getContextId();
            sessionId = conn.getSessionData(ConnectionIF.Session.ID);
            sessionType = conn.getSessionData(ConnectionIF.Session.TYPE);
         }
      }
      catch (ConnectionException ex) {
      }

      // Augment the show on the CONTEXT. 
      data = new ArrayList<String>();
      data.add("ContextId");
      data.add(contextId);
      cliWriter.addRow(data);

      data = new ArrayList<String>();
      data.add("SessionId");
      data.add(sessionId);
      cliWriter.addRow(data);

      data = new ArrayList<String>();
      data.add("SessionType");
      data.add(sessionType);
      cliWriter.addRow(data);

      cliWriter.write(false);

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

      // Get the name=value
      if (iter.hasNext())
      {
         _nameValue = new HashMap<String, String>();

         this.parseKeyValueIntoMap(iter.next(), _nameValue);
      }

      return;
   }
}
