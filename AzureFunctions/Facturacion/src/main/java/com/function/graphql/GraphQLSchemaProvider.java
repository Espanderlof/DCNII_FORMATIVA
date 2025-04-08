package com.function.graphql;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

public class GraphQLSchemaProvider {
    
    private final FacturaDataFetcher facturaDataFetcher;
    
    public GraphQLSchemaProvider(FacturaDataFetcher facturaDataFetcher) {
        this.facturaDataFetcher = facturaDataFetcher;
    }
    
    public GraphQLSchema getSchema() {
        // Cargar el archivo de esquema GraphQL
        TypeDefinitionRegistry typeRegistry = loadSchemaDefinition();
        
        // Configurar el cableado del esquema
        RuntimeWiring runtimeWiring = buildRuntimeWiring();
        
        // Generar el esquema
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }
    
    private TypeDefinitionRegistry loadSchemaDefinition() {
        SchemaParser schemaParser = new SchemaParser();
        try (InputStream is = GraphQLSchemaProvider.class.getResourceAsStream("/schema.graphqls");
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            
            return schemaParser.parse(reader);
        } catch (Exception e) {
            // Si no se puede cargar desde el recurso, intentar cargar desde la cadena
            return schemaParser.parse(getSchemaDefinition());
        }
    }
    
    private String getSchemaDefinition() {
        return "type Query {\n" +
               "  facturaById(id: ID!): Factura\n" +
               "  facturasByCliente(idCliente: ID!): [Factura]\n" +
               "  facturasByEstado(estado: String!): [Factura]\n" +
               "  facturasByFecha(fechaInicio: String!, fechaFin: String!): [Factura]\n" +
               "  allFacturas: [Factura]\n" +
               "}\n" +
               "\n" +
               "type Mutation {\n" +
               "  createFactura(factura: FacturaInput!, detalles: [DetalleFacturaInput]): Factura\n" +
               "  updateEstadoFactura(id: ID!, estado: String!): Factura\n" +
               "}\n" +
               "\n" +
               "type Factura {\n" +
               "  idFactura: ID!\n" +
               "  numeroFactura: String\n" +
               "  idCliente: ID!\n" +
               "  fechaEmision: String\n" +
               "  montoSubtotal: Float\n" +
               "  montoIva: Float\n" +
               "  montoTotal: Float\n" +
               "  estado: String\n" +
               "  formaPago: String\n" +
               "  observaciones: String\n" +
               "  detalles: [DetalleFactura]\n" +
               "}\n" +
               "\n" +
               "type DetalleFactura {\n" +
               "  idDetalle: ID!\n" +
               "  idFactura: ID!\n" +
               "  idCita: ID\n" +
               "  idProducto: ID\n" +
               "  descripcion: String\n" +
               "  cantidad: Int\n" +
               "  precioUnitario: Float\n" +
               "  subtotal: Float\n" +
               "}\n" +
               "\n" +
               "input FacturaInput {\n" +
               "  idCliente: ID!\n" +
               "  numeroFactura: String\n" +
               "  fechaEmision: String\n" +
               "  montoSubtotal: Float\n" +
               "  montoIva: Float\n" +
               "  montoTotal: Float\n" +
               "  estado: String\n" +
               "  formaPago: String\n" +
               "  observaciones: String\n" +
               "}\n" +
               "\n" +
               "input DetalleFacturaInput {\n" +
               "  idCita: ID\n" +
               "  idProducto: ID\n" +
               "  descripcion: String!\n" +
               "  cantidad: Int!\n" +
               "  precioUnitario: Float!\n" +
               "  subtotal: Float\n" +
               "}";
    }
    
    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query")
                        .dataFetcher("facturaById", facturaDataFetcher.getFacturaPorId())
                        .dataFetcher("facturasByCliente", facturaDataFetcher.getFacturasPorCliente())
                        .dataFetcher("facturasByEstado", facturaDataFetcher.getFacturasPorEstado())
                        .dataFetcher("facturasByFecha", facturaDataFetcher.getFacturasPorFecha())
                        .dataFetcher("allFacturas", facturaDataFetcher.getAllFacturas()))
                .type(newTypeWiring("Mutation")
                        .dataFetcher("createFactura", facturaDataFetcher.createFactura())
                        .dataFetcher("updateEstadoFactura", facturaDataFetcher.updateEstadoFactura()))
                .type(newTypeWiring("Factura")
                        .dataFetcher("detalles", facturaDataFetcher.getDetallesPorFactura()))
                .build();
    }
}