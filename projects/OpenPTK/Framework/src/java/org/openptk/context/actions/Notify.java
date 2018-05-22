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
package org.openptk.context.actions;

import java.util.Iterator;
import java.util.List;

import org.openptk.api.State;
import org.openptk.common.AttrIF;
import org.openptk.common.ComponentIF;
import org.openptk.common.ResponseIF;
import org.openptk.exception.ActionException;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.PluginException;
import org.openptk.exception.StructureException;
import org.openptk.plugin.PluginIF;
import org.openptk.spi.ServiceIF;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class Notify extends Action
//===================================================================
{
   public static final String PROP_PLUGIN_TEMPLATE = "plugin.template";
   public static final String PROP_PLUGIN_EMAIL = "plugin.email";
   public static final String PROP_TEMPLATE_DOCUMENT = "template.document";
   public static final String PROP_SENDEMAIL_SUBJECT = "sendemail.subject";
   public static final String PROP_SENDEMAIL_FROMADDRESS = "sendemail.fromaddress";
   public static final String PROP_SENDEMAIL_TOADDRESS_ATTRIBUTE = "sendemail.toaddress.attribute";
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _pluginTemplate = null;
   private String _pluginSendEmail = null;

   //----------------------------------------------------------------
   public Notify()
   //----------------------------------------------------------------
   {
      super();
      this.setDescription("Send email notification, merge Request and Template");
      return;
   }

   /**
    * @throws ActionException
    */
   //----------------------------------------------------------------
   @Override
   public void startup() throws ActionException
   //----------------------------------------------------------------
   {

      _pluginSendEmail = this.getPropValue(PROP_PLUGIN_EMAIL);
      _pluginTemplate = this.getPropValue(PROP_PLUGIN_TEMPLATE);

      this.setState(State.READY);

      return;
   }

   /**
    * @param service
    * @param response
    * @throws ActionException
    */
   //----------------------------------------------------------------
   @Override
   public synchronized void postAction(final ServiceIF service, final ResponseIF response) throws ActionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":postAction(): ";
      String templateDoc = null;
      String emailBody = null;
      String emailSubject = null;
      String emailTo = null;
      String emailFrom = null;
      String toAddrAttr = null;
      ComponentIF comp = null;
      AttrIF attr = null;
      StructureIF structIn = null;
      StructureIF structOut = null;
      PluginIF plugin = null;

      if (this.getState() != State.READY)
      {
         this.handleError(METHOD_NAME + "Action is not READY, State='"
            + this.getState().toString() + "'");
      }

      comp = response.getRequest().getSubject();
      if (comp == null)
      {
         this.handleError(METHOD_NAME + "Response::Request::Subject is null");
      }

      /*
       * get the "From:" email address
       */

      emailFrom = this.getPropValue(PROP_SENDEMAIL_FROMADDRESS);

      /*
       * get the "Subject:" for the email
       */

      emailSubject = this.getPropValue(PROP_SENDEMAIL_SUBJECT);

      /*
       * get the "To:" email address (obtain from Component Attribute)
       */

      toAddrAttr = this.getPropValue(PROP_SENDEMAIL_TOADDRESS_ATTRIBUTE);
      attr = comp.getAttribute(toAddrAttr);
      if (attr == null)
      {
         this.handleError(METHOD_NAME + "Request does not have an email address");
      }
      emailTo = attr.getValueAsString();
      if (emailTo == null || emailTo.length() < 1)
      {
         this.handleError(METHOD_NAME + "email address is null");
      }

      /*
       * Build the "Body" of the email (merge template with data)
       */

      templateDoc = this.getPropValue(PROP_TEMPLATE_DOCUMENT);

      emailBody = this.getEmailBody(templateDoc, response, service);

      structIn = new BasicStructure(StructureIF.NAME_REQUEST);

      try
      {
         structIn.addChild(new BasicStructure(StructureIF.NAME_FROM, emailFrom));
         structIn.addChild(new BasicStructure(StructureIF.NAME_TO, emailTo));
         structIn.addChild(new BasicStructure(StructureIF.NAME_SUBJECT, emailSubject));
         structIn.addChild(new BasicStructure(StructureIF.NAME_BODY, emailBody));
      }
      catch (StructureException ex)
      {
         this.handleError(METHOD_NAME + "structure.addChild():" + ex.getMessage());
      }

      try
      {
         plugin = _context.getConfiguration().getPlugin(_pluginSendEmail);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + "config.getPlugin(): " + ex.getMessage());
      }

      if (plugin == null)
      {
         this.handleError(METHOD_NAME + "Plugin '" + _pluginSendEmail + "' is null");
      }

      /*
       * Send the email
       */

      try
      {
         structOut = plugin.execute(structIn);
      }
      catch (PluginException ex)
      {
         this.handleError(METHOD_NAME + "plugin.execute(): " + ex.getMessage());
      }

      return;
   }

   //----------------------------------------------------------------
   private String getEmailBody(final String templateDoc, final ResponseIF response, final ServiceIF service) throws ActionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getEmailBody(): ";
      String body = null;
      String attrName = null;
      Object attrValue = null;
      List<String> attrNames = null;
      Iterator<String> iter = null;
      ComponentIF comp = null;
      AttrIF attr = null;
      StructureIF structIn = null;
      StructureIF structAttrs = null;
      StructureIF structOut = null;
      StructureIF structStatus = null;
      StructureIF structDocument = null;
      PluginIF plugin = null;

      comp = response.getRequest().getSubject();
      if (comp == null)
      {
         this.handleError(METHOD_NAME + "Response::Request::Subject is null");
      }

      structIn = new BasicStructure(StructureIF.NAME_REQUEST);
      structAttrs = new BasicStructure(StructureIF.NAME_ATTRIBUTES);

      try
      {
         structIn.addChild(new BasicStructure(StructureIF.NAME_DOCUMENT, templateDoc));
         structIn.addChild(structAttrs);
      }
      catch (StructureException ex)
      {
         this.handleError(METHOD_NAME + "structure.addChild(): " + ex.getMessage());
      }

      /*
       * Add the "key" unqueId and all the
       * Component Attribute name/values to the Structure
       */

      attrName = service.getKey(response.getRequest().getOperation());
      attrValue = comp.getUniqueId();

      if (attrValue != null)
      {
         try
         {
            switch (comp.getUniqueIdType())
            {
               case STRING:
                  structAttrs.addChild(new BasicStructure(attrName, (String) attrValue));
                  break;
               case INTEGER:
                  structAttrs.addChild(new BasicStructure(attrName, (Integer) attrValue));
                  break;
               case LONG:
                  structAttrs.addChild(new BasicStructure(attrName, (Long) attrValue));
                  break;
               default:
                  structAttrs.addChild(new BasicStructure(attrName, attrValue));
                  break;
            }
         }
         catch (StructureException ex)
         {
            this.handleError(METHOD_NAME + "structure.addChild(): " + ex.getMessage());
         }
      }

      attrNames = comp.getAttributesNames();
      if (attrNames != null && attrNames.size() > 0)
      {
         iter = attrNames.iterator();
         while (iter.hasNext())
         {
            attrName = iter.next();
            if (attrName != null && attrName.length() > 0)
            {
               attr = comp.getAttribute(attrName);
               if (attr != null)
               {
                  attrValue = attr.getValue();
                  if (attrValue != null)
                  {
                     try
                     {
                        structAttrs.addChild(new BasicStructure(attrName, attrValue.toString()));
                     }
                     catch (StructureException ex)
                     {
                        this.handleError(METHOD_NAME + "structure.addChild(): " + ex.getMessage());
                     }
                  }
               }
            }
         }
      }

      try
      {
         plugin = _context.getConfiguration().getPlugin(_pluginTemplate);
      }
      catch (ConfigurationException ex)
      {
         this.handleError(METHOD_NAME + "config.getPlugin(): " + ex.getMessage());
      }

      if (plugin == null)
      {
         this.handleError(METHOD_NAME + "Plugin '" + _pluginTemplate + "' is null");
      }

      try
      {
         structOut = plugin.execute(structIn);
      }
      catch (PluginException ex)
      {
         this.handleError(METHOD_NAME + "plugin.execute(): " + ex.getMessage());
      }

      if (structOut == null)
      {
         this.handleError(METHOD_NAME + "Template output is null: "
            + (plugin.getUniqueId() != null ? plugin.getUniqueId().toString() : "(null)"));
      }

      if (structOut.getState() == State.ERROR || structOut.getState() == State.FAILED)
      {
         structStatus = structOut.getChild(StructureIF.NAME_STATUS);
         _context.getConfiguration().logError(METHOD_NAME + structStatus.getValueAsString());
      }

      structDocument = structOut.getChild(StructureIF.NAME_DOCUMENT);
      if (structDocument == null)
      {
         this.handleError(METHOD_NAME + "Document Structure is null");
      }

      body = structDocument.getValueAsString();

      return body;
   }
}
