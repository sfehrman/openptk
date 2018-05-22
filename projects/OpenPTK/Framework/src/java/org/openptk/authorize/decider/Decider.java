/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010-2011 Project OpenPTK
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
package org.openptk.authorize.decider;

import org.openptk.api.Opcode;
import org.openptk.authorize.Environment;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.common.Operation;
import org.openptk.exception.AuthorizationException;

/**
 *
 * @author Derrick Harcey
 */
//===================================================================
public abstract class Decider extends Component implements DeciderIF
//===================================================================
{
   private Environment _environment = null;

   /**
    * Default constructor
    */
   //----------------------------------------------------------------
   public Decider()
   //----------------------------------------------------------------
   {
      super();
      this.setCategory(Category.DECIDER);
      return;
   }

   //----------------------------------------------------------------
   public Decider(DeciderIF decider)
   //----------------------------------------------------------------
   {
      super(decider);
      this.setEnvironment(decider.getEnvironment());
      return;
   }

   @Override
   public abstract DeciderIF copy();

   //----------------------------------------------------------------
   @Override
   public final void setEnvironment(final Environment env)
   //----------------------------------------------------------------
   {
      _environment = env;
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final Environment getEnvironment()
   //----------------------------------------------------------------
   {
      return _environment;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   //----------------------------------------------------------------
   protected final Opcode getOpcode(Operation operation)
   //----------------------------------------------------------------
   {
      Opcode opcode = null;

      switch (operation)
      {
         case CREATE:
            opcode = Opcode.CREATE;
            break;
         case READ:
            opcode = Opcode.READ;
            break;
         case UPDATE:
            opcode = Opcode.UPDATE;
            break;
         case DELETE:
            opcode = Opcode.DELETE;
            break;
         case SEARCH:
            opcode = Opcode.SEARCH;
            break;
         case PWDCHANGE:
            opcode = Opcode.PWDCHANGE;
            break;
         case PWDRESET:
            opcode = Opcode.PWDRESET;
            break;
         case PWDFORGOT:
            opcode = Opcode.PWDFORGOT;
            break;
         case AUTHENTICATE:
            break;
         default:
            break;
      }

      return opcode;
   }

   //----------------------------------------------------------------
   protected final void handleError(final String str) throws AuthorizationException
   //----------------------------------------------------------------
   {
      String msg = null;
      if (str == null || str.length() < 1)
      {
         msg = "(null message)";
      }
      else
      {
         msg = str;
      }

      throw new AuthorizationException(msg);
   }
}
