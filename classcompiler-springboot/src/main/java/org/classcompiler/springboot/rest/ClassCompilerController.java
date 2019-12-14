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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.classcompiler.compiler.CompilerFileManager;
import org.classcompiler.compiler.Compilers;
import org.classcompiler.compiler.JavaSource;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ClassCompilerController {

    @RequestMapping("/")
    public String root() {
	return "index.html";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
	try (final CompilerFileManager cfm = new CompilerFileManager(new EclipseCompiler())) {
	    final String fullyQualifiedName = file.getOriginalFilename().replace("/", ".");
	    if (fullyQualifiedName.endsWith(".jar")) { // handle jar
		for (Entry<String, byte[]> entry : unzip(file)) {
		    cfm.getFileSystemWrapper().addClass(entry.getKey(), entry.getValue(), true);
		}
	    } else if (fullyQualifiedName.endsWith(".class")) { // handle class
		cfm.getFileSystemWrapper().addClass(fullyQualifiedName, file.getBytes(), true);
	    } else if (fullyQualifiedName.endsWith(".java")) {
		cfm.getFileSystemWrapper().addSource(fullyQualifiedName, new String(file.getBytes()), true);
	    } else {
		throw new UnsupportedOperationException("Unsupported file format { not jar / class }.");
	    }
	}
	return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    private List<Entry<String, byte[]>> unzip(MultipartFile file) throws IOException {
	final List<Entry<String, byte[]>> entries = new ArrayList<>();
	final ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());
	try (ZipInputStream zipIn = new ZipInputStream(bais)) {
	    ZipEntry ze;
	    while ((ze = zipIn.getNextEntry()) != null) {
		if (!ze.isDirectory() && ze.getName().endsWith(".class")) {
		    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    final String filename = ze.getName();
		    IOUtils.copy(zipIn, baos);
		    entries.add(new Entry<String, byte[]>() {
			@Override
			public String getKey() {
			    return filename;
			}

			@Override
			public byte[] getValue() {
			    return baos.toByteArray();
			}

			@Override
			public byte[] setValue(byte[] value) {
			    throw new UnsupportedOperationException("not supported");
			}

		    });
		}
		zipIn.closeEntry();
	    }
	}
	return entries;
    }

    @RequestMapping(value = "/compile", method = RequestMethod.POST)
    public ResponseEntity<Map<String, byte[]>> compile(@RequestBody Collection<JavaCompileRequest> jcrs)
	    throws IOException {
	final List<JavaSource> sources = new ArrayList<>();
	for (JavaCompileRequest jcr : jcrs) {
	    sources.add(new JavaSource(jcr.getFullyQualifiedClassName(), jcr.getJavaSource()));
	}
	return new ResponseEntity<>(Compilers.compile(sources), HttpStatus.OK);
    }
}
