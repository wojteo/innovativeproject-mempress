package effitest;

import com.google.common.base.Stopwatch;
import mempress.Immutable;
import mempress.MempressException;
import mempress.decision.DecisionSerializeByteArray;
import mempress.decision.DecisionSerializeFile;
import mempress.decision.DecisionTree;
import mempress.decision.DecisionTreeBuilder;
import mempress.list.SmartListBuilder;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class Bytewrap implements Serializable, Immutable {
    private static final Logger log = Logger.getLogger(Bytewrap.class);
    private static final long serialVersionUID = -7608564785355110018L;
    public byte[] b;

    Bytewrap() {
    }

    Bytewrap(byte[] array) {
        this.b = array;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.b);
    }

    @Override
    public boolean equals(Object obj) {
        final Bytewrap other = (Bytewrap) obj;
        return Arrays.equals(this.b, other.b);
    }

}

public class TestBasics {
    private static final Logger log = Logger.getLogger(TestBasics.class);
    public static int parametersQ;

    public static void main(String[] args) {
        //int slip=3600;
        int i;

        Stopwatch swMain = Stopwatch.createStarted();


        //files in format sampleX.txt where X is in[1, liczba_plikow]
        log.debug("Need 'sampleX.txt' files");

//configuration variable declaration
        int addQ = 1180, igQ = 200, igrQ = 200;
        long seed1 = 666;
        long weightLimit = 1000;
        int liczba_plikow = 800;
        parametersQ = 6; //How many parameters for configuration
        boolean tryConsole = false;
        String[] confData = null;
        String whichList;
        String filesDir = "";

//stats
        int countAdd = 0, countRem = 0, countGet = 0;
        long timeAdd = 0, timeRem = 0, timeGet = 0;
        String type = "";

//START reading configuration
        Console co = System.console();
        log.debug("Do you want to use configuration file?\n"
                + "'n' for no, <filename> for yes");
        type = co.readLine();

        if (!type.equals("n")) {
            try {
                confData = getConfFile(type);
                if (confData.length != parametersQ) {
                    throw new Exception("Wrong ammount of parameters");
                }
            } catch (Exception e) {
                log.debug("Failed to load configuration file properly");
                tryConsole = true;

            }
        } else {
            tryConsole = true;
        }

        if (tryConsole) {
            confData = getConfConsole();
        }

//Finished READING configuration

//START Setting configuration
        weightLimit = Long.parseLong(confData[0]);
        liczba_plikow = Integer.parseInt(confData[1]);
        igrQ = igQ = addQ = Integer.parseInt(confData[2]);
        seed1 = Long.parseLong(confData[3]);
        whichList = confData[4];
        filesDir = confData[5];
//FINISH Setting configuration

//START creating CSV file
        for (i = 1; ; i++) {
            File f = new File("dane" + i + ".csv");
            if (!(f.exists() && !f.isDirectory())) {
                break;
            }
        }

        File csv = new File("dane" + i + ".csv");
        FileWriter writer = null;
        int tick = 0;
        try {
            csv.createNewFile();
            writer = new FileWriter(csv);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //START writing configuration to CSV file
        try {
            writer.append("weightLimit,filesQuantity,operationsQuantity,seed,filesDir,,,addsAvg,getAvg,remAvg,runtime,List\n").flush();
            writer.append(String.format("%d,%d,%d,%d,%s,,,=A%d,=B%d,=C%d,=D%d,", weightLimit, liczba_plikow, addQ, seed1, filesDir, 4 * addQ + 7, 4 * addQ + 7, 4 * addQ + 7, 4 * addQ + 7));
            switch (whichList) {
                case "0":
                    writer.append("Array");
                    break;
                case "1":
                    writer.append("Smart");
                    break;
                case "2":
                    writer.append("ZipSmart");
                    break;
                case "3":
                    writer.append("HashCodeSmart");
                    break;
                case "4":
                    writer.append("ZipHashCodeSmart");
                    break;
            }
            writer.append("List\n\n").flush();
            writer.append("tick,add,get,remove\n").flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //FINISH writing configuration to CSV file

//FINISH creating CSV file

//START Create list
        List<Bytewrap> sl = null;
        switch (whichList) {
            case "0":
                log.debug("ArrayListSelected");
                sl = new ArrayList<Bytewrap>();
                break;
            case "1":
                //Start-1-Normal SmartList
                sl = SmartListBuilder.<Bytewrap>create()
                        .weightLimit(weightLimit)
                        .build();
                //End-1
                break;
            case "2": {
                //Start-2-OnlyZipByteArray/File SmartList
                log.debug("SmartListSelected");
                DecisionTree<Bytewrap> d = DecisionTreeBuilder
                        .<Bytewrap>create()
                        .addTreeElement(new DecisionSerializeByteArray())
                        .addTreeElement(new DecisionSerializeFile<Bytewrap>())
                        .build();

                sl = SmartListBuilder.<Bytewrap>create().
                        weightLimit(weightLimit)
                        .decisionTree(d)
                        .build();
                //End-2
                break;
            }
            case "3":
                //Start-3-Normal HashCodeSmartList
                sl = SmartListBuilder.<Bytewrap>create()
                        .weightLimit(weightLimit)
                        .elementsProvideHashCode(true)
                        .build();
                //End-3
                break;
            case "4": {

                //Start-4-OnlyZipByteArray/File HashCodeSmartList
                log.debug("SmartListSelected");
                DecisionTree<Bytewrap> d = DecisionTreeBuilder
                        .<Bytewrap>create()
                        .addTreeElement(new DecisionSerializeByteArray())
                        .addTreeElement(new DecisionSerializeFile<Bytewrap>())
                        .build();

                sl = SmartListBuilder.<Bytewrap>create()
                        .elementsProvideHashCode(true)
                        .weightLimit(weightLimit)
                        .decisionTree(d)
                        .build();
                //End-4
                break;
            }
            default:
                log.debug("Didn't select List properly -> closing program");
                System.exit(0);
        }
//FINISH create list

//START TESTS
        try {
            Random rnd = new Random(seed1);
            log.debug("Starting insertOnly");
            for (i = 0; i < addQ; i++) {
                int x = rnd.nextInt(liczba_plikow) + 1;
                int pos = sl.size();
                if (pos > 0)
                    pos = rnd.nextInt(sl.size());
                else
                    pos = 0;

                System.out.print(x + "-> ");
                String pth = filesDir + "sample" + x + ".txt";
                File f = new File(pth);
                InputStream fis = new FileInputStream(f);
                long size = Files.size(Paths.get(pth));

                Bytewrap bw = new Bytewrap();
                bw.b = new byte[(int) size];
                fis.read(bw.b);

                Stopwatch sw = Stopwatch.createStarted();
                sl.add(pos, bw);
                sw.stop();

                writer.append(tick + "," + (sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS) + ",,\n")).flush();
                tick++;

                timeAdd += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
                countAdd++;
                fis.close();

                log.debug("list.add(" + pos + ", " + pth + "), list.size = " + sl.size());
            }
            log.debug("Finished insertOnly");

            log.debug("Starting getOnly");
            //co.readLine();
            for (i = 0; i < igQ; i++) {
                //if(i%500==0) Thread.sleep(slip);
                int pos = sl.size();
                if (pos > 0)
                    pos = rnd.nextInt(sl.size());
                else
                    pos = 0;
                System.out.print(pos + "<-");
                //random get
                @SuppressWarnings("unused")
                Bytewrap bw;
                Stopwatch sw = Stopwatch.createStarted();
                bw = sl.get(pos);
                sw.stop();

                writer.append(tick + ",," + (sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS) + ",\n")).flush();
                tick++;
                timeGet += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
                countGet++;
                log.debug("list.get(" + pos + "), list.size = " + sl.size());

            }
            log.debug("Finished getOnly");


            log.debug("Starting insert-get");
            //co.readLine();
            for (i = 0; i < igQ; i++) {
                //if(i%500==0) Thread.sleep(slip);
                int x = rnd.nextInt(liczba_plikow) + 1;
                int pos = sl.size();
                if (pos > 0)
                    pos = rnd.nextInt(sl.size());
                else
                    pos = 0;

                if (rnd.nextInt(3) == 0) {
                    //random insert
                    System.out.print(x + "-> ");
                    String pth = filesDir + "sample" + x + ".txt";
                    File f = new File(pth);
                    InputStream fis = new FileInputStream(f);
                    long size = Files.size(Paths.get(pth));

                    Bytewrap bw = new Bytewrap();
                    bw.b = new byte[(int) size];
                    fis.read(bw.b);

                    Stopwatch sw = Stopwatch.createStarted();
                    sl.add(pos, bw);
                    sw.stop();

                    writer.append(tick + "," + (sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS) + ",,\n")).flush();
                    tick++;
                    timeAdd += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
                    countAdd++;
                    fis.close();

                    log.debug("list.add(" + pos + ", " + pth + "), list.size = " + sl.size());
                } else {
                    //random get
                    @SuppressWarnings("unused")
                    Bytewrap bw;
                    Stopwatch sw = Stopwatch.createStarted();
                    bw = sl.get(pos);
                    sw.stop();

                    writer.append(tick + ",," + (sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS) + ",\n")).flush();
                    tick++;
                    timeGet += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
                    countGet++;
                    log.debug("list.get(" + pos + "), list.size = " + sl.size());
                }
            }
            log.debug("Finished Add-Get");

