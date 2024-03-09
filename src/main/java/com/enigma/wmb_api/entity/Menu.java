package com.enigma.wmb_api.entity;

import com.enigma.wmb_api.constant.ConstantTable;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = ConstantTable.MENU)
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false, columnDefinition = "INT CHECK (price >= 0)" )
    private Integer price;

    @OneToOne
    @JoinColumn(name = "image_id", unique = true)
    private Image image;

}
