# Documentación API GraphQL de Facturación

## Descripción General
Esta API permite gestionar facturas y sus detalles para el sistema de veterinaria utilizando GraphQL. A diferencia de REST, GraphQL permite solicitar exactamente la información que necesitas en una sola consulta.

## Información Técnica
- **URL del Endpoint**: `/api/facturacion`
- **Método HTTP**: POST (GraphQL siempre usa POST)
- **Headers Requeridos**: 
  - `Content-Type: application/json`

## Estructura de las Solicitudes GraphQL

Todas las solicitudes a esta API deben seguir esta estructura:

```json
{
  "query": "... tu consulta GraphQL aquí ...",
  "variables": { ... variables para la consulta ... },
  "operationName": "NombreOperacion" (opcional)
}
```

## Modelos de Datos

### Factura
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `idFactura` | ID | Identificador único de la factura |
| `numeroFactura` | String | Número de factura (ej: F-2025-0001) |
| `idCliente` | ID | ID del cliente al que pertenece la factura |
| `fechaEmision` | String | Fecha y hora de emisión (formato ISO) |
| `montoSubtotal` | Float | Monto subtotal |
| `montoIva` | Float | Monto de IVA |
| `montoTotal` | Float | Monto total a pagar |
| `estado` | String | Estado de la factura (PENDIENTE, PAGADA, ANULADA) |
| `formaPago` | String | Forma de pago (EFECTIVO, TARJETA, etc.) |
| `observaciones` | String | Observaciones adicionales |
| `detalles` | [DetalleFactura] | Lista de detalles de la factura |

### DetalleFactura
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `idDetalle` | ID | Identificador único del detalle |
| `idFactura` | ID | ID de la factura a la que pertenece |
| `idCita` | ID | ID de la cita relacionada (opcional) |
| `idProducto` | ID | ID del producto relacionado (opcional) |
| `descripcion` | String | Descripción del ítem |
| `cantidad` | Int | Cantidad |
| `precioUnitario` | Float | Precio unitario |
| `subtotal` | Float | Subtotal (cantidad * precioUnitario) |

## Consultas (Queries)

### Obtener una factura por ID

**Consulta GraphQL:**
```graphql
query GetFactura($id: ID!) {
  facturaById(id: $id) {
    idFactura
    numeroFactura
    idCliente
    fechaEmision
    montoSubtotal
    montoIva
    montoTotal
    estado
    formaPago
    observaciones
    detalles {
      idDetalle
      descripcion
      cantidad
      precioUnitario
      subtotal
    }
  }
}
```

**Variables:**
```json
{
  "id": "1"
}
```

**Solicitud HTTP completa:**
```http
POST /api/facturacion
Content-Type: application/json

{
  "query": "query GetFactura($id: ID!) { facturaById(id: $id) { idFactura numeroFactura idCliente fechaEmision montoSubtotal montoIva montoTotal estado formaPago observaciones detalles { idDetalle descripcion cantidad precioUnitario subtotal } } }",
  "variables": {
    "id": "1"
  },
  "operationName": "GetFactura"
}
```

### Obtener facturas por cliente

**Consulta GraphQL:**
```graphql
query GetFacturasByCliente($idCliente: ID!) {
  facturasByCliente(idCliente: $idCliente) {
    idFactura
    numeroFactura
    fechaEmision
    montoTotal
    estado
  }
}
```

**Variables:**
```json
{
  "idCliente": "1"
}
```

**Solicitud HTTP completa:**
```http
POST /api/facturacion
Content-Type: application/json

{
  "query": "query GetFacturasByCliente($idCliente: ID!) { facturasByCliente(idCliente: $idCliente) { idFactura numeroFactura fechaEmision montoTotal estado } }",
  "variables": {
    "idCliente": "1"
  },
  "operationName": "GetFacturasByCliente"
}
```

### Obtener facturas por estado

**Consulta GraphQL:**
```graphql
query GetFacturasByEstado($estado: String!) {
  facturasByEstado(estado: $estado) {
    idFactura
    numeroFactura
    fechaEmision
    montoTotal
  }
}
```

**Variables:**
```json
{
  "estado": "PENDIENTE"
}
```

**Solicitud HTTP completa:**
```http
POST /api/facturacion
Content-Type: application/json

{
  "query": "query GetFacturasByEstado($estado: String!) { facturasByEstado(estado: $estado) { idFactura numeroFactura fechaEmision montoTotal } }",
  "variables": {
    "estado": "PENDIENTE"
  },
  "operationName": "GetFacturasByEstado"
}
```

### Obtener facturas por rango de fechas

**Consulta GraphQL:**
```graphql
query GetFacturasByFecha($fechaInicio: String!, $fechaFin: String!) {
  facturasByFecha(fechaInicio: $fechaInicio, fechaFin: $fechaFin) {
    idFactura
    numeroFactura
    fechaEmision
    montoTotal
    estado
  }
}
```

**Variables:**
```json
{
  "fechaInicio": "2025-04-01T00:00:00",
  "fechaFin": "2025-04-30T23:59:59"
}
```

