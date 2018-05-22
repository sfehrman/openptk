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
public class ExternalMode implements ExternalModeIF
//===================================================================
{

   private Processes _processes = null;
   private SubAttributes _subattrs = null;
   private Data _data = null;
   private ExternalAttrIF.Mode _mode = ExternalAttrIF.Mode.BOTH;
   private Kind _kind = Kind.SIMPLE; // default

   //----------------------------------------------------------------
   public ExternalMode()
   //----------------------------------------------------------------
   {
      return;
   }

   //----------------------------------------------------------------
   public ExternalMode(ExternalAttrIF.Mode mode)
   //----------------------------------------------------------------
   {
      _mode = mode;
      return;
   }

   //----------------------------------------------------------------
   public ExternalMode(ExternalModeIF extMode)
   //----------------------------------------------------------------
   {
      if (extMode != null)
      {
         _processes = extMode.getProcesses();
         _subattrs = extMode.getSubAttributes();
         _data = extMode.getData();
         _mode = extMode.getMode();
         _kind = extMode.getKind();
      }
      return;
   }

   //----------------------------------------------------------------
   @Override
   public synchronized ExternalModeIF copy()
   //----------------------------------------------------------------
   {
      return new ExternalMode(this);
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
   public final synchronized void setSubAttributes(final SubAttributes attrs)
   //----------------------------------------------------------------
   {
      if (attrs != null)
      {
         _subattrs = attrs.copy();
      }

      return;
   }

   //----------------------------------------------------------------
   @Override
   public final SubAttributes getSubAttributes()
   //----------------------------------------------------------------
   {
      return (_subattrs != null ? _subattrs.copy() : null);
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

   //----------------------------------------------------------------
   @Override
   public final synchronized void setMode(final ExternalAttrIF.Mode mode)
   //----------------------------------------------------------------
   {
      _mode = mode;
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final ExternalAttrIF.Mode getMode()
   //----------------------------------------------------------------
   {
      ExternalAttrIF.Mode mode = null;

      mode = _mode;

      return mode;
   }
   
   //----------------------------------------------------------------
   @Override
   public final synchronized void setKind(Kind kind)
   //----------------------------------------------------------------
   {
      _kind = kind;
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final Kind getKind()
   //----------------------------------------------------------------
   {
      return _kind;
   }

}
