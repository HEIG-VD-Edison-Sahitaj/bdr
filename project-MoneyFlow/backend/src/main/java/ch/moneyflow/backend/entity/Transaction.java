package ch.moneyflow.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transaction")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    @Column(nullable = false)
    private String type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idcategory", nullable = false)
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idaccountfrom", nullable = false)
    private Account accountFrom;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idaccountto")
    private Account accountTo;
}
