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
package org.openptk.representation;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openptk.api.Opcode;
import org.openptk.api.State;
import org.openptk.authorize.Effect;
import org.openptk.authorize.Environment;
import org.openptk.authorize.TargetIF;
import org.openptk.authorize.TargetType;
import org.openptk.authorize.policy.PolicyIF;
import org.openptk.authorize.policy.PolicyMode;
import org.openptk.common.Operation;
import org.openptk.engine.EngineIF;
import org.openptk.session.SessionIF;
import org.openptk.session.SessionType;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Project OpenPTK
 */
//===================================================================
public class PolicyRepresentation extends Representation
//===================================================================
{

   private String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param engine
    */
   //----------------------------------------------------------------
   public PolicyRepresentation(final EngineIF engine)
   //----------------------------------------------------------------
   {
      super(engine);
      return;
   }

   /**
    * @param opcode
    * @param structIn
    * @return
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public StructureIF execute(final Opcode opcode, final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      StructureIF structOut = null;

      if (structIn == null)
      {
         this.handleError(session, METHOD_NAME + "Input Structure is null.");
      }

      if (this.isDebug())
      {
         this.logInfo(session, METHOD_NAME + "Opcode="
                 + opcode.toString() + ", " + structIn.toString());
      }

      switch (opcode)
      {
         case READ:
         {
            structOut = this.doRead(session, structIn);
            break;
         }
         case SEARCH:
         {
            structOut = this.doSearch(session, structIn);
            break;
         }
         default:
         {
            this.handleError(session, METHOD_NAME + "Unsupported Operation: '"
                    + opcode.toString() + "'");
         }
      }

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doRead(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      boolean allowNull = false;
      String uriBase = null;
      String policyid = null;
      String targetValue = null;
      String[] clientIds = null;
      PolicyIF policy = null;
      Effect effect = null;
      PolicyMode mode = null;
      Environment env = null;
      TargetIF target = null;
      TargetType targetType = null;
      List<SessionType> listTypes = null;
      List<Operation> listOperations = null;
      Map<String, TargetIF> mapTargets = null;
      StructureIF structOut = null;
      StructureIF structParamsPath = null;
      StructureIF structSession = null;
      StructureIF structTypes = null;
      StructureIF structClientIds = null;
      StructureIF structTargets = null;
      StructureIF structTarget = null;
      StructureIF structOperations = null;

      structParamsPath = structIn.getChild(StructureIF.NAME_PARAMPATH);
      if (structParamsPath == null)
      {
         this.handleError(session, METHOD_NAME
                 + "Structure '" + StructureIF.NAME_PARAMPATH + "' is null");
      }

      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);
      policyid = this.getStringValue(StructureIF.NAME_POLICYID, structParamsPath, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      policy = this.getEngine().getPolicy(policyid);

      if (policy == null)
      {
         this.handleError(session, METHOD_NAME + "Policy '" + policyid + "' is null");
      }

      structOut.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, policyid));

      structOut.addChild(new BasicStructure(StructureIF.NAME_DESCRIPTION, policy.getDescription()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_STATE, policy.getState().toString()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_STATUS, policy.getStatus()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_CATEGORY, policy.getCategory().toString()));
      structOut.addChild(new BasicStructure(StructureIF.NAME_CLASSNAME, policy.getClass().getName()));

      env = policy.getEnvironment();
      if (env != null)
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_ENVIRONMENT, env.toString()));
      }

      mode = policy.getMode();
      if (mode != null)
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_MODE, mode.toString()));
      }

      effect = policy.getEffect();
      if (effect != null)
      {
         structOut.addChild(new BasicStructure(StructureIF.NAME_EFFECT, effect.toString()));
      }

      /*
       * Properties
       */

      structOut.addChild(this.getPropsAsStruct(policy));

      /*
       * Session
       */

      structSession = new BasicStructure(StructureIF.NAME_SESSION);
      structOut.addChild(structSession);

      /*
       * Session Types
       */

      structTypes = new BasicStructure(StructureIF.NAME_TYPES);
      structSession.addChild(structTypes);

      listTypes = policy.getSessionTypes();
      {
         for (SessionType type : listTypes)
         {
            if (type != null)
            {
               structTypes.addValue(type.toString());
            }
         }
      }

      /*
       * Session Client Ids
       */

      structClientIds = new BasicStructure(StructureIF.NAME_CLIENTS);
      structSession.addChild(structClientIds);

      clientIds = policy.getClientIds();
      if (clientIds != null && clientIds.length > 0)
      {
         for (String clientId : clientIds)
         {
            if (clientId != null && clientId.length() > 0)
            {
               structClientIds.addValue(clientId);
            }
         }
      }

      /*
       * Targets
       */

      structTargets = new BasicStructure(StructureIF.NAME_TARGETS);
      structOut.addChild(structTargets);

      mapTargets = policy.getTargets();
      if (mapTargets != null && !mapTargets.isEmpty())
      {
         for (String targetId : mapTargets.keySet())
         {
            if (targetId != null && targetId.length() > 0)
            {
               target = policy.getTarget(targetId);
               if (target != null)
               {
                  structTarget = new BasicStructure(StructureIF.NAME_TARGET);
                  structTarget.addChild(new BasicStructure(StructureIF.NAME_TARGETID, targetId));

                  targetType = target.getType();
                  if (targetType != null)
                  {
                     structTarget.addChild(new BasicStructure(StructureIF.NAME_TYPE, targetType.toString()));
                  }

                  targetValue = target.getValue();
                  if (targetValue != null && targetValue.length() > 0)
                  {
                     structTarget.addChild(new BasicStructure(StructureIF.NAME_VALUE, targetValue));
                  }

                  /*
                   * Operations
                   */

                  listOperations = target.getOperations();
                  if (listOperations != null && !listOperations.isEmpty())
                  {
                     structOperations = new BasicStructure(StructureIF.NAME_OPERATIONS);
                     for (Operation operation : listOperations)
                     {
                        if (operation != null)
                        {
                           structOperations.addValue(operation.toString());
                        }
                     }
                     structTarget.addChild(structOperations);
                  }
                  structTargets.addChild(structTarget);
               }
            }
         }
      }

      structOut.setState(State.SUCCESS);

      return structOut;
   }

   //----------------------------------------------------------------
   private StructureIF doSearch(final SessionIF session, final StructureIF structIn) throws Exception
   //----------------------------------------------------------------
   {
      boolean allowNull = false;
      int len = 0;
      String METHOD_NAME = CLASS_NAME + ":doSearch(): ";
      String uid = null;
      String uriBase = null;
      String uriChild = null;
      String[] policyIds = null;
      PolicyIF policy = null;
      Environment env = null;
      StructureIF structOut = null;
      StructureIF structEnv = null;
      StructureIF structEnvs = null;
      StructureIF structPolicy = null;
      StructureIF structPolicies = null;
      List<PolicyIF> listPolicies = null;
      Map<Environment, List<PolicyIF>> mapEnvPolicies = null;



      uriBase = this.getStringValue(StructureIF.NAME_URI, structIn, allowNull);

      structOut = new BasicStructure(StructureIF.NAME_RESPONSE);
      structOut.addChild(new BasicStructure(StructureIF.NAME_URI, uriBase));

      policyIds = this.getEngine().getPolicyNames();
      len = policyIds.length;

      structOut.addChild(new BasicStructure(StructureIF.NAME_LENGTH, len));

      /*
       * initialize and load the map Map
       */

      mapEnvPolicies = new LinkedHashMap<Environment, List<PolicyIF>>();
      for (Environment e : Environment.values())
      {
         mapEnvPolicies.put(e, new LinkedList<PolicyIF>());
      }

      for (String id : policyIds)
      {
         policy = this.getEngine().getPolicy(id);
         if (policy != null)
         {
            env = policy.getEnvironment();
            if (env != null)
            {
               listPolicies = mapEnvPolicies.get(env);
               if (listPolicies != null)
               {
                  listPolicies.add(policy);
               }
            }
            else
            {
               this.getEngine().logWarning(METHOD_NAME
                       + "Policy '" + id + "' has a null Environment");
            }
         }
      }

      /*
       * Process all the Policies in each of the Environments
       */

      structEnvs = new BasicStructure(StructureIF.NAME_ENVIRONMENTS);
      structOut.addChild(structEnvs);

      for (Environment e : mapEnvPolicies.keySet())
      {
         structEnv = new BasicStructure(StructureIF.NAME_ENVIRONMENT);
         structEnv.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, e.toString()));

         listPolicies = mapEnvPolicies.get(e);
         if (listPolicies != null)
         {
            structPolicies = new BasicStructure(StructureIF.NAME_POLICIES);
            structPolicies.setMultiValued(true);

            for (PolicyIF p : listPolicies)
            {
               structPolicy = new BasicStructure(StructureIF.NAME_POLICY);

               uid = p.getUniqueId().toString();
               structPolicy.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, uid));

               if (uriBase.endsWith("/"))
               {
                  uriChild = uriBase + uid;
               }
               else
               {
                  uriChild = uriBase + "/" + uid;
               }

               structPolicy.addChild(new BasicStructure(StructureIF.NAME_URI, uriChild));

               structPolicies.addValue(structPolicy);
            }

            structEnv.addChild(structPolicies);
         }

         structEnvs.addChild(structEnv);
      }

      structOut.setState(State.SUCCESS);

      return structOut;
   }
}
