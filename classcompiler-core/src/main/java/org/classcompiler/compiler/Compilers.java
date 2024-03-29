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
package org.classcompiler.compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;

import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

import com.google.common.base.Charsets;

public class Compilers {

    public static Map<String, byte[]> compile(Collection<JavaSource> sources, FileSystemWrapper fileSystem)
	    throws IllegalStateException, IOException {

	final JavaCompiler compiler = new EclipseCompiler();
	final CompilerFileManager cfm = new CompilerFileManager(compiler, fileSystem);

	final ByteArrayOutputStream err = new ByteArrayOutputStream();
	final CompilationTask task = compiler.getTask(new OutputStreamWriter(err), cfm, null, null, null, sources);
	if (task.call()) {
	    return cfm.getCompileResults();
	} else {
	    throw new IllegalStateException("Fail '" + new String(err.toByteArray(), Charsets.UTF_8) + "'.");
	}
    }

    public static Map<String, byte[]> compile(Collection<JavaSource> sources)
	    throws IllegalStateException, IOException {
	return compile(sources, FileSystems.fileSystem());
    }
}
