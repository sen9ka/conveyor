package ru.senya.conveyor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.senya.conveyor.entity.dto.*;
import ru.senya.conveyor.entity.enums.EmploymentStatus;
import ru.senya.conveyor.entity.enums.Gender;
import ru.senya.conveyor.entity.enums.MaritalStatus;
import ru.senya.conveyor.entity.enums.EmploymentPosition;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ru.senya.conveyor.service.Constant.*;

@Service
@RequiredArgsConstructor
public class ConveyorService {

    BigDecimal baseLoanRate = new BigDecimal("10");

    BigDecimal insuranceBonus = new BigDecimal("0.3");

    BigDecimal salaryClientBonus = new BigDecimal("0.1");

    /*
    conveyor/offers
     */

    public List<LoanOfferDTO> getOfferDTOList (LoanApplicationRequestDTO loanApplicationRequestDTO) {

        List<LoanOfferDTO> offerDTOList = new ArrayList<>();

        LoanOfferDTO firstOffer = LoanOfferDTO.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();

        LoanOfferDTO secondOffer = LoanOfferDTO.builder()
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .build();

        LoanOfferDTO thirdOffer = LoanOfferDTO.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(true)
                .build();

        LoanOfferDTO fourthOffer = LoanOfferDTO.builder()
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();

        offerDTOList.add(firstOffer);
        offerDTOList.add(secondOffer);
        offerDTOList.add(thirdOffer);
        offerDTOList.add(fourthOffer);


        offerDTOList.forEach(loanOfferDTO -> loanOfferDTO.setRequestedAmount(loanApplicationRequestDTO.getAmount()));
        offerDTOList.forEach(loanOfferDTO -> loanOfferDTO.setTerm(loanApplicationRequestDTO.getTerm()));
        offerDTOList.forEach(loanOfferDTO -> loanOfferDTO.setRate(calculateOffersRate(loanOfferDTO)));
        offerDTOList.forEach(loanOfferDTO -> loanOfferDTO.setTotalAmount(calculateTotalAmount(loanOfferDTO, loanApplicationRequestDTO)));
        offerDTOList.forEach(loanOfferDTO -> loanOfferDTO.setMonthlyPayment(calculateOfferMonthlyPayment(loanOfferDTO, loanApplicationRequestDTO)));

        return offerDTOList.stream().sorted(Comparator.comparingDouble(loanOfferDto -> loanOfferDto.getRate().doubleValue())).toList();
    }

    // Рассчитать ставку
    private BigDecimal calculateOffersRate(LoanOfferDTO loanOfferDTO) {

        BigDecimal offerRate = new BigDecimal(String.valueOf(baseLoanRate));

        if (Boolean.TRUE.equals(!loanOfferDTO.getIsInsuranceEnabled()) && Boolean.TRUE.equals(!loanOfferDTO.getIsSalaryClient())) {
            return offerRate;
        } else if (Boolean.TRUE.equals(!loanOfferDTO.getIsInsuranceEnabled()) && Boolean.TRUE.equals(loanOfferDTO.getIsSalaryClient())) {
            return offerRate.subtract(salaryClientBonus);
        } else if (Boolean.TRUE.equals(loanOfferDTO.getIsInsuranceEnabled()) && Boolean.TRUE.equals(!loanOfferDTO.getIsSalaryClient())){
            return offerRate.subtract(insuranceBonus);
        } else return offerRate.subtract(salaryClientBonus).subtract(insuranceBonus);

    }

    // Рассчитать финальную сумму
    private BigDecimal calculateTotalAmount(LoanOfferDTO loanOfferDTO, LoanApplicationRequestDTO loanApplicationRequestDTO) {
        BigDecimal baseAmount;
        if (Boolean.FALSE.equals(loanOfferDTO.getIsInsuranceEnabled())) {
            baseAmount = loanApplicationRequestDTO.getAmount()
                    .add(loanApplicationRequestDTO.getAmount().divide(BigDecimal.valueOf(100),RoundingMode.HALF_UP)
                            .multiply(calculateOffersRate(loanOfferDTO)));
        } else {
            baseAmount = loanApplicationRequestDTO.getAmount()
                    .add(loanApplicationRequestDTO.getAmount().divide(BigDecimal.valueOf(100),RoundingMode.HALF_UP)
                            .multiply(calculateOffersRate(loanOfferDTO)))
                    .add(loanApplicationRequestDTO.getAmount().multiply(BigDecimal.valueOf(0.1)));
        }

        return baseAmount;
    }

