package com.enigma.wmb_api.entity;

import com.enigma.wmb_api.constant.ConstantTable;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = ConstantTable.BILL)
public class Bill {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "id")
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "trans_date", nullable = false, updatable = false)
    private Date transDate;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private MTable table;

    @ManyToOne
    @JoinColumn(name = "trans_type")
    private TransType transType;

    @OneToMany(mappedBy = "bill")
    @JsonManagedReference
    private List<BillDetail> billDetails;

}
