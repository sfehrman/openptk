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
 * Portions Copyright 2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.representation;

import org.openptk.api.ElementIF;
import org.openptk.api.Opcode;
import org.openptk.api.State;
import org.openptk.engine.EngineIF;
import org.openptk.session.SessionIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.ConverterIF;
import org.openptk.structure.ConverterType;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class ConverterRepresentation extends Representation
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public ConverterRepresentation(final EngineIF engine)
   //----------------------------------------------------------------
   {
      super(engine);
      return;
   }

   /**
    * @param opcode
    * @param structIn
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public StructureIF execute(final Opcode opcode, final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      StructureIF structOut = null;

      if (structIn == null)
      {
         this.handleError(session, METHOD_NAME + "Input Structure is null.");
      }

      if (this.isDebug())
      {
         this.logInfo(session, METHOD_NAME + "Opcode="
            + opcode.toString() + ", " + structIn.toString());
      }

      switch (opcode)
      {
         case READ:
         {
            structOut = this.doRead(session, structIn);
            break;
         }
         case SEARCH:
         {
            structOut = this.doSearch(session, structIn);
            break;
         }
         default:
         {
            this.handleError(session, METHOD_NAME + "Unsupported Operation: '"
               + opcode.toString() + "'");
         }
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doRead(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      Object uid = null;
      boolean allowNull = false;
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      String uriBase = null;
      String converterId = null;
      String[] structInfoIds = null;
      ElementIF elem = null;
      ConverterType type = null;
      ConverterType[] types = null;
      ConverterIF converter = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structStructs = null;
      StructureIF structStruct = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);
      converterId = this.getStringValue(StructureIF.NAME_CONVERTERID, structParamsPath, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      types = ConverterType.values();
      for (int i = 0; i < types.length; i++)
      {
         if (types[i].toString().equalsIgnoreCase(converterId))
         {
            type = types[i];
         }
      }
      if (type == null)
      {
         this.handleError(session, METHOD_NAME + "Invalid Converter Type '" + converterId + "'");
      }

      converter = this.getEngine().getConverter(type);

      if (converter == null)
      {
         this.handleError(session, METHOD_NAME + "Conveter is null, type='" + converterId + "'");
      }


      structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, converterId));

      /*
       * Structure Info
       */

      structStructs = new BasicStructure(StructureIF.NAME_STRUCTURES);

      structInfoIds = converter.getStructInfoIds();

      for (int i = 0; i < structInfoIds.length; i++)
      {
         elem = converter.getStructInfo(structInfoIds[i]);
         if (elem != null)
         {
            structStruct = new BasicStructure(StructureIF.NAME_STRUCTURE);

            uid = elem.getUniqueId();
            if (uid != null)
            {
               switch (elem.getUniqueIdType())
               {
                  case STRING:
                     structStruct.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (String) uid));
                     break;
                  case INTEGER:
                     structStruct.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Integer) uid));
                     break;
                  case LONG:
                     structStruct.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Long) uid));
                     break;
               }
            }

            structStruct.addChild(this.getPropsAsStruct(elem));
            structStructs.addChild(structStruct);
         }
      }

      structOut.addChild(structStructs);

      structOut.setState(State.SUCCESS);

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doSearch(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      boolean allowNull = false;
      int len = 0;
      String METHOD_NAME = CLASS_NAME + ":doSearch(): ";
      String uriBase = null;
      String uriChild = null;
      ConverterType type = null;
      ConverterType[] types = null;
      ConverterIF converter = null;
      StructureIF structOut = null;
      StructureIF structConverter = null;
      StructureIF structConverters = null;

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      structConverters = new BasicStructure(StructureIF.NAME_CONVERTERS);
      structConverters.setMultiValued(true);

      types = ConverterType.values();
      len = types.length;

      structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));

      for (int i = 0; i < len; i++)
      {
         type = types[i];
         converter = this.getEngine().getConverter(type);
         if (converter != null)
         {
            structConverter = new BasicStructure(StructureIF.NAME_CONVERTER);

            structConverter.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, type.toString()));

            if (uriBase.endsWith("/"))
            {
               uriChild = uriBase + type.toString();
            }
            else
            {
               uriChild = uriBase + "/" + type.toString();
            }

            structConverter.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

            structConverters.addValue(structConverter);
         }
      }

      structOut.addChild(structConverters);

      structOut.setState(State.SUCCESS);

      return structOut;
   }
}
