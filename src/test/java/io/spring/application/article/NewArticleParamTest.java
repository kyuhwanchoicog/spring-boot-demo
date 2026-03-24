package io.spring.application.article;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NewArticleParamTest {

  @Test
  public void should_build_new_article_param_with_all_fields() {
    List<String> tags = Arrays.asList("java", "spring");
    NewArticleParam param =
        NewArticleParam.builder()
            .title("Test Title")
            .description("Test Description")
            .body("Test Body")
            .tagList(tags)
            .build();

    Assertions.assertEquals("Test Title", param.getTitle());
    Assertions.assertEquals("Test Description", param.getDescription());
    Assertions.assertEquals("Test Body", param.getBody());
    Assertions.assertEquals(2, param.getTagList().size());
    Assertions.assertTrue(param.getTagList().contains("java"));
    Assertions.assertTrue(param.getTagList().contains("spring"));
  }

  @Test
  public void should_build_new_article_param_with_empty_tags() {
    NewArticleParam param =
        NewArticleParam.builder()
            .title("Title")
            .description("Desc")
            .body("Body")
            .tagList(Collections.emptyList())
            .build();

    Assertions.assertEquals("Title", param.getTitle());
    Assertions.assertNotNull(param.getTagList());
    Assertions.assertEquals(0, param.getTagList().size());
  }

  @Test
  public void should_build_new_article_param_with_null_tags() {
    NewArticleParam param =
        NewArticleParam.builder()
            .title("Title")
            .description("Desc")
            .body("Body")
            .tagList(null)
            .build();

    Assertions.assertEquals("Title", param.getTitle());
    Assertions.assertNull(param.getTagList());
  }

  @Test
  public void should_create_new_article_param_with_no_arg_constructor() {
    NewArticleParam param = new NewArticleParam();
    Assertions.assertNull(param.getTitle());
    Assertions.assertNull(param.getDescription());
    Assertions.assertNull(param.getBody());
    Assertions.assertNull(param.getTagList());
  }

  @Test
  public void should_create_new_article_param_with_all_args_constructor() {
    List<String> tags = Arrays.asList("tag1", "tag2");
    NewArticleParam param = new NewArticleParam("Title", "Desc", "Body", tags);

    Assertions.assertEquals("Title", param.getTitle());
    Assertions.assertEquals("Desc", param.getDescription());
    Assertions.assertEquals("Body", param.getBody());
    Assertions.assertEquals(tags, param.getTagList());
  }
}
