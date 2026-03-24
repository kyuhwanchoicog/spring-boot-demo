package io.spring.infrastructure.mybatis;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import org.apache.ibatis.type.JdbcType;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DateTimeHandlerTest {

  private DateTimeHandler handler;
  private PreparedStatement ps;
  private ResultSet rs;
  private CallableStatement cs;

  @BeforeEach
  public void setUp() {
    handler = new DateTimeHandler();
    ps = mock(PreparedStatement.class);
    rs = mock(ResultSet.class);
    cs = mock(CallableStatement.class);
  }

  @Test
  public void should_set_parameter_with_non_null_datetime() throws SQLException {
    DateTime dateTime = new DateTime(1609459200000L);

    handler.setParameter(ps, 1, dateTime, JdbcType.TIMESTAMP);

    verify(ps).setTimestamp(eq(1), any(Timestamp.class), any(Calendar.class));
  }

  @Test
  public void should_set_parameter_with_null_datetime() throws SQLException {
    handler.setParameter(ps, 1, null, JdbcType.TIMESTAMP);

    verify(ps).setTimestamp(eq(1), eq(null), any(Calendar.class));
  }

  @Test
  public void should_get_result_by_column_name_with_non_null_timestamp() throws SQLException {
    Timestamp timestamp = new Timestamp(1609459200000L);
    when(rs.getTimestamp(eq("created_at"), any(Calendar.class))).thenReturn(timestamp);

    DateTime result = handler.getResult(rs, "created_at");

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1609459200000L, result.getMillis());
  }

  @Test
  public void should_get_result_by_column_name_with_null_timestamp() throws SQLException {
    when(rs.getTimestamp(eq("created_at"), any(Calendar.class))).thenReturn(null);

    DateTime result = handler.getResult(rs, "created_at");

    Assertions.assertNull(result);
  }

  @Test
  public void should_get_result_by_column_index_with_non_null_timestamp() throws SQLException {
    Timestamp timestamp = new Timestamp(1609459200000L);
    when(rs.getTimestamp(eq(1), any(Calendar.class))).thenReturn(timestamp);

    DateTime result = handler.getResult(rs, 1);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1609459200000L, result.getMillis());
  }

  @Test
  public void should_get_result_by_column_index_with_null_timestamp() throws SQLException {
    when(rs.getTimestamp(eq(1), any(Calendar.class))).thenReturn(null);

    DateTime result = handler.getResult(rs, 1);

    Assertions.assertNull(result);
  }

  @Test
  public void should_get_result_from_callable_statement_with_non_null_timestamp()
      throws SQLException {
    Timestamp timestamp = new Timestamp(1609459200000L);
    when(cs.getTimestamp(eq(1), any(Calendar.class))).thenReturn(timestamp);

    DateTime result = handler.getResult(cs, 1);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1609459200000L, result.getMillis());
  }

  @Test
  public void should_get_result_from_callable_statement_with_null_timestamp() throws SQLException {
    when(cs.getTimestamp(eq(1), any(Calendar.class))).thenReturn(null);

    DateTime result = handler.getResult(cs, 1);

    Assertions.assertNull(result);
  }
}
