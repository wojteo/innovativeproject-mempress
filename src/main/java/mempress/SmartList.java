package mempress;

import com.google.common.base.Preconditions;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Klasa listy; DO NAPISANIA
 * @param <E>
 */
public class SmartList<E> implements List<E>, Iterable<E> {
    protected List<ListElement<E>> _list;
    protected DecisionTree<E> _decisionTree;
    private PriorityQueue<ListElement<E>> _serializationQueue;
    private long weightLimit = -1;
    //private SimpleLongProperty currentWeight = new SimpleLongProperty(0);
    private ObservableLong currentWeight = new ObservableLong(0, true);
    private WeightLimitListener weightLimitListener;
    private Timer cycleTimer;
    private int usesPerCycle = 1;
    private long timeLimit;
    private static int numOfAttemptsToShrinkList = 3;
    private static int numOfAttemptsToGetObject = 3;

    //--------------------------------------------------------------------
    //  KONSTRUKTORY
    //--------------------------------------------------------------------

    protected SmartList() {
        this(DecisionTreeBuilder.<E>buildDefaultTree(), -1, -1);
    }

    protected SmartList(long maxWeight) {
        this(DecisionTreeBuilder.<E>buildDefaultTree(), maxWeight, -1);
    }

    protected SmartList(DecisionTree<E> decTree, long maxWeight) {
        this(decTree, maxWeight, -1);
    }

    protected SmartList(DecisionTree<E> decTree, long maxWeight, long timeLimit) {
        Preconditions.checkNotNull(decTree);

        _decisionTree = decTree;

        _list = new ArrayList<>();
        _serializationQueue = new PriorityQueue<>();
        weightLimit = maxWeight;

        if(maxWeight > 0) {
            //weightLimit = maxWeight;
            currentWeight.addListener((weightLimitListener = new WeightLimitListener()));
        }

        if(timeLimit > 0) {
            // TODO: timer do cyklicznego sprawdzania elementów listy
            cycleTimer = new Timer();
            cycleTimer.schedule(new DemoteTimer(), timeLimit);
            this.timeLimit = TimeUnit.NANOSECONDS.convert(timeLimit, TimeUnit.MILLISECONDS);
        }
    }

    //----------------------------------------------------------------
    //  MODYFIKOWANIE ZAWARTOSCI LISTY
    //----------------------------------------------------------------
    @Override
    public boolean remove(Object o) {
        Preconditions.checkNotNull(o);
        boolean found = false;
        for(int i = 0; i < size(); ++i)
            if(_list.get(i).compare(o)) {
                _list.remove(i);
                found = true;
            }
        return found;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Preconditions.checkNotNull(c);

        final int[] addedElem = {0};

        c.stream().filter(this::checkConditions)
                .map(obj -> wrapToListElement(obj))
                .forEach(le -> {
                    if(le == null)
                        return;
                    boolean prev = true;
                    prev = prev && _list.add(le);
                    prev = prev && _serializationQueue.add(le);
                    currentWeight.add(le.getSize());
                    if(prev) addedElem[0]++;
                });

        return addedElem[0] > 0;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        Preconditions.checkNotNull(c);

        final int[] shift = { index };
        final int[] addedElem = { 0 };

        c.stream().filter(this::checkConditions)
                .map(obj -> wrapToListElement(obj))
                .forEach(le -> {
                    if(le == null)
                        return;
                    _list.add(shift[0]++, le);
                     _serializationQueue.add(le);
                    currentWeight.add(le.getSize());
                    addedElem[0]++;
                });

        return addedElem[0] > 0;
    }

    @Override
    public E set(int index, E element) {
        Preconditions.checkNotNull(element);
        Preconditions.checkArgument(checkConditions(element));
        ListElement<E> el = wrapToListElement(element),
                old = _list.set(index, el);
        _serializationQueue.remove(old);
        currentWeight.subtract(old.getSize());
        currentWeight.add(el.getSize());
        return old.get();
    }

    @Override
    public void add(int index, E element) {
        Preconditions.checkNotNull(element);
        Preconditions.checkArgument(checkConditions(element));
        ListElement<E> el = wrapToListElement(element);
        if(el == null) return;
        _list.add(index, el);
        _serializationQueue.add(el);
        currentWeight.add(el.getSize());
    }

