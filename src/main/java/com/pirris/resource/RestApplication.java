package com.pirris.resource;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import jakarta.ws.rs.core.Application;

/**
 * JAX-RS application, used here only to carry the OpenAPI metadata.
 *
 * <p>The global {@code /api} prefix is set via {@code quarkus.rest.path} rather
 * than {@code @ApplicationPath}: with JAX-RS annotations declared on resource
 * interfaces, the application-path prefix is not reliably applied, whereas the
 * Quarkus property prefixes every REST endpoint consistently and leaves the
 * {@code /q/*} management endpoints untouched.</p>
 */
@OpenAPIDefinition(info = @Info(title = "Gestionale Paola API", version = "1.0.0",
        description = "Backend REST API for the Gestionale Paola training management SaaS.",
        contact = @Contact(name = "Gestionale Paola")))
public class RestApplication extends Application {
}
