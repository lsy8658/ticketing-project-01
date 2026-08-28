package com.ticket.concert.Controller;


import com.ticket.concert.dto.PaymentRequest;
import com.ticket.concert.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(@RequestBody PaymentRequest request) {
        paymentService.confirm(request);
        return ResponseEntity.ok().build();
    }
}