    @Override
    public E remove(int index) {
        ListElement<E> el = _list.remove(index);
        if(el == null)
            return null;
        E obj = null;

            obj = el.get();
            _serializationQueue.remove(el);
            currentWeight.subtract(el.getSize());

        return obj;
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
        Preconditions.checkNotNull(e);
        Preconditions.checkArgument(checkConditions(e));
        ListElement<E> element = wrapToListElement(e);
        if(element == null) return false;
        boolean ret =  _list.add(element) && _serializationQueue.add(element);
        currentWeight.add(element.getSize());
        return ret;
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
        _serializationQueue.clear();
    }

    //----------------------------------------------------------------
    //  ROZMIARY, POJEMNOSC ITP.
    //----------------------------------------------------------------

    @Override
    public int size() {
        return _list.size();
    }

    @Override
    public boolean isEmpty() {
        return _list.isEmpty();
    }


    //-----------------------------------------------------------------
    //  PRZESZUKIWANIE LISTY
    //-----------------------------------------------------------------

    @Override
    public boolean containsAll(Collection<?> c) {
        int counter = 0;
        int colsize = c.size();

        Preconditions.checkNotNull(c);
        Preconditions.checkArgument(colsize > 0);

        //TODO: compare mało wydajny - wielokrotna deserializacja obiektu
        for(ListElement<E> sle : _list) {
            for(Object o : c) {
                if(sle.compare(o))
                    ++counter;
            }
        }

        return counter == colsize;
    }

    @Override
    public boolean equals(Object o) {
        return _list.equals(o);
    }

    @Override
    public int hashCode() {
        return _list.hashCode();
    }

