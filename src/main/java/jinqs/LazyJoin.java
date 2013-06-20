package jinqs;

import java.util.*;

public class LazyJoin<TOuter, TInner, TKey, TResult> implements Iterable<TResult> {
    private Iterable<TOuter> outers;
    private Iterable<TInner> inners;
    private Fn1<TOuter, TKey> outerKeySelector;
    private Fn1<TInner, TKey> innerKeySelector;
    private Fn2<TOuter, TInner, TResult> resultBuilder;

    public LazyJoin(Iterable<TOuter> outers, 
                    Iterable<TInner> inners, 
                    Fn1<TOuter, TKey> outerKeySelector,
                    Fn1<TInner, TKey> innerKeySelector,
                    Fn2<TOuter, TInner, TResult> resultBuilder) {
        this.outers = outers;
        this.inners = inners;
        this.outerKeySelector = outerKeySelector;
        this.innerKeySelector = innerKeySelector;
        this.resultBuilder = resultBuilder;
    }

    // starting with the nested loop join for simplicity - need to add other join algorithms to optimize this
    @Override
    public Iterator<TResult> iterator() {
        // TODO: optimize this to make it more lazy - just perform the join to the point where I find my next match
        // TODO: optimize to support other join agorithms (at least hash join)
        LinkedList<TResult> result = new LinkedList<TResult>();
        for (TOuter outer : outers) {
            for (TInner inner : inners) {
                TKey outerKey = outerKeySelector.apply(outer);
                TKey innerKey = innerKeySelector.apply(inner);

                if (outerKey.equals(innerKey)) {
                    result.add(resultBuilder.apply(outer, inner));
                }
            }
        }

        return result.iterator();
        /*
        return new Iterator<TResult>() {
            public TResult next() {
                throw new NoSuchElementException("no more elements");
            }

            public boolean hasNext() {
                return outers.hasNext() && inners.hasNext(); // "inner" style join
            }

            public void remove() {
                throw new UnsupportedOperationException("Cannot remove items from the join result");
            }
        };
        */
    };
}

