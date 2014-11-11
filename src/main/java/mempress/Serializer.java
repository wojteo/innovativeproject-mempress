package mempress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
//import java.util.zip.CRC32;


public class Serializer<T> {
	
	public static <T>ArrayElement ser(T obj) throws IOException{
		
		Class<?> ctype = obj.getClass();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		
		oos.writeObject(obj);
		
		long hash = oos.hashCode();
		/*
		byte[] data = baos.toByteArray();
		CRC32 c32 = new CRC32();
		c32.update(data);
		hash = c32.getValue();
		*/
		
		ArrayElement ae = new ArrayElement(hash, baos, ctype);
		
		return ae;
	}
	
	public static Object des(ArrayElement ae) throws IOException, ClassNotFoundException{
		
		Object o=pipeIt(ae.getStream()).readObject();
				
		return o;
	}


	public static Object des(byte[] b) throws IOException, ClassNotFoundException{
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		ObjectInputStream ois = new ObjectInputStream(bais);
		
		Object o = ois.readObject();
		return o;
	}
	
	public static ObjectInputStream pipeIt(ByteArrayOutputStream baos) throws IOException{
		
		PipedOutputStream pos = new PipedOutputStream();
		PipedInputStream pis = new PipedInputStream();
		
		pos.connect(pis);
		baos.writeTo(pos);
		ObjectInputStream ois = new ObjectInputStream(pis);
		
		return ois;
	}
	
	
}
