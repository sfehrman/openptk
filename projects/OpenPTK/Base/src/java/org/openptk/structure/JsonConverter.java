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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import org.openptk.exception.ConverterException;
import org.openptk.exception.StructureException;

/**
 *
 * <p>
 * This class supports the conversion of StructureIF data <b>TO and FROM</b>
 * JSON using the Jackson Java-JSON Processor.
 * </p>
 *
 * @author Scott Fehrman
 * @since 2.2.0
 */
//===================================================================
public class JsonConverter extends Converter
//===================================================================
{

   private static String CLASS_NAME = null;
   private JsonFactory _factory = null;

   /**
    * Creates a new JsonConverter object.
    */
   //----------------------------------------------------------------
   public JsonConverter()
   //----------------------------------------------------------------
   {
      super(ConverterType.JSON);

      CLASS_NAME = this.getClass().getSimpleName();
      _factory = new JsonFactory();

      return;
   }

   /**
    * Convert the Structure data into a JSON (syntax) String.
    *
    * @param structTop StructureIF of data
    * @return String JSON representation of the data
    * @throws ConverterException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String encode(final StructureIF structTop) throws ConverterException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String str = null;

      if (structTop != null)
      {
         try
         {
            str = this.toJson(structTop);
         }
         catch (IOException ex)
         {
            throw new ConverterException(METHOD_NAME + ex.getMessage());
         }
      }
      else
      {
         throw new ConverterException(METHOD_NAME + "Structure is null.");
      }
      return str;
   }

   /**
    * Convert the JSON syntax (String) into StructureIF data.
    *
    * @param data JSON representation of the data
    * @return StructureIF data
    * @throws ConverterException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized StructureIF decode(final String data) throws ConverterException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      StructureIF struct = null;

      if (data != null && data.length() > 0)
      {
         try
         {
            struct = this.toStruct(data);
         }
         catch (IOException ex)
         {
            throw new ConverterException(METHOD_NAME + ex.getMessage());
         }
         catch (StructureException ex)
         {
            throw new ConverterException(METHOD_NAME + ex.getMessage());
         }
      }
      else
      {
         throw new ConverterException(METHOD_NAME + "Data is null.");
      }

      return struct;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private String toJson(final StructureIF structCurr) throws IOException, ConverterException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String str = null;
      OutputStream stream = null;
      JsonGenerator generator = null; // Jackson

      stream = new ByteArrayOutputStream();
      generator = _factory.createJsonGenerator(stream, JsonEncoding.UTF8);

      try
      {
         this.processStructure(structCurr, generator);
         generator.close();
      }
      catch (IOException ex)
      {
         str = METHOD_NAME + "error='" + ex.getMessage() + "', output='" + stream.toString() + "'";
         stream.close();
      }

      if (str == null)
      {
         str = stream.toString();
      }

      return str;
   }

   //----------------------------------------------------------------
   private StructureIF toStruct(final String data) throws IOException, StructureException, ConverterException
   //----------------------------------------------------------------
   {
      boolean done = false;
      boolean pop = false;
      int valInt = 0;
      double valDbl = 0.0;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String valStr = null;
      String nameCurr = null;
      String nameParent = null;
      String nameChild = null;
      StructureIF structTop = null;
      StructureIF structCurr = null;
      StructureIF structParent = null;
      JsonParser parser = null; // Jackson
      JsonToken token = null;   // Jackson

      /*
       * We are looking for the following conditions for creating structures
       *
       * Category: singular, Value: primitive, syntax:
       * "name" : "val"
       *
       * StructureIF struct = new BasicStructure("name","val");
       *
       * ----
       *
       * Category: complex (sub-objects), Value: object, syntax:
       * "name" : { "name1":"val1","name2":"val2" }
       *
       * StructureIF struct = new BasicStructure("name");
       * struct.addChild(new BasicStructure("name1","val1");
       * struct.addChild(new BasicStructure("name2","val2");
       *
       * ----
       *
       * Category: multi-valued, Value: primitive (MUST BE ALL THE SAME), syntax:
       * "name" : [ "val1","val2","val3" ]
       *
       * StructureIF struct = new BasicStructure("name");
       * struct.addValue("val1");
       * struct.addValue("val2");
       * struct.addValue("val3");
       *
       * ----
       *
       * Category: multi-valued, Value: object, syntax:
       * "name" : [ { "name1":"val1","name2":"val2" } , { "name3":"val3","name4":"val4" } ]
       *
       * StructureIF struct = new BasicStructure("name");
       *
       * StructureIF structA = new BasicStructure("name");
       * structA.addChild(new BasicStructure("name1","val1"));
       * structA.addChild(new BasicStructure("name2","val2"));
       *
       * StructureIF structB = new BasicStructure("name");
       * structB.addChild(new BasicStructure("name3","val3"));
       * structB.addChild(new BasicStructure("name4","val4"));
       *
       * struct.addValue(structA);
       * struct.addValue(structB);
       *
       */

      parser = _factory.createJsonParser(data);

      if (parser == null)
      {
         this.error(METHOD_NAME + "Parser is null");
      }

      while (!done)
      {
         token = parser.nextToken();
         if (token != null)
         {
            switch (token)
            {
               case START_OBJECT: // {
                  if (structCurr != null && structCurr.isMultiValued())
                  {
                     structParent = structCurr;
                     structCurr = new BasicStructure(StructureIF.NAME_VALUE);
                     structParent.addValue(structCurr);
                  }
                  break;
               case END_OBJECT: // }
                  pop = true;
                  break;
               case START_ARRAY: // [  ... Structure will/can have "multiple values"
                  structCurr.setMultiValued(true);
                  break;
               case END_ARRAY: // ]
                  pop = true;
                  break;
               case FIELD_NAME: // "name" : ...
                  nameCurr = parser.getCurrentName();
                  if (nameCurr != null && nameCurr.length() > 0)
                  {
                     structParent = structCurr;
                     structCurr = new BasicStructure(nameCurr);
                     if (structParent != null)
                     {
                        structParent.addChild(structCurr);
                     }
                     /*
                      * check for the top structure
                      */
                     if (structTop == null)
                     {
                        structTop = structCurr;
                        structParent = structCurr;
                     }
                  }
                  else
                  {
                     this.error("JsonToken has a null/empty name");
                  }
                  break;
               case VALUE_STRING: // ... : "value"
                  valStr = parser.getText();
                  structCurr.addValue(valStr);
                  if (!structCurr.isMultiValued())
                  {
                     pop = true;
                  }
                  break;
               case VALUE_NUMBER_INT: // ... : 1234
                  valInt = parser.getIntValue();
                  structCurr.addValue(valInt);
                  if (!structCurr.isMultiValued())
                  {
                     pop = true;
                  }
                  break;
               case VALUE_NUMBER_FLOAT: // ... : 78.9  (treat as double, 64-bit)
                  // NEED TO IMPLEMENT
                  valDbl = parser.getDoubleValue();
                  this.error("Floating point values are not supported");
                  if (!structCurr.isMultiValued())
                  {
                     pop = true;
                  }
                  break;
               case VALUE_NULL: // ... : null
                  if (!structCurr.isMultiValued())
                  {
                     pop = true;
                  }
                  break;
               case VALUE_FALSE: // ... : false
                  structCurr.addValue(false);
                  if (!structCurr.isMultiValued())
                  {
                     pop = true;
                  }
                  break;
               case VALUE_TRUE: // ... : true
                  structCurr.addValue(true);
                  if (!structCurr.isMultiValued())
                  {
                     pop = true;
                  }
                  break;
               default:
                  this.error("Unknow JsonToken Enum: '" + token.toString() + "'");
                  break;
            }
            if (pop)
            {
               pop = false;
               structCurr = structParent;
               if (structCurr != null && structCurr.getParent() != null)
               {
                  structParent = structCurr.getParent();
               }
            }
         }
         else
         {
            done = true; // got a NULL Token
         }
      }

      return structTop;
   }

   //----------------------------------------------------------------
   private void processStructure(final StructureIF structCurr, final JsonGenerator generator) throws IOException, ConverterException
   //----------------------------------------------------------------
   {
      boolean isMultiValued = false;
      boolean isTopLevel = false;
      boolean isWrapped = false;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String name = null;
      Object[] objects = null;
      StructureIF structParent = null;
      StructureIF[] children = null;
      StructureType type = null;

      name = structCurr.getName();
      if ( name == null || name.length() < 1)
      {
         this.error(METHOD_NAME + "name is null");
      }
      
      type = structCurr.getValueType();
      structParent = structCurr.getParent();

      if (structParent == null)
      {
         isTopLevel = true;
      }

      if (isTopLevel) // top-level object, always enclose with "{ ... }"
      {
         generator.writeStartObject(); // "{"
      }

      if (type == StructureType.PARENT || type == StructureType.CONTAINER) // has children
      {
         children = structCurr.getChildrenAsArray();
         if (children == null || children.length < 1)
         {
            this.error(METHOD_NAME + "Parent Structure has no children, NOT POSSIBLE");
         }
         
         if (structParent != null && structParent.getValueType() == StructureType.STRUCTURE)
         {
            generator.writeStartObject(); // "{"            
            isWrapped = true;
         }
         else if ( type == StructureType.PARENT)
         {
            generator.writeFieldName(name); // "name" :
            generator.writeStartObject(); // "{"            
            isWrapped = true;            
         }

         for (StructureIF structChild : children)
         {
            this.processStructure(structChild, generator);
         }

         if (isWrapped)
         {
            generator.writeEndObject(); // "}"
         }
      }
      else // not a PARENT ... has one or more values
      {
         objects = structCurr.getValuesAsArray();
         if (objects == null || objects.length < 1)
         {
            /*
             * no values, write a null
             */
            generator.writeNullField(name);
         }
         else
         {
            generator.writeFieldName(name); // name required for an array

            isMultiValued = structCurr.isMultiValued();

            if (isMultiValued)
            {
               generator.writeStartArray(); // "["
            }

            for (Object object : objects)
            {
               switch (type)
               {
                  case STRUCTURE:
                     this.processStructure((StructureIF) object, generator);
                     break;
                  case STRING:
                     generator.writeString((String) object);
                     break;
                  case INTEGER:
                     generator.writeNumber(((Integer) object).intValue());
                     break;
                  case BOOLEAN:
                     generator.writeBoolean(((Boolean) object).booleanValue());
                     break;
                  case OBJECT:
                      // OBJECT processing is not implemented (nothing to do)
                     break;
                  default:
                     this.error("Unsupported Structure Type: '" + type.toString() + "'");
                     break;
               }
            }
            if (isMultiValued)
            {
               generator.writeEndArray(); // "]"
            }
         }
      }

      if (isTopLevel)
      {
         generator.writeEndObject(); // "}"
      }

      return;
   }

   //----------------------------------------------------------------
   private void error(String msg) throws ConverterException
   //----------------------------------------------------------------
   {
      throw new ConverterException(msg);
   }
}