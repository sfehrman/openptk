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
package org.openptk.logging;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class which will hold objects that are available globally to
 * the OpenPTK Framework.  
 * <p>
 * The objects that are managed include:
 * <p>
 * <ul>
 * <li> Logger - Master Logger that can be used.
 * </ul>
 * <p> 
 * The usage of the Logger is intended to be extremely simple to use.  
 * At any place withing some Java code, the developer should not be required 
 * to set anything up.  The only thing they need to do is simply call the 
 * log....() message on the Logger.  See example below:
 * <p>
 * <pre>
 * import org.openptk.logging.Logger;
 *
 *  ...
 * Logger.logInfo("HelloWorld!");
 * ...
 * </pre>
 * <p>
 * By default, the logger is initialized with an OpenPTK SysoutLogger() which 
 * simply emits messages to the standard SYSOUT for that platform.  
 * Other optional Loggers available with OpenPTK include:
 * <p>
 * <ul>
 * <li> JavaLogger
 * <li> SimpleLogger (extends JavaLogger with no changes)
 * <li> AtomicLogger (extends JavaLogger.  Every log message emitted will 
 *      open/close the log file, making every log message an atomic operation)
 * </ul>
 * <p>
 * An example of setting up a new CustomLogger is shown below:
 * <p>
 * <pre>
 * import org.openptk.logging.Logger;
 * import org.openptk.logging.LoggingIF;
 * import ....CustomLogger;             // Written by developer (must implement LoggingIF)
 *
 * ...
 * {
 *    ...
 *    LoggingIF  myLogger = new CustomLogger(...);
 * 
 *    Logger.setLogger(myLogger);
 * 
 *    Logger.logInfo("HelloWorld! Using the new CustomLogger()");
 * }
 * </pre>
 * 
 * @author  Terry Sigle
 */
public class Logger
{
   /*
    * These are string keys to set and use different LoggingIF for the Logger
    * singleton.
    */
   public static final String DEFAULT = "default";
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static Logger _openptkLogger = null;
   private static Map<String, LoggingIF> _loggers = new HashMap<String, LoggingIF>();

   /**
    * Private constructor.  Doesn't allow anyone but itself construct a new
    * class.
    *
    * The constructor will create a default logger (SysoutLogger).  This
    * is created to have a default at a minimum should there be any problems
    * with the creation of a different logger.
    */
   private Logger()
   {
      _loggers.put(DEFAULT, new SysoutLogger());

      return;
   }

   /**
    * Since this is a singleton class, ensure that it cannot be cloned by
    * overridding the Object.clone() method, instead throwing a
    * CloneNotSupportedException.
    */
   @Override
   public Object clone() throws CloneNotSupportedException
   {
      throw new CloneNotSupportedException("Cloning of " + CLASS_NAME + " not allowed");
   }

   /**
    * Getter for the logger instance
    */
   //----------------------------------------------------------------
   public static LoggingIF getLogger(String logId)
   //----------------------------------------------------------------
   {
      LoggingIF log = null;

      if ( logId == null || logId.length() < 1)
      {
         logId = DEFAULT;
      }


      if (_openptkLogger == null)
      {
         _openptkLogger = new Logger();
      }

      log = Logger._loggers.get(logId);

      /*
       * If an unknown logger is reqeusted, then return the DEFAULT logger
       * instead
       */

      if (log == null)
      {
         log = Logger._loggers.get(DEFAULT);
      }
      
      return log;
   }

   /**
    * Getter for the Logger instance
    */
   //----------------------------------------------------------------
   public static LoggingIF getLogger()
   //----------------------------------------------------------------
   {
      return Logger.getLogger(DEFAULT);
   }


   /**
    * Add the logging instance to the Logger using a particular
    * name to refer to it, and subsequently get or log messages to later.
    * If the logId is null, or the logging instance is null, then return
    * doing nothing.
    *
    * @param log Log instance (i.e. SimpleLogger, AtomicLogger)
    *
    */
   //----------------------------------------------------------------
   public static void setLogger(final LoggingIF log)
   //----------------------------------------------------------------
   {
      String logId = null;

      if (_openptkLogger == null)
      {
         _openptkLogger = new Logger();
      }

      logId = log.getId();

      if (logId != null && logId.length() > 0 && log != null)
      {
         Logger._loggers.put(logId, log);
      }

      return;
   }

