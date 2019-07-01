package com.github.mikeldpl.lambda.runtime.aws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.github.mikeldpl.lambda.runtime.Invocation;
import com.github.mikeldpl.lambda.runtime.LambdaRuntimeApi;
import com.github.mikeldpl.lambda.runtime.LambdaRuntimeException;


public class AwsLambdaRuntimeApi implements LambdaRuntimeApi {

    private static final String DEFAULT_MEDIA_TYPE = "application/json";
    private static final int DEFAULT_TIMEOUT_MILLIS = 5000;

    private final String awsLambdaRuntimeApi;

    public AwsLambdaRuntimeApi() {
        awsLambdaRuntimeApi = System.getenv("AWS_LAMBDA_RUNTIME_API");
    }

    @Override
    public Invocation getNextInvocation() {
        final String url = "http://" + awsLambdaRuntimeApi + "/2018-06-01/runtime/invocation/next";

        try {
            final HttpURLConnection connection = createConnectionObject(url);
            connection.setRequestMethod("GET");

            validateResponse(connection);
            final String responseBody = readResponseBody(connection);
            final String lambdaRuntimeAwsRequestId = connection.getHeaderField("Lambda-Runtime-Aws-Request-Id");
            final String lambdaRuntimeTraceId = connection.getHeaderField("Lambda-Runtime-Trace-Id");
            final String lambdaRuntimeClientContext = connection.getHeaderField("Lambda-Runtime-Client-Context");
            final String lambdaRuntimeCognitoIdentity = connection.getHeaderField("Lambda-Runtime-Cognito-Identity");
            final String lambdaRuntimeDeadlineMs = connection.getHeaderField("Lambda-Runtime-Deadline-Ms");
            final String lambdaRuntimeInvokedFunctionArn = connection.getHeaderField("Lambda-Runtime-Invoked-Function-Arn");
            return new Invocation(lambdaRuntimeAwsRequestId, lambdaRuntimeTraceId, lambdaRuntimeClientContext, lambdaRuntimeCognitoIdentity,
                                  lambdaRuntimeDeadlineMs, lambdaRuntimeInvokedFunctionArn, responseBody);
        } catch (IOException e) {
            throw new LambdaRuntimeException(e);
        }
    }

    @Override
    public void sendInvocationError(Invocation invocation, String requestBody) {
        final String url =
                "http://" + awsLambdaRuntimeApi + "/2018-06-01/runtime/invocation/" + invocation.getLambdaRuntimeAwsRequestId() + "/error";
        postWithoutResponse(requestBody, url);
    }

    @Override
    public void sendResponse(Invocation invocation, String requestBody) {
        final String url =
                "http://" + awsLambdaRuntimeApi + "/2018-06-01/runtime/invocation/" + invocation.getLambdaRuntimeAwsRequestId() + "/response";
        postWithoutResponse(requestBody, url);
    }

    private void postWithoutResponse(String requestBody, String url) {
        try {
            final HttpURLConnection connection = createConnectionObject(url);
            connection.setRequestMethod("POST");
            connection.addRequestProperty("content-type", DEFAULT_MEDIA_TYPE);
            connection.setDoOutput(true);

            writeRequestBody(requestBody, connection);
            validateResponse(connection);
        } catch (IOException e) {
            throw new LambdaRuntimeException(e);
        }
    }

    private void writeRequestBody(String requestBody, HttpURLConnection connection) throws IOException {
        try (final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream())) {
            outputStreamWriter.write(requestBody);
        }
    }

    private String readResponseBody(HttpURLConnection connection) throws IOException {
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            inputStream = connection.getErrorStream();
        }
        if (inputStream == null) {
            return "";
        }
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final StringBuilder stringBuilder = new StringBuilder(calculateBufferSize(connection));
            int oneChar;
            while ((oneChar = bufferedReader.read()) != -1) {
                stringBuilder.append((char) oneChar);
            }
            return stringBuilder.toString();
        }
    }

    private int calculateBufferSize(HttpURLConnection connection) {
        final int contentLength = connection.getContentLength();
        return contentLength < 0 ? 256 : contentLength;
    }


    private HttpURLConnection createConnectionObject(String path) throws IOException {
        final URL url = new URL(path);
        final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(DEFAULT_TIMEOUT_MILLIS);
        return httpURLConnection;
    }

    private void validateResponse(HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() >= 400) {
            final String responseBody = readResponseBody(connection);
            throw new LambdaRuntimeException(connection.getResponseCode() + " response from url: " + connection.getURL() + " body: " + responseBody);
        }
    }
}
