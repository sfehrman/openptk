/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2012 Project OpenPTK
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * trunk/openptk/resource/legal-notices/OpenPTK.LICENSE.
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
package org.openptk.servlet.filters;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.openptk.logging.Logger;

/**
 *
 * @author Scott Fehrman, Project OpenPTK
 */
//===================================================================
public class MimeTypeHeaderRequestWrapper extends HttpServletRequestWrapper
//===================================================================
{

   public enum Mode
   {

      DISABLED, PARAMETER, SUFFIX, BOTH
   };
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final int EMPTY = -1;
   private static final String NULL = "(NULL)";
   private static final String AMP = "&";
   private static final String EQUAL = "=";
   private static final String METHOD_POST = "POST";
   private static final String METHOD_PUT = "PUT";
   private static final String PARAMETER_FORMAT = "format";
   private static final String HEADER_CONTENT_TYPE = "content-type";
   private static final String HEADER_ACCEPT = "accept";
   private static final String[] _formats =
   {
      "xml", "json", "html", "plain"
   };
   private static final String[] _suffixes =
   {
      ".xml", ".json", ".html", ".plain"
   };
   private static final String[] _mimeTypes =
   {
      "application/xml", "application/json", "text/html", "text/plain"
   };
   private boolean _debug = false;
   private int _debugLevel = 0;
   private int _index = EMPTY;
   private String _body = "";
   private String _pathInfo = null;
   private String _requestURI = null;
   private StringBuffer _requestURL = null;
   private Map<String, String[]> _parameterMap = null;
   private Mode _mode = Mode.DISABLED;

   //----------------------------------------------------------------
   MimeTypeHeaderRequestWrapper(HttpServletRequest request, Mode mode)
   //----------------------------------------------------------------
   {
      super(request);

      _mode = mode;

      this.initialize(request);

      return;
   }

   //----------------------------------------------------------------
   MimeTypeHeaderRequestWrapper(HttpServletRequest request, Mode mode, int debugLevel)
   //----------------------------------------------------------------
   {
      super(request);

      _mode = mode;
      _debugLevel = debugLevel;

      if (_debugLevel > 2)
      {
         _debug = true;
      }

      this.initialize(request);

      return;
   }

   //----------------------------------------------------------------
   @Override
   public String getHeader(String name)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String value = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>: name='" + (name != null ? name : NULL) + "'");
      }
      /*
       * If "format" is set (not null), then set the associated "mimetype" as
       * a String. Else, let the parent method return a value.
       */

      if (name != null && _index != EMPTY
              && (name.equalsIgnoreCase(HEADER_ACCEPT) || name.equalsIgnoreCase(HEADER_CONTENT_TYPE)))
      {
         value = _mimeTypes[_index];
      }
      else
      {
         value = super.getHeader(name);
      }


      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + "<<<: String: '" + (value != null ? value : NULL) + "'");
      }

      return value;
   }

   //----------------------------------------------------------------
   @Override
   @SuppressWarnings("unchecked")
   public Enumeration<String> getHeaders(String name)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      List<String> list = null;
      List<String> output = null;
      Enumeration<String> enumHdrs = null;
      Enumeration<String> enumDebug = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>: "
                 + "name='" + (name != null ? name : NULL) + "'");
      }
      /*
       * If "format" is set (index not empty), then return the associated "mimetype" as
       * an Enumeration from a List. Else, let the parent method return a value.
       */

      if (name != null && _index != EMPTY
              && (name.equalsIgnoreCase(HEADER_ACCEPT) || name.equalsIgnoreCase(HEADER_CONTENT_TYPE)))
      {
         list = new ArrayList<String>();
         list.add(_mimeTypes[_index]);
         enumHdrs = Collections.enumeration(list);
         if (_debug)
         {
            enumDebug = Collections.enumeration(list);
         }
      }
      else
      {
         enumHdrs = super.getHeaders(name);
         if (_debug)
         {
            enumDebug = super.getHeaders(name);
         }
      }

      if (_debug)
      {
         output = Collections.list(enumDebug);
         Logger.logInfo(METHOD_NAME + "<<<: Enumeration<String>: " + (output != null ? output : NULL));
      }

      return enumHdrs;
   }

   //----------------------------------------------------------------
   @Override
   @SuppressWarnings("unchecked")
   public Enumeration<String> getHeaderNames()
   //----------------------------------------------------------------
   {
      boolean needAccept = true;
      boolean needContentType = true;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String str = null;
      List<String> list = null;
      List<String> output = null;
      Enumeration<String> enumHdrNames = null;
      Enumeration<String> enumDebug = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>:");
      }

      /*
       * Get the Enumeration of header names from the original "request". Create
       * a new List containing the original list. Add the new header names to
       * the new List. Create an Enumeration of the new List and return the new
       * Enumeration (it's a super-set)
       */

      enumHdrNames = super.getHeaderNames();

      if (_index != EMPTY && enumHdrNames != null)
      {
         list = new LinkedList<String>();
         while (enumHdrNames.hasMoreElements())
         {
            str = enumHdrNames.nextElement();
            if (str != null && str.length() > 0)
            {
               list.add(str);
               if (str.equalsIgnoreCase(HEADER_ACCEPT))
               {
                  needAccept = false;
               }
               else
               {
                  if (str.equalsIgnoreCase(HEADER_CONTENT_TYPE))
                  {
                     needContentType = false;
                  }
               }
            }
         }

         if (needAccept)
         {
            list.add(HEADER_ACCEPT);
         }

         if (needContentType)
         {
            list.add(HEADER_CONTENT_TYPE);
         }

         enumHdrNames = Collections.enumeration(list);

         if (_debug)
         {
            enumDebug = Collections.enumeration(list);
         }
      }
      else
      {
         if (_debug)
         {
            enumDebug = super.getHeaderNames();
         }
      }

      if (_debug)
      {
         output = Collections.list(enumDebug);
         Logger.logInfo(METHOD_NAME + "<<<: Enumeration<String>: " + (output != null ? output : NULL));
      }

      return enumHdrNames;
   }

   //----------------------------------------------------------------
   @Override
   public String getContentType()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String type = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>:");
      }
      /*
       * If the format has been defined, set the "type" the format's value.
       * Else, return the "type" from the original request.
       */

      if (_index != EMPTY)
      {
         type = _mimeTypes[_index];
      }
      else
      {
         type = super.getContentType();
      }

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + "<<<: String: '" + (type != null ? type : NULL) + "'");
      }
      return type;
   }

   //----------------------------------------------------------------
   @Override
   public String getQueryString()
   //----------------------------------------------------------------
   {
      boolean needSeparator = false;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String newQuery = null;
      StringBuffer buf = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>:");
      }

      if (_parameterMap != null)
      {
         buf = new StringBuffer();

         for (String name : _parameterMap.keySet())
         {
            if (name != null && name.length() > 0)
            {
               if (needSeparator)
               {
                  buf.append(AMP);
               }
               buf.append(name).append(EQUAL);
               needSeparator = true;
               for (String value : _parameterMap.get(name))
               {
                  if (value != null && value.length() > 0)
                  {
                     buf.append(value);
                  }
               }
            }
         }
         newQuery = buf.toString();
      }

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + "<<<: String: '" + (newQuery != null ? newQuery : NULL) + "'");
      }

      return newQuery;
   }

   //----------------------------------------------------------------
   @Override
   public String getParameter(String name)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String param = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>: name='" + (name != null ? name : NULL) + "'");
      }

      if (name != null && name.length() > 0)
      {
         for (String key : _parameterMap.keySet())
         {
            if (key != null && key.equalsIgnoreCase(name))
            {
               param = _parameterMap.get(key)[0];
            }
         }
      }

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + "<<<: String: '" + (param != null ? param : NULL) + "'");
      }

      return param;
   }

   //----------------------------------------------------------------
   @Override
   public Map<String, String[]> getParameterMap()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      StringBuffer buf = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>:");
      }

      if (_debug)
      {
         buf = new StringBuffer();
         if (_parameterMap != null)
         {
            for (String s : _parameterMap.keySet())
            {
               buf.append(s).append(", ");
            }
         }
         Logger.logInfo(METHOD_NAME + "<<<: Map<String, String[]>: '" + buf.toString() + "'");
      }

      return _parameterMap;
   }

   //----------------------------------------------------------------
   @Override
   @SuppressWarnings("unchecked")
   public Enumeration getParameterNames()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      List<String> output = null;
      Enumeration enumParamNames = null;
      Enumeration enumDebug = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>:");
      }


      if (_parameterMap != null)
      {
         enumParamNames = Collections.enumeration(_parameterMap.keySet());
         if (_debug)
         {
            enumDebug = Collections.enumeration(_parameterMap.keySet());
         }
      }

      if (_debug)
      {
         output = Collections.list(enumDebug);
         Logger.logInfo(METHOD_NAME + "<<<: Enumeration<String>: " + (output != null ? output : NULL));
      }

      return enumParamNames;
   }

   //----------------------------------------------------------------
   @Override
   public String[] getParameterValues(String name)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String str = null;
      String[] array = null;
      StringBuffer buf = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>: name=" + (name != null ? name : NULL) + "'");
      }

      if (name != null && name.length() > 0)
      {
         if (!name.equalsIgnoreCase(PARAMETER_FORMAT))
         {
            array = _parameterMap.get(name);
         }
      }

      if (array == null)
      {
         array = new String[0];
      }

      if (_debug)
      {
         buf = new StringBuffer();
         buf.append("[");
         if (array != null && array.length > 0)
         {
            for (int i = 0; i < array.length; i++)
            {
               if (i > 0)
               {
                  buf.append(", ");
               }
               str = array[i];
               if (str != null)
               {
                  buf.append(str);
               }
            }
         }
         buf.append("]");

         Logger.logInfo(METHOD_NAME + "<<<: String[]: " + buf.toString());
      }

      return array;
   }

   //----------------------------------------------------------------
   @Override
   public String getPathInfo()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String value = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>:");
      }

      if (_pathInfo != null)
      {
         value = _pathInfo;
      }
      else
      {
         value = super.getPathInfo();
      }

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + "<<<: String: '" + (value != null ? value : NULL) + "'");
      }

      return value;
   }

   //----------------------------------------------------------------
   @Override
   public String getRequestURI()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String value = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>:");
      }

      if (_requestURI != null)
      {
         value = _requestURI;
      }
      else
      {
         value = super.getRequestURI();
      }

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + "<<<: String: '" + (value != null ? value : NULL) + "'");
      }

      return value;
   }

   //----------------------------------------------------------------
   @Override
   public StringBuffer getRequestURL()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      StringBuffer buf = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>:");
      }

      if (_requestURL != null)
      {
         buf = _requestURL;
      }
      else
      {
         buf = super.getRequestURL();
      }

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + "<<<: StringBuffer: '" + (buf != null ? buf.toString() : NULL) + "'");
      }

      return buf;
   }

   //----------------------------------------------------------------
   @Override
   public ServletInputStream getInputStream() throws IOException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      BodyInputStream bodyIS = null;

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>:");
      }

      bodyIS = new BodyInputStream(_body.getBytes());

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + "<<<: ServletInputStream: '" + _body + "'");
      }

      return bodyIS;
   }

   //----------------------------------------------------------------
   @Override
   public BufferedReader getReader() throws IOException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + ">>>:");
      }

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME + "<<<: BufferedReader: '" + _body + "'");
      }

      return new BufferedReader(new InputStreamReader(this.getInputStream()));
   }

   //----------------------------------------------------------------
   public String getBody()
   //----------------------------------------------------------------
   {
      return _body;
   }

   /*
    * ========================
    * === PRIVATE METHODS ====
    * ========================
    */
   //
   //----------------------------------------------------------------
   @SuppressWarnings("unchecked")
   private void initialize(final HttpServletRequest request)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String key = null;
      String value = null;
      String method = null;
      String contentType = null;
      String[] array = null;
      StringBuffer buf = null;
      Map<String, String[]> map = null;
      Iterator<String> iter = null;

      method = request.getMethod();
      contentType = request.getContentType();

      if (_debug)
      {
         Logger.logInfo(METHOD_NAME
                 + "method='" + (method != null ? method : NULL) + "' "
                 + "contentType='" + (contentType != null ? contentType : NULL) + "' ");
      }

      if (method.equalsIgnoreCase(METHOD_POST) || method.equalsIgnoreCase(METHOD_PUT)) // CREATE - UPDATE
      {
         this.setBody(request);
      }

      /*
       * If the mode is FORMAT or BOTH
       * Get the pathInfo and check for a valid suffix
       */

      if (_mode == Mode.SUFFIX || _mode == Mode.BOTH)
      {
         value = request.getPathInfo();
         if (value != null && value.length() > 0)
         {
            for (int i = 0; i < _suffixes.length; i++)
            {
               if (value.toLowerCase().endsWith(_suffixes[i]))
               {
                  _index = i;
                  break;
               }
            }
         }

         if (_index != EMPTY)
         {
            /*
             * update path related values
             */
            value = request.getPathInfo();
            if (value != null && value.length() > 0 && value.endsWith(_suffixes[_index]))
            {
               _pathInfo = value.substring(0, (value.length() - _suffixes[_index].length()));
            }

            value = request.getRequestURI();
            if (value != null && value.length() > 0 && value.endsWith(_suffixes[_index]))
            {
               _requestURI = value.substring(0, (value.length() - _suffixes[_index].length()));
            }

            buf = request.getRequestURL();
            if (buf != null && buf.length() > 0)
            {
               value = buf.toString();
               if (value != null && value.length() > 0 && value.endsWith(_suffixes[_index]))
               {
                  value = value.substring(0, (value.length() - _suffixes[_index].length()));
                  _requestURL = new StringBuffer(value);
               }
            }
         }
      }

      /*
       * If the mode is PARAMETER or BOTH:
       * Get the query parameter "value" using the "name". If the "value" is not
       * null, set the related format.
       */

      if (_mode == Mode.PARAMETER || _mode == Mode.BOTH)
      {
         value = request.getParameter(PARAMETER_FORMAT);

         if (value != null && value.length() > 0)
         {
            for (int i = 0; i < _formats.length; i++)
            {
               if (value.equalsIgnoreCase(_formats[i]))
               {
                  _index = i;
                  break;
               }
            }
         }
      }

      /*
       * If the format is set ...
       */

      if (_index != EMPTY)
      {
         /*
          * read the original parameterMap and create
          * a new parameterMap but exclude the "format" parameter, if it exists
          * Mode must be either PARAMETER or BOTH
          */

         _parameterMap = new HashMap<String, String[]>();
         map = request.getParameterMap();
         if (map != null && !map.isEmpty())
         {
            iter = map.keySet().iterator();
            while (iter.hasNext())
            {
               key = iter.next();
               if (key != null && key.length() > 0)
               {
                  array = map.get(key);
                  if (_mode == Mode.PARAMETER || _mode == Mode.BOTH)
                  {
                     if (!key.equalsIgnoreCase(PARAMETER_FORMAT))
                     {
                        _parameterMap.put(key, array);
                     }
                  }
                  else
                  {
                     _parameterMap.put(key, array);
                  }
               }
            }
         }

         /*
          * If the contentType is "form" then the data is stored as a Parameter
          * need to copy the data to the body a remove it as a parameter
          * The data is actually in the "name" of the parameter
          * Look for the string "subject" to determine which parameter is the data
          */

         iter = _parameterMap.keySet().iterator();
         while (iter.hasNext())
         {
            key = iter.next();
            if (key != null && key.indexOf("subject") > -1)
            {
               _body = key;
               _parameterMap.remove(key);
            }
         }
      }
      else
      {
         _parameterMap = request.getParameterMap();
      }

      return;
   }

   //----------------------------------------------------------------
   private void setBody(final HttpServletRequest request)
   //----------------------------------------------------------------
   {
      int bytesRead = -1;
      char[] chars = new char[2 * 1024];
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      StringBuilder bldr = new StringBuilder();
      BufferedReader reader = null;
      InputStream inputStream = null;

      /*
       * Get the body and store it
       */

      try
      {
         inputStream = request.getInputStream();
         if (inputStream != null)
         {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((bytesRead = reader.read(chars)) > 0)
            {
               bldr.append(chars, 0, bytesRead);
            }
         }
         else
         {
            bldr.append("");
         }
      }
      catch (IOException ex)
      {
         bldr.append(METHOD_NAME).append(ex.getMessage());
      }
      finally
      {
         if (reader != null)
         {
            try
            {
               reader.close();
            }
            catch (IOException ex)
            {
               bldr.append(METHOD_NAME).append(ex.getMessage());
            }
         }
      }

      if (_debugLevel > 3)
      {
         Logger.logInfo(METHOD_NAME + "body='" + bldr.toString() + "'");
      }

      _body = bldr.toString();

      return;
   }

   //===================================================================
   private class BodyInputStream extends ServletInputStream
   //===================================================================
   {

      ByteArrayInputStream _bAIS = null;

      //----------------------------------------------------------------
      BodyInputStream(byte[] data)
      //----------------------------------------------------------------
      {

         if (data == null)
         {
            _bAIS = new ByteArrayInputStream(new byte[0]);
         }
         else
         {
            _bAIS = new ByteArrayInputStream(data);
         }

         return;
      }

      //----------------------------------------------------------------
      @Override
      public int read() throws IOException
      //----------------------------------------------------------------
      {
         return _bAIS.read();
      }
   }
}
