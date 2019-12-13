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
