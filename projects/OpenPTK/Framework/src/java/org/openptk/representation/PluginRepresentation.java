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
import org.openptk.plugin.PluginIF;
import org.openptk.session.SessionIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class PluginRepresentation extends Representation
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public PluginRepresentation(final EngineIF engine)
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
      String pluginId = null;
      PluginIF plugin = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);
      pluginId = this.getStringValue(StructureIF.NAME_PLUGINID, structParamsPath, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      plugin = this.getEngine().getPlugin(pluginId);

      if (plugin == null)
      {
         this.handleError(session, METHOD_NAME + "Plugin '" + pluginId + "' is null");
      }

      structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, pluginId));

      structOut.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, plugin.getDescription()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, plugin.getState().toString()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, plugin.getStatus()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_CATEGORY, plugin.getCategory().toString()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_CLASSNAME, plugin.getClass().getName()));

      /*
       * Properties
       */

      structOut.addChild(this.getPropsAsStruct(plugin));

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
      String id = null;
      String[] pluginIds = null;
      PluginIF plugin = null;
      StructureIF structOut = null;
      StructureIF structPlugin = null;
      StructureIF structPlugins = null;

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      structPlugins = new BasicStructure(StructureIF.NAME_PLUGINS);
      structPlugins.setMultiValued(true);

      pluginIds = this.getEngine().getPluginNames();
      len = pluginIds.length;

      structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));

      for (int i = 0; i < len; i++)
      {
         id = pluginIds[i];
         plugin = this.getEngine().getPlugin(id);
         if (plugin != null)
         {
            structPlugin = new BasicStructure(StructureIF.NAME_PLUGIN);

            structPlugin.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, id));

            if (uriBase.endsWith("/"))
            {
               uriChild = uriBase + id;
            }
            else
            {
               uriChild = uriBase + "/" + id;
            }

            structPlugin.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

            structPlugins.addValue(structPlugin);
         }
      }

      structOut.addChild(structPlugins);

      structOut.setState(State.SUCCESS);

      return structOut;
   }
}
