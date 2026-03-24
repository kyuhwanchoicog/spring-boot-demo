package io.spring.application.comment;

import io.spring.application.CommentQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.CommentData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.MyBatisArticleRepository;
import io.spring.infrastructure.repository.MyBatisCommentRepository;
import io.spring.infrastructure.repository.MyBatisUserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({
  MyBatisCommentRepository.class,
  MyBatisUserRepository.class,
  CommentQueryService.class,
  MyBatisArticleRepository.class
})
public class CommentQueryServiceTest extends DbTestBase {
  @Autowired private CommentRepository commentRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private CommentQueryService commentQueryService;

  @Autowired private ArticleRepository articleRepository;

  private User user;

  @BeforeEach
  public void setUp() {
    user = new User("aisensiy@test.com", "aisensiy", "123", "", "");
    userRepository.save(user);
  }

  @Test
  public void should_read_comment_success() {
    Comment comment = new Comment("content", user.getId(), "123");
    commentRepository.save(comment);

    Optional<CommentData> optional = commentQueryService.findById(comment.getId(), user);
    Assertions.assertTrue(optional.isPresent());
    CommentData commentData = optional.get();
    Assertions.assertEquals(commentData.getProfileData().getUsername(), user.getUsername());
  }

  @Test
  public void should_read_comments_of_article() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    User user2 = new User("user2@email.com", "user2", "123", "", "");
    userRepository.save(user2);
    userRepository.saveRelation(new FollowRelation(user.getId(), user2.getId()));

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);
    Comment comment2 = new Comment("content2", user2.getId(), article.getId());
    commentRepository.save(comment2);

    List<CommentData> comments = commentQueryService.findByArticleId(article.getId(), user);
    Assertions.assertEquals(comments.size(), 2);
  }

  @Test
  public void should_return_empty_when_find_by_id_returns_null() {
    Optional<CommentData> optional = commentQueryService.findById("non-existent-id", user);
    Assertions.assertFalse(optional.isPresent());
  }

  @Test
  public void should_read_comments_of_article_with_null_user() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);

    List<CommentData> comments = commentQueryService.findByArticleId(article.getId(), null);
    Assertions.assertEquals(1, comments.size());
  }

  @Test
  public void should_return_empty_cursor_pager_when_no_comments() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    CursorPageParameter<DateTime> page = new CursorPageParameter<>(null, 20, Direction.NEXT);
    CursorPager<CommentData> result =
        commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertTrue(result.getData().isEmpty());
    Assertions.assertFalse(result.hasNext());
    Assertions.assertFalse(result.hasPrevious());
  }

  @Test
  public void should_return_comments_with_cursor_and_following() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    User user2 = new User("user2@email.com", "user2", "123", "", "");
    userRepository.save(user2);
    userRepository.saveRelation(new FollowRelation(user.getId(), user2.getId()));

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);
    Comment comment2 = new Comment("content2", user2.getId(), article.getId());
    commentRepository.save(comment2);

    CursorPageParameter<DateTime> page = new CursorPageParameter<>(null, 20, Direction.NEXT);
    CursorPager<CommentData> result =
        commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertEquals(2, result.getData().size());
    boolean hasFollowing =
        result.getData().stream().anyMatch(c -> c.getProfileData().isFollowing());
    Assertions.assertTrue(hasFollowing);
  }

  @Test
  public void should_return_has_extra_when_more_comments_than_limit() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);
    Comment comment2 = new Comment("content2", user.getId(), article.getId());
    commentRepository.save(comment2);
    Comment comment3 = new Comment("content3", user.getId(), article.getId());
    commentRepository.save(comment3);

    CursorPageParameter<DateTime> page = new CursorPageParameter<>(null, 2, Direction.NEXT);
    CursorPager<CommentData> result =
        commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertEquals(2, result.getData().size());
    Assertions.assertTrue(result.hasNext());
  }

  @Test
  public void should_reverse_comments_when_direction_is_prev() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);
    Comment comment2 = new Comment("content2", user.getId(), article.getId());
    commentRepository.save(comment2);

    CursorPageParameter<DateTime> page = new CursorPageParameter<>(null, 20, Direction.PREV);
    CursorPager<CommentData> result =
        commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertFalse(result.getData().isEmpty());
  }

  @Test
  public void should_return_comments_with_cursor_and_null_user() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);

    CursorPageParameter<DateTime> page = new CursorPageParameter<>(null, 20, Direction.NEXT);
    CursorPager<CommentData> result =
        commentQueryService.findByArticleIdWithCursor(article.getId(), null, page);

    Assertions.assertEquals(1, result.getData().size());
  }
}
