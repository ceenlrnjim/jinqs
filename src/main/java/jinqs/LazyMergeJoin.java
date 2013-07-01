package jinqs;

import java.util.*;

/**
 * Merge join logic that assumes the iterators are already sorted based on their key values.
 * Allows the performance of the merge without the cost of the sort, assuming your data is already
 * sorted.
 */
public class LazyMergeJoin<TOuter, TInner, TKey extends Comparable, TResult> implements Iterable<TResult> {
    private final Iterable<TOuter> outers;
    private final Iterable<TInner> inners;
    private final Fn1<TOuter, TKey> outerKeySelector;
    private final Fn1<TInner, TKey> innerKeySelector;
    private final Fn2<TOuter, TInner, TResult> resultBuilder;

    public LazyMergeJoin(final Iterable<TOuter> outers, 
                             final Iterable<TInner> inners, 
                             final Fn1<TOuter, TKey> outerKeySelector,
                             final Fn1<TInner, TKey> innerKeySelector,
                             final Fn2<TOuter, TInner, TResult> resultBuilder) {
        this.outers = outers;
        this.inners = inners;
        this.outerKeySelector = outerKeySelector;
        this.innerKeySelector = innerKeySelector;
        this.resultBuilder = resultBuilder;
    }

    public Iterator<TResult> iterator() {
        return new LazyMergeIterator();
    }

    public class LazyMergeIterator implements Iterator<TResult> {
        private Iterator<TOuter> outerIterator;
        private Iterator<TInner> innerIterator;

        private TResult nextItem = null;
        private TInner innerRow;
        private TOuter outerRow;
        // state used to tell the next iteration what to increment (outer/inner)
        private boolean[] increment = new boolean[] { true, true };

        public LazyMergeIterator() {

            List<TResult> results = new LinkedList<TResult>();
            this.innerIterator = inners.iterator();
            this.outerIterator = outers.iterator();
            advance();
        }

        public boolean hasNext() {
            return nextItem != null;
        }

        public TResult next() {
            TResult t = nextItem;
            advance();
            return t;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }


        private void advance() {
            TKey innerKey, outerKey;
            int comp;

            try {
                while(true) {
                    if (increment[0]) {
                        outerRow = outerIterator.next();
                    }
                    if (increment[1]) {
                        innerRow = innerIterator.next();
                    }

                    innerKey = innerKeySelector.apply(innerRow);
                    outerKey = outerKeySelector.apply(outerRow);
                    comp = innerKey.compareTo(outerKey);
                    if (comp == 0) {
                        nextItem = resultBuilder.apply(outerRow, innerRow);
                        increment[0] = true;
                        increment[1] = false;
                        return;
                    } else if (comp < 0) {
                        increment[0] = false;
                        increment[1] = true;
                    } else if (comp > 0) {
                        increment[0] = true;
                        increment[1] = false;
                    }
                }
            } catch (NoSuchElementException nsee) {
                nextItem = null;
                return;
            }
        }
    }
}
