package mempress;

import java.io.ByteArrayOutputStream;
public class ArrayElement{

	private final long hash;
	private final ByteArrayOutputStream stream;
	private final Class<?> ctype;
	
	ArrayElement(long h, ByteArrayOutputStream b, Class<?> c){
		this.hash=h;
		this.stream=b;
		this.ctype=c;
	}
	
	public long getHash() {
		return hash;
	}

	public ByteArrayOutputStream getStream() {
		return stream;
	}

	public Class<?> getCtype() {
		return ctype;
	}
	
	
}

