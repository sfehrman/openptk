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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.multipart.FormDataParam;

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
public class RelationshipResource extends Resource
//===================================================================
{

   private String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param ctx
    * @param uri
    * @param struct
    */
   //----------------------------------------------------------------
   public RelationshipResource(ServletContext ctx, UriInfo uri, StructureIF struct)
   //----------------------------------------------------------------
   {
      super(ctx, uri);
      String METHOD_NAME = CLASS_NAME + ":RelationshipResource(): ";
      String msg = null;

      if (struct == null)
      {
         msg = METHOD_NAME + "Request Structure is null.";
         throw new WebApplicationException(new Exception(msg), Response.Status.INTERNAL_SERVER_ERROR);
      }

      _type = RepresentationType.RELATIONSHIP;
      _struct = struct;

      return;
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
      String METHOD_NAME = CLASS_NAME + ":readStruct(): ";
      MediaType mtype = null;
      Response response = null;
      Response.ResponseBuilder builder = null;

      try
      {
         mtype = this.getMediaType(hdrs, ResourceIF.HTTP_HEADER_ACCEPT);
      }
      catch (Exception ex)
      {
         builder = Response.serverError();
         builder.entity(METHOD_NAME + ex.getMessage());
      }

      if (builder == null)
      {
         if (mtype == MediaType.TEXT_PLAIN_TYPE
                 || mtype == MediaType.TEXT_HTML_TYPE
                 || mtype == MediaType.APPLICATION_XML_TYPE
                 || mtype == MediaType.APPLICATION_JSON_TYPE)
         {
            try
            {
               _struct.addChild(new BasicStructure(StructureIF.NAME_TYPE, StructureType.STRUCTURE.toString()));
            }
            catch (StructureException ex)
            {
               builder = Response.serverError();
               builder.entity(METHOD_NAME + ex.getMessage());
            }
         }
         else
         {
            try
            {
               _struct.addChild(new BasicStructure(StructureIF.NAME_TYPE, StructureType.OBJECT.toString()));
            }
            catch (StructureException ex)
            {
               builder = Response.serverError();
               builder.entity(METHOD_NAME + ex.getMessage());
            }
         }
      }

      if (builder == null)
      {
         response = this.execute(Opcode.READ, _type, hdrs);
      }
      else
      {
         response = builder.build();
      }

      return response;
   }

   /**
    * @param data
    * @param hdrs
    * @return
    */
   @PUT
   @Consumes(
   {
      MediaType.TEXT_PLAIN,
      MediaType.TEXT_HTML,
      MediaType.APPLICATION_JSON,
      MediaType.APPLICATION_XML
   })
   //----------------------------------------------------------------
   public Response update(String data, @Context HttpHeaders hdrs)
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

      response = this.execute(Opcode.UPDATE, _type, hdrs);

      return response;
   }

   /**
    * @param data
    * @param hdrs
    * @return
    */
   @PUT
   @Consumes(
   {
      MediaType.APPLICATION_OCTET_STREAM
   })
   //----------------------------------------------------------------
   public Response update(byte[] data, @Context HttpHeaders hdrs)
   //----------------------------------------------------------------
   {
      Response response = null;

      try
      {
         _struct.addChild(new BasicStructure(StructureIF.NAME_TYPE, StructureType.OBJECT.toString()));
         _struct.addChild(new BasicStructure(StructureIF.NAME_DATA, data));
         _struct.addChild(new BasicStructure(StructureIF.NAME_LENGTH, data.length));
      }
      catch (StructureException ex)
      {
         throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
      }

      response = this.execute(Opcode.UPDATE, _type, hdrs);

      return response;
   }

   /**
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

   /**
    * @param data
    * @param hdrs
    * @return
    */
   @POST
   @Consumes(
   {
      MediaType.MULTIPART_FORM_DATA
   })
   //----------------------------------------------------------------
   public Response create(@FormDataParam(StructureIF.NAME_MEDIA) byte[] data, @Context HttpHeaders hdrs)
   //----------------------------------------------------------------
   {
      Response response = null;

      try
      {
         _struct.addChild(new BasicStructure(StructureIF.NAME_TYPE, StructureType.OBJECT.toString()));
         _struct.addChild(new BasicStructure(StructureIF.NAME_DATA, data));
         _struct.addChild(new BasicStructure(StructureIF.NAME_LENGTH, data.length));
      }
      catch (StructureException ex)
      {
         throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
      }

      response = this.execute(Opcode.CREATE, _type, hdrs);

      return response;
   }

   /**
    * @param hdrs
    * @return
    */
   @DELETE
   //----------------------------------------------------------------
   public Response delete(@Context HttpHeaders hdrs)
   //----------------------------------------------------------------
   {
      Response response = null;

      response = this.execute(Opcode.DELETE, _type, hdrs);
      return response;
   }
}
