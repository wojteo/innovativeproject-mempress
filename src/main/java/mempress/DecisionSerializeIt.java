package mempress;

import java.io.Serializable;
import mempress.DecisionTree.DecisionTreeElement;

public class DecisionSerializeIt<E> implements DecisionTreeElement<E> {
    @Override
    public boolean checkConditions(Object obj, DecisionTree.ObjectDataCarrier metadata) {
        boolean cond = true;
        cond = cond && obj instanceof Serializable;

        return cond;
    }

    @Override
    public SmartListElement<E> processObject(E obj, DecisionTree.ObjectDataCarrier metadata) {
        int hash = obj.hashCode();
        byte[] s = Serializer.ser(obj);
        
        @SuppressWarnings("unchecked")
		Class<E> cl = (Class<E>)obj.getClass();
        return new ByteArraySmartListElement<>(hash, s, cl);
    }

    @Override
    public Class<? extends SmartListElement<E>> getReturnType() {
        return ByteArraySmartListElement.class;
    }
}