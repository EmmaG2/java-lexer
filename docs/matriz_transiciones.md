# Matriz de Transiciones del Lexer (AFD)

> **Lenguajes y Autómatas 1 — Instituto Tecnológico de Durango**  
> Fernando Emmanuel Rodriguez Granados | Braulio Emilio Villarreal Aguilar | José Alonso García Castro

---

## Clases de Caracteres

| Clase | Descripción | Ejemplos |
|-------|-------------|----------|
| `LET` | Letra del alfabeto | `a-z`, `A-Z` |
| `DIG` | Dígito decimal | `0-9` |
| `UND` | Guion bajo | `_` |
| `QUO` | Comilla doble | `"` |
| `APO` | Comilla simple | `'` |
| `SPC` | Espacio, tab o newline | ` `, `\t`, `\n` |
| `OP`  | Operadores aritméticos simples | `+`, `-`, `*`, `%`, `^` |
| `EQ`  | Signo igual | `=` |
| `LT`  | Menor que | `<` |
| `GT`  | Mayor que | `>` |
| `EXC` | Signo de exclamación | `!` |
| `COL` | Dos puntos | `:` |
| `SEM` | Punto y coma | `;` |
| `COM` | Coma | `,` |
| `DOT` | Punto | `.` |
| `SLA` | Diagonal | `/` |
| `LBR` | Llave de apertura | `{` |
| `RBR` | Llave de cierre | `}` |
| `LPA` | Paréntesis de apertura | `(` |
| `RPA` | Paréntesis de cierre | `)` |
| `EOF` | Fin de archivo | — |

**Convenciones:**
- `*` = estado de aceptación
- `-1` = transición de error → va a `qERR`
- `ret` = retrocede 1 carácter (maximal munch)
- `—` = no aplica en ese estado

---

## Sección 1 — Identificadores y Keywords

| Estado | Descripción | LET | DIG | UND | SPC | EQ | COL | SEM | COM | DOT | SLA | OP | LBR | RBR | LPA | RPA | EOF | Token emitido |
|--------|-------------|-----|-----|-----|-----|----|-----|-----|-----|-----|-----|----|-----|-----|-----|-----|-----|---------------|
| **q0** | Inicio | q1 | q4 | q1 | q0 | q12 | q17 | q20 | q21 | q22 | q23 | q11 | q26 | q27 | q28 | q29 | qF | — |
| **q1** | Leyendo ID/KW | q1 | q1 | q1 | q2* | q2* | q2* | q2* | q2* | q2* | q2* | q2* | q2* | q2* | q2* | q2* | q2* | → a q2* |
| **q2\*** | **ACEPTA: ID o KW** | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | Retrocede 1 char. Lookup en tabla keywords → `TOKEN_KW` o `TOKEN_ID` |

> **Nota q2\*:** Al llegar a q2, se consulta la tabla hash de keywords. Si el lexema coincide → emite `TOKEN_KW` con su categoría. Si no → emite `TOKEN_ID`.

---

## Sección 2 — Literales Numéricos

| Estado | Descripción | LET | DIG | UND | DOT | SPC | OP | EQ | COL | SEM | COM | SLA | LBR | RBR | LPA | RPA | EOF | Token emitido |
|--------|-------------|-----|-----|-----|-----|-----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|---------------|
| **q4** | Leyendo entero | q6* | q4 | -1 | q5 | q6* | q6* | q6* | q6* | q6* | q6* | q6* | q6* | q6* | q6* | q6* | q6* | → a q6* |
| **q5** | Leyendo decimal | -1 | q5 | -1 | -1 | q6* | q6* | q6* | q6* | q6* | q6* | q6* | q6* | q6* | q6* | q6* | q6* | → a q6* |
| **q6\*** | **ACEPTA: NUM** | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | Retrocede 1 char. `TOKEN_INT` (desde q4) o `TOKEN_DEC` (desde q5) |

> **Nota q5:** Un segundo `.` desde q5 es error (`-1`). Un identificador inmediatamente después de un número (ej. `3x`) también es error.

---

## Sección 3 — Literales de Texto

| Estado | Descripción | QUO | APO | cualquier char | EOF | Token emitido |
|--------|-------------|-----|-----|----------------|-----|---------------|
| **q7** | Leyendo string `"…"` | q8* | q7 | q7 | -1 | → cierra al ver `"` |
| **q8\*** | **ACEPTA: STRING** | — | — | — | — | `TOKEN_STRING` |
| **q9** | Leyendo char `'…'` | q9 | q10* | q9 | -1 | → cierra al ver `'` |
| **q10\*** | **ACEPTA: CHAR** | — | — | — | — | `TOKEN_CHAR` |

