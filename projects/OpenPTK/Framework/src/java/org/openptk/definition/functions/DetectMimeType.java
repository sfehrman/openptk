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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.definition.functions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openptk.api.State;
import org.openptk.common.AttrIF;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.FunctionException;
import org.openptk.exception.PluginException;
import org.openptk.plugin.PluginIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class DetectMimeType extends Function implements FunctionIF
//===================================================================
{

   private String CLASS_NAME = this.getClass().getSimpleName();
   private static final String ARG_PLUGIN = "plugin";
   private static final String ARG_DATA = "data";
   private static final String UNKNOWN = "unknown/unknown";


   /**
    * @param context
    * @param key
    * @param mode
    * @param oper
    * @param args
    * @param attributes
    * @throws FunctionException
    */
   //----------------------------------------------------------------
   @Override
   public synchronized void execute(ContextIF context, String key, TaskMode mode,
      Operation oper, List<ArgumentIF> args, Map<String, AttrIF> attributes)
      throws FunctionException
   //----------------------------------------------------------------
   {
      byte[] bytes = null;
      Object objValue = null;
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      String pluginName = null;
      String dataName = null;
      String mimeType = null;
      Iterator<ArgumentIF> iter = null;
      AttrIF attrKey = null;
      AttrIF attrData = null;
      ArgumentIF arg = null;
      PluginIF plugin = null;
      StructureIF structIn = null;
      StructureIF structOut = null;

      /*
       * Get the Attribute name (key) we are transforming
       */

      if (key == null)
      {
         this.handleError(METHOD_NAME + "Key is Null");
      }

      attrKey = attributes.get(key);

      if (attrKey == null)
      {
         this.handleError(METHOD_NAME + "Attribute '" + key + "' is null");
      }

      /*
       * We need the following arguments:
       * "data"   : ATTRIBUTE : name of the attribute that contains the "raw data"
       * "plugin" : LITERAL   : name of the PluginIF that will be used
       */

      if (args == null || args.isEmpty())
      {
         this.handleError(METHOD_NAME + "Arguments are null/empty, required arguments: '" +
            ARG_DATA + "', '" + ARG_PLUGIN + "'");
      }

      iter = args.iterator();
      while (iter.hasNext())
      {
         arg = iter.next();
         if (arg.getName().equalsIgnoreCase(ARG_DATA))
         {
            if (arg.getType() != ArgumentType.ATTRIBUTE)
            {
               this.handleError(METHOD_NAME + "Argument '" + ARG_DATA + "' must be of Type: " +
                  ArgumentType.ATTRIBUTE.toString());
            }

            dataName = arg.getValue();
            if (dataName == null || dataName.length() < 1)
            {
               this.handleError(METHOD_NAME + "Argument value for '" + ARG_DATA + "' is null");
            }
         }
         else if (arg.getName().equalsIgnoreCase(ARG_PLUGIN))
         {
            if (arg.getType() != ArgumentType.LITERAL)
            {
               this.handleError(METHOD_NAME + "Argument '" + ARG_PLUGIN + "' must be of Type: " +
                  ArgumentType.LITERAL.toString());
            }

            pluginName = arg.getValue();
            if (pluginName == null || pluginName.length() < 1)
            {
               this.handleError(METHOD_NAME + "Argument value for '" + ARG_PLUGIN + "' is null");
            }
         }
      }

      if (dataName == null || dataName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Required argument '" + ARG_DATA +
            "' was not found.");
      }

      if (pluginName == null || pluginName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Required argument '" + ARG_PLUGIN +
            "' was not found.");
      }

      /*
       * Get the plugin
       */

      try
      {
         plugin = context.getConfiguration().getPlugin(pluginName);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + "pluginId='" + pluginName + "', " +
            ex.getMessage());
      }

      if (plugin == null)
      {
         this.handleError(METHOD_NAME + "Plugin '" + pluginName + "' is null");
      }

      if (plugin.getState() != State.READY)
      {
         this.handleError(METHOD_NAME + "Plugin '" + pluginName +
            "' is NOT ready, state='" + plugin.getState().toString() + "'");
      }

      /*
       * Get the data
       */

      attrData = attributes.get(dataName);
      if (attrData == null)
      {
         this.handleError(METHOD_NAME + "Attribute '" + ARG_DATA + "' is null");
      }

      switch (attrData.getType())
      {
         case OBJECT:
            objValue = attrData.getValue();
            if (objValue instanceof byte[])
            {
               bytes = (byte[]) objValue;
            }
            break;
      }

      if (bytes == null)
      {
         this.handleError(METHOD_NAME + "data (byte[]) is null");
      }

      /*
       * Call the plugin
       */

      structIn = new BasicStructure(StructureIF.NAME_DOCUMENT, bytes);

      try
      {
         structOut = plugin.execute(structIn);
      }
      catch (PluginException ex)
      {
         this.handleError(METHOD_NAME + "Plugin failure: " + ex.getMessage());
      }

      /*
       * Get the Mime-Type from the results
       */

      if ( structOut == null)
      {
         this.handleError(METHOD_NAME + "Plugin output Structure is null");
      }

      mimeType = structOut.getValueAsString();

      if (mimeType != null && mimeType.length() > 0)
      {
         attrKey.setValue(mimeType);
      }
      else
      {
         attrKey.setValue(UNKNOWN);
      }

      return;
   }
}
