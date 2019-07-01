package com.github.mikeldpl.lambda.runtime;

public final class LambdaRuntime {

    private final LambdaRuntimeApi lambdaRuntimeApi;
    private final Handler handler;
    private final ErrorHandler errorHandler;
    private final int retriesCount;

    public LambdaRuntime(LambdaRuntimeApi lambdaRuntimeApi, Handler handler, ErrorHandler errorHandler) {
        this(lambdaRuntimeApi, handler, errorHandler, 3);
    }

    public LambdaRuntime(LambdaRuntimeApi lambdaRuntimeApi, Handler handler, ErrorHandler errorHandler, int retriesCount) {
        this.lambdaRuntimeApi = lambdaRuntimeApi;
        this.handler = handler;
        this.errorHandler = errorHandler;
        this.retriesCount = retriesCount;
    }

    public void run() {
        while (true) {
            final Invocation invocation = getNextInvocation();
            long startTime = System.currentTimeMillis();
            try {
                String result = handler.handle(invocation);
                System.err.println("Actual execution time: " + (System.currentTimeMillis() - startTime));
                lambdaRuntimeApi.sendResponse(invocation, result);
            } catch (Exception e) {
                logException(e, "Exception on handling invocation: " + invocation);
                final String errorResponse = errorHandler.handle(invocation, e);
                lambdaRuntimeApi.sendInvocationError(invocation, errorResponse);
            }
        }
    }

    private Invocation getNextInvocation() {
        for (int i = 0; i < retriesCount; i++) {
            try {
                return lambdaRuntimeApi.getNextInvocation();
            } catch (Exception e) {
                logException(e, "getNextInvocation request was not successful. tryToGetNextCount: " + i);
            }
        }
        throw new LambdaRuntimeException("Exhausted all retries. This is probably a bug!");
    }

    private void logException(Exception e, String message) {
        System.err.println(message);
        e.printStackTrace(System.err);
    }

}
