package mempress;

import com.google.common.base.Preconditions;

import java.util.*;

/**
 * Created by Bartek on 2014-11-25.
 */
public class HashCodeSmartList<E> extends SmartList<E> {
//    protected Map<ListElementWithHashCode<E>, Integer> holdElements;
    protected Set<ListElementWithHashCode<E>> holdElements;

    protected HashCodeSmartList() {
        init();
    }

    protected HashCodeSmartList(long maxWeight) {
        super(maxWeight); init();
    }

    protected HashCodeSmartList(DecisionTree<E> decTree, long maxWeight) {
        super(decTree, maxWeight); init();
    }

    protected HashCodeSmartList(DecisionTree<E> decTree, long maxWeight, long timeLimit) {
        super(decTree, maxWeight, timeLimit);
        init();
    }

    private void init() {
        if(getMaximumWeight() > 0 || getTimeLimit() > 0) {
            holdElements = Collections.synchronizedSet(new HashSet<ListElementWithHashCode<E>>());
        } else {
            holdElements = new HashSet<>();
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        final int[] counter = {0};
        int colsize = c.size();
        final Collection<?> collection = c;

        Preconditions.checkNotNull(c);
        Preconditions.checkArgument(colsize > 0);

        try {
            _list.stream().map(el -> (ListElementWithHashCode<E>)el).forEach(le -> {
                for(Object o : collection)
                    if(le.getHashcode() == o.hashCode())
                        counter[0] += 1;
            });
        } catch(ClassCastException cce) {
            return false;
        }

        return counter[0] == colsize;
    }

    @Override
    protected ListElement<E> wrapToListElement(E obj) {
        ListElement<E> le = super.wrapToListElement(obj);
        if(le == null)
            return null;

        ListElementWithHashCode<E> lewh =
                new ListElementWithHashCode<>(le.getData(), le.objectType, obj.hashCode());
        lewh.setIdentityHC(le.getIdentityHC());

        if(!holdElements.add(lewh)) {
            for(ListElementWithHashCode<E> listElementWithHashCode : holdElements) {
                if(listElementWithHashCode.equals(lewh)) {
                    lewh = listElementWithHashCode;
                    break;
                }
            }
        }

        return lewh;
    }

}
