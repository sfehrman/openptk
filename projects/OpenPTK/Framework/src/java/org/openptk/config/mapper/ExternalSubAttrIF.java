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
package org.openptk.config.mapper;

import org.openptk.api.AttributeIF;

/**
 *
 * @author Scott Fehrman
 * @since 2.2.0
 */
//===================================================================
public interface ExternalSubAttrIF extends AttributeIF
//===================================================================
{

   @Override
   public ExternalSubAttrIF copy();

   public void setMapTo(String mapTo);

   public String getMapTo();

   public void setProcesses(Processes processes);

   public Processes getProcesses();

   public void setData(Data data);

   public Data getData();

}
