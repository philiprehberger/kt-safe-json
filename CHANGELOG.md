# Changelog

## [0.1.2] - 2026-03-18

- Upgrade to Kotlin 2.0.21 and Gradle 8.12
- Enable explicitApi() for stricter public API surface
- Add issueManagement to POM metadata

## 0.1.0 (2026-03-17)

- Add `safeParseJson()` for non-throwing JSON parsing
- Add `JsonNode` with typed path navigation: `string()`, `int()`, `long()`, `boolean()`, `double()`
- Add convenience accessors: `stringOrDefault()`, `intOrNull()`
- Add dot-notation and array bracket path support (e.g. `"user.roles[0]"`)
- Add sealed `JsonError` hierarchy: `ParseError`, `PathNotFound`, `TypeMismatch`
