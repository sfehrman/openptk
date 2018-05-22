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

import org.openptk.common.ComponentIF;
import org.openptk.exception.PluginException;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public interface PluginIF extends ComponentIF
//===================================================================
{
   /**
    * @throws PluginException
    */
   public void startup() throws PluginException;

   /**
    * @throws PluginException
    */
   public void shutdown() throws PluginException;

   /**
    * @param structure
    * @return StructureIF
    * @throws PluginException
    */
   public StructureIF execute(StructureIF structure) throws PluginException;
}
