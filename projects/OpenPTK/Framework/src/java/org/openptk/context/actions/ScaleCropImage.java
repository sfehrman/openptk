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
package org.openptk.context.actions;

import java.util.Date;

import org.openptk.api.State;
import org.openptk.common.AttrIF;
import org.openptk.common.BasicAttr;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.Request;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.exception.ActionException;
import org.openptk.exception.ServiceException;
import org.openptk.logging.Logger;
import org.openptk.spi.ServiceIF;
import org.openptk.util.Digest;
import org.openptk.util.Media;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 *
 * This "Action" is used to create a Thumbnail image from a larger
 * image.  It originally was created as a "postAction" which was called
 * after a Create or Update Operation
 *
 */
//===================================================================
public class ScaleCropImage extends Action
//===================================================================
{
   private int _sizeWidth = 0;
   private int _sizeHeight = 0;
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _attrNameType = null;
   private String _attrNameData = null;
   private String _attrNameDigest = null;
   private String _attrNameLength = null;
   private String _attrNameModified = null;
   private String _attrNameContext = null;
   private String _attrNameSubject = null;
   private String _attrNameRelationship = null;
   private String _uniqueId = null;
   private static final String MEDIA_TYPE = "image";
   private static final String SUB_TYPE = "png";
   private static final String PROP_ATTRIBUTE_TYPE = "attribute.type";
   private static final String PROP_ATTRIBUTE_DIGEST = "attribute.digest";
   private static final String PROP_ATTRIBUTE_DATA = "attribute.data";
   private static final String PROP_ATTRIBUTE_LENGTH = "attribute.length";
   private static final String PROP_ATTRIBUTE_MODIFIED = "attribute.modified";
   private static final String PROP_ATTRIBUTE_CONTEXT = "attribute.context";
   private static final String PROP_ATTRIBUTE_SUBJECT = "attribute.subject";
   private static final String PROP_ATTRIBUTE_RELATIONSHIP = "attribute.relationship";
   private static final String PROP_SIZE_WIDTH = "size.width";
   private static final String PROP_SIZE_HEIGHT = "size.height";
   private static final String PROP_UNIQUEID = "uniqueid";

   //----------------------------------------------------------------
   public ScaleCropImage()
   //----------------------------------------------------------------
   {
      super();
      this.setDescription("Scale and Crop image, then store it");
      return;
   }


   /**
    * @throws ActionException
    */
   //----------------------------------------------------------------
   @Override
   public void startup() throws ActionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":startup(): ";
      String prop = null;

      _uniqueId = this.getPropValue(PROP_UNIQUEID);
      _attrNameData = this.getPropValue(PROP_ATTRIBUTE_DATA);
      _attrNameDigest = this.getPropValue(PROP_ATTRIBUTE_DIGEST);
      _attrNameType = this.getPropValue(PROP_ATTRIBUTE_TYPE);
      _attrNameLength = this.getPropValue(PROP_ATTRIBUTE_LENGTH);
      _attrNameModified = this.getPropValue(PROP_ATTRIBUTE_MODIFIED);
      _attrNameContext = this.getPropValue(PROP_ATTRIBUTE_CONTEXT);
      _attrNameSubject = this.getPropValue(PROP_ATTRIBUTE_SUBJECT);
      _attrNameRelationship = this.getPropValue(PROP_ATTRIBUTE_RELATIONSHIP);

      try
      {
         _sizeWidth = Integer.parseInt(this.getPropValue(PROP_SIZE_WIDTH));
      }
      catch (NumberFormatException ex)
      {
         this.handleError(METHOD_NAME + "Property '" + PROP_SIZE_WIDTH +
            "' must be an integer, value='" + prop + "'");
      }
      if (_sizeWidth < 10)
      {
         this.handleError(METHOD_NAME + "Property '" + PROP_SIZE_WIDTH +
            "' must be at least 10");
      }

      try
      {
         _sizeHeight = Integer.parseInt(this.getPropValue(PROP_SIZE_HEIGHT));
      }
      catch (NumberFormatException ex)
      {
         this.handleError(METHOD_NAME + "Property '" + PROP_SIZE_HEIGHT +
            "' must be an integer, value='" + prop + "'");
      }
      if (_sizeHeight < 10)
      {
         this.handleError(METHOD_NAME + "Property '" + PROP_SIZE_HEIGHT +
            "' must be at least 10");
      }

      this.setState(State.READY);

      return;
   }


   /**
    * @param service
    * @param response
    * @throws ActionException
    */
   //----------------------------------------------------------------
   @Override
   public void postAction(final ServiceIF service, final ResponseIF response) throws ActionException
   //----------------------------------------------------------------
   {
      byte[] bytes = null;
      String METHOD_NAME = CLASS_NAME + ":postAction(): ";
      RequestIF request = null;
      ComponentIF comp = null;

      if (_context.isDebug())
      {
         Logger.logInfo(METHOD_NAME + "BEGIN: Context=" +
            _context.toString() + ": Operation=" +
            response.getRequest().getOperation().toString());
      }

      request = response.getRequest();
      comp = request.getSubject();

      if (comp == null)
      {
         this.handleError(METHOD_NAME + "Request component is null");
      }

      bytes = this.generateImage(comp);

      if (bytes == null || bytes.length < 1)
      {
         this.handleError(METHOD_NAME + "byte[] is null");
      }

      this.storeImage(service, response, bytes);

      if (_context.isDebug())
      {
         Logger.logInfo(METHOD_NAME + "END: Context=" +
            _context.toString() + ": Operation=" +
            response.getRequest().getOperation().toString());
      }

      return;
   }

   //----------------------------------------------------------------
   private byte[] generateImage(final ComponentIF comp) throws ActionException
   //----------------------------------------------------------------
   {
      byte[] bytesOriginal = null;
      byte[] bytesModified = null;
      String METHOD_NAME = CLASS_NAME + ":generateImage(): ";
      Object obj = null;
      AttrIF attr = null;

      /*
       * get the data
       */

      attr = comp.getAttribute(_attrNameData);
      if (attr == null)
      {
         this.handleError(METHOD_NAME + "Attribute '" + _attrNameData +
            "' is null");
      }

      obj = attr.getValue();
      if (obj == null)
      {
         this.handleError(METHOD_NAME + "Attribute '" + _attrNameData +
            "' has a null value");
      }

      if (obj instanceof byte[])
      {
         bytesOriginal = (byte[]) obj;
         
         if (_context.isDebug())
         {
            Logger.logInfo(METHOD_NAME + "Original byte[] size = " +
               bytesOriginal.length);
         }

         try
         {
            bytesModified = Media.scaleCropImage(bytesOriginal, _sizeWidth, _sizeHeight, SUB_TYPE);
         }
         catch (Exception ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }

         if (_context.isDebug())
         {
            Logger.logInfo(METHOD_NAME + "Modified byte[] size = " +
               bytesModified.length);
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "Attribute '" + _attrNameData +
            "' must be an instanceof byte[]");
      }

      return bytesModified;
   }

   //----------------------------------------------------------------
   private void storeImage(final ServiceIF service, final ResponseIF response, final byte[] bytesImage) throws ActionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":storeImage(): ";
      String uniqueId = null;
      String name = null;
      String key = null;
      ComponentIF requestComp = null;
      ComponentIF readComp = null;
      ComponentIF writeComp = null;
      RequestIF readRequest = null;
      RequestIF writeRequest = null;
      RequestIF request = null;
      ResponseIF readResponse = null;
      ResponseIF writeResponse = null;
      AttrIF attr = null;
      State state = null;
      Operation operation = null;

      request = response.getRequest();
      requestComp = request.getSubject();

      if (requestComp == null)
      {
         this.handleError(METHOD_NAME + "Request component is null");
      }

      uniqueId = this.deriveUniqueId(_uniqueId, requestComp);
      if (uniqueId == null || uniqueId.length() < 1)
      {
         this.handleError(METHOD_NAME + "Subject has a null uniqueId");
      }

      key = request.getKey();
      if (key == null || key.length() < 1)
      {
         this.handleError(METHOD_NAME + "Request has a null Service Key");
      }

      readComp = new Component();
      readComp.setUniqueId(uniqueId);

      /*
       * READ to see if the image exists
       */

      readRequest = new Request();
      readRequest.setOperation(Operation.READ);
      readRequest.setKey(request.getKey());
      readRequest.setSubject(readComp);
      readRequest.setProperties(service.getOperProps(Operation.READ));
      readRequest.setService(service);

      try
      {
         readResponse = service.execute(readRequest);
      }
      catch (ServiceException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      state = readResponse.getState();

      if (state == State.ERROR || state == State.FAILED)
      {
         this.handleError(METHOD_NAME + "Operation READ failed: uniqueId='" +
            uniqueId + "', serviceKey='" + request.getKey() +
            "', status='" + readResponse.getStatus() + "'");
      }
      else if (state == State.SUCCESS)
      {
         operation = Operation.UPDATE;
      }
      else if (state == State.NOTEXIST)
      {
         operation = Operation.CREATE;
      }
      else
      {
         this.handleError(METHOD_NAME + "Operation READ failed uid=" + uniqueId +
            "', state='" + state.toString() + "'");
      }

      writeComp = new Component();
      writeComp.setUniqueId(uniqueId);

      name = _attrNameContext;
      attr = requestComp.getAttribute(name);
      if (attr == null)
      {
         this.handleError(METHOD_NAME + "Request has missing attribute '" + _attrNameContext + "'");
      }
      writeComp.setAttribute(name, attr);

      name = _attrNameSubject;
      attr = requestComp.getAttribute(name);
      if (attr == null)
      {
         this.handleError(METHOD_NAME + "Request has missing attribute '" + _attrNameSubject + "'");
      }
      writeComp.setAttribute(name, attr);

//      name = _attrNameRelationship;
//      attr = new BasicAttr(name, "thumbnail");
//      attr.setServiceName(service.getSrvcName(operation, name));
//      writeComp.setAttribute(name, attr);

      name = _attrNameData;
      attr = new BasicAttr(name, bytesImage);
      attr.setServiceName(service.getSrvcName(operation, name));
      writeComp.setAttribute(name, attr);

      name = _attrNameLength;
      attr = new BasicAttr(name, bytesImage.length);
      attr.setServiceName(service.getSrvcName(operation, name));
      writeComp.setAttribute(name, attr);

      name = _attrNameDigest;
      attr = new BasicAttr(name, Digest.generate(bytesImage));
      attr.setServiceName(service.getSrvcName(operation, name));
      writeComp.setAttribute(name, attr);

      name = _attrNameType;
      attr = new BasicAttr(name, MEDIA_TYPE + "/" + SUB_TYPE);
      attr.setServiceName(service.getSrvcName(operation, name));
      writeComp.setAttribute(name, attr);

      name = _attrNameModified;
      attr = new BasicAttr(name, (new Date(new Long(System.currentTimeMillis()))).toString());
      attr.setServiceName(service.getSrvcName(operation, name));
      writeComp.setAttribute(name, attr);

      writeRequest = new Request();
      writeRequest.setOperation(operation);
      writeRequest.setKey(request.getKey());
      writeRequest.setSubject(writeComp);
      writeRequest.setProperties(service.getOperProps(operation));
      writeRequest.setService(service);

      /*
       * Now CREATE or UPDATE it
       */

      try
      {
         writeResponse = service.execute(writeRequest);
      }
      catch (ServiceException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      state = writeResponse.getState();
      if (state == State.ERROR || state == State.FAILED)
      {
         this.handleError(METHOD_NAME + "Image " + operation.toString() +
            " error: " + writeResponse.getStatus());
      }

      return;
   }
}
