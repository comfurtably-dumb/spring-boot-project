package dumb.comfurtably.springbootproject.queries;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import dumb.comfurtably.springbootproject.entities.ResponseAuthorEntity;
import dumb.comfurtably.springbootproject.models.Article;
import dumb.comfurtably.springbootproject.models.User;

public class MongoQueries {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Article> getAllArticles () {
        Query query = new Query();
        query = query.with(Sort.by("lastEdited").descending());
        return this.mongoTemplate.find(query, Article.class, "Articles");
    }

    public Article getArticleById (int id) {
        Query query = new Query();
        query = query.addCriteria(Criteria.where("id").is(id));
        return this.mongoTemplate.findOne(query, Article.class, "Articles");
    }

    public List<ResponseAuthorEntity> getAllAuthors () {
        Query query = new Query();
        query = query.with(Sort.by("joiningDate").descending());
        List<User> users = this.mongoTemplate.find(query, User.class, "Users");
        List<ResponseAuthorEntity> authors = new ArrayList<ResponseAuthorEntity>();
        for (User user : users) {
            ResponseAuthorEntity author = new ResponseAuthorEntity();
            author.transformUser(user);
            authors.add(author);
        }
        return authors;
    }

    public ResponseAuthorEntity getAuthorById (int id) {
        Query query = new Query();
        query = query.addCriteria(Criteria.where("id").is(id));
        query.fields().exclude("email", "passwordHash", "dateOfBirth", "sessionToken", "resetToken");
        User user = this.mongoTemplate.findOne(query, User.class, "Users");
        ResponseAuthorEntity author = new ResponseAuthorEntity();
        author.transformUser(user);
        return author;
    }

    public User getUserBySessionToken (String sessionToken) {
        Query query = new Query();
        query = query.addCriteria(Criteria.where("sessionToken").is(sessionToken));
        List<User> users = this.mongoTemplate.find(query, User.class, "Users");
        User loggedInUser = null;
        for (User user : users) {
            if (user.getSessionToken() == sessionToken) {
                loggedInUser = user;
            }
        }
        return loggedInUser;
    }

    public void updateSessionTokenByUserId (int id, String sessionToken) {
        Query query = new Query();
        query = query.addCriteria(Criteria.where("id").is(id));
        User user = this.mongoTemplate.findOne(query, User.class, "Users");
        if (user == null) {
            return;
        }
        Update update = new Update();
        update = update.set("sessionToken", sessionToken);
        this.mongoTemplate.upsert(query, update, User.class, "Users");
    }

    public void updateArticleIdListByUserId (int id, List<Integer> newArticleIds) {
        Query query = new Query();
        query = query.addCriteria(Criteria.where("id").is(id));
        Update update = new Update();
        update = update.set("articles", newArticleIds);
        this.mongoTemplate.upsert(query, update, User.class, "Users");
    }

    public void insertArticle (Article article) {
        this.mongoTemplate.insert(article, "Articles");
    }

    public void updateArticleById (int id, String newArticle) {
        Query query = new Query();
        query = query.addCriteria(Criteria.where("id").is(id));
        Update update = new Update();
        update = update.set("article", newArticle);
        this.mongoTemplate.upsert(query, update, Article.class, "Articles");
    }

    public void deleteArticleById (int id) {
        Query query = new Query();
        query = query.addCriteria(Criteria.where("id").is(id));
        this.mongoTemplate.findAndRemove(query, Article.class, "Articles");
    }

    public void loginUserByEmail (String sessionToken, String email) {
        Query query = new Query();
        query = query.addCriteria(Criteria.where("email").is(email));
        Update update = new Update();
        update = update.set("sessionToken", sessionToken);
        this.mongoTemplate.upsert(query, update, User.class, "Users");
    }

    public void insertUser (User user) {
        this.mongoTemplate.insert(user, "Users");
    }

    public boolean checkUserByUserName (String userName) {
        Query query = new Query();
        query = query.addCriteria(Criteria.where("userName").is(userName));
        List<User> users = this.mongoTemplate.find(query, User.class, "Users");
        return users.size() != 0;
    }

    public boolean checkUSerByEmail (String email) {
        Query query = new Query();
        query = query.addCriteria(Criteria.where("email").is(email));
        List<User> users = this.mongoTemplate.find(query, User.class, "Users");
        return users.size() != 0;
    }

    public User getUserById (int id) {
        Query query = new Query();
        query = query.addCriteria(Criteria.where("id").is(id));
        return this.mongoTemplate.findOne(query, User.class, "Users");
    }

    public void updateUser (User user) {
        this.mongoTemplate.save(user, "Users");
    }

    public void updatePasswordByUserId (int id, String newPasswordHash) {
        Query query = new Query();
        query = query.addCriteria(Criteria.where("id").is(id));
        Update update = new Update();
        update = update.set("passwordHash", newPasswordHash);
        this.mongoTemplate.upsert(query, update, User.class, "Users");
    }

    public void deleteUserbyId (int id) {
        Query query = new Query();
        query = query.addCriteria(Criteria.where("id").is(id));
        this.mongoTemplate.findAndRemove(query, User.class, "Users");
    }
    
}
