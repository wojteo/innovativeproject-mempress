package mempress;

import mempress.DecisionTree.DecisionTreeElement;
import mempress.DecisionTree.ObjectDataCarrier;

public class DecisionZipSerializeFile<E> implements DecisionTreeElement<E> {

    @Override
    public boolean checkConditions(E obj, ObjectDataCarrier metadata) {
        return obj instanceof java.io.Serializable;
    }

    @Override
    public ListElement<E> processObject(E obj, ObjectDataCarrier metadata) {

        @SuppressWarnings("unchecked")
        Class<E> objType = (Class<E>) obj.getClass();

        Serializer ser = SerializerFactory.createSerializer(SerializerType.ZipFileSerializer);
        ClassData cd = ser.ser(obj);

        return new ListElement<>(cd, objType);
    }

    @Override
    public SerializerType getOperationType() {
        return SerializerType.ZipFileSerializer;
    }

}
