package com.ticket.concert.Controller;


import com.ticket.concert.domain.Payment;
import com.ticket.concert.dto.PaymentRequest;
import com.ticket.concert.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;


    @GetMapping
    public ResponseEntity<List<Payment>> getMyPayments(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        List<Payment> payments = paymentService.getMyPayments(userId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(
            Authentication authentication,
            @Valid @RequestBody PaymentRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        paymentService.confirm(request, userId);
        return ResponseEntity.ok().build();
    }
}
