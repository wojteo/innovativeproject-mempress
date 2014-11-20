package mempress;

import mempress.DecisionTree.DecisionTreeElement;
import mempress.DecisionTree.ObjectDataCarrier;

public class DecisionSerializeFile<E> implements DecisionTreeElement<E> {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

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
		return SerializerType.FileSerializer;
	}

}
