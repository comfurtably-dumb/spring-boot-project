package dumb.comfurtably.springbootproject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import dumb.comfurtably.springbootproject.entities.RequestEmailEntity;
import dumb.comfurtably.springbootproject.entities.RequestLoginEntity;
import dumb.comfurtably.springbootproject.entities.RequestPasswordHashEntity;
import dumb.comfurtably.springbootproject.entities.RequestSignUpEntity;
import dumb.comfurtably.springbootproject.entities.RequestUserEntity;
import dumb.comfurtably.springbootproject.entities.RequestUserNameEntity;
import dumb.comfurtably.springbootproject.entities.ResponseBooleanEntity;
import dumb.comfurtably.springbootproject.entities.ResponseMessageEntity;
import dumb.comfurtably.springbootproject.models.User;
import dumb.comfurtably.springbootproject.queries.MongoQueries;
import dumb.comfurtably.springbootproject.security.SecurityUtils;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@RestController
public class AccountSessionController {

    @Autowired
    private MongoQueries mongoQueries;

    @PostMapping("/login")
    public ResponseEntity<ResponseMessageEntity> submitLogin (@RequestHeader(name = "jrsession", required = false) final String sessionToken, @RequestBody final RequestLoginEntity requestBody) throws NoSuchAlgorithmException {
        String newSessionToken = SecurityUtils.isUserLoggedIn(sessionToken);
        if (newSessionToken != null) {
            return SecurityUtils.getMessageResponseLoggedIn("Already Logged In", sessionToken, newSessionToken);
        }
        String email = requestBody.getEmail();
        String passwordHash = requestBody.getPasswordHash();
        newSessionToken = SecurityUtils.generateSessionToken(email, passwordHash);
        this.mongoQueries.loginUserByEmail(newSessionToken, email);
        return SecurityUtils.getMessageResponseLoggedIn("Logged In", sessionToken, newSessionToken);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ResponseMessageEntity> submitSignUp (@RequestHeader(name = "jrsession", required = false) final String sessionToken, @RequestBody final RequestSignUpEntity requestBody) throws ParseException, NoSuchAlgorithmException {
        String newSessionToken = SecurityUtils.isUserLoggedIn(sessionToken);
        if (newSessionToken != null) {
            return SecurityUtils.getMessageResponseLoggedIn("Already Logged In", sessionToken, newSessionToken);
        }
        if (this.mongoQueries.checkUserByUserName(requestBody.getUserName()) || this.mongoQueries.checkUSerByEmail(requestBody.getEmail())) {
            return SecurityUtils.getMessageResponseNotLoggedIn("Username or Email already exists in the system");
        }
        User user = new User();
        user.setFirstName(requestBody.getFirstName());
        user.setLastName(requestBody.getLastName());
        user.setUserName(requestBody.getUserName());
        user.setEmail(requestBody.getEmail());
        user.setPasswordHash(requestBody.getPasswordHash());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date dateOfBirth = formatter.parse(requestBody.getDateOfBirth());
        user.setDateOfBirth(dateOfBirth);
        user.setJoiningDate(new Date());
        user.setArticles(new ArrayList<Integer>());
        newSessionToken = SecurityUtils.generateSessionToken(user.getEmail(), user.getPasswordHash());
        user.setSessionToken(newSessionToken);
        this.mongoQueries.insertUser(user);
        return SecurityUtils.getMessageResponseLoggedIn("Signed Up", sessionToken, newSessionToken);
    }

    @PostMapping("/verify-username")
    public ResponseEntity<ResponseBooleanEntity> submitUserName (@RequestBody final RequestUserNameEntity requestBody) {
        String userName = requestBody.getUserName();
        ResponseBooleanEntity userNameExists = new ResponseBooleanEntity();
        userNameExists.setExistance(this.mongoQueries.checkUserByUserName(userName));
        return new ResponseEntity<ResponseBooleanEntity>(userNameExists, HttpStatus.OK);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ResponseBooleanEntity> submitEmailForVerification (@RequestHeader(name = "jrsession", required = false) final String sessionToken, @RequestBody() final RequestEmailEntity requestBody) throws NoSuchAlgorithmException {
        String email = requestBody.getEmail();
        ResponseBooleanEntity emailExists = new ResponseBooleanEntity();
        emailExists.setExistance(this.mongoQueries.checkUSerByEmail(email));
        return new ResponseEntity<ResponseBooleanEntity>(emailExists, HttpStatus.OK);
    }

    /*@PostMapping("/forgot-password")
    public ResponseEntity<ResponseBooleanEntity> submitEmail (@RequestHeader(name = "jrsession", required = false) final String sessionToken, @RequestBody() final RequestEmailEntity requestBody) throws NoSuchAlgorithmException {
        //Check Session
        //String email = requestBody.getEmail();
        return null;
    }

    @PostMapping("/reset-password/{resetToken}")
    public String submitResetPassword (@PathVariable final String resetToken, @RequestHeader(name = "jrsession", required = false) final String sessionToken, @RequestBody final RequestPasswordHashEntity requestBody) {
        //Check Session
        //String passwordHash = requestBody.getPasswordHash();
        return "Submit Reset Password";
    }*/

    @PostMapping("/edit-profile/{userId}")
    public ResponseEntity<ResponseMessageEntity> submitEditProfile (@PathVariable final String userId, @RequestHeader(name = "jrsession", required = true) final String sessionToken, @RequestBody final RequestUserEntity requestBody) throws ParseException, NoSuchAlgorithmException {
        String newSessionToken = SecurityUtils.isUserLoggedIn(sessionToken);
        if (newSessionToken == null) {
            return SecurityUtils.getMessageResponseNotLoggedIn();
        }
        if (this.mongoQueries.checkUserByUserName(requestBody.getUserName()) || this.mongoQueries.checkUSerByEmail(requestBody.getEmail())) {
            return SecurityUtils.getMessageResponseNotLoggedIn("Username or Email already exists in the system");
        }
        int intUserId = Integer.valueOf(userId);
        User user = this.mongoQueries.getUserById(intUserId);
        if (user == null) {
            return SecurityUtils.getMessageResponseLoggedIn("Invalid User ID");
        }
        user.setFirstName(requestBody.getFirstName());
        user.setLastName(requestBody.getLastName());
        user.setUserName(requestBody.getUserName());
        user.setEmail(requestBody.getEmail());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date dateOfBirth = formatter.parse(requestBody.getDateOfBirth());
        user.setDateOfBirth(dateOfBirth);
        this.mongoQueries.updateUser(user);
        return SecurityUtils.getMessageResponseLoggedIn("Profile Updated", sessionToken, newSessionToken);
    }

    @PutMapping("/change-password/{userId}")
    public ResponseEntity<ResponseMessageEntity> submitChangePassword (@PathVariable final String userId, @RequestHeader(name = "jrsession", required = true) final String sessionToken, @RequestBody final RequestPasswordHashEntity requestBody) throws NoSuchAlgorithmException {
        String newSessionToken = SecurityUtils.isUserLoggedIn(sessionToken);
        if (newSessionToken == null) {
            return SecurityUtils.getMessageResponseNotLoggedIn();
        }
        String newPasswordHash = requestBody.getPasswordHash();
        int intUserId = Integer.valueOf(userId);
        this.mongoQueries.updatePasswordByUserId(intUserId, newPasswordHash);
        return SecurityUtils.getMessageResponseLoggedIn("Password Updated", sessionToken, newSessionToken);
    }

    @PostMapping("/logout/{userId}")
    public ResponseEntity<ResponseMessageEntity> submitLogOut (@PathVariable final String userId, @RequestHeader(name = "jrsession", required = true) final String sessionToken) throws NoSuchAlgorithmException {
        String newSessionToken = SecurityUtils.isUserLoggedIn(sessionToken);
        if (newSessionToken == null) {
            return SecurityUtils.getMessageResponseNotLoggedIn();
        }
        int intUserId = Integer.valueOf(userId);
        this.mongoQueries.updateSessionTokenByUserId(intUserId, null);
        return SecurityUtils.getMessageResponseLoggedIn("Password Updated", sessionToken, newSessionToken);
    }

    @DeleteMapping("/delete-account/{userId}")
    public ResponseEntity<ResponseMessageEntity> deleteAccount (@PathVariable final String userId, @RequestHeader(name = "jrsession", required = true) final String sessionToken) throws NoSuchAlgorithmException {
        String newSessionToken = SecurityUtils.isUserLoggedIn(sessionToken);
        if (newSessionToken == null) {
            return SecurityUtils.getMessageResponseNotLoggedIn();
        }
        int intUserId = Integer.valueOf(userId);
        this.mongoQueries.deleteUserbyId(intUserId);
        return SecurityUtils.getMessageResponseLoggedIn("Password Updated", sessionToken, newSessionToken); 
    }

}
