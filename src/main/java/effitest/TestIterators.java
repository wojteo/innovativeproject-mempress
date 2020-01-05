package effitest;

import com.google.common.base.Stopwatch;
import mempress.decision.DecisionSerializeFile;
import mempress.decision.DecisionTree;
import mempress.decision.DecisionTreeBuilder;
import mempress.list.SmartList;
import mempress.list.SmartListBuilder;
import mempress.list.SmartListIterators;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Random;

public class TestIterators {

    private static final Logger log = Logger.getLogger(TestIterators.class);

    public static void main(String[] args) {
        int pos, i;
        Bytewrap bw = null;
        Random rnd;

        long time;
        int filesQuantity = 1000;
        long seed = 234567L;

        DecisionTree<Bytewrap> d = DecisionTreeBuilder
                .<Bytewrap>create()
                .addTreeElement(new DecisionSerializeFile<>())
                .build();

        SmartList<Bytewrap> sl = SmartListBuilder
                .<Bytewrap>create()
                .weightLimit(1000000)
                .decisionTree(d).build();

        log.debug("List created, starting insertion");
        rnd = new Random(seed);
        for (i = 0; i < 5000; i++) {
            try {
                pos = rnd.nextInt(filesQuantity) + 1;
                bw = genBytewrap(pos);
                sl.add(bw);
            } catch (Exception e) {
            }
        }
        log.debug("Finished inserting");

        log.debug("Starting default iterator test");
        Iterator<Bytewrap> itr = sl.iterator();
        Stopwatch sw;

        i = 0;
        time = 0L;
        rnd = new Random(seed);
        while (itr.hasNext()) {

            pos = rnd.nextInt(filesQuantity) + 1;

            sw = Stopwatch.createStarted();
            Bytewrap retrieved = itr.next();
            sw.stop();

            Bytewrap rebuild = genBytewrap(pos);
            time += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
            if (!testEquals(retrieved.b, rebuild.b)) {
                log.debug(i + " failed" + sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS));
                //result=false;
            }
            i++;
        }
        log.debug("Average time for iterator.next(), using default SmartList iterator\n"
                + TestBasics.nanoTime(time / i) + "s");


        log.debug("Starting preloadIterator with 1 advancement1");
        itr = SmartListIterators.makePreloadIterator(sl, 0, 1);
        i = 0;
        time = 0L;
        rnd = new Random(seed);
        while (itr.hasNext()) {

            pos = rnd.nextInt(filesQuantity) + 1;

            sw = Stopwatch.createStarted();
            Bytewrap retrieved = itr.next();
            sw.stop();

            Bytewrap rebuild = genBytewrap(pos);
            time += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
            if (!testEquals(retrieved.b, rebuild.b)) {
                log.debug(i + " failed" + sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS));
                //result=false;
            }
            i++;
        }
        log.debug("Average time for iterator.next(), using preloadIterator with 1 element in advance\n"
                + TestBasics.nanoTime(time / i) + "s");


        log.debug("Starting preloadIterator with 2 advancement");
        itr = SmartListIterators.makePreloadIterator(sl, 0, 2);
        i = 0;
        time = 0L;
        rnd = new Random(seed);
        while (itr.hasNext()) {
            log.debug("Starting " + i);
            pos = rnd.nextInt(filesQuantity) + 1;

            sw = Stopwatch.createStarted();
            Bytewrap retrieved = itr.next();
            sw.stop();

            Bytewrap rebuild = genBytewrap(pos);
            time += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
            if (!testEquals(retrieved.b, rebuild.b)) {
                log.debug(i + " failed" + sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS));
                //result=false;
            }

            log.debug("Ending " + i);
            i++;
        }
        log.debug("Average time for iterator.next(), using preloadIterator with 2 elements in advance\n"
                + TestBasics.nanoTime(time / i) + "s");


        System.exit(1);
    }

    public static Bytewrap genBytewrap(int pos) {
        Bytewrap bw = null;
        try {
            String pth = "sample" + pos + ".txt";
            File f = new File(pth);
            InputStream fis = new FileInputStream(f);
            long size = Files.size(Paths.get(pth));
            bw = new Bytewrap();
            bw.b = new byte[(int) size];
            fis.read(bw.b);
            fis.close();
        } catch (Exception e) {
        }

        return bw;
    }

//	final static long seed = 234567L;
//	final static int filesQuantity=1000;
//	protected static long time;
//	protected SmartList<bytewrap> sl;

    public static boolean testEquals(byte[] ab, byte[] bb) {
        for (int i = 0; i < ab.length; i++) {
            if (ab[i] != bb[i])
                return false;
        }
        return true;
    }


}


