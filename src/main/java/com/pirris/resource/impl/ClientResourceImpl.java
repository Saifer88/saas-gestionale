package com.pirris.resource.impl;

import java.util.List;
import java.util.UUID;

import com.pirris.dto.ClientDraftDto;
import com.pirris.dto.client.ClientDetailDto;
import com.pirris.dto.client.ClientSummaryDto;
import com.pirris.resource.ClientResource;
import com.pirris.service.ClientService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Implementation of {@link ClientResource}. Thin: validates/forwards to
 * {@link ClientService}.
 *
 * <p>Quarkus REST registers a root resource from the concrete class, so the
 * class-level {@code @Path} is repeated here; role rules live on the
 * interface.</p>
 */
@Path("/clients")
@ApplicationScoped
public class ClientResourceImpl implements ClientResource {

    @Inject
    ClientService clientService;

    @Override
    public List<ClientSummaryDto> list(String q) {
        return clientService.search(q);
    }

    @Override
    public ClientDetailDto detail(UUID id) {
        return clientService.detail(id);
    }

    @Override
    public Response create(ClientDraftDto draft) {
        ClientDetailDto created = clientService.create(draft);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @Override
    public ClientDetailDto update(UUID id, ClientDraftDto draft) {
        return clientService.update(id, draft);
    }

    @Override
    public Response archive(UUID id) {
        clientService.archive(id);
        return Response.noContent().build();
    }

    @Override
    public Response restore(UUID id) {
        clientService.unarchive(id);
        return Response.noContent().build();
    }
}
