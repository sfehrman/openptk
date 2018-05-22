/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2012-2013 Project OpenPTK
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
package org.openptk.config.mapper;

/**
 *
 * @author Scott Fehrman
 * @since 2.2.0
 */
//===================================================================
public class Processes
//===================================================================
{

   private String _value = null;

   //----------------------------------------------------------------
   public Processes()
   //----------------------------------------------------------------
   {
      return;
   }

   //----------------------------------------------------------------
   public Processes(Processes processes)
   //----------------------------------------------------------------
   {
      String value = null;

      if (processes != null)
      {
         value = processes.getValue();
         
         if (value != null && value.length() > 0)
         {
            _value = new String(value);
         }
      }

      return;
   }
   
   //----------------------------------------------------------------
   public synchronized Processes copy()
   //----------------------------------------------------------------
   {
      return new Processes(this);
   }

   //----------------------------------------------------------------
   public final synchronized void setValue(String value)
   //----------------------------------------------------------------
   {
      if (value != null && value.length() > 0)
      {
         _value = new String(value);
      }
      else
      {
         _value = null;
      }
      
      return;
   }

   //----------------------------------------------------------------
   public final String getValue()
   //----------------------------------------------------------------
   {
      String value = null;
      
      if (_value != null && _value.length() > 0)
      {
         value = new String(_value);
      }
      
      return value;
   }
}