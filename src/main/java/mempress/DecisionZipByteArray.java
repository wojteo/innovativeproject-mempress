package mempress;

/**
 * Created by Bartek on 2014-12-19.
 */
public class DecisionZipByteArray<E> implements DecisionTree.DecisionTreeElement<E> {

    @Override
    public boolean checkConditions(E obj, DecisionTree.ObjectDataCarrier metadata) {
        if(obj instanceof java.io.Serializable)
            return true;
        return false;
    }

    @Override
    public ListElement<E> processObject(E obj, DecisionTree.ObjectDataCarrier metadata) {

        @SuppressWarnings("unchecked")
        Class<E> objType = (Class<E>)obj.getClass();

        Serializer ser = SerializerFactory.createSerializer(SerializerType.ZipByteArraySerializer);
        ClassData cd = ser.ser(obj);

        return new ListElement<>(cd, objType);
    }

    @Override
    public SerializerType getOperationType() {
        return SerializerType.ZipByteArraySerializer;
    }

    @Override
    public boolean fastForwardAvailable(SerializerType from) {
        if(from == SerializerType.NoSerialized)
            return true;
        else
            return false;
    }

    @Override
    public ListElement<E> fastForward(ListElement<E> source) {
        if(source.getData().getSerializerType() != SerializerType.NoSerialized)
            throw new UnsupportedOperationException();
        source.lock.lock();
        try {
            ClassData classData = SerializerFactory.createSerializer(SerializerType.ZipByteArraySerializer).fastForward(source.getData());
            return new ListElement<>(classData, source.getObjectType());
        } finally {
            source.lock.unlock();
        }
    }
}
