package com.philiprehberger.safejson

/**
 * Represents errors that can occur during JSON parsing or path navigation.
 */
sealed interface JsonError {

    /**
     * The JSON input could not be parsed.
     *
     * @property message the parser error message
     */
    data class ParseError(val message: String) : JsonError

    /**
     * The requested path does not exist in the JSON document.
     *
     * @property path the path that was not found
     */
    data class PathNotFound(val path: String) : JsonError

    /**
     * The value at the requested path is not the expected type.
     *
     * @property path the path where the mismatch occurred
     * @property expected the expected type name
     * @property actual the actual type name
     */
    data class TypeMismatch(val path: String, val expected: String, val actual: String) : JsonError
}
