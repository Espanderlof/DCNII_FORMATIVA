package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    @FunctionName("HttpLaboratorios")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Check the HTTP method
        if (request.getHttpMethod() == HttpMethod.GET) {
            return request.createResponseBuilder(HttpStatus.OK).body("LABORATORIOS GET").build();
        } else if (request.getHttpMethod() == HttpMethod.POST) {
            // Check if the body contains a JSON
            Optional<String> body = request.getBody();
            if (body.isPresent() && !body.get().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.OK).body(body.get()).build();
            } else {
                return request.createResponseBuilder(HttpStatus.OK).body("LABORATORIOS POST").build();
            }
        } else {
            return request.createResponseBuilder(HttpStatus.METHOD_NOT_ALLOWED).body("Método no permitido").build();
        }
    }
}
