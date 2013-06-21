package jinqs;

import java.util.*;

// TODO: is iteratable the interface I want here - may want something that includes the type and collection constructor so that I can return the same type that was provided
// TODO: should type constraints be on the method insteaf of the class
//
// TODO: this should be the implementation of these methods for querying Iterable things
// TODO: need a better name for this
public interface Enumerable {
    // Projection
    public <S,T> Iterable<T> select(Iterable<S> source, Fn1<S,T> selector);
    // Cross-Apply
    public <S,T> Iterable<T> selectMany(Iterable<S> source, Fn1<S, Iterable<T>> selector);
    // selection
    public <T> Iterable<T> where(Iterable<T> source, Fn1<T,Boolean> predicate);
    // join - TKey must be hashable and comparable for equality
    public <TOuter, TInner, TKey, TResult> Iterable<TResult> join(Iterable<TOuter> sourceOuter, 
                                                                  Iterable<TInner> sourceInner, 
                                                                  Fn1<TOuter, TKey> outerKeySelector,
                                                                  Fn1<TInner, TKey> innerKeySelector,
                                                                  Fn2<TOuter, TInner, TResult> resultBuilder);

    public <K,T> IterableMap<K,? extends Iterable<T>> groupBy(Iterable<T> source, Fn1<T,K> classifier);
    public <T> Iterable<T> orderBy(Iterable<T> source, Comparator<? super T> comp);
}
