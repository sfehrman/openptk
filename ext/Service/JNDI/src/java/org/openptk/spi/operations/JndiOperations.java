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
 * Portions Copyright 2011-2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.spi.operations;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.naming.CommunicationException;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.OperationNotSupportedException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attribute;
import javax.naming.directory.AttributeModificationException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.InvalidAttributesException;
import javax.naming.directory.NoSuchAttributeException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.openptk.api.DataType;
import org.openptk.api.State;
import org.openptk.common.AttrIF;
import org.openptk.common.BasicAttr;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.debug.DebugLevel;
import org.openptk.definition.DefinitionIF;
import org.openptk.exception.OperationException;
import org.openptk.logging.Logger;

/*
 * Meaning of Response State:
 *
 * ERROR : An error with the LDAP infrastructure / configuration / connection
 * An Exception will be thrown
 *
 * SUCCESS : The operation was successful
 * Response is returned, error = false (default)
 *
 * INVALID : There is something wrong / missing from the input
 * Response is returned, error = true
 *
 * FAILED : The operation failed due to some business logic problem
 * Response is returned, error = true
 *
 * NOTEXIST : The "entry" being referenced was not found
 * Response is returned, error = true
 */
/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * @author Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public class JndiOperations extends LdapOperations
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String DESCRIPTION = "Java Naming and Directory Interface (JNDI)";
   private static final String LDAP_CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
   private static final int RETRY_MAX = 3;
   private InitialLdapContext _ldapCtx = null;
   private SearchControls _srchCtrls = null;

   //----------------------------------------------------------------
   public JndiOperations()
   //----------------------------------------------------------------
   {
      super();

      this.setDescription(JndiOperations.DESCRIPTION);
      this.setType(OperationsType.JNDI);

      /*
       * Specify which operations are implemented
       */

      this.setImplemented(Operation.CREATE, true);
      this.setImplemented(Operation.READ, true);
      this.setImplemented(Operation.UPDATE, true);
      this.setImplemented(Operation.DELETE, true);
      this.setImplemented(Operation.SEARCH, true);
      this.setImplemented(Operation.PWDCHANGE, true);
      this.setImplemented(Operation.PWDRESET, true);
      this.setImplemented(Operation.PWDFORGOT, true);
      this.setImplemented(Operation.AUTHENTICATE, true);

      /*
       * Specify which operations are enabled (by default)
       * Can be changed at run-time
       */

      this.setEnabled(Operation.CREATE, true);
      this.setEnabled(Operation.READ, true);
      this.setEnabled(Operation.UPDATE, true);
      this.setEnabled(Operation.DELETE, true);
      this.setEnabled(Operation.SEARCH, true);
      this.setEnabled(Operation.PWDCHANGE, true);
      this.setEnabled(Operation.PWDRESET, true);
      this.setEnabled(Operation.PWDFORGOT, true);
      this.setEnabled(Operation.AUTHENTICATE, true);

      return;
   }

   //----------------------------------------------------------------
   @Override
   public synchronized void startup()
   //----------------------------------------------------------------
   {
      int iTryCnt = 1;
      String METHOD_NAME = CLASS_NAME + ":startup() ";
      StringBuilder err = null;
      String template = null;
      String searchScope = null;
      boolean bContinue = true;

      super.startup();

      err = new StringBuilder();

      // RDN

      template = this.getProperty(PROP_DN);

      if (template == null || template.length() < 1)
      {
         template = DEFAULT_DN;
      }

      this.loadMap(_mapDN, template);

      // BASEDN
      template = null;
      template = this.getProperty(PROP_BASEDN);

      if (template == null || template.length() < 1)
      {
         template = DEFAULT_BASEDN;
      }

      this.loadMap(_mapBaseDN, template);

      // Scope

      searchScope = this.getProperty(PROP_SEARCHSCOPE);
      if (searchScope == null || searchScope.length() < 1)
      {
         searchScope = DEFAULT_SEARCHSCOPE;
      }

      do
      {
         bContinue = false;
         try
         {
            /*
             * Create the initial LDAP context
             */

            _ldapCtx = new InitialLdapContext(this.getParams(), null);

            _srchCtrls = new SearchControls();

            if (searchScope.equalsIgnoreCase("SUBTREE_SCOPE"))
            {
               _srchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            }
            else if (searchScope.equalsIgnoreCase("OBJECT_SCOPE"))
            {
               _srchCtrls.setSearchScope(SearchControls.OBJECT_SCOPE);
            }
            else if (searchScope.equalsIgnoreCase("ONELEVEL_SCOPE"))
            {
               _srchCtrls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            }
            else
            {
               _srchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            }
         }
         catch (NamingException e)
         {
            if (iTryCnt >= RETRY_MAX)
            {
               err.append(METHOD_NAME);
               err.append("Failed to get InitialLdapContext after ").append(RETRY_MAX).append(" attempts: ");
               err.append(e.getMessage());

               this.checkException(e, err);
            }
            else
            {
               iTryCnt++;
               bContinue = true;
            }
         }
      }
      while (bContinue);

      if (!this.isError())
      {
         this.setState(State.READY);
      }

      return;
   }

   //----------------------------------------------------------------
   @Override
   public synchronized void shutdown()
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":shutdown() ";
      Throwable cause = null;

      if (_ldapCtx != null)
      {
         try
         {
            _ldapCtx.close();
         }
         catch (Exception e)
         {
            cause = e.getCause();

            if (cause == null)
            {
               Logger.logError(METHOD_NAME + e.getMessage());
            }
            else
            {
               Logger.logError(METHOD_NAME + e.getMessage() + ", " + cause.getMessage());
            }
         }
      }

      super.shutdown();

      return;
   }

   /*
    ********************
    * PROTECTED METHODS
    ********************
    */
   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doCreate(RequestIF request, ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doCreate() ";
      String key = null;
      String fwkey = null;
      String entryDN = null;
      StringBuilder err = null;
      BasicAttributes basicAttrs = null; // javax.naming.directory
      AttrIF ptkAttr = null;
      ComponentIF subject = null;

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      err = new StringBuilder();

      response.setDescription(JndiOperations.DESCRIPTION + ": Create");

      key = request.getKey();

      entryDN = this.getEntryDN(request);

      if (entryDN != null && entryDN.length() > 0)
      {
         if (this.isDebug())
         {
            Logger.logInfo(METHOD_NAME + "EntryDN: " + entryDN);
         }

         basicAttrs = this.getLdapAttributes(request);

         this.addObjectClass(basicAttrs);

         try
         {
            _ldapCtx.createSubcontext(entryDN, basicAttrs);
         }
         catch (NameAlreadyBoundException e)
         {
            err.append("entryDN='").append(entryDN).append("', ").append(e.getMessage());
            response.setState(State.FAILED);
            response.setStatus(err.toString());
         }
         catch (InvalidAttributesException e)
         {
            err.append("entryDN='").append(entryDN).append("', ").append(e.getMessage());
            response.setState(State.INVALID);
            response.setStatus(err.toString());
         }
         catch (NamingException e)
         {
            err.append("entryDN='").append(entryDN).append("'");

            if (!this.checkNamingException(e, response, err))
            {
               this.checkException(e, err);
               response.setState(State.ERROR);
            }

            response.setStatus(err.toString());

            this.handleError(METHOD_NAME + err.toString());
         }

         /*
          * Set the response uniqueid:
          *
          * The value is based on the "key", which is an attribute that
          * must contain a unique value for the entry in a given DIT
          *
          * If the "key" is equal to "entrydn", then use the actual entrydn
          * that was created by the template.
          *
          * If the "key" is not "entrydn", then get the key's value which is
          * an attribute name (must be unique). read the attributes value
          * from the original input/request. If the attributes value is not
          * set (null), then set the response uniqueid to null.
          */

         if (key.equalsIgnoreCase(PROP_ENTRYDN))
         {
            response.setUniqueId(entryDN);
         }
         else
         {
            /*
             * The "key" is the name of the "service" attribute that's unique
             * Get the key's "framework" attribute name (fwkey).
             * If the fwkey is called "uniqueid" it's NOT an attribute in the request
             */

            fwkey = request.getService().getFwName(Operation.CREATE, key);
            if (fwkey != null && fwkey.length() > 0)
            {
               if (fwkey.equalsIgnoreCase(UNIQUEID))
               {
                  response.setUniqueId(subject);
               }
               else
               {
                  ptkAttr = subject.getAttribute(fwkey);
                  if (ptkAttr != null)
                  {
                     response.setUniqueId(ptkAttr.getValueAsString());
                  }
                  else
                  {
                     Logger.logWarning(METHOD_NAME + "Value for '" + key + "' (key) is null");
                  }
               }
            }
            else
            {
               Logger.logWarning(METHOD_NAME + "Service primary key '" + key + "' does not have a framework name");
            }
         }
      }
      else
      {
         err.append("EntryDN is NULL");
         response.setState(State.INVALID);
         response.setStatus(err.toString());
      }

      if (!response.isError())
      {
         response.setState(State.SUCCESS);
         response.setStatus("Entry created");
      }

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @SuppressWarnings("rawtypes")
   @Override
   protected void doRead(RequestIF request, ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doRead() ";
      String keySrvc = null;
      Object keyValue = null;
      String searchStr = null;
      String baseDN = null;
      StringBuilder err = null;
      List<String> attrNames = null;
      List<ComponentIF> ptkResultsList = null;  // OpenPTK
      Map<String, String> fw2srvc = null;
      NamingEnumeration ldapResults = null;   // javax.naming
      ComponentIF subject = null;

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      err = new StringBuilder();

      fw2srvc = request.getService().getFw2SrvcNames(request.getOperation());

      response.setDescription(JndiOperations.DESCRIPTION + ": Read");

      keyValue = subject.getUniqueId();

      if (keyValue != null && keyValue.toString().length() > 0)
      {
         keySrvc = request.getKey();

         if (keySrvc != null && keySrvc.length() > 0)
         {
            searchStr = this.getUniqueSearchStr(request);

            if (searchStr != null && searchStr.length() > 0)
            {
               baseDN = this.getBaseDN(request);

               response.setUniqueId(searchStr);

               attrNames = request.getSubject().getAttributesNames();
               attrNames.add(keySrvc);  // explicitly add the uniqueId

               if (attrNames != null && attrNames.size() > 0)
               {
                  _srchCtrls.setReturningAttributes(this.getSrchCtrlAttrs(request, attrNames));
               }
               else
               {
                  _srchCtrls.setReturningAttributes(null);
               }

               try
               {
                  ldapResults = _ldapCtx.search(baseDN, searchStr, _srchCtrls);
               }
               catch (NamingException e)
               {
                  err.append("filter='").append(searchStr).append("'");

                  if (!this.checkNamingException(e, response, err))
                  {
                     this.checkException(e, err);
                     response.setState(State.ERROR);
                  }

                  response.setStatus(err.toString());

                  this.handleError(METHOD_NAME + err.toString());
               }
            }
            else
            {
               err.append("Unique Search string is not found");

               response.setState(State.INVALID);
               response.setStatus(err.toString());

               this.handleError(METHOD_NAME + err.toString());
            }
         }
         else
         {
            err.append("Unique Id attribute name is not set");

            response.setState(State.INVALID);
            response.setStatus(err.toString());
         }
      }
      else
      {
         err.append("UniqueId value is not set");

         response.setState(State.INVALID);
         response.setStatus(err.toString());
      }

      if (!response.isError())
      {
         ptkResultsList = this.getPtkResults(request, ldapResults);

         response.setResults(ptkResultsList);

         if (ptkResultsList.size() == 1)
         {
            response.setState(State.SUCCESS);
            response.setStatus("Entry Found");
         }
         else if (ptkResultsList.isEmpty())
         {
            response.setState(State.NOTEXIST);
            response.setStatus("Entry does not exist");
         }
         else
         {
            response.setState(State.FAILED);
            response.setStatus("Invalid results, more than one entry was found");
         }
      }

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @SuppressWarnings("rawtypes")
   @Override
   protected void doSearch(RequestIF request, ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doSearch() ";
      String keySrvc = null;
      String searchStr = null;
      String baseDN = null;
      StringBuilder err = null;
      List<String> attrNames = null;
      List<ComponentIF> ptkResultsList = null;  // OpenPTK
      NamingEnumeration ldapResults = null;   // javax.naming
      Map<String, String> fw2srvc = null;
      ComponentIF subject = null;

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      err = new StringBuilder();

      fw2srvc = request.getService().getFw2SrvcNames(request.getOperation());

      response.setDescription(JndiOperations.DESCRIPTION + ": Search");

      searchStr = this.getLdapSearch(request);
      baseDN = this.getBaseDN(request);

      if (searchStr != null && searchStr.length() > 0)
      {
         keySrvc = request.getKey();
         attrNames = subject.getAttributesNames();
         attrNames.add(keySrvc);  // explicitly add the uniqueId

         if (attrNames != null && attrNames.size() > 0)
         {
            _srchCtrls.setReturningAttributes(this.getSrchCtrlAttrs(request, attrNames));
         }
         else
         {
            _srchCtrls.setReturningAttributes(null);
         }

         if (this.isDebug())
         {
            Logger.logInfo(METHOD_NAME + "Search='" + searchStr + "'");
         }

         try
         {
            ldapResults = _ldapCtx.search(baseDN, searchStr, _srchCtrls);
         }
         catch (NamingException e)
         {
            err.append("filter='").append(searchStr).append("'");

            if (!this.checkNamingException(e, response, err))
            {
               this.checkException(e, err);
               response.setState(State.ERROR);
            }

            response.setStatus(err.toString());

            this.handleError(METHOD_NAME + err.toString());
         }
      }
      else
      {
         err.append("Search value is not set");

         response.setState(State.INVALID);
         response.setStatus(err.toString());
      }

      if (!response.isError())
      {
         ptkResultsList = this.getPtkResults(request, ldapResults);

         if (ptkResultsList.isEmpty())
         {
            response.setState(State.SUCCESS);
            response.setStatus("Nothing was found");
         }
         else
         {
            response.setResults(ptkResultsList);
            response.setState(State.SUCCESS);
            response.setStatus("Entries found: " + ptkResultsList.size());
         }
      }

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doUpdate(RequestIF request, ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doUpdate() ";
      String entryDN = null;
      StringBuilder err = null;
      BasicAttributes ldapAttrs = null; // javax.naming.directory

      response.setDescription(JndiOperations.DESCRIPTION + ": Update");

      err = new StringBuilder();

      entryDN = this.getEntryDN(request);

      if (entryDN != null && entryDN.length() > 0)
      {
         if (this.isDebug())
         {
            Logger.logInfo(METHOD_NAME + "EntryDN: " + entryDN);
         }

         ldapAttrs = this.getLdapAttributes(request);

         try
         {
            _ldapCtx.modifyAttributes(entryDN, 2, ldapAttrs);
         }
         catch (AttributeModificationException e)
         {
            err.append("entryDN='").append(entryDN).append("', ").append(e.getMessage());

            response.setState(State.INVALID);
            response.setStatus(err.toString());
         }
         catch (NameNotFoundException e)
         {
            err.append("entryDN='").append(entryDN).append("', ").append(e.getMessage());

            response.setState(State.NOTEXIST);
            response.setStatus(err.toString());
         }
         catch (NamingException e)
         {
            err.append("entryDN='").append(entryDN).append("'");

            if (this.checkNamingException(e, response, err))
            {
            }
            else
            {
               this.checkException(e, err);
               response.setState(State.ERROR);
            }

            response.setStatus(err.toString());

            this.handleError(METHOD_NAME + err.toString());
         }
      }
      else
      {
         err.append("EntryDN is NULL");

         response.setState(State.INVALID);
         response.setStatus(err.toString());
      }

      if (!response.isError())
      {
         response.setState(State.SUCCESS);
         response.setStatus("Entry Updated");
      }

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doDelete(RequestIF request, ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doDelete() ";
      String entryDN = null;
      StringBuilder err = null;

      response.setDescription(JndiOperations.DESCRIPTION + ": Delete");

      err = new StringBuilder();

      entryDN = this.getEntryDN(request);

      if (entryDN != null && entryDN.length() > 0)
      {
         if (this.isDebug())
         {
            Logger.logInfo(METHOD_NAME + "EntryDN: " + entryDN);
         }

         response.setUniqueId(entryDN);

         try
         {
            _ldapCtx.destroySubcontext(entryDN);
         }
         catch (NameNotFoundException e)
         {
            err.append("entryDN='").append(entryDN).append("', ").append(e.getMessage());

            response.setState(State.NOTEXIST);
            response.setStatus(err.toString());
         }
         catch (NotContextException e)
         {
            err.append("entryDN='").append(entryDN).append("', ").append(e.getMessage());

            response.setState(State.ERROR);
            response.setStatus(err.toString());
         }
         catch (ContextNotEmptyException e)
         {
            err.append("entryDN='").append(entryDN).append("', ").append(e.getMessage());

            response.setState(State.ERROR);
            response.setStatus(err.toString());
         }
         catch (NamingException e)
         {
            err.append("entryDN='").append(entryDN).append("'");

            if (!this.checkNamingException(e, response, err))
            {
               this.checkException(e, err);
               response.setState(State.ERROR);
            }
            response.setStatus(err.toString());

            this.handleError(METHOD_NAME + err.toString());
         }
      }
      else
      {
         err.append("EntryDN is NULL");

         response.setState(State.NOTEXIST);
         response.setStatus(err.toString());
      }

      if (!response.isError())
      {
         response.setState(State.SUCCESS);
         response.setStatus("Entry Deleted");
      }

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doPasswordChange(RequestIF request, ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      this.checkPasswordAttribute(request);
      this.doUpdate(request, response);

      response.setDescription(JndiOperations.DESCRIPTION + ": Password Change");

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doPasswordReset(RequestIF request, ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      this.getPasswordAttribute(request);
      this.checkPasswordAttribute(request);
      this.doUpdate(request, response);
      this.getPasswordResult(response);

      response.setDescription(JndiOperations.DESCRIPTION + ": Password Reset");

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doAuthenticate(RequestIF request, ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doAuthenticate(): ";
      String err = null;
      String url = null;
      String entryDN = null;
      String password = null;
      String pwdAttrName = null;
      InitialLdapContext authenCtx = null;
      AttrIF attr = null;
      Hashtable<String, String> params = null;
      ComponentIF subject = null;

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      response.setDescription(JndiOperations.DESCRIPTION + "Authenticate");

      url = this.getProperty(OperationsIF.PROP_URL);
      entryDN = this.getEntryDN(request);

      if (entryDN != null && entryDN.length() > 0)
      {
         response.setUniqueId(entryDN);

         pwdAttrName = subject.getProperty(DefinitionIF.PROP_PASSWORD_ATTR_NAME);
         if (pwdAttrName != null && pwdAttrName.length() > 0)
         {
            attr = subject.getAttribute(pwdAttrName);
            if (attr != null)
            {
               password = attr.getValueAsString();
               if (password != null && password.length() > 0)
               {
                  params = new Hashtable<String, String>(11);
                  params.put(Context.INITIAL_CONTEXT_FACTORY, JndiOperations.LDAP_CTX_FACTORY);
                  params.put(Context.PROVIDER_URL, url);
                  params.put(Context.SECURITY_AUTHENTICATION, this.DEFAULT_AUTHEN);
                  params.put(Context.SECURITY_PRINCIPAL, entryDN);
                  params.put(Context.SECURITY_CREDENTIALS, password);

                  try
                  {
                     authenCtx = new InitialLdapContext(params, null);
                  }
                  catch (NamingException ex)
                  {
                     response.setError(true);
                     response.setState(State.NOTAUTHENTICATED);
                     response.setStatus(ex.getMessage());
                  }
               }
               else
               {
                  err = "The '" + pwdAttrName + "' attribute is empty";

                  response.setState(State.INVALID);
                  response.setStatus(err);
               }
            }
            else
            {
               err = "The '" + pwdAttrName + "' attribute does not exist";

               response.setState(State.INVALID);
               response.setStatus(err);
            }
         }
         else
         {
            err = "The Property for the Password Attribute is null: '"
                    + DefinitionIF.PROP_PASSWORD_ATTR_NAME + "'";

            response.setState(State.ERROR);
            response.setStatus(err);
            this.handleError(METHOD_NAME + err);
         }
      }
      else
      {
         err = "EntryDN is NULL";

         response.setState(State.INVALID);
         response.setStatus(err);
      }

      if (!response.isError())
      {
         response.setState(State.AUTHENTICATED);
         response.setStatus("Authenticated: " + entryDN);
      }

      return;
   }

   /*
    *****************
    * PRIVATE METHODS
    *****************
    */
   //----------------------------------------------------------------------
   @SuppressWarnings("rawtypes")
   private Hashtable getParams()
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getParams() ";
      String url = null;
      String authen = null;
      String username = null;
      String password = null;
      Hashtable<String, String> envParams = null;

      /*
       * using "global" scoped variables, an environmental parameters
       * object is created and populated with required JNDI data
       * to establish the LDAP binding
       */

      envParams = new Hashtable<String, String>(11);

      url = this.getProperty(OperationsIF.PROP_URL);
      authen = this.getProperty(PROP_AUTHEN);
      username = this.getValue(OperationsIF.PROP_USER_NAME);
      password = this.getValue(OperationsIF.PROP_USER_PASSWORD);

      if (this.getDebugLevel() != DebugLevel.NONE)
      {
         Logger.logInfo(METHOD_NAME + "providerURL: " + url);
      }

      envParams.put(Context.INITIAL_CONTEXT_FACTORY,
              JndiOperations.LDAP_CTX_FACTORY);

      if (url == null)
      {
         url = DEFAULT_URL;
      }

      envParams.put(Context.PROVIDER_URL, url);

      if ((username != null && username.length() > 0)
              && (password != null && password.length() > 0))
      {
         if (authen == null)
         {
            authen = DEFAULT_AUTHEN;
         }

         envParams.put(Context.SECURITY_AUTHENTICATION, authen);
         envParams.put(Context.SECURITY_PRINCIPAL, username);
         envParams.put(Context.SECURITY_CREDENTIALS, password);
      }
      else
      {
         envParams.put(Context.SECURITY_AUTHENTICATION, DEFAULT_AUTHEN);
         envParams.put(Context.SECURITY_PRINCIPAL, DEFAULT_BINDDN);
         envParams.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);
      }

      if (this.getDebugLevel() == DebugLevel.FINER
              || this.getDebugLevel() == DebugLevel.FINEST)
      {
         Logger.logInfo(METHOD_NAME + "envParams: " + envParams.toString());
      }

      return envParams;
   }

   //----------------------------------------------------------------------
   private BasicAttribute getBasicAttribute(AttrIF ptkAttr, Operation operation) throws OperationException
   //----------------------------------------------------------------------
   {
      String name = null;
      BasicAttribute basicAttr = null;

      if (ptkAttr != null)
      {
         name = ptkAttr.getServiceName();
         if (operation == Operation.CREATE && ptkAttr.getValue() != null)
         {
            basicAttr = new BasicAttribute(name);
            this.setBasicAttributeValue(basicAttr, ptkAttr);
         }
         else if ((operation == Operation.UPDATE)
                 || (operation == Operation.PWDCHANGE)
                 || (operation == Operation.PWDRESET))
         {
            basicAttr = new BasicAttribute(name);
            this.setBasicAttributeValue(basicAttr, ptkAttr);
         }
      }

      return (basicAttr);
   }

   //----------------------------------------------------------------------
   private void setBasicAttributeValue(BasicAttribute basicAttr, AttrIF ptkAttr) throws OperationException
   //----------------------------------------------------------------------
   {
      boolean bMultivalued = false;
      String METHOD_NAME = CLASS_NAME + ":setBasicAttributeValue(): ";
      String name = null;
      Object value = null;
      String[] strValues = null;
      Object[] objValues = null;
      Long[] lValues = null;
      Integer[] iValues = null;
      Boolean[] bValues = null;
      DataType type = null;

      name = ptkAttr.getServiceName();
      value = ptkAttr.getValue();
      type = ptkAttr.getType();
      bMultivalued = ptkAttr.isMultivalued();

      switch (type)
      {
         case BOOLEAN:
            if (value == null)
            {
               this.handleError(METHOD_NAME + "Boolean Attribute '" + name
                       + "' has a null value");
            }
            if (bMultivalued)
            {
               bValues = (Boolean[]) value;
               for (int i = 0; i < bValues.length; i++)
               {
                  basicAttr.add(i, bValues[i]);
               }
            }
            else
            {
               basicAttr.add((Boolean) value); //must case to Boolean
            }
            break;
         case INTEGER:
            if (value == null)
            {
               this.handleError(METHOD_NAME + "Integer Attribute '" + name
                       + "' has a null value");
            }
            if (bMultivalued)
            {
               iValues = (Integer[]) value;
               for (int i = 0; i < iValues.length; i++)
               {
                  basicAttr.add(i, iValues[i]);
               }
            }
            else
            {
               basicAttr.add((Integer) value); // must cast to Integer
            }
            break;
         case LONG:
            if (value == null)
            {
               this.handleError(METHOD_NAME + "Long Attribute '" + name
                       + "' has a null value");
            }
            if (bMultivalued)
            {
               lValues = (Long[]) value;
               for (int i = 0; i < lValues.length; i++)
               {
                  basicAttr.add(i, lValues[i]);
               }
            }
            else
            {
               basicAttr.add((Long) value); // must cast to Long
            }
            break;
         case STRING:
            if (value != null)
            {
               if (bMultivalued)
               {
                  strValues = (String[]) value;
                  for (int i = 0; i < strValues.length; i++)
                  {
                     basicAttr.add(i, strValues[i]);
                  }
               }
               else
               {
                  if (((String) value).length() > 0)
                  {
                     basicAttr.add((String) value); // must cast to String
                  }
               }
            }
            break;
         default: // Object
            if (value != null)
            {
               if (bMultivalued)
               {
                  objValues = (Object[]) value;
                  for (int i = 0; i < objValues.length; i++)
                  {
                     basicAttr.add(i, objValues[i]);
                  }
               }
               else
               {
                  basicAttr.add(value);
               }
            }
            break;
      }
      return;
   }

   //----------------------------------------------------------------------
   @SuppressWarnings("rawtypes")
   private String getEntryDN(RequestIF request) throws OperationException
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getEntryDN() ";
      Operation operation = null;
      String entryDN = null;
      String err = null;
      int numResults = 0;
      String searchStr = null;
      String baseDN = null;
      List<String> attrNames = null;
      NamingEnumeration ldapResults = null;   // javax.naming
      Map<String, String> fw2srvc = null;
      SearchResult sr = null;
      operation = request.getOperation();

      if (operation == Operation.CREATE)
      {
         entryDN = this.getMapData(_mapDN, request);
      }
      else
      {
         /*
          * Build a map and create a JDNI query with each of them,
          * and the values in the subject attributes.
          */

         fw2srvc = request.getService().getFw2SrvcNames(request.getOperation());

         baseDN = this.getBaseDN(request);
         searchStr = this.getUniqueSearchStr(request);

         if (searchStr != null && searchStr.length() > 0)
         {
            attrNames = request.getSubject().getAttributesNames();
            attrNames.add(request.getKey());  // explicitly add the uniqueId

            if (attrNames != null && attrNames.size() > 0)
            {
               _srchCtrls.setReturningAttributes(this.getSrchCtrlAttrs(request, attrNames));
            }
            else
            {
               _srchCtrls.setReturningAttributes(null);
            }

            if (this.isDebug())
            {
               Logger.logInfo(METHOD_NAME + "Search='" + searchStr + "'");
            }

            try
            {
               ldapResults = _ldapCtx.search(baseDN, searchStr, _srchCtrls);

               while (ldapResults != null && ldapResults.hasMore())
               {
                  numResults += 1;
                  sr = (SearchResult) ldapResults.next();
                  if (numResults == 1)
                  {
                     entryDN = sr.getNameInNamespace();
                  }
                  if (this.isDebug())
                  {
                     Logger.logInfo(METHOD_NAME + "Found EntryDN = '" + entryDN);
                  }
               }
            }
            catch (NamingException e)
            {
               err = e.getMessage() + ", '" + searchStr + "'";
            }
            /*
             * if the number of results is not 1, error uniquely identifying user
             */
            if (numResults == 0)
            {
               entryDN = null;
            }
            if (numResults > 1)
            {
               err = "More than one entry was found, not unique";
            }
         }
         else
         {
            err = "Search value is not set";
         }
      }

      if (err != null)
      {
         this.handleError(METHOD_NAME + err);
      }

      return entryDN;
   }

   //----------------------------------------------------------------------
   private Component getPtkComponent(RequestIF request, SearchResult entry)
   //----------------------------------------------------------------------
   {
      boolean hasUniqueId = false;
      String METHOD_NAME = CLASS_NAME + ":getPtkComponent(): ";
      String key = null;
      String fwName = null;
      String srvcName = null;
      String entryDN = null;
      Component ptkComp = null;    // OpenPTK
      AttrIF requestAttr = null;   // OpenPTK
      AttrIF responseAttr = null;  // OpenPTK
      Attributes ldapAttrs = null; // javax.naming.directory
      Attribute ldapAttr = null;   // javax.naming.directory
      NamingEnumeration<String> nameEnum = null; // javax.naming

      ptkComp = new Component();

      if (entry != null)
      {
         key = request.getKey();

         /*
          * Set the response (ptkComp) uniqueid.
          * The value is based on the "key" defined in the request.
          * The "key" can be either the LDAP "entrydn" or another
          * attribute (that must be unique) in the LDAP record,
          * such as "mail", "employeenumber", "uid", "ssn", etc.
          *
          * Here we will check to see if the "key" is set to "entrydn"
          * and if it is, set the response (ptkComp) uniqueId
          */

         if (key.equalsIgnoreCase(PROP_ENTRYDN))
         {
            ptkComp.setUniqueId(entry.getNameInNamespace());
            hasUniqueId = true;
         }

         /*
          * Check to see if the entrydn is requested (as an attribute)
          * if so, return it as a PTK Attribute
          */

         if (request.getSubject().getAttributesNames().contains(PROP_ENTRYDN))
         {
            entryDN = entry.getNameInNamespace();
            if (entryDN != null)
            {
               fwName = request.getService().getFwName(request.getOperation(), PROP_ENTRYDN);
               requestAttr = new BasicAttr(PROP_ENTRYDN, entryDN);
               ptkComp.setAttribute(fwName, requestAttr);
            }
            else
            {
               ptkComp.setState(State.NULL);
               ptkComp.setStatus("No entrydn found.");
               return ptkComp;
            }
         }

         /*
          * Enumerate through all the attribute NAMES (ids)
          */

         ldapAttrs = entry.getAttributes();

         if (ldapAttrs != null)
         {
            nameEnum = ldapAttrs.getIDs();
            if (nameEnum != null)
            {
               try
               {
                  while (nameEnum.hasMore())
                  {
                     /*
                      * Enumerate through all the VALUES of an attribute
                      */

                     responseAttr = null;
                     fwName = null;
                     srvcName = null;
                     srvcName = nameEnum.next();
                     if (srvcName != null)
                     {
                        fwName = request.getService().getFwName(request.getOperation(), srvcName);
                        ldapAttr = ldapAttrs.get(srvcName);

                        /*
                         * An attribute (non-entrydn) may be used as the
                         * OpenPTK uniqueid, defined as the "key"
                         * If the ptkComp does not have a uniqueId set, check
                         * all of the attributes to determine which one is
                         * the "key"
                         */

                        if (!hasUniqueId && srvcName.equals(key))
                        {
                           ptkComp.setUniqueId((String) ldapAttr.get(0));
                           hasUniqueId = true;
                        }
                        else
                        {
                           requestAttr = request.getSubject().getAttribute(fwName);
                           responseAttr = this.getPtkAttr(requestAttr, ldapAttr);
                           if (responseAttr != null)
                           {
                              responseAttr.setServiceName(srvcName);
                              ptkComp.setAttribute(fwName, responseAttr);
                           }
                        }
                     }
                  }
               }
               catch (NamingException e)
               {
                  ptkComp.setState(State.ERROR);
                  ptkComp.setStatus(METHOD_NAME + ": " + e.getMessage());
               }
            }
            else
            {
               ptkComp.setState(State.NULL);
               ptkComp.setStatus("Attribute Enumeration is NULL");
            }
         }
         else
         {
            ptkComp.setStatus("No Attributes");
         }
      }
      else
      {
         ptkComp.setState(State.NULL);
         ptkComp.setStatus("The Entry is NULL");
      }

      if (!hasUniqueId)
      {
         ptkComp.setState(State.ERROR);
         ptkComp.setStatus(METHOD_NAME + "UniqueId has not be set, key='" + key + "'");
      }

      ptkComp.setDebug(this.isDebug());
      ptkComp.setDebugLevel(this.getDebugLevel());

      return ptkComp;
   }

   //----------------------------------------------------------------------
   private AttrIF getPtkAttr(AttrIF requestAttr, Attribute ldapAttr) throws NamingException
   //----------------------------------------------------------------------
   {
      int size = 0;
      Object valObj = null;
      Object[] valObjArray = null;
      Boolean valBool = null;
      Boolean[] valBoolArray = null;
      Integer valInt = null;
      Integer[] valIntArray = null;
      Long valLong = null;
      Long[] valLongArray = null;
      String fwName = null;
      String valStr = null;
      String[] valStrArray = null;
      AttrIF ptkAttr = null;     // OpenPTK
      DataType type = null; // OpenPTK;

      if (requestAttr != null && ldapAttr != null)
      {
         fwName = requestAttr.getName();
         type = requestAttr.getType();
         size = ldapAttr.size();

         if (size == 0)
         {
            ptkAttr = new BasicAttr(fwName);
         }
         else
         {
            switch (type)
            {
               case BOOLEAN:
                  if (size == 1)
                  {
                     valObj = ldapAttr.get(0);
                     if (valObj instanceof Boolean)
                     {
                        valBool = (Boolean) valObj;
                     }
                     else
                     {
                        valBool = null;
                     }
                     ptkAttr = new BasicAttr(fwName, valBool);
                  }
                  else
                  {
                     valBoolArray = new Boolean[size];
                     for (int i = 0; i < size; i++)
                     {
                        valObj = ldapAttr.get(i);
                        if (valObj instanceof Boolean)
                        {
                           valBool = (Boolean) valObj;
                        }
                        else
                        {
                           valBool = null;
                        }
                        valBoolArray[i] = valBool;
                     }
                     ptkAttr = new BasicAttr(fwName, valBoolArray);
                  }
                  break;
               case INTEGER:
                  if (size == 1)
                  {
                     valObj = ldapAttr.get(0);
                     if (valObj instanceof Integer)
                     {
                        valInt = (Integer) valObj;
                     }
                     else
                     {
                        valInt = null;
                     }
                     ptkAttr = new BasicAttr(fwName, valInt);
                  }
                  else
                  {
                     valIntArray = new Integer[size];
                     for (int i = 0; i < size; i++)
                     {
                        valObj = ldapAttr.get(i);
                        if (valObj instanceof Integer)
                        {
                           valInt = (Integer) valObj;
                        }
                        else
                        {
                           valInt = null;
                        }
                        valIntArray[i] = valInt;
                     }
                     ptkAttr = new BasicAttr(fwName, valIntArray);
                  }
                  break;
               case LONG:
                  if (size == 1)
                  {
                     valObj = ldapAttr.get(0);
                     if (valObj instanceof Long)
                     {
                        valLong = (Long) valObj;
                     }
                     else
                     {
                        valObj = null;
                     }
                     ptkAttr = new BasicAttr(fwName, valLong);
                  }
                  else
                  {
                     valLongArray = new Long[size];
                     for (int i = 0; i < size; i++)
                     {
                        valObj = ldapAttr.get(i);
                        if (valObj instanceof Long)
                        {
                           valLong = (Long) valObj;
                        }
                        else
                        {
                           valLong = null;
                        }
                        valLongArray[i] = valLong;
                     }
                     ptkAttr = new BasicAttr(fwName, valLongArray);
                  }
                  break;
               case STRING:
                  if (size == 1)
                  {
                     valObj = ldapAttr.get(0);
                     if (valObj instanceof String)
                     {
                        valStr = (String) valObj;
                     }
                     else
                     {
                        valStr = JndiOperations.VALUE_NOT_A_STRING;
                     }
                     ptkAttr = new BasicAttr(fwName, valStr);
                  }
                  else
                  {
                     valStrArray = new String[size];
                     for (int i = 0; i < size; i++)
                     {
                        valObj = ldapAttr.get(i);
                        if (valObj instanceof String)
                        {
                           valStr = (String) valObj;
                        }
                        else
                        {
                           valStr = JndiOperations.VALUE_NOT_A_STRING;
                        }
                        valStrArray[i] = valStr;
                     }
                     ptkAttr = new BasicAttr(fwName, valStrArray);
                  }
                  break;
               default: // Object
                  if (size == 1)
                  {
                     valObj = ldapAttr.get(0);
                     ptkAttr = new BasicAttr(fwName, valObj);
                  }
                  else
                  {
                     valObjArray = new Object[size];
                     for (int i = 0; i < size; i++)
                     {
                        valObj = ldapAttr.get(i);
                        valObjArray[i] = valObj;
                     }
                     ptkAttr = new BasicAttr(fwName, valObjArray);
                  }
                  break;
            }
         }
      }

      return ptkAttr;
   }

   //----------------------------------------------------------------------
   private BasicAttributes getLdapAttributes(RequestIF request) throws OperationException
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getLdapAttributes(): ";
      BasicAttributes ldapAttrs = null;      // javax.naming.directory
      BasicAttribute ldapAttr = null;        // javax.naming.directory
      Map<String, AttrIF> ptkAttrMap = null; // OpenPTK
      Iterator<AttrIF> ptkIter = null;       // OpenPTK
      AttrIF ptkAttr = null;                 // OpenPTK

      ptkAttrMap = request.getSubject().getAttributes();

      if (ptkAttrMap != null)
      {
         ptkIter = ptkAttrMap.values().iterator();

         if (ptkIter != null)
         {
            ldapAttrs = new BasicAttributes(true);

            while (ptkIter.hasNext())
            {
               ptkAttr = ptkIter.next();
               if (ptkAttr != null)
               {
                  ldapAttr = null;
                  ldapAttr = this.getBasicAttribute(ptkAttr, request.getOperation());
                  if (ldapAttr != null)
                  {
                     ldapAttrs.put(ldapAttr);
                  }
               }
            }
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "Request has no attributes.");
      }

      return ldapAttrs;
   }

   //----------------------------------------------------------------------
   @SuppressWarnings("rawtypes")
   private List<ComponentIF> getPtkResults(RequestIF request, NamingEnumeration ldapResults) throws OperationException
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getPtkResults(): ";
      String strErr = null;
      List<ComponentIF> ptkResultsList = null;  // OpenPTK
      ComponentIF ptkComponent = null;          // OpenPTK
      SearchResult ldapEntry = null;          // javax.naming.directory

      ptkResultsList = new LinkedList<ComponentIF>();

      if (ldapResults != null)
      {
         try
         {
            while (ldapResults.hasMore())
            {
               ldapEntry = null;
               ldapEntry = (SearchResult) ldapResults.next();
               ptkComponent = this.getPtkComponent(request, ldapEntry);
               ptkResultsList.add(ptkComponent);
            }
         }
         catch (NamingException e)
         {
            strErr = METHOD_NAME + ", " + e.getMessage();
            this.handleError(strErr);
         }
      }
      else
      {
         ptkComponent = new Component();
         ptkComponent.setState(State.NULL);
         ptkComponent.setStatus(METHOD_NAME + "Results are NULL: ");

         ptkResultsList.add(ptkComponent);
      }

      return ptkResultsList;
   }

   //----------------------------------------------------------------------
   private void addObjectClass(BasicAttributes bAttrs)
   //----------------------------------------------------------------------
   {
      String objectClass = null;
      String str = null;
      StringTokenizer strTok = null;
      BasicAttribute attr = null;

      objectClass = this.getProperty(PROP_OBJECTCLASS);

      if (objectClass == null)
      {
         objectClass = DEFAULT_OBJECTCLASS;
      }

      strTok = new StringTokenizer(objectClass, ",");

      attr = new BasicAttribute("objectClass");

      while (strTok.hasMoreTokens())
      {
         str = strTok.nextToken();
         attr.add(str);
      }

      bAttrs.put(attr);

      return;
   }

   //----------------------------------------------------------------------
   private boolean checkNamingException(final NamingException ex, ResponseIF response, StringBuilder buf)
   //----------------------------------------------------------------------
   {
      boolean foundException = false;
      String str = null;

      if (buf == null)
      {
         buf = new StringBuilder();
      }
      else
      {
         if (buf.length() > 0)
         {
            buf.append(", ");
         }
      }

      if (ex instanceof OperationNotSupportedException)
      {
         /*
          * Known usage:
          * OpenDS 2.2: [LDAP: error code 53 - The provided new password was found in the password history for the user]
          */
         response.setState(State.FAILED);
         foundException = true;
      }
      else
      {
         if (ex instanceof InvalidAttributeValueException)
         {
            /*
             * Known usage:
             * Oracle DSEE 11g: [LDAP: error code 19 - password in history]
             */
            response.setState(State.FAILED);
            foundException = true;
         }
         else
         {
            if (ex instanceof CommunicationException)
            {
               response.setState(State.ERROR);
               foundException = true;
            }
            else
            {
               if (ex instanceof ConfigurationException)
               {
                  response.setState(State.ERROR);
                  foundException = true;
               }
               else
               {
                  if (ex instanceof InvalidAttributeIdentifierException)
                  {
                     response.setState(State.ERROR);
                     foundException = true;
                  }
                  else
                  {
                     if (ex instanceof NoSuchAttributeException)
                     {
                        response.setState(State.ERROR);
                        foundException = true;
                     }
                     else
                     {
                        if (ex instanceof ServiceUnavailableException)
                        {
                           response.setState(State.ERROR);
                           foundException = true;
                        }
                     }
                  }
               }
            }
         }
      }

      str = ex.getMessage();
      buf.append((str != null ? str : "(NULL)"));

      return foundException;
   }
}
