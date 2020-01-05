package mempress;

/**
 * Created by Bartek on 2014-11-25.
 */
public class ListElementWithHashCode<E> extends ListElement<E> {
    protected int hashcode;

    public ListElementWithHashCode(ClassData data, Class<E> objectType, int hashcode) {
        super(data, objectType);
        this.hashcode = hashcode;
    }

    @Override
    public boolean compare(Object secondObj) {
        boolean ret = hashcode == secondObj.hashCode();
        if (!ret)
            ret = super.compare(secondObj);

        return ret;
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

    @Override
    public int hashCode() {
        return hashcode;
    }

    // TODO: niekonsekwencja - zmiana działania metody przy nadpisaniu
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;

        if (getClass() != obj.getClass())
            return false;

        ListElementWithHashCode<E> le = (ListElementWithHashCode<E>) obj;

        if (getIdentityHC() == le.getIdentityHC())
            return true;

        //            return le.get(false).equals(get(false));
        return hashcode == le.hashcode;
    }
}
