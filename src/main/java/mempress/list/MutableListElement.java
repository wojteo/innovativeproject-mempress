package mempress.list;

import mempress.ClassData;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Bartek on 2014-12-19.
 */
public class MutableListElement<E> extends ListElement<E> {
    private AtomicInteger sharedCount = new AtomicInteger(0);

    public MutableListElement(ClassData data, Class<E> objectType) {
        super(data, objectType);
    }

    public MutableListElement(ListElement<E> listElement) {
        super(listElement.getData(), listElement.getObjectType());
        setUseCount(getUseCount());
        setIdentityHC(getIdentityHC());
    }

    public void decrementUseCount() {
        if (sharedCount.decrementAndGet() < 0)
            throw new IllegalStateException();
    }

    public void incrementUseCount() {
        sharedCount.incrementAndGet();
    }

    @Override
    public E get(boolean countIt) {
        while (sharedCount.get() != 0) ;
        return super.get(countIt);
    }

    @Override
    public ClassData getData() {
        while (sharedCount.get() != 0) ;
        return super.getData();
    }

    @Override
    public int compareTo(ListElement<E> o) {
        int ret = 0;
        try {
            ret = Integer.compare(sharedCount.get(), ((MutableListElement<E>) o).sharedCount.get());
        } catch (ClassCastException ex) {
        }
        if (ret == 0) {
            ret = super.compareTo(o);
        }

        return ret;
    }

    @Override
    public void assign(ListElement<E> source) {
        while (sharedCount.get() != 0) ;
        super.assign(source);
    }

    public boolean isAssignPossible() {
        return sharedCount.get() == 0;
    }

    public E getShared() {
        E obj = getObject();
        setUseCount(getUseCount() + 1);
        return obj;
    }
}
