package com.philiprehberger.safejson

import kotlinx.serialization.json.*

/**
 * A wrapper around [JsonElement] providing safe, path-based navigation with typed errors.
 *
 * Supports dot-notation for nested objects (e.g. `"user.name"`) and bracket notation
 * for array access (e.g. `"[0].id"` or `"users[2].name"`).
 *
 * @property element the underlying [JsonElement]
 */
public class JsonNode(public val element: JsonElement) {

    /**
     * Navigates to the element at the given path and extracts it as a [String].
     *
     * @param path dot-notation path (e.g. `"user.name"`, `"items[0].title"`)
     * @return [Result.Ok] with the string value, or a [JsonError] on failure
     */
    public fun string(path: String): Result<String, JsonError> {
        return navigate(path).let { result ->
            when (result) {
                is Result.Err -> result
                is Result.Ok -> {
                    val el = result.value
                    if (el is JsonPrimitive && el.isString) {
                        Result.Ok(el.content)
                    } else if (el is JsonPrimitive) {
                        Result.Err(JsonError.TypeMismatch(path, "String", describeType(el)))
                    } else {
                        Result.Err(JsonError.TypeMismatch(path, "String", describeType(el)))
                    }
                }
            }
        }
    }

    /**
     * Navigates to the element at the given path and extracts it as an [Int].
     *
     * @param path dot-notation path
     * @return [Result.Ok] with the int value, or a [JsonError] on failure
     */
    public fun int(path: String): Result<Int, JsonError> {
        return navigate(path).let { result ->
            when (result) {
                is Result.Err -> result
                is Result.Ok -> {
                    val el = result.value
                    if (el is JsonPrimitive) {
                        val intVal = el.intOrNull
                        if (intVal != null) {
                            Result.Ok(intVal)
                        } else {
                            Result.Err(JsonError.TypeMismatch(path, "Int", describeType(el)))
                        }
                    } else {
                        Result.Err(JsonError.TypeMismatch(path, "Int", describeType(el)))
                    }
                }
            }
        }
    }

    /**
     * Navigates to the element at the given path and extracts it as a [Long].
     *
     * @param path dot-notation path
     * @return [Result.Ok] with the long value, or a [JsonError] on failure
     */
    public fun long(path: String): Result<Long, JsonError> {
        return navigate(path).let { result ->
            when (result) {
                is Result.Err -> result
                is Result.Ok -> {
                    val el = result.value
                    if (el is JsonPrimitive) {
                        val longVal = el.longOrNull
                        if (longVal != null) {
                            Result.Ok(longVal)
                        } else {
                            Result.Err(JsonError.TypeMismatch(path, "Long", describeType(el)))
                        }
                    } else {
                        Result.Err(JsonError.TypeMismatch(path, "Long", describeType(el)))
                    }
                }
            }
        }
    }

    /**
     * Navigates to the element at the given path and extracts it as a [Boolean].
     *
     * @param path dot-notation path
     * @return [Result.Ok] with the boolean value, or a [JsonError] on failure
     */
    public fun boolean(path: String): Result<Boolean, JsonError> {
        return navigate(path).let { result ->
            when (result) {
                is Result.Err -> result
                is Result.Ok -> {
                    val el = result.value
                    if (el is JsonPrimitive) {
                        val boolVal = el.booleanOrNull
                        if (boolVal != null) {
                            Result.Ok(boolVal)
                        } else {
                            Result.Err(JsonError.TypeMismatch(path, "Boolean", describeType(el)))
                        }
                    } else {
                        Result.Err(JsonError.TypeMismatch(path, "Boolean", describeType(el)))
                    }
                }
            }
        }
    }

