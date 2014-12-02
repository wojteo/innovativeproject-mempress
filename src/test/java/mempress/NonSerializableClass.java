package mempress;

import java.io.Serializable;

/**
 * Created by Bartek on 2014-11-28.
 */
public class NonSerializableClass {
    private int no = 2;

    public NonSerializableClass() {}

    public NonSerializableClass(int no) {
        this.no = no;
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

        NonSerializableClass that = (NonSerializableClass) o;

        if (no != that.no) return false;

        return true;
    }

}
