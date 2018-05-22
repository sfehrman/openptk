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

import java.util.Date;

import org.openptk.api.Opcode;
import org.openptk.api.State;
import org.openptk.engine.EngineIF;
import org.openptk.session.CacheIF;
import org.openptk.session.SessionIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class CacheRepresentation extends Representation
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public CacheRepresentation(final EngineIF engine)
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
            throw new Exception(METHOD_NAME + ": Unsupported Operation: '"
               + opcode.toString() + "'");
         }
      }

      return structOut;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private StructureIF doRead(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      boolean allowNull = false;
      Long timestamp = null;
      String uriBase = null;
      String cacheId = null;
      String sessionId = null;
      Date date = null;
      CacheIF cache = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structCache = null;
      StructureIF structValue = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);
      sessionId = this.getStringValue(StructureIF.NAME_SESSIONID, structParamsPath, allowNull);
      cacheId = this.getStringValue(StructureIF.NAME_CACHEID, structParamsPath, allowNull);

      if (session == null)
      {
         this.handleError(session, METHOD_NAME + "Session is null.");
      }

      cache = session.getCache(cacheId);

      if (cache == null)
      {
         this.handleError(session, METHOD_NAME + "Cache is null.");
      }

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));
      structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, cacheId));
      structOut.addChild(new BasicStructure(StructureIF.NAME_CATEGORY, cache.getCategory().toString()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, cache.getDescription()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, cache.getState().toString()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, cache.getStatus()));

      timestamp = cache.getCreated();
      if (timestamp != null)
      {
         date = new Date(timestamp);
         if (date != null)
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_CREATED, date.toString()));
         }
      }

      timestamp = cache.getAccessed();
      if (timestamp != null)
      {
         date = new Date(timestamp);
         if (date != null)
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_ACCESSED, date.toString()));
         }
      }

      timestamp = cache.getUpdated();
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

      structOut.addChild(this.getPropsAsStruct(cache));

      /*
       * Attributes
       */

      structOut.addChild(this.getAttrsAsStruct(cache));

      /*
       * Cache's value
       */

      structValue = new BasicStructure(StructureIF.NAME_VALUE);

      structCache = (StructureIF) cache.getValue();
      if (structCache != null)
      {
         structValue.addChild(structCache);
      }

      structOut.addChild(structValue);

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
      String sessionId = null;
      String[] cacheIds = null;
      CacheIF cache = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structCache = null;
      StructureIF structCaches = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      try
      {
         uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + StructureIF.NAME_URI + ": " + ex.getMessage());
      }

      try
      {
         sessionId = this.getStringValue(StructureIF.NAME_SESSIONID, structParamsPath, allowNull);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + StructureIF.NAME_SESSIONID + ": " + ex.getMessage());
      }

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      structCaches = new BasicStructure(StructureIF.NAME_CACHES);
      structCaches.setMultiValued(true);

      if (session != null)
      {
         cacheIds = session.getCacheIds();

         if (cacheIds != null)
         {
            len = cacheIds.length;
         }

         structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));

         for (int i = 0; i < len; i++)
         {
            id = cacheIds[i];

            cache = session.getCache(id);

            if (cache != null)
            {
               structCache = new BasicStructure(StructureIF.NAME_CACHE);

               structCache.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, id));

               if (uriBase.endsWith("/"))
               {
                  uriChild = uriBase + id;
               }
               else
               {
                  uriChild = uriBase + "/" + id;
               }

               structCache.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

               structCaches.addValue(structCache);
            }
         }
      }
      else
      {
         this.handleError(session, METHOD_NAME + "Session '" + sessionId + "' is null.");
      }

      structOut.addChild(structCaches);

      structOut.setState(State.SUCCESS);

      return structOut;
   }
}
