package mempress;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


/**
 * Created by bartek on 2014-11-13.
 */
public class ByteArraySmartListElement<E> extends SmartListElement<E> {
    protected byte[] serialized;
    protected Class<E> objType;
    protected int size;
    
    public ByteArraySmartListElement(long checksum, byte[] sarray, Class<E> objectType) {
        super(checksum);
        if(sarray == null || objectType == null)
            throw new NullPointerException();
        this.serialized = sarray;
        this.objType = objectType;
        this.size=sarray.length;
    }
    
    public static Object des(byte[] b){
		
  		ByteArrayInputStream bais = new ByteArrayInputStream(b);
  		Object o=null;
  		try {
  			ObjectInputStream ois = new ObjectInputStream(bais);
  			
  			o = ois.readObject();
  		} catch (ClassNotFoundException | IOException e) {
  			//ignore
  		}
  		
  		return o;
  	}

    @Override
    public E getObject() {
        Object des = des(serialized);
        E retOb = objType.cast(des);

        return retOb;
    }
}
