/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010 Project OpenPTK
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

package org.openptk.authorize;

import org.openptk.common.Category;
import org.openptk.common.Component;

/**
 *
 * @author Scott Fehrman
 */
//===================================================================
public abstract class Enforcer extends Component implements EnforcerIF
//===================================================================
{
   private Environment _env = null;
   private String _deciderId = null;

   /**
    * Default constructor
    */
   public Enforcer()
   {
      super();
      this.setCategory(Category.ENFORCER);
      return;
   }

   public Enforcer(EnforcerIF enforcer)
   {
      super(enforcer);
      this.setEnvironment(enforcer.getEnvironment());
      this.setDeciderId(enforcer.getDeciderId());
      return;
   }
   
   @Override
   public abstract EnforcerIF copy();

   /**
    * Set the Env enum.
    * @param env
    */
   @Override
   public final void setEnvironment(final Environment env)
   {
      _env = env;
   }

   /**
    * Get the Env enum.
    * @return Environment
    */
   @Override
   public final Environment getEnvironment()
   {
      return _env;
   }

   /**
    * Set the decider id.
    * @param deciderId
    */
   @Override
   public final void setDeciderId(final String deciderId)
   {
      _deciderId = deciderId;
   }

   /**
    * Get the decider id
    * @return String
    */
   @Override
   public final String getDeciderId()
   {
      return _deciderId;
   }
}
