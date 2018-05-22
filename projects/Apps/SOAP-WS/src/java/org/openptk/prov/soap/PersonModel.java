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

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class PersonModel
//===================================================================
{
   private String _uniqueid = null;
   private String _firstname = null;
   private String _lastname = null;
   private String _title = null;
   private String _email = null;
   private String _fullname = null;
   private String _manager = null;
   private String _organization = null;
   private String _password = null;
   private String _telephone = null;

   public String getTelephone()
   {
      return _telephone;
   }

   public void setTelephone(String telephone)
   {
      _telephone = telephone;
      return;
   }

   public String getPassword()
   {
      return _password;
   }

   public void setPassword(String password)
   {
      _password = password;
      return;
   }

   public String getOrganization()
   {
      return _organization;
   }

   public void setOrganization(String organization)
   {
      _organization = organization;
      return;
   }

   public String getManager()
   {
      return _manager;
   }

   public void setManager(String manager)
   {
      _manager = manager;
      return;
   }

   public String getFullname()
   {
      return _fullname;
   }

   public void setFullname(String fullname)
   {
      _fullname = fullname;
      return;
   }

   public String getEmail()
   {
      return _email;
   }

   public void setEmail(String email)
   {
      _email = email;
      return;
   }

   public String getTitle()
   {
      return _title;
   }

   public void setTitle(String title)
   {
      _title = title;
      return;
   }

   public String getLastname()
   {
      return _lastname;
   }

   public void setLastname(String lastname)
   {
      _lastname = lastname;
      return;
   }

   public String getFirstname()
   {
      return _firstname;
   }

   public void setFirstname(String firstname)
   {
      _firstname = firstname;
      return;
   }

   public String getUniqueid()
   {
      return _uniqueid;
   }

   public void setUniqueid(String uniqueid)
   {
      _uniqueid = uniqueid;
      return;
   }

}
