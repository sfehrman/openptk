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
package org.openptk.model;

import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.Query;
import org.openptk.common.Operation;
import org.openptk.definition.SubjectIF;
import org.openptk.exception.ProvisionException;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class MediaRelationship extends Relationship
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public MediaRelationship()
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   /**
    * @param query
    */
   //----------------------------------------------------------------
   public MediaRelationship(final Query query)
   //----------------------------------------------------------------
   {
      super(query);
      return;
   }

   /**
    * @param operation
    * @param element
    * @param structIn
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public Output execute(final Operation operation, final ElementIF element, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      Output output = null;

      /*
       * structIn: 
       * request = {
       *    sessionId = "...";
       *    uri = "...";
       *    paramspath = {
       *       contextid = "...";
       *       subjectid = "...";
       *       realtionshipid = "..."
       *    };
       *    subject = {
       *       type="...";
       *       attributes = {
       *          data = OBJECT (byte[]);
       *          length = NUMBER;
       *       }
       *    }
       * }
       * 
       */

      if (element == null)
      {
         this.handleError(METHOD_NAME + "Argument 'ElementIF' is null");
      }

      if (structIn == null)
      {
         this.handleError(METHOD_NAME + "Argument 'StructureIF' is null");
      }

      switch (operation)
      {
         case CREATE:
            output = this.doCreate(element, structIn);
            break;
         case READ:
            output = this.doRead(element, structIn);
            break;
         case UPDATE:
            output = this.doUpdate(element, structIn);
            break;
         case DELETE:
            output = this.doDelete(element, structIn);
            break;
         default:
            this.handleError(METHOD_NAME + ": Operation: '"
               + operation.toString() + "' is not implemented.");
            break;
      }

      return output;
   }

   //----------------------------------------------------------------
   private Output doCreate(final ElementIF element, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doCreate(): ";
      Output output = null;
      Input input = null;
      SubjectIF subject = null;
      StructureIF structSubject = null;
      StructureIF structAttrs = null;
      StructureIF structParamPath = null;
      StructureIF[] structs = null;

      /*
       * Get the "parampath" structure
       */

      structParamPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamPath == null)
      {
         this.handleError(METHOD_NAME + "The 'parampath' Structure is null");
      }

      /*
       * Get the "subject" structure
       */

      structSubject = structIn.getChild(StructureIF.NAME_SUBJECT);
      if (structSubject == null)
      {
         this.handleError(METHOD_NAME + "The 'subject' Structure is null");
      }

      /*
       * Get the "attributes" structure
       */

      structAttrs = structSubject.getChild(StructureIF.NAME_ATTRIBUTES);
      if (structSubject == null)
      {
         this.handleError(METHOD_NAME + "The 'attributes' Structure is null");
      }

      /*
       * Copy the "params" to the "attributes" structure
       * context, subject, relationship
       */

      structs = structParamPath.getChildrenAsArray();
      for (int i = 0; i < structs.length; i++)
      {
         structAttrs.addChild(structs[i]);
      }

      /*
       * Get the Subject from the Context
       */

      subject = this.getContext().getSubject();

      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject from Context is null");
      }

      /*
       * Need a new input, "build" the attributes from the Structure
       */

      input = new Input();

      Input.build(input, structAttrs);

      try
      {
         this.deriveUniqueId(input, element, structParamPath);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + "Could not update uniqueid: " + ex.getMessage());
      }

      /*
       * Execute the CREATE Operation
       */

      try
      {
         output = subject.execute(Operation.CREATE, input);
      }
      catch (ProvisionException ex)
      {
         this.handleError(METHOD_NAME + "Operation failed: " + ex.getMessage());
      }

      /*
       * Copy over the derived uniqueid
       */

      output.setUniqueId(input);

      return output;
   }

   //----------------------------------------------------------------
   private Output doRead(final ElementIF element, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      Input input = null;
      Output output = null;
      SubjectIF subject = null;
      StructureIF structParamPath = null;

      /*
       * Get the "parampath" structure
       */

      structParamPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamPath == null)
      {
         this.handleError(METHOD_NAME + "The 'parampath' Structure is null");
      }

      /*
       * Get the Subject from the Context
       */

      subject = this.getContext().getSubject();

      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject from Context is null");
      }

      /*
       * Need a new input, "get" the attributes from attr-group for the Operation
       * "get" the uniqueid based on the attributes and path parameters
       */

      input = new Input();

      try
      {
         this.includeOperationAttributes(subject, input, Operation.READ);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + "Could not update Attributes: " + ex.getMessage());
      }

      try
      {
         this.deriveUniqueId(input, element, structParamPath);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + "Could not update uniqueid: " + ex.getMessage());
      }

      /*
       * Execute the READ Operation
       */

      try
      {
         output = subject.execute(Operation.READ, input);
      }
      catch (ProvisionException ex)
      {
         this.handleError(METHOD_NAME + "execute(READ) failed: " + ex.getMessage());
      }

      return output;
   }

   //----------------------------------------------------------------
   private Output doUpdate(final ElementIF element, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doUpdate(): ";
      Output output = null;
      Input input = null;
      SubjectIF subject = null;
      StructureIF structSubject = null;
      StructureIF structAttrs = null;
      StructureIF structParamPath = null;
      StructureIF[] structs = null;

      /*
       * Get the "parampath" structure
       */

      structParamPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamPath == null)
      {
         this.handleError(METHOD_NAME + "The 'parampath' Structure is null");
      }

      /*
       * Get the "subject" structure
       */

      structSubject = structIn.getChild(StructureIF.NAME_SUBJECT);
      if (structSubject == null)
      {
         this.handleError(METHOD_NAME + "The 'subject' Structure is null");
      }

      /*
       * Get the "attributes" structure
       */

      structAttrs = structSubject.getChild(StructureIF.NAME_ATTRIBUTES);
      if (structSubject == null)
      {
         this.handleError(METHOD_NAME + "The 'attributes' Structure is null");
      }

      /*
       * Copy the "params" to the "attributes" structure
       * context, subject, relationship
       */

      structs = structParamPath.getChildrenAsArray();
      for (int i = 0; i < structs.length; i++)
      {
         structAttrs.addChild(structs[i]);
      }

      /*
       * Get the Subject from the Context
       */

      subject = this.getContext().getSubject();

      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject from Context is null");
      }

      /*
       * Need a new input, "build" the attributes from the Structure
       */

      input = new Input();

      Input.build(input, structAttrs);

      try
      {
         this.deriveUniqueId(input, element, structParamPath);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + "Could not update uniqueid: " + ex.getMessage());
      }

      /*
       * Execute the UPDATE Operation
       */

      try
      {
         output = subject.execute(Operation.UPDATE, input);
      }
      catch (ProvisionException ex)
      {
         this.handleError(METHOD_NAME + "Operation failed: " + ex.getMessage());
      }

      return output;
   }

   //----------------------------------------------------------------
   private Output doDelete(final ElementIF element, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doDelete(): ";
      Output output = null;
      Input input = null;
      SubjectIF subject = null;
      StructureIF structParamPath = null;

      /*
       * Get the "parampath" structure
       */

      structParamPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamPath == null)
      {
         this.handleError(METHOD_NAME + "The 'parampath' Structure is null");
      }

      /*
       * Get the Subject from the Context
       */

      subject = this.getContext().getSubject();

      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject from Context is null");
      }

      /*
       * Need a new input, uniqueid only
       */

      input = new Input();

      try
      {
         this.deriveUniqueId(input, element, structParamPath);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + "Could not update uniqueid: " + ex.getMessage());
      }

      /*
       * Execute the DELETE Operation
       */

      try
      {
         output = subject.execute(Operation.DELETE, input);
      }
      catch (ProvisionException ex)
      {
         this.handleError(METHOD_NAME + "Operation failed: " + ex.getMessage());
      }

      return output;
   }
}
