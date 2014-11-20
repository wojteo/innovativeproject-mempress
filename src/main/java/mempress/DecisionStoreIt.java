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

		long hash = obj.hashCode();
		Class<E> objType; //TO-DO
		long size; //TO-DO
		
		ClassData cd = new ClassData(SerializerType.NoSerialized, obj, size);
		
		ListElement<E> elem = new WrappedListElement<E>(cd, hash, objectType);
		return elem;
	}

	@Override
	public SerializerType getOperationType() {
		return SerializerType.NoSerialized;
	}

}
