package org.classcompiler.classloader;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.tools.JavaFileObject.Kind;

import org.apache.log4j.Logger;

public class NIOClassLoader extends ClassLoader {

    private Logger log = Logger.getLogger(getClass());

    private final FileSystem fs;

    public NIOClassLoader(FileSystem fs) {
	super(Thread.currentThread().getContextClassLoader());
	this.fs = fs;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
	final Path path = fs.getPath("/", name.replace('.', '/') + Kind.CLASS.extension);
	if (Files.exists(path)) {
	    byte[] bytes;
	    try {
		bytes = Files.readAllBytes(path);
		return defineClass(name, bytes, 0, bytes.length);
	    } catch (IOException e) {
		log.warn("Error while reading the path '" + path + "'.", e);
	    }
	}
	return super.loadClass(name);
    }
}
