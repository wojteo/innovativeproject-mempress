import mempress.DecisionSerializeByteArray;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

/**
 * Created by Bartek on 2014-11-28.
 */
public class DecisionSerializeByteArrayTest {
    private DecisionSerializeByteArray<Integer> _decision;

    @Before
    public void initTest() {
        _decision = new DecisionSerializeByteArray<>();
    }

    @Test
    public void testCheckConditions() {

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

    private static class NonSerializableClass implements Serializable {
        private int no = 2;

        public int getNo() {
            return no;
        }

        public void setNo(int no) {
            this.no = no;
        }
    }
}
