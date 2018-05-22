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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.definition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openptk.api.Attribute;
import org.openptk.api.AttributeIF;
import org.openptk.api.DataType;
import org.openptk.api.Element;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.Query;
import org.openptk.api.State;
import org.openptk.common.AttrCategory;
import org.openptk.common.AttrIF;
import org.openptk.common.BasicAttr;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.Request;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.config.Configuration;
import org.openptk.context.ContextIF;
import org.openptk.definition.functions.ArgumentIF;
import org.openptk.definition.functions.ArgumentType;
import org.openptk.definition.functions.FunctionIF;
import org.openptk.definition.functions.TaskIF;
import org.openptk.definition.functions.TaskMode;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.FunctionException;
import org.openptk.exception.ProvisionException;
import org.openptk.spi.ServiceIF;
import org.openptk.spi.operations.OperationsIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Subject extends Component implements SubjectIF
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private Configuration _config = null;
   private ContextIF _context = null;
   private boolean _removeInvalidAttr = false;

   //----------------------------------------------------------------
   public Subject()
   //----------------------------------------------------------------
   {
      super();
      this.setDescription("OpenPTK, Consumer Tier API, Subject");
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
    * Initialize the Subject, after it has been created.
    * @param config an existing Configuration
    * @param contextName the name of a Context defined in the Configuration
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   @Override
   public void initialize(final Configuration config, final String contextName) throws ConfigurationException
   //----------------------------------------------------------------
   {
      if (contextName == null)
      {
         throw new ConfigurationException(config.getMessage("subject.context.null"));
      }

      if (config == null)
      {
         throw new ConfigurationException(config.getMessage("subject.configuration.notset"));
      }

      _config = config;
      _context = _config.getContext(contextName);

      this.setDescription(contextName + ":" + this.getClass().getSimpleName());
      this.setState(State.READY);

      _removeInvalidAttr = Boolean.parseBoolean(_context.getProperty(ContextIF.PROP_ATTRIBUTE_INVALID_REMOVE));

      return;
   }

   /**
    * @param operation
    * @param input
    * @return
    * @throws ProvisionException
    * @throws ConfigurationException
    */
   //----------------------------------------------------------------
   @Override
   public Output execute(final Operation operation, final Input input) throws ProvisionException, ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute() ";
      String err = null;
      String key = null;
      Object uniqueId = null;
      DataType uidType = DataType.OBJECT;
      State state = State.NULL;
      Output output = null;
      Query query = null;
      Component comp = null; //            OpenPTK internal API
      RequestIF request = null; //         OpenPTK internal API
      ResponseIF response = null; //       OpenPTK internal API
      Map<String, AttrIF> fwMap = null; // OpenPTK internal API
      ServiceIF service = null; //         OpenPTK

      if (input == null)
      {
         this.handleError(METHOD_NAME
            + this.getConfiguration().getMessage("subject.input.null"));
      }

      if ( _context == null)
      {
         this.handleError(METHOD_NAME
            + this.getConfiguration().getMessage("configuration.context.notexist"));
      }

      output = new Output();

      /*
       * Check to see if the Operation is implemented
       */

      service = _context.getService();

      if (!service.hasOperation(operation))
      {
         this.handleError(METHOD_NAME
            + this.getConfiguration().getMessage("subject.operation.notimplemented")
            + ": " + operation.toString() + ", Context='"
            + ( _context.getUniqueId() != null ? _context.getUniqueId().toString() : "(null)")
            + "'");
      }

      this.setState(State.READY);
      this.setError(false);

      /*
       * Perform operation specific tasks: BEFORE Functions
       */

      switch (operation)
      {
         case CREATE:
            this.checkRequiredAttributes(input, operation);
            break;
         case READ:
            if (input.getAttributesSize() < 1)
            {
               this.addAttrGroupAttributes(input, operation);
            }
            break;
         case UPDATE:
            break;
         case SEARCH:
            if (input.getAttributesSize() < 1)
            {
               this.addAttrGroupAttributes(input, operation);
            }
            query = input.getQuery();
            break;
      }

      /*
       * Validate the Input data
       * Make sure input does not contain attr names that are not configured
       * and that there are no "private" attributes if the Input is "public"
       * Check the value's type against the "declared" type
       * Set the type if needed
       */

      try
      {
         this.validateInput(input, operation);
      }
      catch (ProvisionException ex)
      {
         /**
          * Thrown if the input is invalid (bad attributes)
          * Set the output state and status
          */
         output.setState(State.INVALID);
         output.setStatus(this.getStatus());
         output.setError(true);
      }

      if (!this.isError())
      {
         /*
          * Get a Map<String, AttrIF> from the Input Map<String, Attribute>
          */

         fwMap = this.getFwAttributes(input.getAttributes(), operation);

         /*
          * The fwMap<String, AttrIF> may have attributes with Functions
          * that need to be run before the operation is processed
          */

         this.evaluateFunctions(operation, TaskMode.TOSERVICE, fwMap, query);

         /*
          * The fwMap<String, AttrIF> needs to have its Service Names set
          * This will use the Service specified in the Definition and
          * get the Service Names from the Service's ANT Maps
          */

         this.setServiceNames(operation, fwMap);

         /*
          * Perform operation specific tasks: AFTER Functions / Translation
          */

         switch (operation)
         {
            case CREATE:
               this.setUniqueId(input, operation, fwMap);
               this.checkRequiredAttributes(fwMap, operation);
               this.removeInvalidAttributes(operation, fwMap);
               this.removeEmptyAttributes(operation, fwMap);
               break;
            case READ:
               this.removeInvalidAttributes(operation, fwMap);
               break;
            case UPDATE:
               this.checkRequiredAttributes(fwMap, operation);
               this.removeInvalidAttributes(operation, fwMap);
               break;
            case SEARCH:
               this.removeInvalidAttributes(operation, fwMap);
               break;
         }

         /*
          * Create the OpenPTK Component which is used to set
          * information that is need by the OpenPTK Request
          * Some information comes from the Input and the Context
          */

         comp = new Component();

         switch (input.getUniqueIdType())
         {
            case STRING:
               comp.setUniqueId((String) input.getUniqueId());
               break;
            case INTEGER:
               comp.setUniqueId((Integer) input.getUniqueId());
               break;
            case LONG:
               comp.setUniqueId((Long) input.getUniqueId());
               break;
         }

         comp.setCategory(Category.SUBJECT);
         comp.setDebug(_context.isDebug());
         comp.setDebugLevel(_context.getDebugLevel());
         comp.setDescription(input.getDescription());
         comp.setProperties(_context.getDefinition().getProperties());

         /*
          * Get the common Attributes and "add" (set) them in the Component
          */

         if (fwMap != null && fwMap.size() > 0)
         {
            comp.setAttributes(fwMap);
         }

         request = new Request();
         request.setService(service);

         if (query != null)
         {
            request.setQuery(this.setServiceNames(operation, query));
         }

         /*
          * Use the "key", from the Service, (which is a FwName) to get the
          * associated Service attribute name. Set the Request's Key to be the
          * SrvcName for the uniqueid.
          */

         key = service.getSrvcName(operation, service.getKey(operation));

         request.setOperation(operation);
         request.setKey(key);
         request.setSubject(comp);
         request.setDescription(this.getDescription());
         request.setDebug(_context.isDebug());
         request.setDebugLevel(_context.getDebugLevel());
         request.setProperties(service.getOperProps(operation));

         /*
          * Perform the operation via the Context execute
          */

         response = _context.execute(request);  // already synchronized

         /*
          * Process the results
          * check for null and certain states, if a problem then throw exception
          */

         if (response == null)
         {
            err = this.getConfiguration().getMessage("subject.response.null");
            this.handleError(METHOD_NAME + err);
         }

         state = response.getState();

         switch (state)
         {
            case SUCCESS:
               break;
            case AUTHENTICATED:
            case NOTAUTHENTICATED:
               break;
            case NOTEXIST:
            case INVALID:
            case FAILED:
               /**
                * The Operation business logic failed to execute
                */
               this.setStatus(response.getStatus());
               this.setState(state);
               this.setError(true);
               break;
            case ERROR:
               /**
                * There's a problem with the underlying infrastructure
                */
               this.setStatus(response.getStatus());
               this.setState(State.ERROR);
               this.setError(true);
               break;
            default:
               err = "Invalid State: Operation=" + request.getOperationAsString()
                  + ", State=" + state + ", " + response.getStatus();
               this.setStatus(err);
               this.setState(State.ERROR);
               this.setError(true);
               break;
         }

         if (this.isError())
         {
            switch (this.getState())
            {
               case NOTEXIST:
               case INVALID:
               case FAILED:
                  output.setState(this.getState());
                  output.setStatus(this.getStatus());
                  output.setError(true);
                  switch (response.getUniqueIdType())
                  {
                     case STRING:
                        output.setUniqueId((String) response.getUniqueId());
                        break;
                     case INTEGER:
                        output.setUniqueId((Integer) response.getUniqueId());
                        break;
                     case LONG:
                        output.setUniqueId((Long) response.getUniqueId());
                        break;
                  }
                  break;
               default:
                  this.handleError(METHOD_NAME + this.getStatus());
                  break;
            }
         }
         else
         {

            /*
             * Set uniqueId
             * Add the missing Attributes
             */

            switch (operation)
            {
               case CREATE:
                  uniqueId = response.getUniqueId();
                  uidType = response.getUniqueIdType();
                  break;
               case READ:
               case SEARCH:
                  uniqueId = input.getUniqueId();
                  uidType = input.getUniqueIdType();
                  this.missingAttributes(response, input);
                  break;
               default:
                  uniqueId = input.getUniqueId();
                  uidType = input.getUniqueIdType();
                  break;
            }

            output.setDescription(response.getDescription());
            output.setError(response.isError());
            output.setStatus(response.getStatus());
            output.setState(response.getState());

            /*
             * set the output's uniqueid
             */

            if (uniqueId != null)
            {
               switch (uidType)
               {
                  case STRING:
                     output.setUniqueId((String) uniqueId);
                     break;
                  case INTEGER:
                     output.setUniqueId((Integer) uniqueId);
                     break;
                  case LONG:
                     output.setUniqueId((Long) uniqueId);
                     break;
               }
            }

            /*
             * convert the response "payload" to the Output
             * The PTK Intenal Response has different data based on the operation
             *
             * READ and SEARCH
             * The Response contains a LinkedList of Results.  Each Result
             * gets added, as an Element, to an ordered list in the Output
             *
             * CREATE, UPDATE, DELETE
             * Do not return any data.  Only the Properties (metadata) are
             * copied over the Output, if any exists
             */

            /*
             * Perform operation specific tasks: when the results come back from the context
             */

            if (response.getPropertiesSize() > 0)
            {
               output.setProperties(response.getProperties());
            }

            /*
             * There may be Attributes that have Functions that need to run before
             * the output is returned
             */

            if (response.getResultsSize() > 0)
            {
               this.postprocessComponents(operation, response.getResults(), fwMap);
               this.setOutputResults(operation, output, response.getResults());
            }
         }
      }

      return output;
   }

   /**
    * @param oper
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public List<String> getAvailableAttributes(final Operation oper)
   //----------------------------------------------------------------
   {
      List<String> list = null;
      Map<Operation, OperationsIF> operations = null;

      operations = _context.getService().getOperations();

      if (operations.containsKey(oper))
      {
         list = _context.getService().getOperAttr(oper).getAttributesNames();

         if (list == null)
         {
            list = new LinkedList<String>();
         }
      }

      return list;
   }

   /**
    * @param operation
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public List<String> getRequiredAttributes(final Operation operation)
   //----------------------------------------------------------------
   {
      String name = null;
      List<String> list = null;
      Map<String, AttrIF> map = null;
      Iterator<String> iter = null;
      AttrIF attr = null;

      list = _context.getService().getOperAttr(operation).getAttributesNames();

      if (list != null)
      {
         map = _context.getService().getOperAttr(operation).getAttributes();
         iter = list.iterator();
         while (iter.hasNext())
         {
            name = iter.next();
            attr = map.get(name);
            if (!attr.isRequired())
            {
               list.remove(name);
            }
         }
      }
      else
      {
         list = new LinkedList<String>();
      }

      return list;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   /**
    * @return
    */
   //----------------------------------------------------------------
   protected ContextIF getContext()
   //----------------------------------------------------------------
   {
      return _context;
   }

   //----------------------------------------------------------------
   private void validateInput(final Input input, final Operation oper) throws ProvisionException
   //----------------------------------------------------------------
   {
      boolean bInvalid = false;
      Object inputValue = null;
      Long longVal = null;
      Long[] longArray = null;
      Integer intVal = null;
      Integer[] intArray = null;
      String METHOD_NAME = CLASS_NAME + ":validateInput(): ";
      String strVal = null;
      String msg = null;
      String attrName = null;
      String fwKeyName = null;
      String[] inputAttrNames = null;
      String[] attrValues = null;
      StringBuilder buf = new StringBuilder();
      Map<String, AttrIF> associationMap = null;
      AttributeIF inputAttr = null;
      AttrIF associationAttr = null;
      DataType inputType = null;
      DataType assocType = null;

      if (input.getAttributesSize() > 0)
      {
         inputAttrNames = input.getAttributeNames();
         associationMap = _context.getService().getAssociation(oper).getAttributes();

         /*
          * Check all the Input Attributes, See if they apply to the operation
          */

         for (int i = 0; i < inputAttrNames.length; i++)
         {
            attrName = inputAttrNames[i];
            if (associationMap.containsKey(attrName))
            {
               inputAttr = input.getAttribute(attrName);
               inputType = inputAttr.getType();
               inputValue = inputAttr.getValue();

               associationAttr = associationMap.get(attrName);
               assocType = associationAttr.getType();

               /*
                * Check the association Type against the input Type
                *
                * If the types do not match ...
                *    If Operation is READ or SEARCH, then set the type
                *    Else throw an Exception
                */

               if (inputType != assocType)
               {
                  if (oper == Operation.READ || oper == Operation.SEARCH)
                  {
                     inputAttr.setType(assocType);
                  }
                  else
                  {
                     /*
                      * Try to convert the type:
                      * - if Association type is INTEGER and Input type is STRING
                      *   try Integer.parseInt(String)
                      * - if Association type is LONG and Input type is STRING
                      *   try Long.parseLong(String)
                      * If there's a parse exception, call handleError()
                      *
                      * If the attribute is changed ... it MUST be re-added to the
                      * input, it was a copy
                      */

                     if (assocType == DataType.INTEGER && inputType == DataType.STRING)
                     {
                        intVal = null;
                        if (inputValue == null)
                        {
                           inputAttr.setValue(intVal);
                        }
                        else if (inputAttr.isMultivalued())
                        {
                           attrValues = (String[]) inputValue;
                           intArray = new Integer[attrValues.length];
                           for (int j = 0; j < attrValues.length; j++)
                           {
                              if (attrValues[j] == null || attrValues[j].length() < 1)
                              {
                                 intArray[j] = intVal;
                              }
                              else
                              {
                                 try
                                 {
                                    intArray[j] = Integer.parseInt(attrValues[j]);
                                 }
                                 catch (NumberFormatException ex)
                                 {
                                    msg = "Attribute '" + attrName + "' is defined as an Integer, "
                                       + "could not convert String '" + attrValues[j]
                                       + "' to an Integer.";
                                    this.setState(State.INVALID);
                                    this.setStatus(METHOD_NAME + msg);
                                    this.setError(true);
                                    this.handleError(METHOD_NAME + msg);
                                 }
                              }
                           }
                           inputAttr.setValue(intArray);
                        }
                        else
                        {
                           strVal = (String) inputValue;
                           if (strVal == null || strVal.length() < 1)
                           {
                              inputAttr.setValue(intVal);
                           }
                           else
                           {
                              try
                              {
                                 inputAttr.setValue(Integer.parseInt(strVal));
                              }
                              catch (NumberFormatException ex)
                              {
                                 msg = "Attribute '" + attrName + "' is defined as an Integer, "
                                    + "could not convert String '" + inputAttr.getValueAsString()
                                    + "' to an Integer.";
                                 this.setState(State.INVALID);
                                 this.setStatus(METHOD_NAME + msg);
                                 this.setError(true);
                                 this.handleError(METHOD_NAME + msg);
                              }
                           }
                        }
                        inputAttr.setType(DataType.INTEGER);
                        input.addAttribute(inputAttr);
                     }
                     else if (assocType == DataType.LONG && inputType == DataType.STRING)
                     {
                        longVal = null;
                        if (inputValue == null)
                        {
                           inputAttr.setValue(longVal);
                        }
                        else if (inputAttr.isMultivalued())
                        {
                           attrValues = (String[]) inputAttr.getValue();
                           longArray = new Long[attrValues.length];
                           for (int j = 0; j < attrValues.length; j++)
                           {
                              if (attrValues[j] == null || attrValues[j].length() < 1)
                              {
                                 longArray[j] = longVal;
                              }
                              else
                              {
                                 try
                                 {
                                    longArray[j] = Long.parseLong(attrValues[j]);
                                 }
                                 catch (NumberFormatException ex)
                                 {
                                    msg = "Attribute '" + attrName + "' is defined as a Long, "
                                       + "could not convert String '" + attrValues[j]
                                       + "' to a Long.";
                                    this.setState(State.INVALID);
                                    this.setStatus(METHOD_NAME + msg);
                                    this.setError(true);
                                    this.handleError(METHOD_NAME + msg);
                                 }
                              }
                           }
                           inputAttr.setValue(longArray);
                        }
                        else
                        {
                           strVal = (String) inputValue;
                           if (strVal == null || strVal.length() < 1)
                           {
                              inputAttr.setValue(longVal);
                           }
                           else
                           {
                              try
                              {
                                 inputAttr.setValue(Long.parseLong(strVal));
                              }
                              catch (NumberFormatException ex)
                              {
                                 msg = "Attribute '" + attrName + "' is defined as a Long, "
                                    + "could not convert String '" + inputAttr.getValueAsString()
                                    + "' to a Long.";
                                 this.setState(State.INVALID);
                                 this.setStatus(METHOD_NAME + msg);
                                 this.setError(true);
                                 this.handleError(METHOD_NAME + msg);
                              }
                           }
                        }
                        inputAttr.setType(DataType.LONG);
                        input.addAttribute(inputAttr);
                     }
                     else
                     {
                        msg = "Types do not match, Input="
                           + inputAttr.getTypeAsString()
                           + ", Definition=" + associationAttr.getType();
                        this.setState(State.INVALID);
                        this.setStatus(METHOD_NAME + msg);
                        this.setError(true);
                        this.handleError(METHOD_NAME + msg);
                     }
                  }
               }


               /*
                * If the Association/Definition Attribute is "virtual"
                * mark this one also virtual
                */

               if (associationAttr != null)
               {
                  inputAttr.setVirtual(associationAttr.isVirtual());
               }
               else
               {
                  msg = this.getConfiguration().getMessage("subject.association.attribute.notexist")
                     + ": Association='"
                     + (_context.getService().getAssociation(oper).getUniqueId() != null ? _context.getService().getAssociation(oper).getUniqueId().toString() : "(null)")
                     + "', Attribute='" + attrName + "'";
                  this.setState(State.INVALID);
                  this.setStatus(METHOD_NAME + msg);
                  this.setError(true);
                  this.handleError(METHOD_NAME + msg);
               }
            }
            else
            {
               /*
                * Allow the "key" if it's related attribute is in the Input
                * This attribute may be needed to generate a uniqueid
                * it will be removed later
                */

               fwKeyName = _context.getService().getKey(oper);
               if (!inputAttrNames[i].equalsIgnoreCase(fwKeyName))
               {
                  /*
                   * Check property to see invalid attributes should be removed.
                   */

                  if (_removeInvalidAttr)
                  {
                     input.removeAttribute(attrName);
                     msg = this.getConfiguration().getMessage("subject.exception.invalidinput")
                        + ": ["
                        + (_context.getUniqueId() != null ? _context.getUniqueId().toString() : "(null)")
                        + "]"
                        + " removing attribute '" + attrName + "'";
                     this.getConfiguration().logWarning(msg);
                  }
                  else
                  {
                     buf.append("'").append(inputAttrNames[i]).append("' not allowed, ");
                     bInvalid = true;
                  }
               }
            }
         }
         if (bInvalid)
         {
            msg = this.getConfiguration().getMessage("subject.exception.invalidinput")
               + ": ["
               + (_context.getUniqueId() != null ? _context.getUniqueId().toString() : "(null)")
               + "] "
               + buf.toString();
            this.setState(State.INVALID);
            this.setStatus(msg);
            this.setError(true);
            this.handleError(METHOD_NAME + msg);
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private Query setServiceNames(final Operation operation, final Query inQuery) throws ConfigurationException
   //----------------------------------------------------------------
   {
      ServiceIF service = null;
      Map<String, String> fw2srvc = null;
      Query outQuery = null;

      if (inQuery != null)
      {
         service = _context.getService();
         if (service != null)
         {
            fw2srvc = service.getFw2SrvcNames(operation);
            if (fw2srvc != null)
            {
               outQuery = this.updateQuery(inQuery, fw2srvc);
            }
         }
      }

      return outQuery;
   }

   //----------------------------------------------------------------
   private void setServiceNames(final Operation operation, final Map<String, AttrIF> mapAttrs) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":setServiceNames(): ";
      Map<String, AttrIF> assocAttrs = null;
      Iterator<String> iter = null;
      ServiceIF service = null;
      AttrIF attr = null;
      AttrIF assocAttr = null;
      String nameOfSrvc = null;
      String mapKey = null;
      String fwName = null;
      String srvcName = null;

      if (mapAttrs != null)
      {
         service = _context.getService();
         if (service != null)
         {
            assocAttrs = _context.getService().getAssociation(operation).getAttributes();
            if (assocAttrs != null)
            {
               iter = mapAttrs.keySet().iterator();
               while (iter.hasNext())
               {
                  mapKey = iter.next();
                  attr = mapAttrs.get(mapKey);
                  if (attr != null)
                  {
                     fwName = null;
                     srvcName = null;
                     fwName = attr.getFrameworkName();
                     if (fwName != null)
                     {
                        assocAttr = assocAttrs.get(fwName);
                        if (assocAttr != null)
                        {
                           srvcName = assocAttr.getServiceName();
                        }
                        if (srvcName == null)
                        {
                           srvcName = fwName;
                        }
                        attr.setServiceName(srvcName);
                     }
                  }
                  else
                  {
                     throw new ConfigurationException(METHOD_NAME
                        + this.getConfiguration().getMessage("subject.attribute.null")
                        + ": " + mapKey);
                  }
               }
            }
            else
            {
               throw new ConfigurationException(METHOD_NAME
                  + this.getConfiguration().getMessage("subject.service.nullantmap")
                  + ": Framework to Service");
            }
         }
         else
         {
            throw new ConfigurationException(METHOD_NAME
               + this.getConfiguration().getMessage("subject.service.null") + ": "
               + nameOfSrvc);
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private void postprocessComponents(final Operation operation,
      final List<ComponentIF> listComps, final Map<String, AttrIF> fwMap) throws ProvisionException
   //----------------------------------------------------------------
   {
      Map<String, AttrIF> mapAttrs = null;
      List<AttrIF> removeList = null;
      Collection<AttrIF> collAttrs = null;
      Iterator<AttrIF> iterAttrs = null;
      Iterator<ComponentIF> iterComp = null;
      ComponentIF component = null;
      AttrIF attr = null;
      AttrIF removeArray[] = null;

      /*
       * Get the Attributes that should be removed from the results
       * These were most likely programmatically added by a Function
       * if Category = FRAMEWORK
       */

      if (fwMap != null)
      {
         removeList = new LinkedList<AttrIF>();
         collAttrs = fwMap.values();
         iterAttrs = collAttrs.iterator();
         while (iterAttrs.hasNext())
         {
            attr = iterAttrs.next();
            if (attr.getCategory() == AttrCategory.FRAMEWORK)
            {
               removeList.add(attr);
            }
         }
         removeArray = removeList.toArray(new BasicAttr[0]);
      }

      /*
       * Iterate through all of the Component objects that are in the List
       * call ToFramework for each one to post-process the attributes
       */

      if (listComps != null)
      {
         iterComp = listComps.iterator();
         if (iterComp != null)
         {
            while (iterComp.hasNext())
            {
               mapAttrs = null;
               component = iterComp.next();

               /*
                * Get the "Map" of Attributes.
                * REMEMBER: Component.getAttributes() RETURNS a "copy"
                * if it's modified, it needs to be "put back"
                */

               mapAttrs = component.getAttributes();
               if (mapAttrs != null)
               {
                  this.evaluateFunctions(operation, TaskMode.TOFRAMEWORK, mapAttrs, null);

                  if (removeArray != null && removeArray.length > 0)
                  {
                     /*
                      * Remove the "FRAMEWORK" inserted Attributes
                      */

                     for (int i = 0; i < removeArray.length; i++)
                     {
                        if (mapAttrs.containsKey(removeArray[i].getName()))
                        {
                           mapAttrs.remove(removeArray[i].getName());
                        }
                     }
                  }

                  component.setAttributes(mapAttrs);
               }
            }
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private void setUniqueId(final Input input, final Operation oper, final Map<String, AttrIF> commonMap) throws ConfigurationException
   //----------------------------------------------------------------
   {
      String fwKeyName = null;
      Object inputUid = null;
      Object newUid = null;
      AttrIF attr = null;

      /*
       * Get the uniqueId from the input.
       * If it is null, attempt to derive it by reading the
       * attribute that is named from the Definition key
       */

      fwKeyName = _context.getService().getKey(oper);

      if (fwKeyName != null && fwKeyName.length() > 0)
      {
         inputUid = input.getUniqueId();
         if (inputUid == null)
         {
            /*
             * There's no explicitly set "uniqueId"
             * Look for an attribute name that matches the defined "key"
             * in the Common Map first (which has attributes set by the user)
             * If not found, then look in Definition Map (from the Context)
             * If an attribute is found, with the same name, use it's value
             * to set the "uniqueId".
             */
            attr = commonMap.get(fwKeyName);
            if (attr != null)
            {
               newUid = attr.getValue();
               if (newUid != null)
               {
                  switch (attr.getType())
                  {
                     case STRING:
                        input.setUniqueId((String) newUid);
                        break;
                     case INTEGER:
                        input.setUniqueId((Integer) newUid);
                        break;
                     case LONG:
                        input.setUniqueId((Long) newUid);
                        break;
                  }
               }
            }
         }
      }

      /*
       * Look for an attribute that has the same name as the defined "key"
       * If found, then remove the attribute from the Input, it's not needed
       * It may have been put there by a Function. It was used (above) to
       * implicitly set the uniqueId
       */

      if (commonMap.containsKey(fwKeyName))
      {
         commonMap.remove(fwKeyName);
      }

      return;
   }

   //----------------------------------------------------------------
   private Map<String, AttributeIF> getApiAttributes(final Operation oper, final Map<String, AttrIF> commonMap)
   //----------------------------------------------------------------
   {
      boolean isMultivalued = false;
      Map<String, AttributeIF> apiMap = null;
      Map<String, AttrIF> operMap = null;
      Iterator<String> iterStr = null;
      String name = null;
      AttrIF commonAttr = null;
      AttributeIF apiAttr = null;

      operMap = _context.getService().getOperAttr(oper).getAttributes();

      /*
       * Use the common Map of AttrIF objects to build a new api Map of
       * Attribute objects.  Copy over only the data that is needed.
       * Check each attribute, ensure that it was allowed as part of the
       * operation's attributes
       */

      if (commonMap != null)
      {
         apiMap = new HashMap<String, AttributeIF>();
         iterStr = commonMap.keySet().iterator();
         while (iterStr.hasNext())
         {
            isMultivalued = false;
            name = null;
            commonAttr = null;
            apiAttr = null;
            name = iterStr.next();
            if (name != null && operMap.containsKey(name))
            {
               commonAttr = commonMap.get(name);
               if (commonAttr != null)
               {
                  isMultivalued = commonAttr.isMultivalued();
                  apiAttr = new Attribute(name);

                  switch (commonAttr.getType())
                  {
                     case BOOLEAN:
                        if (isMultivalued)
                        {
                           apiAttr.setValue((Boolean[]) commonAttr.getValue());
                        }
                        else
                        {
                           apiAttr.setValue((Boolean) commonAttr.getValue());
                        }
                        break;
                     case INTEGER:
                        if (isMultivalued)
                        {
                           apiAttr.setValue((Integer[]) commonAttr.getValue());
                        }
                        else
                        {
                           apiAttr.setValue((Integer) commonAttr.getValue());
                        }
                        break;
                     case LONG:
                        if (isMultivalued)
                        {
                           apiAttr.setValue((Long[]) commonAttr.getValue());
                        }
                        else
                        {
                           apiAttr.setValue((Long) commonAttr.getValue());
                        }
                        break;
                     case STRING:
                        if (isMultivalued)
                        {
                           apiAttr.setValue((String[]) commonAttr.getValue());
                        }
                        else
                        {
                           apiAttr.setValue((String) commonAttr.getValue());
                        }
                        break;
                     case OBJECT:
                        if (isMultivalued)
                        {
                           apiAttr.setValue((Object[]) commonAttr.getValue());
                        }
                        else
                        {
                           apiAttr.setValue((Object) commonAttr.getValue());
                        }
                        break;
                     default:
                        break;
                  }

                  apiAttr.setRequired(commonAttr.isRequired());
                  apiAttr.setEncrypted(commonAttr.isEncrypted());
                  apiAttr.setReadOnly(commonAttr.isReadOnly());

                  apiMap.put(name, apiAttr);
               }
            }
         }
      }
      return apiMap;
   }

   //----------------------------------------------------------------
   private Map<String, AttrIF> getFwAttributes(final Map<String, AttributeIF> userMap, final Operation operation)
   //----------------------------------------------------------------
   {
      boolean allowMultivalue = false;
      boolean isMultivalued = false;
      boolean isRequired = false;
      boolean isReadOnly = false;
      boolean isEncrypted = false;
      boolean isVirtual = false;
      int valueLen = 0;
      Object value = null;
      String fwKeyName = null;
      String name = null;
      Properties props = null;
      Collection<AttributeIF> userAttributes = null;
      Map<String, AttrIF> fwMap = null;
      Map<String, AttrIF> operMap = null;
      Map<String, AttrIF> defMap = null;
      Iterator<AttributeIF> iterAttributes = null;
      AttrIF commonAttr = null;
      AttrIF defAttr = null;
      AttrIF operAttr = null;
      AttributeIF attribute = null;
      DataType type = null;

      fwMap = new HashMap<String, AttrIF>();

      /*
       * Use the api Map of Attribute objects to build a new common Map of
       * AttrIF objects.  The new common Map will also merge in data from
       * the Definition attributes
       */

      fwKeyName = _context.getService().getKey(operation);
      operMap = _context.getService().getOperAttr(operation).getAttributes();

      /*
       * Create the Defintion map using the Operation Map and
       * add the uniqueid Attribute from the Definition.
       */

      if (operMap != null)
      {
         defMap = new HashMap<String, AttrIF>(operMap);
         defAttr = _context.getService().getOperAttr(operation).getAttribute(fwKeyName);

         if (defAttr != null)
         {
            defMap.put(fwKeyName, defAttr);
         }
      }

      /*
       * Process each of the user attributes
       */

      if (userMap != null && defMap != null)
      {
         userAttributes = userMap.values();
         iterAttributes = userAttributes.iterator();
         while (iterAttributes.hasNext())
         {
            operAttr = null;
            attribute = null;
            commonAttr = null;
            valueLen = 0;
            props = null;

            attribute = iterAttributes.next();

            if (attribute != null)
            {
               name = attribute.getName();
               isMultivalued = attribute.isMultivalued();
               value = attribute.getValue();

               /*
                * Add the ones that are in the Operation Attribute Map
                */

               operAttr = defMap.get(name);
               if (operAttr != null)
               {
                  allowMultivalue = operAttr.allowMultivalue();
                  isRequired = operAttr.isRequired();
                  isReadOnly = operAttr.isReadOnly();
                  isEncrypted = operAttr.isEncrypted();
                  isVirtual = operAttr.isVirtual();
                  type = operAttr.getType();
                  props = operAttr.getProperties();
               }
               else
               {
                  allowMultivalue = false;
                  isRequired = false;
                  isReadOnly = false;
                  isEncrypted = false;
                  isVirtual = false;
                  type = DataType.STRING; // default
               }

               /*
                * Check to see if the user (input) attribute is "flaged" as encrpted
                */
               
               if ( isEncrypted == false)
               {
                  isEncrypted = attribute.isEncrypted();
               }

               commonAttr = new BasicAttr(name);
               commonAttr.setRequired(isRequired);
               commonAttr.setAllowMultivalue(allowMultivalue);
               commonAttr.setReadOnly(isReadOnly);
               commonAttr.setEncrypted(isEncrypted);
               commonAttr.setVirtual(isVirtual);

               if (props != null && props.size() > 0)
               {
                  commonAttr.setProperties(props);
               }

               if (operAttr != null)
               {
                  commonAttr.setServiceName(operAttr.getServiceName());
                  commonAttr.setTasks(operation, operAttr.getTasks(operation));
               }

               if (value != null)
               {
                  /*
                   * Set the value differently based on the type / multivalued
                   */
                  switch (type)
                  {
                     case BOOLEAN:
                        if (isMultivalued)
                        {
                           commonAttr.setValue((Boolean[]) value);
                        }
                        else
                        {
                           commonAttr.setValue((Boolean) value);
                        }
                        break;
                     case INTEGER:
                        if (isMultivalued)
                        {
                           commonAttr.setValue((Integer[]) value);
                        }
                        else
                        {
                           commonAttr.setValue((Integer) value);
                        }
                        break;
                     case LONG:
                        if (isMultivalued)
                        {
                           commonAttr.setValue((Long[]) value);
                        }
                        else
                        {
                           commonAttr.setValue((Long) value);
                        }
                        break;
                     case STRING:
                        if (isMultivalued)
                        {
                           commonAttr.setValue((String[]) value);
                           valueLen = ((String[]) value).length;
                        }
                        else
                        {
                           commonAttr.setValue((String) value);
                           valueLen = ((String) value).length();
                        }
                        break;
                     case OBJECT:
                        commonAttr.setValue(value);
                        break;
                     default:
                        break;
                  }
               }
               else
               {
                  /*
                   * Since there is no value, set the "type"
                   */
                  commonAttr.setType(attribute.getType());
               }

               /*
                * Operation specific logic
                */

               switch (operation)
               {
                  case UPDATE:
                  {
                     /*
                      * Check to see if an Attribute should actually be removed
                      * instead of just "clearing" / "zeroing" it's value
                      * "flag" the attribute so that the Service will perform the
                      * correct mechanism to remove the atribute instead of just
                      * updating the value.
                      */
                     if (!isRequired)
                     {
                        if (value != null)
                        {
                           if (valueLen == 0)
                           {
                              commonAttr.setProperty(SubjectIF.PROP_UPDATE_ATTR_REMOVE, "true");
                           }
                        }
                     }
                     break;
                  }
               }

               /*
                * Add the new AttrIF to the Map
                */

               fwMap.put(name, commonAttr);
            }
         }
      }
      return fwMap;
   }

   /****************************************************************/
   /*********************      PRIVATE        **********************/
   /****************************************************************/
   //
   //----------------------------------------------------------------
   private void setOutputResults(final Operation oper, final Output output, final List<ComponentIF> listComps)
   //----------------------------------------------------------------
   {
      ElementIF result = null;
      Iterator<ComponentIF> iterComp = null;
      ComponentIF comp = null;
      Map<String, AttrIF> commonMap = null;
      Map<String, AttributeIF> apiMap = null;

      if (listComps != null)
      {
         iterComp = listComps.iterator();
         if (iterComp != null)
         {
            while (iterComp.hasNext())
            {
               commonMap = null;
               apiMap = null;
               result = null;
               comp = iterComp.next();
               if (comp != null)
               {
                  commonMap = comp.getAttributes();
                  if (commonMap != null)
                  {
                     apiMap = this.getApiAttributes(oper, commonMap);
                     if (apiMap != null)
                     {
                        result = new Element();
                        result.setAttributes(apiMap);
                        result.setDescription(comp.getDescription());
                        result.setError(comp.isError());
                        result.setProperties(comp.getProperties());
                        result.setStatus(comp.getStatus());
                        switch (comp.getUniqueIdType())
                        {
                           case STRING:
                              result.setUniqueId((String) comp.getUniqueId());
                              break;
                           case INTEGER:
                              result.setUniqueId((Integer) comp.getUniqueId());
                              break;
                           case LONG:
                              result.setUniqueId((Long) comp.getUniqueId());
                              break;
                        }
                        result.setKey(_context.getService().getKey(oper));
                        output.addResult(result);
                     }
                  }
               }
            }
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private void updateAttrMapFromQuery(final Map<String, AttrIF> map, final Query query)
   //----------------------------------------------------------------
   {
      String name = null;
      String value = null;
      List<Query> qlist = null;
      Iterator<Query> iter = null;
      Query subq = null;
      AttrIF attr = null;

      if (map != null && query != null)
      {
         name = query.getName();
         if (name != null && name.length() > 0)
         {
            value = query.getValue();
            if (map.containsKey(name))
            {
               if (value != null && value.length() > 0)
               {
                  attr = map.get(name);
                  attr.setValue(value);
               }
            }
            else
            {
               attr = new BasicAttr(name, value);
               if (attr != null)
               {
                  map.put(name, attr);
               }
            }
         }

         qlist = query.getQueryList();
         if (qlist != null)
         {
            iter = qlist.iterator();
            while (iter.hasNext())
            {
               subq = iter.next();
               if (subq != null)
               {
                  this.updateAttrMapFromQuery(map, subq);
               }
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void updateQueryFromAttrMap(final Map<String, AttrIF> map, final Query query)
   //----------------------------------------------------------------
   {
      String name = null;
      String value = null;
      List<Query> qlist = null;
      Iterator<Query> iter = null;
      Query subq = null;
      AttrIF attr = null;

      if (map != null && query != null)
      {
         name = query.getName();
         if (name != null && name.length() > 0)
         {
            if (map.containsKey(name))
            {
               attr = map.get(name);
               if (attr != null)
               {
                  value = attr.getValueAsString();
                  if (value != null && value.length() > 0)
                  {
                     query.setValue(value);
                  }
               }
            }
         }

         qlist = query.getQueryList();
         if (qlist != null)
         {
            iter = qlist.iterator();
            while (iter.hasNext())
            {
               subq = iter.next();
               if (subq != null)
               {
                  this.updateQueryFromAttrMap(map, subq);
               }
            }
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private Query updateQuery(final Query inQuery, final Map<String, String> map)
   //----------------------------------------------------------------
   {
      String fwName = null;
      String srvcName = null;
      String value = null;
      List<Query> qList = null;
      Iterator<Query> iter = null;
      Query outQuery = null;
      Query subq = null;

      if (inQuery != null)
      {
         outQuery = new Query();
         outQuery.setType(inQuery.getType());

         fwName = inQuery.getName();
         if (fwName != null && fwName.length() > 0)
         {
            outQuery.setName(fwName);
            srvcName = map.get(fwName);
            if (srvcName != null && srvcName.length() > 0)
            {
               outQuery.setServiceName(srvcName);
            }
            else
            {
               outQuery.setServiceName(fwName);
            }
         }

         value = inQuery.getValue();
         if (value != null && value.length() > 0)
         {
            outQuery.setValue(value);
         }

         qList = inQuery.getQueryList();
         if (qList != null && !qList.isEmpty())
         {
            iter = qList.iterator();
            while (iter.hasNext())
            {
               subq = this.updateQuery(iter.next(), map);
               outQuery.addQuery(subq);
            }
         }
      }

      return outQuery;
   }

   //----------------------------------------------------------------
   private void evaluateFunctions(final Operation operation, final TaskMode execMode,
      final Map<String, AttrIF> mapUserAttrs, final Query query) throws ProvisionException
   //----------------------------------------------------------------
   {
      /*
       * Determine when to execute a Function for a given Attribute
       *    1. Look at each Definition Attribute, determine if it's "required"
       *       See if it has any Functions/Tasks
       *    2. Look at the User Input, does it contain the Attribute
       *       Does the Attribute have a value
       *       Is the value not-null
       *          If String, is it's length = 0
       *    3. call the "runFunction()" method to determine if it should be run
       *       if yes, run the Function
       */

      boolean isRequired = false;
      boolean hasAttribute = false;
      boolean hasValue = false;
      boolean useExisting = false;
      int length = 0;
      Object value = null;
      String METHOD_NAME = CLASS_NAME + ":evaluateFunctions(): ";
      String strFwKeyName = null;
      String functionClassName = null;
      String attrName = null;
      String taskName = null;
      Map<String, AttrIF> mapOperAttrs = null;
      Map<String, AttrIF> mapDefAttrs = null;
      Map<String, TaskIF> mapTasks = null;
      Iterator<String> iterDef = null;
      Iterator<String> iterTask = null;
      AttrIF attrKey = null;
      AttrIF defAttr = null;
      AttrIF userAttr = null;
      FunctionIF function = null;
      TaskIF task = null;
      TaskMode taskMode = null;

      /*
       * Get the Map of Attributes that are applicable to the given Operation
       */

      mapOperAttrs = _context.getService().getOperAttr(operation).getAttributes();

      if (mapUserAttrs != null && mapOperAttrs != null)
      {
         /*
          * Make a new Map of Definition Attributes using the Operation Map
          */

         mapDefAttrs = new HashMap<String, AttrIF>(mapOperAttrs);

         /*
          * Add the key (uniqueid) Attribute from the Definition
          */

         strFwKeyName = _context.getService().getKey(operation);
         attrKey = _context.getDefinition().getAttribute(strFwKeyName);

         mapDefAttrs.put(strFwKeyName, attrKey);

         /*
          * Merge Query Attribute (name/value) into the Map of Attributes
          * Note: Only "internal" queries will be processed
          */

         if (query != null && query.isInternal() && operation == Operation.SEARCH)
         {
            this.updateAttrMapFromQuery(mapUserAttrs, query);
         }

         iterDef = mapDefAttrs.keySet().iterator();

         /*
          * Check each Definition (Attribute) for Functions 
          * that apply to the Operation
          */

         while (iterDef.hasNext())
         {
            attrName = iterDef.next();
            defAttr = mapDefAttrs.get(attrName);
            if (defAttr == null)
            {
               this.handleError(METHOD_NAME
                  + ": Definiton Missing Attribute "
                  + ", name=" + attrName);
            }
            isRequired = defAttr.isRequired();
            mapTasks = defAttr.getTasks(operation);

            /*
             * Get the map of Tasks (a.k.a. Functions) for the given Definition
             */

            if (mapTasks != null && !mapTasks.isEmpty())
            {
               /*
                * Does the user Input contain an Attribute that
                * matches the Definition Attribute
                */

               hasAttribute = mapUserAttrs.containsKey(attrName);

               /*
                * Get information about each Task (a.k.a. Function)
                * for the given Definition (Attribute)
                */

               iterTask = mapTasks.keySet().iterator();
               while (iterTask.hasNext())
               {
                  hasValue = false;
                  length = 0;
                  taskName = iterTask.next();
                  task = mapTasks.get(taskName);
                  useExisting = task.useExisting();
                  taskMode = task.getOperationMode(operation);

                  if (hasAttribute)
                  {
                     userAttr = mapUserAttrs.get(attrName);
                     value = userAttr.getValue();
                     if (value != null)
                     {
                        hasValue = true;
                        switch (userAttr.getType())
                        {
                           case STRING:
                              if (userAttr.isMultivalued())
                              {
                                 length = ((String[]) value).length;
                              }
                              else
                              {
                                 length = ((String) value).length();
                              }
                              break;
                        }
                     }
                  }

                  /*
                   * Process the Task (Function) that has the same Mode
                   */

                  if (this.runFunction(operation, execMode, taskMode, isRequired, useExisting, hasAttribute, hasValue, length))
                  {
                     this.evaluateFunctionArguments(operation, execMode, task, mapUserAttrs);
                     functionClassName = task.getFunctionClassname();
                     if (functionClassName != null && functionClassName.length() > 0)
                     {
                        /*
                         * Get the Function from the Configuration cache
                         * run the "execute" method
                         */

                        function = this.getConfiguration().getFunction(functionClassName);
                        if (function != null)
                        {
                           try
                           {
                              function.execute(_context, attrName, execMode, operation, task.getArguments(), mapUserAttrs);
                           }
                           catch (FunctionException ex)
                           {
                              this.handleError(METHOD_NAME
                                 + ": FunctionException " + ex.getMessage()
                                 + ", name=" + attrName
                                 + ", classname=" + functionClassName);
                           }
                        }
                        else
                        {
                           this.handleError(METHOD_NAME
                              + ": Function '" + functionClassName + "' is null for '"
                              + (task.getUniqueId() != null ? task.getUniqueId().toString() : "(null)")
                              + "' in '" + defAttr.getName() + "'");
                        }
                     }
                     else
                     {
                        this.handleError(METHOD_NAME
                           + ": Function classname is null for '"
                           + (task.getUniqueId() != null ? task.getUniqueId().toString() : "(null)")
                           + "' in '" + defAttr.getName() + "'");
                     }
                  }
               }
            } // if (mapTasks != null && !mapTasks.isEmpty())
         } // while (iterDef.hasNext())

         /*
          * Update the Query value with the "updated" User Attributes
          */

         if (query != null && query.isInternal() && operation == Operation.SEARCH)
         {
            this.updateQueryFromAttrMap(mapUserAttrs, query);
         }

      } // if (mapUserAttrs != null && mapOperAttrs != null)
      return;
   }

   //----------------------------------------------------------------
   private boolean runFunction(final Operation operation, final TaskMode execMode,
      final TaskMode taskMode, final boolean isRequired, final boolean useExisting,
      final boolean hasAttribute, final boolean hasValue, final int length)
   //----------------------------------------------------------------
   {
      /*
       * Determine if the Task (Function) should be run, based on the data
       * Return "true" to indicate that is should be run
       *
       * CREATE Operation
       *
       * - Definition "required" argument is "true"
       *    - User Input has Attribute
       *       - Attribute has Value
       *          - Length > 0
       *             - Function "useexisting" is "false"
       *               - RUN
       *          - Length = 0
       *            - RUN
       *       - Attribute does not have Value
       *         - RUN
       *    - User Input does not have Attribute
       *      - RUN
       * - Definition "required" argument is "false"
       *    - User Input has Attribute
       *       - Function "useexisting" is "false"
       *         - RUN
       *
       * UPDATE Operation
       *
       * - User Input has Attribute
       *   - Attribute has Value
       *     - Function "useexistng" is "false"
       *       - RUN
       *   - Attribute does not have a value
       *     - RUN
       *
       * READ / SEARCH Operation
       *
       * - User Input has Attribute
       *   - Function "useexisting is "false"
       *     - RUN
       *
       * Final check:
       *    If the "taskMode" is NOT EQUAL to "AUTOMATIC" then
       *    compare it to the "execMode".
       *    - If "taskMode" is EQUAL to "execMode" or BOTH
       *      - RUN
       */

      boolean runIt = false;

      switch (operation)
      {
         case CREATE:
            if (isRequired)
            {
               if (hasAttribute)
               {
                  if (hasValue)
                  {
                     if (length > 0)
                     {
                        if (!useExisting)
                        {
                           runIt = true;
                        }
                     }
                     else
                     {
                        runIt = true;
                     }
                  }
                  else
                  {
                     runIt = true;
                  }
               }
               else
               {
                  runIt = true;
               }
            }
            else // NOT REQUIRED
            {
               if (hasAttribute)
               {
                  if (hasValue)
                  {
                     if (length > 0)
                     {
                        if (!useExisting)
                        {
                           runIt = true;
                        }
                     }
                     else
                     {
                        runIt = true;
                     }
                  }
                  else
                  {
                     if (!useExisting)
                     {
                        runIt = true;
                     }
                  }
               }
            }
            break;
         case UPDATE:
            if (hasAttribute)
            {
               if (hasValue)
               {
                  if (!useExisting)
                  {
                     runIt = true;
                  }
               }
               else
               {
                  runIt = true;
               }
            }
            break;
         case READ:
         case SEARCH:
            if (hasAttribute)
            {
               runIt = true;
            }
            break;
      }

      if (taskMode != TaskMode.AUTOMATIC && taskMode != TaskMode.BOTH)
      {
         if (taskMode != execMode)
         {
            runIt = false;
         }
      }

      return runIt;
   }

   //----------------------------------------------------------------
   private void evaluateFunctionArguments(final Operation operation, final TaskMode mode,
      final TaskIF task, final Map<String, AttrIF> mapUserAttrs)
   //----------------------------------------------------------------
   {
      /*
       * For ALL of the Function/Task's arguments, that are of
       * type "ATTRIBUTE", make sure that ALL of those
       * have been provided by the user.
       */

      String attrName = null;
      List<ArgumentIF> arguments = null;
      Iterator<ArgumentIF> iterArg = null;
      ArgumentIF argument = null;
      ArgumentType argType = null;
      AttrIF attr = null;

      arguments = task.getArguments();

      if (arguments != null && !arguments.isEmpty())
      {
         iterArg = arguments.iterator();
         while (iterArg.hasNext())
         {
            argument = iterArg.next();
            argType = argument.getType();
            if (argType == ArgumentType.ATTRIBUTE)
            {
               attrName = argument.getValue();
               if (attrName != null && attrName.length() > 0)
               {
                  if (!mapUserAttrs.containsKey(attrName))
                  {
                     /*
                      * The Function/Task depends on an Attribute which
                      * does not exist in the User's Input
                      *
                      * Create a new Attribute, no value, Category=FRAMEWORK,
                      */

                     attr = new BasicAttr(attrName);
                     attr.setCategory(AttrCategory.FRAMEWORK);
                     mapUserAttrs.put(attrName, attr);
                  }
               }
            }
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private void checkRequiredAttributes(final Input input, final Operation operation)
   //----------------------------------------------------------------
   {
      /*
       * This method will get the Attributes that are specified for a given
       * operation.  For each one, get the Definition Attribute and determine
       * if it is flaged as "required".  If it is required, it will automatically
       * be added it to the Input, if the operation is CREATE.
       */

      boolean bRequired = false;
      String strFwKeyName = null;
      String attrName = null;
      Map<String, AttrIF> defMap = null;
      Set<String> inputSet = null;
      Iterator<String> defIter = null;
      Attribute attribute = null;
      AttrIF attrKey = null;
      AttrIF defAttr = null;

      /*
       * Add the key (uniqueid) Attribute from the Definition
       */

      strFwKeyName = _context.getService().getKey(operation);
      attrKey = _context.getDefinition().getAttribute(strFwKeyName);

      defMap = _context.getService().getOperAttr(operation).getAttributes();
      defMap.put(strFwKeyName, attrKey);

      defIter = defMap.keySet().iterator();
      inputSet = input.getAttributes().keySet();

      /*
       * Check all the "required" attributes (from the Definition)
       */

      while (defIter.hasNext())
      {
         attrName = defIter.next();
         defAttr = defMap.get(attrName);
         if (defAttr != null)
         {
            bRequired = defAttr.isRequired();
            if (bRequired)
            {
               switch (operation)
               {
                  case CREATE:
                     /*
                      * If the "attribute" is not part of the Input, add it
                      */
                     if (!inputSet.contains(attrName))
                     {
                        attribute = new Attribute(attrName);
                        input.addAttribute(attribute);
                     }
                     break;
                  default:
                     break;
               }
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void checkRequiredAttributes(final Map<String, AttrIF> map, final Operation operation) throws ProvisionException
   //----------------------------------------------------------------
   {
      /*
       * This method will get the Attributes that are specified for a given
       * operation.  For each one, get the Definition Attribute and determine
       * if it is flaged as "required".  
       *
       *
       * CREATE and UPDATE: 
       * The Attribute MUST (for CREATE) be in the map
       * Check the value, should not be null or length == 0
       */

      boolean bRequired = false;
      Object obj = null;
      String METHOD_NAME = CLASS_NAME + ":checkRequiredAttributes(): ";
      String key = null;
      String name = null;
      String value = null;
      Map<String, AttrIF> defAttrs = null;
      Set<String> mapSet = null;
      Iterator<String> defIter = null;
      AttrIF defAttr = null;
      AttrIF mapAttr = null;

      /*
       * Remove the key (uniqueid) Attribute from the Definition
       * The key is managed by the Component setUniqueId()
       */

      key = _context.getService().getKey(operation);

      defAttrs = _context.getService().getOperAttr(operation).getAttributes();
      if (defAttrs.containsKey(key))
      {
         defAttrs.remove(key);
      }

      defIter = defAttrs.keySet().iterator();

      mapSet = map.keySet();

      /*
       * Check all the "required" attributes (from the Definition)
       */

      while (defIter.hasNext())
      {
         name = defIter.next();
         defAttr = defAttrs.get(name);
         if (defAttr != null)
         {
            bRequired = defAttr.isRequired();
            if (bRequired)
            {
               if (mapSet.contains(name))
               {
                  mapAttr = map.get(name);
                  if (mapAttr != null)
                  {
                     obj = mapAttr.getValue();
                     if (obj != null)
                     {
                        if (mapAttr.getType() == DataType.STRING)
                        {
                           if (mapAttr.isMultivalued())
                           {
                              // assume has at least one non-zero length if multi-valued
                           }
                           else
                           {
                              value = (String) obj;
                              if (value.length() < 1)
                              {
                                 this.handleError(METHOD_NAME + "Required attribute '"
                                    + name + "' has zero length");
                              }
                           }
                        }
                     }
                     else
                     {
                        this.handleError(METHOD_NAME + "Required attribute '"
                           + name + "' has a null value");
                     }
                  }
                  else
                  {
                     this.handleError(METHOD_NAME + "Required attribute '"
                        + name + "' is null");
                  }
               }
               else
               {
                  if (operation == Operation.CREATE)
                  {
                     this.handleError(METHOD_NAME + "Required attribute '"
                        + name + "' was not found in the request.");
                  }
               }
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void addAttrGroupAttributes(final Input input, final Operation operation)
   //----------------------------------------------------------------
   {
      /*
       * This method is typically called for READ and SEARCH Operations
       * when the Input does not contain any Consumer specified Attributes.
       *
       * The AttrGroup Attributes are obtained for the given Operations.
       * All of the Attributes are "added" to the Input object
       *
       * We need to actually copy / add the Attributes from the Service's
       * Operation Attributes because they contain all the
       * "meta data" about the Attribute.  The AttrGroup Attributes are
       * only used to relate a goup of Attributes to Operation.
       */

      String attrName = null;
      Map<String, AttrIF> attrGroupMap = null;
      Map<String, AttrIF> operMap = null;
      Iterator<String> attrGroupIter = null;
      AttrIF attr = null;

      attrGroupMap = _context.getService().getAttrGroup(operation).getAttributes();
      operMap = _context.getService().getOperAttr(operation).getAttributes();

      if (attrGroupMap != null && operMap != null)
      {
         attrGroupIter = attrGroupMap.keySet().iterator();
         while (attrGroupIter.hasNext())
         {
            attrName = attrGroupIter.next();
            attr = operMap.get(attrName);
            if (attr != null)
            {
               input.addAttribute(attr);
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void missingAttributes(final ResponseIF response, final Input input)
   //----------------------------------------------------------------
   {
      /*
       * A Input contains attributes that the Consumer Tier
       * wants returned.  Some services (JNDI) may not return an attribute
       * if is not part of the Subject's record (as either NULL or empty).
       * In this case, we want to "insert" an attribute with a NULL/empty
       * value into the Response.  This is important because there may be
       * a Function that is looking for the attribute.  The Function
       * for a given attribute will not run unless it is returned, even if
       * the "value" is NULL.
       */

      String[] attrArray = null;
      List<ComponentIF> listResults = null;
      Iterator<ComponentIF> iterResults = null;
      ComponentIF comp = null;
      AttrIF attr = null;

      /*
       * Create a String Array of the Input Attribute Names
       * from the original Input
       */

      attrArray = input.getAttributeNames();

      /*
       * Process each of the Response Subjects.
       */

      listResults = response.getResults();
      if (listResults != null && listResults.size() > 0)
      {
         iterResults = listResults.iterator();
         while (iterResults.hasNext())
         {
            comp = iterResults.next();
            /*
             * See if every "request" attribute is in the response
             */
            for (int i = 0; i < attrArray.length; i++)
            {
               attr = null;
               attr = comp.getAttribute(attrArray[i]);
               if (attr == null)
               {
                  attr = new BasicAttr(attrArray[i]);
                  attr.setCategory(AttrCategory.SERVICE);
                  attr.setState(State.NOTEXIST);
                  comp.setAttribute(attrArray[i], attr);
               }
            }
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private void removeEmptyAttributes(final Operation operation, final Map<String, AttrIF> fwAttrs)
   //----------------------------------------------------------------
   {
      /*
       * Remove the attribute if the value is either NULL or zero length (String)
       */

      Object value = null;
      String namesArray[] = null;
      String strArray[] = null;
      Map<String, AttrIF> assocAttrs = null;
      List<String> strList = null;
      AttrIF attr = null;

      if (fwAttrs != null && !fwAttrs.isEmpty())
      {
         namesArray = fwAttrs.keySet().toArray(new String[fwAttrs.keySet().size()]);
         assocAttrs = _context.getService().getAssociation(operation).getAttributes();

         if (namesArray != null && namesArray.length > 0 && assocAttrs != null)
         {
            for (String name : namesArray)
            {
               if (name != null && name.length() > 0)
               {
                  attr = fwAttrs.get(name);
                  if (attr != null)
                  {
                     value = attr.getValue();
                     if (value == null)
                     {
                        /*
                         * Null value, remove the attribute
                         */
                        fwAttrs.remove(name);
                     }
                     else
                     {
                        switch (attr.getType())
                        {
                           case STRING:
                              if (attr.isMultivalued())
                              {
                                 /*
                                  * go through each String in the array
                                  * if a String is not NULL and not empty ...
                                  * add it to a LinkedList
                                  */

                                 strArray = (String[]) value;

                                 strList = new LinkedList<String>();
                                 for (String str : strArray)
                                 {
                                    if (str != null && str.length() > 0)
                                    {
                                       strList.add(str);
                                    }
                                 }

                                 /*
                                  * If the ListList is empty
                                  * remove the attribute
                                  * Else replace the value with a new String Array
                                  * Note: The new Array maybe a sub-set of the values.
                                  */

                                 if (strList.isEmpty())
                                 {
                                    fwAttrs.remove(name);
                                 }
                                 else
                                 {
                                    attr.setValue(strList.toArray(new String[strList.size()]));
                                 }
                              }
                              else
                              {
                                 if (((String) value).length() < 1)
                                 {
                                    /*
                                     * String length is zero, remove the attribute
                                     */
                                    fwAttrs.remove(name);
                                 }
                              }
                              break;
                        }
                     }
                  }
               }
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private void removeInvalidAttributes(final Operation operation, final Map<String, AttrIF> fwAttrs)
   //----------------------------------------------------------------
   {
      /*
       * Look for Attributes that don't exist in the Association
       * The Attribute may have been part of the Input and a Function
       * added/included "real" attributes.  The "requested" attribute
       * may not actually exist.
       *
       * Remove the Attribute if it was defined as "virtual"
       *

       */

      String namesArray[] = null;
      Map<String, AttrIF> assocAttrs = null;
      AttrIF attr = null;

      if (fwAttrs != null && !fwAttrs.isEmpty())
      {
         namesArray = fwAttrs.keySet().toArray(new String[fwAttrs.keySet().size()]);

         assocAttrs = _context.getService().getAssociation(operation).getAttributes();

         if (namesArray != null && namesArray.length > 0 && assocAttrs != null)
         {
            for (String name : namesArray)
            {
               if (name != null && name.length() > 0)
               {
                  if (!assocAttrs.containsKey(name))
                  {
                     fwAttrs.remove(name);
                  }
                  attr = fwAttrs.get(name);
                  if (attr != null)
                  {
                     if (assocAttrs.get(name).isVirtual())
                     {
                        fwAttrs.remove(name);
                     }
                  }
               }
            }
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private void handleError(final String msg) throws ProvisionException
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
}
