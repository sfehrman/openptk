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
package org.openptk.context.actions;

import org.openptk.api.DataType;
import org.openptk.api.State;
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

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 *
 * This "Action" was originally designed to be used with "create"
 * Operations as a "pre" Action.  The Action will use the "request"
 * and check to see if the entry already exists.  If the entry does
 * exist, the Operation will be changed from CREATE to UPDATE
 */
//===================================================================
public class IfExistsThenUpdate extends Action
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public IfExistsThenUpdate()
   //----------------------------------------------------------------
   {
      super();
      this.setDescription("If Entry exists then Update");
      return;
   }

   /**
    * @param service
    * @param request
    * @throws ActionException
    */
   //----------------------------------------------------------------
   @Override
   public void preAction(final ServiceIF service, final RequestIF request) throws ActionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":preAction(): ";
      Object uniqueId = null;
      ComponentIF comp = null;
      ComponentIF subject = null;
      RequestIF readRequest = null;
      ResponseIF readResponse = null;
      State state = null;
      Operation operation = null;
      DataType type = null;

      /*
       * The Request MUST have a uniqueid.  Use the uniqueid to
       * attempt a "read" on the Context.
       * If the read is success ... then change the Request from a
       * CREATE to an UPDATE
       */

      if (_context.isDebug())
      {
         Logger.logInfo(METHOD_NAME + "BEGIN: Context="
            + _context.toString() + ": Operation="
            + request.getOperation().toString());
      }

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject is null");
      }

      uniqueId = subject.getUniqueId();
      if (uniqueId == null)
      {
         this.handleError(METHOD_NAME + "Subject has a null uniqueId");
      }
      type = subject.getUniqueIdType();

      comp = new Component();
      switch (type)
      {
         case STRING:
            comp.setUniqueId((String) uniqueId);
            break;
         case INTEGER:
            comp.setUniqueId((Integer) uniqueId);
            break;
         case LONG:
            comp.setUniqueId((Long) uniqueId);
            break;
         default:
            this.handleError(METHOD_NAME + "UniqueId is a '" + type.toString() + "' which is not supported");
            break;
      }

      if (_context.isDebug())
      {
         Logger.logInfo(METHOD_NAME + "READ Request:"
            + " uniqueid='" + (comp.getUniqueId() != null ? comp.getUniqueId().toString() : "(null)")
            + "', attrs=" + comp.getAttributesNames().toString());
      }

      readRequest = new Request();
      readRequest.setOperation(Operation.READ);
      readRequest.setKey(request.getKey());
      readRequest.setSubject(comp);
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

      if (_context.isDebug())
      {
         Logger.logInfo(METHOD_NAME + "READ Response:"
            + " state=" + readResponse.getState().toString()
            + ", status='" + readResponse.getStatus() + "'");
      }

      if (state == State.ERROR || state == State.FAILED)
      {
         this.handleError(METHOD_NAME + "Operation READ failed: uniqueId='"
            + uniqueId.toString() + "', serviceKey='" + request.getKey()
            + "', status='" + readResponse.getStatus() + "'");
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
         this.handleError(METHOD_NAME + "Operation READ failed uid='" + uniqueId.toString()
            + "', state='" + state.toString() + "'");
      }

      request.setOperation(operation);

      if (_context.isDebug())
      {
         Logger.logInfo(METHOD_NAME + "END: Context="
            + _context.toString() + ": Operation="
            + request.getOperation().toString());
      }

      return;
   }
}
