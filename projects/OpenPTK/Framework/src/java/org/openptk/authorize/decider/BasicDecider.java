/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010-2010 Sun Microsystems, Inc.
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
package org.openptk.authorize.decider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openptk.api.Opcode;
import org.openptk.api.State;
import org.openptk.authenticate.PrincipalIF;
import org.openptk.authorize.Effect;
import org.openptk.authorize.TargetIF;
import org.openptk.authorize.policy.PolicyIF;
import org.openptk.authorize.policy.PolicyMode;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.debug.DebugIF;
import org.openptk.logging.Logger;
import org.openptk.exception.AuthorizationException;
import org.openptk.session.SessionIF;
import org.openptk.session.SessionType;
import org.openptk.util.StringUtil;

/**
 *
 * @author Derrick Harcey
 */
//===================================================================
public class BasicDecider extends Decider
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String SESSION_PRINCIPAL_CONTEXTID = "${session.principal.contextid}";
   private static final String SESSION_PRINCIPAL_UNIQUEID = "${session.principal.uniqueid}";
   private static final String SESSION_CLIENTID = "${session.clientid}";
   private final Map<String, PolicyIF> _policies = new HashMap<String, PolicyIF>();

   /**
    * Default constructor
    */
   //----------------------------------------------------------------
   public BasicDecider()
   //----------------------------------------------------------------
   {
      super();
      this.setDescription("Basic Decider");
      return;
   }

   //----------------------------------------------------------------
   public BasicDecider(DeciderIF decider)
   //----------------------------------------------------------------
   {
      super(decider);
      return;
   }

   @Override
   public DeciderIF copy()
   {
      return new BasicDecider(this);
   }

   /**
    * Add the Policy to the DeciderIF using the policyId
    * @param policyId
    * @param policy
    * @throws AuthorizationException
    */
   //----------------------------------------------------------------
   public final void addPolicy(final String policyId, final PolicyIF policy) throws AuthorizationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":addPolicy(): ";

      if (policyId == null || policyId.length() < 1)
      {
         this.handleError(METHOD_NAME + "policyId is null.");
      }

      if (policy == null)
      {
         this.handleError(METHOD_NAME + "policy is null.");
      }

      _policies.put(policyId, policy);

      return;
   }

   /**
    * Check to requested operation to determine if is is allowed.
    * @param SessionIF, TargetIF
    * @returns ComponentIF
    */
   //----------------------------------------------------------------
   @Override
   public synchronized ComponentIF check(final SessionIF session,
      final TargetIF target) throws AuthorizationException
   //----------------------------------------------------------------
   {
      boolean explicitAllow = false;
      boolean explicitDeny = false;
      String METHOD_NAME = CLASS_NAME + ":check(): ";
      String request = null;
      String[] requestParts = null;
      String[] policyNames = null;
      ComponentIF responseCheck = null;
      ComponentIF responsePolicy = null;
      PolicyIF policy = null;
      Opcode opcode = null;

      if (this.isTimeStamp())
      {
         this.setTimeStamp(ComponentIF.EXECUTE_BEGIN);
      }

      /*
       * Check the inputs
       */

      if (session == null)
      {
         this.handleError(METHOD_NAME + "Argument session is null");
      }

      if (target == null)
      {
         this.handleError(METHOD_NAME + "Argument target is null");
      }

      opcode = target.getOpcode();  // could be null

      /*
       * Get the relative URL of the request
       */

      request = target.getProperty(TargetIF.PROP_RELATIVEPATH);

      if (request == null || request.length() < 1)
      {
         this.handleError(METHOD_NAME + "Target Property '" + TargetIF.PROP_RELATIVEPATH + "' has a null value");
      }

      /*
       * Create String array from the URL path
       */

      requestParts = StringUtil.stringToArray(request, "/");

      if (requestParts.length < 1)
      {
         this.handleError(METHOD_NAME + "Target Array for Relative Path is empty");
      }

      responseCheck = new Component();

      /*
       * Process each policy that has been configured for this Decider
       */

      policyNames = _policies.keySet().toArray(new String[_policies.size()]);

      if (this.getDebugLevelAsInt() >= DebugIF.CONFIG) // 1
      {
         Logger.logInfo(METHOD_NAME
            + "deciderEnvironment=" + this.getEnvironment().toString() + ", "
            + "opcode=" + (opcode != null ? opcode.toString() : "(null)") + ", "
            + "request='" + request + "', "
            + "policyNames=" + StringUtil.arrayToString(policyNames));
      }

      for (String policyId : policyNames)
      {
         if (policyId == null || policyId.length() < 1)
         {
            this.handleError(METHOD_NAME + "PolicyId is null");
         }

         policy = _policies.get(policyId);
         if (policy == null)
         {
            this.handleError(METHOD_NAME + "Policy is null, policyId='" + policyId + "'");
         }

         if (this.getDebugLevelAsInt() >= DebugIF.FINE)  // 2
         {
            Logger.logInfo(METHOD_NAME + policyId
               + ": environment=" + _policies.get(policyId).getEnvironment().toString()
               + ", mode=" + _policies.get(policyId).getMode().toString()
               + ", effect=" + _policies.get(policyId).getEffect().toString()
               + ", sessionTypes=" + _policies.get(policyId).getSessionTypes().toString());
         }

         responsePolicy = this.evaluatePolicy(session, opcode, requestParts, policy);

         switch (responsePolicy.getState())
         {
            case ALLOWED:
            {
               explicitAllow = true;
               break;
            }
            case DENIED:
            {
               explicitDeny = true;
               break;
            }
            case NOTAPPLICABLE:
            {
               break;
            }
            default:
            {
               this.handleError(METHOD_NAME + "Invalid Policy response: "
                  + "policyId='" + policyId + "', "
                  + "responseState='" + responsePolicy.getStateAsString() + "'");
               break;
            }
         }
      }

      if (explicitAllow && !explicitDeny)
      {
         responseCheck.setState(State.ALLOWED);
      }
      else
      {
         responseCheck.setState(State.DENIED);
      }

      responseCheck.setStatus("Decider=" + CLASS_NAME + ", "
         + "Environment=" + this.getEnvironment().toString() + ": "
         + request + " was " + responseCheck.getStateAsString()
         + ", opcode=" + (opcode != null ? opcode.toString() : "(null)"));

      if (this.isTimeStamp())
      {
         this.setTimeStamp(ComponentIF.EXECUTE_END);
         Logger.logInfo(METHOD_NAME + "Duration: "
            + this.getDuration(this.getTimeStamp(ComponentIF.EXECUTE_BEGIN), this.getTimeStamp(ComponentIF.EXECUTE_END))
            + " (msec) " + this.getEnvironment().toString());
      }

      if (this.getDebugLevelAsInt() >= DebugIF.CONFIG)  // 1
      {
         Logger.logInfo(METHOD_NAME + responseCheck.getStateAsString());
      }

      return responseCheck;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private ComponentIF evaluatePolicy(final SessionIF session, final Opcode opcode,
      final String[] requestParts, final PolicyIF policy) throws AuthorizationException
   //----------------------------------------------------------------
   {
      boolean matchPolicy = false;
      boolean matchTargetOper = false;
      boolean matchSessionClient = false;
      boolean requestAllowed = false;
      boolean requestDenied = false;
      Object policyUid = null;
      String METHOD_NAME = CLASS_NAME + ":evaluatePolicy(): ";
      String sessionPrincipal = null;
      String sessionClient = null;
      String policyName = null;
      String[] policyParts = null;
      String[] policyTargetIds = null;
      String[] policyClientIds = null;
      List<SessionType> policySessionTypes = null;
      TargetIF policyTarget = null;
      ComponentIF response = null;
      SessionType sessionType = null;
      Effect effect = null;
      PolicyMode mode = null;

      if (policy == null)
      {
         this.handleError(METHOD_NAME + "Policy is null");
      }

      response = new Component();

      policyUid = policy.getUniqueId();
      if ( policyUid != null)
      {
         policyName = policyUid.toString();
      }
      else
      {
         policyName = "(null)";
      }
      
      policySessionTypes = policy.getSessionTypes();
      policyTargetIds = policy.getTargetIds();
      policyClientIds = policy.getClientIds();
      effect = policy.getEffect();
      mode = policy.getMode();
      sessionType = session.getType();
      sessionClient = session.getClientId();

      if (session.getPrincipal() != null && session.getPrincipal().getUniqueId() != null)
      {
         sessionPrincipal = session.getPrincipal().getUniqueId().toString();
      }

      if (this.getDebugLevelAsInt() >= DebugIF.FINE) // 2
      {
         Logger.logInfo(METHOD_NAME
            + policyName + ": targets=" + StringUtil.arrayToString(policyTargetIds));
      }

      /*
       * Process each Target URL in the Policy
       */

      for (String policyTargetId : policyTargetIds)
      {
         if (policyTargetId != null && policyTargetId.length() > 0)
         {
            policyTarget = policy.getTarget(policyTargetId);
            if (policyTarget != null)
            {
               policyParts = policyTarget.getParsedValue();
               if (policyParts != null && policyParts.length > 0)
               {
                  if (this.getDebugLevelAsInt() >= DebugIF.FINER)  // 3
                  {
                     Logger.logInfo(METHOD_NAME
                        + policyName + ": " + policyTargetId
                        + ", policy=" + StringUtil.arrayToString(policyParts));
                  }

                  /*
                   * Compare the Policy's Target against the request
                   * Determine if there is a match
                   */

                  matchPolicy = this.evaluateTarget(session, requestParts, policyParts);

                  if (this.getDebugLevelAsInt() >= DebugIF.FINER) // 3
                  {
                     Logger.logInfo(METHOD_NAME
                        + policyName + ": " + policyTargetId + ", match=" + matchPolicy);
                  }

                  /*
                   * If there is a policy match with the requested resource, inspect policy
                   */

                  if (matchPolicy)
                  {
                     /*
                      * At this point:
                      * The request target "matches" a Policy's target
                      */

                     if (this.getDebugLevelAsInt() >= DebugIF.FINEST) // 4
                     {
                        Logger.logInfo(METHOD_NAME
                           + policyName + ": " + policyTargetId
                           + ", type='" + sessionType + "'"
                           + ", client='" + (sessionClient != null ? sessionClient : "(null)") + "'"
                           + ", principal='" + (sessionPrincipal != null ? sessionPrincipal : "(null)") + "'");
                        Logger.logInfo(METHOD_NAME
                           + policyName + ": " + policyTargetId
                           + ", targetOperations=" + policyTarget.getOperations().toString());
                        Logger.logInfo(METHOD_NAME
                           + policyName + ": " + policyTargetId
                           + ", targetClients=" + StringUtil.arrayToString(policyClientIds));
                     }

                     if (policySessionTypes.contains(sessionType))
                     {
                        /*
                         * At this point:
                         * The request target "matches" a Policy's target
                         * The Session's Type is one of the Policy's Session Types
                         */

                        if (policyClientIds.length < 1 || sessionClient == null || sessionClient.length() < 1)
                        {
                           /*
                            * The Policy' Session does not define any "Clients"
                            * In this case, set flag to "true",
                            * Either all Clients apply or are not applicable
                            */

                           matchSessionClient = true;
                        }
                        else
                        {
                           matchSessionClient = false;
                           for (String s : policyClientIds)
                           {
                              if (s.equalsIgnoreCase(sessionClient))
                              {
                                 matchSessionClient = true;
                              }
                           }
                        }

                        if (policyTarget.getOperations().isEmpty())
                        {
                           /*
                            * The Policy's Target does not have any Operations
                            * In this case, set flag to "true",
                            * Either all Operations apply or are not applicable
                            */

                           matchTargetOper = true;
                        }
                        else
                        {
                           /*
                            * The Policy's Target "has" at least one Operation
                            * Check to see if the opcode matches one of the Target Operations
                            */

                           matchTargetOper = this.evaluateOpcode(policyTarget, opcode);
                        }

                        if (matchTargetOper && matchSessionClient)
                        {
                           /*
                            * At this point:
                            * The request target "matches" a Policy's target
                            * The Session's Type is one of the Policy's Session Types
                            * The opcode matches one of the Target's Operations
                            * There are either NO defined Clients, or the Client matches
                            */

                           if (effect == Effect.ALLOW)
                           {
                              requestAllowed = true;
                           }
                           else
                           {
                              /*
                               * this is an explicit DENY in a policy,
                               * for the given Target / Session Type
                               * will override any allow
                               */

                              requestDenied = true;
                           }
                        }

                        if (this.getDebugLevelAsInt() >= DebugIF.FINEST) // 4
                        {
                           Logger.logInfo(METHOD_NAME
                              + policyName + ": " + policyTargetId
                              + ", opcode='" + (opcode != null ? opcode.toString() : "(null)") + "'"
                              + ", matchTargetOper=" + matchTargetOper
                              + ", matchClient=" + matchSessionClient
                              + ", policyEffect='" + effect + "'"
                              + ", policyMode='" + mode + "'"
                              + ", requestAllowed=" + requestAllowed
                              + ", requestDenied=" + requestDenied);
                        }
                     }
                  }
                  else
                  {
                     // The Policy's Target does not match the request
                  }
               }
               else
               {
                  this.handleError(METHOD_NAME + "Policy Target Value is null, name='" + policyTargetId + "'");
               }
            }
            else
            {
               this.handleError(METHOD_NAME + "Policy Target is null, name='" + policyTargetId + "'");
            }
         }
         else
         {
            this.handleError(METHOD_NAME + "Policy Target Name is null");
         }
      }

      if ((requestAllowed) && (!requestDenied))
      {
         /*
          * if request is allowed and request is NOT denied, set state to ALLOWED
          *
          */
         response.setState(State.ALLOWED);
      }
      else if (requestDenied)
      {
         /*
          * the request was denied, (explicitly or implicitly)
          * need to set denied state explicitly
          */
         response.setState(State.DENIED);
      }
      else
      {
         /*
          * This is a "not applicable" type of response
          * Not explicitly "ALLOWED" or explicitly "DENIED"
          * The Policies Targets did not match the Request's Target
          */
         response.setState(State.NOTAPPLICABLE);
      }

      if (this.getDebugLevelAsInt() >= DebugIF.FINE) // 2
      {
         Logger.logInfo(METHOD_NAME + policyName + ": " + response.getStateAsString());
      }

      return response;

   }

   //----------------------------------------------------------------
   private boolean evaluateTarget(final SessionIF session, final String[] requestParts,
      final String[] targetParts) throws AuthorizationException
   //----------------------------------------------------------------
   {
      int i = 0;
      boolean bDone = false;
      boolean bMatch = false;
      String METHOD_NAME = CLASS_NAME + ":evaluateTarget(): ";
      String policyPart = null;
      String requestPart = null;
      String[] policyParts = null;

      /*
       * Compare the request target (parts) against the policy target (parts)
       *
       * Step 1:
       * Check for variable replacement using session data
       * 
       * Step 2:
       * Evaluate target and policy part quantities ...
       *   Number of target "parts" must equal number of policy "parts"
       *   Unless ... the last policy part equals "*"
       *
       * Step 3:
       * Evaluate each "part" of the target ...
       *   Each literal string value (ignoring case)
       *   The "." single-dot character mean a wild card for only one "part"
       *   The "*" astrisk character means a wild card for any number of parts
       *     Can only be used at "the end", the last "part"
       *   When a given "part" does not match, stop, return false
       */

      policyParts = this.replaceSessionVariables(session, targetParts);

      if (this.getDebugLevelAsInt() >= DebugIF.FINEST) // 4
      {
         Logger.logInfo(METHOD_NAME
            + "target=" + StringUtil.arrayToString(targetParts)
            + ", policy=" + StringUtil.arrayToString(policyParts)
            + ", request=" + StringUtil.arrayToString(requestParts));
      }

      if (policyParts.length != requestParts.length)
      {
         if (!policyParts[policyParts.length - 1].equals("*"))
         {
            bDone = true;
            bMatch = false;
         }
      }

      while (!bDone)
      {
         if (i < requestParts.length && i < policyParts.length)
         {
            requestPart = requestParts[i];
            policyPart = policyParts[i];

            if (policyPart.equals("*"))
            {
               /*
                * Must be the LAST "part" of the Policy's Target
                */
               if (i != (policyParts.length - 1))
               {
                  throw new AuthorizationException(METHOD_NAME
                     + "invalid use of wildcard '*' in Policy Target="
                     + StringUtil.arrayToString(policyParts)
                     + ", can only be used as the last element");
               }
               bMatch = true;
               bDone = true;
            }
            else if (policyPart.equals("."))
            {
               /*
                * Wild card for a "middle part" of the Policy's Target
                */
               bMatch = true;
            }
            else if (policyPart.equalsIgnoreCase(requestPart))
            {
               /*
                * The policy "part" matches request "part" (ignoring case)
                */
               bMatch = true;
            }
            else
            {
               /*
                * The policy "part" DOES NOT match the request "part", stop processing
                */
               bMatch = false;
               bDone = true;
            }
         }
         else
         {
            bDone = true;
         }
         i++;
      }

      return bMatch;
   }

   //----------------------------------------------------------------
   private boolean evaluateOpcode(final TargetIF target, final Opcode opcode)
   //----------------------------------------------------------------
   {
      boolean match = false;
      Operation[] ops = null;
      Opcode oc = null;

      /*
       * Get the Target's colelction of allowed "operations" as an array
       * For each "Operation" get the associated "Opcode"
       * Compare the Opcode's for a match.
       */

      ops = target.getOperations().toArray(new Operation[target.getOperations().size()]);

      for (Operation o : ops)
      {
         oc = this.getOpcode(o);
         if (oc != null && oc == opcode)
         {
            match = true;
         }
      }

      return match;
   }

   //----------------------------------------------------------------
   private String[] replaceSessionVariables(final SessionIF session, final String[] array)
   //----------------------------------------------------------------
   {
      String str = null;
      String val = null;
      String[] output = null;
      PrincipalIF principal = null;

      /*
       * Check the Strings in the array for variables.
       * Look for "${...}" syntax and replace with data from the session
       *
       * Allowed variable names:
       * ${session.principal.contextid}  ContextId that authenticated the principal
       * ${session.principal.uniqueid}   UniqueId of the principal
       * ${session.clientid}             ClientId for the session
       *
       */

      if (array != null && session != null)
      {
         output = new String[array.length];

         for (int i = 0; i < array.length; i++)
         {
            val = null;
            str = array[i];
            if (str != null && str.length() > 0 && str.startsWith("${"))
            {
               if (str.equalsIgnoreCase(SESSION_PRINCIPAL_CONTEXTID))
               {
                  principal = session.getPrincipal();
                  if (principal != null)
                  {
                     val = principal.getContextId();
                  }
               }
               else if (str.equalsIgnoreCase(SESSION_PRINCIPAL_UNIQUEID))
               {
                  principal = session.getPrincipal();
                  if (principal != null && principal.getUniqueId() != null)
                  {
                     val = principal.getUniqueId().toString();
                  }
               }
               else if (str.equalsIgnoreCase(SESSION_CLIENTID))
               {
                  val = session.getClientId();
               }

               if (val == null || val.length() < 1)
               {
                  val = str;
               }

               output[i] = val;
            }
            else
            {
               output[i] = str;
            }
         }
      }
      else
      {
         output = new String[0];
      }

      return output;
   }

   //----------------------------------------------------------------
   private String getDuration(final Long begin, final Long end)
   //----------------------------------------------------------------
   {
      int iMsec = 0;
      String str = null;

      iMsec = end.intValue() - begin.intValue();

      str = Integer.toString(iMsec);

      return str;
   }
}
