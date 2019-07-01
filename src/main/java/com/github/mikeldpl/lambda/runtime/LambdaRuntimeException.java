package com.github.mikeldpl.lambda.runtime;

public class LambdaRuntimeException extends RuntimeException {
    public LambdaRuntimeException(String message) {
        super(message);
    }

    public LambdaRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public LambdaRuntimeException(Throwable cause) {
        super(cause);
    }
}
