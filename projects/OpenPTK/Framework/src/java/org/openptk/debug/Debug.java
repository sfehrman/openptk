/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2009 Sun Microsystems, Inc.
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

package org.openptk.debug;

import java.util.Properties;
import java.util.TreeSet;


//===================================================================
public abstract class Debug implements DebugIF
//===================================================================
{
   protected String _extIndent = "";
   
   //----------------------------------------------------------------
   public Debug()  // output to Console
   //----------------------------------------------------------------
   {
      return;
   }
   

   /**
    * @param obj
    * @param callerId
    */
   //----------------------------------------------------------------
   @Override
   public abstract void logData(final Object obj, final String callerId);
   //----------------------------------------------------------------
   

   /**
    * @param obj
    * @param callerId
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public abstract String getData(final Object obj, final String callerId);
   //----------------------------------------------------------------
   
   /**
    * Returns the keys of a Properties object and returns them in a sorted 
    * set
    * 
    * @param props
    * @return 
    */
   public TreeSet<String> getSortedKeys(final Properties props)
   {
      /*
       * Sort the properties by adding them to a TreeSet
       */
      
      TreeSet<String> sortedSet = new TreeSet<String>();

      if (props != null)
      {
         for (Object o : props.keySet())
         {
            sortedSet.add((String) o);
         }
      }
      
      return sortedSet;
   }
}
