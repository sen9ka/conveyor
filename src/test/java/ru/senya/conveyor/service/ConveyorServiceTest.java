package ru.senya.conveyor.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.senya.conveyor.entity.dto.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = ConveyorService.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ConveyorServiceTest {

    @Autowired
    private ConveyorService conveyorService;

    @Test
    @DisplayName("Сравнить ожидаемые данные с данными, которые рассчитывает программа")
    void shouldReturnSameLoanOfferTest() {

        LoanOfferDTO actual = conveyorService.getOfferDTOList(TestData.getTestLoanApplicationRequestDTO()).get(0);
        assertEquals(TestData.getTestLoanOffer(), actual);

    }

    @Test
    @DisplayName("Сравнить ожидаемое CreditDTO с дейтсвительным")
    void shouldReturnSameCreditDTOTest() {

        ScoringDataDTO testScoringDataDTO = TestData.getTestScoringData();
        List<PaymentScheduleElement> paymentScheduleElements = TestData.getTestPaymentScheduleElementList();
        CreditDTO expected = TestData.getTestCreditDTO(paymentScheduleElements);

        CreditDTO actual = conveyorService.getCreditDTO(testScoringDataDTO);
        assertEquals(expected.toString(), actual.toString());
    }

}