package ch.moneyflow.backend.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDateTime date;
    private String type;
    private String nameCategory;
    private String nameAccountFrom;
    private String nameAccountTo;
}
