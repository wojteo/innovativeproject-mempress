package mempress;

import com.google.common.base.Preconditions;

/**
 * Created by Bartek on 2014-11-20.
 */
public abstract class ListElement<E> implements Comparable<ListElement<E>> {
    protected ClassData data;
    private long hashcode;
    protected Class<E> objectType;
    private int useCount;

    public ListElement(ClassData data, long hashcode, Class<E> objectType) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(objectType);
        this.data = data;
        this.hashcode = hashcode;
        this.objectType = objectType;
    }

    public E get() {
        E e = getObject();
        ++useCount;
        return e;
    }

    protected abstract E getObject();

    public long getSize() {
        return data.getSize();
    }

    public long getHashCode() {
        return hashcode;
    }

    public int getUseCount() {
        return useCount;
    }

    public Class<E> getObjectType() {
        return objectType;
    }

    public ClassData getData() {
        return data;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    @Override
    public int compareTo(ListElement<E> o) {
        int ret = Integer.compare(getUseCount(), o.getUseCount());

        if(ret == 0)
            ret = -Long.compare(getSize(), o.getSize());

        return ret;
    }

    public static <E> void assign(ListElement<E> target, ListElement<E> source) {
        Preconditions.checkNotNull(target);
        Preconditions.checkNotNull(source);

        target.data = source.data;
        target.hashcode = source.hashcode;
        target.objectType = source.objectType;
        target.useCount = source.useCount;
    }
}
