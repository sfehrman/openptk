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

import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;
import org.openptk.common.AttrIF;
import org.openptk.common.ComponentIF;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.config.Configuration;
import org.openptk.context.ContextIF;
import org.openptk.definition.DefinitionIF;
import org.openptk.logging.Logger;
import org.openptk.spi.ServiceIF;
import org.openptk.spi.operations.OperationsIF;

//===================================================================
public class Debugger
//===================================================================
{

   private DebugIF _dbgContextIF = null;
   private DebugIF _dbgComponentIF = null;
   private DebugIF _dbgRequestIF = null;
   private DebugIF _dbgResponseIF = null;
   private DebugIF _dbgServiceIF = null;
   private DebugIF _dbgOperationsIF = null;
   private DebugIF _dbgElementIF = null;
   private DebugIF _dbgDefinitionIF = null;
   private DebugIF _dbgConfiguration = null;
   private DebugIF _dbgAttrIF = null;
   private DebugIF _dbgAttributeIF = null;
   private final String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public Debugger()  // output to Console
   //----------------------------------------------------------------
   {
      return;
   }


   /**
    * @param obj
    * @param callerId
    */
   //----------------------------------------------------------------
   public void logData(final Object obj, final String callerId)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":logData()";
      this.checkDbg(obj);

      if (obj != null)
      {
         if (obj instanceof ResponseIF)
         {
            _dbgResponseIF.logData(obj, callerId);
         }
         else if (obj instanceof RequestIF)
         {
            _dbgRequestIF.logData(obj, callerId);
         }
         else if (obj instanceof ServiceIF)
         {
            _dbgServiceIF.logData(obj, callerId);
         }
         else if (obj instanceof OperationsIF)
         {
            _dbgOperationsIF.logData(obj, callerId);
         }
         else if (obj instanceof ContextIF)
         {
            _dbgContextIF.logData(obj, callerId);
         }
         else if (obj instanceof ComponentIF)
         {
            _dbgComponentIF.logData(obj, callerId);
         }
         else if (obj instanceof AttrIF)
         {
            _dbgAttrIF.logData(obj, callerId);
         }
         else if (obj instanceof ElementIF)
         {
            _dbgElementIF.logData(obj, callerId);
         }
         else if (obj instanceof DefinitionIF)
         {
            _dbgDefinitionIF.logData(obj, callerId);
         }
         else if (obj instanceof Configuration)
         {
            _dbgConfiguration.logData(obj, callerId);
         }
         else if (obj instanceof AttributeIF)
         {
            _dbgAttributeIF.logData(obj, callerId);
         }
         else
         {
            Logger.logError(METHOD_NAME + ": Can not debug Class: " +
               obj.getClass().getSimpleName() +
               ", callerId=" + callerId);
         }
      }
      else
      {
         Logger.logError(METHOD_NAME + ": Object is null, callerId=" +
            callerId);
      }

      return;
   }


   /**
    * @param obj
    * @param callerId
    * @return
    */
   //----------------------------------------------------------------
   public String getData(final Object obj, final String callerId)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getData()";
      String data = null;

      this.checkDbg(obj);

      if (obj != null)
      {
         if (obj instanceof ResponseIF)
         {
            data = _dbgResponseIF.getData(obj, callerId);
         }
         else if (obj instanceof RequestIF)
         {
            data = _dbgRequestIF.getData(obj, callerId);
         }
         else if (obj instanceof ServiceIF)
         {
            data = _dbgServiceIF.getData(obj, callerId);
         }
         else if ( obj instanceof OperationsIF)
         {
            data = _dbgOperationsIF.getData(obj, callerId);
         }
         else if (obj instanceof ContextIF)
         {
            data = _dbgContextIF.getData(obj, callerId);
         }
         else if (obj instanceof ComponentIF)
         {
            data = _dbgComponentIF.getData(obj, callerId);
         }
         else if (obj instanceof AttrIF)
         {
            data = _dbgAttrIF.getData(obj, callerId);
         }
         else if (obj instanceof ElementIF)
         {
            data = _dbgElementIF.getData(obj, callerId);
         }
         else if (obj instanceof DefinitionIF)
         {
            data = _dbgDefinitionIF.getData(obj, callerId);
         }
         else if (obj instanceof Configuration)
         {
            data = _dbgConfiguration.getData(obj, callerId);
         }
         else if (obj instanceof AttributeIF)
         {
            data = _dbgAttributeIF.getData(obj, callerId);
         }
         else
         {
            data = METHOD_NAME + ": Can not debug Class: " +
               obj.getClass().getSimpleName() +
               ", callerId=" + callerId;
         }
      }
      else
      {
         data = METHOD_NAME + ": Object is null, callerId=" + callerId;
      }

      return data;
   }

   //----------------------------------------------------------------
   private void checkDbg(final Object obj)
   //----------------------------------------------------------------
   {
      if (obj != null)
      {
         if (obj instanceof ResponseIF)
         {
            if (_dbgResponseIF == null)
            {
               _dbgResponseIF = new DebugResponse();
            }
         }
         else if (obj instanceof RequestIF)
         {
            if (_dbgRequestIF == null)
            {
               _dbgRequestIF = new DebugRequest();
            }
         }
         else if (obj instanceof ServiceIF)
         {
            if (_dbgServiceIF == null)
            {
               _dbgServiceIF = new DebugService();
            }
         }
         else if (obj instanceof OperationsIF)
         {
            if (_dbgOperationsIF == null)
            {
               _dbgOperationsIF = new DebugOperations();
            }
         }
         else if (obj instanceof ContextIF)
         {
            if (_dbgContextIF == null)
            {
               _dbgContextIF = new DebugContext();
            }
         }
         else if (obj instanceof ComponentIF)
         {
            if (_dbgComponentIF == null)
            {
               _dbgComponentIF = new DebugComponent();
            }
         }
         else if (obj instanceof AttrIF)
         {
            if (_dbgAttrIF == null)
            {
               _dbgAttrIF = new DebugAttr();
            }
         }
         else if (obj instanceof ElementIF)
         {
            if (_dbgElementIF == null)
            {
               _dbgElementIF = new DebugElement();
            }
         }
         else if (obj instanceof DefinitionIF)
         {
            if (_dbgDefinitionIF == null)
            {
               _dbgDefinitionIF = new DebugDefinition();
            }
         }
         else if (obj instanceof Configuration)
         {
            if (_dbgConfiguration == null)
            {
               _dbgConfiguration = new DebugConfiguration();
            }
         }
         else if (obj instanceof AttributeIF)
         {
            if (_dbgAttributeIF == null)
            {
               _dbgAttributeIF = new DebugAttribute();
            }
         }
         else
         {
            Logger.logError("Can not debug Class: " + obj.getClass().getSimpleName());
         }
      }
      else
      {
         Logger.logError("Can not debug a null Object");
      }
      return;
   }
}
