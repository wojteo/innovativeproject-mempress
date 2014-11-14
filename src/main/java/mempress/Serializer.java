package mempress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class Serializer {
	
	
	//Serializacja
	//do tablicy
	public static <T>byte[] ser(T obj){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			
			oos.writeObject(obj);
		}catch(IOException e){
			//ignore
		}
		byte[] b = baos.toByteArray();
		
		return b;
	}
	
	//do pliku
	public static <T>File serf(T obj) throws IOException{
		
		String tDir = System.getProperty("user.dir");//java.io.tmpdir");
		File tFile = File.createTempFile("tmpfile",".mempress", new File(tDir));
		
		FileOutputStream fos = new FileOutputStream(tFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		oos.writeObject(obj);
		fos.flush();
		fos.close();
		oos.close();
		
		tFile.deleteOnExit();
		return tFile;
	}
	
	//do zip-tablicy
	public static <T>byte[] serz(T obj) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gout = new GZIPOutputStream(baos);
		ObjectOutputStream oos = new ObjectOutputStream(gout);
		oos.writeObject(obj);
		oos.flush();
		gout.finish();
		return baos.toByteArray();
			 
	}
		
	//do zip-pliku
	public static <T>File serfz(T obj) throws IOException{
			
		String tDir = System.getProperty("user.dir");//java.io.tmpdir");
		File tFile = File.createTempFile("tmpfile",".mempress", new File(tDir));
		
		FileOutputStream fos = new FileOutputStream(tFile);
	
	    GZIPOutputStream gout = new GZIPOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(gout);
		
		oos.writeObject(obj);
		fos.flush();
		fos.close();
		
		tFile.deleteOnExit();
		return tFile;
	}
	
	
	// Deserializacja
	//z tablicy
	public static Object des(byte[] b){
		
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		Object o=null;
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			
			o = ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			//ignore
		}
		
		return o;
	}
	
	//z pliku
	public static Object desf(File file) {
		FileInputStream bais;
		Object o=null;
		try {
			bais = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(bais);
			
			o = ois.readObject();
			ois.close();
		} catch (ClassNotFoundException | IOException e) {
			//ignore
		}
		
		return o;
	}
			
	//z zip-tablice
	public static Object desz(byte[] b) throws IOException, ClassNotFoundException{
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		GZIPInputStream gin = new GZIPInputStream(bais);
		ObjectInputStream ois = new ObjectInputStream(gin);
		
		Object o = ois.readObject();
		return o;
	}
	
	//z zip-pliku
	public static Object desfz(File file) throws IOException, ClassNotFoundException{
		FileInputStream fis = new FileInputStream(file);
		GZIPInputStream gin = new GZIPInputStream(fis);
		ObjectInputStream ois = new ObjectInputStream(gin);
		
		Object o = ois.readObject();
		ois.close();
		return o;
	}
	
}
