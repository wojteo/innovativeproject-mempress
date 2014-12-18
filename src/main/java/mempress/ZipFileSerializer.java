package mempress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.google.common.io.ByteStreams;

public class ZipFileSerializer implements Serializer {

	@Override
	public ClassData ser(Object obj) {
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			byte[] b = baos.toByteArray();
			baos.close();
			oos.close();
			
			byte[] b_compressed = QuickLZ.compress(b, 1);
			b=null;
			
			
			String tDir = System.getProperty("user.dir");// java.io.tmpdir");
			File tFile = File.createTempFile("tmpfile", ".mempress", new File(
					tDir));
			FileOutputStream fos = new FileOutputStream(tFile);
			fos.write(b_compressed);
			fos.close();

			tFile.deleteOnExit();

			ClassData cd = new ClassData(SerializerType.ZipFileSerializer, tFile,0){
				@Override
				protected void finalize() {
					try{
						((File)getData()).delete();
					}catch(Exception e){}
				}
			}; 

			return cd;
		} catch (IOException e) {
			throw new MempressException("Couldn't serialize file");
		}
	}

	@Override
	public Object des(ClassData cd) {

		FileInputStream fis;
		Object o = null;
		try {
			fis = new FileInputStream((File) cd.getData());
			byte[] b =  QuickLZ.decompress(ByteStreams.toByteArray(fis));
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			ObjectInputStream oos = new ObjectInputStream(bais);
			o = oos.readObject();
			
		} catch (IOException | ClassNotFoundException e) {
			throw new MempressException("Couldn't deserialize file");
		}

		return o;
	}

	@Override
	public ClassData fastForward(ClassData o) {
		byte[] data = (byte[])o.getData();
		FileOutputStream fos = null;
		try {
			String tDir = System.getProperty("user.dir");// java.io.tmpdir");
			File tFile = File.createTempFile("tmpfile", ".mempress", new File(
					tDir));
			fos = new FileOutputStream(tFile);
			fos.write(data);

			tFile.deleteOnExit();

			return new ClassData(SerializerType.ZipFileSerializer, tFile,0){
				@Override
				protected void finalize() {
					try{
						((File)getData()).delete();
					}catch(Exception e){}
				}
			};
		} catch (IOException e) {
			throw new MempressException("Couldn't deserialize file");
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (Exception e) {}
			}
		}
	}
}
