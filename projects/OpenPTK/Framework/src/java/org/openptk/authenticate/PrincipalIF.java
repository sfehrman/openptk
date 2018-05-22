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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.authenticate;

import org.openptk.common.ComponentIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public interface PrincipalIF extends ComponentIF
//===================================================================
{
   public static final String ATTR_NAME_CREDENTIALS = "credentials";

   /**
    * Make a copy of the principal.
    * @return PrincipalIF
    */
   @Override
   public PrincipalIF copy();

   /**
    * Set the Context Id.
    * @param ctxId
    */
   public void setContextId(String ctxId);

   /**
    * Get the Context Id.
    * @return
    */
   public String getContextId();

   /**
    * Set the credentials object.
    * @param credentials
    */
   public void setCredentials(CredentialsIF credentials);

   /**
    * Get a object (by name) from the credential.
    * @param credName
    * @return
    */
   public Object getCredential(String credName);

   /**
    * Get the credential.
    * @return
    */
   public CredentialsIF getCredentials();

   /**
    * Set the principal type.
    * @param type
    */
   public void setType(PrincipalType type);

   /**
    * Get the principal type.
    * @return
    */
   public PrincipalType getType();
}
