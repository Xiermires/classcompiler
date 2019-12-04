/*******************************************************************************
 * Copyright (c) 2019, Xavier Miret Andres <xavier.mires@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package org.classcompiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utils {

    public static Map<String, byte[]> parseJarFile(String jarFileFilename) throws IOException {
	final Map<String, byte[]> classpath = new HashMap<>();
	try (JarFile jar = new JarFile(jarFileFilename)) {
	    final Enumeration<JarEntry> entries = jar.entries();
	    while (entries.hasMoreElements()) {
		final JarEntry entry = entries.nextElement();
		if (entry.getName().endsWith(".class")) {
		    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    copyStream(jar.getInputStream(entry), baos);
		    classpath.put(entry.getName(), baos.toByteArray());
		}
	    }
	}
	return classpath;
    }

    private static void copyStream(InputStream is, ByteArrayOutputStream baos) throws IOException {
	final byte[] buffer = new byte[4096];
	int len;
	try {
	    while ((len = is.read(buffer)) != -1) {
		baos.write(buffer, 0, len);
	    }
	} finally {
	    try {
		is.close();
	    } catch (Exception e) {
		// ignore
	    }
	}
    }
}
