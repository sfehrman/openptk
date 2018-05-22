/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009-2010 Sun Microsystems, Inc.
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
 * Portions Copyright 2011-2012, Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.crypto;

import java.io.UnsupportedEncodingException;

import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import org.openptk.exception.CryptoException;

import org.apache.commons.codec.binary.Base64;

/**
 * Abstract class that implements the CryptoIF interface
 * Provides foundation "common" methods.
 * 
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Crypto implements CryptoIF
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _id = null;
   protected String STRING_ENCODING = "UTF8";
   protected Cipher _encryptCipher = null;
   protected Cipher _decryptCipher = null;
   protected Base64 _encoder = null;
   protected Base64 _decoder = null;

  /**
   * initialize the _encoder and _decoder.
   */
   //----------------------------------------------------------------
   public Crypto()
   //----------------------------------------------------------------
   {
      _decoder = new Base64();
      _encoder = new Base64();
      return;
   }


   /**
    * @param args optional arguments
    */
   //----------------------------------------------------------------
   public static void main(String[] args) // throws CryptoException
   //----------------------------------------------------------------
   {
      String impl[] = null;

      impl = Crypto.getImplementations();

      if (impl != null)
      {
         for (int i = 0; i < impl.length; i++)
         {
            System.out.print(impl[i] + "\n");
         }
      }
      return;
   }

 
   /**
    * @return array of Strings, implementations
    */
   //----------------------------------------------------------------
   public static String[] getImplementations()
   //----------------------------------------------------------------
   {
      double version = 0.0;
      String strArray[] = null;
      String name = null;
      String algorithm = null;
      String type = null;
      String result = null;
      String classname = null;
      List<String> results = null;
      Set<Service> services = null;
      Iterator<Service> iter = null;
      Provider[] providers = null;
      Provider provider = null;
      Service service = null;

      results = new LinkedList<String>();

      providers = Security.getProviders();
      if (providers != null)
      {
         results.add("Name : Version : Type : Algorithm : Classname");
         for (int i = 0; i < providers.length; i++)
         {
            name = null;
            provider = providers[i];
            if (provider != null)
            {
               name = provider.getName();
               version = provider.getVersion();
               services = provider.getServices();
               if (services != null)
               {
                  iter = services.iterator();
                  while (iter.hasNext())
                  {
                     algorithm = null;
                     type = null;
                     classname = null;
                     service = iter.next();
                     if (service != null)
                     {
                        algorithm = service.getAlgorithm();
                        type = service.getType();
                        classname = service.getClassName();
                        if (type != null && algorithm != null)
                        {
                           result = name + " : " + version + " : " + type +
                              " : " + algorithm + " : " + classname;
                           results.add(result);
                        }
                     }
                  }
               }
            }
         }
      }
      strArray = results.toArray(new String[results.size()]);
      return strArray;
   }

   /**
    * @param input the clear text value
    * @return string the encrypted value
    * @throws CryptoException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String encrypt(final String input) throws CryptoException
   //----------------------------------------------------------------
   {
      byte[] inBytes = null;
      byte[] outBytes = null;
      String METHOD_NAME = CLASS_NAME + ":encrypt(): ";
      String output = null;

      try
      {
         inBytes = input.getBytes(STRING_ENCODING);
      }
      catch (UnsupportedEncodingException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }

      try
      {
         outBytes = _encryptCipher.doFinal(inBytes);
      }
      catch (IllegalBlockSizeException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }
      catch (BadPaddingException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }

      output = _encoder.encodeToString(outBytes);

      return output;
   }

   /**
    * @param input the encrypted value
    * @return string the clear text value
    * @throws CryptoException
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized String decrypt(final String input) throws CryptoException
   //----------------------------------------------------------------
   {
      byte[] inBytes = null;
      byte[] outBytes = null;
      String METHOD_NAME = CLASS_NAME + ":decrypt(): ";
      String output = null;

      inBytes = _decoder.decode(input);

      try
      {
         outBytes = _decryptCipher.doFinal(inBytes);
      }
      catch (IllegalBlockSizeException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }
      catch (BadPaddingException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }

      try
      {
         output = new String(outBytes, STRING_ENCODING);
      }
      catch (UnsupportedEncodingException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }

      return output;
   }

   /**
    * @return String
    */
   //----------------------------------------------------------------
   @Override
   public final String getId()
   //----------------------------------------------------------------
   {
      String str = null;

      str = _id;

      return str; // return a copy of the value
   }

   /**
    * @param id
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setId(final String id)
   //----------------------------------------------------------------
   {
      _id = id;
      return;
   }
}
