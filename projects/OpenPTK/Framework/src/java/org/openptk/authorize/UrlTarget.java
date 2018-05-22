/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010 Project OpenPTK
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
package org.openptk.authorize;

import java.net.MalformedURLException;
import java.net.URL;

import org.openptk.api.Opcode;
import org.openptk.api.State;
import org.openptk.exception.AuthorizationException;

/**
 *
 * Component with properties for an authorization target
 *
 * @author Derrick Harcey
 */
//===================================================================
public class UrlTarget extends Target
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * Generate a Component with the supplied URL
    *
    * The component will be set with the following properties
    *
    * "PROTOCOL"     -  URL protocol
    * "SERVER"       -  URL server with domain
    * "PORT"         -  URL port
    * "CONTEXTPATH"  -  URL context path
    * "RELATIVEPATH" -  URL relative path
    * "FULLURL"      -  full URL
    *
    * @param URL String of a full URL
    * @return
    */
   //----------------------------------------------------------------
   public UrlTarget(String url) throws AuthorizationException
   //----------------------------------------------------------------
   {
      super();

      if (url != null && url.length() > 0)
      {
         this.init(url);
      }
      else
      {
         throw new AuthorizationException(CLASS_NAME + "url is null");
      }

      return;
   }

   //----------------------------------------------------------------
   public UrlTarget(String url, Opcode opcode) throws AuthorizationException
   //----------------------------------------------------------------
   {
      super();

      if (url != null && url.length() > 0)
      {
         if (opcode != null)
         {
            this.init(url);
            _opcode = opcode;
         }
         else
         {
            throw new AuthorizationException(CLASS_NAME + ": opcode is null");
         }
      }
      else
      {
         throw new AuthorizationException(CLASS_NAME + "url is null");
      }

      return;
   }

   /**
    * Do not allow this class to be instanciated without the UrlTarget(String tURL) constructor
    */
   //----------------------------------------------------------------
   public UrlTarget()
   //----------------------------------------------------------------
   {
      throw new AssertionError();
   }

   /**
    * Do not allow this class to be instanciated without the UrlTarget(String tURL) constructor
    */
   //----------------------------------------------------------------
   public UrlTarget(final TargetIF target)
   //----------------------------------------------------------------
   {
      throw new AssertionError();
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private void init(String url) throws AuthorizationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":init(): ";
      String path = null;
      String contextroot = null;
      String[] pathParts = null;
      URL targetURL = null;

      try
      {
         targetURL = new URL(url);
      }
      catch (MalformedURLException ex)
      {
         throw new AuthorizationException(METHOD_NAME + ex.getMessage());
      }

      this.setDescription("Target used for authorization");
      this.setState(State.VALID);
      this.setType(TargetType.URI);

      this.setProperty(PROP_FULL_URL, url);
      this.setProperty(PROP_PROTOCOL, targetURL.getProtocol());
      this.setProperty(PROP_SERVER, targetURL.getHost());
      this.setProperty(PROP_PORT, Integer.toString(targetURL.getPort()));

      path = targetURL.getPath();
      pathParts = path.split("/");
      contextroot = "/" + pathParts[1];

      this.setProperty(PROP_CONTEXTPATH, "/" + pathParts[1]);
      this.setProperty(PROP_RELATIVEPATH, path.substring(contextroot.length()));

      return;
   }
}
