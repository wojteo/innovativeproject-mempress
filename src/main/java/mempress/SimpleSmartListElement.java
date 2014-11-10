package mempress;

/**
 * Ta klasa przechowuje zwykły niezmodyfikowany element
 * @param <E>
 */
public class SimpleSmartListElement<E> extends SmartListElement<E> {
    private final E element;

    public SimpleSmartListElement(E holdElement) {
        super(holdElement.hashCode());

        element = holdElement;
    }

    @Override
    public E getObject() {
        return element;
    }
}
