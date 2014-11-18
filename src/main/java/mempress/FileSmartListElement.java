package mempress;

import com.google.common.base.Preconditions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by bartek on 2014-11-13.
 */
public class FileSmartListElement<E> extends SmartListElement<E> {
	protected File storedData;
	protected Class<E> objectType;

	public FileSmartListElement(long chck, File tmpFile, Class<E> obType) {
		super(chck);
		Preconditions.checkNotNull(tmpFile);
		Preconditions.checkNotNull(obType);

		storedData = tmpFile;
		objectType = obType;
	}

	@Override
	public E getObject() {

		FileInputStream bais;
		Object des = null;
		try {
			bais = new FileInputStream(storedData);
			ObjectInputStream ois = new ObjectInputStream(bais);
			des = ois.readObject();
			ois.close();
		} catch (ClassNotFoundException | IOException e) {
			// ignore
			//throw new MempressException("Failed to deserialize from file");
		}

		E et = objectType.cast(des);
		return et;
	}
}
