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

import java.util.LinkedList;
import java.util.List;

import org.openptk.api.Opcode;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.common.Operation;
import org.openptk.util.StringUtil;

/**
 *
 * @author Scott Fehrman
 */
//===================================================================
public abstract class Target extends Component implements TargetIF
//===================================================================
{
   private TargetType _type = null;
   private String _value = null;
   private String[] _valueArray = null;
   private List<Operation> _operations = null;
   protected Opcode _opcode = null;

   //----------------------------------------------------------------
   public Target()
   //----------------------------------------------------------------
   {
      super();
      this.setCategory(Category.TARGET);
      _operations = new LinkedList<Operation>();
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setType(final TargetType type)
   //----------------------------------------------------------------
   {
      _type = type;
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final TargetType getType()
   //----------------------------------------------------------------
   {
      return _type;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void addOperation(final Operation operation)
   //----------------------------------------------------------------
   {
      if (!_operations.contains(operation))
      {
         _operations.add(operation);
      }
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final List<Operation> getOperations()
   //----------------------------------------------------------------
   {
      return _operations;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(final String value)
   //----------------------------------------------------------------
   {
      _value = value;
      _valueArray = StringUtil.stringToArray(value, VALUE_PRASE_STRING);
      return;
   }

   //----------------------------------------------------------------
   @Override
   public String getValue()
   //----------------------------------------------------------------
   {
      return _value;
   }

   //----------------------------------------------------------------
   @Override
   public String[] getParsedValue()
   //----------------------------------------------------------------
   {
      return _valueArray;
   }

   //----------------------------------------------------------------
   @Override
   public Opcode getOpcode()
   //----------------------------------------------------------------
   {
      return _opcode;
   }
}
