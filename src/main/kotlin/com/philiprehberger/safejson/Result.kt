package com.philiprehberger.safejson

/**
 * A lightweight Result type for safe JSON operations.
 *
 * @param T the success value type
 * @param E the error value type
 */
public sealed interface Result<out T, out E> {

    /**
     * Represents a successful result.
     *
     * @property value the success value
     */
    public data class Ok<out T>(public val value: T) : Result<T, Nothing>

    /**
     * Represents an error result.
     *
     * @property error the error value
     */
    public data class Err<out E>(public val error: E) : Result<Nothing, E>
}

/**
 * Returns `true` if this result is [Result.Ok].
 */
public val <T, E> Result<T, E>.isOk: Boolean
    get() = this is Result.Ok

/**
 * Returns `true` if this result is [Result.Err].
 */
public val <T, E> Result<T, E>.isErr: Boolean
    get() = this is Result.Err

/**
 * Returns the success value, or `null` if this is an error.
 */
public fun <T, E> Result<T, E>.getOrNull(): T? = when (this) {
    is Result.Ok -> value
    is Result.Err -> null
}

/**
 * Returns the success value, or the result of [default] if this is an error.
 */
public inline fun <T, E> Result<T, E>.getOrElse(default: (E) -> T): T = when (this) {
    is Result.Ok -> value
    is Result.Err -> default(error)
}
