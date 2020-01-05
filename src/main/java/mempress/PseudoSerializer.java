package mempress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class PseudoSerializer implements Serializer {

    @Override
    public ClassData ser(Object obj) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] b = baos.toByteArray();

            return new ClassData(SerializerType.NoSerialized, obj, b.length);

        } catch (IOException e) {
            throw new MempressException("Couldn't read object properly");
        }
    }

    @Override
    public Object des(ClassData cd) {
        return cd.getData();
    }

}
