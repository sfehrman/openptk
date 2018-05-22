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

import org.openptk.exception.ConverterException;

/**
 * <p>
 * This class supports the conversion of StructureIF data <b>TO</b>
 * Plain Text. Nested data is presented as indented rows.
 * <br/>
 * This Converter does <b>NOT</b> implement the conversion of Plain Text
 * to StructureIF data.
 * </p>
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class PlainConverter extends Converter
//===================================================================
{

   private static String KEYVALUE = "=";
   private static String CLASS_NAME = null;

   /**
    * Create a new PlainConverter object.
    */
   //----------------------------------------------------------------
   public PlainConverter()
   //----------------------------------------------------------------
   {
      super(ConverterType.PLAIN);
      CLASS_NAME = this.getClass().getSimpleName();
      return;
   }

   /**
    * Convert the Structure data into a Plain Text formated String.
    *
    * @param struct StructureIF of data
    * @return String Plain Text representation of the data
    * @throws ConverterException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String encode(final StructureIF struct) throws ConverterException
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();
      if (struct != null)
      {
         buf.append(this.toPlain(struct, 0));
      }
      else
      {
         throw new ConverterException("Structure is null.");
      }
      return buf.toString();
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private String toPlain(final StructureIF parent, int level)
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();
      String name = null;
      Object[] values = null;
      StructureIF[] children = null;
      StructureIF child = null;
      StructureType type = null;

      name = parent.getName();
      if (name != null)
      {
         buf.append(pad(level, PAD)).append(name).append(KEYVALUE);
      }

      if (parent.hasChildren())
      {
         buf.append(NEWLINE);
         children = parent.getChildrenAsArray();
         level++;

         for (int i = 0; i < children.length; i++)
         {
            child = children[i];
            buf.append(this.toPlain(child, level));
         }

         level--;
      }
      else
      {
         values = parent.getValuesAsArray();
         type = parent.getValueType();
         buf.append(this.processValues(values, type, level));
      }

      return buf.toString();
   }

   //----------------------------------------------------------------
   private String processValues(final Object[] values, final StructureType type, int level)
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();
      String valStr = null;
      Boolean valBool = null;
      Integer valInt = null;
      Long valLong = null;
      StructureIF valStruct = null;

      if (values != null && values.length > 0)
      {
         switch (type)
         {
            case STRUCTURE:
               buf.append(NEWLINE);
               level++;
               for (int j = 0; j < values.length; j++)
               {
                  valStruct = (StructureIF) values[j];
                  buf.append(this.toPlain(valStruct, level));
               }
               level--;
               break;
            case STRING:
               for (int j = 0; j < values.length; j++)
               {
                  valStr = (String) values[j];
                  buf.append(QUOTE).append(valStr).append(QUOTE);
                  if (j < (values.length - 1))
                  {
                     buf.append(SEPARATOR).append(" ");
                  }
               }
               buf.append(NEWLINE);
               break;
            case BOOLEAN:
               for (int j = 0; j < values.length; j++)
               {
                  valBool = (Boolean) values[j];
                  buf.append(valBool.toString());
                  if (j < (values.length - 1))
                  {
                     buf.append(SEPARATOR).append(" ");
                  }
               }
               buf.append(NEWLINE);
               break;
            case INTEGER:
               for (int j = 0; j < values.length; j++)
               {
                  valInt = (Integer) values[j];
                  buf.append(valInt.toString());
                  if (j < (values.length - 1))
                  {
                     buf.append(SEPARATOR).append(" ");
                  }
               }
               buf.append(NEWLINE);
               break;
            case LONG:
               for (int j = 0; j < values.length; j++)
               {
                  valLong = (Long) values[j];
                  buf.append(valLong.toString()).append("L");
                  if (j < (values.length - 1))
                  {
                     buf.append(SEPARATOR).append(" ");
                  }
               }
               buf.append(NEWLINE);
               break;
            case OBJECT:
               buf.append("OBJECT::").append(values[0].getClass().getSimpleName());
               buf.append(NEWLINE);
               break;
            default:
               buf.append("Unsupported Structure Type: ").append(type.toString());
               buf.append(NEWLINE);
               break;
         }
      }
      else
      {
         buf.append(NEWLINE);
      }

      return buf.toString();
   }
}
