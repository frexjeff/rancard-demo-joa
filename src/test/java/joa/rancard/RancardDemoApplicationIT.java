package joa.rancard;

import joa.rancard.commons.Transform;
import joa.rancard.exception.TransactionNotFoundException;
import joa.rancard.model.Transaction;
import joa.rancard.model.User;
import joa.rancard.model.dto.UpdateTransactionDTO;
import joa.rancard.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class RancardDemoApplicationIT {

    private static final Transaction TRANSACTION = Transaction.builder()
            .transId(1L)
            .sender(User.builder()
                    .userId(2L)
                    //.username("User 2")
                    .build())
            .receiver(User.builder()
                    .userId(1L)
                    //.username("User 1")
                    .build())
            .amount(new BigDecimal("100.00"))
            .transactionDate(LocalDate.now())
            .build();

    @Autowired
    TransactionService transactionService;

    @Test
    void testTransactionMethods() {
        Transaction t = transactionService.save(Transform.toDTO(TRANSACTION));
        assertEquals(TRANSACTION, t);

        List<Transaction> t_list = transactionService.get();
        assertEquals(List.of(TRANSACTION), t_list);

        t = transactionService.get(1L);
        assertEquals(TRANSACTION, t);

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.get(2L);
        });
        var updateTransDTO = UpdateTransactionDTO.builder()
                .transId(1L)
                .sender(User.builder()
                        .userId(2L)
                        .build())
                .receiver(User.builder()
                        .userId(1L)
                        .build())
                .amount(new BigDecimal("200"))
                .transactionDate(LocalDate.now())
                .build();

        t = transactionService.update(updateTransDTO);
        assertEquals(TRANSACTION.withAmount(new BigDecimal("200")), t);

        transactionService.delete(1L);

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.delete(2L);
        });

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.delete(1L);
        });
    }
}
