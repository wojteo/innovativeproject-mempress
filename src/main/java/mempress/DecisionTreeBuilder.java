package mempress;

/**
 * Created by Bartek on 2014-11-19.
 */
public class DecisionTreeBuilder<E> {
    public static <E> DecisionTreeBuilder<E> create() {
        return new DecisionTreeBuilder<E>();
    }

    public DecisionTree<E> build() {
        return buildObject;
    }

    private DecisionTree<E> buildObject;

    private DecisionTreeBuilder() {
        buildObject = new DecisionTree<>();
    }


}
