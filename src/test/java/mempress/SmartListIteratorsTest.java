package mempress;

import com.google.common.base.Preconditions;
import org.junit.Before;
import org.junit.Test;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
/**
 * Created by Bartek on 2014-12-02.
 */
public class SmartListIteratorsTest {
    private SmartList<SerializableClass> _testedList;
    private SerializableClass firstElement, secondElement, thirdElement;
    private List<SerializableClass> _serializableClasses;

    @Before
    public void initTest() {
        _testedList = SmartListBuilder.<SerializableClass>create()
                .weightLimit(1)
                .decisionTree(DecisionTreeBuilder.<SerializableClass>create()
                        .addTreeElement(new DecStoreIt<>())
                        .addTreeElement(new DecisionSerializeByteArray<>())
                        .build())
                .build();
        _testedList.add((firstElement = make(1)));
        _testedList.add((secondElement = make(2)));
        _testedList.add((thirdElement = make(3)));

        _serializableClasses = new ArrayList<>(4);
        _serializableClasses.add(make(4));
        _serializableClasses.add(make(5));
        _serializableClasses.add(make(6));
    }

    private SerializableClass cloneSC(SerializableClass sc) {
        Preconditions.checkNotNull(sc);
        return new SerializableClass(sc.getNo());
    }

    private SerializableClass make(int n) {
        return new SerializableClass(n);
    }

    @Test
    public void testPreloadIterator() {
//        try { Thread.sleep(10000); } catch (Exception e) { throw new RuntimeException("Exception occured during testing.", e); }

        _testedList.demoteElements(3);
        SmartListIterators.PreloadIterator<SerializableClass> it = (SmartListIterators.PreloadIterator<SerializableClass>) SmartListIterators.<SerializableClass>makePreloadIterator(_testedList, 0, 3);

        assertEquals(SerializerType.ByteArraySerializer, _testedList._list.get(0).getData().getSerializerType());
        assertEquals(SerializerType.ByteArraySerializer, _testedList._list.get(1).getData().getSerializerType());
        assertEquals(SerializerType.ByteArraySerializer, _testedList._list.get(2).getData().getSerializerType());

        it.next();
//        try { Thread.sleep(20000); } catch (Exception e) { throw new RuntimeException("Exception occured during testing.", e); }
//        while(!it.tasks.isTerminated());
        try { it.tasks.awaitTermination(60, TimeUnit.SECONDS); } catch (Exception e) { throw new RuntimeException("Exception occured during testing.", e); }

        assertEquals(SerializerType.NoSerialized, _testedList._list.get(1).getData().getSerializerType());
        assertEquals(SerializerType.NoSerialized, _testedList._list.get(2).getData().getSerializerType());
    }

    @Test
    public void testPreloadIterator2() {
        _testedList.demoteElements(3);
        SmartListIterators.PreloadIterator<SerializableClass> it = (SmartListIterators.PreloadIterator<SerializableClass>) SmartListIterators.<SerializableClass>makePreloadIterator(_testedList, 0, 3);

        assertEquals(SerializerType.ByteArraySerializer, _testedList._list.get(0).getData().getSerializerType());
        assertEquals(SerializerType.ByteArraySerializer, _testedList._list.get(1).getData().getSerializerType());
        assertEquals(SerializerType.ByteArraySerializer, _testedList._list.get(2).getData().getSerializerType());

        assertEquals(firstElement, it.next());
        assertEquals(secondElement, it.next());
        assertEquals(thirdElement, it.next());
    }

    private static class DecStoreIt<E> implements DecisionTree.DecisionTreeElement<E> {
        @Override
        public boolean checkConditions(E obj, DecisionTree.ObjectDataCarrier metadata) {
            return true;
        }

        @Override
        public ListElement<E> processObject(E obj, DecisionTree.ObjectDataCarrier metadata) {
            Class<E> objType = (Class<E>)obj.getClass();

            Serializer pser = SerializerFactory.createSerializer(SerializerType.NoSerialized);
            ClassData cd = pser.ser(obj);

//		ListElement<E> elem = new WrappedListElement<E>(cd, hash, objType);
            ClassData cd2 = new ClassData(cd.getSerializerType(), cd.getData(), Long.MAX_VALUE);
            ListElement<E> elem = new ListElement<>(cd2, objType);
            return elem;
        }

        @Override
        public SerializerType getOperationType() {
            return SerializerType.NoSerialized;
        }
    }
}
