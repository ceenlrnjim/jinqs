package jinqs;

import java.util.*;

public class LazyWhere<T> implements Iterable<T> {
    
    private final Iterable<T> source;
    private final Fn1<T,Boolean> pred;

    public LazyWhere(Iterable<T> source, Fn1<T,Boolean> pred) {
        this.source = source;
        this.pred = pred;
    }

    public Iterator<T> iterator() {
        final Iterator<T> srcItr = source.iterator();
        return new Iterator<T>() {
            private T nextItem = nextMatching();

            private T nextMatching() {
                while(srcItr.hasNext()) {
                    T t = srcItr.next();
                    if (pred.apply(t).booleanValue()) {
                        return t;
                    }
                }

                return null;
            }

            public boolean hasNext() {
                return nextItem != null;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public T next() {
                T result = nextItem;
                nextItem = nextMatching();
                return result;
            }
        };
    }
}
