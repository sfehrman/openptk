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
package org.openptk.client;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class BasicClient extends Client
//===================================================================
{

   /**
    * Constructor using a unique id only.
    * @param uniqueId String
    */
   //----------------------------------------------------------------
   public BasicClient(final String uniqueId)
   //----------------------------------------------------------------
   {
      super(uniqueId);
      this.setDescription("Basic Client (no secret)");
      return;
   }

   /**
    * Constructor using a unique id and secret.
    * @param uniqueId String
    * @param secret String
    */
   //----------------------------------------------------------------
   public BasicClient(final String uniqueId, final String secret)
   //----------------------------------------------------------------
   {
      super(uniqueId, secret);
      this.setDescription("Basic Client (with secret)");
      return;
   }
}
