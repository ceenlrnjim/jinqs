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
    private Source source;
    private String documentVarName;

    /**
     * @param source the xml document to be queries
     * @param varName the name of the external xquery variable that the document should be set to for referring to it in the query string
     */
    // TODO: support for querying multiple xml documents
    public XQueryInterface(Source source, String varName) {
        this.source = source;
        this.documentVarName = varName;
    }

    public XQueryInterface(Source source, String q, String varName) {
        this.source = source;
        this.queryString = q;
        this.documentVarName = varName;
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

    public XQueryInterface bind(String var, String value) {
        return new XQueryInterface(source, queryString.replace(var,value));
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
            
            // TODO: multiple xml documents?
            Configuration cfg = new Configuration();
            //cfg.addSchemaSource(source);
            SaxonXQDataSource ds = new SaxonXQDataSource(cfg);
            // TODO: connection handling
            XQConnection conn = ds.getConnection();
            XQPreparedExpression xqp = conn.prepareExpression("declare variable $" + documentVarName + " external; " + queryString);
            xqp.bindDocument(new QName(documentVarName), source, null);
            XQResultSequence seq = xqp.executeQuery();

            return new IterableXQSequence(seq);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
