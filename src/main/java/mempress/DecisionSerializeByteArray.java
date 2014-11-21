package mempress;

import mempress.DecisionTree.DecisionTreeElement;
import mempress.DecisionTree.ObjectDataCarrier;

public class DecisionSerializeByteArray<E> implements DecisionTreeElement<E> {

	@Override
	public boolean checkConditions(E obj, ObjectDataCarrier metadata) {
		if(obj instanceof java.io.Serializable)
			return true;
		return false;
	}

	@Override
	public ListElement<E> processObject(E obj, ObjectDataCarrier metadata) {
		
		long hash = obj.hashCode();
		Class<E> objType = (Class<E>)obj.getClass();

		Serializer ser = SerializerFactory.createSerializer(SerializerType.ByteArraySerializer);
		ClassData cd = ser.ser(obj);
		
		ListElement<E> le = new SerializedListElement<E>(cd, hash, objType);
		
		return le;
	}

	@Override
	public SerializerType getOperationType() {
		// TODO Auto-generated method stub
		return SerializerType.ByteArraySerializer;
	}

}
