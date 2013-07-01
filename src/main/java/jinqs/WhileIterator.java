package jinqs;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class WhileIterator<T> implements Iterator<T> {
    private final Fn1<T,Boolean> pred;
    private final Iterator<T> src;
    private T nextItem;
    private boolean predHolds = true;

    public WhileIterator(Iterator<T> src, Fn1<T,Boolean> pred) {
        this.pred = pred;
        this.src = src;

        advance();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public T next() {
        if (nextItem == null) throw new NoSuchElementException();

        T result = nextItem;
        advance();
        return result;
    }

    public boolean hasNext() {
        return predHolds && nextItem != null;
    }

    private void advance() {
        if (!src.hasNext()) {
            predHolds = false;
            nextItem = null;
            return;
        }

        T t = src.next();
        predHolds = pred.apply(t).booleanValue();
        if (predHolds) {
            nextItem = t;
        } else {
            predHolds = false;
            nextItem = null;
        }
    }
}
