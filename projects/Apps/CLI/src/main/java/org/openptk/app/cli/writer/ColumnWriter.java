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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;

/**
 *
 * @author Tery Sigle
 */
public class ColumnWriter extends CLIWriter implements CLIWriterIF {

   private static final String COLUMN_SPACING = "   ";
   private static final char HEADER_BAR = '-';
   private static final String UNIQUE_ID = "uniqueid";

   public ColumnWriter (PrintWriter out)
   {
      _out = out;
      return;
   }


   public void write(boolean showRowTotal)
   {

      // print a newline before printing all results
      _out.println("");


      /*
       * The following processes an output object that generally results in an
       * operation such as search
       */
      if (_output != null)
      {
         this.processOutput();
      }
      
      /*
       * The following processes a list of metaData and related data.
       * Currently this includes the ENV and SHOW commands.
       */
      if (_metaData != null && _data != null)
      {
         this.processData(showRowTotal);
      }

      // print a newline after printing all results
      _out.println("");
      
      return;
   }

   private void processData(boolean showRowTotal)
   {
      if (_data != null && _data.size() > 0)
      {
         /*
          * Print the header based on the metaData (i.e. column names)
          */
         int i = 0;
         String completeHeader = "";

         for (String header : _metaData)
         {
            /*
             * Print out the column name.  This will be padded with spaces
             * based on the metaDataSize, which was previous calculated when
             * interrogating the results of command.
             */
            completeHeader += rightPadData(header, _metaDataSize[i++]) + COLUMN_SPACING;
         }

         /*
          * Print the column names
          */
         _out.println(completeHeader);

         /*
          * Print out a line below the column header
          */
         for (i = 0; i < completeHeader.length(); i++)
         {
            _out.print(HEADER_BAR);
         }
         _out.println("");

         /*
          * Print the data:
          */
         for (List<String> row : _data)
         {
            i = 0;

            for (String value : row)
            {
               /*
                * Print out the actual data.  This is padded using the same
                * process as the column headers.
                */
               _out.print(rightPadData(value, _metaDataSize[i++]) + COLUMN_SPACING);
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
      List<ElementIF> results = null;
      Map<String, Integer> columnSize = null;
      String[] attrNames = null;
      char[] bar = null;
      int recsReturned = 0;
      String header = null;
      String[] rows = null;

      recsReturned = _output.getResultsSize();

      if (recsReturned > 0)
      {
         results = _output.getResults();

         /*
          * Get a mapping of column names to the max size of each column.
          * This is used to show a list of results that have nice spacing between
          * columns (both header and data)
          */

         columnSize = computeColumnSize(results);

         /*
          * Build a list of the attrNames that we'll use to build the header
          * and iterate over each result on the output.
          */
         
         if (columnSize.size() > 0)
         {
            attrNames = columnSize.keySet().toArray(new String[0]);
         }

         /*
          * Print the header and the bar after the header
          */

         header = getHeader(attrNames, columnSize);
         _out.println(header);

         bar = new char[header.length()];
         Arrays.fill(bar, HEADER_BAR);

         _out.println(bar);

         /*
          * Print out the data
          */

         rows = getDataRows(results, attrNames, columnSize);

         for (String row : rows)
         {
            _out.print(row);
         }

         /*
          * Print a footer bar
          */
         _out.println(bar);

         /*
          * And finally, print a number of records returned result.
          */
         _out.println("Records: " + recsReturned);
      }
      else
      {
         _out.println("No records returned.");
      }
      return;
   }

   private Map<String, Integer> computeColumnSize(List<ElementIF> results)
   {
      Map<String, Integer> columnSize = null;
      String[] attrNames = null;
      Map<String, AttributeIF> attrs = null;
      AttributeIF attr = null;
      int numResults = 0;
      Integer size = 0;
      String uniqueId = null;
      String value;
      Set<String> attrNamesSet = null;

      /*
       * Create a mapping of Column Name --> Column Size that will be used
       * to create a column size that fits all data (header or data)
       * comfortably on the output
       */

      columnSize = new LinkedHashMap<String, Integer>();

      /*
       * Manually add the "uniqueId" attribute along with it's size
       */

      columnSize.put(UNIQUE_ID, new Integer(UNIQUE_ID.length()));

      /*
       * This section reads data to obtain the column / attribute names
       * and the length of the longest attribute value (or attribute name)
       */

      for (ElementIF result : results)
      {
         if (result != null)
         {
            /*
             * If this is the first result processed, every attribute name
             * will be loaded in to the map.  We are checking if it is the 
             * first result, so we don't have to perfom this fo every result.
             * Just an optimization.
             * 
             */

            attrNames = result.getAttributeNames();

            if (0 == numResults++)
            {
               for (int i = 0; i < attrNames.length; i++)
               {
                  /*
                   *
                   * Load the attribute names (and their length) into a Map if that
                   * column isn't already in the map.
                   *
                   *
                   *   Map (attrName) --> size
                   */
                  if (!columnSize.containsKey(attrNames[i]))
                  {
                     columnSize.put(attrNames[i], new Integer(attrNames[i].length()));
                  }
               }
            }

            /*
             * Update the "max" length of the attribute value, if it's larger
             *
             * Do this for each uniqueId, since we currently always show that
             * and then for each data item.
             */

            uniqueId = result.getUniqueId().toString();

            if (uniqueId.length() > columnSize.get(UNIQUE_ID).intValue())
            {
               columnSize.put(UNIQUE_ID, new Integer(uniqueId.length()));
            }

            attrs = result.getAttributes();
            attrNamesSet = attrs.keySet();

            for (String attrName : attrNamesSet)
            {
               attr = attrs.get(attrName);
               if (attr != null) {
                  value = attr.getValueAsString();
                  size = columnSize.get(attr.getName());

                  if (size != null && value != null)
                  {
                     if (value.length() > size.intValue())
                     {
                        columnSize.put(attr.getName(), new Integer(value.length()));
                     }
                  }
               }
            }
         }
      }

      return columnSize;
   }

   private String getHeader(String[] columnNames, Map<String, Integer> columnSize)
   {
      Integer size = null;
      StringBuilder buf = new StringBuilder();

      for (String columnName : columnNames)
      {
         size = columnSize.get(columnName);
         buf.append(rightPadData(columnName, size.intValue()))
                 .append(COLUMN_SPACING);
      }

      return buf.toString();
   }

   private String[] getDataRows(
           List<ElementIF> results,
           String[] attrNames,
           Map<String, Integer> columnSize)
   {
      String [] rows = null;
      AttributeIF attr = null;
      String uniqueId = null;
      String value = null;
      Integer maxSize = null;
      StringBuilder buf = null;
      int rowCnt = 0;

      rows = new String[results.size()];

      for (ElementIF result : results) {
         buf = new StringBuilder();

         /*
          * Get / display the uniqueid in the first column
          */

         uniqueId = result.getUniqueId().toString();
         maxSize = columnSize.get(UNIQUE_ID);
         buf.append(rightPadData(uniqueId, maxSize.intValue())
                 + COLUMN_SPACING);

         for (int i = 1; i < attrNames.length; i++)
         {
            /*
             * Get each of the attributes
             * note: skips the first [0] attribute name in the array,
             * it's actually the uniqueid
             */

            attr = result.getAttribute(attrNames[i]);

            if (attr != null && attr.getValue() != null)
            {
               value = attr.getValueAsString();
            }
            else
            {
               value = "";
            }

            maxSize = columnSize.get(attrNames[i]);

            buf.append(this.rightPadData(value, maxSize.intValue())
                    + COLUMN_SPACING);
         }
         buf.append("\n");

         rows[rowCnt++] = buf.toString();
      }

      return rows;
   }
}
