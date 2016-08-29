import java.util.Date;

/**
 * CS 180 - Project 4 (Reggie Hopton and Omar Raza)
 * Reggie - L02
 * Omar - L03
 * 4/14/16
 * Email
 */
public class Email {
    private String recipient;
    private String sender;
    private long id;
    private String message;
    private Date date;

    public Email(String recipient, String sender, long id, String message) {
        this.recipient = recipient;
        this.sender = sender;
        this.id = id;
        this.message = message;
        date = new Date();

    }

    public long getID() {
        return id;
    }

    public String getOwner() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {

        return id + ";" + date.toString() + ";" + " From: " + sender + " " + "\"" + message + "\"";

    }

    public static boolean validEmail(String email) {
        if (email.charAt(0) == ' ') {
            email = email.trim();
        }
        if (email.charAt(email.length() - 1) == ' ') {
            email = email.trim();
        }
        if (email.contains("\t")) {
            return false;
        }
        if (email.length() == 1 && email.charAt(0) == ' ') {
            return false;
        }
        if (email.length() < 1) {
            return false;
        }
        return true;
    }

}
