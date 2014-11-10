package mempress;

import com.google.common.collect.ForwardingList;

import java.util.*;

/**
 * Klasa listy; DO NAPISANIA
 * @param <E>
 */
public class SmartList<E> extends List<E> {
    private ArrayList<SmartListElement<E>> _list =
            new ArrayList<>();

    @Override
    public int size() {
        return _list.size();
    }

    @Override
    public boolean isEmpty() {
        return _list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public E get(int index) {
        return _list.get(index).getObject();
    }

    @Override
    public boolean add(E e) {
        return false;
    }
}
