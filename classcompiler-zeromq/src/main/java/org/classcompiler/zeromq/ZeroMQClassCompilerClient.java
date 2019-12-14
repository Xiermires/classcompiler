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
