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
package org.classcompiler.springboot.rest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map.Entry;

import org.classcompiler.Utils;
import org.classcompiler.compiler.Compilers;
import org.classcompiler.compiler.JavaSource;
import org.junit.BeforeClass;
import org.junit.Test;

import kong.unirest.HttpResponse;
import kong.unirest.MultipartBody;
import kong.unirest.Unirest;

public class TestClassCompiler {

    @BeforeClass
    public static void pre() throws Exception {
	ClassCompiler.main();
    }

    @Test
    public void shouldCompile() throws Exception {
	uploadDependency();

	final String fullyQualifiedName = "org.classcompiler.compiler.CompileMe";
	final String javaSource = Utils.readJavaSource(fullyQualifiedName, "/CompileMe.java");

	final JavaCompileRequest jcr = JavaCompileRequest.of(fullyQualifiedName, javaSource);

	final HttpResponse<String> res = Unirest.post("http://localhost:8080/compile")//
		.header("Content-Type", "application/json")//
		.body(Collections.singletonList(jcr))//
		.asString();
	assertThat(res.getStatus(), is(200));
    }

    // Compile the version dependency in test to upload its class bytes
    private void uploadDependency() throws IOException, URISyntaxException {
	final String fullyQualifiedName = "org.classcompiler.compiler.SomeExternalDependency";
	final String javaSource = Utils.readJavaSource(fullyQualifiedName, "/SomeExternalDependency.java");

	for (Entry<String, byte[]> entry : Compilers
		.compile(Collections.singletonList(new JavaSource(fullyQualifiedName, javaSource))).entrySet()) {
	    final MultipartBody req = Unirest//
		    .post("http://localhost:8080/upload")//
		    .field("file", new ByteArrayInputStream(entry.getValue()), entry.getKey() + ".class");
	    final HttpResponse<?> res = req.asEmpty();
	    assertThat(res.getStatus(), is(200));
	}
    }
}
