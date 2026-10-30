package com.pirris.resource.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.pirris.dto.payment.ClientBalanceDto;
import com.pirris.dto.payment.StatementDto;
import com.pirris.dto.payment.UnpaidLessonDto;
import com.pirris.resource.PaymentResource;
import com.pirris.service.PaymentService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

/**
 * Implementation of {@link PaymentResource}. Thin: forwards to
 * {@link PaymentService}. The class-level {@code @Path} is repeated because
 * Quarkus REST registers the root resource from the concrete class.
 */
@Path("/payments")
@ApplicationScoped
public class PaymentResourceImpl implements PaymentResource {

    @Inject
    PaymentService paymentService;

    @Override
    public List<ClientBalanceDto> balances() {
        return paymentService.balances();
    }

    @Override
    public ClientBalanceDto balance(UUID clientId) {
        return paymentService.balance(clientId);
    }

    @Override
    public List<UnpaidLessonDto> unpaidLessons(UUID clientId) {
        return paymentService.unpaidLessons(clientId);
    }

    @Override
    public StatementDto statement(UUID clientId, LocalDate from, LocalDate to) {
        return paymentService.statement(clientId, from, to);
    }
}
