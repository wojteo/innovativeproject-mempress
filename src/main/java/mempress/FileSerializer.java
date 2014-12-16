package mempress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

			ClassData cd = new ClassData(SerializerType.FileSerializer, tFile,
					0);

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
			((File)cd.getData()).delete();
		} catch (ClassNotFoundException | IOException e) {
			throw new MempressException("Couldn't deserialize file");
		}

		return o;
	}

}
