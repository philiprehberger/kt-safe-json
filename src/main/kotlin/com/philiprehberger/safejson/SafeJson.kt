package com.philiprehberger.safejson

import kotlinx.serialization.json.Json

/**
 * Parses a JSON string into a [JsonNode] without throwing exceptions.
 *
 * Returns a [Result.Ok] containing a navigable [JsonNode] on success,
 * or a [Result.Err] containing a [JsonError.ParseError] if the input is malformed.
 *
 * @param input the JSON string to parse
 * @return a [Result] containing either a [JsonNode] or a [JsonError]
 */
fun safeParseJson(input: String): Result<JsonNode, JsonError> {
    return try {
        val element = Json.parseToJsonElement(input)
        Result.Ok(JsonNode(element))
    } catch (e: Exception) {
        Result.Err(JsonError.ParseError(e.message ?: "Unknown parse error"))
    }
}
