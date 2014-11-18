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

    public static Object desf(File file) {
		FileInputStream bais;
		Object o=null;
		try {
			bais = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(bais);
			
			o = ois.readObject();
			ois.close();
		} catch (ClassNotFoundException | IOException e) {
			//ignore
		}
		
		return o;
    }

    public FileSmartListElement(long chck, File tmpFile, Class<E> obType) {
        super(chck);
        Preconditions.checkNotNull(tmpFile);
        Preconditions.checkNotNull(obType);

        storedData = tmpFile;
        objectType = obType;
    }

    @Override
    public E getObject() {
        Object des = desf(storedData);
        E et = objectType.cast(des);

        return et;
    }
}
