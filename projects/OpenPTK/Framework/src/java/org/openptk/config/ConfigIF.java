/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2008-2010 Sun Microsystems, Inc.
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
 * Portions Copyright 2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.config;

import java.util.Map;

import org.openptk.authenticate.AuthenticatorIF;
import org.openptk.authorize.EnforcerIF;
import org.openptk.authorize.decider.DeciderIF;
import org.openptk.authorize.policy.PolicyIF;
import org.openptk.client.ClientIF;
import org.openptk.common.ComponentIF;
import org.openptk.config.mapper.AttrMapIF;
import org.openptk.context.ContextIF;
import org.openptk.context.actions.ActionIF;
import org.openptk.logging.LoggingIF;
import org.openptk.model.ModelIF;
import org.openptk.plugin.PluginIF;
import org.openptk.structure.ConverterIF;
import org.openptk.structure.ConverterType;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public interface ConfigIF extends ComponentIF
//===================================================================
{

   /**
    * @return
    */
   public String getDefaultContextName();

   /**
    * @param name
    * @return
    */
   public ContextIF getContext(String name);

   /**
    * @return
    */
   public Map<String, ContextIF> getContexts();

   /**
    * @return
    */
   public String[] getContextNames();

   /**
    * @return
    */
   public String getLoggerName();

   /**
    * @return
    */
   public Map<String, LoggingIF> getLoggers();

   /**
    * @return
    */
   public Map<String, AuthenticatorIF> getAuthenticators();

   /**
    * @return
    */
   public Map<String, ClientIF> getClients();

   /**
    * @return
    */
   public Map<String, ModelIF> getModels();

   /**
    * @return
    */
   public Map<ConverterType, ConverterIF> getConverters();

   /**
    * @return
    */
   public Map<String, PluginIF> getPlugins();

   /**
    * @return
    */
   public Map<String, ActionIF> getActions();

   /**
    * @return
    */
   public Map<String, PolicyIF> getPolicies();

   /**
    * @return
    */
   public Map<String, DeciderIF> getDeciders();

   /**
    *
    * @return
    */
   public Map<String, EnforcerIF> getEnforcers();
   
   /**
    *
    * @return
    */
   public Map<String, AttrMapIF> getAttrMaps();
}
