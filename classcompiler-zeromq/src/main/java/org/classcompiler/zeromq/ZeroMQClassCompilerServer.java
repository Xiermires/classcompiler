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
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.classcompiler.compiler.Compilers;
import org.classcompiler.compiler.FileSystems;
import org.classcompiler.compiler.JavaSource;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

public class ZeroMQClassCompilerServer implements Closeable {

    private final Logger log = Logger.getLogger(getClass());

    private final ZMQ.Context context;
    private final ZMQ.Socket compile;
    private final ZMQ.Socket upload;

    private final UploadWorker uploadWorker;
    private final CompileWorker compileWorker;

    private final Serializer serializer;

    public ZeroMQClassCompilerServer(int threads, int uploadPort, int compilePort) {
	context = ZMQ.context(threads);

	upload = context.socket(SocketType.REP);
	upload.bind("tcp://*:" + uploadPort);
	compile = context.socket(SocketType.REP);
	compile.bind("tcp://*:" + compilePort);

	serializer = new Serializer();

	uploadWorker = new UploadWorker();
	uploadWorker.start();
	
	compileWorker = new CompileWorker();
	compileWorker.start();
	
	waitForStart();
    }

    private void waitForStart() {
	while (!uploadWorker.ready.get() && !compileWorker.ready.get()) {
	    try {
		Thread.sleep(10);
	    } catch (InterruptedException e) {
		return;
	    }
	}
    }

    class UploadWorker extends Thread {

	final AtomicBoolean ready = new AtomicBoolean(false);

	@Override
	public void run() {
	    try {
		while (!Thread.interrupted()) {
		    try {
			ready.set(true);
			final JavaUploadRequest jur = serializer.deserialize(upload.recv(0), JavaUploadRequest.class);
			for (Entry<String, byte[]> entry : jur) {
			    FileSystems.fileSystem().addClass(entry.getKey(), entry.getValue(), true);
			}
			upload.send("Success.");
		    } catch (Exception e) {
			log.error("Unexpected", e);
			upload.send(e.getMessage());
		    }
		}
	    } finally {
		upload.close();
		context.term();
	    }
	}
    }

    class CompileWorker extends Thread {

	final AtomicBoolean ready = new AtomicBoolean(false);

	@Override
	public void run() {
	    try {
		while (!Thread.interrupted()) {
		    try {
			final List<JavaSource> sources = new ArrayList<>();
			ready.set(true);
			final JavaCompileRequest jcr = serializer.deserialize(compile.recv(0), // receive request
				JavaCompileRequest.class);
			for (Entry<String, String> entry : jcr) {
			    sources.add(new JavaSource(entry.getKey(), entry.getValue()));
			}
			// send response
			compile.send(serializer.serialize(CompilationResult.of(Compilers.compile(sources))), 0);
		    } catch (Exception e) {
			log.error("Cannot compile.", e);
		    }
		}
	    } finally {
		compile.close();
		context.term();
	    }
	}
    }

    @Override
    public void close() {
	this.uploadWorker.interrupt();
	this.compileWorker.interrupt();
    }
}
