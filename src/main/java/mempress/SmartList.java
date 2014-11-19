package mempress;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingList;
import javafx.beans.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.IOException;
import java.util.*;
import java.util.Observable;

/**
 * Klasa listy; DO NAPISANIA
 * @param <E>
 */
public class SmartList<E> implements List<E> {
    private List<SmartListElement<E>> _list;
    private DecisionTree<E> _decisionTree;
    private PriorityQueue<SmartListElement<E>> _serializationQueue;
    private SimpleLongProperty currentSize;
    private final long maxWeight;

    public SmartList() {
        _list = new SmartList<>();
        _serializationQueue = new PriorityQueue<>();
        currentSize = new SimpleLongProperty(0);
        maxWeight = 2048;

    }

    @Override
    public int size() {
        return _list.size();
    }

    @Override
    public Iterator<E> iterator() {
        return new IteratorDecorator(_list.iterator());
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
        return null;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    /**
     * Niezaimplementowane
     * @param c
     * @return
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        int counter = 0;
        int colsize = c.size();

        Preconditions.checkNotNull(c);
        Preconditions.checkArgument(colsize > 0);


        for(SmartListElement<E> sle : _list) {
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

        for(SmartListElement<E> sle : _list) {
            for(long hc : hashcodes) {
                if(sle.hashcode == hc)
                    ++counter;
            }
        }

        return counter == collectionSize;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Preconditions.checkNotNull(c);
        SmartListElement<E> sle;
        boolean b = true;
        for(E e : c) {
            sle = _decisionTree.processObject(e);
            b = b && _list.add(sle);
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
        return false;
    }

    /**
     * Niezaimplementowane
     * @param c
     * @return
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    /**
     * Niezaimplementowane
     * @param c
     * @return
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
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
        return false;
    }

    @Override
    public int hashCode() {
        return _list.hashCode();
    }

    /**
     * Niezaimplementowane
     * @param index
     * @param element
     * @return
     */
    @Override
    public E set(int index, E element) {
        return null;
    }

    @Override
    public void add(int index, E element) {
        SmartListElement<E> el = _decisionTree.processObject(element);
        _list.add(index, el);
    }

    @Override
    public E remove(int index) {
        SmartListElement<E> el = _list.remove(index);
        E obj = el.get();
        try { el.release(); } catch(IOException ioe) {}
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
            if(_list.get(i).hashcode == hc)
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
            if(_list.get(i).hashcode == hc)
                return i;
        }

        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new ListIteratorDecorator(_list.listIterator());
    }

    /**
     * Niezaimplementowane
     * @param index
     * @return
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        return null;
    }

    /**
     * Niezaimplementowane
     * @param fromIndex
     * @param toIndex
     * @return
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return _list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {

        for(SmartListElement<E> sle : _list) {
            if(sle.get().equals(o))
                return true;
        }

        return false;
    }

    public boolean containsByHashCode(Object o) {
        long hc = o.hashCode();
        for(SmartListElement<E> sle : _list) {
            if(sle.hashcode == hc)
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
        SmartListElement<E> sle = _list.get(index);
        E obj = sle.get();
        int useCount = sle.useCount;
        sle = _decisionTree.processObject(obj);
        sle.useCount = useCount;
        _list.set(index, sle);
        return obj;
    }

    @Override
    public boolean add(E e) {
        SmartListElement<E> element = _decisionTree.processObject(e);
        return _list.add(element);
    }

    // TODO: Dokończyć pisanie
    protected void demoteElements(int numOfElements) {
        long releasedBytes = 0;
        long tmp;
        for(int i = 0; i < numOfElements; ++i) {
            SmartListElement<E> sle = _serializationQueue.poll();
            if(sle == null) continue;
            tmp = sle.getObjectSize();
            sle = _decisionTree.demote(sle);
            if(sle == null)
                continue;
            releasedBytes += Math.abs(tmp - sle.getObjectSize());
            // TODO: Co to k**** jest?!

        }

    }

    private class ListIteratorDecorator implements ListIterator<E> {
        private final ListIterator<SmartListElement<E>> it;

        public ListIteratorDecorator(ListIterator<SmartListElement<E>> iterator) {
            it = iterator;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public E next() {
            return it.next().get();
        }

        @Override
        public boolean hasPrevious() {
            return it.hasPrevious();
        }

        @Override
        public E previous() {
            return it.previous().get();
        }

        @Override
        public int nextIndex() {
            return it.nextIndex();
        }

        @Override
        public int previousIndex() {
            return it.previousIndex();
        }

        @Override
        public void remove() {
            it.remove();
        }

        @Override
        public void set(E e) {
            it.set(_decisionTree.processObject(e));
        }

        @Override
        public void add(E e) {
            it.add(_decisionTree.processObject(e));
        }
    }

    private class IteratorDecorator implements Iterator<E> {
        private final Iterator<SmartListElement<E>> it;

        public IteratorDecorator(Iterator<SmartListElement<E>> iterator) {
            it = iterator;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public E next() {
            return it.next().get();
        }
    }

    private class ListWeightListener {
        private final long weightLimit;
        private long currentWeight;

        public ListWeightListener(long weightLimit) {
            Preconditions.checkArgument(weightLimit > 0, "Weight limit of list elements must be greater than zero");
            this.weightLimit = weightLimit;
        }

        public void increase(long val) {
            Preconditions.checkArgument(val > 0);
            currentWeight += val;

            if(currentWeight > weightLimit)
                tryToShrink();
        }

        public void decrease(long val) {
            Preconditions.checkArgument(val > 0);
            currentWeight -= val;
            if(currentWeight < 0)
                currentWeight = 0;
        }

        // TODO: wywołuje demoteElements
        public void tryToShrink() {

        }

    }
}
