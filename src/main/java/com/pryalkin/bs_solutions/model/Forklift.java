package com.pryalkin.bs_solutions.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "forklift")
@Data
public class Forklift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "capacity", nullable = false, precision = 10, scale = 3)
    private BigDecimal capacity;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "modified_user")
    private String modifiedUser;

    @OneToMany(mappedBy = "forklift", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Чтобы избежать бесконечной рекурсии, передаем простои отдельно
    private List<Downtime> downtimes;
}
