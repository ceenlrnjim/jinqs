package jinqs.xml;

import javax.xml.xquery.XQSequence;
import javax.xml.xquery.XQItem;
import javax.xml.xquery.XQException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IterableXQSequence implements Iterable<XQItem> {
    private XQSequence source;

    public IterableXQSequence(XQSequence source) {
        this.source = source;
    }

    public Iterator<XQItem> iterator()  {
        //try {
            //if (!source.isBeforeFirst()) source.first();
        //} catch (XQException sqle) {
            //throw new IllegalStateException(sqle);
        //}

        return new XQItemIterator();
    }

    private class XQItemIterator implements Iterator<XQItem> {
        private boolean nextAvailable;
        public XQItemIterator() {
            try {
                nextAvailable = source.next();
            } catch (XQException e) {
                throw new RuntimeException(e);
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }

        public XQItem next() {
            try {
                if (!nextAvailable) throw new NoSuchElementException("no more elements");
                XQItem result = source.getItem();
                nextAvailable = source.next();
                return result;
            } catch (XQException SQLe) {
                throw new RuntimeException(SQLe);
            }
        }

        public boolean hasNext() {
            return nextAvailable;
        }

    }
}
