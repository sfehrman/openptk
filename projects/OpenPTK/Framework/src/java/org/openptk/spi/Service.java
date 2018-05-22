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
package org.openptk.spi;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openptk.api.Query;
import org.openptk.api.State;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.Response;
import org.openptk.common.ResponseIF;
import org.openptk.context.ContextIF;
import org.openptk.exception.OperationException;
import org.openptk.exception.ServiceException;
import org.openptk.logging.Logger;
import org.openptk.spi.operations.OperationsIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Service extends Component implements ServiceIF
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private ContextIF _context = null;
   private List<String> _sortAttrs = null;
   private Map<Operation, Integer> _timeouts = null;
   private Map<Operation, OperationsIF> _operations = null;
   private Map<Operation, ComponentIF> _attrgroups = null;
   private Map<Operation, ComponentIF> _associations = null;
   private Map<Operation, ComponentIF> _operattrs = null;
   private Map<Operation, Properties> _operprops = null;
   private Map<Operation, Map<String, String>> _fw2srvc = null;
   private Map<Operation, Map<String, String>> _srvc2fw = null;
   private Map<Operation, Query> _queries = null;
   private Map<Operation, String> _keys = null;
   protected static final int DEF_TIMEOUT = 5000; // milliseconds
   protected String RESPONSE_DESC = "Service Response";

   //----------------------------------------------------------------
   public Service()
   //----------------------------------------------------------------
   {
      super();

      this.init();

      return;
   }

   //----------------------------------------------------------------
   @Override
   public void startup()
   //----------------------------------------------------------------
   {
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void shutdown()
   //----------------------------------------------------------------
   {
      return;
   }

   /**
    * @param request
    * @return ResponseIF
    * @throws ServiceException
    */
   //----------------------------------------------------------------
   @Override
   public ResponseIF execute(final RequestIF request) throws ServiceException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute() ";
      Operation operation = Operation.READ;

      ResponseIF response = null;
      OperationsIF oper = null;

      this.setError(false);
      operation = request.getOperation();

      response = new Response(request);
      response.setDescription(RESPONSE_DESC);
      response.setDebugLevel(request.getDebugLevel());
      response.setDebug(request.isDebug());

      if (this.validateRequest(request, response))
      {

         oper = this.getOperation(operation);

         if (oper != null)
         {
            if (oper.isImplemented(operation))
            {
               if (oper.isEnabled(operation))
               {
                  try
                  {
                     oper.preExecute(request, response);
                     oper.execute(request, response);
                     oper.postExecute(request, response);
                  }
                  catch (OperationException ex)
                  {
                     /*
                      * The Operation (implementation) class should be
                      * setting the Response object's error, state, status
                      * This is typically reached if there's a null pointer
                      * type of error
                      */
                     response.setError(true);
                     response.setState(State.ERROR);
                     response.setStatus(METHOD_NAME + ex.getMessage());
                  }
               }
               else
               {
                  response.setError(true);
                  response.setState(State.ERROR);
                  response.setStatus(METHOD_NAME + "Operation " + operation.toString() + " is not enabled");
               }
            }
            else
            {
               response.setError(true);
               response.setState(State.ERROR);
               response.setStatus(METHOD_NAME + "Operation " + operation.toString() + " is not implemented");
            }
         }
         else
         {
            response.setError(true);
            response.setState(State.ERROR);
            response.setStatus(METHOD_NAME + "Operation is not null");
         }
      }

      return response;
   }

   /**
    * @return ContextIF
    */
   //----------------------------------------------------------------
   @Override
   public final ContextIF getContext()
   //----------------------------------------------------------------
   {
      return _context;
   }

   /**
    * @param context
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setContext(final ContextIF context)
   //----------------------------------------------------------------
   {
      _context = context;
      return;
   }

   /**
    * @param oper
    * @param operation
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setOperation(final Operation oper, final OperationsIF operation)
   //----------------------------------------------------------------
   {
      _operations.put(oper, operation);
      return;
   }

   /**
    * @return Map<Operation, OperationsIF>
    */
   //----------------------------------------------------------------
   @Override
   public final Map<Operation, OperationsIF> getOperations()
   //----------------------------------------------------------------
   {
      return _operations;
   }

   /**
    * @param oper
    * @return OperationsIF
    */
   //----------------------------------------------------------------
   @Override
   public final OperationsIF getOperation(final Operation oper)
   //----------------------------------------------------------------
   {
      OperationsIF operation = null;

      operation = _operations.get(oper);

      return operation;
   }

   /**
    * @param operation
    * @return boolean
    */
   //----------------------------------------------------------------
   @Override
   public final boolean hasOperation(final Operation operation)
   //----------------------------------------------------------------
   {
      boolean bRet = false;
      OperationsIF oper = null;

      oper = this.getOperation(operation);

      if (oper != null)
      {
         bRet = oper.isEnabled(operation);
      }

      return bRet;
   }

   /**
    * @param operStr
    * @return boolean
    */
   //----------------------------------------------------------------
   @Override
   public final boolean hasOperation(final String operStr)
   //----------------------------------------------------------------
   {
      boolean bRet = false;
      OperationsIF oper = null;
      Operation[] operArray = null;
      Operation operation = null;

      operArray = Operation.values();

      if (operArray != null)
      {
         for (int i = 0; i < operArray.length; i++)
         {
            if (operStr.equalsIgnoreCase(operArray[i].toString()))
            {
               operation = operArray[i];
               oper = this.getOperation(operation);
               if (oper != null)
               {
                  bRet = oper.isEnabled(operation);
               }
               break;
            }
         }
      }
      return bRet;
   }

   /**
    * @param operation
    * @param comp
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setAttrGroup(final Operation operation, final ComponentIF comp)
   //----------------------------------------------------------------
   {
      _attrgroups.put(operation, comp);
      return;
   }

   /**
    * @param operation
    * @return ComponentIF
    */
   //----------------------------------------------------------------
   @Override
   public final ComponentIF getAttrGroup(final Operation operation)
   //----------------------------------------------------------------
   {
      return _attrgroups.get(operation);
   }

   /**
    * @return Map<Operation, ComponentIF>
    */
   //----------------------------------------------------------------
   public final Map<Operation, ComponentIF> getAttrGroups()
   //----------------------------------------------------------------
   {
      return _attrgroups;
   }

   /**
    * @param operation
    * @param msec
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setTimeout(final Operation operation, final int msec)
   //----------------------------------------------------------------
   {
      _timeouts.put(operation, new Integer(msec));
      return;
   }

   /**
    * @param operation
    * @return int
    */
   //----------------------------------------------------------------
   @Override
   public final int getTimeout(final Operation operation)
   //----------------------------------------------------------------
   {
      return _timeouts.get(operation).intValue();
   }

   /**
    * @param operation
    * @param comp
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setAssociation(final Operation operation, final ComponentIF comp)
   //----------------------------------------------------------------
   {
      _associations.put(operation, comp);
      return;
   }

   /**
    * @param operation
    * @return ComponentIF
    */
   //----------------------------------------------------------------
   @Override
   public final ComponentIF getAssociation(final Operation operation)
   //----------------------------------------------------------------
   {
      return _associations.get(operation);
   }

   /**
    * @return Map<Operation, ComponentIF>
    */
   //----------------------------------------------------------------
   public final Map<Operation, ComponentIF> getAssociations()
   //----------------------------------------------------------------
   {
      return _associations;
   }

   /**
    * @param operation
    * @param comp
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setOperAttr(final Operation operation, final ComponentIF comp)
   //----------------------------------------------------------------
   {
      _operattrs.put(operation, comp);
      return;
   }

   /**
    * @param operation
    * @return ComponentIF
    */
   //----------------------------------------------------------------
   @Override
   public final ComponentIF getOperAttr(final Operation operation)
   //----------------------------------------------------------------
   {
      return _operattrs.get(operation);
   }

   /**
    * @param operation
    * @param map
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setFw2SrvcNames(final Operation operation, final Map<String, String> map)
   //----------------------------------------------------------------
   {
      _fw2srvc.put(operation, map);
      return;
   }

   /**
    * @param operation
    * @param map
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setSrvc2FwNames(final Operation operation, final Map<String, String> map)
   //----------------------------------------------------------------
   {
      _srvc2fw.put(operation, map);
      return;
   }

   /**
    * @param operation
    * @return Map<String, String>
    */
   //----------------------------------------------------------------
   @Override
   public final Map<String, String> getFw2SrvcNames(final Operation operation)
   //----------------------------------------------------------------
   {
      return _fw2srvc.get(operation);
   }

   /**
    * @param operation
    * @return Map<String, String>
    */
   //----------------------------------------------------------------
   @Override
   public final Map<String, String> getSrvc2FwNames(final Operation operation)
   //----------------------------------------------------------------
   {
      return _srvc2fw.get(operation);
   }

   /**
    * @param operation
    * @param name
    * @return String
    */
   //----------------------------------------------------------------
   @Override
   public final String getFwName(final Operation operation, final String name)
   //----------------------------------------------------------------
   {
      String str = null;
      Map<String, String> map = null;

      if (_srvc2fw.containsKey(operation))
      {
         map = _srvc2fw.get(operation);
         if (map != null && map.containsKey(name))
         {
            str = map.get(name);
         }
         else
         {
            str = name;
         }
      }
      else
      {
         str = name;
      }

      if (str == null)
      {
         str = name;
      }

      return str;
   }

   /**
    * @param operation
    * @param name
    * @return String
    */
   //----------------------------------------------------------------
   @Override
   public final String getSrvcName(final Operation operation, final String name)
   //----------------------------------------------------------------
   {
      String str = null;
      Map<String, String> map = null;

      if (_fw2srvc.containsKey(operation))
      {
         map = _fw2srvc.get(operation);
         if (map != null && map.containsKey(name))
         {
            str = map.get(name);
         }
         else
         {
            str = name;
         }
      }
      else
      {
         str = name;
      }

      if (str == null)
      {
         str = name;
      }

      return str;
   }

   /**
    * @param operation
    * @param fw
    * @return String[]
    */
   //----------------------------------------------------------------
   @Override
   public final String[] toSrvc(final Operation operation, final String[] fw)
   //----------------------------------------------------------------
   {
      String[] srvc = null;

      srvc = new String[fw.length];

      for (int i = 0; i < srvc.length; i++)
      {
         srvc[i] = this.getSrvcName(operation, fw[i]);
      }

      return srvc;
   }

   /**
    * @param operation
    * @param srvc
    * @return String[]
    */
   //----------------------------------------------------------------
   @Override
   public final String[] toFw(final Operation operation, final String[] srvc)
   //----------------------------------------------------------------
   {
      String[] fw = null;

      fw = new String[srvc.length];

      for (int i = 0; i < fw.length; i++)
      {
         fw[i] = this.getFwName(operation, srvc[i]);
      }

      return fw;
   }

   /**
    * @param operation
    * @param query
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setQuery(final Operation operation, final Query query)
   //----------------------------------------------------------------
   {
      _queries.put(operation, query);
      return;
   }

   /**
    * @param operation
    * @return Query
    */
   //----------------------------------------------------------------
   @Override
   public final Query getQuery(final Operation operation)
   //----------------------------------------------------------------
   {
      return _queries.get(operation);
   }

   /**
    * @return List<String>
    */
   //----------------------------------------------------------------
   @Override
   public final List<String> getSortAttributes()
   //----------------------------------------------------------------
   {
      return _sortAttrs;
   }

   /**
    * @param sortAttrs
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setSortAttributes(final List<String> sortAttrs)
   //----------------------------------------------------------------
   {
      _sortAttrs = sortAttrs;
      return;
   }

   /**
    * @param operation
    * @param key
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setKey(final Operation operation, final String key)
   //----------------------------------------------------------------
   {
      /*
       * The "key" is the name of the unique attribute for a 
       * specific Operation
       */
      _keys.put(operation, key);
      return;
   }

   /**
    * @param operation
    * @return String
    */
   //----------------------------------------------------------------
   @Override
   public final String getKey(final Operation operation)
   //----------------------------------------------------------------
   {
      return _keys.get(operation);
   }

   /**
    * @param operation
    * @param props
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setOperProps(final Operation operation, final Properties props)
   //----------------------------------------------------------------
   {
      _operprops.put(operation, props);
      return;
   }

   /**
    * @param operation
    * @return Properties
    */
   //----------------------------------------------------------------
   @Override
   public final Properties getOperProps(final Operation operation)
   //----------------------------------------------------------------
   {
      return _operprops.get(operation);
   }

   //
   //  =============================
   //  ===== PROTECTED METHODS =====
   //  =============================
   //
   /**
    * @param ex
    * @throws ServiceException
    */
   //----------------------------------------------------------------
   protected final void throwServiceException(final Throwable ex) throws ServiceException
   //----------------------------------------------------------------
   {
      this.setError(true);
      throw new ServiceException(ex);
   }

   /**
    * @param msg
    * @throws ServiceException
    */
   //----------------------------------------------------------------
   protected final void throwServiceException(final String msg) throws ServiceException
   //----------------------------------------------------------------
   {
      this.setError(true);
      throw new ServiceException(this.checkNullPointer(msg));
   }

   //----------------------------------------------------------------
   protected boolean validateRequest(final RequestIF request, final ResponseIF response)
   //----------------------------------------------------------------
   {
      boolean bValid = false;
      ComponentIF subject = null;

      if (request != null)
      {
         subject = request.getSubject();
         if (subject != null)
         {
            bValid = true;
         }
         else
         {
            response.setState(State.FAILED);
            response.setError(true);
            response.setStatus("subject is null");
         }
      }
      else
      {
         response.setState(State.FAILED);
         response.setError(true);
         response.setStatus("request is null");
      }
      return bValid;
   }

   //----------------------------------------------------------------
   protected String checkNullPointer(final String msg)
   //----------------------------------------------------------------
   {
      String ret = null;

      if (msg.toLowerCase().indexOf("nullpointer") > -1)
      {
         ret = msg + " [Check Service configuration/properties]";
      }
      else
      {
         ret = msg;
      }

      return ret;
   }

   //----------------------------------------------------------------
   protected void handleError(final String msg) throws ServiceException
   //----------------------------------------------------------------
   {
      String str = null;

      this.setError(true);
      if (msg == null || msg.length() < 1)
      {
         str = "(null message)";
      }
      else
      {
         str = msg;
      }

      Logger.logError(str);

      throw new ServiceException(str);
   }

   //
   //  ==========================
   //  ==== PRIVATE  METHODS ====
   //  ==========================
   //
   //----------------------------------------------------------------
   private void init()
   //----------------------------------------------------------------
   {
      Operation[] operArray = null;

      this.setCategory(Category.SERVICE);

      _sortAttrs = new LinkedList<String>();
      _operations = new HashMap<Operation, OperationsIF>();
      _attrgroups = new HashMap<Operation, ComponentIF>();
      _associations = new HashMap<Operation, ComponentIF>();
      _operattrs = new HashMap<Operation, ComponentIF>();
      _fw2srvc = new HashMap<Operation, Map<String, String>>();
      _srvc2fw = new HashMap<Operation, Map<String, String>>();
      _queries = new HashMap<Operation, Query>();
      _keys = new HashMap<Operation, String>();
      _operprops = new HashMap<Operation, Properties>();
      _timeouts = new HashMap<Operation, Integer>();

      operArray = Operation.values();

      for (int i = 0; i < operArray.length; i++)
      {
         _timeouts.put(operArray[i], new Integer(Service.DEF_TIMEOUT));
      }
      return;
   }
}