**Solicitud HTTP completa:**
```http
POST /api/facturacion
Content-Type: application/json

{
  "query": "query GetFacturasByFecha($fechaInicio: String!, $fechaFin: String!) { facturasByFecha(fechaInicio: $fechaInicio, fechaFin: $fechaFin) { idFactura numeroFactura fechaEmision montoTotal estado } }",
  "variables": {
    "fechaInicio": "2025-04-01T00:00:00",
    "fechaFin": "2025-04-30T23:59:59"
  },
  "operationName": "GetFacturasByFecha"
}
```

### Obtener todas las facturas

**Consulta GraphQL:**
```graphql
query GetAllFacturas {
  allFacturas {
    idFactura
    numeroFactura
    idCliente
    fechaEmision
    montoTotal
    estado
  }
}
```

**Solicitud HTTP completa:**
```http
POST /api/facturacion
Content-Type: application/json

{
  "query": "query GetAllFacturas { allFacturas { idFactura numeroFactura idCliente fechaEmision montoTotal estado } }",
  "variables": {},
  "operationName": "GetAllFacturas"
}
```

## Mutaciones (Mutations)

### Crear nueva factura

**Mutación GraphQL:**
```graphql
mutation CreateFactura($factura: FacturaInput!, $detalles: [DetalleFacturaInput]) {
  createFactura(factura: $factura, detalles: $detalles) {
    idFactura
    numeroFactura
    fechaEmision
    montoSubtotal
    montoIva
    montoTotal
    estado
    detalles {
      idDetalle
      descripcion
      cantidad
      precioUnitario
      subtotal
    }
  }
}
```

**Variables:**
```json
{
  "factura": {
    "idCliente": "1",
    "numeroFactura": "F-2025-0001",
    "formaPago": "EFECTIVO",
    "observaciones": "Pago por servicios veterinarios"
  },
  "detalles": [
    {
      "descripcion": "Consulta veterinaria",
      "cantidad": 1,
      "precioUnitario": 35000
    },
    {
      "descripcion": "Vacuna antirrábica",
      "cantidad": 1,
      "precioUnitario": 25000
    }
  ]
}
```

**Solicitud HTTP completa:**
```http
POST /api/facturacion
Content-Type: application/json

{
  "query": "mutation CreateFactura($factura: FacturaInput!, $detalles: [DetalleFacturaInput]) { createFactura(factura: $factura, detalles: $detalles) { idFactura numeroFactura fechaEmision montoSubtotal montoIva montoTotal estado detalles { idDetalle descripcion cantidad precioUnitario subtotal } } }",
  "variables": {
    "factura": {
      "idCliente": "1",
      "numeroFactura": "F-2025-0001",
      "formaPago": "EFECTIVO",
      "observaciones": "Pago por servicios veterinarios"
    },
    "detalles": [
      {
        "descripcion": "Consulta veterinaria",
        "cantidad": 1,
        "precioUnitario": 35000
      },
      {
        "descripcion": "Vacuna antirrábica",
        "cantidad": 1,
        "precioUnitario": 25000
      }
    ]
  },
  "operationName": "CreateFactura"
}
```

### Actualizar estado de factura

**Mutación GraphQL:**
```graphql
mutation UpdateEstadoFactura($id: ID!, $estado: String!) {
  updateEstadoFactura(id: $id, estado: $estado) {
    idFactura
    numeroFactura
    estado
    fechaEmision
    montoTotal
  }
}
```

**Variables:**
```json
{
  "id": "1",
  "estado": "PAGADA"
}
```

**Solicitud HTTP completa:**
```http
POST /api/facturacion
Content-Type: application/json

{
  "query": "mutation UpdateEstadoFactura($id: ID!, $estado: String!) { updateEstadoFactura(id: $id, estado: $estado) { idFactura numeroFactura estado fechaEmision montoTotal } }",
  "variables": {
    "id": "1",
    "estado": "PAGADA"
  },
  "operationName": "UpdateEstadoFactura"
}
```

## Ejemplos Completos de Uso

### Ejemplo 1: Crear una nueva factura

**Consulta GraphQL:**
```graphql
mutation CreateFactura($factura: FacturaInput!, $detalles: [DetalleFacturaInput]) {
  createFactura(factura: $factura, detalles: $detalles) {
    idFactura
    numeroFactura
    fechaEmision
    montoSubtotal
    montoIva
    montoTotal
    estado
    detalles {
      idDetalle
      descripcion
      cantidad
      precioUnitario
      subtotal
    }
  }
}
```

**Variables:**
```json
{
  "factura": {
    "idCliente": "1",
    "numeroFactura": "F-2025-0001",
    "formaPago": "EFECTIVO",
    "observaciones": "Pago por servicios veterinarios"
  },
  "detalles": [
    {
      "descripcion": "Consulta veterinaria",
      "cantidad": 1,
      "precioUnitario": 35000
    },
    {
      "descripcion": "Vacuna antirrábica",
      "cantidad": 1,
      "precioUnitario": 25000
    }
  ]
}
```

