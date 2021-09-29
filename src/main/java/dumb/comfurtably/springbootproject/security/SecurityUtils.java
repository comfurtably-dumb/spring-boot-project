package dumb.comfurtably.springbootproject.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import dumb.comfurtably.springbootproject.entities.ResponseMessageEntity;
import dumb.comfurtably.springbootproject.models.User;
import dumb.comfurtably.springbootproject.queries.MongoQueries;
 
public class SecurityUtils {

    private static final String secretSalt = "secret";
    
    public static String generateSessionToken (String email, String passwordHash) throws NoSuchAlgorithmException {
        String sha256Hash = getSHA256(email + passwordHash);
        String currentTimeStampStr = Long.toString(Instant.now().getEpochSecond());
        String strForBytes = email + "$$$$$" + currentTimeStampStr + "$$$$$" + secretSalt;
        return sha256Hash + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(strForBytes.getBytes());
    }

    private static boolean hasTokenExpired (String sessionToken) {
        String[] tokenArray = sessionToken.split("\\.");
        String encodedPart = tokenArray[tokenArray.length - 1];
        String currentTimeStampStr = Long.toString(Instant.now().getEpochSecond());
        String decodedPart = new String(Base64.getUrlDecoder().decode(encodedPart));
        String[] decodedArray = decodedPart.split("$$$$$");
        return Long.parseLong(decodedArray[1]) - Long.parseLong(currentTimeStampStr) > 120000L ;
    }

    private static boolean verifySecretSalt (String sessionToken) {
        String[] tokenArray = sessionToken.split("\\.");
        String encodedPart = tokenArray[tokenArray.length - 1];
        String decodedPart = new String(Base64.getUrlDecoder().decode(encodedPart));
        String[] decodedArray = decodedPart.split("$$$$$");
        return decodedArray[2] == secretSalt;
    }

    public static String getUserEmail (String sessionToken) {
        String[] tokenArray = sessionToken.split("\\.");
        String encodedPart = tokenArray[tokenArray.length - 1];
        String decodedPart = new String(Base64.getUrlDecoder().decode(encodedPart));
        String[] decodedArray = decodedPart.split("$$$$$");
        return decodedArray[0];
    }

    private static String getSHA256 (String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return new String(bytes);
    }

    public static String isUserLoggedIn (String sessionToken) throws NoSuchAlgorithmException {
        String email = getUserEmail(sessionToken);
        MongoQueries mongoQueries = new MongoQueries();
        User loggedInUser = mongoQueries.getUserBySessionToken(sessionToken);
        if (loggedInUser != null) {
            if (loggedInUser.getEmail().equals(email)) {
                if (verifySecretSalt(sessionToken)) {
                    if (hasTokenExpired(sessionToken)) {
                        String newSessionToken = generateSessionToken(loggedInUser.getEmail(), loggedInUser.getPasswordHash());
                        mongoQueries.updateSessionTokenByUserId(loggedInUser.getId(), newSessionToken);
                        return newSessionToken;
                    }
                    return sessionToken;
                }
                return null;
            }
            return null;
        }
        return null;
    }

    public static ResponseEntity<ResponseMessageEntity> getMessageResponseLoggedIn (String message, String sessionToken, String newSessionToken) throws NoSuchAlgorithmException {
        ResponseMessageEntity responseMessage = new ResponseMessageEntity();
        responseMessage.setMessage(message);
        if (newSessionToken.equals(sessionToken)) {
            return new ResponseEntity<ResponseMessageEntity>(responseMessage, HttpStatus.OK);
        }
        HttpHeaders header = new HttpHeaders();
        header.add("Set-Cookie", "jrsession=" + newSessionToken + "; Secure; HttpOnly");
        return new ResponseEntity<ResponseMessageEntity>(responseMessage, header, HttpStatus.OK);
    }

    public static ResponseEntity<ResponseMessageEntity> getMessageResponseLoggedIn (String message) {
        ResponseMessageEntity responseMessage = new ResponseMessageEntity();
        responseMessage.setMessage(message);
        return new ResponseEntity<ResponseMessageEntity>(responseMessage, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ResponseMessageEntity> getMessageResponseNotLoggedIn () {
        ResponseMessageEntity responseMessage = new ResponseMessageEntity();
        responseMessage.setMessage("Access Denied: User not logged in");
        return new ResponseEntity<ResponseMessageEntity>(responseMessage, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ResponseMessageEntity> getMessageResponseNotLoggedIn (String message) {
        ResponseMessageEntity responseMessage = new ResponseMessageEntity();
        responseMessage.setMessage(message);
        return new ResponseEntity<ResponseMessageEntity>(responseMessage, HttpStatus.BAD_REQUEST);
    }

}
