import java.io.*;
import java.util.Objects;
import java.util.Scanner;

/**
 * 4/14/16
 * EmailServer
 */

public class EmailServer {
    // Useful constants
    public static final String FAILURE = "FAILURE";
    public static final String DELIMITER = "\t";
    public static final String SUCCESS = "SUCCESS";
    public static final String CRLF = "\r\n";

    // Used to print out extra information
    private boolean verbose = false;
    User[] users = new User[100];
    int counter = 1;
    int userlocation = 0;

    public EmailServer() {
        User root = new User("root", "cs180");
        users[0] = root;

    }

    public EmailServer(String filename) throws IOException {

    }


    public boolean userConfirmed(String username) {
        for (int i = 0; i < users.length; i++) {
            if (users[i] == null)
                break;
            if (users[i].getName().equals(username)) {
                userlocation = i;
                return true;
            }
        }
        return false;
    }

    public String addUser(String[] args) {
        users[counter] = new User(args[1], args[2]);
        counter++;
        return SUCCESS + CRLF;
    }

    public String getAllUsers(String[] args) {
        String swer = "";
        for (int i = 0; i < users.length; i++) {
            if (users[i] == null)
                break;
            swer += users[i].getName() + DELIMITER;
        }
        swer = swer.substring(0, swer.length() - 1);
        return SUCCESS + DELIMITER + swer + CRLF;
    }

    public String deleteUser(String[] args) {
        if (args[1].equals("root"))
            return ErrorFactory.makeErrorMessage(-23);
        for (int i = 0; i < users.length; i++) {
            if (Objects.equals(users[i], args[1])) {
                users[i] = users[i + 1];
            }
        }
        counter--;
        return SUCCESS + CRLF;
    }

    public String sendEmail(String[] args) {
        if (userConfirmed(args[3])) {
            users[userlocation].receiveEmail(args[1], args[4]);
            return SUCCESS + CRLF;
        } else return ErrorFactory.makeErrorMessage(-20);
    }

    public String getEmails(String[] args) {
        Scanner sc = new Scanner(args[3]);
        if (sc.hasNextInt()) {
            return SUCCESS + users[userlocation].retrieveEmail(Integer.parseInt(args[3])) + CRLF;
        } else return ErrorFactory.makeErrorMessage(-23);
    }

    public String deleteEmail(String[] args) {
        if (userConfirmed(args[1])) {
            try {
                if (users[userlocation].removeEmail(Integer.parseInt(args[3]))) {
                    return SUCCESS + CRLF;
                } else {
                    return ErrorFactory.makeErrorMessage(-23);
                }
            } catch (Exception e) {
                return ErrorFactory.makeErrorMessage(-23);
            }
        }
        return ErrorFactory.makeErrorMessage(-11);
    }


    public void run() {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.printf("Input Server Request: ");
            String command = in.nextLine();

            command = replaceEscapeChars(command);

            if (command.equalsIgnoreCase("kill") || command.equalsIgnoreCase("kill\r\n"))
                break;

            if (command.equalsIgnoreCase("verbose") || command.equalsIgnoreCase("verbose\r\n")) {
                verbose = !verbose;
                System.out.printf("VERBOSE has been turned %s.\n\n", verbose ? "on" : "off");
                continue;
            }

            String response = null;
            try {
                response = parseRequest(command);
            } catch (Exception ex) {
                response = ErrorFactory.makeErrorMessage(ErrorFactory.UNKNOWN_ERROR,
                        String.format("An exception of %s occurred.", ex.getClass().toString()));
            }
            // change the formatting of the server response so it prints well on the terminal (for testing purposes onl
            //if (response.startsWith("SUCCESS" + DELIMITER))
            //	response = response.replace(DELIMITER, NEWLINE);
            if (response.startsWith(FAILURE) && !DELIMITER.equals("\t"))
                response = response.replace(DELIMITER, "\t");

            if (verbose)
                System.out.print("response: ");
            System.out.printf("\"%s\"\n\n", response);
        }

