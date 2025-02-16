package ch.moneyflow.backend.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RecurringTransactionDto {
    private Integer id;
    private BigDecimal amount;
    private String description;
    private LocalDateTime date;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime beginTransactionDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTransactionDate;
    private String type;
    private String frequency;
    private String nameCategory;
    private String debitedAccount;
    private String creditedAccount;
}
