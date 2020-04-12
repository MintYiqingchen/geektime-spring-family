package geektime.spring.data.simplejdbcdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Repository
public class BatchFooDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void batchInsert() {
        final Iterator<Integer> iter = Stream.iterate(1, x -> (x % 5) + 1).limit(15).iterator();
        jdbcTemplate.batchUpdate("INSERT INTO FOO (BAR) VALUES (?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, "b-" + iter.next());
                    }

                    @Override
                    public int getBatchSize() {
                        return 15;
                    }  // JDBCTemplate根据这个值决定循环的大小
                });

//        List<Foo> list = new ArrayList<>();
//        list.add(Foo.builder().id(100L).bar("b-100").build());
//        list.add(Foo.builder().id(101L).bar("b-101").build());
//        namedParameterJdbcTemplate
//                .batchUpdate("INSERT INTO FOO (ID, BAR) VALUES (:id, :bar)",
//                        SqlParameterSourceUtils.createBatch(list));
    }
}
