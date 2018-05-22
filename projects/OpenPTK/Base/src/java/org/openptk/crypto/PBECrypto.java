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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.openptk.exception.CryptoException;

/**
 * Class extends Crypto (abstract) class and provides a
 * Password Based Encryption (PBE) implementation.
 * Uses a pre-defined "salt"
 * 
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class PBECrypto extends Crypto
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * Create a CryptoIF instance that uses Password Based Encryption.
    * 
    * @param passPhrase String used for encryption
    * @param cipher String of a valid cipher implementation
    * @throws CryptoException
    */
   //----------------------------------------------------------------
   public PBECrypto(final String passPhrase, final String cipher) throws CryptoException
   //----------------------------------------------------------------
   {
      super();

      byte[] salt =
      {
         0x53, 0x46, 0x65, 0x68, 0x72, 0x6d, 0x61, 0x6e
      };
      int iterCnt = 19;
      String METHOD_NAME = CLASS_NAME + ":PBECrypto(phrase,cipher): ";
      String algorithm = null;
      KeySpec spec = null;
      SecretKey key = null;
      SecretKeyFactory factory = null;
      AlgorithmParameterSpec param = null;

      try
      {
         factory = SecretKeyFactory.getInstance(cipher);
      }
      catch (NoSuchAlgorithmException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }

      spec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterCnt);

      try
      {
         key = factory.generateSecret(spec);
      }
      catch (InvalidKeySpecException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }

      algorithm = key.getAlgorithm();

      try
      {
         _encryptCipher = Cipher.getInstance(algorithm);
         _decryptCipher = Cipher.getInstance(algorithm);
      }
      catch (NoSuchAlgorithmException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }
      catch (NoSuchPaddingException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }

      param = new PBEParameterSpec(salt, iterCnt);

      try
      {
         _encryptCipher.init(Cipher.ENCRYPT_MODE, key, param);
         _decryptCipher.init(Cipher.DECRYPT_MODE, key, param);
      }
      catch (InvalidKeyException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }
      catch (InvalidAlgorithmParameterException ex)
      {
         throw new CryptoException(METHOD_NAME + ex);
      }

      return;
   }
}
