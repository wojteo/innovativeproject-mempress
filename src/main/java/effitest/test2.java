package effitest;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.google.common.base.Stopwatch;

public class test2 {

	public static void main(String[] args){
		System.out.println("Test wymaga pliku sample1.txt");
		ArrayList<byte[]> sl = new ArrayList<byte[]>();
		int i=0;
		Console co = System.console();
		co.readLine();
		Stopwatch sw;
		long timeAdd=0;
		int countAdd=0;
		
		try{
			for(i=0; i<30; i++){
				File f = new File("sample1.txt");
				InputStream fis = new FileInputStream(f);
				byte[] b = com.google.common.io.ByteStreams.toByteArray(fis);
				
				sw = Stopwatch.createStarted();
				sl.add(b);
				sw.stop();
				timeAdd += sw.elapsed(java.util.concurrent.TimeUnit.NANOSECONDS);
				countAdd++;
				System.out.println("ArrayList.size() = "+sl.size());
			}
		}catch(OutOfMemoryError | Exception e){
			e.printStackTrace();
			sl.clear();
		}finally{
			System.out.println("Final is " + i + " additions, with ArrayList size = " + sl.size());
			System.out.println("\n"+ countAdd + " Add's with avg time = " + test.nanoTime(timeAdd/countAdd));
			
		}
			
	}
		
}

