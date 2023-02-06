package ru.senya.conveyor.entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@Builder
public class PaymentScheduleElement {

    private Integer number;

    private LocalDate date;

    private BigDecimal totalPayment;

    private BigDecimal interestPayment;

    private BigDecimal debtPayment;

    private BigDecimal remainingDebt;

    @Override
    public String toString() {
        return "PaymentScheduleElement{" +
                "number=" + number +
                ", date=" + date +
                ", totalPayment=" + totalPayment +
                ", interestPayment=" + interestPayment +
                ", debtPayment=" + debtPayment +
                ", remainingDebt=" + remainingDebt +
                '}';
    }
}
