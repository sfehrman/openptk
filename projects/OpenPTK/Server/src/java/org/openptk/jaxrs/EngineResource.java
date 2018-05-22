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
package org.openptk.jaxrs;

import javax.servlet.ServletContext;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.openptk.api.Opcode;
import org.openptk.exception.StructureException;
import org.openptk.representation.RepresentationType;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;
import org.openptk.structure.StructureType;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
@Path("/" + StructureIF.NAME_ENGINE)
//===================================================================
public class EngineResource extends Resource
//===================================================================
{
   /**
    * @param ctx
    * @param uri
    */
   //----------------------------------------------------------------
   public EngineResource(@Context ServletContext ctx, @Context UriInfo uri)
   //----------------------------------------------------------------
   {
      super(ctx, uri);

      _type = RepresentationType.ENGINE;
      _struct = new BasicStructure(StructureIF.NAME_REQUEST);

      return;
   }

   /**
    *
    * @return
    */
   @Path("/" + StructureIF.NAME_ATTRMAPS)
   //----------------------------------------------------------------
   public AttrMapsResource getAttrMaps()
   //----------------------------------------------------------------
   {
      AttrMapsResource resource = null;

      resource = new AttrMapsResource(_ctx, _uri, _struct);

      return resource;
   }

   /**
    *
    * @return
    */
   @Path("/" + StructureIF.NAME_AUTHENTICATORS)
   //----------------------------------------------------------------
   public AuthenticatorsResource getAuthenticators()
   //----------------------------------------------------------------
   {
      AuthenticatorsResource resource = null;

      resource = new AuthenticatorsResource(_ctx, _uri, _struct);

      return resource;
   }

   /**
    *
    * @return
    */
   @Path("/" + StructureIF.NAME_CLIENTS)
   //----------------------------------------------------------------
   public ClientsResource getClients()
   //----------------------------------------------------------------
   {
      ClientsResource resource = null;

      resource = new ClientsResource(_ctx, _uri, _struct);

      return resource;
   }

   /**
    *
    * @return
    */
   @Path("/" + StructureIF.NAME_CONVERTERS)
   //----------------------------------------------------------------
   public ConvertersResource getConverters()
   //----------------------------------------------------------------
   {
      ConvertersResource resource = null;

      resource = new ConvertersResource(_ctx, _uri, _struct);

      return resource;
   }

   /**
    *
    * @return
    */
   @Path("/" + StructureIF.NAME_DECIDERS)
   //----------------------------------------------------------------
   public DecidersResource getDecicers()
   //----------------------------------------------------------------
   {
      DecidersResource resource = null;

      resource = new DecidersResource(_ctx, _uri, _struct);

      return resource;
   }

   /**
    *
    * @return
    */
   @Path("/" + StructureIF.NAME_ENFORCERS)
   //----------------------------------------------------------------
   public EnforcersResource getEnforcers()
   //----------------------------------------------------------------
   {
      EnforcersResource resource = null;

      resource = new EnforcersResource(_ctx, _uri, _struct);

      return resource;
   }

   /**
    *
    * @return
    */
   @Path("/" + StructureIF.NAME_MODELS)
   //----------------------------------------------------------------
   public ModelsResource getModels()
   //----------------------------------------------------------------
   {
      ModelsResource resource = null;

      resource = new ModelsResource(_ctx, _uri, _struct);

      return resource;
   }

   /**
    *
    * @return
    */
   @Path("/" + StructureIF.NAME_POLICIES)
   //----------------------------------------------------------------
   public PoliciesResource getPolicies()
   //----------------------------------------------------------------
   {
      PoliciesResource resource = null;

      resource = new PoliciesResource(_ctx, _uri, _struct);

      return resource;
   }

   /**
    *
    * @return
    */
   @Path("/" + StructureIF.NAME_SESSIONS)
   //----------------------------------------------------------------
   public SessionsResource getSessions()
   //----------------------------------------------------------------
   {
      SessionsResource resource = null;

      resource = new SessionsResource(_ctx, _uri, _struct);

      return resource;
   }

   /**
    *
    * @return
    */
   @Path("/" + StructureIF.NAME_STATS)
   //----------------------------------------------------------------
   public StatsResource getStats()
   //----------------------------------------------------------------
   {
      StatsResource resource = null;

      resource = new StatsResource(_ctx, _uri, _struct);

      return resource;
   }

   /**
    *
    * @return
    */
   @Path("/" + StructureIF.NAME_CONTEXTS)
   //----------------------------------------------------------------
   public ContextsResource getContexts()
   //----------------------------------------------------------------
   {
      ContextsResource resource = null;

      resource = new ContextsResource(_ctx, _uri, _struct);

      return resource;
   }

   /**
    * 
    * @return
    */
   //----------------------------------------------------------------
   @Path("/" + StructureIF.NAME_PLUGINS)
   //----------------------------------------------------------------
   public PluginsResource getPlugins()
   {
      PluginsResource resource = null;

      resource = new PluginsResource(_ctx, _uri, _struct);

      return resource;
   }

   /**
    *
    * @return
    */
   //----------------------------------------------------------------
   @Path("/" + StructureIF.NAME_ACTIONS)
   //----------------------------------------------------------------
   public ActionsResource getActions()
   {
      ActionsResource resource = null;

      resource = new ActionsResource(_ctx, _uri, _struct);

      return resource;
   }

   /**
    * @param hdrs
    * @return
    */
   @GET
   @Produces(
   {
      MediaType.TEXT_PLAIN,
      MediaType.TEXT_HTML,
      MediaType.APPLICATION_JSON,
      MediaType.APPLICATION_XML
   })
   //----------------------------------------------------------------
   public Response read(@Context HttpHeaders hdrs)
   //----------------------------------------------------------------
   {
      Response response = null;

      try
      {
         _struct.addChild(new BasicStructure(StructureIF.NAME_TYPE, StructureType.STRUCTURE.toString()));
      }
      catch (StructureException ex)
      {
         throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
      }

      response = this.execute(Opcode.READ, _type, hdrs);

      return response;
   }
}
