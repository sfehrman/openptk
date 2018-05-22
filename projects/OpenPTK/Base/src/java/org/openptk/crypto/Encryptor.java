/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2011 Project OpenPTK
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

import java.util.HashMap;
import java.util.Map;

import org.openptk.exception.CryptoException;
import org.openptk.logging.Logger;

/*
 * Singleton class which will hold objects that are available globally to
 * the OpenPTK Framework.  The objects that are managed include:
 *
 *    Encryptor - Master Encryptor that can be used.
 */
//===================================================================
public class Encryptor
//===================================================================
{
   /*
    * These are string keys to set and use different cryptos for the Encryptor
    * singleton.
    */
   public static final String NETWORK = "network";
   public static final String CONFIG = "config";
   public static final String DEFAULT = "default";
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static Encryptor _encryptor = null;
   private static Map<String, CryptoIF> _cryptos = new HashMap<String, CryptoIF>();

   /*
    * Private constructor.  Doesn't allow anyone but itself construct a new
    * class.
    *
    * The constructor will create a default crypto (DESCrypto).  This
    * is created to have a default at a minimum should there be any problems
    * with the creation of a different crypto.
    */
   //----------------------------------------------------------------
   private Encryptor()
   //----------------------------------------------------------------
   {
      try
      {
         _cryptos.put(DEFAULT, new DESCrypto());
      }
      catch (CryptoException ex)
      {
         Logger.logError("Problems creating default Crypto - DESCrypto: " + ex.getMessage());
      }
      return;
   }

   /*
    * Since this is a singleton class, ensure that it cannot be cloned by
    * overridding the Object.clone() method, instead throwing a
    * CloneNotSupportedException.
    */
   //----------------------------------------------------------------
   @Override
   public Object clone() throws CloneNotSupportedException
   //----------------------------------------------------------------
   {
      throw new CloneNotSupportedException("Cloning of " + CLASS_NAME + " not allowed");
   }

   /*
    * Getter for the Encryptor instance for a specific encryptor name
    *
    * Get the CryptoIF object
    * @return CryptoIF
    */
   //----------------------------------------------------------------
   public static CryptoIF getCrypto(String cryptoName) throws CryptoException
   //----------------------------------------------------------------
   {
      CryptoIF crypto = null;

      if ( cryptoName == null || cryptoName.length() < 1)
      {
         throw new CryptoException("crypto is null");
      }

      if (_encryptor == null)
      {
         _encryptor = new Encryptor();
      }

      crypto = Encryptor._cryptos.get(cryptoName);

      /*
       * Note: it is valid to return a crypto object == null
       */

      return crypto;
   }

   /*
    * Getter for the Encryptor instance
    */
   //----------------------------------------------------------------
   public static CryptoIF getCrypto() throws CryptoException
   //----------------------------------------------------------------
   {
      return Encryptor.getCrypto(DEFAULT);
   }

   /*
    * Setter for the _logger
    *
    * Set the CryptoIF object
    * @param crypto CryptoIF object
    */
   //----------------------------------------------------------------
   public static synchronized void setCrypto(String cryptoName, CryptoIF crypto) throws CryptoException
   //----------------------------------------------------------------
   {
      if (cryptoName == null || cryptoName.length() < 1)
      {
         throw new CryptoException("crypto name is null/empty");
      }

      if (crypto == null)
      {
         throw new CryptoException("crypto is null");
      }

      if (_encryptor == null)
      {
         _encryptor = new Encryptor();
      }

      Encryptor._cryptos.put(cryptoName, crypto);

      return;
   }

   /*
    * Setter for the Encryptor
    */
   //----------------------------------------------------------------
   public static void setCrypto(CryptoIF crypto) throws CryptoException
   //----------------------------------------------------------------
   {
      if (crypto == null)
      {
         throw new CryptoException("crypto can not be null");
      }
      Encryptor.setCrypto(DEFAULT, crypto);
      return;
   }

   /*
    * encrypt - encrypt a string using the Encryptor
    * @param clear text value to encrypt
    * @return String encrypted value of clear text
    * @throws CryptoException
    */
   //----------------------------------------------------------------
   public static String encrypt(String encryptorName, String clear) throws CryptoException
   //----------------------------------------------------------------
   {
      return Encryptor.getCrypto(encryptorName).encrypt(clear);
   }

   /*
    * encrypt - encrypt a string using the Encryptor
    */
   //----------------------------------------------------------------
   public static String encrypt(String clear) throws CryptoException
   //----------------------------------------------------------------
   {
      return Encryptor.encrypt(DEFAULT, clear);
   }

   /**
    * decrypt - decrypt a string using the Encryptor
    * @param encrypted text value to decrypt
    * @return String decrypted value of the encrypted text
    * @throws CryptoException
    */
   //----------------------------------------------------------------
   public static String decrypt(String encryptorName, String encrypted) throws CryptoException
   //----------------------------------------------------------------
   {
      return Encryptor.getCrypto(encryptorName).decrypt(encrypted);
   }


   /*
    * decrypt - decrypt a string using the Encryptor
    */
   //----------------------------------------------------------------
   public static String decrypt(String encrypted) throws CryptoException
   //----------------------------------------------------------------
   {
      return Encryptor.decrypt(DEFAULT, encrypted);
   }
}
