package mempress;

/**
 * Created by Bartek on 2014-11-20.
 */
public class ClassData {
    private final SerializerType serializerType;
    private final Object data;
    private final long size;
    private final long hashCode;

    public ClassData(SerializerType serializerType, Object data, long size, long hashCode) {
        this.serializerType = serializerType;
        this.data = data;
        this.size = size;
        this.hashCode = hashCode;
    }

    public SerializerType getSerializerType() {
        return serializerType;
    }

    public Object getData() {
        return data;
    }

    public long getSize() {
        return size;
    }

    public long getHashCode() {
        return hashCode;
    }
}
