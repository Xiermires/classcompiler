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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import org.classcompiler.Utils;
import org.classcompiler.compiler.Compilers;
import org.classcompiler.compiler.FileSystemWrapper;
import org.classcompiler.compiler.JavaSource;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class TestClassLoader {

    @Test
    public void shouldLoadClass() throws IOException, URISyntaxException, ClassNotFoundException {
	final FileSystemWrapper fs = new FileSystemWrapper(Jimfs.newFileSystem(Configuration.unix()));

	// compile into in memory file system
	final String fullyQualifiedName = "org.classcompiler.compiler.SomeExternalDependency";
	final String javaSource = Utils.readJavaSource(fullyQualifiedName, "/SomeExternalDependency.java");
	Compilers.compile(Collections.singletonList(new JavaSource(fullyQualifiedName, javaSource)), fs);

	final NIOClassLoader cl = new NIOClassLoader(fs);
	final Class<?> clazz = cl.loadClass(fullyQualifiedName);
	assertThat(clazz, is(not(nullValue())));
	assertThat(clazz.getName(), is(fullyQualifiedName));
    }
}
