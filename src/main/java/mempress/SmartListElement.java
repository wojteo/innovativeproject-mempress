package mempress;

import java.io.InputStream;

/**
 * Klasa bazowa do przechowywania elementów w liście
 * PO CO? Elementy w liście mogą być zarówno zserializowane
 * jak i nie zserializowane, musi więc być jakaś klasa bazowa
 * dla obu możliwości, która będzie po prostu zwracać obiekt
 * bez zagłębiania się w szczegóły.
 * @param <E>
 */
public abstract class SmartListElement<E> {
    protected final long checksum;

    protected SmartListElement(long checksum) {
        this.checksum = checksum;
    }

    public abstract E getObject();

    public long getChecksum() {
        return checksum;
    }
}
