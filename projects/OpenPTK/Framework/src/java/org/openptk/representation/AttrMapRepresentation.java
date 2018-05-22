/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2012-2013 Project OpenPTK
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
package org.openptk.representation;

import java.util.List;
import java.util.Set;

import org.openptk.api.Opcode;
import org.openptk.api.State;
import org.openptk.config.mapper.AttrMapIF;
import org.openptk.config.mapper.Data;
import org.openptk.config.mapper.Datum;
import org.openptk.config.mapper.ExternalAttrIF;
import org.openptk.config.mapper.ExternalAttrIF.Mode;
import org.openptk.config.mapper.ExternalModeIF;
import org.openptk.config.mapper.ExternalSubAttrIF;
import org.openptk.config.mapper.Match;
import org.openptk.config.mapper.Processes;
import org.openptk.config.mapper.SubAttributes;
import org.openptk.engine.EngineIF;
import org.openptk.session.SessionIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman
 * @since 2.2.0
 */
//===================================================================
public class AttrMapRepresentation extends Representation
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public AttrMapRepresentation(final EngineIF engine)
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
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
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
            throw new Exception(METHOD_NAME + "Unsupported Operation: '"
               + opcode.toString() + "'");
         }
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doRead(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      boolean allowNull = false;
      String uriBase = null;
      String attrmapId = null;
      StructureIF structResponse = null;
      StructureIF structParamsPath = null;
      AttrMapIF attrmap = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);
      attrmapId = this.getStringValue(StructureIF.NAME_ATTRMAPID, structParamsPath, allowNull);

      structResponse = new BasicStructure(StructureIF.NAME_RESPONSE);
      structResponse.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      attrmap = this.getEngine().getAttrMap(attrmapId);

      if (attrmap == null)
      {
         this.handleError(session, METHOD_NAME + "AttrMap is null.");
      }

      structResponse.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, attrmapId));
      structResponse.addChild(new BasicStructure(StructureIF.NAME_CLASSNAME, attrmap.getClass().getCanonicalName()));
      structResponse.addChild(new BasicStructure(StructureIF.NAME_LENGTH, attrmap.getAttributesSize()));

      /*
       * Attributes
       */

      structResponse.addChild(this.getExternalAttrsAsStruct(attrmap));

      structResponse.setState(State.SUCCESS);

      return structResponse;
   }

   //----------------------------------------------------------------
   private StructureIF doSearch(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      boolean allowNull = false;
      int len = 0;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String uriBase = null;
      String uriChild = null;
      String id = null;
      String[] attrMapIds = null;
      StructureIF structOut = null;
      StructureIF structAttrMap = null;
      StructureIF structAttrMaps = null;
      AttrMapIF attrMap = null;

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      structAttrMaps = new BasicStructure(StructureIF.NAME_ATTRMAPS);
      structAttrMaps.setMultiValued(true);

      attrMapIds = this.getEngine().getAttrMapNames();
      len = attrMapIds.length;

      structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));

      for (int i = 0; i < len; i++)
      {
         id = attrMapIds[i];
         attrMap = this.getEngine().getAttrMap(id);
         if (attrMap != null)
         {
            structAttrMap = new BasicStructure(StructureIF.NAME_ATTRMAP);

            structAttrMap.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, id));

            if (uriBase.endsWith("/"))
            {
               uriChild = uriBase + id;
            }
            else
            {
               uriChild = uriBase + "/" + id;
            }

            structAttrMap.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

            structAttrMaps.addValue(structAttrMap);
         }
      }

      structOut.addChild(structAttrMaps);

      structOut.setState(State.SUCCESS);

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF getExternalAttrsAsStruct(final AttrMapIF attrmap) throws Exception
   //----------------------------------------------------------------
   {
      boolean useFwKey = false;
      String value = null;
      StructureIF structAttributes = null;
      StructureIF structAttribute = null;
      StructureIF structExtMode = null;
      Mode mode = null;
      ExternalAttrIF extAttr = null;
      ExternalModeIF extMode = null;
      List<String> listAttrNames = null;

      structAttributes = new BasicStructure(StructureIF.NAME_ATTRIBUTES);

      listAttrNames = attrmap.getAttributesNames(useFwKey);
      if (listAttrNames != null && !listAttrNames.isEmpty())
      {
         for (String attrName : listAttrNames)
         {
            if (attrName != null && attrName.length() > 0)
            {
               structAttribute = new BasicStructure(StructureIF.NAME_ATTRIBUTE);
               extAttr = attrmap.getAttribute(attrName, useFwKey);
               if (extAttr != null)
               {
                  structAttribute.addChild(new BasicStructure(StructureIF.NAME_NAME, attrName));

                  value = extAttr.getMapTo();
                  if (value == null)
                  {
                     value = "";
                  }
                  structAttribute.addChild(new BasicStructure(StructureIF.NAME_MAPTO, value));

                  structAttribute.addChild(new BasicStructure(StructureIF.NAME_REQUIRED, extAttr.isRequired()));

                  value = extAttr.getTypeAsString();
                  if (value == null)
                  {
                     value = "__UNKNOWN__";
                  }
                  structAttribute.addChild(new BasicStructure(StructureIF.NAME_TYPE, value));

                  structAttribute.addChild(new BasicStructure(StructureIF.NAME_ALLOWMULTI, extAttr.allowMultivalue()));

                  mode = extAttr.getMode();
                  if (mode == null)
                  {
                     value = "";
                  }
                  else
                  {
                     value = mode.toString();
                  }
                  structAttribute.addChild(new BasicStructure(StructureIF.NAME_MODE, value));

                  if (mode != null)
                  {
                     extMode = extAttr.getInbound();
                     if (extMode != null)
                     {
                        structExtMode = this.getExtModeAsStructure(extMode, ExternalAttrIF.Mode.INBOUND.toString());
                        if (structExtMode != null)
                        {
                           structAttribute.addChild(structExtMode);
                        }
                     }
                     extMode = extAttr.getOutbound();
                     if (extMode != null)
                     {
                        structExtMode = this.getExtModeAsStructure(extMode, ExternalAttrIF.Mode.OUTBOUND.toString());
                        if (structExtMode != null)
                        {
                           structAttribute.addChild(structExtMode);
                        }
                     }
                  }
               }
               structAttributes.addChild(structAttribute);
            }
         }
      }
      return structAttributes;
   }

   //----------------------------------------------------------------
   private StructureIF getExtModeAsStructure(final ExternalModeIF extMode, final String mode) throws Exception
   //----------------------------------------------------------------
   {
      StructureIF structExtMode = null;
      StructureIF structProcesses = null;
      StructureIF structSubAttrs = null;
      StructureIF structData = null;
      SubAttributes subAttrs = null;
      Processes processes = null;
      Data data = null;

      structExtMode = new BasicStructure(mode);

      /*
       * Processes
       */

      processes = extMode.getProcesses();
      if (processes != null)
      {
         structProcesses = this.getProcessesAsStructure(processes);
         structExtMode.addChild(structProcesses);
      }

      /*
       * SubAttributes
       */

      subAttrs = extMode.getSubAttributes();
      if (subAttrs != null)
      {
         structSubAttrs = this.getSubAttrsAsStructure(subAttrs);
         structExtMode.addChild(structSubAttrs);
      }

      /*
       * Data
       */

      data = extMode.getData();
      if (data != null)
      {
         structData = this.getDataAsStructure(data);
         structExtMode.addChild(structData);
      }

      return structExtMode;
   }

   //----------------------------------------------------------------
   private StructureIF getProcessesAsStructure(final Processes processes) throws Exception
   //----------------------------------------------------------------
   {
      String value = null;
      StructureIF structProcesses = null;
      StructureIF structProcess = null;

      if (processes != null)
      {
         structProcesses = new BasicStructure(StructureIF.NAME_PROCESSES);

         value = processes.getValue();
         if (value != null && value.length() > 0)
         {
            structProcess = new BasicStructure(value);
            structProcesses.addChild(structProcess);
         }
      }

      return structProcesses;
   }

   //----------------------------------------------------------------
   private StructureIF getDataAsStructure(final Data data) throws Exception
   //----------------------------------------------------------------
   {
      String value = null;
      StructureIF structData = null;
      StructureIF structDatum = null;
      StructureIF structMatch = null;
      StructureIF structProcesses = null;
      Datum datum = null;
      Match match = null;
      Processes processes = null;
      Set<String> datumIds = null;
      Set<String> matchIds = null;

      if (data != null)
      {
         structData = new BasicStructure(StructureIF.NAME_DATA);
         structData.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, data.getId()));
         datumIds = data.getDatumIds();
         for (String datumId : datumIds)
         {
            if (datumId != null && datumId.length() > 0)
            {
               structDatum = new BasicStructure(StructureIF.NAME_DATUM);
               structDatum.addChild(new BasicStructure(StructureIF.NAME_NAME, datumId));

               datum = data.getDatum(datumId);

               if (datum != null)
               {
                  value = datum.getMapTo();
                  if (value != null && value.length() > 0)
                  {
                     structDatum.addChild(new BasicStructure(StructureIF.NAME_MAPTO, value));
                  }

                  /*
                   * Processes
                   */

                  processes = datum.getProcesses();
                  if (processes != null)
                  {
                     structProcesses = this.getProcessesAsStructure(processes);
                     structDatum.addChild(structProcesses);
                  }
               }

               structData.addChild(structDatum);
            }
         }

         matchIds = data.getMatchIds();
         for (String matchId : matchIds)
         {
            if (matchId != null && matchId.length() > 0)
            {
               structMatch = new BasicStructure(StructureIF.NAME_MATCH);
               structMatch.addChild(new BasicStructure(StructureIF.NAME_NAME, matchId));

               match = data.getMatch(matchId);
               if (match != null)
               {
                  value = match.getValue();
                  if (value != null && value.length() > 0)
                  {
                     structMatch.addChild(new BasicStructure(StructureIF.NAME_VALUE, value));
                  }

                  value = match.getDatumId();
                  if (value != null && value.length() > 0)
                  {
                     structMatch.addChild(new BasicStructure(StructureIF.NAME_DATUM, value));
                  }

                  value = match.getMapFrom();
                  if (value != null && value.length() > 0)
                  {
                     structMatch.addChild(new BasicStructure(StructureIF.NAME_MAPFROM, value));
                  }

                  value = match.getMapTo();
                  if (value != null && value.length() > 0)
                  {
                     structMatch.addChild(new BasicStructure(StructureIF.NAME_MAPTO, value));
                  }
               }

               structData.addChild(structMatch);
            }
         }
      }

      return structData;
   }

   //----------------------------------------------------------------
   private StructureIF getSubAttrsAsStructure(final SubAttributes subAttrs) throws Exception
   //----------------------------------------------------------------
   {
      String value = null;
      ExternalSubAttrIF extSubAttr = null;
      Mode mode = null;
      Processes processes = null;
      Data data = null;
      StructureIF structSubAttrs = null;
      StructureIF structSubAttr = null;
      StructureIF structProcesses = null;
      StructureIF structData = null;
      Set<String> names = null;

      if (subAttrs != null)
      {
         structSubAttrs = new BasicStructure(StructureIF.NAME_SUBATTRS);

         names = subAttrs.getNames();
         if (names != null && !names.isEmpty())
         {
            for (String name : names)
            {
               if (name != null && name.length() > 0)
               {
                  extSubAttr = subAttrs.getSubAttribute(name);
                  if (extSubAttr != null)
                  {
                     structSubAttr = new BasicStructure(StructureIF.NAME_SUBATTR);

                     structSubAttr.addChild(new BasicStructure(StructureIF.NAME_NAME, name));

                     value = extSubAttr.getMapTo();
                     if (value == null)
                     {
                        value = "";
                     }
                     structSubAttr.addChild(new BasicStructure(StructureIF.NAME_MAPTO, value));

                     structSubAttr.addChild(new BasicStructure(StructureIF.NAME_REQUIRED, extSubAttr.isRequired()));

                     value = extSubAttr.getTypeAsString();
                     if (value == null)
                     {
                        value = "__UNKNOWN__";
                     }
                     structSubAttr.addChild(new BasicStructure(StructureIF.NAME_TYPE, value));

                     structSubAttr.addChild(new BasicStructure(StructureIF.NAME_ALLOWMULTI, extSubAttr.allowMultivalue()));

                     /*
                      * Processes
                      */

                     processes = extSubAttr.getProcesses();
                     if (processes != null)
                     {
                        structProcesses = this.getProcessesAsStructure(processes);
                        structSubAttr.addChild(structProcesses);
                     }

                     /*
                      * Data
                      */

                     data = extSubAttr.getData();
                     if (data != null)
                     {
                        structData = this.getDataAsStructure(data);
                        structSubAttr.addChild(structData);
                     }

                     structSubAttrs.addChild(structSubAttr);
                  }
               }
            }
         }

      }

      return structSubAttrs;
   }
}
