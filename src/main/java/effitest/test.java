package effitest;
import mempress.*;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import com.google.common.base.Stopwatch;

import mempress.SmartList;

class wrap implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6073417514699813769L;
	public String[] array=null;
	wrap(){}
	
	wrap(int val){
		String[] a = new String[10000];
		Arrays.fill(a, (val+""));
		this.array=a;
	}
}

public class test{
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
	
	
	public static void insertRemove(int liczba_plikow, long weightLimit){
		if(weightLimit<0)
			weightLimit=333333;
		
		Console co = System.console();
		System.out.println("Starting 'insertRemove', press enter to begin");
		co.readLine();
		
		int countRemove=0, countAdd=0;
		long timeRemove=0, timeAdd=0;
		
		SmartList<byte[]> sl = SmartListBuilder.<byte[]>create().weightLimit(weightLimit).build();
		int i=0;
		Random rnd = new Random(666);
		Random add = new Random(333);
		Stopwatch sw;
		try{
			for(i=0; i<40; i++){
				if(add.nextBoolean()){
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
					
					sw = Stopwatch.createStarted();
					sl.add(b);
					sw.stop();
					timeAdd += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
					countAdd++;
					fis.close();
					System.out.println("SmartList.add("+pth+"), SmartList.size() = "+sl.size());
				}else{
					if(!sl.isEmpty()){
						int x = rnd.nextInt(sl.size());
						
						sw = Stopwatch.createStarted();
						sl.remove(x);
						sw.stop();
						timeRemove += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
						countRemove++;
						System.out.println("r-> SmartList.remove("+x+") OK");
					}else{
						System.out.println("r-> SmartList is empty, can't remove");
					}
				}
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
			System.out.println(countRemove + " Remove's with avg time = " + nanoTime(timeRemove/countRemove));
				
		}
		sl.clear();
		System.gc();
		System.out.println("Finished 'insertRemove'");
	}
	
	
	public static void main(String[] args){
		
		//pliki w formacie sampleX.txt gdzie X nalezy do [1, liczba_plikow]
		
		int liczba_plikow = 4;
		int job = 1;
		if(args.length>0){
			try{
				liczba_plikow=Integer.parseInt(args[0]);
			}catch(Exception e){
				System.out.println("Default: liczba_plikow=4");
			}
			
			try{
				job=Integer.parseInt(args[1]);
			}catch(Exception e){
				System.out.println("Default: job=1");
			}
		}
		long weightLimit = 10000;
		System.out.println("Do testów należy utworzyć pliki 'sampleX.txt' w ilości 4 "
				+ "\nlub podanej jako pierwszy parametr uruchomienia programu."
				+ "\nJako drugi parametr numer testu do wykonania.\n");
		

		switch(job){
			case 1:
				insertOnly(liczba_plikow, weightLimit);
			break;
			case 2:
				insertRemove(liczba_plikow, weightLimit);
			break;
			case 3:
				insertRandomly(liczba_plikow, weightLimit);
			break;
			default:
				System.out.println("Wrong job number");
			break;
		}
		
	}
};
