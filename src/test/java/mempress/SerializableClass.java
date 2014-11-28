package mempress;

import java.io.Serializable;

/**
 * Created by Bartek on 2014-11-28.
 */
public class SerializableClass extends NonSerializableClass implements Serializable {
    protected int no = 1;

    public SerializableClass() {}

    public SerializableClass(int n) {
        super(n);
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }
}
