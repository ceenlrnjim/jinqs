package jinqs.obj;


import jinqs.*;

/**
 * ObjectQueryInterface.from(rows).where(someFn).where(someOtherFn).select(selectorFn)
 *
 * from :- [select | [where+ - select]]
 *
 * TODO: group by, join, etc.
 */
public class ObjectsQueryInterface {
    private static Enumerable enumerable = new NaiveEnumerable(); // TODO: dependency injection

    public static <T,U> QuerySource from(Iterable<T> source) {
        return new QuerySource<T,U>(source);
    };

    public static class QuerySource<T,U> {
        private Iterable<T> source;

        public QuerySource(Iterable<T> source) {
            this.source = source;
        }

        public QueryPred<T,U> where(Fn1<T,Boolean> predicate) {
            return new QueryPred<T,U>(source,predicate);
        }

        public Iterable<U> select(Fn1<T,U> selector) {
            QueryPred<T,U> qp = new QueryPred<T,U>(source, new Fn1<T,Boolean>() {
                public Boolean apply(T unused) {
                    return Boolean.TRUE;
                }
            });

            return qp.select(selector);
        }

        // TODO: support where before join? join after selector?
        public <TInner,TKey,TJoined> QuerySource<TJoined,U> join(Iterable<TInner> innerSource, 
                                            Fn1<T,TKey> outerKeyFn,
                                            Fn1<TInner,TKey> innerKeyFn,
                                            Fn2<T,TInner,TJoined> selector) {
            return new QuerySource<TJoined,U>(enumerable.join(source, innerSource, outerKeyFn, innerKeyFn, selector));
        }
    };

    public static class QueryPred<T,U> {
        private Iterable<T> source;
        private final Fn1<T,Boolean> predicate;

        public QueryPred(Iterable<T> source, Fn1<T,Boolean> predicate) {
            this.source = source;
            this.predicate = predicate;
        }

        public QueryPred<T,U> where(final Fn1<T,Boolean> pred2) {
            return new QueryPred<T,U>(this.source, new Fn1<T,Boolean>() {
                public Boolean apply(T t) {
                    return Boolean.valueOf(predicate.apply(t).booleanValue() && pred2.apply(t).booleanValue());
                }
            });
        }

        public Iterable<U> select(Fn1<T,U> selector) {
            return enumerable.select(enumerable.where(source, predicate), selector);
        }
    };
}
