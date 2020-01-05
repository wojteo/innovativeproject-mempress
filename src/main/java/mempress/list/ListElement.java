package mempress.list;

import com.google.common.base.Preconditions;
import mempress.ClassData;
import mempress.serialization.SerializerFactory;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Bartek on 2014-11-20.
 */
public class ListElement<E> implements Comparable<ListElement<E>> {
    protected ClassData data;
    public Class<E> objectType;
    public final ReentrantLock lock = new ReentrantLock(); //todo protected/private
    private int useCount;
    private int identityHC;
    private final long timeCreated;

    public ListElement(ClassData data, Class<E> objectType) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(objectType);
        this.data = data;
        this.objectType = objectType;
        timeCreated = System.nanoTime();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;

        if (getClass() != obj.getClass())
            return false;

        ListElement le = (ListElement) obj;
        return getData().equals(le.getData()) &&
                objectType.equals(le.objectType) &&
                useCount == le.useCount;
    }

    public E get() {
        return get(true);
    }

    public E get(boolean countIt) {
        E e = getObject();
        if (countIt) {
            ++useCount;
        }
        return e;
    }

    // nie używa getData
    protected E getObject() {
        E e = null;
        lock.lock();
        try {
            Object obj = SerializerFactory
                    .createSerializer(data.getSerializerType())
                    .des(data);
            e = objectType.cast(obj);
        } finally {
            lock.unlock();
        }

        return e;
    }

    public long getSize() {
        return getData().getSize();
    }

    // NO LOCK
    public int getUseCount() {
        return useCount;
    }

    public ClassData getData() {
        lock.lock();
        ClassData cd = null;
        try {
            cd = data;
        } finally {
            lock.unlock();
        }

        return cd;
    }

    protected void setData(ClassData data) {
        Preconditions.checkNotNull(data);
        lock.lock();
        try {
            this.data = data;
        } finally {
            lock.unlock();
        }
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    // NO LOCK
    @Override
    public int compareTo(ListElement<E> o) {
        int ret = Boolean.compare(lock.isLocked(), o.lock.isLocked());

        if (ret == 0)
            ret = Integer.compare(getUseCount(), o.getUseCount());

        if (ret == 0)
            ret = -Long.compare(getSize(), o.getSize());

        return ret;
    }

    public void assign(ListElement<E> source) {
        Preconditions.checkNotNull(source);
        lock.lock();
        try {
            data = source.data;
            objectType = source.objectType;
//            useCount = source.useCount;
//            timeCreated = source.timeCreated;
        } finally {
            lock.unlock();
        }
    }

    public int getIdentityHC() {
        return identityHC;
    }

    public void setIdentityHC(int identityHC) {
        this.identityHC = identityHC;
    }

    public boolean compare(Object secondObj) {
        if (System.identityHashCode(secondObj) == identityHC)
            return true;
        else
            return get(false).equals(secondObj);
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public Class<E> getObjectType() {
        lock.lock();
        try {
            return objectType;
        } finally {
            lock.unlock();
        }
    }
}
