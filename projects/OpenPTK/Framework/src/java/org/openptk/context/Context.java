/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2010 Sun Microsystems, Inc.
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
package org.openptk.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openptk.api.Query;
import org.openptk.api.State;
import org.openptk.authenticate.AuthenticatorIF;
import org.openptk.common.AssignmentIF;
import org.openptk.common.AttrIF;
import org.openptk.common.BasicAssignment;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.config.Configuration;
import org.openptk.context.actions.Action;
import org.openptk.context.actions.ActionIF;
import org.openptk.context.actions.ActionMode;
import org.openptk.debug.Debugger;
import org.openptk.definition.DefinitionIF;
import org.openptk.definition.SubjectIF;
import org.openptk.exception.ActionException;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.ProvisionException;
import org.openptk.exception.ServiceException;
import org.openptk.logging.Logger;
import org.openptk.model.ModelIF;
import org.openptk.model.RelationshipIF;
import org.openptk.spi.ServiceIF;
import org.openptk.spi.operations.OperationsIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Context extends Component implements ContextIF
//===================================================================
{

   private boolean _bAudit = false;
   private boolean _bSort = false;
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private Query _query = null;
   private ServiceIF _service = null;
   private Map<String, OperationsIF> _operations = null;  // <Classname, Object>
   private Map<String, RelationshipIF> _relationships = null;
   private ModelIF _model = null;
   private DefinitionIF _definition = null;
   private AuthenticatorIF _authen = null;
   private Debugger _debugger = null;
   private Configuration _config = null;
   private Map<Operation, List<ActionIF>> _actions = null;
   private Map<String, AssignmentIF> _assignments = null;

   //----------------------------------------------------------------
   public Context()
   //----------------------------------------------------------------
   {
      super();
      this.setDescription("Abstract Context");
      _operations = new HashMap<String, OperationsIF>();
      _relationships = new HashMap<String, RelationshipIF>();
      _actions = new HashMap<Operation, List<ActionIF>>();
      return;
   }

   /**
    * @param ctx
    */
   //----------------------------------------------------------------
   public Context(final ContextIF ctx)
   //----------------------------------------------------------------
   {
      super(ctx);

      this.setDescription("Abstract Context (copy)");
      _operations = new HashMap<String, OperationsIF>();
      _relationships = new HashMap<String, RelationshipIF>();
      _actions = new HashMap<Operation, List<ActionIF>>();
      _bAudit = ctx.isAudit();

      return;
   }

   /**
    * @throws Throwable
    */
   //----------------------------------------------------------------
   @Override
   public void finalize() throws Throwable
   //----------------------------------------------------------------
   {
      super.finalize();
      this.stopService();
      return;
   }

   /**
    * @param request
    * @return
    * @throws ProvisionException
    */
   //----------------------------------------------------------------
   @Override
   public synchronized ResponseIF execute(final RequestIF request) throws ProvisionException
   //----------------------------------------------------------------
   {
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      String callerId = null;
      ResponseIF response = null;

      if (request == null)
      {
         this.handleError(METHOD_NAME + "Request is null");
      }

      this.checkService();

      uid = this.getUniqueId();

      callerId = METHOD_NAME + (uid != null ? "[" + uid.toString() + "]" : "(null)");

      if (request.isDebug())
      {
         this.debug(request, callerId);
      }

      /*
       * Run all preActions
       */

      try
      {
         this.preAction(request);
      }
      catch (ProvisionException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      if (this.isTimeStamp())
      {
         this.setTimeStamp(ComponentIF.EXECUTE_BEGIN);
      }

      /*
       * Execute
       */

      try
      {
         response = _service.execute(request);
      }
      catch (ServiceException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      if (this.isTimeStamp())
      {
         this.setTimeStamp(ComponentIF.EXECUTE_END);
         this.logTime(this.getTimeStamp(ComponentIF.EXECUTE_BEGIN),
            this.getTimeStamp(ComponentIF.EXECUTE_END),
            callerId + "::" + request.getOperationAsString());
      }

      if (_bAudit)
      {
         this.logAudit(response);
      }

      if (_bSort)
      {
         this.sortResults(response);
      }

      switch (response.getState())
      {
         case SUCCESS:

            /*
             * Run all postActions
             */

            try
            {
               this.postAction(response);
            }
            catch (ProvisionException ex)
            {
               this.handleError(METHOD_NAME + ex.getMessage());
            }
            break;
         case AUTHENTICATED:
         case NOTAUTHENTICATED:
            break;
         case NOTEXIST:
         case INVALID:
         case FAILED:
            Logger.logWarning(METHOD_NAME + response.getStatus());
            break;
         case ERROR:
            this.handleError(METHOD_NAME + response.getStatus());
            break;
         default:
            this.handleError(METHOD_NAME + "Invalid State: "
               + response.getStateAsString() + ", " + response.getStatus());
            break;
      }

      if (response.isDebug())
      {
         this.debug(response, callerId);
      }

      return response;
   }

   /**
    * @param config
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setConfiguration(final Configuration config)
   //----------------------------------------------------------------
   {
      _config = config;
      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Configuration getConfiguration()
   //----------------------------------------------------------------
   {
      return _config;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Query getQuery()
   //----------------------------------------------------------------
   {
      return _query;
   }

   /**
    * @param query
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setQuery(final Query query)
   //----------------------------------------------------------------
   {
      _query = query;
      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final ServiceIF getService()
   //----------------------------------------------------------------
   {
      return _service;
   }

   /**
    * @param service
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setService(final ServiceIF service)
   //----------------------------------------------------------------
   {
      _service = service;
      return;
   }

   /**
    * @param id
    * @param operation
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setOperation(final String id, final OperationsIF operation)
   //----------------------------------------------------------------
   {
      if (id != null && id.length() > 0 && operation != null)
      {
         _operations.put(id, operation);
      }

      return;
   }

   /**
    * @param id
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final OperationsIF getOperation(final String id)
   //----------------------------------------------------------------
   {
      OperationsIF operation = null;

      if (id != null && id.length() > 0 && _operations.containsKey(id))
      {
         operation = _operations.get(id);
      }

      return operation;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getOperationNames()
   //----------------------------------------------------------------
   {
      String[] array = null;

      if (!_operations.isEmpty())
      {
         array = _operations.keySet().toArray(new String[_operations.size()]);
      }
      else
      {
         array = new String[0];
      }

      return array;
   }

   /**
    * @param id
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final boolean hasOperation(final String id)
   //----------------------------------------------------------------
   {
      boolean found = false;

      if (id != null && id.length() > 0)
      {
         found = _operations.containsKey(id);
      }

      return found;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final DefinitionIF getDefinition()
   //----------------------------------------------------------------
   {
      return _definition;
   }

   /**
    * @param def
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setDefinition(final DefinitionIF def)
   //----------------------------------------------------------------
   {
      _definition = def;
      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final AuthenticatorIF getAuthenticator()
   //----------------------------------------------------------------
   {
      return _authen;
   }

   /**
    * @param authen
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setAuthenticator(final AuthenticatorIF authen)
   //----------------------------------------------------------------
   {
      _authen = authen;
      return;
   }

   /**
    * @param model
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setModel(final ModelIF model)
   //----------------------------------------------------------------
   {
      _model = model;
      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final ModelIF getModel()
   //----------------------------------------------------------------
   {
      return _model;
   }

   /**
    * @param id
    * @param relationship
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setRelationship(final String id, final RelationshipIF relationship)
   //----------------------------------------------------------------
   {
      if (id != null && id.length() > 0 && relationship != null)
      {
         _relationships.put(id, relationship);
      }
      return;
   }

   /**
    * @param id
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized boolean hasRelationship(final String id)
   //----------------------------------------------------------------
   {
      boolean found = false;

      if (id != null && id.length() > 0)
      {
         found = _relationships.containsKey(id);
      }

      return found;
   }

   /**
    * @param id
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final RelationshipIF getRelationship(final String id)
   //----------------------------------------------------------------
   {
      RelationshipIF relationship = null;

      if (id != null && id.length() > 0 && _relationships.containsKey(id))
      {
         relationship = _relationships.get(id);
      }

      return relationship;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getRelationshipNames()
   //----------------------------------------------------------------
   {
      String[] array = null;

      if (!_relationships.isEmpty())
      {
         array = _relationships.keySet().toArray(new String[_relationships.size()]);
      }
      else
      {
         array = new String[0];
      }

      return array;
   }

   /**
    * @param operation
    * @param action
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addAction(final Operation operation, final ActionIF action)
   //----------------------------------------------------------------
   {
      List<ActionIF> actions = null;

      if (action != null)
      {
         actions = _actions.get(operation);
         if (actions == null)
         {
            actions = new LinkedList<ActionIF>();
            actions.add(action);
            _actions.put(operation, actions);
         }
         else
         {
            actions.add(action);
         }
      }
      return;
   }

   /**
    * @param operation
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final ActionIF[] getActions(final Operation operation)
   //----------------------------------------------------------------
   {
      ActionIF[] actionsArray = null;
      List<ActionIF> actionsList = null;

      actionsList = _actions.get(operation);
      if (actionsList != null)
      {
         actionsArray = actionsList.toArray(new Action[actionsList.size()]);
      }
      else
      {
         actionsArray = new Action[0];
      }

      return actionsArray;
   }

   /**
    * @param sort
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setSort(final boolean sort)
   //----------------------------------------------------------------
   {
      _bSort = sort;
      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isSort()
   //----------------------------------------------------------------
   {
      return _bSort;
   }

   /**
    * @param audit
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setAudit(final boolean audit)
   //----------------------------------------------------------------
   {
      _bAudit = audit;
      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isAudit()
   //----------------------------------------------------------------
   {
      return _bAudit;
   }

   /**
    * @param obj
    * @param callerId
    */
   //----------------------------------------------------------------
   @Override
   public final void logData(final Object obj, final String callerId)
   //----------------------------------------------------------------
   {
      this.debug(obj, callerId);
      return;
   }

   /**
    * @param obj
    * @param callerId
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String getData(final Object obj, final String callerId)
   //----------------------------------------------------------------
   {
      if (_debugger == null)
      {
         _debugger = new Debugger();
      }
      return _debugger.getData(obj, callerId);
   }

   /**
    * @param begin
    * @param end
    * @param msg
    */
   //----------------------------------------------------------------
   @Override
   public final void logTime(final Long begin, final Long end, final String msg)
   //----------------------------------------------------------------
   {
      int iMsec = 0;

      iMsec = end.intValue() - begin.intValue();

      Logger.logInfo("Timestamp: " + iMsec + " (msec) [" + msg + "]");

      return;
   }

   /**
    * @param resp
    */
   //----------------------------------------------------------------
   @Override
   public final void logAudit(final ResponseIF resp)
   //----------------------------------------------------------------
   {
      Object uid = null;
      StringBuilder buf = new StringBuilder("Audit: ");
      RequestIF request = null;
      ComponentIF subject = null;

      buf.append(_service.getClass().getSimpleName()).append(": ");

      if (resp != null)
      {
         buf.append(resp.getStateAsString()).append(": ");

         request = resp.getRequest();
         if (request != null)
         {
            subject = request.getSubject();
            if (subject != null)
            {
               uid = subject.getUniqueId();
               if (uid != null)
               {
                  buf.append(request.getOperationAsString()).append(": ");
                  buf.append("UniqueId=").append(uid.toString());
               }
               else
               {
                  buf.append("Uid is null");
               }
            }
            else
            {
               buf.append("Subject is null");
            }
         }
         else
         {
            buf.append("Request is null");
         }
      }
      else
      {
         buf.append("Response is null");
      }

      Logger.logInfo(buf.toString());

      return;
   }

   /**
    * @return
    * @throws ProvisionException
    */
   //----------------------------------------------------------------
   @Override
   public final SubjectIF getSubject() throws ProvisionException
   //----------------------------------------------------------------
   {
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":getSubject(): ";
      SubjectIF subject = null;

      uid = this.getUniqueId();
      if (uid == null)
      {
         this.handleError(METHOD_NAME + "Uid is null");
      }

      if (_config != null)
      {
         try
         {
            subject = _config.getSubject(uid.toString());
         }
         catch (ConfigurationException ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "Config is null");
      }

      return subject;
   }

   /**
    * Set an Assignment
    *
    * @param id
    * @param assignment
    * @since 2.2
    */
   //----------------------------------------------------------------
   @Override
   public synchronized void setAssignment(String id, AssignmentIF assignment)
   //----------------------------------------------------------------
   {
      if (assignment != null && id != null && id.length() > 0)
      {
         if (_assignments == null)
         {
            _assignments = new HashMap<String, AssignmentIF>();
         }
         _assignments.put(id, assignment.copy());
      }
      return;
   }
   
   /**
    * Get an Assignment
    * @param id
    * @return 
    * @since 2.2
    */
   //----------------------------------------------------------------
   @Override
   public synchronized AssignmentIF getAssignment(String id)
   //----------------------------------------------------------------
   {
      AssignmentIF assignment = null;
      
      if ( id != null && id.length() > 0 && _assignments != null && _assignments.containsKey(id))
      {
         assignment = _assignments.get(id);
      }
      
      return assignment;
   }

   /**
    * Get an array of all the Assignments
    *
    * @return
    * @since 2.2
    */
   //----------------------------------------------------------------
   @Override
   public String[] getAssignmentNames()
   //----------------------------------------------------------------
   {
      String[] names = null;

      if (_assignments != null && !_assignments.isEmpty())
      {
         names = _assignments.keySet().toArray(new String[_assignments.size()]);
      }
      else
      {
         names = new String[0];
      }

      return names;
   }

   /*
    * *****************
    * PROTECTED METHODS
    * *****************
    */
   /**
    * @param request
    * @throws ProvisionException
    */
   //----------------------------------------------------------------
   protected void preAction(final RequestIF request) throws ProvisionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":preAction(): ";
      List<ActionIF> actions = null;
      Operation operation = null;
      ActionMode mode = null;

      /*
       * Call the "preAction" method on all the Actions related to
       * this Operation that have a mode of either PRE or BOTH
       */

      operation = request.getOperation();

      if (_actions.containsKey(operation))
      {
         actions = _actions.get(operation);

         if (actions != null && !actions.isEmpty())
         {

            for (ActionIF action : actions)
            {
               if (action != null)
               {
                  mode = action.getMode();
                  if (mode == ActionMode.PRE || mode == ActionMode.BOTH)
                  {
                     try
                     {
                        action.preAction(_service, request);
                     }
                     catch (ActionException ex)
                     {
                        this.handleError(METHOD_NAME + ex.getMessage());
                     }
                  }
               }
            }
         }
      }

      return;
   }

   /**
    * @param response
    * @throws ProvisionException
    */
   //----------------------------------------------------------------
   protected void postAction(final ResponseIF response) throws ProvisionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":postAction(): ";
      List<ActionIF> actions = null;
      Operation operation = null;
      ActionMode mode = null;

      /*
       * Call the "postAction" method on all the Actions related to
       * this Operation that have a mode of either POST or BOTH
       */

      operation = response.getRequest().getOperation();

      if (_actions.containsKey(operation))
      {
         actions = _actions.get(operation);
         if (actions != null && !actions.isEmpty())
         {
            for (ActionIF action : actions)
            {
               if (action != null)
               {
                  mode = action.getMode();
                  if (mode == ActionMode.POST || mode == ActionMode.BOTH)
                  {
                     try
                     {
                        action.postAction(_service, response);
                     }
                     catch (ActionException ex)
                     {
                        this.handleError(METHOD_NAME + ex.getMessage());
                     }
                  }
               }
            }
         }
      }

      return;
   }

   /**
    * @param msg
    * @throws ProvisionException
    */
   //----------------------------------------------------------------
   protected final void handleError(final String msg) throws ProvisionException
   //----------------------------------------------------------------
   {
      String str = null;

      if (msg == null || msg.length() < 1)
      {
         str = "(null message)";
      }
      else
      {
         str = msg;
      }
      throw new ProvisionException(str);
   }

   /**
    * @param response
    */
   //----------------------------------------------------------------
   protected final synchronized void sortResults(final ResponseIF response)
   //----------------------------------------------------------------
   {
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":sortResults(): ";
      String callerId = null;

      uid = this.getUniqueId();
      callerId = METHOD_NAME + (uid != null ? "[" + uid.toString() + "]" : "(null)");

      /*
       * walk through all the result Components and set the sort value.
       * the sort value is a concatination of the Components attribute
       * values that are set in the Contexts sortAttribute names
       */

      List<ComponentIF> results = null;
      List<String> sortAttrs = null;
      AttrIF attr = null;
      Object obj = null;
      StringBuilder buf = null;

      if (response.getResultsSize() > 1)
      {
         if (this.isTimeStamp())
         {
            this.setTimeStamp(ComponentIF.SORT_BEGIN);
         }

         sortAttrs = this.getService().getSortAttributes();

         /*
          * Process all of the Components that are stored as a List in the
          * Response object.
          * Get the Components attributes (which are listed in the array)
          * Read the attribute values and concatinate them to build a new
          * String that will be used for sort comparison.
          */

         results = response.getResults();

         for (ComponentIF result : results)
         {
            buf = new StringBuilder();
            for (String str : sortAttrs)
            {
               attr = null;
               obj = null;

               attr = result.getAttribute(str);
               if (attr != null)
               {
                  obj = attr.getValue();
                  if (obj != null)
                  {
                     buf.append(obj.toString().toLowerCase()).append(" ");
                  }
               }
            }
            result.setSortValue(buf.toString());
         }

         /*
          * use the Collections sort method passing it the List of Components
          */

         Collections.sort(results);

         if (this.isTimeStamp())
         {
            this.setTimeStamp(ComponentIF.SORT_END);
            this.logTime(this.getTimeStamp(ComponentIF.SORT_BEGIN),
               this.getTimeStamp(ComponentIF.SORT_END),
               callerId + " " + response.getRequest().getOperationAsString());
         }

      }
      return;
   }

   /**
    * @param obj
    * @param callerId
    */
   //----------------------------------------------------------------
   protected final void debug(final Object obj, final String callerId)
   //----------------------------------------------------------------
   {
      if (_debugger == null)
      {
         _debugger = new Debugger();
      }
      _debugger.logData(obj, callerId);

      return;
   }

   //----------------------------------------------------------------
   protected final void stopService()
   //----------------------------------------------------------------
   {
      _service.shutdown();
      return;
   }

   /**
    * @throws ProvisionException
    */
   //----------------------------------------------------------------
   protected final void checkService() throws ProvisionException
   //----------------------------------------------------------------
   {
      if (_service.getState() != State.READY)
      {
         this.handleError("Service (" + _service.getDescription()
            + ") is NOT ready: status='"
            + _service.getStatus() + "'");
      }

      return;
   }
}
