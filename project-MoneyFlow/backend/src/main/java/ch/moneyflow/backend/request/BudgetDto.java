package ch.moneyflow.backend.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class BudgetDto {
    private Long id;
    private BigDecimal maxAmount;
    private BigDecimal currentAmount;
    private LocalDate startDate;
    private LocalDate finishDate;
    private String nameCategory;
}
