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
 * Portions Copyright 2011-2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */

package org.openptk.app.cli.writer;

import java.io.PrintWriter;
import java.util.List;

import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;

/**
 *
 * Syntax:
 *        attribute_name : attribute_value
 *
 *  Example:
 *       uniqueid : jsmith
 *      firstname : John
 *       lastname : Smith
 *          email : john.smith@openptk.org.
 *
 * @author Tery Sigle
 */
public class RowWriter extends CLIWriter implements CLIWriterIF {

   private static final String DELIMETER = " : ";

   /**
    * @param out
    */
   public RowWriter (PrintWriter out)
   {
      _out = out;
      return;
   }

   public void write(boolean showRowTotal)
   {

      // print a newline before printing all results
      _out.println("");

       if (_output != null)
      {
         this.processOutput();
      }
      else
      {
         this.processLists(showRowTotal);
      }

      // print a newline after printing all results
      _out.println("");
   }

   private void processLists(boolean showRowTotal)
   {
      if (this._data != null && this._data.size() > 0)
      {

         /*
          * Print the data:
          */
         for (List<String> row : this._data)
         {
            int i = 0;

            for (String value : row)
            {
               _out.print(this._metaData.get(i));
               _out.print(DELIMETER);
               _out.print(value);
               _out.println("");
            }
            _out.println("");
         }

         if (showRowTotal)
         {
            _out.println("");

            _out.println("Records: " + this._data.size());
         }
      }
      else
      {
         _out.println("No records returned.");
      }


      return;
   }

   private void processOutput ()
   {
      int longestAttrName = 0;
      String uidName = "uniqueid";
      ElementIF result = null;
      List<ElementIF> results = null;
      AttributeIF attr = null;
      String[] attrNames = null;
      String uniqueId = null;
      String value = null;
      StringBuilder str = null;
      int recsReturned = 0;

      recsReturned = _output.getResultsSize();

      if (recsReturned > 0)
      {
         /*
          * This section reads data to obtain the column / attribute names
          * and the length of the longest attribute value (or attribute name)
          */

         results = _output.getResults();

         result = results.get(0);
         
         attrNames = result.getAttributeNames();

         for (int i = 0; i < attrNames.length; i++)
         {
            // Find the longest attribute name

            longestAttrName = Math.max(longestAttrName, attrNames[i].length());
         }

         /*
          * Print out the data
          */

         str = new StringBuilder();

         for (ElementIF row : results)
         {
            /*
             * Get / display the uniqueid in the first column
             */

            uniqueId = row.getUniqueId().toString();

            str.append(leftPadData(uidName, longestAttrName));
            str.append(DELIMETER);
            str.append(uniqueId);
            str.append("\n");

            for (int i = 0; i < attrNames.length; i++)
            {
               value = "";
               attr = row.getAttribute(attrNames[i]);

               if (attr != null)
               {
                  if (attr.getValue() != null)
                  {
                     value = attr.getValueAsString();
                  }
                  else
                  {
                     value = "";
                  }
               }
               else
               {
                  value = "";
               }

               str.append(leftPadData(attrNames[i], longestAttrName));
               str.append(DELIMETER);
               str.append(value);
               str.append("\n");
            }
            str.append("\n");
         }
         _out.print(str.toString());

         _out.println("Records: " + recsReturned);
      }
      else
      {
         _out.println("No records returned.");
      }
      return;
   }
}
