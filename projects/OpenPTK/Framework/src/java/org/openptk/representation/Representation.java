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
 * Portions Copyright 2011-2013 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.representation;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openptk.api.Attribute;
import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.common.AttrIF;
import org.openptk.common.BasicAttr;
import org.openptk.common.ComponentIF;
import org.openptk.engine.EngineIF;
import org.openptk.exception.StructureException;
import org.openptk.session.SessionIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;
import org.openptk.structure.StructureType;

/**
 * This class provides an "interface" to OpenPTK.
 * It sets up the framework
 * It must be sub-classed and expose application specific operations
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * @since 2.0.0
 */
//===================================================================
public abstract class Representation implements RepresentationIF
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private EngineIF _engine = null;

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public Representation(final EngineIF engine)
   //----------------------------------------------------------------
   {
      _engine = engine;
      return;
   }


   /*
    * *****************
    * PROTECTED METHODS
    * *****************
    */
   /**
    * @return
    */
   //----------------------------------------------------------------
   protected final EngineIF getEngine()
   //----------------------------------------------------------------
   {
      return _engine;
   }

   /**
    * @param name
    * @param structIn
    * @param allowNull
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final String getStringValue(final String name, final StructureIF structIn, final boolean allowNull) throws Exception
   //----------------------------------------------------------------
   {
      /*
       * This method looks for a Structure, child of specified Structure (argument),
       * that matches the specified name (argument). There could be more than one
       * child with the same name (it's a linked list), when there is the first
       * one is used. The value is then obtained (from the frist Child).
       * If the argument "allowNull" is true then a value will not throw an Exception
       */

      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String value = null;
      StructureType type = null;
      StructureIF child = null;

      child = structIn.getChild(name);
      if (child != null)
      {
         type = child.getValueType();
         if (type == StructureType.STRING)
         {
            value = child.getValueAsString();
            if (value == null || value.length() < 1)
            {
               if (!allowNull)
               {
                  this.handleError(METHOD_NAME + "Structure '" + name + "' has no value");
               }
            }
         }
         else
         {
            this.handleError(METHOD_NAME + "Structure '" + name + "' must be of Type STRING");
         }
      }
      else
      {
         if (!allowNull)
         {
            this.handleError(METHOD_NAME + "Input Structure is missing data '" + name + "'");
         }
      }

      return value;
   }

   /**
    * @param elem
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final StructureIF getPropsAsStruct(final ElementIF elem) throws Exception
   //----------------------------------------------------------------
   {
      Properties props = null;
      StructureIF structProps = null;

      props = elem.getProperties();

      structProps = this.getStructFromProperties(props);

      return structProps;
   }

   /**
    * @param comp
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final StructureIF getPropsAsStruct(final ComponentIF comp) throws Exception
   //----------------------------------------------------------------
   {
      Properties props = null;
      StructureIF structProps = null;

      props = comp.getProperties();

      structProps = this.getStructFromProperties(props);

      return structProps;
   }

   /**
    * @param attr
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final StructureIF getPropsAsStruct(final AttributeIF attr) throws Exception
   //----------------------------------------------------------------
   {
      Properties props = null;
      StructureIF structProps = null;

      props = attr.getProperties();

      structProps = this.getStructFromProperties(props);

      return structProps;
   }

   /**
    * @param comp
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final StructureIF getAttrsAsStruct(final ComponentIF comp) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String key = null;
      Map<String, AttrIF> attrs = null;
      AttrIF attr = null;
      Iterator<String> iterStr = null;
      StructureIF structAttrs = null;
      StructureIF structAttr = null;

      structAttrs = new BasicStructure(StructureIF.NAME_ATTRIBUTES);

      attrs = comp.getAttributes();
      if (attrs != null && !attrs.isEmpty())
      {
         iterStr = attrs.keySet().iterator();
         while (iterStr.hasNext())
         {
            key = iterStr.next();
            attr = attrs.get(key);

            structAttr = this.getStructFromAttribute(attr);

            try
            {
               structAttrs.addChild(structAttr);
            }
            catch (StructureException ex)
            {
               throw new Exception(METHOD_NAME + ex.getMessage());
            }
         }
      }

      return structAttrs;
   }

   /**
    * @param attr
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final StructureIF getStructFromAttribute(final AttributeIF attr) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String name = null;
      StructureIF structAttr = null;
      Object obj = null;

      if (attr == null)
      {
         this.handleError(METHOD_NAME + "Attribute is null");
      }

      name = attr.getName();

      if (name == null || name.length() < 1)
      {
         this.handleError(METHOD_NAME + "Attribute has a null name");
      }

      obj = attr.getValue();

      if (obj != null)
      {
         switch (attr.getType())
         {
            case STRING:
               if (attr.isMultivalued())
               {
                  structAttr = new BasicStructure(name, (String[]) obj);
               }
               else
               {
                  structAttr = new BasicStructure(name, (String) obj);
               }
               break;
            case BOOLEAN:
               if (attr.isMultivalued())
               {
                  structAttr = new BasicStructure(name, (Boolean[]) obj);
               }
               else
               {
                  structAttr = new BasicStructure(name, (Boolean) obj);
               }
               break;
            case INTEGER:
               if (attr.isMultivalued())
               {
                  structAttr = new BasicStructure(name, (Integer[]) obj);
               }
               else
               {
                  structAttr = new BasicStructure(name, (Integer) obj);
               }
               break;
            case LONG:
               if (attr.isMultivalued())
               {
                  structAttr = new BasicStructure(name, (Long[]) obj);
               }
               else
               {
                  structAttr = new BasicStructure(name, (Long) obj);
               }
               break;
            case OBJECT:
               if (attr.isMultivalued())
               {
                  this.handleError(METHOD_NAME + "Multivalued OBJECT not supported");
               }
               else
               {
                  structAttr = new BasicStructure(name, obj);
               }
               break;
            default:
               this.handleError(METHOD_NAME + "Unknown Type: " + obj.getClass().getSimpleName());
               break;
         }
      }
      else
      {
         structAttr = new BasicStructure(name, "");
      }

      return structAttr;
   }

   /**
    * @param attr
    * @return
    */
   //----------------------------------------------------------------
   protected final AttributeIF getAttributeFromAttr(final AttrIF attr)
   //----------------------------------------------------------------
   {
      boolean isMultivalued = false;
      Object obj = null;
      String name = null;
      AttributeIF attribute = null;

      name = attr.getName();
      obj = attr.getValue();
      isMultivalued = attr.isMultivalued();

      if (obj != null)
      {
         switch (attr.getType())
         {
            case STRING:
               if (isMultivalued)
               {
                  attribute = new Attribute(name, (String[]) obj);
               }
               else
               {
                  attribute = new Attribute(name, (String) obj);
               }
               break;
            case BOOLEAN:
               if (isMultivalued)
               {
                  attribute = new Attribute(name, (Boolean[]) obj);
               }
               else
               {
                  attribute = new Attribute(name, (Boolean) obj);
               }
               break;
            case INTEGER:
               if (isMultivalued)
               {
                  attribute = new Attribute(name, (Integer[]) obj);
               }
               else
               {
                  attribute = new Attribute(name, (Integer) obj);
               }
               break;
            case LONG:
               if (isMultivalued)
               {
                  attribute = new Attribute(name, (Long[]) obj);
               }
               else
               {
                  attribute = new Attribute(name, (Long) obj);
               }
               break;
            default:
               attribute = new Attribute(name);
               break;
         }
      }
      else
      {
         attribute = new Attribute(name);
      }

      attribute.setEncrypted(attr.isEncrypted());
      attribute.setReadOnly(attr.isReadOnly());
      attribute.setRequired(attr.isRequired());

      return attribute;
   }

   /**
    * @param attribute
    * @return
    */
   //----------------------------------------------------------------
   protected final AttrIF getAttrFromAttribute(final AttributeIF attribute)
   //----------------------------------------------------------------
   {
      boolean isMultivalued = false;
      Object obj = null;
      String name = null;
      AttrIF attr = null;

      name = attribute.getName();
      obj = attribute.getValue();
      isMultivalued = attribute.isMultivalued();

      if (obj != null)
      {
         switch (attribute.getType())
         {
            case STRING:
               if (isMultivalued)
               {
                  attr = new BasicAttr(name, (String[]) obj);
               }
               else
               {
                  attr = new BasicAttr(name, (String) obj);
               }
               break;
            case BOOLEAN:
               if (isMultivalued)
               {
                  attr = new BasicAttr(name, (Boolean[]) obj);
               }
               else
               {
                  attr = new BasicAttr(name, (Boolean) obj);
               }
               break;
            case INTEGER:
               if (isMultivalued)
               {
                  attr = new BasicAttr(name, (Integer[]) obj);
               }
               else
               {
                  attr = new BasicAttr(name, (Integer) obj);
               }
               break;
            case LONG:
               if (isMultivalued)
               {
                  attr = new BasicAttr(name, (Long[]) obj);
               }
               else
               {
                  attr = new BasicAttr(name, (Long) obj);
               }
               break;
         }
      }
      else
      {
         attr = new BasicAttr(name);
      }

      attr.setEncrypted(attribute.isEncrypted());
      attr.setReadOnly(attribute.isReadOnly());
      attr.setRequired(attribute.isRequired());

      return attr;
   }

   /**
    * @param structOut
    * @param output
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final void getReadOutput(final StructureIF structOut, final Output output) throws Exception
   //----------------------------------------------------------------
   {
      int qty = 0;
      Object uniqueId = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String[] names = null;
      ElementIF result = null;
      AttributeIF attr = null;
      StructureIF structSubject = null;
      StructureIF structAttrs = null;

      if (output == null)
      {
         this.handleError(METHOD_NAME + "Output is null");
      }

      qty = output.getResultsSize();

      if (qty == 0)
      {
         this.handleError(METHOD_NAME + "Output has no Result");
      }

      if (qty > 1)
      {
         this.handleError(METHOD_NAME + "More than one result, size="
            + output.getResultsSize());
      }

      result = output.getResults().get(0);


      if (result == null)
      {
         this.handleError(METHOD_NAME + "Result is null");
      }

      uniqueId = result.getUniqueId();
      if (uniqueId == null)
      {
         this.handleError(METHOD_NAME + "UniqueId (from Result) is null");
      }

      structSubject = new BasicStructure(StructureIF.NAME_SUBJECT);

      try
      {
         switch (result.getUniqueIdType())
         {
            case STRING:
               structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (String) uniqueId));
               break;
            case INTEGER:
               structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Integer) uniqueId));
               break;
            case LONG:
               structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Long) uniqueId));
               break;
         }
      }
      catch (StructureException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      structAttrs = new BasicStructure(StructureIF.NAME_ATTRIBUTES);

      try
      {
         structSubject.addChild(structAttrs);
      }
      catch (StructureException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      names = result.getAttributeNames();
      for (int i = 0; i < names.length; i++)
      {
         attr = result.getAttribute(names[i]);
         if (attr != null)
         {
            try
            {
               structAttrs.addChild(this.getStructFromAttribute(attr));
            }
            catch (StructureException ex)
            {
               this.handleError(METHOD_NAME + ex.getMessage());
            }
         }
      }

      try
      {
         structOut.addChild(structSubject);
      }
      catch (StructureException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      return;
   }

   /**
    * @param structOut
    * @param output
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final void getSearchOutput(final StructureIF structOut, final Output output) throws Exception
   //----------------------------------------------------------------
   {
      int iLen = 0;
      Object uid = null;
      String uniqueId = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String uriBase = null;
      String uriChild = null;
      String[] names = null;
      List<ElementIF> list = null;
      Iterator<ElementIF> iter = null;
      ElementIF result = null;
      AttributeIF attr = null;
      StructureIF structResults = null;
      StructureIF structSubject = null;
      StructureIF structAttrs = null;

      try
      {
         uriBase = this.getStringValue(StructureIF.NAME_URI, structOut, false);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + "can not get 'uriBase': " + ex.getMessage());
      }

      structResults = new BasicStructure(StructureIF.NAME_RESULTS);
      structResults.setMultiValued(true);
      structResults.setType(StructureType.STRUCTURE); // explicitly set, results maybe empty 

      if (output != null)
      {
         iLen = output.getResultsSize();
         if (iLen > 0)
         {
            list = output.getResults();

            iter = list.iterator();
            while (iter.hasNext())
            {
               result = iter.next();

               if (result != null)
               {
                  uid = result.getUniqueId();
                  if (uid == null)
                  {
                     this.handleError(METHOD_NAME + "UniqueId (from Result) is null");
                  }

                  uniqueId = uid.toString();

                  if (uriBase.endsWith("/"))
                  {
                     uriChild = uriBase + uniqueId;
                  }
                  else
                  {
                     uriChild = uriBase + "/" + uniqueId;
                  }

                  structSubject = new BasicStructure(StructureIF.NAME_SUBJECT);
                  structAttrs = new BasicStructure(StructureIF.NAME_ATTRIBUTES);

                  try
                  {
                     structSubject.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));
                     switch (result.getUniqueIdType())
                     {
                        case STRING:
                           structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (String) uid));
                           break;
                        case INTEGER:
                           structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Integer) uid));
                           break;
                        case LONG:
                           structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Long) uid));
                           break;
                     }
                     structSubject.addChild(structAttrs);
                  }
                  catch (StructureException ex)
                  {
                     this.handleError(METHOD_NAME + "can not add child Structure: " + ex.getMessage());
                  }

                  names = result.getAttributeNames();
                  for (int i = 0; i < names.length; i++)
                  {
                     attr = result.getAttribute(names[i]);
                     if (attr != null)
                     {
                        try
                        {
                           structAttrs.addChild(this.getStructFromAttribute(attr));
                        }
                        catch (StructureException ex)
                        {
                           this.handleError(METHOD_NAME + "can not add child Structure: " + ex.getMessage());
                        }
                     }
                  }
                  try
                  {
                     structResults.addValue(structSubject);
                  }
                  catch (StructureException ex)
                  {
                     this.handleError(METHOD_NAME + "can not add value to Structure: " + ex.getMessage());
                  }
               }
            }
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "Output is null.");
      }

      try
      {
         structOut.addChild(structResults);
         structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, iLen));
      }
      catch (StructureException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      return;
   }

   /**
    * @param struct
    * @param input
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final void processSubjectInput(final StructureIF struct, final Input input) throws Exception
   //----------------------------------------------------------------
   {
      Object value = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      StructureIF structAttrs = null;
      StructureIF structUid = null;

      if (struct == null)
      {
         this.handleError(METHOD_NAME + "Structure is null");
      }

      if (input == null)
      {
         this.handleError(METHOD_NAME + "Input is null");
      }

      structAttrs = struct.getChild(StructureIF.NAME_ATTRIBUTES);

      if (structAttrs == null)
      {
         this.handleError(METHOD_NAME + "Subject has a null 'attributes' Structure");
      }

      Input.build(input, structAttrs);

      /*
       * Check for uniqueId
       */

      structUid = struct.getChild(StructureIF.NAME_UNIQUEID);
      if (structUid != null)
      {
         value = structUid.getValue();
         if (value != null)
         {
            switch (structUid.getValueType())
            {
               case STRING:
                  input.setUniqueId((String) value);
                  break;
               case INTEGER:
                  input.setUniqueId((Integer) value);
                  break;
               case LONG:
                  input.setUniqueId((Long) value);
                  break;
            }
         }
      }

      return;
   }

   /**
    *
    * @return boolean
    */
   //----------------------------------------------------------------
   protected final boolean isDebug()
   //----------------------------------------------------------------
   {
      return _engine.isDebug();
   }

   /**
    *
    * @return int
    */
   //----------------------------------------------------------------
   protected final int getDebugLevelAsInt()
   //----------------------------------------------------------------
   {
      return _engine.getDebugLevelAsInt();
   }

   /**
    *
    * @param session
    * @param str
    */
   //----------------------------------------------------------------
   protected final void logInfo(SessionIF session, String str)
   //----------------------------------------------------------------
   {
      _engine.logInfo(session, str);
      return;
   }

   /**
    *
    * @param session
    * @param str
    */
   //----------------------------------------------------------------
   protected final void logWarning(SessionIF session, String str)
   //----------------------------------------------------------------
   {
      _engine.logWarning(session, str);
      return;
   }

   /**
    *
    * @param session
    * @param str
    */
   //----------------------------------------------------------------
   protected final void logError(SessionIF session, String str)
   //----------------------------------------------------------------
   {
      _engine.logError(session, str);
      return;
   }

   /**
    * @param msg
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final void handleError(final String msg) throws Exception
   //----------------------------------------------------------------
   {
      this.handleError(null, msg);
   }

   /**
    * @param session
    * @param msg
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final void handleError(SessionIF session, final String msg) throws Exception
   //----------------------------------------------------------------
   {
      if (this.isDebug())
      {
         this.logError(session, msg);
      }

      throw new Exception(msg);
   }

   //----------------------------------------------------------------
   private StructureIF getStructFromProperties(final Properties props) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String name = null;
      String value = null;
      Integer num = null;
      SortedSet<String> propKeys = null;
      Iterator<String> propIter = null;
      StructureIF structProps = null;
      StructureIF structProp = null;

      structProps = new BasicStructure(StructureIF.NAME_PROPERTIES);

      if (props != null && !props.isEmpty())
      {
         // Sort the properties by adding them to a TreeSet

         propKeys = new TreeSet<String>();

         for (Enumeration<Object> e = props.keys(); e.hasMoreElements();)
         {
            propKeys.add((String) e.nextElement());
         }

         propIter = propKeys.iterator();

         while (propIter.hasNext())
         {
            name = null;
            value = null;
            name = propIter.next();
            value = props.getProperty(name);

            /*
             * <name type="string">value</name>
             */

            if (value == null)
            {
               value = "";
            }

            /*
             * Is the value a INTEGER or BOOLEAN
             */

            if (value.equalsIgnoreCase("true"))
            {
               structProp = new BasicStructure(name, Boolean.valueOf(true));

            }
            else if (value.equalsIgnoreCase("false"))
            {
               structProp = new BasicStructure(name, Boolean.valueOf(false));
            }
            else
            {
               num = null;
               try
               {
                  num = new Integer(value);
               }
               catch (NumberFormatException ex)
               {
                  num = null;
               }

               if (num != null)
               {
                  structProp = new BasicStructure(name, num);
               }
               else
               {
                  structProp = new BasicStructure(name, value);
               }
            }

            try
            {
               structProps.addChild(structProp);
            }
            catch (StructureException ex)
            {
               this.handleError(METHOD_NAME + ex.getMessage());
            }
         }
      }

      return structProps;
   }

   /**
    * 
    * @param struct
    * @return String containing the base URI
    * @throws Exception 
    * @since 2.2.0
    */
   //----------------------------------------------------------------
   protected final String getUriBase(StructureIF struct) throws Exception
   //----------------------------------------------------------------
   {
      boolean allowNull = false;
      String uriBase = null;
      String propValue = null;
      Properties props = null;

      uriBase = this.getStringValue(StructureIF.NAME_URI, struct, allowNull);

      /*
       * Check for a property that could override the uriBase
       */

      props = struct.getProperties();

      if (props != null)
      {
         propValue = props.getProperty(StructureIF.NAME_URI);
         if (propValue != null && propValue.length() > 0)
         {
            uriBase = propValue;
         }
      }

      return uriBase;
   }
}
