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

/**
 *
 * @author Scott Fehrman
 * @since 2.2.0
 */
//===================================================================
public class Datum
//===================================================================
{

   private static final String NULL = "(null)";
   private String _id = null;
   private String _mapTo = null;
   private Processes _processes = null;

   //----------------------------------------------------------------
   public Datum(final Datum datum)
   //----------------------------------------------------------------
   {
      _id = datum.getId();
      _mapTo = datum.getMapTo();
      _processes = datum.getProcesses();

      return;
   }

   //----------------------------------------------------------------
   public Datum(final String id)
   //----------------------------------------------------------------
   {
      if (id != null && id.length() > 0)
      {
         _id = new String(id);
      }
      else
      {
        _id = Integer.toString(this.getClass().hashCode());
      }

      return;
   }

   //----------------------------------------------------------------
   public synchronized Datum copy()
   //----------------------------------------------------------------
   {
      return new Datum(this);
   }

   //----------------------------------------------------------------
   @Override
   public String toString()
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();

      buf.append(_id == null ? NULL : _id).append(": ");
      buf.append("mapto=").append(_mapTo == null ? NULL : _mapTo);

      return buf.toString();
   }

   //----------------------------------------------------------------
   public final String getId()
   //----------------------------------------------------------------
   {
      return (_id != null ? new String(_id) : null);
   }

   //----------------------------------------------------------------
   public final synchronized void setMapTo(final String mapTo)
   //----------------------------------------------------------------
   {
      if (mapTo != null && mapTo.length() > 0)
      {
         _mapTo = new String(mapTo);
      }

      return;
   }

   //----------------------------------------------------------------
   public final String getMapTo()
   //----------------------------------------------------------------
   {
      return (_mapTo != null ? new String(_mapTo) : null);
   }

   //----------------------------------------------------------------
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
   public final Processes getProcesses()
   //----------------------------------------------------------------
   {
      return (_processes != null ? _processes.copy() : null);
   }
}
