/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2011-2012 Project OpenPTK
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
import org.openptk.authenticate.PrincipalIF;
import org.openptk.engine.EngineIF;
import org.openptk.session.SessionIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;
import org.openptk.structure.StructureType;

/**
 * @author Scott Fehrman
 * @since 2.2
 */
//===================================================================
public class SessionInfoRepresentation extends Representation
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public SessionInfoRepresentation(final EngineIF engine)
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
         default:
            throw new Exception(METHOD_NAME + ": Unsupported Operation: '"
               + opcode.toString() + "'");
      }

      return structOut;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private StructureIF doRead(final SessionIF sessionUser, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      boolean allowNull = false;
      String uriBase = null;
      String sessionId = null;
      Object val = null;
      SessionIF session = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structPrincipal = null;
      StructureIF structChild = null;
      PrincipalIF principal = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(sessionUser, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      sessionId = this.getStringValue(StructureIF.NAME_SESSIONID, structIn, allowNull);

      session = this.getEngine().getSession(sessionId); // Note: session is a "copy"

      if (session == null)
      {
         this.handleError(sessionUser, METHOD_NAME + ": Session is null.");
      }

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      /*
       * enclose the session data in a "collection" ...
       * future support for multiple sessions
       */

      structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, sessionId));
      structOut.addChild(new BasicStructure(StructureIF.NAME_TYPE, session.getType().toString()));

      /*
       * Get the Principal
       */

      principal = session.getPrincipal();
      structPrincipal = new BasicStructure(StructureIF.NAME_PRINCIPAL);

      if (principal != null)
      {
         val = principal.getUniqueId();
         if (val != null)
         {
            switch (principal.getUniqueIdType())
            {
               case STRING:
                  structPrincipal.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (String) val));
                  break;
               case INTEGER:
                  structPrincipal.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Integer) val));
                  break;
               case LONG:
                  structPrincipal.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Long) val));
                  break;
            }
         }
         else
         {
            /*
             * There is no value ... need to explicitly set the type ... use STRING
             */
            structChild = new BasicStructure(StructureIF.NAME_UNIQUEID);
            structChild.setType(StructureType.STRING);
            structPrincipal.addChild(structChild);
         }

         val = principal.getContextId();
         if (val != null)
         {
            structPrincipal.addChild(new BasicStructure(StructureIF.NAME_CONTEXTID, val.toString()));
         }
         else
         {
            /*
             * There is no value ... need to explicitly set the type ... use STRING
             */
            structChild = new BasicStructure(StructureIF.NAME_CONTEXTID);
            structChild.setType(StructureType.STRING);
            structPrincipal.addChild(structChild);
         }
      }
      else
      {
         /*
          * There is no value ... need to explicitly set the type ... use STRING
          */
         structChild = new BasicStructure(StructureIF.NAME_UNIQUEID);
         structChild.setType(StructureType.STRING);
         structPrincipal.addChild(structChild);

         structChild = new BasicStructure(StructureIF.NAME_CONTEXTID);
         structChild.setType(StructureType.STRING);
         structPrincipal.addChild(structChild);
      }

      structOut.addChild(structPrincipal);

      structOut.setState(State.SUCCESS);

      return structOut;
   }
}
