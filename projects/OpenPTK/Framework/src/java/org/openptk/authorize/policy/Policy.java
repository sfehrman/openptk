/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010-2011 Project OpenPTK
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
package org.openptk.authorize.policy;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openptk.authorize.Effect;
import org.openptk.authorize.Environment;
import org.openptk.authorize.TargetIF;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.session.SessionType;

/**
 *
 * @author Derrick Harcey
 */
//===================================================================
public  class Policy extends Component implements PolicyIF
//===================================================================
{
   private Map<String, TargetIF> _targets = null;
   private List<SessionType> _sessionTypes = null;
   private List<String> _clientIds = null;
   private Environment _env = null;
   private PolicyMode _mode = null;
   private Effect _effect = null;

   //----------------------------------------------------------------
   public Policy()
   //----------------------------------------------------------------
   {
      super();
      this.setCategory(Category.POLICY);
      _targets = new LinkedHashMap<String, TargetIF>();
      _sessionTypes = new LinkedList<SessionType>();
      _clientIds = new LinkedList<String>();
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final Map<String, TargetIF> getTargets()
   //----------------------------------------------------------------
   {
      return _targets;
   }

   //----------------------------------------------------------------
   @Override
   public final void addTarget(final String id, final TargetIF target)
   //----------------------------------------------------------------
   {
      if ( id !=null && id.length() > 0)
      {
         _targets.put(id, target);
      }
      
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final TargetIF getTarget(final String id)
   //----------------------------------------------------------------
   {
      TargetIF target = null;

      if ( id != null && id.length() > 0 && _targets.containsKey(id))
      {
         target = _targets.get(id);
      }

      return target;
   }

   //----------------------------------------------------------------
   @Override
   public final String[] getTargetIds()
   //----------------------------------------------------------------
   {
      return _targets.keySet().toArray(new String[_targets.size()]);
   }

   //----------------------------------------------------------------
   @Override
   public final void setMode(final PolicyMode mode)
   //----------------------------------------------------------------
   {
      _mode = mode;
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final PolicyMode getMode()
   //----------------------------------------------------------------
   {
      return _mode;
   }

   //----------------------------------------------------------------
   @Override
   public final void setEffect(final Effect effect)
   //----------------------------------------------------------------
   {
      _effect = effect;
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final Effect getEffect()
   //----------------------------------------------------------------
   {
      return _effect;
   }

   //----------------------------------------------------------------
   @Override
   public final void setEnvironment(final Environment env)
   //----------------------------------------------------------------
   {
      _env = env;
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final Environment getEnvironment()
   //----------------------------------------------------------------
   {
      return _env;
   }

   //----------------------------------------------------------------
   @Override
   public final void addSessionType(final SessionType type)
   //----------------------------------------------------------------
   {
      if (!_sessionTypes.contains(type))
      {
         _sessionTypes.add(type);
      }
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final List<SessionType> getSessionTypes()
   //----------------------------------------------------------------
   {
      return _sessionTypes;
   }

   //----------------------------------------------------------------
   @Override
   public final void addClientId(final String id)
   //----------------------------------------------------------------
   {
      if (!_clientIds.contains(id))
      {
         _clientIds.add(id);
      }
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final String[] getClientIds()
   //----------------------------------------------------------------
   {
      return _clientIds.toArray(new String[_clientIds.size()]);
   }
}
