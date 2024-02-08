package joa.rancard.service;

import joa.rancard.commons.Transform;
import joa.rancard.exception.TransactionNotFoundException;
import joa.rancard.model.Transaction;
import joa.rancard.model.dto.FilterTransactionDTO;
import joa.rancard.model.dto.TransactionDTO;
import joa.rancard.model.dto.UpdateTransactionDTO;
import joa.rancard.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    public List<Transaction> get() {
        return transactionRepository.findAll();
    }

    public Page<Transaction> get(FilterTransactionDTO filterTransactionDTO) {
        return transactionRepository.findAll(PageRequest.of(filterTransactionDTO.getPage(), filterTransactionDTO.getSize()));
    }

    public Transaction get(Long id) {
        return  transactionRepository.findById(id).orElseThrow(
                () -> new TransactionNotFoundException(String.format("No such transaction with id: %s ", id)));
    }

    public Transaction save(TransactionDTO transactionDTO) {
        Transaction transaction = Transform.fromDTO(transactionDTO);
        transaction.setSender(userService.get(transaction.getSender().getUserId()));
        transaction.setReceiver(userService.get(transaction.getReceiver().getUserId()));
        return transactionRepository.saveAndFlush(transaction);
    }

    public Transaction update(UpdateTransactionDTO updateTransactionDTO) {
        Transaction transaction = Transform.fromDTO(updateTransactionDTO);
        transaction = transactionRepository.save(transaction);
        return get(transaction.getTransId());
    }

    public void delete(Long id) {
        log.info("start of deleting transaction with id {}", id);

        var transaction = get(id);
        log.info("found transaction as DTO for delete id {}", transaction);

        transactionRepository.delete(transaction);

        log.info("successfully delete transaction {}", transaction);
    }
}
