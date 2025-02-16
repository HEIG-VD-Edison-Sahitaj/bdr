package ch.moneyflow.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Budget")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "maxamount", nullable = false)
    private BigDecimal maxAmount;

    @Column(name="currentamount", nullable = false)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(name = "startdate", nullable = false)
    private LocalDate startDate = LocalDate.now();

    @Column(name = "finishdate")
    private LocalDate finishDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idaccount", nullable = false)
    private Account account;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idcategory", nullable = false)
    private Category category;
}
