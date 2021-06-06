package com.github.frtu.logs.core.benchmark;

import com.github.frtu.logs.core.StructuredLogger;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static com.github.frtu.logs.core.RpcLogger.requestBody;
import static com.github.frtu.logs.core.RpcLogger.uri;

@Slf4j
@BenchmarkMode(Mode.All)
@Fork(value = 2, jvmArgs = {"-Xms100M", "-Xmx100M"})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class StructuredLoggerBenchmark {
    private StructuredLogger structuredLogger;

    @Setup
    public void setup() {
        structuredLogger = StructuredLogger.create(LOGGER);
    }

    public static enum Status {
        INIT, SUCCESFUL, FAILED
    }

    @State(Scope.Thread)
    public static class Request {
        public String url = "http://localhost:8080/event";
        public int count = 0;
        public Status status = Status.INIT;
    }

    @Benchmark
    public void regularLogger(Request request) {
        LOGGER.info("{\"request\":{}}", request.url);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void structuredLogger(Request request) {
        structuredLogger.info(uri(request.url));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void structuredLoggerJson(Request request) {
        structuredLogger.info(requestBody(request.url));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StructuredLoggerBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
