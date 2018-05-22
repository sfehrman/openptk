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
package org.openptk.client;

import java.util.LinkedList;
import java.util.List;

import org.openptk.common.Category;
import org.openptk.common.Component;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Client extends Component implements ClientIF
//===================================================================
{

   private String _secret = null;
   private String _defaultContextId = null;
   private List<String> _authenticatorIds = null;
   private List<String> _contextIds = null;


   /**
    * Create a Client using the unique id.
    * @param uniqueId
    */
   //----------------------------------------------------------------
   public Client(final String uniqueId)
   //----------------------------------------------------------------
   {
      this.init(uniqueId, null);
      return;
   }


   /**
    * Create a Client using the unique id and secret.
    * @param uniqueId
    * @param secret
    */
   //----------------------------------------------------------------
   public Client(final String uniqueId, final String secret)
   //----------------------------------------------------------------
   {
      this.init(uniqueId, secret);
      return;
   }

   /**
    * Set the secret.
    * @param str
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setSecret(final String str) throws Exception
   //----------------------------------------------------------------
   {
      if (str != null && str.length() > 0)
      {
         _secret = str;
      }
      else
      {
         throw new Exception("Secret can not be null");
      }
      return;
   }

   /**
    * Get the secret.
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String getSecret()
   //----------------------------------------------------------------
   {
      return _secret;
   }

   /**
    * Add a context.
    * @param contextId
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addContextId(final String contextId)
   //----------------------------------------------------------------
   {
      if (contextId != null && contextId.length() > 0)
      {
         _contextIds.add(contextId);
      }
      return;
   }

   /**
    * Get all the contexts.
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getContextIds()
   //----------------------------------------------------------------
   {
      return _contextIds.toArray(new String[_contextIds.size()]);
   }

   /**
    * Set the default context.
    * @param contextId
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setDefaultContextId(final String contextId)
   //----------------------------------------------------------------
   {
      _defaultContextId = contextId;
      return;
   }

   /**
    * Get the default context.
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String getDefaultContextId()
   //----------------------------------------------------------------
   {
      return _defaultContextId;
   }

   /**
    * Add an authenticator.
    * @param authenId
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void addAuthenticatorId(final String authenId)
   //----------------------------------------------------------------
   {
      if (authenId != null && authenId.length() > 0)
      {
         _authenticatorIds.add(authenId);
      }
      return;
   }

   /**
    * Get all the authenticators.
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getAuthenticatorIds()
   //----------------------------------------------------------------
   {
      String[] array = null;

      if ( _authenticatorIds != null && _authenticatorIds.size() > 0)
      {
         array = _authenticatorIds.toArray(new String[_authenticatorIds.size()]);
      }
      else
      {
         array = new String[0];
      }

      return array;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private void init(final String uniqueId, final String secret)
   //----------------------------------------------------------------
   {
      _authenticatorIds = new LinkedList<String>();
      _contextIds = new LinkedList<String>();
      _defaultContextId = "";

      if (secret != null && secret.length() > 0)
      {
         _secret = secret;
      }

      this.setUniqueId(uniqueId);
      this.setDescription("Abstract Client");
      this.setCategory(Category.CLIENT);

      return;
   }
}
