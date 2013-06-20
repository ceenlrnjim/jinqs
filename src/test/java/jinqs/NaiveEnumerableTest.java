package jinqs;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

public class NaiveEnumerableTest {
    private LinkedList<Object[]> dataSet = new LinkedList<Object[]>();
    private NaiveEnumerable ne = new NaiveEnumerable();

    public NaiveEnumerableTest() {
        dataSet.add(new Object[] {"Jim","10","A"});
        dataSet.add(new Object[] {"Joan","11","A"});
        dataSet.add(new Object[] {"Jerry","12","B"});
        dataSet.add(new Object[] {"Jules","13","B"});
    }

    @Test
    public void testWhere() {
        Iterable<Object[]> result = ne.where(dataSet, Fns.Predicates.indexEquals(2, "B"));
        HashSet names = new HashSet();
        HashSet ltrs = new HashSet();
        for (Object[] row : result) {
            names.add(row[0]);
            ltrs.add(row[2]);
        }

        assertEquals("Only B's", 1, ltrs.size());
        assertEquals("B", "B", ltrs.iterator().next());
        assertTrue("Contains Jerry", names.contains("Jerry"));
        assertTrue("Contains Jules", names.contains("Jules"));
    }

    @Test
    public void testSelect() {
        Iterable<Map<String,String>> result = ne.select(dataSet, new Fn1<Object[], Map<String,String>>() {
            public Map<String,String> apply(Object[] row) {
                Map<String,String> result = new HashMap<String,String>();
                result.put("name", row[0].toString());
                result.put("id", row[1].toString());
                result.put("class", row[2].toString());
                return result;
            }
        });

        HashMap<String,Map> lookup = new HashMap<String,Map>();
        for (Map<String,String> row : result) {
            lookup.put(row.get("id"), row);
        }

        assertEquals("checking for Jim", "Jim", lookup.get("10").get("name"));
        assertEquals("checking for Joan", "Joan", lookup.get("11").get("name"));
        assertEquals("checking for Jerry", "Jerry", lookup.get("12").get("name"));
        assertEquals("checking for Jules", "Jules", lookup.get("13").get("name"));
    }


}
