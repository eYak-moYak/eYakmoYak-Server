package capstone.eYakmoYak.auth.response;

public enum AuthenticationErrorCode {
    EMPTY_AUTHENTICATION("Empty Authentication"),
    INVALID_TOKEN("Invalid Token");

    private final String message;

    AuthenticationErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
