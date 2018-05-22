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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.openptk.exception.ConverterException;
import org.openptk.exception.StructureException;

/**
 * <p>
 * This class supports the conversion of StructureIF data <b>TO and FROM</b>
 * XML. Nested data is presented using standard XML syntax.
 * </p>
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class XmlConverter extends Converter
//===================================================================
{

   private static String CLASS_NAME = null;
   private XMLInputFactory _xmlInputFactory = null;
   private XMLOutputFactory _xmlOutputFactory = null;

   /**
    * Create a new XmlConverter object.
    */
   //----------------------------------------------------------------
   public XmlConverter()
   //----------------------------------------------------------------
   {
      super(ConverterType.XML);
      CLASS_NAME = this.getClass().getSimpleName();
      _xmlInputFactory = XMLInputFactory.newInstance();
      _xmlOutputFactory = XMLOutputFactory.newInstance();
      return;
   }

   /**
    * Convert the Structure data into a XML (syntax) String.
    *
    * @param structTop StructureIF of data
    * @return String XML representation of the data
    * @throws ConverterException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String encode(final StructureIF structTop) throws ConverterException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      StringWriter stringWriter = null;
      XMLStreamWriter xmlWriter = null;

      if (structTop != null)
      {
         stringWriter = new StringWriter();
         try
         {
            xmlWriter = _xmlOutputFactory.createXMLStreamWriter(stringWriter);
            this.toXml(xmlWriter, structTop);
            xmlWriter.flush();
            xmlWriter.close();
         }
         catch (XMLStreamException ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "Structure is null.");
      }

      return stringWriter.toString();
   }

   /**
    * Convert the XML syntax (String) into StructureIF data.
    *
    * @param data XML representation of the data
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
      StringReader stringReader = null;
      XMLStreamReader xmlReader = null;

      if (data != null && data.length() > 0)
      {
         stringReader = new StringReader(data);
         try
         {
            xmlReader = _xmlInputFactory.createXMLStreamReader(stringReader);
         }
         catch (XMLStreamException ex)
         {
            this.handleError(METHOD_NAME + "Data is null.");
         }
         struct = this.toStruct(xmlReader);
      }
      else
      {
         this.handleError(METHOD_NAME + "Data is null.");
      }

      return struct;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private void toXml(final XMLStreamWriter xmlWriter, final StructureIF structCurr) throws ConverterException, XMLStreamException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String nameCurr = null;
      String nameParent = null;
      String nameChild = null;
      StructureType typeStruct = null;
      StructureIF[] children = null;
      StructureIF structParent = null;

      nameCurr = structCurr.getName();
      typeStruct = structCurr.getValueType();
      structParent = structCurr.getParent();

      if (nameCurr == null || nameCurr.length() < 1)
      {
         /*
          * name may be null if it's a value of a multivalued attribute
          * check the parent, if it's in the Set of multi-values
          * use the string "value" for each sub-element
          */

         if (structParent != null)
         {
            nameParent = structParent.getName();
            if (nameParent != null && nameParent.length() > 0)
            {
               if (_multivalue.contains(nameParent))
               {
                  nameCurr = StructureIF.NAME_VALUE;
               }
            }
         }
      }

      if (nameCurr == null || nameCurr.length() < 1)
      {
         this.handleError(METHOD_NAME + "Structure name is null, parent='"
            + (structParent != null ? structParent.getName() : "null") + "'");
      }

      if (typeStruct == null)
      {
         /*
          * null type ... write an empty XML element
          */
         xmlWriter.writeStartElement(nameCurr);
         xmlWriter.writeEndElement();
      }
      else if (typeStruct == StructureType.PARENT) // sub elements
      {
         xmlWriter.writeStartElement(nameCurr);
         if (typeStruct != StructureType.PARENT && typeStruct != StructureType.STRUCTURE)
         {
            xmlWriter.writeAttribute(StructureIF.NAME_TYPE, typeStruct.toString().toLowerCase());
         }
         children = structCurr.getChildrenAsArray();
         for (StructureIF structChild : children)
         {
            this.toXml(xmlWriter, structChild);
         }
         xmlWriter.writeEndElement();
      }
      else
      {
         this.processStructure(xmlWriter, structCurr);
      }

      return;
   }

   //----------------------------------------------------------------
   private StructureIF toStruct(final XMLStreamReader xmlReader) throws ConverterException
   //----------------------------------------------------------------
   {
      boolean pop = true;
      int event = 0;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String name = null;
      String value = null;
      String type = null;
      StructureIF structTop = null;
      StructureIF structCurr = null;
      StructureIF structParent = null;
      StructureType typeStruct = null;

      try
      {
         while (xmlReader.hasNext())
         {
            event = xmlReader.next();
            switch (event)
            {
               case XMLStreamConstants.START_ELEMENT:
                  type = null;
                  name = null;
                  value = null;

                  name = xmlReader.getLocalName();

                  /*
                   * Set the StructureType based on the XML "type" attribute
                   */

                  type = xmlReader.getAttributeValue(null, StructureIF.NAME_TYPE);
                  if (type != null && type.length() > 0)
                  {
                     if (type.equalsIgnoreCase(StructureType.STRING.toString()))
                     {
                        typeStruct = StructureType.STRING;
                     }
                     else if (type.equalsIgnoreCase(StructureType.INTEGER.toString()))
                     {
                        typeStruct = StructureType.INTEGER;
                     }
                     else if (type.equalsIgnoreCase(StructureType.LONG.toString()))
                     {
                        typeStruct = StructureType.LONG;
                     }
                     else if (type.equalsIgnoreCase(StructureType.BOOLEAN.toString()))
                     {
                        typeStruct = StructureType.BOOLEAN;
                     }
                     else if (type.equalsIgnoreCase(StructureType.OBJECT.toString()))
                     {
                        typeStruct = StructureType.OBJECT;
                     }
                     else if (type.equalsIgnoreCase(StructureType.STRUCTURE.toString()))
                     {
                        typeStruct = StructureType.STRUCTURE;
                     }
                     else
                     {
                        this.handleError(METHOD_NAME + "Invalid Type: '" + type + "', Element name: '" + name + "'");
                     }
                  }

                  /*
                   * Is this element suppose to be processed with
                   * multi-values instead of a sub-element (child)
                   *
                   * Set contains: "roles", "forgottenPasswordQuestions", "..."
                   *
                   */

                  if (_multivalue.contains(name.toLowerCase()))
                  {
                     /*
                      * check the type, if it's not set, implicitly set it to STRUCTURE
                      */
                     if (typeStruct == null)
                     {
                        typeStruct = StructureType.STRUCTURE;
                     }
                     /*
                      * The structure for holding the values
                      */
                     structParent = structCurr;
                     structCurr = new BasicStructure(name);
                     structCurr.setMultiValued(true);
                     structCurr.setType(typeStruct);
                     structParent.addChild(structCurr);
                  }
                  else if (name.equalsIgnoreCase(StructureIF.NAME_VALUE))
                  {
                     /*
                      * A "value" for a multi-valued attribute
                      * if the parent element is a type STRUCTURE
                      * then create a new Structure
                      * else don't need a new structure, just add the CHARACTERS
                      * as a value
                      */
                     if (typeStruct == StructureType.STRUCTURE)
                     {
                        structParent = structCurr;
                        structCurr = new BasicStructure(name);
                        structParent.addValue(structCurr);
                     }
                     else
                     {
                        /*
                         * Add the value (CHARACTERS) to the current Structure
                         */
                        pop = false;
                     }
                  }
                  else
                  {
                     if (structTop == null)
                     {
                        structCurr = new BasicStructure(name);
                        structTop = structCurr;
                        structParent = structCurr;
                     }
                     else
                     {
                        structParent = structCurr;
                        structCurr = new BasicStructure(name);
                        structParent.addChild(structCurr);
                     }
                  }
                  break;
               case XMLStreamConstants.CHARACTERS:
                  if (!xmlReader.isWhiteSpace())
                  {
                     value = xmlReader.getText();
                     value = (value == null ? null : value.trim());
                     if (value != null && value.length() > 0)
                     {
                        if (typeStruct == null)
                        {
                           this.handleError(METHOD_NAME + "Type is null, Element name='" + name + "'");
                        }
                        switch (typeStruct)
                        {
                           case STRING:
                              structCurr.addValue(value);
                              break;
                           case INTEGER:
                              structCurr.addValue(Integer.parseInt(value));
                              break;
                           case LONG:
                              structCurr.addValue(Long.parseLong(value));
                              break;
                           case BOOLEAN:
                              structCurr.addValue(Boolean.parseBoolean(value));
                              break;
                           case OBJECT:
                              /*
                               * OBJECT processing is not implemented
                               */
                              break;
                           default:
                              handleError(METHOD_NAME + "Invalid type '"
                                 + typeStruct.toString() + "', name='" + name + "'");
                              break;
                        }
                     }
                  }
                  break;
               case XMLStreamConstants.END_ELEMENT:
                  if (pop)
                  {
                     structCurr = structParent;
                     if (structCurr.getParent() != null)
                     {
                        structParent = structCurr.getParent();
                     }
                  }
                  else
                  {
                     pop = true;
                  }
                  break;
            }
         }
         xmlReader.close();
      }
      catch (XMLStreamException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }
      catch (StructureException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }
      catch (NumberFormatException ex)
      {
         this.handleError(METHOD_NAME
            + "Declared Type is NUMBER, value is not a NUMBER. " + ex.getMessage());
      }

      return structTop;
   }

   //----------------------------------------------------------------
   private void processStructure(final XMLStreamWriter xmlWriter, final StructureIF structCurr) throws ConverterException, XMLStreamException
   //----------------------------------------------------------------
   {
      int len = 0;
      Object[] values = null;
      String name = null;
      String nameChild = null;
      StructureType type = null;

      name = structCurr.getName();
      type = structCurr.getValueType();
      values = structCurr.getValuesAsArray();

      xmlWriter.writeStartElement(name);
      xmlWriter.writeAttribute(StructureIF.NAME_TYPE, type.toString().toLowerCase());

      if (values != null && values.length > 0)
      {
         len = values.length;
      }

      if (len == 1)
      {
         switch (type)
         {
            case STRUCTURE:
               this.toXml(xmlWriter, (StructureIF) values[0]);
               break;
            case STRING:
               xmlWriter.writeCharacters((String) values[0]);
               break;
            case INTEGER:
               xmlWriter.writeCharacters(((Integer) values[0]).toString());
               break;
            case LONG:
               xmlWriter.writeCharacters(((Long) values[0]).toString());
               break;
            case BOOLEAN:
               xmlWriter.writeCharacters(((Boolean) values[0]).toString());
               break;
            case OBJECT:
               /*
                * OBJECT processing is not implemented
                */
               break;
         }
      }
      else if (len > 1)
      {
         /*
          * set the multi-value element name to "value"
          */

         nameChild = StructureIF.NAME_VALUE;

         for (Object value : values)
         {
            switch (type)
            {
               case STRUCTURE:
                  this.toXml(xmlWriter, (StructureIF) value);
                  break;
               case STRING:
                  xmlWriter.writeStartElement(nameChild);
                  xmlWriter.writeCharacters((String) value);
                  xmlWriter.writeEndElement();
                  break;
               case INTEGER:
                  xmlWriter.writeStartElement(nameChild);
                  xmlWriter.writeCharacters(((Integer) value).toString());
                  xmlWriter.writeEndElement();
                  break;
               case LONG:
                  xmlWriter.writeStartElement(nameChild);
                  xmlWriter.writeCharacters(((Long) value).toString());
                  xmlWriter.writeEndElement();
                  break;
               case BOOLEAN:
                  xmlWriter.writeStartElement(nameChild);
                  xmlWriter.writeCharacters(((Boolean) value).toString());
                  xmlWriter.writeEndElement();
                  break;
               case OBJECT:
                  /*
                   * OBJECT processing is not implemented
                   */
                  break;
            }
         }
      }
      xmlWriter.writeEndElement();

      return;
   }
}
