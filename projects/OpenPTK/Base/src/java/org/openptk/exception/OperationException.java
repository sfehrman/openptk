/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2008-2009 Sun Microsystems, Inc.
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

package org.openptk.exception;


/**
 * Provision Framework Operation related Exceptions.
 */

//===================================================================
public class OperationException extends Exception
//===================================================================
{
   public static final long serialVersionUID = 200L;
   
   /**
    * Create a new OperationException, use the provided message.
    * 
    * @param message a description of the exception
    */

   //----------------------------------------------------------------
   public OperationException(String message)
   //----------------------------------------------------------------
   {
      super(message);
   }
   
   /**
    * Create a new OperationException, use the provided Exception.
    * 
    * @param ex an existing Exception (Throwable)
    */
   
   //----------------------------------------------------------------
   public OperationException(Throwable ex)
   //----------------------------------------------------------------
   {
      super(ex);
   }
   
}