    @Override
    public int indexOf(Object o) {
        int size = size();
        for(int i = 0; i < size; ++i) {
            if(_list.get(i).compare(o))
                return i;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        Preconditions.checkNotNull(o);
        for(int i = size() - 1; i >= 0; --i) {
            if(_list.get(i).compare(o))
                return i;
        }

        return -1;
    }

    @Override
    public boolean contains(Object o) {

        for(ListElement<E> sle : _list) {
            if(sle.compare(o))
                return true;
        }

        return false;
    }

    //--------------------------------------------------------------------
    // ITERATORY
    //--------------------------------------------------------------------

    @Override
    public Iterator<E> iterator() {
        return new SimpleIterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        return new SimpleListIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new SimpleListIterator(index);
    }

    //--------------------------------------------------------------------
    // WYDZIELANIE KOLEKCJI
    //--------------------------------------------------------------------

    @Override
    public Object[] toArray() {
        int size = size();
        Object[] array = new Object[size()];
        for(int i = 0; i < size; ++i)
            array[i] = _list.get(i).get();
        return array;
    }

    /*
    @Override
    public <T> T[] toArray(T[] a) {
        if(a == null || a.length < size()) {
            T[] table = (T[]) Array.newInstance(Object.class, size());
            for (int i = 0; i < size(); ++i)
                table[i] = (T) get(i);
            return table;
        }
        else {
            for(int i = 0; i < a.length; ++i) {
                a[i] = (T)get(i);
            }
            return a;
        }
    }*/

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        Preconditions.checkNotNull(a);
        if(a.length < size()) {
            return (T[])Arrays.copyOf(toArray(), size(), a.getClass());
        } else {
            System.arraycopy(toArray(), 0, a, 0, size());
            return a;
        }
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
    //--------------------------------------------------------------------
    // METODY ZWIĄZANE Z FUNKCJONALNOSCIĄ LISTY ORAZ METODY SPOZA List<E>
    //--------------------------------------------------------------------

    // TODO: Dokończyć pisanie
    protected long demoteElements(int numOfElements) {
        long releasedBytes = 0;
        long tmp;
        for(int i = 0; i < numOfElements; ++i) {
            try {
                ListElement<E> sle = _serializationQueue.poll();
                if (sle == null) continue;
                int index = _list.indexOf(sle);
                tmp = sle.getSize();
                sle = _decisionTree.demote(sle);
                if (sle == null)
                    continue;
                releasedBytes += Math.abs(tmp - sle.getSize());
//                _list.set(index, sle);
                _serializationQueue.add(sle);
            } catch (MempressException me) {
                System.err.println("Przechwycono wyjatek: " + me.getMessage());
            }
        }

        return releasedBytes;
    }

    /**
     * Ta metoda TYLKO dostarcza element do dodania. Dodawanie do listy nie może się tutaj znaleźć
     * @param obj
     * @return ListElement opakowujacy obiekt. MOZE ZWROCIC NULL!
     */
    protected ListElement<E> wrapToListElement(E obj) {
        Preconditions.checkNotNull(obj);

        ListElement<E> listElement = null;

        for (int i = 0; i < numOfAttemptsToShrinkList; ++i)
            try {
                listElement = _decisionTree.processObject(obj);
                break;
            } catch (MempressException me) {
                demoteElements(1);
            }

        return listElement;
    }

    protected boolean checkConditions(E obj) {
        boolean pred = true;

        pred = pred && obj instanceof Serializable;
        pred = pred && obj instanceof Immutable;

//        if(!pred)
//            throw new IllegalArgumentException("Given object is not serializable or immutable");
        return pred;
    }

    public long getMaximumWeight() { return weightLimit; }

    public long getCurrentWeight() { return currentWeight.get(); }

    public int getUsesPerCycle() {
        return usesPerCycle;
    }

    public void setUsesPerCycle(int usesPerCycle) {
        this.usesPerCycle = usesPerCycle;
    }

    //--------------------------------------------------------------------
    // KLASY WEWNETRZNE
    //--------------------------------------------------------------------

    protected class SimpleIterator implements Iterator<E> {
        private int index = -1;

        @Override
        public boolean hasNext() {
            return index + 1 < _list.size();
        }

        @Override
        public E next() {
            int tmp = index + 1;
            if(tmp >= _list.size()) throw new NoSuchElementException();
            index = tmp;
            return get(tmp);
        }

        @Override
        public void remove() {
            SmartList.this.remove(index);
        }
    }

    protected class SimpleListIterator implements ListIterator<E> {
        private int index;

        public SimpleListIterator() {
            index = -1;
        }

        public SimpleListIterator(int index) {
            if(index < 0 || index > _list.size())
                throw new IndexOutOfBoundsException();
            this.index = index - 1;
        }

        @Override
        public boolean hasNext() {
            return index + 1 < _list.size();
        }

        @Override
        public E next() {
            int tmp = index + 1;
            if(tmp >= _list.size()) throw new NoSuchElementException();
            index = tmp;
            return get(tmp);
        }

        @Override
        public boolean hasPrevious() {
            int tmp = index - 1;
            return tmp >= 0 && tmp < _list.size();
        }

        @Override
        public E previous() {
            int tmp = index - 1;
            if(!(tmp >= 0 && tmp < _list.size()))
                throw new NoSuchElementException();
            index = tmp;
            return get(tmp);
        }

        @Override
        public int nextIndex() {
            return Math.min(index + 1, _list.size());
        }

        @Override
        public int previousIndex() {
            return Math.max(index - 1, _list.size());
        }

        @Override
        public void remove() {
            SmartList.this.remove(index);
        }

        @Override
        public void set(E e) {
            SmartList.this.set(index, e);
        }

        @Override
        public void add(E e) {
            SmartList.this.add(index++, e);
        }
    }

    //TODO: changed nie jest wywoływane!!! Czemu?
    private class WeightLimitListener implements Observer {
//        @Override
//        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//            long l = newValue.longValue();
//
//            if(l > weightLimit)
//                tryToShrink(l);
//        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        @Override
        public void update(Observable o, Object arg) {
            long l = ((ObservableLong)arg).get();

            final long calculatedLimit = ((weightLimit * 9) / 10) + 1;
            if(l > calculatedLimit) {
                executorService.submit(() -> {
                    tryToShrink(calculatedLimit);
                });
            }
        }

        private void tryToShrink(long newVal) {
            long recoveredSpace = 0;
            boolean breakOuterLoop = false;
            for(int attemptLeft = numOfAttemptsToShrinkList; attemptLeft > 0 && !breakOuterLoop; --attemptLeft) {
                for (int counter = Math.max(_list.size() / 2, 1); counter > 0 ; --counter) {
                    recoveredSpace += demoteElements(1);

                    if(newVal >= currentWeight.get() - recoveredSpace) {
                        breakOuterLoop = true;
                        break;
                    }
                }
            }

            currentWeight.subtract(recoveredSpace);
        }
    }

    private class DemoteTimer extends TimerTask {
        // TODO: rozwiązać problem - o ile stopni degradować?

        @Override
        public void run() {
            _list.forEach(le -> {
                long diff = System.nanoTime() - le.getTimeCreated();
                int useC = le.getUseCount();
                if(useC == 0 || diff / useC > usesPerCycle * timeLimit) {
                    try {
                        _decisionTree.demote(le);
                    } catch (MempressException e) {}
                }
            });
        }
    }
}
