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

import java.util.List;

import org.openptk.api.Opcode;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;

/**
 *
 * @author Scott Fehrman
 */
//===================================================================
public interface TargetIF extends ComponentIF
//===================================================================
{
   public static final String PROP_FULL_URL = "FULLURL";
   public static final String PROP_PROTOCOL = "PROTOCOL";
   public static final String PROP_SERVER = "SERVER";
   public static final String PROP_PORT = "PORT";
   public static final String PROP_CONTEXTPATH = "CONTEXTPATH";
   public static final String PROP_RELATIVEPATH = "RELATIVEPATH";
   public static final String VALUE_PRASE_STRING = "/";

   public void setType(TargetType type);

   public TargetType getType();

   public void setValue(String value);

   public String getValue();

   public String[] getParsedValue();

   public void addOperation(Operation operation);

   public List<Operation> getOperations();

   public Opcode getOpcode();
}
