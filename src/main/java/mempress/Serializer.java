package mempress;

public interface Serializer {
	
	public ClassData ser(Object obj);
	
	public Object des(ClassData cd);
	
}
