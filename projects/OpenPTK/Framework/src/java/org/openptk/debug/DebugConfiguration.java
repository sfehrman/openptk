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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import org.openptk.authenticate.AuthenticatorIF;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.config.Configuration;
import org.openptk.context.ContextIF;
import org.openptk.definition.DefinitionIF;
import org.openptk.exception.ConfigurationException;
import org.openptk.logging.Logger;

//===================================================================
public class DebugConfiguration extends Debug implements DebugIF
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private List<String> _contextNames = new LinkedList<String>();

   /**
    * @param obj
    * @param callerId
    */
   //----------------------------------------------------------------
   @Override
   public void logData(final Object obj, final String callerId)
   //----------------------------------------------------------------
   {
      Logger.logInfo(process((Configuration) obj, callerId));

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
      return process((Configuration) obj, callerId);
   }

   /**
    * @param obj
    * @param callerId
    * @param ident
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public String getData(final Object obj, final String callerId, final String ident)
   //----------------------------------------------------------------
   {
      _extIndent = ident;
      return process((Configuration) obj, callerId);
   }

   /* 
    * =================
    * PROTECTED METHODS 
    * =================
    */
   /**
    * @param conf
    * @return
    */
   //----------------------------------------------------------------
   protected StringBuffer dbg_level_1(final Configuration conf)
   //----------------------------------------------------------------
   {
      int iCnt = 0;
      StringBuffer buf = new StringBuffer();
      String[] authenArray = null;
      Map<String, ContextIF> contexts = null;
      ContextIF context = null;
      AuthenticatorIF authen = null;

      // Contexts

      buf.append(_extIndent
         + "Contexts: default="
         + conf.getDefaultContextName() + " (" + conf.hasDefaultContext() + ")"
         + ", current=" + conf.getContextName()
         + "");

      contexts = conf.getContexts();

      if (contexts.size() > 0)
      {
         buf.append(": " + contexts.size() + "\n");
         
         iCnt = 0;
         
         for (String key : contexts.keySet())
         {
            if (key != null)
            {
               context = null;
               try
               {
                  context = conf.getContext(key);
               }
               catch (ConfigurationException ex)
               {
                  buf.append(_extIndent + DebugIF.INDENT_1 + key
                     + ", ConfigurationException: " + ex.getMessage() + "\n");
               }
            if (context != null)
            {
               buf.append(_extIndent + DebugIF.INDENT_1 + iCnt + ": "
                       + key + ": " + context.getDescription() + "\n");
               this._contextNames.add(key);
            }
            else
            {
               buf.append(_extIndent + DebugIF.INDENT_1 + iCnt + ": "
                       + key + ": " + DebugIF.NULL + "\n");
            }
            iCnt++;
         }
            else
            {
               buf.append(_extIndent + DebugIF.INDENT_1
                  + "WARNING: Name of the Context is NULL" + "\n");
      }
         }
      }
      else
      {
         buf.append(": 0\n");
         buf.append(_extIndent + DebugIF.INDENT_2 + "No Contexts" + "\n");
      }

      // Authenticators

      authenArray = conf.getAuthenticatorNames();

      buf.append(_extIndent
         + "Authenticators: " + authenArray.length + "\n");

      iCnt = 0;
      
      for (String key : authenArray)
      {
         authen = null;
         try
         {
            authen = conf.getAuthenticator(key);
         }
         catch (ConfigurationException ex)
         {
            buf.append(_extIndent + DebugIF.INDENT_1 + key
               + ", ConfigurationException: " + ex.getMessage() + "\n");
         }

         if (authen != null)
         {
            buf.append(_extIndent + DebugIF.INDENT_1 + iCnt + ": "
               + key + ": type=" + authen.getType().toString() + ", "
               + authen.getClass().getName()
               + ", " + authen.getDescription() + "\n");
         }
         iCnt++;
      }

      return buf;
   }

   /**
    * @param conf
    * @return
    */
   //----------------------------------------------------------------
   protected StringBuffer dbg_level_2(final Configuration conf)
   //----------------------------------------------------------------
   {
      int iCnt = 0;
      StringBuffer buf = new StringBuffer();
      Properties messages = null;
      SortedSet<String> msgKeys = null;
      String msgValue = null;

      // Messages

      buf.append(_extIndent).append("Messages: ");

      messages = conf.getMessages();

      if (messages != null)
      {
         buf.append(messages.size()).append("\n");

         msgKeys = getSortedKeys(messages);

         for (String msgName : msgKeys)
         {
            buf.append(_extIndent).append(DebugIF.INDENT_1).append(iCnt++).append(": ");
            msgValue = null;
            
            if (msgName != null)
            {
               msgValue = messages.getProperty(msgName);
               if (msgValue != null)
               {
                  buf.append(msgName).append("=").append(msgValue).append("\n");
               }
               else
               {
                  buf.append(msgName).append("=" + DebugIF.NULL + "\n");
               }
            }
            else
            {
               buf.append("Message name is NULL" + "\n");
            }
         }
      }
      else
      {
         buf.append(DebugIF.NULL + "\n");
      }
      return buf;
   }

   /**
    * @param conf
    * @return
    */
   //----------------------------------------------------------------
   protected StringBuffer dbg_level_3(final Configuration conf)
   //----------------------------------------------------------------
   {
      int cnt = 0;
      StringBuffer buf = new StringBuffer();
      String defName = null;
      String assocName = null;
      ContextIF ctx = null;
      DefinitionIF def = null;
      ComponentIF assoc = null;
      Operation operations[] = Operation.values();
      Map<String, DefinitionIF> defs = null;
      Map<String, ComponentIF> assocs = null;

      defs = new HashMap<String, DefinitionIF>();
      assocs = new HashMap<String, ComponentIF>();

      /*
       * Show the Definitions and Associations
       * Access each Context and show only the unique ones
       */


      for (String ctxName : _contextNames)
      {
         ctx = null;
         
         if (ctxName != null)
         {
            try
            {
               ctx = conf.getContext(ctxName);
            }
            catch (ConfigurationException ex)
            {
               buf.append("ConfigurationException: ").append(ex.getMessage()).append("name=").append(ctxName).append("\n");
            }
            if (ctx != null)
            {
               def = ctx.getDefinition();
               if (def != null && def.getUniqueId() != null)
               {
                  defName = def.getUniqueId().toString();
                  if (!defs.containsKey(defName))
                  {
                     defs.put(defName, def);
                  }
               }
               for (int i = 0; i < operations.length; i++)
               {
                  assoc = ctx.getService().getAssociation(operations[i]);
                  if (assoc != null && assoc.getUniqueId() != null)
                  {
                     assocName = assoc.getUniqueId().toString();
                     if (!assocs.containsKey(assocName))
                     {
                        assocs.put(assocName, assoc);
                     }
                  }
               }
            }
         }
      }

      buf.append(_extIndent).append("Definitions: ").append(defs.size()).append("\n");

      cnt = 0;
      
      for (DefinitionIF defObj : defs.values())
      {
         if (defObj != null)
         {
            buf.append(_extIndent).append(DebugIF.INDENT_1).append(cnt++).append(": ").append(conf.getData(defObj, this.getClass().getName()));
         }
      }

      buf.append(_extIndent).append("Associations: ").append(assocs.size()).append("\n");

      cnt = 0;
      
      for (ComponentIF assocObj : assocs.values())
      {
         if (assocObj != null)
         {
            buf.append(_extIndent + DebugIF.INDENT_1
               + cnt++ + ": "
               + conf.getData(assocObj, this.getClass().getName()));
         }
      }

      return buf;
   }

   /**
    * @param conf
    * @return
    */
   //----------------------------------------------------------------
   protected StringBuffer dbg_level_4(final Configuration conf)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();
      ContextIF ctx = null;

      // Get the details for each Context

      for (String name : _contextNames)
      {
         ctx = null;
         
         if (name != null)
         {
            try
            {
               ctx = conf.getContext(name);
            }
            catch (ConfigurationException ex)
            {
               buf.append("ConfigurationException: ").append(ex.getMessage()).append("name=").append(name).append("\n");
            }
            if (ctx != null)
            {
               buf.append("\n" + conf.getData(ctx, this.getClass().getName()));
            }
         }
      }

      return buf;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private String process(final Configuration conf, final String callerId)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();

      buf.append(_extIndent + CLASS_NAME
         + ", callerId=" + callerId
         + ", " + ((conf != null) ? conf.getClass().getName() : DebugIF.NULL)
         + "\n");

      buf.append(dbg_level_1(conf));
      buf.append(dbg_level_2(conf));
      buf.append(dbg_level_3(conf));
      buf.append(dbg_level_4(conf));

      /*
       * may add more debug levels in the future
       */

      return buf.toString();
   }
}
