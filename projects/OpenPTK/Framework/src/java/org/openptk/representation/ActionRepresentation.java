/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010-2012 Project OpenPTK
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

import org.openptk.api.Opcode;
import org.openptk.api.State;
import org.openptk.context.actions.ActionIF;
import org.openptk.engine.EngineIF;
import org.openptk.session.SessionIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman
 */
//===================================================================
public class ActionRepresentation extends Representation
//===================================================================
{
   private String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public ActionRepresentation(final EngineIF engine)
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
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      boolean allowNull = false;
      String uriBase = null;
      String actionId = null;
      ActionIF action = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);
      actionId = this.getStringValue(StructureIF.NAME_ACTIONID, structParamsPath, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      action = this.getEngine().getAction(actionId);

      if (action == null)
      {
         this.handleError(session, METHOD_NAME + "Action '" + actionId + "' is null");
      }

      structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, actionId));

      structOut.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, action.getDescription()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, action.getState().toString()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, action.getStatus()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_CATEGORY, action.getCategory().toString()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_CLASSNAME, action.getClass().getName()));

      structOut.addChild(new BasicStructure(StructureIF.NAME_CONTEXT, action.getContext()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_MODE, action.getMode().toString()));

      /*
       * Properties
       */

      structOut.addChild(this.getPropsAsStruct(action));

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
      String[] actionIds = null;
      ActionIF action = null;
      StructureIF structOut = null;
      StructureIF structAction = null;
      StructureIF structActions = null;

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      structActions = new BasicStructure(StructureIF.NAME_ACTIONS);
      structActions.setMultiValued(true);

      actionIds = this.getEngine().getActionNames();
      len = actionIds.length;

      structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));

      for (int i = 0; i < len; i++)
      {
         id = actionIds[i];
         action = this.getEngine().getAction(id);
         if (action != null)
         {
            structAction = new BasicStructure(StructureIF.NAME_ACTION);

            structAction.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, id));

            if (uriBase.endsWith("/"))
            {
               uriChild = uriBase + id;
            }
            else
            {
               uriChild = uriBase + "/" + id;
            }

            structAction.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

            structActions.addValue(structAction);
         }
      }

      structOut.addChild(structActions);

      structOut.setState(State.SUCCESS);

      return structOut;
   }
}
