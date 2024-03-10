package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.payment.PaymentDetailRequest;
import com.enigma.wmb_api.dto.request.payment.PaymentItemDetailRequest;
import com.enigma.wmb_api.dto.request.payment.PaymentRequest;
import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.entity.Payment;
import com.enigma.wmb_api.repositry.PaymentRepository;
import com.enigma.wmb_api.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final RestClient restClient;
    private final String SECRET_KEY;
    private final String BASE_URL_SNAP;

    @Autowired
    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            RestClient restClient,
            @Value("${midtrans.api.key}") String secretKey,
            @Value("${midtrans.api.snap-url}") String baseUrlSnap
    ) {
        this.paymentRepository = paymentRepository;
        this.restClient = restClient;
        SECRET_KEY = secretKey;
        BASE_URL_SNAP = baseUrlSnap;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Payment createPayment(Bill bill) {
        // Count amount for Payment
        long amount = bill.getBillDetails().stream()
                .mapToInt(billDetail -> (billDetail.getQty()) * billDetail.getMenu().getPrice())
                .reduce(0, Integer::sum);

        // Item Detail request for Payment
        List<PaymentItemDetailRequest> paymentItemDetailRequests = bill.getBillDetails().stream()
                .map(billDetail -> PaymentItemDetailRequest.builder()
                        .name(billDetail.getMenu().getName())
                        .price(billDetail.getMenu().getPrice())
                        .quantity(billDetail.getQty())
                        .build()
                ).toList();

        // Payment request
        PaymentRequest paymentRequest = PaymentRequest.builder()
                // Payment Detail request
                .paymentDetail(PaymentDetailRequest.builder()
                        .orderId(bill.getId())
                        .amount(amount)  // Amount Payment
                        .build())

                // Payment Item Detail request
                .paymentItemDetails(paymentItemDetailRequests)
                .paymentMethod(
                        List.of("shopeepay", "gopay")
                )
                .build();

        // Response Entity from Use restClient Midtrans (post) -> payment bill
        ResponseEntity<Map<String, String>> response = restClient.post()
                .uri(BASE_URL_SNAP)
                .body(paymentRequest)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + SECRET_KEY)
                // Use retrieve to async and toEntity to change response HTTP to entity
                .retrieve().toEntity(new ParameterizedTypeReference<>() {});

        // Get Body Response for Payment
        Map<String, String> body = response.getBody();

        // Create Payment
        Payment payment = Payment.builder()
                .token(body.get("token"))
                .redirectUrl(body.get("redirect_url"))
                .transactionStatus("ordered")
                .build();

        // Save to Repository
        paymentRepository.saveAndFlush(payment);

        return payment;
    }
}
