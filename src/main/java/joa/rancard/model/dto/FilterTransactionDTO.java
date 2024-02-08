package joa.rancard.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterTransactionDTO {
    @Builder.Default
    int page = 1;
    @Builder.Default
    int size = 50;
}
