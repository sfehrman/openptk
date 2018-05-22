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

/**
 * Logger implementation that logs all messages to standard system output.
 * 
 * @author Terry Sigle
 */

//===================================================================
public class SysoutLogger implements LoggingIF
//===================================================================
{
   private LoggingLevel _level = null;
   private static final String prefix = "OpenPTK: ";

   //----------------------------------------------------------------
   public SysoutLogger()
   //----------------------------------------------------------------
   {
      _level = LoggingLevel.INFO;
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
      super.finalize();
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void addConsole()
   //----------------------------------------------------------------
   {
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
      return;
   }

   /**
    * Prints the message to standard output using the <b>INFO</b> level.
    * 
    * @param msg String to be printed to standard output.
    */
   //----------------------------------------------------------------
   public void put(final String msg)
   //----------------------------------------------------------------
   {
      this.put(LoggingLevel.INFO, msg);
      return;
   }

   /**
    * Prints the message to standard output using the LoggingLevel type passed.
    * 
    * @param type LoggingLevel to log the message as
    * @param msg String to be printed to standard output.
    */
   //----------------------------------------------------------------
   public void put(final LoggingLevel type, final String msg)
   //----------------------------------------------------------------
   {
      switch (type)
      {
         case WARNING:
            System.out.println(prefix + LoggingLevel.WARNING.name() + ": "+ msg);
            break;
         case SEVERE:
            System.out.println(prefix + LoggingLevel.SEVERE.name() + ": "+ msg);
            break;
         case INFO:
         default:
            System.out.println(prefix + LoggingLevel.INFO.name() + ": " + msg);
            break;
      }
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void close()
   //----------------------------------------------------------------
   {
      return;
   }

   /**
    * Prints the message to standard output as an <b>ERROR</b> level message.
    * 
    * @param msg String to be printed to standard output.
    */
   //----------------------------------------------------------------
   public void logError(String msg)
   //----------------------------------------------------------------
   {
      this.put(LoggingLevel.SEVERE, msg);
      return;
   }

   /**
    * Prints the message to standard output as an <b>WARNING</b> level message.
    * 
    * @param msg String to be printed to standard output.
    */
   //----------------------------------------------------------------
   public void logWarning(String msg)
   //----------------------------------------------------------------
   {
      this.put(LoggingLevel.WARNING, msg);
      return;
   }

   /**
    * Prints the message to standard output as an <b>INFO</b> level message.
    * 
    * @param msg String to be printed to standard output.
    */
   //----------------------------------------------------------------
   public void logInfo(String msg)
   //----------------------------------------------------------------
   {
      this.put(LoggingLevel.INFO, msg);
      return;
   }

   /**
    * Prints the message to standard output as an <b>INFO</b> level message.
    * 
    * @param msg String to be printed to standard output.
    */
   //----------------------------------------------------------------
   public void log(String msg)
   //----------------------------------------------------------------
   {
      logInfo(msg);
      return;
   }

   //----------------------------------------------------------------
   public String getId()
   //----------------------------------------------------------------
   {
      return Logger.DEFAULT;
   }
}
