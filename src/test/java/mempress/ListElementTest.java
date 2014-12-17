package mempress;

import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;

import static org.junit.Assert.*;
/**
 * Created by Bartek on 2014-11-29.
 */
public class ListElementTest {
    private ListElement<HCSerializableClass> _wrappedObj;
    private ClassData _classData;
    private HCSerializableClass _scl;

    private void prepareObjects(SerializerType sType) {
        _scl = new HCSerializableClass(47, "Forty seven");
        _classData = SerializerFactory.createSerializer(sType).ser(_scl);
        _wrappedObj = new ListElement<>(_classData, HCSerializableClass.class);
    }

    @Test
    public void testConstructor() {
        prepareObjects(SerializerType.NoSerialized);
        try {
            _wrappedObj = new ListElement<>(_classData, null);
            fail();
        } catch (NullPointerException ex) {}

        try {
            _wrappedObj = new ListElement<>(null, HCSerializableClass.class);
            fail();
        } catch (NullPointerException ex) {}
    }

    @Test
    public void testGet() {
        prepareObjects(SerializerType.NoSerialized);

        int useCount = _wrappedObj.getUseCount();
        _wrappedObj.get();
        assertEquals(_wrappedObj.getUseCount() - useCount, 1);
    }

    @Test
    public void testGetBoolean() {
        prepareObjects(SerializerType.NoSerialized);

        int useCount = _wrappedObj.getUseCount();
        _wrappedObj.get(true);
        assertEquals(_wrappedObj.getUseCount() - useCount, 1);

        useCount = _wrappedObj.getUseCount();
        _wrappedObj.get(false);
        assertEquals(_wrappedObj.getUseCount() - useCount, 0);

    }

    @Test
    public void testGetObject() {
        prepareObjects(SerializerType.NoSerialized);

        HCSerializableClass hcSerializableClass = new HCSerializableClass(47, "Forty seven"), tmp = null;

        tmp = _wrappedObj.get();
        assertEquals(hcSerializableClass.getClass(), tmp.getClass());
        assertEquals(hcSerializableClass, tmp);
    }

    @Test
    public void testCompareTo() {
        prepareObjects(SerializerType.NoSerialized);

        ClassData classDataTmp = SerializerFactory.createSerializer(SerializerType.ByteArraySerializer).ser(new HCSerializableClass(48, "Forty eight"));


        ListElement<HCSerializableClass> tmp1 = new ListElement<>(
                new ClassData(classDataTmp.getSerializerType(), classDataTmp.getData(), Long.MAX_VALUE),
                HCSerializableClass.class
        ),
        tmp2 = new ListElement<>(
                SerializerFactory.createSerializer(SerializerType.NoSerialized).ser(new HCSerializableClass(46, "Forty six")),
                HCSerializableClass.class
        );

        // Increase useCount by 1
        _wrappedObj.get();
        tmp1.get();


        ListElement[] wrappedObjects = {
                _wrappedObj, tmp1, tmp2
        };

        ListElement[] expectedOrder = {
                tmp2, tmp1, _wrappedObj
        };

        Arrays.sort(wrappedObjects);

        assertArrayEquals(expectedOrder, wrappedObjects);
    }

    @Test
    public void testAssign() {
        prepareObjects(SerializerType.NoSerialized);

        ListElement<HCSerializableClass> tmp = new ListElement<>(
                SerializerFactory.createSerializer(SerializerType.NoSerialized).ser(new HCSerializableClass(46, "Forty six")),
                HCSerializableClass.class
        );

        try {
            tmp.assign(null);
        } catch (NullPointerException e) {}

        assertNotEquals(_wrappedObj, "");

        tmp.assign(_wrappedObj);
        assertEquals(_wrappedObj, tmp);
    }

    @Test
    public void testCompare() {
        prepareObjects(SerializerType.NoSerialized);
        HCSerializableClass hcSerializableClass = new HCSerializableClass(_scl.getMyId(), _scl.getSomething());

        assertTrue(_wrappedObj.compare(hcSerializableClass));

        hcSerializableClass.setMyId(0);
        assertFalse(_wrappedObj.compare(hcSerializableClass));

        SerializableWOEquals swe = new SerializableWOEquals();
        ListElement<SerializableWOEquals> leswe = new ListElement<>(SerializerFactory.createSerializer(SerializerType.ByteArraySerializer).ser(swe), SerializableWOEquals.class);
        leswe.setIdentityHC(System.identityHashCode(swe));
        assertTrue(leswe.compare(swe));
        assertEquals(SerializerType.ByteArraySerializer, leswe.getData().getSerializerType());
    }

    static class SerializableWOEquals implements Serializable {
        public int i = 3;
        public double d = 5.25;
    }
}
