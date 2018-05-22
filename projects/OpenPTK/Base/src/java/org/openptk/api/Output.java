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
package org.openptk.api;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The Output object contains the State/Status and Results of an Operation.
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class Output extends Element
//===================================================================
{
   private List<ElementIF> _results = null;

   /**
    * Creates a new Output object.
    */
   //----------------------------------------------------------------
   public Output()
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   /**
    * Creates a new Output that is a copy of the specified Output.
    *
    * @param output
    */
   //----------------------------------------------------------------
   public Output(final Output output)
   //----------------------------------------------------------------
   {
      super(output);

      _results = output.getResults();

      return;
   }

   /**
    * Creates a "deep" copy of the Output.
    *
    * @return Output the new Output (a copy)
    */
   //----------------------------------------------------------------
   @Override
   public Output copy()
   //----------------------------------------------------------------
   {
      return new Output(this);
   }

   /**
    * Get the List of Results
    *
    * A List of ElementIF objects will be returned. Notice: the List and
    * its Elements are a "copy" of the internal List/Elements.
    *
    * @return List the list of Elements
    */
   //----------------------------------------------------------------
   public final synchronized List<ElementIF> getResults()
   //----------------------------------------------------------------
   {
      ElementIF elem = null;
      List<ElementIF> list = null;
      Iterator<ElementIF> iter = null;

      list = new LinkedList<ElementIF>();

      if (_results != null && !_results.isEmpty())
      {
         iter = _results.iterator();
         while (iter.hasNext())
         {
            elem = iter.next();
            if (elem != null)
            {
               list.add(elem.copy());
            }
         }
      }

      return list;
   }

   /**
    * Get the size (number) of the results.
    * 
    * @return int the number of results that are available
    */
   //----------------------------------------------------------------
   public final synchronized int getResultsSize()
   //----------------------------------------------------------------
   {
      int i = 0;

      if (_results != null && !_results.isEmpty())
      {
         i = _results.size();
      }

      return i;
   }

   /**
    * Add a Result, (ElementIF object) to the Output.
    * 
    * @param result a result object from an operation
    */
   //----------------------------------------------------------------
   public final synchronized void addResult(final ElementIF result)
   //----------------------------------------------------------------
   {
      if (_results == null)
      {
         _results = new LinkedList<ElementIF>();
      }

      _results.add(result);

      return;
   }
}
