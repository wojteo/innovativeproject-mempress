package mempress;

import mempress.decision.DecisionSerializeByteArray;
import mempress.decision.DecisionTree;
import mempress.list.ListElement;
import mempress.serialization.SerializerType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Bartek on 2014-11-28.
 */
public class DecisionSerializeByteArrayTest {
    private DecisionSerializeByteArray<SerializableClass> _decision1;
    private DecisionSerializeByteArray<NonSerializableClass> _decision2;
    private SerializableClass _sClass;
    private NonSerializableClass _nsClass;
    private DecisionTree.ObjectDataCarrier _odc;

    @Before
    public void initTest() {
        _decision1 = new DecisionSerializeByteArray<>();
        _decision2 = new DecisionSerializeByteArray<>();
        _sClass = new SerializableClass();
        _nsClass = new NonSerializableClass();
        _odc = new DecisionTree.ObjectDataCarrier();
    }

    @Test
    public void testCheckConditions() {
        assertTrue(_decision1.checkConditions(_sClass, _odc));
        assertFalse(_decision2.checkConditions(_nsClass, _odc));
    }

    @Test
    public void testProcessObject() {
        ListElement<SerializableClass> sctmp = null;
        ListElement<NonSerializableClass> nsctmp = null;

        int num = 47;
        _sClass.setNo(num);
        sctmp = _decision1.processObject(_sClass, _odc);
        assertEquals(sctmp.get().getNo(), num);

        try {
            nsctmp = _decision2.processObject(_nsClass, _odc);
            fail();
        } catch (MempressException ex) {
        }
    }

    @Test
    public void testGetOperationType() {
        assertSame(_decision1.getOperationType(), SerializerType.ByteArraySerializer);
    }


}
