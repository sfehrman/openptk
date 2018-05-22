/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009-2011 Sun Microsystems, Inc.
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

import java.util.Iterator;
import java.util.List;

import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.Query;
import org.openptk.api.State;
import org.openptk.common.Operation;
import org.openptk.config.Configuration;
import org.openptk.definition.SubjectIF;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.ProvisionException;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Derrick Harcey
 */
//===================================================================
public class ChildrenLinkRelationship extends Relationship
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   public static final String PROP_LINK_CONTEXT_NAME = "relationship.link.context.name";
   public static final String PROP_PRIMARY_CONTEXT_NAME = "relationship.primary.context.name";
   public static final String PROP_LINKCONTEXT_LINKEDID_ATTRIBUTE = "relationship.linkcontext.linkedid.attribute";

   //----------------------------------------------------------------
   public ChildrenLinkRelationship()
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
   public ChildrenLinkRelationship(final Query query)
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
   public Output execute(Operation operation, ElementIF element, StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      Output output = null;

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
         case READ:
            output = this.doRead(element, structIn);
            break;
         default:
            this.handleError(METHOD_NAME + ": Operation: '"
               + operation.toString() + "' is not implemented.");
            break;
      }

      return output;

   }

   //----------------------------------------------------------------
   private Output doRead(ElementIF result, StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {

      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      Input input = null;
      Output output = null;
      SubjectIF subject = null;

      try
      {
         subject = getPrimaryContextSubject();
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + "config.getSubject(): " + ex.getMessage());
         //set error state in output
         output.setError(true);
         output.setDescription("Failed to get Subject for primary context");
         output.setStatus("error");
         output.setState(State.ERROR);
         return output;
      }


      if (result == null)
      {
         this.handleError(METHOD_NAME + "Argument Result is null");
      }

      input = new Input();

      try
      {
         this.includeOperationAttributes(subject, input, Operation.SEARCH);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + "Could not get Operation Attributes: " + ex.getMessage());
      }

      try
      {
         this.updateQuery(result, input);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + "Could not obtain query: " + ex.getMessage());
      }

      try
      {
         output = subject.execute(Operation.SEARCH, input);
      }
      catch (ProvisionException ex)
      {
         this.handleError(METHOD_NAME + "Could not execute Search Operation: " + ex.getMessage());
      }

      return this.getLinkedSubjects(output);
   }

   //----------------------------------------------------------------
   private Output getLinkedSubjects(final Output output) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getLinkedSubjects(): ";
      String linkContext = null;
      String linkIdAttr = null;
      List<ElementIF> list = null;
      Iterator<ElementIF> iter = null;
      ElementIF result = null;
      AttributeIF attr = null;
      Input input = null;
      Output linkedOutput = null;
      Output linkedOutputs = null;
      SubjectIF subject = null;
      Configuration config = null;

      linkContext = this.getProperty(PROP_LINK_CONTEXT_NAME);

      if (linkContext == null || linkContext.length() < 1)
      {
         this.handleError(METHOD_NAME + "Property '" + PROP_LINK_CONTEXT_NAME + "' is null");
      }

      linkIdAttr = this.getProperty(PROP_LINKCONTEXT_LINKEDID_ATTRIBUTE);

      if (linkIdAttr == null || linkIdAttr.length() < 1)
      {
         this.handleError(METHOD_NAME + "Property '" + PROP_LINKCONTEXT_LINKEDID_ATTRIBUTE + "' is null");
      }

      if (output != null)
      {

         config = this.getContext().getConfiguration();
         linkedOutputs = new Output();

         if (output.getResultsSize() > 0)
         {
            list = output.getResults();
            iter = list.iterator();

            while (iter.hasNext())
            {
               result = iter.next();

               if (result != null)
               {

                  attr = result.getAttribute(linkIdAttr);

                  try
                  {
                     subject = config.getSubject(linkContext);
                  }
                  catch (ConfigurationException ex)
                  {
                     this.handleError(METHOD_NAME + "config.getSubject(): " + ex.getMessage());
                     //set error state in output
                     linkedOutputs.setError(true);
                     linkedOutputs.setDescription("Failed to get Subject for linked context");
                     linkedOutputs.setStatus("error");
                     linkedOutputs.setState(State.ERROR);
                     return linkedOutputs;
                  }

                  input = new Input();
                  
                  switch (attr.getType())
                  {
                     case STRING:
                        input.setUniqueId((String) attr.getValue());
                        break;
                     case INTEGER:
                        input.setUniqueId((Integer) attr.getValue());
                        break;
                     case LONG:
                        input.setUniqueId((Long) attr.getValue());
                        break;
                  }

                  try
                  {
                     this.includeOperationAttributes(subject, input, Operation.READ);
                  }
                  catch (Exception ex)
                  {
                     this.handleError(METHOD_NAME + "Could not read subject: " + ex.getMessage());
                  }

                  try
                  {
                     linkedOutput = subject.execute(Operation.READ, input);
                     linkedOutputs.addResult(linkedOutput.getResults().get(0));
                  }
                  catch (ProvisionException ex)
                  {
                     this.handleError(METHOD_NAME + "Could not execute Read Operation: " + ex.getMessage());
                  }
               }
            }

            linkedOutputs.setState(State.SUCCESS);
            linkedOutputs.setStatus("Entries: " + linkedOutputs.getResultsSize());
         }
         else
         {
            linkedOutputs.setState(State.SUCCESS);
            linkedOutputs.setStatus("Entries: 0");
         }
      }
      return linkedOutputs;
   }

   //----------------------------------------------------------------
   private SubjectIF getPrimaryContextSubject() throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getLinkedSubjects(): ";
      SubjectIF subject = null;
      String primaryContext = null;
      Configuration config = null;

      primaryContext = this.getProperty(PROP_PRIMARY_CONTEXT_NAME);

      if (primaryContext == null || primaryContext.length() < 1)
      {
         this.handleError(METHOD_NAME + "Property '" + PROP_LINK_CONTEXT_NAME + "' is null");
      }

      config = this.getContext().getConfiguration();
      subject = config.getSubject(primaryContext);

      return subject;
   }
}
