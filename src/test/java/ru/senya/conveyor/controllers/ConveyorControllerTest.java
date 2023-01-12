package ru.senya.conveyor.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.senya.conveyor.entity.dto.*;
import ru.senya.conveyor.entity.enums.EmploymentPosition;
import ru.senya.conveyor.entity.enums.EmploymentStatus;
import ru.senya.conveyor.entity.enums.Gender;
import ru.senya.conveyor.entity.enums.MaritalStatus;
import ru.senya.conveyor.services.ConveyorService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ConveyorControllerTest {

    LoanOfferDTO firstOffer = LoanOfferDTO.builder()
            .applicationId(null)
            .requestedAmount(BigDecimal.valueOf(50000))
            .totalAmount(BigDecimal.valueOf(59800.0))
            .term(12)
            .monthlyPayment(BigDecimal.valueOf(4983.3))
            .rate(BigDecimal.valueOf(9.6))
            .isInsuranceEnabled(true)
            .isSalaryClient(true)
            .build();

    LoanOfferDTO secondOffer = LoanOfferDTO.builder()
            .applicationId(null)
            .requestedAmount(BigDecimal.valueOf(50000))
            .totalAmount(BigDecimal.valueOf(59850.0))
            .term(12)
            .monthlyPayment(BigDecimal.valueOf(4987.5))
            .rate(BigDecimal.valueOf(9.7))
            .isInsuranceEnabled(true)
            .isSalaryClient(false)
            .build();

    LoanOfferDTO thirdOffer = LoanOfferDTO.builder()
            .applicationId(null)
            .requestedAmount(BigDecimal.valueOf(50000))
            .totalAmount(BigDecimal.valueOf(54950.0))
            .term(12)
            .monthlyPayment(BigDecimal.valueOf(4579.2))
            .rate(BigDecimal.valueOf(9.9))
            .isInsuranceEnabled(false)
            .isSalaryClient(true)
            .build();

    LoanOfferDTO fourthOffer = LoanOfferDTO.builder()
            .applicationId(null)
            .requestedAmount(BigDecimal.valueOf(50000))
            .totalAmount(BigDecimal.valueOf(55000))
            .term(12)
            .monthlyPayment(BigDecimal.valueOf(4583))
            .rate(BigDecimal.valueOf(10))
            .isInsuranceEnabled(false)
            .isSalaryClient(false)
            .build();

    public List<LoanOfferDTO> createList(LoanOfferDTO firstOffer, LoanOfferDTO secondOffer, LoanOfferDTO thirdOffer ,LoanOfferDTO fourthOffer) {
        List<LoanOfferDTO> loanOfferDTOList = new ArrayList<>();
        loanOfferDTOList.add(firstOffer);
        loanOfferDTOList.add(secondOffer);
        loanOfferDTOList.add(thirdOffer);
        loanOfferDTOList.add(fourthOffer);
        return loanOfferDTOList;
    }

    LoanApplicationRequestDTO testApplicationRequest = LoanApplicationRequestDTO.builder()
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

    @Test
    void getLoanOffers() {
        ConveyorService conveyorService = new ConveyorService();
        ConveyorController conveyorController = new ConveyorController(conveyorService);
        List<LoanOfferDTO> loanOfferDTOS = createList(firstOffer,secondOffer,thirdOffer,fourthOffer);
        ResponseEntity<?> testResponse = conveyorController.getLoanOffers(testApplicationRequest);
        ResponseEntity<?> estimatedResponse = new ResponseEntity<>(loanOfferDTOS, HttpStatus.OK);
        assertEquals(testResponse, estimatedResponse);
    }

    @Test
    void getCreditDto() {

        ScoringDataDTO testScoringDataDTO = ScoringDataDTO.builder()
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
                        .employmentId(31)
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

        PaymentScheduleElement firstElement = PaymentScheduleElement.builder()
                .number(1)
                .date(LocalDate.of(2023, 2, 12))
                .totalPayment(BigDecimal.valueOf(5995.00).setScale(2))
                .interestPayment(BigDecimal.valueOf(5395.5).setScale(4))
                .debtPayment(BigDecimal.valueOf(599.5).setScale(4))
                .remainingDebt(BigDecimal.valueOf(5995.00).setScale(2))
                .build();

        PaymentScheduleElement secondElement = PaymentScheduleElement.builder()
                .number(2)
                .date(LocalDate.of(2023, 3, 12))
                .totalPayment(BigDecimal.valueOf(5995.00).setScale(2))
                .interestPayment(BigDecimal.valueOf(5395.5).setScale(4))
                .debtPayment(BigDecimal.valueOf(599.5).setScale(4))
                .remainingDebt(BigDecimal.valueOf(0.00).setScale(2))
                .build();

        List<PaymentScheduleElement> paymentScheduleElementList = new ArrayList<>();
        paymentScheduleElementList.add(firstElement);
        paymentScheduleElementList.add(secondElement);

        CreditDTO testCreditDTO = CreditDTO.builder()
                .amount(BigDecimal.valueOf(10000).setScale(0))
                .term(2)
                .monthlyPayment(BigDecimal.valueOf(5995.00).setScale(2))
                .rate(BigDecimal.valueOf(9.0))
                .psk(BigDecimal.valueOf(11990.00).setScale(2))
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .paymentSchedule(paymentScheduleElementList)
                .build();

        ConveyorService conveyorService = new ConveyorService();
        ConveyorController conveyorController = new ConveyorController(conveyorService);
        ResponseEntity<?> testResponseEntity = conveyorController.getCreditDto(testScoringDataDTO);
        ResponseEntity<?> estimatedResponseEntity = new ResponseEntity<>(testCreditDTO, HttpStatus.OK);

        assertEquals(testResponseEntity.toString(), estimatedResponseEntity.toString());

    }

}