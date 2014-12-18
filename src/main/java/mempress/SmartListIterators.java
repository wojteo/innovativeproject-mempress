package mempress;

import com.google.common.base.Preconditions;

import java.util.*;
import java.util.concurrent.*;

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


    public static <T> Iterator<T> makeReversedPreloadIterator(SmartList<T> smartList) {

        return new ReversedPreloadIterator<T>(smartList);
    }

    public static <T> Iterator<T> makeReversedPreloadIterator(SmartList<T> smartList, int startIndex) {
        return new ReversedPreloadIterator<T>(smartList, startIndex);
    }

    public static <T> Iterator<T> makeReversedPreloadIterator(SmartList<T> smartList, int startIndex, int numOfObjectsToPrepareInAdvance) {
        return new ReversedPreloadIterator<T>(smartList, startIndex, numOfObjectsToPrepareInAdvance, 1);
    }


    static class PreloadIterator<T> implements Iterator<T> {
        private final SmartList<T> smartList;
        private int index = -1;
        private int prepareInAdv;
//        private LinkedList<Future<T>> buffer;
        private ArrayDeque<Future<T>> buffer;
        protected ExecutorService tasks;

        public PreloadIterator(SmartList<T> sl) {
            this(sl, 0, 2);
        }

        public PreloadIterator(SmartList<T> sl, int startIndex) {
            this(sl, startIndex, 2);
        }

        public PreloadIterator(SmartList<T> sl, int startIndex, int prepareObjectsInAdvance) {
            Preconditions.checkNotNull(sl);
            smartList = sl;

            Preconditions.checkArgument(startIndex >= 0 && startIndex < smartList.size());
            index = startIndex - 1;
            prepareInAdv = prepareObjectsInAdvance;
//            buffer = new LinkedList<>();
            buffer = new ArrayDeque<>(prepareInAdv + 1);
            tasks = Executors.newFixedThreadPool(Math.min(3, prepareObjectsInAdvance));

        }

        @Override
        public boolean hasNext() {
            return index + 1 < smartList.size();
        }

        @Override
        public T next() {
            int tmp = index + 1;
            if(tmp >= smartList.size())
                throw new NoSuchElementException();

            prepareNextElements(Math.max(prepareInAdv - buffer.size(), 0));

            try {
//                T obj = buffer.removeFirst().get();
                T obj = buffer.poll().get();
                index = tmp;
                return obj;
            } catch (Exception e) {
                throw new NoSuchElementException(e.getMessage());
            }
        }

        private void prepareNextElements(final int num) {

            int startIndex = index + 1;

            for(int i = startIndex; i < startIndex + num; ++i) {
                if(smartList.size() <= i)
                    break;

                final int pos = i;

                FutureTask<T> ft = new FutureTask<>(() -> smartList.get(pos));


                tasks.submit(ft);
//                new Thread(ft).start();
//                buffer.addLast(ft);
//                buffer.offer(ft);
                buffer.add(ft);
            }
        }
    }

    static class ReversedPreloadIterator<T> implements Iterator<T> {
        private final SmartList<T> smartList;
        private int index = -1;
        private int prepareInAdv;
        private LinkedList<Future<T>> buffer;
        protected ExecutorService tasks;

        public ReversedPreloadIterator(SmartList<T> smartList) { this(smartList, 0, 2, 1); }

        public ReversedPreloadIterator(SmartList<T> smartList, int startIndex) { this(smartList, startIndex, 2, 1); }


        public ReversedPreloadIterator(SmartList<T> smartList, int startIndex, int prepareInAdv, int step) {
            Preconditions.checkNotNull(smartList);
            Preconditions.checkArgument(startIndex >= 0 && startIndex < smartList.size() && prepareInAdv >= 1);

            this.smartList = smartList;
            this.index = startIndex + 1;
            this.prepareInAdv = prepareInAdv;
            this.buffer = new LinkedList<>();
            this.tasks = Executors.newFixedThreadPool(Math.min(3, prepareInAdv));
        }

        @Override
        public boolean hasNext() {
            return index - 1 >= 0;
        }

        @Override
        public T next() {
            int tmp = index - 1;
            if(tmp < 0)
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

            int startIndex = index - 1;

            for(int i = startIndex; i > startIndex - num; --i) {
                if(i < 0)
                    break;

                final int pos = i;

                FutureTask<T> ft = new FutureTask<>(() -> smartList.get(pos));

                tasks.submit(ft);
                buffer.addLast(ft);
            }
        }
    }
}
