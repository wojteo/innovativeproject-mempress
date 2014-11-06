package mempress;

import java.util.Random;

public class DataProviderProvider implements Provider<DataProvider> {
	private DataProvider.ObjectSize[] sizeList = 
			DataProvider.ObjectSize.values();
	private Random random;
	
	public DataProviderProvider(int seed) {
		random = new Random(seed);
	}
	
	@Override
	public DataProvider getNext(Object arg) {
		return new DataProvider(
				sizeList[random.nextInt(sizeList.length)]
		);
	}

}
