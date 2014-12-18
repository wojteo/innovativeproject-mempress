package effitest;

import com.google.common.base.Stopwatch;
import mempress.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


/**
 * Created by Bartek on 2014-12-18.
 */
public class IteratorsEfficiencyTest {
    private SmartList<TestClass> smartList;
    private long[] time1, time2;
    private int numOfElements;

    public IteratorsEfficiencyTest(int numOfElements) {
        this.numOfElements = numOfElements;
    }

    private void init() {
        DecisionTree<TestClass> dt = DecisionTreeBuilder
                .<TestClass>create()
//                .addTreeElement(new DecisionStoreIt<>())
//                .addTreeElement(new DecisionSerializeByteArray<>())
//                .addTreeElement(new DecisionSerializeFile<>())
                .addTreeElement(new DecisionZipSerializeFile<>())
                .build();
        smartList = SmartListBuilder.<TestClass>create()
                .decisionTree(dt)
//                .weightLimit(numOfElements * 4)
                .build();
        time1 = (time1 == null) ? new long[numOfElements] : time1;
        time2 = (time2 == null) ? new long[numOfElements] : time2;
    }

    private void generateElements() {
        for(int i = 1; i <= numOfElements; ++i) {
            TestClass tc = new TestClass(i, Math.pow(i, 2));
            smartList.add(tc);
        }

    }


    public static void main(String[] args) {
        IteratorsEfficiencyTest efficiencyTest = new IteratorsEfficiencyTest(10000);
        efficiencyTest.init();
        efficiencyTest.generateElements();

        Stopwatch sw;

        int index = 0;

        Iterator<TestClass> normalIterator = efficiencyTest.smartList.iterator();

        while (normalIterator.hasNext()) {
            sw = Stopwatch.createStarted();
            normalIterator.next();
            efficiencyTest.time1[index++] = sw.elapsed(TimeUnit.MICROSECONDS);
        }

        efficiencyTest.init();
        efficiencyTest.generateElements();

        Iterator<TestClass> preloadIterator = SmartListIterators.makePreloadIterator(efficiencyTest.smartList, 0, 15);
        index = 0;

        while (preloadIterator.hasNext()) {
            sw = Stopwatch.createStarted();
            preloadIterator.next();
            efficiencyTest.time2[index++] = sw.elapsed(TimeUnit.MICROSECONDS);
        }

        double averageTime1 = Arrays.stream(efficiencyTest.time1).average().getAsDouble(),
                averageTime2 = Arrays.stream(efficiencyTest.time2).average().getAsDouble();
        long sumTime1 = Arrays.stream(efficiencyTest.time1).sum(),
                sumTime2 = Arrays.stream(efficiencyTest.time2).sum();
        System.out.println("[Standard iterator] Entire time in Ms: " + sumTime1 + ", average: " + averageTime1);
        System.out.println("[Preload iterator] Entire time in Ms: " + sumTime2 + ", average: " + averageTime2);

        for(int i = 0; i < 100; ++i)
            System.out.println(String.format("(%d, %d) ", efficiencyTest.time1[i], efficiencyTest.time2[i]));

        System.out.println();
    }
}

class TestClass implements Serializable, Immutable {
    private int ival;
    private double dval;

    public TestClass(int ival, double dval) {
        this.ival = ival;
        this.dval = dval;
    }

    public int getIval() {
        return ival;
    }

    public void setIval(int ival) {
        this.ival = ival;
    }

    public double getDval() {
        return dval;
    }

    public void setDval(double dval) {
        this.dval = dval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestClass testClass = (TestClass) o;

        if (Double.compare(testClass.dval, dval) != 0) return false;
        if (ival != testClass.ival) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = ival;
        temp = Double.doubleToLongBits(dval);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}