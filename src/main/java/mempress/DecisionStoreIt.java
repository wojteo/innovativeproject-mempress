package mempress;

import mempress.DecisionTree.DecisionTreeElement;
import mempress.DecisionTree.ObjectDataCarrier;

public class DecisionStoreIt<E> implements DecisionTreeElement<E> {

	@Override
	public boolean checkConditions(E obj, ObjectDataCarrier metadata) {
		return true;
	}

	@Override
	public ListElement<E> processObject(E obj, ObjectDataCarrier metadata) {

//		long hash = obj.hashCode();
		Class<E> objType = (Class<E>)obj.getClass();
		
		Serializer pser = SerializerFactory.createSerializer(SerializerType.NoSerialized);
		ClassData cd = pser.ser(obj);
		
//		ListElement<E> elem = new WrappedListElement<E>(cd, hash, objType);
		ListElement<E> elem = new ListElement<>(cd, objType);
		return elem;
	}

	@Override
	public SerializerType getOperationType() {
		return SerializerType.NoSerialized;
	}

}
