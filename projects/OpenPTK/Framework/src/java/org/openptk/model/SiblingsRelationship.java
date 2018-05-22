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

import java.util.Iterator;
import java.util.List;

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
public class SiblingsRelationship extends Relationship
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public SiblingsRelationship()
   //----------------------------------------------------------------
   {
      super();
      _type = RelationshipType.LIST;
      return;
   }

   /**
    * @param query
    */
   //----------------------------------------------------------------
   public SiblingsRelationship(final Query query)
   //----------------------------------------------------------------
   {
      super(query);
      _type = RelationshipType.LIST;
      return;
   }

   /**
    * @param operation
    * @param result
    * @param struct
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public Output execute(final Operation operation, final ElementIF result, final StructureIF struct) throws Exception
   //----------------------------------------------------------------
   {
      Object uid = null;
      Object elemUid = null;
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      Input input = null;
      Output output = null;
      SubjectIF subject = null;
      ElementIF elem = null;
      ElementIF self = null;
      List<ElementIF> siblings = null;
      Iterator<ElementIF> iter = null;

      subject = this.getContext().getSubject();

      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject from Context is null");
      }

      if (result == null)
      {
         this.handleError(METHOD_NAME + "Argument Result is null");
      }

      uid = result.getUniqueId();
      if (uid == null)
      {
         this.handleError(METHOD_NAME + "UniqueId (from Result) is null");
      }

      input = new Input();

      try
      {
         this.includeOperationAttributes(subject, input, Operation.SEARCH);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + "Could not update Attributes: " + ex.getMessage());
      }

      try
      {
         this.updateQuery(result, input);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + "Could not update query: " + ex.getMessage());
      }

      try
      {
         output = subject.execute(Operation.SEARCH, input);
      }
      catch (ProvisionException ex)
      {
         this.handleError(METHOD_NAME + "Could not execute Search Operation: " + ex.getMessage());
      }

      /*
       * Go through the output results and remove the
       * result that relates to the provided result
       */

      if (output != null)
      {
         siblings = output.getResults();
         if (siblings != null && !siblings.isEmpty())
         {
            iter = siblings.iterator();
            while (iter.hasNext())
            {
               elem = iter.next();
               elemUid = elem.getUniqueId();
               if (elemUid != null)
               {
                  if (elemUid.toString().equals(uid.toString()))
                  {
                     self = elem;
                  }
               }
            }
         }

         if (self != null)
         {
            siblings.remove(self);
         }
      }

      return output;
   }
}
