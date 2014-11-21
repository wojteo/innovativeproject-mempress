package mempress;

import com.google.common.base.Stopwatch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by bartek on 2014-11-13.
 */

public class SmartListArrayListCompareTest {
    @Test
    public void testAddInsertObject() {
        int len = ArrayListLongTest.commandAddInsert.length();
        Provider<Integer> objProvider = new ObjectProvider(len),
                objProvider2 = new ObjectProvider(len);
        List<Integer> testedArrayList = new ArrayList<>(),
                testedSmartList = new SmartList<>();

        ListTester.TestResults results =
                ListTester.testCollection(testedArrayList, objProvider, ArrayListLongTest.commandAddInsert);
        ListTester.TestResults results2 =
                ListTester.testCollection(testedSmartList, objProvider2, ArrayListLongTest.commandAddInsert);

        System.out.println("\nWyniki testu AddInsert dla ArrayList i SmartList wypełniane obiektami Integer: \n");
        System.out.println("ArrayList:");
        System.out.println(results);
        System.out.println("SmartList:");
        System.out.println(results2);
    }

    @Test
    public void testAddInsertRemoveObject() {
        int len = commandAddInsertRemove.length();
        Provider<Integer> objProvider = new ObjectProvider(len),
                objProvider2 = new ObjectProvider(len);
        List<Integer> testedArrayList = new ArrayList<>();
        DecisionTree<Integer> dt = new DecisionTree<Integer>() {
            {
                processors.add(new DecisionStoreIt<>());
                processors.add(new DecisionSerializeByteArray<>());
                processors.add(new DecisionSerializeFile<>());
            }

            @Override
            public ListElement<Integer> demote(ListElement<Integer> wrappedObj) {
                long oldSize = wrappedObj.getSize(), newSize;
                System.out.print("Demote from " + wrappedObj.getData().getSerializerType() + " to ");
                super.demote(wrappedObj);
                System.out.println(wrappedObj.getData().getSerializerType());
                newSize = wrappedObj.getSize();
                System.out.println(oldSize + "b -> " + newSize);
                return wrappedObj;

            }
        };
        List<Integer> testedSmartList = new SmartList<Integer>(dt, len * 2) {
            @Override
            protected long demoteElements(int numOfElements) {
                System.out.println("demoteElements called, maxWeight: " + getMaximumWeight() + ", currentWeight: " + getCurrentWeight());
                return super.demoteElements(numOfElements);
            }

            @Override
            public boolean add(Integer integer) {
                System.out.println("Before addition; maxWeight: " + getMaximumWeight() + ", currentWeight: " + getCurrentWeight());
                return super.add(integer);
            }

            @Override
            public void add(int index, Integer element) {
                System.out.println("Before addition; maxWeight: " + getMaximumWeight() + ", currentWeight: " + getCurrentWeight());
                super.add(index, element);
            }
        };

        ListTester.TestResults results =
                ListTester.testCollection(testedArrayList, objProvider, commandAddInsertRemove);
        ListTester.TestResults results2 =
                ListTester.testCollection(testedSmartList, objProvider2, commandAddInsertRemove);

        System.out.println("\nWyniki testu AddInsertRemove dla ArrayList i SmartList wypełniane obiektami Integer: \n");
        System.out.println("ArrayList:");
        System.out.println(results);
        System.out.println("SmartList:");
        System.out.println(results2);
    }

    @Test
    public void testAddInsertDataProvider() {
        Provider<DataProvider> objProvider = new DataProviderProvider(ListTester.RANDOM_FACTOR, 200),
                objProvider2 = new DataProviderProvider(ListTester.RANDOM_FACTOR, 200);
        List<DataProvider> testedArrayList = new ArrayList<DataProvider>(),
                testedSmartList = new SmartList<DataProvider>();

        ListTester.TestResults results =
                ListTester.testCollection(testedArrayList, objProvider, commandAddInsert);
        ListTester.TestResults results2 =
                ListTester.testCollection(testedSmartList, objProvider2, commandAddInsert);

        System.out.println("\nWyniki testu AddInsert dla ArrayList i SmartList wypełniane obiektami DataProvider: \n");
        System.out.println("ArrayList:");
        System.out.println(results);
        System.out.println("SmartList:");
        System.out.println(results2);
    }

