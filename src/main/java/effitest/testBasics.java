package effitest;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mempress.DecisionSerializeFile;
import mempress.DecisionTree;
import mempress.DecisionTreeBuilder;
import mempress.Immutable;
import mempress.MempressException;
import mempress.SmartListBuilder;

import com.google.common.base.Stopwatch;

class bytewrap implements Serializable, Immutable{

	private static final long serialVersionUID = -7608564785355110018L;
	public byte[] b;
	
	bytewrap(){}
	
	bytewrap(byte[] array){
		this.b=array;
	}	
	
}

public class testBasics{
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
		//int slip=3600;
		int i;
		for(i=1;;i++){
			File f = new File("dane"+i+".csv");
			if(!(f.exists() && !f.isDirectory())){
				break;
			}
		}
		
		File csv = new File("dane"+i+".csv");
		FileWriter writer=null;
		int tick=0;
		try{
			csv.createNewFile();
			writer = new FileWriter(csv);
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
		
		//pliki w formacie sampleX.txt gdzie X nalezy do [1, liczba_plikow]
		System.out.println("Do testów należy utworzyć pliki 'sampleX.txt'");
		
		//configuration
		int addQ=1180, igQ=200, igrQ=200;
		long seed1=666;
		long weightLimit = 1000;
		int liczba_plikow = 800;
		boolean dontUseFile=true;
		
		//stats
		int countAdd=0, countRem=0, countGet=0;
		long timeAdd=0, timeRem=0, timeGet=0;
		Console co = System.console();
		String type = "";
		
		System.out.println("Do you want to use configuration file?\n"
				+ "'n' for no, <filename> for yes");
		type=co.readLine();
		String[] confData=null;
		if(!type.equals("n")){
			try{
				byte[] encoded = Files.readAllBytes(Paths.get(type));
				confData = new String(encoded).split("\n");
				if(confData.length==5)	dontUseFile=false;
			}catch(Exception e){
				System.out.println("Failed to load configuration file");
			}
		}
		

		System.out.println("What size of weightLimit?");
		if(dontUseFile){
			type = co.readLine();
		}else{
			type=confData[0];
		}
		weightLimit = Long.parseLong(type);
		
		System.out.println("How many sample files?");
		if(dontUseFile){
			type = co.readLine();
		}else{
			type=confData[1];
		}
		liczba_plikow = Integer.parseInt(type);
		
		System.out.println("How many adds, inserts, add/inserts, add/insert/removes (one number for all) ?");
		if(dontUseFile){
			type = co.readLine();
		}else{
			type=confData[2];
		}
		igrQ = igQ = addQ = Integer.parseInt(type);
		
		System.out.println("What seed for RNG?");
		if(dontUseFile){
			type = co.readLine();
		}else{
			type=confData[3];
		}
		seed1 = Long.parseLong(type);
				
		System.out.println("Write 1 for ArrayList or 2 for SmartList");
		if(dontUseFile){
			type = co.readLine();
		}else{
			type=confData[4];
		}
		List<bytewrap> sl = null;
		
		
		if(type.equals("1")){
			System.out.println("ArrayListSelected");
			sl = new ArrayList<bytewrap>();
		}else if(type.equals("2")){
			/*
			//Start-1-OnlyZipFileSerialization SmartList
			System.out.println("SmartListSelected");
			DecisionTree< bytewrap> d = DecisionTreeBuilder
					.<bytewrap>create()
					.addTreeElement(new DecisionSerializeFile<bytewrap>()) //change to DecisionSerializeFile for normal serialization
					.build();
			sl = SmartListBuilder.<bytewrap>create().weightLimit(weightLimit).decisionTree(d).build();
			//End-1
			*/
			//Start-2-Normal SmartList
			sl = SmartListBuilder.<bytewrap>create().weightLimit(weightLimit).build();
			//End-2
			
		}else{
			System.out.println("Wrong. Setting ArrayList as default");
			sl = new ArrayList<bytewrap>();
		}
		
		Random rnd = new Random(seed1);
		try{
			writer.append("weightLimit,filesQuantity,operationsQuantity,seed,List\n").flush();
			writer.append(weightLimit+","+liczba_plikow+","+addQ+","+seed1+",");
				if(type.equals("2")){
					writer.append("Smart");
				}else{
					writer.append("Array");
				}
				writer.append("List\n\n").flush();
			writer.append("tick,add,get,remove\n").flush();
			
				
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
				bw.b=new byte[(int)size];
				fis.read(bw.b);
				
				Stopwatch sw = Stopwatch.createStarted();
					sl.add(pos, bw);
				sw.stop();
				
				writer.append(tick+","+(sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS)+",,\n")).flush();
				tick++;
				
				timeAdd += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
				countAdd++;
				fis.close();
				
				System.out.println("list.add("+pos+", "+pth+"), list.size = "+sl.size());
			}
			System.out.println("Finished insertOnly");
			