> **Nota q7 / q9:** Llegar a EOF sin cerrar la literal es un error léxico (`-1`). Se debe reportar la línea de apertura de la literal sin cerrar.

---

## Sección 4 — Operadores Aritméticos

| Estado | Descripción | Todos los demás | Token emitido |
|--------|-------------|-----------------|---------------|
| **q11\*** | **ACEPTA: OP_ARIT** | — | `TOKEN_OP` para `+` `-` `%`. Para `**` se requiere lookahead de 2 chars → emite `TOKEN_POW`. Para `*` solo → `TOKEN_MUL` |

---

## Sección 5 — Operadores de Comparación

| Estado | Descripción | EQ | cualquier otro | Token emitido |
|--------|-------------|-----|----------------|---------------|
| **q12** | Vio `=` | q13* | q30* (ret) | — |
| **q13\*** | **ACEPTA: `==`** | — | — | `TOKEN_EQ` |
| **q30\*** | **ACEPTA: `=`** | — | — | `TOKEN_ASSIGN` (retrocede 1 char) |
| **q14** | Vio `<` | q31* | q32* (ret) | — |
| **q31\*** | **ACEPTA: `<=`** | — | — | `TOKEN_LTE` |
| **q32\*** | **ACEPTA: `<`** | — | — | `TOKEN_LT` (retrocede 1 char) |
| **q15** | Vio `>` | q33* | q34* (ret) | — |
| **q33\*** | **ACEPTA: `>=`** | — | — | `TOKEN_GTE` |
| **q34\*** | **ACEPTA: `>`** | — | — | `TOKEN_GT` (retrocede 1 char) |
| **q16** | Vio `!` | q35* | -1 | — |
| **q35\*** | **ACEPTA: `!=`** | — | — | `TOKEN_NEQ` |

---

## Sección 6 — Asignación del lenguaje (`:=`) y dos puntos (`:`)

| Estado | Descripción | EQ | cualquier otro | Token emitido |
|--------|-------------|-----|----------------|---------------|
| **q17** | Vio `:` | q18* | q19* (ret) | — |
| **q18\*** | **ACEPTA: `:=`** | — | — | `TOKEN_DECL_ASSIGN` |
| **q19\*** | **ACEPTA: `:`** | — | — | `TOKEN_COLON` (retrocede 1 char) |

> **Nota:** `:=` es el operador de asignación declarativa del lenguaje. `:` sigue siendo válido como separador de tipo (ej. `x: int`).

---

## Sección 7 — Delimitadores Simples

| Estado | Descripción | Token emitido |
|--------|-------------|---------------|
| **q20\*** | **ACEPTA: `;`** | `TOKEN_SEMI` |
| **q21\*** | **ACEPTA: `,`** | `TOKEN_COMMA` |
| **q22\*** | **ACEPTA: `.`** | `TOKEN_DOT` — acceso a campo. Nota: si el estado anterior es q4 (entero), se transiciona a q5 (decimal) en lugar de aceptar aquí |
| **q26\*** | **ACEPTA: `{`** | `TOKEN_LBRACE` |
| **q27\*** | **ACEPTA: `}`** | `TOKEN_RBRACE` |
| **q28\*** | **ACEPTA: `(`** | `TOKEN_LPAREN` |
| **q29\*** | **ACEPTA: `)`** | `TOKEN_RPAREN` |

---

## Sección 8 — Comentarios

| Estado | Descripción | SLA | `*` | NEWLINE | EOF | cualquier otro | Comportamiento |
|--------|-------------|-----|-----|---------|-----|----------------|----------------|
| **q23** | Vio `/` | q24 | q25 | q11* (ret) | q11* (ret) | q11* (ret) | Distingue entre `/`, `//` y `/*` |
| **q24** | Comentario `//` | q24 | q24 | q0 | q0 | q24 | Consume hasta NEWLINE → regresa a q0 |
| **q25** | Comentario `/*` | q25 | q25† | q25 | **-1** | q25 | Consume hasta `*/` → regresa a q0 |

> **† Lookahead en q25:** Al ver `*`, se hace lookahead de 1 char. Si el siguiente es `/` → cierra el comentario y regresa a q0. Si no → permanece en q25.  
> **EOF en q25** es error léxico: comentario de bloque sin cerrar.

---

## Sección 9 — Estados Especiales

| Estado | Descripción | Token emitido |
|--------|-------------|---------------|
| **qERR** | Error léxico | `TOKEN_ERROR` — incluye número de línea, columna y carácter inesperado |
| **qF** | Fin de archivo | `TOKEN_EOF` — el parser detiene el análisis |

---

## Tabla de Keywords (consultada en q2\*)

