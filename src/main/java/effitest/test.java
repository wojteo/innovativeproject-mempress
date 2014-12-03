package effitest;
import mempress.*;

import java.io.Serializable;
import java.util.Arrays;

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

public class test {

	public static void main(String[] args){
		SmartList<wrap> sl = new SmartList<wrap>(33435);
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
		}catch(Exception e){
			System.out.println("Final is " + i + " iterations, with SmartList size = " + sl.size());
			e.printStackTrace();
		}
		
		
		
	}
}
