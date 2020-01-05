package mempress.decision;

import mempress.ClassData;
import mempress.decision.DecisionTree.DecisionTreeElement;
import mempress.decision.DecisionTree.ObjectDataCarrier;
import mempress.list.ListElement;
import mempress.serialization.Serializer;
import mempress.serialization.SerializerFactory;
import mempress.serialization.SerializerType;

public class DecisionStoreIt<E> implements DecisionTreeElement<E> {

    @Override
    public boolean checkConditions(E obj, ObjectDataCarrier metadata) {
        return true;
    }

    @Override
    public ListElement<E> processObject(E obj, ObjectDataCarrier metadata) {

//		long hash = obj.hashCode();
        Class<E> objType = (Class<E>) obj.getClass();

        Serializer serializer = SerializerFactory.createSerializer(SerializerType.NoSerialized);
        ClassData cd = serializer.ser(obj);

//		ListElement<E> elem = new WrappedListElement<E>(cd, hash, objType);
        return new ListElement<>(cd, objType);
    }

    @Override
    public SerializerType getOperationType() {
        return SerializerType.NoSerialized;
    }

}
