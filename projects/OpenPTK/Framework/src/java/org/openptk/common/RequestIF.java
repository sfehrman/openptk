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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.common;

import org.openptk.api.Query;
import org.openptk.spi.ServiceIF;

//===================================================================
public interface RequestIF extends ComponentIF
//===================================================================
{
   /**
    * @param resource
    */
   public void setResource(ComponentIF resource);

   /**
    * @param query
    */
   public void setQuery(Query query);

   /**
    * @param subject
    */
   public void setSubject(ComponentIF subject);

   /**
    * @param operation
    */
   public void setOperation(Operation operation);

   /**
    * @param operation
    */
   public void setOperation(String operation);

   /**
    * @return
    */
   public Operation getOperation();

   /**
    * @return
    */
   public String getOperationAsString();

   /**
    * @return
    */
   public ComponentIF getResource();

   /**
    * @return
    */
   public ComponentIF getSubject();

   /**
    * @return
    */
   public Query getQuery();

   /**
    * @param service
    */
   public void setService(ServiceIF service);

   /**
    * @return
    */
   public ServiceIF getService();

   /**
    * @param key
    */
   public void setKey(String key);

   /**
    * @return
    */
   public String getKey();

   public void addAttempt();

   public int getAttempts();
}
