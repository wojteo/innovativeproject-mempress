package mempress;

import java.io.*;

public class FileSerializer implements Serializer {

	@Override
	public ClassData ser(Object obj) {

		try {
			String tDir = System.getProperty("user.dir");// java.io.tmpdir");
			File tFile = File.createTempFile("tmpfile", ".mempress", new File(
					tDir));

			FileOutputStream fos = new FileOutputStream(tFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(obj);
			fos.flush();
			fos.close();
			oos.close();

			tFile.deleteOnExit();

			ClassData cd = new ClassData(SerializerType.FileSerializer, tFile,0){
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

		FileInputStream bais;
		Object o = null;
		try {
			bais = new FileInputStream((File) cd.getData());
			ObjectInputStream ois = new ObjectInputStream(bais);

			o = ois.readObject();
			ois.close();
		} catch (ClassNotFoundException | IOException e) {
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
			tFile.deleteOnExit();

			fos = new FileOutputStream(tFile);
			fos.write(data);

			return new ClassData(SerializerType.FileSerializer, tFile,0){
				@Override
				protected void finalize() {
					try{
						((File)getData()).delete();
					}catch(Exception e){}
				}
			};
		} catch (Exception e) {
			throw new MempressException("Couldn't serialize file");
		} finally {
			if(fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (Exception e) {}
			}
		}
	}
}
