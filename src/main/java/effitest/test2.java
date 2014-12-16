package effitest;
import java.util.List;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import mempress.Immutable;
import mempress.SmartList;
import mempress.SmartListBuilder;

import com.google.common.base.Stopwatch;

class bytewrap implements Serializable, Immutable{

	private static final long serialVersionUID = -7608564785355110018L;
	public byte[] b;
	
	bytewrap(byte[] array){
		this.b=array;
	}
		
	
}


public class test2{
	/**
	 * Funkcja konwertująca nanosekundy na sekundy
	 */
	public static String nanoTime(long time){
		String res = new StringBuilder(time +"").reverse().toString();
		while(res.length()<=9){
			res+="0";
		}
		res = res.substring(0, 9) + "." + res.substring(9, res.length());
		res = new StringBuilder(res).reverse().toString();
		return res;
	}
	
	
	public static void insertOnly(int liczba_plikow, long weightLimit){
		if(weightLimit<0)
			weightLimit=333333;
		
		int countAdd=0;
		long timeAdd=0;
		
		Console co = System.console();
		System.out.println("Starting 'insertOnly', press enter to begin");
		co.readLine();
	
		SmartList<byte[]> sl = SmartListBuilder.<byte[]>create().weightLimit(weightLimit).build();
		int i=0;
		Random rnd = new Random(666);
		try{
			for(i=0; i<30; i++){
				int x = rnd.nextInt(liczba_plikow)+1;
				System.out.print(x+"-> ");
				String pth = "sample"+x+".txt";
				File f = new File(pth);
				InputStream fis = new FileInputStream(f);
				long size = Files.size(Paths.get(pth));
				int s = (int)size;
				byte[] b = new byte[s+100];
				//byte[] b = com.google.common.io.ByteStreams.toByteArray(fis);
				fis.read(b);
				
				Stopwatch sw = Stopwatch.createStarted();
				sl.add(b);
				sw.stop();
				
				timeAdd += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
				countAdd++;
				fis.close();
				
				System.out.println("SmartList.add("+pth+"), SmartList.size = "+sl.size());
			}
			
			
		}catch(OutOfMemoryError e){
			System.out.println("OOME :(");
			e.printStackTrace();
			System.gc();
		}catch(Exception e){
			System.out.println("Something went wrong");
			e.printStackTrace();
		}finally{
			System.out.println("\n"+ countAdd + " Add's with avg time = " + nanoTime(timeAdd/countAdd));
			
		}
		sl.clear();
		System.gc();
		System.out.println("Finished 'insertOnly'");
	}
	
	public static void insertRandomly(int liczba_plikow, long weightLimit){
		if(weightLimit<0)
			weightLimit=333333;
		
		int countAdd=0;
		long timeAdd=0;
		
		Console co = System.console();
		System.out.println("Starting 'insertRandomly', press enter to begin");
		co.readLine();
	
		SmartList<byte[]> sl = SmartListBuilder.<byte[]>create().weightLimit(weightLimit).build();
		int i=0;
		Random rnd = new Random(666);
		Random rnd2 = new Random(333);
		try{
			for(i=0; i<30; i++){
				int x = rnd.nextInt(liczba_plikow)+1;
				int pos=sl.size();
				if(pos>0)
					pos = rnd2.nextInt(sl.size());
				else
					pos=0;
				
				System.out.print(x+"-> ");
				String pth = "sample"+x+".txt";
				File f = new File(pth);
				InputStream fis = new FileInputStream(f);
				long size = Files.size(Paths.get(pth));
				int s = (int)size;
				byte[] b = new byte[s+100];
				//byte[] b = com.google.common.io.ByteStreams.toByteArray(fis);
				fis.read(b);
				
				Stopwatch sw = Stopwatch.createStarted();
					sl.add(pos, b);
					sw.stop();
				
				
				timeAdd += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
				countAdd++;
				fis.close();
				
				System.out.println("SmartList.add("+pos+", "+pth+"), SmartList.size = "+sl.size());
			}
			
			
		}catch(OutOfMemoryError e){
			System.out.println("OOME :(");
			e.printStackTrace();
			System.gc();
		}catch(Exception e){
			System.out.println("Something went wrong");
			e.printStackTrace();
		}finally{
			System.out.println("\n"+ countAdd + " Add's with avg time = " + nanoTime(timeAdd/countAdd));
			
		}
		sl.clear();
		System.gc();
		System.out.println("Finished 'insertRandomly'");
	}
		
