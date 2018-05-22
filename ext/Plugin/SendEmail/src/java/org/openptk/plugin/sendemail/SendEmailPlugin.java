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
 * Portions Copyright 2011-2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.plugin.sendemail;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Security;
import java.util.Iterator;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.net.ssl.internal.ssl.Provider;

import org.openptk.api.State;
import org.openptk.exception.PluginException;
import org.openptk.logging.Logger;
import org.openptk.plugin.Plugin;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;
import org.openptk.structure.StructureType;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class SendEmailPlugin extends Plugin
//===================================================================
{
   private boolean _online = true;
   private boolean _useAuth = false;
   private boolean _includeBody = false;
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _file = null;
   private Session _session = null;
   private static final String PROP_MAIL_FROM = "mail.from";
   private static final String PROP_SENDEMAIL_ONLINE = "sendemail.online";
   private static final String PROP_SENDEMAIL_FILE = "sendemail.file";
   private static final String PROP_SENDEMAIL_FILE_INCLUDE_BODY = "sendemail.file.include.body";
   private static final String PROP_SENDEMAIL_AUTHEN_USER = "sendemail.authen.user";
   private static final String PROP_SENDEMAIL_AUTHEN_PASSWORD = "sendemail.authen.password";
   private static final String PROP_MAIL_SMTP_AUTH = "mail.smtp.auth";
   private static final String MSG_MIME_TYPE = "text/html";
   private static final String JAVAMAIL_PROP_PREFIX = "mail.";

   //----------------------------------------------------------------
   public SendEmailPlugin()
   //----------------------------------------------------------------
   {
      super();
      this.setDescription("Send emails using JavaMail API (use file if offline/error)");
      return;
   }

   /**
    * @throws PluginException
    */
   //----------------------------------------------------------------
   @Override
   public void startup() throws PluginException
   //----------------------------------------------------------------
   {
      Object obj = null;
      String METHOD_NAME = CLASS_NAME + ":startup(): ";
      String temp = null;
      String propName = null;
      String propValue = null;
      String authenUser = null;
      String authenPasswd = null;
      Properties mailProps = null;
      Set<Object> mailSet = null;
      Iterator<Object> iter = null;
      PluginAuthenticator authen = null;

      super.startup();

      temp = this.getProperty(PROP_OPENPTK_TEMP);
      if (temp == null || temp.length() < 1)
      {
         this.handleError(METHOD_NAME + "Property '" + PROP_OPENPTK_TEMP + "' is null/empty");
      }

      propValue = this.getProperty(PROP_SENDEMAIL_ONLINE);
      if (propValue != null && propValue.length() > 0)
      {
         _online = Boolean.parseBoolean(propValue);
      }

      propValue = this.getProperty(PROP_SENDEMAIL_FILE);

      if (propValue != null && propValue.length() > 0)
      {
         if (temp.endsWith(System.getProperty("file.separator")))
         {
            _file = temp + propValue;
         }
         else
         {
            _file = temp + System.getProperty("file.separator") + propValue;
         }
         _includeBody = Boolean.parseBoolean(this.getProperty(PROP_SENDEMAIL_FILE_INCLUDE_BODY));
      }

      /*
       * Get the "authen" properties (if they exist)
       */

      authenUser = this.getProperty(PROP_SENDEMAIL_AUTHEN_USER);
      authenPasswd = this.getProperty(PROP_SENDEMAIL_AUTHEN_PASSWORD);

      /*
       * Get just the "mail.*" properties
       */

      mailProps = new Properties();

      mailSet = this.getProperties().keySet();
      iter = mailSet.iterator();
      while (iter.hasNext())
      {
         propName = null;
         propValue = null;
         obj = iter.next();
         if (obj instanceof String)
         {
            propName = (String) obj;
            if (propName.startsWith(JAVAMAIL_PROP_PREFIX))
            {
               propValue = this.getProperty(propName);
               if (propValue != null && propValue.length() > 0)
               {
                  mailProps.setProperty(propName, propValue);
               }
            }
         }
      }

      _useAuth = Boolean.parseBoolean(mailProps.getProperty(PROP_MAIL_SMTP_AUTH));

      Security.addProvider(new Provider());

      if (authenUser != null && authenUser.length() > 0
         && authenPasswd != null && authenPasswd.length() > 0
         && _useAuth)
      {
         authen = new PluginAuthenticator(authenUser, authenPasswd);
         _session = Session.getInstance(mailProps, authen); // was getDefaultInstance 
      }
      else
      {
         _session = Session.getInstance(mailProps, null); // was getDefaultInstance
      }

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
      StructureIF structOut = null;
      MimeMessage msg = null;

      if (_session == null)
      {
         this.handleError(METHOD_NAME + "Startup problem, javax.mail.Session is null");
      }

      msg = new MimeMessage(_session);

      if (msg == null)
      {
         this.handleError(METHOD_NAME + "MimeMessage is null");
      }

      this.setFrom(msg, structIn);
      this.setTo(msg, structIn);
      this.setSubject(msg, structIn);
      this.setDate(msg);
      this.setBody(msg, structIn);

      if (_online)
      {
         try
         {
            Transport.send(msg);
            structOut = new BasicStructure(StructureIF.NAME_STATUS, "Message Sent to Server");
         }
         catch (MessagingException ex)
         {
            if (_file != null && _file.length() > 0)
            {
               this.sendToFile(msg);
            }
            this.setState(State.FAILED);
            this.setStatus(ex.getMessage());
            Logger.logError(ex.getMessage());
         }
      }
      else
      {
         if (_file != null && _file.length() > 0)
         {
            this.sendToFile(msg);
         }
         this.setState(State.READY);
      }

      if (structOut == null)
      {
         if (_file != null && _file.length() > 0)
         {
            structOut = new BasicStructure(StructureIF.NAME_STATUS, "Message Sent to File: " + _file);
            this.setStatus("Last Message sent to File: " + (new Date()).toString());
         }
         else
         {
            structOut = new BasicStructure(StructureIF.NAME_STATUS, "Message not sent, file is null");
            this.setStatus("Message not sent, file is null: " + (new Date()).toString());
         }
      }
      else
      {
         this.setState(State.READY);
         this.setStatus("Last Message sent to Server: " + (new Date()).toString());
      }

      structOut.setState(State.SUCCESS);

      return structOut;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private void setFrom(final MimeMessage msg, final StructureIF structIn) throws PluginException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getFrom(): ";
      String name = null;
      String value = null;
      StructureIF struct = null;
      Address addr = null;

      /*
       * "from", structure value overrides a property value
       *
       */

      name = StructureIF.NAME_FROM;
      struct = structIn.getChild(name);
      if (struct == null)
      {
         value = this.getProperty(PROP_MAIL_FROM);
         if (value == null || value.length() < 1)
         {
            this.handleError(METHOD_NAME + "From address is required. "
               + "Either set Property '" + PROP_MAIL_FROM
               + "', and/or provide a structure called '" + name + "'");
         }
      }
      else
      {
         if (struct.getValueType() != StructureType.STRING)
         {
            this.handleError(METHOD_NAME + "Value Type for '" + name + "' must be a String");
         }
         value = struct.getValueAsString();
         if (value == null || value.length() < 1)
         {
            this.handleError(METHOD_NAME + "Child Structure '" + name + "' is null");
         }
      }
      try
      {
         addr = new InternetAddress(value);
      }
      catch (AddressException ex)
      {
         this.handleError(METHOD_NAME + "Problem with 'From' address '" + value
            + "': " + ex.getMessage());
      }
      catch (MessagingException ex)
      {
         this.handleError(METHOD_NAME + "Problem with 'From' address '" + value
            + "': " + ex.getMessage());
      }

      try
      {
         msg.setFrom(addr);
      }
      catch (MessagingException ex)
      {
         this.handleError(METHOD_NAME + "msg.setFrom(): " + ex.getMessage());
      }

      return;
   }

   //----------------------------------------------------------------
   private void setTo(final MimeMessage msg, final StructureIF structIn) throws PluginException
   //----------------------------------------------------------------
   {
      Object[] objects = null;
      String METHOD_NAME = CLASS_NAME + ":getTo(): ";
      String name = null;
      String value = null;
      StructureIF struct = null;
      Address[] addrs = null;

      name = StructureIF.NAME_TO;
      struct = structIn.getChild(name);
      if (struct == null)
      {
         this.handleError(METHOD_NAME + "Child Structure '" + name + "' is null");
      }

      if (struct.getValueType() != StructureType.STRING)
      {
         this.handleError(METHOD_NAME + "Value Type for '" + name + "' must be a String");
      }
      objects = struct.getValuesAsArray();
      if (objects == null || objects.length < 1)
      {
         this.handleError(METHOD_NAME + "Must be at least one 'to' address");
      }

      addrs = new InternetAddress[objects.length];

      for (int i = 0; i < objects.length; i++)
      {
         try
         {
            addrs[i] = new InternetAddress((String) objects[i]);
         }
         catch (AddressException ex)
         {
            this.handleError(METHOD_NAME + "Problem with 'To' address '" + value
               + "': " + ex.getMessage());
         }
      }

      try
      {
         msg.setRecipients(RecipientType.TO, addrs);
      }
      catch (MessagingException ex)
      {
         this.handleError(METHOD_NAME + "msg.setRecipients(): " + ex.getMessage());
      }

      return;
   }

   //----------------------------------------------------------------
   private void setDate(final MimeMessage msg) throws PluginException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":setDate(): ";

      try
      {
         msg.setSentDate(new Date());
      }
      catch (MessagingException ex)
      {
         this.handleError(METHOD_NAME + "msg.setSentDate(): " + ex.getMessage());
      }

      return;
   }

   //----------------------------------------------------------------
   private void setSubject(final MimeMessage msg, final StructureIF structIn) throws PluginException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getSubject(): ";
      String name = null;
      String subject = null;
      StructureIF struct = null;

      name = StructureIF.NAME_SUBJECT;
      struct = structIn.getChild(name);
      if (struct == null)
      {
         this.handleError(METHOD_NAME + "Child Structure '" + name + "' is null");
      }

      if (struct.getValueType() != StructureType.STRING)
      {
         this.handleError(METHOD_NAME + "Value Type for '" + name + "' must be a String");
      }

      subject = struct.getValueAsString();
      if (subject == null || subject.length() < 1)
      {
         this.handleError(METHOD_NAME + "Subject is null");
      }

      try
      {
         msg.setSubject(subject);
      }
      catch (MessagingException ex)
      {
         this.handleError(METHOD_NAME + "msg.setSubject:(): " + ex.getMessage());
      }

      return;
   }

   //----------------------------------------------------------------
   private void setBody(final MimeMessage msg, final StructureIF structIn) throws PluginException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getSubject(): ";
      String name = null;
      String body = null;
      StructureIF struct = null;

      name = StructureIF.NAME_BODY;
      struct = structIn.getChild(name);
      if (struct == null)
      {
         this.handleError(METHOD_NAME + "Child Structure '" + name + "' is null");
      }

      if (struct.getValueType() != StructureType.STRING)
      {
         this.handleError(METHOD_NAME + "Value Type for '" + name + "' must be a String");
      }

      body = struct.getValueAsString();
      if (body == null || body.length() < 1)
      {
         this.handleError(METHOD_NAME + "Body is null");
      }

      try
      {
         msg.setContent(body, MSG_MIME_TYPE);
      }
      catch (MessagingException ex)
      {
         this.handleError(METHOD_NAME + "msg.setContent(): " + ex.getMessage());
      }

      return;
   }

   //----------------------------------------------------------------
   private void sendToFile(final MimeMessage msg) throws PluginException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":sendToFile(): ";
      StringBuffer buf = null;
      Address[] addrs = null;
      FileOutputStream fos = null;

      try
      {
         fos = new FileOutputStream(_file, true);
      }
      catch (FileNotFoundException ex)
      {
         this.handleError(METHOD_NAME + "FileOutputStream: " + ex.getMessage());
      }

      buf = new StringBuffer();
      try
      {
         buf.append("--------------------------------------------------------\n");
         buf.append("   Date: ").append(msg.getSentDate().toString()).append("\n");

         buf.append("   From: ");
         addrs = msg.getFrom();
         for (int i = 0; i < addrs.length; i++)
         {
            buf.append(addrs[i].toString());
            if (i < (addrs.length - 1))
            {
               buf.append(", ");
            }
         }
         buf.append("\n");

         buf.append("     To: ");
         addrs = msg.getRecipients(RecipientType.TO);
         for (int i = 0; i < addrs.length; i++)
         {
            buf.append(addrs[i].toString());
            if (i < (addrs.length - 1))
            {
               buf.append(", ");
            }
         }
         buf.append("\n");

         buf.append("Subject: ").append(msg.getSubject()).append("\n");

         if (_includeBody)
         {
            buf.append("   Body:\n").append(msg.getContent().toString()).append("\n");
         }
         else
         {
            buf.append("   Body: (not included)\n");
         }
      }
      catch (MessagingException ex)
      {
         this.handleError(METHOD_NAME + "buf.append(): " + ex.getMessage());
      }
      catch (IOException ex)
      {
         this.handleError(METHOD_NAME + "buf.append(): " + ex.getMessage());
      }

      try
      {
         fos.write(buf.toString().getBytes());
         fos.close();
      }
      catch (IOException ex)
      {
         this.handleError(METHOD_NAME + "write/close error: " + ex.getMessage()
            + ", file='" + _file + "'");
      }
   }

   //===================================================================
   public class PluginAuthenticator extends Authenticator
   //===================================================================
   {
      private String _user = null;
      private String _password = null;

      /**
       * @param user
       * @param password
       */
      //----------------------------------------------------------------
      public PluginAuthenticator(final String user, final String password)
      //----------------------------------------------------------------
      {
         super();
         _user = user;
         _password = password;
         return;
      }

      /**
       * @return
       */
      //----------------------------------------------------------------
      @Override
      protected PasswordAuthentication getPasswordAuthentication()
      //----------------------------------------------------------------
      {
         return new PasswordAuthentication(_user, _password);
      }
   }
}
