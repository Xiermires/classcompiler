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

public class JavaCompileRequest implements Iterable<Entry<String, String>>, Externalizable {

    private String[] fullyQualifiedClassNames;
    private String[] javaSources;

    public static JavaCompileRequest of(String fullyQualifiedClassName, String javaSource) {
	return of(new String[] { fullyQualifiedClassName }, new String[] { javaSource });
    }

    public static JavaCompileRequest of(String[] fullyQualifiedClassNames, String[] javaSources) {
	final JavaCompileRequest jcr = new JavaCompileRequest();
	jcr.setFullyQualifiedClassNames(fullyQualifiedClassNames);
	jcr.setJavaSources(javaSources);
	return jcr;
    }

    public Map<String, String> asMap() {
	final Map<String, String> map = new HashMap<>();
	for (int i = 0; i < fullyQualifiedClassNames.length; i++) {
	    map.put(fullyQualifiedClassNames[i], javaSources[i]);
	}
	return map;
    }

    public String[] getFullyQualifiedClassNames() {
	return fullyQualifiedClassNames;
    }

    public void setFullyQualifiedClassNames(String[] fullyQualifiedClassNames) {
	this.fullyQualifiedClassNames = fullyQualifiedClassNames;
    }

    public String[] getJavaSources() {
	return javaSources;
    }

    public void setJavaSources(String[] javaSources) {
	this.javaSources = javaSources;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeInt(fullyQualifiedClassNames.length);
	for (int i = 0; i < fullyQualifiedClassNames.length; i++) {
	    out.writeUTF(fullyQualifiedClassNames[i]);
	    out.writeUTF(javaSources[i]);
	}
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	int items = in.readInt();
	fullyQualifiedClassNames = new String[items];
	javaSources = new String[items];
	for (int i = 0; i < items; i++) {
	    fullyQualifiedClassNames[i] = in.readUTF();
	    javaSources[i] = in.readUTF();
	}
    }

    @Override
    public Iterator<Entry<String, String>> iterator() {
	return new Iterator<Entry<String, String>>() {

	    int pos = 0;

	    @Override
	    public boolean hasNext() {
		return pos < fullyQualifiedClassNames.length;
	    }

	    @Override
	    public Entry<String, String> next() {
		try {
		    return new Entry<String, String>() {

			final String key = fullyQualifiedClassNames[pos];
			final String value = javaSources[pos];
			    
			
			@Override
			public String getKey() {
			    return key;
			}

			@Override
			public String getValue() {
			    return value;
			}

			@Override
			public String setValue(String value) {
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
