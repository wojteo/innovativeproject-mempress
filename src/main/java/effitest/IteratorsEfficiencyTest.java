package effitest;

import com.google.common.base.Stopwatch;
import mempress.Immutable;
import mempress.decision.DecisionTree;
import mempress.decision.DecisionTreeBuilder;
import mempress.decision.DecisionZipSerializeFile;
import mempress.list.SmartList;
import mempress.list.SmartListBuilder;
import mempress.list.SmartListIterators;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


/**
 * Created by Bartek on 2014-12-18.
 */
public class IteratorsEfficiencyTest {
    private static final Logger log = Logger.getLogger(IteratorsEfficiencyTest.class);
    private final int numOfElements;
    private SmartList<TestClass> smartList;
    private long[] time1, time2;

    public IteratorsEfficiencyTest(int numOfElements) {
        this.numOfElements = numOfElements;
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
        log.debug("[Standard iterator] Entire time in Ms: " + sumTime1 + ", average: " + averageTime1);
        log.debug("[Preload iterator] Entire time in Ms: " + sumTime2 + ", average: " + averageTime2);

        for (int i = 0; i < 100; ++i)
            log.debug(String.format("(%d, %d) ", efficiencyTest.time1[i], efficiencyTest.time2[i]));

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
        for (int i = 1; i <= numOfElements; ++i) {
            TestClass tc = new TestClass(i, Math.pow(i, 2));
            smartList.add(tc);
        }

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
    public int hashCode() {
        int result;
        long temp;
        result = ival;
        temp = Double.doubleToLongBits(dval);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestClass testClass = (TestClass) o;

        if (Double.compare(testClass.dval, dval) != 0) return false;
        return ival == testClass.ival;
    }
}