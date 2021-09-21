package com.github.frtu.logs.tracing.core.jaeger;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Factory for ManagedChannel, using PLAINTEXT or secured one if needed.
 */
@Slf4j
public class ManagedChannelFactory {
    public ManagedChannel managedChannel(String host, int port, ClientInterceptor[] interceptors) {
        // Create the correct ManagedChannelBuilder
        ManagedChannelBuilder<?> managedChannelBuilder = ManagedChannelBuilder.forAddress(host, port).usePlaintext();

        // Adding Interceptors
        if (interceptors != null) {
            Arrays.stream(interceptors).forEach(interceptor ->
                    LOGGER.info("Adding interceptor:{}", interceptor.getClass())
            );
            managedChannelBuilder = managedChannelBuilder.intercept(interceptors);
        } else {
            LOGGER.info("No interceptor to add");
        }
        return managedChannelBuilder.build();
    }
}
