# safe-json

[![Tests](https://github.com/philiprehberger/kt-safe-json/actions/workflows/publish.yml/badge.svg)](https://github.com/philiprehberger/kt-safe-json/actions/workflows/publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.philiprehberger/safe-json.svg)](https://central.sonatype.com/artifact/com.philiprehberger/safe-json)
[![Last updated](https://img.shields.io/github/last-commit/philiprehberger/kt-safe-json)](https://github.com/philiprehberger/kt-safe-json/commits/main)

Non-throwing JSON parsing with typed errors and path-based navigation.

## Installation

### Gradle Kotlin DSL

```kotlin
implementation("com.philiprehberger:safe-json:0.2.0")
```

### Maven

```xml
<dependency>
    <groupId>com.philiprehberger</groupId>
    <artifactId>safe-json</artifactId>
    <version>0.2.0</version>
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

### Path Existence Check

```kotlin
if (node.exists("user.email")) {
    val email = node.string("user.email")
}
```

### Array Access

```kotlin
val items = node.array("items")
when (items) {
    is Result.Ok -> items.value.forEach { item ->
        println(item.string("name"))
    }
    is Result.Err -> println("Not an array")
}
```

### Object Keys and Size

```kotlin
val keys = node.keys("user")     // Ok(setOf("name", "age"))
val size = node.size("items")    // Ok(5) for arrays, Ok(3) for objects
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
| `JsonNode.exists(path)` | Check if a path exists |
| `JsonNode.array(path)` | Get array elements as List of JsonNodes |
| `JsonNode.keys(path)` | Get object keys at path |
| `JsonNode.size(path)` | Get element count at path |
| `JsonError.ParseError` | Malformed JSON input |
| `JsonError.PathNotFound` | Path does not exist in document |
| `JsonError.TypeMismatch` | Value at path is not the expected type |

## Development

```bash
./gradlew build
./gradlew test
```

## Support

If you find this project useful:

⭐ [Star the repo](https://github.com/philiprehberger/kt-safe-json)

🐛 [Report issues](https://github.com/philiprehberger/kt-safe-json/issues?q=is%3Aissue+is%3Aopen+label%3Abug)

💡 [Suggest features](https://github.com/philiprehberger/kt-safe-json/issues?q=is%3Aissue+is%3Aopen+label%3Aenhancement)

❤️ [Sponsor development](https://github.com/sponsors/philiprehberger)

🌐 [All Open Source Projects](https://philiprehberger.com/open-source-packages)

💻 [GitHub Profile](https://github.com/philiprehberger)

🔗 [LinkedIn Profile](https://www.linkedin.com/in/philiprehberger)

## License

[MIT](LICENSE)
