package org.classcompiler.zeromq;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map.Entry;

import org.classcompiler.Utils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestClassCompiler {

    private static ZeroMQClassCompilerServer server = null;

    @BeforeClass
    public static void pre() {
	server = new ZeroMQClassCompilerServer(2, 5556, 5555);
    }

    @AfterClass
    public static void post() {
	if (server != null) {
	    server.close();
	}
    }

    @Test
    public void shouldCompile() throws IOException, URISyntaxException, ClassNotFoundException {
	
	final ZeroMQClassCompilerClient client = new ZeroMQClassCompilerClient("127.0.0.1", 5556, 5555);

	uploadDependency(client);

	final String fullyQualifiedName = "org.classcompiler.compiler.CompileMe";
	final String javaSource = Utils.readJavaSource(fullyQualifiedName, "/CompileMe.java");

	client.compile(JavaCompileRequest.of(fullyQualifiedName, javaSource));
    }

    // Compile the version dependency in test to upload its class bytes
    private void uploadDependency(ZeroMQClassCompilerClient client) throws IOException, URISyntaxException, ClassNotFoundException {
	final String fullyQualifiedName = "org.classcompiler.compiler.SomeExternalDependency";
	final String javaSource = Utils.readJavaSource(fullyQualifiedName, "/SomeExternalDependency.java");

	for (Entry<String, byte[]> entry : client.compile(JavaCompileRequest.of(fullyQualifiedName, javaSource))
		.entrySet()) {
	    client.upload(JavaUploadRequest.of(entry.getKey(), entry.getValue()));
	}
    }
}
