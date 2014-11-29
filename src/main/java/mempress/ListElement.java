package mempress;

import com.google.common.base.Preconditions;

/**
 * Created by Bartek on 2014-11-20.
 */
public class ListElement<E> implements Comparable<ListElement<E>> {
    protected ClassData data;
    protected Class<E> objectType;
    private int useCount;

    public ListElement(ClassData data, Class<E> objectType) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(objectType);
        this.data = data;
        this.objectType = objectType;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;

        if(getClass() != obj.getClass())
            return false;

        ListElement le = (ListElement)obj;
        return data.equals(le.data) &&
                objectType.equals(le.objectType) &&
                useCount == le.useCount;
    }

    public E get() {
        return get(true);
    }

    public E get(boolean countIt) {
        E e = getObject();
        if(countIt) {
            ++useCount;
        }
        return e;
    }

    protected E getObject() {
        Object obj = SerializerFactory
                .createSerializer(data.getSerializerType())
                .des(data);
        return objectType.cast(obj);
    }

    public long getSize() {
        return data.getSize();
    }

    public int getUseCount() {
        return useCount;
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

    public void assign(ListElement<E> source) {
        Preconditions.checkNotNull(source);

        data = source.data;
        objectType = source.objectType;
        useCount = source.useCount;
    }

    public boolean compare(Object secondObj) {
        return get(false).equals(secondObj);
    }
}
