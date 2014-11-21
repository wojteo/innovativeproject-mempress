package mempress;

import com.google.common.base.Preconditions;

import java.util.Collection;

/**
 * Created by Bartek on 2014-11-19.
 */
public class DecisionTreeBuilder<E> {
    public static <E> DecisionTreeBuilder<E> create() {
        return new DecisionTreeBuilder<E>();
    }

    // TODO: dodać klasy w podanej kolejności: zwykły wrapper, serializer do pamięci, serializer do pliku
    public static <E> DecisionTree<E> buildDefaultTree() {
        return DecisionTreeBuilder.<E>create()
                .addTreeElement(new DecisionStoreIt<E>())
                .addTreeElement(new DecisionSerializeByteArray<E>())
                .addTreeElement(new DecisionSerializeFile<E>())
                .build();
    }

    private DecisionTree<E> buildObject;

    private DecisionTreeBuilder() {
        buildObject = new DecisionTree<>();
    }

    public DecisionTree<E> build() {
        return buildObject;
    }

    public DecisionTreeBuilder<E> addTreeElement(DecisionTree.DecisionTreeElement<E> element) {
        Preconditions.checkNotNull(element);
        buildObject.processors.add(element);

        return this;
    }

    public DecisionTreeBuilder<E> addTreeElements(Collection<? extends DecisionTree.DecisionTreeElement<E>> elements) {
        Preconditions.checkNotNull(elements);
        Preconditions.checkArgument(elements.size() > 0, "Collection has to have at least one element");

        buildObject.processors.addAll(elements);

        return this;
    }


}
