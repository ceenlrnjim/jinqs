package jinqs;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DropIterator<T> implements Iterator<T> {
    private int count;
    private final Iterator<T> src;

    public DropIterator(Iterator<T> src, int count) {
        this.count = count;
        this.src = src;

        while (count > 0 && src.hasNext()) {
            src.next();
            count--;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public T next() {
        return src.next();
    }

    public boolean hasNext() {
        return src.hasNext();
    }
}
