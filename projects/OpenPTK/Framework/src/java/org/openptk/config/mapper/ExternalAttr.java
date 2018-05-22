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
public class ExternalAttr extends Attribute implements ExternalAttrIF
//===================================================================
{

   private String _mapTo = null;
   private Mode _mode = Mode.BOTH; // default
   private ExternalModeIF _inbound = null;
   private ExternalModeIF _outbound = null;

   //----------------------------------------------------------------
   public ExternalAttr(final ExternalAttrIF attr)
   //----------------------------------------------------------------
   {
      super(attr);

      _mapTo = attr.getMapTo();
      _inbound = attr.getInbound();
      _outbound = attr.getOutbound();
      _mode = attr.getMode();

      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name)
   //----------------------------------------------------------------
   {
      super(name);
      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name, final Object value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name, final Object[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name, final boolean value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name, final boolean[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name, final Boolean value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name, final Boolean[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name, final int value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name, final int[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name, final Integer value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name, final Integer[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name, final Long value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name, final Long[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name, final String value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   public ExternalAttr(final String name, final String[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      return;
   }

   //----------------------------------------------------------------
   @Override
   public ExternalAttrIF copy()
   //----------------------------------------------------------------
   {
      return new ExternalAttr(this);
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setMode(final Mode mode)
   //----------------------------------------------------------------
   {
      _mode = mode;
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final Mode getMode()
   //----------------------------------------------------------------
   {
      Mode mode = null;

      mode = _mode;

      return mode;
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
   public final synchronized void setInbound(final ExternalModeIF mode)
   //----------------------------------------------------------------
   {
      if ( mode != null)
      {
         _inbound = mode.copy();
      }
      else
      {
         _inbound = null;
      }
      
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final ExternalModeIF getInbound()
   //----------------------------------------------------------------
   {
      return (_inbound == null ? null : _inbound.copy());
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setOutbound(final ExternalModeIF mode)
   //----------------------------------------------------------------
   {
      if (mode != null)
      {
         _outbound = mode.copy();
      }
      else
      {
         _outbound = null;
      }
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final ExternalModeIF getOutbound()
   //----------------------------------------------------------------
   {
      return (_outbound == null ? null : _outbound.copy());
   }
}
