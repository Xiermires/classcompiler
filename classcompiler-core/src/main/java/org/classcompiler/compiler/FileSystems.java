package org.classcompiler.compiler;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class FileSystems {

    private static FileSystemWrapper fs = null;

    public static FileSystemWrapper fileSystem() {
	if (fs == null) {
	    fs = new FileSystemWrapper(Jimfs.newFileSystem(Configuration.unix()));
	}
	return fs;
    }
}
