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
package org.openptk.taglib;

import java.io.IOException;

import org.openptk.connection.ConnectionIF;
import org.openptk.connection.ConnectionIF.Session;

/**
 *
 * @author Scott Fehrman
 */
//===================================================================
public class getConnectionDataTag extends AbstractTag
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _connection = null;
   private String _name = null;
   private String _var = null;
   private String[] _names = null;
   private final Session[] _sessions = Session.values();

   //----------------------------------------------------------------
   public getConnectionDataTag()
   //----------------------------------------------------------------
   {
      super();

      _names = new String[_sessions.length];
      for (int i = 0; i < _sessions.length; i++)
      {
         _names[i] = _sessions[i].toString();
      }

      return;
   }

   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setConnection(final String arg)
   //----------------------------------------------------------------
   {
      _connection = arg;
      return;
   }

   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setName(final String arg)
   //----------------------------------------------------------------
   {
      _name = arg;
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
      boolean bFound = false;
      String METHOD_NAME = CLASS_NAME + ":process(): ";
      String dataValue = null;
      ConnectionIF conn = null;
      Session session = null;

      for ( int i=0 ; i<_names.length ; i++)
      {
         if ( _names[i].equalsIgnoreCase(_name))
         {
            bFound = true;
            session = _sessions[i];
         }
      }

      if (!bFound)
      {
         throw new Exception(METHOD_NAME + "Data name '" + _name +
            "' is not valid, must be one of these : " + _names.toString());
      }

      conn = this.getConnection(_connection);

      dataValue = conn.getSessionData(session);

      if (dataValue == null)
      {
         dataValue = "";
      }

      if (_var != null && _var.length() > 0)
      {
         this.setString(_var, dataValue);
      }
      else
      {
         try
         {
            _jspWriter.print(dataValue);
         }
         catch (IOException ex)
         {
            throw new Exception(METHOD_NAME + ex.getMessage());
         }
      }

      return;
   }
}
