package com.pirris.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.pirris.dto.payment.ClientBalanceDto;
import com.pirris.dto.payment.StatementDto;
import com.pirris.dto.payment.UnpaidLessonDto;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Builds the data for the Pagamenti screen: client balances, unpaid lessons and
 * statements.
 *
 * <p><b>Stub.</b> Everything here is <b>derived</b> accounting (no ledger):
 * charges and payments are computed from completed sessions, their
 * participants and package purchases. That logic belongs to the Phase 3
 * {@code AccountService}.</p>
 */
@ApplicationScoped
public class PaymentService {

    /** Balances of the studio's clients. */
    public List<ClientBalanceDto> balances() {
        // TODO(Phase 3): AccountService — derive charges/payments per client.
        throw new UnsupportedOperationException("PaymentService.balances not yet implemented");
    }

    /** Balance of one client. */
    public ClientBalanceDto balance(UUID clientId) {
        // TODO(Phase 3): AccountService — tenant-check the client, derive its balance.
        throw new UnsupportedOperationException("PaymentService.balance not yet implemented");
    }

    /** Completed lessons of one client not paid and not covered by a package. */
    public List<UnpaidLessonDto> unpaidLessons(UUID clientId) {
        // TODO(Phase 3): AccountService — tenant-check the client, derive unpaid participations.
        throw new UnsupportedOperationException("PaymentService.unpaidLessons not yet implemented");
    }

    /** Statement of one client over {@code [from, to]}; either bound may be null. */
    public StatementDto statement(UUID clientId, LocalDate from, LocalDate to) {
        // TODO(Phase 3): AccountService — tenant-check the client, derive opening/closing balance and entries.
        throw new UnsupportedOperationException("PaymentService.statement not yet implemented");
    }
}
