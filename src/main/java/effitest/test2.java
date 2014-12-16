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
import mempress.*;
import com.google.common.base.Stopwatch;

class bytewrap implements Serializable, Immutable{

	private static final long serialVersionUID = -7608564785355110018L;
	public byte[] b;
	
	bytewrap(){}
	
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
	
	public static void main(String[] args){
		
		System.out.println("Files 'sampleX.txt' are required, where X goes from 1 to number of files");
		
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
		
		System.out.println("How many adds, inserts, add/inserts, add/insert/removes (one number for all) ?");
		type = co.readLine();
		igrQ = igQ = addQ = Integer.parseInt(type);
		
		
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
		
		int i=0;
		Random rnd = new Random(seed1);
		try{
			System.out.println("Starting insertOnly");
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
				bytewrap bw = new bytewrap();
				bw.b = new byte[(int)size];
				fis.read(bw.b);
				
				Stopwatch sw = Stopwatch.createStarted();
					sl.add(pos, bw);
				sw.stop();
				
				timeAdd += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
				countAdd++;
				fis.close();
				
				System.out.println("list.add("+pos+", "+pth+"), list.size = "+sl.size());
			}
			System.out.println("Finished insertOnly");
			
			System.out.println("Starting getOnly, press enter");
			co.readLine();
			for(i=0; i<igQ; i++){
				int pos = sl.size();
				if(pos>0)
					pos = rnd.nextInt(sl.size());
				else
					pos=0;
				System.out.print(pos+"<-");
				//random get
					bytewrap bw ;
					Stopwatch sw = Stopwatch.createStarted();
					bw = sl.get(pos);
					sw.stop();
					timeGet += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
					countGet++;
					System.out.println("list.get("+pos+"), list.size = "+sl.size());
				
			}
			System.out.println("Finished getOnly");
			
			
			System.out.println("Starting insert-get, press enter");
			co.readLine();
			for(i=0; i<igQ; i++){
				int x = rnd.nextInt(liczba_plikow)+1;
				int pos = sl.size();
				if(pos>0)
					pos = rnd.nextInt(sl.size());
				else
					pos=0;
				
				if(rnd.nextInt(3)==0){
					//random insert
					System.out.print(x+"-> ");
					String pth = "sample"+x+".txt";
					File f = new File(pth);
					InputStream fis = new FileInputStream(f);
					long size = Files.size(Paths.get(pth));
					bytewrap bw = new bytewrap();
					bw.b = new byte[(int)size];
					fis.read(bw.b);
					
					Stopwatch sw = Stopwatch.createStarted();
						sl.add(pos, bw);
					sw.stop();
					
					timeAdd += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
					countAdd++;
					fis.close();
					
					System.out.println("list.add("+pos+", "+pth+"), list.size = "+sl.size());
				}else{
					//random get
					bytewrap bw ;
					Stopwatch sw = Stopwatch.createStarted();
					bw = sl.get(pos);
					sw.stop();
					timeGet += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
					countGet++;
					System.out.println("list.get("+pos+"), list.size = "+sl.size());
				}
			}
			System.out.println("Finished Add-Get");
			
			System.out.println("Starting Add-Get-Remove, press enter");
			co.readLine();
			for(i=0; i<igQ; i++){
				int x = rnd.nextInt(liczba_plikow)+1;
				int pos = sl.size();
				if(pos>0)
					pos = rnd.nextInt(sl.size());
				else
					pos=0;
				int what = rnd.nextInt(7);
				if(what==0){
					//random insert
					System.out.print(x+"-> ");
					String pth = "sample"+x+".txt";
					File f = new File(pth);
					InputStream fis = new FileInputStream(f);
					long size = Files.size(Paths.get(pth));
					bytewrap bw = new bytewrap();
					bw.b = new byte[(int)size];
					fis.read(bw.b);
					
					Stopwatch sw = Stopwatch.createStarted();
						sl.add(pos, bw);
					sw.stop();
					
					timeAdd += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
					countAdd++;
					fis.close();
					
					System.out.println("list.add("+pos+", "+pth+"), list.size = "+sl.size());
				}else if(what==1 || what==2){
					//random get
					bytewrap bw ;
					Stopwatch sw = Stopwatch.createStarted();
					bw = sl.get(pos);
					sw.stop();
					timeGet += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
					countGet++;
					System.out.println("list.get("+pos+"), list.size = "+sl.size());
				}else{
					//random remove
					bytewrap bw ;
					Stopwatch sw = Stopwatch.createStarted();
					bw = sl.remove(pos);
					sw.stop();
					timeRem += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
					countRem++;
					System.out.println("REM list.remove("+pos+"), list.size = "+sl.size());
					
				}
			}
			System.out.println("Finished Add-Get-Remove");
		}catch(OutOfMemoryError e){
			System.out.println("OOME :(");
			e.printStackTrace();
			System.gc();
		}catch(Exception e){
			System.out.println("Something went wrong");
			e.printStackTrace();
		}finally{
			System.out.println("\nTotal: "+ (countAdd+countGet+countRem) + " operations.");
			System.out.println("\n"+ countAdd + " Adds with avg time = " + nanoTime(timeAdd/countAdd));
			System.out.println("\n"+ countGet + " Gets with avg time = " + nanoTime(timeGet/countGet));
			System.out.println("\n"+ countRem + " Removes with avg time = " + nanoTime(timeRem/countRem));
			
		}
		
		System.out.println("Press enter for GC");
		co.readLine();
		System.gc();
		System.out.println("Press enter to delete+GC");
		co.readLine();
		sl = null;
		System.gc(); 
		System.out.println("Finished\n Press enter again");
		co.readLine();
		
		
	}
};
