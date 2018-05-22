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
import org.openptk.client.ClientIF;
import org.openptk.engine.EngineIF;
import org.openptk.session.SessionIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public class ClientRepresentation extends Representation
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public ClientRepresentation(final EngineIF engine)
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
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      boolean allowNull = false;
      boolean isEngine = false;
      String uriBase = null;
      String clientId = null;
      String[] authenNames = null;
      String[] contextNames = null;
      ClientIF client = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structAuthens = null;
      StructureIF structContexts = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);
      clientId = this.getStringValue(StructureIF.NAME_CLIENTID, structParamsPath, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      client = this.getEngine().getClient(clientId);

      if (client == null)
      {
         this.handleError(session, METHOD_NAME + "Client is null.");
      }

      if (uriBase.indexOf(StructureIF.NAME_ENGINE) >= 0)
      {
         isEngine = true;
      }

      structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, clientId));

      if (isEngine)
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, client.getDescription()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, client.getState().toString()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, client.getStatus()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_CATEGORY, client.getCategory().toString()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_SECRET, client.getSecret()));

         /*
          * Authenticators
          */

         structAuthens = new BasicStructure(StructureIF.NAME_AUTHENTICATORS);

         authenNames = client.getAuthenticatorIds();
         for (int i = 0; i < authenNames.length; i++)
         {
            structAuthens.addValue(authenNames[i]);
         }

         structOut.addChild(structAuthens);
      }

      /*
       * Contexts
       */

      structContexts = new BasicStructure(StructureIF.NAME_CONTEXTS);

      contextNames = client.getContextIds();
      for (int i = 0; i < contextNames.length; i++)
      {
         structContexts.addValue(contextNames[i]);
      }

      structOut.addChild(structContexts);
      structOut.addChild(new BasicStructure(StructureIF.NAME_DEFAULT, client.getDefaultContextId()));

      /*
       * Properties
       */

      structOut.addChild(this.getPropsAsStruct(client));

      /*
       * Attributes
       */

      structOut.addChild(this.getAttrsAsStruct(client));

      structOut.setState(State.SUCCESS);

      return structOut;
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
      String[] clientIds = null;
      ClientIF client = null;
      StructureIF structOut = null;
      StructureIF structClient = null;
      StructureIF structClients = null;

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      structClients = new BasicStructure(StructureIF.NAME_CLIENTS);
      structClients.setMultiValued(true);

      clientIds = this.getEngine().getClientNames();
      len = clientIds.length;

      structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));

      for (int i = 0; i < len; i++)
      {
         id = clientIds[i];
         client = this.getEngine().getClient(id);
         if (client != null)
         {
            structClient = new BasicStructure(StructureIF.NAME_CLIENT);

            structClient.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, id));

            if (uriBase.endsWith("/"))
            {
               uriChild = uriBase + id;
            }
            else
            {
               uriChild = uriBase + "/" + id;
            }

            structClient.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

            structClients.addValue(structClient);
         }
      }

      structOut.addChild(structClients);

      structOut.setState(State.SUCCESS);

      return structOut;
   }
}
