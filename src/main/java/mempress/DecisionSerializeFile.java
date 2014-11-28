package mempress;

import java.io.File;

import mempress.DecisionTree.DecisionTreeElement;
import mempress.DecisionTree.ObjectDataCarrier;

public class DecisionSerializeFile<E> implements DecisionTreeElement<E> {

	@Override
	public boolean checkConditions(E obj, ObjectDataCarrier metadata) {
		if(obj instanceof java.io.Serializable)
			return true;
		return false;
	}

	@Override
	public ListElement<E> processObject(E obj, ObjectDataCarrier metadata) {
		
//		long hash = obj.hashCode();
		Class<E> objType = (Class<E>)obj.getClass();

		Serializer ser = SerializerFactory.createSerializer(SerializerType.FileSerializer);
		ClassData cd = ser.ser(obj);
		
//		ListElement<E> le = new SerializedListElement<E>(cd, hash, objType);
		ListElement<E> le = new ListElement<>(cd, objType);
		return le;
	}

	@Override
	public SerializerType getOperationType() {
		return SerializerType.FileSerializer;
	}

}
