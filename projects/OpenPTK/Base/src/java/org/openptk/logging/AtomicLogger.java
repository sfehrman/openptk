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
package org.openptk.logging;

import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

//===================================================================
public class AtomicLogger extends JavaLogger
//===================================================================
{
   private FileHandler _fileHandler = null;
   private String _logFQName = null;
   private final Object _logLock = new Object();

   /**
    * AtomicLogger - Create a new simple logger with a file to be written to.
    * This Logger is based on the OpenPTK JavaLogger which is based on the 
    * java.util.logging.Logger facility.  Note that only 1 AtomicLogger should 
    * be used within a JVM.  There are no real advantages to using multiple 
    * JavaLogger child classes as they are based on the Java Logging facility
    * which is a singleton with a single global logger.
    * @param file to write all messages to
    */
   //----------------------------------------------------------------
   public AtomicLogger(final String file)
   //----------------------------------------------------------------
   {
      super();
      
      _logFQName = file;

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
      _logFQName = file;

      return;
   }

   /**
    * @param type
    * @param msg
    */
   //----------------------------------------------------------------
   @Override
   public void put(final LoggingLevel type, final String msg)
   //----------------------------------------------------------------
   {
      synchronized (_logLock)
      {
         if (_logFQName != null)
         {
            try
            {
               if (_fileHandler == null)
               {
                  _fileHandler = new FileHandler(_logFQName, true);
                  _fileHandler.setFormatter(new SimpleFormatter());
                  _logger.addHandler(_fileHandler);
               }
            }
            catch (Exception e)
            {
               this.addConsole();
            //this.put(this.TYPE_SEVERE, e.getMessage());
            }
         }
         else
         {
            this.addConsole();
         }

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

         this.close();  // may need to be commented out
      }
      return;
   }
   //----------------------------------------------------------------
   @Override
   public void close()
   //----------------------------------------------------------------
   {
      super.close();

      _fileHandler = null;
      
      return;
   }


}
