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

import org.openptk.api.Opcode;
import org.openptk.api.State;
import org.openptk.engine.EngineIF;
import org.openptk.engine.Stats;
import org.openptk.session.SessionIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class StatRepresentation extends Representation
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public StatRepresentation(final EngineIF engine)
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
            structOut = this.doRead(session, structIn);
            break;
         case SEARCH:
            structOut = this.doSearch(session, structIn);
            break;
         default:
            this.handleError(session, METHOD_NAME + "Unsupported Operation: '"
               + opcode.toString() + "'");
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doRead(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      boolean allowNull = false;
      String uriBase = null;
      String contextId = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);
      contextId = this.getStringValue(StructureIF.NAME_STATID, structParamsPath, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));
      structOut.addChild(new BasicStructure(StructureIF.NAME_CONTEXT, contextId));

      structOut.addChild(this.getStatsAsStruct(this.getEngine(), contextId));

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
      String contextId = null;
      String[] contextIds = null;
      StructureIF structOut = null;
      StructureIF structStat = null;
      StructureIF structStats = null;

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      structStats = new BasicStructure(StructureIF.NAME_STATS);
      structStats.setMultiValued(true);

      contextIds = this.getEngine().getContextNames();
      len = contextIds.length;
      structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));

      for (int i = 0; i < len; i++)
      {
         contextId = contextIds[i];

         structStat = new BasicStructure(StructureIF.NAME_STAT);

         structStat.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, contextId));

         if (uriBase.endsWith("/"))
         {
            uriChild = uriBase + contextId;
         }
         else
         {
            uriChild = uriBase + "/" + contextId;
         }

         structStat.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

         structStats.addValue(structStat);
      }

      structOut.addChild(structStats);

      structOut.setState(State.SUCCESS);

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF getStatsAsStruct(final EngineIF engine, final String contextId) throws Exception
   //----------------------------------------------------------------
   {
      Opcode[] opcodes = Opcode.values();
      Opcode opcode = null;
      StructureIF structStats = null;
      StructureIF structStat = null;
      Stats stats = null;
      Long value = 0L;

      structStats = new BasicStructure(StructureIF.NAME_STATS);

      for (int i = 0; i < opcodes.length; i++)
      {
         opcode = opcodes[i];
         stats = engine.getStats(contextId, opcode);
         if (stats != null)
         {
            structStat = new BasicStructure(opcode.toString());

            structStat.addChild(new BasicStructure(StructureIF.NAME_COUNT, stats.count()));

            value = new Long(stats.average());
            structStat.addChild(new BasicStructure(StructureIF.NAME_AVERAGE, value.intValue()));

            value = new Long(stats.min());
            structStat.addChild(new BasicStructure(StructureIF.NAME_MINIMUM, value.intValue()));

            value = new Long(stats.max());
            structStat.addChild(new BasicStructure(StructureIF.NAME_MAXIMUM, value.intValue()));

            structStats.addChild(structStat);
         }
      }

      return structStats;
   }
}
