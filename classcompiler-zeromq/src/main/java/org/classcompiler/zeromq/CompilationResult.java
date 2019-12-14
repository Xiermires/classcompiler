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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CompilationResult implements Externalizable {

    private String[] filenames;
    private byte[][] bytes;

    public CompilationResult() {
    }

    public static CompilationResult of(Map<String, byte[]> map) {
	final CompilationResult cr = new CompilationResult();
	final String[] filenames = new String[map.size()];
	final byte[][] bytes = new byte[map.size()][];

	int pos = 0;
	for (Entry<String, byte[]> entry : map.entrySet()) {
	    filenames[pos] = entry.getKey();
	    bytes[pos] = entry.getValue();
	}

	cr.setFilenames(filenames);
	cr.setBytes(bytes);
	return cr;
    }

    public Map<String, byte[]> asMap() {
	final Map<String, byte[]> map = new HashMap<>();
	for (int i = 0; i < filenames.length; i++) {
	    map.put(filenames[i], bytes[i]);
	}
	return map;
    }

    public String[] getFilenames() {
	return filenames;
    }

    public void setFilenames(String[] filenames) {
	this.filenames = filenames;
    }

    public byte[][] getBytes() {
	return bytes;
    }

    public void setBytes(byte[][] bytes) {
	this.bytes = bytes;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeInt(filenames.length);
	for (int i = 0; i < filenames.length; i++) {
	    out.writeUTF(filenames[i]);
	    out.writeInt(bytes[i].length);
	    out.write(bytes[i]);
	}
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	int items = in.readInt();
	filenames = new String[items];
	bytes = new byte[items][];
	for (int i = 0; i < items; i++) {
	    filenames[i] = in.readUTF();
	    int fileLength = in.readInt();
	    bytes[i] = new byte[fileLength];
	    in.read(bytes[i]);
	}
    }
}
