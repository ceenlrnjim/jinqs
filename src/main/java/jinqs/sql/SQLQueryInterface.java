package jinqs.sql;

import java.sql.*;
import java.util.*;
import java.util.regex.*;
import jinqs.*;

public class SQLQueryInterface {
    private static Enumerable enumerable = new NaiveEnumerable(); // TODO: dependency injection
    private Connection conn;

    private SQLQueryInterface(Connection connection) {
        conn = connection;
    }

    public static SQLQueryInterface withConnection(Connection connection) {
        return new SQLQueryInterface(connection);
    }

    // TODO: additional interface, introduces connection management
    //public static SQLQueryInterface withDataSource(DataSource ds) {
    //}

    public Query query(String query) {
        return new Query(query);
    }

    public class Query {
        private PreparedStatement stmt;
        private Map<Integer,String> indexMapping;
        private Map<String,Object> valueMapping;

        public Query(String queryString) {
            try {
                int i=1;
                String regex = ":[a-zA-Z0-9]+";
                Matcher m = Pattern.compile(regex).matcher(queryString);

                valueMapping = new HashMap<String,Object>();
                indexMapping = new HashMap<Integer,String>();
                while (m.find()) {
                    indexMapping.put(new Integer(i),m.group().substring(1));
                    i++;
                }

                String finalQuery = queryString.replaceAll(regex, "?");
                stmt = conn.prepareStatement(finalQuery);
            } catch (SQLException sqle) {
                throw new RuntimeException(sqle);
            }
        }

        // TODO: other types
        public Query bind(String var, Object o) {
            valueMapping.put(var, o);
            return this;
        }

        public Iterable<ResultSet> run() {
            try {
                for (Map.Entry<Integer,String> entry : indexMapping.entrySet()) {
                    stmt.setObject(entry.getKey().intValue(), valueMapping.get(entry.getValue()));
                }

                return new IterableResultSet(stmt.executeQuery());
            } catch (SQLException sqle) {
                throw new RuntimeException(sqle);
            }
        }

        // TODO: closing RS/Statement
    }
}
