package joa.rancard.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import joa.rancard.model.serializer.MoneySerializer;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactions",
        indexes = {
                @Index(columnList = "transactionDate")
        }
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long transId;

    @OneToOne(fetch = FetchType.EAGER)
    @With
    User sender;

    @OneToOne(fetch = FetchType.EAGER)
    @With
    User receiver;

    @With
    @JsonSerialize(using = MoneySerializer.class)
    BigDecimal amount;

    LocalDate transactionDate;
}
