package mempress;

import com.google.common.base.Preconditions;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Created by Bartek on 2014-12-02.
 */
public class SmartListIterators {

    public static <T> Iterator<T> makePreloadIterator(SmartList<T> smartList) {

        return new PreloadIterator<T>(smartList);
    }

    public static <T> Iterator<T> makePreloadIterator(SmartList<T> smartList, int startIndex) {
        return new PreloadIterator<T>(smartList, startIndex);
    }

    public static <T> Iterator<T> makePreloadIterator(SmartList<T> smartList, int startIndex, int numOfObjectsToPrepareInAdvance) {
        return new PreloadIterator<T>(smartList, startIndex, numOfObjectsToPrepareInAdvance);
    }

    public static <T> Iterator<T> makePreloadIterator(SmartList<T> smartList, int startIndex, int numOfObjectsToPrepareInAdvance, int step) {
        return new PreloadIterator<T>(smartList, startIndex, numOfObjectsToPrepareInAdvance, step);
    }


    public static <T> Iterator<T> makeReversedPreloadIterator(SmartList<T> smartList) {

        return new ReversedPreloadIterator<T>(smartList);
    }

    public static <T> Iterator<T> makeReversedPreloadIterator(SmartList<T> smartList, int startIndex) {
        return new ReversedPreloadIterator<T>(smartList, startIndex);
    }

    public static <T> Iterator<T> makeReversedPreloadIterator(SmartList<T> smartList, int startIndex, int numOfObjectsToPrepareInAdvance) {
        return new ReversedPreloadIterator<T>(smartList, startIndex, numOfObjectsToPrepareInAdvance, 1);
    }

    public static <T> Iterator<T> makeReversedPreloadIterator(SmartList<T> smartList, int startIndex, int numOfObjectsToPrepareInAdvance, int step) {
        return new ReversedPreloadIterator<T>(smartList, startIndex, numOfObjectsToPrepareInAdvance, step);
    }


    public static <T> Iterator<T> makeStepIterator(SmartList<T> smartList) {
        return new StepIterator<T>(smartList);
    }

    public static <T> Iterator<T> makeStepIterator(SmartList<T> smartList, int startIndex) {
        return new StepIterator<T>(smartList, startIndex);
    }

    public static <T> Iterator<T> makeStepIterator(SmartList<T> smartList, int startIndex, int step) {
        return new StepIterator<T>(smartList, startIndex, step);
    }


    static class PreloadIterator<T> implements Iterator<T> {
        private final SmartList<T> smartList;
        private int index = -1;
        private final int prepareInAdv;
        private final ArrayDeque<Future<T>> buffer;
        private final int step;
        private boolean init = false;
        protected final ExecutorService tasks;

        public PreloadIterator(SmartList<T> sl) {
            this(sl, 0, 2, 1);
        }

        public PreloadIterator(SmartList<T> sl, int startIndex) {
            this(sl, startIndex, 2, 1);
        }

        public PreloadIterator(SmartList<T> sl, int startIndex, int prepareObjectsInAdvance) {
            this(sl, startIndex, prepareObjectsInAdvance, 1);
        }

        public PreloadIterator(SmartList<T> sl, int startIndex, int prepareObjectsInAdvance, int step) {
            Preconditions.checkNotNull(sl);
            Preconditions.checkArgument(step > 0);
            smartList = sl;

            Preconditions.checkArgument(startIndex >= 0 && startIndex < smartList.size());
            index = startIndex - step;
            prepareInAdv = prepareObjectsInAdvance;
            buffer = new ArrayDeque<>(prepareInAdv + 1);
            tasks = Executors.newFixedThreadPool(Math.min(3, prepareObjectsInAdvance));
            this.step = step;
        }

        @Override
        public boolean hasNext() {
            return index + step < smartList.size();
        }

        @Override
        public T next() {
            int tmp = index + step;

            if (tmp >= smartList.size())
                throw new NoSuchElementException();

            prepareNextElements(Math.max(prepareInAdv - buffer.size(), 0));

            try {
                T obj = buffer.poll().get();
                index = tmp;
                return obj;
            } catch (Exception e) {
                throw new NoSuchElementException(e.getMessage());
            }
        }

        private void prepareNextElements(final int num) {

            int startIndex = index + step;

            for (int i = startIndex; i < startIndex + num * step; i += step) {
                if (smartList.size() <= i)
                    break;

                final int pos = i;

                FutureTask<T> ft = new FutureTask<>(() -> smartList.get(pos));


                tasks.submit(ft);
                buffer.add(ft);
            }
        }
    }

    static class ReversedPreloadIterator<T> implements Iterator<T> {
        private final SmartList<T> smartList;
        private int index = -1;
        private final int prepareInAdv;
        private final int step;
        private final LinkedList<Future<T>> buffer;
        protected final ExecutorService tasks;

        public ReversedPreloadIterator(SmartList<T> smartList) {
            this(smartList, 0, 2, 1);
        }

        public ReversedPreloadIterator(SmartList<T> smartList, int startIndex) {
            this(smartList, startIndex, 2, 1);
        }

        public ReversedPreloadIterator(SmartList<T> smartList, int startIndex, int prepareInAdv) {
            this(smartList, startIndex, prepareInAdv, 1);
        }

        public ReversedPreloadIterator(SmartList<T> smartList, int startIndex, int prepareInAdv, int step) {
            Preconditions.checkNotNull(smartList);
            Preconditions.checkArgument(startIndex >= 0 && startIndex < smartList.size() && prepareInAdv >= 1);
            Preconditions.checkArgument(step > 0);

            this.smartList = smartList;
            this.index = startIndex + step;
            this.prepareInAdv = prepareInAdv;
            this.buffer = new LinkedList<>();
            this.tasks = Executors.newFixedThreadPool(Math.min(3, prepareInAdv));
            this.step = step;
        }

        @Override
        public boolean hasNext() {
            return index - step >= 0;
        }

        @Override
        public T next() {
            int tmp = index - step;

            if (tmp < 0)
                throw new NoSuchElementException();

            prepareNextElements(Math.max(prepareInAdv - buffer.size(), 0));

            try {
                T obj = buffer.removeFirst().get();
                index = tmp;
                return obj;
            } catch (Exception e) {
                throw new NoSuchElementException(e.getMessage());
            }
        }

        private void prepareNextElements(final int num) {

            int startIndex = index - step;

            for (int i = startIndex; i > startIndex - num * step; i -= step) {
                if (i < 0)
                    break;

                final int pos = i;

                FutureTask<T> ft = new FutureTask<>(() -> smartList.get(pos));

                tasks.submit(ft);
                buffer.addLast(ft);
            }
        }
    }

    static class StepIterator<T> implements Iterator<T> {
        private final SmartList<T> list;
        private int index;
        private final int step;

        public StepIterator(SmartList<T> list) {
            this(list, 0, 1);
        }

        public StepIterator(SmartList<T> list, int index) {
            this(list, index, 1);
        }

        public StepIterator(SmartList<T> list, int index, int step) {
            Preconditions.checkNotNull(list);
            Preconditions.checkArgument(index >= 0 && step > 0);
            this.list = list;
            this.index = index - step;
            this.step = step;
        }

        @Override
        public boolean hasNext() {
            return index + step < list.size();
        }

        @Override
        public T next() {
            int tmp = index + step;
            if (tmp >= list.size())
                throw new NoSuchElementException();
            index = tmp;
            return list.get(index);
        }
    }
}
