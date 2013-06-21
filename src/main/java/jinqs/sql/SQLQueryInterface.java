package jinqs.sql;

import java.sql.*;
import java.util.*;
import java.util.regex.*;
import jinqs.*;

public class SQLQueryInterface {
    private static Enumerable enumerable = new NaiveEnumerable(); // TODO: dependency injection
    private Connection conn;

    private SQLQueryInterface(Connection connection) {
    }

    public static SQLQueryInterface withConnection(Connection connection) {
        return new SQLQueryInterface(connection);
    }

    // TODO: additional interface, introduces connection management
    //public static SQLQueryInterface withDataSource(DataSource ds) {
    //}

    public Query executeQuery(String query) {
        return new Query(query);
    }

    public class Query {
        private PreparedStatement stmt;
        private Map<String,Integer> indexMapping;

        public Query(String queryString) {
            try {
                int i=1;
                String regex = ":[a-zA-Z0-9]+";
                Matcher m = Pattern.compile(regex).matcher(queryString);

                indexMapping = new HashMap<String,Integer>();
                while (m.find()) {
                    indexMapping.put(m.group().substring(1), new Integer(i));
                    i++;
                }

                stmt = conn.prepareStatement(queryString.replaceAll(regex, "?"));
            } catch (SQLException sqle) {
                throw new RuntimeException(sqle);
            }
        }

        public Query(PreparedStatement stmt, Map<String,Integer> indexMapping) {
            this.stmt = stmt;
            this.indexMapping = indexMapping;
        }

        // TODO: other types
        public void bind(String var, Object o) {
            try {
                stmt.setObject(indexMapping.get(var).intValue(), o);
            } catch (SQLException sqle) {
                throw new RuntimeException(sqle);
            }
        }

        public Iterable<ResultSet> run() {
            try {
                return new IterableResultSet(stmt.executeQuery());
            } catch (SQLException sqle) {
                throw new RuntimeException(sqle);
            }
        }

        // TODO: closing RS/Statement
    }
}
