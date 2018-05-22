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
 * Portions Copyright 2011-2013 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.jaxrs;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.openptk.api.Opcode;
import org.openptk.api.State;
import org.openptk.authorize.TargetIF;
import org.openptk.authorize.UrlTarget;
import org.openptk.authorize.decider.DeciderIF;
import org.openptk.authorize.decider.DeciderManager;
import org.openptk.common.AssignmentIF;
import org.openptk.common.ComponentIF;
import org.openptk.config.mapper.AttrMapIF;
import org.openptk.context.ContextIF;
import org.openptk.debug.DebugIF;
import org.openptk.engine.EngineIF;
import org.openptk.exception.AuthorizationException;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.ConverterException;
import org.openptk.exception.EngineException;
import org.openptk.exception.StructureException;
import org.openptk.logging.Logger;
import org.openptk.representation.RepresentationType;
import org.openptk.session.BasicSession;
import org.openptk.session.SessionIF;
import org.openptk.session.SessionType;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.ConverterType;
import org.openptk.structure.StructureIF;
import org.openptk.structure.StructureType;
import org.openptk.util.Digest;
import org.openptk.util.StringUtil;
import org.openptk.util.UniqueId;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 * @since 2.0.0
 */
//===================================================================
public abstract class Resource implements ResourceIF
//===================================================================
{

   private final int _logStructLevel = DebugIF.FINE;
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String NULL = "(NULL)";
   private static final String SEPARATOR = ", ";
   private static final String COOKIE_JSESSIONID = "JSESSIONID";
   private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
   private static final String PARAMETER_FORMAT = "format";
   private static final String MEDIA_TYPE_IMAGE = "image";
   private static final String MEDIA_TYPE_WILDCARD = "*";
   private static final MediaType[] _mediaTypes =
   {
      MediaType.APPLICATION_XML_TYPE,
      MediaType.APPLICATION_JSON_TYPE,
      MediaType.TEXT_HTML_TYPE,
      MediaType.TEXT_PLAIN_TYPE
   };
   private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.TEXT_PLAIN_TYPE;
   private int _formatIndex = -1;
   private String COOKIE_OPENPTKSESSIONID = null;
   private String _absolutePath = null;
   private String _contextId = null;
   private State _state = State.NEW;
   private Status _status = Status.OK;
   private EngineIF _engine = null;
   private Format _formatter = null;
   private DeciderIF _decider = null;
   private CacheControl _cacheCtrl = null;
   protected String _allowedChars = null;
   protected ServletContext _ctx = null;
   protected UriInfo _uri = null;
   protected StructureIF _struct = null;
   protected RepresentationType _type = null;
   protected List<String> _attrMapNames = null;

   /**
    *
    * @param ctx
    * @param uri
    */
   //----------------------------------------------------------------
   public Resource(final ServletContext ctx, final UriInfo uri)
   //----------------------------------------------------------------
   {
      Object obj = null;
      String METHOD_NAME = this.CLASS_NAME + ":Resource(): ";
      String msg = null;
      String enforcerId = null;
      String[] arrayAttrMapNames = null;
      DeciderManager dMgr = null;

      if (ctx == null)
      {
         _state = State.ERROR;
         _status = Status.INTERNAL_SERVER_ERROR;
         msg = "ServletContext is null";
         throw new WebApplicationException(new Exception(METHOD_NAME + msg), _status);
      }

      if (uri == null)
      {
         _state = State.ERROR;
         _status = Status.INTERNAL_SERVER_ERROR;
         msg = "UriInfo is null";
         throw new WebApplicationException(new Exception(METHOD_NAME + msg), _status);
      }

      _ctx = ctx;
      _uri = uri;

      obj = _ctx.getAttribute(EngineIF.ATTR_SERVLET_CONTEXT_ENGINE);

      if (obj != null)
      {
         if (obj instanceof EngineIF)
         {
            //Get the OpenPTK Server engine
            _engine = (EngineIF) obj;
         }
         else
         {
            _state = State.ERROR;
            _status = Status.INTERNAL_SERVER_ERROR;
            msg = EngineIF.MSG_ENGINE_NOT_VALID + ", "
               + EngineIF.ATTR_SERVLET_CONTEXT_ENGINE;
            throw new WebApplicationException(new Exception(METHOD_NAME + msg), _status);
         }
      }
      else
      {
         // Engine not found in application servlet context
         _state = State.ERROR;
         _status = Status.INTERNAL_SERVER_ERROR;
         msg = EngineIF.MSG_ENGINE_NULL + ", "
            + EngineIF.ATTR_SERVLET_CONTEXT_ENGINE;
         throw new WebApplicationException(new Exception(METHOD_NAME + msg), _status);
      }

      COOKIE_OPENPTKSESSIONID = _engine.getProperty(EngineIF.PROP_HTTP_SESSION_COOKIE_UNIQUEID);
      if (COOKIE_OPENPTKSESSIONID == null)
      {
         _state = State.ERROR;
         _status = Status.INTERNAL_SERVER_ERROR;
         msg = "Property COOKIE_OPENPTKSESSIONID is null";
         throw new WebApplicationException(new Exception(METHOD_NAME + msg), _status);
      }

      /*
       * get the decider, used for authorization
       */

      dMgr = _engine.getDeciderManager();
      if (dMgr != null)
      {
         enforcerId = _engine.getProperty(EngineIF.PROP_SECURITY_ENFORCER_ENGINE);
         if (enforcerId != null && enforcerId.length() > 0)
         {
            try
            {
               _decider = dMgr.getDecider(enforcerId);
            }
            catch (ConfigurationException ex)
            {
               throw new WebApplicationException(new Exception(METHOD_NAME + ex.getMessage()), _status);
            }
            if (_decider == null)
            {
               throw new WebApplicationException(new Exception(METHOD_NAME + "Decider is null"), _status);
            }
         }
      }
      else
      {
         throw new WebApplicationException(new Exception(METHOD_NAME + "DeciderManager is null"), _status);
      }

      /*
       * sample date output = 'Tue Dec 01 17:57:47 CST 2009'
       */

      _formatter = new SimpleDateFormat(DATE_FORMAT);

      /*
       * set the allowed characters for input cleaning
       */

      _allowedChars = _engine.getProperty(EngineIF.PROP_INPUT_ALLOWED_CHARACTERS);
      if (_allowedChars == null || _allowedChars.length() < 1)
      {
         _allowedChars = StringUtil.BASIC_WEB;
      }

      /*
       * Header variable name for AttrMap Id
       */

      arrayAttrMapNames = _engine.getAttrMapNames();
      if (arrayAttrMapNames != null && arrayAttrMapNames.length > 0)
      {
         _attrMapNames = new ArrayList<String>(Arrays.asList(arrayAttrMapNames));
      }
      else
      {
         _attrMapNames = new ArrayList<String>();
      }

      /*
       * Create / set the CacheControl for responses
       */

      _cacheCtrl = new CacheControl();
      _cacheCtrl.setNoCache(true);

      return;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   /**
    *
    * @param opcode
    * @param repType
    * @param hdrs
    * @return
    */
   //----------------------------------------------------------------
   protected Response execute(final Opcode opcode, final RepresentationType repType, final HttpHeaders hdrs)
   //----------------------------------------------------------------
   {
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String ptkSessionId = null;
      String jSessionId = null;
      StringBuilder buf = null;
      List<String> hdrValues = null;
      TargetIF target = null;              // OpenPTK
      ComponentIF authorization = null;    // OpenPTK
      SessionIF session = null;            // OpenPTK
      Map<String, Cookie> cookies = null;  // JAX-RS
      Cookie cookie = null;                // JAX-RS
      ResponseBuilder builder = null;      // JAX-RS
      Response response = null;            // JAX-RS

      /*
       * Add the "uri" to the Structure
       */

      try
      {
         if (_struct != null)
         {
            this.getUriData();
         }
         else
         {
            _status = Status.NO_CONTENT;
            this.handleError(METHOD_NAME + "The Request Structure is null.");
         }
      }
      catch (EngineException ex)
      {
         builder = Response.serverError();
         builder.entity(METHOD_NAME + ex.getMessage());
      }

      /*
       * get the uniqueid from the cookie, else create a new uniqueid
       */

      if (builder == null)
      {
         cookies = hdrs.getCookies();

         if (_engine.isDebug())
         {
            buf = new StringBuilder(METHOD_NAME + "Cookies: ");
            for (Cookie c : cookies.values())
            {
               if (c != null)
               {
                  buf.append(c.getName()).append("=").append(c.getValue()).append(", ");
               }
            }
            _engine.logInfo(session, buf.toString());
         }

         if (cookies.containsKey(COOKIE_OPENPTKSESSIONID))
         {
            cookie = cookies.get(COOKIE_OPENPTKSESSIONID);
            ptkSessionId = cookie.getValue();

            try
            {
               if (ptkSessionId != null && ptkSessionId.length() > 0)
               {
                  session = _engine.getSession(ptkSessionId);
                  if (session == null)
                  {
                     this.handleError(METHOD_NAME
                        + COOKIE_OPENPTKSESSIONID + " has a null Session, id='"
                        + ptkSessionId + "'");
                  }
               }
               else
               {
                  this.handleError(METHOD_NAME + "Cookie " + COOKIE_OPENPTKSESSIONID
                     + " has a null value");
               }
            }
            catch (EngineException ex)
            {
               builder = Response.serverError();
               builder.entity(METHOD_NAME + ex.getMessage());
            }
         }
         else
         {

            /*
             * The ServletFilter did not create a OPENPTKSESSIONID Cookie, therefore
             * it did not create a Session object in the Engine.
             * Create an "INTERNAL" Session object. Use JSESSIONID as an external index
             * to the INTERNAL Session (should this Session be used for "state")
             */

            if (cookies.containsKey(COOKIE_JSESSIONID))
            {
               cookie = cookies.get(COOKIE_OPENPTKSESSIONID);
               jSessionId = cookie.getValue();

               try
               {
                  if (jSessionId != null && jSessionId.length() > 0)
                  {
                     /*
                      * Try to "get" the Session using the JSESSIONID
                      */

                     session = _engine.getSession(jSessionId);
                     if (session != null)
                     {
                        uid = session.getUniqueId();
                        if (uid != null)
                        {
                           ptkSessionId = uid.toString();
                        }
                        else
                        {
                           this.handleError(METHOD_NAME
                              + "Session, for JSESSIONID '" + jSessionId
                              + "', has a null UniqueId");
                        }
                     }
                     else
                     {
                        ptkSessionId = UniqueId.getUniqueId();
                        session = new BasicSession(_engine, SessionType.INTERNAL, ptkSessionId);
                        _engine.setSession(ptkSessionId, session);
                     }
                  }
                  else
                  {
                     _status = Status.INTERNAL_SERVER_ERROR;
                     this.handleError(METHOD_NAME + "Cookie does not exist");
                  }
               }
               catch (EngineException ex)
               {
                  builder = Response.serverError();
                  builder.entity(METHOD_NAME + ex.getMessage());
               }
            }
         }
      }

      if (_engine.isDebug())
      {
         _engine.logInfo(session, METHOD_NAME + opcode.toString() + ": "
            + this.getUriInfoAsString() + ", "
            + this.getHeaderInfoAsString(hdrs));
      }

      /*
       * update Structure with sessionId data
       */

      if (builder == null)
      {
         try
         {
            if (ptkSessionId != null && ptkSessionId.length() > 0 && session != null)
            {
               try
               {
                  _struct.addChild(new BasicStructure(StructureIF.NAME_SESSIONID, ptkSessionId));
               }
               catch (StructureException ex)
               {
                  _status = Status.INTERNAL_SERVER_ERROR;
                  this.handleError(session, METHOD_NAME + "StructureException: " + ex.getMessage());
               }
            }
            else
            {
               _status = Status.INTERNAL_SERVER_ERROR;
               this.handleError(session, METHOD_NAME + "Session ID is null");
            }
         }
         catch (EngineException ex)
         {
            builder = Response.serverError();
            builder.entity(METHOD_NAME + ex.getMessage());
         }
      }

      /*
       * check "decider", is this authorized
       */

      if (builder == null)
      {
         try
         {
            target = new UrlTarget(_absolutePath, opcode);
         }
         catch (AuthorizationException ex)
         {
            builder = Response.serverError();
            builder.entity(METHOD_NAME + ex.getMessage());
         }

         if (builder == null)
         {
            try
            {
               authorization = _decider.check(session, target);
            }
            catch (AuthorizationException ex)
            {
               builder = Response.serverError();
               builder.entity(METHOD_NAME + ex.getMessage());
            }

            if (authorization == null)
            {
               builder = Response.serverError();
               builder.entity(METHOD_NAME + "Authorization is null");
            }
         }
      }

      if (builder == null && authorization.getState() != State.ALLOWED)
      {
         _status = Status.UNAUTHORIZED;
         builder = Response.status(_status); // 401: UNAUTHORIZED
         _engine.logWarning(session, "401: Unauthorized:"
            + " State: " + authorization.getStateAsString()
            + ", Status: '" + authorization.getStatus() + "'");
      }

      if (builder == null)
      {
         /*
          * Operation specific logic
          */

         try
         {
            switch (opcode)
            {
               case CREATE:
                  builder = this.doCreate(session, repType, hdrs);
                  break;
               case READ:
                  builder = this.doRead(session, repType, hdrs);
                  break;
               case UPDATE:
                  builder = this.doUpdate(session, repType, hdrs);
                  break;
               case DELETE:
                  builder = this.doDelete(session, repType, hdrs);
                  break;
               case SEARCH:
                  builder = this.doSearch(session, repType, hdrs);
                  break;
               case PWDRESET:
                  builder = this.doPwdReset(session, repType, hdrs);
                  break;
               case PWDCHANGE:
                  builder = this.doPwdChange(session, repType, hdrs);
                  break;
               case PWDFORGOT:
                  builder = this.doPwdForgot(session, repType, hdrs);
                  break;
               default:
                  this.handleError(session, METHOD_NAME + "Operation '" + opcode.toString()
                     + "' is not implemented.");
            }
         }
         catch (Exception ex)
         {
            if (_status == Status.OK)
            {
               _status = Status.INTERNAL_SERVER_ERROR;
            }
            builder = Response.serverError();
            builder.entity(METHOD_NAME + ex.getMessage());
         }
      }

      /*
       * fail safe ... in case response/builder is null (it should NOT be null)
       */

      if (builder == null)
      {
         _state = State.ERROR;
         if (_status == Status.OK)
         {
            _status = Status.INTERNAL_SERVER_ERROR;
         }
         builder = Response.serverError();
         builder.entity(METHOD_NAME + "ResponseBuilder is null");
      }

      if (_formatIndex > -1 && _formatIndex < _mediaTypes.length)
      {
         builder.type(_mediaTypes[_formatIndex]);
      }
      else
      {
         builder.type(DEFAULT_MEDIA_TYPE);
      }

      builder.cacheControl(_cacheCtrl);

      response = builder.build();

      if (_engine.isDebug())
      {
         _engine.logInfo(session, METHOD_NAME + "HTTP Response: "
            + response.getStatus() + ": "
            + Status.fromStatusCode(response.getStatus()).toString());
      }

      return response;
   }

   /**
    *
    * @return
    */
   //----------------------------------------------------------------
   protected final State getState()
   //----------------------------------------------------------------
   {
      return _state;
   }

   /**
    *
    * @return
    */
   //----------------------------------------------------------------
   protected final Status getStatus()
   //----------------------------------------------------------------
   {
      return _status;
   }

   /**
    *
    * @param hdrs
    * @param hdrName
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected MediaType getMediaType(final HttpHeaders hdrs, final String hdrName) throws Exception
   //----------------------------------------------------------------
   {
      /*
       * Determine the MediaType (mime-type) by checking the following sources,
       * in this order: a query parameter then the Header variable.
       * If none of these specify a valid MediaType, set a default value.
       */

      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String accept = null;
      String[] arrayAccept = null;
      List<String> listAccept = null;
      MultivaluedMap<String, String> mapQueryParams = null;
      MediaType mtype = null;

      /*
       * the media type may have been set by either the query parameter or
       * by a URI suffix. If it is, then the _formatIndex will be > -1
       */

      if (_formatIndex > -1)
      {
         mtype = _mediaTypes[_formatIndex];
      }
      else
      {
         if (hdrName == null || hdrName.length() < 1)
         {
            _status = Status.INTERNAL_SERVER_ERROR;
            this.handleError(METHOD_NAME + "Header name is null");
         }

         /*
          * The string might contain multiple types ...
          * "text/html,application/xhtml+xml,application/xml"
          *
          * We will look for one of the valid types, in this order:
          * "application/xml"
          * "application/json"
          * "text/html"
          * "text/plain"
          * Use the first one that is found
          */

         listAccept = hdrs.getRequestHeader(hdrName);
         if (listAccept == null || listAccept.isEmpty())
         {
            _status = Status.INTERNAL_SERVER_ERROR;
            this.handleError(METHOD_NAME + "Header '" + hdrName + "' was not found");
         }

         arrayAccept = listAccept.toArray(new String[listAccept.size()]);
         for (int i = 0; i < arrayAccept.length; i++)
         {
            accept = arrayAccept[i];
            if (accept == null || accept.length() < 1)
            {
               _status = Status.INTERNAL_SERVER_ERROR;
               this.handleError(METHOD_NAME + "Header '" + hdrName
                  + "' has an empty/null value");
            }

            if (accept.indexOf(MediaType.APPLICATION_XML) > -1) // 0: APPLICATION_XML
            {
               _formatIndex = 0;
               mtype = _mediaTypes[_formatIndex];
               break;
            }
            else
            {
               if (accept.indexOf(MediaType.APPLICATION_JSON) > -1) // 1: APPLICATION_JSON
               {
                  _formatIndex = 1;
                  mtype = _mediaTypes[_formatIndex];
                  break;
               }
               else
               {
                  if (accept.indexOf(MediaType.TEXT_HTML) > -1) // 2: TEXT_HTML
                  {
                     _formatIndex = 2;
                     mtype = _mediaTypes[_formatIndex];
                     break;
                  }
                  else
                  {
                     if (accept.indexOf(MediaType.TEXT_PLAIN) > -1) // 3: TEXT_PLAIN
                     {
                        _formatIndex = 3;
                        mtype = _mediaTypes[_formatIndex];
                        break;
                     }
                     else
                     {
                        if (accept.indexOf(MEDIA_TYPE_IMAGE) > -1)
                        {
                           mtype = MediaType.WILDCARD_TYPE;
                           break;
                        }
                        else
                        {
                           if (accept.indexOf(MEDIA_TYPE_WILDCARD) > -1)
                           {
                              mtype = MediaType.WILDCARD_TYPE;
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      if (mtype == null)
      {
         if (accept == null)
         {
            accept = "";
         }
         mtype = DEFAULT_MEDIA_TYPE;
         _engine.logWarning(METHOD_NAME + "The HTTP Header '" + hdrName
            + "' has a value of '" + accept + "', which is not supported. "
            + "The _formatIndex=[" + _formatIndex + "] "
            + "Using MediaType '" + mtype.toString() + "'");
      }

      return mtype;
   }

   /**
    *
    * @param msg
    * @throws EngineException
    */
   //----------------------------------------------------------------
   protected final void handleError(final String msg) throws EngineException
   //----------------------------------------------------------------
   {
      this.handleError(null, msg);
      return;
   }

   /**
    * @param session
    * @param msg
    * @throws EngineException
    */
   //----------------------------------------------------------------
   protected final void handleError(final SessionIF session, final String msg) throws EngineException
   //----------------------------------------------------------------
   {
      String str = null;

      if (msg == null || msg.length() < 1)
      {
         str = "(null message)";
      }
      else
      {
         str = msg;
      }

      _state = State.ERROR;

      if (session != null)
      {
         _engine.logError(session, str);
      }
      else
      {
         _engine.logError(str);
      }

      throw new EngineException(str);
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private ResponseBuilder doCreate(final SessionIF session, final RepresentationType type, final HttpHeaders hdrs) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String status = null;
      URI uri = null;
      ResponseBuilder builder = null;
      ConverterType ctype = null;
      StructureIF structType = null;
      StructureIF structData = null;
      StructureIF structLength = null;
      StructureIF structSubject = null;
      StructureIF structAttrs = null;
      StructureIF structRequest = null;
      StructureIF structOut = null;
      StructureIF structUri = null;
      StructureType stype = null;
      State state = null;

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "INPUT: ", _struct);
      }

      /*
       * Create a new "request" structure to be processed by the Engine
       * add structures from the original input structure
       */

      structRequest = new BasicStructure(StructureIF.NAME_REQUEST);

      this.getCommonStructures(session, structRequest);

      /*
       * Get Context assignments (added as Properties to the Structure)
       */

      if (_contextId != null && _contextId.length() > 0)
      {
         this.getContextAssignments(session, structRequest, hdrs);
      }

      /*
       * Determine the "type" for the submitted "data", either: OBJECT or STRUCTURE
       */

      structType = _struct.getChild(StructureIF.NAME_TYPE);
      if (structType == null)
      {
         this.handleError(session, METHOD_NAME + "Structure does not have a TYPE child");
      }
      if (structType.getValueAsString().equalsIgnoreCase(StructureType.STRUCTURE.toString()))
      {
         stype = StructureType.STRUCTURE;
      }
      else
      {
         stype = StructureType.OBJECT;
      }

      /*
       * get the "raw" data
       */

      structData = _struct.getChild(StructureIF.NAME_DATA);
      if (structData == null)
      {
         this.handleError(session, METHOD_NAME + "Structure does not have a DATA child");
      }
      if (structData.getValueType() == StructureType.STRING)
      {
         if (structData.getValueAsString() == null || structData.getValueAsString().length() < 1)
         {
            this.handleError(session, METHOD_NAME + "Structure Data is empty");
         }
      }

      /*
       * get the length of the data
       */

      structLength = _struct.getChild(StructureIF.NAME_LENGTH);
      if (structLength == null)
      {
         this.handleError(session, METHOD_NAME + "Structure does not have a LENGTH child");
      }
      if (structLength.getValueType() == StructureType.INTEGER)
      {
         if (((Integer) structLength.getValue()) < 1)
         {
            this.handleError(session, METHOD_NAME + "Structure Length must be greater than zero");
         }
      }
      else
      {
         this.handleError(session, METHOD_NAME + "Structure Length must be an INTEGER");
      }

      /*
       * If the "type" is STRUCTURE ...
       * get the Header's "Content-type" so that we can
       * process the "data" with the correct converter.
       * else ...
       * do nothing because "data" does not need to have it's value processed
       */

      if (stype == StructureType.STRUCTURE)
      {
         try
         {
            ctype = this.getConverterType(hdrs, ResourceIF.HTTP_HEADER_CONTENT_TYPE);
         }
         catch (Exception ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }

         try
         {
            structSubject = this.getSubjectFromStringValue(structData, ctype);
         }
         catch (Exception ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }

         /*
          * preprocess the "subject" Structure, if the AttrMap property (structure) is set
          */

         try
         {
            structSubject = this.preprocessRequest(structSubject, structRequest, Opcode.CREATE);
         }
         catch (Exception ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }
      }
      else
      {
         structSubject = new BasicStructure(StructureIF.NAME_SUBJECT);
         structSubject.addChild(structType);
         structAttrs = new BasicStructure(StructureIF.NAME_ATTRIBUTES);
         structAttrs.addChild(structData);
         structAttrs.addChild(structLength);
         structSubject.addChild(structAttrs);
      }

      try
      {
         structRequest.addChild(structSubject);
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + "structSubject: " + ex.getMessage());
      }

      /*
       * Have the Engine execute the CREATE operation
       */

      try
      {
         structOut = _engine.execute(Opcode.CREATE, session, type, structRequest);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      if (structOut == null)
      {
         this.handleError(session, METHOD_NAME + "Output Structure is null");
      }

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "OUTPUT: ", structOut);
      }

      /*
       * Get / build the HTTP Response
       */

      state = structOut.getState();
      status = this.getStatus(structOut);

      switch (state)
      {
         case SUCCESS:
            _status = Status.CREATED;
            structUri = structOut.getChild(StructureIF.NAME_URI);
            if (structUri == null)
            {
               this.handleError(session, METHOD_NAME + "Created URI is null");
            }
            uri = URI.create(this.encodeURL(structUri.getValueAsString()));
            builder = Response.created(uri); // 201: CREATED
            break;
         case INVALID:
            _engine.logWarning(session, METHOD_NAME + state.toString()
               + ": Status='" + status + "'");
            _status = Status.BAD_REQUEST;
            builder = Response.status(_status); // 400: BAD_REQUEST
            builder.entity(status);
            break;
         case FAILED:
            _engine.logWarning(session, METHOD_NAME + state.toString()
               + ": Status='" + status + "'");
            _status = Status.FORBIDDEN;
            builder = Response.status(_status); // 403: FORBIDDEN
            builder.entity(status);
            break;
         case ERROR:
            _status = Status.INTERNAL_SERVER_ERROR;
            this.handleError(session, METHOD_NAME + "Operation error, State='"
               + state.toString() + "', Status='" + status + "'");
            break;
         default:
            this.handleError(session, METHOD_NAME + "Invalid State '"
               + state.toString() + "', Status='" + status + "'");
            break;
      }

      return builder;
   }

   //----------------------------------------------------------------
   private ResponseBuilder doRead(final SessionIF session, final RepresentationType type, final HttpHeaders hdrs) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String str = null;
      String status = null;
      ResponseBuilder builder = null;
      StructureIF structOut = null;
      StructureIF structSubject = null;
      StructureIF structType = null;
      StructureIF structRequest = null;
      StructureType stype = null;
      ConverterType ctype = null;
      State state = null;

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "INPUT: ", _struct);
      }

      /*
       * Create a new "request" structure to be processed by the Engine
       * add structures from the original input structure
       */

      structRequest = new BasicStructure(StructureIF.NAME_REQUEST);

      this.getCommonStructures(session, structRequest);

      /*
       * Get Context assignments (added as Properties Structure, to the Request Structure)
       */

      if (_contextId != null && _contextId.length() > 0)
      {
         this.getContextAssignments(session, structRequest, hdrs);
      }

      /*
       * Determine the "type" for the submitted "data", either: OBJECT or STRUCTURE
       */

      structType = _struct.getChild(StructureIF.NAME_TYPE);
      if (structType == null)
      {
         this.handleError(session, METHOD_NAME + "Structure does not have a TYPE child");
      }
      if (structType.getValueAsString().equalsIgnoreCase(StructureType.STRUCTURE.toString()))
      {
         stype = StructureType.STRUCTURE;
      }
      else
      {
         stype = StructureType.OBJECT;
      }

      structSubject = new BasicStructure(StructureIF.NAME_SUBJECT);
      structSubject.addChild(structType);

      try
      {
         structRequest.addChild(structSubject);
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + "structSubject: " + ex.getMessage());
      }

      /*
       * Have the Engine execute the READ operation
       */

      try
      {
         structOut = _engine.execute(Opcode.READ, session, type, structRequest);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      if (structOut == null)
      {
         this.handleError(session, METHOD_NAME + "Output Structure is null");
      }

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "OUTPUT: ", structOut);
      }

      /*
       * Post process the output Structure
       */

      state = structOut.getState();
      status = this.getStatus(structOut);

      switch (state)
      {
         case SUCCESS:
            _status = Status.OK;

            if (stype == StructureType.STRUCTURE)
            {
               try
               {
                  ctype = this.getConverterType(hdrs, ResourceIF.HTTP_HEADER_ACCEPT);
               }
               catch (Exception ex)
               {
                  this.handleError(session, METHOD_NAME + ex.getMessage());
               }
               try
               {
                  structOut = this.postprocessResponse(structOut, structRequest, Opcode.READ);
                  str = this.encode(ctype, structOut);
               }
               catch (Exception ex)
               {
                  this.handleError(session, METHOD_NAME + ex.getMessage());
               }

               builder = Response.ok(str); // 200: OK
            }
            else // StructureType == OBJECT
            {
               builder = this.buildDataResponse(structOut);
            }
            break;
         case INVALID:
         case FAILED:
            _engine.logWarning(session, METHOD_NAME + state.toString()
               + ": Status='" + status + "'");
            _status = Status.BAD_REQUEST;
            builder = Response.status(_status); // 400: BAD_REQUEST
            builder.entity(status);
            break;
         case NOTEXIST:
            _status = Status.NOT_FOUND;
            _engine.logInfo(session, METHOD_NAME + "Entry does not exist: "
               + structOut.toString());
            builder = Response.status(_status); // 404: NOT_FOUND
            break;
         case ERROR:
            _status = Status.INTERNAL_SERVER_ERROR;
            this.handleError(session, METHOD_NAME + "Operation failed, State='"
               + state.toString() + "'");
            break;
         default:
            this.handleError(session, METHOD_NAME + "Invalid State '"
               + state.toString() + "', Status='" + status + "'");
            break;
      }


      return builder;
   }

   //----------------------------------------------------------------
   private ResponseBuilder doUpdate(final SessionIF session, final RepresentationType type, final HttpHeaders hdrs) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String status = null;
      ResponseBuilder builder = null;
      ConverterType ctype = null;
      StructureIF structOut = null;
      StructureIF structType = null;
      StructureIF structData = null;
      StructureIF structLength = null;
      StructureIF structSubject = null;
      StructureIF structAttrs = null;
      StructureIF structRequest = null;
      StructureType stype = null;
      State state = null;

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "INPUT: ", _struct);
      }

      /*
       * Create a new "request" structure to be processed by the Engine
       * add structures from the original input structure
       */

      structRequest = new BasicStructure(StructureIF.NAME_REQUEST);

      this.getCommonStructures(session, structRequest);

      /*
       * Get Context assignments (added as Properties Structure)
       */

      if (_contextId != null && _contextId.length() > 0)
      {
         this.getContextAssignments(session, structRequest, hdrs);
      }

      /*
       * Determine the "type" for the submitted "data", either: OBJECT or STRUCTURE
       */

      structType = _struct.getChild(StructureIF.NAME_TYPE);
      if (structType == null)
      {
         this.handleError(session, METHOD_NAME + "Structure does not have a TYPE child");
      }
      if (structType.getValueAsString().equalsIgnoreCase(StructureType.STRUCTURE.toString()))
      {
         stype = StructureType.STRUCTURE;
      }
      else
      {
         stype = StructureType.OBJECT;
      }

      /*
       * get the "raw" data
       */

      structData = _struct.getChild(StructureIF.NAME_DATA);

      if (structData == null)
      {
         this.handleError(session, METHOD_NAME + "Structure does not have a DATA child");
      }
      if (structData.getValueType() == StructureType.STRING)
      {
         if (structData.getValueAsString() == null || structData.getValueAsString().length() < 1)
         {
            this.handleError(session, METHOD_NAME + "Structure Data is empty");
         }
      }

      /*
       * get the length of the data
       */

      structLength = _struct.getChild(StructureIF.NAME_LENGTH);
      if (structLength == null)
      {
         this.handleError(session, METHOD_NAME + "Structure does not have a LENGTH child");
      }
      if (structLength.getValueType() == StructureType.INTEGER)
      {
         if (((Integer) structLength.getValue()) < 1)
         {
            this.handleError(session, METHOD_NAME + "Structure Length must be greater than zero");
         }
      }
      else
      {
         this.handleError(session, METHOD_NAME + "Structure Length must be an INTEGER");
      }

      /*
       * If the "type" is STRUCTURE ...
       * get the Header's "Content-type" so that we can
       * process the "data" with the correct converter.
       * else ...
       * do nothing because "data" does not need to have it's value processed
       */

      if (stype == StructureType.STRUCTURE)
      {
         try
         {
            ctype = this.getConverterType(hdrs, ResourceIF.HTTP_HEADER_CONTENT_TYPE);
         }
         catch (Exception ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }

         try
         {
            structSubject = this.getSubjectFromStringValue(structData, ctype);
         }
         catch (Exception ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }

         /*
          * preprocess the "subject" Structure, if the AttrMap header variable is set
          */

         try
         {
            structSubject = this.preprocessRequest(structSubject, structRequest, Opcode.UPDATE);
         }
         catch (Exception ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }
      }
      else
      {
         structSubject = new BasicStructure(StructureIF.NAME_SUBJECT);
         structSubject.addChild(structType);
         structAttrs = new BasicStructure(StructureIF.NAME_ATTRIBUTES);
         structAttrs.addChild(structData);
         structAttrs.addChild(structLength);
         structSubject.addChild(structAttrs);
      }


      try
      {
         structRequest.addChild(structSubject);
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      /*
       * Have the Engine execute the UPDATE operation
       */

      try
      {
         structOut = _engine.execute(Opcode.UPDATE, session, type, structRequest);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      if (structOut == null)
      {
         this.handleError(session, METHOD_NAME + "Output Structure is null");
      }

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "OUTPUT: ", structOut);
      }

      /*
       * Get / build the HTTP Response
       */

      state = structOut.getState();
      status = this.getStatus(structOut);

      switch (state)
      {
         case SUCCESS:
            _status = Status.NO_CONTENT;
            builder = Response.noContent(); // 204: NO_CONTENT
            break;
         case INVALID:
            _engine.logWarning(session, METHOD_NAME + state.toString()
               + ": Status='" + status + "'");
            _status = Status.BAD_REQUEST;
            builder = Response.status(_status); // 400: BAD_REQUEST
            builder.entity(status);
            break;
         case FAILED:
            _engine.logWarning(session, METHOD_NAME + state.toString()
               + ": Status='" + status + "'");
            _status = Status.FORBIDDEN;
            builder = Response.status(_status); // 403: FORBIDDEN
            builder.entity(status);
            break;
         case NOTEXIST:
            _engine.logInfo(session, METHOD_NAME + "Entry does not exist: "
               + structOut.toString());
            _status = Status.NOT_FOUND;
            builder = Response.status(_status); // 404: NOT_FOUND
            break;
         case ERROR:
            _status = Status.INTERNAL_SERVER_ERROR;
            this.handleError(session, METHOD_NAME + "Operation error, State='"
               + state.toString() + "', Status='" + status + "'");
            break;
         default:
            this.handleError(session, METHOD_NAME + "Invalid State '"
               + state.toString() + "', Status='" + status + "'");
            break;
      }

      return builder;
   }

   //----------------------------------------------------------------
   private ResponseBuilder doDelete(final SessionIF session, final RepresentationType type, final HttpHeaders hdrs) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String status = null;
      ResponseBuilder builder = null;
      StructureIF structSubject = null;
      StructureIF structRequest = null;
      StructureIF structOut = null;
      State state = null;

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "INPUT: ", _struct);
      }

      /*
       * Create a new "request" structure to be processed by the Engine
       * add structures from the original input structure
       */

      structRequest = new BasicStructure(StructureIF.NAME_REQUEST);

      this.getCommonStructures(session, structRequest);

      /*
       * Get Context assignments (added as Properties Structure)
       */

      if (_contextId != null && _contextId.length() > 0)
      {
         this.getContextAssignments(session, structRequest, hdrs);
      }

      structSubject = new BasicStructure(StructureIF.NAME_SUBJECT);

      try
      {
         structRequest.addChild(structSubject);
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + "structSubject: " + ex.getMessage());
      }

      /*
       * Have the Engine execute the DELETE operation
       */

      try
      {
         structOut = _engine.execute(Opcode.DELETE, session, type, structRequest);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      if (structOut == null)
      {
         this.handleError(session, METHOD_NAME + "Output Structure is null");
      }

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "OUTPUT: ", structOut);
      }

      /*
       * Get / build the HTTP Response
       */

      state = structOut.getState();
      status = this.getStatus(structOut);

      switch (state)
      {
         case SUCCESS:
            _status = Status.NO_CONTENT;
            builder = Response.noContent(); // 204: NO_CONTENT
            break;
         case NOTEXIST:
            _engine.logInfo(session, METHOD_NAME + "Entry does not exist: "
               + structOut.toString());
            _status = Status.NOT_FOUND;
            builder = Response.status(_status); // 404: NOT_FOUND
            break;
         case INVALID:
         case FAILED:
            _engine.logWarning(session, METHOD_NAME + state.toString()
               + ": Status='" + status + "'");
            _status = Status.BAD_REQUEST;
            builder = Response.status(_status); // 400: BAD_REQUEST
            builder.entity(status);
            break;
         case ERROR:
            _status = Status.INTERNAL_SERVER_ERROR;
            this.handleError(session, METHOD_NAME + "Operation error, State='"
               + state.toString() + "', Status='" + status + "'");
            break;
         default:
            this.handleError(session, METHOD_NAME + "Invalid State '"
               + state.toString() + "', Status='" + status + "'");
            break;
      }

      return builder;
   }

   //----------------------------------------------------------------
   private ResponseBuilder doSearch(final SessionIF session, final RepresentationType type, final HttpHeaders hdrs) throws Exception
   //----------------------------------------------------------------
   {
      Integer len = 0;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String str = null;
      String status = null;
      ResponseBuilder builder = null;
      StructureIF structRequest = null;
      StructureIF structOut = null;
      StructureIF structLength = null;
      ConverterType ctype = null;
      State state = null;

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "INPUT: ", _struct);
      }

      structRequest = _struct;

      this.getCommonStructures(session, structRequest);

      /*
       * Get Context assignments (added as Properties Structure)
       */

      if (_contextId != null && _contextId.length() > 0)
      {
         this.getContextAssignments(session, structRequest, hdrs);
      }

      /*
       * Have the Engine execute the SEARCH operation
       */

      try
      {
         structOut = _engine.execute(Opcode.SEARCH, session, type, structRequest);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      if (structOut == null)
      {
         this.handleError(session, METHOD_NAME + "Output Structure is null");
      }

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "OUTPUT: ", structOut);
      }

      state = structOut.getState();
      status = this.getStatus(structOut);

      switch (state)
      {
         case SUCCESS:
            ctype = this.getConverterType(hdrs, ResourceIF.HTTP_HEADER_ACCEPT);

            try
            {
               structOut = this.postprocessResponse(structOut, structRequest, Opcode.SEARCH);
               str = this.encode(ctype, structOut);
            }
            catch (Exception ex)
            {
               this.handleError(session, METHOD_NAME + ex.getMessage());
            }

            /**
             * Get the length of the "search", number of items found
             * if greater than 0 (zero) return "OK", else return "NO_CONTENT"
             */
            structLength = structOut.getChild(StructureIF.NAME_LENGTH);
            if (structLength == null)
            {
               _status = Status.INTERNAL_SERVER_ERROR;
               this.handleError(session, METHOD_NAME + "Length structure is null");
            }
            if (structLength.getValueType() != StructureType.INTEGER)
            {
               _status = Status.INTERNAL_SERVER_ERROR;
               this.handleError(session, METHOD_NAME + "Length structure is not an Integer");
            }
            len = (Integer) structLength.getValue();

            if (len > 0)
            {
               _status = Status.OK;
               builder = Response.ok(str); // 200: OK
            }
            else
            {
               _status = Status.NO_CONTENT;
               builder = Response.noContent(); // 204: NO_CONTENT
            }
            break;
         case INVALID:
         case FAILED:
            _engine.logWarning(session, METHOD_NAME + state.toString()
               + ": Status='" + status + "'");
            _status = Status.BAD_REQUEST;
            builder = Response.status(_status); // 400: BAD_REQUEST
            builder.entity(status);
            break;
         case ERROR:
            _status = Status.INTERNAL_SERVER_ERROR;
            this.handleError(session, METHOD_NAME + "Operation failed, State='"
               + state.toString() + "'");
            break;
         default:
            this.handleError(session, METHOD_NAME + "Invalid State '"
               + state.toString() + "', Status='" + status + "'");
            break;
      }

      return builder;
   }

   //----------------------------------------------------------------
   private ResponseBuilder doPwdReset(final SessionIF session, final RepresentationType type, final HttpHeaders hdrs) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String str = null;
      String status = null;
      ResponseBuilder builder = null;
      StructureIF structRequest = null;
      StructureIF structOut = null;
      ConverterType ctype = null;
      State state = null;

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "INPUT: ", _struct);
      }

      structRequest = _struct;

      this.getCommonStructures(session, structRequest);

      /*
       * Get Context assignments (added as Properties Structure)
       */

      if (_contextId != null && _contextId.length() > 0)
      {
         this.getContextAssignments(session, structRequest, hdrs);
      }

      /*
       * Have the Engine execute the PWDRESET operation
       */

      try
      {
         structOut = _engine.execute(Opcode.PWDRESET, session, type, structRequest);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      if (structOut == null)
      {
         this.handleError(session, METHOD_NAME + "Output Structure is null");
      }

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "OUTPUT: ", structOut);
      }

      /*
       * Build the response
       */

      state = structOut.getState();
      status = this.getStatus(structOut);

      switch (state)
      {
         case SUCCESS:
            try
            {
               ctype = this.getConverterType(hdrs, ResourceIF.HTTP_HEADER_ACCEPT);
            }
            catch (Exception ex)
            {
               this.handleError(session, METHOD_NAME + ex.getMessage());
            }

            try
            {
               str = this.encode(ctype, structOut);
            }
            catch (Exception ex)
            {
               this.handleError(session, METHOD_NAME + ex.getMessage());
            }

            _status = Status.OK;
            builder = Response.ok(str); // 200: OK
            break;
         case INVALID:
         case FAILED:
            _engine.logWarning(session, METHOD_NAME + state.toString()
               + ": Status='" + status + "'");
            _status = Status.BAD_REQUEST;
            builder = Response.status(_status); // 400: BAD_REQUEST
            builder.entity(status);
            break;
         case NOTEXIST:
            _engine.logInfo(session, METHOD_NAME + "Entry does not exist: "
               + structOut.toString());
            _status = Status.NOT_FOUND;
            builder = Response.status(_status); // 404: NOT_FOUND
            break;
         case ERROR:
            _status = Status.INTERNAL_SERVER_ERROR;
            this.handleError(session, METHOD_NAME + "Operation failed, State='"
               + state.toString() + "'");
            break;
         default:
            this.handleError(session, METHOD_NAME + "Invalid State '"
               + state.toString() + "', Status='" + status + "'");
            break;
      }

      return builder;
   }

   //----------------------------------------------------------------
   private ResponseBuilder doPwdChange(final SessionIF session, final RepresentationType type, final HttpHeaders hdrs) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String status = null;
      ResponseBuilder builder = null;
      ConverterType ctype = null;
      StructureIF structOut = null;
      StructureIF structData = null;
      StructureIF structSubject = null;
      StructureIF structRequest = null;
      State state = null;

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "INPUT: ", _struct);
      }

      /*
       * Create a new "request" structure to be processed by the Engine
       * add structures from the original input structure
       */

      structRequest = new BasicStructure(StructureIF.NAME_REQUEST);

      this.getCommonStructures(session, structRequest);

      /*
       * Get Context assignments (added as Properties Structure)
       */

      if (_contextId != null && _contextId.length() > 0)
      {
         this.getContextAssignments(session, structRequest, hdrs);
      }

      structData = _struct.getChild(StructureIF.NAME_DATA);

      if (structData == null)
      {
         this.handleError(session, METHOD_NAME + "Structure does not have a DATA child");
      }

      try
      {
         ctype = this.getConverterType(hdrs, ResourceIF.HTTP_HEADER_CONTENT_TYPE);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      try
      {
         structSubject = this.getSubjectFromStringValue(structData, ctype);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      try
      {
         structRequest.addChild(structSubject);
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      /*
       * Have the Engine execute the PWDCHANGE operation
       */

      try
      {
         structOut = _engine.execute(Opcode.PWDCHANGE, session, type, structRequest);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      if (structOut == null)
      {
         this.handleError(session, METHOD_NAME + "Output Structure is null");
      }

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "OUTPUT: ", structOut);
      }

      /*
       * Get / build the HTTP Response
       */

      state = structOut.getState();
      status = this.getStatus(structOut);

      switch (state)
      {
         case SUCCESS:
            _status = Status.NO_CONTENT;
            builder = Response.noContent(); // 204: NO_CONTENT
            break;
         case INVALID:
         case FAILED:
            _engine.logWarning(session, METHOD_NAME + state.toString()
               + ": Status='" + status + "'");
            _status = Status.BAD_REQUEST;
            builder = Response.status(_status); // 400: BAD_REQUEST
            builder.entity(status);
            break;
         case NOTEXIST:
            _engine.logInfo(session, METHOD_NAME + "Entry does not exist: "
               + structOut.toString());
            _status = Status.NOT_FOUND;
            builder = Response.status(_status); // 404: NOT_FOUND
            break;
         case ERROR:
            _status = Status.INTERNAL_SERVER_ERROR;
            this.handleError(session, METHOD_NAME + "Operation failed, State='"
               + state.toString() + "'");
            break;
         default:
            this.handleError(session, METHOD_NAME + "Invalid State '"
               + state.toString() + "', Status='" + status + "'");
            break;
      }

      return builder;
   }

   //----------------------------------------------------------------
   private ResponseBuilder doPwdForgot(final SessionIF session, final RepresentationType type, final HttpHeaders hdrs) throws Exception
   //----------------------------------------------------------------
   {
      Object[] objects = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String mode = null;
      String str = null;
      String status = null;
      ResponseBuilder builder = null;
      ConverterType ctype = null;
      StructureIF structMode = null;
      StructureIF structData = null;
      StructureIF structSubject = null;
      StructureIF structRequest = null;
      StructureIF structOut = null;
      State state = null;

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "INPUT: ", _struct);
      }

      /*
       * Create a new "request" structure to be processed by the Engine
       * add structures from the original input structure
       */

      structRequest = new BasicStructure(StructureIF.NAME_REQUEST);

      this.getCommonStructures(session, structRequest);

      /*
       * Get Context assignments (added as Properties Structure)
       */

      if (_contextId != null && _contextId.length() > 0)
      {
         this.getContextAssignments(session, structRequest, hdrs);
      }

      structMode = _struct.getChild(StructureIF.NAME_MODE);
      if (structMode == null)
      {
         this.handleError(session, METHOD_NAME + "Mode is null must be '"
            + StructureIF.NAME_QUESTIONS + "' or '"
            + StructureIF.NAME_ANSWERS + "' or '"
            + StructureIF.NAME_CHANGE + "'");
      }

      try
      {
         structRequest.addChild(structMode);
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + ex.getMessage());
      }

      objects = structMode.getValuesAsArray();
      mode = (String) objects[0];

      if (mode.equals(StructureIF.NAME_ANSWERS) || mode.equals(StructureIF.NAME_CHANGE))
      {
         /*
          * This was a "PUT" which should contain a Structure named "data"
          * that represents a "Subject". Extract the string and convert it to a
          * Structure (structSubject). Add it to the Request Structure
          */
         structData = _struct.getChild(StructureIF.NAME_DATA);

         if (structData == null)
         {
            this.handleError(session, METHOD_NAME + "_struct does not have a '"
               + StructureIF.NAME_DATA + "' Structure");
         }

         try
         {
            ctype = this.getConverterType(hdrs, ResourceIF.HTTP_HEADER_CONTENT_TYPE);
         }
         catch (Exception ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }

         try
         {
            structSubject = this.getSubjectFromStringValue(structData, ctype);
         }
         catch (Exception ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }

         if (structSubject == null)
         {
            this.handleError(session, METHOD_NAME + "Subject Structure is null");
         }

         structRequest.addChild(structSubject);
      }

      /*
       * Have the Engine execute the PWDFORGOT operation
       */

      try
      {
         structOut = _engine.execute(Opcode.PWDFORGOT, session, type, structRequest);
      }
      catch (Exception ex)
      {
         this.handleError(session, METHOD_NAME + "_engine.execute(): " + ex.getMessage());
      }

      if (structOut == null)
      {
         this.handleError(session, METHOD_NAME + "Output Structure is null");
      }

      if (_engine.isDebug())
      {
         this.logStructure(session, METHOD_NAME + "OUTPUT: ", structOut);
      }

      state = structOut.getState();
      status = this.getStatus(structOut);

      switch (state)
      {
         case SUCCESS:
            try
            {
               ctype = this.getConverterType(hdrs, ResourceIF.HTTP_HEADER_ACCEPT);
            }
            catch (Exception ex)
            {
               this.handleError(session, METHOD_NAME + ex.getMessage());
            }

            try
            {
               str = this.encode(ctype, structOut);
            }
            catch (Exception ex)
            {
               this.handleError(session, METHOD_NAME + ex.getMessage());
            }

            _status = Status.OK;
            builder = Response.ok(str); // 200: OK
            break;
         case INVALID:
         case FAILED:
            _engine.logWarning(session, METHOD_NAME + state.toString()
               + ": Status='" + status + "'");
            _status = Status.BAD_REQUEST;
            builder = Response.status(_status); // 400: BAD_REQUEST
            builder.entity(status);
            break;
         case NOTEXIST:
            _engine.logInfo(session, METHOD_NAME + "Entry does not exist: "
               + structOut.toString());
            _status = Status.NOT_FOUND;
            builder = Response.status(_status); // 404: NOT_FOUND
            break;
         case ERROR:
            _status = Status.INTERNAL_SERVER_ERROR;
            this.handleError(session, METHOD_NAME + "Operation failed, State='"
               + state.toString() + "'");
            break;
         default:
            this.handleError(session, METHOD_NAME + "Invalid State '"
               + state.toString() + "', Status='" + status + "'");
            break;
      }

      return builder;
   }

   //----------------------------------------------------------------
   private String getStatus(final StructureIF struct)
   //----------------------------------------------------------------
   {
      String str = null;
      StructureIF structStatus = null;

      if (struct != null)
      {
         structStatus = struct.getChild(StructureIF.NAME_STATUS);
         if (structStatus != null)
         {
            str = structStatus.getValueAsString();
         }
      }

      if (str == null)
      {
         str = "";
      }

      return str;
   }

   //----------------------------------------------------------------
   private ConverterType getConverterType(final HttpHeaders hdrs, final String name) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      MediaType mtype = null;
      ConverterType ctype = null;

      mtype = this.getMediaType(hdrs, name);

      if (mtype == MediaType.TEXT_PLAIN_TYPE)
      {
         ctype = ConverterType.PLAIN;
      }
      else
      {
         if (mtype == MediaType.TEXT_HTML_TYPE)
         {
            ctype = ConverterType.HTML;
         }
         else
         {
            if (mtype == MediaType.APPLICATION_JSON_TYPE)
            {
               ctype = ConverterType.JSON;
            }
            else
            {
               if (mtype == MediaType.APPLICATION_XML_TYPE)
               {
                  ctype = ConverterType.XML;
               }
               else
               {
                  this.handleError(METHOD_NAME + "Converter not available for MediaType '"
                     + mtype.toString() + "'");
               }
            }
         }
      }

      return ctype;
   }

   //----------------------------------------------------------------
   private StructureIF getSubjectFromStringValue(final StructureIF structData, final ConverterType ctype) throws Exception
   //----------------------------------------------------------------
   {
      Object[] values = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String value = null;
      String msg = null;
      StructureIF structSubject = null;

      values = structData.getValuesAsArray();
      if (values != null)
      {
         if (structData.getValueType() == StructureType.STRING)
         {
            value = (String) values[0];
            if (value != null && value.length() > 0)
            {
               try
               {
                  structSubject = this.decode(ctype, value);
               }
               catch (Exception ex)
               {
                  msg = ex.getMessage();
               }
            }
            else
            {
               msg = "DATA is not set";
            }
         }
         else
         {
            msg = "DATA must be of type STRING";
         }
      }
      else
      {
         msg = "Value for DATA is NULL";
      }

      if (msg != null)
      {
         _state = State.ERROR;
         _status = Status.INTERNAL_SERVER_ERROR;
         this.handleError(METHOD_NAME + msg);
      }

      return structSubject;
   }

   //----------------------------------------------------------------
   private String encode(final ConverterType type, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String str = null;
      String data = null;
      String msg = null;

      if (structIn != null)
      {
         if (type != null)
         {
            if (type == ConverterType.JSON && structIn.getName().equals(StructureIF.NAME_RESPONSE))
            {
               structIn.setType(StructureType.CONTAINER);
            }

            try
            {
               data = _engine.encode(type, structIn);
            }
            catch (ConverterException ex)
            {
               msg = ex.getMessage();
            }

            switch (type)
            {
               case PLAIN:
               {
                  str = data + "\n";
                  break;
               }
               case HTML:
               {
                  str = "<html>\n" + HTML_HEAD
                     + "<body>\n" + data + "</body>\n"
                     + "</html>\n";
                  break;
               }
               case JSON:
               {
                  str = data + "\n";
                  break;
               }
               case XML:
               {
                  str = ResourceIF.XML_HEADER + "\n" + data + "\n";
                  break;
               }
               default:
               {
                  msg = "Header '" + ResourceIF.HTTP_HEADER_ACCEPT
                     + "' has an unsupported value: '" + type.toString() + "'";
                  break;
               }
            }
         }
         else
         {
            msg = "ConverterType is null.";
         }
      }
      else
      {
         msg = "Output Structure is null.";
      }

      if (msg != null)
      {
         _state = State.ERROR;
         _status = Status.INTERNAL_SERVER_ERROR;
         this.handleError(METHOD_NAME + msg);
      }

      return str;
   }

   //----------------------------------------------------------------
   private StructureIF decode(final ConverterType type, final String data) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      StructureIF structOut = null;

      if (data != null)
      {
         if (type != null)
         {
            try
            {
               structOut = _engine.decode(type, data);
            }
            catch (ConverterException ex)
            {
               msg = ex.getMessage();
            }
         }
         else
         {
            msg = "ConverterType is null.";
         }
      }
      else
      {
         msg = "Data String is null.";
      }

      if (msg != null)
      {
         _state = State.ERROR;
         _status = Status.INTERNAL_SERVER_ERROR;
         this.handleError(METHOD_NAME + msg);
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private ResponseBuilder buildDataResponse(final StructureIF structOut) throws EngineException
   //----------------------------------------------------------------
   {
      byte[] bytes = null;
      Object object = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String mimeType = null;
      String digest = null;
      String modified = null;
      Date date = null;
      ResponseBuilder builder = null;
      StructureIF structResults = null;
      StructureIF structSubject = null;
      StructureIF structAttrs = null;
      StructureIF structAttr = null;

      structResults = structOut.getChild(StructureIF.NAME_RESULTS);
      if (structResults == null)
      {
         this.handleError(METHOD_NAME + "Response:Results structure is null");
      }

      if (structResults.getValueType() == StructureType.STRUCTURE)
      {
         structSubject = (StructureIF) structResults.getValue();
      }
      else
      {
         this.handleError(METHOD_NAME
            + "Response:Results: Value must be a Structure, value type='"
            + structResults.getValueType().toString() + "'");
      }

      if (structSubject == null)
      {
         this.handleError(METHOD_NAME + "Response:Results:Subject structure is null");
      }

      structAttrs = structSubject.getChild(StructureIF.NAME_ATTRIBUTES);
      if (structAttrs == null)
      {
         this.handleError(METHOD_NAME + "Response:Results:Subject:Attributes structure is null");
      }

      /*
       * Data
       */

      structAttr = structAttrs.getChild(StructureIF.NAME_DATA);
      if (structAttr == null)
      {
         this.handleError(METHOD_NAME + "Attribute '" + StructureIF.NAME_DATA
            + "' is null");
      }
      if (structAttr.getValueType() != StructureType.OBJECT)
      {
         this.handleError(METHOD_NAME + "Attribute 'data' must be of type 'OBJECT'");
      }
      object = structAttr.getValue();
      if (object instanceof byte[])
      {
         bytes = (byte[]) object;
      }
      else
      {
         this.handleError(METHOD_NAME + "Object must be a byte[]");
      }

      /*
       * Mime-Type
       */

      structAttr = structAttrs.getChild(StructureIF.NAME_TYPE);
      if (structAttr == null)
      {
         this.handleError(METHOD_NAME + "Attribute '" + StructureIF.NAME_TYPE
            + "' is null");
      }
      mimeType = structAttr.getValueAsString();
      if (mimeType == null || mimeType.length() < 1)
      {
         this.handleError(METHOD_NAME + "Mime-Type is null");
      }

      /*
       * Last Modified: format = 'Tue Dec 01 17:57:47 CST 2009'
       */

      structAttr = structAttrs.getChild(StructureIF.NAME_MODIFIED);
      if (structAttr == null)
      {
         this.handleError(METHOD_NAME + "Attribute '"
            + StructureIF.NAME_MODIFIED + "' is null");
      }
      modified = structAttr.getValueAsString();
      if (modified == null || modified.length() < 1)
      {
         this.handleError(METHOD_NAME + "Last Modified Date is null");
      }
      try
      {
         date = (Date) _formatter.parseObject(modified);
      }
      catch (ParseException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage() + ", modified='"
            + modified + "'");
      }

      /*
       * Check the digest
       */

      structAttr = structAttrs.getChild(StructureIF.NAME_DIGEST);
      if (structAttr == null)
      {
         this.handleError(METHOD_NAME + "Attribute '"
            + StructureIF.NAME_DIGEST + "' is null");
      }
      digest = structAttr.getValueAsString();
      if (digest == null || digest.length() < 1)
      {
         this.handleError(METHOD_NAME + "Digest is null");
      }
      if (!Digest.validate(digest, bytes))
      {
         this.handleError(METHOD_NAME + "Corrupt data, digest values do not match");
      }

      /*
       * build the response
       */

      builder = Response.ok(bytes).type(mimeType).lastModified(date);

      return builder;
   }

   //----------------------------------------------------------------
   private String encodeURL(final String url)
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();
      char c;
      char[] chars = null;

      if (url != null && url.length() > 0)
      {
         chars = url.toCharArray();

         for (int i = 0; i < chars.length; i++)
         {
            c = chars[i];
            switch (c)
            {
               case ' ':
                  buf.append("%20");
                  break;
               default:
                  buf.append(c);
                  break;
            }
         }
      }

      return buf.toString();
   }

   //----------------------------------------------------------------
   private void getUriData() throws EngineException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String key = null;
      String value = null;
      MultivaluedMap<String, String> map = null;
      Iterator<String> iterKeys = null;
      Iterator<String> iterValues = null;
      List<String> listValues = null;
      StructureIF structParams = null;
      StructureIF structParam = null;
      StructureIF structQueries = null;
      StructureIF structQuery = null;

      _absolutePath = this.uriDecode(_uri.getAbsolutePath().toASCIIString());
      try
      {
         _struct.addChild(new BasicStructure(StructureIF.NAME_URI, this.cleanString(_absolutePath)));
      }
      catch (StructureException ex)
      {
         _engine.logError(METHOD_NAME + ex.getMessage());
      }

      /*
       * PathParams
       */

      structParams = new BasicStructure(StructureIF.NAME_PARAMPATH);

      map = _uri.getPathParameters();

      if (map != null && !map.isEmpty())
      {

         iterKeys = map.keySet().iterator();

         while (iterKeys.hasNext())
         {
            key = iterKeys.next();
            if (key != null && key.length() > 0)
            {
               structParam = new BasicStructure(this.uriDecode(key));
               listValues = map.get(key);
               if (listValues != null && !listValues.isEmpty())
               {
                  iterValues = listValues.iterator();
                  while (iterValues.hasNext())
                  {
                     value = this.uriDecode(iterValues.next());
                     try
                     {
                        structParam.addValue(this.cleanString(value));
                     }
                     catch (StructureException ex)
                     {
                        _engine.logError(METHOD_NAME + ex.getMessage());
                     }

                     /*
                      * Save the Context Id
                      */

                     if (key.equalsIgnoreCase(StructureIF.NAME_CONTEXTID))
                     {
                        _contextId = this.cleanString(value);
                     }
                  }
               }
               try
               {
                  structParams.addChild(structParam);
               }
               catch (StructureException ex)
               {
                  _engine.logError(METHOD_NAME + ex.getMessage());
               }
            }
         }
      }

      try
      {
         _struct.addChild(structParams);
      }
      catch (StructureException ex)
      {
         _engine.logError(METHOD_NAME + ex.getMessage());
      }

      /*
       * QueryParams: check for the "format", then strip it out
       */

      structQueries = new BasicStructure(StructureIF.NAME_PARAMQUERY);

      map = _uri.getQueryParameters();
      if (map != null && !map.isEmpty())
      {
         iterKeys = map.keySet().iterator();

         while (iterKeys.hasNext())
         {
            key = iterKeys.next();
            if (key != null && key.length() > 0)
            {
               if (!key.equalsIgnoreCase(PARAMETER_FORMAT))
               {
                  structQuery = new BasicStructure(key);
                  listValues = map.get(key);
                  if (listValues != null && !listValues.isEmpty())
                  {
                     iterValues = listValues.iterator();
                     while (iterValues.hasNext())
                     {
                        value = iterValues.next();
                        try
                        {
                           structQuery.addValue(this.cleanString(value));
                        }
                        catch (StructureException ex)
                        {
                           _engine.logError(METHOD_NAME + ex.getMessage());
                        }
                     }
                  }
                  try
                  {
                     structQueries.addChild(structQuery);
                  }
                  catch (StructureException ex)
                  {
                     _engine.logError(METHOD_NAME + ex.getMessage());
                  }
               }
            }
         }
      }

      try
      {
         _struct.addChild(structQueries);
      }
      catch (StructureException ex)
      {
         _engine.logError(METHOD_NAME + ex.getMessage());
      }

      return;
   }

   //----------------------------------------------------------------
   private void getContextAssignments(final SessionIF session, final StructureIF structRequest, final HttpHeaders hdrs) throws EngineException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String src_type = null;
      String src_name = null;
      String src_value = null;
      String dst_type = null;
      String dst_name = null;
      String dst_value = null;
      String hdr_value = null;
      String prop_value = null;
      String[] ids = null;
      List<String> hdr_values = null;
      ContextIF context = null;
      AssignmentIF assignment = null;
      ComponentIF src = null;
      ComponentIF dst = null;

      /*
       * Assignments are a means to "assign" data from a "source" mechanism to
       * a "destination" mechanism.  In this situations, Contexts, assignments
       * are optional.  If they exist, an assignment maps HTTP Header Variable
       * to Properties (added to the Structure).
       * 
       * <Assignments>
       *    <Assignment id="http-hdr-attrmap" description="HTTP Header for SCIM map">
       *       <Source      type="http_header" name="X-OPENPTK-ATTRMAP" value="SCIM-USER-1.0"/>
       *       <Destination type="property"    name="attrmap" value="scim.user-1.0-consumer"/>
       *    </Assignment>
       *    <Assignment id="http-hdr-alias-url" description="re-write URL for responses">
       *       <Source      type="http_header" name="X-OPENPTK-ALIAS-URL"/>
       *       <Destination type="property"    name="uri"/>
       *    </Assignment>
       * </Assignments>
       *
       * Check the related Context (from the Engine) and see if it
       * has Assignments, specifically ones that have a source type of "http_header"
       * Add properties to the Request Structure
       */

      if (_contextId == null || _contextId.length() < 1)
      {
         _engine.logInfo(session, METHOD_NAME + "ContextId is null/empty");
      }
      else
      {
         try
         {
            context = _engine.getContext(_contextId);
         }
         catch (ConfigurationException ex)
         {
            this.handleError(session, METHOD_NAME + ex.getMessage());
         }

         ids = context.getAssignmentNames();
         if (ids != null && ids.length > 0)
         {
            for (String id : ids)
            {
               if (id != null && id.length() > 0)
               {
                  assignment = context.getAssignment(id);
                  if (assignment != null)
                  {
                     src_type = src_name = src_value = dst_type = dst_name = dst_value = hdr_value = prop_value = null;
                     /*
                      * Get the assignment's "source" ... type, name, value
                      */
                     src = assignment.getSource();
                     if (src != null)
                     {
                        src_type = src.getProperty(StructureIF.NAME_TYPE);
                        if (src_type != null && src_type.length() > 0)
                        {
                           /*
                            * A "source" must have a "name", a "value" is optional
                            * The "name", is the name of the HTTP Header that we are interested in.
                            * 
                            * If the "source" value is non-null, the "value's" data is used for comparison ...
                            * The if the HTTP Header value equals the "source" value,
                            * then the "destintation" is processed, such as applying an attrmap
                            * 
                            * If the "source" value is null ...
                            * The HTTP Header's value is ready and applied to the "destination"
                            * such as setting a destination property
                            */

                           src_name = src.getProperty(StructureIF.NAME_NAME);
                           if (src_name != null && src_name.length() > 0)
                           {
                              src_value = src.getProperty(StructureIF.NAME_VALUE);

                              /*
                               * Get the assignment's "destination" ... type, name, value
                               */

                              dst = assignment.getDestination();
                              if (dst != null)
                              {
                                 dst_type = dst.getProperty(StructureIF.NAME_TYPE);
                                 if (dst_type != null && dst_type.length() > 0)
                                 {
                                    /*
                                     * The type is typically "property"
                                     * A "destination" must have a "name", a "value" is optional
                                     * 
                                     * If the "destination" value is non-null, the "value's" data is
                                     * used if the "source" evaluation is TRUE
                                     * 
                                     * If the "destination" value is null ...
                                     * The "source" value is used 
                                     */

                                    dst_name = dst.getProperty(StructureIF.NAME_NAME);
                                    if (dst_name != null && dst_name.length() > 0)
                                    {
                                       dst_value = dst.getProperty(StructureIF.NAME_VALUE);

                                       /*
                                        * we care about "http_header" types
                                        */

                                       if (src_type.equalsIgnoreCase(AssignmentIF.Type.HTTP_HEADER.toString()))
                                       {
                                          /*
                                           * get the HTTP Header that matches the src name
                                           */

                                          hdr_values = hdrs.getRequestHeader(src_name);
                                          if (hdr_values != null && !hdr_values.isEmpty())
                                          {
                                             hdr_value = hdr_values.get(0);

                                             if (src_value != null && src_value.length() > 0
                                                && hdr_value != null && hdr_value.length() > 0)
                                             {
                                                /*
                                                 * compare the src_value to the hdr_value
                                                 * if they match
                                                 * apply the destination logic
                                                 */
                                                if (src_value.equalsIgnoreCase(hdr_value))
                                                {
                                                   if (dst_type.equalsIgnoreCase(AssignmentIF.Type.PROPERTY.toString()))
                                                   {
                                                      /*
                                                       * property value logic:
                                                       * - if dst_value is non-null
                                                       * - if src_value is non-null
                                                       * - if hdr_value is non-null
                                                       */
                                                      if (dst_value != null && dst_value.length() > 0)
                                                      {
                                                         prop_value = dst_value;
                                                      }
                                                      else
                                                      {
                                                         if (src_value != null && src_value.length() > 0)
                                                         {
                                                            prop_value = src_value;
                                                         }
                                                         else
                                                         {
                                                            prop_value = hdr_value;
                                                         }
                                                      }
                                                   }
                                                }
                                             }
                                             else
                                             {
                                                /*
                                                 * no src_value
                                                 * just use the hdr_value 
                                                 * in the destintation logic
                                                 */
                                                if (hdr_value != null && hdr_value.length() > 0)
                                                {
                                                   prop_value = hdr_value;
                                                }
                                             }
                                             if (prop_value != null && prop_value.length() > 0)
                                             {
                                                structRequest.setProperty(dst_name, prop_value);
                                             }
                                          }
                                       }
                                    }
                                    else
                                    {
                                       _engine.logWarning(session, METHOD_NAME
                                          + "Context='" + _contextId + "' Assignment='" + id + "'"
                                          + ", Destination has a null/empty 'name'");
                                    }
                                 }
                              }
                           }
                           else
                           {
                              _engine.logWarning(session, METHOD_NAME
                                 + "Context='" + _contextId + "' Assignment='" + id + "'"
                                 + ", Source has a null/empty 'name'");
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private String cleanString(String input)
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String output = null;

      /*
       * Make sure the input only contains allowed characters
       */

      if (input != null && input.length() > 0)
      {
         output = StringUtil.clean(_allowedChars, input);
         if (output != null && !output.equals(input))
         {
            Logger.logWarning(METHOD_NAME + "String was cleaned: before='" + input + "' after='" + output + "'");
         }
      }

      if (output == null)
      {
         output = "";
      }

      return output;
   }

   //----------------------------------------------------------------
   private String uriDecode(String uri) throws EngineException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String newUri = null;
      try
      {
         newUri = URLDecoder.decode(uri, "UTF-8");
      }
      catch (UnsupportedEncodingException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      return newUri;
   }

   //----------------------------------------------------------------
   private void logStructure(final SessionIF session, final String banner, final StructureIF struct)
   //----------------------------------------------------------------
   {
      if (_engine.getDebugLevelAsInt() >= _logStructLevel)
      {
         _engine.logInfo(session, (banner != null ? banner : "") + struct.toString());
      }
      return;
   }

   //----------------------------------------------------------------
   private String getUriInfoAsString()
   //----------------------------------------------------------------
   {
      /*
       * get detailed information from the UriInfo object. Return a string
       * containing the data.
       */
      String key = null;
      StringBuilder bldr = new StringBuilder();
      PathSegment segment = null;
      MultivaluedMap<String, String> mapPathParams = null;
      MultivaluedMap<String, String> mapQueryParams = null;
      List<String> listValues = null;
      List<PathSegment> listPathSegments = null;
      String[] arrayKeys = null;
      String[] arrayValues = null;
      PathSegment[] arraySegments = null;

      bldr.append("path='").append(_uri.getPath()).append("', ");
      bldr.append("absolutePath='").append(_uri.getAbsolutePath().toASCIIString()).append("', ");
      bldr.append("baseUri='").append(_uri.getBaseUri().toASCIIString()).append("', ");
      bldr.append("requestUri='").append(_uri.getRequestUri().toASCIIString()).append("', ");

      bldr.append("pathParameters=[");
      mapPathParams = _uri.getPathParameters();
      if (mapPathParams != null && !mapPathParams.isEmpty())
      {
         arrayKeys = mapPathParams.keySet().toArray(new String[mapPathParams.size()]);
         for (int i = 0; i < arrayKeys.length; i++)
         {
            key = arrayKeys[i];
            if (key != null && key.length() > 0)
            {
               if (i > 0)
               {
                  bldr.append(", ");
               }
               bldr.append("'").append(key).append("':'");

               listValues = mapPathParams.get(key);
               arrayValues = listValues.toArray(new String[listValues.size()]);
               for (int j = 0; j < arrayValues.length; j++)
               {
                  if (j > 0)
                  {
                     bldr.append(";");
                  }
                  bldr.append(arrayValues[j]);
               }

               bldr.append("'");
            }
         }
      }
      bldr.append("], ");

      bldr.append("pathSegments=[");
      listPathSegments = _uri.getPathSegments();
      if (listPathSegments != null && !listPathSegments.isEmpty())
      {
         arraySegments = listPathSegments.toArray(new PathSegment[listPathSegments.size()]);
         for (int i = 0; i < arraySegments.length; i++)
         {
            if (i > 0)
            {
               bldr.append(", ");
            }
            segment = arraySegments[i];
            bldr.append("'").append(segment.getPath()).append("'");
         }
      }
      bldr.append("], ");

      bldr.append("queryParameters=[");
      mapQueryParams = _uri.getQueryParameters();
      if (mapQueryParams != null && !mapQueryParams.isEmpty())
      {
         arrayKeys = mapQueryParams.keySet().toArray(new String[mapQueryParams.size()]);
         for (int i = 0; i < arrayKeys.length; i++)
         {
            key = arrayKeys[i];
            if (key != null && key.length() > 0)
            {
               if (i > 0)
               {
                  bldr.append(", ");
               }
               bldr.append("'").append(key).append("':'");

               listValues = mapQueryParams.get(key);
               arrayValues = listValues.toArray(new String[listValues.size()]);
               for (int j = 0; j < arrayValues.length; j++)
               {
                  if (j > 0)
                  {
                     bldr.append(";");
                  }
                  bldr.append(arrayValues[j]);
               }

               bldr.append("'");
            }
         }
      }
      bldr.append("]");

      return bldr.toString();
   }

   //----------------------------------------------------------------
   private String getHeaderInfoAsString(final HttpHeaders hdrs)
   //----------------------------------------------------------------
   {
      boolean needKeySeparator = false;
      boolean needValSeparator = false;
      String value = null;
      String[] arrayKeys = null;
      String[] arrayValues = null;
      StringBuilder bldr = new StringBuilder();
      Cookie cookie = null;
      MediaType mediaType = null;
      MediaType[] arrayMediaTypes = null;
      List<String> listValues = null;
      List<MediaType> listMediaTypes = null;
      MultivaluedMap<String, String> mapHeaders = null;
      Map<String, Cookie> mapCookies = null;

      bldr.append("headers=[");
      if (hdrs != null)
      {
         mapHeaders = hdrs.getRequestHeaders();

         if (mapHeaders != null && !mapHeaders.isEmpty())
         {
            arrayKeys = mapHeaders.keySet().toArray(new String[mapHeaders.size()]);
            for (String key : arrayKeys)
            {
               if (key != null && key.length() > 0)
               {
                  if (needKeySeparator)
                  {
                     bldr.append(SEPARATOR);
                  }
                  bldr.append(key).append("='");
                  needKeySeparator = true;

                  listValues = hdrs.getRequestHeader(key);
                  if (listValues != null && !listValues.isEmpty())
                  {
                     needValSeparator = false;
                     arrayValues = listValues.toArray(new String[listValues.size()]);
                     for (String val : arrayValues)
                     {
                        if (needValSeparator)
                        {
                           bldr.append(";");
                        }
                        bldr.append(val);
                        needValSeparator = true;
                     }
                  }
                  bldr.append("'");
               }
            }
         }
      }
      bldr.append("], ");

      needKeySeparator = false;
      bldr.append("cookies=[");
      if (hdrs != null)
      {
         mapCookies = hdrs.getCookies();
         if (mapCookies != null && !mapCookies.isEmpty())
         {
            arrayKeys = mapCookies.keySet().toArray(new String[mapCookies.size()]);
            for (String key : arrayKeys)
            {
               if (key != null && key.length() > 0)
               {
                  if (needKeySeparator)
                  {
                     bldr.append(SEPARATOR);
                  }
                  bldr.append("'").append(key).append("'='");
                  needKeySeparator = true;
                  cookie = mapCookies.get(key);
                  if (cookie != null)
                  {
                     value = cookie.getValue();
                     bldr.append((value != null ? value : NULL));
                  }
                  bldr.append("'");
               }
            }
         }
      }
      bldr.append("], ");

      needKeySeparator = false;
      bldr.append("acceptableMediaTypes=[");
      if (hdrs != null)
      {
         listMediaTypes = hdrs.getAcceptableMediaTypes();
         if (listMediaTypes != null && !listMediaTypes.isEmpty())
         {
            arrayMediaTypes = listMediaTypes.toArray(new MediaType[listMediaTypes.size()]);
            for (MediaType type : arrayMediaTypes)
            {
               if (needKeySeparator)
               {
                  bldr.append(SEPARATOR);
               }
               if (type != null)
               {
                  bldr.append("'").append(type.toString()).append("'");
                  needKeySeparator = true;
               }
            }
         }
      }
      bldr.append("], ");

      bldr.append("mediaType='");
      if (hdrs != null)
      {
         mediaType = hdrs.getMediaType();
         if (mediaType != null)
         {
            bldr.append(mediaType.toString());
         }
      }
      bldr.append("'");

      return bldr.toString();
   }

   //----------------------------------------------------------------
   private StructureIF preprocessRequest(final StructureIF structIn, final StructureIF structRequest, final Opcode opcode) throws Exception
   //----------------------------------------------------------------
   {
      String attrMapName = null;
      AttrMapIF attrMap = null;
      StructureIF structOut = null;

      /*
       * If the AttrMap property is set, and valid ...
       * get the related AttrMapIF object from the Engine
       * convert the external structure to a framework structure,
       * using the AttrMapIF object
       */

      if (structRequest == null)
      {
         throw new Exception("Request Structure is null");
      }

      attrMapName = structRequest.getProperty(StructureIF.NAME_ATTRMAP);
      if (attrMapName != null && attrMapName.length() > 0)
      {
         attrMap = _engine.getAttrMap(attrMapName);
         if (attrMap != null)
         {
            structOut = attrMap.externalToFramework(structIn);
         }
         else
         {
            throw new Exception("AttrMap name '" + attrMapName + "' is not valid");
         }
      }

      if (structOut == null)
      {
         structOut = structIn;
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF postprocessResponse(final StructureIF structIn, final StructureIF structRequest, final Opcode opcode) throws Exception
   //----------------------------------------------------------------
   {
      String attrMapName = null;
      AttrMapIF attrMap = null;
      StructureIF structOut = null;

      /*
       * If the AttrMap property is set, and valid ...
       * get the related AttrMapIF object from the Engine
       * convert the framework structure to a external structure,
       * using the AttrMapIF object
       */

      if (structRequest == null)
      {
         throw new Exception("Request Structure is null");
      }

      attrMapName = structRequest.getProperty(StructureIF.NAME_ATTRMAP);
      if (attrMapName != null && attrMapName.length() > 0)
      {
         attrMap = _engine.getAttrMap(attrMapName);
         if (attrMap != null)
         {
            structOut = attrMap.frameworkToExternal(structIn);
         }
         else
         {
            throw new Exception("AttrMap name '" + attrMapName + "' is not valid");
         }
      }

      if (structOut == null)
      {
         structOut = structIn;
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private void getCommonStructures(final SessionIF session, final StructureIF struct) throws EngineException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      try
      {
         struct.addChild(_struct.getChild(StructureIF.NAME_SESSIONID));
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + StructureIF.NAME_URI + ": " + ex.getMessage());
      }

      try
      {
         struct.addChild(_struct.getChild(StructureIF.NAME_URI));
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + StructureIF.NAME_URI + ": " + ex.getMessage());
      }

      try
      {
         struct.addChild(_struct.getChild(StructureIF.NAME_PARAMPATH));
      }
      catch (StructureException ex)
      {
         this.handleError(session, METHOD_NAME + StructureIF.NAME_PARAMPATH + ": " + ex.getMessage());
      }

      return;
   }
}
