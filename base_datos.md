# Estructura de Base de Datos - Sistema de Veterinaria

## CATEGORIAS_INVENTARIO
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_categoria` | NUMBER | Identificador único de la categoría (PK) |
| `nombre` | VARCHAR2(100) | Nombre de la categoría |
| `descripcion` | VARCHAR2(200) | Descripción de la categoría |

## CITAS
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_cita` | NUMBER | Identificador único de la cita (PK) |
| `id_mascota` | NUMBER | Identificador de la mascota (FK) |
| `id_empleado` | NUMBER | Identificador del empleado que atiende (FK) |
| `fecha_hora` | DATE | Fecha y hora programada de la cita |
| `tipo_cita` | VARCHAR2(50) | Tipo de cita (consulta, vacunación, etc.) |
| `motivo` | VARCHAR2(200) | Motivo de la cita |
| `estado` | VARCHAR2(20) | Estado de la cita (programada, completada, cancelada) |
| `notas` | VARCHAR2(500) | Notas adicionales sobre la cita |

## CLIENTES
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_cliente` | NUMBER | Identificador único del cliente (PK) |
| `rut` | VARCHAR2(15) | RUT del cliente (UK) |
| `nombres` | VARCHAR2(100) | Nombres del cliente |
| `apellidos` | VARCHAR2(100) | Apellidos del cliente |
| `email` | VARCHAR2(100) | Correo electrónico del cliente |
| `telefono` | VARCHAR2(20) | Teléfono de contacto |
| `direccion` | VARCHAR2(200) | Dirección del cliente |
| `ciudad` | VARCHAR2(50) | Ciudad de residencia |
| `fecha_registro` | DATE | Fecha de registro en el sistema |
| `activo` | NUMBER(1) | Indica si el cliente está activo (1) o no (0) |

## DETALLES_FACTURA
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_detalle` | NUMBER | Identificador único del detalle (PK) |
| `id_factura` | NUMBER | Identificador de la factura relacionada (FK) |
| `id_cita` | NUMBER | Identificador de la cita relacionada (FK) |
| `id_producto` | NUMBER | Identificador del producto relacionado (FK) |
| `descripcion` | VARCHAR2(200) | Descripción del ítem facturado |
| `cantidad` | NUMBER | Cantidad del ítem |
| `precio_unitario` | NUMBER(10,2) | Precio unitario del ítem |
| `subtotal` | NUMBER(10,2) | Subtotal (cantidad * precio unitario) |

## EMPLEADOS
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_empleado` | NUMBER | Identificador único del empleado (PK) |
| `rut` | VARCHAR2(15) | RUT del empleado (UK) |
| `nombres` | VARCHAR2(100) | Nombres del empleado |
| `apellidos` | VARCHAR2(100) | Apellidos del empleado |
| `cargo` | VARCHAR2(50) | Cargo del empleado en la veterinaria |
| `especialidad` | VARCHAR2(100) | Especialidad médica (si aplica) |
| `email` | VARCHAR2(100) | Correo electrónico laboral |
| `telefono` | VARCHAR2(20) | Teléfono de contacto |
| `fecha_contratacion` | DATE | Fecha de contratación |
| `activo` | NUMBER(1) | Indica si el empleado está activo (1) o no (0) |

## FACTURAS
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_factura` | NUMBER | Identificador único de la factura (PK) |
| `numero_factura` | VARCHAR2(20) | Número de factura (UK) |
| `id_cliente` | NUMBER | Identificador del cliente (FK) |
| `fecha_emision` | DATE | Fecha de emisión de la factura |
| `monto_subtotal` | NUMBER(10,2) | Monto subtotal |
| `monto_iva` | NUMBER(10,2) | Monto de IVA |
| `monto_total` | NUMBER(10,2) | Monto total a pagar |
| `estado` | VARCHAR2(20) | Estado de la factura (pendiente, pagada, etc.) |
| `forma_pago` | VARCHAR2(50) | Forma de pago establecida |
| `observaciones` | VARCHAR2(200) | Observaciones adicionales |

