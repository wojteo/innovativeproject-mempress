package mempress;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Bartek on 2014-11-25.
 */
public class HashCodeSmartList<E> extends SmartList<E> {
    HashCodeSmartList() {
    }

    HashCodeSmartList(long maxWeight) {
        super(maxWeight);
    }

    HashCodeSmartList(DecisionTree<E> decTree, long maxWeight) {
        super(decTree, maxWeight);
    }

    HashCodeSmartList(DecisionTree<E> decTree, long maxWeight, long timeLimit) {
        super(decTree, maxWeight, timeLimit);
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
        else
            return new ListElementWithHashCode<>(le.data, le.objectType, obj.hashCode());
    }
}
