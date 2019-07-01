package com.github.mikeldpl.lambda.runtime;

public interface LambdaRuntimeApi {
    Invocation getNextInvocation();

    void sendInvocationError(Invocation invocation, String response);

    void sendResponse(Invocation invocation, String response);
}
