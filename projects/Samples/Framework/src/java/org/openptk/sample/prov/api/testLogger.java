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
package org.openptk.sample.prov.api;

import org.openptk.logging.AtomicLogger;
import org.openptk.logging.Logger;
import org.openptk.logging.LoggingIF;
import org.openptk.logging.SimpleLogger;


//===================================================================
class testLogger
//===================================================================
{

   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      testLogger test = new testLogger();

      try
      {
         test.run();
      }
      catch (Exception ex)
      {
         System.out.println(ex.getMessage());
      }

      return;
   }

   //----------------------------------------------------------------
   public void run() throws Exception
   //----------------------------------------------------------------
   {
      LoggingIF logger1 = null;
      LoggingIF logger2 = null;
      LoggingIF logger3 = null;
      LoggingIF logger4 = null;
      
      test(null);

      logger1 = new AtomicLogger("/var/tmp/atomic.log");
      test(logger1);
      
      logger2 = new SimpleLogger("/var/tmp/simple1.log");
      test(logger2);

      logger3 = new SimpleLogger("/var/tmp/simple2.log");
      test(logger3);

      logger4 = new SimpleLogger("/var/tmp/simple3.log");
      test(logger4);

      test (null);

   }

   private void test(LoggingIF logger)
   {
      String logId = null;

      if (logger != null)
         logId = logger.getId();

      if (logId != null)
      {
         Logger.setLogger(logger);
         Logger.logError(logId, "Using new Logger: " + logger.toString());

         Logger.logError(logId, "This is an error");
         Logger.logWarning(logId, "This is an warning");
         Logger.logInfo(logId, "This is an info");
      }
      else
      {
         if (logger != null)
            Logger.setLogger(logger);
         Logger.logError("This is an error");
         Logger.logWarning("This is an warning");
         Logger.logInfo("This is an info");
      }

      System.out.println("============================================");

   }

}
