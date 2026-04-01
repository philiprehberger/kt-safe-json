package com.philiprehberger.safejson

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SafeJsonTest {

    @Test
    fun `parse valid JSON`() {
        val result = safeParseJson("""{"name": "Alice", "age": 30}""")
        assertTrue(result.isOk)
    }

    @Test
    fun `malformed JSON returns ParseError`() {
        val result = safeParseJson("{invalid json")
        assertIs<Result.Err<JsonError>>(result)
        assertIs<JsonError.ParseError>(result.error)
    }

    @Test
    fun `string path navigation`() {
        val node = (safeParseJson("""{"name": "Alice"}""") as Result.Ok).value
        val result = node.string("name")
        assertIs<Result.Ok<String>>(result)
        assertEquals("Alice", result.value)
    }

    @Test
    fun `int path navigation`() {
        val node = (safeParseJson("""{"age": 30}""") as Result.Ok).value
        val result = node.int("age")
        assertIs<Result.Ok<Int>>(result)
        assertEquals(30, result.value)
    }

    @Test
    fun `boolean path navigation`() {
        val node = (safeParseJson("""{"active": true}""") as Result.Ok).value
        val result = node.boolean("active")
        assertIs<Result.Ok<Boolean>>(result)
        assertEquals(true, result.value)
    }

    @Test
    fun `double path navigation`() {
        val node = (safeParseJson("""{"score": 9.5}""") as Result.Ok).value
        val result = node.double("score")
        assertIs<Result.Ok<Double>>(result)
        assertEquals(9.5, result.value)
    }

    @Test
    fun `long path navigation`() {
        val node = (safeParseJson("""{"big": 9999999999}""") as Result.Ok).value
        val result = node.long("big")
        assertIs<Result.Ok<Long>>(result)
        assertEquals(9999999999L, result.value)
    }

    @Test
    fun `nested path navigation`() {
        val json = """{"user": {"profile": {"name": "Bob"}}}"""
        val node = (safeParseJson(json) as Result.Ok).value
        val result = node.string("user.profile.name")
        assertIs<Result.Ok<String>>(result)
        assertEquals("Bob", result.value)
    }

    @Test
    fun `array access`() {
        val json = """{"items": [{"id": 1}, {"id": 2}, {"id": 3}]}"""
        val node = (safeParseJson(json) as Result.Ok).value
        val result = node.int("items[1].id")
        assertIs<Result.Ok<Int>>(result)
        assertEquals(2, result.value)
    }

    @Test
    fun `top-level array access`() {
        val json = """[{"name": "first"}, {"name": "second"}]"""
        val node = (safeParseJson(json) as Result.Ok).value
        val result = node.string("[0].name")
        assertIs<Result.Ok<String>>(result)
        assertEquals("first", result.value)
    }

    @Test
    fun `missing path returns PathNotFound`() {
        val node = (safeParseJson("""{"name": "Alice"}""") as Result.Ok).value
        val result = node.string("email")
        assertIs<Result.Err<JsonError>>(result)
        assertIs<JsonError.PathNotFound>(result.error)
        assertEquals("email", (result.error as JsonError.PathNotFound).path)
    }

    @Test
    fun `missing nested path returns PathNotFound`() {
        val node = (safeParseJson("""{"user": {"name": "Alice"}}""") as Result.Ok).value
        val result = node.string("user.email")
        assertIs<Result.Err<JsonError>>(result)
        assertIs<JsonError.PathNotFound>(result.error)
    }

    @Test
    fun `type mismatch returns TypeMismatch`() {
        val node = (safeParseJson("""{"name": "Alice"}""") as Result.Ok).value
        val result = node.int("name")
        assertIs<Result.Err<JsonError>>(result)
        val error = result.error
        assertIs<JsonError.TypeMismatch>(error)
        assertEquals("name", error.path)
        assertEquals("Int", error.expected)
    }

    @Test
    fun `stringOrDefault returns value when present`() {
        val node = (safeParseJson("""{"name": "Alice"}""") as Result.Ok).value
        assertEquals("Alice", node.stringOrDefault("name", "Unknown"))
    }

    @Test
    fun `stringOrDefault returns default when missing`() {
        val node = (safeParseJson("""{"name": "Alice"}""") as Result.Ok).value
        assertEquals("Unknown", node.stringOrDefault("email", "Unknown"))
    }

    @Test
    fun `intOrNull returns value when present`() {
        val node = (safeParseJson("""{"age": 30}""") as Result.Ok).value
        assertEquals(30, node.intOrNull("age"))
    }

    @Test
    fun `intOrNull returns null when missing`() {
        val node = (safeParseJson("""{"name": "Alice"}""") as Result.Ok).value
        assertNull(node.intOrNull("age"))
    }

    @Test
    fun `intOrNull returns null on type mismatch`() {
        val node = (safeParseJson("""{"name": "Alice"}""") as Result.Ok).value
        assertNull(node.intOrNull("name"))
    }

    @Test
    fun `deeply nested with array`() {
        val json = """
            {
                "data": {
                    "users": [
                        {"id": 10, "roles": ["admin", "user"]},
                        {"id": 20, "roles": ["viewer"]}
                    ]
                }
            }
        """.trimIndent()
        val node = (safeParseJson(json) as Result.Ok).value
        assertEquals(20, (node.int("data.users[1].id") as Result.Ok).value)
        assertEquals("admin", (node.string("data.users[0].roles[0]") as Result.Ok).value)
    }

    @Test
    fun `exists returns true for existing path`() {
        val json = """{"user":{"name":"Alice"}}"""
        val node = (safeParseJson(json) as Result.Ok).value
        assertTrue(node.exists("user.name"))
    }

    @Test
    fun `exists returns false for missing path`() {
        val json = """{"user":{"name":"Alice"}}"""
        val node = (safeParseJson(json) as Result.Ok).value
        assertFalse(node.exists("user.email"))
    }

    @Test
    fun `array returns list of JsonNodes`() {
        val json = """{"items":[1,2,3]}"""
        val node = (safeParseJson(json) as Result.Ok).value
        val result = node.array("items")
        assertTrue(result is Result.Ok)
        assertEquals(3, (result as Result.Ok).value.size)
    }

    @Test
    fun `array returns error for non-array`() {
        val json = """{"name":"Alice"}"""
        val node = (safeParseJson(json) as Result.Ok).value
        val result = node.array("name")
        assertTrue(result is Result.Err)
    }

    @Test
    fun `keys returns object keys`() {
        val json = """{"user":{"name":"Alice","age":30,"active":true}}"""
        val node = (safeParseJson(json) as Result.Ok).value
        val result = node.keys("user")
        assertTrue(result is Result.Ok)
        val keys = (result as Result.Ok).value
        assertEquals(setOf("name", "age", "active"), keys)
    }

    @Test
    fun `keys returns error for non-object`() {
        val json = """{"items":[1,2,3]}"""
        val node = (safeParseJson(json) as Result.Ok).value
        val result = node.keys("items")
        assertTrue(result is Result.Err)
    }

    @Test
    fun `size returns array length`() {
        val json = """{"items":[1,2,3,4,5]}"""
        val node = (safeParseJson(json) as Result.Ok).value
        val result = node.size("items")
        assertTrue(result is Result.Ok)
        assertEquals(5, (result as Result.Ok).value)
    }

    @Test
    fun `size returns object key count`() {
        val json = """{"user":{"name":"Alice","age":30}}"""
        val node = (safeParseJson(json) as Result.Ok).value
        val result = node.size("user")
        assertTrue(result is Result.Ok)
        assertEquals(2, (result as Result.Ok).value)
    }

    @Test
    fun `size returns error for primitives`() {
        val json = """{"name":"Alice"}"""
        val node = (safeParseJson(json) as Result.Ok).value
        val result = node.size("name")
        assertTrue(result is Result.Err)
    }

    @Test
    fun `exists works with array paths`() {
        val json = """{"users":[{"name":"Alice"},{"name":"Bob"}]}"""
        val node = (safeParseJson(json) as Result.Ok).value
        assertTrue(node.exists("users[0].name"))
        assertTrue(node.exists("users[1].name"))
        assertFalse(node.exists("users[2].name"))
    }
}
