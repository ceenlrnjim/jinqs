package jinqs;

import java.util.*;

public class LazySelectMany<T,U> implements Iterable<U> {
    
    private final Iterable<T> source;
    private final Fn1<T,Iterable<U>> mapper;

    public LazySelectMany(Iterable<T> source, Fn1<T,Iterable<U>> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    public Iterator<U> iterator() {
        final Iterator<T> srcItr = source.iterator();
        return new Iterator<U>() {
            private Iterator<U> mappedItr = null; 
            public boolean hasNext() {

                return srcItr.hasNext() || (mappedItr != null && mappedItr.hasNext());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public U next() {
                if (mappedItr == null) {
                    mappedItr = mapper.apply(srcItr.next()).iterator();
                    if (mappedItr.hasNext()) return mappedItr.next();
                    else {
                        mappedItr = null;
                        return next(); // not safe recursion
                    }
                } else if (mappedItr.hasNext()) {
                    return mappedItr.next();
                } else {
                    mappedItr = null;
                    return next(); // not safe recursion
                }
            }
        };
    }
}
