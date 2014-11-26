package mempress;

/**
 * Created by Bartek on 2014-11-20.
 */
@Deprecated
public class SerializedListElement<E> extends ListElement<E> {
    public SerializedListElement(ClassData data, long hashcode, Class<E> objectType) {
        super(data, objectType);
    }

    @Override
    protected E getObject() {
        Object object = SerializerFactory
                .createSerializer(data.getSerializerType())
                .des(data);
        return objectType.cast(object);
    }
}
