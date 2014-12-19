package effitest;
import java.io.*;
import java.util.Random;

public class sampleTextFileGenerator{

	public static void main(String[] args){
		Random rnd = new Random(123);

		for(int i=10; i<10000; i++){
			
			PrintWriter out;
			try {
				out = new PrintWriter("sample"+i+".txt");
				int bound = rnd.nextInt(1000000)+1000000;
				
				for(int j=0; j<bound; j++){
					char c = (char) (rnd.nextInt(91)+32);
					out.write(c);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}
}
