package ru.senya.conveyor.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.senya.conveyor.entity.dto.*;
import ru.senya.conveyor.entity.enums.EmploymentPosition;
import ru.senya.conveyor.entity.enums.EmploymentStatus;
import ru.senya.conveyor.entity.enums.Gender;
import ru.senya.conveyor.entity.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TestData {

    public static LoanOfferDTO getTestLoanOffer() {
        return LoanOfferDTO.builder()
                .applicationId(null)
                .requestedAmount(BigDecimal.valueOf(50000))
                .totalAmount(BigDecimal.valueOf(59800.0))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(4983.3))
                .rate(BigDecimal.valueOf(9.6))
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();
    }

    public static LoanApplicationRequestDTO getTestLoanApplicationRequestDTO() {
        return LoanApplicationRequestDTO.builder()
                .amount(BigDecimal.valueOf(50000))
                .term(12)
                .firstName("Arseniy")
                .lastName("Shvets")
                .middleName("Konstantinovich")
                .email("arsshvets@gmail.com")
                .birthdate(LocalDate.of(1999, 3, 22))
                .passportSeries(String.valueOf(1414))
                .passportNumber(String.valueOf(142141))
                .build();
    }

    public static ScoringDataDTO getTestScoringData() {

        return ScoringDataDTO.builder()
                .amount(BigDecimal.valueOf(10000))
                .term(2)
                .firstName("Senya")
                .lastName("Shvets")
                .middleName("Kostya")
                .gender(Gender.MALE)
                .birthdate(LocalDate.of(1999, 3, 22))
                .passportSeries(String.valueOf(1424))
                .passportNumber(String.valueOf(194500))
                .passportIssueDate(LocalDate.of(2024, 3, 22))
                .passportIssueBranch("Spb")
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(BigDecimal.valueOf(12))
                .employment(EmploymentDTO.builder()
                        .status(EmploymentStatus.SELF_EMPLOYED)
                        .employerINN(String.valueOf(15116161))
                        .salary(BigDecimal.valueOf(400000))
                        .position(EmploymentPosition.TOP_MANAGER)
                        .workExperienceTotal(500)
                        .workExperienceCurrent(200)
                        .build())
                .account("account")
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();
    }

    public static List<PaymentScheduleElement> getTestPaymentScheduleElementList() {

        PaymentScheduleElement firstElement = PaymentScheduleElement.builder()
                .number(1)
                .date(LocalDate.now().plusMonths(1))
                .totalPayment(BigDecimal.valueOf(5995.00).setScale(2))
                .interestPayment(BigDecimal.valueOf(5395.5).setScale(4))
                .debtPayment(BigDecimal.valueOf(599.5).setScale(4))
                .remainingDebt(BigDecimal.valueOf(5995.00).setScale(2))
                .build();

        PaymentScheduleElement secondElement = PaymentScheduleElement.builder()
                .number(2)
                .date(LocalDate.now().plusMonths(2))
                .totalPayment(BigDecimal.valueOf(5995.00).setScale(2))
                .interestPayment(BigDecimal.valueOf(5395.5).setScale(4))
                .debtPayment(BigDecimal.valueOf(599.5).setScale(4))
                .remainingDebt(BigDecimal.valueOf(0.00).setScale(2))
                .build();

        List<PaymentScheduleElement> paymentScheduleElements = new ArrayList<>();

        paymentScheduleElements.add(firstElement);
        paymentScheduleElements.add(secondElement);

        return paymentScheduleElements;

    }

    public static CreditDTO getTestCreditDTO(List<PaymentScheduleElement> paymentScheduleElements) {

        return CreditDTO.builder()
                .amount(BigDecimal.valueOf(10000).setScale(0))
                .term(2)
                .monthlyPayment(BigDecimal.valueOf(5995.00).setScale(2))
                .rate(BigDecimal.valueOf(9.0))
                .psk(BigDecimal.valueOf(119.40).setScale(2))
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .paymentSchedule(paymentScheduleElements)
                .build();

    }

}
