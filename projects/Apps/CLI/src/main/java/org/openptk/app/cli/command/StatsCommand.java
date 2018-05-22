/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2011 Project OpenPTK
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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */

package org.openptk.app.cli.command;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.openptk.app.cli.PTKStatistics;
/**
 *
 * @author Tery Sigle
 */

public class StatsCommand extends Command implements CommandIF{

   private static final String RESET = "reset";

   private boolean _resetStats = false;

   /**
    * @throws CommandException
    */
   public void execute() throws CommandException
   {
      PTKStatistics stats = null;

      if (_resetStats)
      {
         this.getEnv().resetStats();
         
         _screen.println("\nOpenPTK Console Statistics Successfully Reset\n");
      }
      else
      {
         stats = this.getEnv().getStats();

         _screen.println("");
         _screen.println("OpenPTK Console Statistics ('help stats' for more info)");
         _screen.println("-------------------------------------------------------");
         //_screen.println(STATS_INFO);

         _screen.println(stats.outputStats());
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
      String arg = null;

      // If no args are passed, then siimply return
      if (args == null)
      {
         return;
      }

      iter = args.iterator();

      iter.next(); // Pop off the command


      // Check to see if we need to reset statistics
      if (iter.hasNext())
      {
         _resetStats = RESET.equalsIgnoreCase(iter.next());

         /*
          * If the only argument isn't a reset option, then error off
          */
         if (! _resetStats)
         {
            handleError("Only option is 'reset', to clear statistics");
         }

      }
   }
}
