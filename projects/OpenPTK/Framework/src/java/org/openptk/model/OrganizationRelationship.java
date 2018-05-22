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
package org.openptk.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.Query;
import org.openptk.api.State;
import org.openptk.common.Operation;
import org.openptk.definition.SubjectIF;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class OrganizationRelationship extends Relationship
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public OrganizationRelationship()
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
   public OrganizationRelationship(final Query query)
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
      int len = 0;
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      Map<String, ElementIF> organization = null;
      Input input = null;
      Output output = null;
      SubjectIF subject = null;
      ElementIF elem = null;
      ElementIF[] elements = null;

      subject = this.getContext().getSubject();

      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject from Context is null");
      }

      if (result == null)
      {
         this.handleError(METHOD_NAME + "Argument Result is null");
      }

      input = new Input();
      organization = new LinkedHashMap<String, ElementIF>();

      try
      {
         this.includeOperationAttributes(subject, input, Operation.READ);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + "Could not update Attributes: " + ex.getMessage());
      }

      this.getParent(organization, subject, result, input);

      /*
       * Turn organization list into an Output object
       * (process the list in reverse order)
       */

      output = new Output();
      output.setDescription("Organization for '" 
         + (result.getUniqueId() != null ? result.getUniqueId().toString() : "(null)")
         + "'");
      
      if (!organization.isEmpty())
      {
         elements = organization.values().toArray(new ElementIF[organization.size()]);
         len = elements.length;
         for (int i = (len - 1); i >= 0; i--)
         {
            elem = elements[i];
            if (elem != null)
            {
               output.addResult(elem);
            }
         }
         output.setState(State.SUCCESS);
         output.setStatus("Entries: " + len);
      }
      else
      {
         output.setState(State.NOTEXIST);
         output.setStatus("No Entries Found");
      }

      return output;
   }
}
