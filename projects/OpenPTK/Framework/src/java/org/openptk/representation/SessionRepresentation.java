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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.representation;

import java.util.Date;

import org.openptk.api.Opcode;
import org.openptk.api.State;
import org.openptk.authenticate.PrincipalIF;
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
public class SessionRepresentation extends Representation
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public SessionRepresentation(final EngineIF engine)
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
      String METHOD_NAME = CLASS_NAME + ":execute()";
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
      Long timestamp = null;
      String id = null;
      String uriBase = null;
      String sessionId = null;
      String uriCaches = null;
      Object val = null;
      Date date = null;
      SessionIF sessionEngine = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structResources = null;
      StructureIF structResource = null;
      StructureIF structPrincipal = null;
      PrincipalIF principal = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(sessionUser, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      sessionId = this.getStringValue(StructureIF.NAME_SESSIONID, structParamsPath, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      sessionEngine = this.getEngine().getSession(sessionId); // Note: session is a "copy"

      if (sessionEngine == null)
      {
         this.handleError(sessionUser, METHOD_NAME + ": engineSession is null.");
      }

      structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, sessionId));
      structOut.addChild(new BasicStructure(StructureIF.NAME_TYPE, sessionEngine.getType().toString()));

      /*
       * Get the Principal
       */

      principal = sessionEngine.getPrincipal();
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
            structPrincipal.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID));
         }

         val = principal.getContextId();
         if (val != null)
         {
            structPrincipal.addChild(new BasicStructure(StructureIF.NAME_CONTEXTID, val.toString()));
         }
         else
         {
            structPrincipal.addChild(new BasicStructure(StructureIF.NAME_CONTEXTID));
         }
      }
      else
      {
         structPrincipal.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID));
         structPrincipal.addChild(new BasicStructure(StructureIF.NAME_CONTEXTID));
      }

      structOut.addChild(structPrincipal);

      structOut.addChild(new BasicStructure(StructureIF.NAME_CATEGORY, sessionEngine.getCategory().toString()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, sessionEngine.getDescription()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, sessionEngine.getState().toString()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, sessionEngine.getStatus()));

      /*
       * Get the ClientId
       */

      structOut.addChild(new BasicStructure(StructureIF.NAME_CLIENTID, sessionEngine.getClientId()));


      /*
       * Timestamps
       */

      timestamp = sessionEngine.getCreated();
      if (timestamp != null)
      {
         date = new Date(timestamp);
         if (date != null)
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_CREATED, date.toString()));
         }
      }

      timestamp = sessionEngine.getAccessed();
      if (timestamp != null)
      {
         date = new Date(timestamp);
         if (date != null)
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_ACCESSED, date.toString()));
         }
      }

      timestamp = sessionEngine.getUpdated();
      if (timestamp != null)
      {
         date = new Date(timestamp);
         if (date != null)
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_UPDATED, date.toString()));
         }
      }

      /*
       * Properties
       */

      structOut.addChild(this.getPropsAsStruct(sessionEngine));

      /*
       * Attributes
       */

      structOut.addChild(this.getAttrsAsStruct(sessionEngine));

      /*
       * Resources
       */

      structResources = new BasicStructure(StructureIF.NAME_RESOURCES);

      id = StructureIF.NAME_CACHES;
      if (uriBase.endsWith("/"))
      {
         uriCaches = uriBase + id;
      }
      else
      {
         uriCaches = uriBase + "/" + id;
      }
      structResource = new BasicStructure(StructureIF.NAME_CACHES);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Caches managed by this Session"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriCaches));
      structResources.addChild(structResource);

      structOut.addChild(structResources);

      structOut.setState(State.SUCCESS);

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doSearch(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      boolean allowNull = false;
      int len = 0;
      String METHOD_NAME = CLASS_NAME + ":doSearch()";
      String uriBase = null;
      String uriChild = null;
      String id = null;
      String[] sessionIds = null;
      SessionIF sessionItem = null;
      StructureIF structOut = null;
      StructureIF structSession = null;
      StructureIF structSessions = null;

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      structSessions = new BasicStructure(StructureIF.NAME_SESSIONS);
      structSessions.setMultiValued(true);

      sessionIds = this.getEngine().getSessionIds();
      len = sessionIds.length;

      structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));

      for (int i = 0; i < len; i++)
      {
         id = sessionIds[i];
         sessionItem = this.getEngine().getSession(id); // Note: session is a "copy"
         if (sessionItem != null)
         {
            structSession = new BasicStructure(StructureIF.NAME_SESSION);

            structSession.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, id));

            if (uriBase.endsWith("/"))
            {
               uriChild = uriBase + id;
            }
            else
            {
               uriChild = uriBase + "/" + id;
            }

            structSession.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

            structSession.addChild(new BasicStructure(StructureIF.NAME_TYPE, sessionItem.getType().toString()));

            structSession.addChild(new BasicStructure(StructureIF.NAME_CLIENTID, sessionItem.getClientId()));

            structSessions.addValue(structSession);
         }
      }

      structOut.addChild(structSessions);

      structOut.setState(State.SUCCESS);

      return structOut;
   }
}
