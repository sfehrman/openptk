/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2010 Sun Microsystems, Inc.
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

package org.openptk.prov.soap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Opcode;
import org.openptk.api.Output;
import org.openptk.api.Query;
import org.openptk.connection.ConnectionIF;
import org.openptk.connection.Setup;
import org.openptk.connection.SetupIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class PersonController
//===================================================================
{
   private static final String ATTR_EMAIL = "email";
   private static final String ATTR_FIRSTNAME = "firstname";
   private static final String ATTR_FULLNAME = "fullname";
   private static final String ATTR_LASTNAME = "lastname";
   private static final String ATTR_MANAGER = "manager";
   private static final String ATTR_ORGANIZATION = "organization";
   private static final String ATTR_PASSWORD = "password";
   private static final String ATTR_TELEPHONE = "telephone";
   private static final String ATTR_TITLE = "title";
   private static final String ATTR_UNIQUEID = "uniqueid";
   private final String CLASS = this.getClass().getSimpleName();
   private final Logger _logger = Logger.getLogger(CLASS);
   private SetupIF _setup = null;
   private ConnectionIF _connection = null;

   //----------------------------------------------------------------
   public PersonController()
   //----------------------------------------------------------------
   {
      try
      {
         _setup = new Setup("openptk_client");
         _connection = _setup.getConnection();
      }
      catch (Exception ex)
      {
         _logger.log(Level.SEVERE, null, ex);
      }

      return;
   }

   //----------------------------------------------------------------
   public String doCreate(PersonModel person)
   //----------------------------------------------------------------
   {
      Input input = null;
      Output output = null;

      input = this.personToInput(person);

      output = this.execute(Opcode.CREATE, input);

      return output.getStatus();
   }

   //----------------------------------------------------------------
   public PersonModel doRead(String uniqueid)
   //----------------------------------------------------------------
   {
      Input input = new Input();
      Output output = null;
      PersonModel person = null;

      input.setUniqueId(uniqueid);

      output = this.execute(Opcode.READ, input);

      person = this.outputToPerson(output);

      return person;
   }

   //----------------------------------------------------------------
   public String doUpdate(PersonModel person)
   //----------------------------------------------------------------
   {
      Input input = null;
      Output output = null;

      input = this.personToInput(person);

      output = this.execute(Opcode.UPDATE, input);

      return output.getStatus();
   }

   //----------------------------------------------------------------
   public String doDelete(String uniqueid)
   //----------------------------------------------------------------
   {
      Input input = new Input();
      Output output = null;

      input.setUniqueId(uniqueid);

      output = this.execute(Opcode.DELETE, input);

      return output.getStatus();
   }

   //----------------------------------------------------------------
   public PersonModel[] doSearch(String search)
   //----------------------------------------------------------------
   {
      Input input = new Input();
      Output output = null;
      PersonModel[] people = null;

      input.setQuery(new Query(Query.Type.NOOPERATOR, "search", search));

      output = this.execute(Opcode.SEARCH, input);

      people = this.outputToPeople(output);

      return people;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private Output execute(Opcode opcode, Input input)
   //----------------------------------------------------------------
   {
      Output output = null;

      try
      {
         output = _connection.execute(opcode, input);
      }
      catch (Exception ex)
      {
         _logger.log(Level.SEVERE, null, ex);
      }

      return output;
   }

   //----------------------------------------------------------------
   private Input personToInput(PersonModel person)
   //----------------------------------------------------------------
   {
      Input input = new Input();

      input.setUniqueId(person.getUniqueid());

      this.updateInput(input, ATTR_FIRSTNAME, person.getFirstname());
      this.updateInput(input, ATTR_LASTNAME, person.getLastname());
      this.updateInput(input, ATTR_TELEPHONE, person.getTelephone());
      this.updateInput(input, ATTR_TITLE, person.getTitle());
      this.updateInput(input, ATTR_EMAIL, person.getEmail());
      this.updateInput(input, ATTR_FULLNAME, person.getFullname());
      this.updateInput(input, ATTR_MANAGER, person.getManager());
      this.updateInput(input, ATTR_ORGANIZATION, person.getOrganization());
      this.updateInput(input, ATTR_PASSWORD, person.getPassword());

      return input;
   }

   //----------------------------------------------------------------
   private void updateInput(Input input, String name, String value)
   //----------------------------------------------------------------
   {
      if (value != null && value.length() > 0)
      {
         input.addAttribute(name, value);
      }

      return;
   }

   //----------------------------------------------------------------
   private PersonModel outputToPerson(Output output)
   //----------------------------------------------------------------
   {
      PersonModel person = new PersonModel();
      ElementIF elem = null;
      Set<String> names = null;
      AttributeIF attribute = null;

      if (output != null)
      {
         if (output.getResultsSize() > 0)
         {
            elem = output.getResults().get(0);
            if (elem != null)
            {
               if (elem.getUniqueId() != null)
               {
                  person.setUniqueid(elem.getUniqueId().toString());
               }
               names = elem.getAttributes().keySet();
               for (String name : names)
               {
                  attribute = elem.getAttribute(name);
                  this.updatePerson(person, attribute);
               }
            }
         }
      }

      return person;
   }

   //----------------------------------------------------------------
   private void updatePerson(PersonModel person, AttributeIF attribute)
   //----------------------------------------------------------------
   {
      String name = null;
      String value = null;

      if (person != null && attribute != null)
      {
         name = attribute.getName();
         value = attribute.getValueAsString();
         if (name != null && value != null)
         {
            if (name.equals(ATTR_FIRSTNAME))
            {
               person.setFirstname(value);
            }
            else if (name.equals(ATTR_LASTNAME))
            {
               person.setLastname(value);
            }
            else if (name.equals(ATTR_EMAIL))
            {
               person.setEmail(value);
            }
            else if (name.equals(ATTR_TELEPHONE))
            {
               person.setTelephone(value);
            }
            else if (name.equals(ATTR_TITLE))
            {
               person.setTitle(value);
            }
            else if ( name.equals(ATTR_FULLNAME))
            {
               person.setFullname(value);
            }
            else if ( name.equals(ATTR_MANAGER))
            {
               person.setManager(value);
            }
            else if ( name.equals(ATTR_ORGANIZATION))
            {
               person.setOrganization(value);
            }
            else if (name.equals(ATTR_PASSWORD))
            {
               person.setPassword(value);
            }
         }
      }

      return;
   }

   //----------------------------------------------------------------
   private PersonModel[] outputToPeople(Output output)
   //----------------------------------------------------------------
   {
      List<ElementIF> list = null;
      List<PersonModel> people = new LinkedList<PersonModel>();
      Iterator<ElementIF> iter = null;
      Set<String> names = null;
      ElementIF elem = null;
      AttributeIF attribute = null;
      PersonModel person = null;

      if (output != null)
      {
         list = output.getResults();
         iter = list.iterator();

         while ( iter.hasNext())
         {
            elem = iter.next();
            if ( elem != null)
            {
               person = new PersonModel();
               if (elem.getUniqueId() != null)
               {
                  person.setUniqueid(elem.getUniqueId().toString());
               }
               names = elem.getAttributes().keySet();
               for (String name : names)
               {
                  attribute = elem.getAttribute(name);
                  this.updatePerson(person, attribute);
               }
               people.add(person);
            }
         }
      }

      return people.toArray(new PersonModel[people.size()]);
   }
}
