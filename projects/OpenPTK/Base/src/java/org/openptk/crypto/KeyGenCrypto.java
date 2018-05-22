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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.openptk.exception.CryptoException;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class KeyGenCrypto extends Crypto
//===================================================================
{

   private String CLASS_NAME = this.getClass().getSimpleName();

   /**
    *
    * @param keygen String name of the key generator instance
    * @param transformation String type of transformation
    * @throws CryptoException
    */
   //----------------------------------------------------------------
   public KeyGenCrypto(final String keygen, final String transformation) throws CryptoException
   //----------------------------------------------------------------
   {
      /*
       * Transformation:  (cipher/mode/padding)
       * cipher = AES, Advanced Encryption Standard algorithm
       * mode = ECB, Elctronic Code Book
       * padding = PKCS5Padding, PKCS #5 Padding
       */

      super();

      String METHOD_NAME = CLASS_NAME + ":KeyGenCrypto(): ";
      KeyGenerator keyGen = null;
      SecretKey secretKey = null;

      /*
       * Generate a key
       */

      try
      {
         keyGen = KeyGenerator.getInstance(keygen);
      }
      catch (NoSuchAlgorithmException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }

      secretKey = keyGen.generateKey();

      /*
       * Create the cipher
       */

      try
      {
         _decryptCipher = Cipher.getInstance(transformation);
         _encryptCipher = Cipher.getInstance(transformation);
      }
      catch (NoSuchAlgorithmException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }
      catch (NoSuchPaddingException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }

      /*
       * Initialize the cipher
       */

      try
      {
         _decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
         _encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
      }
      catch (InvalidKeyException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }

      return;
   }

}
