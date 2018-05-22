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

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.config.mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Scott Fehrman
 * @since 2.2.0
 */
//===================================================================
public class Data
//===================================================================
{

   private String _id = null;
   private Map<String, Datum> _datum = null; // <name, datum>
   private Map<String, Match> _match = null; // <name, match>

   //----------------------------------------------------------------
   public Data(String id)
   //----------------------------------------------------------------
   {
      _datum = new LinkedHashMap<String, Datum>();
      _match = new LinkedHashMap<String, Match>();

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
   public Data(Data data)
   //----------------------------------------------------------------
   {
      _datum = new LinkedHashMap<String, Datum>();
      _match = new LinkedHashMap<String, Match>();

      if (data != null)
      {
         _id = data.getId();

         for (String datumId : data.getDatumIds())
         {
            _datum.put(datumId, data.getDatum(datumId));
         }
         
         for (String matchId : data.getMatchIds())
         {
            _match.put(matchId, data.getMatch(matchId));
         }
      }

      return;
   }

   //----------------------------------------------------------------
   public synchronized Data copy()
   //----------------------------------------------------------------
   {
      return new Data(this);
   }
   
   //----------------------------------------------------------------
   public final String getId()
   //----------------------------------------------------------------
   {
      return (_id != null ? new String(_id) : null);
   }

   //----------------------------------------------------------------
   public final synchronized void addDatum(Datum datum)
   //----------------------------------------------------------------
   {
      if (datum != null)
      {
         _datum.put(datum.getId(), datum.copy());
      }

      return;
   }

   //----------------------------------------------------------------
   public final Datum getDatum(String name)
   //----------------------------------------------------------------
   {
      Datum datum = null;

      if (name != null && name.length() > 0 && _datum.containsKey(name))
      {
         datum = _datum.get(name).copy();
      }

      return datum;
   }

   //----------------------------------------------------------------
   public final Set<String> getDatumIds()
   //----------------------------------------------------------------
   {
      return _datum.keySet();
   }

   //----------------------------------------------------------------
   public final synchronized void addMatch(Match match)
   //----------------------------------------------------------------
   {
      if (match != null)
      {
         _match.put(match.getId(), match.copy());
      }

      return;
   }

   //----------------------------------------------------------------
   public final Match getMatch(String name)
   //----------------------------------------------------------------
   {
      Match match = null;

      if (name != null && name.length() > 0 && _match.containsKey(name))
      {
         match = _match.get(name).copy();
      }

      return match;
   }

   //----------------------------------------------------------------
   public final Set<String> getMatchIds()
   //----------------------------------------------------------------
   {
      return _match.keySet();
   }
}
