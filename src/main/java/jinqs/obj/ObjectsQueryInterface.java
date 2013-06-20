package jinqs.obj;


import jinqs.*;

/**
 * ObjectQueryInterface.from(rows).where(someFn).where(someOtherFn).select(selectorFn)
 *
 * TODO: group by, order by etc
 */
public class ObjectsQueryInterface {
    private static Enumerable enumerable = new NaiveEnumerable(); // TODO: dependency injection

    public static <T> Query from(Iterable<T> source) {
        return new Query<T>(source);
    };

    // This is effectively the threading operator, creates a pipeline of iterators and filters resulting in a single Iterable returned by run
    public static class Query<T> {
        private Iterable<T> source;

        public Query(Iterable<T> source) {
            this.source = source;
        }

        public Query<T> where(Fn1<T,Boolean> predicate) {
            return new Query<T>(enumerable.where(source, predicate));
        }

        public <U> Query<U> select(Fn1<T,U> selector) {
            return new Query<U>(enumerable.select(source,selector));
        }

        // TODO: support where before join? join after selector?
        public <TInner,TKey,TJoined> Query<TJoined> join(Iterable<TInner> innerSource, 
                                            Fn1<T,TKey> outerKeyFn,
                                            Fn1<TInner,TKey> innerKeyFn,
                                            Fn2<T,TInner,TJoined> selector) {
            return new Query<TJoined>(enumerable.join(source, innerSource, outerKeyFn, innerKeyFn, selector));
        }

        public Iterable<T> run() {
            return source;
        }
    };
}
