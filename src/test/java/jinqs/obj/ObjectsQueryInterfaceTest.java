package jinqs.obj;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import jinqs.*;
import jinqs.Fns;
import static jinqs.Fns.Predicates.*;
import static jinqs.Fns.Accessors.*;
import static jinqs.Fns.Comparators.*;

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
                                                       //TODO: this type isn't right, why didn't this fail?
                                                       //.select(Fns.<Map<String,String>>identity())
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

    @Test
    public void testHashJoin() {
        Fn2<String[],String[],String> selector = new Fn2<String[],String[],String>() {
            public String apply(String[] o, String[] i) {
                return o[0] + "_" + i[1];
            }
        };

        Iterable<String> result = ObjectsQueryInterface.from(dataSet)
                                                       .hashJoin(dataSet2, valueAtIndex(2), valueAtIndex(0), selector)
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


    @Test
    public void testSelectMany() {
        // implementing join with select many
        Fn1<String[],Iterable<String[]>> selector = new Fn1<String[], Iterable<String[]>>() {
            public Iterable<String[]> apply(final String[] a) {
                return ObjectsQueryInterface.from(dataSet)
                                            .where(indexEquals(2, a[0]))
                                            .select(new Fn1<String[],String[]>() {
                                                public String[] apply(String[] b) {
                                                    List<String> result = new ArrayList<String>(a.length + b.length);
                                                    result.addAll(Arrays.asList(a));
                                                    result.addAll(Arrays.asList(b));
                                                    return result.toArray(a); // this sucks
                                                    
                                                }
                                            }).run();
            }
        };

        Fn1<String[],String> formatter = new Fn1<String[],String>() {
            public String apply(String[] o) {
                return o[2] + "_" + o[1];
            }
        };

        Iterable<String> result = ObjectsQueryInterface.from(dataSet2)
                                                       .selectMany(selector)
                                                       .select(formatter)
                                                       .run();
        
        HashSet allresults = new HashSet();
        for (String s : result) {
            System.out.println(s);
            allresults.add(s);
        }

        assertEquals("Result Count", 4, allresults.size());
        assertTrue("Jim", allresults.contains("Jim_Category A"));
        assertTrue("Joan", allresults.contains("Joan_Category A"));
        assertTrue("Jerry", allresults.contains("Jerry_Category B"));
        assertTrue("Jules", allresults.contains("Jules_Category B"));
    }

    @Test
    public void testOrderBy() {
        Iterable<String[]> result = ObjectsQueryInterface.from(dataSet)
                                                       .orderBy(arrayIndexComparator(Fns.Accessors.<String>valueAtIndex(0)))
                                                       //.select(valueAtIndex(0))
                                                       .run();

        Iterator<String[]> resultItr = result.iterator();
        assertEquals("Jerry", resultItr.next()[0]);
        assertEquals("Jim", resultItr.next()[0]);
        assertEquals("Joan", resultItr.next()[0]);
        assertEquals("Jules", resultItr.next()[0]);
    }
}
