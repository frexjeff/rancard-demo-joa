package joa.rancard;

import com.fasterxml.jackson.databind.ObjectMapper;
import joa.rancard.commons.Transform;
import joa.rancard.controller.TransactionController;
import joa.rancard.exception.TransactionNotFoundException;
import joa.rancard.model.Transaction;
import joa.rancard.model.User;
import joa.rancard.model.dto.FilterTransactionDTO;
import joa.rancard.model.dto.TransactionDTO;
import joa.rancard.model.dto.UpdateTransactionDTO;
import joa.rancard.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class RancardDemoApplicationTests {

    private static final Transaction TRANSACTION = Transaction.builder()
            .transId(1L)
            .sender(User.builder()
                    .userId(2L)
                    .username("S1")
                    .build())
            .receiver(User.builder()
                    .userId(1L)
                    .username("R1")
                    .build())
            .amount(new BigDecimal("100.00"))
            .transactionDate(LocalDate.now())
            .build();

    private static final List<Transaction> TRANSACTION_LIST = List.of(Transaction.builder()
                    .transId(1L)
                    .sender(User.builder()
                            .userId(2L)
                            .username("S2")
                            .build())
                    .receiver(User.builder()
                            .userId(1L)
                            .username("R1")
                            .build())
                    .amount(new BigDecimal("100.00"))
                    .transactionDate(LocalDate.now())
                    .build(),
            Transaction.builder()
                    .transId(2L)
                    .sender(User.builder()
                            .userId(1L)
                            .username("S1")
                            .build())
                    .receiver(User.builder()
                            .userId(2L)
                            .username("R2")
                            .build())
                    .amount(new BigDecimal("140.00"))
                    .transactionDate(LocalDate.now())
                    .build());

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

//    @MockBean
//    private Validator validator; // Mock the validator bean

    @Test
    void testGetById() throws Exception {

        Mockito.when(transactionService.get(anyLong())).thenReturn(
                TRANSACTION);

        // Perform the request and verify the response
        this.mockMvc.perform(get("/api/v1/transaction/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(Transform.toDTO(TRANSACTION))));

        Mockito.when(transactionService.get(2L))
                .thenThrow(new TransactionNotFoundException(String.format("No such transaction with %s", 2)));

        this.mockMvc.perform(get("/api/v1/transaction/2"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No such transaction with 2"));

    }

    @Test
    void testGetAll() throws Exception {
        var TRANSACTION_DTO_LIST = TRANSACTION_LIST.stream().map(Transform::toDTO).collect(Collectors.toList());

        Mockito.when(transactionService.get()).thenReturn(
                TRANSACTION_LIST);

        // Perform the request and verify the response
        this.mockMvc.perform(get("/api/v1/transaction"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(TRANSACTION_DTO_LIST)));
    }

    @Test
    void testFilterIsValid() throws Exception {
        FilterTransactionDTO f = FilterTransactionDTO.builder()
                .page(1)
                .size(1)
                .build();

        Page<Transaction> page =
                new PageImpl<>(TRANSACTION_LIST.subList(f.getPage() * f.getSize(),
                        Math.min((f.getPage() + 1) * f.getSize(), TRANSACTION_LIST.size())),
                        PageRequest.of(f.getPage(), f.getSize()),
                        TRANSACTION_LIST.size());

        Mockito.when(transactionService.get(f)).thenReturn(page);

        // Perform the request and verify the response
        this.mockMvc.perform(get("/api/v1/transaction/filter")
                        .param("page", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(page.map(Transform::toDTO))));
    }

    @Test
    void checkPostForValidTransaction() throws Exception {
        var transDTO = TransactionDTO.builder()
                .sender(User.builder()
                        .userId(2L)
                        .username("S1")
                        .build())
                .receiver(User.builder()
                        .userId(1L)
                        .username("R1")
                        .build())
                .amount(new BigDecimal("100.00"))
                //.transactionDate(LocalDate.now())
                .build();

        TransactionDTO transactionDTO = Transform.toDTO(TRANSACTION);
        Mockito.when(transactionService.save(transDTO)).thenReturn(TRANSACTION);

        mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(jsonPath("$.transactionDate").value(LocalDate.now().toString()));
    }

    @Test
    void checkPostNullReceiverIsValidated() throws Exception {
        var transDTO = TransactionDTO.builder()
                .sender(User.builder()
                        .userId(2L)
                        .username("S1")
                        .build())
                .amount(new BigDecimal("100.00"))
                .transactionDate(LocalDate.now())
                .build();
        System.out.println(objectMapper.writeValueAsString(transDTO));

        mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.receiver").value("receiver is required"));
    }

    @Test
    void checkPostNullSenderIsValidated() throws Exception {
        var transDTO = TransactionDTO.builder()
                .receiver(User.builder()
                        .userId(1L)
                        .username("R1")
                        .build())
                .amount(new BigDecimal("100.00"))
                .transactionDate(LocalDate.now())
                .build();

        mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.sender").value("sender is required"));
    }

    @Test
    void checkPostTransactionDateIsValidated() throws Exception {
        var transDTO = TransactionDTO.builder()
                .sender(User.builder()
                        .userId(2L)
                        .username("S1")
                        .build())
                .receiver(User.builder()
                        .userId(1L)
                        .username("R1")
                        .build())
                .amount(new BigDecimal("100.00"))
                .transactionDate(LocalDate.parse("2025-01-01"))
                .build();

        mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.transactionDate").value("Date must be in the past or present"));
    }


    //PUT Test

    @Test
    void checkPutForValidTransaction() throws Exception {

        BigDecimal originalValue = new BigDecimal("200.00");
        BigDecimal amount = originalValue.setScale(2, RoundingMode.HALF_UP);

        var updateTransDTO = UpdateTransactionDTO.builder()
                .transId(1L)
                .sender(User.builder()
                        .userId(2L)
                        .username("S1")
                        .build())
                .receiver(User.builder()
                        .userId(1L)
                        .username("R1")
                        .build())
                .amount(amount)
                .build();

        Mockito.when(transactionService.update(any()))
                .thenReturn(TRANSACTION.withAmount(amount));

        mockMvc.perform(put("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTransDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(Transform.toDTO(TRANSACTION.withAmount(amount)))))
                .andExpect(jsonPath("$.amount").value(objectMapper.writeValueAsString(amount)));
    }

    @Test
    void checkPut_For_NullReceiverIsValidated() throws Exception {
        var updateTransDTO = UpdateTransactionDTO.builder()
                .transId(1L)
                .sender(User.builder()
                        .userId(2L)
                        .username("S1")
                        .build())
                .amount(new BigDecimal("200.00"))
                .build();
        System.out.println(objectMapper.writeValueAsString(updateTransDTO));

        mockMvc.perform(put("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTransDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.receiver").value("receiver is required"));
    }

    @Test
    void checkPut_For_NullSenderIsValidated() throws Exception {
        var updateTransDTO = UpdateTransactionDTO.builder()
                .transId(1L)
                .receiver(User.builder()
                        .userId(1L)
                        .username("R1")
                        .build())
                .amount(new BigDecimal("200.00"))
                .build();
        System.out.println(objectMapper.writeValueAsString(updateTransDTO));

        mockMvc.perform(put("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTransDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.sender").value("sender is required"));
    }

    @Test
    void checkDelete_For_Transaction() throws Exception {
        mockMvc.perform(delete("/api/v1/transaction/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
