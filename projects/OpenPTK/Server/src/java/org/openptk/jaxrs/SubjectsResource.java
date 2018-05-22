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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.jaxrs;

import javax.servlet.ServletContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
//===================================================================
public class SubjectsResource extends Resource
//===================================================================
{
   private String CLASS_NAME = this.getClass().getSimpleName();

   /**
    *
    * @param ctx
    * @param uri
    * @param struct
    */
   //----------------------------------------------------------------
   public SubjectsResource(ServletContext ctx, UriInfo uri, StructureIF struct)
   //----------------------------------------------------------------
   {
      super(ctx, uri);
      String METHOD_NAME = CLASS_NAME + ":SubjectsResource(): ";
      String msg = null;

      if (struct == null)
      {
         msg = METHOD_NAME + "Structure is null.";
         throw new WebApplicationException(new Exception(msg), Response.Status.INTERNAL_SERVER_ERROR);
      }

      _type = RepresentationType.SUBJECT;
      _struct = struct;

      return;
   }

   /**
    * Sub-resource locator method for {id}.
    * @param subject
    * @return
    */
   @Path("/{" + StructureIF.NAME_SUBJECTID + "}")
   //----------------------------------------------------------------
   public SubjectResource getSubjectResource(@PathParam(StructureIF.NAME_SUBJECTID) String subject)
   //----------------------------------------------------------------
   {
      SubjectResource resource = null;

      resource = new SubjectResource(_ctx, _uri, _struct);

      return resource;
   }

   /**
    *
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
   public Response search(@Context HttpHeaders hdrs)
   //----------------------------------------------------------------
   {
      Response response = null;

      response = this.execute(Opcode.SEARCH, _type, hdrs);

      return response;
   }

   /**
    *
    * @param data
    * @param hdrs
    * @return
    */
   @POST
   @Consumes(
   {
      MediaType.APPLICATION_JSON,
      MediaType.APPLICATION_XML
   })
   //----------------------------------------------------------------
   public Response create(String data, @Context HttpHeaders hdrs)
   //----------------------------------------------------------------
   {
      Response response = null;

      try
      {
         _struct.addChild(new BasicStructure(StructureIF.NAME_TYPE, StructureType.STRUCTURE.toString()));
         _struct.addChild(new BasicStructure(StructureIF.NAME_DATA, data));
         _struct.addChild(new BasicStructure(StructureIF.NAME_LENGTH, data.length()));
      }
      catch (StructureException ex)
      {
         throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
      }

      response = this.execute(Opcode.CREATE, _type, hdrs);

      return response;
   }
}
