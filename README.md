# safe-json

[![Tests](https://github.com/philiprehberger/kt-safe-json/actions/workflows/publish.yml/badge.svg)](https://github.com/philiprehberger/kt-safe-json/actions/workflows/publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.philiprehberger/safe-json.svg)](https://central.sonatype.com/artifact/com.philiprehberger/safe-json)
[![License](https://img.shields.io/github/license/philiprehberger/kt-safe-json)](LICENSE)

Non-throwing JSON parsing with typed errors and path-based navigation.

## Installation

### Gradle Kotlin DSL

```kotlin
implementation("com.philiprehberger:safe-json:0.1.5")
```

### Maven

```xml
<dependency>
    <groupId>com.philiprehberger</groupId>
    <artifactId>safe-json</artifactId>
    <version>0.1.5</version>
</dependency>
```

## Usage

```kotlin
import com.philiprehberger.safejson.*

val json = """{"user": {"name": "Alice", "age": 30, "tags": ["admin"]}}"""

when (val result = safeParseJson(json)) {
    is Result.Ok -> {
        val node = result.value
        val name = node.string("user.name")        // Result.Ok("Alice")
        val age = node.int("user.age")              // Result.Ok(30)
        val tag = node.string("user.tags[0]")       // Result.Ok("admin")
        val missing = node.string("user.email")     // Result.Err(PathNotFound("user.email"))

        // Convenience methods
        val fallback = node.stringOrDefault("user.email", "N/A")  // "N/A"
        val nullable = node.intOrNull("user.missing")              // null
    }
    is Result.Err -> {
        // result.error is JsonError.ParseError
    }
}
```

## API

| Function / Class | Description |
|---|---|
| `safeParseJson(input)` | Parses JSON string into `Result<JsonNode, JsonError>` |
| `JsonNode.string(path)` | Extracts string at path; returns `Result<String, JsonError>` |
| `JsonNode.int(path)` | Extracts int at path |
| `JsonNode.long(path)` | Extracts long at path |
| `JsonNode.boolean(path)` | Extracts boolean at path |
| `JsonNode.double(path)` | Extracts double at path |
| `JsonNode.stringOrDefault(path, default)` | Returns string or default if missing/wrong type |
| `JsonNode.intOrNull(path)` | Returns int or null if missing/wrong type |
| `JsonError.ParseError` | Malformed JSON input |
| `JsonError.PathNotFound` | Path does not exist in document |
| `JsonError.TypeMismatch` | Value at path is not the expected type |

## Development

```bash
./gradlew build
./gradlew test
```

## License

MIT
