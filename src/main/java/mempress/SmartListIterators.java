package mempress;

import com.google.common.base.Preconditions;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

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

            T obj = smartList.get(tmp);
            index = tmp;

            prepareNextElements(prepareInAdv);

            return obj;
        }

        private void prepareNextElements(final int num) {
            final int startIndex = index + 1;

            Thread t = new Thread() {
                @Override
                public void run() {
                    for(int i = startIndex; i < startIndex + num; ++i) {
                        if(smartList.size() <= i)
                            break;
                        smartList._decisionTree.goBackToHighestState(smartList._list.get(i));
                    }
                }
            };

            t.start();

            int z = 2;
        }
    }
}
