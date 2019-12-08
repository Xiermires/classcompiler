package org.classcompiler.compiler;

import java.nio.file.FileSystem;

public class FileSystemWrapper extends ForwardingFileSystem {

    private final FileSystem delegate;

    public FileSystemWrapper(FileSystem delegate) {
	this.delegate = delegate;
    }

    @Override
    protected FileSystem delegate() {
	return delegate;
    }
}
