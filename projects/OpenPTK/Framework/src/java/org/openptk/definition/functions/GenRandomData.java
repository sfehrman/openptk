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
package org.openptk.definition.functions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openptk.api.DataType;
import org.openptk.common.AttrIF;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.exception.FunctionException;
import org.openptk.util.RandomData;

/**
 *
 * @author Terry Sigle
 */
//===================================================================
public class GenRandomData extends Function implements FunctionIF
//===================================================================
{

   private static final String LITERAL_ARG_SIZE = "size";
   private static final String LITERAL_ARG_CASE = "case";
   private static final String LITERAL_ARG_TYPE = "type";
   private static final String LITERAL_ARG_CHARSET = "charset";

   /**
    * @param context
    * @param key
    * @param mode
    * @param oper
    * @param args
    * @param attributes
    * @throws FunctionException
    */
   //----------------------------------------------------------------
   @Override
   public synchronized void execute(ContextIF context, String key, TaskMode mode,
                                    Operation oper, List<ArgumentIF> args, Map<String, AttrIF> attributes)
           throws FunctionException
   //----------------------------------------------------------------
   {
      Properties randomDataProps = new Properties();
      Integer dataSize = new Integer(8);
      String dataCase = null;
      String dataType = null;
      String dataCharset = null;
      AttrIF attribute = null;
      String randomData = null;
      ArgumentIF arg = null;
      Iterator<ArgumentIF> iter = null;
      String argName = null;

      // Get the arguments used to customize the type of random data to return
      if (args != null)
      {

         iter = args.iterator();

         while (iter.hasNext())
         {
            arg = iter.next();

            argName = arg.getName();

            if (argName != null && argName.length() > 0)
            {
               if (argName.equalsIgnoreCase(GenRandomData.LITERAL_ARG_SIZE))
               {
                  try
                  {
                     dataSize = new Integer(arg.getValue());
                  }
                  catch (NumberFormatException ex)
                  {
                     this.handleError("Argument 'size' is not a number");
                  }
               }
               else if (argName.equalsIgnoreCase(GenRandomData.LITERAL_ARG_CASE))
               {
                  dataCase = arg.getValue();

                  if ( !"both".equalsIgnoreCase(dataCase) &&
                          !"upper".equalsIgnoreCase(dataCase) &&
                          !"lower".equalsIgnoreCase(dataCase))
                  {
                     this.handleError("Argument 'case' can only be set to upper,lower,both");
                  }

                  randomDataProps.put(RandomData.PROP_CASE, dataCase);
               }
               else if (argName.equalsIgnoreCase(GenRandomData.LITERAL_ARG_TYPE))
               {
                  dataType = arg.getValue();

                  if ( !"both".equalsIgnoreCase(dataType) &&
                          !"numeric".equalsIgnoreCase(dataType) &&
                          !"alpha".equalsIgnoreCase(dataType))
                  {
                     this.handleError("Argument 'type' can only be set to numeric,alpha,both");
                  }

                  randomDataProps.put(RandomData.PROP_TYPE, dataType);
               }
               else if (argName.equalsIgnoreCase(GenRandomData.LITERAL_ARG_CHARSET))
               {
                  dataCharset = arg.getValue();
                  randomDataProps.put(RandomData.PROP_CHAR_SET, dataCharset);
               }
            }
         }
      }

      randomDataProps.put(RandomData.PROP_SIZE, dataSize);
      
      // Create the random data
      randomData = RandomData.getString(randomDataProps);

      // Get the Attribute we are transforming

      if (key != null)
      {
         attribute = attributes.get(key);
      }
      else
      {
         this.handleError("Attributes name (key) is null");
      }

      if (attribute == null)
      {
         this.handleError("Attribute '" + key + "' is null");
      }

      if (attribute.getType() == DataType.STRING)
      {
         attribute.setValue(randomData);
      } else if (attribute.getType() == DataType.INTEGER)
      {
         attribute.setValue(randomData);
      } else if (attribute.getType() == DataType.OBJECT)
      {
         attribute.setValue(randomData);
      }


      return;
   }

}
