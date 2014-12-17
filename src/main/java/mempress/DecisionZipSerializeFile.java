package mempress;

import mempress.DecisionTree.DecisionTreeElement;
import mempress.DecisionTree.ObjectDataCarrier;

public class DecisionZipSerializeFile<E> implements DecisionTreeElement<E> {

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

		Serializer ser = SerializerFactory.createSerializer(SerializerType.ZipFileSerializer);
		ClassData cd = ser.ser(obj);
		
		ListElement<E> le = new ListElement<>(cd, objType);
		return le;
	}

	@Override
	public SerializerType getOperationType() {
		return SerializerType.ZipFileSerializer;
	}

}
