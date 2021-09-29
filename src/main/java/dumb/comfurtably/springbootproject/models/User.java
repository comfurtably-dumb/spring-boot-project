package dumb.comfurtably.springbootproject.models;

import java.util.Date;
import java.util.List;
//import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Users") 
public class User {
    
    @Id
    private int id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String passwordHash;
    private Date dateOfBirth;
    private Date joiningDate;
    private List<Integer> articles;
    private String sessionToken;
    private String resetToken;
    @Transient
    private static int lastId = 0;

    //Constructor
    public User () {
        lastId++;
        this.id = lastId;
    }

    //Getters & Setters
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

    public String getEmail () {
        return email;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public String getPasswordHash () {
        return passwordHash;
    }

    public void setPasswordHash (String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Date getDateOfBirth () {
        return dateOfBirth;
    }

    public void setDateOfBirth (Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getJoiningDate () {
        return joiningDate;
    }

    public void setJoiningDate (Date joiningDate) {
        this.joiningDate = joiningDate;
    }

    public List<Integer> getArticles () {
        return articles;
    }

    public void setArticles (List<Integer> articles) {
        this.articles = articles;
    }

    public String getSessionToken () {
        return sessionToken;
    }

    public void setSessionToken (String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getResetToken () {
        return resetToken;
    }

    public void setResetToken (String resetToken) {
        this.resetToken = resetToken;
    }

}
