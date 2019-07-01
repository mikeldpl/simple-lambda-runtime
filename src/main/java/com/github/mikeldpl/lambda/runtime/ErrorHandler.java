package com.github.mikeldpl.lambda.runtime;

@FunctionalInterface
public interface ErrorHandler {
    String handle(Invocation invocation, Throwable e);
}