			System.out.println("Starting getOnly");
			//co.readLine();
			for(i=0; i<igQ; i++){
				//if(i%500==0) Thread.sleep(slip);
				int pos = sl.size();
				if(pos>0)
					pos = rnd.nextInt(sl.size());
				else
					pos=0;
				System.out.print(pos+"<-");
				//random get
					@SuppressWarnings("unused")
					bytewrap bw ;
					Stopwatch sw = Stopwatch.createStarted();
					bw = sl.get(pos);
					sw.stop();
					
					writer.append(tick+",,"+(sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS)+",\n")).flush();
					tick++;
					timeGet += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
					countGet++;
					System.out.println("list.get("+pos+"), list.size = "+sl.size());
				
			}
			System.out.println("Finished getOnly");
			
			
			System.out.println("Starting insert-get");
			//co.readLine();
			for(i=0; i<igQ; i++){
				//if(i%500==0) Thread.sleep(slip);
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
					bw.b=new byte[(int)size];
					fis.read(bw.b);
					
					Stopwatch sw = Stopwatch.createStarted();
						sl.add(pos, bw);
					sw.stop();
					
					writer.append(tick+","+(sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS)+",,\n")).flush();
					tick++;
					timeAdd += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
					countAdd++;
					fis.close();
					
					System.out.println("list.add("+pos+", "+pth+"), list.size = "+sl.size());
				}else{
					//random get
					@SuppressWarnings("unused")
					bytewrap bw ;
					Stopwatch sw = Stopwatch.createStarted();
					bw = sl.get(pos);
					sw.stop();
					
					writer.append(tick+",,"+(sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS)+",\n")).flush();
					tick++;
					timeGet += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
					countGet++;
					System.out.println("list.get("+pos+"), list.size = "+sl.size());
				}
			}
			System.out.println("Finished Add-Get");
			
			System.out.println("Starting Add-Get-Remove");
			//co.readLine();
			for(i=0; i<igrQ; i++){
				//if(i%500==0) Thread.sleep(slip);
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
					bw.b=new byte[(int)size];
					fis.read(bw.b);
					
					Stopwatch sw = Stopwatch.createStarted();
						sl.add(pos, bw);
					sw.stop();
					
					writer.append(tick+","+(sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS)+",,\n")).flush();
					tick++;
					timeAdd += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
					countAdd++;
					fis.close();
					
					System.out.println("list.add("+pos+", "+pth+"), list.size = "+sl.size());
				}else if(what==1 || what==2){
					//random get
					@SuppressWarnings("unused")
					bytewrap bw ;
					Stopwatch sw = Stopwatch.createStarted();
					bw = sl.get(pos);
					sw.stop();
					
					writer.append(tick+",,"+(sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS)+",\n")).flush();
					tick++;
					timeGet += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
					countGet++;
					System.out.println("list.get("+pos+"), list.size = "+sl.size());
				}else{
					//random remove
					@SuppressWarnings("unused")
					bytewrap bw ;
					Stopwatch sw = Stopwatch.createStarted();
					bw = sl.remove(pos);
					sw.stop();
										
					writer.append(tick+",,,"+(sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS)+"\n")).flush();
					tick++;
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
		}catch(MempressException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			System.out.println("\nTotal: "+ (countAdd+countGet+countRem) + " operations.");
			System.out.println("\n"+ countAdd + " Adds with avg time = " + nanoTime(timeAdd/countAdd));
			System.out.println("\n"+ countGet + " Gets with avg time = " + nanoTime(timeGet/countGet));
			System.out.println("\n"+ countRem + " Removes with avg time = " + nanoTime(timeRem/countRem));
			System.out.println("\nDetailed measures saved in " + csv.getName()+" file");
		}
		
		try{
			writer.append("\n addsAvg,getAvg,remAvg\n"
					+ nanoTime(timeAdd/countAdd)+","+nanoTime(timeGet/countGet)+","+nanoTime(timeRem/countRem)).flush();
		}catch(Exception e){}
		
		System.out.println("Press enter for GC");
		co.readLine();
		System.gc();
		System.out.println("Press enter to delete+GC");
		co.readLine();
		sl=null;
		System.gc(); 
		System.out.println("Finished\n Press enter to exit");
		
		System.exit(1);
	}
};
