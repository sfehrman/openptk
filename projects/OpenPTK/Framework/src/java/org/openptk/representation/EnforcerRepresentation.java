/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2012 Project OpenPTK
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
import org.openptk.authorize.EnforcerIF;
import org.openptk.authorize.Environment;
import org.openptk.engine.EngineIF;
import org.openptk.session.SessionIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Project OpenPTK
 */
//===================================================================
public class EnforcerRepresentation extends Representation
//===================================================================
{

   private String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public EnforcerRepresentation(final EngineIF engine)
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
      String enforcerid = null;
      EnforcerIF enforcer = null;
      Environment env = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
                 + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);
      enforcerid = this.getStringValue(StructureIF.NAME_ENFORCERID, structParamsPath, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      enforcer = this.getEngine().getEnforcer(enforcerid);

      if (enforcer == null)
      {
         this.handleError(session, METHOD_NAME + "Enforcer '" + enforcerid + "' is null");
      }

      structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, enforcerid));

      structOut.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, enforcer.getDescription()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, enforcer.getState().toString()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, enforcer.getStatus()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_CATEGORY, enforcer.getCategory().toString()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_CLASSNAME, enforcer.getClass().getName()));

      env = enforcer.getEnvironment();
      if (env != null)
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_ENVIRONMENT, env.toString()));
      }

      structOut.addChild(new BasicStructure(StructureIF.NAME_DECIDERID, enforcer.getDeciderId()));

      /*
       * Properties
       */

      structOut.addChild(this.getPropsAsStruct(enforcer));

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
      String[] enforcerIds = null;
      EnforcerIF enforcer = null;
      StructureIF structOut = null;
      StructureIF structEnforcer = null;
      StructureIF structEnforcers = null;

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      structEnforcers = new BasicStructure(StructureIF.NAME_ENFORCERS);
      structEnforcers.setMultiValued(true);

      enforcerIds = this.getEngine().getEnforcerNames();
      len = enforcerIds.length;

      structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));

      for (String id : enforcerIds)
      {
         enforcer = this.getEngine().getEnforcer(id);
         if (enforcer != null)
         {
            structEnforcer = new BasicStructure(StructureIF.NAME_ENFORCER);

            structEnforcer.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, id));

            if (uriBase.endsWith("/"))
            {
               uriChild = uriBase + id;
            }
            else
            {
               uriChild = uriBase + "/" + id;
            }

            structEnforcer.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

            structEnforcers.addValue(structEnforcer);
         }
      }

      structOut.addChild(structEnforcers);

      structOut.setState(State.SUCCESS);

      return structOut;
   }
}
