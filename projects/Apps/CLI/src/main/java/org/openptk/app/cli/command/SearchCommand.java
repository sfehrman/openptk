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
import org.openptk.api.Output;
import org.openptk.api.Query;
import org.openptk.api.Opcode;
import org.openptk.app.cli.writer.CLIWriterIF;
import org.openptk.app.cli.writer.ColumnWriter;
import org.openptk.exception.AuthenticationException;
import org.openptk.exception.ConnectionException;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Terry Sigle
 */
public class SearchCommand extends Command implements CommandIF
{

   private Query _query = null;                  // -q    Query for Search

   /**
    * @throws CommandException
    */
   public void execute() throws CommandException
   {
      Input input = null;
      Output output = null;

      input = new Input();

      input.setDescription("ptkcli search"); // optional

      if (_query != null)
      {
         input.setQuery(_query);
      }

      try
      {
         output = this.execute(Opcode.SEARCH, input);
         if (output != null)
         {

            switch (output.getState())
            {
               case SUCCESS:
                  CLIWriterIF cliWriter = new ColumnWriter(_screen);

                  cliWriter.setOutput(output);

                  cliWriter.write();
                  break;
               case DENIED:
                  _screen.println("Failed: SEARCH not allowed.");
                  break;
               case ERROR:
                  _screen.println("Failed: CONTEXT '" + this.getEnv().getContextId() + "' not available.");
                  break;
               default:
                  _screen.println("Failed " + output.getState());
            }
         }
      }
      catch (AuthenticationException ex) {
         handleError(ex.getMessage());
      }
      catch (ConnectionException ex)
      {
         handleError("Check query value : " +
            ex.getMessage());
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
      Iterator<String> iter = null;
      String searchString = "";

      iter = args.iterator();

      iter.next(); // Pop off the command

      // Iterate through each of the command arguments
      while (iter.hasNext())
      {
         searchString += deQuote(iter.next()) + " ";
      }

      if (searchString.length() > 0)
      {
         _query = new Query(Query.Type.NOOPERATOR, StructureIF.NAME_SEARCH, searchString);
      }
      
      return;
   }
}
