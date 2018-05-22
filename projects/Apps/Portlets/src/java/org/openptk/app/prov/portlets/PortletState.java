/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2009 Sun Microsystems, Inc.
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
package org.openptk.app.prov.portlets;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletMode;

//===================================================================
public class PortletState
//===================================================================
{

   private boolean _reset = true;
   private boolean _modified = false;
   private boolean _error = false;
   private String _id = null;
   private String _pageView = null;
   private String _pageEdit = null;
   private String _pageHelp = null;
   private String _portalUserId = null;
   private String _ptkUserId = null;
   private Map<String, String> _attributes = null;
   private PortletMode _mode = null;

   //----------------------------------------------------------------
   public PortletState(String Id)
   //----------------------------------------------------------------
   {
      _id = Id;
      _attributes = new HashMap<String, String>();
      _mode = PortletMode.VIEW;
      return;
   }

   public void setId(String id)
   {
      _id = id;
      return;
   }

   public String getId()
   {
      return _id;
   }

   public void setMode(PortletMode mode)
   {
      _mode = mode;
      _modified = true;
      return;
   }

   public PortletMode getMode()
   {
      return _mode;
   }

   public String getModeAsString()
   {
      return _mode.toString();
   }

   public void setPageView(String page)
   {
      _pageView = page;
      _modified = true;
      return;
   }

   public String getPageView()
   {
      return _pageView;
   }

   public void setPageEdit(String page)
   {
      _pageEdit = page;
      _modified = true;
      return;
   }

   public String getPageEdit()
   {
      return this._pageEdit;
   }

   public void setPageHelp(String page)
   {
      _pageHelp = page;
      _modified = true;
      return;
   }

   public String getPageHelp()
   {
      return _pageHelp;
   }

   public void setAttributes(Map<String, String> attributes)
   {
      _attributes = attributes;
      _modified = true;
      return;
   }

   public Map<String, String> getAttributes()
   {
      return _attributes;
   }

   public void putAttribute(String key, String value)
   {
      _attributes.put(key, value);
      _modified = true;
      return;
   }

   public String getAttribute(String key)
   {
      return _attributes.get(key);
   }

   public void setReset(boolean reset)
   {
      _reset = reset;
      return;
   }

   public boolean isReset()
   {
      return _reset;
   }

   public void setModified(boolean modified)
   {
      _modified = modified;
      return;
   }

   public boolean isModified()
   {
      return _modified;
   }

   public void setError(boolean error)
   {
      _error = error;
      return;
   }

   public boolean isError()
   {
      return _error;
   }

   public void setPortalUserId(String userId)
   {
      _portalUserId = userId;
      return;
   }

   public String getPortalUserId()
   {
      return _portalUserId;
   }

   public void setPtkUserId(String userId)
   {
      _ptkUserId = userId;
      return;
   }

   public String getPtkUserId()
   {
      return _ptkUserId;
   }
}