    @Test
    public void testAddInsertRemoveDataProvider() {
        Provider<DataProvider> objProvider = new DataProviderProvider(ListTester.RANDOM_FACTOR, 200+200),
                objProvider2 = new DataProviderProvider(ListTester.RANDOM_FACTOR, 200+200);
        List<DataProvider> testedArrayList = new ArrayList<DataProvider>(),
                testedSmartList = new SmartList<DataProvider>();

        ListTester.TestResults results =
                ListTester.testCollection(testedArrayList, objProvider, commandAddInsertRemove+commandAddInsertRemove);
        ListTester.TestResults results2 =
                ListTester.testCollection(testedSmartList, objProvider2, commandAddInsertRemove+commandAddInsertRemove);

        System.out.println("\nWyniki testu AddInsertRemove dla ArrayList i SmartList wypełniane obiektami DataProvider: \n");
        System.out.println("ArrayList:");
        System.out.println(results);
        System.out.println("SmartList:");
        System.out.println(results2);
    }

    @Test
    public void testAddInsertRemoveDataProviderLimitedSize() {
        Provider<DataProvider> objProvider = new DataProviderProvider(ListTester.RANDOM_FACTOR, 200+200),
                objProvider2 = new DataProviderProvider(ListTester.RANDOM_FACTOR, 200+200);
        List<DataProvider> testedArrayList = new ArrayList<DataProvider>();
        DecisionTree<DataProvider> dt = new DecisionTree<DataProvider>() {
            {
                processors.add(new DecisionStoreIt<>());
                processors.add(new DecisionSerializeByteArray<>());
                processors.add(new DecisionSerializeFile<>());
            }

            @Override
            public ListElement<DataProvider> demote(ListElement<DataProvider> wrappedObj) {
                long oldSize = wrappedObj.getSize(), newSize;
                System.out.print("[DP]Demote from " + wrappedObj.getData().getSerializerType() + " to ");
                super.demote(wrappedObj);
                System.out.println(wrappedObj.getData().getSerializerType());
                newSize = wrappedObj.getSize();
                System.out.println(oldSize + "b -> " + newSize);
                return wrappedObj;

            }
        };
        SmartList<DataProvider> testedSmartList = new SmartList<DataProvider>(dt, 10240) {
            @Override
            protected long demoteElements(int numOfElements) {
                System.out.println("[DP]demoteElements called, maxWeight: " + getMaximumWeight() + ", currentWeight: " + getCurrentWeight());
                return super.demoteElements(numOfElements);
            }

            @Override
            public boolean add(DataProvider dataProvider) {
                System.out.println("[DP]Before addition; maxWeight: " + getMaximumWeight() + ", currentWeight: " + getCurrentWeight());
                return super.add(dataProvider);
            }

            @Override
            public void add(int index, DataProvider element) {
                System.out.println("[DP]Before addition; maxWeight: " + getMaximumWeight() + ", currentWeight: " + getCurrentWeight());
                super.add(index, element);
            }

            @Override
            public void clear() {
                int wrapped, barr, serf, oth;
                wrapped = barr = serf = oth = 0;

                for(ListElement<DataProvider> le: _list) {
                    switch (le.getData().getSerializerType())
                    {
                        case NoSerialized:
                            ++wrapped;
                            break;
                        case ByteArraySerializer:
                            ++barr;
                            break;
                        case FileSerializer:
                            ++serf;
                            break;
                        default:
                            ++oth;
                            break;
                    }
                }

                System.out.println(String.format("List elements types in SmartList:\n\tWrapped w-out serialization object: %d\n\tSerialized to byte array: %d\n\tSerialized to file: %d\n\tOther: %d\n",
                        wrapped, barr, serf, oth));

                super.clear();
            }
        };

        ListTester.TestResults results =
                ListTester.testCollection(testedArrayList, objProvider, commandAddInsertRemove+commandAddInsertRemove);
        ListTester.TestResults results2 =
                ListTester.testCollection(testedSmartList, objProvider2, commandAddInsertRemove+commandAddInsertRemove);

        System.out.println(String.format("\nWyniki testu AddInsertRemove dla ArrayList i SmartList wypełniane obiektami DataProvider, dla SmartList o ograniczonej pojemnosci (%d b): \n",
                testedSmartList.getMaximumWeight()));
        System.out.println("ArrayList:");
        System.out.println(results);
        System.out.println("SmartList:");
        System.out.println(results2);
        testedSmartList.clear();
    }

