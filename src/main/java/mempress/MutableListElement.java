package mempress;

/**
 * Created by Bartek on 2014-12-19.
 */
public class MutableListElement<E> extends ListElement<E> {
    private volatile int sharedCount;

    public MutableListElement(ClassData data, Class<E> objectType) {
        super(data, objectType);
    }

    public MutableListElement(ListElement<E> listElement) {
        super(listElement.getData(), listElement.getObjectType());
        setUseCount(getUseCount());
        setIdentityHC(getIdentityHC());
    }

    public void decrementUseCount() {
        --sharedCount;
        if(sharedCount < 0)
            throw new IllegalStateException();
    }

    public void incrementUseCount() {
        ++sharedCount;
    }

    @Override
    public E get(boolean countIt) {
        while (sharedCount != 0);
        return super.get(countIt);
    }

    @Override
    public ClassData getData() {
        while (sharedCount != 0);
        return super.getData();
    }

    @Override
    public void assign(ListElement<E> source) {
        while (sharedCount != 0);
        super.assign(source);
    }

    public boolean isAssignPossible() {
        return sharedCount == 0;
    }

    public E getShared() {
        E obj = getObject();
        setUseCount(getUseCount() + 1);
        return obj;
    }

    @Override
    public int compareTo(ListElement<E> o) {
        int ret = 0;
        try {
            ret = Integer.compare(sharedCount, ((MutableListElement<E>)o).sharedCount);
        } catch (ClassCastException ex) {}
        if(ret == 0) {
            ret = super.compareTo(o);
        }

        return ret;
    }
}
