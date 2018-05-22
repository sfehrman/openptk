/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2012-2013 Project OpenPTK
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
package org.openptk.config.mapper;

import java.util.List;
import java.util.Map;

import org.openptk.exception.StructureException;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman
 * 
 * @since 2.2.0
 */
//===================================================================
public interface AttrMapIF
//===================================================================
{
   public static final String SUBCHAR = "/";
   public static final String DATACHAR = "|";

   public StructureIF externalToFramework(StructureIF structExternal) throws StructureException;

   public StructureIF frameworkToExternal(StructureIF structFramework) throws StructureException;

   public String getUniqueId();

   public void setUniqueId(String value);

   public boolean isError();

   public void setError(boolean error);

   public ExternalAttrIF getAttribute(String key, boolean useFwKey);

   public Map<String, ExternalAttrIF> getAttributes();

   public List<String> getAttributesNames(boolean useFwKey);

   public int getAttributesSize();

   public void setAttribute(ExternalAttrIF attr);

}
