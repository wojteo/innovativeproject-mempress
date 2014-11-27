package mempress;

import com.google.common.base.Preconditions;

/**
 * Created by Bartek on 2014-11-25.
 */
public class SmartListBuilder<E> {
    private boolean _elementsProvideHashCode = false;
    private DecisionTree<E> _decisionTree;
    private long _weightLimit = -1;
    private long _stateTimeLimit = -1;
    private boolean _allowNonImmutableObjects = true;

    public static <E> SmartListBuilder<E> create() {
        return new SmartListBuilder<E>();
    }

    private SmartListBuilder() {

    }

    public SmartListBuilder<E> elementsProvideHashCode(boolean doesProvide) {
        _elementsProvideHashCode = doesProvide;
        return this;
    }

    public SmartListBuilder<E> decisionTree(DecisionTree<E> decTree) {
        Preconditions.checkNotNull(decTree);
        _decisionTree = decTree;
        return this;
    }

    public SmartListBuilder<E> weightLimit(long wl) {
        Preconditions.checkArgument(wl > 0, "Weight limit must be positive");
        _weightLimit = wl;
        return this;
    }

    public SmartListBuilder<E> statesTimeLimit(long tl) {
        Preconditions.checkArgument(tl > 0, "Time limit must be greater than zero");
        _stateTimeLimit = tl;
        return this;
    }

    public SmartListBuilder<E> allowNonImmutableObjects(boolean allow) {
        _allowNonImmutableObjects = allow;
        return this;
    }

    public SmartList<E> build() {
        SmartList<E> tmp = null;
        if(_decisionTree == null)
            _decisionTree = DecisionTreeBuilder.buildDefaultTree();
        if(_elementsProvideHashCode) {
            tmp = new HashCodeSmartList<E>(_decisionTree, _weightLimit, _stateTimeLimit);
        } else {
            tmp = new SmartList<E>(_decisionTree, _weightLimit, _stateTimeLimit);
        }

        if(!_allowNonImmutableObjects)
            tmp = new ImmutableDecorator<E>().decorate(tmp);

        return tmp;
    }

}
