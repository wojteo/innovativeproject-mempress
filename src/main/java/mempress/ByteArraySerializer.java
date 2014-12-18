package mempress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ByteArraySerializer implements Serializer {

	@Override
	public ClassData ser(Object obj) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			byte[] b = baos.toByteArray();

			ClassData cd = new ClassData(SerializerType.ByteArraySerializer, b, b.length);

			return cd;

		} catch (IOException e) {
			throw new MempressException("Couldn't serialize ByteArray");
		}
	}

	@Override
	public Object des(ClassData cd) {

		ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) cd.getData());
		Object o = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			o = ois.readObject();

			return o;
		} catch (ClassNotFoundException | IOException e) {
			throw new MempressException("Couldn't deserialize ByteArray");
		}
	}

	@Override
	public ClassData fastForward(ClassData o) {
		Object data = o.getData();
		return ser(data);
	}
}
