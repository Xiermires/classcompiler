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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class JavaUploadRequest implements Iterable<Entry<String, byte[]>>, Externalizable {

    private String[] fullyQualifiedClassNames;
    private byte[][] bytes;

    public static JavaUploadRequest of(String fullyQualifiedClassName, byte[] bytes) {
	return of(new String[] { fullyQualifiedClassName }, new byte[][] { bytes });
    }

    public static JavaUploadRequest of(String[] fullyQualifiedClassNames, byte[][] bytes) {
	final JavaUploadRequest jcr = new JavaUploadRequest();
	jcr.setFullyQualifiedClassNames(fullyQualifiedClassNames);
	jcr.setBytes(bytes);
	return jcr;
    }

    public Map<String, byte[]> asMap() {
	final Map<String, byte[]> map = new HashMap<>();
	for (int i = 0; i < fullyQualifiedClassNames.length; i++) {
	    map.put(fullyQualifiedClassNames[i], bytes[i]);
	}
	return map;
    }

    public String[] getFullyQualifiedClassNames() {
	return fullyQualifiedClassNames;
    }

    public void setFullyQualifiedClassNames(String[] fullyQualifiedClassNames) {
	this.fullyQualifiedClassNames = fullyQualifiedClassNames;
    }

    public byte[][] getBytes() {
	return bytes;
    }

    public void setBytes(byte[][] bytes) {
	this.bytes = bytes;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeInt(fullyQualifiedClassNames.length);
	for (int i = 0; i < fullyQualifiedClassNames.length; i++) {
	    out.writeUTF(fullyQualifiedClassNames[i]);
	    out.writeInt(bytes[i].length);
	    out.write(bytes[i]);
	}
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	int items = in.readInt();
	fullyQualifiedClassNames = new String[items];
	bytes = new byte[items][];
	for (int i = 0; i < items; i++) {
	    fullyQualifiedClassNames[i] = in.readUTF();
	    int fileLength = in.readInt();
	    bytes[i] = new byte[fileLength];
	    in.read(bytes[i]);
	}
    }

    @Override
    public Iterator<Entry<String, byte[]>> iterator() {
	return new Iterator<Entry<String, byte[]>>() {

	    int pos = 0;

	    @Override
	    public boolean hasNext() {
		return pos < fullyQualifiedClassNames.length;
	    }

	    @Override
	    public Entry<String, byte[]> next() {
		try {
		    return new Entry<String, byte[]>() {

			final String key = fullyQualifiedClassNames[pos];
			final byte[] value = bytes[pos];
			
			@Override
			public String getKey() {
			    return key;
			}

			@Override
			public byte[] getValue() {
			    return value;
			}

			@Override
			public byte[] setValue(byte[] value) {
			    throw new UnsupportedOperationException("Immutable entrySet");
			}
		    };
		} finally {
		    pos++;
		}
	    }
	};
    }
}
