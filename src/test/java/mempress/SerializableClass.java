package mempress;

import java.io.Serializable;

/**
 * Created by Bartek on 2014-11-28.
 */
public class SerializableClass extends NonSerializableClass implements Serializable {
    private int no = 1;

    public SerializableClass() {}

    public SerializableClass(int n) {
        no = n;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SerializableClass that = (SerializableClass) o;

        if (no != that.no) return false;

        return true;
    }

}
