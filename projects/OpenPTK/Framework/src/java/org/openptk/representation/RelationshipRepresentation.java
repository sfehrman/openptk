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
import org.openptk.model.RelationshipIF;
import org.openptk.session.SessionIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//================================================================
public class RelationshipRepresentation extends Representation
//================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _uri = null;
   private String _contextId = null;
   private String _subjectId = null;
   private String _relationshipId = null;

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public RelationshipRepresentation(final EngineIF engine)
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

      this.setVariables(opcode, structIn);

      switch (opcode)
      {
         case CREATE:
            structOut = this.doCreate(session, structIn);
            break;
         case READ:
            structOut = this.doRead(session, structIn);
            break;
         case UPDATE:
            structOut = this.doUpdate(session, structIn);
            break;
         case DELETE:
            structOut = this.doDelete(session, structIn);
            break;
         case SEARCH:
            structOut = this.doSearch(session, structIn);
            break;
         default:
            this.handleError(session, METHOD_NAME + ": Operation: '"
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
   private void setVariables(final Opcode opcode, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      boolean allowNull = false;
      String METHOD_NAME = CLASS_NAME + ":setVariables(): ";
      StructureIF structParamsPath = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      try
      {
         _uri = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);
         _contextId = this.getStringValue(StructureIF.NAME_CONTEXTID, structParamsPath, allowNull);
         _subjectId = this.getStringValue(StructureIF.NAME_SUBJECTID, structParamsPath, allowNull);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      if (opcode != Opcode.SEARCH)
      {
         try
         {
            _relationshipId = this.getStringValue(StructureIF.NAME_RELATIONSHIPID, structParamsPath, allowNull);
         }
         catch (Exception ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private StructureIF doCreate(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doCreate(): ";
      String uriBase = null;
      Object uniqueId = null;
      ElementIF elem = null;
      Output output = null;
      ContextIF context = null;
      RelationshipIF relationship = null;
      StructureIF structOut = null;

      /*
       * Get the Context and Relationship and Element (primary subject)
       */

      try
      {
         context = this.getEngine().getContext(_contextId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(session, METHOD_NAME
            + "Could not get the Context '" + _contextId + "', " + ex.getMessage());
      }

      if (context == null)
      {
         this.handleError(session, METHOD_NAME + "Context '" + _contextId + "' is null");
      }

      relationship = context.getRelationship(_relationshipId);
      if (relationship == null)
      {
         this.handleError(session, METHOD_NAME
            + "Could not get Relationship '" + _relationshipId + "' for Context '"
            + _contextId + "'");
      }

      elem = this.getSubjectElement();
      if (elem == null)
      {
         this.handleError(session, METHOD_NAME
            + "Could not get Subject '" + _subjectId + "' for Context '" + _contextId + "'");
      }

      /*
       * get the uri
       */

      uriBase = this.getUriBase(relationship);

      /*
       * Execute the Relationship's Operation
       */

      try
      {
         output = relationship.execute(Operation.CREATE, elem, structIn);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + "relationship.execute(): Relationship='"
            + _relationshipId + "', " + ex.getMessage());
      }

      if (output == null)
      {
         this.handleError(session, METHOD_NAME + "Output is null");
      }

      uniqueId = output.getUniqueId();
      if (uniqueId == null)
      {
         this.handleError(session, METHOD_NAME + "UniqueId (from Output) is null");
      }

      /*
       * Create the Response
       */

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase + "/" + uniqueId.toString()));

      /*
       * build the output
       */

      try
      {
         switch (output.getUniqueIdType())
         {
            case STRING:
               structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (String) uniqueId));
               break;
            case INTEGER:
               structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Integer) uniqueId));
               break;
            case LONG:
               structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Long) uniqueId));
               break;
         }

         structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, output.getStateAsString()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, output.getStatus()));
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      structOut.setState(output.getState());

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doRead(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      String uriBase = null;
      ContextIF context = null;
      Output output = null;
      ElementIF elem = null;
      RelationshipIF relationship = null;
      StructureIF structOut = null;

      /*
       * Get the Context and Relationship and Element (primary subject)
       */

      try
      {
         context = this.getEngine().getContext(_contextId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(session, METHOD_NAME
            + "Could not get the Context '" + _contextId + "', " + ex.getMessage());
      }
      if (context == null)
      {
         this.handleError(session, METHOD_NAME
            + "Context '" + _contextId + "' is null");
      }

      relationship = context.getRelationship(_relationshipId);
      if (relationship == null)
      {
         this.handleError(session, METHOD_NAME
            + "Could not get Relationship '" + _relationshipId + "' for Context '"
            + _contextId + "'");
      }

      elem = this.getSubjectElement();
      if (elem == null)
      {
         this.handleError(session, METHOD_NAME
            + "Could not get Subject '" + _subjectId + "' for Context '" + _contextId + "'");
      }

      /*
       * get the uri
       */

      uriBase = this.getUriBase(relationship);

      /*
       * Create the Response
       */

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      /*
       * Execute the Relationship's Operation
       */

      try
      {
         output = relationship.execute(Operation.READ, elem, structIn);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      if (output == null)
      {
         this.handleError(session, METHOD_NAME + "Output is null");
      }

      /*
       * build the output
       */

      this.getSearchOutput(structOut, output);

      try
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, output.getState().toString()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, output.getStatus()));
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      structOut.setState(output.getState());

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doUpdate(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      Object uniqueId = null;
      String METHOD_NAME = CLASS_NAME + ":doUpdate(): ";
      String uriBase = null;
      ElementIF elem = null;
      Output output = null;
      ContextIF context = null;
      RelationshipIF relationship = null;
      StructureIF structOut = null;

      /*
       * Get the Context and Relationship and Element (primary subject)
       */

      try
      {
         context = this.getEngine().getContext(_contextId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(session, METHOD_NAME
            + "Could not get the Context '" + _contextId + "', " + ex.getMessage());
      }

      if (context == null)
      {
         this.handleError(session, METHOD_NAME + "Context '" + _contextId + "' is null");
      }

      relationship = context.getRelationship(_relationshipId);
      if (relationship == null)
      {
         this.handleError(session, METHOD_NAME
            + "Could not get Relationship '" + _relationshipId + "' for Context '"
            + _contextId + "'");
      }

      elem = this.getSubjectElement();
      if (elem == null)
      {
         this.handleError(session, METHOD_NAME
            + "Could not get Subject '" + _subjectId + "' for Context '" + _contextId + "'");
      }

      /*
       * get the uri
       */

      uriBase = this.getUriBase(relationship);

      /*
       * Create the Response
       */

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      /*
       * Execute the Relationship's Operation
       */

      try
      {
         output = relationship.execute(Operation.UPDATE, elem, structIn);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + "relationship.execute(): Relationship='"
            + _relationshipId + "', " + ex.getMessage());
      }

      if (output == null)
      {
         this.handleError(session, METHOD_NAME + "Output is null");
      }

      uniqueId = output.getUniqueId();
      if (uniqueId == null)
      {
         this.handleError(session, METHOD_NAME + "UniqueId (from Output) is null");
      }

      /*
       * build the output
       */

      try
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, uniqueId.toString()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, output.getState().toString()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, output.getStatus()));
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      structOut.setState(output.getState());

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doDelete(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      Object uniqueId = null;
      String METHOD_NAME = CLASS_NAME + ":doDelete(): ";
      String uriBase = null;
      ElementIF elem = null;
      Output output = null;
      ContextIF context = null;
      RelationshipIF relationship = null;
      StructureIF structOut = null;

      /*
       * Get the Context and Relationship and Element (primary subject)
       */

      try
      {
         context = this.getEngine().getContext(_contextId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(session, METHOD_NAME
            + "Could not get the Context '" + _contextId + "', " + ex.getMessage());
      }

      if (context == null)
      {
         this.handleError(session, METHOD_NAME + "Context '" + _contextId + "' is null");
      }

      relationship = context.getRelationship(_relationshipId);
      if (relationship == null)
      {
         this.handleError(session, METHOD_NAME
            + "Could not get Relationship '" + _relationshipId + "' for Context '"
            + _contextId + "'");
      }

      elem = this.getSubjectElement();
      if (elem == null)
      {
         this.handleError(session, METHOD_NAME
            + "Could not get Subject '" + _subjectId + "' for Context '" + _contextId + "'");
      }

      /*
       * get the uri
       */

      uriBase = this.getUriBase(relationship);

      /*
       * Create the Response
       */

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      /*
       * Execute the Relationship's Operation
       */

      try
      {
         output = relationship.execute(Operation.DELETE, elem, structIn);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + "relationship.execute(): Relationship='"
            + _relationshipId + "', " + ex.getMessage());
      }

      if (output == null)
      {
         this.handleError(session, METHOD_NAME + "Output is null");
      }

      uniqueId = output.getUniqueId();
      if (uniqueId == null)
      {
         this.handleError(session, METHOD_NAME + "UniqueId (from Output) is null");
      }


      /*
       * build the output
       */

      try
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, uniqueId.toString()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, output.getState().toString()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, output.getStatus()));
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      structOut.setState(output.getState());

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
      String relId = null;
      String[] relIds = null;
      ContextIF context = null;
      StructureIF structOut = null;
      StructureIF structRelationships = null;
      StructureIF structRelationship = null;

      try
      {
         context = this.getEngine().getContext(_contextId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      relIds = context.getRelationshipNames();

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      structRelationships = new BasicStructure(StructureIF.NAME_RELATIONSHIPS);
      structRelationships.setMultiValued(true);

      len = relIds.length;

      structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));

      for (int i = 0; i < len; i++)
      {
         relId = relIds[i];
         structRelationship = new BasicStructure(StructureIF.NAME_RELATIONSHIP);

         structRelationship.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, relId));

         if (uriBase.endsWith("/"))
         {
            uriChild = uriBase + relId;
         }
         else
         {
            uriChild = uriBase + "/" + relId;
         }

         structRelationship.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

         structRelationships.addValue(structRelationship);
      }

      structOut.addChild(structRelationships);

      structOut.setState(State.SUCCESS);

      return structOut;
   }

   //----------------------------------------------------------------
   private ElementIF getSubjectElement() throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getSubjectElement(): ";
      List<String> availAttrNames = null;
      Iterator<String> iter = null;
      ElementIF result = null;
      Input input = null;
      Output output = null;
      SubjectIF subject = null;

      input = new Input();
      input.setUniqueId(_subjectId);

      try
      {
         subject = this.getEngine().getSubject(_contextId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME
            + "Could not get Subject for Context '" + _contextId + "' " + ex.getMessage());
      }

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
         this.handleError(METHOD_NAME + "Context '" + _contextId + "', " + ex.getMessage());
      }

      if (output.getResultsSize() == 1)
      {
         result = output.getResults().get(0);
      }
      else
      {
         this.handleError(METHOD_NAME + "Output for '" + _subjectId + "' is null");
      }

      return result;
   }

   //----------------------------------------------------------------
   private String getUriBase(final RelationshipIF relationship)
   //----------------------------------------------------------------
   {
      String uriBase = null;
      String relCtxId = null;
      ContextIF relCtx = null;

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
         relCtxId = _contextId;
      }

      /*
       * build the "uriBase" value
       */

      if (relCtxId.equals(_contextId))
      {
         if (_uri.indexOf(_subjectId) >= 0)
         {
            uriBase = _uri.substring(0, _uri.indexOf("/" + _subjectId));
         }
         else
         {
            uriBase = _uri;
         }
      }
      else
      {
         uriBase = _uri.substring(0, _uri.indexOf(_contextId)) + relCtxId
            + "/" + StructureIF.NAME_SUBJECTS;
      }

      return uriBase;
   }
}
