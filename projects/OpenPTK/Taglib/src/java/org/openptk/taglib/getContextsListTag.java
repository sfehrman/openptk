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

import java.util.LinkedList;
import java.util.List;

import org.openptk.connection.ConnectionIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class getContextsListTag extends AbstractTag
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _connName = null;
   private String _var = null;
   private String _size = null;


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
   public void setVar(final String arg)
   //----------------------------------------------------------------
   {
      _var = arg;
      return;
   }


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setSizevar(final String arg)
   //----------------------------------------------------------------
   {
      _size = arg;
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
      String[] array = null;
      List<String> list = null;
      ConnectionIF conn = null;

      conn = this.getConnection(_connName);

      /*
       * Get the List of available Contexts
       */

      array = conn.getContextIds();
      if (array != null && array.length > 0)
      {
         list = new LinkedList<String>();

         for (int i = 0; i < array.length; i++)
         {
            list.add(array[i]);
         }

         this.setContextList(_var, list);

         if (_size != null && _size.length() > 0)
         {
            this.setString(_size, Integer.toString(list.size()));
         }
      }
      else
      {
         throw new Exception(METHOD_NAME + ": Connection '" + _connName + "' has no Contexts.");
      }

      return;
   }
}
