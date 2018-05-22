/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * 
 * This file is available and licensed under the following license:
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer. 
 * 
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 
 * 
 * Neither the name of Sun Microsystems nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openspml.v2.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {

    private static final String code_id = "$Id: FileUtil.java,v 1.3 2006/08/31 22:49:47 kas Exp $";

    public static byte[] readFileBytes(File file)
            throws IOException {

        byte[] bytes = null;
        FileInputStream fis = new FileInputStream(file);
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
            byte[] buf = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = fis.read(buf)) != -1) {
                bout.write(buf, 0, bytesRead);
            }
            bytes = bout.toByteArray();
        }
        finally {
            try {
                fis.close();
            }
            catch (IOException ex) {
                // ignore these
            }
        }
        return bytes;
    }

    public static String readFile(File file) throws IOException {
        byte[] bytes = readFileBytes(file);
        return new String(bytes);
    }

    public static String readFile(String filename) throws IOException {
        return readFile(new File(filename));
    }

    /**
     * Store the contents of a String in a file.
     */
    public static void writeFile(String filename, String contents)
            throws IOException {

        File path = new File(filename);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(contents.getBytes());
        }
        finally {
            try {
                if (fos != null) fos.close();
            }
            catch (java.io.IOException e) {
                // ignore
            }
        }
    }

    public interface FileLineProcessor {
        /** process the line that was read from a file. */
        public void process(String line);
    };

    public static void readFileAndProcessLines(File name, FileLineProcessor processor)
            throws FileNotFoundException, IOException {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(name));

            String line = reader.readLine();
            while (line != null) {
                processor.process(line);
                line = reader.readLine();
            }
        }
        finally {
            if (reader != null) reader.close();
        }
    }
}