## HISTORIAL_CLINICO
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_historial` | NUMBER | Identificador único del registro clínico (PK) |
| `id_mascota` | NUMBER | Identificador de la mascota (FK) |
| `id_cita` | NUMBER | Identificador de la cita relacionada (FK) |
| `id_empleado` | NUMBER | Identificador del empleado que atendió (FK) |
| `fecha_consulta` | DATE | Fecha de la consulta |
| `motivo_consulta` | VARCHAR2(200) | Motivo de la consulta |
| `diagnostico` | VARCHAR2(500) | Diagnóstico emitido |
| `tratamiento` | VARCHAR2(500) | Tratamiento indicado |
| `observaciones` | VARCHAR2(1000) | Observaciones adicionales |

## HORARIOS_EMPLEADOS
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_horario` | NUMBER | Identificador único del horario (PK) |
| `id_empleado` | NUMBER | Identificador del empleado (FK) |
| `dia_semana` | NUMBER(1) | Día de la semana (1-7) |
| `hora_inicio` | DATE | Hora de inicio de la jornada |
| `hora_fin` | DATE | Hora de fin de la jornada |

## INVENTARIO
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_producto` | NUMBER | Identificador único del producto (PK) |
| `id_categoria` | NUMBER | Identificador de la categoría (FK) |
| `codigo` | VARCHAR2(50) | Código único del producto |
| `nombre` | VARCHAR2(100) | Nombre del producto |
| `descripcion` | VARCHAR2(200) | Descripción del producto |
| `stock_actual` | NUMBER | Cantidad actual en inventario |
| `stock_minimo` | NUMBER | Nivel mínimo de stock para alertas |
| `unidad_medida` | VARCHAR2(20) | Unidad de medida |
| `precio_costo` | NUMBER(10,2) | Precio de costo |
| `precio_venta` | NUMBER(10,2) | Precio de venta |
| `fecha_vencimiento` | DATE | Fecha de vencimiento (si aplica) |
| `proveedor` | VARCHAR2(100) | Nombre del proveedor |
| `activo` | NUMBER(1) | Indica si el producto está activo (1) o no (0) |

## LABORATORIOS
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_laboratorio` | NUMBER | Identificador único del laboratorio (PK) |
| `nombre` | VARCHAR2(100) | Nombre del laboratorio |
| `direccion` | VARCHAR2(200) | Dirección física |
| `telefono` | VARCHAR2(20) | Teléfono de contacto |
| `email` | VARCHAR2(100) | Correo electrónico |
| `contacto_nombre` | VARCHAR2(100) | Nombre de la persona de contacto |
| `activo` | NUMBER(1) | Indica si el laboratorio está activo (1) o no (0) |

## MASCOTAS
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_mascota` | NUMBER | Identificador único de la mascota (PK) |
| `id_cliente` | NUMBER | Identificador del cliente dueño (FK) |
| `nombre` | VARCHAR2(50) | Nombre de la mascota |
| `especie` | VARCHAR2(50) | Especie (perro, gato, etc.) |
| `raza` | VARCHAR2(50) | Raza de la mascota |
| `fecha_nacimiento` | DATE | Fecha de nacimiento o aproximada |
| `sexo` | CHAR(1) | Sexo (M: macho, H: hembra) |
| `color` | VARCHAR2(50) | Color del pelaje o plumaje |
| `peso` | NUMBER(5,2) | Peso en kg |
| `activo` | NUMBER(1) | Indica si la mascota está activa (1) o no (0) |
| `fecha_registro` | DATE | Fecha de registro en el sistema |

## MOVIMIENTOS_INVENTARIO
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_movimiento` | NUMBER | Identificador único del movimiento (PK) |
| `id_producto` | NUMBER | Identificador del producto (FK) |
| `tipo_movimiento` | VARCHAR2(20) | Tipo de movimiento (entrada o salida) |
| `cantidad` | NUMBER | Cantidad de producto |
| `fecha_movimiento` | DATE | Fecha del movimiento |
| `id_cita` | NUMBER | Identificador de la cita relacionada (FK) |
| `id_empleado` | NUMBER | Identificador del empleado que realizó el movimiento (FK) |
| `observacion` | VARCHAR2(200) | Observaciones del movimiento |

