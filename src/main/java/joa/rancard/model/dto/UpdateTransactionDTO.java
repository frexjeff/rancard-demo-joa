package joa.rancard.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import joa.rancard.model.User;
import joa.rancard.model.serializer.MoneySerializer;
import lombok.*;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdateTransactionDTO {

    @NotNull(message = "Id cannot be null")
    private Long transId;

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
    private LocalDate transactionDate;
}
