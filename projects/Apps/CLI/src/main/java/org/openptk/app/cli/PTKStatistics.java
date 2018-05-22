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

import org.openptk.api.Opcode;
import org.openptk.api.State;

public final class PTKStatistics
{
   private final static String EOL = "\n";

   private final class OperationStats
   {
      Opcode opcode = null;
      int numOps = 0;
      int numRows = 0;
      int elapsedTime = 0;
      int avgTime = 0;
      int minTime = 0;
      int maxTime = 0;

      public OperationStats (Opcode opcode)
      {
         this.opcode = opcode;
      }
   }

   private OperationStats readStats = null;
   private OperationStats searchStats = null;
   private OperationStats createStats = null;
   private OperationStats updateStats = null;
   private OperationStats deleteStats = null;
   private int totalOps = 0;

   public PTKStatistics()
   {
      readStats = new OperationStats(Opcode.READ);
      searchStats = new OperationStats(Opcode.SEARCH);
      createStats = new OperationStats(Opcode.CREATE);
      updateStats = new OperationStats(Opcode.UPDATE);
      deleteStats = new OperationStats(Opcode.DELETE);

      return;
   }

   private String formatStats(OperationStats opStats)
   {
      String statsStr = null;
      int percentOps = 0;

      if (this.totalOps > 0)
      {
         percentOps = (opStats.numOps * 100) / this.totalOps;
      }

      statsStr = String.format (
              "%-9s %5d %% %7d %7d %7d %7d",
              opStats.opcode.name(),
              percentOps,
              opStats.numOps,
              opStats.avgTime,
              opStats.minTime,
              opStats.maxTime);

      return statsStr;
   }

   public String outputStats()
   {
      StringBuilder str = null;

      str = new StringBuilder();

      str.append("                                 Times (ms)" + EOL);
      str.append("Operation  % Ops   # Ops    Avg     Min     Max  " + EOL);
      str.append("========= ======= ======= ======= ======= =======" + EOL);
      str.append(formatStats(readStats)).append(EOL);
      str.append(formatStats(searchStats)).append(EOL);
      str.append(formatStats(createStats)).append(EOL);
      str.append(formatStats(updateStats)).append(EOL);
      str.append(formatStats(deleteStats)).append(EOL);
      str.append(EOL);
      str.append("Total Operations = ").append(totalOps).append(EOL);

      return str.toString();
   }

   /*
    * addStat
    *
    * Add the stistic for the passed opcode
    *
    * @param opcode Operation Code based on the Opcode value (i.e. read, search, ...)
    * @param elapsedTime Time in ms that the operation took
    */
   public void addStat(Opcode opcode, long elapsedTime, State state)
   {
      OperationStats opStats = null;

      switch (opcode)
      {
         case READ:
            opStats = readStats;
            break;
         case SEARCH:
            opStats = searchStats;
            break;
         case CREATE:
            opStats = createStats;
            break;
         case UPDATE:
            opStats = updateStats;
            break;
         case DELETE:
            opStats = deleteStats;
            break;
         default:
      }

      if (opStats != null && state == State.SUCCESS)
      {
         totalOps++;

         opStats.numOps++;
         opStats.elapsedTime += elapsedTime;
         opStats.avgTime = opStats.elapsedTime / opStats.numOps;

         if (opStats.numOps == 1)
         {
            opStats.minTime = (int) elapsedTime;
         }
         else
         {
            opStats.minTime = (int) Math.min(opStats.minTime, elapsedTime);
         }

         opStats.maxTime = (int) Math.max(opStats.maxTime, elapsedTime);
      }

      return;
   }

}
