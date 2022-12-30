package ru.senya.conveyor.entity.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@Builder(toBuilder = true)
@AllArgsConstructor
public class LoanOfferDTO {

    private Long applicationId;

    private BigDecimal requestedAmount;

    private BigDecimal totalAmount;

    private Integer term;

    private BigDecimal monthlyPayment;

    private BigDecimal rate;

    private Boolean isInsuranceEnabled;

    private Boolean isSalaryClient;


}
