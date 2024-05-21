package capstone.eYakmoYak.auth.response;

public enum BaseResponseStatus {
    INVALID_JWT("Invalid JWT Token"),
    EMPTY_JWT("Empty JWT Token");

    private final String message;

    BaseResponseStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

