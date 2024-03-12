package dataAccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    private final Integer statusCode;
    public DataAccessException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    public Integer StatusCode() {
        return this.statusCode;
    }
}
