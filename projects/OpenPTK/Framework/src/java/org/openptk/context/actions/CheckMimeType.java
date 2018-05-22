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
package org.openptk.context.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.openptk.api.State;
import org.openptk.common.AttrIF;
import org.openptk.common.ComponentIF;
import org.openptk.common.RequestIF;
import org.openptk.exception.ActionException;
import org.openptk.logging.Logger;
import org.openptk.spi.ServiceIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 *
 * Check the "type" it must be a valid mime-type and either in the
 * "allow" list or NOT in the "deny" list
 *
 * "type" is an attribute that should be in the Request
 * it should either be explicitly set or set via a Function
 *
 * Properties:
 *    plugin.name = "mimeutil"
 *
 *    mimetype.allow = "image/png, image/jpg, image/gif"
 *    -OR-
 *    mimetype.deny = "application/exe"
 *
 *    attribute.type = "type"
 */
//===================================================================
public class CheckMimeType extends Action
//===================================================================
{
   private boolean _modeAllow = false;
   private boolean _modeDeny = false;
   private Map<String, List<String>> _mimeTypes = null;
   private String _attrNameType = null;
   private final String CLASS_NAME = this.getClass().getSimpleName();
   public static final String DELIM_CHARS = ", ";
   public static final String PROP_MIMETYPE_ALLOW = "mimetype.allow";
   public static final String PROP_MIMETYPE_DENY = "mimetype.deny";
   public static final String PROP_ATTRIBUTE_TYPE = "attribute.type";

   //----------------------------------------------------------------
   public CheckMimeType()
   //----------------------------------------------------------------
   {
      super();
      this.setDescription("Check data for valid mime type");
      _mimeTypes = new HashMap<String, List<String>>();
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
      String METHOD_NAME = CLASS_NAME + ":startup(): ";
      String prop = null;

      prop = this.getProperty(PROP_MIMETYPE_ALLOW);
      if (prop != null && prop.length() > 0)
      {
         _modeAllow = true;

         this.parseProps(prop);
      }

      prop = null;
      prop = this.getProperty(PROP_MIMETYPE_DENY);
      if (prop != null && prop.length() > 0)
      {
         _modeDeny = true;
         if (_modeAllow)
         {
            this.handleError(METHOD_NAME + "Can not have both Properties: '" +
               PROP_MIMETYPE_ALLOW + "' and '" + PROP_MIMETYPE_DENY + "'");
         }

         this.parseProps(prop);
      }

      /*
       * Make sure we have at least one
       */

      if (!_modeAllow && !_modeDeny)
      {
         this.handleError(METHOD_NAME + "Missing Required Property: '" +
            PROP_MIMETYPE_ALLOW + "' or '" + PROP_MIMETYPE_DENY + "'");
      }

      /*
       * Get Property that has the attribute name for the "type"
       */

      prop = null;
      prop = this.getProperty(PROP_ATTRIBUTE_TYPE);
      if (prop == null || prop.length() < 1)
      {
         this.handleError(METHOD_NAME + "Missing Required Property: '" +
            PROP_ATTRIBUTE_TYPE + "'");
      }

      _attrNameType = prop;

      this.setState(State.READY);

      return;
   }


   /**
    * @param service
    * @param request
    * @throws ActionException
    */
   //----------------------------------------------------------------
   @Override
   public void preAction(final ServiceIF service, final RequestIF request) throws ActionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":preAction(): ";
      String type = null;
      String media = null;
      String sub = null;
      String[] array = null;
      ComponentIF subject = null;
      AttrIF attr = null;

      /*
       * Check the Request for an attribute that has the name "_attrTypeName"
       * If it exists and has a value, check to see if it is valid.
       *
       * If mode = "allow" then the value MUST match one the entries in
       * the map/list
       *
       * If mode = "deny" then the value MUST NOT match any of the entries
       * in the map/list
       *
       *
       */

      if (_context.isDebug())
      {
         Logger.logInfo(METHOD_NAME + "BEGIN: Context=" +
            _context.toString() + ": Operation=" +
            request.getOperation().toString());
      }

      if (_attrNameType == null || _attrNameType.length() < 1)
      {
         this.handleError(METHOD_NAME + "Attribute name for 'type' is null");
      }

      subject = request.getSubject();
      attr = subject.getAttribute(_attrNameType);

      type = attr.getValueAsString();
      if (type == null || type.length() < 1)
      {
         this.handleError(METHOD_NAME + "Attribute '" + _attrNameType +
            "' has a null value.");
      }

      if (_context.isDebug())
      {
         Logger.logInfo(METHOD_NAME + "Mime-Type='" + type +
            "', Map=" + _mimeTypes.toString());
      }

      array = type.split("/");
      if (array == null || array.length != 2)
      {
         this.handleError(METHOD_NAME + "Attribute '" + _attrNameType +
            "' has an invalid format, must be: <mediaType>/<subType>");
      }

      media = array[0];
      if (media == null || media.length() < 1)
      {
         this.handleError(METHOD_NAME + "Attribute '" + _attrNameType +
            "' has a null MediaType");
      }

      sub = array[1];
      if (sub == null || sub.length() < 1)
      {
         this.handleError(METHOD_NAME + "Attribute '" + _attrNameType +
            "' has a null SubType");
      }

      if (!this.checkType(media, sub))
      {
         this.handleError(METHOD_NAME + "Mime-Type '" + type + "' is not allowed");
      }

      if (_context.isDebug())
      {
         Logger.logInfo(METHOD_NAME + "END: Context=" +
            _context.toString() + ": Operation=" +
            request.getOperation().toString());
      }

      return;
   }

   //----------------------------------------------------------------
   private void parseProps(final String props)
   //----------------------------------------------------------------
   {
      String prop = null;
      String mediaType = null;
      String subType = null;
      String[] array = null;
      StringTokenizer tokenizer = null;

      /*
       * props should be a String with a comma/space separated list
       * of mime-types:  "image/png, image/jpg, image/gif"
       *
       * each mime-type is actual a combination of a "media" and "sub" type
       * such as <media>/<sub>
       *
       * extract the media and sub types and populate the "map" of "lists"
       */

      tokenizer = new StringTokenizer(props, DELIM_CHARS);
      while (tokenizer.hasMoreTokens())
      {
         prop = tokenizer.nextToken();
         array = prop.split("/");
         if (array.length >= 1)
         {
            mediaType = array[0];
            if (mediaType != null && mediaType.length() > 0)
            {
               if (!_mimeTypes.containsKey(mediaType))
               {
                  _mimeTypes.put(mediaType, new ArrayList<String>());
               }
               if (array.length >= 2)
               {
                  subType = array[1];
                  if (subType != null && subType.length() > 0)
                  {
                     if (!_mimeTypes.get(mediaType).contains(subType))
                     {
                        _mimeTypes.get(mediaType).add(subType);
                     }
                  }
               }
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private boolean checkType(final String media, final String sub)
   //----------------------------------------------------------------
   {
      boolean pass = false;
      boolean found = false;

      if (_mimeTypes.containsKey(media))
      {
         if (_mimeTypes.get(media).contains(sub))
         {
            found = true;
         }
      }

      if (_modeAllow)
      {
         if (found)
         {
            pass = true;
         }
      }
      else
      {
         if (!found)
         {
            pass = true;
         }
      }

      return pass;
   }
}
