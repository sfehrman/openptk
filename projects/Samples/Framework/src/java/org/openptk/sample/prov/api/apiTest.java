/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2010 Sun Microsystems, Inc.
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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.sample.prov.api;

import java.util.Properties;

import org.openptk.engine.EngineIF;

//===================================================================
abstract class apiTest
//===================================================================
{
   protected String CONFIG = "openptk.xml"; // ../../OpenPTK/Server/config/openptk.xml
   protected String CONTEXT_PERSON_SPML1 = "Person-SunIdm-SPML1";
   protected String CONTEXT_PERSON_SPML2 = "Person-SunIdm-SPML2";
   protected String CONTEXT_PERSON_OIM_SPML2 = "Person-OIM-SPML2";
   protected String CONTEXT_PERSON_SPE = "Person-SunIdm-SPE";
   protected String CONTEXT_EMPLOYEES_JNDI = "Employees-OpenDS-JNDI";
   protected String CONTEXT_CUSTOMERS_JNDI = "Customers-OpenDS-JNDI";
   protected String CONTEXT_LOCATIONS_JNDI = "Locations-OpenDS-JNDI";
   protected String CONTEXT_EMPLOYEES_MYSQL = "Employees-MySQL-JDBC";
   protected String CONTEXT_EMPLOYEES_ORACLE = "Employees-Oracle-JDBC";
   protected String CONTEXT_EMPLOYEES_UNBOUND = "Employees-UnboundID-LDAP";
   protected String CONTEXT_EMPLOYEES_EMBED = "Employees-Embed-JDBC";
   protected String CONTEXT_REGISTER_OIM = "Register-Oracle-IdMgr";
   protected String CONTEXT_USER_OIM11G = "User-Oracle-OIMClient";
   protected String CONTEXT_REGISTER_OIM11G = "Register-Oracle-OIMClient";
   protected String CONTEXT = CONTEXT_EMPLOYEES_JNDI;
   protected String OPENPTK_HOME = "/var/tmp/openptk";
   protected final Properties _props = new Properties();

   //----------------------------------------------------------------
   public apiTest()
   //----------------------------------------------------------------
   {
      _props.setProperty(EngineIF.PROP_OPENPTK_HOME, OPENPTK_HOME);

      return;
   }
}
