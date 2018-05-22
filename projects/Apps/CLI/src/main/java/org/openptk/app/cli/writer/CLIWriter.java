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

package org.openptk.app.cli.writer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.openptk.api.Output;

/**
 *
 * @author Tery Sigle
 */
public abstract class CLIWriter implements CLIWriterIF 
{
   final static private String ELLIPSE = " ...";
   
   protected PrintWriter _out = null;

   // Some output comes from OpenPTK Output objects
   protected Output _output = null;

   // Others come from manufactured lists (i.e. show)
   protected List<String> _metaData = null;
   protected int[] _metaDataSize = null;
   protected List<List<String>> _data = null;

   /**
    * @param md
    */
   public void setMetaData(List<String> md)
   {
      int i = 0;
      
      _metaData = md;
      _metaDataSize = new int[md.size()];

      for (String meta : _metaData)
      {
         _metaDataSize[i] = meta.length();
         i++;
      }
      
      return;
   }

   /**
    * @param row
    */
   public void addRow(List<String> row)
   {
      int i = 0;

      // If this is the first row, then create data list
      if (_data == null)
      {
         _data = new ArrayList<List<String>>();
      }

      // Add the row to the current set of data
      _data.add(row);
      
      // Adjust the MetaDataSize to the max of current setting and this data
      //
      // TODO - Add a variable to allow setting max size to output

      for (String value : row)
      {
         _metaDataSize[i] = Math.max(_metaDataSize[i], value.length());
         i++;
      }
      
      return;
   }

   /**
    * @param str
    * @param len
    * @return
    */
   //----------------------------------------------------------------
   protected String leftPadData(String str, int maxLen)
   //----------------------------------------------------------------
   {
      return padData(str, maxLen, true);
   }

   /**
    * @param str
    * @param len
    * @return
    */
   //----------------------------------------------------------------
   protected String rightPadData(String str, int maxLen)
   //----------------------------------------------------------------
   {
      return padData(str, maxLen, false);
   }

   /**
    * @param str
    * @param len
    * @return
    */
   //----------------------------------------------------------------
   private String padData(String str, int maxLen, boolean leftPad)
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();
      int strLen = 0;

      if (str == null)
      {
         str = "";
      }

      strLen = str.length();

      // If the string is shorter than the max length, then pad it with
      // spaces
      if (strLen < maxLen)
      {
         if (!leftPad)
         {
            buf.append(str);
         }

         for (int i = 0; i < (maxLen - strLen); i++)
         {
            buf.append(" ");
         }

         if (leftPad)
         {
            buf.append(str);
         }
      }
      else if (strLen == maxLen)
      {
         buf.append(str);
      }

      return buf.toString();
   }

   /**
    * @param output
    */
   public void setOutput (Output output)
   {
      _output = output;
      return;
   }

   public void write()
   {
      write (true);
   }
}
