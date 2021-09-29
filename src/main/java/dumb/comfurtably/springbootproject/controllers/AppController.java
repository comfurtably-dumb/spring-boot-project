package dumb.comfurtably.springbootproject.controllers;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import dumb.comfurtably.springbootproject.entities.RequestArticleEntity;
import dumb.comfurtably.springbootproject.entities.ResponseAuthorEntity;
import dumb.comfurtably.springbootproject.entities.ResponseMessageEntity;
import dumb.comfurtably.springbootproject.models.Article;
import dumb.comfurtably.springbootproject.models.User;
import dumb.comfurtably.springbootproject.queries.MongoQueries;
import dumb.comfurtably.springbootproject.security.SecurityUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
public class AppController {

    @Autowired
    private MongoQueries mongoQueries;
    
    @GetMapping("/home")
    public String getHomePage () {
        return "Home Page";
    }

    @GetMapping("/read")
    public List<Article> getArticles () {
        return this.mongoQueries.getAllArticles();
    }

    @GetMapping("/read/{articleId}")
    public Article getArticle (@PathVariable final String articleId) {
        int intArticleId = Integer.valueOf(articleId);
        return this.mongoQueries.getArticleById(intArticleId);
    }

    @GetMapping("/authors")
    public List<ResponseAuthorEntity> getAllAuthorsPage () { 
        return this.mongoQueries.getAllAuthors();
    }

    @GetMapping("/authors/{authorId}")
    public ResponseAuthorEntity getAuthor (@PathVariable final String authorId) {
        int intAuthorId = Integer.valueOf(authorId);
        return this.mongoQueries.getAuthorById(intAuthorId);
    }

    @PostMapping("/create-article")
    public ResponseEntity<ResponseMessageEntity> submitCreateArticle (@RequestHeader(name = "jrsession", required = true) final String sessionToken, RequestArticleEntity createdArticle) throws NoSuchAlgorithmException {
        String newSessionToken = SecurityUtils.isUserLoggedIn(sessionToken);
        if (newSessionToken == null) {
            return SecurityUtils.getMessageResponseNotLoggedIn();
        }
        User loggedInUser = mongoQueries.getUserBySessionToken(newSessionToken);
        String authorName = loggedInUser.getUserName();
        if (authorName == null) {
            authorName = loggedInUser.getFirstName() + " " + loggedInUser.getLastName();
        }
        Date currentDateTime = new Date();
        Article articleToCreate = new Article();
        articleToCreate.setCreatedTime(currentDateTime);
        articleToCreate.setLastEdited(currentDateTime);
        articleToCreate.setArticle(createdArticle.getArticle());
        articleToCreate.setAuthorId(loggedInUser.getId());
        articleToCreate.setAuthorName(authorName);
        articleToCreate.setNsfw(createdArticle.getNsfw());
        List<Integer> articleIds = loggedInUser.getArticles();
        articleIds.add(articleToCreate.getId());
        this.mongoQueries.insertArticle(articleToCreate);
        this.mongoQueries.updateArticleIdListByUserId(loggedInUser.getId(), articleIds);
        return SecurityUtils.getMessageResponseLoggedIn("Article Created", sessionToken, newSessionToken);
    }

    @PutMapping("/edit-article/{articleId}")
    public ResponseEntity<ResponseMessageEntity> submitEditArticle (@PathVariable final String articleId, @RequestHeader(name = "jrsession", required = true) final String sessionToken, @RequestBody final RequestArticleEntity editArticle) throws NoSuchAlgorithmException {
        String newSessionToken = SecurityUtils.isUserLoggedIn(sessionToken);
        if (newSessionToken == null) {
            return SecurityUtils.getMessageResponseNotLoggedIn();
        }
        int intArticleId = Integer.valueOf(articleId);
        this.mongoQueries.updateArticleById(intArticleId, editArticle.getArticle());
        return SecurityUtils.getMessageResponseLoggedIn("Article Edited", sessionToken, newSessionToken);
    }

    @DeleteMapping("/delete-article/{articleId}")
    public ResponseEntity<ResponseMessageEntity> deleteArticle (@PathVariable final String articleId, @RequestHeader(name = "jrsession", required = true) final String sessionToken) throws NoSuchAlgorithmException {
        String newSessionToken = SecurityUtils.isUserLoggedIn(sessionToken);
        if (newSessionToken == null) {
            return SecurityUtils.getMessageResponseNotLoggedIn();
        }
        int intArticleId = Integer.valueOf(articleId);
        this.mongoQueries.deleteArticleById(intArticleId);
        return SecurityUtils.getMessageResponseLoggedIn("Article Deleted", sessionToken, newSessionToken);
    }

}
