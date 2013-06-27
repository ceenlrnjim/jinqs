package jinqs;

import java.util.*;

public class LazyHashJoin<TOuter, TInner, TKey, TResult> implements Iterable<TResult> {
    
    private final Iterable<TOuter> outers;
    private final Iterable<TInner> inners;
    private final Fn1<TOuter, TKey> outerKeySelector;
    private final Fn1<TInner, TKey> innerKeySelector;
    private final Fn2<TOuter, TInner, TResult> resultBuilder;

    public LazyHashJoin(Iterable<TOuter> outers, 
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

    public Iterator<TResult> iterator() {
        final HashMap<TKey,Collection<TInner>> hashedInners = new <TKey,Collection<TInner>>HashMap();
        for (TInner innerRow : inners) {
            TKey innerKey = innerKeySelector.apply(innerRow);
            Collection<TInner> rows = hashedInners.get(innerKey);
            if (rows == null) {
                rows = new LinkedList<TInner>();
                hashedInners.put(innerKey, rows);
            }
            rows.add(innerRow);
        }

        return new LazyFlatteningIterator<TOuter,TResult>(outers.iterator(), new Fn1<TOuter, Iterable<TResult>>() {
            public Iterable<TResult> apply(final TOuter outerRow) {
                TKey outerKey = outerKeySelector.apply(outerRow);
                Collection<TInner> matches = hashedInners.get(outerKey);
                if (matches == null) {
                    return Collections.emptyList();
                } else {
                    return new NaiveEnumerable().select(matches, new Fn1<TInner, TResult>() {
                        public TResult apply(TInner innerRow) {
                            return resultBuilder.apply(outerRow, innerRow);
                        }
                    });
                }
            }
        });
    }
}
