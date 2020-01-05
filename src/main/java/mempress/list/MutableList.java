package mempress.list;

import com.google.common.base.Preconditions;
import mempress.MempressException;
import mempress.ObservableLong;
import mempress.SharedObject;
import mempress.decision.DecisionTree;
import mempress.decision.DecisionTreeBuilder;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Bartek on 2014-12-19.
 */
public class MutableList<E> implements List<E> {
    public static final int NUMBER_OF_ASYNC_TASK = 5;
    private static final Logger log = Logger.getLogger(MutableList.class);
    private static final int NUM_OF_ATTEMPTS_TO_SHRINK_LIST = 3;
    protected final List<MutableListElement<E>> _list;
    protected final DecisionTree<E> _decisionTree;
    private final ObservableLong currentWeight = new ObservableLong(0, true);
    private final ExecutorService _listTasks;
    WeightLimitListener weightLimitListener;
    private Queue<MutableListElement<E>> _serializationQueue;
    private long weightLimit = 0;
    private int usesPerCycle = 1;
    private long timeLimit = 0;

    //-------------------------------------------------
    //  STATYCZNE METODY
    //-------------------------------------------------

    protected MutableList() {
        this(DecisionTreeBuilder.buildDefaultTree(), -1, -1);
    }

    protected MutableList(DecisionTree<E> decTree, long maxWeight, long timeLimit) {
        Preconditions.checkNotNull(decTree);

        _decisionTree = decTree;

        _serializationQueue = new PriorityQueue<>();

        if (maxWeight > 0 || timeLimit > 0) {
            _list = Collections.synchronizedList(new ArrayList<MutableListElement<E>>());
            _serializationQueue = new PriorityBlockingQueue<>();
        } else {
            _list = new ArrayList<>();
            _serializationQueue = new PriorityQueue<>();
        }

        weightLimit = maxWeight;

        if (maxWeight > 0) {
            weightLimitListener = new WeightLimitListener();
            currentWeight.addListener(weightLimitListener);
        }

        if (timeLimit > 0) {
            Timer cycleTimer = new Timer();
            cycleTimer.schedule(new DemoteTimer(), timeLimit);
            this.timeLimit = TimeUnit.NANOSECONDS.convert(timeLimit, TimeUnit.MILLISECONDS);
        }

        _listTasks = Executors.newFixedThreadPool(NUMBER_OF_ASYNC_TASK);
    }

    //-------------------------------------------------
    //  KONSTRUKTORY
    //-------------------------------------------------

    protected MutableList(long maxWeight) {
        this(DecisionTreeBuilder.buildDefaultTree(), maxWeight, -1);
    }

    protected MutableList(DecisionTree<E> decTree, long maxWeight) {
        this(decTree, maxWeight, -1);
    }

    public static <T> SharedObject<T> get(MutableList<T> mutableList, int index) {
        MutableListElement<T> mle = mutableList._list.get(index);
        mutableList._decisionTree.goBackToHighestState(mle);
        return new SharedObject<>(mle);
    }

    public static <T> Iterator<SharedObject<T>> iterator(MutableList<T> mutableList) {
        Preconditions.checkNotNull(mutableList);
        return new SimpleIterator<>(mutableList);
    }


    //-------------------------------------------------
    //  OPERACJE SPECYFICZNE DLA TEGO TYPU
    //-------------------------------------------------

    protected long demoteElements() {
        long releasedBytes = 0;
        long tmp;
        for (int i = 0; i < 1; ++i) {
            try {
                MutableListElement<E> sle = null;
                sle = _serializationQueue.poll();
                if (sle == null) continue;

                tmp = sle.getSize();
                sle = new MutableListElement<E>(_decisionTree.demote(sle));
                releasedBytes += tmp - sle.getSize();
                _serializationQueue.add(sle);

            } catch (MempressException me) {
            }
        }

        return releasedBytes;
    }

    public long getMaximumWeight() {
        return weightLimit;
    }

