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
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;

import javax.tools.JavaFileObject.Kind;

public class FileSystemWrapper extends ForwardingFileSystem {

    private final FileSystem delegate;

    public FileSystemWrapper(FileSystem delegate) {
	this.delegate = delegate;
    }

    @Override
    protected FileSystem delegate() {
	return delegate;
    }

    public void addClasses(Map<String, byte[]> newClasses) throws IOException {
	for (Entry<String, byte[]> entry : newClasses.entrySet()) {
	    addClass(entry.getKey(), entry.getValue(), true);
	}
    }

    public void addClass(String fullyQualifiedName, byte[] bytes, boolean replace) throws IOException {
	fullyQualifiedName = unixfy(fullyQualifiedName, Kind.CLASS);

	// Write to FileSystem
	final Path path = getPath(fullyQualifiedName);
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

    public void addSources(Map<String, String> newSources) throws IOException {
	for (Entry<String, String> entry : newSources.entrySet()) {
	    addSource(entry.getKey(), entry.getValue(), true);
	}
    }

    public void addSource(String fullyQualifiedName, String source, boolean replace) throws IOException {
	fullyQualifiedName = unixfy(fullyQualifiedName, Kind.SOURCE);

	// Write to FileSystem
	final Path path = getPath(fullyQualifiedName);
	if (Files.exists(path)) {
	    if (!replace) {
		throw new FileAlreadyExistsException(path.toString());
	    }
	    Files.delete(path);
	}
	Files.createDirectories(path.getParent());
	Files.createFile(path);
	Files.write(path, source.getBytes());
    }

    private String unixfy(String fullyQualifiedName, Kind kind) {
	if (!fullyQualifiedName.endsWith(kind.extension)) {
	    if (fullyQualifiedName.indexOf('.') != -1) {
		fullyQualifiedName = fullyQualifiedName.replace('.', '/');
	    }
	    fullyQualifiedName += kind.extension;
	} else {
	    if (fullyQualifiedName.indexOf('/') == -1) {
		fullyQualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.length() - kind.extension.length()).replace('.', '/')
			+ kind.extension;
	    }
	}
	return fullyQualifiedName;
    }
}
