/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2008-2010 Sun Microsystems, Inc.
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
package org.openptk.definition.functions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openptk.common.Component;
import org.openptk.common.Operation;

//===================================================================
public class Task extends Component implements TaskIF
//===================================================================
{
   private String _classname = null;
   private boolean _useExisting = false;
   private List<ArgumentIF> _arguments = null;
   private Map<Operation, TaskMode> _operMode = null; // used by these Operations


   /**
    * @param id
    * @param classname
    */
   //----------------------------------------------------------------
   public Task(String id, String classname)
   //----------------------------------------------------------------
   {
      super();

      this.setUniqueId(id);
      _classname = classname;
      _arguments = new LinkedList<ArgumentIF>();
      _operMode = new HashMap<Operation, TaskMode>();

      return;
   }


   /**
    * @param task
    */
   //----------------------------------------------------------------
   public Task(TaskIF task)
   //----------------------------------------------------------------
   {
      super(task);

      _classname = task.getFunctionClassname();
      _useExisting = task.useExisting();
      _operMode = task.getOperationsMode();
      _arguments = task.getArguments();

      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public TaskIF copy()
   //----------------------------------------------------------------
   {
      return new Task(this);
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   public synchronized String getFunctionClassname()
   //----------------------------------------------------------------
   {
      String val = null;

      if (_classname != null)
      {
         val = new String(_classname); // always return a copy
      }

      return val;
   }


   /**
    * @param classname
    */
   //----------------------------------------------------------------
   public synchronized void setFunctionClassname(String classname)
   //----------------------------------------------------------------
   {
      _classname = classname;
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   public boolean useExisting()
   //----------------------------------------------------------------
   {
      return _useExisting;
   }


   /**
    * @param useExisting
    */
   //----------------------------------------------------------------
   public synchronized void setUseExisting(boolean useExisting)
   //----------------------------------------------------------------
   {
      _useExisting = useExisting;
      return;
   }


   /**
    * @param argument
    */
   //----------------------------------------------------------------
   public synchronized void addArgument(ArgumentIF argument)
   //----------------------------------------------------------------
   {
      if (argument != null)
      {
         _arguments.add(argument);
      }
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   public synchronized List<ArgumentIF> getArguments()
   //----------------------------------------------------------------
   {
      List<ArgumentIF> list = null;

      if (_arguments != null)
      {
         list = new LinkedList<ArgumentIF>();
         for (ArgumentIF arg : _arguments)
         {
            list.add(arg.copy());
         }
      }

      return list;
   }


   /**
    * @param oper
    * @return
    */
   //----------------------------------------------------------------
   public boolean hasOperation(Operation oper)
   //----------------------------------------------------------------
   {
      boolean bRet = false;

      if (_operMode.containsKey(oper))
      {
         bRet = true;
      }

      return bRet;
   }


   /**
    * @param str
    * @return
    */
   //----------------------------------------------------------------
   public boolean hasOperation(String str)
   //----------------------------------------------------------------
   {
      boolean bRet = false;
      Operation[] operArray = null;

      operArray = Operation.values();

      for (int i = 0; i < operArray.length; i++)
      {
         if (str.equalsIgnoreCase(operArray[i].toString()))
         {
            bRet = true;
         }
      }

      return bRet;
   }


   /**
    * @param oper
    * @return
    */
   //----------------------------------------------------------------
   public synchronized TaskMode getOperationMode(Operation oper)
   //----------------------------------------------------------------
   {
      TaskMode mode = null;

      if (oper != null)
      {
         if (_operMode.containsKey(oper))
         {
            mode = _operMode.get(oper);
         }
      }

      return mode;
   }


   /**
    * @param oper
    * @param mode
    */
   //----------------------------------------------------------------
   public synchronized void setOperationMode(Operation oper, TaskMode mode)
   //----------------------------------------------------------------
   {
      if (oper != null && mode != null)
      {
         _operMode.put(oper, mode);
      }
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   public synchronized Map<Operation, TaskMode> getOperationsMode()
   //----------------------------------------------------------------
   {
      Map<Operation, TaskMode> map = null;
      Set<Operation> set = null;
      TaskMode mode = null;

      if (_operMode != null)
      {
         map = new HashMap<Operation, TaskMode>();
         set = _operMode.keySet();
         for (Operation oper : set)
         {
            mode = _operMode.get(oper);
            map.put(oper, mode);
         }
      }
      return map;
   }
}
