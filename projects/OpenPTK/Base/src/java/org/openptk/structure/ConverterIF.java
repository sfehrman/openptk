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
 * Portions Copyright 2012, Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.structure;

import org.openptk.api.ElementIF;
import org.openptk.exception.ConverterException;

/**
 * This interface defines the methods used to implement the following features:
 * <ul>
 * <li>Convert a StructureIF, containing hierarchical data, into a specific
 * String representation of the data(encode)
 * <li>Convert a specific String representation of data into a StrucureIF,
 * containing hierarchical data (decode).
 * </ul>
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public interface ConverterIF
//===================================================================
{
   /**
    * Convert the Structure into a formated String.
    *
    * @param struct StructureIF of data
    * @return String encoded representation of the data
    * @throws ConverterException
    */
   public String encode(StructureIF struct) throws ConverterException;

   /**
    * Convert the String of encoded data into a StructureIF object.
    * 
    * @param string Encoded representation of the data
    * @return StructureIF Structure of the data
    * @throws ConverterException
    */
   public StructureIF decode(String string) throws ConverterException;

   /**
    * Return the Converter's Type.
    *
    * @return ConverterType
    */
   public ConverterType getType();

   /**
    * Get Structure Information using provided Id.
    *
    * @param structId Structure Information Id
    * @return ElementIF Information about the Structure
    */
   public ElementIF getStructInfo(String structId);

   /**
    * Set Structure Information using the provided Id.
    *
    * @param structId Structure Information Id
    * @param element Structure Information
    */
   public void setStructInfo(String structId, ElementIF element);

   /**
    * Return Array of Structure Information Ids.
    *
    * @return String Array of Ids
    */
   public String[] getStructInfoIds();
}
