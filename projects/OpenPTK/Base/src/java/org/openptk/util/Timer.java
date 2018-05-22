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

package org.openptk.util;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Singleton class which will hold timers that can be used by any calling
 * class to time start/stop timers and report on timings
 */
public class Timer
{

   private final String CLASS_NAME = this.getClass().getSimpleName();

   private static Timer _openptkTimer = null;

   private Map<String, Stack<Long>> _timerMap = null;

   private final static String DEFAULT_TIMER = "_DefaultTimer_";

   /*
    * Private constructor.  Doesn't allow anyone but itself construct a new
    * class.
    *
    * The constructor will create a map of timesr.
    */
   private Timer()
   {
      _timerMap = new HashMap<String,Stack<Long>>();
   }

   /**
    * StartTimer will capture the time this method is called and store it in
    * a timer named by the passed argument timerName.
    * <p>
    * Multiple successive startTimer methods can be called.  Each timer is
    * pushed onto a stack that will be popped off only after a stopTimer
    * method is called.
    * <p>
    * Note: This is a static factory method that will create the one and
    * only singleton instance if it hasn't been created yet.
    *
    * @param timerName String of name of timer
    * @return long Current time, in ms, of this timer
    */
   public static synchronized long startTimer(String timerName)
   {
      Long currTime = null;
      Stack <Long>timerStack = null;

      if (Timer._openptkTimer == null)
      {
         Timer._openptkTimer = new Timer();
      }

      currTime = new Long(System.currentTimeMillis());

      timerStack = Timer._openptkTimer._timerMap.get(timerName);

      if (timerStack == null)
      {
         timerStack = new Stack<Long>();
         Timer._openptkTimer._timerMap.put(timerName, timerStack);
      }

      timerStack.push(currTime);

      return currTime.longValue();
   }

   /**
    * StopTimer will capture the time this method is called and return the
    * elapsed time in ms since the corresponding StartTimer was called for
    * that timerName.  If the timer wasn't started, then the resulting
    * time returned will be -1.
    * <p>
    * Note: This is a static factory method that will create the one and
    * only singleton instance if it hasn't been created yet.
    *
    * @param timerName String of name of timer
    * @return long Elapsed time, in ms, since last startTimer was called
    */
   public static synchronized long stopTimer(String timerName)
   {
      Long currTime = null;
      Long lastTime = null;
      Stack <Long>timerStack = null;

      if (Timer._openptkTimer == null)
      {
         Timer._openptkTimer = new Timer();
      }

      currTime = new Long(System.currentTimeMillis());

      timerStack = Timer._openptkTimer._timerMap.get(timerName);

      if (timerStack == null)
      {
         lastTime = currTime + 1;
      }
      else
      {
         try
         {
            lastTime = timerStack.pop();
         }
         /*
          * If the stack is empty, then more stopTimers was called than
          * startTimers
          */
         catch (EmptyStackException e)
         {
            lastTime = currTime + 1;
         }
      }


      return currTime.longValue() - lastTime.longValue();
   }

   public static void main (String argv[])
   {
      System.out.println("Timer Test");
      System.out.println("==========");

      System.out.println("Starting (Timer Test1) Clock at: " + Timer.startTimer("Timer Test1"));
      System.out.println("Starting (Timer Test2) Clock at: " + Timer.startTimer("Timer Test2"));
      System.out.println("Starting (Timer Test1) Clock at: " + Timer.startTimer("Timer Test1"));

      System.out.println("Stopping (Timer Test1) Clock at: " + Timer.stopTimer("Timer Test1"));
      System.out.println("Stopping (Timer Test2) Clock at: " + Timer.stopTimer("Timer Test2"));
      System.out.println("Stopping (Timer Test1) Clock at: " + Timer.stopTimer("Timer Test1"));
      System.out.println("Stopping (Timer Test) Clock at: " + Timer.stopTimer("Other Test"));

   }
}

