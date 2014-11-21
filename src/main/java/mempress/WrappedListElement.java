package mempress;

/**
 * Created by Bartek on 2014-11-20.
 */
public class WrappedListElement<E> extends ListElement<E> {
    public WrappedListElement(ClassData data, long hashcode, Class<E> objectType) {
        super(data, hashcode, objectType);
    }

    @Override
    protected E getObject() {
        return objectType.cast(SerializerFactory
                .createSerializer(getData().getSerializerType())
                .des(data));
    }
}
