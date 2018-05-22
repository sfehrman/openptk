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

import org.openptk.connection.ConnectionIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class setContextTag extends AbstractTag
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _connName = null;
   private String _value = null;


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


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setValue(final String arg)
   //----------------------------------------------------------------
   {
      _value = arg;
      return;
   }


   /**
    * Set the Context associated with the Connection.
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected void process() throws Exception
   //----------------------------------------------------------------
   {
      ConnectionIF conn = null;

      conn = this.getConnection(_connName);

      if (conn != null)
      {
         if (_value != null && _value.length() > 0)
         {
            conn.setContextId(_value);
         }
         else
         {
            throw new Exception(CLASS_NAME + ": Value is null.");
         }
      }
      else
      {
         throw new Exception(CLASS_NAME + ": Connection '" + _connName + "' is null.");
      }

      return;
   }
}
