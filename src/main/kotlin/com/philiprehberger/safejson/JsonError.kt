package com.philiprehberger.safejson

/**
 * Represents errors that can occur during JSON parsing or path navigation.
 */
public sealed interface JsonError {

    /**
     * The JSON input could not be parsed.
     *
     * @property message the parser error message
     */
    public data class ParseError(public val message: String) : JsonError

    /**
     * The requested path does not exist in the JSON document.
     *
     * @property path the path that was not found
     */
    public data class PathNotFound(public val path: String) : JsonError

    /**
     * The value at the requested path is not the expected type.
     *
     * @property path the path where the mismatch occurred
     * @property expected the expected type name
     * @property actual the actual type name
     */
    public data class TypeMismatch(public val path: String, public val expected: String, public val actual: String) : JsonError
}
