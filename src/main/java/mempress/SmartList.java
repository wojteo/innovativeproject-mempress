package mempress;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Observable;
import java.util.stream.Collectors;

/**
 * Klasa listy; DO NAPISANIA
 * @param <E>
 */
public class SmartList<E> implements List<E> {
    //private List<ListElement<E>> _list;
    protected List<ListElement<E>> _list;
    private DecisionTree<E> _decisionTree;
    private PriorityQueue<ListElement<E>> _serializationQueue;
    private ListWeightListener weight;

    public SmartList() {
        this(Long.MAX_VALUE);
        _decisionTree = DecisionTreeBuilder.<E>buildDefaultTree();
    }

    public SmartList(long maxWeight) {
        _list = new ArrayList<>();
        _serializationQueue = new PriorityQueue<>();
        weight = new ListWeightListener(maxWeight);
        _decisionTree = DecisionTreeBuilder.<E>buildDefaultTree();
    }

    public SmartList(DecisionTree<E> decTree, long maxWeight) {
        this(maxWeight);
        _decisionTree = decTree;
    }

    @Override
    public int size() {
        return _list.size();
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        int size = size();
        Object[] array = new Object[size()];
        for(int i = 0; i < size; ++i)
            array[i] = _list.get(i).get();
        return array;
    }

    /**
     * Niezaimplementowane
     * @param a
     * @param <T>
     * @return
     */
    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        int counter = 0;
        int colsize = c.size();

        Preconditions.checkNotNull(c);
        Preconditions.checkArgument(colsize > 0);


        for(ListElement<E> sle : _list) {
            Object obj = sle.get();
            if(obj == null)
                continue;
            for(Object o : c) {
                if(obj.equals(o))
                    ++counter;
            }
        }

        return counter == colsize;
    }

    public boolean containsAllByHashCode(Collection<?> c) {
        int counter = 0, collectionSize = c.size();

        long[] hashcodes = c.stream()
                .mapToLong((el) -> el.hashCode())
                .toArray();

        Preconditions.checkArgument(collectionSize > 0);

        for(ListElement<E> sle : _list) {
            for(long hc : hashcodes) {
                if(sle.getHashCode() == hc)
                    ++counter;
            }
        }

        return counter == collectionSize;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Preconditions.checkNotNull(c);
        ListElement<E> sle;
        boolean b = true;
        for(E e : c) {
            sle = _decisionTree.processObject(e);
            b = b && _list.add(sle) && _serializationQueue.add(sle);
            weight.increase(sle.getSize());
        }

        return b;
    }

    /**
     * Niezaimplementowane
     * @param index
     * @param c
     * @return
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Niezaimplementowane
     * @param c
     * @return
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Niezaimplementowane
     * @param c
     * @return
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        _list.clear();
    }

    /**
     * Niezaimplementowane
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return _list.hashCode();
    }

    @Override
    public E set(int index, E element) {
        Preconditions.checkNotNull(element);
        ListElement<E> el = _decisionTree.processObject(element),
                old = _list.set(index, el);
        _serializationQueue.remove(old);
        weight.decrease(old.getSize());
        weight.increase(el.getSize());
        return old.get();
    }

    @Override
    public void add(int index, E element) {
        ListElement<E> el = _decisionTree.processObject(element);
        _list.add(index, el);
        _serializationQueue.add(el);
        weight.increase(el.getSize());
    }

    @Override
    public E remove(int index) {
        ListElement<E> el = _list.remove(index);
        E obj = el.get();
        _serializationQueue.remove(el);
        weight.decrease(el.getSize());

        return obj;
    }

    @Override
    public int indexOf(Object o) {
        int size = size();
        for(int i = 0; i < size; ++i) {
            Object obj = _list.get(i).get();
            if(obj != null && obj.equals(o))
                return i;
        }

        return -1;
    }

    public int indexOfByHashCode(Object o) {
        int size = size();
        int hc = o.hashCode();
        for(int i = 0; i < size; ++i) {
            if(_list.get(i).getHashCode() == hc)
                return i;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        Preconditions.checkNotNull(o);
        for(int i = size() - 1; i >= 0; --i) {
            Object obj = _list.get(i).get();
            if(obj != null && obj.equals(o))
                return i;
        }

        return -1;
    }

    public int lastIndexOfByHashCode(Object o) {
        int hc = o.hashCode();

        for(int i = size() - 1; i >= 0; --i) {
            if(_list.get(i).getHashCode() == hc)
                return i;
        }

        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    /**
     * Niezaimplementowane
     * @param index
     * @return
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * Niezaimplementowane
     * @param fromIndex
     * @param toIndex
     * @return
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return _list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {

        for(ListElement<E> sle : _list) {
            if(sle.get().equals(o))
                return true;
        }

        return false;
    }

    public boolean containsByHashCode(Object o) {
        long hc = o.hashCode();
        for(ListElement<E> sle : _list) {
            if(sle.getHashCode() == hc)
                return true;
        }
        return false;
    }

    /**
     * Od momentu odczytania elementu, jest on trzymany w formie zdekodowanej
     * @param index
     * @return
     */
    @Override
    public E get(int index) {
        ListElement<E> sle = _list.get(index);
        _decisionTree.goBackToHighestState(sle);
        E obj = sle.get();
        return obj;
    }

    @Override
    public boolean add(E e) {
        ListElement<E> element = _decisionTree.processObject(e);
        if(element == null) return false;
        boolean ret =  _list.add(element) && _serializationQueue.add(element);
        weight.increase(element.getSize());
        return ret;
    }

    public long getMaximumWeight() { return weight.weightLimit; }

    public long getCurrentWeight() { return weight.currentWeight; }

    // TODO: Dokończyć pisanie
    protected long demoteElements(int numOfElements) {
        long releasedBytes = 0;
        long tmp;
        for(int i = 0; i < numOfElements; ++i) {
            ListElement<E> sle = _serializationQueue.poll();
            if(sle == null) continue;
            int index = _list.indexOf(sle);
            tmp = sle.getSize();
            sle = _decisionTree.demote(sle);
            if(sle == null)
                continue;
            releasedBytes += Math.abs(tmp - sle.getSize());
            _list.set(index, sle);
            _serializationQueue.add(sle);
        }

        return releasedBytes;
    }


    private class ListWeightListener {
        private final long weightLimit;
        private long currentWeight;

        public ListWeightListener(long weightLimit) {
            Preconditions.checkArgument(weightLimit > 0, "Weight limit of list elements must be greater than zero");
            this.weightLimit = weightLimit;
        }

        public void increase(long val) {
            Preconditions.checkArgument(val >= 0);
            currentWeight += val;

            if(currentWeight > weightLimit)
                tryToShrink();
        }

        public void decrease(long val) {
            Preconditions.checkArgument(val >= 0);
            currentWeight -= val;
            if(currentWeight < 0)
                currentWeight = 0;
        }

        /**
         * Próbuje zwolnić miejsce zajmowane przez listę. Póki co maksymalna liczba prób to liczba elementów w liście
         */
        public void tryToShrink() {
            long tmp;
            int attemptLeft = _list.size();
            while (currentWeight > weightLimit && attemptLeft > 0) {
                tmp = demoteElements(1);
                System.out.println("tmp: " + tmp);
                currentWeight -= tmp;
                --attemptLeft;
            }
        }

    }
}