   /**
    * Log an <b>ERROR</b> message to a particular Logger.
    * 
    * @param logId Name of the Logger to log message to
    * @param msg String message to be logged
    */
   //----------------------------------------------------------------
   public static void logError(String logId, String msg)
   //----------------------------------------------------------------
   {
      Logger.getLogger(logId).logError(msg);
      return;
   }

   /**
    * Log an <b>ERROR</b> message to <b>DEFAULT</b> Logger.
    * 
    * @param msg String message to be logged
    */
   //----------------------------------------------------------------
   public static void logError(String msg)
   //----------------------------------------------------------------
   {
      Logger.logError(DEFAULT, msg);
      return;
   }

   /**
    * Log an <b>WARNING</b> message to a particular Logger.
    * 
    * @param logId Name of the Logger to log message to
    * @param msg String message to be logged
    */
   //----------------------------------------------------------------
   public static void logWarning(String logId, String msg)
   //----------------------------------------------------------------
   {
      Logger.getLogger(logId).logWarning(msg);
      return;
   }

   /**
    * Log an <b>WARNING</b> message to <b>DEFAULT</b> Logger.
    * 
    * @param msg String message to be logged
    */
   //----------------------------------------------------------------
   public static void logWarning(String msg)
   //----------------------------------------------------------------
   {
      Logger.logWarning(DEFAULT, msg);
      return;
   }

   /**
    * Log an <b>INFO</b> message to a particular Logger.
    * 
    * @param logId Name of the Logger to log message to
    * @param msg String message to be logged
    */
   //----------------------------------------------------------------
   public static void logInfo(String logId, String msg)
   //----------------------------------------------------------------
   {
      Logger.getLogger(logId).logInfo(msg);
      return;
   }

   /**
    * Log an <b>INFO</b> message to <b>DEFAULT</b> Logger.
    * 
    * @param msg String message to be logged
    */
   //----------------------------------------------------------------
   public static void logInfo(String msg)
   //----------------------------------------------------------------
   {
      Logger.logInfo(DEFAULT, msg);
      return;
   }

   /**
    * Log a message to a particular Logger.  This is a helper method to call
    * logIfo().
    * 
    * @param logId Name of the Logger to log message to
    * @param msg String message to be logged
    */
   //----------------------------------------------------------------
   public static void log(String logId, String msg)
   //----------------------------------------------------------------
   {
      Logger.getLogger(logId).logInfo(msg);
      return;
   }

   /**
    * Log a message to <b>DEFAULT</b> Logger.
    * 
    * @param msg String message to be logged
    */
   //----------------------------------------------------------------
   public static void log(String msg)
   //----------------------------------------------------------------
   {
      Logger.log(DEFAULT, msg);
      return;
   }

   /**
    * Log a message to a particular Logger at a given LoggingLevel.
    * 
    * @param logId Name of the Logger to log message to
    * @param logLevel LoggingLevel to log the message to
    * @param msg String message to be logged
    */
   //----------------------------------------------------------------
   public static void log(String logId, LoggingLevel logLevel, String msg)
   //----------------------------------------------------------------
   {
      switch (logLevel)
      {
         case SEVERE:
            logError(logId, msg);
            break;
         case WARNING:
            logWarning(logId, msg);
            break;
         case INFO:
         default:
            logInfo(logId, msg);
            break;
      }
      return;
   }

   /**
    * Log a message to <b>DEFAULT</b> Logger at a given LoggingLevel.
    * 
    * @param logLevel LoggingLevel to log the message to
    * @param msg String message to be logged
    */
   //----------------------------------------------------------------
   public static void log(LoggingLevel logLevel, String msg)
   //----------------------------------------------------------------
   {
      Logger.log(DEFAULT, logLevel, msg);
   }
   
}
