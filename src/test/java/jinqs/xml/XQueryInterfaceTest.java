package jinqs.xml;

import org.junit.Test;
import static org.junit.Assert.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xquery.*;
import java.util.*;
import jinqs.*;
import static jinqs.xml.XQueryInterface.getItemAttributeValue;

public class XQueryInterfaceTest {

    @Test
    public void testNoParam() {
        SAXSource src = new SAXSource(new org.xml.sax.InputSource(new java.io.StringReader(testData)));
        Iterable<XQItem> result = new XQueryInterface(src, "n")
                                        .query("for $e in $n/emps/emp\nlet $name := $e/name\nwhere $e/salary > 250\nreturn <earner name='{$name}'>{$e}</earner>")
                                        .run();
        HashSet names = new HashSet();

        for (XQItem item : result) {
            names.add(getItemAttributeValue(item, "name"));
        }

        assertTrue(names.contains("Jenny"));
        assertTrue(names.contains("Jake"));
        assertTrue(names.contains("Jerome"));
        assertTrue(names.contains("Julie"));
        assertTrue(names.contains("Jessica"));
    }

    @Test
    public void testMultipleDocuments() {
        SAXSource emps = new SAXSource(new org.xml.sax.InputSource(new java.io.StringReader(testData)));
        SAXSource deps = new SAXSource(new org.xml.sax.InputSource(new java.io.StringReader(depts)));

        HashMap<String,String> resultMap = new HashMap<String,String>();
        Iterable<String[]> result = new NaiveEnumerable().select(new XQueryInterface(emps, "edoc")
                                                                    .addSource(deps, "ddoc")
                                                                    .query("for $d in $ddoc/depts/deptno "+
                                                                           "let $e := $edoc/emps/emp[deptno = $d] "+
                                                                           //"where fn:count($e) >= 2"+
                                                                           "order by fn:avg($e/salary) descending "+
                                                                           "return <result dept='{$d/text()}' size='{fn:count($e)}'/>")
                                                                    .run(), 
                                                                  new Fn1<XQItem,String[]>() {
                                                                    public String[] apply(XQItem item) {
                                                                        String[] result = new String[2];
                                                                        result[0] = getItemAttributeValue(item, "dept");
                                                                        result[1] = getItemAttributeValue(item,"size");
                                                                        return result;
                                                                    }
                                                                });

        // TODO: need a reduce function
        for (String[] item : result) {
            resultMap.put(item[0], item[1]);
        }

        assertEquals("2",resultMap.get("1"));
        assertEquals("2",resultMap.get("2"));
        assertEquals("2",resultMap.get("3"));
        assertEquals("2",resultMap.get("4"));
        assertEquals("1",resultMap.get("5"));

    }

    @Test 
    public void testBind() {
        SAXSource emps = new SAXSource(new org.xml.sax.InputSource(new java.io.StringReader(testData)));
        Iterable<XQItem> result = new XQueryInterface(emps, "e")
                                        .query("declare variable $limit external; for $emp in $e/emps/emp where $emp/salary <= $limit return <result name='{$emp/name}' salary='{$emp/salary}'/>")
                                        .bindObject("limit", new Integer(200))
                                        .run();

        HashSet names = new HashSet();
        for (XQItem item : result) {
            names.add(getItemAttributeValue(item, "name"));
        }

        assertTrue(names.contains("Jeremy"));
        assertTrue(names.contains("John"));
        assertTrue(names.contains("Joan"));
        assertFalse(names.contains("Julie"));
        assertFalse(names.contains("Jessica"));

    }

    private static final String depts = "<depts>"+
                                        "    <deptno>1</deptno>"+
                                        "    <deptno>2</deptno>"+
                                        "    <deptno>3</deptno>"+
                                        "    <deptno>4</deptno>"+
                                        "    <deptno>5</deptno>"+
                                        "</depts>";

    private static final String testData =  "<emps>"+
                                            "    <emp active='true'>"+
                                            "        <name>John</name>"+
                                            "        <deptno>1</deptno>"+
                                            "        <salary>100</salary>"+
                                            "    </emp>"+
                                            "    <emp active='true'>"+
                                            "        <name>Joan</name>"+
                                            "        <deptno>1</deptno>"+
                                            "        <salary>100</salary>"+
                                            "    </emp>"+
                                            "    <emp active='true'>"+
                                            "        <name>Jane</name>"+
                                            "        <deptno>2</deptno>"+
                                            "        <salary>200</salary>"+
                                            "    </emp>"+
                                            "    <emp active='true'>"+
                                            "        <name>Jeremy</name>"+
                                            "        <deptno>2</deptno>"+
                                            "        <salary>200</salary>"+
                                            "    </emp>"+
                                            "    <emp active='true'>"+
                                            "        <name>Jenny</name>"+
                                            "        <deptno>3</deptno>"+
                                            "        <salary>300</salary>"+
                                            "    </emp>"+
                                            "    <emp active='true'>"+
                                            "        <name>Jessica</name>"+
                                            "        <deptno>3</deptno>"+
                                            "        <salary>300</salary>"+
                                            "    </emp>"+
                                            "    <emp active='false'>"+
                                            "        <name>Jake</name>"+
                                            "        <deptno>4</deptno>"+
                                            "        <salary>400</salary>"+
                                            "    </emp>"+
                                            "    <emp active='false'>"+
                                            "        <name>Jerome</name>"+
                                            "        <deptno>4</deptno>"+
                                            "        <salary>400</salary>"+
                                            "    </emp>"+
                                            "    <emp active='true'>"+
                                            "        <name>Julie</name>"+
                                            "        <deptno>5</deptno>"+
                                            "        <salary>500</salary>"+
                                            "    </emp>"+
                                            "</emps>";
}