    @Test
    public void factorialTest() {
        Integer[] c = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        List<Integer> coefficients = Arrays.asList(c);
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new SmartList<>(1);

        Assert.assertTrue(list1.addAll(coefficients));
        Assert.assertTrue(list2.addAll(coefficients));

        int factorial1 = 1, factorial2 = 1;
        long time1, time2;
        Stopwatch stopwatch = Stopwatch.createStarted();
        for(int i = 0; i < 10; ++i) {
            factorial1 *= list1.get(i);
        }
        time1 = stopwatch.elapsed(TimeUnit.NANOSECONDS);
        stopwatch = Stopwatch.createStarted();
        for(int i = 0; i < 10; ++i) {
            factorial2 *= list2.get(i);
        }
        time2 = stopwatch.elapsed(TimeUnit.NANOSECONDS);

        System.out.println("ArrayList: 10! = " + factorial1 + "in " + time1 + "ns");
        System.out.println("SmartList: 10! = " + factorial2 + "in " + time2 + "ns");
    }

    private static class DataProviderProvider implements Provider<DataProvider> {
        private DataProvider.ObjectSize[] sizeList =
                new DataProvider.ObjectSize[] { DataProvider.ObjectSize.SMALL, DataProvider.ObjectSize.MEDIUM, DataProvider.ObjectSize.BIG };
        private Random random;
        private int numOfElements = 1;
        private DataProvider[] data;
        int index = 0;


        public DataProviderProvider(int seed, int numOfElements) {

            random = new Random(seed);

            data = new DataProvider[numOfElements];
            for(int i = 0; i < data.length; ++i)
                data[i] = new DataProvider(sizeList[random.nextInt(sizeList.length)]);

            this.numOfElements = numOfElements;
        }

        @Override
        public DataProvider getNext(Object arg) {
            return data[index++ % data.length];
        }

    }

    private static class ObjectProvider implements Provider<Integer> {
        private int counter = 0;
        private int limit = 1;
        private Integer[] data;

        public ObjectProvider(int limit) {
            this.limit = limit;
            data = new Integer[limit];
            for(int i = 0; i < limit; ++i)
                data[i] = new Integer(i);
        }

        @Override
        public Integer getNext(Object arg) {
            // TODO Auto-generated method stub
            return data[counter++ % limit];
        }
    }


    private static String commandAddInsertRemove = "aaaaaaaaaaaaaaaariiiaiiaiiaiirarairariraaarariiriirrraairrraiarriaaaiiaaaiirirairiiiirairaraiarrriiriiraaraarrrrirraiaaaaaiirrraiiarararairaaariararrraiairrairiiiiriiaiarairairriraaiaaaiaiiraraiaarrri";
    private static String commandAddInsert = "aaiiiaiaiaiiaaaiaaaaiaiaaiiiiaiaiiiaaaiiaaaaiaaaiaaaaaiiaaiiaaiaaaaiaaiaaiiaiaiiiaiiaiaaiaiiiaiiiaaaaaiaaaiaaiaiaiiiaaaaaaiaiaiaiaiiiiiiiiaiaaiaiiaaiaaiiiiiaiiaaaiaiaiaiiiiiaaiiiaaiiaiiiaiiaiiiiaaiaii";

}
