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
package org.openptk.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;

/**
 * Media manipulation methods.
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class Media
//===================================================================
{
   //----------------------------------------------------------------
   public Media()
   //----------------------------------------------------------------
   {
      /*
       * Do not allow this class to be instanciated
       */

      throw new AssertionError();
   }

   /**
    * Scale and Crop the image (byte[]) to the specified size (width and height).
    *
    * @param bytesIn byte[] the original image
    * @param width int size of the returned image width
    * @param height int size of the returned image height
    * @param type String the original image type
    * @return byte[] modified image
    * @throws Exception
    */
   //----------------------------------------------------------------
   public static synchronized byte[] scaleCropImage(final byte[] bytesIn,
      final int width, final int height, final String type) throws Exception
   //----------------------------------------------------------------
   {
      byte[] bytesOut = null;
      boolean success = false;
      int hint = java.awt.Image.SCALE_SMOOTH;
      int imageHeight = 0;
      int imageWidth = 0;
      ByteArrayOutputStream baos = null;
      BufferedImage bi = null;
      ImageIcon imageIn = null;
      ImageIcon imageOut = null;
      Graphics g = null;


      if (bytesIn == null || bytesIn.length < 1)
      {
         throw new Exception("byte[] is null");
      }

      if (width < 10 || height < 10)
      {
         throw new Exception("width and height must be at least 10");
      }

      if (type == null || type.length() < 1)
      {
         throw new Exception("type is null");
      }

      imageIn = new ImageIcon(bytesIn);
      imageHeight = imageIn.getIconHeight();
      imageWidth = imageIn.getIconWidth();

      if (imageWidth > imageHeight)
      {
         imageOut = new ImageIcon(imageIn.getImage().getScaledInstance(-1, height, hint));
      }
      else
      {
         imageOut = new ImageIcon(imageIn.getImage().getScaledInstance(width, -1, hint));
      }

      bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

      /*
       * drawImage(
       * image,
       *
       * dx1,  // dest, 1st corner, X coord
       * dy1,  //                   Y
       * dx2,  //       2nd corner, X coord
       * dy2,  //                   Y
       *
       * sx1,  //  src, 1st corner, X coord
       * sy1,  //                   Y
       * sx2,  //       2nd corner, X coord
       * sy2,  //                   Y coord
       *
       * null
       */

      g = bi.getGraphics();
      g.drawImage(imageOut.getImage(),
         0, 0, width, height,
         0, 0, width, height,
         null);

      baos = new ByteArrayOutputStream();

      success = ImageIO.write(bi, type, baos);

      if (!success)
      {
         throw new Exception("No appropriate writer is found for '" + type + "'");
      }

      bytesOut = baos.toByteArray();

      return bytesOut;
   }
}
