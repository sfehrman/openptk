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
 * Portions Copyright 2011-2013 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.representation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openptk.api.AttributeIF;
import org.openptk.api.DataType;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Opcode;
import org.openptk.api.Output;
import org.openptk.api.Query;
import org.openptk.api.QueryBuilderIF;
import org.openptk.api.State;
import org.openptk.common.AttrIF;
import org.openptk.common.BasicAttr;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.context.ContextQueryBuilder;
import org.openptk.debug.DebugIF;
import org.openptk.definition.DefinitionIF;
import org.openptk.definition.SubjectIF;
import org.openptk.engine.EngineIF;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.ProvisionException;
import org.openptk.exception.StructureException;
import org.openptk.logging.Logger;
import org.openptk.model.ModelIF;
import org.openptk.session.CacheIF;
import org.openptk.session.PwdForgotCache;
import org.openptk.session.SearchCache;
import org.openptk.session.SessionIF;
import org.openptk.spi.ServiceIF;
import org.openptk.spi.operations.OperationsIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;
import org.openptk.structure.StructureType;
import org.openptk.util.Digest;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * @since 2.0.0
 */
//================================================================
public class SubjectRepresentation extends Representation
//================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final int DEFAULT_PAGE_OFFSET = 0;
   private static final int DEFAULT_PAGE_QUANTITY = 10;
   private final Map<String, QueryBuilderIF> _qbuildMap = new HashMap<String, QueryBuilderIF>();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public SubjectRepresentation(final EngineIF engine)
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
   public synchronized StructureIF execute(final Opcode opcode, final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      StructureIF structOut = null;

      if (structIn == null)
      {
         this.handleError(session, METHOD_NAME + "Input Structure is null.");
      }

      if (this.isDebug())
      {
         this.logInfo(session, METHOD_NAME + "Opcode=" + opcode.toString());
         if (this.getDebugLevelAsInt() >= DebugIF.FINE)
         {
            this.logInfo(session, METHOD_NAME + structIn.toString());
         }
      }

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
         case PWDRESET:
            structOut = this.doPwdReset(session, structIn);
            break;
         case PWDCHANGE:
            structOut = this.doPwdChange(session, structIn);
            break;
         case PWDFORGOT:
            structOut = this.doPwdForgot(session, structIn);
            break;
         default:
         {
            this.handleError(session, METHOD_NAME + "Operation: '"
               + opcode.toString() + "' is not implemented.");
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
   private StructureIF doCreate(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      boolean allowNull = false;
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String contextId = null;
      String uriBase = null;
      String uriChild = null;
      String uniqueId = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structSubject = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);

      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      try
      {
         uriBase = this.getUriBase(structIn);
         contextId = this.getStringValue(StructureIF.NAME_CONTEXTID, structParamsPath, allowNull);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      structSubject = structIn.getChild(StructureIF.NAME_SUBJECT);

      if (structSubject != null)
      {
         subject = this.getSubject(session, contextId);

         input = new Input();

         try
         {
            this.processSubjectInput(structSubject, input);
         }
         catch (Exception ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }

         try
         {
            output = subject.execute(Operation.CREATE, input);
         }
         catch (ProvisionException ex)
         {
            this.handleError(session, METHOD_NAME + "subject.execute(): Context '"
               + contextId + "', " + ex.getMessage());
         }

         if (output == null) // fail safe ... output should not be null
         {
            output = new Output();
            output.setUniqueId("");
            output.setError(true);
            output.setState(subject.getState());
            output.setStatus(subject.getStatus());
         }

         uid = output.getUniqueId();
         if (uid == null)
         {
            Logger.logInfo(METHOD_NAME + "Create operation did not return the uniqueId");
            uid = "";
         }

         uniqueId = uid.toString();

         if (uriBase.endsWith("/"))
         {
            uriChild = uriBase + uniqueId;
         }
         else
         {
            uriChild = uriBase + "/" + uniqueId;
         }

         structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
         structOut.setState(output.getState());

         try
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));
            switch (output.getUniqueIdType())
            {
               case STRING:
                  structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (String) uid));
                  break;
               case INTEGER:
                  structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Integer) uid));
                  break;
               case LONG:
                  structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Long) uid));
                  break;
            }
            structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, output.getState().toString()));
            structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, output.getStatus()));
         }
         catch (StructureException ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }
      }
      else
      {
         this.handleError(session, METHOD_NAME + "Subject is null");
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doRead(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      boolean allowNull = false;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String contextId = null;
      String uriBase = null;
      String subjectId = null;
      List<String> availAttrNames = null;
      Iterator<String> iter = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structResources = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      try
      {
         uriBase = this.getUriBase(structIn);
         contextId = this.getStringValue(StructureIF.NAME_CONTEXTID, structParamsPath, allowNull);
         subjectId = this.getStringValue(StructureIF.NAME_SUBJECTID, structParamsPath, allowNull);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      subject = this.getSubject(session, contextId);

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
      structOut.setState(output.getState());

      try
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, output.getState().toString()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, output.getStatus()));
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      if (!output.isError())
      {
         try
         {
            this.getReadOutput(structOut, output);
         }
         catch (Exception ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }
      }

      /*
       * Check the Context's Model for extended URIs (sub resources)
       */

      structResources = this.processContextModel(structIn);
      if (structResources != null)
      {
         try
         {
            structOut.addChild(structResources);
         }
         catch (StructureException ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doUpdate(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String contextId = null;
      String uriBase = null;
      String subjectId = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structSubject = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      try
      {
         uriBase = this.getUriBase(structIn);
         contextId = this.getStringValue(StructureIF.NAME_CONTEXTID, structParamsPath, false);
         subjectId = this.getStringValue(StructureIF.NAME_SUBJECTID, structParamsPath, false);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      structSubject = structIn.getChild(StructureIF.NAME_SUBJECT);

      if (structSubject != null)
      {
         subject = this.getSubject(session, contextId);

         input = new Input();
         input.setUniqueId(subjectId);

         try
         {
            this.processSubjectInput(structSubject, input);
         }
         catch (Exception ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }

         if (input == null)
         {
            this.handleError(session, METHOD_NAME + "Input is null");
         }

         try
         {
            output = subject.execute(Operation.UPDATE, input);
         }
         catch (ProvisionException ex)
         {
            this.handleError(session, METHOD_NAME + "subject.execute(): Context '"
               + contextId + "', " + ex.getMessage());
         }

         structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
         structOut.setState(output.getState());

         try
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, subjectId));
            structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, output.getState().toString()));
            structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, output.getStatus()));
         }
         catch (StructureException ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }

      }
      else
      {
         this.handleError(session, METHOD_NAME + "Subject is null");
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doDelete(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String contextId = null;
      String uriBase = null;
      String subjectId = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;
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
         uriBase = this.getUriBase(structIn);
         contextId = this.getStringValue(StructureIF.NAME_CONTEXTID, structParamsPath, false);
         subjectId = this.getStringValue(StructureIF.NAME_SUBJECTID, structParamsPath, false);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      if (contextId != null)
      {
         subject = this.getSubject(session, contextId);

         if (subjectId != null)
         {
            input = new Input();
            input.setUniqueId(subjectId);

            try
            {
               output = subject.execute(Operation.DELETE, input);
            }
            catch (ProvisionException ex)
            {
               this.handleError(session, METHOD_NAME + "Context '"
                  + contextId + "', " + ex.getMessage());
            }

            structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
            structOut.setState(output.getState());

            try
            {
               structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));
               structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, output.getState().toString()));
               structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, output.getStatus()));
            }
            catch (StructureException ex)
            {
               this.handleError(session, METHOD_NAME + ex.getMessage());
            }
         }
         else
         {
            this.handleError(session, METHOD_NAME + "Subject ID is null");
         }
      }
      else
      {
         this.handleError(session, METHOD_NAME + "Context ID is null");
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doSearch(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      Object obj = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String contextId = null;
      String search = null;
      String offset = null;
      String quantity = null;
      String uriBase = null;
      String sessionId = null;
      String cacheId = null;
      String prop = null;
      List<String> availAttrNames = null;
      Iterator<String> iter = null;
      ContextIF context = null;
      ServiceIF service = null;
      OperationsIF operation = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;
      Query query = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structParamsQuery = null;
      StructureIF structPage = null;
      CacheIF cache = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      structParamsQuery = structIn.getChild(StructureIF.NAME_PARAMQUERY);

      try
      {
         uriBase = this.getUriBase(structIn);
         sessionId = this.getStringValue(StructureIF.NAME_SESSIONID, structIn, false);
         contextId = this.getStringValue(StructureIF.NAME_CONTEXTID, structParamsPath, false);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      cacheId = Opcode.SEARCH.toString();

      try
      {
         context = this.getEngine().getContext(contextId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      service = context.getService();
      operation = service.getOperation(Operation.SEARCH);

      if (operation == null)
      {
         this.handleError(session, METHOD_NAME + "SEARCH not implemented for the '"
            + contextId + "' Context. Is the Context ENABLED.");
      }

      /*
       * Determine if the cache should be checked ... conditions:
       * 1. Does the StructIn contain an "offset",
       * a "quantity" is optional
       * 2. If the SructIn contains a "search" value
       * if the value matches the "cached" value
       * then compare it to the user input value
       * if the two values "match"
       * then check the UPDATED timestamp
       * if the time has expired, do a new search
       * else use the cached search results
       */

      if (structParamsQuery != null)
      {
         try
         {
            offset = this.getStringValue(StructureIF.NAME_OFFSET, structParamsQuery, true);
            quantity = this.getStringValue(StructureIF.NAME_QUANTITY, structParamsQuery, true);
            search = this.getStringValue(StructureIF.NAME_SEARCH, structParamsQuery, true);
         }
         catch (Exception ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }
      }

      if (search == null)
      {
         search = "";
      }

      if (session != null)
      {
         cache = session.getCache(cacheId); // Note: cache is a "copy"
      }
      else
      {
         this.handleError(session, METHOD_NAME + "Session is null, sessionId='" + sessionId + "'");
      }

      if (offset != null && offset.length() > 0)
      {
         if (cache != null)
         {
            /*
             * Check the SEARCH property ... does it match
             */

            prop = cache.getProperty(StructureIF.NAME_SEARCH);
            if (prop != null && prop.equalsIgnoreCase(search))
            {
               obj = cache.getValue();
               if (obj != null && obj instanceof StructureIF)
               {
                  structOut = (StructureIF) obj;
               }

            }
         }
      }

      if (structOut == null)
      {
         offset = Integer.toString(DEFAULT_PAGE_OFFSET);

         structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
         try
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));
         }
         catch (StructureException ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }

         subject = this.getSubject(session, contextId);

         input = new Input();

         // Add the attributes to return for the operation

         availAttrNames = subject.getAvailableAttributes(Operation.SEARCH);
         if (availAttrNames != null)
         {
            iter = availAttrNames.iterator();
            while (iter.hasNext())
            {
               input.addAttribute(iter.next());
            }

         }
         else
         {
            this.handleError(session, METHOD_NAME + "Operation '" + Operation.SEARCH.toString()
               + "' does not have assigned any Attributes.");
         }

         if (search != null && search.length() > 0)
         {
            try
            {
               query = this.getDefaultQuery(search, context);
            }
            catch (Exception ex)
            {
               this.handleError(session, METHOD_NAME + ex.getMessage());
            }

            if (query != null)
            {
               input.setQuery(query);
            }

         }

         // Execute the SEARCH Operation

         try
         {
            output = subject.execute(Operation.SEARCH, input);
         }
         catch (ProvisionException ex)
         {
            this.handleError(session, METHOD_NAME + "Context '"
               + contextId + "', " + ex.getMessage());
         }

         structOut.setState(output.getState());

         if (!output.isError())
         {
            try
            {
               this.getSearchOutput(structOut, output);
            }
            catch (Exception ex)
            {
               this.handleError(session, METHOD_NAME + ex.getMessage());
            }
         }

         /*
          * Create a new Cache (if needed)
          */

         if (cache == null)
         {
            cache = new SearchCache(session);
         }

         cache.setValue(structOut);
         if (search != null)
         {
            cache.setProperty(StructureIF.NAME_SEARCH, search);
         }

         /*
          * Note:
          * The "session" object is a copy of the session in the Engine
          * The "cache" is a copy of the cache in the session
          * We need to replace/set the (new/updated) cache in local copy of session
          * Then, replace/set the updated session in the Engine
          */

         session.setCache(cacheId, cache);
         this.getEngine().setSession(sessionId, session);
      }

      try
      {
         structPage = this.getPage(structOut, offset, quantity);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      if (output != null)
      {
         structPage.setState(output.getState());
      }

      return structPage;
   }

   //----------------------------------------------------------------
   private StructureIF doPwdReset(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String contextId = null;
      String uriBase = null;
      String subjectId = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;
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
         uriBase = this.getUriBase(structIn);
         contextId = this.getStringValue(StructureIF.NAME_CONTEXTID, structParamsPath, false);
         subjectId = this.getStringValue(StructureIF.NAME_SUBJECTID, structParamsPath, false);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      if (contextId != null)
      {
         subject = this.getSubject(session, contextId);

         if (subjectId != null)
         {
            input = new Input();
            input.setUniqueId(subjectId);

            try
            {
               output = subject.execute(Operation.PWDRESET, input);
            }
            catch (ProvisionException ex)
            {
               this.handleError(session, METHOD_NAME + "Context '"
                  + contextId + "', " + ex.getMessage());
            }

            structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
            structOut.setState(output.getState());

            try
            {
               structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));
               structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, output.getState().toString()));
               structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, output.getStatus()));
            }
            catch (StructureException ex)
            {
               this.handleError(session, METHOD_NAME + ex.getMessage());
            }

            try
            {
               this.processResetOutput(structOut, output);
            }
            catch (Exception ex)
            {
               this.handleError(session, METHOD_NAME + ex.getMessage());
            }
         }
         else
         {
            this.handleError(session, METHOD_NAME + "Subject ID is null");
         }
      }
      else
      {
         this.handleError(session, METHOD_NAME + "Context ID is null");
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doPwdChange(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String contextId = null;
      String uriBase = null;
      String subjectId = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structSubject = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      try
      {
         uriBase = this.getUriBase(structIn);
         contextId = this.getStringValue(StructureIF.NAME_CONTEXTID, structParamsPath, false);
         subjectId = this.getStringValue(StructureIF.NAME_SUBJECTID, structParamsPath, false);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      structSubject = structIn.getChild(StructureIF.NAME_SUBJECT);

      if (structSubject != null)
      {
         subject = this.getSubject(session, contextId);

         input = new Input();
         input.setUniqueId(subjectId);

         try
         {
            this.processSubjectInput(structSubject, input);
         }
         catch (Exception ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }

         try
         {
            output = subject.execute(Operation.PWDCHANGE, input);
         }
         catch (ProvisionException ex)
         {
            this.handleError(session, METHOD_NAME + "Context '"
               + contextId + "', " + ex.getMessage());
         }

         structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
         structOut.setState(output.getState());

         try
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, subjectId));
            structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, output.getState().toString()));
            structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, output.getStatus()));
         }
         catch (StructureException ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }
      }
      else
      {
         this.handleError(session, METHOD_NAME + "Subject is null");
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doPwdForgot(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      boolean areEqual = false;
      boolean allowChange = false;
      boolean allowNull = false;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String contextId = null;
      String uriBase = null;
      String sessionId = null;
      String subjectId = null;
      String mode = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;
      StructureIF structSubject = null;
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
         uriBase = this.getUriBase(structIn);
         sessionId = this.getStringValue(StructureIF.NAME_SESSIONID, structIn, allowNull);
         contextId = this.getStringValue(StructureIF.NAME_CONTEXTID, structParamsPath, allowNull);
         subjectId = this.getStringValue(StructureIF.NAME_SUBJECTID, structParamsPath, allowNull);
         mode = this.getStringValue(StructureIF.NAME_MODE, structIn, allowNull);
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

      /*
       * Based on the "mode" use a different "phase"
       * P1: GET "questions", store answers and questions, return questions
       * P2: PUT "answers", compare response with stored answers
       * P3: PUT "password", update the password attribute
       *
       */

      if (session == null)
      {
         this.handleError(session, METHOD_NAME + "Session is null");
      }

      if (mode.equals(StructureIF.NAME_QUESTIONS))
      {
         /*
          * =======
          * Phase 1
          * =======
          *
          * Get the questions
          */
         subject = this.getSubject(session, contextId);

         input = new Input();
         input.setUniqueId(subjectId);
         input.addAttribute(DefinitionIF.ATTR_PWD_FORGOT_QUESTIONS);
         input.addAttribute(DefinitionIF.ATTR_PWD_FORGOT_ANSWERS);

         try
         {
            output = subject.execute(Operation.READ, input);
         }
         catch (ProvisionException ex)
         {
            this.handleError(session, METHOD_NAME + "Context '"
               + contextId + "', " + ex.getMessage());
         }

         if (output != null)
         {
            structOut.setState(output.getState());

            try
            {
               this.processForgotPhase1(structOut, output, session);
            }
            catch (Exception ex)
            {
               this.handleError(session, METHOD_NAME + ex.getMessage());
            }

            /*
             * The session (a copy) has been updated to include a cache,
             * it needs to be "put back" in the Engine's Collection of Sessions
             */

            this.getEngine().setSession(sessionId, session);

            structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, output.getState().toString()));
         }
         else
         {
            this.handleError(session, METHOD_NAME + "Output is null");
         }
      }
      else if (mode.equals(StructureIF.NAME_ANSWERS))
      {
         /*
          * =======
          * Phase 2
          * =======
          *
          * Get the stored anwers from the Session
          * Get the user input which is a Structure named "subject"
          * this wrapped in a "data" Structure, which is the "request"
          * Compare them ... are they equal (ignore case)
          */

         structSubject = structIn.getChild(StructureIF.NAME_SUBJECT);
         if (structSubject == null)
         {
            this.handleError(session, METHOD_NAME + "Subject structure is null, phase=2");
         }

         structSubject.addChild(structParamsPath.getChild(StructureIF.NAME_CONTEXTID));
         structSubject.addChild(structParamsPath.getChild(StructureIF.NAME_SUBJECTID));

         areEqual = this.processForgotPhase2(structSubject, session);

         if (areEqual)
         {
            structOut.setState(State.SUCCESS);
            try
            {
               structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS,
                  "Input matches answers"));
            }
            catch (StructureException ex)
            {
               this.handleError(session, METHOD_NAME + ex.getMessage());
            }

            /*
             * The session (a copy) has been updated with a new cache
             * it needs to be "put back" in the Engine's Collection of Sessions
             */

            this.getEngine().setSession(sessionId, session);
         }
         else
         {
            structOut.setState(State.FAILED);
            try
            {
               structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS,
                  "Input DOES NOT match answers"));
            }
            catch (StructureException ex)
            {
               this.handleError(session, METHOD_NAME + ex.getMessage());
            }
         }
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, structOut.getState().toString()));
      }
      else if (mode.equals(StructureIF.NAME_CHANGE))
      {
         /*
          * =======
          * Phase 3
          * =======
          *
          * Change the password
          */

         structSubject = structIn.getChild(StructureIF.NAME_SUBJECT);
         if (structSubject == null)
         {
            this.handleError(session, METHOD_NAME + "Subject structure is null, phase=3");
         }

         allowChange = this.processForgotPhase3(session);

         if (allowChange)
         {
            subject = this.getSubject(session, contextId);

            /*
             * The session (a copy) has been updated with a new cache
             * it needs to be "put back" in the Engine's Collection of Sessions
             */

            this.getEngine().setSession(sessionId, session);

            input = new Input();
            input.setUniqueId(subjectId);

            try
            {
               this.processSubjectInput(structSubject, input);
            }
            catch (Exception ex)
            {
               this.handleError(session, METHOD_NAME + "processInput(subject,input) phase=3, "
                  + ex.getMessage());
            }

            try
            {
               output = subject.execute(Operation.PWDCHANGE, input);
            }
            catch (Exception ex)
            {
               this.handleError(session, METHOD_NAME + "subject.execute(PWDCHANGE) context='"
                  + contextId + "', subjectId='" + subjectId + "', " + ex.getMessage());
            }

            structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
            structOut.setState(output.getState());

            try
            {
               structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, subjectId));
               structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, output.getState().toString()));
               structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, output.getStatus()));
            }
            catch (StructureException ex)
            {
               this.handleError(session, METHOD_NAME + "subjectId, phase=3, " + ex.getMessage());
            }

            try
            {
               structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, output.getStatus()));
            }
            catch (StructureException ex)
            {
               this.handleError(session, METHOD_NAME + "output status, phase=3, " + ex.getMessage());
            }
         }
         else
         {
            this.handleError(session, METHOD_NAME + "Not allowed to Change Password, phase=3");
         }
      }
      else
      {
         /*
          * Invalid mode
          */
         this.handleError(session, METHOD_NAME + "Invalid mode: '" + mode + "'");
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private void processResetOutput(final StructureIF structOut, final Output output) throws Exception
   //----------------------------------------------------------------
   {
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String[] names = null;
      StructureIF structSubject = null;
      StructureIF structAttrs = null;
      ElementIF result = null;
      AttributeIF attr = null;

      structSubject = new BasicStructure(StructureIF.NAME_SUBJECT);

      if (output.getResultsSize() == 1)
      {
         result = output.getResults().get(0);
         if (result != null)
         {

            uid = result.getUniqueId();
            if (uid == null)
            {
               this.handleError(METHOD_NAME + "UniqueId (from Result) is null");
            }

            structAttrs = new BasicStructure(StructureIF.NAME_ATTRIBUTES);

            try
            {
               switch (result.getUniqueIdType())
               {
                  case STRING:
                     structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (String) uid));
                     break;
                  case INTEGER:
                     structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Integer) uid));
                     break;
                  case LONG:
                     structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Long) uid));
                     break;
               }
               structSubject.addChild(structAttrs);
            }
            catch (StructureException ex)
            {
               this.handleError(METHOD_NAME + ex.getMessage());
            }


            names = result.getAttributeNames();
            for (int i = 0; i < names.length; i++)
            {
               attr = result.getAttribute(names[i]);
               if (attr != null)
               {
                  try
                  {
                     structAttrs.addChild(this.getStructFromAttribute(attr));
                  }
                  catch (StructureException ex)
                  {
                     this.handleError(METHOD_NAME + ex.getMessage());
                  }
               }
            }
         }
         else
         {
            this.handleError(METHOD_NAME + "Result is null");
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "Results not equal to one, size="
            + output.getResultsSize());
      }

      try
      {
         structOut.addChild(structSubject);
      }
      catch (StructureException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      return;
   }

   //----------------------------------------------------------------
   private Query getDefaultQuery(final String search, final ContextIF context) throws Exception
   //----------------------------------------------------------------
   {
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String ctxId = null;
      Query query = null;
      QueryBuilderIF qbldr = null;

      /*
       * Check the "cache" to see if the QueryBuilder has already been created
       * else, create one and store it in the "cache"
       */

      uid = context.getUniqueId();
      if (uid == null)
      {
         this.handleError(METHOD_NAME + "UniqueId (from Context) is null");
      }
      ctxId = uid.toString();

      if (_qbuildMap.containsKey(ctxId))
      {
         qbldr = _qbuildMap.get(ctxId);
      }
      else
      {
         qbldr = new ContextQueryBuilder(context);
         _qbuildMap.put(ctxId, qbldr);
      }

      if (qbldr == null)
      {
         this.handleError(METHOD_NAME + "QueryBuilder is null");
      }

      query = qbldr.build(search);

      return query;
   }

   //----------------------------------------------------------------
   private StructureIF processContextModel(final StructureIF structRequest) throws Exception
   //----------------------------------------------------------------
   {
      boolean allowNull = false;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String uriChild = null;
      String uriBase = null;
      String contextId = null;
      ContextIF context = null;
      ModelIF model = null;
      StructureIF structParamsPath = null;
      StructureIF structResources = null;
      StructureIF structResource = null;
      
      /*
       * If the related Context contains a "model" 
       * create relative URIs for Relationships and Views
       */

      structParamsPath = structRequest.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(METHOD_NAME
            + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      try
      {
         uriBase = this.getStringValue(StructureIF.NAME_URI, structRequest, allowNull);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + StructureIF.NAME_URI + ", " + ex.getMessage());
      }

      try
      {
         contextId = this.getStringValue(StructureIF.NAME_CONTEXTID, structParamsPath, allowNull);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + StructureIF.NAME_CONTEXTID + ", " + ex.getMessage());
      }

      try
      {
         context = this.getEngine().getContext(contextId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      model = context.getModel();
      if (model != null)
      {
         structResources = new BasicStructure(StructureIF.NAME_RESOURCES);
         structResources.setMultiValued(true);

         /*
          * Relationships
          */

         structResource = new BasicStructure(StructureIF.NAME_RESOURCE);

         if (uriBase.endsWith("/"))
         {
            uriChild = uriBase + StructureIF.NAME_RELATIONSHIPS;
         }
         else
         {
            uriChild = uriBase + "/" + StructureIF.NAME_RELATIONSHIPS;
         }

         structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Relationships"));
         structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

         structResources.addValue(structResource);

         /*
          * Views
          */

         structResource = new BasicStructure(StructureIF.NAME_RESOURCE);

         if (uriBase.endsWith("/"))
         {
            uriChild = uriBase + StructureIF.NAME_VIEWS;
         }
         else
         {
            uriChild = uriBase + "/" + StructureIF.NAME_VIEWS;
         }

         structResource.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, "Views"));
         structResource.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

         structResources.addValue(structResource);
      }

      return structResources;
   }

   //----------------------------------------------------------------
   private StructureIF getPage(final StructureIF structIn, final String offset, final String quantity) throws Exception
   //----------------------------------------------------------------
   {
      Object[] values = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      StructureIF structOut = null;
      Object value = null;
      StructureIF structResults = null;
      int iOff = DEFAULT_PAGE_OFFSET;
      int iQty = DEFAULT_PAGE_QUANTITY;
      int iLength = 0;

      if (offset != null && offset.length() > 0)
      {
         try
         {
            iOff = Integer.parseInt(offset);
         }
         catch (NumberFormatException ex)
         {
            iOff = DEFAULT_PAGE_OFFSET;
         }

         if (iOff < 0)
         {
            iOff = DEFAULT_PAGE_OFFSET;
         }

      }

      if (quantity != null && quantity.length() > 0)
      {
         try
         {
            iQty = Integer.parseInt(quantity);
         }
         catch (NumberFormatException ex)
         {
            iQty = DEFAULT_PAGE_QUANTITY;
         }

         if (iQty < 0)
         {
            iQty = 0;
         }

      }

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.setState(structIn.getState());

      try
      {
         structOut.addChild(structIn.getChild(StructureIF.NAME_URI));
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, structIn.getState().toString()));
      }
      catch (StructureException ex)
      {
         this.handleError(METHOD_NAME + StructureIF.NAME_URI + ", " + ex.getMessage());
      }

      if (structIn.getState() == State.SUCCESS)
      {
         structResults = structIn.getChild(StructureIF.NAME_RESULTS);

         if (structResults == null)
         {
            this.handleError(METHOD_NAME + "Search Results are null.");
         }

         values = structResults.getValuesAsArray();
         iLength = values.length;

         structResults = new BasicStructure(StructureIF.NAME_RESULTS);
         structResults.setMultiValued(true);
         structResults.setType(StructureType.STRUCTURE); // explicitly set, results maybe empty 

         /*
          * determine the sub-set to create. two variables need to be set:
          *
          * quantity = the quantity of items to return
          * if the values size is bigger, then set qty equal to it
          *
          * index = the (internal) offset used to access the array items
          * if the offset + qty is GE the values size then
          * set it to the values's size - qty
          */

         if (iOff >= iLength)
         {
            iOff = 0;
         }

         if (iQty > iLength)
         {
            iQty = iLength;
         }

         if ((iOff + iQty) >= iLength)
         {
            iQty = iLength - iOff; // Offset takes priority (change qty)
         }

         for (int i = 0; i < iQty; i++)
         {
            if ((i + iOff) < iLength)
            {
               value = values[i + iOff];
               if (value != null)
               {
                  try
                  {
                     structResults.addValue((StructureIF) value);
                  }
                  catch (StructureException ex)
                  {
                     this.handleError(METHOD_NAME + value.toString() + ", " + ex.getMessage());
                  }
               }
               else
               {
                  this.handleError(METHOD_NAME + "Structure for value is null");
               }
            }
         }

         try
         {
            structOut.addChild(structIn.getChild(StructureIF.NAME_LENGTH));
         }
         catch (StructureException ex)
         {
            this.handleError(METHOD_NAME + StructureIF.NAME_LENGTH + ", " + ex.getMessage());
         }

         try
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_OFFSET, iOff));
         }
         catch (StructureException ex)
         {
            this.handleError(METHOD_NAME + StructureIF.NAME_OFFSET + ", " + ex.getMessage());
         }

         try
         {
            structOut.addChild(new BasicStructure(StructureIF.NAME_QUANTITY, iQty));
         }
         catch (StructureException ex)
         {
            this.handleError(METHOD_NAME + StructureIF.NAME_QUANTITY + ", " + ex.getMessage());
         }

         try
         {
            structOut.addChild(structResults);
         }
         catch (StructureException ex)
         {
            this.handleError(METHOD_NAME + "structResults, " + ex.getMessage());
         }
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private void processForgotPhase1(final StructureIF structOut, final Output output, final SessionIF session) throws Exception
   //----------------------------------------------------------------
   {
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String name = null;
      String[] names = null;
      AttributeIF attribute = null;
      AttrIF attr = null;
      ElementIF result = null;
      CacheIF cache = null;
      StructureIF structSubject = null;
      StructureIF structAttributes = null;
      StructureIF structAttribute = null;

      if (this.isDebug())
      {
         this.logInfo(session, METHOD_NAME + structOut.toString());
      }

      /*
       * Get the questions and answers from the output
       * Create a Structure (for response) which only contains the questions
       * Store the questions and answers in the user's session
       */

      structSubject = new BasicStructure(StructureIF.NAME_SUBJECT);
      structAttributes = new BasicStructure(StructureIF.NAME_ATTRIBUTES);

      if (output.getResultsSize() == 1)
      {
         result = output.getResults().get(0);
         if (result != null)
         {
            uid = result.getUniqueId();
            if (uid == null)
            {
               this.handleError(METHOD_NAME + "UniqueId (from Result) is null");
            }

            try
            {
               switch (result.getUniqueIdType())
               {
                  case STRING:
                     structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (String) uid));
                     break;
                  case INTEGER:
                     structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Integer) uid));
                     break;
                  case LONG:
                     structSubject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, (Long) uid));
                     break;
               }
            }
            catch (StructureException ex)
            {
               this.handleError(METHOD_NAME + ex.getMessage());
            }

            cache = new PwdForgotCache(session);
            cache.setProperty(StructureIF.NAME_UNIQUEID, uid.toString());
            cache.setProperty(StructureIF.NAME_PHASE, "1");

            names = result.getAttributeNames();
            for (int i = 0; i < names.length; i++)
            {
               attribute = result.getAttribute(names[i]);
               if (attribute != null)
               {
                  /*
                   * Update the user's Cache, in their Session
                   */

                  name = attribute.getName();
                  attr = this.getAttrFromAttribute(attribute);
                  if (attr != null)
                  {
                     cache.setAttribute(name, attr);
                  }

                  /*
                   * Update the response Structure
                   */

                  if (name.equals(DefinitionIF.ATTR_PWD_FORGOT_QUESTIONS))
                  {
                     structAttribute = getStructFromAttribute(attribute);
                     if (structAttribute != null)
                     {
                        structAttributes.addChild(structAttribute);
                     }
                  }
               }
            }

            session.setCache(Opcode.PWDFORGOT.toString(), cache);
            structSubject.addChild(structAttributes);
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "Results not equal to one, size="
            + output.getResultsSize() + ", subject may not exist.");
      }

      try
      {
         structOut.addChild(structSubject);
      }
      catch (StructureException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      return;
   }

   //----------------------------------------------------------------
   private boolean processForgotPhase2(final StructureIF structSubject, final SessionIF session) throws Exception
   //----------------------------------------------------------------
   {
      boolean areEqual = false;
      boolean allowNull = false;
      Object obj = null;
      Object[] objArrayAnswers = null;
      String str = null;
      String[] strArray = null;
      String[] strArrayCache = null;  // cached answers
      String[] strArrayAnswers = null; // user provided
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String cacheId = null;
      String contextId = null;
      String subjectId = null;
      SubjectIF subject = null;
      Input input = null;
      Output output = null;
      CacheIF cache = null;
      StructureIF structAnswers = null;
      AttrIF attrQuestions = null;
      AttrIF attrCache = null; // cached answers
      AttrIF attrAnswers = null;
      AttrIF attrValues = null;
      EngineIF engine = null;

      if (this.isDebug() && this.getDebugLevelAsInt() >= DebugIF.FINE)
      {
         this.logInfo(session, METHOD_NAME + structSubject.toString());
      }

      /*
       * NOTE:
       *
       * The attribute called "forgottenPasswordAnswers" is used two different ways:
       *
       * 1) User provided input.
       *
       * When evaluating the input, provided by the end-user, it is stored within the
       * Subject structure as a child attribute (structure) named "forgottenPasswordAnswers"
       * Eventhough the end-user provided attribute is named "forgottenPasswordAnsers",
       * it is referenced, by this method, as "values" / "forgotValues" / "attrValues"
       * We need to reference this attribute as "forgottenPasswordValues" because this same
       * attribute name is used by another part of the process (see note #2)
       *
       * 2) Performing the comparison check
       *
       * After the end-user's "input" is collected. Two other attributes (and their values)
       * are obtained from the logged-in user's Session (actually the session Cache)
       * The Cache contains the attributes; "forgottenPasswordQuestions" and "forgottenPasswordAnswers"
       * In this case (compared to note #1) the "forgottenPasswordAnswers" attribute is the
       * answers to the questions that the user had previously stored in the repository.
       *
       * The comparison process requires all three attributes;
       * - questions,
       * - stored answers
       * - user provided answers (values)
       *
       */

      /*
       * Get the User's input ... format:
       *
       * subject:
       * attributes:
       * forgottenPasswordQuestions : value(s):
       * forgottenPasswordAnswers : value(s):
       *
       *
       */

      engine = this.getEngine();
      if (engine == null)
      {
         this.handleError(session, METHOD_NAME + "Engine is null");
      }

      contextId = this.getStringValue(StructureIF.NAME_CONTEXTID, structSubject, allowNull);
      subjectId = this.getStringValue(StructureIF.NAME_SUBJECTID, structSubject, allowNull);

      /*
       * Get the answers to the questions
       * These were provided by the end-user
       */

      structAnswers = this.getAttributeFromSubject(structSubject, DefinitionIF.ATTR_PWD_FORGOT_ANSWERS); // user input

      if (structAnswers == null)
      {
         this.handleError(METHOD_NAME
            + "Answers Structure for '" + DefinitionIF.ATTR_PWD_FORGOT_ANSWERS + "' is null");
      }

      objArrayAnswers = structAnswers.getValuesAsArray();

      if (objArrayAnswers == null)
      {
         this.handleError(METHOD_NAME
            + "Answers Value for '" + DefinitionIF.ATTR_PWD_FORGOT_ANSWERS + "' is null");
      }

      strArrayAnswers = new String[objArrayAnswers.length];
      for (int i = 0; i < objArrayAnswers.length; i++)
      {
         strArrayAnswers[i] = (String) objArrayAnswers[i];
      }

      attrAnswers = new BasicAttr(DefinitionIF.ATTR_PWD_FORGOT_ANSWERS, strArrayAnswers);

      /*
       * Get the "cached" data from the session
       * The cache contains the stored forgotten password questions and answers
       */

      cacheId = Opcode.PWDFORGOT.toString();
      cache = session.getCache(cacheId); // Note: cache is a copy
      if (cache == null)
      {
         this.handleError(METHOD_NAME + "The Cache '" + cacheId + "' is null or does not exist");
      }

      if (cache.isExpired())
      {
         this.handleError(METHOD_NAME + "The Cache '" + cacheId + "' has expired");
      }

      if (cache.getState() != State.VALID)
      {
         this.handleError(METHOD_NAME + "The Cache '" + cacheId + "' is not VALID, STATE=" + cache.getStateAsString());
      }

      /*
       * Get the Questions that are in the Cache (in the Session)
       */

      attrQuestions = cache.getAttribute(DefinitionIF.ATTR_PWD_FORGOT_QUESTIONS);

      if (attrQuestions == null)
      {
         this.handleError(METHOD_NAME
            + "Cache Attribute for '" + DefinitionIF.ATTR_PWD_FORGOT_QUESTIONS + "' is null");
      }

      if (attrQuestions.getType() != DataType.STRING)
      {
         this.handleError(METHOD_NAME
            + "Cache Attribute value must be a STRING");
      }

      obj = attrQuestions.getValue();

      if (obj == null)
      {
         this.handleError(METHOD_NAME
            + "Cache Value for '" + DefinitionIF.ATTR_PWD_FORGOT_QUESTIONS + "' is null");
      }

      /*
       * Get the Answers (Values) that are in the Cache (in the Session)
       * These "answers" where previously stored by the end-user
       *
       * The name of the attribute (in the cache) is "forgottenPasswordAnswers",
       * we need to rename this attribute to "forgottenPasswordValues" beacause the
       * input attribute from the User (client application) is called "forgottenPasswordAnswers"
       *
       * These may be "null" if the Service does not provide the values
       */

      attrCache = cache.getAttribute(DefinitionIF.ATTR_PWD_FORGOT_ANSWERS);

      if (attrCache == null)
      {
         /*
          * Create an empty Attribute
          */

         strArrayCache = new String[1];
         strArrayCache[0] = new String();

         attrValues = new BasicAttr(DefinitionIF.ATTR_PWD_FORGOT_VALUES, strArrayCache);

         if (this.isDebug())
         {
            this.logInfo(session, METHOD_NAME + "Cache does not contain answer values");
         }
      }
      else
      {
         if (attrCache.getType() != DataType.STRING)
         {
            this.handleError(METHOD_NAME + "Cache value must be a STRING");
         }

         obj = attrCache.getValue();

         if (obj == null)
         {
            this.handleError(METHOD_NAME
               + "Cache for '" + DefinitionIF.ATTR_PWD_FORGOT_VALUES + "' is null");
         }

         if (attrCache.isMultivalued())
         {
            strArray = (String[]) obj;
            attrValues = new BasicAttr(DefinitionIF.ATTR_PWD_FORGOT_VALUES, strArray);
            str = strArray[0];
         }
         else
         {
            str = (String) obj;
            attrValues = new BasicAttr(DefinitionIF.ATTR_PWD_FORGOT_VALUES, str);
         }

         if (Digest.isHashed(str))
         {
            attrValues.setEncrypted(true);
         }

         if (this.isDebug())
         {
            this.logInfo(session, METHOD_NAME + "Found cache answer values='" + attrCache.getValueAsString() + "'");
         }
      }

      /*
       * Execute the PWDFORGOT Operation on the Service
       * It will determine if the end-user input is valid
       */

      try
      {
         subject = engine.getSubject(contextId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(session, METHOD_NAME + "Could not get Subject for Context '"
            + contextId + "', " + ex.getMessage());
      }

      if (subject == null)
      {
         this.handleError(session, METHOD_NAME
            + "Session is null, Engine / Subject, ContextId='" + contextId + "'");
      }

      input = new Input();
      input.setUniqueId(subjectId);
      input.addAttribute(this.getAttributeFromAttr(attrQuestions));
      input.addAttribute(this.getAttributeFromAttr(attrValues));
      input.addAttribute(this.getAttributeFromAttr(attrAnswers));

      try
      {
         output = subject.execute(Operation.PWDFORGOT, input);
      }
      catch (ProvisionException ex)
      {
         this.handleError(session, METHOD_NAME + "Context '"
            + contextId + "', " + ex.getMessage());
      }

      if (output == null)
      {
         this.handleError(session, METHOD_NAME + "output is null: subject.execute(PWDFORGOT)");
      }

      if (output.getState() == State.AUTHENTICATED)
      {
         areEqual = true;
         cache.setProperty(StructureIF.NAME_PHASE, "2");
         session.setCache(cacheId, cache);
      }

      return areEqual;
   }

   //----------------------------------------------------------------
   private boolean processForgotPhase3(final SessionIF session) throws Exception
   //----------------------------------------------------------------
   {
      boolean allowChange = false;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String cacheId = null;
      String phase = null;
      CacheIF cache = null;

      cacheId = Opcode.PWDFORGOT.toString();

      if (session == null)
      {
         this.handleError(METHOD_NAME + "Session is null");
      }

      if (this.isDebug())
      {
         this.logInfo(session, METHOD_NAME + session.toString());
      }

      cache = session.getCache(cacheId);

      if (cache == null)
      {
         this.handleError(METHOD_NAME + "The Cache '" + cacheId + "' is null or does not exist");
      }

      if (cache.isExpired())
      {
         this.handleError(METHOD_NAME + "The Cache '" + cacheId + "' has expired");
      }

      if (cache.getState() != State.VALID)
      {
         this.handleError(METHOD_NAME + "The Cache '" + cacheId + "' is not VALID, STATE=" + cache.getStateAsString());
      }

      phase = cache.getProperty(StructureIF.NAME_PHASE);

      if (phase != null && phase.length() > 0)
      {
         if (phase.equals("2"))
         {
            /*
             * A valid phase
             * Change phase to "3", don't allow another change
             */
            allowChange = true;
            cache.setProperty(StructureIF.NAME_PHASE, "3");

            /*
             * Write the updated cache back to the session
             */

            session.setCache(cacheId, cache);
         }
         else
         {
            this.handleError(METHOD_NAME + "The Phase is not correct, need to answer questions");
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "The Phase is missing, have questions been answered");
      }

      return allowChange;
   }

   //----------------------------------------------------------------
   private StructureIF getAttributeFromSubject(final StructureIF structSubject, final String attrName)
   //----------------------------------------------------------------
   {
      StructureIF[] arrayStructAttrs = null;
      StructureIF structAttrs = null;
      StructureIF structAttr = null;

      if (structSubject != null)
      {
         structAttrs = structSubject.getChild(StructureIF.NAME_ATTRIBUTES);
         if (structAttrs != null)
         {
            arrayStructAttrs = structAttrs.getChildrenAsArray();
            if (arrayStructAttrs != null && arrayStructAttrs.length > 0)
            {
               for (int i = 0; i < arrayStructAttrs.length; i++)
               {
                  if (arrayStructAttrs[i].getName().equalsIgnoreCase(attrName))
                  {
                     structAttr = arrayStructAttrs[i];
                     break;
                  }
               }
            }
         }
      }
      return structAttr;
   }

   //----------------------------------------------------------------
   private SubjectIF getSubject(final SessionIF session, final String contextId) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      SubjectIF subject = null;

      try
      {
         subject = this.getEngine().getSubject(contextId);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(session, METHOD_NAME + "Could not get Subject for Context '"
            + contextId + "', " + ex.getMessage());
      }

      return subject;
   }
}
