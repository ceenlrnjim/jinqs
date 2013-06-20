package jinqs;

// needs to be expression based - create some sort of AST to allow SQL providers etc to convert the
// expression to the target language
public interface Queryable<T> extends Iterable<T> {
    // all
    // any
    // concat
    // contains
    // distinct
    // element at
    // group by
    // intersect
    // union
    // difference
    // last or default
    // min/max
    // order by
    // single - returns only element or throws exception if > 1
    // skip while
    // take while
    // where
    // (and others)
}
