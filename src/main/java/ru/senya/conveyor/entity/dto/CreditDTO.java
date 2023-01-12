package ru.senya.conveyor.entity.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditDTO {

    private BigDecimal amount;

    private Integer term;

    private BigDecimal monthlyPayment;

    private BigDecimal rate;

    private BigDecimal psk;

    private Boolean isInsuranceEnabled;

    private Boolean isSalaryClient;

    private List<PaymentScheduleElement> paymentSchedule;

    @Override
    public String toString() {
        return "CreditDTO{" +
                "amount=" + amount +
                ", term=" + term +
                ", monthlyPayment=" + monthlyPayment +
                ", rate=" + rate +
                ", psk=" + psk +
                ", isInsuranceEnabled=" + isInsuranceEnabled +
                ", isSalaryClient=" + isSalaryClient +
                ", paymentSchedule=" + paymentSchedule +
                '}';
    }
}
