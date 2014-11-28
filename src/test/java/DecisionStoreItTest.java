import mempress.*;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Bartek on 2014-11-28.
 */
public class DecisionStoreItTest {
    private DecisionStoreIt<SerializableClass> _decision1;
    private DecisionStoreIt<NonSerializableClass> _decision2;
    private SerializableClass _sClass;
    private NonSerializableClass _nsClass;
    private DecisionTree.ObjectDataCarrier _odc;

    @Before
    public void initTest() {
        _decision1 = new DecisionStoreIt<>();
        _decision2 = new DecisionStoreIt<>();
        _sClass = new SerializableClass();
        _nsClass = new NonSerializableClass();
        _odc = new DecisionTree.ObjectDataCarrier();
    }

    @Test
    public void testCheckConditions() {
        assertTrue(_decision1.checkConditions(_sClass, _odc));
        assertTrue(_decision2.checkConditions(_nsClass, _odc));
    }

    @Test
    public void testProcessObject() {
        ListElement<SerializableClass> sctmp = null;
        ListElement<NonSerializableClass> nsctmp = null;

        int num = 47;
        _sClass.setNo(num);
        sctmp = _decision1.processObject(_sClass, _odc);
        assertTrue(sctmp.get().getNo() == num);

        try {
            nsctmp = _decision2.processObject(_nsClass, _odc);
            fail();
        } catch (MempressException me) {

        }
    }

    @Test
    public void testGetOperationType() {
        assertTrue(_decision1.getOperationType() == SerializerType.NoSerialized);
    }

    private static class SerializableClass implements Serializable {
        private int no = 1;

        public int getNo() {
            return no;
        }

        public void setNo(int no) {
            this.no = no;
        }
    }

    private static class NonSerializableClass {
        private int no = 2;

        public int getNo() {
            return no;
        }

        public void setNo(int no) {
            this.no = no;
        }
    }
}
