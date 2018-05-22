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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

//===================================================================
public class OpenPTKLogFormatter extends Formatter
//===================================================================
{
   private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS z";
   private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

   //----------------------------------------------------------------
   @Override
   public String format(final LogRecord r)
   //----------------------------------------------------------------
   {
      /*
       * The format of the log messages is defined below.  Future changes may
       * include other items, such as DATE.  Additionally, we may want to
       * emit different log messages depending on what level the error is.
       */

      String currDateTime = OpenPTKLogFormatter.dateFormat.format(new Date());

      if (r == null)
      {
         return "SEVERE: " + currDateTime + ": " + "(null Log Record)\n";
      }
      else
      {
         return "" + r.getLevel() + ": " + currDateTime + ": " + r.getMessage() + "\n";
      }

   }
}
