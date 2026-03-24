package io.spring.core.service;

import io.spring.core.article.Article;
import io.spring.core.comment.Comment;
import io.spring.core.user.User;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthorizationServiceTest {

  @Test
  public void should_allow_article_author_to_write_article() {
    User user = new User("user@example.com", "user", "pass", "", "");
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());

    Assertions.assertTrue(AuthorizationService.canWriteArticle(user, article));
  }

  @Test
  public void should_deny_non_author_to_write_article() {
    User author = new User("author@example.com", "author", "pass", "", "");
    User other = new User("other@example.com", "other", "pass", "", "");
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), author.getId());

    Assertions.assertFalse(AuthorizationService.canWriteArticle(other, article));
  }

  @Test
  public void should_allow_article_author_to_write_comment() {
    User articleAuthor = new User("author@example.com", "author", "pass", "", "");
    User commenter = new User("commenter@example.com", "commenter", "pass", "", "");
    Article article =
        new Article("title", "desc", "body", Arrays.asList("java"), articleAuthor.getId());
    Comment comment = new Comment("comment body", commenter.getId(), article.getId());

    Assertions.assertTrue(AuthorizationService.canWriteComment(articleAuthor, article, comment));
  }

  @Test
  public void should_allow_comment_author_to_write_comment() {
    User articleAuthor = new User("author@example.com", "author", "pass", "", "");
    User commenter = new User("commenter@example.com", "commenter", "pass", "", "");
    Article article =
        new Article("title", "desc", "body", Arrays.asList("java"), articleAuthor.getId());
    Comment comment = new Comment("comment body", commenter.getId(), article.getId());

    Assertions.assertTrue(AuthorizationService.canWriteComment(commenter, article, comment));
  }

  @Test
  public void should_deny_unrelated_user_to_write_comment() {
    User articleAuthor = new User("author@example.com", "author", "pass", "", "");
    User commenter = new User("commenter@example.com", "commenter", "pass", "", "");
    User unrelated = new User("unrelated@example.com", "unrelated", "pass", "", "");
    Article article =
        new Article("title", "desc", "body", Arrays.asList("java"), articleAuthor.getId());
    Comment comment = new Comment("comment body", commenter.getId(), article.getId());

    Assertions.assertFalse(AuthorizationService.canWriteComment(unrelated, article, comment));
  }
}
