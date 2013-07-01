package jinqs;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TakeIterator<T> implements Iterator<T> {
    private int count;
    private final Iterator<T> src;

    public TakeIterator(Iterator<T> src, int count) {
        this.count = count;
        this.src = src;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public T next() {
        if (!hasNext()) throw new NoSuchElementException();

        T result = src.next();
        count--;
        return result;
    }

    public boolean hasNext() {
        return count > 0 && src.hasNext();
    }
}
