package mempress;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;



public class Serializer {
	
	public static void ser(Object o, String src) throws IOException{
		
	     FileOutputStream fileOut = new FileOutputStream(src);
	     ObjectOutputStream out = new ObjectOutputStream(fileOut);
	     out.writeObject(o);
	     out.close();
	     fileOut.close();
	}
	
	public static Object des(String src) throws IOException, ClassNotFoundException{
		FileInputStream fileIn = new FileInputStream(src);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		Object o = in.readObject();
		in.close();
		fileIn.close();
		
		return o;
	}
	
	
	
}
