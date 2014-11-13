package mempress;

import com.google.common.collect.ForwardingList;

import java.io.IOException;
import java.util.*;

/**
 * Klasa listy; DO NAPISANIA
 * @param <E>
 */
public class SmartList<E> implements List<E> {
    private List<SmartListElement<E>> _list =
            new ArrayList<>();
    private DecisionTree<E> decisionTree = new DecisionTree<>();

    @Override
    public int size() {
        return _list.size();
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        int size = size();
        Object[] array = new Object[size()];
        for(int i = 0; i < size; ++i)
            array[i] = _list.get(i).getObject();
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
        return false;
    }

    /**
     * Niezaimplementowane
     * @param c
     * @return
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
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
        SmartListElement<E> el = decisionTree.processObject(element);
        _list.add(index, el);
    }

    @Override
    public E remove(int index) {
        SmartListElement<E> el = _list.remove(index);
        E obj = el.getObject();
        try { el.release(); } catch(IOException ioe) {}
        return obj;
    }

    /**
     * Niezaimplementowane
     * @param o
     * @return
     */
    @Override
    public int indexOf(Object o) {
        return 0;
    }

    /**
     * Niezaimplementowane
     * @param o
     * @return
     */
    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    /**
     * Niezaimplementowane
     * @return
     */
    @Override
    public ListIterator<E> listIterator() {
        return null;
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
        long hash = o.hashCode();
        boolean ret = false;
        for(SmartListElement<E> sle : _list) {
            if(sle.getChecksum() == hash) {
                ret = true;
                break;
            }
        }

        return ret;
    }

    @Override
    public E get(int index) {
        return _list.get(index).getObject();
    }

    @Override
    public boolean add(E e) {
        SmartListElement<E> element = decisionTree.processObject(e);
        _list.add(element);
    }

}
