package mempress;

public class LongProvider implements Provider<Long> {
	private long counter = 1;

	@Override
	public Long getNext(Object arg) {
		// TODO Auto-generated method stub
		return new Long(counter++);
	}

}
