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

//===================================================================
public interface LoggingIF
//===================================================================
{

   public static final String PROP_CLASSNAME = "log.classname";
   public static final String PROP_FILENAME = "log.filename";

   public void addConsole();

   /**
    * @param filename
    * @throws Exception
    */
   public void addFile(String filename) throws Exception;

   public void close();

   public void log(String msg);
   
   /**
    * Emits the passed message to the Logger at logging level of <b>INFO</b>.  
    * These messages should represent messages that are of a urgent nature that 
    * pertain to system errors or failures.
    * 
    * @param msg Message to be logged to logging subsystem
    */
   public void logInfo(String msg);
   
   /**
    * Emits the passed message to the Logger at logging level of <b>WARNING</b>.  
    * These messages should represent messages that are of a urgent nature that 
    * pertain to system errors or failures.
    * 
    * @param msg Message to be logged to logging subsystem
    */
   public void logWarning(String msg);
   
   /**
    * Emits the passed message to the Logger at logging level of <b>ERROR</b>.  
    * These messages should represent messages that are of a urgent nature that 
    * pertain to system errors or failures.
    * 
    * @param msg Message to be logged to logging subsystem
    */
   public void logError(String msg);

   /**
    * @param msg Message to be logged to logging subsystem
    */
   public void put(String msg);
   
   /**
    * @param level LoggingLevel of message to be logged
    * @param msg Message to be logged to logging subsystem
    */
   public void put(LoggingLevel level, String msg);

   public String getId();
}
