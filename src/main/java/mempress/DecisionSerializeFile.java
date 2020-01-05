package mempress;

import mempress.DecisionTree.DecisionTreeElement;
import mempress.DecisionTree.ObjectDataCarrier;

public class DecisionSerializeFile<E> implements DecisionTreeElement<E> {

    @Override
    public boolean checkConditions(E obj, ObjectDataCarrier metadata) {
        return obj instanceof java.io.Serializable;
    }

    @Override
    public ListElement<E> processObject(E obj, ObjectDataCarrier metadata) {

        @SuppressWarnings("unchecked")
        Class<E> objType = (Class<E>) obj.getClass();

        Serializer ser = SerializerFactory.createSerializer(SerializerType.FileSerializer);
        ClassData cd = ser.ser(obj);

        return new ListElement<>(cd, objType);
    }

    @Override
    public SerializerType getOperationType() {
        return SerializerType.FileSerializer;
    }

    @Override
    public boolean fastForwardAvailable(SerializerType from) {
        return from == SerializerType.ByteArraySerializer;
    }

    @Override
    public ListElement<E> fastForward(ListElement<E> source) {
        if (source.getData().getSerializerType() != SerializerType.ByteArraySerializer)
            throw new UnsupportedOperationException("Not implemented yet");
        source.lock.lock();
        try {
            ClassData classData = SerializerFactory.createSerializer(SerializerType.FileSerializer).fastForward(source.getData());
            return new ListElement<>(classData, source.getObjectType());
        } finally {
            source.lock.unlock();
        }
    }
}
