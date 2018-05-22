/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2012 Project OpenPTK
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.openptk.exception.CryptoException;

/**
 *
 * @author tls
 */
public class CryptoTest
{
   /**
    * Test Crypt Implementations.
    */
   @Test
   public void testCryptoImplementations()
   {
      String clear = "OpenPTK";
      CryptoIF crypto = null;

      try
      {
         System.out.println("* CryptoTest: test AESCrypto");
         crypto = new AESCrypto();
         assertEquals(clear, crypto.decrypt(crypto.encrypt(clear)));
         
         System.out.println("* CryptoTest: test DESCrypto");
         crypto = new DESCrypto();
         assertEquals(clear, crypto.decrypt(crypto.encrypt(clear)));
         
         System.out.println("* CryptoTest: test KeyGenCrypto");
         crypto = new KeyGenCrypto("AES", "AES/ECB/PKCS5Padding");
         assertEquals(clear, crypto.decrypt(crypto.encrypt(clear)));
         
//         System.out.println("* CryptoTest: test PBECrypto");
//         crypto = new PBECrypto("password", "DES");
//         assertEquals(clear, crypto.decrypt(crypto.encrypt(clear)));
         
//         System.out.println("* CryptoTest: test TripleDESCrypto");
//         crypto = new TripleDESCrypto();
//         assertEquals(clear, crypto.decrypt(crypto.encrypt(clear)));
         
      }
      catch (CryptoException ex)
      {
         fail(ex.getMessage());
      }

   }
}
