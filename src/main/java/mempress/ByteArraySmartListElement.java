package mempress;

import java.io.IOException;

/**
 * Created by bartek on 2014-11-13.
 */
public class ByteArraySmartListElement<E> extends SmartListElement<E> {
    protected byte[] serialized;
    protected Class<E> objType;

    public ByteArraySmartListElement(long checksum, byte[] sarray, Class<E> objectType) {
        super(checksum);
        if(sarray == null || objectType == null)
            throw new NullPointerException();
        serialized = sarray;
        objType = objectType;
    }

    @Override
    public E getObject() {
        Object des = Serializer.des(serialized);
        E retOb = objType.cast(des);

        return retOb;
    }
}
