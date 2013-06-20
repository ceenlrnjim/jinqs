package jinqs;

import java.util.*;

public class NaiveEnumerable implements Enumerable {

    // Projection
    public <S,T> Iterable<T> select(Iterable<S> source, Fn1<S,T> selector) {
        LinkedList<T> result = new LinkedList<T>();
        for (S s : source) {
            result.add(selector.apply(s));
        }

        return result;
    }

    // Cross-Apply
    public <S,T> Iterable<T> selectMany(Iterable<S> source, Fn1<S, Iterable<T>> selector) {
        LinkedList<T> result = new LinkedList<T>();
        for (S s : source) {
            Iterable<T> ts = selector.apply(s);
            // addAll requires a collection
            for (T t : ts) {
                result.add(t);
            }
        }

        return result;
    }

    // selection
    public <T> Iterable<T> where(Iterable<T> source, Fn1<T,Boolean> predicate) {
        LinkedList<T> result = new LinkedList<T>();
        for (T t : source) {
            if (predicate.apply(t).booleanValue()) {
                result.add(t);
            }
        }

        return result;
    }


    public <TOuter, TInner, TKey, TResult> Iterable<TResult> join(Iterable<TOuter> outers, 
                                                                  Iterable<TInner> inners, 
                                                                  Fn1<TOuter, TKey> outerKeySelector,
                                                                  Fn1<TInner, TKey> innerKeySelector,
                                                                  Fn2<TOuter, TInner, TResult> resultBuilder) {
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

}
