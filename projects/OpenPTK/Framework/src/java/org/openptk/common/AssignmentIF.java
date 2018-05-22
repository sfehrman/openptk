/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2012 Project OpenPTK
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
package org.openptk.common;

/**
 *
 * @author Scott Fehrman
 * @since 2.2
 */
//===================================================================
public interface AssignmentIF extends ComponentIF
//===================================================================
{

   public static enum Type
   {

      HTTP_HEADER,
      PROPERTY;
   }

   public ComponentIF getSource();

   public ComponentIF getDestination();

   public void setSource(ComponentIF component);

   public void setDestination(ComponentIF component);

   @Override
   public AssignmentIF copy();
}
