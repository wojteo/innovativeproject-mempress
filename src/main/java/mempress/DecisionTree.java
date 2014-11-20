package mempress;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by bartek on 2014-11-13.
 */
public class DecisionTree<E> {
    List<DecisionTreeElement<E>> processors =
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



    public ListElement<E> processObject(E obj) {
        return processObject(obj, 0);
    }

    public ListElement<E> processObject(E obj, int startPoint) {
        Preconditions.checkNotNull(obj);

        ListElement<E> tmp = null;
        int s = processors.size();
        Preconditions.checkArgument(startPoint >= 0 && startPoint < s);

        ObjectDataCarrier metadata = new ObjectDataCarrier();


        for(int i = 0; i < s; ++i) {
            DecisionTreeElement<E> dte = processors.get(i);
            if(dte.checkConditions(obj, metadata)) {
                tmp = dte.processObject(obj, metadata);
                if(tmp != null)
                    return tmp;
            }
        }

        throw new MempressException("Object isn't supported");
    }

    /**
     * Degraduje obiekt w dół drzewa dopóki nie zostanie znalezione dopasowanie (jedna z metod checkConditions zwróci true)
     * @param wrappedObj
     * @return
     */
    public ListElement<E> demote(ListElement<E> wrappedObj) {
        boolean itsTimeToDemote = false;
        ObjectDataCarrier metadata = new ObjectDataCarrier();
        E obj = null;
        ListElement<E> tmp = null;
        SerializerType st = wrappedObj.getData().getSerializerType();
        for(int i = 0, s = processors.size(); i < s; ++i) {
            DecisionTreeElement<E> dce = processors.get(i);
            if(!itsTimeToDemote) {
                if(dce.getOperationType() == st) {
                    itsTimeToDemote = true;
                    obj = wrappedObj.get();
                }
            }
            else {
                tmp = processObject(obj, i);
                ListElement.assign(wrappedObj, tmp);
            }
        }

        throw new MempressException("Can't demote object");
    }

    public interface DecisionTreeElement<E> {
        public boolean checkConditions(E obj, ObjectDataCarrier metadata);
        public ListElement<E> processObject(E obj, ObjectDataCarrier metadata);
        public SerializerType getOperationType();
    }

    static class ObjectDataCarrier
    {
        public final Map<String, Object> data =
                new HashMap<String, Object>();
    }
}


