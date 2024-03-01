package com.enigma.wmb_api.entity;

import com.enigma.wmb_api.constant.ConstantTable;
import com.enigma.wmb_api.constant.TransTypeEnum;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = ConstantTable.TRANS_TYPE)
public class TransType {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "id")
    private TransTypeEnum transTypeEnum;

    @Column(name = "description")
    private String description;

}

