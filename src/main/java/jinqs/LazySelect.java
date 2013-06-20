package jinqs;

import java.util.*;

public class LazySelect<T,U> implements Iterable<U> {
    
    private final Iterable<T> source;
    private final Fn1<T,U> mapper;

    public LazySelect(Iterable<T> source, Fn1<T,U> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    public Iterator<U> iterator() {
        final Iterator<T> srcItr = source.iterator();
        return new Iterator<U>() {
            public boolean hasNext() {
                return srcItr.hasNext();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public U next() {
                return mapper.apply(srcItr.next());
            }
        };
    }
}
