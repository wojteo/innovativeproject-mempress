package mempress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

    public interface DecisionTreeElement {
        public boolean checkConditions(Object obj, ObjectDataCarrier metadata);
        public <E> SmartListElement<E> processObject(E obj, ObjectDataCarrier metadata);
    }

}

class ObjectDataCarrier
{
  public final Map<String, Object> data =
    new HashMap<String, Object>();
}
