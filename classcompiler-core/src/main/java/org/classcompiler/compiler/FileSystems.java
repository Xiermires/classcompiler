package org.classcompiler.compiler;

import java.nio.file.FileSystem;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class FileSystems {

    private static FileSystem fs = null;
    
    public static FileSystem fileSystem() {
	if (fs == null) {
	    fs = Jimfs.newFileSystem(Configuration.unix());
	}
	return fs; 
    }
}
