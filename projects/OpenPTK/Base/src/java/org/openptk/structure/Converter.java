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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openptk.api.ElementIF;
import org.openptk.exception.ConverterException;

/**
 * The abstract base class for a Converter.
 * Provides core method implementations.
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Converter implements ConverterIF
//===================================================================
{

   private ConverterType _type = null;
   protected static String PAD = "  ";
   protected static String QUOTE = "\"";
   protected static String NEWLINE = "\n";
   protected static String KEYVALUE = " : ";
   protected static String SEPARATOR = ";";
   protected static String GT = "<";
   protected static String GTE = "</";
   protected static String LT = ">";
   protected static String LTE = "/>";
   protected Map<String, ElementIF> _structInfo = null;
   protected Set<String> _multivalue = null;

   /**
    * Create a new Converter, set specified Type.
    *
    * @param type ConverterType
    */
   //----------------------------------------------------------------
   public Converter(final ConverterType type)
   //----------------------------------------------------------------
   {
      _type = type;
      _structInfo = new HashMap<String, ElementIF>();
      _multivalue = new HashSet<String>();

      return;
   }

   /**
    * Return the Converter's Type.
    *
    * @return ConverterType
    */
   //----------------------------------------------------------------
   @Override
   public final ConverterType getType()
   //----------------------------------------------------------------
   {
      return _type;
   }

   /**
    * Get Structure Information using provided Id.
    *
    * @param structId Structure Information Id
    * @return ElementIF Information about the Structure
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized ElementIF getStructInfo(final String structId)
   //----------------------------------------------------------------
   {
      ElementIF elem = null;

      if (structId != null && structId.length() > 0
         && _structInfo != null && _structInfo.containsKey(structId))
      {
         elem = _structInfo.get(structId);
      }

      return elem;
   }

   /**
    * Set Structure Information using the provided Id.
    *
    * @param structId Structure Information Id
    * @param element Structure Information
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setStructInfo(final String structId, final ElementIF element)
   //----------------------------------------------------------------
   {
      boolean isMulti = false;
      String val = null;
      Properties props = null;

      if (structId != null && structId.length() > 0 && element != null)
      {
         _structInfo.put(structId, element);

         /*
          * look for a property named "multivalue"
          * The property value is "true" add the structId
          * to the Set of structId names
          */

         props = element.getProperties();
         if (props != null)
         {
            val = props.getProperty(StructureIF.NAME_MULTIVALUE);
            isMulti = Boolean.parseBoolean(val);
            if (isMulti)
            {
               _multivalue.add(structId.toLowerCase());
            }
         }

      }
      return;
   }

   /**
    * Return Array of Structure Information Ids.
    *
    * @return String Array of Ids
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String[] getStructInfoIds()
   //----------------------------------------------------------------
   {
      return _structInfo.keySet().toArray(new String[_structInfo.size()]);
   }

   /**
    * Convert the Structure into a formated String.
    *
    * @param struct StructureIF of data
    * @return String encoded representation of the data
    * @throws ConverterException
    */
   //----------------------------------------------------------------
   @Override
   public String encode(StructureIF struct) throws ConverterException
   //----------------------------------------------------------------
   {
      throw new ConverterException("encode not implemented.");
   }

   /**
    * Convert the String of encoded data into a StructureIF object.
    *
    * @param string Encoded representation of the data
    * @return StructureIF Structure of the data
    * @throws ConverterException
    */
   //----------------------------------------------------------------
   @Override
   public StructureIF decode(String string) throws ConverterException
   //----------------------------------------------------------------
   {
      throw new ConverterException("decode not implemented.");
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   /**
    * Get a property value from Structure Information.
    *
    * @param structName Structure name
    * @param propName Property name
    * @return String Structure / Property value
    */
   //----------------------------------------------------------------
   protected final String getStructProperty(final String structName, final String propName)
   //----------------------------------------------------------------
   {
      String value = null;
      ElementIF elem = null;

      if (structName != null && structName.length() > 0
         && propName != null && propName.length() > 0)
      {
         elem = _structInfo.get(structName);
         if (elem != null)
         {
            value = elem.getProperty(propName);
         }
      }

      return value;
   }

   /**
    * Returns a String built by repeating the String.
    *
    * @param qty number of times to repeat the String
    * @param chars String to repeat
    * @return String the new concatinated value
    */
   //----------------------------------------------------------------
   protected final synchronized String pad(int qty, String chars)
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();

      for (int i = 0; i < qty; i++)
      {
         buf.append(chars);
      }

      return buf.toString();
   }

   /**
    * Standard error handling method.
    *
    * @param msg String message containing what happened.
    * @throws ConverterException
    */
   //----------------------------------------------------------------
   protected final void handleError(final String msg) throws ConverterException
   //----------------------------------------------------------------
   {
      String str = null;

      if (msg == null || msg.length() < 1)
      {
         str = "(null message)";
      }
      else
      {
         str = msg;
      }

      throw new ConverterException(str);
   }
}
