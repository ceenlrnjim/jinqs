package jinqs;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.*;

public class WhileIteratorTest {
    @Test 
    public void testPredicate() {
        Iterator<Integer> iter = new WhileIterator<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10).iterator(), new Fn1<Integer, Boolean>() {
            public Boolean apply(Integer val) {
                return Boolean.valueOf(val.intValue() < 6);
            }
        });
        
        int last = -1;
        int counter = 0;
        while (iter.hasNext()) {
            last = iter.next();
            counter++;
        }

        assertEquals(5, last);
        assertEquals(5, counter);
    }

    @Test
    public void testNoMatches() {
        Iterator<Integer> iter = new WhileIterator<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10).iterator(), new Fn1<Integer, Boolean>() {
            public Boolean apply(Integer val) {
                return Boolean.valueOf(val.intValue() > 10);
            }
        });
        
        int last = -1;
        int counter = 0;
        while (iter.hasNext()) {
            last = iter.next();
            counter++;
        }

        assertEquals(-1, last);
        assertEquals(0, counter);

    }

    @Test 
    public void testAllMatches() {
        Iterator<Integer> iter = new WhileIterator<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10).iterator(), new Fn1<Integer, Boolean>() {
            public Boolean apply(Integer val) {
                return Boolean.valueOf(val.intValue() < 100);
            }
        });
        
        int last = -1;
        int counter = 0;
        while (iter.hasNext()) {
            last = iter.next();
            counter++;
        }

        assertEquals(10, last);
        assertEquals(10, counter);

    }
}
