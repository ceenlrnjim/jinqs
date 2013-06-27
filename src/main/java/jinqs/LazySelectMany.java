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
        return new LazyFlatteningIterator<T,U>(source.iterator(), mapper);
    }
}