## NOTIFICACIONES
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_notificacion` | NUMBER | Identificador único de la notificación (PK) |
| `id_cliente` | NUMBER | Identificador del cliente destinatario (FK) |
| `tipo` | VARCHAR2(50) | Tipo de notificación |
| `titulo` | VARCHAR2(100) | Título de la notificación |
| `mensaje` | VARCHAR2(500) | Contenido del mensaje |
| `fecha_creacion` | DATE | Fecha de creación |
| `fecha_envio` | DATE | Fecha de envío |
| `estado` | VARCHAR2(20) | Estado de la notificación (pendiente, enviada, etc.) |
| `canal` | VARCHAR2(20) | Canal de envío (email, SMS, push) |

## PAGOS
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_pago` | NUMBER | Identificador único del pago (PK) |
| `id_factura` | NUMBER | Identificador de la factura relacionada (FK) |
| `fecha_pago` | DATE | Fecha del pago |
| `monto` | NUMBER(10,2) | Monto pagado |
| `metodo_pago` | VARCHAR2(50) | Método de pago utilizado |
| `referencia_pago` | VARCHAR2(100) | Referencia o comprobante del pago |
| `estado` | VARCHAR2(20) | Estado del pago |

## RESULTADOS_LABORATORIO
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_resultado` | NUMBER | Identificador único del resultado (PK) |
| `id_solicitud` | NUMBER | Identificador de la solicitud relacionada (FK) |
| `fecha_resultado` | DATE | Fecha del resultado |
| `archivo_adjunto` | BLOB | Archivo adjunto con resultados |
| `nombre_archivo` | VARCHAR2(200) | Nombre del archivo adjunto |
| `resultado_texto` | CLOB | Texto del resultado |
| `observaciones` | VARCHAR2(500) | Observaciones adicionales |

## SOLICITUDES_LABORATORIO
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_solicitud` | NUMBER | Identificador único de la solicitud (PK) |
| `id_laboratorio` | NUMBER | Identificador del laboratorio (FK) |
| `id_mascota` | NUMBER | Identificador de la mascota (FK) |
| `id_cita` | NUMBER | Identificador de la cita relacionada (FK) |
| `id_empleado` | NUMBER | Identificador del empleado solicitante (FK) |
| `tipo_examen` | VARCHAR2(100) | Tipo de examen solicitado |
| `fecha_solicitud` | DATE | Fecha de la solicitud |
| `fecha_entrega_esperada` | DATE | Fecha esperada de entrega |
| `estado` | VARCHAR2(20) | Estado de la solicitud |
| `observaciones` | VARCHAR2(500) | Observaciones adicionales |

## USUARIOS_SISTEMA
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_usuario` | NUMBER | Identificador único del usuario (PK) |
| `id_empleado` | NUMBER | Identificador del empleado relacionado (FK) |
| `username` | VARCHAR2(50) | Nombre de usuario para acceso al sistema |
| `password` | VARCHAR2(100) | Contraseña encriptada |
| `rol` | VARCHAR2(20) | Rol del usuario en el sistema |
| `ultimo_acceso` | DATE | Fecha y hora del último acceso |
| `activo` | NUMBER(1) | Indica si el usuario está activo (1) o no (0) |

## Relaciones principales:

- Un **CLIENTE** puede tener muchas **MASCOTAS**
- Una **MASCOTA** puede tener muchas **CITAS**
- Una **MASCOTA** tiene un **HISTORIAL_CLINICO** compuesto por varios registros
- Un **EMPLEADO** puede atender muchas **CITAS**
- Una **CITA** puede generar registros en **HISTORIAL_CLINICO**
- Una **CITA** puede generar **SOLICITUDES_LABORATORIO**
- Una **FACTURA** tiene varios **DETALLES_FACTURA**
- Un **PRODUCTO** de **INVENTARIO** pertenece a una **CATEGORIA_INVENTARIO**
- Un **PRODUCTO** puede tener múltiples **MOVIMIENTOS_INVENTARIO**