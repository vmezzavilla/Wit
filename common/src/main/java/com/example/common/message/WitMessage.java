package com.example.common.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WitMessage {
    private UUID id;
    private BigDecimal number1;
    private BigDecimal number2;
    private BigDecimal result;
}
