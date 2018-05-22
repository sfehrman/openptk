/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009 Sun Microsystems, Inc.
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
package org.openptk.sample.client.api;

import java.util.Enumeration;

import org.openptk.connection.ConnectionIF;
import org.openptk.connection.ConnectionIF.Session;
import org.openptk.exception.ConnectionException;
import org.openptk.logging.Logger;

/**
 *
 * @author tls
 */
//===================================================================
public abstract class TestClient
//===================================================================
{
   private static final String NULL = "(null)";

   //----------------------------------------------------------------
   @SuppressWarnings("rawtypes")
   public String printConnectionInfo(ConnectionIF connection) throws Exception
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();
      Enumeration eprops = null;
      String propName = null;
      String propValue = null;
      String status = null;
      String[] attrNames = null;

      if (connection == null)
      {
         throw new Exception("printConnectionInfo(): connection is null");
      }

      status = connection.getStatus();

      buf.append("***********************************************************\n");
      buf.append("*                 CONNECTION INFO                         *\n");
      buf.append("***********************************************************\n");

      buf.append("  UniqueId: " + connection.getUniqueId().toString() + "\n");
      buf.append("     State: " + connection.getState().toString() + "\n");
      buf.append("    Status: " + (status != null ? status : NULL) + "\n");
      buf.append("   Changed: " + connection.hasSessionChanged() + "\n");
      buf.append("Properties: \n");

      eprops = connection.getProperties().propertyNames();

      while (eprops.hasMoreElements())
      {
         propName = (String) eprops.nextElement();
         propValue = connection.getProperty(propName);
         buf.append("          : " + propName + "=" + propValue + "\n");
      }

      return buf.toString();
   }

   //----------------------------------------------------------------
   String printContextInfo(ConnectionIF connection) throws Exception
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();
      String contextId = null;
      String[] contextIds = null;

      if (connection == null)
      {
         throw new Exception("printContextInfo(): connection is null");
      }

      contextId = connection.getContextId();

      buf.append("***********************************************************\n");
      buf.append("*                  CONTEXT INFO                           *\n");
      buf.append("***********************************************************\n");
      buf.append("   Default: " + (contextId != null ? contextId : NULL) + "\n");

      buf.append(" Available: \n");
      contextIds = connection.getContextIds();

      if (contextIds != null && contextIds.length > 0)
      {
         for (int i = 0; i < contextIds.length; i++)
         {
            try
            {
               connection.setContextId(contextIds[i]);
               buf.append("          : " + connection.getContextId() + "\n");
            }
            catch (ConnectionException ex)
            {
               Logger.logError(ex.getMessage());
            }
         }
      }
      return buf.toString();
   }

   //----------------------------------------------------------------
   String printSessionInfo(ConnectionIF connection) throws Exception
   //----------------------------------------------------------------
   {
      String value = null;
      StringBuilder buf = new StringBuilder();

      if (connection == null)
      {
         throw new Exception("printSessionInfo(): connection is null");
      }

      buf.append("***********************************************************\n");
      buf.append("*                  SESSION INFO                           *\n");
      buf.append("***********************************************************\n");

      try
      {
         value = connection.getSessionData(Session.ID);
         buf.append("  uniqueId: ").append((value != null ? value : NULL)).append("\n");

         value = connection.getSessionData(Session.TYPE);
         buf.append("      type: ").append((value != null ? value : NULL)).append("\n");

         value = connection.getSessionData(Session.PRINCIPAL);
         buf.append(" principal: ").append((value != null ? value : NULL)).append("\n");

         value = connection.getSessionData(Session.AUTHEN);
         buf.append("    authen: ").append((value != null ? value : NULL)).append("\n");
      }
      catch (ConnectionException ex)
      {
         Logger.logError(ex.getMessage());
      }

      return buf.toString();
   }
}
