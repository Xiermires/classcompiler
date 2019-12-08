package org.classcompiler.zeromq;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

public class ZeroMQClassCompilerClient implements Closeable {

    private final ZMQ.Context context;

    private final ZMQ.Socket upload;
    private final ZMQ.Socket compile;

    private final Serializer serializer;

    public ZeroMQClassCompilerClient(String hostname, int uploadPort, int compilePort) {
	context = ZMQ.context(1);

	upload = context.socket(SocketType.REQ);
	upload.connect("tcp://" + hostname + ":" + uploadPort);
	compile = context.socket(SocketType.REQ);
	compile.connect("tcp://" + hostname + ":" + compilePort);

	serializer = new Serializer();
    }

    public String upload(JavaUploadRequest jur) throws IOException, ClassNotFoundException {
	upload.send(serializer.serialize(jur), 0);
	return new String(upload.recv());
    }

    public Map<String, byte[]> compile(JavaCompileRequest jcr) throws IOException, ClassNotFoundException {
	compile.send(serializer.serialize(jcr), 0);
	return serializer.deserialize(compile.recv(), CompilationResult.class).asMap();
    }

    @Override
    public void close() throws IOException {
	if (upload != null) {
	    upload.close();
	}
	if (compile != null) {
	    compile.close();
	}
	if (context != null) {
	    context.close();
	}

    }
}
