package jinqs.sql;

import java.sql.*;
import java.util.*;
import jinqs.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

public class SQLQueryInterfaceTest {
    
    private Connection conn;

    @Before
    public void setUp() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "spr", "password");
        } catch (Exception se) {
            throw new RuntimeException(se);
        }
    }

    @After
    public void tearDown() {
        try {
            conn.close();
        } catch (Exception se) {
            throw new RuntimeException(se);
        }
    }


    @Test
    public void testNoParams() {
        Iterable<ResultSet> queryResult = SQLQueryInterface.withConnection(conn)
                                                           .query("select wrkr_ref_nbr from wrkr where wrkr_std_rglr_hrs = 480 and gcc_id = 10000018390978")
                                                           .run();
        ResultSet r = queryResult.iterator().next();
        try
        {
            assertEquals("checking id", 5, r.getInt("wrkr_ref_nbr"));
        }
        catch (SQLException s)
        {
            s.printStackTrace();
            fail("sqlexception thrown: " + s.getMessage());
        }
    }

    @Test
    public void testSingleParam() {
        Iterable<ResultSet> queryResult = SQLQueryInterface.withConnection(conn)
                                                           .query("select wrkr_std_rglr_hrs from wrkr where wrkr_ref_nbr = :wrkrId and gcc_id = 10000018390978")
                                                           .bind("wrkrId", 5)
                                                           .run();
        ResultSet r = queryResult.iterator().next();
        try
        {
            assertEquals(480, r.getInt("wrkr_std_rglr_hrs"));
        }
        catch (SQLException s)
        {
            s.printStackTrace();
            fail("sqlexception thrown: " + s.getMessage());
        }
    }

    @Test 
    public void testRepeatedParam() {
        Iterable<ResultSet> queryResult = SQLQueryInterface.withConnection(conn)
                                                           .query("select count(*) from wrkr w join wrkr_pay_cpnt wpc on W.WRKR_ID = WPC.WRKPC_WRKR_ID where w.gcc_id = :gccid and wpc.gcc_id = :gccid and w.wrkr_ref_nbr = :wrn")
                                                           .bind("wrn", 5)
                                                           .bind("gccid", 10000018390978L)
                                                           .run();
        ResultSet r = queryResult.iterator().next();
        try
        {
            assertEquals(3, r.getInt(1));
        }
        catch (SQLException s)
        {
            s.printStackTrace();
            fail("sqlexception thrown: " + s.getMessage());
        }
    }
}
