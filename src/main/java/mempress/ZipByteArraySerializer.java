package mempress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ZipByteArraySerializer implements Serializer {

	@Override
	public ClassData ser(Object obj) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			byte[] b = baos.toByteArray();
			byte[] b_compressed = QuickLZ.compress(b, 1);
			b=null;
			ClassData cd = new ClassData(SerializerType.ZipByteArraySerializer, b_compressed, b_compressed.length);

			return cd;

		} catch (IOException e) {
			throw new MempressException("Couldn't serialize ByteArray");
		}
	}

	@Override
	public Object des(ClassData cd) {

		byte[] b = QuickLZ.decompress((byte[])cd.getData());
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		Object o = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			o = ois.readObject();

			return o;
		} catch (ClassNotFoundException | IOException e) {
			throw new MempressException("Couldn't deserialize ByteArray");
		}
	}

}
