package ch.moneyflow.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "RecurringTransaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecurringTransaction extends Transaction {
    @Column(nullable = false)
    private String frequency;
    @Column(nullable = false, name = "begintransactiondate")
    private LocalDateTime beginTransactionDate = LocalDateTime.now();
    @Column(name = "endtransactiondate")
    private LocalDateTime endTransactionDate;
}
