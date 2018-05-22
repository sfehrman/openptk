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
import javax.ws.rs.PUT;
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

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class PasswordResource extends Resource
//===================================================================
{

   private String CLASS_NAME = this.getClass().getSimpleName();


   /**
    * @param ctx
    * @param uri
    * @param struct
    */
   //----------------------------------------------------------------
   public PasswordResource(ServletContext ctx, UriInfo uri, StructureIF struct)
   //----------------------------------------------------------------
   {
      super(ctx, uri);
      String METHOD_NAME = CLASS_NAME + ":PasswordResource(): ";
      String msg = null;

      if (struct == null)
      {
         msg = METHOD_NAME + "Request Structure is null.";
         throw new WebApplicationException(new Exception(msg), Response.Status.INTERNAL_SERVER_ERROR);
      }

      _type = RepresentationType.SUBJECT;
      _struct = struct;

      return;
   }

   /**
    * @param hdrs
    * @return
    */
   @Path("/forgot/questions")
   @GET
   @Produces(
   {
      MediaType.TEXT_PLAIN,
      MediaType.TEXT_HTML,
      MediaType.APPLICATION_JSON,
      MediaType.APPLICATION_XML
   })
   //----------------------------------------------------------------
   public Response forgotQuestions(@Context HttpHeaders hdrs)
   //----------------------------------------------------------------
   {
      Response response = null;

      try
      {
         _struct.addChild(new BasicStructure(StructureIF.NAME_MODE, StructureIF.NAME_QUESTIONS));
      }
      catch (StructureException ex)
      {
         throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
      }

      response = this.execute(Opcode.PWDFORGOT, _type, hdrs);

      return response;
   }

   /**
    * @param data
    * @param hdrs
    * @return
    */
   @Path("/forgot/answers")
   @PUT
   @Consumes(
   {
      MediaType.APPLICATION_JSON,
      MediaType.APPLICATION_XML
   })
   //----------------------------------------------------------------
   public Response forgotAnswers(String data, @Context HttpHeaders hdrs)
   //----------------------------------------------------------------
   {
      Response response = null;

      try
      {
         _struct.addChild(new BasicStructure(StructureIF.NAME_MODE, StructureIF.NAME_ANSWERS));
         _struct.addChild(new BasicStructure(StructureIF.NAME_DATA, data));
      }
      catch (StructureException ex)
      {
         throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
      }

      response = this.execute(Opcode.PWDFORGOT, _type, hdrs);

      return response;
   }

   /**
    * @param data
    * @param hdrs
    * @return
    */
   @Path("/forgot/change")
   @PUT
   @Consumes(
   {
      MediaType.APPLICATION_JSON,
      MediaType.APPLICATION_XML
   })
   //----------------------------------------------------------------
   public Response forgotChange(String data, @Context HttpHeaders hdrs)
   //----------------------------------------------------------------
   {
      Response response = null;

      try
      {
         _struct.addChild(new BasicStructure(StructureIF.NAME_MODE, StructureIF.NAME_CHANGE));
         _struct.addChild(new BasicStructure(StructureIF.NAME_DATA, data));
      }
      catch (StructureException ex)
      {
         throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
      }

      response = this.execute(Opcode.PWDFORGOT, _type, hdrs);

      return response;
   }

   /**
    * @param hdrs
    * @return
    */
   @Path("/reset")
   @GET
   @Produces(
   {
      MediaType.TEXT_PLAIN,
      MediaType.TEXT_HTML,
      MediaType.APPLICATION_JSON,
      MediaType.APPLICATION_XML
   })
   //----------------------------------------------------------------
   public Response reset(@Context HttpHeaders hdrs)
   //----------------------------------------------------------------
   {
      Response response = null;

      response = this.execute(Opcode.PWDRESET, _type, hdrs);

      return response;
   }

   /**
    * @param data
    * @param hdrs
    * @return
    */
   @Path("/change")
   @PUT
   @Consumes(
   {
      MediaType.APPLICATION_JSON,
      MediaType.APPLICATION_XML
   })
   @Produces(
   {
      MediaType.APPLICATION_JSON,
      MediaType.APPLICATION_XML
   })
   //----------------------------------------------------------------
   public Response change(String data, @Context HttpHeaders hdrs)
   //----------------------------------------------------------------
   {
      Response response = null;

      try
      {
         _struct.addChild(new BasicStructure(StructureIF.NAME_DATA, data));
      }
      catch (StructureException ex)
      {
         throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
      }

      response = this.execute(Opcode.PWDCHANGE, _type, hdrs);

      return response;
   }
}
