package mempress;

/**
 * Created by Bartek on 2014-11-20.
 */
public class ClassData {
    private final SerializerType serializerType;
    private final Object data;
    private final long size;

    public ClassData(SerializerType serializerType, Object data, long size) {
        this.serializerType = serializerType;
        this.data = data;
        this.size = size;
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

}
