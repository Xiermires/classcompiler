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
package org.classcompiler.frontend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map.Entry;

import org.classcompiler.ClassCompiler;
import org.classcompiler.Utils;
import org.junit.Test;

import com.google.common.base.Charsets;

import kong.unirest.MultipartBody;
import kong.unirest.Unirest;

public class TestClassCompilerController {

    @Test
    public void testControllerCompileRequest() throws Exception {
	ClassCompiler.main();

	sendPicocliDependency();

	final String fullyQualifiedClassName = "org.classcompiler.test.example.CompileMe";
	final String javaSource = new String(
		Files.readAllBytes(Paths.get(getClass().getResource("/CompileMe.java").toURI())), Charsets.UTF_8);
	final JavaCompileRequest jcr = JavaCompileRequest.of(fullyQualifiedClassName, javaSource);

	Unirest.post("http://localhost:8080/compile")//
		.header("Content-Type", "application/json")//
		.body(Collections.singletonList(jcr))//
		.asString();
    }

    private void sendPicocliDependency() throws IOException {
	for (Entry<String, byte[]> entry : Utils
		.parseJarFile("d:\\dev\\repo\\info\\picocli\\picocli\\4.1.1\\picocli-4.1.1.jar").entrySet()) {
	    final MultipartBody req = Unirest//
		    .post("http://localhost:8080/upload")//
		    .field("file", new ByteArrayInputStream(entry.getValue()), entry.getKey());
	    req.asEmpty();
	}
    }
}
