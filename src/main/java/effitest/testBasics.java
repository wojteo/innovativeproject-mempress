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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import mempress.DecisionSerializeByteArray;
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
	
	@Override
	public boolean equals(Object obj){
		final bytewrap other = (bytewrap) obj;
		return Arrays.equals(this.b, other.b);
	}
	
	@Override
	public int hashCode(){
		return Arrays.hashCode(this.b);
	}
	
}

public class testBasics{
	
	public static int parametersQ;
	
	///Method converting nanoseconds to seconds
	public static String nanoTime(long time){
		String res = new StringBuilder(time +"").reverse().toString();
		while(res.length()<=9){
			res+="0";
		}
		res = res.substring(0, 9) + "." + res.substring(9, res.length());
		res = new StringBuilder(res).reverse().toString();
		return res;
	}
	
	///Method reading configuration from file
	public static String[] getConfFile(String path) throws IOException{
		
		byte[] confFileContent = Files.readAllBytes(Paths.get(path));
		String[] confData = new String(confFileContent).split("\n");
		
		return confData;
	}
	
	///Method reading configuration from console
	public static String[] getConfConsole(){
		String[] confData = new String[parametersQ];
		
		Console co2 = System.console();
		System.out.println("What size of weightLimit?");
			confData[0] = co2.readLine();
		System.out.println("How many sample files?");
			confData[1] = co2.readLine();
		
		System.out.println("How many adds, inserts, add/inserts, add/insert/removes (one number for all) ?");
			confData[2] = co2.readLine();
		
		System.out.println("What seed for RNG?");
			confData[3] = co2.readLine();
				
		System.out.println("Write 0 for ArrayList, 1 or 2 for SmartList, 3 or 4 for HashCodeSmartList ");
			confData[4] = co2.readLine();

		System.out.println("Give path to sample files");
			confData[5] = co2.readLine();
			
		return confData;
	}
	
	
	public static void main(String[] args){
		//int slip=3600;
		int i;
		
		Stopwatch swMain = Stopwatch.createStarted();
		
		
		//files in format sampleX.txt where X is in[1, liczba_plikow]
		System.out.println("Need 'sampleX.txt' files");
		
//configuration variable declaration
		int addQ=1180, igQ=200, igrQ=200;
		long seed1=666;
		long weightLimit = 1000;
		int liczba_plikow = 800;
		parametersQ = 6; //How many parameters for configuration
		boolean tryConsole=false;
		String[] confData=null;
		String whichList;
		String filesDir="";
		
//stats
		int countAdd=0, countRem=0, countGet=0;
		long timeAdd=0, timeRem=0, timeGet=0;
		String type = "";
		
//START reading configuration
		Console co = System.console();
		System.out.println("Do you want to use configuration file?\n"
				+ "'n' for no, <filename> for yes");
		type=co.readLine();
		
		if(!type.equals("n")){
			try{
				confData = getConfFile(type);
				if(confData.length!=parametersQ){
					throw new Exception("Wrong ammount of parameters");
				}
			}catch(Exception e){
				System.out.println("Failed to load configuration file properly");
				tryConsole = true;
				
			}
		}else{
			tryConsole = true;
		}
		
		if(tryConsole){
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
		//START writing configuration to CSV file
				try{
					writer.append("weightLimit,filesQuantity,operationsQuantity,seed,filesDir,,,addsAvg,getAvg,remAvg,runtime,List\n").flush();
					writer.append(weightLimit+","+liczba_plikow+","+addQ+","+seed1+","+filesDir+",,,=A"+(4*addQ+7)+",=B"+(4*addQ+7)+",=C"+(4*addQ+7)+",=D"+(4*addQ+7)+",");
						if(whichList.equals("0")){
							writer.append("Array");
						}else if(whichList.equals("1")){
							writer.append("Smart");
						}else if(whichList.equals("2")){
							writer.append("ZipSmart");
						}else if(whichList.equals("3")){
							writer.append("HashCodeSmart");
						}else if(whichList.equals("4")){
							writer.append("ZipHashCodeSmart");
						}
						writer.append("List\n\n").flush();
					writer.append("tick,add,get,remove\n").flush();
				}catch(IOException e){
					e.printStackTrace();
				}
		//FINISH writing configuration to CSV file
		
//FINISH creating CSV file
		
//START Create list
		List<bytewrap> sl = null;
		if(whichList.equals("0")){
			
			System.out.println("ArrayListSelected");
			sl = new ArrayList<bytewrap>();
			
		}else if(whichList.equals("1")){
			
			//Start-1-Normal SmartList
			sl = SmartListBuilder.<bytewrap>create()
					.weightLimit(weightLimit)
					.build();
			//End-1
			
		}else if(whichList.equals("2")){
			
			//Start-2-OnlyZipByteArray/File SmartList
			System.out.println("SmartListSelected");
			DecisionTree< bytewrap> d = DecisionTreeBuilder
					.<bytewrap>create()
					.addTreeElement(new DecisionSerializeByteArray())
					.addTreeElement(new DecisionSerializeFile<bytewrap>())
					.build();
					
			sl = SmartListBuilder.<bytewrap>create().
					weightLimit(weightLimit)
					.decisionTree(d)
					.build();
			//End-2
			
		}else if(whichList.equals("3")){
			
			//Start-3-Normal HashCodeSmartList
			sl = SmartListBuilder.<bytewrap>create()
					.weightLimit(weightLimit)
					.elementsProvideHashCode(true)
					.build();
			//End-3
			
		}else if(whichList.equals("4")){
			
			//Start-4-OnlyZipByteArray/File HashCodeSmartList
			System.out.println("SmartListSelected");
			DecisionTree< bytewrap> d = DecisionTreeBuilder
					.<bytewrap>create()
					.addTreeElement(new DecisionSerializeByteArray())
					.addTreeElement(new DecisionSerializeFile<bytewrap>())
					.build();
					
			sl = SmartListBuilder.<bytewrap>create()
					.elementsProvideHashCode(true)
					.weightLimit(weightLimit)
					.decisionTree(d)
					.build();
			//End-4
			
		}else{
			System.out.println("Didn't select List properly -> closing program");
			System.exit(0);
		}
//FINISH create list
		
//START TESTS
		try{
			Random rnd = new Random(seed1);
			System.out.println("Starting insertOnly");
			for(i=0; i<addQ; i++){
				int x = rnd.nextInt(liczba_plikow)+1;
				int pos = sl.size();
				if(pos>0)
					pos = rnd.nextInt( sl.size());
				else
					pos=0;
				
				System.out.print(x+"-> ");
				String pth = filesDir+"sample"+x+".txt";
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
					String pth = filesDir+"sample"+x+".txt";
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
					String pth = filesDir+"sample"+x+".txt";
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
//FINISH tests		

		swMain.stop();
		
		try{
			writer.append("\n addsAvg,getAvg,remAvg,runtime\n"
					+ nanoTime(timeAdd/countAdd)+","+nanoTime(timeGet/countGet)+","
					+nanoTime(timeRem/countRem)+","+nanoTime(swMain.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS)))
					.flush();
		}catch(IOException e){}
		
		
		System.out.println("Program was running for (seconds): " + nanoTime(swMain.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS)));
		System.out.println("Press enter for GC");
		co.readLine();
		System.gc();
		System.out.println("Press enter to delete+GC");
		co.readLine();
		sl=null;
		System.gc(); 
		System.out.println("Finished");
		
		System.exit(1);
	}
};
