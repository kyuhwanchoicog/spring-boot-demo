package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

public class DateTimeCursorTest {

  @Test
  public void should_convert_to_string_as_millis() {
    DateTime dateTime = new DateTime(1234567890000L, DateTimeZone.UTC);
    DateTimeCursor cursor = new DateTimeCursor(dateTime);
    assertEquals("1234567890000", cursor.toString());
  }

  @Test
  public void should_parse_millis_string_to_datetime() {
    DateTime result = DateTimeCursor.parse("1234567890000");
    assertNotNull(result);
    assertEquals(1234567890000L, result.getMillis());
    assertEquals(DateTimeZone.UTC, result.getZone());
  }

  @Test
  public void should_return_null_when_parsing_null() {
    DateTime result = DateTimeCursor.parse(null);
    assertNull(result);
  }

  @Test
  public void should_get_data_from_cursor() {
    DateTime dateTime = new DateTime(1000L, DateTimeZone.UTC);
    DateTimeCursor cursor = new DateTimeCursor(dateTime);
    assertEquals(dateTime, cursor.getData());
  }
}
