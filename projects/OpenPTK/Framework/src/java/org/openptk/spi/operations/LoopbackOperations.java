/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2008-2010 Sun Microsystems, Inc.
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
package org.openptk.spi.operations;

import java.util.LinkedList;
import java.util.List;

import org.openptk.api.State;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.exception.OperationException;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public class LoopbackOperations extends Operations implements OperationsIF
//===================================================================
{

   private static final String DESCIPTION = "Loopback Provision Response";

   //----------------------------------------------------------------
   public LoopbackOperations()
   //----------------------------------------------------------------
   {
      super();
      this.init();
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void startup()
   //----------------------------------------------------------------
   {
      this.setState(State.READY);
      this.setDescription("OpenPTK Loopback Opertions");
      return;
   }


   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   public void execute(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      Operation oper = null;
      oper = request.getOperation();
      
      switch (oper)
      {
         case CREATE:
            this.doCreate(request, response);
            break;
         case READ:
            this.doRead(request, response);
            break;
         case UPDATE:
            this.doUpdate(request, response);
            break;
         case DELETE:
            this.doDelete(request, response);
            break;
         case SEARCH:
            this.doSearch(request, response);
            break;
         case PWDCHANGE:
            this.doPasswordChange(request, response);
            break;
         case PWDRESET:
            this.doPasswordReset(request, response);
            break;
         default:
            throw new OperationException("Unimplemented Operation: " + oper.toString());
      }
      return;
   }
   
   //
   // ===========================
   // ===== PRIVATE METHODS =====
   // ===========================
   //
   //----------------------------------------------------------------
   private void doCreate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      response.setDescription(LoopbackOperations.DESCIPTION + ": Create");
      response.setUniqueId(request.getSubject());

      return;
   }
   
   //----------------------------------------------------------------
   private void doRead(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      List<ComponentIF> result = null;
      Component comp = null;

      comp = new Component();
      comp.setUniqueId(request.getSubject());
      comp.setAttributes(request.getSubject().getAttributes());
      comp.setDebug(this.isDebug());
      comp.setDebugLevel(this.getDebugLevel());

      result = new LinkedList<ComponentIF>();
      result.add(comp);

      response.setDescription(LoopbackOperations.DESCIPTION + ": Read");
      response.setUniqueId(request.getSubject());
      response.setResults(result);

      return;
   }
   //----------------------------------------------------------------
   private void doSearch(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      List<ComponentIF> result = null;
      Component comp = null;

      comp = new Component();
      comp.setUniqueId(request.getSubject());
      comp.setAttributes(request.getSubject().getAttributes());
      comp.setDebug(this.isDebug());
      comp.setDebugLevel(this.getDebugLevel());

      result = new LinkedList<ComponentIF>();
      result.add(comp);

      response.setDescription(LoopbackOperations.DESCIPTION + ": Search");
      response.setUniqueId(request.getSubject());
      response.setResults(result);

      return;
   }
   //----------------------------------------------------------------
   private void doUpdate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      response.setDescription(LoopbackOperations.DESCIPTION + ": Update");
      response.setUniqueId(request.getSubject());

      return;
   }
   //----------------------------------------------------------------
   private void doDelete(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      response.setDescription(LoopbackOperations.DESCIPTION + ": Delete");
      response.setUniqueId(request.getSubject());

      return;
   }
   //----------------------------------------------------------------
   private void doPasswordChange(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      response.setDescription(LoopbackOperations.DESCIPTION + ": Password Change");
      response.setUniqueId(request.getSubject());

      return;
   }
   //----------------------------------------------------------------
   private void doPasswordReset(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      response.setDescription(LoopbackOperations.DESCIPTION + ": Password Reset");
      response.setUniqueId(request.getSubject());

      return;
   }
   
   //----------------------------------------------------------------
   private void init()
   //----------------------------------------------------------------
   {
      this.setType(OperationsType.LOOPBACK);
      /*
       * Specify which operations are implemented
       */
      this.setImplemented(Operation.CREATE, true);
      this.setImplemented(Operation.READ, true);
      this.setImplemented(Operation.UPDATE, true);
      this.setImplemented(Operation.DELETE, true);
      this.setImplemented(Operation.SEARCH, true);
      this.setImplemented(Operation.PWDCHANGE, true);
      this.setImplemented(Operation.PWDRESET, true);
      /*
       * Specify which operations are enabled
       */
      this.setEnabled(Operation.CREATE, true);
      this.setEnabled(Operation.READ, true);
      this.setEnabled(Operation.UPDATE, true);
      this.setEnabled(Operation.DELETE, true);
      this.setEnabled(Operation.SEARCH, true);
      this.setEnabled(Operation.PWDCHANGE, true);
      this.setEnabled(Operation.PWDRESET, true);
      
      return;
   }
   
}
