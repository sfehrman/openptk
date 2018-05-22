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
 *
 * Stores "Match" data for a <tt><Data></tt> Element
 * <pre>
 * <Attribute id="addresses" multivalued="true">
 *    <Data id="address">
 *    ...
 *       <Match id="work_address" datum="type" value="work" mapfrom="streetAddress" mapto="address"/>
 *    </Data>
 * </Attribute>
 * </pre>
 */
//===================================================================
public class Match
//===================================================================
{
   private static final String NULL = "(null)";
   
   private String _id = null;
   private String _value = null;
   private String _datumId = null;
   private String _mapTo = null;
   private String _mapFrom = null;

   //----------------------------------------------------------------
   public Match(final Match match)
   //----------------------------------------------------------------
   {
      _id = match.getId();
      _value = match.getValue();
      _datumId = match.getDatumId();
      _mapTo = match.getMapTo();
      _mapFrom = match.getMapFrom();

      return;
   }

   //----------------------------------------------------------------
   public Match(final String id)
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
   public synchronized Match copy()
   //----------------------------------------------------------------
   {
      return new Match(this);
   }
   
   //----------------------------------------------------------------
   @Override
   public String toString()
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();
      
      buf.append(_id == null ? NULL : _id).append(": ");
      buf.append("value=").append(_value == null ? NULL : _value).append(", ");
      buf.append("datumid=").append(_datumId == null ? NULL : _datumId).append(", ");
      buf.append("mapfrom=").append(_mapFrom == null ? NULL : _mapFrom).append(", ");
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
   public final String getValue()
   //----------------------------------------------------------------
   {
      return (_value != null ? new String(_value) : null);
   }

   //----------------------------------------------------------------
   public final synchronized void setValue(final String value)
   //----------------------------------------------------------------
   {
      if (value != null && value.length() > 0)
      {
         _value = new String(value);
      }

      return;
   }

   //----------------------------------------------------------------
   public final synchronized void setMapFrom(final String mapFrom)
   //----------------------------------------------------------------
   {
      if (mapFrom != null && mapFrom.length() > 0)
      {
         _mapFrom = new String(mapFrom);
      }
      return;
   }

   //----------------------------------------------------------------
   public final String getMapFrom()
   //----------------------------------------------------------------
   {
      return (_mapFrom != null ? new String(_mapFrom) : null);
   }

   //----------------------------------------------------------------
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
   public final String getMapTo()
   //----------------------------------------------------------------
   {
      return (_mapTo != null ? new String(_mapTo) : null);
   }

   //----------------------------------------------------------------
   public final synchronized void setDatumId(String datumId)
   //----------------------------------------------------------------
   {
      if (datumId != null && datumId.length() > 0)
      {
         _datumId = new String(datumId);
      }

      return;
   }

   //----------------------------------------------------------------
   public final String getDatumId()
   //----------------------------------------------------------------
   {
      return (_datumId != null ? new String(_datumId) : null);
   }
}
