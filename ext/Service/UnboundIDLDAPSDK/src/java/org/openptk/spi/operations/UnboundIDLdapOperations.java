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

//import java.net.MalformedURLException;
//import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.FailoverServerSet;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.LDAPURL;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.ServerSet;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.SingleServerSet;

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
 * ERROR   : An error with the LDAP infrastructure / configuration / connection
 *           An Exception will be thrown
 *
 * SUCCESS : The operation was successful
 *           Response is returned, error = false (default)
 *
 * INVALID : There is something wrong / missing from the input
 *           Response is returned, error = true
 *
 * FAILED  : The operation failed due to some business logic problem
 *           Response is returned, error = true
 *
 * NOTEXIST : The "entry" being referenced was not found
 *           Response is returned, error = true
 */
/**
 *
 * @author Terry Sigle, UnboundID
 */
//===================================================================
public class UnboundIDLdapOperations extends LdapOperations
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String DESCRIPTION = "UnboundID LDAP SDK";
   private static final int RETRY_MAX = 3;
   private LDAPConnectionPool _connectionPool = null;
   private ServerSet _serverSet = null;
   private SimpleBindRequest _simpleBindRequest = null;
   private String _ldapBindDN = null;
   private String _ldapPassword = null;
   private int _connectionPoolMin = 0;
   private int _connectionPoolMax = 0;
   private SearchScope DEFAULT_UNBOUNDID_SEARCHSCOPE = SearchScope.SUB;
   private final String PROP_CONNECTION_POOL_MIN = "connection.pool.min";
   private final String PROP_CONNECTION_POOL_MAX = "connection.pool.max";
   private final String PROP_UNBOUNDID_INMEMORY = "unboundid.inmemory";
   private final String PROP_UNBOUNDID_INMEMORY_ROOTDSE = "unboundid.inmemory.rootdse";
   private final String PROP_UNBOUNDID_INMEMORY_LDIF = "unboundid.inmemory.ldif";

   /*
    * Variables used for the UnboundID InMemory Directory Server
    */
   private boolean _useInMemoryServer = false;
   private String _inMemoryRootDSE = null;
   private String _inMemoryLdif = null;
   private InMemoryDirectoryServer _inMemoryServer = null;

   //----------------------------------------------------------------
   public UnboundIDLdapOperations()
   //----------------------------------------------------------------
   {
      super();
      
      this.setDescription(DESCRIPTION);
      this.setType(OperationsType.UNBOUNDID);

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
      String METHOD_NAME = CLASS_NAME + ":startup() ";
      String template = null;

      super.startup();

      // RDN

      template = this.getProperty(PROP_DN);

      if (template == null)
      {
         template = DEFAULT_DN;
      }

      this.loadMap(_mapDN, template);

      // BASEDN

      template = null;
      template = this.getProperty(PROP_BASEDN);

      if (template == null)
      {
         template = DEFAULT_BASEDN;
      }

      this.loadMap(_mapBaseDN, template);

      /*
       * Following methods will initilize different class variables with
       * properties from the Connection definition in openptk.xml.
       */

      this.initLdapBindPasswordParams();
      this.initLdapInMemoryServerParams();
      this.initLdapConnectionPoolParams();

      if (_useInMemoryServer)
      {
         this.createConnectionPoolToInMemoryServer();
      }
      else
      {
         this.initLdapConnectionParams();
         this.createConnectionPoolToLdapHost();
      }

      return;
   }

   //----------------------------------------------------------------
   @Override
   public synchronized void shutdown()
   //----------------------------------------------------------------
   {
      if (_connectionPool != null)
      {
         _connectionPool.close();
      }

      /*
       * If we are using an inMemoryServer, then shut it down gracefully
       */

      if (_inMemoryServer != null)
      {
         _inMemoryServer.shutDown(true);
      }

      super.shutdown();

      return;
   }

   /*
    ********************
    *  PROTECTED METHODS
    ********************
    */
   /**
    * Performs a create in the LDAP Directory.  Via the request, this will
    * create a new entry with the DN/key passed and the attributes/values
    * passed.
    * <br>
    * Upon success, the DN/key of the created entry will be returned along
    * with a success/error status.
    *
    * @param  request              OpenPTK RequestIF that holds the key (DN)
    *                              and attributes to be created
    * @param  response             OpenPTK ResponseIF that returns the created
    *                              key (RDN).
    * @throws OperationException   Upon any type of exception during the
    *                              doCreate, this exception will be thrown along
    *                              with a proper error message.
    */
   //----------------------------------------------------------------
   @Override
   protected void doCreate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doCreate() ";
      String key = null;
      String fwkey = null;
      String entryDN = null;
      String err = null;
      Collection<Attribute> attributes = null;
      AttrIF ptkAttr = null;
      LDAPResult ldapResult = null;
      ComponentIF subject = null;

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      response.setDescription(DESCRIPTION + ": doCreate");

      key = request.getKey();

      entryDN = this.getEntryDN(request);

      if (entryDN != null && entryDN.length() > 0)
      {
         Logger.logInfo(METHOD_NAME + "EntryDN: " + entryDN);

         attributes = this.getLdapAttributes(request);

         this.addObjectClass(attributes);

         try
         {
            ldapResult = _connectionPool.add(entryDN, attributes);
         }
         catch (LDAPException e)
         {
            err = this.logUnboundIDException(e);

            switch (e.getResultCode().intValue())
            {
               case ResultCode.ENTRY_ALREADY_EXISTS_INT_VALUE:
                  // Create - Already Exists = FAILED
               default:
                  response.setState(State.FAILED, err);
            }
         }

         if (!response.isError())
         {

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
             * an attribute name (must be unique).  read the attributes value
             * from the original input/request.  If the attributes value is not
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
      }
      else
      {
         response.setState(State.INVALID, "EntryDN is NULL");
      }

      if (!response.isError())
      {
         response.setState(State.SUCCESS, "Entry created");
      }

      return;
   }

   /**
    * Performs a read in the LDAP Directory.  The request should specify a
    * single key that will be used to do an equality filter to return a single
    * entry.  Additionally, a set of attributes being requested should
    * accompany the request.
    * <br>
    * Upon success, the entry will be returned along with attributes asked
    * for.
    *
    * @param  request              OpenPTK RequestIF that holds the key (DN)
    *                              of the entry to be returned along with the
    *                              list of attributes.
    * @param  response             OpenPTK ResponseIF that returns entry
    *                              requested.
    * @throws OperationException   Upon any type of exception during the
    *                              doCreate, this exception will be thrown along
    *                              with a proper error message.
    */
   //----------------------------------------------------------------
   @Override
   protected void doRead(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      doSearchAndRead(request, response);

      return;
   }

   /**
    * Performs a search in the LDAP Directory.  The request should specify
    * the LDAP query to be used to return a set of entries.
    * Additionally, a set of attributes being requested should
    * accompany the request.
    * <br>
    * Upon success, the entries will be returned along with attributes asked
    * for.
    *
    * @param  request              OpenPTK RequestIF that holds the query to
    *                              be run along with the
    *                              list of attributes.
    * @param  response             OpenPTK ResponseIF that returns the entries
    *                              requested.
    * @throws OperationException   Upon any type of exception during the
    *                              doCreate, this exception will be thrown along
    *                              with a proper error message.
    */
   //----------------------------------------------------------------
   @Override
   protected void doSearch(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      doSearchAndRead(request, response);

      return;
   }

   /**
    * Performs a modify in the LDAP Directory.  Via the request, this will
    * modify an existing entry based on the DN/key and the attributes/values
    * passed.
    * <br>
    * Upon success, a success/error status will be returned.
    *
    * @param  request              OpenPTK RequestIF that holds the key (DN)
    *                              and attributes to be modified
    * @param  response             OpenPTK ResponseIF that returns the status.
    * @throws OperationException   Upon any type of exception during the
    *                              doCreate, this exception will be thrown along
    *                              with a proper error message.
    */
   //----------------------------------------------------------------
   @Override
   protected void doUpdate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doUpdate() ";
      String err = null;
      String entryDN = null;
      Collection<Attribute> ldapAttrs = null;
      List<Modification> ldapMods = null;
      Modification mod = null;

      response.setDescription(DESCRIPTION + ": Update");

      entryDN = this.getEntryDN(request);

      if (entryDN != null && entryDN.length() > 0)
      {
         Logger.logInfo(METHOD_NAME + "EntryDN: " + entryDN);

         ldapAttrs = this.getLdapAttributes(request);

         ldapMods = new ArrayList<Modification>();

         for (Attribute ldapAttr : ldapAttrs)
         {
            mod = new Modification(ModificationType.REPLACE, ldapAttr.getName(), ldapAttr.getValues());

            ldapMods.add(mod);
         }

         try
         {
            _connectionPool.modify(entryDN, ldapMods);
         }
         catch (LDAPException e)
         {
            err = this.logUnboundIDException(e);

            switch (e.getResultCode().intValue())
            {
               case ResultCode.NO_SUCH_OBJECT_INT_VALUE:
                  response.setState(State.NOTEXIST, err);
                  break;
               default:
                  response.setState(State.FAILED, err);
            }
         }
      }
      else
      {
         response.setState(State.NOTEXIST, "EntryDN is NULL");
      }

      if (!response.isError())
      {
         response.setState(State.SUCCESS, "Entry Updated");
      }

      return;
   }

   /**
    * Performs a delete in the LDAP Directory.  Via the request, this will
    * delete an existing entry based on the DN/key.
    * <br>
    * Upon success, a success/error status will be returned.
    *
    * @param  request              OpenPTK RequestIF that holds the key (DN)
    *                              of the entry to be deleted.
    * @param  response             OpenPTK ResponseIF that returns the status.
    * @throws OperationException   Upon any type of exception during the
    *                              doCreate, this exception will be thrown along
    *                              with a proper error message.
    */
   //----------------------------------------------------------------
   @Override
   protected void doDelete(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doDelete() ";
      String err = null;
      String entryDN = null;

      response.setDescription(DESCRIPTION + ": Delete");

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
            _connectionPool.delete(entryDN);
         }
         catch (LDAPException e)
         {
            err = this.logUnboundIDException(e);

            switch (e.getResultCode().intValue())
            {
               case ResultCode.NO_SUCH_OBJECT_INT_VALUE:
                  response.setState(State.NOTEXIST, err);
                  break;
               default:
                  response.setState(State.ERROR, err);
            }
         }
      }

      if (!response.isError())
      {
         response.setState(State.SUCCESS, "Entry Deleted");
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
   protected void doPasswordChange(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      this.checkPasswordAttribute(request);
      this.doUpdate(request, response);

      response.setDescription(DESCRIPTION + ": Password Change");

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doPasswordReset(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      this.getPasswordAttribute(request);
      this.checkPasswordAttribute(request);
      this.doUpdate(request, response);
      this.getPasswordResult(response);

      response.setDescription(DESCRIPTION + ": Password Reset");

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doAuthenticate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doAuthenticate(): ";
      String err = null;
      String entryDN = null;
      String password = null;
      String pwdAttrName = null;
      AttrIF attr = null;
      LDAPConnection authConn = null;

      response.setDescription(DESCRIPTION + "Authenticate");

      entryDN = this.getEntryDN(request);

      if (entryDN != null && entryDN.length() > 0)
      {
         response.setUniqueId(entryDN);

         pwdAttrName = request.getSubject().getProperty(DefinitionIF.PROP_PASSWORD_ATTR_NAME);
         if (pwdAttrName != null && pwdAttrName.length() > 0)
         {
            attr = request.getSubject().getAttribute(pwdAttrName);
            if (attr != null)
            {
               password = attr.getValueAsString();
               if (password != null && password.length() > 0)
               {
                  try
                  {
                     if (_inMemoryServer != null)
                     {
                        authConn = _inMemoryServer.getConnection();
                     }
                     else
                     {
                        authConn = _serverSet.getConnection();
                     }

                     authConn.bind(entryDN, password);

                     /*
                      * Close out the temporary authentication connection before we leave.
                      */

                     authConn.close();
                  }
                  catch (LDAPException e)
                  {
                     err = this.logUnboundIDException(e);

                     switch (e.getResultCode().intValue())
                     {
                        default:
                           response.setState(State.NOTAUTHENTICATED, err);
                     }
                  }
               }
               else
               {
                  response.setState(State.INVALID, "The '" + pwdAttrName + "' attribute is empty");
               }
            }
            else
            {
               response.setState(State.INVALID, "The '" + pwdAttrName + "' attribute does not exist");
            }
         }
         else
         {
            err = "The Property for the Password Attribute is null: '"
               + DefinitionIF.PROP_PASSWORD_ATTR_NAME + "'";
            response.setState(State.ERROR, err);
            this.handleError(METHOD_NAME + err);
         }
      }
      else
      {
         response.setState(State.INVALID, "EntryDN is NULL");
      }

      if (!response.isError())
      {
         response.setState(State.AUTHENTICATED, "Authenticated: " + entryDN);
      }

      return;
   }

   /*
    ********************
    *  PRIVATE METHODS
    ********************
    */

   //----------------------------------------------------------------------
   private void createConnectionPoolToInMemoryServer()
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":createConnectionPoolToInMemoryServer() ";
      InMemoryDirectoryServerConfig dsConfig = null;
      String err = null;

      try
      {
         Logger.logInfo("LDIF File = " + _inMemoryLdif);

         /*
          * Create the confguration of the InMemory Server with the Root DSE, LDAP Bind
          * and Password
          */

         dsConfig = new InMemoryDirectoryServerConfig(_inMemoryRootDSE);
         dsConfig.addAdditionalBindCredentials(_ldapBindDN, _ldapPassword);

         _inMemoryServer = new InMemoryDirectoryServer(dsConfig);

         /*
          * Initialize the server with the LDIF file
          */

         _inMemoryServer.importFromLDIF(true, _inMemoryLdif);
         _inMemoryServer.startListening();

         _connectionPool = _inMemoryServer.getConnectionPool(_connectionPoolMax);


         if (this.getDebugLevel() != DebugLevel.NONE)
         {
            Logger.logInfo(METHOD_NAME + "InMemory Server:"
               + " (min/max conn): "
               + " 1/" + _connectionPoolMax);
         }
      }
      catch (LDAPException e)
      {
         err = "Failed attempt at starting InMemory Directory Server";

         Logger.logError(err);

         Logger.logError(e.getMessage());

         this.setState(State.FAILED);
         this.setStatus(err);
      }

      return;

   }

   //----------------------------------------------------------------------
   private void createConnectionPoolToLdapHost()
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":createConnectionPoolToLdapHost() ";
      int iTryCnt = 0;
      boolean bContinue = false;
      String err = null;

      do
      {
         bContinue = false;
         try
         {
            _connectionPool = new LDAPConnectionPool(
               _serverSet,
               _simpleBindRequest,
               _connectionPoolMin,
               _connectionPoolMax);

            if (this.getDebugLevel() != DebugLevel.NONE)
            {
               Logger.logInfo(METHOD_NAME + "ldapHostPort: " + _serverSet
                  + " (min/max conn): "
                  + _connectionPoolMin + "/" + _connectionPoolMax);
            }
         }
         catch (LDAPException e)
         {
            if (iTryCnt > RETRY_MAX)
            {
               err = METHOD_NAME
                  + "Failed to setup LDAP Connection after " + RETRY_MAX
                  + " attempts: " + e.getMessage();
               Logger.logError(err);
               this.setState(State.FAILED);
               this.setStatus(err);
            }
            else
            {
               iTryCnt++;
               bContinue = true;
            }
         }
      }
      while (bContinue);

      return;
   }

   /*
    * Setup the class varibles for the host/port based on the configuration
    * openptk.xml file.
    *
    * Example Configuration:
    *
    *
    *  <Connection id="UnboundID">
    *     <Properties>
    *        <Property name="connection.description"  value="UnboundID 2.1" />
    *        <Property name="url"                     value="ldap://host1:389,ldap://host2:389" />
    *        ....
    *     </Properties>
    *  </Connection>
    */
   private void initLdapConnectionParams()
   {
      String METHOD_NAME = CLASS_NAME + ":initLdapConnectionParams() ";
      String urlStr = null;
      String[] multipleURLs = null;
      String[] multipleProtocols = null;
      String[] multipleHosts = null;
      int[] multiplePorts = null;
      LDAPURL ldapURL = null;

      /*
       * Get the LDAP URL or Host and Port to connect to.  
       * 
       * If the URL is passed, then ignore the Host/Port.
       * 
       * If the URL has multiple URLs passed seperated by a comma, then create
       * a FailoverServerSet so that the URLs can be used to try the 2nd, 3rd, 
       * ... servers upon a failure of the initial URLs.
       * 
       * If only 1 URL is passed, or the host/port is 
       * not null, then create a SingleServerSet (1 single server) that will
       * be used to create the ConnectionPool
       */

      urlStr = this.getProperty(OperationsIF.PROP_URL);

      /*
       * Check to ensure that a url property is passed.
       */

      if (urlStr == null)
      {
         Logger.logError("UnboundID Property 'url' is required to successfully "
            + "connect to an LDAP service");
      }
      else
      {
         /*
          * There may be multiple URLs, separated by commas.  In this case
          * parse thgouth these and turn into String arrays of protocols, hosts
          * and ports.
          */
         multipleURLs = urlStr.split(",");

         try
         {
            if (multipleURLs.length > 0)
            {
               multipleProtocols = new String[multipleURLs.length];
               multipleHosts = new String[multipleURLs.length];
               multiplePorts = new int[multipleURLs.length];

               for (int i = 0; i < multipleURLs.length; i++)
               {
                  ldapURL = new LDAPURL(multipleURLs[i]);

                  multipleProtocols[i] = ldapURL.getScheme();
                  multipleHosts[i] = ldapURL.getHost();
                  multiplePorts[i] = ldapURL.getPort();
               }
            }
         }
         catch (LDAPException ex)
         {
            Logger.logError("Invalid LDAP URL specified: " + ex.getMessage());
         }
      }
      /*
       * Now, we should have a set of Protocols, Hosts and Ports.  If the set
       * is only a length of 1, then we will create a SingleServerSet.
       * Otherwise a FailoverServerSet will be used.
       */
      {
         if (multipleHosts == null || multipleHosts.length == 0)
         {
            Logger.logError("Invalid LDAP URL or Host/Port specified");
         }
         else if (multipleHosts.length == 1)
         {
            _serverSet = new SingleServerSet(multipleHosts[0], multiplePorts[0]);
         }
         else
         {
            _serverSet = new FailoverServerSet(multipleHosts, multiplePorts);
         }

      }

      return;
   }

   /*
    * Setup the class varibles for the host/port based on the configuration
    * openptk.xml file.
    *
    * Example Configuration:
    *
    *
    *  <Connection id="UnboundID">
    *     <Properties>
    *        <Property name="connection.description"  value="UnboundID 2.1" />
    *        ....
    *        <Property name="connection.pool.min"     value="4" />
    *        <Property name="connection.pool.max"     value="8" />
    *     </Properties>
    *  </Connection>
    */
   private void initLdapConnectionPoolParams()
   {
      String METHOD_NAME = CLASS_NAME + ":initLdapConnectionPoolParams() ";
      String intStr = null;

      /*
       * Get the maximim number of connections to setup in the LDAP Connection
       * pool.  This value is optional.
       */
      intStr = this.getProperty(PROP_CONNECTION_POOL_MAX);

      if (intStr != null)
      {
         _connectionPoolMax = Integer.valueOf(intStr);
      }

      /*
       * Get the minumum number of connections to setup in the LDAP Connection
       * pool.  This value is optional.
       */
      intStr = this.getProperty(PROP_CONNECTION_POOL_MIN);

      if (intStr != null)
      {
         _connectionPoolMin = Integer.valueOf(intStr);
      }

      /*
       * Ensure the min/max values are 1 or greater an the min value cannot be
       * greater than the max value.
       */
      _connectionPoolMax = Math.max(_connectionPoolMax, 1);
      _connectionPoolMin = Math.max(_connectionPoolMin, 1);
      _connectionPoolMin = Math.min(_connectionPoolMin, _connectionPoolMax);

      return;
   }

   /*
    * Setup the class varibles for the In Memory Dir based on the configuration
    * openptk.xml file.
    *
    * Example Configuration:
    *
    *
    *  <Connection id="UnboundID-InMemory">
    *     <Properties>
    *        <Property name="connection.description"  value="UnboundID 2.1" />
    *        <Property name="unboundid.inmemory"      value="true" />
    *        <Property name="unboundid.inmemory.rootdse" value="dc=openptk,dc=org" />
    *        <Property name="unboundid.inmemory.ldif" value="OpenPTK.ldif" />
    *        ....
    *     </Properties>
    *  </Connection>
    */
   private void initLdapInMemoryServerParams()
   {
      String METHOD_NAME = CLASS_NAME + ":initLdapInMemoryServerParams() ";
      String bStr = null;

      /*
       * Find out if the configuration specifies the use of the UnboundID
       * InMemory directory server
       */

      bStr = this.getProperty(this.PROP_UNBOUNDID_INMEMORY);

      if (bStr != null)
      {
         _useInMemoryServer = Boolean.parseBoolean(bStr);
      }

      if (_useInMemoryServer)
      {
         _inMemoryLdif = this.getProperty(this.PROP_UNBOUNDID_INMEMORY_LDIF);

         if (_inMemoryLdif == null)
         {
            _useInMemoryServer = false;

            Logger.logError("UnboundID Directory Server enabled.  Need to specify an LDIF file");
         }


         _inMemoryRootDSE = this.getProperty(this.PROP_UNBOUNDID_INMEMORY_ROOTDSE);

         if (_inMemoryRootDSE == null)
         {
            _useInMemoryServer = false;

            Logger.logError("UnboundID Directory Server enabled.  Need to specify an Root DSE");
         }
      }

      return;
   }

   /*
    * Setup the class varibles for the bind/password based on the configuration
    * openptk.xml file.
    *
    * Example Configuration:
    *
    *
    * <Connection id="UnboundID">
    *    <Properties>
    *       <Property name="connection.description"  value="UnboundID 2.1" />
    *       <Property name="user.name"               value="%{jndi.user.name}" />
    *       <Property name="user.password.encrypted" value="%{jndi.user.password.encrypted}" />
    *       ....
    *    </Properties>
    * </Connection>
    */
   private void initLdapBindPasswordParams()
   {
      String METHOD_NAME = CLASS_NAME + ":initLdapBindPasswordParams() ";

      /*
       * Get the User/Password used to bind to the directory server
       */

      _ldapBindDN = this.getValue(OperationsIF.PROP_USER_NAME);
      _ldapPassword = this.getValue(OperationsIF.PROP_USER_PASSWORD);

      if ((_ldapBindDN != null && _ldapBindDN.length() > 0)
         && (_ldapPassword != null && _ldapPassword.length() > 0))
      {
         // All is good
      }
      else
      {
         Logger.logError("LDAP BindDN " + _ldapBindDN + " and/or Password " + _ldapPassword + " not passed.  Using default");

         _ldapBindDN = DEFAULT_BINDDN;
         _ldapPassword = DEFAULT_PASSWORD;
      }

      /*
       * Create the simple bind request based on the userid/password
       */
      this._simpleBindRequest = new SimpleBindRequest(_ldapBindDN, _ldapPassword);

      return;
   }

   //----------------------------------------------------------------------
   private Attribute getLdapAttribute(AttrIF ptkAttr, Operation operation) throws OperationException
   //----------------------------------------------------------------------
   {
      String name = null;
      Attribute attr = null;

      boolean bMultivalued = false;
      String METHOD_NAME = CLASS_NAME + ":getLdapAttribute(): ";
      Object value = null;
      String[] values = null;
      String[] strValues = null;
      Object[] objValues = null;
      Long[] lValues = null;
      Integer[] iValues = null;
      Boolean[] bValues = null;
      DataType type = null;
      Set<String> valueSet = null;

      /*
       * If the ptkAttr is null, then return a null Attribute
       */

      if (ptkAttr == null)
      {
         return attr;
      }

      name = ptkAttr.getServiceName();
      if ((operation == Operation.CREATE && ptkAttr.getValue() != null)
         || Operation.UPDATE.equals(operation)
         || Operation.PWDCHANGE.equals(operation)
         || Operation.PWDRESET.equals(operation))
      {

         value = ptkAttr.getValue();
         type = ptkAttr.getType();
         bMultivalued = ptkAttr.isMultivalued();

         valueSet = new HashSet<String>();

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

                  values = new String[bValues.length];

                  for (int i = 0; i < bValues.length; i++)
                  {
                     values[i] = bValues[i].toString();
                  }
                  valueSet.addAll(Arrays.asList(values));
               }
               else
               {
                  valueSet.add(value.toString()); //must case to Boolean
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

                  values = new String[iValues.length];

                  for (int i = 0; i < iValues.length; i++)
                  {
                     values[i] = iValues[i].toString();
                  }
                  valueSet.addAll(Arrays.asList(values));
               }
               else
               {
                  valueSet.add(value.toString()); // must cast to Integer
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

                  values = new String[lValues.length];

                  for (int i = 0; i < lValues.length; i++)
                  {
                     values[i] = lValues[i].toString();
                  }
                  valueSet.addAll(Arrays.asList(values));
               }
               else
               {
                  valueSet.add(value.toString()); // must cast to Long
               }
               break;
            case STRING:
               if (value != null)
               {
                  if (bMultivalued)
                  {
                     strValues = (String[]) value;
                     valueSet.addAll(Arrays.asList(strValues));
                  }
                  else
                  {
                     if (((String) value).length() > 0)
                     {
                        valueSet.add((String) value); // must cast to String
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

                     values = new String[objValues.length];

                     for (int i = 0; i < objValues.length; i++)
                     {
                        values[i] = objValues[i].toString();
                     }
                     valueSet.addAll(Arrays.asList(values));
                  }
                  else
                  {
                     valueSet.add(value.toString());
                  }
               }
               break;
         }

         attr = new Attribute(name, valueSet);

      }

      return (attr);
   }

   //----------------------------------------------------------------------
   private String getEntryDN(final RequestIF request) throws OperationException
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getEntryDN() ";
      Operation operation = null;
      String entryDN = null;
      String err = null;
      String searchStr = null;
      String baseDN = null;
      SearchResultEntry searchResultEntry = null;
      SearchRequest searchRequest = null;
      operation = request.getOperation();

      if (operation == Operation.CREATE)
      {
         entryDN = this.getMapData(_mapDN, request);
      }
      else
      {
         baseDN = this.getBaseDN(request);
         searchStr = this.getUniqueSearchStr(request);

         if (searchStr != null && searchStr.length() > 0)
         {
            try
            {
               searchRequest = new SearchRequest(baseDN, SearchScope.SUB, searchStr, "dn");

               searchResultEntry = _connectionPool.searchForEntry(searchRequest);

               if (searchResultEntry != null)
               {
                  entryDN = searchResultEntry.getDN();
               }
            }
            catch (LDAPException e)
            {
               err = this.logUnboundIDException(e);

               entryDN = null;
            }
         }
      }
      return entryDN;
   }

   //----------------------------------------------------------------------
   private AttrIF getPtkAttr(AttrIF requestAttr, Attribute ldapAttr)
   //----------------------------------------------------------------------
   {
      int size = 0;
      String fwName = null;
      AttrIF ptkAttr = null;     // OpenPTK
      DataType type = null; // OpenPTK;

      if (requestAttr != null && ldapAttr != null)
      {
         fwName = requestAttr.getName();
         type = requestAttr.getType();
         size = ldapAttr.size();

         switch (size)
         {
            case 0:
               ptkAttr = new BasicAttr(fwName);
               break;
            case 1:   // single-value
               ptkAttr = new BasicAttr(fwName, ldapAttr.getValue());
               break;
            default:  // multi-value
               ptkAttr = new BasicAttr(fwName, ldapAttr.getValues());
         }

         ptkAttr.setType(type);
      }

      return ptkAttr;
   }

   //----------------------------------------------------------------------
   private List<Attribute> getLdapAttributes(final RequestIF request) throws OperationException
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getLdapAttributes(): ";
      List<Attribute> ldapAttrs = null;
      Attribute ldapAttr = null;             // com.unboundid.ldap.sdk
      Map<String, AttrIF> ptkAttrMap = null; // OpenPTK
      Collection<AttrIF> ptkAttrs = null;

      /*
       * Represents the attributes that are inbound on the request.
       *
       * For example, on a CREATE, this will conver the list of inbound
       * PTK Attributes over to LDAP Attribues that can be used for the
       * actual LDAP operations
       */

      ptkAttrMap = request.getSubject().getAttributes();

      if (ptkAttrMap != null)
      {
         ptkAttrs = ptkAttrMap.values();

         if (ptkAttrs != null)
         {
            /*
             * Create a list to hold the LDAP Attributes
             */

            ldapAttrs = new ArrayList<Attribute>();

            for (AttrIF ptkAttr : ptkAttrs)
            {
               ldapAttr = this.getLdapAttribute(ptkAttr, request.getOperation());

               if (ldapAttr != null)
               {
                  ldapAttrs.add(ldapAttr);
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
   private List<ComponentIF> getPtkResults(final RequestIF request, final SearchResult searchResult) throws OperationException
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getPtkResults(): ";
      List<ComponentIF> ptkResultsList = null;  // OpenPTK
      ComponentIF ptkResult = null;          // OpenPTK
      List<SearchResultEntry> entries = null;

      ptkResultsList = new LinkedList<ComponentIF>();

      if (searchResult != null)
      {
         entries = searchResult.getSearchEntries();

         for (SearchResultEntry entry : entries)
         {
            ptkResult = this.getPtkResult(request, entry);
            ptkResultsList.add(ptkResult);
         }
      }
      else
      {
         ptkResult = new Component();
         ptkResult.setState(State.NULL);
         ptkResult.setStatus(METHOD_NAME + "Results are NULL: ");

         ptkResultsList.add(ptkResult);
      }

      return ptkResultsList;
   }

   //----------------------------------------------------------------------
   private Component getPtkResult(final RequestIF request, final SearchResultEntry entry)
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getPtkComponent(): ";
      String key = null;
      String fwName = null;
      String srvcName = null;
      Component ptkResult = null;  // OpenPTK
      AttrIF requestAttr = null;   // OpenPTK
      AttrIF responseAttr = null;  // OpenPTK
      Collection<Attribute> ldapAttrs = null;
      boolean hasUniqueId = false; // Use thit to check for uniqueID because
      // Component sets a default uniqueID

      ptkResult = new Component();
      ptkResult.setDebug(this.isDebug());
      ptkResult.setDebugLevel(this.getDebugLevel());

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
            ptkResult.setUniqueId(entry.getDN());
            hasUniqueId = true;
         }


         /*
          * Check to see if the entrydn is requested (as an attribute)
          * if so, return it as a PTK Attribute
          */

         if (request.getSubject().getAttributesNames().contains(PROP_ENTRYDN))
         {
            fwName = request.getService().getFwName(request.getOperation(), PROP_ENTRYDN);
            requestAttr = new BasicAttr(PROP_ENTRYDN, entry.getDN());
            ptkResult.setAttribute(fwName, requestAttr);
         }

         /*
          * Enumerate through all the attribute NAMES (ids)
          */

         ldapAttrs = entry.getAttributes();

         if (ldapAttrs != null)
         {
            for (Attribute ldapAttr : ldapAttrs)
            {
               /*
                *  Enumerate through all the VALUES of an attribute
                */

               requestAttr = null;
               responseAttr = null;
               fwName = null;
               srvcName = null;
               srvcName = ldapAttr.getName();
               if (srvcName != null)
               {
                  fwName = request.getService().getFwName(request.getOperation(), srvcName);

                  /*
                   * An attribute (non-entrydn) may be used as the
                   * OpenPTK uniqueid, defined as the "key"
                   * If the ptkComp does not have a uniqueId set, check
                   * all of the attributes to determine which one is
                   * the "key"
                   */

                  if (!hasUniqueId && srvcName.equals(key))
                  {
                     ptkResult.setUniqueId(ldapAttr.getValue());
                     hasUniqueId = true;
                  }

                  requestAttr = request.getSubject().getAttribute(fwName);

                  responseAttr = this.getPtkAttr(requestAttr, ldapAttr);

                  if (responseAttr != null)
                  {
                     responseAttr.setServiceName(srvcName);
                     ptkResult.setAttribute(fwName, responseAttr);
                  }
               }
            }
         }
         else
         {
            ptkResult.setStatus("No Attributes");
         }
      }

      if (!hasUniqueId)
      {
         ptkResult.setState(State.ERROR);
         ptkResult.setStatus(METHOD_NAME + "UniqueId has not be set, key='" + key + "'");
      }

      return ptkResult;
   }

   //----------------------------------------------------------------------
   private void addObjectClass(Collection<Attribute> attrs)
   //----------------------------------------------------------------------
   {
      String objectClass = null;
      Attribute attr = null;
      String[] objectClassNames = null;

      objectClass = this.getProperty(PROP_OBJECTCLASS);

      if (objectClass == null)
      {
         objectClass = DEFAULT_OBJECTCLASS;
      }

      objectClassNames = objectClass.split(",");

      if (objectClassNames != null)
      {
         attr = new Attribute("objectClass", objectClassNames);

         attrs.add(attr);
      }

      return;
   }

   //----------------------------------------------------------------
   private void doSearchAndRead(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doSearch() ";
      String err = null;
      List<ComponentIF> ptkResultsList = null;  // OpenPTK
      SearchResult searchResult = null;
      SearchRequest searchRequest = null;

      response.setDescription(DESCRIPTION + ": Search");

      searchRequest = buildSearchRequest(request, response);

      /*
       * If we had an error during the SearchReqeust build, then simply return
       */

      if (response.isError())
      {
         return;
      }

      if (searchRequest != null)
      {
         try
         {
            /*
             * Make the actual search request to the server
             */

            searchResult = _connectionPool.search(searchRequest);
         }
         catch (LDAPSearchException e)
         {
            err = this.logUnboundIDException(e);

            response.setState(State.ERROR, err);
         }
      }
      else
      {
         response.setState(State.INVALID, "Search value is not set");
      }

      /*
       * If there are no errors, then get the ptkRresults from the search.
       * This will convert all the LDAP Entries/Attributes to a list of PTK
       * Components.
       */

      if (!response.isError())
      {
         ptkResultsList = this.getPtkResults(request, searchResult);

         switch (request.getOperation())
         {
            case READ:
               if (ptkResultsList.size() == 1)
               {
                  response.setResults(ptkResultsList);
                  response.setState(State.SUCCESS, "Entry Found");
               }
               else if (ptkResultsList.isEmpty())
               {
                  response.setState(State.NOTEXIST, "Entry does not exist");
               }
               else
               {
                  response.setState(State.FAILED, "Invalid results, more than one entry was found");
               }
               break;
            case SEARCH:

               if (ptkResultsList.isEmpty())
               {
                  response.setState(State.SUCCESS, "Nothing was found");
               }
               else
               {
                  response.setResults(ptkResultsList);
                  response.setState(State.SUCCESS, "Entries found: " + ptkResultsList.size());
               }
               break;
         }
      }

      return;
   }

   //----------------------------------------------------------------------
   private SearchRequest buildSearchRequest(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":buildSearchRequest()";
      SearchRequest searchRequest = null;
      String searchStr = null;
      String baseDN = null;
      String[] returnAttrs = null;
      List<String> attrNames = null;
      Object keyValue = null;
      String serviceKey = null;

      switch (request.getOperation())
      {
         case READ:
            /*
             * If we are building a search reqeuest for a PTK READ operation
             * then we are trying to find a single unique record from LDAP
             * so, this will create a UniqueSearchStr (i.e. uid=jsmith...)
             */

            keyValue = request.getSubject().getUniqueId();

            if (keyValue != null && keyValue.toString().length() > 0)
            {
               serviceKey = request.getKey();

               if (serviceKey != null && serviceKey.length() > 0)
               {
                  searchStr = this.getUniqueSearchStr(request);

                  if (searchStr == null || searchStr.length() == 0)
                  {
                     response.setState(State.ERROR, "Unique Search string is not found");
                  }
               }
               else
               {
                  response.setState(State.INVALID, "UniqueId attribute name is not set");
               }
            }
            else
            {
               response.setState(State.INVALID, "UniqueId value is not set");
            }
            break;
         case SEARCH:
            searchStr = this.getLdapSearch(request);
            break;
         default:
      }

      /*
       * If the response is in an error state, then we have an issue and will
       * return a null
       */
      if (response.isError())
      {
         return null;
      }

      baseDN = this.getBaseDN(request);

      /*
       * Get a list of all the attibutes to return for the LDAP Search
       */

      attrNames = request.getSubject().getAttributesNames();
      attrNames.add(request.getKey());  // explicitly add the uniqueId

      // TODO: We may have to look at this in the case that no attributes are
      //       requested.  In this case, all attriutes should be returned
      returnAttrs = this.getSrchCtrlAttrs(request, attrNames);

      try
      {
         searchRequest = new SearchRequest(baseDN, SearchScope.SUB, searchStr, returnAttrs);
      }
      catch (LDAPException ex)
      {
         Logger.logError(ex.getDiagnosticMessage());
         response.setState(State.ERROR, "Unable to build a valid LDAP Search");
      }

      if (this.isDebug())
      {
         Logger.logInfo(searchRequest.toString());
      }

      return searchRequest;
   }

   //----------------------------------------------------------------------
   private String logUnboundIDException(LDAPException e)
   //----------------------------------------------------------------------
   {
      String err = "LDAP_CODE (" + e.getResultCode().intValue() + ") : "
         + e.getDiagnosticMessage();
      Logger.logInfo(err);

      return err;
   }
}
