package joa.rancard.model.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import joa.rancard.model.User;
import joa.rancard.model.serializer.MoneySerializer;
import lombok.*;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TransactionDTO {

    @NotNull(message = "sender is required")
    @With
    private User sender;

    @NotNull(message = "receiver is required")
    @With
    private User receiver;

    @DecimalMin(value = "0.01", message = "Amount must be greater than or equal to 0.01")
    @With
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal amount;

    @PastOrPresent(message = "Date must be in the past or present")
    @Builder.Default
    private LocalDate transactionDate = LocalDate.now();
}
