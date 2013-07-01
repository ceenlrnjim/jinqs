package jinqs;

import java.util.*;

public class LazySortMergeJoin<TOuter, TInner, TKey extends Comparable, TResult> implements Iterable<TResult> {
    private final Iterable<TOuter> outers;
    private final Iterable<TInner> inners;
    private final Fn1<TOuter, TKey> outerKeySelector;
    private final Fn1<TInner, TKey> innerKeySelector;
    private final Fn2<TOuter, TInner, TResult> resultBuilder;

    public LazySortMergeJoin(final Iterable<TOuter> outers, 
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
        return new LazySortMergeIterator();
    }

    public class LazySortMergeIterator implements Iterator<TResult> {
        private Iterator<TOuter> outerIterator;
        private Iterator<TInner> innerIterator;

        private TResult nextItem = null;
        private TInner innerRow;
        private TOuter outerRow;
        // state used to tell the next iteration what to increment (outer/inner)
        private boolean[] increment = new boolean[] { true, true };

        public LazySortMergeIterator() {

            List<TResult> results = new LinkedList<TResult>();
            List<TOuter> sortedOuters = new LinkedList<TOuter>();
            List<TInner> sortedInners = new LinkedList<TInner>();

            for (TOuter row : outers) sortedOuters.add(row);
            for (TInner row : inners) sortedInners.add(row);

            if (sortedOuters.size() == 0 || sortedInners.size() == 0) {
                return; // and nextItem will be null and hasNext will be false
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

            this.innerIterator = sortedInners.iterator();
            this.outerIterator = sortedOuters.iterator();
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

