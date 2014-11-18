package mempress;

import java.io.IOException;
import java.io.InputStream;

/**
 * Klasa bazowa do przechowywania elementów w liście
 * PO CO? Elementy w liście mogą być zarówno zserializowane
 * jak i nie zserializowane, musi więc być jakaś klasa bazowa
 * dla obu możliwości, która będzie po prostu zwracać obiekt
 * bez zagłębiania się w szczegóły.
 * @param <E>
 */
public abstract class SmartListElement<E> implements Comparable<SmartListElement<E>> {
    protected final long hashcode;
    protected long objectSize;
    private int useCount;

    protected SmartListElement(long hashcode) {
        this.hashcode = hashcode;
    }

    public E get()
    {
        ++useCount;
        return getObject();
    }

    protected abstract E getObject();

    public long getHashCode() {
        return hashcode;
    }

    public void release() throws IOException {}

    public long getObjectSize() { return objectSize; }

    public int getUseCount() {
        return useCount;
    }

    @Override
    public int compareTo(SmartListElement<E> o) {
        int order;
        order = ((Long)hashcode).compareTo(o.hashcode);

        if(order == 0) {
            order = ((Long)objectSize).compareTo(o.objectSize);
        }

        return -order;
    }
}
