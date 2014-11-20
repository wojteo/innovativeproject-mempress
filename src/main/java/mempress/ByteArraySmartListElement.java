package mempress;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by bartek on 2014-11-13.
 */
@Deprecated
public class ByteArraySmartListElement<E> extends SmartListElement<E> {
	protected byte[] serialized;
	protected Class<E> objType;
	protected int size;

	public ByteArraySmartListElement(long checksum, byte[] sarray,
			Class<E> objectType) {
		super(checksum);
		if (sarray == null || objectType == null)
			throw new NullPointerException();
		this.serialized = sarray;
		this.objType = objectType;
		this.size = sarray.length;
	}

	@Override
	public E getObject() {
		Object des = null;

		ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			des = ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			// ignore
			//throw new MempressException("Failed to deserialize from byte array");
		}

		E retOb = objType.cast(des);
		return retOb;
	}
}