    public long getCurrentWeight() {
        return currentWeight.get();
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public int getUsesPerCycle() {
        return usesPerCycle;
    }

    protected MutableListElement<E> wrapToListElement(E obj) {
        Preconditions.checkNotNull(obj);

        ListElement<E> le = null;
        for (int i = 0; i < NUM_OF_ATTEMPTS_TO_SHRINK_LIST; ++i)
            try {
                le = _decisionTree.processObject(obj);
                le.setIdentityHC(System.identityHashCode(obj));
                break;
            } catch (MempressException me) {
            }

        return new MutableListElement<E>(le);
    }

    public void setUsesPerCycle(int usesPerCycle) {
        this.usesPerCycle = usesPerCycle;
    }

    @Override
    public int hashCode() {
        return _list.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return _list.equals(o);
    }

    static class SimpleIterator<T> implements Iterator<SharedObject<T>> {
        private final MutableList<T> _list;
        private int index = -1;

        public SimpleIterator(MutableList<T> ml) {
            Preconditions.checkNotNull(ml);
            _list = ml;
        }

        @Override
        public boolean hasNext() {
            return index + 1 < _list.size();
        }

        @Override
        public SharedObject<T> next() {
            int tmp = index + 1;
            if (tmp >= _list.size()) throw new NoSuchElementException();
            index = tmp;
            return MutableList.get(_list, index);
        }
    }

    class WeightLimitListener implements Observer {
        final ReentrantLock lock = new ReentrantLock();

        @Override
        public void update(Observable o, Object arg) {
            long l = ((ObservableLong) arg).get();

            final long calculatedLimit = ((weightLimit * 9) / 10) + 1;
            if (l > calculatedLimit) {
                _listTasks.submit(() -> {
                    lock.lock();
                    try {
                        tryToShrink(calculatedLimit);
                    } finally {
                        lock.unlock();
                    }
                });
            }
        }

        private void tryToShrink(long newVal) {
            long recoveredSpace = 0;
            if (newVal >= currentWeight.get())
                return;
            boolean breakOuterLoop = false;
            for (int attemptLeft = NUM_OF_ATTEMPTS_TO_SHRINK_LIST; attemptLeft > 0 && !breakOuterLoop; --attemptLeft) {
                for (int counter = Math.max(_serializationQueue.size() / 2, 1); counter > 0; --counter) {
                    recoveredSpace += demoteElements();

                    if (newVal >= currentWeight.get() - recoveredSpace) {
                        breakOuterLoop = true;
                        break;
                    }
                }
            }

            currentWeight.subtractWithoutNotify(recoveredSpace);
        }
    }

    protected Future<MutableListElement<E>> wrapElementAsync(final E obj) {
        return _listTasks.submit(() -> wrapToListElement(obj));
    }

    private class DemoteTimer extends TimerTask {
        // TODO: rozwiązać problem - o ile stopni degradować?

        @Override
        public void run() {
            _list.forEach(le -> {
                long diff = System.nanoTime() - le.getTimeCreated();
                int useC = le.getUseCount();
                if (useC == 0 || (useC == 0 && diff > 2 * timeLimit) || diff / useC > usesPerCycle * timeLimit) {
                    try {
                        _decisionTree.demote(le);
                    } catch (MempressException e) {
                    }
                }
            });
        }
    }


    protected boolean checkConditions(E obj) {
        return obj instanceof Serializable;
    }


    //-------------------------------------------------
    //  MODYFIKACJA ZAWARTOSCI LISTY
    //-------------------------------------------------


    @Override
    public boolean remove(Object o) {
        Preconditions.checkNotNull(o);
        boolean found = false;
        for (int i = 0; i < size(); ++i)
            if (_list.get(i).compare(o)) {
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
                        log.error(e.getMessage(), e);

                        return null;
                    }
                })
                .forEach(le -> {
                    if (le == null)
                        return;
                    boolean prev = true;
                    prev = prev && _list.add(le);
                    prev = prev && _serializationQueue.add(le);
                    currentWeight.add(le.getSize());
                    if (prev) addedElem[0]++;
                });

        return addedElem[0] > 0;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        Preconditions.checkNotNull(c);

        final int[] shift = {index};
        final int[] addedElem = {0};

