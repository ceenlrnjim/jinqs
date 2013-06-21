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
        // TODO: refactor join to use selectMany for the nested loop join
        // TODO: add explicit merge sort join for times when the iterables are already sorted in the same order
        return new LazyJoin<TOuter, TInner, TKey, TResult>(outers,inners,outerKeySelector, innerKeySelector, resultBuilder);
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
