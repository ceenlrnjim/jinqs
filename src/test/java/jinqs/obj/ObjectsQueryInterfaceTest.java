package jinqs.obj;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import jinqs.*;
import static jinqs.Fns.Predicates.*;
import static jinqs.Fns.Accessors.*;

public class ObjectsQueryInterfaceTest {
    private LinkedList<String[]> dataSet = new LinkedList<String[]>();
    private LinkedList<String[]> dataSet2 = new LinkedList<String[]>();

    public ObjectsQueryInterfaceTest() {
        dataSet.add(new String[] {"Jim","10","A"});
        dataSet.add(new String[] {"Joan","11","A"});
        dataSet.add(new String[] {"Jerry","12","B"});
        dataSet.add(new String[] {"Jules","13","B"});
        dataSet2.add(new String[] {"A","Category A"});
        dataSet2.add(new String[] {"B","Category B"});
    }

    @Test
    public void testQuerySingleWhere() {
        Iterable<String> result = ObjectsQueryInterface.from(dataSet)
                                                        .where(indexEquals(1, "12"))
                                                        .select(valueAtIndex(0))
                                                        .run();
        Iterator<String> itr = result.iterator();
        assertTrue("at least one match", itr.hasNext());
        assertEquals("checking value", "Jerry", itr.next());
        assertFalse("exactly one match", itr.hasNext());
    }

    @Test
    public void testMultipleWhere() {
        Iterable<String> result = ObjectsQueryInterface.from(dataSet)
                                                        .where(indexEquals(1, "12"))
                                                        .where(indexEquals(2, "B"))
                                                        .select(valueAtIndex(0))
                                                        .run();
    
        Iterator<String> itr = result.iterator();
        assertTrue("at least one match", itr.hasNext());
        assertEquals("checking value", "Jerry", itr.next());
        assertFalse("exactly one match", itr.hasNext());

        result = ObjectsQueryInterface.from(dataSet)
                                    .where(indexEquals(1, "12"))
                                    .where(indexEquals(2, "A"))
                                    .select(valueAtIndex(0))
                                    .run();
        assertFalse("expect no matches", itr.hasNext());
    }

    @Test
    public void testJoin() {
        Fn2<String[],String[],String> selector = new Fn2<String[],String[],String>() {
            public String apply(String[] o, String[] i) {
                return o[0] + "_" + i[1];
            }
        };

        Iterable<String> result = ObjectsQueryInterface.from(dataSet)
                                                       .join(dataSet2, valueAtIndex(2), valueAtIndex(0), selector)
                                                       .select(Fns.<Map<String,String>>identity())
                                                       .run();
        
        HashSet allresults = new HashSet();
        for (String s : result) {
            allresults.add(s);
        }

        assertEquals("Result Count", 4, allresults.size());
        assertTrue("Jim", allresults.contains("Jim_Category A"));
        assertTrue("Joan", allresults.contains("Joan_Category A"));
        assertTrue("Jerry", allresults.contains("Jerry_Category B"));
        assertTrue("Jules", allresults.contains("Jules_Category B"));
    }
}
