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
package org.openptk.api;

/**
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */

/**
 * Supported operation codes:
 *
 * <table>
 * <tr><td align="right"><b>CREATE:</b></td><td>Create a new Entry</td></tr>
 * <tr><td align="right"><b>READ:</b></td><td>Return a single Entry</td></tr>
 * <tr><td align="right"><b>UPDATE:</b></td><td>Update a single Entry</td></tr>
 * <tr><td align="right"><b>DELETE:</b></td><td>Delete a single Entry</td></tr>
 * <tr><td align="right"><b>SEARCH:</b></td><td>Search for and return multiple Entries</td></tr>
 * <tr><td align="right"><b>PWDCHANGE:</b></td><td>Change an Entry's password, with provided value</td></tr>
 * <tr><td align="right"><b>PWDRESET</b></td><td>Change an Entry's password, system generated</td></tr>
 * <tr><td align="right"><b>PWDFORGOT:</b></td><td>Forgotten password questions/answers process</td></tr>
 * </table>
 * 
 * Used at all Tiers in the Project.
 */
//===================================================================
public enum Opcode
//===================================================================
{
   CREATE,
   READ,
   UPDATE,
   DELETE,
   SEARCH,
   PWDCHANGE,
   PWDRESET,
   PWDFORGOT
}
