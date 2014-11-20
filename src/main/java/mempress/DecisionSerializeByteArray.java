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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SerializerType getOperationType() {
		// TODO Auto-generated method stub
		return SerializerType.ByteArraySerializer;
	}

}
