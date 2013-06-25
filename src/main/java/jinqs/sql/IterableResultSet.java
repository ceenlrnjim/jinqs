package jinqs.sql;

import java.sql.*;
import java.util.*;

/*
 * Makes a result set iterable so that it can be used with Enumerable for combining with
 * other data sources
 *
 * You're on your honor to not mess with which row the result set is on 
 */
public class IterableResultSet implements Iterable<ResultSet> {
    private ResultSet source;

    public IterableResultSet(ResultSet rs) {
        source = rs;
    }

    public Iterator<ResultSet> iterator() {
        try {
            if (!source.isBeforeFirst()) source.first();
        } catch (SQLException sqle) {
            throw new IllegalStateException(sqle);
        }

        return new Iterator<ResultSet>() {
            public void remove() {
                throw new UnsupportedOperationException("remove not supported");
            }

            public ResultSet next() {
                try {
                    boolean valid = source.next();
                    if (!valid) throw new NoSuchElementException("no more elements");
                    return source;
                } catch (SQLException SQLe) {
                    throw new RuntimeException(SQLe);
                }
            }

            public boolean hasNext() {
                try {
                    return !source.isLast() && !source.isAfterLast();
                } catch (SQLException SQLe) {
                    throw new RuntimeException(SQLe);
                }
            }
        };
    }

}
