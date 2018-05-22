/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2008 Sun Microsystems, Inc.
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

/**
 *
 * @author Tery Sigle
 */
public class CLIException extends Exception
{
   public static final long serialVersionUID = 200L;
   private String _message = null;

   /**
    * Create a new CLIException, use the provided _message.
    * @param _message a description of the exception
    */

   //----------------------------------------------------------------
   public CLIException(String message)
   //----------------------------------------------------------------
   {
      super(message);
      this._message = message;
   }


   /**
    * Create a new CLIException, use the provided Exception.
    * @param ex an existing Exception (Throwable)
    */

   //----------------------------------------------------------------
   public CLIException(Throwable ex)
   //----------------------------------------------------------------
   {
      super(ex);
      this._message = ex.getMessage();
   }


   /**
    * Get the _message.
    * @return String the _message
    */

   //----------------------------------------------------------------
   @Override
   public String getMessage()
   //----------------------------------------------------------------
   {
      return _message;
   }
}