            log.debug("Starting Add-Get-Remove");
            //co.readLine();
            for (i = 0; i < igrQ; i++) {
                //if(i%500==0) Thread.sleep(slip);
                int x = rnd.nextInt(liczba_plikow) + 1;
                int pos = sl.size();
                if (pos > 0)
                    pos = rnd.nextInt(sl.size());
                else
                    pos = 0;
                int what = rnd.nextInt(7);
                if (what == 0) {
                    //random insert
                    System.out.print(x + "-> ");
                    String pth = filesDir + "sample" + x + ".txt";
                    File f = new File(pth);
                    InputStream fis = new FileInputStream(f);
                    long size = Files.size(Paths.get(pth));

                    Bytewrap bw = new Bytewrap();
                    bw.b = new byte[(int) size];
                    fis.read(bw.b);

                    Stopwatch sw = Stopwatch.createStarted();
                    sl.add(pos, bw);
                    sw.stop();

                    writer.append(tick + "," + (sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS) + ",,\n")).flush();
                    tick++;
                    timeAdd += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
                    countAdd++;
                    fis.close();

                    log.debug("list.add(" + pos + ", " + pth + "), list.size = " + sl.size());
                } else if (what == 1 || what == 2) {
                    //random get
                    @SuppressWarnings("unused")
                    Bytewrap bw;
                    Stopwatch sw = Stopwatch.createStarted();
                    bw = sl.get(pos);
                    sw.stop();

                    writer.append(tick + ",," + (sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS) + ",\n")).flush();
                    tick++;
                    timeGet += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
                    countGet++;
                    log.debug("list.get(" + pos + "), list.size = " + sl.size());
                } else {
                    //random remove
                    @SuppressWarnings("unused")
                    Bytewrap bw;
                    Stopwatch sw = Stopwatch.createStarted();
                    bw = sl.remove(pos);
                    sw.stop();

                    writer.append(tick + ",,," + (sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS) + "\n")).flush();
                    tick++;
                    timeRem += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
                    countRem++;
                    log.debug("REM list.remove(" + pos + "), list.size = " + sl.size());

                }
            }
            log.debug("Finished Add-Get-Remove");
        } catch (OutOfMemoryError e) {
            log.debug("OOME :(");
            e.printStackTrace();
            System.gc();
        } catch (MempressException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            log.debug("\nTotal: " + (countAdd + countGet + countRem) + " operations.");
            log.debug("\n" + countAdd + " Adds with avg time = " + nanoTime(timeAdd / countAdd));
            log.debug("\n" + countGet + " Gets with avg time = " + nanoTime(timeGet / countGet));
            log.debug("\n" + countRem + " Removes with avg time = " + nanoTime(timeRem / countRem));
            log.debug("\nDetailed measures saved in " + csv.getName() + " file");
        }