    // Рассчитать месячный платеж
    private BigDecimal calculateOfferMonthlyPayment(LoanOfferDTO loanOfferDTO, LoanApplicationRequestDTO loanApplicationRequestDTO) {

        return calculateTotalAmount(loanOfferDTO, loanApplicationRequestDTO).divide(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm()),RoundingMode.HALF_UP);

    }

    /*
    conveyor/calculation
    */

    public CreditDTO getCreditDTO(ScoringDataDTO scoringDataDTO) {

        return CreditDTO.builder()
                .term(scoringDataDTO.getTerm())
                .rate(calculateCreditRate(scoringDataDTO))
                .isInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled())
                .isSalaryClient(scoringDataDTO.getIsSalaryClient())
                .amount(scoringDataDTO.getAmount())
                .psk(calculatePskPercentage(scoringDataDTO))
                .monthlyPayment(calculateMonthlyPayment(scoringDataDTO))
                .paymentSchedule(paymentScheduleElements(scoringDataDTO))
                .build();

    }

    // Скоринг ставки
    private BigDecimal calculateCreditRate(ScoringDataDTO scoringDataDTO) {

        BigDecimal loanRate = new BigDecimal(String.valueOf(baseLoanRate));

        if (scoringDataDTO.getIsInsuranceEnabled() && scoringDataDTO.getIsSalaryClient()) {
            loanRate = loanRate.subtract(insuranceBonus).subtract(salaryClientBonus);
        } else if (Boolean.TRUE.equals(scoringDataDTO.getIsInsuranceEnabled()) && Boolean.TRUE.equals(!scoringDataDTO.getIsSalaryClient())) {
            loanRate = loanRate.subtract(insuranceBonus);
        } else if (Boolean.TRUE.equals(!scoringDataDTO.getIsInsuranceEnabled()) && Boolean.TRUE.equals(scoringDataDTO.getIsSalaryClient())) {
            loanRate = loanRate.subtract(salaryClientBonus);
        }

        if (scoringDataDTO.getEmployment().getStatus().equals(EmploymentStatus.UNEMPLOYED)) {
            loanRate = BigDecimal.valueOf(CREDIT_REJECTION_VALUE);
            return loanRate;
        } else if (scoringDataDTO.getEmployment().getStatus().equals(EmploymentStatus.SELF_EMPLOYED)) {
            loanRate = loanRate.add(BigDecimal.valueOf(SELFEMPLOYMENT_BONUS));
        } else if (scoringDataDTO.getEmployment().getStatus().equals(EmploymentStatus.BUSINESS_OWNER)) {
            loanRate = loanRate.add(BigDecimal.valueOf(BUSINESSOWNER_BONUS));
        }

        if (scoringDataDTO.getEmployment().getPosition().equals(EmploymentPosition.MID_MANAGER)) {
            loanRate = loanRate.subtract(BigDecimal.valueOf(MIDDLEMANAGER_BONUS));
        } else if (scoringDataDTO.getEmployment().getPosition().equals(EmploymentPosition.TOP_MANAGER)) {
            loanRate = loanRate.subtract(BigDecimal.valueOf(TOPMANAGER_BONUS));
        }

        if (scoringDataDTO.getAmount().compareTo(scoringDataDTO.getEmployment().getSalary().multiply(BigDecimal.valueOf(20))) > 0) {
            loanRate = BigDecimal.valueOf(CREDIT_REJECTION_VALUE);
            return loanRate;
        }

        if (scoringDataDTO.getMaritalStatus().equals(MaritalStatus.MARRIED)) {
            loanRate = loanRate.subtract(BigDecimal.valueOf(MARRIED_BONUS));
        } else if (scoringDataDTO.getMaritalStatus().equals(MaritalStatus.DIVORCED)) {
            loanRate = loanRate.add(BigDecimal.valueOf(DIVORCED_FINE));
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate birthday = scoringDataDTO.getBirthdate();
        Period age = Period.between(birthday, currentDate);
        if (age.getYears() < Constant.AGE_20 || age.getYears() > AGE_60) {
            loanRate = BigDecimal.valueOf(CREDIT_REJECTION_VALUE);
            return loanRate;
        }

        if (scoringDataDTO.getGender().equals(Gender.FEMALE) && age.getYears() > AGE_35 && age.getYears() < AGE_60) {
            loanRate = loanRate.subtract(BigDecimal.valueOf(MIDDLEAGE_FEMALE_BONUS));
        } else if (scoringDataDTO.getGender().equals(Gender.MALE) && age.getYears() > AGE_30 && age.getYears() < AGE_55) {
            loanRate = loanRate.subtract(BigDecimal.valueOf(MIDDLEAGE_MALE_BONUS));
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < MIN_WORKEXPERIENCE_TOTAL) {
            loanRate = BigDecimal.valueOf(CREDIT_REJECTION_VALUE);
            return loanRate;
        } else if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < MIN_WORKEXPERIENCE_CURRENT) {
            loanRate = BigDecimal.valueOf(CREDIT_REJECTION_VALUE);
            return loanRate;
        }

        return loanRate;
    }

    // Полная стоимость кредита (Формула расчета (упрощенная) - https://mokka.ru/blog/chto-takoe-polnaya-stoimost-kredita/)
    private BigDecimal calculatePsk(ScoringDataDTO scoringDataDTO) {
        BigDecimal totalAmount = new BigDecimal(String.valueOf(scoringDataDTO.getAmount()));

        if (Boolean.TRUE.equals(scoringDataDTO.getIsInsuranceEnabled())) {
            totalAmount = totalAmount.add(scoringDataDTO.getAmount().multiply(BigDecimal.valueOf(0.1)));
        }

        BigDecimal deptAmount = new BigDecimal(String.valueOf(totalAmount.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP).multiply(calculateCreditRate(scoringDataDTO))));

        return totalAmount.add(deptAmount);
    }

    // Месячный платеж
    private BigDecimal calculateMonthlyPayment(ScoringDataDTO scoringDataDTO) {
        return calculatePsk(scoringDataDTO).divide(BigDecimal.valueOf(scoringDataDTO.getTerm()), RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePskPercentage(ScoringDataDTO scoringDataDTO) {
        MathContext mc = new MathContext(10000, RoundingMode.HALF_DOWN);

        BigDecimal pskPercentage = new BigDecimal(String.valueOf(calculatePsk(scoringDataDTO)));
        pskPercentage = pskPercentage.divide(scoringDataDTO.getAmount(), mc);
        pskPercentage = pskPercentage.subtract(BigDecimal.valueOf(1));
        pskPercentage = pskPercentage.divide(BigDecimal.valueOf(scoringDataDTO.getTerm()).divide(BigDecimal.valueOf(12), mc), mc);
        pskPercentage = pskPercentage.multiply(BigDecimal.valueOf(100));

        return pskPercentage.setScale(2, RoundingMode.HALF_UP);
    }

    private List<PaymentScheduleElement> paymentScheduleElements(ScoringDataDTO scoringDataDTO) {

        List<PaymentScheduleElement> paymentScheduleElements = new ArrayList<>();
        BigDecimal remainingDebt = new BigDecimal(String.valueOf(calculatePsk(scoringDataDTO)));

        for (int i = 1; i < scoringDataDTO.getTerm() + 1; i++) {

            PaymentScheduleElement paymentScheduleElement = PaymentScheduleElement.builder()
                    .number(i)
                    .date(LocalDate.now().plusMonths(i))
                    .totalPayment(calculateMonthlyPayment(scoringDataDTO))
                    .interestPayment(calculateMonthlyPayment(scoringDataDTO).multiply(calculateCreditRate(scoringDataDTO).multiply(BigDecimal.valueOf(0.1))))
                    .debtPayment(calculateMonthlyPayment(scoringDataDTO).subtract(calculateMonthlyPayment(scoringDataDTO).multiply(calculateCreditRate(scoringDataDTO).multiply(BigDecimal.valueOf(0.1)))))
                    .remainingDebt(remainingDebt = remainingDebt.subtract(calculateMonthlyPayment(scoringDataDTO)))
                    .build();

            paymentScheduleElements.add(paymentScheduleElement);
        }

        return paymentScheduleElements;
    }

}
