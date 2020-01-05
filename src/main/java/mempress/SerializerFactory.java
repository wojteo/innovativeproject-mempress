package mempress;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SerializerFactory {

    private interface SerializerFac {
        Serializer create();
    }

    private static final Map<SerializerType, SerializerFac> factoryMap = Collections
            .unmodifiableMap(new HashMap<SerializerType, SerializerFac>() {

                private static final long serialVersionUID = 2221579104683932370L;

                {

                    put(SerializerType.NoSerialized,
                            new SerializerFac() {
                                public Serializer create() {
                                    return new PseudoSerializer();
                                }
                            });
                    put(SerializerType.ByteArraySerializer,
                            new SerializerFac() {
                                public Serializer create() {
                                    return new ByteArraySerializer();
                                }
                            });
                    put(SerializerType.ZipByteArraySerializer,
                            new SerializerFac() {
                                public Serializer create() {
                                    return new ZipByteArraySerializer();
                                }
                            });
                    put(SerializerType.FileSerializer,
                            new SerializerFac() {
                                public Serializer create() {
                                    return new FileSerializer();
                                }
                            });
                    put(SerializerType.ZipFileSerializer,
                            new SerializerFac() {
                                public Serializer create() {
                                    return new ZipFileSerializer();
                                }
                            });
                }
            });

    public static Serializer createSerializer(SerializerType st) {
        SerializerFac factory = factoryMap.get(st);
        if (factory == null) {
            throw new MempressException(
                    "Couldn't provide Serializer from Factory");
        }
        return factory.create();
    }
}
