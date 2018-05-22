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
 * Portions Copyright 2011-2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.representation;

import java.util.Iterator;
import java.util.List;

import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Opcode;
import org.openptk.api.Output;
import org.openptk.api.State;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.definition.SubjectIF;
import org.openptk.engine.EngineIF;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.ProvisionException;
import org.openptk.exception.StructureException;
import org.openptk.model.ModelIF;
import org.openptk.model.RelationshipIF;
import org.openptk.model.ViewIF;
import org.openptk.session.SessionIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//================================================================
public class ViewRepresentation extends Representation
//================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public ViewRepresentation(final EngineIF engine)
   //----------------------------------------------------------------
   {
      super(engine);
      return;
   }

   /**
    * @param opcode
    * @param structIn
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public StructureIF execute(final Opcode opcode, final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      StructureIF structOut = null;

      if (structIn == null)
      {
         this.handleError(session, METHOD_NAME + "Input Structure is null.");
      }

      if (this.isDebug())
      {
         this.logInfo(session, METHOD_NAME + "Opcode="
            + opcode.toString() + ", " + structIn.toString());
      }

      switch (opcode)
      {
         case READ:
            structOut = doRead(session, structIn);
            break;
         case SEARCH:
            structOut = doSearch(session, structIn);
            break;
         default:
            this.handleError(session, METHOD_NAME + "Operation: '"
               + opcode.toString() + "' is not implemented.");
      }

      return structOut;
   }

   /*
    * ***************
    * PRIVATE METHODS
    * ***************
    */
   //----------------------------------------------------------------
   private StructureIF doRead(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      String contextId = null;
      String uri = null;
      String uriBase = null;
      String subjectId = null;
      String viewId = null;
      String relId = null;
      String relCtxId = null;
      String[] relIds = null;
      List<String> availAttrNames = null;
      Iterator<String> iter = null;
      ContextIF context = null;
      ContextIF relCtx = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;
      List<ElementIF> results = null;
      ElementIF result = null;
      ModelIF model = null;
      ViewIF view = null;
      RelationshipIF relationship = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structRelationship = null;
      StructureIF structRelationships = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      try
      {
         uri = this.getStringValue(StructureIF.NAME_URI, structIn, false);
         contextId = this.getStringValue(StructureIF.NAME_CONTEXTID, structParamsPath, false);
         subjectId = this.getStringValue(StructureIF.NAME_SUBJECTID, structParamsPath, false);
         viewId = this.getStringValue(StructureIF.NAME_VIEWID, structParamsPath, false);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      try
      {
         context = this.getEngine().getContext(contextId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      try
      {
         subject = this.getEngine().getSubject(contextId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(session, METHOD_NAME + "Could not get Subject for Context '"
            + contextId + "', " + ex.getMessage());
      }

      /*
       * Get the Subject's "core" data
       */

      input = new Input();
      input.setUniqueId(subjectId);

      availAttrNames = subject.getAvailableAttributes(Operation.READ);
      iter = availAttrNames.iterator();
      while (iter.hasNext())
      {
         input.addAttribute(iter.next());
      }

      try
      {
         output = subject.execute(Operation.READ, input);
      }
      catch (ProvisionException ex)
      {
         this.handleError(session, METHOD_NAME + "Context '"
            + contextId + "', " + ex.getMessage());
      }

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);

      try
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uri));
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      try
      {
         this.getReadOutput(structOut, output);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      /*
       * Get the Relationship(s) defined in the Model's View
       */

      if (uri.indexOf(subjectId) >= 0)
      {
         uriBase = uri.substring(0, uri.indexOf("/" + subjectId));
      }
      else
      {
         uriBase = uri;
      }

      structRelationships = new BasicStructure(StructureIF.NAME_RELATIONSHIPS);

      results = output.getResults();
      if (results == null)
      {
         this.handleError(session, METHOD_NAME + "Output Results are null");
      }

      result = results.get(0);

      model = context.getModel();
      if (model != null)
      {
         view = model.getView(viewId);
         if (view != null)
         {
            relIds = view.getRelationshipIds();
            if (relIds != null && relIds.length > 0)
            {

               for (int i = 0; i < relIds.length; i++)
               {
                  relId = relIds[i];
                  relationship = context.getRelationship(relId);

                  if (relationship == null)
                  {
                     this.handleError(session, METHOD_NAME
                        + "Could not get Relationship '" + relId + "' for Context '"
                        + contextId + "'");
                  }

                  /*
                   * Determine the uriBase
                   * Need to check if the Relationship uses a different Context
                   */

                  relCtx = relationship.getContext();
                  if (relCtx != null && relCtx.getUniqueId() != null)
                  {
                     relCtxId = relCtx.getUniqueId().toString();
                  }
                  else
                  {
                     relCtxId = contextId;
                  }

                  if (relCtxId.equals(contextId))
                  {
                     if (uri.indexOf(subjectId) >= 0)
                     {
                        uriBase = uri.substring(0, uri.indexOf("/" + subjectId));
                     }
                     else
                     {
                        uriBase = uri;
                     }
                  }
                  else
                  {
                     uriBase = uri.substring(0, uri.indexOf(contextId)) + relCtxId
                        + "/" + StructureIF.NAME_SUBJECTS;
                  }

                  try
                  {
                     output = relationship.execute(Operation.READ, result, structIn);
                  }
                  catch (Exception ex)
                  {
                     output = null;
                     this.logError(session, METHOD_NAME + relationship.getClass().getSimpleName() +
                        ".execute(): " + ex.getMessage());
                  }

                  structRelationship = new BasicStructure(relId);
                  if (output != null && output.getResultsSize() > 0)
                  {
                     structRelationship.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));
                     this.getSearchOutput(structRelationship, output);
                  }

                  structRelationships.addChild(structRelationship);
               }
            }
         }
      }

      structOut.addChild(structRelationships);

      structOut.setState(State.SUCCESS);

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doSearch(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      boolean allowNull = false;
      int len = 0;
      String METHOD_NAME = CLASS_NAME + ":doSearch(): ";
      String contextId = null;
      String uriBase = null;
      String uriChild = null;
      String viewId = null;
      String[] viewIds = null;
      ContextIF context = null;
      ModelIF model = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structViews = null;
      StructureIF structView = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      try
      {
         uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, false);
         contextId = this.getStringValue(StructureIF.NAME_CONTEXTID, structParamsPath, false);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      try
      {
         context = this.getEngine().getContext(contextId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      structViews = new BasicStructure(StructureIF.NAME_VIEWS);
      structViews.setMultiValued(true);

      /*
       * Get the Model and then it's View(s)
       */

      model = context.getModel();

      if (model != null)
      {
         viewIds = model.getViewNames();
         len = viewIds.length;

         for (int i = 0; i < len; i++)
         {
            viewId = viewIds[i];
            structView = new BasicStructure(StructureIF.NAME_VIEW);
            structView.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, viewId));

            if (uriBase.endsWith("/"))
            {
               uriChild = uriBase + viewId;
            }
            else
            {
               uriChild = uriBase + "/" + viewId;
            }

            structView.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

            structViews.addValue(structView);
         }
      }

      structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));
      structOut.addChild(structViews);

      structOut.setState(State.SUCCESS);

      return structOut;
   }
}
