package mempress;

/**
 * Created by Bartek on 2014-11-20.
 */
@Deprecated
public class WrappedListElement<E> extends ListElement<E> {
    public WrappedListElement(ClassData data, long hashcode, Class<E> objectType) {
        super(data, objectType);
    }

    @Override
    protected E getObject() {
        return objectType.cast(SerializerFactory
                .createSerializer(getData().getSerializerType())
                .des(data));
    }
}
