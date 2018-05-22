/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2008-2010 Sun Microsystems, Inc.
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
package org.openptk.authenticate;

import java.util.Map;

import org.openptk.api.State;
import org.openptk.common.Category;
import org.openptk.common.Component;

/**
 *
 * @author Derrick Harcey
 */
//===================================================================
public abstract class Credentials extends Component implements CredentialsIF
//===================================================================
{
   private Map<String, Object> _credsHash = null;

   /**
    * Constructor for a Credentials object using a map of Objects.
    * @param creds
    */
   //----------------------------------------------------------------
   public Credentials(final Map<String, Object> creds)
   //----------------------------------------------------------------
   {
      super();
      this.setCategory(Category.CREDENTIALS);
      _credsHash = creds;

      if (_credsHash == null || _credsHash.isEmpty())
      {
         this.setState(State.NOTEXIST);
         this.setStatus("No Credentials present");
      }
      else
      {
         this.setState(State.VALID);
         this.setStatus("Credentials available");
      }
      return;
   }

   /**
    * Get the credential Object using the name.
    * @param credName name of the credential
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Object getCredential(final String credName)
   //----------------------------------------------------------------
   {
      Object credvalue = null;

      if (credName != null && credName.length() > 0 && _credsHash.containsKey(credName))
      {
         credvalue = _credsHash.get(credName);
      }
      
      return credvalue;
   }
}
