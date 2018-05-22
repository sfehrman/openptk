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
package org.openptk.plugin;

import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.exception.PluginException;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Plugin extends Component implements PluginIF
//===================================================================
{
   //----------------------------------------------------------------
   public Plugin()
   //----------------------------------------------------------------
   {
      super();
      this.setCategory(Category.PLUGIN);
      return;
   }


   /**
    * @throws PluginException
    */
   //----------------------------------------------------------------
   @Override
   public void startup() throws PluginException
   //----------------------------------------------------------------
   {
      return;
   }


   /**
    * @throws PluginException
    */
   //----------------------------------------------------------------
   @Override
   public void shutdown() throws PluginException
   //----------------------------------------------------------------
   {
      return;
   }


   /**
    * @param structure
    * @return StructureIF
    * @throws PluginException
    */
   //----------------------------------------------------------------
   @Override
   public abstract StructureIF execute(StructureIF structure) throws PluginException;
   //----------------------------------------------------------------


   /**
    * @param msg
    * @throws PluginException
    */
   //----------------------------------------------------------------
   public void handleError(final String msg) throws PluginException
   //----------------------------------------------------------------
   {
      String str = null;

      if ( msg == null || msg.length() < 1)
      {
         str = "(null message)";
      }
      else
      {
         str = msg;
      }
      throw new PluginException(str);
   }
}
