package mempress;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by bartek on 2014-11-13.
 */
public class SmartListArrayListCompareTest {

    @Test
    public void testAddInsertObject() {
        Provider<Object> objProvider = new ObjectProvider(200),
                objProvider2 = new ObjectProvider(200);
        List<Object> testedArrayList = new ArrayList<Object>(),
                testedSmartList = new SmartList<Object>();

        ListTester.TestResults results =
                ListTester.<Object>testCollection(testedArrayList, objProvider, commandAddInsert);
        ListTester.TestResults results2 =
                ListTester.<Object>testCollection(testedSmartList, objProvider2, commandAddInsert);

        System.out.println("\nWyniki testu AddInsert dla ArrayList i SmartList wypełniane obiektami Object: \n");
        System.out.println("ArrayList:");
        System.out.println(results);
        System.out.println("SmartList:");
        System.out.println(results2);
    }

    @Test
    public void testAddInsertRemoveObject() {
        Provider<Object> objProvider = new ObjectProvider(200),
                objProvider2 = new ObjectProvider(200);
        List<Object> testedArrayList = new ArrayList<Object>(),
                testedSmartList = new SmartList<Object>();

        ListTester.TestResults results =
                ListTester.<Object>testCollection(testedArrayList, objProvider, commandAddInsertRemove);
        ListTester.TestResults results2 =
                ListTester.<Object>testCollection(testedSmartList, objProvider2, commandAddInsertRemove);

        System.out.println("\nWyniki testu AddInsertRemove dla ArrayList i SmartList wypełniane obiektami Object: \n");
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
        Provider<DataProvider> objProvider = new DataProviderProvider(ListTester.RANDOM_FACTOR, 200),
                objProvider2 = new DataProviderProvider(ListTester.RANDOM_FACTOR, 200);
        List<DataProvider> testedArrayList = new ArrayList<DataProvider>(),
                testedSmartList = new SmartList<DataProvider>();

        ListTester.TestResults results =
                ListTester.testCollection(testedArrayList, objProvider, commandAddInsertRemove);
        ListTester.TestResults results2 =
                ListTester.testCollection(testedSmartList, objProvider2, commandAddInsertRemove);

        System.out.println("\nWyniki testu AddInsertRemove dla ArrayList i SmartList wypełniane obiektami DataProvider: \n");
        System.out.println("ArrayList:");
        System.out.println(results);
        System.out.println("SmartList:");
        System.out.println(results2);
    }

    private static class DataProviderProvider implements Provider<DataProvider> {
        private DataProvider.ObjectSize[] sizeList =
                DataProvider.ObjectSize.values();
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

    private static class ObjectProvider implements Provider<Object> {
        private int counter = 0;
        private int limit = 1;
        private Object[] data;

        public ObjectProvider(int limit) {
            this.limit = limit;
            data = new Object[limit];
            for(int i = 0; i < limit; ++i)
                data[i] = new Object();
        }

        @Override
        public Object getNext(Object arg) {
            // TODO Auto-generated method stub
            return data[counter++ % limit];
        }
    }


    private static String commandAddInsertRemove = "aaaaaaaaaaaaaaaariiiaiiaiiaiirarairariraaarariiriirrraairrraiarriaaaiiaaaiirirairiiiirairaraiarrriiriiraaraarrrrirraiaaaaaiirrraiiarararairaaariararrraiairrairiiiiriiaiarairairriraaiaaaiaiiraraiaarrri";
    private static String commandAddInsert = "aaiiiaiaiaiiaaaiaaaaiaiaaiiiiaiaiiiaaaiiaaaaiaaaiaaaaaiiaaiiaaiaaaaiaaiaaiiaiaiiiaiiaiaaiaiiiaiiiaaaaaiaaaiaaiaiaiiiaaaaaaiaiaiaiaiiiiiiiiaiaaiaiiaaiaaiiiiiaiiaaaiaiaiaiiiiiaaiiiaaiiaiiiaiiaiiiiaaiaii";
}
