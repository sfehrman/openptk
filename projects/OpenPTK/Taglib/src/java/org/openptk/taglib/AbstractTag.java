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
package org.openptk.taglib;

import java.util.List;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.Query;
import org.openptk.connection.ConnectionIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class AbstractTag extends SimpleTagSupport
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   protected static final String COOKIE_NAME = "OPENPTKSESSIONID";
   protected static final String INFO_UID = "uniqueid";
   protected static final String INFO_CATEGORY = "category";
   protected static final String INFO_DEBUG = "debug";
   protected static final String INFO_LEVEL = "level";
   protected static final String INFO_DESC = "desc";
   protected static final String INFO_ERROR = "error";
   protected static final String INFO_STATUS = "status";
   protected static final String INFO_STATE = "state";
   protected static final String INFO_STATESTRING = "statestring";
   protected static final String SCOPE_APPLICATION = "application";
   protected static final String SCOPE_SESSION = "session";
   protected static final String SCOPE_REQUEST = "request";
   protected static final String SCOPE_PAGE = "page";
   protected static final String PTK_ERROR_ATTR = "ptkError";
   protected static final String PTK_STATUS_ATTR = "ptkStatus";
   protected static final String CONNECTION_TYPE = "connectiontype";
   protected boolean _debug = false;
   protected boolean _error = false;
   protected int _scope = PageContext.PAGE_SCOPE;
   protected JspContext _jspContext = null;
   protected JspWriter _jspWriter = null;
   protected PageContext _pageContext = null;

   //================================================================
   public AbstractTag()
   //================================================================
   {
      return;
   }

   /**
    * @throws JspException
    */
   //----------------------------------------------------------------
   @Override
   public final void doTag() throws JspException
   //----------------------------------------------------------------
   {
      _jspContext = this.getJspContext();
      _jspWriter = _jspContext.getOut();
      _pageContext = (PageContext) this.getJspContext();

      this.updateError(false);
      this.updateStatus("");

      try
      {
         this.process();
      }
      catch (Exception ex)
      {
         this.updateError(true);
         this.updateStatus(ex.getMessage());
      }

      return;
   }

   /**
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected abstract void process() throws Exception;
   //----------------------------------------------------------------

   /**
    * @param value
    */
   //----------------------------------------------------------------
   public final void setScope(final String value)
   //----------------------------------------------------------------
   {
      if (value.equalsIgnoreCase(SCOPE_PAGE))
      {
         _scope = PageContext.PAGE_SCOPE;
      }
      else if (value.equalsIgnoreCase(SCOPE_SESSION))
      {
         _scope = PageContext.SESSION_SCOPE;
      }
      else if (value.equalsIgnoreCase(SCOPE_APPLICATION))
      {
         _scope = PageContext.APPLICATION_SCOPE;
      }
      return;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   /**
    * @param name
    * @param conn
    */
   //----------------------------------------------------------------
   protected final void setConnection(final String name, final ConnectionIF conn)
   //----------------------------------------------------------------
   {
      /*
       * Save the configuration object
       */

      _jspContext.setAttribute(name, conn, _scope);

      if (conn != null)
      {
         _debug = conn.isDebug();
      }

      return;
   }

   /**
    * @param name
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final ConnectionIF getConnection(final String name) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getConnection(): ";
      ConnectionIF conn = null;

      conn = (ConnectionIF) this.getObject(name);

      if (conn == null)
      {
         throw new Exception(METHOD_NAME + "Connection '" + name + "' is null");
      }

      return conn;
   }

   /**
    * @param name
    * @param elem
    */
   //----------------------------------------------------------------
   protected final void setElement(final String name, final ElementIF elem)
   //----------------------------------------------------------------
   {
      _jspContext.setAttribute(name, elem, _scope);

      if (elem != null)
      {
         this.updateError(elem.isError());
         this.updateStatus(elem.getStatus());
      }

      return;
   }

   /**
    * @param name
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final ElementIF getElement(final String name) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getElement(): ";
      ElementIF elem = null;

      elem = (ElementIF) this.getObject(name);

      if (elem == null)
      {
         throw new Exception(METHOD_NAME + "Element '" + name + "' is null");
      }

      return elem;
   }

   /**
    * @param name
    * @param attr
    */
   //----------------------------------------------------------------
   protected final void setAttribute(final String name, final AttributeIF attr)
   //----------------------------------------------------------------
   {
      _jspContext.setAttribute(name, attr, _scope);

      return;
   }

   /**
    * @param name
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final AttributeIF getAttribute(final String name) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getAttribute(): ";
      AttributeIF attr = null;

      attr = (AttributeIF) this.getObject(name);

      if (attr == null)
      {
         throw new Exception(METHOD_NAME + "Attribute '" + name + "' is null");
      }

      return attr;
   }

   /**
    * @param name
    * @param input
    */
   //----------------------------------------------------------------
   protected final void setInput(final String name, final Input input)
   //----------------------------------------------------------------
   {
      _jspContext.setAttribute(name, input, _scope);

      if (input != null)
      {
         this.updateError(input.isError());
         this.updateStatus(input.getStatus());
      }

      return;
   }

   /**
    * @param name
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final Input getInput(final String name) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getInput(): ";
      Input input = null;

      input = (Input) this.getObject(name);

      if (input == null)
      {
         throw new Exception(METHOD_NAME + "Input '" + name + "' is null.");
      }

      return input;
   }

   /**
    * @param name
    * @param output
    */
   //----------------------------------------------------------------
   protected final void setOutput(final String name, final Output output)
   //----------------------------------------------------------------
   {
      _jspContext.setAttribute(name, output, _scope);

      if (output != null)
      {
         this.updateError(output.isError());
         this.updateStatus(output.getStatus());
      }

      return;
   }

   /**
    * @param name
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final Output getOutput(final String name) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getOutput(): ";
      Output output = null;

      output = (Output) this.getObject(name);

      if (output == null)
      {
         throw new Exception(METHOD_NAME + "Output '" + name + "' is null");
      }

      return output;
   }

   /**
    * @param name
    * @param results
    */
   //----------------------------------------------------------------
   protected final void setResultList(final String name, final List<ElementIF> results)
   //----------------------------------------------------------------
   {
      _jspContext.setAttribute(name, results, _scope);

      return;
   }

   /**
    * @param name
    * @param attrs
    */
   //----------------------------------------------------------------
   protected final void setAttributeList(final String name, final List<AttributeIF> attrs)
   //----------------------------------------------------------------
   {
      _jspContext.setAttribute(name, attrs, _scope);

      return;
   }

   /**
    * @param name
    * @param strings
    */
   //----------------------------------------------------------------
   protected final void setContextList(final String name, final List<String> strings)
   //----------------------------------------------------------------
   {
      _jspContext.setAttribute(name, strings, _scope);

      return;
   }

   /**
    * @param name
    * @param data
    */
   //----------------------------------------------------------------
   protected final void setString(final String name, final String data)
   //----------------------------------------------------------------
   {
      String str = null;

      if (name != null && name.length() > 0)
      {
         if (data == null || data.length() < 1)
         {
            str = "";
         }
         else
         {
            str = data;
         }
         _jspContext.setAttribute(name, str, _scope);
      }

      return;
   }

   /**
    * @param name
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final String getString(final String name) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getString(): ";
      String str = null;
      Object obj = null;

      if (name != null && name.length() > 0)
      {
         obj = this.getObject(name);

         if (obj == null)
         {
            throw new Exception(METHOD_NAME + "String '" + name + "' is null");
         }

         if (obj instanceof String)
         {
            str = (String) obj;
         }
         else
         {
            throw new Exception(METHOD_NAME + "Object '" + name + "' is not a String");
         }
      }

      if (str == null)
      {
         throw new Exception(METHOD_NAME + "String name is not set");
      }

      return str;
   }

   /**
    * @param name
    * @param data
    */
   //----------------------------------------------------------------
   protected final void setStringArray(final String name, final String[] data)
   //----------------------------------------------------------------
   {
      String[] array = null;

      if (name != null && name.length() > 0)
      {
         if (data == null)
         {
            array = new String[0];
         }
         else
         {
            array = data;
         }
         _jspContext.setAttribute(name, array, _scope);
      }

      return;
   }

   /**
    * @param name
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final String[] getStringArray(final String name) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getStringArray(): ";
      String[] str = null;
      Object obj = null;

      if (name != null && name.length() > 0)
      {
         obj = this.getObject(name);
         if (obj == null)
         {
            throw new Exception(METHOD_NAME + "StringArray '" + name + "' is null");
         }

         if (obj instanceof String[])
         {
            str = (String[]) obj;
         }
         else
         {
            throw new Exception(METHOD_NAME + "Object '" + name + "' is not a StringArray");
         }
      }

      if (str == null)
      {
         throw new Exception(METHOD_NAME + "StringArray name is not set");
      }

      return str;
   }

   /**
    * @param name
    * @param query
    */
   //----------------------------------------------------------------
   protected final void setQuery(final String name, final Query query)
   //----------------------------------------------------------------
   {
      if (name != null && name.length() > 0 && query != null)
      {
         _jspContext.setAttribute(name, query, _scope);
      }

      return;
   }

   /**
    * @param name
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected final Query getQuery(final String name) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getQuery(): ";
      Object obj = null;
      Query query = null;

      if (name != null && name.length() > 0)
      {
         obj = this.getObject(name);
         if (obj == null)
         {
            throw new Exception(METHOD_NAME + "Query '" + name + "' is null");
         }

         if (obj instanceof Query)
         {
            query = (Query) obj;
         }
         else
         {
            throw new Exception(METHOD_NAME + "Object '" + name + "' is not a Query");
         }
      }

      if (query == null)
      {
         throw new Exception(METHOD_NAME + "Query name is not set");
      }

      return query;
   }

   /**
    * @param error
    */
   //----------------------------------------------------------------
   protected final void updateError(final boolean error)
   //----------------------------------------------------------------
   {
      _jspContext.setAttribute(
         AbstractTag.PTK_ERROR_ATTR,
         Boolean.toString(error),
         PageContext.SESSION_SCOPE);

      return;
   }

   /**
    * @param status
    */
   //----------------------------------------------------------------
   protected final void updateStatus(final String status)
   //----------------------------------------------------------------
   {
      _jspContext.setAttribute(
         AbstractTag.PTK_STATUS_ATTR,
         status,
         PageContext.SESSION_SCOPE);

      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   protected final boolean isDebug()
   //----------------------------------------------------------------
   {
      return _debug;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private Object getObject(final String name)
   //----------------------------------------------------------------
   {
      Object obj = null;

      /*
       * Check for it in these "scopes"
       * 1: PAGE
       * 2: SESSION
       * 3: APPLICATION
       */

      obj = _jspContext.getAttribute(name, PageContext.PAGE_SCOPE);
      if (obj == null)
      {
         obj = _jspContext.getAttribute(name, PageContext.SESSION_SCOPE);
         if (obj == null)
         {
            obj = _jspContext.getAttribute(name, PageContext.APPLICATION_SCOPE);
         }
      }

      return obj;
   }
}
