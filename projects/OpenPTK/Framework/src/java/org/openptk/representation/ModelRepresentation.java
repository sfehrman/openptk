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
package org.openptk.representation;

import org.openptk.api.Opcode;
import org.openptk.api.Query;
import org.openptk.api.State;
import org.openptk.engine.EngineIF;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.StructureException;
import org.openptk.model.ModelIF;
import org.openptk.model.RelationshipIF;
import org.openptk.model.RelationshipType;
import org.openptk.model.ViewIF;
import org.openptk.session.SessionIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class ModelRepresentation extends Representation
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public ModelRepresentation(final EngineIF engine)
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
            structOut = this.doRead(session, structIn);
            break;
         case SEARCH:
            structOut = this.doSearch(session, structIn);
            break;
         default:
            this.handleError(session, METHOD_NAME + "Unsupported Operation: '"
               + opcode.toString() + "'");
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doRead(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      boolean allowNull = false;
      String uriBase = null;
      String modelId = null;
      ModelIF model = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      try
      {
         uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);
         modelId = this.getStringValue(StructureIF.NAME_MODELID, structParamsPath, allowNull);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);

      try
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      try
      {
         model = this.getEngine().getModel(modelId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      if (model == null)
      {
         this.handleError(session, METHOD_NAME + "Model is null.");
      }
      try
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, modelId));
         structOut.addChild(new BasicStructure(StructureIF.NAME_CATEGORY, model.getCategory().toString()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, model.getDescription()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, model.getState().toString()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, model.getStatus()));
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      /*
       * Properties
       */

      try
      {
         structOut.addChild(this.getPropsAsStruct(model));
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      /*
       * Attributes
       */

      try
      {
         structOut.addChild(this.getAttrsAsStruct(model));
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      /*
       * Relationship
       */

      try
      {
         structOut.addChild(this.getRelationships(model));
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      /*
       * Views
       */

      try
      {
         structOut.addChild(this.getViews(model));
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

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
      String uriBase = null;
      String uriChild = null;
      String id = null;
      String[] modelIds = null;
      ModelIF model = null;
      StructureIF structOut = null;
      StructureIF structModel = null;
      StructureIF structModels = null;

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      structModels = new BasicStructure(StructureIF.NAME_MODELS);
      structModels.setMultiValued(true);

      modelIds = this.getEngine().getModelNames();
      len = modelIds.length;

      structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));

      for (int i = 0; i < len; i++)
      {
         id = modelIds[i];
         model = this.getEngine().getModel(id);
         if (model != null)
         {
            structModel = new BasicStructure(StructureIF.NAME_MODEL);

            structModel.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, id));

            if (uriBase.endsWith("/"))
            {
               uriChild = uriBase + id;
            }
            else
            {
               uriChild = uriBase + "/" + id;
            }

            structModel.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

            structModels.addValue(structModel);
         }
      }

      structOut.addChild(structModels);

      structOut.setState(State.SUCCESS);

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF getRelationships(final ModelIF model) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getRelationships(): ";
      String[] relIds = null;
      String relId = null;
      String queryStr = null;
      Query query = null;
      RelationshipIF relationship = null;
      RelationshipType relType = null;
      StructureIF structRels = null;
      StructureIF structRel = null;

      structRels = new BasicStructure(StructureIF.NAME_RELATIONSHIPS);

      relIds = model.getRelationshipNames();

      if (relIds != null && relIds.length > 0)
      {
         for (int i = 0; i < relIds.length; i++)
         {
            relId = relIds[i];

            relationship = model.getRelationship(relId);
            if (relationship != null)
            {
               relType = relationship.getType();
               query = relationship.getQuery();
               if (query != null)
               {
                  queryStr = query.toString();
               }
               else
               {
                  queryStr = "";
               }

               structRel = new BasicStructure(StructureIF.NAME_RELATIONSHIP);

               try
               {
                  structRel.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, relId));
                  structRel.addChild(new BasicStructure(StructureIF.NAME_TYPE, relType.toString()));
                  structRel.addChild(new BasicStructure(StructureIF.NAME_CATEGORY, relationship.getCategory().toString()));
                  structRel.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, relationship.getDescription()));
                  structRel.addChild(new BasicStructure(StructureIF.NAME_STATE, relationship.getState().toString()));
                  structRel.addChild(new BasicStructure(StructureIF.NAME_STATUS, relationship.getStatus()));
                  structRel.addChild(new BasicStructure(StructureIF.NAME_QUERY, queryStr));
                  structRel.addChild(this.getPropsAsStruct(relationship));
                  structRel.addChild(this.getAttrsAsStruct(relationship));
               }
               catch (Exception ex)
               {
                  throw new Exception(METHOD_NAME + ex.getMessage());
               }

               try
               {
                  structRels.addChild(structRel);
               }
               catch (StructureException ex)
               {
                  throw new Exception(METHOD_NAME + ex.getMessage());
               }
            }
         }
      }

      return structRels;
   }

   //----------------------------------------------------------------
   private StructureIF getViews(final ModelIF model) throws Exception
   //----------------------------------------------------------------
   {
      String[] viewIds = null;
      String viewId = null;
      String[] relIds = null;
      String relId = null;
      ViewIF view = null;
      StructureIF structViews = null;
      StructureIF structView = null;
      StructureIF structRels = null;

      structViews = new BasicStructure(StructureIF.NAME_VIEWS);

      viewIds = model.getViewNames();
      if (viewIds != null && viewIds.length > 0)
      {
         for (int i = 0; i < viewIds.length; i++)
         {
            viewId = viewIds[i];
            structView = new BasicStructure(StructureIF.NAME_VIEW);
            structView.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, viewId));

            view = model.getView(viewId);
            structRels = new BasicStructure(StructureIF.NAME_RELATIONSHIPS);

            if (view != null)
            {
               relIds = view.getRelationshipIds();
               if (relIds != null && relIds.length > 0)
               {
                  for (int j = 0; j < relIds.length; j++)
                  {
                     relId = relIds[j];
                     structRels.addValue(relId);
                  }
               }
            }
            structView.addChild(structRels);
            structViews.addChild(structView);
         }
      }

      return structViews;
   }
}