**Solicitud HTTP completa:**
```http
POST /api/facturacion
Content-Type: application/json

{
  "query": "mutation CreateFactura($factura: FacturaInput!, $detalles: [DetalleFacturaInput]) { createFactura(factura: $factura, detalles: $detalles) { idFactura numeroFactura fechaEmision montoSubtotal montoIva montoTotal estado detalles { idDetalle descripcion cantidad precioUnitario subtotal } } }",
  "variables": {
    "factura": {
      "idCliente": "1",
      "numeroFactura": "F-2025-0001",
      "formaPago": "EFECTIVO",
      "observaciones": "Pago por servicios veterinarios"
    },
    "detalles": [
      {
        "descripcion": "Consulta veterinaria",
        "cantidad": 1,
        "precioUnitario": 35000
      },
      {
        "descripcion": "Vacuna antirrábica",
        "cantidad": 1,
        "precioUnitario": 25000
      }
    ]
  },
  "operationName": "CreateFactura"
}
```

**Respuesta:**
```json
{
  "data": {
    "createFactura": {
      "idFactura": "1",
      "numeroFactura": "F-2025-0001",
      "fechaEmision": "2025-04-05T15:30:22",
      "montoSubtotal": 60000,
      "montoIva": 11400,
      "montoTotal": 71400,
      "estado": "PENDIENTE",
      "detalles": [
        {
          "idDetalle": "1",
          "descripcion": "Consulta veterinaria",
          "cantidad": 1,
          "precioUnitario": 35000,
          "subtotal": 35000
        },
        {
          "idDetalle": "2",
          "descripcion": "Vacuna antirrábica",
          "cantidad": 1,
          "precioUnitario": 25000,
          "subtotal": 25000
        }
      ]
    }
  }
}
```

### Ejemplo 2: Consultar una factura con todos sus detalles

**Consulta GraphQL:**
```graphql
query GetFacturaById($id: ID!) {
  facturaById(id: $id) {
    idFactura
    numeroFactura
    idCliente
    fechaEmision
    montoSubtotal
    montoIva
    montoTotal
    estado
    formaPago
    observaciones
    detalles {
      idDetalle
      descripcion
      cantidad
      precioUnitario
      subtotal
    }
  }
}
```

**Variables:**
```json
{
  "id": "1"
}
```

**Solicitud HTTP completa:**
```http
POST /api/facturacion
Content-Type: application/json

{
  "query": "query GetFacturaById($id: ID!) { facturaById(id: $id) { idFactura numeroFactura idCliente fechaEmision montoSubtotal montoIva montoTotal estado formaPago observaciones detalles { idDetalle descripcion cantidad precioUnitario subtotal } } }",
  "variables": {
    "id": "1"
  },
  "operationName": "GetFacturaById"
}
```

**Respuesta:**
```json
{
  "data": {
    "facturaById": {
      "idFactura": "1",
      "numeroFactura": "F-2025-0001",
      "idCliente": "1",
      "fechaEmision": "2025-04-05T15:30:22",
      "montoSubtotal": 60000,
      "montoIva": 11400,
      "montoTotal": 71400,
      "estado": "PENDIENTE",
      "formaPago": "EFECTIVO",
      "observaciones": "Pago por servicios veterinarios",
      "detalles": [
        {
          "idDetalle": "1",
          "descripcion": "Consulta veterinaria",
          "cantidad": 1,
          "precioUnitario": 35000,
          "subtotal": 35000
        },
        {
          "idDetalle": "2",
          "descripcion": "Vacuna antirrábica",
          "cantidad": 1,
          "precioUnitario": 25000,
          "subtotal": 25000
        }
      ]
    }
  }
}
```

## Consideraciones Importantes

1. **Fechas**: Todas las fechas deben ser proporcionadas en formato ISO-8601 (YYYY-MM-DDThh:mm:ss).

2. **Cálculos automáticos**: Si no se proporcionan montos al crear una factura, el sistema calculará:
   - `montoSubtotal`: Suma de subtotales de todos los detalles
   - `montoIva`: 19% del subtotal
   - `montoTotal`: Subtotal + IVA

3. **Estados de factura válidos**:
   - PENDIENTE: Factura generada pero no pagada
   - PAGADA: Factura pagada completamente
   - ANULADA: Factura cancelada o anulada

4. **Formas de pago aceptadas**:
   - EFECTIVO
   - TARJETA
   - TRANSFERENCIA
   - CHEQUE

5. **Manejo de errores**: Los errores serán devueltos en el campo "errors" de la respuesta GraphQL.

## Ventajas de usar GraphQL

- **Consultas flexibles**: Puedes solicitar exactamente los campos que necesitas
- **Menos peticiones**: Puedes obtener múltiples recursos relacionados en una sola consulta
- **Tipado fuerte**: El esquema define claramente qué datos están disponibles
- **Documentación integrada**: El esquema GraphQL sirve como documentación viva de la API