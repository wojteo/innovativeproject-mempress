package mempress;

import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;

/**
 * Klasa listy; DO NAPISANIA
 * @param <E>
 */
public class SmartList<E> implements List<E>, Iterable<E> {
    protected List<ListElement<E>> _list;
    protected DecisionTree<E> _decisionTree;
    private Queue<ListElement<E>> _serializationQueue;
    private long weightLimit = 0;
    //private SimpleLongProperty currentWeight = new SimpleLongProperty(0);
    private ObservableLong currentWeight = new ObservableLong(0, true);
    WeightLimitListener weightLimitListener;
    private Timer cycleTimer;
    private int usesPerCycle = 1;
    private long timeLimit = 0;
    private ExecutorService _listTasks;
    private static int numOfAttemptsToShrinkList = 3;
    private static int numOfAttemptsToGetObject = 3;
    public static int numberOfAsyncTask = 5;


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

//        _list = new ArrayList<>();
        _serializationQueue = new PriorityQueue<>();

        if(maxWeight > 0 || timeLimit > 0) {
            _list = Collections.synchronizedList(new ArrayList<ListElement<E>>());
            _serializationQueue = new PriorityBlockingQueue<>();
        } else {
            _list = new ArrayList<>();
            _serializationQueue = new PriorityQueue<>();
        }

        weightLimit = maxWeight;

        if(maxWeight > 0) {
            //weightLimit = maxWeight;
            currentWeight.addListener((weightLimitListener = new WeightLimitListener()));
        }

        if(timeLimit > 0) {
            cycleTimer = new Timer();
            cycleTimer.schedule(new DemoteTimer(), timeLimit);
            this.timeLimit = TimeUnit.NANOSECONDS.convert(timeLimit, TimeUnit.MILLISECONDS);
        }

        _listTasks = Executors.newFixedThreadPool(numberOfAsyncTask);
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
                .map(obj -> {
                    try {
                        return wrapElementAsync(obj).get();
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        return null;
                    }
                })
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
                .map(obj -> {
                    try {
                        return wrapElementAsync(obj).get();
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        return null;
                    }
                })
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
        try {
            ListElement<E> el = wrapElementAsync(element).get(),
                    old = _list.set(index, el);

            _serializationQueue.remove(old);
            currentWeight.subtract(old.getSize());
            currentWeight.add(el.getSize());
            return old.get();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public void add(int index, E element) {
        Preconditions.checkNotNull(element);
        Preconditions.checkArgument(checkConditions(element));
        try {
            ListElement<E> el = wrapElementAsync(element).get();
            if (el == null) return;
            _list.add(index, el);
            _serializationQueue.add(el);
            currentWeight.add(el.getSize());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public E remove(int index) {
        ListElement<E> el = _list.remove(index);

        if(el == null)
            return null;
        E obj = el.get();
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
        try {
            ListElement<E> element = wrapElementAsync(e).get();
            if (element == null) return false;
            boolean ret = _list.add(element);

            ret = ret && _serializationQueue.add(element);

            currentWeight.add(element.getSize());
            return ret;
        } catch (InterruptedException | ExecutionException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for(int i = 0; i < _list.size(); ++i) {
            ListElement<E> le = _list.get(i);
            int hc = le.getIdentityHC();
            E obj = null;
            for(Object o : c) {
                if(System.identityHashCode(o) == hc) {
                    if(remove(i--) != null) {
                        modified = true;
                    }
                    break;
                }
                else {
                    if(obj == null) {
                        obj = le.get(false);
                    }
                    if(obj.equals(o)) {
                        if(remove(i--) != null) {
                            modified = true;
                        }
                        break;
                    }
                }
            }
        }

        return modified;
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
     * Zwraca fragment SmartListy. Fragment też jest typu SmartList
     * @param fromIndex indeks pierwszego elementu do skopiowania
     * @param toIndex
     * @return
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        Preconditions.checkArgument(!(fromIndex < 0 || toIndex > size() || fromIndex > toIndex));

        SmartList<E> sl = new SmartList<>(_decisionTree, weightLimit, TimeUnit.MILLISECONDS.convert(timeLimit, TimeUnit.NANOSECONDS));
        final long[] weight = {0};
        _list.stream()
                .skip(fromIndex)
                .limit(toIndex - fromIndex)
                .forEach(el -> {
                    sl._list.add(el);
                    sl._serializationQueue.add(el);
                    weight[0] += el.getSize();
                });

        if(sl.weightLimitListener != null)
            sl.currentWeight.setValue(weight[0]);

        return sl;
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
                ListElement<E> sle = null;
                sle = _serializationQueue.poll();
                if (sle == null) continue;

                    tmp = sle.getSize();
                    sle = _decisionTree.demote(sle);
                    if (sle == null)
                        continue;
                    releasedBytes += tmp - sle.getSize();

                _serializationQueue.add(sle);

            } catch (MempressException me) {
//                System.err.println("Przechwycono wyjatek: " + me.getMessage());
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
                listElement.setIdentityHC(System.identityHashCode(obj));
                break;
            } catch (MempressException me) {
            }

        return listElement;
    }

    private Future<ListElement<E>> wrapElementAsync(final E obj) {
        return _listTasks.submit(() -> wrapToListElement(obj));
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

    public long getTimeLimit() {
        return timeLimit;
    }

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

    class WeightLimitListener implements Observer {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        @Override
        public void update(Observable o, Object arg) {
            long l = ((ObservableLong)arg).get();

            final long calculatedLimit = ((weightLimit * 9) / 10) + 1;
            if(l > calculatedLimit) {
//                executorService.submit(() -> {
//                    tryToShrink(calculatedLimit);
//                });
                _listTasks.submit(() -> tryToShrink(calculatedLimit));
            }
        }

        private void tryToShrink(long newVal) {
            long recoveredSpace = 0;
            boolean breakOuterLoop = false;
            for(int attemptLeft = numOfAttemptsToShrinkList; attemptLeft > 0 && !breakOuterLoop; --attemptLeft) {
                for (int counter = Math.max(_serializationQueue.size() / 2, 1); counter > 0 ; --counter) {
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
