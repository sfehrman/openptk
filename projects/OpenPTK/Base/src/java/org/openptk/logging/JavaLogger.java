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

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements the core Java2 logging facilities (java.util.logging.*).
 * 
 * @author Terry Sigle
 */
//===================================================================
abstract class JavaLogger implements LoggingIF
//===================================================================
{
   //protected Logger _logger = null;
   protected static final Logger _logger = Logger.getLogger(org.openptk.logging.Logger.DEFAULT);

   //----------------------------------------------------------------
   public JavaLogger()
   //----------------------------------------------------------------
   {
      //_logger = Logger.getLogger(org.openptk.logging.Logger.DEFAULT);
      _logger.setLevel(Level.ALL);
      _logger.setUseParentHandlers(false);
      
      return;
   }

   /**
    * @throws Throwable
    */
   //----------------------------------------------------------------
   public void finialize() throws Throwable
   //----------------------------------------------------------------
   {
      this.close();
      return;
   }
   //----------------------------------------------------------------
   @Override
   public void addConsole()
   //----------------------------------------------------------------
   {
      ConsoleHandler handler = new ConsoleHandler();
      _logger.addHandler(handler);
      return;
   }

   /**
    * @param file
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public void addFile(final String file) throws Exception
   //----------------------------------------------------------------
   {
      FileHandler handler = new FileHandler(file, true);
      handler.setFormatter(new OpenPTKLogFormatter());
      _logger.addHandler(handler);
      return;
   }

   /**
    * Prints the message to standard output using the <b>INFO</b>.
    * 
    * @param msg String to be printed to logger.
    */
   //----------------------------------------------------------------
   public void put(final String msg)
   //----------------------------------------------------------------
   {
      _logger.info(msg);
      return;
   }

   /**
    * Prints the message to logger using the LoggingLevel type passed.
    * 
    * @param type LoggingLevel to log the message as
    * @param msg String to be printed to logger.
    */
   //----------------------------------------------------------------
   public void put(final LoggingLevel type, final String msg)
   //----------------------------------------------------------------
   {
      switch (type)
      {
         case INFO:
            _logger.info(msg);
            break;
         case WARNING:
            _logger.warning(msg);
            break;
         case SEVERE:
            _logger.severe(msg);
            break;
         case CONFIG:
            _logger.config(msg);
            break;
         case FINE:
            _logger.fine(msg);
            break;
         case FINER:
            _logger.finer(msg);
            break;
         case FINEST:
            _logger.finest(msg);
            break;
      }
      return;
   }
   //----------------------------------------------------------------
   @Override
   public void close()
   //----------------------------------------------------------------
   {
      Handler handlers[] = _logger.getHandlers();
      for (int i = 0; i < handlers.length; i++)
      {
         handlers[i].close();
      }
      return;
   }

   /**
    * Prints the message to logger as an <b>ERROR</b> level message.
    * 
    * @param msg String to be printed to logger.
    */
   public void logError(String msg)
   {
      _logger.severe(msg);
   }

   /**
    * Prints the message to logger as an <b>WARNING</b> level message.
    * 
    * @param msg String to be printed to logger.
    */
   public void logWarning(String msg)
   {
      _logger.warning(msg);
   }

   /**
    * Prints the message to logger as an <b>INFO</b> level message.
    * 
    * @param msg String to be printed to logger.
    */
   public void logInfo(String msg)
   {
      _logger.info(msg);
   }

   /**
    * Prints the message to logger as an <b>INFO</b> level message.
    * 
    * @param msg String to be printed to logger.
    */
   public void log(String msg)
   {
      logInfo(msg);
   }


   /**
    * Returns the ID/Name of the logging facility
    * 
    * @return name
    */
   //----------------------------------------------------------------
   public String getId()
   //----------------------------------------------------------------
   {
      return _logger.getName();
   }

}
