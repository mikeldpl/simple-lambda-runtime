package com.github.mikeldpl.lambda.runtime;

import lombok.Data;

@Data
public class Invocation {
    //Lambda-Runtime-Aws-Request-Id
    private final String lambdaRuntimeAwsRequestId;
    //Lambda-Runtime-Trace-Id
    private final String lambdaRuntimeTraceId;
    private final String body;
}
