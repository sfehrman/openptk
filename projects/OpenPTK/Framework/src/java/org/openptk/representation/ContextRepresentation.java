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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openptk.api.AttributeIF;
import org.openptk.api.Opcode;
import org.openptk.api.Query;
import org.openptk.api.State;
import org.openptk.common.AssignmentIF;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.context.actions.ActionIF;
import org.openptk.engine.EngineIF;
import org.openptk.exception.ConfigurationException;
import org.openptk.model.ModelIF;
import org.openptk.model.RelationshipIF;
import org.openptk.model.ViewIF;
import org.openptk.session.SessionIF;
import org.openptk.spi.ServiceIF;
import org.openptk.spi.operations.OperationsIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//================================================================
public class ContextRepresentation extends Representation
//================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public ContextRepresentation(final EngineIF engine)
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
         {
            structOut = this.doRead(session, structIn);
            break;
         }
         case SEARCH:
         {
            structOut = this.doSearch(session, structIn);
            break;
         }
         default:
         {
            this.handleError(session, METHOD_NAME + "Unsupported Operation: '"
               + opcode.toString() + "'");
         }
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
      boolean allowNull = false;
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      String str = null;
      String uriBase = null;
      String contextId = null;
      String uriSubjects = null;
      String status = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structResources = null;
      StructureIF structResource = null;
      StructureIF structMeta = null;
      StructureIF structService = null;
      Query query = null;
      ContextIF context = null;
      ModelIF model = null;
      State state = State.NEW;
      Collection<OperationsIF> opers = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);
      contextId = this.getStringValue(StructureIF.NAME_CONTEXTID, structParamsPath, allowNull);

      if (uriBase.endsWith("/"))
      {
         uriSubjects = uriBase + StructureIF.NAME_SUBJECTS;
      }
      else
      {
         uriSubjects = uriBase + "/" + StructureIF.NAME_SUBJECTS;
      }

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);

      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      structResources = new BasicStructure(StructureIF.NAME_RESOURCES);
      structResource = new BasicStructure(StructureIF.NAME_SUBJECTS);
      structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Subjects for this Context"));
      structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriSubjects));
      structResources.addChild(structResource);

      structOut.addChild(structResources);

      try
      {
         context = this.getEngine().getContext(contextId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(session, METHOD_NAME + "Context '" + contextId + "' was not found.");
      }

      structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, contextId));

      str = context.getCategory().toString();
      if (str != null && str.length() > 0)
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_CATEGORY, str));
      }

      str = context.getDescription();
      if (str != null && str.length() > 0)
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, str));
      }

      uid = context.getDefinition().getUniqueId();
      if (uid != null)
      {
         str = uid.toString();
         if (str != null && str.length() > 0)
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_DEFINITION, str));
         }
      }

      str = context.getDebugLevelAsString();
      if (str != null && str.length() > 0)
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_DEBUGLEVEL, str));
      }

      query = context.getQuery();
      if (query != null)
      {
         str = query.toString();
         if (str != null && str.length() > 0)
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_QUERY, str));
         }
      }

      uid = context.getService().getUniqueId();
      if (uid != null)
      {
         str = uid.toString();
         if (str != null && str.length() > 0)
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_SERVICE, str));
         }
      }

      this.updateContextStateStatus(context);

      structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, context.getState().toString()));

      if (status != null && status.length() > 0)
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, context.getStatus()));
      }

      str = context.getSortValue();
      if (str != null && str.length() > 0)
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_SORT, str));
      }

      model = context.getModel();
      if (model != null)
      {
         uid = model.getUniqueId();
         if (uid != null)
         {
            str = uid.toString();
            if (str != null && str.length() > 0)
            {
               structOut.addChild(new BasicStructure(StructureIF.NAME_MODEL, str));
            }
         }
      }

      /*
       * Properties
       */

      structOut.addChild(this.getPropsAsStruct(context));

      /*
       * Attributes
       */

      structOut.addChild(this.getAttrsAsStruct(context));

      /*
       * Assignments
       */

      structOut.addChild(this.getAssignmentsAsStruct(context));

      /*
       * Get the Meta Information
       */

      structMeta = this.getMeta(context);
      structOut.addChild(structMeta);

      /*
       * Get Service / Operations
       */

      structService = this.getServiceOperations(context);
      structOut.addChild(structService);

      structOut.setState(State.SUCCESS);

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doSearch(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      boolean isConfig = false;
      boolean allowNull = false;
      int len = 0;
      String METHOD_NAME = CLASS_NAME + ":doSearch(): ";
      String uriBase = null;
      String uriChild = null;
      String[] contextNames = null;
      ContextIF context = null;
      StructureIF structOut = null;
      StructureIF structContext = null;
      StructureIF structContexts = null;

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);

      structContexts = new BasicStructure(StructureIF.NAME_CONTEXTS);
      structContexts.setMultiValued(true);

      /*
       * If the URI contains "engine" then set Config Flag to TRUE
       */

      if (uriBase.toLowerCase().indexOf(StructureIF.NAME_ENGINE.toLowerCase()) >= 0)
      {
         isConfig = true;
      }

      contextNames = this.getEngine().getContextNames();
      Arrays.sort(contextNames);
      len = 0;

      for (String contextName : contextNames)
      {
         if (uriBase.endsWith("/"))
         {
            uriChild = uriBase + contextName;
         }
         else
         {
            uriChild = uriBase + "/" + contextName;
         }

         context = this.getEngine().getContext(contextName);
         if (context != null)
         {
            if (isConfig || context.getState() == State.READY)
            {
               len++;

               this.updateContextStateStatus(context);

               structContext = new BasicStructure(StructureIF.NAME_CONTEXT);
               structContext.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, contextName));

               if (isConfig)
               {
                  structContext.addChild(new BasicStructure(StructureIF.NAME_STATE, context.getState().toString()));
               }

               structContext.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

               structContexts.addValue(structContext);
            }
         }
      }

      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));
      structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));
      structOut.addChild(structContexts);

      structOut.setState(State.SUCCESS);

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF getServiceOperations(final ContextIF context) throws Exception
   //----------------------------------------------------------------
   {
      Object uid = null;
      String name = null;
      ComponentIF component = null;
      AttributeIF attr = null;
      ServiceIF service = null;
      OperationsIF oper = null;
      ActionIF[] actions = null;
      StructureIF structService = null;
      StructureIF structOperation = null;
      StructureIF structOperations = null;
      StructureIF structAction = null;
      StructureIF structActions = null;
      StructureIF structAttr = null;
      StructureIF structAttrs = null;
      List<String> attrNames = null;
      Iterator<String> iterStr = null;

      service = context.getService();

      structService = new BasicStructure(StructureIF.NAME_SERVICE);

      structService.addChild(new BasicStructure(StructureIF.NAME_CLASSNAME, service.getClass().getName()));
      structService.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, service.getDescription()));

      /*
       * Operations
       */

      structOperations = new BasicStructure(StructureIF.NAME_OPERATIONS);
      structService.addChild(structOperations);

      for (Operation operation : Operation.values())
      {
         structOperation = new BasicStructure(operation.toString());

         oper = service.getOperation(operation);
         if (oper != null)
         {
            structOperation.addChild(new BasicStructure(StructureIF.NAME_CLASSNAME, oper.getClass().getName()));
            structOperation.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, oper.getDescription()));
            structOperation.addChild(new BasicStructure(StructureIF.NAME_STATE, oper.getStateAsString()));
            structOperation.addChild(new BasicStructure(StructureIF.NAME_STATUS, oper.getStatus()));
         }

         /*
          * Actions
          */

         structActions = new BasicStructure(StructureIF.NAME_ACTIONS);

         actions = context.getActions(operation);
         if (actions != null && actions.length > 0)
         {
            for (ActionIF action : actions)
            {
               if (action != null)
               {
                  structAction = new BasicStructure(StructureIF.NAME_ACTION);

                  uid = action.getUniqueId();
                  if (uid != null)
                  {
                     structAction.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, uid.toString()));
                  }

                  structAction.addChild(new BasicStructure(StructureIF.NAME_MODE, action.getMode().toString()));
                  structAction.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, action.getDescription()));
                  structAction.addChild(new BasicStructure(StructureIF.NAME_CLASSNAME, action.getClass().getName()));
                  structAction.addChild(this.getPropsAsStruct(action));

                  structActions.addChild(structAction);
               }
            }
         }
         structOperation.addChild(structActions);

         /*
          * Attributes
          */

         component = service.getOperAttr(operation);
         if (component != null)
         {
            attrNames = component.getAttributesNames();

            if (attrNames != null)
            {
               structAttrs = new BasicStructure(StructureIF.NAME_ATTRIBUTES);

               iterStr = attrNames.iterator();
               while (iterStr.hasNext())
               {
                  name = iterStr.next();
                  attr = component.getAttribute(name);
                  if (attr != null)
                  {
                     structAttr = new BasicStructure(StructureIF.NAME_ATTRIBUTE);

                     structAttr.addChild(new BasicStructure(StructureIF.NAME_NAME,
                        name));
                     structAttr.addChild(new BasicStructure(StructureIF.NAME_TYPE,
                        attr.getTypeAsString()));
                     structAttr.addChild(new BasicStructure(StructureIF.NAME_ALLOWMULTI,
                        attr.allowMultivalue()));
                     structAttr.addChild(new BasicStructure(StructureIF.NAME_ENCRYPTED,
                        attr.isEncrypted()));
                     structAttr.addChild(new BasicStructure(StructureIF.NAME_READONLY,
                        attr.isReadOnly()));
                     structAttr.addChild(new BasicStructure(StructureIF.NAME_REQUIRED,
                        attr.isRequired()));
                     structAttr.addChild(new BasicStructure(StructureIF.NAME_VIRTUAL,
                        attr.isVirtual()));

                     structAttr.addChild(this.getPropsAsStruct(attr));

                     structAttrs.addChild(structAttr);
                  }
               }
               structOperation.addChild(structAttrs);
            }
         }
         structOperations.addChild(structOperation);
      }

      return structService;
   }

   //----------------------------------------------------------------
   private StructureIF getMeta(final ContextIF context) throws Exception
   //----------------------------------------------------------------
   {
      Object uid = null;
      String[] relNames = null;
      String[] viewNames = null;
      RelationshipIF relationship = null;
      Query query = null;
      ModelIF model = null;
      ViewIF view = null;
      StructureIF structView = null;
      StructureIF structViews = null;
      StructureIF structMeta = null;
      StructureIF structRel = null;
      StructureIF structRels = null;

      structMeta = new BasicStructure(StructureIF.NAME_META);

      /*
       * Relationships
       */

      structRels = new BasicStructure(StructureIF.NAME_RELATIONSHIPS);

      structMeta.addChild(structRels);

      relNames = context.getRelationshipNames();
      if (relNames != null && relNames.length > 0)
      {
         for (int i = 0; i < relNames.length; i++)
         {
            relationship = context.getRelationship(relNames[i]);
            if (relationship != null)
            {
               structRel = new BasicStructure(relNames[i]);
               structRel.addChild(new BasicStructure(StructureIF.NAME_TYPE, relationship.getType().toString()));

               uid = relationship.getContext().getUniqueId();
               if (uid != null)
               {
                  structRel.addChild(new BasicStructure(StructureIF.NAME_CONTEXTID, uid.toString()));
               }

               query = relationship.getQuery();
               if (query != null)
               {
                  structRel.addChild(new BasicStructure(StructureIF.NAME_QUERY, query.toString()));
               }

               structRel.addChild(this.getPropsAsStruct(relationship));
               structRels.addChild(structRel);
            }
         }
      }

      /*
       * Views
       */

      structViews = new BasicStructure(StructureIF.NAME_VIEWS);

      structMeta.addChild(structViews);

      model = context.getModel();
      if (model != null)
      {
         viewNames = model.getViewNames();
         if (viewNames != null && viewNames.length > 0)
         {
            for (int i = 0; i < viewNames.length; i++)
            {
               view = model.getView(viewNames[i]);
               if (view != null)
               {
                  structView = new BasicStructure(viewNames[i]);

                  relNames = view.getRelationshipIds();
                  if (relNames != null && relNames.length > 0)
                  {
                     structRels = new BasicStructure(StructureIF.NAME_RELATIONSHIPS);

                     for (int j = 0; j < relNames.length; j++)
                     {
                        structRels.addValue(relNames[j]);
                     }

                     structView.addChild(structRels);
                  }
                  structViews.addChild(structView);
               }
            }
         }
      }

      return structMeta;
   }

   //----------------------------------------------------------------
   private void updateContextStateStatus(final ContextIF context)
   //----------------------------------------------------------------
   {
      ServiceIF service = null;
      Map<Operation, OperationsIF> map = null;
      Collection<OperationsIF> opers = null;

      if (context != null)
      {
         /*
          * If any of the Operations "state" is different than the
          * Contact "state", update the Context.
          * update both the state and the status.
          */

         service = context.getService();
         if (service != null)
         {
            map = service.getOperations();
            if (map != null && !map.isEmpty())
            {
               opers = map.values();
               if (opers != null && !opers.isEmpty())
               {
                  for (OperationsIF oper : opers)
                  {
                     if (oper != null && oper.getState() != context.getState())
                     {
                        context.setState(oper.getState());
                        context.setStatus(oper.getStatus());
                     }
                  }
               }
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private StructureIF getAssignmentsAsStruct(ContextIF context) throws Exception
   //----------------------------------------------------------------
   {
      String[] ids = null;
      ComponentIF src = null;
      ComponentIF dst = null;
      StructureIF struct = null;
      StructureIF structAssign = null;
      StructureIF structSrc = null;
      StructureIF structDst = null;
      AssignmentIF assignment = null;

      struct = new BasicStructure(StructureIF.NAME_ASSIGNMENTS);

      if (context != null)
      {
         ids = context.getAssignmentNames();
         if (ids != null && ids.length > 0)
         {
            for (String id : ids)
            {
               if (id != null && id.length() > 0)
               {
                  assignment = context.getAssignment(id);
                  if (assignment != null)
                  {
                     structAssign = new BasicStructure(id);
                     struct.addChild(structAssign);
                     
                     structAssign.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, assignment.getDescription()));
                     
                     src = assignment.getSource();
                     if (src != null)
                     {
                        structSrc = new BasicStructure(StructureIF.NAME_SOURCE);
                        structAssign.addChild(structSrc);
                        
                        structSrc.addChild(new BasicStructure(StructureIF.NAME_TYPE, src.getProperty(StructureIF.NAME_TYPE)));
                        structSrc.addChild(new BasicStructure(StructureIF.NAME_NAME, src.getProperty(StructureIF.NAME_NAME)));
                        structSrc.addChild(new BasicStructure(StructureIF.NAME_VALUE, src.getProperty(StructureIF.NAME_VALUE)));
                     }
                     
                     dst = assignment.getDestination();
                     if (dst != null)
                     {
                        structDst = new BasicStructure(StructureIF.NAME_DESTINATION);
                        structAssign.addChild(structDst);
                        
                        structDst.addChild(new BasicStructure(StructureIF.NAME_TYPE, dst.getProperty(StructureIF.NAME_TYPE)));
                        structDst.addChild(new BasicStructure(StructureIF.NAME_NAME, dst.getProperty(StructureIF.NAME_NAME)));
                        structDst.addChild(new BasicStructure(StructureIF.NAME_VALUE, dst.getProperty(StructureIF.NAME_VALUE)));
                     }
                  }
               }
            }
         }
      }

      return struct;
   }
}
