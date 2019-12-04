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

import com.google.common.collect.Iterables;

public class CompilerFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    private static final PackageNode packageRoot = new PackageNode(null);
    private static final Map<String, byte[]> classpath = new ConcurrentHashMap<>();
    private final Map<String, JavaClass> compileResults = new ConcurrentHashMap<>();

    public CompilerFileManager(JavaCompiler compiler) throws IOException {
	super(getFileManager(compiler));
	super.fileManager.setLocation(StandardLocation.CLASS_PATH, Collections.emptyList());
    }

    private static StandardJavaFileManager getFileManager(JavaCompiler compiler) {
	StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);
	return standardFileManager;
    }

    public void addToClasspath(String qualifiedName, byte[] bytes) {
	PackageNode lastPackage = packageRoot;
	final String[] packageNames = qualifiedName.split("\\.");
	int i = 0;
	for (; i < packageNames.length; i++) {
	    PackageNode nextPackage = searchPackageNode(lastPackage, packageNames[i]);
	    if (nextPackage != null) {
		lastPackage = nextPackage;
	    } else {
		break;
	    }
	}
	for (; i < packageNames.length - 1; i++) {
	    final PackageNode child = new PackageNode(packageNames[i]);
	    lastPackage.addChild(child);
	    child.setParent(lastPackage);
	    lastPackage = child;
	}
	lastPackage.getClasses().add(new JavaClass(qualifiedName, bytes));
	classpath.put(qualifiedName, bytes);
    }

    public void addToClasspath(Map<String, byte[]> newClasses) {
	for (Entry<String, byte[]> entry : newClasses.entrySet()) {
	    addToClasspath(entry.getKey().replace(".class", "").replace('/', '.'), entry.getValue());
	}
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
	if (Kind.CLASS.equals(kind)) {
	    final byte[] hit = classpath.get(className.replaceAll("/", "."));
	    if (hit != null) {
		return new JavaClass(className, hit);
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

	final List<JavaFileObject> results = new ArrayList<>();
	final PackageNode packageNode = searchPackageNode(packageName);
	if (packageNode != null) {
	    drainClasses(results, packageNode, recurse);
	}

	Iterables.addAll(results, fileManager.list(location, packageName, kinds, recurse));
	return results;
    }

    private void drainClasses(List<JavaFileObject> results, PackageNode packageNode, boolean recurse) {
	if (!recurse) {
	    results.addAll(packageNode.getClasses());
	} else {
	    results.addAll(packageNode.getClasses());
	    for (PackageNode child : packageNode.getChildren()) {
		drainClasses(results, child, recurse);
	    }
	}
    }

    private PackageNode searchPackageNode(PackageNode root, String packageName) {
	return root.getChild(packageName);
    }

    private PackageNode searchPackageNode(String packageName) {
	final String[] packageNames = packageName.split("/");
	PackageNode packageNode = packageRoot;

	for (int i = 0; i < packageNames.length && packageNode != null; i++) {
	    packageNode = packageNode.getChild(packageNames[i]);
	}
	return packageNode != null ? packageNode : null;
    }

    public Map<String, byte[]> getCompiledClasses() {
	final Map<String, byte[]> classes = new HashMap<>();
	for (Entry<String, JavaClass> entry : compileResults.entrySet()) {
	    classes.put(entry.getKey(), entry.getValue().getBytes());
	}
	return classes;
    }

    @Override
    public void flush() throws IOException {
	final FileSystem fs = CompilerFactory.fileSystem();
	for (Entry<String, JavaClass> entry : compileResults.entrySet()) {
	    final JavaClass jc = entry.getValue();
	    final int last = jc.getName().lastIndexOf("/");
	    final String foldername = jc.getName().substring(0, last + 1); // include /
	    Files.createDirectories(fs.getPath(foldername));
	    final Path path = fs.getPath("/", jc.getName());
	    Files.createFile(path);
	    Files.write(path, jc.getBytes());
	}
    }
}
