package mempress;

import java.io.Serializable;
import mempress.DecisionTree.DecisionTreeElement;

public class DecisionSerializeIt implements DecisionTreeElement {
    @Override
    public boolean checkConditions(Object obj, ObjectDataCarrier metadata) {
        boolean cond = true;
        cond = cond && obj instanceof Serializable;

        return cond;
    }

    @Override
    public <E> SmartListElement<E> processObject(E obj, ObjectDataCarrier metadata) {
        int hash = obj.hashCode();
        byte[] s = Serializer.ser(obj);
        
        @SuppressWarnings("unchecked")
		Class<E> cl = (Class<E>)obj.getClass();
        return new ByteArraySmartListElement<>(hash, s, cl);
    }
}