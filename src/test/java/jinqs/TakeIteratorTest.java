package jinqs;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.*;

public class TakeIteratorTest {
    @Test 
    public void testLimitEnforced() {
        Iterator<String> iter = new TakeIterator<String>(Arrays.asList("a","b","c","d","e").iterator(), 3);
        int counter = 0;
        while (iter.hasNext()) {
            iter.next();
            counter++;
        }

        assertEquals(3, counter);
    }

    @Test
    public void testLessThanLimit() {
        Iterator<String> iter = new TakeIterator<String>(Arrays.asList("a","b","c","d","e").iterator(), 10);
        int counter = 0;
        while (iter.hasNext()) {
            iter.next();
            counter++;
        }

        assertEquals(5, counter);
    }

    @Test 
    public void testEmptyIterator() {
        Iterator<String> iter = new TakeIterator<String>(Collections.<String>emptyList().iterator(), 10);
        int counter = 0;
        while (iter.hasNext()) {
            iter.next();
            counter++;
        }

        assertEquals(0, counter);

    }
}