| Lexema | Keywords | Token |
|--------|----------|-------|
| **Estado** | `pub` `priv` `protect` | `TOKEN_KW_ESTADO` |
| **Tipo de Dato** | `int` `long` `str` `double` `char` `float` `short` `byte` `bool` `void` | `TOKEN_KW_TIPO` |
| **Condicionales** | `exist` `if` `else` | `TOKEN_KW_COND` |
| **Definiciones** | `enum` `struct` `trait` `class` | `TOKEN_KW_DEF` |
| **Ciclos** | `repeat` `while` `each` `in` | `TOKEN_KW_CICLO` |
| **Control de Flujo** | `return` `break` `continue` | `TOKEN_KW_FLUJO` |
| **Funciones** | `fn` `lambda` `async` `await` | `TOKEN_KW_FN` |
| **Variables** | `let` `const` `mut` `static` | `TOKEN_KW_VAR` |
| **Op. Lógicos** | `and` `or` `not` | `TOKEN_KW_LOGIC` |
| **Importaciones** | `use` `from` `export` `module` `package` | `TOKEN_KW_IMPORT` |
| **Errores** | `try` `catch` `throw` `finally` `raise` | `TOKEN_KW_ERROR` |

---

## Resumen de todos los tokens

| Token | Valor | Descripción |
|-------|-------|-------------|
| `TOKEN_ID` | identificador | Nombre de variable, función, etc. |
| `TOKEN_KW_*` | keyword | Palabra reservada (ver tabla anterior) |
| `TOKEN_INT` | entero | Literal numérico entero, ej. `42` |
| `TOKEN_DEC` | decimal | Literal numérico decimal, ej. `3.14` |
| `TOKEN_STRING` | string | Literal de cadena, ej. `"hola"` |
| `TOKEN_CHAR` | char | Literal de carácter, ej. `'a'` |
| `TOKEN_OP` | operador | `+` `-` `*` `/` `%` |
| `TOKEN_POW` | potencia | `**` |
| `TOKEN_EQ` | igual | `==` |
| `TOKEN_ASSIGN` | asignación simple | `=` |
| `TOKEN_DECL_ASSIGN` | asignación declarativa | `:=` |
| `TOKEN_NEQ` | distinto | `!=` |
| `TOKEN_LT` | menor que | `<` |
| `TOKEN_LTE` | menor o igual | `<=` |
| `TOKEN_GT` | mayor que | `>` |
| `TOKEN_GTE` | mayor o igual | `>=` |
| `TOKEN_COLON` | dos puntos | `:` |
| `TOKEN_SEMI` | punto y coma | `;` |
| `TOKEN_COMMA` | coma | `,` |
| `TOKEN_DOT` | punto | `.` |
| `TOKEN_LBRACE` | llave apertura | `{` |
| `TOKEN_RBRACE` | llave cierre | `}` |
| `TOKEN_LPAREN` | paréntesis apertura | `(` |
| `TOKEN_RPAREN` | paréntesis cierre | `)` |
| `TOKEN_COMMENT` | comentario | `//…` o `/*…*/` |
| `TOKEN_EOF` | fin de archivo | — |
| `TOKEN_ERROR` | error léxico | Carácter inesperado |

---

## Buenas Prácticas Aplicadas

| Práctica | Descripción |
|----------|-------------|
| **Maximal Munch** | q1, q4, q5 consumen el máximo posible antes de aceptar. Garantiza que `<=` no se tokenice como `<` + `=`. |
| **Retroceso explícito (ret)** | q2\*, q6\*, q19\*, q30\*, q32\*, q34\* retroceden 1 char para no consumir el delimitador del siguiente token. |
| **Lookup de keywords en q2\*** | Una sola tabla hash en lugar de estados separados por keyword — O(1) y fácil de extender sin modificar el AFD. |
| **`-1` explícito** | Toda transición inválida va a `qERR` con número de línea, columna y carácter inesperado para mensajes de error útiles. |
| **Lookahead en operadores dobles** | `==` `:=` `<=` `>=` `!=` `**` tienen estado intermedio propio (q12, q14, q15, q16, q17, q23). |
| **EOF como error en literales** | q7 y q9 van a `-1` si llega EOF sin cerrar la literal de string o char. |
| **EOF como error en comentario bloque** | q25 va a `-1` si llega EOF sin `*/` de cierre. |
| **Comentarios retornan a q0** | q24 y q25 no emiten tokens al parser (o emiten `TOKEN_COMMENT` para soporte de IDEs/documentación). |
| **Estados numerados por categoría** | q0–q2 identificadores, q4–q6 números, q7–q10 literales texto, q11 aritmética, q12–q35 comparación, q17–q19 asignación, q20–q29 delimitadores, q23–q25 comentarios. |
