package mempress;

public interface Serializer {
	
	public ClassData ser(Object obj);
	
	public Object des(ClassData cd);

	default public ClassData fastForward(ClassData o) { throw new UnsupportedOperationException(); }
}
