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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.common;

import org.openptk.api.Query;
import org.openptk.spi.ServiceIF;

//===================================================================
public class Request extends Component implements RequestIF
//===================================================================
{

   private ComponentIF _resource = null;
   private ComponentIF _subject = null;
   private Query _query = null;
   private Operation _operation = Operation.READ;
   private ServiceIF _service = null;
   private String _key = null;
   private int _attempts = 0;
   
   //----------------------------------------------------------------
   public Request()
   //----------------------------------------------------------------
   {
      this.setCategory(Category.REQUEST);
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final ComponentIF getResource()
   //----------------------------------------------------------------
   {
      return _resource;
   }


   /**
    * @param resource
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setResource(final ComponentIF resource)
   //----------------------------------------------------------------
   {
      _resource = resource;
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Query getQuery()
   //----------------------------------------------------------------
   {
      return _query;
   }


   /**
    * @param query
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setQuery(final Query query)
   //----------------------------------------------------------------
   {
      _query = query;
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final ComponentIF getSubject()
   //----------------------------------------------------------------
   {
      return _subject;
   }


   /**
    * @param subject
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setSubject(final ComponentIF subject)
   //----------------------------------------------------------------
   {
      _subject = subject;
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Operation getOperation()
   //----------------------------------------------------------------
   {
      return _operation;
   }


   /**
    * @param operation
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setOperation(final Operation operation)
   //----------------------------------------------------------------
   {
      _operation = operation;
      return;
   }


   /**
    * @param str
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setOperation(final String str)
   //----------------------------------------------------------------
   {
      /*
       * Convert the operation (String) which is based on the Operation
       */

      Operation[] operArray = null;

      operArray = Operation.values();

      if (operArray != null)
      {
         for (int i = 0; i < operArray.length; i++)
         {
            if (str.equalsIgnoreCase(operArray[i].toString()))
            {
               _operation = operArray[i];
               break;
            }
         }
      }

      return;
   }
   

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String getOperationAsString()
   //----------------------------------------------------------------
   {
      return (_operation.toString());
   }


   /**
    * @param service
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setService(final ServiceIF service)
   //----------------------------------------------------------------
   {
      _service = service;
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final ServiceIF getService()
   //----------------------------------------------------------------
   {
      return _service;
   }


   /**
    * @param key
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setKey(final String key)
   //----------------------------------------------------------------
   {
      _key = key;
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String getKey()
   //----------------------------------------------------------------
   {
      return _key;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void addAttempt()
   //----------------------------------------------------------------
   {
      _attempts++;
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final int getAttempts()
   //----------------------------------------------------------------
   {
      int i = 0;

      i = _attempts;

      return i;
   }
}
