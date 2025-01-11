package com.example.common.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WitMessage {
    @JsonView(View.Kafka.class)
    private UUID id;
    @JsonView(View.Kafka.class)
    private Number number1;
    @JsonView(View.Kafka.class)
    private Number number2;
    @JsonView(View.Rest.class)
    private Number result;
}
