/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2013 Project OpenPTK
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

/**
 *
 * @author Scott Fehrman
 * @since 2.2.0
 */
//===================================================================
public interface ExternalModeIF
//===================================================================
{

   public static enum Kind
   {

      SIMPLE, // default
      PROCESS,
      SUBATTR,
      DATA
   }

   public ExternalModeIF copy();

   public void setProcesses(Processes processes);

   public Processes getProcesses();

   public void setSubAttributes(SubAttributes attrs);

   public SubAttributes getSubAttributes();

   public void setData(Data data);

   public Data getData();

   public void setMode(ExternalAttrIF.Mode mode);

   public ExternalAttrIF.Mode getMode();

   public void setKind(Kind kind);

   public Kind getKind();
}
