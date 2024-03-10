package com.enigma.wmb_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "token")
    private String token;

    @Column(name = "redirect_url")
    private String redirectUrl;

    // ordeed, pending, settlement, cancel, deny, expire, failure
    @Column(name = "transaction_status")
    private String transactionStatus;

    @OneToOne(mappedBy = "payment")
    private Bill bill;

}
