package dataAccessObject;

/**
 * Created by Admin on 2/16/17.
 */

public class databaseException extends Exception {

    public databaseException(String s) {
        super(s);
    }

    public databaseException(String s, Throwable throwable) {
        super(s, throwable);
    }
}