package mempress;

/**
 * Created by Bartek on 2014-11-25.
 */
public class ListElementWithHashCode<E> extends ListElement<E> {
    private int hashcode;

    public ListElementWithHashCode(ClassData data, Class<E> objectType, int hashcode) {
        super(data, objectType);
        this.hashcode = hashcode;
    }

    @Override
    public boolean compare(Object secondObj) {
        return Integer.compare(hashcode, secondObj.hashCode()) == 0 ? true : false;
    }

    @Override
    public void assign(ListElement<E> source) {
        ListElementWithHashCode<E> obj = (ListElementWithHashCode<E>) source;
        super.assign(source);
        hashcode = obj.hashcode;
    }

    public int getHashcode() {
        return hashcode;
    }

    public void setHashcode(int hashcode) {
        this.hashcode = hashcode;
    }

}
