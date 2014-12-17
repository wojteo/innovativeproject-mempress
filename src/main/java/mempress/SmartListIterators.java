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


    public static <T> ListIterator<T> makeReverseIterator(SmartList<T> smartList) {
        return null;
    }


    static class PreloadIterator<T> implements Iterator<T> {
        private final SmartList<T> smartList;
        private int index = -1;
        private int prepareInAdv;
        private LinkedList<Future<T>> buffer;
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
            buffer = new LinkedList<>();
            tasks = Executors.newSingleThreadExecutor();
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
                T obj = buffer.removeFirst().get();
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

                FutureTask<T> ft = new FutureTask<T>(new Callable<T>() {
                    @Override
                    public T call() throws Exception {
                        return smartList.get(pos);
                    }
                });

                tasks.execute(ft);
                buffer.addLast(ft);
            }
        }
    }
}
