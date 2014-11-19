package mempress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by bartek on 2014-11-13.
 */
public class DecisionTree<E> {
    List<DecisionTreeElement> processors =
            new ArrayList<>();

    /*
    public DecisionTree() {
        processors.add(new DecisionSerializeIt());
        processors.add(new DecisionStoreIt());
    }*/

    /**
     * Konstruktor tylko o zasięgu pakietowym. Drzewo może być tworzone tylko przez DecisionTreeBuilder<E>
     */
    DecisionTree() {

    }



    public SmartListElement<E> processObject(E obj) {
        SmartListElement<E> tmp = null;
        
        ObjectDataCarrier metadata = new ObjectDataCarrier();

        for(DecisionTreeElement dte : processors) {
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
    public SmartListElement<E> demote(SmartListElement<E> wrappedObj) {
        boolean itsTimeToDemote = false;
        ObjectDataCarrier metadata = new ObjectDataCarrier();
        E obj = null;
        SmartListElement<E> tmp = null;
        for(DecisionTreeElement dce : processors) {
            if(!itsTimeToDemote) {
                if(wrappedObj.getClass() == dce.getReturnType()) {
                    itsTimeToDemote = true;
                    obj = wrappedObj.get();
                }
            }
            else {
                if(dce.checkConditions(obj, metadata)) {
                    tmp = dce.processObject(obj, metadata);
                    if(tmp != null)
                        return tmp;
                }
            }
        }

        throw new MempressException("Can't demote object");
    }

    public interface DecisionTreeElement<E> {
        public boolean checkConditions(Object obj, ObjectDataCarrier metadata);
        public SmartListElement<E> processObject(E obj, ObjectDataCarrier metadata);
        public Class<? extends SmartListElement<E>> getReturnType();
    }

    static class ObjectDataCarrier
    {
        public final Map<String, Object> data =
                new HashMap<String, Object>();
    }
}


