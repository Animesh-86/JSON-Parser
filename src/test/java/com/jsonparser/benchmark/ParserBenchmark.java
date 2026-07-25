package com.jsonparser.benchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jsonparser.core.Parser;
import com.jsonparser.streaming.ZeroAllocStreamParser;
import org.openjdk.jmh.annotations.*;

import java.io.StringReader;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class ParserBenchmark {

    private String json;
    private ObjectMapper jacksonMapper;
    private Gson gson;

    @Setup
    public void setup() {
        json = "[{\"id\": 1, \"name\": \"Test 1\", \"active\": true}, " +
               "{\"id\": 2, \"name\": \"Test 2\", \"active\": false}, " +
               "{\"id\": 3, \"name\": \"Test 3\", \"active\": true}]";
        jacksonMapper = new ObjectMapper();
        gson = new Gson();
    }

    @Benchmark
    public Object customParser() {
        Parser parser = new Parser(json);
        return parser.parse();
    }

    @Benchmark
    public Object customZeroAllocStream() throws Exception {
        int[] count = {0};
        try (ZeroAllocStreamParser parser = new ZeroAllocStreamParser(new StringReader(json), (type, buf, start, len) -> {
            count[0]++; // Just count to avoid dead code elimination
        })) {
            parser.parse();
        }
        return count[0];
    }

    @Benchmark
    public Object jacksonParser() throws Exception {
        return jacksonMapper.readTree(json);
    }

    @Benchmark
    public Object gsonParser() {
        return gson.fromJson(json, Object.class);
    }
}
