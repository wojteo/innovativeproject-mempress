package mempress.decision;

import mempress.ClassData;
import mempress.list.ListElement;
import mempress.serialization.Serializer;
import mempress.serialization.SerializerFactory;
import mempress.serialization.SerializerType;

/**
 * Created by Bartek on 2014-12-19.
 */
public class DecisionZipByteArray<E> implements DecisionTree.DecisionTreeElement<E> {

    @Override
    public boolean checkConditions(E obj, DecisionTree.ObjectDataCarrier metadata) {
        return obj instanceof java.io.Serializable;
    }

    @Override
    public ListElement<E> processObject(E obj, DecisionTree.ObjectDataCarrier metadata) {

        @SuppressWarnings("unchecked")
        Class<E> objType = (Class<E>) obj.getClass();

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
        return from == SerializerType.NoSerialized;
    }

    @Override
    public ListElement<E> fastForward(ListElement<E> source) {
        if (source.getData().getSerializerType() != SerializerType.NoSerialized)
            throw new UnsupportedOperationException("Not implemented yet");
        source.lock.lock();
        try {
            ClassData classData = SerializerFactory.createSerializer(SerializerType.ZipByteArraySerializer).fastForward(source.getData());
            return new ListElement<>(classData, source.getObjectType());
        } finally {
            source.lock.unlock();
        }
    }
}
