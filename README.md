# JSON Parser

A robust and extensible JSON parsing library for Java, designed to handle both standard and complex JSON operations efficiently. This library goes beyond simple parsing, offering advanced capabilities such as querying, schema validation, diffing, streaming, and indexing.

## Key Highlights

- **Query Engine:** Navigate nested JSON structures easily with JSONPath-like queries using `JsonQuery`.
- **Schema Validation:** Enforce JSON structure and data types with `JsonValidator`, ensuring data correctness.
- **JSON Diffing:** Detect and display differences between two JSON objects efficiently using `JsonDiff`.
- **Event-Driven Streaming Parser:** Process large JSON files without loading them entirely into memory via `JsonStreamParser`.
- **Fast Indexing:** Quickly access JSON values by path using `JsonIndexer`, enabling O(1) lookups.
- **Rich Data Model:** Handle all JSON types—objects, arrays, strings, numbers, booleans, and nulls—through the flexible `JsonValue` hierarchy.
- **Extensible & Modular:** Clean architecture with separate packages for core parsing, querying, validation, diffing, streaming, and indexing, making it easy to extend or integrate into other projects.

## Features

- **Standard JSON Parsing:** Parse JSON strings into Java objects.
- **Query Support:** JSONPath-like queries for nested objects and arrays.
- **Schema Validation:** Validate JSON data against custom schemas.
- **Diffing:** Compare two JSON objects and get detailed differences.
- **Streaming Parsing:** Event-driven parsing for memory-efficient processing of large JSON files.
- **Indexing:** Fast retrieval of JSON values by path.

### Installation

Clone the repository:

```sh
git clone https://github.com/yourusername/json-parser.git
cd json-parser
```

Compile source files:

```sh
javac -d bin src/core/*.java src/query/*.java src/schema/*.java src/diff/*.java src/streaming/*.java src/index/*.java
```

## Usage

### Parsing JSON

```java
import core.Parser;
import core.JsonValue;

String json = "{\"name\":\"Animesh\",\"age\":20}";
Parser parser = new Parser(json);
JsonValue value = parser.parse();
```

### Querying

```java
import query.JsonQuery;

JsonQuery query = new JsonQuery(value);
JsonValue name = query.query("$.name");
```

### Schema Validation

```java
import schema.JsonValidator;
import java.util.Map;

Map<String, Object> schema = Map.of(
    "name", "string",
    "age", "number"
);

JsonValidator.validate(value, schema);
```

### Diffing

```java
import diff.JsonDiff;

JsonDiff.DiffResult result = JsonDiff.diff(json1, json2);
System.out.println(result.toPrettyString());
```

### Streaming

```java
import streaming.JsonStreamParser;
import streaming.PrintEventHandler;
import java.io.FileReader;

JsonStreamParser parser = new JsonStreamParser(
    new FileReader("sample.json"),
    new PrintEventHandler(),
    null  // null for no key filtering
);
parser.parse();
```

### Indexing

```java
import index.JsonIndexer;

JsonIndexer indexer = new JsonIndexer(value);
JsonValue age = indexer.get("$.age");
```

## Project Structure

- `src/core/` — Core JSON model and parser
- `src/query/` — Query engine
- `src/schema/` — Schema validation
- `src/diff/` — Diffing utilities
- `src/streaming/` — Streaming/event-based parser
- `src/index/` — Indexing support
- `test/` — Unit tests

## Running Tests

Run JUnit 5 tests via the command line:

```sh
javac -d bin src/core/*.java src/query/*.java src/schema/*.java src/diff/*.java src/streaming/*.java src/index/*.java test/*.java
java -cp bin org.junit.platform.console.ConsoleLauncher --scan-class-path
```

Or use your IDE’s built-in test runner.

## License

This project is licensed under the MIT License.

## Author

**Animesh Sharma**  
Passionate about Java, backend development, and AI/ML integration  
GitHub: [https://github.com/Animesh-86](https://github.com/Animesh-86)  
LinkedIn: [https://www.linkedin.com/in/animesh-sharma-adev](https://www.linkedin.com/in/animesh-sharma-adev)

