package mempress;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Created by Bartek on 2014-12-01.
 */
public class ListElementWithHashCodeTest {
    private static int _le1int = 5, _le2int = 13;
    private static String _le1String = "Five", _le2String = "Thirteen";
    private ListElementWithHashCode<HCSerializableClass> _le1, _le2;

    @Before
    public void initTest() {
        HCSerializableClass hcSerializableClass = new HCSerializableClass(_le1int, _le1String);
        ClassData cd = SerializerFactory.createSerializer(SerializerType.NoSerialized).ser(hcSerializableClass);
        _le1 = new ListElementWithHashCode<>(cd, (Class<HCSerializableClass>)hcSerializableClass.getClass(), hcSerializableClass.hashCode());

        hcSerializableClass = new HCSerializableClass(_le2int, _le2String);
        cd = SerializerFactory.createSerializer(SerializerType.NoSerialized).ser(hcSerializableClass);
        _le2 = new ListElementWithHashCode<>(cd, (Class<HCSerializableClass>)hcSerializableClass.getClass(), hcSerializableClass.hashCode());
    }

    @Test
    public void testAssign() {
        _le1.assign(_le2);
        HCSerializableClass hcClass = _le2.get();
        assertTrue(_le1.compare(hcClass));

        ListElement<HCSerializableClass> le = new ListElement<>(_le2.getData(), _le2.objectType);
        try {
            _le1.assign(le);
            fail();
        } catch (ClassCastException e) {}
    }

    @Test
    public void testGetHashCode() {
        HCSerializableClass hcSerializableClass =
                (HCSerializableClass)SerializerFactory.createSerializer(_le1.getData().getSerializerType()).des(_le1.getData());

        assertEquals( hcSerializableClass.hashCode(), _le1.getHashcode());

    }

    @Test
    public void testSetHashcode() {
        _le1.setHashcode(97);
        assertEquals(97, _le1.getHashcode());
    }

}
