package io.spring.application.article;

import io.spring.application.ArticleQueryService;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.MyBatisArticleFavoriteRepository;
import io.spring.infrastructure.repository.MyBatisArticleRepository;
import io.spring.infrastructure.repository.MyBatisUserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({
  ArticleCommandService.class,
  ArticleQueryService.class,
  MyBatisArticleRepository.class,
  MyBatisUserRepository.class,
  MyBatisArticleFavoriteRepository.class
})
public class ArticleCommandServiceTest extends DbTestBase {

  @Autowired private ArticleCommandService articleCommandService;

  @Autowired private ArticleRepository articleRepository;

  @Autowired private UserRepository userRepository;

  private User user;

  @BeforeEach
  public void setUp() {
    user = new User("test@test.com", "testuser", "123", "", "");
    userRepository.save(user);
  }

  @Test
  public void should_create_article_successfully() {
    NewArticleParam param =
        NewArticleParam.builder()
            .title("Test Article")
            .description("Test description")
            .body("Test body")
            .tagList(Arrays.asList("java", "spring"))
            .build();

    Article article = articleCommandService.createArticle(param, user);

    Assertions.assertNotNull(article);
    Assertions.assertNotNull(article.getId());
    Assertions.assertEquals("Test Article", article.getTitle());
    Assertions.assertEquals("Test description", article.getDescription());
    Assertions.assertEquals("Test body", article.getBody());
    Assertions.assertEquals("test-article", article.getSlug());
    Assertions.assertEquals(user.getId(), article.getUserId());
    Assertions.assertEquals(2, article.getTags().size());

    Optional<Article> saved = articleRepository.findById(article.getId());
    Assertions.assertTrue(saved.isPresent());
  }

  @Test
  public void should_create_article_with_empty_tag_list() {
    NewArticleParam param =
        NewArticleParam.builder()
            .title("No Tags Article")
            .description("desc")
            .body("body")
            .tagList(Collections.emptyList())
            .build();

    Article article = articleCommandService.createArticle(param, user);

    Assertions.assertNotNull(article);
    Assertions.assertEquals(0, article.getTags().size());
  }

  @Test
  public void should_update_article_title() {
    NewArticleParam createParam =
        NewArticleParam.builder()
            .title("Original Title")
            .description("desc")
            .body("body")
            .tagList(Collections.emptyList())
            .build();

    Article article = articleCommandService.createArticle(createParam, user);

    UpdateArticleParam updateParam = new UpdateArticleParam("Updated Title", "", "");
    Article updated = articleCommandService.updateArticle(article, updateParam);

    Assertions.assertEquals("Updated Title", updated.getTitle());
    Assertions.assertEquals("updated-title", updated.getSlug());
  }

  @Test
  public void should_update_article_description() {
    NewArticleParam createParam =
        NewArticleParam.builder()
            .title("Title")
            .description("old desc")
            .body("body")
            .tagList(Collections.emptyList())
            .build();

    Article article = articleCommandService.createArticle(createParam, user);

    UpdateArticleParam updateParam = new UpdateArticleParam("", "", "new description");
    Article updated = articleCommandService.updateArticle(article, updateParam);

    Assertions.assertEquals("new description", updated.getDescription());
  }

  @Test
  public void should_update_article_body() {
    NewArticleParam createParam =
        NewArticleParam.builder()
            .title("Title")
            .description("desc")
            .body("old body")
            .tagList(Collections.emptyList())
            .build();

    Article article = articleCommandService.createArticle(createParam, user);

    UpdateArticleParam updateParam = new UpdateArticleParam("", "new body", "");
    Article updated = articleCommandService.updateArticle(article, updateParam);

    Assertions.assertEquals("new body", updated.getBody());
  }

  @Test
  public void should_update_article_all_fields() {
    NewArticleParam createParam =
        NewArticleParam.builder()
            .title("Title")
            .description("desc")
            .body("body")
            .tagList(Collections.emptyList())
            .build();

    Article article = articleCommandService.createArticle(createParam, user);

    UpdateArticleParam updateParam =
        new UpdateArticleParam("New Title", "new body", "new description");
    Article updated = articleCommandService.updateArticle(article, updateParam);

    Assertions.assertEquals("New Title", updated.getTitle());
    Assertions.assertEquals("new body", updated.getBody());
    Assertions.assertEquals("new description", updated.getDescription());
  }

  @Test
  public void should_create_article_with_tags_and_verify_persistence() {
    NewArticleParam param =
        NewArticleParam.builder()
            .title("Persistent Article")
            .description("desc")
            .body("body")
            .tagList(Arrays.asList("tag1", "tag2", "tag3"))
            .build();

    Article article = articleCommandService.createArticle(param, user);

    Optional<Article> found = articleRepository.findBySlug(article.getSlug());
    Assertions.assertTrue(found.isPresent());
    Assertions.assertEquals(article.getId(), found.get().getId());
  }

  @Test
  public void should_persist_updated_article() {
    NewArticleParam createParam =
        NewArticleParam.builder()
            .title("Before Update")
            .description("desc")
            .body("body")
            .tagList(Collections.emptyList())
            .build();

    Article article = articleCommandService.createArticle(createParam, user);
    String originalId = article.getId();

    UpdateArticleParam updateParam = new UpdateArticleParam("After Update", "new body", "new desc");
    articleCommandService.updateArticle(article, updateParam);

    Optional<Article> found = articleRepository.findById(originalId);
    Assertions.assertTrue(found.isPresent());
    Assertions.assertEquals("After Update", found.get().getTitle());
    Assertions.assertEquals("after-update", found.get().getSlug());
  }
}
