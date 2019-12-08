package org.classcompiler.classloader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import org.classcompiler.Utils;
import org.classcompiler.compiler.Compilers;
import org.classcompiler.compiler.FileSystemWrapper;
import org.classcompiler.compiler.JavaSource;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class TestClassLoader {

    @Test
    public void shouldLoadClass() throws IOException, URISyntaxException, ClassNotFoundException {
	final FileSystemWrapper fs = new FileSystemWrapper(Jimfs.newFileSystem(Configuration.unix()));

	// compile into in memory file system
	final String fullyQualifiedName = "org.classcompiler.compiler.SomeExternalDependency";
	final String javaSource = Utils.readJavaSource(fullyQualifiedName, "/SomeExternalDependency.java");
	Compilers.compile(Collections.singletonList(new JavaSource(fullyQualifiedName, javaSource)), fs);

	final NIOClassLoader cl = new NIOClassLoader(fs);
	final Class<?> clazz = cl.loadClass(fullyQualifiedName);
	assertThat(clazz, is(not(nullValue())));
	assertThat(clazz.getName(), is(fullyQualifiedName));
    }
}
