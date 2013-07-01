package jinqs.obj;


import jinqs.*;
import java.util.*;

/**
 * ObjectQueryInterface.from(rows).where(someFn).where(someOtherFn).select(selectorFn)
 *
 * TODO: what about a more limited, comprehension like syntax (for in do if then yield)
 */
public class ObjectsQueryInterface {
    private static Enumerable enumerable = new NaiveEnumerable(); // TODO: dependency injection

    public static <T> Query from(Iterable<T> source) {
        return new Query<T>(source);
    };

    // This is effectively the threading operator, creates a pipeline of iterators and filters resulting in a single Iterable returned by run
    public static class Query<T> {
        private Iterable<T> source;

        //public <S extends Iterable<T>> Query(S source) {
        public Query(Iterable<T> source) {
            this.source = source;
        }

        public Query<T> where(Fn1<T,Boolean> predicate) {
            return new Query<T>(enumerable.where(source, predicate));
        }

        public <U> Query<U> select(Fn1<T,U> selector) {
            return new Query<U>(enumerable.select(source,selector));
        }

        public <U> Query<U> selectMany(Fn1<T,Iterable<U>> selector) {
            return new Query<U>(enumerable.selectMany(source,selector));
        }

        public <TInner,TKey,TJoined> Query<TJoined> join(Iterable<TInner> innerSource, 
                                            Fn1<T,TKey> outerKeyFn,
                                            Fn1<TInner,TKey> innerKeyFn,
                                            Fn2<T,TInner,TJoined> selector) {
            return new Query<TJoined>(enumerable.join(source, innerSource, outerKeyFn, innerKeyFn, selector));
        }

        public <TInner,TKey,TJoined> Query<TJoined> nestedLoopJoin(Iterable<TInner> innerSource, 
                                            Fn1<T,TKey> outerKeyFn,
                                            Fn1<TInner,TKey> innerKeyFn,
                                            Fn2<T,TInner,TJoined> selector) {
            return new Query<TJoined>(enumerable.nestedLoopJoin(source, innerSource, outerKeyFn, innerKeyFn, selector));
        }


        public <TInner,TKey,TJoined> Query<TJoined> hashJoin(Iterable<TInner> innerSource, 
                                            Fn1<T,TKey> outerKeyFn,
                                            Fn1<TInner,TKey> innerKeyFn,
                                            Fn2<T,TInner,TJoined> selector) {
            return new Query<TJoined>(enumerable.hashJoin(source, innerSource, outerKeyFn, innerKeyFn, selector));
        }

        public <TInner,TKey extends Comparable,TJoined> Query<TJoined> sortMergeJoin(Iterable<TInner> innerSource, 
                                            Fn1<T,TKey> outerKeyFn,
                                            Fn1<TInner,TKey> innerKeyFn,
                                            Fn2<T,TInner,TJoined> selector) {
            return new Query<TJoined>(enumerable.sortMergeJoin(source, innerSource, outerKeyFn, innerKeyFn, selector));
        }

        public <TInner,TKey extends Comparable,TJoined> Query<TJoined> mergeJoin(Iterable<TInner> innerSource, 
                                            Fn1<T,TKey> outerKeyFn,
                                            Fn1<TInner,TKey> innerKeyFn,
                                            Fn2<T,TInner,TJoined> selector) {
            return new Query<TJoined>(enumerable.mergeJoin(source, innerSource, outerKeyFn, innerKeyFn, selector));
        }

        public Query<T> take(int count) {
            return new Query<T>(enumerable.take(source, count));
        }

        public Query<T> drop(int count) {
            return new Query<T>(enumerable.drop(source, count));
        }

        public Query<T> whileTrue(Fn1<T,Boolean> pred) {
            return new Query<T>(enumerable.whileTrue(source, pred));
        }

        public Query<T> orderBy(Comparator<? super T> comp) {
            return new Query<T>(enumerable.orderBy(source, comp));
        }


        // Note: this is not lazy
        // TODO: get the types to work
        //public <U> Query<Map.Entry<U,T>> groupBy(Fn1<T,U> classifier) {
            //return new Query<Map.Entry<U,T>>(enumerable.<U,T>groupBy(source, classifier));
        //}

        public Iterable<T> run() {
            return source;
        }
    };
}
