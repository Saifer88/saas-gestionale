package com.pirris.service;

import java.util.List;
import java.util.UUID;

import com.pirris.dto.lessonpackage.LessonPackageDto;
import com.pirris.dto.lessonpackage.PackagePurchaseDraftDto;
import com.pirris.mapper.LessonPackageMapper;
import com.pirris.model.ClientModel;
import com.pirris.model.LessonPackageModel;
import com.pirris.repository.ClientRepository;
import com.pirris.repository.LessonPackageRepository;
import com.pirris.resource.error.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Business logic for the Pacchetti view: list (per client), detail and
 * purchase of prepaid packages. All data access goes through tenant-aware
 * repositories; a referenced client is resolved in the current studio first.
 *
 * <p>Remaining credits are <b>derived</b> (Phase 3, {@code PackageService});
 * the mapper leaves them at neutral values. Reads are implemented; the purchase
 * is a stub.</p>
 */
@ApplicationScoped
public class LessonPackageService {

    @Inject
    LessonPackageRepository packages;

    @Inject
    ClientRepository clients;

    @Inject
    LessonPackageMapper mapper;

    /**
     * Packages of one client (404 when the client is not in the studio), or of
     * the whole studio when {@code clientId} is null.
     */
    public List<LessonPackageDto> list(UUID clientId) {
        if (clientId == null) {
            return mapper.toDtos(packages.listAllSorted());
        }
        ClientModel client = requireClient(clientId);
        return mapper.toDtos(packages.listByClient(client.id));
    }

    /** One package; 404 when it does not belong to the studio. */
    public LessonPackageDto detail(UUID id) {
        return mapper.toDto(requirePackage(id));
    }

    /** Purchases a package for a client. */
    public LessonPackageDto purchase(PackagePurchaseDraftDto draft) {
        // TODO: tenant-check clientId, persist, update client preferredPackageId if needed;
        // income is derived from the package itself (no ledger).
        throw new UnsupportedOperationException("LessonPackageService.purchase not yet implemented");
    }

    private ClientModel requireClient(UUID id) {
        return clients.findByIdForTenant(id)
                .orElseThrow(() -> NotFoundException.of("Client", id));
    }

    private LessonPackageModel requirePackage(UUID id) {
        return packages.findByIdForTenant(id)
                .orElseThrow(() -> NotFoundException.of("LessonPackage", id));
    }
}
