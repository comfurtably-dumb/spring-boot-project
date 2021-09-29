package dumb.comfurtably.springbootproject.entities;

import java.util.List;

import dumb.comfurtably.springbootproject.models.User;

public class ResponseAuthorEntity {

    private int id;
    private String firstName;
    private String lastName;
    private String userName;
    private List<Integer> articles;

    //Getters and setters
    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public String getFirstName () {
        return firstName;
    }

    public void setFirstName (String firstName) {
        this.firstName = firstName;
    }

    public String getLastName () {
        return lastName;
    }

    public void setLastName (String lastName) {
        this.lastName = lastName;
    }

    public String getUserName () {
        return userName;
    }

    public void setUserName (String userName) {
        this.userName = userName;
    }
    
    public List<Integer> getArticles () {
        return articles;
    }

    public void setArticles (List<Integer> articles) {
        this.articles = articles;
    }

    //Other Methods
    public void transformUser (User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.userName = user.getUserName();
        this.articles = user.getArticles();
    }
    
}
