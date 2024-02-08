package joa.rancard.controller;

import joa.rancard.commons.Transform;
import joa.rancard.model.Transaction;
import joa.rancard.model.dto.FilterTransactionDTO;
import joa.rancard.model.dto.TransactionDTO;
import joa.rancard.model.dto.UpdateTransactionDTO;
import joa.rancard.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAll() {
        return ResponseEntity.ok(transactionService.get().stream()
                .map(Transform::toDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("filter")
    public ResponseEntity<Page<TransactionDTO>> filter(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(transactionService.get(FilterTransactionDTO.builder()
                        .page(page)
                        .size(size)
                        .build())
                .map(Transform::toDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(Transform.toDTO(transactionService.get(id)));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<TransactionDTO> save(@RequestBody @Valid TransactionDTO transactionDTO) {
        log.info("creating transaction {}", transactionDTO);
        Transaction t = transactionService.save(transactionDTO);
        log.info("creating successfully {}", transactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Transform.toDTO(t));
    }

    @Transactional
    @PutMapping
    public ResponseEntity<TransactionDTO> update(@RequestBody @Valid UpdateTransactionDTO updateTransactionDTO) {
        return ResponseEntity.ok(Transform.toDTO(transactionService.update(updateTransactionDTO)));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        transactionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
