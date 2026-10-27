package com.pirris.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.pirris.dto.ClientDraftDto;
import com.pirris.dto.client.ClientDetailDto;
import com.pirris.dto.client.ClientSummaryDto;
import com.pirris.mapper.ClientMapper;
import com.pirris.model.ClientModel;
import com.pirris.repository.ClientRepository;
import com.pirris.resource.error.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Business logic for the Clienti view: list, search, detail, save (create or
 * update) and archive. All data access goes through {@link ClientRepository},
 * so every operation is confined to the current studio. Entity/DTO conversion
 * is delegated to the compile-time {@link ClientMapper}.
 *
 * <p>The client's {@code balanceCents} and activity history are <b>derived</b>
 * from sessions, packages and expenses. That accounting logic is owned by a
 * dedicated collaborator (Phase 3, {@code AccountService}); the mapper leaves
 * those fields at their neutral values so the Clienti context stays
 * self-contained and does not grow premature accounting queries.</p>
 */
@ApplicationScoped
public class ClientService {

    @Inject
    ClientRepository clients;

    @Inject
    ClientMapper mapper;

    /** Active (non-archived) clients of the current studio, ordered by name. */
    public List<ClientSummaryDto> listActive() {
        return mapper.toSummaries(clients.listActive());
    }

    /** Case-insensitive search over name, phone and email within the studio. */
    public List<ClientSummaryDto> search(String term) {
        if (term == null || term.isBlank()) {
            return listActive();
        }
        return mapper.toSummaries(clients.search(term));
    }

    /** Full profile of one client; 404 when it does not belong to the studio. */
    public ClientDetailDto detail(UUID id) {
        return mapper.toDetail(requireClient(id));
    }

    /** Creates a new client from the draft and returns its full profile. */
    @Transactional
    public ClientDetailDto create(ClientDraftDto draft) {
        ClientModel client = new ClientModel();
        mapper.applyDraft(client, draft);
        client.joinedOn = draft.joinedOn() != null ? draft.joinedOn() : LocalDateTime.now();
        clients.persistForTenant(client);
        return mapper.toDetail(client);
    }

    /**
     * Updates an existing client from the draft. Scheduling preferences are not
     * touched: they are a snapshot maintained by the session flow, not editable
     * from the client form (the mapper ignores them). 404 when not in the studio.
     */
    @Transactional
    public ClientDetailDto update(UUID id, ClientDraftDto draft) {
        ClientModel client = requireClient(id);
        mapper.applyDraft(client, draft);
        if (draft.joinedOn() != null) {
            client.joinedOn = draft.joinedOn();
        }
        return mapper.toDetail(client);
    }

    /** Archives a client (soft delete); 404 when not in the studio. */
    @Transactional
    public void archive(UUID id) {
        requireClient(id).archived = true;
    }

    /** Restores a previously archived client; 404 when not in the studio. */
    @Transactional
    public void unarchive(UUID id) {
        requireClient(id).archived = false;
    }

    private ClientModel requireClient(UUID id) {
        return clients.findByIdForTenant(id)
                .orElseThrow(() -> NotFoundException.of("Client", id));
    }
}
