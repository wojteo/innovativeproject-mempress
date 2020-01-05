package mempress;

import mempress.decision.*;
import mempress.list.ListElement;
import mempress.serialization.SerializerType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Created by Bartek on 2014-11-28.
 */
public class DecisionTreeTest {
    private DecisionTree<SerializableClass> _decTree;
    private SerializableClass _firstElement;
    private SerializableClass _secondElement;
    private static int firstElementNum = 13, secondElementNum = 17;

    @Before
    public void initTest() {
        _decTree = DecisionTreeBuilder.<SerializableClass>create()
                .addTreeElement(new DecisionStoreIt<>())
                .addTreeElement(new DecisionSerializeByteArray<>())
                .addTreeElement(new DecisionSerializeFile<>())
                .build();

//        _firstElement = new NonSerializableClass(firstElementNum);
        _firstElement = new SerializableClass(firstElementNum);
        _secondElement = new SerializableClass(secondElementNum);
    }

    @Test
    public void testProcessObject() {
        assertSame(_decTree.processObject(_firstElement).getData().getSerializerType(), SerializerType.NoSerialized);
        assertSame(_decTree.processObject(_secondElement).getData().getSerializerType(), SerializerType.NoSerialized);

        try {
            _decTree.processObject(null);

        } catch (NullPointerException e) {
        }
    }

    //ListElement<NonSerializableClass> tmp1, tmp2;

    @Test
    public void testProcessObjectWithDifferentStartPoint() {
        assertSame(_decTree.processObject(_secondElement, 1).getData().getSerializerType(), SerializerType.ByteArraySerializer);

        try {
//            _decTree.processObject(_firstElement, 2);
//            fail();

            assertSame(_decTree.processObject(_firstElement, 2).getData().getSerializerType(), SerializerType.FileSerializer);
        } catch (MempressException e) {
        }

        try {
            _decTree.processObject(null, 1);

        } catch (NullPointerException e) {
        }
    }

    @Test
    public void testDemote() {
        ListElement<SerializableClass> tmp1 = _decTree.processObject(_firstElement),
                tmp2 = _decTree.processObject(_secondElement);

        tmp2 = _decTree.demote(tmp2);
        assertSame(tmp2.getData().getSerializerType(), SerializerType.ByteArraySerializer);

        try {
            tmp1 = _decTree.demote(tmp1);
            // fail();
            assertSame(tmp1.getData().getSerializerType(), SerializerType.ByteArraySerializer);
        } catch (MempressException e) {
        }

        tmp2 = _decTree.demote(tmp2);
        assertSame(tmp2.getData().getSerializerType(), SerializerType.FileSerializer);

        try {
            tmp2 = _decTree.demote(tmp2);
            fail();
        } catch (MempressException e) {
        }
    }

    @Test
    public void testGoBackToHighestState() {
        ListElement<SerializableClass> tmp1 = _decTree.processObject(_firstElement),
                tmp2 = _decTree.processObject(_secondElement, 1);

        assertSame(tmp1.getData().getSerializerType(), SerializerType.NoSerialized);
        assertSame(tmp2.getData().getSerializerType(), SerializerType.ByteArraySerializer);

        tmp1 = _decTree.goBackToHighestState(tmp1);
        tmp2 = _decTree.goBackToHighestState(tmp2);

        assertSame(tmp1.getData().getSerializerType(), SerializerType.NoSerialized);
        assertSame(tmp2.getData().getSerializerType(), SerializerType.NoSerialized);
    }
}
