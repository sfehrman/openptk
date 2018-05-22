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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.plugin.template;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openptk.api.State;
import org.openptk.exception.PluginException;
import org.openptk.exception.StructureException;
import org.openptk.plugin.Plugin;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class TemplatePlugin extends Plugin
//===================================================================
{
   public static final String PROP_TEMPLATE_LIBRARY = "template.library";
   public static final String PROP_MISSING_ATTRIBUTE = "missing.attribute";
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private Map<String, String> _templateVars = null;
   private Missing _mode = Missing.VARIABLE;
   public static final String VAR_BEGIN = "${";
   public static final String VAR_END = "}";
   public static final String VAR_SCOPE_ATTRIBUTE = "attribute:";
   public static final String VAR_SCOPE_TEMPLATE = "template:";

   private enum Missing
   {
      EXCEPTION, VARIABLE, BLANK
   }

   //----------------------------------------------------------------
   public TemplatePlugin()
   //----------------------------------------------------------------
   {
      super();

      this.setDescription("Merge Template Document with variable values");
      _templateVars = new HashMap<String, String>();
      return;
   }

   /**
    * @throws PluginException
    */
   //----------------------------------------------------------------
   @Override
   public synchronized void startup() throws PluginException
   //----------------------------------------------------------------
   {
      boolean foundMode = false;
      String METHOD_NAME = CLASS_NAME + ":startup(): ";
      String home = null;
      String library = null;
      String propValue = null;
      StringBuilder buf = new StringBuilder();
      Missing[] modes = null;

      super.startup();

      /*
       * template library is relative to the openptk.home property
       */

      home = this.getProperty(PROP_OPENPTK_HOME);
      if (home == null || home.length() < 1)
      {
         this.setState(State.FAILED);
         this.handleError(METHOD_NAME + "Required Property '" + PROP_OPENPTK_HOME
            + "' is null/empty");
      }

      library = this.getProperty(PROP_TEMPLATE_LIBRARY);
      if (library == null || library.length() < 1)
      {
         this.setState(State.FAILED);
         this.handleError(METHOD_NAME + "Required Property '" + PROP_TEMPLATE_LIBRARY
            + "' is null/empty");
      }

      if (home.endsWith(System.getProperty("file.separator")))
      {
         _templateVars.put(StructureIF.NAME_LIBRARY, home + library);
      }
      else
      {
         _templateVars.put(StructureIF.NAME_LIBRARY, home + System.getProperty("file.separator") + library);
      }


      propValue = this.getProperty(PROP_MISSING_ATTRIBUTE);
      if (propValue != null && propValue.length() > 0)
      {
         modes = Missing.values();
         for (int i = 0; i < modes.length; i++)
         {
            if (propValue.equalsIgnoreCase(modes[i].toString()))
            {
               foundMode = true;
               _mode = modes[i];
            }
            buf.append(modes[i].toString().toLowerCase()).append(", ");
         }
         if (!foundMode)
         {
            this.setStatus("Value " + propValue + "' for Property '"
               + PROP_MISSING_ATTRIBUTE + "' is invalid. Allowed value: "
               + buf.toString());
         }
      }

      this.setState(State.READY);

      return;
   }

   /**
    * @param structIn
    * @return
    * @throws PluginException
    */
   //----------------------------------------------------------------
   @Override
   public synchronized StructureIF execute(final StructureIF structIn) throws PluginException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      String docName = null;
      String attrName = null;
      String attrValue = null;
      String template = null;
      StructureIF structChild = null;
      StructureIF structOut = null;
      StructureIF[] structAttrs = null;
      Map<String, String> attrMap = null;

      /*
       * input = {
       *    document = "<template_name>";
       *    attributes = {
       *       attribute1 = "<value1>";
       *       ...
       *       attributeN = "<valueN>";
       *    }
       * }
       */

      if (this.getState() != State.READY)
      {
         this.handleError(METHOD_NAME + "Plugin is NOT READY, State='"
            + this.getState().toString() + "'");
      }

      this.setStatus("");

      /*
       * Check the input structure
       */

      if (structIn == null)
      {
         this.handleError(METHOD_NAME + "Input Structure is null");
      }

      structChild = structIn.getChild(StructureIF.NAME_DOCUMENT);
      if (structChild == null)
      {
         this.handleError(METHOD_NAME + "Input is missing the '"
            + StructureIF.NAME_DOCUMENT + "' child Structure");
      }
      docName = structChild.getValueAsString();
      if (docName == null || docName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Child Structure '"
            + StructureIF.NAME_DOCUMENT + "' has an empty value");
      }

      /*
       * Look for the Template document
       */

      template = this.getDocument(docName);
      if (template == null || template.length() < 1)
      {
         this.handleError(METHOD_NAME + "Template '" + docName + "' is null");
      }
      _templateVars.put(StructureIF.NAME_DOCUMENT, docName);

      /*
       * Read the attributes into the map
       */

      attrMap = new HashMap<String, String>();

      structChild = structIn.getChild(StructureIF.NAME_ATTRIBUTES);
      if (structChild != null)
      {
         structAttrs = structChild.getChildrenAsArray();
         if (structAttrs != null && structAttrs.length > 0)
         {
            for (int i = 0; i < structAttrs.length; i++)
            {
               attrName = null;
               attrValue = null;
               attrName = structAttrs[i].getName();
               if (attrName != null && attrName.length() > 0)
               {
                  attrValue = structAttrs[i].getValueAsString();
                  if (attrValue != null)
                  {
                     attrMap.put(attrName.toLowerCase(), attrValue);
                  }
               }
            }
         }
      }

      structOut = this.buildOutput(attrMap, template);

      return structOut;
   }

   //----------------------------------------------------------------
   private synchronized String getDocument(final String docName) throws PluginException
   //----------------------------------------------------------------
   {
      boolean bDone = false;
      int ch = 0;
      String METHOD_NAME = CLASS_NAME + ":getDocument(): ";
      StringBuilder buf = new StringBuilder();
      File file = null;
      FileReader reader = null;

      file = new File(_templateVars.get(StructureIF.NAME_LIBRARY)
         + System.getProperty("file.separator") + docName);

      try
      {
         reader = new FileReader(file);
      }
      catch (FileNotFoundException ex)
      {
         this.handleError(METHOD_NAME + "FileInputStream(): "
            + ex.getMessage() + ", user.dir='" + System.getProperty("user.dir")
            + "'");
      }
      
      try
      {
         do
         {
            ch = reader.read();
            if (ch != -1)
            {
               buf.append((char) ch);
            }
            else
            {
               bDone = true;
            }
         }
         while (!bDone);
      }
      catch (IOException ex)
      {
         this.handleError(METHOD_NAME + "FileReader.read(): " + ex.getMessage()
            + ", user.dir='" + System.getProperty("user.dir") + "'");
      }

      return buf.toString();
   }

   //----------------------------------------------------------------
   private StructureIF buildOutput(final Map<String, String> map, final String template) throws PluginException
   //----------------------------------------------------------------
   {
      char[] chars = null;
      boolean done = false;
      boolean isVar = false;
      boolean isEscape = false;
      int index = 0;
      String METHOD_NAME = CLASS_NAME + ":merge(): ";
      StringBuffer bufData = null;
      StringBuffer bufVar = null;
      StructureIF structOut = null;

      /*
       * Scan through the template and replace the variables.
       * The variables are enclosed in the "${" and "}" strings.
       *
       * The variable needs to
       * contain the "scope" and "name".  The scope "attribute:" is used to
       * get the value from the Map (name/value). The scope
       * "template:" is used to get an internal value.  Names are either
       * "library" or "document".
       *
       * BEGIN = "${"
       * END   = "}"
       *
       *           1         2         3
       * 0123456789012345678901234567890
       * ${template:library}
       * ${attribute:lastname}
       *
       */


      if (template == null | template.length() < 1)
      {
         this.handleError(METHOD_NAME + "Template is null");
      }

      if (map == null)
      {
         this.handleError(METHOD_NAME + "Attribute Map is null");
      }

      bufData = new StringBuffer();
      bufVar = new StringBuffer();
      chars = template.toCharArray();

      while (!done)
      {
         switch (chars[index])
         {
            case '\\': // escape character
               isEscape = true;
               break;
            case '$':
            case '{':
            case '}':
               if (isEscape)
               {
                  bufData.append(chars[index]);
                  isEscape = false;
               }
               else
               {
                  if (chars[index] == '$')
                  {
                     if (index + 1 < chars.length && chars[index + 1] == '{')
                     {
                        isVar = true;
                        index++;
                     }
                     else
                     {
                        bufData.append(chars[index]);
                     }
                  }
                  else if (chars[index] == '}')
                  {
                     if (isVar)
                     {
                        bufData.append(this.getValue(map, bufVar.toString()));
                        bufVar = new StringBuffer();
                        isVar = false;
                     }
                     else
                     {
                        bufData.append(chars[index]);
                     }
                  }
               }
               break;
            default:
               isEscape = false;
               if (isVar)
               {
                  bufVar.append(chars[index]);
               }
               else
               {
                  bufData.append(chars[index]);
               }
               break;
         }
         index++;
         if (index >= chars.length)
         {
            done = true;
         }
      }

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);

      if (this.getStatus().length() > 0)
      {
         structOut.setState(State.ERROR);
      }
      try
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_DOCUMENT, bufData.toString()));
         structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, this.getStatus()));
      }
      catch (StructureException ex)
      {
         this.handleError(METHOD_NAME + "StructureIF.addChild(): " + ex.getMessage());
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private String getValue(final Map<String, String> map, final String name) throws PluginException
   //----------------------------------------------------------------
   {
      boolean useTemplateData = false;
      String METHOD_NAME = CLASS_NAME + ":getValue(): ";
      String[] varArray = null;
      String scope = null;
      String key = null;
      String value = null;

      /*
       * name = "scope:key"
       * name = "key" // use scope = "attribute"
       *
       * scope options:  "attribute", "template"
       */

      if (name.indexOf(":") >= 0)
      {
         varArray = name.split(":");
         scope = varArray[0];
         key = varArray[1].toLowerCase();
      }
      else
      {
         key = name.toLowerCase();
      }

      if (scope != null && scope.length() > 0)
      {
         if (scope.equalsIgnoreCase(StructureIF.NAME_TEMPLATE))
         {
            useTemplateData = true;
         }
      }

      if (useTemplateData)
      {
         value = _templateVars.get(key);
      }
      else
      {
         value = map.get(key);
      }

      if (value == null)
      {
         this.setStatus("Error: value is null for '" + name + "'");
         switch (_mode)
         {
            case BLANK:
               value = "";
               break;
            case VARIABLE:
               value = "${" + name + "}";
               break;
            case EXCEPTION:
               this.handleError(METHOD_NAME + "Variable '" + name + "' not found.");
               break;
         }
      }

      return value;
   }
}
