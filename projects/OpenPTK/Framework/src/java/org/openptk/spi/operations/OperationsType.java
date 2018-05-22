/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009 Sun Microsystems, Inc.
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
package org.openptk.spi.operations;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
public enum OperationsType
{

   LOOPBACK, //    testing only
   SPML10, //      OASIS OpenSPML 1.0
   SPML20, //      OASIS OpenSPML 2.0
   UNBOUNDID, //   UnboundID LDAP
   JNDI, //        Java Naming Directory Interface
   JDBC, //        Java DataBase Connectivity
   IDCONNECTORS, // Identity Connectors, identityconnectors.dev.java.net
   ORACLEIDM
}
