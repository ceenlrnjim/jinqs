package jinqs;

import java.util.*;

public class NaiveEnumerable implements Enumerable {

    // Projection
    public <S,T> Iterable<T> select(Iterable<S> source, Fn1<S,T> selector) {
        return new LazySelect(source, selector);
    }

    // Cross-Apply
    public <S,T> Iterable<T> selectMany(Iterable<S> source, Fn1<S, Iterable<T>> selector) {
        return new LazySelectMany(source, selector);
    }

    // selection
    public <T> Iterable<T> where(Iterable<T> source, Fn1<T,Boolean> predicate) {
        return new LazyWhere(source,predicate);
    }


    public <TOuter, TInner, TKey, TResult> Iterable<TResult> join(Iterable<TOuter> outers, 
                                                                  Iterable<TInner> inners, 
                                                                  Fn1<TOuter, TKey> outerKeySelector,
                                                                  Fn1<TInner, TKey> innerKeySelector,
                                                                  Fn2<TOuter, TInner, TResult> resultBuilder) {
        return nestedLoopJoin(outers,inners,outerKeySelector, innerKeySelector, resultBuilder);
    }

    public <TOuter, TInner, TKey, TResult> Iterable<TResult> nestedLoopJoin(final Iterable<TOuter> outers, 
                                                                            final Iterable<TInner> inners, 
                                                                            final Fn1<TOuter, TKey> outerKeySelector,
                                                                            final Fn1<TInner, TKey> innerKeySelector,
                                                                            final Fn2<TOuter, TInner, TResult> resultBuilder) {
        return selectMany(outers, new Fn1<TOuter, Iterable<TResult>>() {
            public Iterable<TResult> apply(final TOuter outerRow) {
                final Iterable<TInner> matches = where(inners, new Fn1<TInner,Boolean>() {
                    public Boolean apply(TInner innerRow) {
                        return Boolean.valueOf(outerKeySelector.apply(outerRow).equals(innerKeySelector.apply(innerRow)));
                    }
                });

                return select(matches, new Fn1<TInner, TResult>() {
                    public TResult apply(TInner ir) {
                        return resultBuilder.apply(outerRow, ir);
                    }
                });
            }
        });
    }

    public <TOuter, TInner, TKey, TResult> Iterable<TResult> hashJoin(final Iterable<TOuter> outers, 
                                                                      final Iterable<TInner> inners, 
                                                                      final Fn1<TOuter, TKey> outerKeySelector,
                                                                      final Fn1<TInner, TKey> innerKeySelector,
                                                                      final Fn2<TOuter, TInner, TResult> resultBuilder) {
        return new LazyHashJoin(outers,inners,outerKeySelector,innerKeySelector, resultBuilder);
    }

    /**
     * Note that this join currently only supports 1-1 or many-1 joins (not 1-many or many-many).
     * The "outer" Iterable may contain multiple matches for an item in the inners Iterable but
     * not the other way around
     */
    public <TOuter, TInner, TKey extends Comparable, TResult> Iterable<TResult> sortMergeJoin(final Iterable<TOuter> outers, 
                                                                          final Iterable<TInner> inners, 
                                                                          final Fn1<TOuter, TKey> outerKeySelector,
                                                                          final Fn1<TInner, TKey> innerKeySelector,
                                                                          final Fn2<TOuter, TInner, TResult> resultBuilder) {
        // TODO: this currently only supports primary key joins - there can only be one record with a given TKey in each
        // of the Iterables.  Need to implement multiple passes to support one to many relationships
        
        // TODO: make lazy?
        // TODO: require list for iterables or add to lists?
        List<TResult> results = new LinkedList<TResult>();
        List<TOuter> sortedOuters = new LinkedList<TOuter>();
        List<TInner> sortedInners = new LinkedList<TInner>();

        for (TOuter row : outers) sortedOuters.add(row);
        for (TInner row : inners) sortedInners.add(row);

        if (sortedOuters.size() == 0 || sortedInners.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        Collections.sort(sortedOuters, new Comparator<TOuter>() {
            public int compare(TOuter a, TOuter b) {
                return outerKeySelector.apply(a).compareTo(outerKeySelector.apply(b));
            }
        });

        Collections.sort(sortedInners, new Comparator<TInner>() {
            public int compare(TInner a, TInner b) {
                return innerKeySelector.apply(a).compareTo(innerKeySelector.apply(b));
            }
        });

        Iterator<TInner> innerIterator = sortedInners.iterator();
        Iterator<TOuter> outerIterator = sortedOuters.iterator();

        TInner innerRow;
        TOuter outerRow;
        TKey innerKey, outerKey;
        int comp;

        innerRow = innerIterator.next();
        outerRow = outerIterator.next();
        try {
            while (true) {
                innerKey = innerKeySelector.apply(innerRow);
                outerKey = outerKeySelector.apply(outerRow);
                comp = innerKey.compareTo(outerKey);
                if (comp == 0) {
                    results.add(resultBuilder.apply(outerRow, innerRow));
                    // In theory, only advancing the outer row allows multiple records in the outer collection to
                    // match a single record in the inner collection - TODO: do I want the "many" on the outer or the inner?
                    //innerRow = innerIterator.next();
                    outerRow = outerIterator.next();
                } else if (comp < 0) {
                    innerRow = innerIterator.next();
                } else if (comp > 0) {
                    outerRow = outerIterator.next();
                }
            }
        } catch (NoSuchElementException nsee) {
            // we've run out of rows - not an error, just easier that putting all the right hasNext calls
            
        }

        return results;
    }


    public <K,T> IterableMap<K,? extends Iterable<T>> groupBy(Iterable<T> source, Fn1<T,K> classifier) {
        IterableMap<K,LinkedList<T>> result = new IterableHashMap<K,LinkedList<T>>();
        for (T t : source) {
            K classification = classifier.apply(t);
            LinkedList<T> bucket = result.get(classification);
            if (bucket == null) {
                bucket = new LinkedList<T>();
                result.put(classification, bucket);
            }
            bucket.add(t);
        }

        return result;
    }

    public <T> Iterable<T> orderBy(final Iterable<T> source, final Comparator<? super T> comp) {
        // want lazy sorting
        return new Iterable<T>() {
            private Iterable<T> orderedSource = null;

            public synchronized Iterator<T> iterator() {
                // but we don't want to do the sort multiple times
                if (orderedSource == null) {
                    // must be a better way to do this
                    LinkedList<T> list = new LinkedList<T>();
                    for (T t : source) list.add(t);
                    Collections.sort(list, comp);
                    orderedSource = list;
                }

                return orderedSource.iterator();
            }
        };
    }

}
