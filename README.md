# java-lexer

Analizador léxico base en Java. La estructura está diseñada para ser extendida
una vez que se defina el lenguaje objetivo.

## Estructura

```
src/main/java/lexer/
├── Main.java       # Punto de entrada con ejemplo
├── Lexer.java      # Motor del analizador (recorrido carácter por carácter)
├── Token.java      # Record que representa un token: type, value, line
└── TokenType.java  # Enum con tipos genéricos
```

## MacOs

```bash
make run
```

## Windows

```bash
run.bat
```

## Extender el lexer

- **Nuevo lenguaje**: agregar palabras clave en `Lexer.isKeyword()`
- **Nuevos tipos**: ampliar el enum `TokenType`
- **Reglas especiales**: agregar casos en el bucle de `Lexer.tokenize()`
