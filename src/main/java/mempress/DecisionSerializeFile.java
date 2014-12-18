package mempress;

import com.google.common.base.Preconditions;
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
		
		@SuppressWarnings("unchecked")
		Class<E> objType = (Class<E>)obj.getClass();

		Serializer ser = SerializerFactory.createSerializer(SerializerType.FileSerializer);
		ClassData cd = ser.ser(obj);
		
		ListElement<E> le = new ListElement<>(cd, objType);
		return le;
	}

	@Override
	public SerializerType getOperationType() {
		return SerializerType.FileSerializer;
	}

	@Override
	public boolean fastForwardAvailable(SerializerType from) {
		if(from == SerializerType.ByteArraySerializer)
			return true;
		else
			return false;
	}

	@Override
	public ListElement<E> fastForward(ListElement<E> source) {
		if(source.getData().getSerializerType() != SerializerType.ByteArraySerializer)
			throw new UnsupportedOperationException();
		source.lock.lock();
		try {
			ClassData classData = SerializerFactory.createSerializer(SerializerType.FileSerializer).fastForward(source.getData());
			return new ListElement<>(classData, source.getObjectType());
		} finally {
			source.lock.unlock();
		}
	}
}
