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
import org.openptk.app.cli.PTKEnvironment;
import org.openptk.connection.ConnectionIF;
import org.openptk.exception.ConnectionException;

/**
 *
 * @author Tery Sigle
 */
public class ContextCommand extends Command implements CommandIF
{
   private final static String DEFAULT = "default";

   private boolean _settingNewContextId = false;
   private String _contextId = null;

   /**
    * @throws CommandException
    */
   public void execute() throws CommandException
   {
      ConnectionIF conn = null;
      String [] contextIds = null;
      String currentContextId = null;

      conn = this.getConnection();

      /*
       * If a new 'context id' is being set then we will first set that 
       * context id and persist it.
       */
      if (_settingNewContextId)
      {
         
         /*
          * If the context being set is equal to DEFAULT, then set the _contextId
          * to null, so that triggers the code null out the contextId in the
          * persisted property file as well as emitting different success
          * messages.
          */

         if (DEFAULT.equalsIgnoreCase(_contextId))
         {
            _contextId = null;
         }

         try
         {

            /*
             * This will set the current context to a new value.
             If the user is
             * logged in, then it will detect any issues.
             */

            this.getEnv().setContextId(_contextId);

            if (_contextId == null)
            {
               _screen.println("The context is set to the server default context");
            }
            else
            {
               _screen.println("The context is set to '" + _contextId + "'");
            }
         }
         catch (ConnectionException e)
         {
            handleError(e.getMessage());
         }

      }

      /*
       * If there is a list contextIds, then print a list.
       * Else throw a message that specifies a login is require to get the
       * contexts.  Reason...Anonymous connection not allowed.
       */
      contextIds = this.getEnv().getContextIds();
      currentContextId = this.getEnv().getContextId();

      if (contextIds != null)
      {
         _screen.println("");
         _screen.println("   Available Contexts");
         _screen.println("   ------------------");

         for (String contextId : contextIds)
         {
            if (contextId.equals(currentContextId))
            {
               _screen.print(" * ");
            }
            else
            {
               _screen.print("   ");
            }

            _screen.print(contextId);

            if (contextId.equals(this.getEnv().getDefaultContextId()))
            {
               _screen.print(" (Server Default)");
            }

            _screen.println("");
         }

         _screen.println("");

         if (conn != null)
         {
            _screen.println(" * = Context in Use");
         }

         _screen.println("");
      }
      else
      {
         if ( !_settingNewContextId )
         {
            _screen.println("No contexts displayed until a valid login session created.");
         }
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

      // Get the contextId
      if (iter.hasNext())
      {
         _settingNewContextId = true;
        
         _contextId = this.parseId(iter.next());
      }

      if (iter.hasNext())
      {
         handleError("Only 1 optional argument allowed.");
      }

      return;
   }
}
