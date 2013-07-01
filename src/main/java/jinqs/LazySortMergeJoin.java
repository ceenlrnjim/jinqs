package jinqs;

import java.util.*;

public class LazySortMergeJoin<TOuter, TInner, TKey extends Comparable, TResult> implements Iterable<TResult> {
    private final Iterable<TOuter> outers;
    private final Iterable<TInner> inners;
    private final Fn1<TOuter, TKey> outerKeySelector;
    private final Fn1<TInner, TKey> innerKeySelector;
    private final Fn2<TOuter, TInner, TResult> resultBuilder;

    public LazySortMergeJoin(final Iterable<TOuter> outers, 
                             final Iterable<TInner> inners, 
                             final Fn1<TOuter, TKey> outerKeySelector,
                             final Fn1<TInner, TKey> innerKeySelector,
                             final Fn2<TOuter, TInner, TResult> resultBuilder) {
        this.outers = outers;
        this.inners = inners;
        this.outerKeySelector = outerKeySelector;
        this.innerKeySelector = innerKeySelector;
        this.resultBuilder = resultBuilder;
    }

    public Iterator<TResult> iterator() {
            List<TResult> results = new LinkedList<TResult>();
            List<TOuter> sortedOuters = new LinkedList<TOuter>();
            List<TInner> sortedInners = new LinkedList<TInner>();

            for (TOuter row : outers) sortedOuters.add(row);
            for (TInner row : inners) sortedInners.add(row);

            if (sortedOuters.size() == 0 || sortedInners.size() == 0) {
                return Collections.<TResult>emptyList().iterator();
            }

            Collections.sort(sortedOuters, new Comparator<TOuter>() {
                public int compare(TOuter a, TOuter b) {
                    return outerKeySelector.apply(a).compareTo(outerKeySelector.apply(b));
                }
            });

            Collections.sort(sortedInners, new Comparator<TInner>() {
                public int compare(TInner a, TInner b) {
                    return innerKeySelector.apply(a).compareTo(innerKeySelector.apply(b));
                }
            });

        return new LazyMergeJoin(sortedOuters, sortedInners, outerKeySelector, innerKeySelector, resultBuilder).iterator();
    }
}