        c.stream().filter(this::checkConditions)
                .map(obj -> {
                    try {
                        return wrapElementAsync(obj).get();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                })
                .forEach(le -> {
                    if (le == null)
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
            MutableListElement<E> el = wrapElementAsync(element).get(),
                    old = _list.set(index, el);

            _serializationQueue.remove(old);
            currentWeight.subtract(old.getSize());
            currentWeight.add(el.getSize());
            return old.get();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void add(int index, E element) {
        Preconditions.checkNotNull(element);
        Preconditions.checkArgument(checkConditions(element));
        try {
            MutableListElement<E> el = wrapElementAsync(element).get();
            if (el == null) return;
            _list.add(index, el);
            _serializationQueue.add(el);
            currentWeight.add(el.getSize());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public E remove(int index) {
        ListElement<E> el = _list.remove(index);

        if (el == null)
            return null;
        E obj = el.get();
        _serializationQueue.remove(el);
        currentWeight.subtract(el.getSize());

        return obj;
    }

    /**
     * Od momentu odczytania elementu, jest on trzymany w formie zdekodowanej
     */
    @Override
    public E get(int index) {
        throw new UnsupportedOperationException("Unsupported operation. Use MutableList.get instead.");
    }

    @Override
    public boolean add(E e) {
        Preconditions.checkNotNull(e);
        Preconditions.checkArgument(checkConditions(e));
        try {
            MutableListElement<E> element = wrapElementAsync(e).get();
            if (element == null) return false;
            boolean ret = _list.add(element);

            ret = ret && _serializationQueue.add(element);

            currentWeight.add(element.getSize());
            return ret;
        } catch (InterruptedException | ExecutionException ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (int i = 0; i < _list.size(); ++i) {
            ListElement<E> le = _list.get(i);
            int hc = le.getIdentityHC();
            E obj = null;
            for (Object o : c) {
                if (System.identityHashCode(o) == hc) {
                    if (remove(i--) != null) {
                        modified = true;
                    }
                    break;
                } else {
                    if (obj == null) {
                        obj = le.get(false);
                    }
                    if (obj.equals(o)) {
                        if (remove(i--) != null) {
                            modified = true;
                        }
                        break;
                    }
                }
            }
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not implemented yet");
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

        for (ListElement<E> sle : _list) {
            for (Object o : c) {
                if (sle.compare(o))
                    ++counter;
            }
        }

        return counter == colsize;
    }


    @Override
    public int indexOf(Object o) {
        int size = size();
        for (int i = 0; i < size; ++i) {
            if (_list.get(i).compare(o))
                return i;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        Preconditions.checkNotNull(o);
        for (int i = size() - 1; i >= 0; --i) {
            if (_list.get(i).compare(o))
                return i;
        }

        return -1;
    }

    @Override
    public boolean contains(Object o) {

        for (ListElement<E> sle : _list) {
            if (sle.compare(o))
                return true;
        }

        return false;
    }

    //--------------------------------------------------------------------
    // ITERATORY
    //--------------------------------------------------------------------

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Unsupported operation. Use MutableList.iterator instead.");
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException("Unsupported operation. Use MutableList.listIterator instead.");
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException("Unsupported operation. Use MutableList.listIterator instead.");
    }

    //--------------------------------------------------------------------
    // WYDZIELANIE KOLEKCJI
    //--------------------------------------------------------------------

    @Override
    public Object[] toArray() {
        int size = size();
        Object[] array = new Object[size()];
        for (int i = 0; i < size; ++i)
            array[i] = _list.get(i).get();
        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        Preconditions.checkNotNull(a);
        if (a.length < size()) {
            return (T[]) Arrays.copyOf(toArray(), size(), a.getClass());
        } else {
            System.arraycopy(toArray(), 0, a, 0, size());
            return a;
        }
    }

    /**
     * Zwraca fragment SmartListy. Fragment też jest typu SmartList
     *
     * @param fromIndex indeks pierwszego elementu do skopiowania
     * @param toIndex
     * @return
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        Preconditions.checkArgument(!(fromIndex < 0 || toIndex > size() || fromIndex > toIndex));

        MutableList<E> ml = new MutableList<>(_decisionTree, weightLimit, TimeUnit.MILLISECONDS.convert(timeLimit, TimeUnit.NANOSECONDS));
        final long[] weight = {0};
        _list.stream()
                .skip(fromIndex)
                .limit(toIndex - fromIndex)
                .forEach(el -> {
                    ml._list.add(el);
                    ml._serializationQueue.add(el);
                    weight[0] += el.getSize();
                });

        if (ml.weightLimitListener != null)
            ml.currentWeight.setValue(weight[0]);

        return ml;
    }

    //--------------------------------------------------------------------
    // METODY ZWIĄZANE Z FUNKCJONALNOSCIĄ LISTY ORAZ METODY SPOZA List<E>
    //--------------------------------------------------------------------


    //---------------------------------------------------
    // KLASY WEWNETRZNE
    //---------------------------------------------------


}