	public static void main(String[] args){
		
		//pliki w formacie sampleX.txt gdzie X nalezy do [1, liczba_plikow]
		
	
		System.out.println("Do testów należy utworzyć pliki 'sampleX.txt'");
		
		//configuration
		int addQ=1180, igQ=200, igrQ=200;
		long seed1=666;
		long weightLimit = 1000;
		int liczba_plikow = 800;
		
		//stats
		int countAdd=0, countRem=0, countGet=0;
		long timeAdd=0, timeRem=0, timeGet=0;
		Console co = System.console();
		String type = "";

		System.out.println("What size of weightLimit?");
		type = co.readLine();
		weightLimit = Long.parseLong(type);
		
		System.out.println("How many sample files?");
		type = co.readLine();
		liczba_plikow = Integer.parseInt(type);
		
		System.out.println("How many inserts?");
		type = co.readLine();
		addQ = Integer.parseInt(type);
		
		
		System.out.println("Write 1 for ArrayList or 2 for SmartList");
		type = co.readLine();
		List<bytewrap> sl = null;
		
		
		if(type.equals("1")){
			System.out.println("ArrayListSelected");
			sl = new ArrayList<bytewrap>();
		}else if(type.equals("2")){
			System.out.println("SmartListSelected");
			sl = SmartListBuilder.<bytewrap>create().weightLimit(weightLimit).build();
		}else{
			System.out.println("Wrong. Setting ArrayList as default");
			sl = new ArrayList<bytewrap>();
		}
		
		System.out.println("Starting insertOnly");
		//SmartList<byte[]> sl = SmartListBuilder.<byte[]>create().weightLimit(weightLimit).build();
		//ArrayList<bytewrap> sl = new ArrayList<bytewrap>();
		int i=0;
		Random rnd = new Random(seed1);
		try{
			for(i=0; i<addQ; i++){
				int x = rnd.nextInt(liczba_plikow)+1;
				int pos = sl.size();
				if(pos>0)
					pos = rnd.nextInt( sl.size());
				else
					pos=0;
				
				System.out.print(x+"-> ");
				String pth = "sample"+x+".txt";
				File f = new File(pth);
				InputStream fis = new FileInputStream(f);
				long size = Files.size(Paths.get(pth));
				int s = (int)size;
				byte[] b = new byte[s+100];
				//byte[] b = com.google.common.io.ByteStreams.toByteArray(fis);
				fis.read(b);
				bytewrap bw = new bytewrap(b);
				b=null;
				Stopwatch sw = Stopwatch.createStarted();
					sl.add(pos, bw);
				sw.stop();
				
				timeAdd += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
				countAdd++;
				fis.close();
				
				System.out.println("list.add("+pos+", "+pth+"), list.size = "+sl.size());
			}
			
			
		}catch(OutOfMemoryError e){
			System.out.println("OOME :(");
			e.printStackTrace();
			System.gc();
		}catch(Exception e){
			System.out.println("Something went wrong");
			e.printStackTrace();
		}finally{
			System.out.println("\n"+ countAdd + " Add's with avg time = " + nanoTime(timeAdd/countAdd));
			
		}
		
		System.out.println("Press enter for GC");
		co.readLine();
		System.gc();
		System.out.println("Press enter to delete+GC");
		co.readLine();
		sl = null;
		System.gc();
		System.out.println("Finished 'insertRandomly'\nPress enter again");
		co.readLine();
		
		
	}
};
