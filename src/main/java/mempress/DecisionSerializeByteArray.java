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
		
//		long hash = obj.hashCode();
		Class<E> objType = (Class<E>)obj.getClass();

		Serializer ser = SerializerFactory.createSerializer(SerializerType.ByteArraySerializer);
		ClassData cd = ser.ser(obj);
		
//		ListElement<E> le = new SerializedListElement<E>(cd, hash, objType);
		ListElement<E> le = new ListElement<>(cd, objType);
		return le;
	}

	@Override
	public SerializerType getOperationType() {
		// TODO Auto-generated method stub
		return SerializerType.ByteArraySerializer;
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
			ClassData classData = SerializerFactory.createSerializer(SerializerType.ByteArraySerializer).fastForward(source.getData());
			return new ListElement<>(classData, source.getObjectType());
		} finally {
			source.lock.unlock();
		}
	}
}
