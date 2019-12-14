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
package org.classcompiler.classloader;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.tools.JavaFileObject.Kind;

import org.apache.log4j.Logger;

public class NIOClassLoader extends ClassLoader {

    private Logger log = Logger.getLogger(getClass());

    private final FileSystem fs;

    public NIOClassLoader(FileSystem fs) {
	super(Thread.currentThread().getContextClassLoader());
	this.fs = fs;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
	final Path path = fs.getPath("/", name.replace('.', '/') + Kind.CLASS.extension);
	if (Files.exists(path)) {
	    byte[] bytes;
	    try {
		bytes = Files.readAllBytes(path);
		return defineClass(name, bytes, 0, bytes.length);
	    } catch (IOException e) {
		log.warn("Error while reading the path '" + path + "'.", e);
	    }
	}
	return super.loadClass(name);
    }
}
