/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2009 Sun Microsystems, Inc.
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

package org.openptk.app.prov.portlets;

import java.util.Properties;
import java.util.StringTokenizer;

//===================================================================
 public class PortalUserEntryDN extends PortalUser implements PortalUserIF
//===================================================================
{
   
   //----------------------------------------------------------------
   @Override
   public String getId(Properties props)
   //----------------------------------------------------------------
   {
      String          retStr   = null;
      String          uniqueId = null;
      String          uid      = null;
      StringTokenizer strtok   = null;
      
      uniqueId = props.getProperty(ProvisioningPortlet.PORTALUSER_UNIQUEID);
      
      if ( uniqueId != null )  // should be:  "uid=jwayne, ou=People, dc=openptk, dc=org"
      {
         strtok = new StringTokenizer(uniqueId,",");
         uid = strtok.nextToken();
         if ( uid != null ) // should be: "uid=jwayne"
         {
            strtok = new StringTokenizer(uid,"=");
            while ( strtok.hasMoreTokens() )
            {
               retStr = strtok.nextToken();
            }
         }
      }
      
      return retStr;
   }   
   
}
