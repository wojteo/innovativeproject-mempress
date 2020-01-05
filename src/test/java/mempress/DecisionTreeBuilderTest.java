package mempress; /**
 * Created by Bartek on 2014-11-28.
 */

import mempress.decision.DecisionSerializeByteArray;
import mempress.decision.DecisionStoreIt;
import mempress.decision.DecisionTree;
import mempress.decision.DecisionTreeBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class DecisionTreeBuilderTest {
    private DecisionTreeBuilder<SerializableClass> _scobject;

    @Before
    public void initTest() {
        _scobject = DecisionTreeBuilder.create();
    }

    @Test
    public void testAddTreeElement() {
        DecisionTreeBuilder<SerializableClass> tmp = null;

        try {
            _scobject.addTreeElement(null);
            fail();
        } catch (NullPointerException e) {
        }

        List<DecisionTree.DecisionTreeElement<SerializableClass>> elements = new ArrayList<DecisionTree.DecisionTreeElement<SerializableClass>>() {{
            add(new DecisionStoreIt<>());
            add(new DecisionSerializeByteArray<>());
        }};

        assertNotNull(_scobject.addTreeElement(elements.get(0)));
        assertNotNull(_scobject.addTreeElements(elements));

        elements.clear();
        try {
            _scobject.addTreeElements(elements);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            _scobject.addTreeElements(null);
            fail();
        } catch (NullPointerException e) {
        }

        assertNotNull(_scobject.build());
    }

    @Test
    public void testBuildDefaultTree() {
        assertNotNull(DecisionTreeBuilder.<SerializableClass>buildDefaultTree());
    }

    @Test
    public void testAddTreeElements() {

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
}
