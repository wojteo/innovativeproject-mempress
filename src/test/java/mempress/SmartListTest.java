package mempress;

import com.google.common.base.Preconditions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by Bartek on 2014-12-02.
 */
public class SmartListTest {
    private SmartList<SerializableClass> _testedList;
    private SerializableClass firstElement, secondElement, thirdElement;
    private List<SerializableClass> _serializableClasses;

    @Before
    public void initTest() {
        _testedList = SmartListBuilder.<SerializableClass>create().build();
        _testedList.add((firstElement = make(1)));
        _testedList.add((secondElement = make(2)));
        _testedList.add((thirdElement = make(3)));

//        _serializableClasses = Arrays.asList(make(4), make(5), make(6));
        _serializableClasses = new ArrayList<>(4);
        _serializableClasses.add(make(4));
        _serializableClasses.add(make(5));
        _serializableClasses.add(make(6));
    }


    @Test
    public void testRemove() {
        assertTrue(_testedList.remove(cloneSC(secondElement)));
        assertFalse(_testedList.remove(new SerializableClass(Integer.MIN_VALUE)));
    }

    @Test
    public void testAddAll() {
        assertTrue(_testedList.addAll(_serializableClasses));

        for (int i = 0; i < _serializableClasses.size(); ++i)
            assertTrue(_testedList.contains(_serializableClasses.get(i)));
    }

    @Test
    public void testAddAllInt() {
        assertTrue(_testedList.addAll(0, _serializableClasses));
        for (int i = 0; i < _serializableClasses.size(); ++i) {
            assertTrue(_serializableClasses.contains(_testedList.get(i)));
        }
    }

    @Test
    public void testAddSetRemove() {
        _testedList.add(1, make(4));
        SerializableClass serializableClass = _testedList.set(1, make(5));
        assertEquals(make(4), serializableClass);

        serializableClass = make(5);
        assertEquals(serializableClass, _testedList.get(1));

        assertEquals(serializableClass, _testedList.remove(1));

        assertTrue(_testedList.add(make(6)));
    }

    @Test
    public void testClearIsEmpty() {
        _testedList.clear();
        assertEquals(0, _testedList.size());

        try {
            _testedList.get(0);
            fail();
        } catch (IndexOutOfBoundsException e) {
        }

        assertTrue(_testedList.isEmpty());
    }

    @Test
    public void testContainsAll() {
        assertTrue(_testedList.addAll(_serializableClasses));
        assertTrue(_testedList.containsAll(_serializableClasses));
        _serializableClasses.add(make(9));
        assertFalse(_testedList.containsAll(_serializableClasses));

        try {
            _serializableClasses.containsAll(null);
            fail();
        } catch (NullPointerException e) {
        }

        _serializableClasses.clear();
        try {
            _testedList.containsAll(_serializableClasses);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testIndexOf() {
        SerializableClass sc = make(2), sc2 = make(3);
        assertEquals(1, _testedList.lastIndexOf(sc));
        assertEquals(2, _testedList.indexOf(sc2));

        sc = make(Integer.MIN_VALUE);
        assertEquals(-1, _testedList.indexOf(sc));
        assertEquals(-1, _testedList.lastIndexOf(sc));
    }


    @Test
    public void testContains() {
        assertTrue(_testedList.contains(make(3)));
        assertFalse(_testedList.contains(make(Integer.MIN_VALUE)));
    }


    @Test
    public void testToArray() {
        SerializableClass[] serializableClasses = _testedList.toArray(new SerializableClass[4]),
                serializableClasses1 = _testedList.toArray(new SerializableClass[]{}),
                serializableClasses2 = _testedList.toArray(new SerializableClass[3]);
        Object[] objects = _testedList.toArray();

        SerializableClass[] expectedValues = {firstElement, secondElement, thirdElement};

        assertArrayEquals(expectedValues, objects);
        assertArrayEquals(expectedValues, serializableClasses1);
        assertArrayEquals(expectedValues, serializableClasses2);

        try {
            assertArrayEquals(expectedValues, serializableClasses);
            fail();
        } catch (AssertionError e) {
        }

        try {
            _testedList.toArray(null);
            fail();
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void testIterators() {
        int index = 0;
        SerializableClass[] elements = {firstElement, secondElement, thirdElement};
        for (SerializableClass sc : _testedList) {
            assertEquals(elements[index++], sc);
        }

        ListIterator<SerializableClass> it = _testedList.listIterator();
        SerializableClass sc4 = make(4);
        for (int i = 0; it.hasNext(); ++i) {
            assertEquals(elements[i], it.next());
            if (i == 1) {
                it.add(sc4);
            }
        }
        elements = new SerializableClass[]{firstElement, sc4, secondElement, thirdElement};

        it = _testedList.listIterator(1);
        for (int i = 1; it.hasNext(); ++i) {
            assertEquals(elements[i], it.next());
        }
    }

    @Test
    public void testWeightListener() {
        SmartList<SerializableClass> serializableClassSmartList = SmartListBuilder.<SerializableClass>create()
                .weightLimit(70)
                .decisionTree(DecisionTreeBuilder.<SerializableClass>create()
                        .addTreeElement(new DecisionStoreIt<>()).addTreeElement(new DecisionSerializeFile<>()).build())
                .build();
        SerializableClass[] serializableClasses = {make(4), make(5), make(6)};
        serializableClassSmartList.addAll(Arrays.asList(serializableClasses));

        //try { Thread.sleep(1000); } catch (Exception e) { throw new RuntimeException(e); }
        try {
            serializableClassSmartList.weightLimitListener.executorService.awaitTermination(15, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        for (ListElement<SerializableClass> le : serializableClassSmartList._list)
            Assert.assertTrue(le.getData().getSerializerType() == SerializerType.FileSerializer || le.getData().getSerializerType() == SerializerType.NoSerialized);
    }

    @Test
    public void testCheckConditions() {
        SerializableMutableClass smc = new SerializableMutableClass(29);
        SerializableImmutableClass sic = new SerializableImmutableClass(31);

        List<SerializableMutableClass> list = SmartListBuilder.<SerializableMutableClass>create().build();

        try {
            list.add(smc);
            fail();
        } catch (IllegalArgumentException e) {
        }

        assertTrue(list.add(sic));

        try {
            list.set(0, smc);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            list.add(0, smc);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }


    private SerializableClass cloneSC(SerializableClass sc) {
        Preconditions.checkNotNull(sc);
        return new SerializableClass(sc.getNo());
    }

    private SerializableClass make(int n) {
        return new SerializableClass(n);
    }

    private static class SerializableImmutableClass extends SerializableMutableClass implements Immutable {
        public SerializableImmutableClass() {
        }

        public SerializableImmutableClass(int no) {
            super(no);
        }
    }

    private static class SerializableMutableClass implements Serializable {
        private int no = 1;

        public SerializableMutableClass() {
        }

        public SerializableMutableClass(int no) {
            this.no = no;
        }

        public int getNo() {
            return no;
        }

        public void setNo(int no) {
            this.no = no;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SerializableMutableClass that = (SerializableMutableClass) o;

            return no == that.no;
        }
    }
}
