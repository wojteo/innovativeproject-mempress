package mempress;

import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
/**
 * Created by Bartek on 2014-11-29.
 */
public class HashCodeSmartListTest {
    private HashCodeSmartList<HCSerializableClass> _list;
    private HCSerializableClass _sampleElement;

    @Before
    public void initTest() {
        _list = new HashCodeSmartList<>(); // z domyślnym drzewem
        _list.add(new HCSerializableClass(1, "First element"));
        _list.add(new HCSerializableClass(2, "Second element"));
        _list.add(new HCSerializableClass(3, "Third element"));
        _list.add(new HCSerializableClass(4, "Fourth element"));

        _sampleElement = new HCSerializableClass(2, "Second element");
    }

    @Test
    public void testContainsAll() {

        List<HCSerializableClass> classes = Arrays.asList(
                new HCSerializableClass(1, "First element"),
                new HCSerializableClass(3, "Third element"),
                new HCSerializableClass(4, "Fourth element")
        );

        int hashCodeUseCount = HCSerializableClass.numOfHashCodeCalls;
        assertTrue(_list.containsAll(classes));
        assertTrue(HCSerializableClass.numOfHashCodeCalls - hashCodeUseCount >= 3);
    }

    @Test
    public void testContains() {
        assertTrue(_list.contains(_sampleElement));
    }

    @Test
    public void testIndexOf() {
        assertEquals(1, _list.indexOf(_sampleElement));
    }

    @Test
    public void testLastIndexOf() {
        _list.add(new HCSerializableClass(3, "Third element"));

        int hashCodeUseCountBefore = HCSerializableClass.numOfHashCodeCalls;
        HCSerializableClass hcSerializableClass = new HCSerializableClass(3, "Third element");

        assertEquals(2, _list.indexOf(hcSerializableClass));
        assertEquals(4, _list.lastIndexOf(hcSerializableClass));

        // Maksimum cztery wywołania hashCode!!!
        assertTrue(HCSerializableClass.numOfHashCodeCalls - hashCodeUseCountBefore == 4);
    }

    @Test
    public void testWrapToListElement() {
        HCSerializableClass hcSerializableClass = new HCSerializableClass(1, "First element");
        assertTrue(_list.wrapToListElement(hcSerializableClass).getClass() == ListElementWithHashCode.class);
    }

    @Test
    public void testConstructors() {
        HashCodeSmartList<HCSerializableClass> tmp = null;
        try {
            new HashCodeSmartList<HCSerializableClass>(null, 1);
            fail();
        } catch (NullPointerException ex) {}

        tmp = new HashCodeSmartList<>(-1);
        tmp = new HashCodeSmartList<>(1);

        tmp = new HashCodeSmartList<>(DecisionTreeBuilder.<HCSerializableClass>buildDefaultTree(), 1, -1);
        try {
            tmp = new HashCodeSmartList<>(null, 1, 1);
        } catch (NullPointerException ex) {}
    }


}
