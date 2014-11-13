package mempress;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bartek on 2014-11-13.
 */
public class DecisionTree<E> {
    private List<DecisionTreeElement> processors =
            new ArrayList<>();

    public DecisionTree() {
        processors.add(new DecisionSerializeIt());
        processors.add(new DecisionStoreIt());
    }

    public SmartListElement<E> processObject(E obj) {
        SmartListElement<E> tmp = null;

        for(DecisionTreeElement dte : processors) {
            if(dte.checkConditions(obj, 0)) {
                tmp = dte.processObject(obj);
                if(tmp != null)
                    break;
            }
        }

        return tmp;
    }

    public interface DecisionTreeElement {
        public boolean checkConditions(Object obj, long size);
        public <E> SmartListElement<E> processObject(E obj);
    }

    private static class DecisionStoreIt implements DecisionTreeElement {
        @Override
        public boolean checkConditions(Object obj, long size) {
            boolean cond = true;

            return true;
        }

        @Override
        public <E> SmartListElement<E> processObject(E obj) {
            return new SimpleSmartListElement<E>(obj);
        }
    }

    private static class DecisionSerializeIt implements DecisionTreeElement {
        @Override
        public boolean checkConditions(Object obj, long size) {
            boolean cond = true;
            cond = cond && obj instanceof Serializable;

            return cond;
        }

        @Override
        public <E> SmartListElement<E> processObject(E obj) {
            int hash = obj.hashCode();
            byte[] s = Serializer.ser(obj);
            Class<E> cl = (Class<E>)obj.getClass();
            return new ByteArraySmartListElement<>(hash, s, cl);
        }
    }
}
