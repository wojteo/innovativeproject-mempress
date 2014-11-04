import java.io.Serializable;
import java.util.Random;

/**
 * DataProvider is a class which generates Serializable object filled with random data (based on seed)
 *
 */
public class DataProvider implements Serializable{
	
	private static final long serialVersionUID = 2240731182143546585L;
	private int[] intArray;
	private float[] floArray;
	private String[] stringArray;
	private DataProvider child;
	private Random rr = new Random((long)1234);
	
	public enum ObjectSize {
		SMALL(1000), MEDIUM(10000), BIG(100000), HUGE(10000000);
		
		private int size;

		ObjectSize(int size) {
			this.size = size;
		}
		public int getSize() {
			return size;
		}
	}
	
	public DataProvider(ObjectSize s, int i) {
		fillData(s.getSize());
	}
	
	public DataProvider(ObjectSize s) {
		fillData(s.getSize());
	}
	
	public DataProvider(int i){
		fillData(ObjectSize.BIG.getSize());
		child = new DataProvider(i-1);
	}
	
	public DataProvider(int size, int i){
		fillData(size);
		child = new DataProvider(size, i-1);
	}

	
	private void fillData(int size){
		intArray = new int[size];
		floArray = new float[size];
		stringArray = new String[size];
		
		
		for(int i=0, j; i<size; i++){
			intArray[i]=rr.nextInt();
			floArray[i]=rr.nextFloat();
			for(j=0;j<10;j++)
				stringArray[i]=""+(rr.nextInt(93)+32);
		}
		
	}
	
	public int[] getIntArray() {
		return intArray;
	}

	public float[] getFloArray() {
		return floArray;
	}

	public String[] getStringArray() {
		return stringArray;
	}

	public DataProvider getChild() {
		return child;
	}


}
