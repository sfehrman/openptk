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

import org.openptk.api.Attribute;

/**
 *
 * @author Scott Fehrman
 * @since 2.2.0
 */
//===================================================================
public class ExternalSubAttr extends Attribute implements ExternalSubAttrIF
//===================================================================
{

   private String _mapTo = null;
   private Processes _processes = null;
   private Data _data = null;

   //----------------------------------------------------------------
   public ExternalSubAttr(final ExternalSubAttrIF subattr)
   //----------------------------------------------------------------
   {
      super(subattr);

      _mapTo = subattr.getMapTo();
      _processes = subattr.getProcesses();
      _data = subattr.getData();

      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name)
   //----------------------------------------------------------------
   {
      super(name);
      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name, final Object value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name, final Object[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name, final boolean value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name, final boolean[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name, final Boolean value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name, final Boolean[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name, final int value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name, final int[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name, final Integer value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name, final Integer[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name, final Long value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name, final Long[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name, final String value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalSubAttr(final String name, final String[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   @Override
   public ExternalSubAttrIF copy()
   //----------------------------------------------------------------
   {
      return new ExternalSubAttr(this);
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setMapTo(final String mapTo)
   //----------------------------------------------------------------
   {
      if (mapTo != null && mapTo.length() > 0)
      {
         _mapTo = mapTo;
      }
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final String getMapTo()
   //----------------------------------------------------------------
   {
      return (_mapTo != null ? new String(_mapTo) : null);
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setProcesses(final Processes processes)
   //----------------------------------------------------------------
   {
      if (processes != null)
      {
         _processes = processes.copy();
      }

      return;
   }

   //----------------------------------------------------------------
   @Override
   public final Processes getProcesses()
   //----------------------------------------------------------------
   {
      return (_processes != null ? _processes.copy() : null);
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setData(Data data)
   //----------------------------------------------------------------
   {
      if (data != null)
      {
         _data = data.copy();
      }

      return;
   }

   //----------------------------------------------------------------
   @Override
   public final Data getData()
   //----------------------------------------------------------------
   {
      return (_data != null ? _data.copy() : null);
   }

}
