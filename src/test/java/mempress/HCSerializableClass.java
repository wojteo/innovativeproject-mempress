package mempress;

import java.io.Serializable;

/**
 * Created by Bartek on 2014-11-29.
 */
public class HCSerializableClass implements Serializable {
    public static int numOfHashCodeCalls = 0;
    private static int id = 0;
    private int myId;
    private String something;

    public HCSerializableClass(String something) {
        this.something = something;
        myId = id++;
    }

    public HCSerializableClass(int myId, String something) {
        this.myId = myId;
        this.something = something;
    }

    public void setMyId(int myId) {
        this.myId = myId;
    }

    public int getMyId() {
        return myId;
    }

    public String getSomething() {
        return something;
    }

    public void setSomething(String something) {
        this.something = something;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HCSerializableClass that = (HCSerializableClass) o;

        if (myId != that.myId) return false;
        if (something != null ? !something.equals(that.something) : that.something != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = myId;
        result = 31 * result + (something != null ? something.hashCode() : 0);

        ++numOfHashCodeCalls;

        return result;
    }
}
