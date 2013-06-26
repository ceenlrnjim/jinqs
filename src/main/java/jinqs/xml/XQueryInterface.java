package jinqs.xml;

import java.util.*;

//import net.sf.saxon.s9api.*;
import javax.xml.transform.Source;
import net.sf.saxon.xqj.SaxonXQDataSource;
import net.sf.saxon.Configuration;
import javax.xml.xquery.*;
import javax.xml.namespace.QName;

public class XQueryInterface {

    private String queryString;
    private List<Source> sources = new LinkedList<Source>();
    private List<String> sourceNames = new LinkedList<String>();
    private Map<String,Object> bindValues = new HashMap<String,Object>();

    public XQueryInterface() {
    }

    /**
     * @param source the xml document to be queries
     * @param varName the name of the external xquery variable that the document should be set to for referring to it in the query string
     */
    public XQueryInterface(Source source, String varName) {
        this.sources.add(source);
        this.sourceNames.add(varName);
    }

    public XQueryInterface addSource(Source source, String name) {
        sources.add(source);
        sourceNames.add(name);
        return this;
    }

    /** 
     * The query to be executed - the interface will automatically declare
     * an external variable of the name specified in the constructor before executing this
     * query
     */
    public XQueryInterface query(String q) {
        queryString = q; 
        return this;
    }

    // TODO: other types
    public XQueryInterface bindObject(String name, Object value) {
        bindValues.put(name, value);
        return this;
    }
    
    /**
     * Utility method for accessing attributes of XQItems 
     */
    public static String getItemAttributeValue(XQItem item, String attributeName) {
        try {
            return item.getNode().getAttributes().getNamedItem(attributeName).getNodeValue();
        } catch (XQException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterable<XQItem> run() {
        try {
            // Saxon S9 api
            //Processor proc = new Processor(false);
            //DocumentBuilder bldr = proc.newDocumentBuilder();
            //XdmNode doc = bldr.build(source);
            //XQueryCompiler compiler = proc.newXQueryCompiler();
            //XQueryExecutable executable = compiler.compile("declare variable $" + documentVarName + " external; " + queryString);
            //XQueryEvaluator evaler = executable.load();
            //evaler.setExternalVariable(new QName(documentVarName), doc);
            //return evaler;
            StringBuilder bldr = new StringBuilder();
            
            Configuration cfg = new Configuration();
            SaxonXQDataSource ds = new SaxonXQDataSource(cfg);
            // TODO: connection handling
            XQConnection conn = ds.getConnection();

            for (int i=0;i<sources.size();i++) {
                bldr.append("declare variable $").append(sourceNames.get(i)).append(" external; ");
            }
            bldr.append(queryString);
            
            XQPreparedExpression xqp = conn.prepareExpression(bldr.toString());
            for (int i=0;i<sources.size();i++) {
                xqp.bindDocument(new QName(sourceNames.get(i)), sources.get(i), null);
            }
            for (Map.Entry<String,Object> e : bindValues.entrySet()) {
                xqp.bindObject(new QName(e.getKey()), e.getValue(), null);
            }

            XQResultSequence seq = xqp.executeQuery();

            return new IterableXQSequence(seq);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
