package effitest;
import java.util.ArrayList;

public class test2 {

	public static void main(String[] args){
		ArrayList<wrap> sl = new ArrayList<wrap>();
		int i=0;
		try{
			for(i=0; i<1000000; i++){
				sl.add(new wrap(43));
				sl.add(new wrap(64));
				sl.add(new wrap(24));
				sl.add(new wrap(54));
				sl.add(new wrap(23));
				System.out.println(sl.size());
			}
		}catch(OutOfMemoryError | Exception e){
			System.out.println("Final is " + i + " iterations, with SmartList size = " + sl.size());
			e.printStackTrace();
		}
		
		
		
	}
}
