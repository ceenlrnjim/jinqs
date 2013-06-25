package jinqs.xml;

import org.junit.Test;
import static org.junit.Assert.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xquery.*;
import java.util.*;

public class XQueryInterfaceTest {

    @Test
    public void testNoParam() {
        SAXSource src = new SAXSource(new org.xml.sax.InputSource(new java.io.StringReader(testData)));
        Iterable<XQItem> result = new XQueryInterface(src, "n")
                                        .query("for $e in $n/emps/emp\nlet $name := $e/name\nwhere $e/salary > 250\nreturn <earner name='{$name}'>{$e}</earner>")
                                        .run();
        HashSet names = new HashSet();

        try {
            for (XQItem item : result) {
                names.add(item.getNode().getAttributes().getNamedItem("name").getNodeValue());
            }

            assertTrue(names.contains("Jenny"));
            assertTrue(names.contains("Jake"));
            assertTrue(names.contains("Jerome"));
            assertTrue(names.contains("Julie"));
            assertTrue(names.contains("Jessica"));
        } catch (XQException xe) {
            xe.printStackTrace();
            fail(xe.getMessage());
        }
    }

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
