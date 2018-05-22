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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
@WebService()
public class Person
//===================================================================
{
   private final PersonController _controller = new PersonController();

   @WebMethod(operationName = "create")
   public String create(@WebParam(name = "person") PersonModel person)
   {
      return _controller.doCreate(person);
   }

   @WebMethod(operationName = "read")
   public PersonModel read(@WebParam(name = "uniqueid") String uniqueid)
   {
      return _controller.doRead(uniqueid);
   }

   @WebMethod(operationName = "update")
   public String update(@WebParam(name = "person") PersonModel person)
   {
      return _controller.doUpdate(person);
   }

   @WebMethod(operationName = "delete")
   public String delete(@WebParam(name = "uniqueid") String uniqueid)
   {
      return _controller.doDelete(uniqueid);
   }

   @WebMethod(operationName = "search")
   public PersonModel[] search(@WebParam(name = "search") String search)
   {
      return _controller.doSearch(search);
   }
}
