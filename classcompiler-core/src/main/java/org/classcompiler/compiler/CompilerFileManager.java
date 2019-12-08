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

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.apache.log4j.Logger;

import com.google.common.collect.Iterables;

public class CompilerFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    private final Logger log = Logger.getLogger(getClass());

    private final Map<String, JavaClass> compileResults = new ConcurrentHashMap<>();

    private final FileSystem cp;
    private final ClassLoader cl;

    public CompilerFileManager(JavaCompiler compiler) throws IOException {
	super(getFileManager(compiler));
	super.fileManager.setLocation(StandardLocation.CLASS_PATH, Collections.emptyList());
	cp = FileSystems.fileSystem();
	cl = new NIOClassLoader(cp, Thread.currentThread().getContextClassLoader());
    }

    public CompilerFileManager(JavaCompiler compiler, FileSystem fs) throws IOException {
	super(getFileManager(compiler));
	super.fileManager.setLocation(StandardLocation.CLASS_PATH, Collections.emptyList());
	cp = fs;
	cl = new NIOClassLoader(fs, Thread.currentThread().getContextClassLoader());
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
	return cl;
    }

    private static StandardJavaFileManager getFileManager(JavaCompiler compiler) {
	StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);
	return standardFileManager;
    }

    public void addClasses(Map<String, byte[]> newClasses) throws IOException {
	for (Entry<String, byte[]> entry : newClasses.entrySet()) {
	    addClass(entry.getKey(), entry.getValue(), true);
	}
    }

    public void addClass(String fullyQualifiedName, byte[] bytes, boolean replace) throws IOException {
	if (!fullyQualifiedName.endsWith(Kind.CLASS.extension)) {
	    fullyQualifiedName = unixfy(fullyQualifiedName, Kind.CLASS.extension);
	} else {
	    if (fullyQualifiedName.indexOf('.') != -1) {
		fullyQualifiedName = unixfy(fullyQualifiedName.substring(0, fullyQualifiedName.length() - 6),
			Kind.CLASS.extension);
	    }
	}

	// Write to FileSystem
	final Path path = cp.getPath(fullyQualifiedName);
	if (Files.exists(path)) {
	    if (!replace) {
		throw new FileAlreadyExistsException(path.toString());
	    }
	    Files.delete(path);
	}
	Files.createDirectories(path.getParent());
	Files.createFile(path);
	Files.write(path, bytes);
    }

    private String unixfy(String fullyQualifiedName, String suffix) {
	return fullyQualifiedName.replace('.', '/') + suffix;
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
	if (Kind.CLASS.equals(kind)) {
	    final Path path = cp.getPath(className + Kind.CLASS.extension);
	    if (Files.exists(path) && !Files.isDirectory(path)) {
		return new JavaClass(className, Files.readAllBytes(path));
	    }
	}
	return super.getJavaFileForInput(location, className, kind);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling)
	    throws IOException {

	final JavaClass javaClass = new JavaClass(className);
	compileResults.put(className.replaceAll("/", "."), javaClass);
	return javaClass;
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse)
	    throws IOException {

	final List<JavaFileObject> results = getFiles(cp.getPath(packageName), recurse);
	Iterables.addAll(results, fileManager.list(location, packageName, kinds, recurse));
	return results;
    }

    private List<JavaFileObject> getFiles(Path path, boolean recurse) throws IOException {
	final List<JavaFileObject> files = new ArrayList<>();
	if (Files.exists(path) && Files.isDirectory(path)) {
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
		for (Path sub : directoryStream) {
		    if (recurse) {
			files.addAll(getFiles(sub, recurse));
		    }
		    files.add(new JavaClass(sub.toString(), Files.readAllBytes(sub)));
		}
	    }
	}
	return files;
    }

    public Map<String, byte[]> getCompileResults() {
	final Map<String, byte[]> classes = new HashMap<>();
	for (Entry<String, JavaClass> entry : compileResults.entrySet()) {
	    final JavaClass javaClass = entry.getValue();
	    if (javaClass.getBytes() != null) {
		try {
		    addClass(javaClass.getName(), javaClass.getBytes(), true);
		} catch (IOException e) {
		    log.warn("Couldn't write class file '" + javaClass.getName() + "' into file system.");
		}
	    }
	    classes.put(entry.getKey(), javaClass.getBytes());
	}
	return classes;
    }
}
