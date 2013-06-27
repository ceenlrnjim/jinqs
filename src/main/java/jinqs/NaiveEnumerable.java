package jinqs;

import java.util.*;

public class NaiveEnumerable implements Enumerable {

    // Projection
    public <S,T> Iterable<T> select(Iterable<S> source, Fn1<S,T> selector) {
        return new LazySelect(source, selector);
    }

    // Cross-Apply
    public <S,T> Iterable<T> selectMany(Iterable<S> source, Fn1<S, Iterable<T>> selector) {
        return new LazySelectMany(source, selector);
    }

    // selection
    public <T> Iterable<T> where(Iterable<T> source, Fn1<T,Boolean> predicate) {
        return new LazyWhere(source,predicate);
    }


    public <TOuter, TInner, TKey, TResult> Iterable<TResult> join(Iterable<TOuter> outers, 
                                                                  Iterable<TInner> inners, 
                                                                  Fn1<TOuter, TKey> outerKeySelector,
                                                                  Fn1<TInner, TKey> innerKeySelector,
                                                                  Fn2<TOuter, TInner, TResult> resultBuilder) {
        // TODO: refactor join to use selectMany for the nested loop join - should be more lazy that way
        // TODO: add explicit merge sort join for times when the iterables are already sorted in the same order
        // TODO: add hash-join implementation
        //return new LazyJoin<TOuter, TInner, TKey, TResult>(outers,inners,outerKeySelector, innerKeySelector, resultBuilder);
        return nestedLoopJoin(outers,inners,outerKeySelector, innerKeySelector, resultBuilder);
    }

    public <TOuter, TInner, TKey, TResult> Iterable<TResult> nestedLoopJoin(final Iterable<TOuter> outers, 
                                                                            final Iterable<TInner> inners, 
                                                                            final Fn1<TOuter, TKey> outerKeySelector,
                                                                            final Fn1<TInner, TKey> innerKeySelector,
                                                                            final Fn2<TOuter, TInner, TResult> resultBuilder) {
        return selectMany(outers, new Fn1<TOuter, Iterable<TResult>>() {
            public Iterable<TResult> apply(final TOuter outerRow) {
                final Iterable<TInner> matches = where(inners, new Fn1<TInner,Boolean>() {
                    public Boolean apply(TInner innerRow) {
                        return Boolean.valueOf(outerKeySelector.apply(outerRow).equals(innerKeySelector.apply(innerRow)));
                    }
                });

                return select(matches, new Fn1<TInner, TResult>() {
                    public TResult apply(TInner ir) {
                        return resultBuilder.apply(outerRow, ir);
                    }
                });
            }
        });
    }

    public <TOuter, TInner, TKey, TResult> Iterable<TResult> hashJoin(final Iterable<TOuter> outers, 
                                                                      final Iterable<TInner> inners, 
                                                                      final Fn1<TOuter, TKey> outerKeySelector,
                                                                      final Fn1<TInner, TKey> innerKeySelector,
                                                                      final Fn2<TOuter, TInner, TResult> resultBuilder) {
        return new LazyHashJoin(outers,inners,outerKeySelector,innerKeySelector, resultBuilder);
    }


    public <K,T> IterableMap<K,? extends Iterable<T>> groupBy(Iterable<T> source, Fn1<T,K> classifier) {
        IterableMap<K,LinkedList<T>> result = new IterableHashMap<K,LinkedList<T>>();
        for (T t : source) {
            K classification = classifier.apply(t);
            LinkedList<T> bucket = result.get(classification);
            if (bucket == null) {
                bucket = new LinkedList<T>();
                result.put(classification, bucket);
            }
            bucket.add(t);
        }

        return result;
    }

    public <T> Iterable<T> orderBy(final Iterable<T> source, final Comparator<? super T> comp) {
        // want lazy sorting
        return new Iterable<T>() {
            private Iterable<T> orderedSource = null;

            public synchronized Iterator<T> iterator() {
                // but we don't want to do the sort multiple times
                if (orderedSource == null) {
                    // must be a better way to do this
                    LinkedList<T> list = new LinkedList<T>();
                    for (T t : source) list.add(t);
                    Collections.sort(list, comp);
                    orderedSource = list;
                }

                return orderedSource.iterator();
            }
        };
    }

}
