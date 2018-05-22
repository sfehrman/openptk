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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.client;

import org.openptk.common.ComponentIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public interface ClientIF extends ComponentIF
//===================================================================
{
   /**
    * Set the secret.
    * @param str
    * @throws Exception
    */
   public void setSecret(String str) throws Exception;

   /**
    * Get the secret.
    * @return
    */
   public String getSecret();

   /**
    * Add a context.
    * @param contextId
    */
   public void addContextId(String contextId);

   /**
    * Get all the contexts.
    * @return
    */
   public String[] getContextIds();

   /**
    * Set the default context.
    * @param contextId
    */
   public void setDefaultContextId(String contextId);

   /**
    * Get the default context.
    * @return
    */
   public String getDefaultContextId();

   /**
    * Add an authenticator.
    * @param authenId
    */
   public void addAuthenticatorId(String authenId);

   /**
    * Get all the authenticators.
    * @return
    */
   public String[] getAuthenticatorIds();
}
