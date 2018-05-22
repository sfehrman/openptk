/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009-2010 Sun Microsystems, Inc.
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
 * Portions Copyright 2011-2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.session;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openptk.engine.EngineIF;
import org.openptk.logging.Logger;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class ScanningSessionManager extends SessionManager
//===================================================================
{
   private static final long DEFAULT_DELAY = 30;
   private static final long DEFAULT_SLEEP = 5;
   private static final String PROP_ENGINE_SESSION_SCAN_DELAY = "engine.session.scan.delay";
   private static final String PROP_ENGINE_SESSION_SCAN_SLEEP = "engine.session.scan.sleep";
   private ScheduledExecutorService _scheduler = null;
   private ScheduledFuture<?> _handle = null;
   private Runnable _scanner = null;
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private long _delay = DEFAULT_DELAY;
   private long _sleep = DEFAULT_SLEEP;

   //----------------------------------------------------------------
   public ScanningSessionManager()
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public ScanningSessionManager(final EngineIF engine)
   //----------------------------------------------------------------
   {
      super();
      String value = null;

      if (engine != null)
      {
         value = engine.getProperty(PROP_ENGINE_SESSION_SCAN_DELAY);
         if (value != null && value.length() > 0)
         {
            try
            {
               _delay = Long.parseLong(value);
            }
            catch (NumberFormatException ex)
            {
               _delay = DEFAULT_DELAY;
            }
         }

         value = engine.getProperty(PROP_ENGINE_SESSION_SCAN_SLEEP);
         if (value != null && value.length() > 0)
         {
            try
            {
               _sleep = Long.parseLong(value);
            }
            catch (NumberFormatException ex)
            {
               _sleep = DEFAULT_SLEEP;
            }
         }
      }
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void startup()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":startup(): ";

      _scheduler = Executors.newScheduledThreadPool(1);
      _scanner = new Scanner(_sessions);
      _handle = _scheduler.scheduleAtFixedRate(_scanner, _delay, _sleep, TimeUnit.MINUTES);

      Logger.logInfo(METHOD_NAME + "Scheduler Started, delay: "
         + _delay + " minutes, sleep: " + _sleep + " minutes");

      return;
   }

   //----------------------------------------------------------------
   @Override
   public void shutdown()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":shutdown(): ";

      _scheduler.shutdown();

      Logger.logInfo(METHOD_NAME + "Scheduler Shutdown");

      return;
   }

   //===================================================================
   private class Scanner implements Runnable
   //===================================================================
   {
      private ConcurrentMap<String, SessionIF> _sessions = null;
      private final String CLASS_NAME = this.getClass().getSimpleName();

      /**
       * @param sessions
       */
      //----------------------------------------------------------------
      public Scanner(final ConcurrentMap<String, SessionIF> sessions)
      //----------------------------------------------------------------
      {
         super();

         _sessions = sessions;

         return;
      }

      //----------------------------------------------------------------
      @Override
      public void run()
      //----------------------------------------------------------------
      {
         String METHOD_NAME = CLASS_NAME + ":run(): ";
         SessionIF session = null;
         Set<String> keys = null;

         Logger.logInfo("ScanningSessionManager:" + METHOD_NAME + "Session count=" + _sessions.size());

         keys = _sessions.keySet();
         if (keys != null && !keys.isEmpty())
         {
            for (String key : keys)
            {
               if (key != null && key.length() > 0)
               {
                  session = _sessions.get(key);
                  if (session != null && session.isExpired())
                  {
                     _sessions.remove(key);
                  }
               }
            }
         }

         return;
      }
   }
}
