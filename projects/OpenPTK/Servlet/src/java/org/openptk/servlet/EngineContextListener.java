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
package org.openptk.servlet;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openptk.api.State;
import org.openptk.engine.EngineAction;
import org.openptk.engine.EngineIF;
import org.openptk.engine.WebEngine;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.EngineException;

/**
 *
 * @author Derrick Harcey, Sun Microsystems, Inc.
 *
 */
//===================================================================
public class EngineContextListener implements ServletContextListener
//===================================================================
{
   private static final String MSG_CONFIGFILE_INVALID = "Invalid configuration file";
   private static final String MSG_SERVLETCONTEXT_NULL = "ServletContext is null";
   private static final String MSG_ENGINEOBJECT_INVALID = "Invalid engine object";
   private static final String MSG_ENGINE_STARTUP_ERROR = "Engine startup failed.";
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _error = null;
   private State _state = State.NEW;
   private ServletContext _servletCtx = null;
   private EngineIF _engine = null;
   private final Logger _logger = Logger.getLogger(CLASS_NAME);
   public static final String SERVLET_INITPARAM_CONFIG_FILENAME = "org.openptk.config.filename";

   /**
    *
    * This method is invoked when the Web Application has been removed
    * and is no longer able to accept requests.
    * @param event
    */
   //----------------------------------------------------------------
   @Override
   public void contextDestroyed(ServletContextEvent event)
   //----------------------------------------------------------------
   {
      //Output a simple message to the server's console
      this.stopEngine();
      this.logInfo("The OpenPTK Server has been stopped");
      _servletCtx = null;
      return;
   }

   /**
    * This method is invoked when the Web Application
    * is ready to service requests.
    * @param event
    */
   //----------------------------------------------------------------
   @Override
   public void contextInitialized(ServletContextEvent event)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":contextInitialized(): ";

      _servletCtx = event.getServletContext();

      try
      {
         this.startEngine();
      }
      catch (EngineException eex)
      {
         _state = State.FAILED;
         _error = METHOD_NAME + MSG_ENGINE_STARTUP_ERROR + ": " + eex.getMessage();
         this.logError(getError());
      }

      return;
   }

   //----------------------------------------------------------------
   private void startEngine() throws EngineException
   //----------------------------------------------------------------
   {

      Object obj = null;
      String METHOD_NAME = CLASS_NAME + ":startEngine(): ";
      String configFilename = null;
      String propName = null;
      String propValue = null;
      Properties props = null;

      if (_engine == null)
      {
         if (_servletCtx == null)
         {
            _state = State.FAILED;
            _error = METHOD_NAME + MSG_SERVLETCONTEXT_NULL;
            throw new EngineException(getError());
         }

         obj = _servletCtx.getInitParameter(EngineIF.ATTR_SERVLET_CONTEXT_ENGINE);

         if (obj != null)
         {
            if (obj instanceof EngineIF)
            {
               //Create the OpenPTK Server engine
               _engine = (EngineIF) obj;
            }
            else
            {
               _state = State.FAILED;
               _error = METHOD_NAME
                  + MSG_ENGINEOBJECT_INVALID + ", "
                  + EngineIF.ATTR_SERVLET_CONTEXT_ENGINE;
               throw new EngineException(getError());
            }
         }
         else
         {
            /*
             * Create new Engine (and its Configuration) and store in Servlet Context
             * get the name of the config file from a context param:
             *
             *   <context-param>
             *      <description>The name of the XML configuration file for the OpenPTK Framework</description>
             *      <param-name>org.openptk.config.filename</param-name>
             *      <param-value>openptk.xml</param-value>
             *   </context-param>
             */

            configFilename = _servletCtx.getInitParameter(SERVLET_INITPARAM_CONFIG_FILENAME);

            if (configFilename != null && configFilename.length() > 0)
            {
               /*
                * Get servlet properties.  Send them to the Engine / Configuration
                */

               props = new Properties();

               propName = EngineIF.PROP_OPENPTK_HOME;
               propValue = _servletCtx.getRealPath("/");
               if ( propValue != null && propValue.length() > 0)
               {
                  props.setProperty(propName, propValue);
               }

               propName = EngineIF.PROP_SERVER_INFO;
               propValue = _servletCtx.getServerInfo();
               if ( propValue != null && propValue.length() > 0)
               {
                  props.setProperty(propName, propValue);
               }

               try
               {
                  _engine = new WebEngine(configFilename, props);
               }
               catch (ConfigurationException ex)
               {
                  _state = State.FAILED;
                  _error = METHOD_NAME + ex.getMessage();
                  throw new EngineException(getError());
               }

               /*
                * Save the config as a Servlet Attribute
                */

               _servletCtx.setAttribute(EngineIF.ATTR_SERVLET_CONTEXT_ENGINE, _engine);
            }
            else
            {
               _state = State.FAILED;
               _error = METHOD_NAME + MSG_CONFIGFILE_INVALID
                  + ", '" + SERVLET_INITPARAM_CONFIG_FILENAME + "'";
               throw new EngineException(getError());
            }
         }

         //Output a simple message to the app server's console
         this.logInfo(EngineIF.MSG_ENGINE_STARTUP_SUCCESS);
      }

      return;
   }

   //----------------------------------------------------------------
   private void stopEngine()
   //----------------------------------------------------------------
   {
      if ( _engine != null )
      {
         _engine.action(EngineAction.STOP);
      }
      
      return;
   }

   /**
    *
    * @return
    */
   //----------------------------------------------------------------
   protected String getError()
   //----------------------------------------------------------------
   {
      return _error;
   }

   //----------------------------------------------------------------
   private void logError(String msg)
   //----------------------------------------------------------------
   {
      _logger.log(Level.SEVERE, msg);
      return;
   }

   //----------------------------------------------------------------
   private void logInfo(String msg)
   //----------------------------------------------------------------
   {
      _logger.log(Level.INFO, msg);
      return;
   }
}
