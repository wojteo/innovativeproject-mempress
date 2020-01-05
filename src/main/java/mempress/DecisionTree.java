package mempress;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bartek on 2014-11-13.
 */
public class DecisionTree<E> {
    final List<DecisionTreeElement<E>> processors =
            new ArrayList<>();

    /*
    public DecisionTree() {
        processors.add(new DecisionSerializeIt());
        processors.add(new DecisionStoreIt());
    }*/

    /**
     * Konstruktor tylko o zasięgu pakietowym. Drzewo powinno być tworzone tylko przez DecisionTreeBuilder<E>
     */
    DecisionTree() {

    }

    /**
     * Degraduje obiekt w dół drzewa dopóki nie zostanie znalezione dopasowanie (jedna z metod checkConditions zwróci true)
     */
    public ListElement<E> demote(ListElement<E> wrappedObj) {
        wrappedObj.lock.lock();
        try {
            if (processors.get(processors.size() - 1).getOperationType() != wrappedObj.getData().getSerializerType()) {
                ObjectDataCarrier metadata = new ObjectDataCarrier();
                E obj = null;
                ListElement<E> tmp = null;
                SerializerType st = wrappedObj.getData().getSerializerType();
                int processorNum;
                boolean itsTimeToDemote = false;
                for (processorNum = 0; processorNum < processors.size() && !itsTimeToDemote; ++processorNum) {
                    DecisionTreeElement<E> dce = processors.get(processorNum);
                    if (dce.getOperationType() == st) {
                        itsTimeToDemote = true;
                        obj = wrappedObj.get();
                        break;
                    }
                }

                ++processorNum;
                if (itsTimeToDemote && processorNum < processors.size()) {
                    DecisionTreeElement<E> dte = processors.get(processorNum);
                    if (dte.fastForwardAvailable(wrappedObj.getData().getSerializerType())) {
                        tmp = dte.fastForward(wrappedObj);
                    } else {
                        obj = wrappedObj.get(false);
                        tmp = processObject(obj, processorNum);
                    }
                    wrappedObj.assign(tmp);
                    return wrappedObj;
                }
            }
        } finally {
            wrappedObj.lock.unlock();
        }

        throw new MempressException("Can't demote object");
    }

    public ListElement<E> processObject(E obj, int startPoint) {
        Preconditions.checkNotNull(obj);

        ListElement<E> tmp = null;
        int s = processors.size();
        Preconditions.checkArgument(startPoint >= 0 && startPoint < s);

        ObjectDataCarrier metadata = new ObjectDataCarrier();


        for (int i = startPoint; i < s; ++i) {
            DecisionTreeElement<E> dte = processors.get(i);
            if (dte.checkConditions(obj, metadata)) {
                try {
                    tmp = dte.processObject(obj, metadata);
                    if (tmp != null)
                        return tmp;
                } catch (OutOfMemoryError oom) {
                }
            }
        }

        throw new MempressException("Object isn't supported");
    }

    public ListElement<E> goBackToHighestState(ListElement<E> wrappedObj) {
        Preconditions.checkNotNull(wrappedObj);

        if (wrappedObj.getData().getSerializerType() == processors.get(0).getOperationType())
            return wrappedObj;

        E obj = wrappedObj.get();
        int uc = wrappedObj.getUseCount();
        //long tmcr = wrappedObj.getTimeCreated();

        ListElement<E> firstStateElem = processObject(obj);
        firstStateElem.setUseCount(uc);
        wrappedObj.assign(firstStateElem);

        return wrappedObj;
    }

    public ListElement<E> processObject(E obj) {
        return processObject(obj, 0);
    }

    public interface DecisionTreeElement<E> {
        boolean checkConditions(E obj, ObjectDataCarrier metadata);

        ListElement<E> processObject(E obj, ObjectDataCarrier metadata);

        SerializerType getOperationType();

        default boolean fastForwardAvailable(SerializerType from) {
            return false;
        }

        default ListElement<E> fastForward(ListElement<E> source) {
            throw new UnsupportedOperationException("Not implemented yet");
        }
    }

    public static class ObjectDataCarrier {
        public final Map<String, Object> data =
                new HashMap<String, Object>();
    }
}


