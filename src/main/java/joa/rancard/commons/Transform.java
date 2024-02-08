package joa.rancard.commons;

import joa.rancard.model.Transaction;
import joa.rancard.model.dto.TransactionDTO;
import joa.rancard.model.dto.UpdateTransactionDTO;

public class Transform {

    public static TransactionDTO toDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .sender(transaction.getSender())
                .receiver(transaction.getReceiver())
                .transactionDate(transaction.getTransactionDate())
                .amount(transaction.getAmount())
                .build();
    }

    public static Transaction fromDTO(TransactionDTO transaction) {
        return Transaction.builder()
                .sender(transaction.getSender())
                .receiver(transaction.getReceiver())
                .transactionDate(transaction.getTransactionDate())
                .amount(transaction.getAmount())
                .build();
    }

    public static Transaction fromDTO(UpdateTransactionDTO transaction) {
        return Transaction.builder()
                .transId(transaction.getTransId())
                .sender(transaction.getSender())
                .receiver(transaction.getReceiver())
                .transactionDate(transaction.getTransactionDate())
                .amount(transaction.getAmount())
                .build();
    }
}
