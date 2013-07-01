package jinqs;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.*;

public class DropIteratorTest {
    @Test 
    public void testLimitEnforced() {
        Iterator<String> iter = new DropIterator<String>(Arrays.asList("a","b","c","d","e").iterator(), 2);
        assertEquals("c", iter.next());
        assertEquals("d", iter.next());
        assertEquals("e", iter.next());
    }

    @Test
    public void testLessThanLimit() {
        Iterator<String> iter = new DropIterator<String>(Arrays.asList("a","b","c","d","e").iterator(), 10);
        assertTrue(!iter.hasNext());
    }

    @Test 
    public void testEmptyIterator() {
        Iterator<String> iter = new DropIterator<String>(Collections.<String>emptyList().iterator(), 10);
        assertTrue(!iter.hasNext());
    }
}
