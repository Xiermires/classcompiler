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
