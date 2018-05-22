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

import org.openptk.exception.CryptoException;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class TripleDESCrypto extends PBECrypto
//===================================================================
{

   private static final String _passPhrase = "Project Open_Provisioning_ToolKit-2.0";
   private static final String _cipher = "PBEWithMD5AndTripleDES";

   /**
    * Creates a CryptoIF instance 
    * using the PBEWithMD5AndripleDES cipher with a pre-defined pass phrase.
    * 
    * @throws CryptoException
    */
   //----------------------------------------------------------------
   public TripleDESCrypto() throws CryptoException
   //----------------------------------------------------------------
   {
      super(TripleDESCrypto._passPhrase, TripleDESCrypto._cipher);
      return;
   }

   /**
    * Creates a CryptoIF instance using the PBEWithMD5AndTripleDES cipher. 
    *
    * @param passPhrase unique strong pass phrase
    * @throws CryptoException
    */
   //----------------------------------------------------------------
   public TripleDESCrypto(final String passPhrase) throws CryptoException
   //----------------------------------------------------------------
   {
      super(passPhrase, TripleDESCrypto._cipher);
      return;
   }

   /**
    *
    * @param args (not used)
    */
   //----------------------------------------------------------------
   public static void main(String[] args)
   //----------------------------------------------------------------
   {
      String clear = "password";
      String encrypted = null;
      String decrypted = null;
      CryptoIF crypto = null;

      try
      {
         crypto = new TripleDESCrypto();
      }
      catch (CryptoException ex)
      {
         System.err.println(ex.getMessage());
      }

      if (crypto != null)
      {
         try
         {
            encrypted = crypto.encrypt(clear);
            decrypted = crypto.decrypt(encrypted);
         }
         catch (CryptoException ex)
         {
            System.err.println(ex.getMessage());
         }
      }

      if (encrypted != null && decrypted != null)
      {
         System.out.print(
            "cleartext='" + clear + "'\n" +
            "encrypted='" + encrypted + "'\n" +
            "decrypted='" + decrypted + "'\n");
      }

   }
}
