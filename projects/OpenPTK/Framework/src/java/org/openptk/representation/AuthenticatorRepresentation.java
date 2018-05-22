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
import org.openptk.authenticate.AuthenticatorIF;
import org.openptk.engine.EngineIF;
import org.openptk.session.SessionIF;
import org.openptk.session.SessionType;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public class AuthenticatorRepresentation extends Representation
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public AuthenticatorRepresentation(final EngineIF engine)
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
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      boolean allowNull = false;
      String uriBase = null;
      String authenId = null;
      StructureIF structResponse = null;
      StructureIF structParamsPath = null;
      AuthenticatorIF authenticator = null;
      SessionType level = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);
      authenId = this.getStringValue(StructureIF.NAME_AUTHENTICATORID, structParamsPath, allowNull);

      structResponse = new BasicStructure(StructureIF.NAME_RESPONSE);
      structResponse.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      authenticator = this.getEngine().getAuthenticator(authenId);

      if (authenticator == null)
      {
         this.handleError(session, METHOD_NAME + "Authenticator is null.");
      }

      structResponse.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, authenId));
      structResponse.addChild(new BasicStructure(StructureIF.NAME_CATEGORY, authenticator.getCategory().toString()));
      structResponse.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, authenticator.getDescription()));
      structResponse.addChild(new BasicStructure(StructureIF.NAME_STATE, authenticator.getState().toString()));
      structResponse.addChild(new BasicStructure(StructureIF.NAME_STATUS, authenticator.getStatus()));
      structResponse.addChild(new BasicStructure(StructureIF.NAME_TYPE, authenticator.getType().toString()));

      level = authenticator.getLevel();

      if (level != null)
      {
         structResponse.addChild(new BasicStructure(StructureIF.NAME_LEVEL, level.toString()));
      }
      else
      {
         structResponse.addChild(new BasicStructure(StructureIF.NAME_CONTEXT));
      }

      /*
       * Properties
       */

      structResponse.addChild(this.getPropsAsStruct(authenticator));

      /*
       * Attributes
       */

      structResponse.addChild(this.getAttrsAsStruct(authenticator));

      structResponse.setState(State.SUCCESS);

      return structResponse;
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
      String[] authenIds = null;
      StructureIF structOut = null;
      StructureIF structAuthen = null;
      StructureIF structAuthens = null;
      AuthenticatorIF authenticator = null;

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      structAuthens = new BasicStructure(StructureIF.NAME_AUTHENTICATORS);
      structAuthens.setMultiValued(true);

      authenIds = this.getEngine().getAuthenticatorNames();
      len = authenIds.length;

      structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));

      for (int i = 0; i < len; i++)
      {
         id = authenIds[i];
         authenticator = this.getEngine().getAuthenticator(id);
         if (authenticator != null)
         {
            structAuthen = new BasicStructure(StructureIF.NAME_AUTHENTICATOR);

            structAuthen.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, id));

            if (uriBase.endsWith("/"))
            {
               uriChild = uriBase + id;
            }
            else
            {
               uriChild = uriBase + "/" + id;
            }

            structAuthen.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

            structAuthens.addValue(structAuthen);
         }
      }

      structOut.addChild(structAuthens);

      structOut.setState(State.SUCCESS);

      return structOut;
   }
}
