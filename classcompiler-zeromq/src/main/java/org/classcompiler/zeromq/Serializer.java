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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class Serializer {

    public byte[] serialize(Object o) throws IOException {
	final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
	    oos.writeObject(o);
	}
	return baos.toByteArray();
    }

    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException, ClassNotFoundException {
	final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
	T t;
	try (ObjectInputStream ois = new ObjectInputStream(bais)) {
	    t = type.cast(ois.readObject());
	}
	return t;
    }
    
    @SuppressWarnings("unchecked")
    public <T> List<T> deserializeList(byte[] bytes, Class<T> type) throws IOException, ClassNotFoundException {
	final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
	List<T> ts;
	try (ObjectInputStream ois = new ObjectInputStream(bais)) {
	    ts = (List<T>) ois.readObject();
	}
	return ts;
    }
}
