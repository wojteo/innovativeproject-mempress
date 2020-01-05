package mempress.list;

/**
 * Created by Bartek on 2014-11-26.
 */
public interface SmartListDecorator<E> {
    SmartList<E> decorate(SmartList<E> list);
}
