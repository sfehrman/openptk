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
 * HTML. Nested data is presented using standard HTML tables.
 * <br/>
 * This Converter does <b>NOT</b> implement the conversion of HTML
 * to StructureIF data.
 * </p>
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class HtmlConverter extends Converter
//===================================================================
{

   private static final String CLASS_OPENPTK = "\"openptk\"";
   private static final String CLASS_NAME = "\"name\"";
   private static final String CLASS_CHILD = "\"value\"";
   private static final String CLASS_STRING = "\"data\"";
   private static final String TABLE_BEGIN = "<table class=" + CLASS_OPENPTK + ">";
   private static final String TABLE_END = "</table>";
   private static final String TR_BEGIN = "<tr>";
   private static final String TR_END = "</tr>";
   private static final String TD_BEGIN_NAME = "<td class=" + CLASS_NAME + ">";
   private static final String TD_BEGIN_CHILD = "<td class=" + CLASS_CHILD + ">";
   private static final String TD_BEGIN_STRING = "<td class=" + CLASS_STRING + ">";
   private static final String TD_END = "</td>";

   /**
    * Creates a new HtmlConvert object.
    */
   //----------------------------------------------------------------
   public HtmlConverter()
   //----------------------------------------------------------------
   {
      super(ConverterType.HTML);
      return;
   }

   /**
    * Convert the Structure data into a HTML formated String.
    *
    * @param struct StructureIF of data
    * @return String HTML representation of the data
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
         buf.append(this.toHtml(struct, 0, true));
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
   private String toHtml(final StructureIF parent, int level, final boolean needTable)
   //----------------------------------------------------------------
   {
      Object[] values = null;
      StringBuilder buf = new StringBuilder();
      String name = null;
      StructureIF[] children = null;
      StructureType type = null;

      name = parent.getName();

      if (needTable)
      {
         buf.append(pad(level, PAD)).append(TABLE_BEGIN).append(NEWLINE);
         level++;
         buf.append(pad(level, PAD)).append(TR_BEGIN).append(NEWLINE);
         level++;
      }

      if (name != null && name.length() > 0)
      {
         buf.append(pad(level, PAD)).append(TD_BEGIN_NAME).append(name).append(":").append(TD_END).append(NEWLINE);
      }

      children = parent.getChildrenAsArray();
      if (children != null && children.length > 0)
      {
         buf.append(pad(level, PAD)).append(TD_BEGIN_CHILD).append(NEWLINE);
         level++;

         buf.append(pad(level, PAD)).append(TABLE_BEGIN).append(NEWLINE);
         level++;

         for (StructureIF child : children)
         {
            buf.append(pad(level, PAD)).append(TR_BEGIN).append(NEWLINE);
            level++;

            buf.append(this.toHtml(child, level, false));

            level--;
            buf.append(pad(level, PAD)).append(TR_END).append(NEWLINE);
         }

         level--;
         buf.append(pad(level, PAD)).append(TABLE_END).append(NEWLINE);

         level--;
         buf.append(pad(level, PAD)).append(TD_END).append(NEWLINE);

      }
      else
      {
         values = parent.getValuesAsArray();
         type = parent.getValueType();
         if (values != null && values.length > 0)
         {
            buf.append(this.processValues(values, type, name, level));
         }
      }

      if (needTable)
      {
         level--;
         buf.append(pad(level, PAD)).append(TR_END).append(NEWLINE);

         level--;
         buf.append(pad(level, PAD)).append(TABLE_END).append(NEWLINE);
      }

      return buf.toString();
   }

   //----------------------------------------------------------------
   private String processValues(final Object[] values, final StructureType type,
      final String name, int level)
   //----------------------------------------------------------------
   {
      boolean multiValued = false;
      StringBuilder buf = new StringBuilder();
      String valStr = null;
      Boolean valBool = null;
      Integer valInt = null;
      Long valLong = null;


      if (values.length > 1)
      {
         multiValued = true;
      }

      switch (type)
      {
         case STRUCTURE:
            buf.append(pad(level, PAD)).append(TD_BEGIN_CHILD).append(NEWLINE);
            level++;
            
            buf.append(pad(level, PAD)).append(TABLE_BEGIN).append(NEWLINE);
            level++;

            for (Object value : values)
            {
               buf.append(pad(level, PAD)).append(TR_BEGIN).append(NEWLINE);
               level++;

               buf.append(this.toHtml((StructureIF) value, level, false));

               level--;
               buf.append(pad(level, PAD)).append(TR_END).append(NEWLINE);
            }

            level--;
            buf.append(pad(level, PAD)).append(TABLE_END).append(NEWLINE);
            
            level--;
            buf.append(pad(level, PAD)).append(TD_END).append(NEWLINE);
            
            break;
         case STRING:
            if (multiValued)
            {
               buf.append(pad(level, PAD)).append(TD_BEGIN_STRING).append(NEWLINE);
               level++;

               buf.append(pad(level, PAD)).append(TABLE_BEGIN).append(NEWLINE);
               level++;

               for (Object value : values)
               {
                  valStr = (String) value;

                  buf.append(pad(level, PAD)).append(TR_BEGIN);
                  buf.append(TD_BEGIN_STRING);

                  if (name != null && name.equalsIgnoreCase(StructureIF.NAME_URI))
                  {
                     buf.append("<a href=\"").append(valStr).append("\">").append(valStr).append("</a>");
                  }
                  else
                  {
                     buf.append(valStr);
                  }

                  buf.append(TD_END);
                  buf.append(TR_END).append(NEWLINE);
               }

               level--;
               buf.append(pad(level, PAD)).append(TABLE_END).append(NEWLINE);

               level--;
               buf.append(pad(level, PAD)).append(TD_END).append(NEWLINE);
            }
            else
            {
               valStr = (String) values[0];

               buf.append(pad(level, PAD)).append(TD_BEGIN_STRING);

               if (name != null && name.equalsIgnoreCase(StructureIF.NAME_URI))
               {
                  buf.append("<a href=\"").append(valStr).append("\">").append(valStr).append("</a>");
               }
               else
               {
                  buf.append(valStr);
               }

               buf.append(TD_END).append(NEWLINE);
            }
            break;
         case BOOLEAN:
            if (multiValued)
            {
               buf.append(pad(level, PAD)).append(TD_BEGIN_STRING).append(NEWLINE);
               level++;

               buf.append(pad(level, PAD)).append(TABLE_BEGIN).append(NEWLINE);
               level++;

               for (Object value : values)
               {
                  valBool = (Boolean) value;

                  buf.append(pad(level, PAD)).append(TR_BEGIN);
                  buf.append(TD_BEGIN_STRING);
                  buf.append(valBool.toString());
                  buf.append(TD_END);
                  buf.append(TR_END).append(NEWLINE);
               }

               level--;
               buf.append(pad(level, PAD)).append(TABLE_END).append(NEWLINE);

               level--;
               buf.append(pad(level, PAD)).append(TD_END).append(NEWLINE);
            }
            else
            {
               valBool = (Boolean) values[0];

               buf.append(pad(level, PAD)).append(TD_BEGIN_STRING);
               buf.append(valBool.toString());
               buf.append(TD_END).append(NEWLINE);
            }
            break;
         case INTEGER:
            if (multiValued)
            {
               buf.append(pad(level, PAD)).append(TD_BEGIN_STRING).append(NEWLINE);
               level++;

               buf.append(pad(level, PAD)).append(TABLE_BEGIN).append(NEWLINE);
               level++;

               for (Object value : values)
               {
                  valInt = (Integer) value;

                  buf.append(pad(level, PAD)).append(TR_BEGIN);
                  buf.append(TD_BEGIN_STRING);
                  buf.append(valInt.toString());
                  buf.append(TD_END);
                  buf.append(TR_END).append(NEWLINE);
               }

               level--;
               buf.append(pad(level, PAD)).append(TABLE_END).append(NEWLINE);

               level--;
               buf.append(pad(level, PAD)).append(TD_END).append(NEWLINE);
            }
            else
            {
               valInt = (Integer) values[0];

               buf.append(pad(level, PAD)).append(TD_BEGIN_STRING);
               buf.append(valInt.toString());
               buf.append(TD_END).append(NEWLINE);
            }
            break;
         case LONG:
            if (multiValued)
            {
               buf.append(pad(level, PAD)).append(TD_BEGIN_STRING).append(NEWLINE);
               level++;

               buf.append(pad(level, PAD)).append(TABLE_BEGIN).append(NEWLINE);
               level++;

               for (Object value : values)
               {
                  valLong = (Long) value;

                  buf.append(pad(level, PAD)).append(TR_BEGIN);
                  buf.append(TD_BEGIN_STRING);
                  buf.append(valLong.toString());
                  buf.append(TD_END);
                  buf.append(TR_END).append(NEWLINE);
               }

               level--;
               buf.append(pad(level, PAD)).append(TABLE_END).append(NEWLINE);

               level--;
               buf.append(pad(level, PAD)).append(TD_END).append(NEWLINE);
            }
            else
            {
               valLong = (Long) values[0];

               buf.append(pad(level, PAD)).append(TD_BEGIN_STRING);
               buf.append(valLong.toString());
               buf.append(TD_END).append(NEWLINE);
            }
            break;
         case OBJECT:
            if (multiValued)
            {
               // not supported
            }
            else
            {
               buf.append(pad(level, PAD)).append(TD_BEGIN_STRING);
               buf.append("OBJECT::").append(values[0].getClass().getSimpleName());
               buf.append(TD_END).append(NEWLINE);
            }
            break;
         default:
            buf.append(pad(level, PAD)).append(TD_BEGIN_STRING);
            buf.append("Unsupported Structure Type: ").append(type.toString());
            buf.append(TD_END).append(NEWLINE);
            break;
      }

      return buf.toString();
   }
}
