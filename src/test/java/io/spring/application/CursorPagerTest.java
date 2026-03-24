package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.application.CursorPager.Direction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CursorPagerTest {

  private static class TestNode implements Node {
    private final PageCursor cursor;

    TestNode(String cursorValue) {
      this.cursor =
          new PageCursor<String>(cursorValue) {
            @Override
            public String toString() {
              return cursorValue;
            }
          };
    }

    @Override
    public PageCursor getCursor() {
      return cursor;
    }
  }

  @Test
  public void should_set_next_true_when_direction_next_and_has_extra() {
    List<TestNode> data = Arrays.asList(new TestNode("a"), new TestNode("b"));
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.NEXT, true);

    assertTrue(pager.hasNext());
    assertFalse(pager.hasPrevious());
  }

  @Test
  public void should_set_next_false_when_direction_next_and_no_extra() {
    List<TestNode> data = Arrays.asList(new TestNode("a"));
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.NEXT, false);

    assertFalse(pager.hasNext());
    assertFalse(pager.hasPrevious());
  }

  @Test
  public void should_set_previous_true_when_direction_prev_and_has_extra() {
    List<TestNode> data = Arrays.asList(new TestNode("a"), new TestNode("b"));
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.PREV, true);

    assertFalse(pager.hasNext());
    assertTrue(pager.hasPrevious());
  }

  @Test
  public void should_set_previous_false_when_direction_prev_and_no_extra() {
    List<TestNode> data = Arrays.asList(new TestNode("a"));
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.PREV, false);

    assertFalse(pager.hasNext());
    assertFalse(pager.hasPrevious());
  }

  @Test
  public void should_return_start_cursor_from_first_element() {
    List<TestNode> data = Arrays.asList(new TestNode("first"), new TestNode("last"));
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.NEXT, false);

    assertNotNull(pager.getStartCursor());
    assertEquals("first", pager.getStartCursor().toString());
  }

  @Test
  public void should_return_end_cursor_from_last_element() {
    List<TestNode> data = Arrays.asList(new TestNode("first"), new TestNode("last"));
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.NEXT, false);

    assertNotNull(pager.getEndCursor());
    assertEquals("last", pager.getEndCursor().toString());
  }

  @Test
  public void should_return_null_cursors_when_data_is_empty() {
    List<TestNode> data = new ArrayList<>();
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.NEXT, false);

    assertNull(pager.getStartCursor());
    assertNull(pager.getEndCursor());
  }

  @Test
  public void should_have_both_direction_enum_values() {
    Direction[] values = Direction.values();
    assertEquals(2, values.length);
    assertEquals(Direction.PREV, Direction.valueOf("PREV"));
    assertEquals(Direction.NEXT, Direction.valueOf("NEXT"));
  }
}