        in.close();
    }

    /**
     * Determines which client command the request is using and calls
     * the function associated with that command.
     *
     * @param request - the full line of the client request (CRLF included)
     * @return the server response
     */
    public String parseRequest(String request) {
        //example client request: "GET-ALL-USERS\troot\tcs180\r\n"
        //example Email: SEND-EMAIL\tsender\tpassword\trecipient\tMessage!\r\n
        String[] parameters = request.split(DELIMITER);
        if (parameters.length < 2 || (!parameters[parameters.length - 1].contains("\r\n"))) {
            return ErrorFactory.makeErrorMessage(-10);
        }
        parameters[parameters.length - 1] = parameters[parameters.length - 1].substring(0,
                parameters[parameters.length - 1].length() - 2);
        switch (parameters[0]) {
            case "ADD-USER":
                if (parameters.length == 3) {
                    if (parameters[1].equals("root")) {
                        return ErrorFactory.makeErrorMessage(-22);
                    } else {
                        if (!userConfirmed(parameters[1])) {
                            if (User.validUsername(parameters[1]) &&
                                    User.validPassword(parameters[2])) {
                                return addUser(parameters);
                            } else {
                                return ErrorFactory.makeErrorMessage(-23);
                            }
                        } else {
                            return ErrorFactory.makeErrorMessage(-22);
                        }
                    }
                } else {
                    return ErrorFactory.makeErrorMessage(-10);
                }
            case "GET-ALL-USERS":
                if (parameters.length == 3) {
                    if (userConfirmed(parameters[1])) {
                        //if (User[tba] )
                        if (User.validUsername(parameters[1]) &&
                                User.validPassword(parameters[2])) {
                            return getAllUsers(parameters);
                        } else {
                            return ErrorFactory.makeErrorMessage(-23);
                        }
                    } else {
                        return ErrorFactory.makeErrorMessage(-22);
                    }
                } else {
                    return ErrorFactory.makeErrorMessage(-10);
                }
            case "DELETE-USER":
                if (parameters[1].equals("root")) {
                    return ErrorFactory.makeErrorMessage(-23);
                }
                if (parameters.length == 3) {
                    if (userConfirmed(parameters[1])) {
                        if (users[userlocation].checkPassword(parameters[2])) {
                            return deleteEmail(parameters);
                        } else {
                            return ErrorFactory.makeErrorMessage(-21);
                        }
                    } else {
                        return ErrorFactory.makeErrorMessage(-20);
                    }
                } else {
                    return ErrorFactory.makeErrorMessage(-10);
                }
            case "SEND-EMAIL":
                if (parameters.length == 5) {
                    if (users[userlocation].checkPassword(parameters[2])) {
                        if (Email.validEmail(parameters[4])) {
                            return sendEmail(parameters);
                        } else {
                            return ErrorFactory.makeErrorMessage(-23);
                        }
                    } else {
                        return ErrorFactory.makeErrorMessage(-23);
                    }
                } else {
                    return ErrorFactory.makeErrorMessage(-10);
                }
            case "GET-EMAILS":
                if (parameters.length == 4) {
                    if (users[userlocation].checkPassword(parameters[2])) {
                        return getEmails(parameters);
                    } else {
                        return ErrorFactory.makeErrorMessage(-23);
                    }
                } else {
                    return ErrorFactory.makeErrorMessage(-10);
                }
            case "DELETE-EMAIL":
                if (parameters.length == 4) {
                    if (users[userlocation].checkPassword(parameters[2])) {

                    } else {
                        return ErrorFactory.makeErrorMessage(-23);
                    }
                } else {
                    return ErrorFactory.makeErrorMessage(-10);
                }
            default:
                return ErrorFactory.makeErrorMessage(-11);

        }
    }

    /**
     * Replaces "poorly formatted" escape characters with their proper
     * values. For some terminals, when escaped characters are
     * entered, the terminal includes the "\" as a character instead
     * of entering the escape character. This function replaces the
     * incorrectly inputed characters with their proper escaped
     * characters.
     *
     * @param str - the string to be edited
     * @return the properly escaped string
     */
    private static String replaceEscapeChars(String str) {
        str = str.replace("\\r\\n", "\r\n"); // may not be necessary, but just in case
        str = str.replace("\\r", "\r");
        str = str.replace("\\n", "\n");
        str = str.replace("\\t", "\t");
        str = str.replace("\\f", "\f");

        return str;
    }

    /**
     * This main method is for testing purposes only.
     *
     * @param args - the command line arguments
     */
    public static void main(String[] args) {

        (new EmailServer()).run();

    }
}

