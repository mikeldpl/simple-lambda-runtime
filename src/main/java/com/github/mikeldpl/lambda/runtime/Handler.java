package com.github.mikeldpl.lambda.runtime;

@FunctionalInterface
public interface Handler {

    String handle(Invocation invocation) throws Exception;
}
