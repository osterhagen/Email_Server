
public class User {
    private String username;
    private String password;
    private Email[] inbox;
    private int iD = 0;
    DynamicBuffer d = new DynamicBuffer(10);

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        inbox = new Email[10];

    }
    public String getName() {
        return this.username;
    }
    public boolean checkPassword(String password) {
        if (password == null || this.password == null) {
            return false;
        }
        if (password.equals(this.password)) {
            return true;
        } else {
            return false;
        }
    }
    public int numEmail() {
        return d.numElements();
    }
    public void receiveEmail(String sender, String message) {
        d.add(new Email(username, sender, (long) iD, message));
        iD++;
    }
    public Email[] retrieveEmail(int n) {
        return d.getNewest(n);
    }
    public boolean removeEmail(long emailID) {
        /**
         for (int i = 0; i < d.numElements(); i++) {
         if (emailID == d.getNewest(i)[0].getID()) {
         d.remove(i);
         return true;
         }
         }
         return false;
         **/
        for (int i = 0; i < d.numElements(); i++) {
            if (emailID == d.getNewest(d.numElements())[i].getID()) {
                d.remove(i);
                return true;
            }
        }
        return false;
    }
    public static boolean validUsername(String username) {
        String[] usernameArray = username.split("");
        if (usernameArray.length <= 1 || usernameArray.length >= 20) {
            return false;
        }
        return username.matches("[A-Za-z0-9]+");
    }
    public static boolean validPassword(String password) {
        String[] passwordArray = password.split("");
        if (passwordArray.length <= 4 || passwordArray.length >= 40) {
            return false;
        }
        return password.matches("[A-Za-z0-9]+");
    }
}
