package mempress;

import mempress.DecisionTree.DecisionTreeElement;

public class DecisionStoreIt implements DecisionTreeElement {
    @Override
    public boolean checkConditions(Object obj, ObjectDataCarrier metadata) {
        boolean cond = true;

        return cond;
    }

    @Override
    public <E> SmartListElement<E> processObject(E obj, ObjectDataCarrier metadata) {
		return new SimpleSmartListElement<E>(obj);
	}
}