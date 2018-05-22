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

//===================================================================
public abstract class Argument implements ArgumentIF
//===================================================================
{
   private boolean _isRequired = false;
   private ArgumentType _type = ArgumentType.ATTRIBUTE;
   private String _name = null;
   private String _value = null;


   /**
    * @param name
    * @param type
    * @param value
    */
   //----------------------------------------------------------------
   public Argument(String name, ArgumentType type, String value)
   //----------------------------------------------------------------
   {
      _name = name;
      _type = type;
      _value = value;
      
      return;
   }


   /**
    * @param name
    * @param type
    * @param value
    */
   //----------------------------------------------------------------
   public Argument(String name, String type, String value)
   //----------------------------------------------------------------
   {
      ArgumentType[] typeArray = null;

      typeArray = ArgumentType.values();

      for (int i = 0; i < typeArray.length; i++)
      {
         if (type.equalsIgnoreCase(typeArray[i].toString()))
         {
            this._type = typeArray[i];
         }
      }

      _name = name;
      _value = value;

      return;
   }


   /**
    * @param argument
    */
   //----------------------------------------------------------------
   public Argument(ArgumentIF argument)
   //----------------------------------------------------------------
   {
      _type = argument.getType();
      _name = argument.getName();
      _value = argument.getValue();
      _isRequired = argument.isRequired();

      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public abstract ArgumentIF copy();
   //----------------------------------------------------------------


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String getTypeAsString()
   //----------------------------------------------------------------
   {
      return _type.toString(); 
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final ArgumentType getType()
   //----------------------------------------------------------------
   {
      ArgumentType type = null;
      type = _type;
      return type;
   }


   /**
    * @param type
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setType(ArgumentType type)
   //----------------------------------------------------------------
   {
      _type = type;
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String getValue()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_value != null)
      {
         str = new String(_value); // always return a copy
      }

      return str;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String getName()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_name != null)
      {
         str = new String(_name); // always return a copy
      }

      return str;
   }


   /**
    * @param str
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setValue(String str)
   //----------------------------------------------------------------
   {
      _value = str;
      return;
   }


   /**
    * @param bool
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setRequired(boolean bool)
   //----------------------------------------------------------------
   {
      _isRequired = bool;
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isRequired()
   //----------------------------------------------------------------
   {
      return _isRequired;
   }
}