    /**
     * Navigates to the element at the given path and extracts it as a [Double].
     *
     * @param path dot-notation path
     * @return [Result.Ok] with the double value, or a [JsonError] on failure
     */
    public fun double(path: String): Result<Double, JsonError> {
        return navigate(path).let { result ->
            when (result) {
                is Result.Err -> result
                is Result.Ok -> {
                    val el = result.value
                    if (el is JsonPrimitive) {
                        val doubleVal = el.doubleOrNull
                        if (doubleVal != null) {
                            Result.Ok(doubleVal)
                        } else {
                            Result.Err(JsonError.TypeMismatch(path, "Double", describeType(el)))
                        }
                    } else {
                        Result.Err(JsonError.TypeMismatch(path, "Double", describeType(el)))
                    }
                }
            }
        }
    }

    /**
     * Navigates to the element at the given path and returns it as a [String],
     * or [default] if the path is not found or the type doesn't match.
     *
     * @param path dot-notation path
     * @param default the fallback value
     * @return the string value at [path], or [default]
     */
    public fun stringOrDefault(path: String, default: String): String {
        return when (val result = string(path)) {
            is Result.Ok -> result.value
            is Result.Err -> default
        }
    }

    /**
     * Navigates to the element at the given path and returns it as an [Int],
     * or `null` if the path is not found or the type doesn't match.
     *
     * @param path dot-notation path
     * @return the int value at [path], or `null`
     */
    public fun intOrNull(path: String): Int? {
        return when (val result = int(path)) {
            is Result.Ok -> result.value
            is Result.Err -> null
        }
    }

    /**
     * Navigates the JSON tree to the element at the given [path].
     *
     * @param path dot-notation path with optional array indices
     * @return [Result.Ok] with the [JsonElement], or [JsonError.PathNotFound]
     */
    internal fun navigate(path: String): Result<JsonElement, JsonError> {
        val segments = parsePath(path)
        var current: JsonElement = element

        for (segment in segments) {
            current = when (segment) {
                is PathSegment.Key -> {
                    if (current is JsonObject) {
                        current[segment.name] ?: return Result.Err(JsonError.PathNotFound(path))
                    } else {
                        return Result.Err(JsonError.PathNotFound(path))
                    }
                }
                is PathSegment.Index -> {
                    if (current is JsonArray && segment.index in current.indices) {
                        current[segment.index]
                    } else {
                        return Result.Err(JsonError.PathNotFound(path))
                    }
                }
            }
        }

        return Result.Ok(current)
    }

    private fun describeType(element: JsonElement): String = when (element) {
        is JsonObject -> "Object"
        is JsonArray -> "Array"
        is JsonPrimitive -> when {
            element.isString -> "String"
            element.booleanOrNull != null -> "Boolean"
            element.longOrNull != null -> "Number"
            element.doubleOrNull != null -> "Number"
            element.content == "null" -> "Null"
            else -> "Primitive"
        }
    }

    private sealed interface PathSegment {
        data class Key(val name: String) : PathSegment
        data class Index(val index: Int) : PathSegment
    }

    private fun parsePath(path: String): List<PathSegment> {
        val segments = mutableListOf<PathSegment>()
        var i = 0
        val len = path.length

        while (i < len) {
            if (path[i] == '[') {
                // Array index
                val end = path.indexOf(']', i)
                if (end == -1) break
                val index = path.substring(i + 1, end).toIntOrNull() ?: break
                segments.add(PathSegment.Index(index))
                i = end + 1
                if (i < len && path[i] == '.') i++ // skip dot after ]
            } else {
                // Object key
                val dotPos = path.indexOf('.', i)
                val bracketPos = path.indexOf('[', i)
                val end = when {
                    dotPos == -1 && bracketPos == -1 -> len
                    dotPos == -1 -> bracketPos
                    bracketPos == -1 -> dotPos
                    else -> minOf(dotPos, bracketPos)
                }
                if (end > i) {
                    segments.add(PathSegment.Key(path.substring(i, end)))
                }
                i = if (end < len && path[end] == '.') end + 1 else end
            }
        }

        return segments
    }
}
