/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010-2011 Project OpenPTK
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openspml.v2.msg.pass.ResetPasswordRequest;
import org.openspml.v2.msg.pass.SetPasswordRequest;
import org.openspml.v2.msg.spml.AddRequest;
import org.openspml.v2.msg.spml.DeleteRequest;
import org.openspml.v2.msg.spml.ExecutionMode;
import org.openspml.v2.msg.spml.LookupRequest;
import org.openspml.v2.msg.spml.LookupResponse;
import org.openspml.v2.msg.spmlsearch.SearchResponse;
import org.openspml.v2.msg.spml.ModifyRequest;
import org.openspml.v2.msg.spml.PSO;
import org.openspml.v2.msg.spml.PSOIdentifier;
import org.openspml.v2.msg.spml.ReturnData;
import org.openspml.v2.msg.spmlsearch.Scope;
import org.openspml.v2.msg.spmlsearch.SearchRequest;

import org.openptk.api.Query;
import org.openptk.api.State;
import org.openptk.common.AttrIF;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.Request;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.exception.OperationException;

/**
 * This class extends the Spml2Operations to support the
 * Oracle Identity Manager SPML2 provisioning interface.
 *
 * @author Scott Fehrman
 */
//===================================================================
public class Spml2OIMOperations extends Spml2Operations
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String DESCRIPTION = "Oracle Identity Manager (SPML) 2.0";
   private static final String ATTR_NAME_STATUS = "status";
   private static final String STATUS_DELETED = "Deleted";

   //----------------------------------------------------------------
   public Spml2OIMOperations()
   //----------------------------------------------------------------
   {
      super();

      this.setDescription(Spml2OIMOperations.DESCRIPTION);
      Spml2OIMOperations.RESPONSE_DESC = CLASS_NAME + ", Provision Response";

      return;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   /**
    * @param request
    * @param createRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void preCreate(final RequestIF request, final AddRequest createRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String uniqueId = null;

      /*
       * make sure that uniqueId (Users.User Id) does not already exist.
       * If it does exist AND it is DELETED, append a number to it.
       * Then test again.
       */

      uniqueId = this.getUniqueIdFromRequest(request);
      request.getSubject().setUniqueId(uniqueId);

      super.preCreate(request, createRequest);

      return;
   }

   /**
    * @param request
    * @param readRequest
    * @return
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void preRead(final RequestIF request, final LookupRequest readRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String psoValue = null;
      PSOIdentifier psoId = null; // SPML2

      psoValue = this.getPsoIdFromRequest(request);

      psoId = new PSOIdentifier(psoValue, null, null);
      readRequest.setPsoID(psoId);
      readRequest.setExecutionMode(ExecutionMode.SYNCHRONOUS);
      readRequest.setReturnData(ReturnData.EVERYTHING);

      return;
   }

   /**
    * @param response
    * @param readResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void postRead(final ResponseIF response, final LookupResponse readResponse) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":postRead(): ";
      String value = null;
      ComponentIF subject = null;
      AttrIF attr = null;

      super.postRead(response, readResponse);

      if (response.getState() == State.SUCCESS)
      {
         /*
          * Check for the subject / entry status of "Deleted"
          */

         subject = response.getResults().get(0);
         if (subject != null)
         {
            attr = subject.getAttribute(ATTR_NAME_STATUS);
            if (attr != null)
            {
               value = attr.getValueAsString();
               if (value.equalsIgnoreCase(STATUS_DELETED))
               {
                  /*
                   * replace the results with an empty list
                   */
                  response.setResults(new ArrayList<ComponentIF>());
                  response.setError(true);
                  response.setState(State.ERROR);
                  response.setStatus("Does not exist (Deleted)");
                  this.handleError(METHOD_NAME + "Does not exist (Deleted)");
               }
            }
         }
      }

      return;
   }

   /**
    * @param request
    * @param modifyRequest
    * @return
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void preUpdate(final RequestIF request, final ModifyRequest modifyRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String psoValue = null;
      PSOIdentifier psoId = null; // SPML2

      super.preUpdate(request, modifyRequest);

      psoValue = this.getPsoIdFromRequest(request);

      psoId = new PSOIdentifier(psoValue, null, null);

      modifyRequest.setPsoID(psoId);

      return;
   }

   /**
    * @param request
    * @param deleteRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void preDelete(final RequestIF request, final DeleteRequest deleteRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String psoValue = null;
      PSOIdentifier psoId = null; // SPML2

      super.preDelete(request, deleteRequest);

      psoValue = this.getPsoIdFromRequest(request);

      psoId = new PSOIdentifier(psoValue, null, null);

      deleteRequest.setPsoID(psoId);

      return;
   }

   /**
    * @param request
    * @param searchRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void preSearch(final RequestIF request, final SearchRequest searchRequest) throws OperationException
   //----------------------------------------------------------------
   {
      PSOIdentifier psoId = null; // SPML2

      super.preSearch(request, searchRequest);

      psoId = new PSOIdentifier("", null, null);

      searchRequest.getQuery().setBasePsoID(psoId); // set to "" in OIM SPML2 examples
      searchRequest.getQuery().setScope(Scope.PSO); // Scope.PSO is used in OIM SPML2 examples

      return;
   }

   /**
    * @param response
    * @param searchResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void postSearch(final ResponseIF response, final SearchResponse searchResponse) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":postRead(): ";
      String value = null;
      List<ComponentIF> orgList = null;
      List<ComponentIF> newList = null;
      Iterator<ComponentIF> iter = null;
      ComponentIF subject = null;
      AttrIF attr = null;

      super.postSearch(response, searchResponse);

      if (response.getState() == State.SUCCESS)
      {
         /*
          * Check for the subject / entry status of "Deleted", on each result
          */

         newList = new ArrayList<ComponentIF>();

         orgList = response.getResults();
         if (orgList != null && !orgList.isEmpty())
         {
            iter = orgList.iterator();

            while (iter.hasNext())
            {
               subject = iter.next();
               if (subject != null)
               {
                  attr = subject.getAttribute(ATTR_NAME_STATUS);
                  if (attr != null)
                  {
                     value = attr.getValueAsString();
                     if (!value.equalsIgnoreCase(STATUS_DELETED))
                     {
                        /*
                         * add the subject to the new list
                         */

                        newList.add(subject);
                     }
                  }
               }
            }
         }
         response.setResults(newList);
         response.setStatus(newList.size() + " entries returned");
      }

      return;
   }

   /**
    * @param request
    * @param setPwdRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void prePasswordChange(final RequestIF request, final SetPasswordRequest setPwdRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String psoValue = null;
      PSOIdentifier psoId = null; // SPML2

      super.prePasswordChange(request, setPwdRequest);

      psoValue = this.getPsoIdFromRequest(request);

      psoId = new PSOIdentifier(psoValue, null, null);

      setPwdRequest.setPsoID(psoId);

      return;
   }

   /**
    * @param request
    * @param resetPwdRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void prePasswordReset(final RequestIF request, final ResetPasswordRequest resetPwdRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":prePasswordReset(): ";
      String psoValue = null;
      PSOIdentifier psoId = null; // SPML2

      super.prePasswordReset(request, resetPwdRequest);

      psoValue = this.getPsoIdFromRequest(request);

      psoId = new PSOIdentifier(psoValue, null, null);

      resetPwdRequest.setPsoID(psoId);

      return;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private String getUniqueIdFromRequest(final RequestIF request) throws OperationException
   //----------------------------------------------------------------
   {
      /*
       * Try to search for the entry using the "requested" uniqueid.
       * If it exists ... append a counter and try again.
       * Keep trying until a uniqueid is found.
       */

      int iCnt = 1;
      boolean isUnique = false;
      String METHOD_NAME = CLASS_NAME + ":getUniqueIdFromRequest(): ";
      String srvcKey = null;
      String uniqueId = null;
      String baseId = null;
      String err = null;
      SearchRequest searchRequest = null;   // SPML2
      SearchResponse searchResponse = null; // SPML2
      PSOIdentifier psoId = null;           // SPML2
      PSO psoArray[] = null;                // SPML2
      RequestIF ptkReq = null;              // OpenPTK
      Query query = null;                   // OpenPTK
      Component subject = null;             // OpenPTK

      srvcKey = request.getKey();
      baseId = request.getSubject().getUniqueId().toString();

      ptkReq = new Request();
      ptkReq.setSubject(request.getSubject());
      ptkReq.setOperation(Operation.SEARCH);
      ptkReq.setKey(request.getKey());
      ptkReq.setService(request.getService());

      searchRequest = new SearchRequest();
      searchRequest.setExecutionMode(ExecutionMode.SYNCHRONOUS);
      searchRequest.setReturnData(ReturnData.EVERYTHING);

      psoId = new PSOIdentifier("", null, null);

      uniqueId = baseId;

      while (!isUnique)
      {
         query = new Query(Query.Type.EQ, srvcKey, uniqueId);
         this.updateQueryServiceName(query);
         ptkReq.setQuery(query);

         this.addSearchRequestFilter(searchRequest, ptkReq);

         searchRequest.getQuery().setBasePsoID(psoId); // set to "" in OIM SPML2 examples
         searchRequest.getQuery().setScope(Scope.PSO); // Scope.PSO is used in OIM SPML2 examples

         try
         {
            searchResponse = (SearchResponse) _facade.send(searchRequest);
            _facade.throwErrors(searchResponse);
         }
         catch (Exception ex)
         {
            request.setError(true);
            request.setState(State.FAILED);
            request.setStatus(ex.getMessage());
            this.handleError(METHOD_NAME + ex.getMessage());
         }

         psoArray = searchResponse.getPSOs();
         if (psoArray.length == 0)
         {
            /*
             * No entry ... return "current" uniqueId
             */

            isUnique = true;
         }
         else
         {
            /*
             * Found an entry ... need to check it's status ...
             * if status equals "Deleted":
             * then increment and try again.
             * else throw error ... record already exists.
             */

            subject = this.getComponent(ptkReq, psoArray[0]);

            if (this.isDeleted(subject))
            {
               uniqueId = baseId + iCnt++;
            }
            else
            {
               err = "Entry '" + uniqueId + "' already exists.";
               request.setError(true);
               request.setState(State.FAILED);
               request.setStatus(err);
               this.handleError(METHOD_NAME + err);
            }
         }
      }

      return uniqueId;
   }

   //----------------------------------------------------------------
   private String getPsoIdFromRequest(final RequestIF request) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getPsoIdFromRequest(): ";
      String ret = null;
      String psoValue = null;
      String uniqueId = null;
      String srvcKey = null;
      String err = null;
      SearchRequest searchRequest = null;   // SPML2
      SearchResponse searchResponse = null; // SPML2
      PSO psoArray[] = null;                // SPML2
      PSO pso = null;                       // SPML2
      PSOIdentifier psoId = null;           // SPML2
      ComponentIF reqSub = null;            // OpenPTK
      ComponentIF resSub = null;            // OpenPTK
      RequestIF ptkReq = null;              // OpenPTK
      Query query = null;                   // OpenPTK

      /*
       * The "pso Id" must be set to match the unique row in a table.
       * Need to perform a "search" using the PTK uniqueid (maps to OIM "Users.User ID")
       * The search should ONLY return a single "entry".  The entries
       * "pso Id" (maps to OIM "Users.Key") is the row number.
       * Set the readRequest pso Id equals to "<objectclass>:<psoId>"
       */

      reqSub = request.getSubject();

      if (reqSub == null)
      {
         err = "Subject is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      uniqueId = reqSub.getUniqueId().toString();
      if (uniqueId == null || uniqueId.length() < 1)
      {
         err = "Subject uniqueid is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      srvcKey = request.getKey();
      if (srvcKey == null || srvcKey.length() < 1)
      {
         err = "Service key is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      /*
       * Make a PTK Request for searching
       */

      query = new Query(Query.Type.EQ, srvcKey, uniqueId);
      this.updateQueryServiceName(query);

      ptkReq = new Request();
      ptkReq.setSubject(reqSub);
      ptkReq.setOperation(Operation.SEARCH);
      ptkReq.setKey(request.getKey());
      ptkReq.setService(request.getService());
      ptkReq.setQuery(query);

      /*
       * Make a SPML2 SearchRequest
       */

      searchRequest = new SearchRequest();
      searchRequest.setExecutionMode(ExecutionMode.SYNCHRONOUS);
      searchRequest.setReturnData(ReturnData.EVERYTHING);

      this.addSearchRequestFilter(searchRequest, ptkReq);

      psoId = new PSOIdentifier("", null, null);
      searchRequest.getQuery().setBasePsoID(psoId); // set to "" in OIM SPML2 examples
      searchRequest.getQuery().setScope(Scope.PSO); // Scope.PSO is used in OIM SPML2 examples

      /*
       * Execute the SPML2 request
       */

      try
      {
         searchResponse = (SearchResponse) _facade.send(searchRequest);
         _facade.throwErrors(searchResponse);
      }
      catch (Exception ex)
      {
         err = ex.getMessage();
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      if (searchResponse == null)
      {
         err = "SearchResponse is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      if (!searchResponse.isValid())
      {
         err = "SearchResponse is NOT VALID";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + "SearchResponse is NOT VALID");
      }

      /*
       * There should be one (and only one) result, from the search
       */

      psoArray = searchResponse.getPSOs();
      if (psoArray == null || psoArray.length == 0)
      {
         err = "SearchResponse is empty (PSOs==0)";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }
      if (psoArray.length > 1)
      {
         err = "SearchResponse has more than one result, " + "length=" + psoArray.length;
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      /*
       * Check to see if it has been "deleted"
       * create a OpenPTK Component from the SPML2 response
       * call "isDeleted" which will check if it has been deleted.
       */

      pso = psoArray[0];

      resSub = this.getComponent(ptkReq, pso);

      if (this.isDeleted(resSub))
      {
         err = "Entry '" + uniqueId + "' does not exist.";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      psoValue = pso.getPsoID().getID();
      if (psoValue == null || psoValue.length() < 1)
      {
         err = "PSO Id is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      ret = psoValue;

      return ret;
   }

   //----------------------------------------------------------------
   private boolean isDeleted(ComponentIF subject) throws OperationException
   //----------------------------------------------------------------
   {
      boolean deleted = false;
      String METHOD_NAME = CLASS_NAME + ":isDeleted(): ";
      String err = null;
      AttrIF attr = null;                   // OpenPTK

      /*
       * Get the subject's attrbute that contains the "status"
       * if the status indicates that the entry is "deleted"
       * then return "true", else "false" is returned (default).
       */

      if (subject != null)
      {
         attr = subject.getAttribute(ATTR_NAME_STATUS);
         if (attr != null)
         {
            if (attr.getValueAsString().equalsIgnoreCase(STATUS_DELETED))
            {
               deleted = true;
            }
         }
         else
         {
            err = "Attribute '" + ATTR_NAME_STATUS + "' is null";
         }
      }
      else
      {
         err = "Subject is null";
      }

      if (err != null)
      {
         this.handleError(METHOD_NAME + err);
      }

      return deleted;
   }
}
