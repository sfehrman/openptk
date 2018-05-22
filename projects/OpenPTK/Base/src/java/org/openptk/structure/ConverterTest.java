/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009 Sun Microsystems, Inc.
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
package org.openptk.structure;

import java.util.LinkedList;
import java.util.List;

import org.openptk.api.Element;
import org.openptk.api.State;
import org.openptk.exception.ConverterException;
import org.openptk.logging.Logger;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class ConverterTest
//===================================================================
{

   /**
    * @param args optional arguments
    */
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      ConverterTest test = new ConverterTest();
      try
      {
         test.runEncode();
//         test.runDecode();
      }
      catch (Exception ex)
      {
         Logger.logError(ex.getMessage());
      }
   }

   //----------------------------------------------------------------
   private void runEncode() throws Exception
   //----------------------------------------------------------------
   {
      /*
       * This main method is for testing the different types of converters
       */

      List<String> values = null;

//      ConverterIF plain = null;
//      ConverterIF html = null;
      ConverterIF json = null;
      ConverterIF xml = null;

      StructureIF readResponse = null;
      StructureIF searchResponse = null;
      StructureIF subjects = null;
      StructureIF subject = null;
      StructureIF attributes = null;
      StructureIF resources = null;
      StructureIF resource = null;
      Element elem = null;

      // search response --------------------------------------------

      searchResponse = new BasicStructure(StructureIF.NAME_RESPONSE);
      searchResponse.setType(StructureType.CONTAINER); // default is PARENT (for addChild methods)
      searchResponse.addChild(new BasicStructure(StructureIF.NAME_URI, "http://localhost:8080/.../subjects"));
      searchResponse.addChild(new BasicStructure(StructureIF.NAME_LENGTH, 15));
      searchResponse.addChild(new BasicStructure(StructureIF.NAME_OFFSET, 15));
      searchResponse.addChild(new BasicStructure(StructureIF.NAME_QUANTITY, 10));

      // subjects

      subjects = new BasicStructure(StructureIF.NAME_SUBJECTS);
      subjects.setMultiValued(true);

      searchResponse.addChild(subjects);

      // ------- user one

      subject = new BasicStructure(StructureIF.NAME_SUBJECT);
      subject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, "jwayne"));
      attributes = new BasicStructure(StructureIF.NAME_ATTRIBUTES);
      subject.addChild(attributes);
      attributes.addChild(new BasicStructure("firstname", "John"));
      attributes.addChild(new BasicStructure("lastname", "Wayne"));
      attributes.addChild(new BasicStructure("email", "john.wayne@duke.com"));

      subjects.addValue(subject);

      // ------- user two

      subject = new BasicStructure(StructureIF.NAME_SUBJECT);
      subject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, "jsparrow"));
      attributes = new BasicStructure(StructureIF.NAME_ATTRIBUTES);
      subject.addChild(attributes);
      attributes.addChild(new BasicStructure("firstname", "Jack"));
      attributes.addChild(new BasicStructure("lastname", "Sparrow"));
      attributes.addChild(new BasicStructure("email", "jack.sparrow@pirates.com"));

      subjects.addValue(subject);

      // ------- user three

      subject = new BasicStructure(StructureIF.NAME_SUBJECT);
      subject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, "jharknes"));
      attributes = new BasicStructure(StructureIF.NAME_ATTRIBUTES);
      subject.addChild(attributes);
      attributes.addChild(new BasicStructure("firstname", "Jack"));
      attributes.addChild(new BasicStructure("lastname", "Harkness"));
      attributes.addChild(new BasicStructure("email", "jack.harkness@torchwood.com"));

      subjects.addValue(subject);

      // read response ----------------------------------------------

      readResponse = new BasicStructure(StructureIF.NAME_RESPONSE);
      readResponse.setType(StructureType.CONTAINER); // default is PARENT (for addChild methods)
      readResponse.addChild(new BasicStructure(StructureIF.NAME_URI, "http://localhost:8080/.../subjects/aaa"));
      readResponse.addChild(new BasicStructure(StructureIF.NAME_STATE, State.SUCCESS.toString()));
      readResponse.addChild(new BasicStructure(StructureIF.NAME_STATUS, "Entry found."));

      values = new LinkedList<String>();
      values.add("Mothers maiden");
      values.add("City Born");
      values.add("Frequent Flyer");

      subject = new BasicStructure(StructureIF.NAME_SUBJECT);
      subject.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID, "jharknes"));
      
      attributes = new BasicStructure(StructureIF.NAME_ATTRIBUTES);
      attributes.addChild(new BasicStructure("firstname", "Jack"));
      attributes.addChild(new BasicStructure("lastname", "Harkness"));
      attributes.addChild(new BasicStructure("email", "jack.harkness@torchwood.com"));
      attributes.addChild(new BasicStructure("manager", "CEO"));
      attributes.addChild(new BasicStructure("location", "loc35"));
      attributes.addChild(new BasicStructure("forgottenPasswordQuestions", values));
      attributes.addChild(new BasicStructure("forgottenPasswordAnswers", new LinkedList<String>()));
      attributes.addChild(new BasicStructure("title", "Captain"));
      attributes.addChild(new BasicStructure("lastcommafirst", "Harkness, Jack"));

      values = new LinkedList<String>();
      values.add("employee");
      values.add("manager");
      values.add("leader");
      
      attributes.addChild(new BasicStructure("roles", values));

      subject.addChild(attributes);
      readResponse.addChild(subject);
      
      resources = new BasicStructure(StructureIF.NAME_RESOURCES);
      
      resource = new BasicStructure(StructureIF.NAME_RESOURCE);
      resource.addChild(new BasicStructure("description", "Relationships"));
      resource.addChild(new BasicStructure("uri", "http://..."));
      
      resources.addValue(resource);
      
      resource = new BasicStructure(StructureIF.NAME_RESOURCE);
      resource.addChild(new BasicStructure("description", "Views"));
      resource.addChild(new BasicStructure("uri", "http://..."));
      
      resources.addValue(resource);
      
      readResponse.addChild(resources);

      // create the converters

