package dumb.comfurtably.springbootproject.entities;

public class RequestLoginEntity {

    private String email;
    private String passwordHash;
    
    //Getters and setters
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
    
}
