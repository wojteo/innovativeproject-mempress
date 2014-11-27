package mempress;

/**
 * Created by Bartek on 2014-11-26.
 */
public interface SmartListDecorator<E> {
    public SmartList<E> decorate(SmartList<E> list);
}
