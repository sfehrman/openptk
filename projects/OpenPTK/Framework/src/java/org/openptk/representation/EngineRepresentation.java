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
import org.openptk.session.SessionIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public class EngineRepresentation extends Representation
//===================================================================
{
   private static final int HOURS_PER_DAY = 24;
   private static final int MINUTES_PER_HOUR = 60;
   private static final int SECONDS_PER_MINUTE = 60;
   private static final int MILLISECONDS_PER_SECOND = 1000;
   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public EngineRepresentation(final EngineIF engine)
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
            this.handleError(session, METHOD_NAME + "Unsupported Operation: '"
               + opcode.toString() + "'");
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doRead(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doRead() ";
      boolean allowNull = false;
      Long startTime = null;
      String uriActions = null;
      String uriAttrMaps = null;
      String uriAuthenticators = null;
      String uriBase = null;
      String uriClients = null;
      String uriContexts = null;
      String uriConverters = null;
      String uriDeciders = null;
      String uriEnforcers = null;
      String uriModels = null;
      String uriPlugins = null;
      String uriPolicies = null;
      String uriSessions = null;
      String uriStats = null;
      Date date = null;
      StructureIF structOut = null;
      StructureIF structResource = null;
      StructureIF structResources = null;

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      if (!uriBase.endsWith("/"))
      {
         uriBase += "/";
      }

      uriActions = uriBase + StructureIF.NAME_ACTIONS;
      uriAttrMaps = uriBase + StructureIF.NAME_ATTRMAPS;
      uriAuthenticators = uriBase + StructureIF.NAME_AUTHENTICATORS;
      uriClients = uriBase + StructureIF.NAME_CLIENTS;
      uriContexts = uriBase + StructureIF.NAME_CONTEXTS;
      uriConverters = uriBase + StructureIF.NAME_CONVERTERS;
      uriDeciders = uriBase + StructureIF.NAME_DECIDERS;
      uriEnforcers = uriBase + StructureIF.NAME_ENFORCERS;
      uriModels = uriBase + StructureIF.NAME_MODELS;
      uriPlugins = uriBase + StructureIF.NAME_PLUGINS;
      uriPolicies = uriBase + StructureIF.NAME_POLICIES;
      uriSessions = uriBase + StructureIF.NAME_SESSIONS;
      uriStats = uriBase + StructureIF.NAME_STATS;

      structResources = new BasicStructure(StructureIF.NAME_RESOURCES);

      // Actions

      structResource = new BasicStructure(StructureIF.NAME_ACTIONS);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Actions"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriActions));
      structResources.addChild(structResource);
      
      // AttrMaps

      structResource = new BasicStructure(StructureIF.NAME_ATTRMAPS);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Attribute Maps (External)"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriAttrMaps));
      structResources.addChild(structResource);

      // Authenticators

      structResource = new BasicStructure(StructureIF.NAME_AUTHENTICATORS);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Authenticators"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriAuthenticators));
      structResources.addChild(structResource);

      // Clients

      structResource = new BasicStructure(StructureIF.NAME_CLIENTS);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Clients"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriClients));
      structResources.addChild(structResource);

      // Contexts

      structResource = new BasicStructure(StructureIF.NAME_CONTEXTS);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Contexts"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriContexts));
      structResources.addChild(structResource);

      // Converters

      structResource = new BasicStructure(StructureIF.NAME_CONVERTERS);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Converters"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriConverters));
      structResources.addChild(structResource);

      // Deciders
      
      structResource = new BasicStructure(StructureIF.NAME_DECIDERS);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Deciders"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriDeciders));
      structResources.addChild(structResource);      
      
      // Enforcers
      
      structResource = new BasicStructure(StructureIF.NAME_ENFORCERS);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Enforcers"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriEnforcers));
      structResources.addChild(structResource);      
      
      // Models

      structResource = new BasicStructure(StructureIF.NAME_MODELS);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Models"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriModels));
      structResources.addChild(structResource);

      // Plugins

      structResource = new BasicStructure(StructureIF.NAME_PLUGINS);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Plugins"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriPlugins));
      structResources.addChild(structResource);

      // Policies
      
      structResource = new BasicStructure(StructureIF.NAME_POLICIES);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Policies"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriPolicies));
      structResources.addChild(structResource);      
      
      // Sessions

      structResource = new BasicStructure(StructureIF.NAME_SESSIONS);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Sessions"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriSessions));
      structResources.addChild(structResource);

      // Statistics

      structResource = new BasicStructure(StructureIF.NAME_STATS);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Statistics"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriStats));
      structResources.addChild(structResource);

      structOut.addChild(structResources);

      structOut.addChild(new BasicStructure(StructureIF.NAME_CATEGORY, this.getEngine().getCategory().toString()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, this.getEngine().getDescription()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, this.getEngine().getState().toString()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, this.getEngine().getStatus()));

      startTime = this.getEngine().getTimeStamp(SessionIF.TIMESTAMP_CREATED);

      if (startTime != null)
      {
         date = new Date(startTime);
         if (date != null)
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_CREATED, date.toString()));
            structOut.addChild(new BasicStructure("uptime", this.getUptime(startTime)));
         }
      }

      /*
       * Properties
       */

      structOut.addChild(this.getPropsAsStruct(this.getEngine()));

      structOut.setState(State.SUCCESS);

      return structOut;
   }

   //----------------------------------------------------------------
   private String getUptime(final Long startTime)
   //----------------------------------------------------------------
   {
      int msec = 0;
      int seconds = 0;
      int minutes = 0;
      int hours = 0;
      int days = 0;
      Long currentTime = 0L;
      Long upTime = 0L;
      String time = null;

      if (startTime != null)
      {
         currentTime = new Long(System.currentTimeMillis());
         upTime = currentTime - startTime;

         msec = (int) (upTime % MILLISECONDS_PER_SECOND);
         upTime /= MILLISECONDS_PER_SECOND;

         seconds = (int) (upTime % SECONDS_PER_MINUTE);
         upTime /= SECONDS_PER_MINUTE;

         minutes = (int) (upTime % MINUTES_PER_HOUR);
         upTime /= MINUTES_PER_HOUR;

         hours = (int) (upTime % HOURS_PER_DAY);
         days = (int) (upTime / HOURS_PER_DAY);

         time = days + " days, "
            + hours + " hours, "
            + minutes + " minutes, "
            + seconds + " seconds, "
            + msec + " msec";
      }

      if (time == null)
      {
         time = "not available";
      }

      return time;
   }
}
