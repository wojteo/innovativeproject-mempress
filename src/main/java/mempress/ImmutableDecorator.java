package mempress;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Bartek on 2014-11-26.
 */
public class ImmutableDecorator<E> implements SmartListDecorator<E> {

    @Override
    public SmartList<E> decorate(SmartList<E> list) {
        Preconditions.checkNotNull(list);
        return new Decorator<>(list);
    }


    private static class Decorator<E> extends SmartList<E> {
        private SmartList<E> delegatedList;

        public Decorator(SmartList<E> delegatedList) {
            Preconditions.checkNotNull(delegatedList);
            this.delegatedList = delegatedList;
        }

        @Override
        public long demoteElements(int numOfElements) {
            return delegatedList.demoteElements(numOfElements);
        }

        @Override
        public long getCurrentWeight() {
            return delegatedList.getCurrentWeight();
        }

        @Override
        public long getMaximumWeight() {
            return delegatedList.getMaximumWeight();
        }

        @Override
        public boolean add(E e) {
            if(e instanceof Immutable)
                return delegatedList.add(e);
            else
                throw new MempressException("Given object must be immutable");
        }

        @Override
        public E get(int index) {
            return delegatedList.get(index);
        }

        @Override
        public boolean contains(Object o) {
            return delegatedList.contains(o);
        }

        @Override
        public boolean isEmpty() {
            return delegatedList.isEmpty();
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return delegatedList.subList(fromIndex, toIndex);
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return delegatedList.listIterator(index);
        }

        @Override
        public ListIterator<E> listIterator() {
            return delegatedList.listIterator();
        }

        @Override
        public int lastIndexOf(Object o) {
            return delegatedList.lastIndexOf(o);
        }

        @Override
        public int indexOf(Object o) {
            return delegatedList.indexOf(o);
        }

        @Override
        public E remove(int index) {
            return delegatedList.remove(index);
        }

        @Override
        public void add(int index, E element) {
            if(element instanceof Immutable)
                delegatedList.add(index, element);
            else
                throw new MempressException("Given object must be immutable");
        }

        @Override
        public E set(int index, E element) {
            if(element instanceof Immutable)
                return delegatedList.set(index, element);
            else
                throw new MempressException("Given object must be immutable");
        }

        @Override
        public int hashCode() {
            return delegatedList.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return delegatedList.equals(o);
        }

        @Override
        public void clear() {
            delegatedList.clear();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return delegatedList.retainAll(c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return delegatedList.removeAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            return delegatedList.addAll(index, c);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return delegatedList.addAll(c);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return delegatedList.containsAll(c);
        }

        @Override
        public boolean remove(Object o) {
            return delegatedList.remove(o);
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return delegatedList.toArray(a);
        }

        @Override
        public Object[] toArray() {
            return delegatedList.toArray();
        }

        @Override
        public Iterator<E> iterator() {
            return delegatedList.iterator();
        }

        @Override
        public int size() {
            return delegatedList.size();
        }
    }
}
