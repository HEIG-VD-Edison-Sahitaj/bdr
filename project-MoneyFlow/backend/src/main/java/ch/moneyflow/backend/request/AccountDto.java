package ch.moneyflow.backend.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String name;
    private BigDecimal balance;
    private LocalDate creationDate;
}
