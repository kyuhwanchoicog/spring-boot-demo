package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.application.CursorPager.Direction;
import org.junit.jupiter.api.Test;

public class CursorPageParameterTest {

  @Test
  public void should_return_true_for_isNext_when_direction_is_next() {
    CursorPageParameter<String> param = new CursorPageParameter<>("cursor", 10, Direction.NEXT);
    assertTrue(param.isNext());
  }

  @Test
  public void should_return_false_for_isNext_when_direction_is_prev() {
    CursorPageParameter<String> param = new CursorPageParameter<>("cursor", 10, Direction.PREV);
    assertFalse(param.isNext());
  }

  @Test
  public void should_return_limit_plus_one_for_query_limit() {
    CursorPageParameter<String> param = new CursorPageParameter<>("cursor", 10, Direction.NEXT);
    assertEquals(11, param.getQueryLimit());
  }

  @Test
  public void should_cap_limit_at_max() {
    CursorPageParameter<String> param = new CursorPageParameter<>("cursor", 2000, Direction.NEXT);
    assertEquals(1000, param.getLimit());
  }

  @Test
  public void should_use_default_limit_for_zero_or_negative() {
    CursorPageParameter<String> param = new CursorPageParameter<>("cursor", 0, Direction.NEXT);
    assertEquals(20, param.getLimit());

    CursorPageParameter<String> param2 = new CursorPageParameter<>("cursor", -1, Direction.NEXT);
    assertEquals(20, param2.getLimit());
  }

  @Test
  public void should_use_default_values_with_no_arg_constructor() {
    CursorPageParameter<String> param = new CursorPageParameter<>();
    assertEquals(20, param.getLimit());
    assertNull(param.getCursor());
    assertNull(param.getDirection());
  }
}
