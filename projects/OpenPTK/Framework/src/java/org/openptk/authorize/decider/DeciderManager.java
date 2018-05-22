/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009-2010 Sun Microsystems, Inc.
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
package org.openptk.authorize.decider;

import java.util.Map;

import org.openptk.authorize.EnforcerIF;
import org.openptk.authorize.policy.PolicyIF;
import org.openptk.exception.AuthorizationException;
import org.openptk.exception.ConfigurationException;

/**
 *
 * @author Derrick Harcey
 */
//===================================================================
public class DeciderManager
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private Map<String, EnforcerIF> _enforcers;
   private Map<String, DeciderIF> _deciders;
   private Map<String, PolicyIF> _policies;
   private String[] _policyIdArray = null;

   /**
    *
    * @param enforcers
    * @param deciders
    */
   //----------------------------------------------------------------
   public DeciderManager(final Map<String, EnforcerIF> enforcers,
      final Map<String, DeciderIF> deciders,
      final Map<String, PolicyIF> policies)
   //----------------------------------------------------------------
   {
      _enforcers = enforcers;
      _deciders = deciders;
      _policies = policies;

      _policyIdArray = _policies.keySet().toArray(new String[_policies.size()]);

      return;
   }

   /**
    * Get a decider using the enforcer id.
    * @param environment
    * @returns DeciderIF
    */
   //----------------------------------------------------------------
   public final DeciderIF getDecider(final String enforcerId) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getDecider(): ";
      String deciderId = null;
      EnforcerIF enforcer = null;
      DeciderIF decider = null;

      if (enforcerId == null || enforcerId.length() < 1)
      {
         this.handleError(METHOD_NAME + "Enforcer Id is null");
      }

      if (!_enforcers.containsKey(enforcerId))
      {
         this.handleError(METHOD_NAME + "Enforcer not found for '"
            + enforcerId + "'.");
      }

      enforcer = _enforcers.get(enforcerId).copy();
      deciderId = enforcer.getDeciderId();

      decider = _deciders.get(deciderId).copy();
      decider.setEnvironment(enforcer.getEnvironment());

      /*
       * if the "decider" is an instance of BasicDecider (internal)
       * then set class specific information
       */

      if (decider instanceof BasicDecider)
      {
         try
         {
            this.setBasicDecider((BasicDecider) decider);
         }
         catch (AuthorizationException ex)
         {
            this.handleError(ex.getMessage());
         }
      }

      return decider;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */

   //----------------------------------------------------------------
   private void setBasicDecider(final BasicDecider decider) throws AuthorizationException
   //----------------------------------------------------------------
   {
      String policyId = null;
      PolicyIF policy = null;

      /*
       * Add the policies that have the same Environment as the decider
       */

      for (int i = 0; i < _policyIdArray.length; i++)
      {
         policyId = _policyIdArray[i];
         policy = _policies.get(policyId);
         if (policy.getEnvironment() == decider.getEnvironment())
         {
            decider.addPolicy(policyId, policy);
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private void handleError(final String msg) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String str = null;

      if (msg != null && msg.length() > 0)
      {
         str = msg;
      }
      else
      {
         str = "(null message)";
      }

      throw new ConfigurationException(str);
   }
}
