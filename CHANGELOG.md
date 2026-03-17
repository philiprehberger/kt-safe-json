# Changelog

## 0.1.0 (2026-03-17)

- Add `safeParseJson()` for non-throwing JSON parsing
- Add `JsonNode` with typed path navigation: `string()`, `int()`, `long()`, `boolean()`, `double()`
- Add convenience accessors: `stringOrDefault()`, `intOrNull()`
- Add dot-notation and array bracket path support (e.g. `"user.roles[0]"`)
- Add sealed `JsonError` hierarchy: `ParseError`, `PathNotFound`, `TypeMismatch`
