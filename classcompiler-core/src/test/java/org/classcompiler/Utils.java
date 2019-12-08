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

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map.Entry;

import org.classcompiler.compiler.CompilerFileManager;
import org.classcompiler.compiler.Compilers;
import org.classcompiler.compiler.JavaSource;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

import com.google.common.base.Charsets;

public class Utils {

    public static void addToCompilerClasspath(CompilerFileManager cfm, String fullyQualifiedName, String resourceName)
	    throws IOException, URISyntaxException {
	final JavaSource javaSource = new JavaSource(fullyQualifiedName,
		readJavaSource(fullyQualifiedName, resourceName));

	for (Entry<String, byte[]> entry : Compilers.compile(Collections.singletonList(javaSource)).entrySet()) {
	    cfm.getFileSystemWrapper().addClass(entry.getKey(), entry.getValue(), true);
	}
    }

    public static void addToCompilerClasspath(String fullyQualifiedName, String resourceName)
	    throws IOException, URISyntaxException {
	addToCompilerClasspath(new CompilerFileManager(new EclipseCompiler()), fullyQualifiedName, resourceName);
    }

    public static String readJavaSource(String fullyQualfiedName, String resourceName)
	    throws IOException, URISyntaxException {
	return new String(Files.readAllBytes(Paths.get(Utils.class.getResource(resourceName).toURI())), Charsets.UTF_8);
    }
}
