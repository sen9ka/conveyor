package ru.senya.conveyor.entity.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanOfferDTO that = (LoanOfferDTO) o;
        return Objects.equals(applicationId, that.applicationId) && requestedAmount.equals(that.requestedAmount) && totalAmount.equals(that.totalAmount) && term.equals(that.term) && monthlyPayment.equals(that.monthlyPayment) && rate.equals(that.rate) && isInsuranceEnabled.equals(that.isInsuranceEnabled) && isSalaryClient.equals(that.isSalaryClient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationId, requestedAmount, totalAmount, term, monthlyPayment, rate, isInsuranceEnabled, isSalaryClient);
    }
}
