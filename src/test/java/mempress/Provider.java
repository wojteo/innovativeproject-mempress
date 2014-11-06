package mempress;

public interface Provider<T> {
	public T getNext(Object arg);
}
