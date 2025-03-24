# Documentación del Microservicio de Inventario

## Índice
1. [Descripción General](#descripción-general)
2. [Estructura del Proyecto](#estructura-del-proyecto)
3. [Modelo de Datos](#modelo-de-datos)
4. [Servicios](#servicios)
5. [API REST](#api-rest)
6. [Integración con Azure Functions](#integración-con-azure-functions)
7. [Ejemplos de Uso](#ejemplos-de-uso)
8. [Consideraciones de Implementación](#consideraciones-de-implementación)

## Descripción General

El microservicio de Inventario es parte del sistema de gestión de veterinaria CloudNative. Este microservicio se encarga de gestionar todo lo relacionado con el inventario de productos veterinarios, incluyendo medicamentos, alimentos, accesorios e insumos clínicos.

### Responsabilidades principales:
- Gestión de categorías de productos
- Administración de productos (stock, precios, vencimientos)
- Control de movimientos de inventario (entradas y salidas)
- Alertas de stock bajo (mediante Azure Functions)
- Alertas de productos próximos a vencer (mediante Azure Functions)
- Reportes de inventario

### Tecnologías utilizadas:
- Java 17
- Spring Boot 3.3.7
- JPA/Hibernate
- Oracle Database
- REST API
- Azure Functions (integración con servicios serverless)

## Estructura del Proyecto

```
com.duoc.app_spring/
├── config/
│   └── WebConfig.java
├── controller/
│   ├── CategoriaInventarioController.java
│   ├── ProductoController.java
│   ├── MovimientoInventarioController.java
│   └── VencimientoController.java
├── dto/
│   ├── CategoriaInventarioDTO.java
│   ├── ProductoDTO.java
│   └── MovimientoInventarioDTO.java
├── model/
│   ├── CategoriaInventario.java
│   ├── Producto.java
│   └── MovimientoInventario.java
├── repository/
│   ├── CategoriaInventarioRepository.java
│   ├── ProductoRepository.java
│   └── MovimientoInventarioRepository.java
├── service/
│   ├── AzureFunctionService.java
│   ├── CategoriaInventarioService.java
│   ├── ProductoService.java
│   ├── MovimientoInventarioService.java
│   └── VencimientoService.java
└── util/
    ├── ResourceNotFoundException.java
    ├── BadRequestException.java
    ├── GlobalExceptionHandler.java
    └── ErrorResponse.java
```

## Modelo de Datos

### Tabla: CATEGORIAS_INVENTARIO
Almacena las categorías para clasificar los productos.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_categoria` | NUMBER | Clave primaria |
| `nombre` | VARCHAR2(100) | Nombre de la categoría |
| `descripcion` | VARCHAR2(200) | Descripción de la categoría |

### Tabla: INVENTARIO
Almacena la información de los productos del inventario.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_producto` | NUMBER | Clave primaria |
| `id_categoria` | NUMBER | Clave foránea a CATEGORIAS_INVENTARIO |
| `codigo` | VARCHAR2(50) | Código único del producto |
| `nombre` | VARCHAR2(100) | Nombre del producto |
| `descripcion` | VARCHAR2(200) | Descripción del producto |
| `stock_actual` | NUMBER | Cantidad actual en inventario |
| `stock_minimo` | NUMBER | Nivel mínimo para alertas |
| `unidad_medida` | VARCHAR2(20) | Unidad de medida |
| `precio_costo` | NUMBER(10,2) | Precio de costo |
| `precio_venta` | NUMBER(10,2) | Precio de venta |
| `fecha_vencimiento` | DATE | Fecha de vencimiento |
| `proveedor` | VARCHAR2(100) | Nombre del proveedor |
| `activo` | NUMBER(1) | Estado (1: activo, 0: inactivo) |

### Tabla: MOVIMIENTOS_INVENTARIO
Registra todos los movimientos (entradas y salidas) de los productos.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id_movimiento` | NUMBER | Clave primaria |
| `id_producto` | NUMBER | Clave foránea a INVENTARIO |
| `tipo_movimiento` | VARCHAR2(20) | Tipo (ENTRADA o SALIDA) |
| `cantidad` | NUMBER | Cantidad del movimiento |
| `fecha_movimiento` | DATE | Fecha y hora del movimiento |
| `id_cita` | NUMBER | Clave foránea a CITAS (opcional) |
| `id_empleado` | NUMBER | Clave foránea a EMPLEADOS |
| `observacion` | VARCHAR2(200) | Observaciones adicionales |

## Servicios

### CategoriaInventarioService
Servicio para la gestión de categorías de inventario.

#### Métodos principales:
- `findAll()`: Obtiene todas las categorías
- `findById(Long id)`: Busca una categoría por ID
- `findByNombre(String nombre)`: Busca categorías por nombre
- `save(CategoriaInventarioDTO categoriaDTO)`: Guarda o actualiza una categoría
- `deleteById(Long id)`: Elimina una categoría por ID

### ProductoService
Servicio para la gestión de productos del inventario.

#### Métodos principales:
- `findAll()`: Obtiene todos los productos
- `findById(Long id)`: Busca un producto por ID
- `findByNombre(String nombre)`: Busca productos por nombre
- `findByCategoria(Long idCategoria)`: Busca productos por categoría
- `findProductosBajoStock()`: Obtiene productos con stock menor o igual al mínimo
- `findByFechaVencimiento(Date fecha)`: Obtiene productos que vencen antes de una fecha
- `save(ProductoDTO productoDTO)`: Guarda o actualiza un producto
- `deleteById(Long id)`: Elimina un producto por ID

### MovimientoInventarioService
Servicio para la gestión de movimientos de inventario.

#### Métodos principales:
- `findAll()`: Obtiene todos los movimientos
- `findById(Long id)`: Busca un movimiento por ID
- `findByProducto(Long idProducto)`: Busca movimientos por producto
- `findByFechaBetween(Date fechaInicio, Date fechaFin)`: Busca movimientos en un rango de fechas
- `findByTipoMovimiento(String tipoMovimiento)`: Busca movimientos por tipo
- `save(MovimientoInventarioDTO movimientoDTO)`: Guarda un movimiento y actualiza el stock
- `deleteById(Long id)`: Elimina un movimiento y revierte el cambio en el stock
- `verificarYNotificarStockBajo(Producto producto, Integer stockAnterior)`: Verifica si el stock está por debajo del mínimo y envía notificación mediante Azure Functions

### VencimientoService
Servicio para verificar y notificar sobre productos próximos a vencer.

#### Métodos principales:
- `verificarProductosPorVencer()`: Verifica productos que vencen dentro de un período configurable (por defecto 30 días) y envía notificaciones mediante Azure Functions

### AzureFunctionService
Servicio para la comunicación con las Azure Functions.

#### Métodos principales:
- `enviarAlertaStockBajo(Long idProducto, String nombreProducto, Integer stockActual, Integer stockMinimo)`: Envía notificaciones cuando un producto llega a su stock mínimo
- `enviarAlertaProductoPorVencer(Long idProducto, String nombreProducto, String fechaVencimiento, long diasRestantes)`: Envía notificaciones cuando un producto está próximo a vencer

## API REST

### Endpoints de Categorías

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/categorias` | Obtiene todas las categorías |
| GET | `/api/categorias/{id}` | Obtiene una categoría por ID |
| GET | `/api/categorias/nombre/{nombre}` | Busca categorías por nombre |
| POST | `/api/categorias` | Crea una nueva categoría |
| PUT | `/api/categorias/{id}` | Actualiza una categoría existente |
| DELETE | `/api/categorias/{id}` | Elimina una categoría |

### Endpoints de Productos

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/productos` | Obtiene todos los productos |
| GET | `/api/productos/{id}` | Obtiene un producto por ID |
| GET | `/api/productos/nombre/{nombre}` | Busca productos por nombre |
| GET | `/api/productos/categoria/{idCategoria}` | Busca productos por categoría |
| GET | `/api/productos/bajo-stock` | Obtiene productos con stock bajo |
| GET | `/api/productos/vencimiento-antes/{fecha}` | Obtiene productos que vencen antes de una fecha |
| POST | `/api/productos` | Crea un nuevo producto |
| PUT | `/api/productos/{id}` | Actualiza un producto existente |
| DELETE | `/api/productos/{id}` | Elimina un producto |

### Endpoints de Movimientos

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/movimientos` | Obtiene todos los movimientos |
| GET | `/api/movimientos/{id}` | Obtiene un movimiento por ID |
| GET | `/api/movimientos/producto/{idProducto}` | Busca movimientos por producto |
| GET | `/api/movimientos/periodo` | Busca movimientos en un rango de fechas |
| GET | `/api/movimientos/tipo/{tipoMovimiento}` | Busca movimientos por tipo |
| POST | `/api/movimientos` | Crea un nuevo movimiento |
| PUT | `/api/movimientos/{id}` | Actualiza un movimiento existente |
| DELETE | `/api/movimientos/{id}` | Elimina un movimiento |

### Endpoints de Verificación de Vencimientos

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/vencimientos/verificar` | Ejecuta manualmente la verificación de productos próximos a vencer |

## Integración con Azure Functions

El microservicio se integra con Azure Functions para proporcionar alertas y notificaciones automáticas en diferentes escenarios. Esta integración permite una arquitectura más desacoplada y escalable.

### 1. Configuración

La configuración de las Azure Functions se realiza en el archivo `application.properties`:

```properties
# URLs de Funciones Azure
azure.functions.notificaciones.url=https://for1notificacion.azurewebsites.net/api/HttpNotificaciones
azure.functions.facturacion.url=https://for1facturacion.azurewebsites.net/api/HttpFacturacion
azure.functions.laboratorios.url=https://for1laboratorios.azurewebsites.net/api/HttpLaboratorios

# Configuración de alertas de stock y vencimiento
inventario.alertas.vencimiento.dias=30
```

### 2. Funcionalidades Implementadas

#### a) Alertas de Stock Bajo

**Cuándo se activa:**
- Cuando un producto cae por debajo de su nivel mínimo de stock después de un movimiento de SALIDA
- Cuando se modifica el stock mínimo de un producto y el stock actual queda por debajo del nuevo mínimo

**Implementación:**
- En `MovimientoInventarioService`, después de cada movimiento de inventario, se verifica si el stock ha caído por debajo del mínimo
- Si es así, se llama a `AzureFunctionService.enviarAlertaStockBajo()`
- La función Azure recibe los datos y se encarga de enviar las notificaciones correspondientes

**Ejemplo de datos enviados a la función Azure:**
```json
{
  "tipo": "STOCK_BAJO",
  "titulo": "Alerta de Stock Bajo",
  "mensaje": "El producto 'Amoxicilina 250mg' (ID: 3) tiene un stock bajo: 5 unidades (mínimo: 10).",
  "fechaCreacion": "2025-03-23T22:15:30.123456789"
}
```

#### b) Alertas de Productos por Vencer

**Cuándo se activa:**
- Automáticamente todos los días a las 8:00 AM (programado con @Scheduled)
- Manualmente al ejecutar el endpoint `/api/vencimientos/verificar`

**Implementación:**
- En `VencimientoService`, se obtienen todos los productos y se verifica su fecha de vencimiento
- Para aquellos que vencen dentro del período configurable (por defecto 30 días), se llama a `AzureFunctionService.enviarAlertaProductoPorVencer()`
- La función Azure recibe los datos y se encarga de enviar las notificaciones correspondientes

**Ejemplo de datos enviados a la función Azure:**
```json
{
  "tipo": "PRODUCTO_POR_VENCER",
  "titulo": "Alerta de Producto por Vencer",
  "mensaje": "El producto 'Royal Canin Cachorro 15kg' (ID: 22) está próximo a vencer: 12-04-2025 (faltan 20 días).",
  "fechaCreacion": "2025-03-23T22:26:20.644190545"
}
```

### 3. Respuesta de la Función Azure

La función Azure actualmente responde con una confirmación que incluye el tipo de operación realizada. En el futuro, se planea expandir esta respuesta para incluir información detallada sobre los canales utilizados para enviar las notificaciones (email, SMS, etc.).

## Ejemplos de Uso

### Crear una Categoría

**Petición:**
```
POST /api/categorias
Content-Type: application/json

{
  "nombre": "Medicamentos",
  "descripcion": "Todo tipo de medicamentos para mascotas"
}
```

**Respuesta:**
```json
{
  "idCategoria": 1,
  "nombre": "Medicamentos",
  "descripcion": "Todo tipo de medicamentos para mascotas"
}
```

### Crear un Producto

**Petición:**
```
POST /api/productos
Content-Type: application/json

{
  "idCategoria": 1,
  "codigo": "MED-001",
  "nombre": "Amoxicilina 250mg",
  "descripcion": "Antibiótico para tratamiento de infecciones bacterianas",
  "stockActual": 50,
  "stockMinimo": 10,
  "unidadMedida": "Unidad",
  "precioCosto": 5000.00,
  "precioVenta": 8500.00,
  "fechaVencimiento": "2026-06-30",
  "proveedor": "Laboratorios ABC",
  "activo": 1
}
```

**Respuesta:**
```json
{
  "idProducto": 1,
  "idCategoria": 1,
  "nombreCategoria": "Medicamentos",
  "codigo": "MED-001",
  "nombre": "Amoxicilina 250mg",
  "descripcion": "Antibiótico para tratamiento de infecciones bacterianas",
  "stockActual": 50,
  "stockMinimo": 10,
  "unidadMedida": "Unidad",
  "precioCosto": 5000.00,
  "precioVenta": 8500.00,
  "fechaVencimiento": "2026-06-30",
  "proveedor": "Laboratorios ABC",
  "activo": 1
}
```

### Registrar un Movimiento de Salida que Active Alerta de Stock Bajo

**Petición:**
```
POST /api/movimientos
Content-Type: application/json

{
  "idProducto": 1,
  "tipoMovimiento": "SALIDA",
  "cantidad": 45,
  "fechaMovimiento": "2025-03-23T14:30:00",
  "idEmpleado": 1,
  "observacion": "Salida para atención de mascota"
}
```

**Respuesta:**
```json
{
  "idMovimiento": 1,
  "idProducto": 1,
  "nombreProducto": "Amoxicilina 250mg",
  "tipoMovimiento": "SALIDA",
  "cantidad": 45,
  "fechaMovimiento": "2025-03-23T14:30:00",
  "idCita": null,
  "idEmpleado": 1,
  "observacion": "Salida para atención de mascota"
}
```

**Efecto Adicional:**
- Se envía una notificación a la función Azure de tipo STOCK_BAJO
- Logs del sistema muestran: "Enviando alerta de stock bajo a la función Azure..."
- La función Azure procesa la alerta y responde con confirmación

### Verificar Productos por Vencer Manualmente

**Petición:**
```
GET /api/vencimientos/verificar
```

**Respuesta:**
```
Verificación de productos por vencer ejecutada correctamente
```

**Efecto Adicional:**
- Se escanean todos los productos en busca de aquellos que vencen en los próximos 30 días
- Para cada producto que cumple el criterio, se envía una notificación a la función Azure
- Logs del sistema muestran: "Enviando alerta para producto próximo a vencer..."
- La función Azure procesa cada alerta y responde con confirmación

## Consideraciones de Implementación

### Actualizaciones de Stock
Cuando se registra un movimiento de inventario, el sistema automáticamente actualiza el stock del producto:
- En movimientos de ENTRADA: se incrementa el stock
- En movimientos de SALIDA: se decrementa el stock

### Validaciones
- No se permite registrar salidas de inventario si el stock disponible es insuficiente
- Se valida que las categorías y productos existan antes de crear relaciones
- Los códigos de producto deben ser únicos

### Manejo de Excepciones
- `ResourceNotFoundException`: para recursos no encontrados
- `BadRequestException`: para solicitudes con datos inválidos
- `GlobalExceptionHandler`: maneja todas las excepciones de forma centralizada

### Integridad Referencial
- No se permite eliminar categorías que tienen productos asociados
- No se permite eliminar productos que tienen movimientos asociados

### Integración con Azure Functions
- Las comunicaciones con Azure Functions son asíncronas y no bloqueantes
- Si una función Azure no responde, el flujo principal del microservicio no se ve afectado
- Se registran logs detallados de las comunicaciones para facilitar la depuración

### CORS
Se ha habilitado CORS para permitir que el frontend Angular realice peticiones al API sin restricciones de origen.

### Tareas Programadas
- La verificación de productos por vencer se ejecuta automáticamente cada día a las 8:00 AM
- También se puede ejecutar manualmente a través del endpoint dedicado