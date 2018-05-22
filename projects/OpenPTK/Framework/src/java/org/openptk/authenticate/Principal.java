/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009-2010 Sun Microsystems, Inc.
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

import org.openptk.api.State;
import org.openptk.common.Category;
import org.openptk.common.Component;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey
 */
//===================================================================
public abstract class Principal extends Component implements PrincipalIF
//===================================================================
{
   private String _contextId = null;
   private CredentialsIF _creds = null;
   private PrincipalType _type = null;

   /**
    * Constructor using credentials.
    * @param creds
    */
   //----------------------------------------------------------------
   public Principal(final CredentialsIF creds)
   //----------------------------------------------------------------
   {
      super();

      this.setCategory(Category.PRINCIPAL);
      this.setState(State.NOTAUTHENTICATED);
      _creds = creds;

      return;
   }

   /**
    * Constructor using a principal.
    * @param principal
    */
   //----------------------------------------------------------------
   public Principal(final PrincipalIF principal)
   //----------------------------------------------------------------
   {
      super(principal);

      if (principal != null)
      {
         this.setContextId(principal.getContextId());
         this.setType(principal.getType());
         _creds = principal.getCredentials();
      }

      return;
   }

   /**
    * Copy the object.  (abstract class, must overide / implement).
    * @return Principal
    */
   //----------------------------------------------------------------
   @Override
   public abstract PrincipalIF copy();
   //----------------------------------------------------------------

   /**
    * @param ctxId
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setContextId(final String ctxId)
   //----------------------------------------------------------------
   {
      _contextId = ctxId;
      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String getContextId()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_contextId != null)
      {
         str = new String(_contextId);  // ALWAYS return a copy of the data
      }

      return str;
   }

   /**
    * @param credName
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Object getCredential(final String credName)
   //----------------------------------------------------------------
   {
      Object credvalue = null;

      /*
       * WARNING:
       * This should return a "copy" of the object
       * to be "completely" thread safe and better
       * support concurrency
       */

      credvalue = _creds.getCredential(credName);

      return credvalue;
   }

   /**
    * Set / replace the credentials object.
    * @param credentials
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setCredentials(final CredentialsIF credentials)
   //----------------------------------------------------------------
   {
      _creds = credentials;
      return;
   }

   /**
    * Get the credentials.
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final CredentialsIF getCredentials()
   //----------------------------------------------------------------
   {
      /*
       * WARNING:
       * ideally this should be a "copy" of the credentials
       * this is returning the reference to the data
       */
      return _creds;
   }

   /**
    * Set the (enumeration) Type.
    * @param type
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setType(final PrincipalType type)
   //----------------------------------------------------------------
   {
      _type = type;
      return;
   }

   /**
    * Get the (enumeration) Type.
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final PrincipalType getType()
   //----------------------------------------------------------------
   {
      return _type;
   }
}
