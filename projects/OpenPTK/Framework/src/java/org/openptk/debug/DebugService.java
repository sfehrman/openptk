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
package org.openptk.debug;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;

import org.openptk.api.Query;
import org.openptk.common.AttrIF;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.logging.Logger;
import org.openptk.spi.ServiceIF;
import org.openptk.spi.operations.OperationsIF;

//===================================================================
public class DebugService extends DebugComponent implements DebugIF
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private Map<String, OperationsIF> _availOperMap = null;
   //----------------------------------------------------------------

   public DebugService()
   //----------------------------------------------------------------
   {
      super();
      _availOperMap = new HashMap<String, OperationsIF>();
      return;
   }

   /**
    * @param obj
    * @param callerId
    */
   //----------------------------------------------------------------
   @Override
   public void logData(final Object obj, final String callerId)
   //----------------------------------------------------------------
   {
      Logger.logInfo(process((ServiceIF) obj, callerId));

      return;
   }

   /**
    * @param obj
    * @param callerId
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public String getData(final Object obj, final String callerId)
   //----------------------------------------------------------------
   {
      return process((ServiceIF) obj, callerId);
   }

   /**
    * @param obj
    * @param callerId
    * @param indent
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public String getData(final Object obj, final String callerId, final String indent)
   //----------------------------------------------------------------
   {
      String tmp = _extIndent;
      String data = null;
      _extIndent = indent;
      data = process((ServiceIF) obj, callerId);
      _extIndent = tmp;
      return data;
   }

   //----------------------------------------------------------------
   private String process(final ServiceIF srvc, final String callerId)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();

      buf.append(_extIndent + DebugIF.INDENT_1 + CLASS_NAME + ", callerId=" + callerId + "\n");
      buf.append(dbg_level_1(srvc));
      buf.append(dbg_level_2(srvc));
      buf.append(dbg_level_3(srvc));
      buf.append(dbg_level_4(srvc));

      /*
       * may add more debug levels in the future
       */

      return buf.toString();
   }

   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   @Override
   protected StringBuffer dbg_level_1(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      int timeout = 0;
      String key = null;
      ServiceIF srvc = null;
      StringBuffer buf = new StringBuffer();
      ComponentIF assoc = null;
      Operation[] operArray = null;
      Properties props = null;

      srvc = (ServiceIF) comp;

      if (srvc != null)
      {
         if (srvc.isDebug())
         {
            buf.append(super.dbg_level_1(comp));

            /*
             * Add level 1 debugging for the ServiceIF HERE
             */

            operArray = Operation.values();


            /*
             * Timeouts
             */

            buf.append(_extIndent + DebugIF.INDENT_1 + "Timeouts (msec): ");
            for (int i = 0; i < operArray.length; i++)
            {
               if (srvc.hasOperation(operArray[i]))
               {
                  timeout = srvc.getTimeout(operArray[i]);
                  buf.append(operArray[i].toString() + "="
                     + Integer.toString(timeout) + " ");
               }
            }
            buf.append("\n");

            /*
             * Keys
             */

            buf.append(_extIndent + DebugIF.INDENT_1 + "Keys: ");
            for (int i = 0; i < operArray.length; i++)
            {
               if (srvc.hasOperation(operArray[i]))
               {
                  key = srvc.getKey(operArray[i]);
                  if (key != null)
                  {
                     buf.append(operArray[i].toString() + "=" + key + " ");
                  }
               }
            }
            buf.append("\n");

            /*
             * Sort Attributes
             */

            buf.append(_extIndent + DebugIF.INDENT_1 + "Sort Attributes: ");
            if (srvc.getSortAttributes() != null)
            {
               buf.append(srvc.getSortAttributes() + "\n");
            }
            else
            {
               buf.append(DebugIF.NULL + "\n");
            }

            /*
             * Association data
             */

            buf.append(_extIndent + DebugIF.INDENT_1 + "Associations:");
            for (int i = 0; i < operArray.length; i++)
            {
               assoc = srvc.getAssociation(operArray[i]);
               buf.append(" " + operArray[i].toString() + "=");
               if (assoc != null)
               {
                  buf.append(
                     (assoc.getUniqueId() != null ? assoc.getUniqueId().toString() : "(null)"));
               }
            }
            buf.append("\n");

            /*
             * Properties
             */

            buf.append(_extIndent + DebugIF.INDENT_1 + "Properties: \n");
            for (int i = 0; i < operArray.length; i++)
            {
               if (srvc.hasOperation(operArray[i]))
               {
                  props = srvc.getOperProps(operArray[i]);
                  if (props != null && !props.isEmpty())
                  {
                     buf.append(_extIndent + DebugIF.INDENT_2
                        + operArray[i].toString() + "\n");
                     buf.append(this.getPropertyData(props));
                  }
               }
            }
         }
         else
         {
            buf.append(_extIndent + DebugIF.INDENT_1 + "NOTICE (DL1): Debug not enabled.\n");
         }
      }
      else
      {
         buf.append(_extIndent + DebugIF.INDENT_1 + "WARNING: Service is null\n");
      }

      return buf;
   }

   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   @Override
   protected StringBuffer dbg_level_2(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      String serviceName = null;
      ServiceIF srvc = null;
      StringBuffer buf = new StringBuffer();
      Map<Operation, OperationsIF> operations = null;
      OperationsIF operation = null;
      Operation[] operArray = null;
      Query query = null;

      srvc = (ServiceIF) comp;

      if (srvc != null)
      {
         if (srvc.isDebug())
         {
            buf.append(super.dbg_level_2(comp));

            /*
             * Add level 2 debugging for the ServiceIF HERE
             */

            operArray = Operation.values();

            /*
             * Queries
             */

            buf.append(_extIndent + DebugIF.INDENT_1 + "Queries:\n");

            for (int i = 0; i < operArray.length; i++)
            {
               if (srvc.hasOperation(operArray[i]))
               {
                  query = srvc.getQuery(operArray[i]);

                  if (query != null)
                  {
                     buf.append(_extIndent + DebugIF.INDENT_2);
                     buf.append(operArray[i].toString() + ": " + query.toXML());
                  }
               }
            }

            /*
             * Populate the "availOperMap" with the "uniqueId" Operations
             * that are being used by the Operation codes.
             */

            buf.append(_extIndent + DebugIF.INDENT_1 + "Operations: ");


            operations = srvc.getOperations();

            if (operations != null)
            {
               buf.append(operations.size() + "\n");

               for (int i = 0; i < operArray.length; i++)
               {
                  // Get each Operation

                  operation = srvc.getOperation(operArray[i]);
                  if (operation != null && operation.getUniqueId() != null)
                  {
                     serviceName = operation.getUniqueId().toString();
                     if (!_availOperMap.containsKey(serviceName))
                     {
                        _availOperMap.put(serviceName, operation);
                     }

                     buf.append(_extIndent + DebugIF.INDENT_2
                        + "[" + operArray[i].toString() + "] "
                        + serviceName + ": " + operation.getClass().getName() + "\n");
                  }
               }
            }
            else
            {
               buf.append(DebugIF.NULL + "\n");
            }
         }
         else
         {
            buf.append(_extIndent + INDENT_1 + "NOTICE (DL2): Debug not enabled.\n");
         }
      }
      else
      {
         buf.append(_extIndent + DebugIF.INDENT_1 + "WARNING: Service is null\n");
      }


      return buf;
   }

   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   @Override
   protected StringBuffer dbg_level_3(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();
      ServiceIF srvc = null;
      OperationsIF operation = null;
      DebugOperations dbgOper = null;

      srvc = (ServiceIF) comp;

      if (srvc != null)
      {
         if (srvc.isDebug())
         {
            buf.append(super.dbg_level_3(comp));

            /*
             * Add level 3 debugging for the ServiceIF HERE
             */

            buf.append(_extIndent + DebugIF.INDENT_1 + "Connections: ");

            if (!this._availOperMap.isEmpty())
            {
               buf.append(_availOperMap.size() + "\n");

               for (String serviceName : _availOperMap.keySet())
               {
                  operation = _availOperMap.get(serviceName);

                  buf.append(_extIndent + DebugIF.INDENT_2 + serviceName + ": ");
                  if (operation != null)
                  {
                     buf.append(operation.getClass().getName() + "\n");

                     dbgOper = new DebugOperations();
                     buf.append(dbgOper.getData(operation, this.CLASS_NAME, DebugIF.INDENT_2));
                  }
                  else
                  {
                     buf.append(DebugIF.NULL + "\n");
                  }
               }
            }
            else
            {
               buf.append(DebugIF.NULL + "\n");
            }
         }
         else
         {
            buf.append(_extIndent + INDENT_1 + "NOTICE (DL3): Debug not enabled.\n");
         }
      }
      else
      {
         buf.append(_extIndent + DebugIF.INDENT_1 + "WARNING: Service is null\n");
      }

      return buf;
   }

   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   @Override
   protected StringBuffer dbg_level_4(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      int iCnt = 0;
      StringBuffer buf = new StringBuffer();
      ServiceIF srvc = null;
      Map<String, AttrIF> attrs = null;
      Map<String, String> mapLeft = null;
      Map<String, String> mapRight = null;
      Operation[] operations = Operation.values();
      ComponentIF attrComp = null;
      AttrIF attr = null;
      DebugAttr dbgAttr = null;

      srvc = (ServiceIF) comp;

      if (srvc != null)
      {
         if (srvc.isDebug())
         {
            buf.append(super.dbg_level_4(comp));

            /*
             * Add level 4 debugging for the ServiceIF HERE
             */

            dbgAttr = new DebugAttr();

            /*
             * Attribute Data and Attribute Name Translation maps
             */

            buf.append(_extIndent + DebugIF.INDENT_1
               + "Operation Attribute Data:\n");

            for (int i = 0; i < operations.length; i++)
            {
               iCnt = 0;
               attrComp = srvc.getOperAttr(operations[i]);
               mapLeft = srvc.getFw2SrvcNames(operations[i]);
               mapRight = srvc.getSrvc2FwNames(operations[i]);

               if (attrComp != null)
               {
                  buf.append(_extIndent + DebugIF.INDENT_2
                     + "[" + operations[i].toString() + "] ");

                  attrs = attrComp.getAttributes();
                  if (attrs != null && !attrs.isEmpty())
                  {                        
                     buf.append(attrs.size() + "\n");
                     
                     
                     for (String attrName : attrs.keySet())
                     {
                        buf.append(_extIndent + DebugIF.INDENT_2 + iCnt++ + ": ");
                        
                        attr = attrs.get(attrName);
                        
                        if (attr != null)
                        {
                           buf.append(dbgAttr.getData(attr, this.CLASS_NAME, DebugIF.INDENT_1));
                        }
                        else
                        {
                           buf.append("Attribute is NULL for key '" + attrName + "'\n");
                        }
                     }
                  }
                  else
                  {
                     buf.append("0\n");
                  }
               }

               if (mapLeft != null && mapRight != null)
               {
                  buf.append(_extIndent + DebugIF.INDENT_2
                     + "Framework to Service--------------------"
                     + "Service to Framework" + "\n");

                  buf.append(this.getMapData(mapLeft, mapRight));
               }
            }
         }
         else
         {
            buf.append(_extIndent + INDENT_1 + "NOTICE (DL4): Debug not enabled.\n");
         }
      }
      else
      {
         buf.append(_extIndent + DebugIF.INDENT_1 + "WARNING: Service is null\n");
      }
      return buf;
   }

   //----------------------------------------------------------------
   private StringBuffer getPropertyData(final Properties props)
   //----------------------------------------------------------------
   {
      int iCnt = 0;
      StringBuffer buf = new StringBuffer();
      String val = null;
      SortedSet<String> propKeys = null;

      propKeys = getSortedKeys(props);
      
      for (String key : propKeys)
      {              
         buf.append(_extIndent + DebugIF.INDENT_3 + iCnt++ + ": ");
         
         val = null;
         
         if (key != null)
         {
            if (key.length() > 0)
            {
               val = props.getProperty(key);
               if (val != null)
               {
                  if (val.length() > 0)
                  {
                     buf.append(key + "=" + val);
                  }
                  else
                  {
                     buf.append(key + ": (EMPTY value)");
                  }
               }
               else
               {
                  buf.append(key + ": (NULL value)");
               }
            }
            else
            {
               buf.append("key length is 0");
            }
         }
         else
         {
            buf.append("key is NULL");
         }
         buf.append("\n");
      }

      return buf;
   }

   //----------------------------------------------------------------
   private StringBuffer getMapData(final Map<String, String> mapLeft, final Map<String, String> mapRight)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();
      int iCnt = 0;
      int gap = 30;
      int padding = 0;
      String valueLeft = null;
      String keyRight = null;
      String valueRight = null;

      iCnt = 0;

      for (String keyLeft : mapLeft.keySet())
      {
         valueLeft = null;
         keyRight = null;
         valueRight = null;
         
         buf.append(_extIndent + DebugIF.INDENT_2 + iCnt + ": ");
         if (keyLeft != null && keyLeft.length() > 0)
         {
            valueLeft = mapLeft.get(keyLeft);
            if (valueLeft != null)
            {
               buf.append("'" + keyLeft + "' > '" + valueLeft + "'");
            }
            else
            {
               buf.append("'" + keyLeft + "' > " + DebugIF.NULL);
            }

            padding = gap - keyLeft.length() - valueLeft.length();
            if (padding < 2)
            {
               padding = 5;
            }

            for (int i = 0; i
               < padding; i++)
            {
               buf.append(" ");
            }

            keyRight = valueLeft;
            if (keyRight != null && keyRight.length() > 0)
            {
               valueRight = mapRight.get(keyRight);
               if (valueRight != null)
               {
                  buf.append("'" + keyRight + "' > '" + valueRight + "'");
               }
               else
               {
                  buf.append("'" + keyRight + "' > " + DebugIF.NULL);
               }
            }
            else
            {
               buf.append("Name is Null");
            }
         }
         else
         {
            buf.append("Name is Null");
         }

         buf.append("\n");
         iCnt++;
      }
      return buf;
   }
}