//      plain = new PlainConverter();
//      html = new HtmlConverter();
      json = new JsonConverter();
      xml = new XmlConverter();

      /*
       * XML
       */

//      elem = new Element();
//      elem.setUniqueId("forgottenPasswordQuestions");
//      elem.setProperty(StructureIF.NAME_MULTIVALUE, "true");
//      xml.setStructInfo("forgottenPasswordQuestions", elem);
//
//      elem = new Element();
//      elem.setUniqueId("forgottenPasswordAnswers");
//      elem.setProperty(StructureIF.NAME_MULTIVALUE, "true");
//      xml.setStructInfo("forgottenPasswordAnswers", elem);
//
//      elem = new Element();
//      elem.setUniqueId("roles");
//      elem.setProperty(StructureIF.NAME_MULTIVALUE, "true");
//      xml.setStructInfo("roles", elem);

//      try
//      {
//         System.out.println("------\n" + plain.encode(response) + "-------\n\n");
//      }
//      catch (ConverterException ex)
//      {
//         Logger.getLogger(ex.getMessage());
//      }
//
//      try
//      {
//         System.out.println("------\n" + html.encode(response) + "------\n\n");
//      }
//      catch (ConverterException ex)
//      {
//         Logger.getLogger(ex.getMessage());
//      }
//
//      try
//      {
//         System.out.println("------\n" + response.toString() + "\n------\n\n");
//         System.out.println("------\n" + json.encode(response) + "\n------\n\n");
//      }
//      catch (ConverterException ex)
//      {
//         Logger.getLogger(ex.getMessage());
//      }
//
      try
      {
         System.out.println("\n\n------\n" + searchResponse.toString() + "\n------\n\n");
         System.out.println("\n\n------\n" + readResponse.toString() + "\n------\n\n");
//         System.out.println("\n\n------\n" + xml.encode(response) + "\n------\n\n");
         System.out.println("\n\n------\n" + json.encode(searchResponse) + "\n------\n\n");
         System.out.println("\n\n------\n" + json.encode(readResponse) + "\n------\n\n");
         System.out.println("\n\n------\n" + json.encode(new BasicStructure("foo", "bar")) + "\n------\n\n");
         System.out.println("\n\n------\n" + json.encode(new BasicStructure("number", 1234)) + "\n------\n\n");
      }
      catch (ConverterException ex)
      {
         Logger.logError(ex.getMessage());
      }

      return;
   }

   //----------------------------------------------------------------
   private void runDecode()
   //----------------------------------------------------------------
   {
      String dataJSON = null;
      String dataXML = null;
      String scimJSON = null;
//      String scimXML = null;
      ConverterIF json = null;
//      ConverterIF xml = null;
      StructureIF struct = null;
      Element elem = null;

      scimJSON = "{"
         + "\"schemas\":[\"urn:scim:schemas:core:1.0\",\"urn:scim:schemas:user:1.0\",\"urn:scim:schemas:group:1.0\"],"
         + "\"id\":\"2819c223-7f76-453a-919d-413861904646\","
         + "\"userName\":\"bjensen@example.com\","
         + "\"name\":{"
         + "\"familyName\":\"Jensen\","
         + "\"givenName\":\"Barbara\""
         + "},"
         + "\"emails\":["
         + "{"
         + "\"value\":\"bjensen@example.com\","
         + "\"type\":\"work\","
         + "\"primary\":true"
         + "},"
         + "{"
         + "\"value\":\"babs@jensen.org\","
         + "\"type\":\"home\","
         + "\"primary\":false"
         + "}"
         + "],"
         + "\"phoneNumbers\":["
         + "{"
         + "\"value\":\"555-555-5555\","
         + "\"type\":\"work\""
         + "}"
         + "],"
         + "\"userType\":\"Employee\","
         + "\"title\":\"Tour Guide\""
         + "}";

      dataJSON = "{\"subject\":{"
         + "\"uniqueid\":\"jbauer\","
         + "\"lastname\":\"Bauer\","
         + "\"forgottenPasswordAnswers\":[\"bauer\",\"Los Angles\",\"1234\"],"
         + "\"title\":\"Agent\","
         + "\"forgottenPasswordQuestions\":[\"Mothers Maiden Name\",\"City you were born\",\"Last 4 digits of Frequent Flyer\"],"
         + "\"firstname\":\"Jack\","
         + "\"telephone\":\"secret\","
         + "\"email\":\"jbauer@ctu.gov\","
         + "\"manager\":\"pres\","
         + "\"roles\":[\"agent\"],"
         + "\"fullname\":\"Jack Bauer\","
         + "\"organization\":\"ctu\""
         + "}}";

      /*
       * multi-valued attributes =
       * emails
       * phoneNumbers
       */
//      scimXML =
//         "<User xmlns=\"urn:scim:schemas:core:1.0\">"
//         + "<id>2819c223-7f76-453a-919d-413861904646</id>"
//         + "<userName>bjensen@example.com</userName>"
//         + "<name>"
//         + "<familyName>Jensen</familyName>"
//         + "<givenName>Barbara</givenName>"
//         + "</name>"
//         + "<emails>"
//         + "<email>"
//         + "<value>bjensen@example.com</value>"
//         + "<type>work</type>"
//         + "<primary type=\"boolean\">true</primary>"
//         + "</email>"
//         + "<email>"
//         + "<value>babs@jensen.com</value>"
//         + "<type>home</type>"
//         + "</email>"
//         + "</emails>"
//         + "<phoneNumbers>"
//         + "<phoneNumber>"
//         + "<value>555-555-5555</value>"
//         + "<type>work</type>"
//         + "</phoneNumber>"
//         + "</phoneNumbers>"
//         + "<userType>Employee</userType>"
//         + "<title>Tour Guide</title>"
//         + "</User>";

      dataXML =
         "<subject>"
         + "<uniqueid type=\"string\">cuser</uniqueid>"
         + "<attributes>"
         + "<empno type=\"Integer\">123456</empno>"
         + "<active type=\"boolean\">true</active>"
         + "<lastname type=\"string\">User</lastname>"
         + "<forgottenPasswordAnswers type=\"string\">"
         + "<value></value>"
         + "<value></value>"
         + "<value></value>"
         + "</forgottenPasswordAnswers>"
         + "<title type=\"string\">Jersey (JSR-311) Expert</title>"
         + "<forgottenPasswordQuestions type=\"string\">"
         + "<value>Mothers Maiden Name</value>"
         + "<value>City you were born</value>"
         + "<value>Last 4 digits of Frequent Flyer</value>"
         + "</forgottenPasswordQuestions>"
         + "<firstname type=\"string\">Curl</firstname>"
         + "<telephone type=\"string\">123-456-7890</telephone>"
         + "<email type=\"string\">restful@openptk.org</email>"
         + "<manager type=\"string\">owner</manager>"
         + "<roles type=\"string\">"
         + "<value>user</value>"
         + "<value>admin</value>"
         + "<value>operator</value>"
         + "</roles>"
         + "<fullname type=\"string\">Curl User</fullname>"
         + "<organization type=\"string\">openptk</organization>"
         + "<manager type=\"Long\">42</manager>"
         + "</attributes>"
         + "</subject>";

      /*
       * JSON
       */
      json = new JsonConverter();

      /*
       * XML
       */
//      xml = new XmlConverter();
//      
//      elem = new Element();
//      elem.setUniqueId("forgottenPasswordQuestions");
//      elem.setProperty(StructureIF.NAME_MULTIVALUE, "true");
//      xml.setStructInfo("forgottenPasswordQuestions", elem);
//
//      elem = new Element();
//      elem.setUniqueId("forgottenPasswordAnswers");
//      elem.setProperty(StructureIF.NAME_MULTIVALUE, "true");
//      xml.setStructInfo("forgottenPasswordAnswers", elem);
//
//      elem = new Element();
//      elem.setUniqueId("roles");
//      elem.setProperty(StructureIF.NAME_MULTIVALUE, "true");
//      xml.setStructInfo("roles", elem);

      try
      {
         struct = json.decode(dataJSON); // scimJSON and dataJSON
//         struct = xml.decode(dataXML);
         if (struct != null)
         {
            System.out.println("\nOrig JSON Source:\n" + dataJSON + "\n\n");
            System.out.println("Structure:\n" + struct.toString());
            System.out.println("\nCreated JSON Source:\n" + json.encode(struct) + "\n\n");
//            System.out.println("\nOrig XML Source:\n" + dataXML + "\n\n");
//            System.out.println("Structure:\n" + struct.toString());
//            System.out.println("\nCreated XML Source:\n" + xml.encode(struct) + "\n\n");
         }
      }
      catch (ConverterException ex)
      {
         Logger.logError(ex.getMessage());
      }

      return;
   }
}
