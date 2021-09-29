package dumb.comfurtably.springbootproject.models;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Articles")
public class Article {

    @Id
    private int id;
    private String article;
    private int authorId;
    private String authorName;
    private boolean nsfw;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastEdited;
    @Transient
    private static int lastId = 0;

    //Constructor
    public Article () {
        lastId++;
        this.id = lastId;
    }

    //Getters and setters
    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }
    
    public String getArticle () {
        return article;
    }

    public void setArticle (String article) {
        this.article = article;
    }

    public int getAuthorId () {
        return authorId;
    }

    public void setAuthorId (int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName () {
        return authorName;
    }

    public void setAuthorName (String authorName) {
        this.authorName = authorName;
    }

    public boolean getNsfw () {
        return this.nsfw;
    }

    public void setNsfw (boolean nsfw) {
        this.nsfw = nsfw;
    }

    public Date getCreatedTime () {
        return createdTime;
    }

    public void setCreatedTime (Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getLastEdited () {
        return lastEdited;
    }

    public void setLastEdited (Date lastEdited) {
        this.lastEdited = lastEdited;
    }
    
}
