package jinqs;

import java.util.*;

public class LazyFlatteningIterator<S,T> implements Iterator<T> {

    private Iterator<S> sourceIterator;
    private Fn1<S,Iterable<T>> mapFn;
    private Iterator<T> subIterator;

    /** Item retrieved by look ahead */
    private T nextItem = null;

    // mapFn may return an empty iterator, which means that we need to look ahead one
    // entry to determine if there are any more matches
    public LazyFlatteningIterator(Iterator<S> src, Fn1<S,Iterable<T>> mapFn) {
        this.sourceIterator = src;
        this.mapFn = mapFn;
        advance();
    }

    public boolean hasNext() {
        return nextItem != null;
    }

    public T next() {
        T t = nextItem;
        advance();
        return t;
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove not supported");
    }

    private void advance() {
        if (subIterator != null && subIterator.hasNext()) {
            nextItem = subIterator.next();
        } else {
            // since a source item might not yield any output, we need to
            // advance until we hit the next match, or run out of source items
            nextItem = null;
            while (sourceIterator.hasNext()) {
                S s = sourceIterator.next();
                subIterator = mapFn.apply(s).iterator();
                if (subIterator.hasNext()) {
                    nextItem = subIterator.next();
                    break;
                }
            }
        }
    }

}
