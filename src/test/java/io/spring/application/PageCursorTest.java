package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

public class PageCursorTest {

  @Test
  public void should_return_data() {
    DateTime dateTime = new DateTime(5000L, DateTimeZone.UTC);
    DateTimeCursor cursor = new DateTimeCursor(dateTime);
    assertEquals(dateTime, cursor.getData());
  }

  @Test
  public void should_convert_to_string_via_data_toString() {
    DateTime dateTime = new DateTime(5000L, DateTimeZone.UTC);
    DateTimeCursor cursor = new DateTimeCursor(dateTime);
    // DateTimeCursor overrides toString to return millis
    assertEquals("5000", cursor.toString());
  }
}
