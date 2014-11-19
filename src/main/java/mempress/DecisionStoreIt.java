package mempress;

import mempress.DecisionTree.DecisionTreeElement;

public class DecisionStoreIt<E> implements DecisionTreeElement<E> {
    @Override
    public boolean checkConditions(Object obj, DecisionTree.ObjectDataCarrier metadata) {
        boolean cond = true;

        return cond;
    }

    @Override
    public SmartListElement<E> processObject(E obj, DecisionTree.ObjectDataCarrier metadata) {
		return new SimpleSmartListElement<E>(obj);
	}

    @Override
    public Class<? extends SmartListElement<E>> getReturnType() {
        return (Class<? extends SmartListElement<E>>) SimpleSmartListElement.class;
    }
}