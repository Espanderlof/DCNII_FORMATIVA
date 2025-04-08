package com.function.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import com.function.graphql.FacturaDataFetcher;
import com.function.graphql.GraphQLSchemaProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.function.util.GsonConfig;

import java.util.Map;
import java.util.Optional;

public class FacturacionGraphQLFunction {

    private final Gson gson = GsonConfig.getGson();
    private final GraphQL graphQL;

    public FacturacionGraphQLFunction() {
        FacturaDataFetcher dataFetcher = new FacturaDataFetcher();
        GraphQLSchemaProvider schemaProvider = new GraphQLSchemaProvider(dataFetcher);
        graphQL = GraphQL.newGraphQL(schemaProvider.getSchema()).build();
    }

    @FunctionName("HttpFacturacion")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", 
                      methods = {HttpMethod.POST}, 
                      authLevel = AuthorizationLevel.ANONYMOUS,
                      route = "facturacion") 
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        
        context.getLogger().info("Procesando solicitud GraphQL para Facturación");
        
        try {
            // Verificar si hay cuerpo en la solicitud
            if (!request.getBody().isPresent() || request.getBody().get().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Solicitud inválida", "El cuerpo de la solicitud está vacío"))
                        .build();
            }
            
            // Parsear la solicitud GraphQL
            String requestBody = request.getBody().get();
            JsonObject jsonRequest = JsonParser.parseString(requestBody).getAsJsonObject();
            
            if (!jsonRequest.has("query")) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Solicitud inválida", "Se requiere el campo 'query' en la solicitud GraphQL"))
                        .build();
            }
            
            String query = jsonRequest.get("query").getAsString();
            
            // Procesar variables si están presentes
            Map<String, Object> variables = null;
            if (jsonRequest.has("variables") && !jsonRequest.get("variables").isJsonNull()) {
                variables = gson.fromJson(jsonRequest.get("variables"), Map.class);
            }
            
            // Procesar nombre de operación si está presente
            String operationName = null;
            if (jsonRequest.has("operationName") && !jsonRequest.get("operationName").isJsonNull()) {
                operationName = jsonRequest.get("operationName").getAsString();
            }
            
            // Ejecutar la consulta GraphQL
            ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                    .query(query)
                    .variables(variables)
                    .operationName(operationName)
                    .build();
            
            ExecutionResult executionResult = graphQL.execute(executionInput);
            
            // Construir la respuesta
            Map<String, Object> result = executionResult.toSpecification();
            
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(result))
                    .build();
            
        } catch (Exception e) {
            context.getLogger().severe("Error en la función GraphQL: " + e.getMessage());
            e.printStackTrace();
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Crea una respuesta de error en formato JSON
     */
    private String createErrorResponse(String error, String mensaje) {
        JsonObject response = new JsonObject();
        response.addProperty("error", error);
        response.addProperty("mensaje", mensaje);
        return gson.toJson(response);
    }
}