/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2012-2013 Project OpenPTK
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
package org.openptk.config.mapper;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openptk.exception.StructureException;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman
 *
 * @since 2.2.0
 */
//===================================================================
public abstract class AttrMap implements AttrMapIF
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private boolean _error = false;
   private String _uniqueId = null;
   protected Map<String, ExternalAttrIF> _attributes = null;
   protected Map<String, String> _indexFwKey = null;
   protected Set<String> _requiredExtKey = null;
   protected Set<String> _requiredFwKey = null;

   //----------------------------------------------------------------
   public AttrMap()
   //----------------------------------------------------------------
   {
      _attributes = new LinkedHashMap<String, ExternalAttrIF>();
      _indexFwKey = new LinkedHashMap<String, String>();
      _requiredExtKey = new LinkedHashSet<String>();
      _requiredFwKey = new LinkedHashSet<String>();

      return;
   }

   //----------------------------------------------------------------
   @Override
   public final String getUniqueId()
   //----------------------------------------------------------------
   {
      return (_uniqueId != null ? new String(_uniqueId) : null);
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setUniqueId(final String uniqueId)
   //----------------------------------------------------------------
   {
      if (uniqueId != null && uniqueId.length() > 0)
      {
         _uniqueId = new String(uniqueId);
      }

      return;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized boolean isError()
   //----------------------------------------------------------------
   {
      boolean err = false;
      err = _error;
      return err;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setError(final boolean error)
   //----------------------------------------------------------------
   {
      _error = error;
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final ExternalAttrIF getAttribute(String key, boolean useFwKey)
   //----------------------------------------------------------------
   {
      String extKey = null;
      ExternalAttrIF attr = null;
      ExternalAttrIF val = null;

      /*
       * If "useFwKey" is true ... the key is a framework attribute name
       * else the key is a external attribute name
       */

      if (key != null && key.length() > 0)
      {
         if (useFwKey)
         {
            extKey = _indexFwKey.get(key);
         }
         else
         {
            extKey = key;
         }

         if (extKey != null && extKey.length() > 0)
         {
            attr = _attributes.get(extKey);
            if (attr != null)
            {
               val = attr.copy();
            }
         }
      }

      return val;
   }

   //----------------------------------------------------------------
   @Override
   public final Map<String, ExternalAttrIF> getAttributes()
   //----------------------------------------------------------------
   {
      Map<String, ExternalAttrIF> attrs = null;
      Set<String> set = null;
      ExternalAttrIF attr = null;

      if (!_attributes.isEmpty())
      {
         /*
          * use a "deep copy"
          */
         attrs = new LinkedHashMap<String, ExternalAttrIF>();
         set = _attributes.keySet();
         for (String s : set)
         {
            attr = _attributes.get(s).copy();
            attrs.put(s, attr);
         }
      }

      return attrs;
   }

   //----------------------------------------------------------------
   @Override
   public final List<String> getAttributesNames(boolean useFwKey)
   //----------------------------------------------------------------
   {
      List<String> names = null;
      Set<String> keys = null;

      names = new LinkedList<String>();

      if (useFwKey)
      {
         keys = _indexFwKey.keySet();
         if (keys != null)
         {
            for (String s : keys)
            {
               names.add(s);
            }
         }
      }
      else
      {
         keys = _attributes.keySet();
         if (keys != null)
         {
            for (String s : keys)
            {
               names.add(s);
            }
         }
      }

      return names;
   }

   //----------------------------------------------------------------
   @Override
   public final int getAttributesSize()
   //----------------------------------------------------------------
   {
      return _attributes.size();
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setAttribute(final ExternalAttrIF attr)
   //----------------------------------------------------------------
   {
      String extKey = null;
//      ExternalAttrIF.Kind kind = null;
      String fwKey = null;

      /*
       * Uses a 1:1 mapping of external name to the framework name
       * If the framework name (fwKey) is null, from the getMapTo() method ... 
       * it's the same as the external name (extKey)
       * 
       * <Attribute id="userName" required="true" mapto="uniqueId"/>
       */

      if (attr != null)
      {
         extKey = attr.getName();
         fwKey = attr.getMapTo();

         if (fwKey == null || fwKey.length() < 1)
         {
            fwKey = extKey;
         }

         _attributes.put(extKey, attr);
         _indexFwKey.put(fwKey, extKey);

         if (attr.isRequired())
         {
            _requiredExtKey.add(extKey);
            _requiredFwKey.add(fwKey);
         }
      }

//      /*
//       * Need to handle the following types of External <--> Internal attribute mapping:
//       */
//
//      if (attr != null)
//      {
//         extKey = attr.getName();
//         if (extKey != null && extKey.length() > 0)
//         {
//            kind = attr.getKind();
//
//            switch (kind)
//            {
//               case SIMPLE:
//                  this.storeAttribute(attr);
//                  break;
//               case SUBATTR:
//                  this.setSubattrAttribute(attr);
//                  break;
//               case PROCESS:
//                  this.setProcessAttribute(attr);
//                  break;
//               case DATA:
//                  this.setDataAttribute(attr);
//                  break;
//            }
//
//         }
//      }
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized StructureIF externalToFramework(StructureIF structExternal) throws StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String extName = null;
      StringBuilder buf = null;
      Map<String, Boolean> mapRequired = null;
      StructureIF structFramework = null;
//      StructureIF structSubject = null;
//      StructureIF structAttrs = null;
//      StructureIF structFwAttr = null;
      StructureIF[] arrayStructAttrs = null;

      /*
       * Used by these operations: Create, Update
       * Convert the external Structure into a framework Structure
       * Uses the collection ExternalAttrIF objects to transform data
       *
       */
//
// EXTERNAL FORMAT (SCIM 1.0)
//
//{
//  "schemas": ["urn:scim:schemas:core:1.0"],
//  "id": "2819c223-7f76-453a-919d-413861904646",
//  "userName": "bjensen@example.com",
//  "name": {
//    "familyName": "Jensen",
//    "givenName": "Barbara"
//  },
//  "emails": [
//    {
//      "value": "bjensen@example.com",
//      "type": "work",
//      "primary": true
//    }
//  ],
//  "phoneNumbers": [
//    {
//      "value": "555-555-5555",
//      "type": "work"
//    }
//  ],
//  "userType": "Employee",
//  "title": "Tour Guide"
//}  
//
// FRAMEWORK FORMAT:
//
//{
//  "subject" : { 
//    "attributes" : { 
//      "uniqueId": "bjensen@example.com",
//      "lastname" : "Jensen",
//      "firstname" : "Barbara",
//      "email" : "bjensen@example.com",
//      "phone" : "555-555-1212",
//      "title" : "Tour Guide"
//    }
//  }
//}

      if (structExternal == null)
      {
         this.handleError(METHOD_NAME + "External Structure is null");
      }

      /*
       * initialize the map of required external attributes, set to false
       */

      mapRequired = new LinkedHashMap<String, Boolean>();
      for (String s : _requiredExtKey)
      {
         mapRequired.put(s, false);
      }

      structFramework = this.getFrameworkStructure(structExternal, mapRequired);

//         arrayStructAttrs = structExternal.getChildrenAsArray();
//         if (arrayStructAttrs != null && arrayStructAttrs.length > 0)
//         {
//            /*
//             * Create the "outer" structures for "subject" and "attributes"
//             */
//            structSubject = new BasicStructure(StructureIF.NAME_SUBJECT);
//            structAttrs = new BasicStructure(StructureIF.NAME_ATTRIBUTES);
//
//            for (StructureIF structExtAttr : arrayStructAttrs)
//            {
//               /*
//                * update the "required" map if needed
//                */
//               extName = structExtAttr.getId();
//               if (extName != null && extName.length() > 0 && mapRequiredExtAttrs.containsKey(extName))
//               {
//                  mapRequiredExtAttrs.put(extName, true);
//               }
//
//               /*
//                * get the Framework structure
//                */
//               structFwAttr = this.getFrameworkStructure(structExtAttr);
//               if (structFwAttr != null)
//               {
//                  structAttrs.addChild(structFwAttr);
//               }
//            }
//
//            structSubject.addChild(structAttrs);
//
//         }

      /*
       * Make sure all the required attributes where found
       */

      buf = new StringBuilder();
      for (String s : _requiredExtKey)
      {
         if (!mapRequired.get(s))
         {
            if (buf.length() > 0)
            {
               buf.append(", ");
            }
            buf.append(s);
         }
      }
      if (buf.length() > 0)
      {
         this.handleError(METHOD_NAME + "Missing required attributes: '"
            + buf.toString() + "'");
      }

      return structFramework;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized StructureIF frameworkToExternal(StructureIF structFramework) throws StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      StructureIF structResponse = null;
      StructureIF structSubject = null;
      StructureIF structAttrs = null;
      StructureIF structData = null;
      StructureIF structExtAttr = null;
      StructureIF[] arrayStructAttrs = null;

      /*
       * Used by these operations: Read, Search
       * Convert the framework Structure to an external structure
       * Uses the collection ExternalAttrIF objects to transform data
       *
       * Sample internal Structure: OpenPTK Subject (JSON syntax)
       */
//
// FRAMEWORK FORMAT:
//
//{
//    "response" : {
//        "uri" : "http:...",
//        "state" : "SUCCESS",
//        "status" : "Entry found",
//        "subject" : {
//            "uniqueid" : "JBAUER2",
//            "attributes" : {
//                "status" : "Active",
//                "lastname" : "Bauer",
//                "firstname" : "Jack",
//                "type" : "End-User",
//                "uniqueid" : "JBAUER2",
//                "title" : "Special Agent",
//                "email" : "jack@ctu.org",
//                "telephone" : null,
//                "fullname" : "Jack Bauer"
//            }
//        }
//    }
//}     
//
// EXTERNAL FORMAT (SCIM 1.0)
//
//{
//  "schemas": ["urn:scim:schemas:core:1.0"],
//  "id": "2819c223-7f76-453a-919d-413861904646",
//  "userName": "bjensen@example.com",
//  "name": {
//    "familyName": "Jensen",
//    "givenName": "Barbara"
//  },
//  "emails": [
//    {
//      "value": "bjensen@example.com",
//      "type": "work",
//      "primary": true
//    }
//  ],
//  "phoneNumbers": [
//    {
//      "value": "555-555-5555",
//      "type": "work"
//    }
//  ],
//  "userType": "Employee",
//  "title": "Tour Guide"
//}  

      if (structFramework != null)
      {
         /*
          * Unwrap the outter Structure elements:
          * response, subject, attributes
          */
         structResponse = structFramework.getChild(StructureIF.NAME_RESPONSE);
         if (structResponse != null)
         {
            structSubject = structResponse.getChild(StructureIF.NAME_SUBJECT);
            if (structSubject != null)
            {
               structAttrs = structSubject.getChild(StructureIF.NAME_ATTRIBUTES);
               if (structAttrs != null)
               {
                  arrayStructAttrs = structAttrs.getChildrenAsArray();
                  if (arrayStructAttrs != null && arrayStructAttrs.length > 0)
                  {
                     structData = new BasicStructure(StructureIF.NAME_DATA);

                     for (StructureIF structFwAttr : arrayStructAttrs)
                     {
                        /*
                         * get the External structure
                         */
                        structExtAttr = this.getExternalStructure(structFwAttr);
                        if (structExtAttr != null)
                        {
                           structData.addChild(structExtAttr);
                        }
                     }
                  }
               }
               else
               {
                  this.handleError(METHOD_NAME + "Attributes structure is null");
               }
            }
            else
            {
               this.handleError(METHOD_NAME + "Subject structure is null");
            }
         }
         else
         {
            this.handleError(METHOD_NAME + "Response structure is null");
         }
      }

      return structData;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   //----------------------------------------------------------------
   protected void handleError(String msg) throws StructureException
   //----------------------------------------------------------------
   {
      if (msg == null || msg.length() < 1)
      {
         msg = "(empty message)";
      }
      throw new StructureException(msg);
   }

   abstract protected StructureIF getFrameworkStructure(final StructureIF structExtAttr, final Map<String, Boolean> mapRequired) throws StructureException;

   abstract protected StructureIF getExternalStructure(final StructureIF structFwAttr) throws StructureException;

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
//   //----------------------------------------------------------------
//   private synchronized void setSimpleAttribute(final ExternalAttrIF attr)
//   //----------------------------------------------------------------
//   {
//      String extKey = null;
//      String fwKey = null;
//
//      /*
//       * Uses a 1:1 mapping of external name to the framework name
//       * If the framework name (fwKey) is null, from the getMapTo() method ... 
//       * it's the same as the external name (extKey)
//       * 
//       * <Attribute id="userName" required="true" mapto="uniqueId"/>
//       */
//
//      extKey = attr.getName();
//      fwKey = attr.getMapTo();
//
//      if (fwKey == null || fwKey.length() < 1)
//      {
//         fwKey = extKey;
//      }
//
//      _attributes.put(extKey, attr);
//      _indexFwKey.put(fwKey, extKey);
//
//      if (attr.isRequired())
//      {
//         _requiredExtKey.add(extKey);
//         _requiredFwKey.add(fwKey);
//      }
//
//      return;
//   }

//   //----------------------------------------------------------------
//   private synchronized void setSubattrAttribute(final ExternalAttrIF attr)
//   //----------------------------------------------------------------
//   {
//      String outerKey = null;
//      String extKey = null;
//      String fwKey = null;
//      SubAttributes subattrs = null;
//      ExternalAttrIF subAttr = null;
//      Mode mode = null;
//
//      /*
//       * Uses a nested model (X/Y:1)
//       * The outer name (extKey) does not map to a Framework name
//       * The inner name(s) map to a Framework name
//       * Need to concatinate the outer+"/"+level as key for external name
//       * If the framework name (fwKey) is null, from the getMapTo() method ... 
//       * it's the same as the external name (subKey)
//       * 
//       * <Attribute id="name">
//       *    <SubAttributes>
//       *       <SubAttribute id="familyName" mapto="lastname" required="true"/>
//       *       <SubAttribute id="givenName" mapto="firstname" required="true"/>
//       *    </SubAttributes>
//       * </Attribute>
//       * 
//       * Mapping:  External          Framework
//       *           name/familyName   lastname
//       *           name/givenName    firstname
//       * (opt)     familyName        lastname
//       *           givenName         firstname
//       * 
//       */
//
//      outerKey = attr.getName();
//
//      subattrs = attr.getSubAttributes();
//      if (subattrs != null)
//      {
//         for (String subKey : subattrs.getNames())
//         {
//            if (subKey != null && subKey.length() > 0)
//            {
//               subAttr = subattrs.getAttribute(subKey);
//               if (subAttr != null)
//               {
//                  mode = subAttr.getMode();
//                  extKey = outerKey + SUBCHAR + subKey;
//                  fwKey = subAttr.getMapTo();
//
//                  if (fwKey == null || fwKey.length() < 1)
//                  {
//                     fwKey = subKey;
//                  }
//
//                  _attributes.put(extKey, subAttr);
//                  _indexFwKey.put(fwKey, extKey);
//
//                  if (mode != Mode.IGNORE && subAttr.isRequired())
//                  {
//                     _requiredExtKey.add(extKey);
//                     _requiredFwKey.add(fwKey);
//                  }
////                  else if (mode == Mode.OUTBOUND || mode == Mode.BOTH && subAttr.isRequired())
////                  {
////                     _reqProducerExtKey.add(extKey);
////                     _reqProducerFwKey.add(fwKey);
////                  }
//               }
//            }
//         }
//      }
//
//      return;
//   }

//   //----------------------------------------------------------------
//   private synchronized void setProcessAttribute(final ExternalAttrIF attr)
//   //----------------------------------------------------------------
//   {
//      /*
//       * The process attribute is used to "define" runtime values
//       * Mostly for output (READ/SEARCH) for a OUTBOUND
//       * Relative to indexing ... it's treated like a "simple" attribute
//       * 
//       * <Attribute id="id" mode="producer" required="true">
//       *    <Processes>
//       *       <Process mode="producer" value="Project OpenPTK SCIM 1.0 Service"/>
//       *    </Processes>
//       * </Attribute>
//       * 
//       */
//
//      this.storeAttribute(attr);
//
//      return;
//   }

//   //----------------------------------------------------------------
//   private synchronized void setDataAttribute(final ExternalAttrIF attr)
//   //----------------------------------------------------------------
//   {
//      String extKey = null;
//      String outerKey = null;
//      String value = null;
//      String mapFrom = null;
//      String fwKey = null;
//      ExternalAttrIF dataAttr = null;
//      Data data = null;
//      Data dataMatch = null;
//      Match match = null;
//      Mode mode = null;
//      Map<String, Datum> mapDatum = null;
//
//      /*
//       * This is conditional (X\Y\Z:1) nesting based on a  match
//       * The Datum elements are ONLY mapped when the "mapto" is set,
//       * there is no implicit framework mapping (using the external name)
//       * 
//       * <Attribute id="phoneNumbers" multivalued="true">
//       *    <Data id="phoneNumber" undefined="value">
//       *       <Datum id="type"/>
//       *       <Datum id="value"/>
//       *       <Datum id="primary" type="boolean"/>
//       *       <Match id="work_phone" datum="type" value="work" mapto="phone"/>
//       *    </Data>
//       * </Attribute>
//       * 
//       * Mapping:  External                      Framework (when "type" equals "work")
//       *           phonenumbers|work             phone
//       * 
//       * <Attribute id="addresses" multivalued="true">
//       *    <Data id="address">
//       *       <Datum id="type"/>
//       *       <Datum id="primary" type="boolean"/>
//       *       <Datum id="formatted" mode="ignore"/>
//       *       <Datum id="streetAddress"/>
//       *       <Datum id="locality"/>
//       *       <Datum id="region"/>
//       *       <Datum id="postalCode"/>
//       *       <Datum id="country"/>
//       *       <Match id="work_address"    datum="type" value="work" mapfrom="streetAddress" mapto="address"/>
//       *       <Match id="work_city"       datum="type" value="work" mapfrom="locality"      mapto="city"/>
//       *       <Match id="work_state"      datum="type" value="work" mapfrom="region"        mapto="state"/>
//       *       <Match id="work_postalcode" datum="type" value="work" mapfrom="postalCode"    mapto="postalcode"/>
//       *       <Match id="work_country"    datum="type" value="work" mapfrom="country"       mapto="country"/>
//       *    </Data>
//       * </Attribute>
//       * 
//       * Mapping:  External                       Framework   (when "type" equals "work")
//       *           addresses|work|streetAddress   address
//       *           addresses|work|locality        city
//       *           addresses|work|region          state
//       *           addresses|work|postalCode      postalcode
//       *           addresses|work|country         country
//       * (opt)     streetAddress                  address
//       *           locality                       city
//       *           region                         state
//       */
//
//      data = attr.getData();
//
//      if (data != null)
//      {
//         mode = attr.getMode();
//         outerKey = attr.getName();
//
//         /*
//          * For processing / indexing, we only care about the Matches
//          * The "stored" attr shall only contain the given "match" and all the "datum"
//          */
//
//         mapDatum = new LinkedHashMap<String, Datum>();
//         for (String datumId : data.getDatumIds())
//         {
//            mapDatum.put(datumId, data.getDatum(datumId));
//         }
//
//         for (String matchId : data.getMatchIds())
//         {
//            if (matchId != null && matchId.length() > 0)
//            {
//               match = data.getMatch(matchId);
//               if (match != null)
//               {
//                  value = match.getValue();
//                  if (value != null && value.length() > 0)
//                  {
//                     fwKey = match.getMapTo();
//                     if (fwKey != null && fwKey.length() > 0)
//                     {
//                        extKey = outerKey + DATACHAR + value; // two part extKey
//
//                        mapFrom = match.getMapFrom();
//                        if (mapFrom != null && mapFrom.length() > 0)
//                        {
//                           extKey += DATACHAR + mapFrom; // three part extKey
//                        }
//
//                        dataAttr = new ExternalAttr(extKey);
//                        dataAttr.setMapTo(fwKey);
//                        dataAttr.setKind(Kind.DATA);
//                        dataAttr.setMode(attr.getMode());
//
//                        /*
//                         * create a Data object that contains all the Datum object
//                         * and only include the current Match object
//                         */
//
//                        dataMatch = new Data(extKey);
//                        for (String k : data.getDatumIds())
//                        {
//                           dataMatch.addDatum(data.getDatum(k));
//                        }
//                        dataMatch.addMatch(match);
//
//                        dataAttr.setData(dataMatch);
//
//                        /*
//                         * Add the External Attribute to the "map" and update the index
//                         */
//
//                        _attributes.put(extKey, dataAttr);
//                        _indexFwKey.put(fwKey, extKey);
//
//                        if (mode != Mode.IGNORE && attr.isRequired())
//                        {
//                           _requiredExtKey.add(extKey);
//                           _requiredFwKey.add(fwKey);
//                        }
////                        else if (mode == Mode.OUTBOUND || mode == Mode.BOTH && attr.isRequired())
////                        {
////                           _reqProducerExtKey.add(extKey);
////                           _reqProducerFwKey.add(fwKey);
////                        }
//                     }
//                  }
//               }
//            }
//         }
//      }
//
//      return;
//   }
}
