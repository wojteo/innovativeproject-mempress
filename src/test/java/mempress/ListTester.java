package mempress;

import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class ListTester {
	public static class TestResults {
		public TestResults(long e, long a, long i, long r, long p) {
			timeElapsed = e;
			elementsAdded = a;
			elementsInserted = i;
			elementsRemoved = r;
			elementsPassed = p;
		}
		
		public long timeElapsed;
		public long elementsAdded;
		public long elementsInserted;
		public long elementsRemoved;
		public long elementsPassed;
		
		@Override
		public String toString() {
			StringBuilder bd = new StringBuilder();
			bd.append("\nTime elapsed: " + timeElapsed + ", \n");
			bd.append("Elements added: " + elementsAdded + ", \n");
			bd.append("Elements inserted: " + elementsInserted + ", \n");
			bd.append("Elements removed: " + elementsRemoved + ", \n");
			bd.append("Elements passed: " + elementsPassed + ", \n");
			return bd.toString();
		}
	}
	
	
	public static <T> TestResults testCollection(List<T> list, Provider<T> provider, String testString) {
		int i1, l = testString.length();
		long a, i, r, p, counter;
		a = i = r = p = 0;
		char[] commands = testString.toCharArray();
		Random ra = new Random(RANDOM_FACTOR);
		Stopwatch stopwatch = Stopwatch.createStarted();
		for(char command : commands) {
			switch(command) {
			case 'a':
				list.add(provider.getNext(null));
				++a;
				break;
			case 'i':
				list.add(ra.nextInt(list.size()), provider.getNext(null));
				++i;
				break;
			case 'r':
				int s = list.size();
				if(s > 0) {
					list.remove(ra.nextInt(s));
					++r;
				} else
					++p;
				break;
			case 'p': // nic nie rób
				++p;
				break;
			}
		}
		stopwatch.stop();
		
		long elapsed = stopwatch.elapsed(java.util.concurrent.TimeUnit.MILLISECONDS); 
		
		//highestNumber = (int)(counter - 1);
		
		return new TestResults(elapsed, a, i, r, p);
	}
	
	
	//private static int highestNumber = 1;
	public static final int RANDOM_FACTOR = 2971; //1029983
	public static final List<Integer> PRIMES = ImmutableList.<Integer>builder()
			.add(619, 1019, 1193, 1471, 1907, 2143, 2633, 3137, 4967, 4999).build();
}
