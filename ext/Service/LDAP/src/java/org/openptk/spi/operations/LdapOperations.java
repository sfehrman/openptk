/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2011 Project OpenPTK
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
package org.openptk.spi.operations;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openptk.api.Query;
import org.openptk.common.AttrIF;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.exception.OperationException;
import org.openptk.exception.QueryException;
import org.openptk.spi.LdapQueryConverter;
import org.openptk.spi.QueryConverterIF;

/**
 *
 * @author Terry Sigle, UnboundID
 */
//===================================================================
abstract class LdapOperations extends Operations
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   protected static final String VALUE_NOT_A_STRING = "__NOT_A_STRING__";
   protected static final String UNIQUEID = "uniqueid";
   private static final int RETRY_MAX = 3;
   protected Map<String, Boolean> _mapDN = null;
   protected Map<String, Boolean> _mapBaseDN = null;
   protected String PROP_AUTHEN = "authen";
   protected String PROP_BASEDN = "basedn";
   protected String PROP_DN = "template.create";
   protected String PROP_ENTRYDN = "entrydn";
   protected String PROP_OBJECTCLASS = "objectclass";
   protected String PROP_SEARCHSCOPE = "scope";
   protected String DEFAULT_URL = "ldap://localhost:389";
   protected String DEFAULT_AUTHEN = "simple";
   protected String DEFAULT_DN = "uid=${uid},ou=People,dc=openptk,dc=org";
   protected String DEFAULT_BASEDN = "ou=People,dc=openptk,dc=org";
   protected String DEFAULT_SEARCHSCOPE = "SUBTREE_SCOPE";
   protected String DEFAULT_OBJECTCLASS = "inetOrgPerson";
   protected String DEFAULT_BINDDN = "cn=Directory Manager";
   protected String DEFAULT_PASSWORD = "openptk";

   //----------------------------------------------------------------
   public LdapOperations()
   //----------------------------------------------------------------
   {
      super();

      _mapDN = new LinkedHashMap<String, Boolean>();
      _mapBaseDN = new LinkedHashMap<String, Boolean>();

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   public synchronized void execute(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      Operation oper = null;

      if (request == null)
      {
         this.handleError(METHOD_NAME + "Request is null");
      }

      if (response == null)
      {
         this.handleError(METHOD_NAME + "Response is null");
      }

      response.setUniqueId(CLASS_NAME);

      oper = request.getOperation();

      switch (oper)
      {
         case CREATE:
            this.doCreate(request, response);
            break;
         case READ:
            this.doRead(request, response);
            break;
         case UPDATE:
            this.doUpdate(request, response);
            break;
         case DELETE:
            this.doDelete(request, response);
            break;
         case SEARCH:
            this.doSearch(request, response);
            break;
         case PWDCHANGE:
            this.doPasswordChange(request, response);
            break;
         case PWDRESET:
            this.doPasswordReset(request, response);
            break;
         case PWDFORGOT:
            this.doPasswordForgot(request, response);
            break;
         case AUTHENTICATE:
            this.doAuthenticate(request, response);
            break;
         default:
            throw new OperationException("Unimplemented Operation: " + oper.toString());
      }
      return;
   }

   /*
    ********************
    *  PROTECTED METHODS
    ********************
    */
   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doCreate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doCreate() ";

      this.handleError(METHOD_NAME + "Not implemented.");
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doRead(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doRead() ";

      this.handleError(METHOD_NAME + "Not implemented.");
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doSearch(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doSearch() ";

      this.handleError(METHOD_NAME + "Not implemented.");
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doUpdate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doUpdate() ";

      this.handleError(METHOD_NAME + "Not implemented.");
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doDelete(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doDelete() ";

      this.handleError(METHOD_NAME + "Not implemented.");
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doPasswordChange(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doPasswordChange() ";

      this.handleError(METHOD_NAME + "Not implemented.");
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doPasswordReset(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doPasswordReset() ";

      this.handleError(METHOD_NAME + "Not implemented.");
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doAuthenticate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doAuthenticate(): ";

      this.handleError(METHOD_NAME + "Not implemented.");
   }

   //----------------------------------------------------------------------
   protected void loadMap(Map<String, Boolean> map, String template)
   //----------------------------------------------------------------------
   {
      /*
       * process the property PROP_ENTRYDN
       * convert it into an ordered collection (Map) of literal Strings
       * and variable_name Strings.  The Boolean value in the Map is
       * used to "flag" if the String is a literal or variable. A Boolean
       * that is equal to TRUE means it is a variable.
       *
       * example:
       *     EntryDn = "uid=${uniqueid}, ou=People, o=${dept}, dc=openptk, dc=org"
       *
       *     LinkedHashMap<String, Boolean>
       *     ["uid=", FALSE]                  // literal
       *     ["uid", TRUE]                    // attribute name
       *     [", ou=People, o=", FALSE]       // literal
       *     ["dept", TRUE]                   // attribute name
       *     [", dc=openptk, dc=org", FALSE]  // literal
       */

      Boolean bDone = false;
      Boolean bIsVar = false;
      int Offset = 0;
      int Length = 0;
      char[] chars = null;
      char achar;
      StringBuffer litBuf = null;
      StringBuffer varBuf = null;

      chars = template.toCharArray();
      Length = chars.length;
      bDone = false;
      bIsVar = false;
      litBuf = new StringBuffer();
      varBuf = new StringBuffer();

      do
      {
         if (Offset < Length)
         {
            achar = chars[Offset];
            switch (achar)
            {
               case '$':  // begin variable name, part 1, do nothing, ignore it
                  break;
               case '{':  // begin variable name, part 2
                  /*
                   * if the literal buffer has data, add it to the Map
                   * reset the literal buffer to be empty
                   * set the flag, now processing a variable name
                   */
                  if (litBuf.length() > 0)
                  {
                     map.put(litBuf.toString(), Boolean.valueOf(bIsVar));
                     litBuf = new StringBuffer();
                  }
                  bIsVar = true;
                  break;
               case '}':  // end variable name
                  /*
                   * if the variable name buffer has data, add it to the Map
                   * reset the variable name buffer to be empty
                   * set the flag, now processin a literal
                   */
                  if (varBuf.length() > 0)
                  {
                     map.put(varBuf.toString(), Boolean.valueOf(bIsVar));
                     varBuf = new StringBuffer();
                  }
                  bIsVar = false;
                  break;
               default:   // a normal character

                  if (bIsVar)
                  {
                     varBuf.append(achar);
                  }
                  else
                  {
                     litBuf.append(achar);
                  }
            }
            Offset++;
         }
         else
         {
            bDone = true;
         }
      }
      while (!bDone);

      /*
       * Flush out last literal / variable name buffer
       */

      if (varBuf.length() > 0)
      {
         map.put(varBuf.toString(), Boolean.valueOf(bIsVar));
      }
      if (litBuf.length() > 0)
      {
         map.put(litBuf.toString(), Boolean.valueOf(bIsVar));
      }

      return;
   }

   //----------------------------------------------------------------------
   protected String getUniqueSearchStr(final RequestIF request) throws OperationException
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getUniqueSearchStr() ";
      String serviceUniqueAttr = null;
      String uniqueAttr = null;
      Object uniqueValue = null;
      String uniqueSearchStr = null;
      String searchStr = null;
      String contextSearchStr = null;
      ComponentIF subject = null;

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      uniqueAttr = request.getKey();
      serviceUniqueAttr = request.getService().getSrvcName(request.getOperation(), uniqueAttr);
      uniqueValue = subject.getUniqueId();

      /*
       * check to see if the uniqueid for the subject is to be used for the ldap query
       */

      if (request.getService().getKey(request.getOperation()).equalsIgnoreCase("uniqueid")
         && uniqueValue != null
         && uniqueValue.toString().length() > 0)
      {
         /*
          * the unique search attribute is the uniqueid, create the filter
          */
         uniqueSearchStr = serviceUniqueAttr + "=" + uniqueValue.toString();
         contextSearchStr = this.getLdapSearch(request);

         searchStr = "(&(" + uniqueSearchStr + ")" + contextSearchStr + ")";
      }
      else
      {
         this.handleError(METHOD_NAME + "Unique Search string not found");
      }

      return searchStr;
   }

   //----------------------------------------------------------------------
   protected String getBaseDN(final RequestIF request) throws OperationException
   //----------------------------------------------------------------------
   {
      String BaseDN = null;

      BaseDN = this.getMapData(_mapBaseDN, request);

      return BaseDN;
   }

   //----------------------------------------------------------------------
   protected String getMapData(Map<String, Boolean> dataMap, final RequestIF request) throws OperationException
   //----------------------------------------------------------------------
   {
      boolean bFoundVar = false;
      String METHOD_NAME = CLASS_NAME + ":getMapData() ";
      Object uniqueId = null;
      String subStr = null;
      String var = null;
      String retStr = null;
      String strErr = null;
      Boolean valueBool = false;
      StringBuilder buf = new StringBuilder();
      Map<String, AttrIF> attrMap = null;
      Set<String> SetDN = null;
      Collection<Boolean> BoolValues = null;
      Iterator<String> IterStr = null;
      Iterator<Boolean> IterBool = null;
      ComponentIF subject = null;
      AttrIF attr = null;

      if (dataMap != null)
      {

         /*
          * Look for values to variables in this order, stop at the first match:
          * 1. Check for a matching attribute name in the attrMap
          * 2. Look for a property matching the attribute name in the subject
          * 3. Look for a property matching the attribute name in the request
          */

         subject = request.getSubject();

         if (subject != null)
         {
            uniqueId = subject.getUniqueId();
            SetDN = dataMap.keySet();
            BoolValues = dataMap.values();
            IterStr = SetDN.iterator();
            IterBool = BoolValues.iterator();

            attrMap = subject.getAttributes();

            while (IterStr.hasNext())
            {
               subStr = IterStr.next();
               valueBool = IterBool.next();

               if (valueBool == true)
               {
                  bFoundVar = false;
                  /*
                   * This is a variable, look for the value ...
                   * Check to see if the uniqueId is requested first then the attribute Map
                   */

                  if (request.getService().getKey(request.getOperation()).equalsIgnoreCase(subStr)
                     && uniqueId != null
                     && uniqueId.toString().length() > 0)
                  {
                     if (request.getService().getKey(request.getOperation()).equalsIgnoreCase("uniqueid"))
                     {
                        buf.append(uniqueId.toString());
                        bFoundVar = true;
                     }
                  }

                  if (!bFoundVar)
                  {
                     if (attrMap != null)
                     {
                        attr = attrMap.get(subStr);
                        if (attr != null)
                        {
                           var = attr.getValue().toString();
                           if (var != null && var.length() > 0)
                           {
                              buf.append(var);
                              bFoundVar = true;
                           }
                        }
                     }
                  }
                  /*
                   * Check for a property on the subject
                   */
                  if (!bFoundVar)
                  {
                     var = subject.getProperty(subStr);
                     if (var != null && var.length() > 0)
                     {
                        buf.append(var);
                        bFoundVar = true;
                     }
                  }
                  /*
                   * Check for a property on the request
                   */
                  if (!bFoundVar)
                  {
                     var = request.getProperty(subStr);
                     if (var != null && var.length() > 0)
                     {
                        buf.append(var);
                        bFoundVar = true;
                     }
                  }
               }
               else
               {
                  /*
                   *  This is a literal string ...
                   */
                  buf.append(subStr);
               }
            }
            retStr = buf.toString();

         }
         else
         {
            strErr = "subject is NULL";
         }
      }
      else
      {
         strErr = "dataMap is NULL";
      }

      if (strErr != null)
      {
         throw new OperationException(METHOD_NAME + strErr);
      }

      return retStr;
   }

   //----------------------------------------------------------------------
   protected String getLdapSearch(final RequestIF request) throws OperationException
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getLdapSearch(): ";
      String srch = null;
      Query srvcQuery = null;
      Query userQuery = null;
      Query ldapQuery = null;
      QueryConverterIF qConverter = null;

      srvcQuery = request.getService().getQuery(request.getOperation());
      userQuery = request.getQuery();


      /*
       * if the Operation Query is set
       * set its "serviceName" equal to the "frameworkName"
       * the "frameworkName is used by the QueryConverter
       */

      if (srvcQuery != null)
      {
         srvcQuery.setServiceName(srvcQuery.getName());
      }

      /*
       * if there's a Operation Query and a User Query
       * create a new Query and "AND" them together
       * else use either the User or Operation Query
       */

      if (srvcQuery != null && userQuery != null)
      {
         ldapQuery = new Query(Query.Type.AND);

         ldapQuery.addQuery(userQuery);
         ldapQuery.addQuery(srvcQuery);
      }
      else if (srvcQuery != null)
      {
         ldapQuery = srvcQuery;
      }
      else if (userQuery != null)
      {
         ldapQuery = userQuery;
      }

      if (ldapQuery != null)
      {
         qConverter = new LdapQueryConverter(ldapQuery);
         try
         {
            srch = (String) qConverter.convert();
         }
         catch (QueryException e)
         {
            this.handleError(METHOD_NAME + e.getMessage());
         }
      }

      return srch;
   }

   //----------------------------------------------------------------------
   protected String[] getSrchCtrlAttrs(final RequestIF request, List<String> FwAttrList)
   //----------------------------------------------------------------------
   {
      String[] strArray = null;

      strArray = this.getArrayFromList(FwAttrList);

      return request.getService().toSrvc(request.getOperation(), strArray);
   }
}
