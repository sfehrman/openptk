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
package org.openptk.common;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openptk.api.Attribute;
import org.openptk.definition.functions.TaskIF;

//===================================================================
public abstract class Attr extends Attribute implements AttrIF
//===================================================================
{
   private String _frameworkName = null;
   private String _serviceName = null;
   private AttrCategory _category = AttrCategory.DEFINITION;
   private Map<String, TaskIF> _tasks = null;
   private Map<Operation, List<String>> _operations = null;

   /**
    * Create a new Attr from an existing Attr
    * All of the information is copied, including the Tasks.
    * @param attr AttrIF instance
    */
   //----------------------------------------------------------------
   public Attr(final AttrIF attr)
   //----------------------------------------------------------------
   {
      super(attr);

      Operation[] operArray = Operation.values();
      Operation oper = null;
      Map<String, TaskIF> map = null;

      _frameworkName = attr.getFrameworkName();
      _serviceName = attr.getServiceName();

      for (int i = 0; i < operArray.length; i++)
      {
         oper = operArray[i];
         map = attr.getTasks(oper);
         this.setTasks(oper, map);
      }

      return;
   }

   /**
    * Creates a new Attr using the provided name.
    *
    * The AttributeType will be set to STRING.
    * The value will be NULL.
    *
    * @param name the Attributes name
    */
   //----------------------------------------------------------------
   public Attr(final String name)
   //----------------------------------------------------------------
   {
      super(name);
      _frameworkName = name;
      return;
   }

   /**
    * Creates a new Attr using the provided name and value.
    *
    * The AttributeType will be set to OBJECT, this can not be multi-valued.
    *
    * @param name the Attrs name
    * @param value the Attrs value, Java Object
    */
   //----------------------------------------------------------------
   public Attr(final String name, final Object value)
   //----------------------------------------------------------------
   {
      super(name, value);
      _frameworkName = name;
      return;
   }

   /**
    * Creates a new Attr using the provided name and value.
    *
    * The AttributeType will be set to OBJECT, this will be multivalued.
    *
    * @param name the Attr's name
    * @param value the Attr's value, Java Object[]
    */
   //----------------------------------------------------------------
   public Attr(final String name, final Object[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      _frameworkName = name;
      return;
   }

   /**
    * Creates a new Attr using the provided name.
    *
    * The AttributeType will be set to BOOLEAN.
    * The value will be set using the provided value.
    *
    * @param  name the Attr's name
    * @param  value the Attr's value [ true | false ]
    */
   //----------------------------------------------------------------
   public Attr(final String name, final boolean value)
   //----------------------------------------------------------------
   {
      super(name, value);
      _frameworkName = name;
      return;
   }

   /**
    * Creates a new Attr using the provided name.
    *
    * The AttributeType will be set to BOOLEAN, this will be multivalued.
    * The value will be set using the provided value.
    *
    * @param  name the Attr's name
    * @param  value the Attr's value, Boolean[]
    */
   //----------------------------------------------------------------
   public Attr(final String name, final boolean[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      _frameworkName = name;
      return;
   }

   /**
    * Creates a new Attr using the provided name.
    *
    * The AttributeType will be set to BOOLEAN.
    * The value will be set using the provided value.
    *
    * @param  name the Attr's name
    * @param  value the Attr's value, Integer [ true | false ]
    */
   //----------------------------------------------------------------
   public Attr(final String name, final Boolean value)
   //----------------------------------------------------------------
   {
      super(name, value);
      _frameworkName = name;
      return;
   }

   /**
    * Creates a new Attr using the provided name.
    *
    * The AttributeType will be set to BOOLEAN, this will be multivalued
    * The value will be set using the provided value.
    *
    * @param  name the Attr's name
    * @param  value the Attr's value, Boolean[]
    */
   //----------------------------------------------------------------
   public Attr(final String name, final Boolean[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      _frameworkName = name;
      return;
   }

   /**
    * Creates a new Attr using the provided name.
    *
    * The AttributeType will be set to INTEGER.
    * The value will be set using the provided value.
    *
    * @param  name the Attr's name
    * @param  value the Attr's value, Integer
    */
   //----------------------------------------------------------------
   public Attr(final String name, final int value)
   //----------------------------------------------------------------
   {
      super(name, value);
      _frameworkName = name;
      return;
   }

   /**
    * Creates a new Attr using the provided name.
    *
    * The AttributeType will be set to INTEGER.
    * The value will be set using the provided value, this will be multivalued
    *
    * @param  name the Attr's name
    * @param  value the Attr's value, Integer[]
    */
   //----------------------------------------------------------------
   public Attr(final String name, final int[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      _frameworkName = name;
      return;
   }

   /**
    * Creates a new Attr using the provided name.
    *
    * The AttributeType will be set to INTEGER.
    * The value will be set using the provided value.
    *
    * @param  name the Attr's name
    * @param  value the Attr's value, Integer
    */
   //----------------------------------------------------------------
   public Attr(final String name, final Integer value)
   //----------------------------------------------------------------
   {
      super(name, value);
      _frameworkName = name;
      return;
   }

   /**
    * Creates a new Attr using the provided name.
    *
    * The AttributeType will be set to INTEGER.
    * The value will be set using the provided value, this will be multivalued
    *
    * @param  name the Attr's name
    * @param  value the Attr's value, Integer[]
    */
   //----------------------------------------------------------------
   public Attr(final String name, final Integer[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      _frameworkName = name;
      return;
   }

   /**
    * Creates a new Attr using the provided name.
    *
    * The AttributeType will be set to INTEGER.
    * The value will be set using the provided value.
    *
    * @param  name the Attr's name
    * @param  value the Attr's value, Integer
    */
   //----------------------------------------------------------------
   public Attr(final String name, final Long value)
   //----------------------------------------------------------------
   {
      super(name, value);
      _frameworkName = name;
      return;
   }

   /**
    * Creates a new Attr using the provided name.
    *
    * The AttributeType will be set to INTEGER.
    * The value will be set using the provided value, this will be multivalued
    *
    * @param  name the Attr's name
    * @param  value the Attr's value, Integer[]
    */
   //----------------------------------------------------------------
   public Attr(final String name, final Long[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      _frameworkName = name;
      return;
   }

   /**
    * Creates a new Attr using the provided name.
    *
    * The AttributeType will be set to STRING.
    * The value will be set using the provided value.
    *
    * @param  name the Attr's name
    * @param  value the Attr's value, String
    */
   //----------------------------------------------------------------
   public Attr(final String name, final String value)
   //----------------------------------------------------------------
   {
      super(name, value);
      _frameworkName = name;
      return;
   }

   /**
    * Creates a new Attr using the provided name.
    *
    * The AttributeType will be set to STRING.
    * The value will be set using the provided value, this will be multivalued
    *
    * @param name the Attr's name
    * @param value the Attr's value, String[]
    */
   //----------------------------------------------------------------
   public Attr(final String name, final String[] value)
   //----------------------------------------------------------------
   {
      super(name, value);
      _frameworkName = name;
      return;
   }

   /**
    * Create a copy of the AttrIF instance.
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public abstract AttrIF copy();
   //----------------------------------------------------------------

   /**
    * Get the Framework name for the attribute.
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getFrameworkName()
   //----------------------------------------------------------------
   {
      String val = null;
      if (_frameworkName != null)
      {
         val = new String(_frameworkName); // always make a copy!
      }
      return val;
   }

   /**
    * Set the attribute category.
    * @param category
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setCategory(final AttrCategory category)
   //----------------------------------------------------------------
   {
      _category = category;
      return;
   }

   /**
    * Get the attribute category.
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized AttrCategory getCategory()
   //----------------------------------------------------------------
   {
      AttrCategory val = null;
      if (_category != null)
      {
         val = _category;
      }
      return val;
   }

   /**
    * Set the Service name.
    * @param name
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setServiceName(final String name)
   //----------------------------------------------------------------
   {
      _serviceName = name;
      return;
   }

   /**
    * Get the Service name.
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String getServiceName()
   //----------------------------------------------------------------
   {
      String val = null;

      if (_serviceName != null)
      {
         val = new String(_serviceName); // always make a copy!
      }

      return val;
   }

   /**
    * Add a task for a specific operation.
    * @param oper
    * @param task
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addTask(final Operation oper, final TaskIF task)
   //----------------------------------------------------------------
   {
      /*
       * Add the Task to the specified Operation
       * Check to see if "this" Attribute's "maps" are initialized.
       * Create a LinkedList for the specified Opertion, if needed.
       * See if the specified Task is in the List for this Operation
       * If not, add the "taskid" to the List for the Operation
       * NOTE: the taskid is the "id" that is set in the XML element
       * If the Task Map for this Attribute does not already contain
       * the specified Task, put it in the map using the taskid.
       *
       * A given Task (mapTask) can be used / referenced by more than
       * one Operation
       */

      int index = -1;
      List<String> listOperations = null;
      String taskId = null;

      if (task != null && task.getUniqueId() != null)
      {
         taskId = task.getUniqueId().toString();

         /*
          * If the "collections" are null ... create them
          */

         if (_operations == null)
         {
            _operations = new HashMap<Operation, List<String>>();
         }

         if (_tasks == null)
         {
            _tasks = new HashMap<String, TaskIF>();
         }

         /*
          * see if there is an existing list of Tasks for the Operation
          */

         listOperations = _operations.get(oper);

         if (listOperations == null)
         {
            listOperations = new LinkedList<String>();
         }

         /*
          * See if the Task is already "assigned" to this Operation
          * A given Task, for an Operation can only occur once in the List
          */

         if (listOperations.contains(taskId))
         {
            /*
             * "replace" the existing task
             */
            index = listOperations.indexOf(taskId);
            if (index >= 0)
            {
               listOperations.remove(index);
               listOperations.add(index, taskId);
            }
         }
         else
         {
            /*
             * Not in list, just add it
             */
            listOperations.add(taskId);
         }

         /*
          * Save the Task in the "Task Map" 
          */

         _tasks.put(taskId, task);

         /*
          * Add/Replace the LinkedList in the Map for the related Operation
          */

         _operations.put(oper, listOperations);
      }
      return;
   }

   /**
    * Set all (map) tasks for a specific operation.
    * @param oper
    * @param tasks
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setTasks(final Operation oper, final Map<String, TaskIF> tasks)
   //----------------------------------------------------------------
   {
      /*
       * Apply/Assign a Map of Tasks to a specific Operation
       * Iterate through the Map of Tasks
       * get the taskid (classname) and the actual Task
       * call the "addTask()" method to assign it to the Operation
       */

      Set<String> set = null;
      TaskIF task = null;

      if (tasks != null)
      {
         set = tasks.keySet();
         for (String s : set)
         {
            task = tasks.get(s);
            this.addTask(oper, task);
         }
      }

      return;
   }

   /**
    * Get all (map) the task for a specific operation.
    * @param oper
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized Map<String, TaskIF> getTasks(final Operation oper)
   //----------------------------------------------------------------
   {
      /*
       * Return a Map that contains all the "ordered" Tasks
       * for a given Operation.
       * Read the List of TaskId's for the specified Operation
       * For each TaskId, get the actual Task from the "map of tasks"
       * note: the Taskid is the full classname
       * Add the Taskid/Task to the Map that will be returned.
       */

      Map<String, TaskIF> map = null;
      List<String> list = null;
      TaskIF task = null;

      if (_operations != null)
      {
         list = _operations.get(oper);
         if (list != null)
         {
            map = new LinkedHashMap<String, TaskIF>();

            for (String str : list)
            {
               task = _tasks.get(str);
               if (task != null)
               {
                  map.put(str, task.copy());
               }
            }
         }
      }

      return map;
   }

   /**
    * Get all (map) the tasks, for all the operations.
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized Map<String, TaskIF> getTasks()
   //----------------------------------------------------------------
   {
      /*
       * Bulk return the Map of Tasks/Functions regardless of what
       * Operation they apply to.
       */
      Map<String, TaskIF> tasks = null;
      Set<String> keys = null;

      if (_tasks != null)
      {
         tasks = new LinkedHashMap<String, TaskIF>();
         keys = _tasks.keySet();
         for (String s : keys)
         {
            tasks.put(s, _tasks.get(s).copy());
         }
      }

      return tasks;
   }

   //
   // PROTECTED 
   //
   /**
    * Set the attributes framework name.
    * @param name
    */
   //----------------------------------------------------------------
   protected synchronized void setFrameworkName(final String name)
   //----------------------------------------------------------------
   {
      _frameworkName = name;
      return;
   }
}