//FINISH tests

        swMain.stop();

        try {
            writer.append("\n addsAvg,getAvg,remAvg,runtime\n"
                    + nanoTime(timeAdd / countAdd) + "," + nanoTime(timeGet / countGet) + ","
                    + nanoTime(timeRem / countRem) + "," + nanoTime(swMain.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS)))
                    .flush();
        } catch (IOException e) {
        }


        log.debug("Program was running for (seconds): " + nanoTime(swMain.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS)));
        log.debug("Press enter for GC");
        co.readLine();
        System.gc();
        log.debug("Press enter to delete+GC");
        co.readLine();
        sl = null;
        System.gc();
        log.debug("Finished");

        System.exit(1);
    }

    ///Method reading configuration from file
    public static String[] getConfFile(String path) throws IOException {

        byte[] confFileContent = Files.readAllBytes(Paths.get(path));

        return new String(confFileContent).split("\n");
    }

    ///Method reading configuration from console
    public static String[] getConfConsole() {
        String[] confData = new String[parametersQ];

        Console co2 = System.console();
        log.debug("What size of weightLimit?");
        confData[0] = co2.readLine();
        log.debug("How many sample files?");
        confData[1] = co2.readLine();

        log.debug("How many adds, inserts, add/inserts, add/insert/removes (one number for all) ?");
        confData[2] = co2.readLine();

        log.debug("What seed for RNG?");
        confData[3] = co2.readLine();

        log.debug("Write 0 for ArrayList, 1 or 2 for SmartList, 3 or 4 for HashCodeSmartList ");
        confData[4] = co2.readLine();

        log.debug("Give path to sample files");
        confData[5] = co2.readLine();

        return confData;
    }

    ///Method converting nanoseconds to seconds
    public static String nanoTime(long time) {
        String res = new StringBuilder(time + "").reverse().toString();
        while (res.length() <= 9) {
            res += "0";
        }
        res = res.substring(0, 9) + "." + res.substring(9);
        res = new StringBuilder(res).reverse().toString();
        return res;
    }
}
