/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010 Sun Microsystems, Inc.
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
package org.openptk.taglib;

import java.io.IOException;

import org.openptk.connection.ConnectionIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class getContextTag extends AbstractTag
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _connName = null;


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setConnection(final String arg)
   //----------------------------------------------------------------
   {
      _connName = arg;
      return;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */

   /**
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected void process() throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":process(): ";
      String contextId = null;
      ConnectionIF conn = null;

      /*
       * Get the current Context associated with the Connection
       */

      conn = this.getConnection(_connName);

      contextId = conn.getContextId();
      
      if (contextId != null && contextId.length() > 0)
      {
         try
         {
            _jspWriter.print(contextId);
         }
         catch (IOException ex)
         {
            throw new Exception(METHOD_NAME + ex.getMessage());
         }
      }
      else
      {
         throw new Exception(METHOD_NAME + ": contextId is null.");
      }

      return;
   }
}
