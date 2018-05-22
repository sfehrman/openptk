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
public class SubAttributes
//===================================================================
{

   private Map<String, ExternalSubAttrIF> _subAttrs = null; // <attr name, attr>

   //----------------------------------------------------------------
   public SubAttributes()
   //----------------------------------------------------------------
   {
      _subAttrs = new LinkedHashMap<String, ExternalSubAttrIF>();
      return;
   }

   //----------------------------------------------------------------
   public SubAttributes(SubAttributes subattrs)
   //----------------------------------------------------------------
   {
      _subAttrs = new LinkedHashMap<String, ExternalSubAttrIF>();

      if (subattrs != null)
      {
         for (String name : subattrs.getNames())
         {
            _subAttrs.put(name, subattrs.getSubAttribute(name));
         }
      }

      return;
   }
   
   //----------------------------------------------------------------
   public synchronized SubAttributes copy()
   //----------------------------------------------------------------
   {
      return new SubAttributes(this);
   }

   //----------------------------------------------------------------
   public final synchronized void add(String name, ExternalSubAttrIF subattr)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0 && subattr != null)
      {
         _subAttrs.put(name, subattr.copy());
      }
      return;
   }

   //----------------------------------------------------------------
   public final Set<String> getNames()
   //----------------------------------------------------------------
   {
      return _subAttrs.keySet();
   }

   //----------------------------------------------------------------
   public final ExternalSubAttrIF getSubAttribute(String name)
   //----------------------------------------------------------------
   {
      ExternalSubAttrIF subattr = null;

      if (name != null && name.length() > 0 && _subAttrs.containsKey(name))
      {
         subattr = _subAttrs.get(name).copy();
      }

      return subattr;
   }
}
