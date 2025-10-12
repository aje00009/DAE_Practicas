# DAE PRÁCTICAS
### ITERACIÓN 1

- Inicialización del proyecto
- Creación de la entidad ``Usuario`` (Alberto Jiménez Expósito)
- Creación del servicio ``ServicioIncidencias`` (María Ximena Galdames Fernandes)
- Creación de la entidad ``Incidencia`` (Carlos Mayor Navarro)
- Añadido atributo `loginUsuario` a ``Incidencia`` para indicar el `Usuario` que la registró (Alberto Jiménez Expósito)
- Añadido método ``nuevaIncidencia`` que registra una nueva incidencia en el sistema (Alberto Jiménez Expósito)
- Añadida clase excepción ``UsuarioYaRegistrado`` para lanzar una excepción cuando un usuario ya exista en el sistema (Alberto Jiménez Expósito)
- Añadido método `nuevoUsuario` a ``ServicioIncidencia`` para registrar un nuevo `Usuario` en el sistema (Alberto Jiménez Expósito)
- Añadido método ``login`` a `ServicioIncidencia` para hacer login de un usuario ya registrado en el sistema (Alberto Jiménez Expósito)
- Inicialización de ``tiposIncidencia`` en `ServicioIncidencia` para añadir los tipos de incidencia por defecto en el sistema (Alberto Jiménez Expósito)
- Añadido método ``obtenerListaIncidenciasUsuario`` a `ServicioIncidencia` para buscar incidencias por email (Alberto Jiménez Expósito)
- Añadida clase ``EstadoIncidencia`` que sirve para indicar los estados posibles de una `Incidencia` (Carlos Mayor Navarro)
- Modificaciones en la declaración del atributo``estado`` dentro de `Incidencia` y métodos getter y setter, además del resto métodos relacionados con la misma en ``ServicioIncidencia`` (Carlos Mayor Navarro)
- Modificación del método ``buscarIncidenciasTipoEstado`` en `ServicioIncidencias` para simplificar su lógica (Alberto Jiménez Expósito)