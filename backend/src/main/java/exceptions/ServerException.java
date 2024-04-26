package exceptions;

import common.HttpStatusCode;

public class ServerException extends RuntimeException {
    private int errorCode = HttpStatusCode.INTERNAL_SERVER_ERROR;

    public ServerException() {
        super("Internal Server Error");
    }

    public int getErrorCode() {
        return errorCode;
    }
}
