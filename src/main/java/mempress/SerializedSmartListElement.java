package mempress;

import java.io.InputStream;

/**
 * Ta klasa przechowuje zserializowany objekt. Funkcja get korzysta z klasy
 * Serializer do deserializacji objektu. Zakłada się, że do klasy jest dostarczany
 * obiekt poddany serializacji wcześniej.
 * @param <E>
 */
public class SerializedSmartListElement<E> extends SmartListElement<E>  {
    protected final InputStream serializedObject;
    protected final Class<E> objectType;

    public SerializedSmartListElement(long hash, InputStream inStream, Class<E> type) {
        super(hash);
        serializedObject = inStream;
        objectType = type;
    }

    /**
     * Korzystając ze statycznej metody des, odczytywany jest z InputStream obiekt, który
     * zostaje zdeserializowany i zwrócony jako "Object"
     * Następnie korzystając z informacji o typie jest rzutowany do typu E.
     * @return zdeserializowany objekt
     */
    @Override
    public E getObject() {
        // Poniższe poprawić tak by przyjmowało InputStream.
        Object deserialized = Serializer.des(serializedObject);
        return objectType.cast(deserialized);
    }
}
