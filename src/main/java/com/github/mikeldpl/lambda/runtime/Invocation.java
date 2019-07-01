package com.github.mikeldpl.lambda.runtime;

import lombok.Data;

@Data
public class Invocation {
    /**
     * Lambda-Runtime-Aws-Request-Id:
     *     description: AWS request ID associated with the request.
     */
    private final String lambdaRuntimeAwsRequestId;
    /**
     * Lambda-Runtime-Trace-Id:
     *     description: X-Ray tracing header.
     */
    private final String lambdaRuntimeTraceId;
    /**
     * Lambda-Runtime-Client-Context:
     *     description: Information about the client application and device when invoked through the AWS Mobile SDK.
     */
    private final String lambdaRuntimeClientContext;
    /**
     * Lambda-Runtime-Cognito-Identity:
     *     description: Information about the Amazon Cognito identity provider when invoked through the AWS Mobile SDK.
     */
    private final String lambdaRuntimeCognitoIdentity;
    /**
     * Lambda-Runtime-Deadline-Ms:
     *     description: Function execution deadline counted in milliseconds since the Unix epoch.
     */
    private final String lambdaRuntimeDeadlineMs;
    /**
     * Lambda-Runtime-Invoked-Function-Arn:
     *     description: The ARN requested. This can be different in each invoke that executes the same version.
     */
    private final String lambdaRuntimeInvokedFunctionArn;
    /**
     * Event payload
     */
    private final String body;

}
