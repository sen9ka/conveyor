package ru.senya.conveyor.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.senya.conveyor.dto.*;
import ru.senya.conveyor.entity.enums.EmploymentStatus;
import ru.senya.conveyor.entity.enums.Gender;
import ru.senya.conveyor.entity.enums.MaritalStatus;
import ru.senya.conveyor.entity.enums.Position;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class ConveyorService {

    @Value("${baseLoanRate}")
    private BigDecimal baseLoanRate;

    @Value("${insuranceBonus}")
    private BigDecimal insuranceBonus;

    @Value("${salaryClientBonus}")
    private BigDecimal salaryClientBonus;

    /*
    conveyor/offers
     */

    public List<LoanOfferDTO> offerDTOList (LoanApplicationRequestDTO loanApplicationRequestDTO) {

        List<LoanOfferDTO> offerDTOList = new ArrayList<>();

        LoanOfferDTO firstOffer = new LoanOfferDTO();
        LoanOfferDTO secondOffer = new LoanOfferDTO();
        LoanOfferDTO thirdOffer = new LoanOfferDTO();
        LoanOfferDTO fourthOffer = new LoanOfferDTO();

        offerDTOList.add(firstOffer);
        offerDTOList.add(secondOffer);
        offerDTOList.add(thirdOffer);
        offerDTOList.add(fourthOffer);

        offerDTOList.forEach(loanOfferDTO -> loanOfferDTO.setApplicationId(1L));
        offerDTOList.forEach(loanOfferDTO -> loanOfferDTO.setRequestedAmount(loanApplicationRequestDTO.getAmount()));
        offerDTOList.forEach(loanOfferDTO -> loanOfferDTO.setTerm(loanApplicationRequestDTO.getTerm()));
        setInsuranceAndSalary(offerDTOList);
        offerDTOList.forEach(this::calculateOffersRate);
        offerDTOList.forEach(this::calculateTotalAmount);
        offerDTOList.forEach(this::calculateOfferMonthlyPayment);

        return offerDTOList.stream().sorted(Comparator.comparingDouble(loanOfferDto -> loanOfferDto.getRate().doubleValue())).toList();
    }

    // Установить есть ли страховка и является ли клиентом банка
    private void setInsuranceAndSalary(List<LoanOfferDTO> offerDTOList) {
        offerDTOList.get(0).setIsInsuranceEnabled(false);
        offerDTOList.get(0).setIsSalaryClient(false);

        offerDTOList.get(1).setIsInsuranceEnabled(true);
        offerDTOList.get(1).setIsSalaryClient(false);

        offerDTOList.get(2).setIsInsuranceEnabled(false);
        offerDTOList.get(2).setIsSalaryClient(true);

        offerDTOList.get(3).setIsInsuranceEnabled(true);
        offerDTOList.get(3).setIsSalaryClient(true);
    }

    // Рассчитать ставку
    private void calculateOffersRate(LoanOfferDTO loanOfferDTO) {
        if (!loanOfferDTO.getIsInsuranceEnabled() && !loanOfferDTO.getIsSalaryClient()) {
            loanOfferDTO.setRate(baseLoanRate);
        } else if (!loanOfferDTO.getIsInsuranceEnabled() && loanOfferDTO.getIsSalaryClient()) {
            loanOfferDTO.setRate(baseLoanRate.subtract(salaryClientBonus));
        } else if (loanOfferDTO.getIsInsuranceEnabled() && !loanOfferDTO.getIsSalaryClient()){
            loanOfferDTO.setRate(baseLoanRate.subtract(insuranceBonus));
        } else loanOfferDTO.setRate(baseLoanRate.subtract(salaryClientBonus).subtract(insuranceBonus));
    }

    // Рассчитать финальную сумму
    private void calculateTotalAmount(LoanOfferDTO loanOfferDTO) {
        if (!loanOfferDTO.getIsInsuranceEnabled()) {
            BigDecimal baseAmount;
            baseAmount = loanOfferDTO.getRequestedAmount()
                    .add(loanOfferDTO.getRequestedAmount().divide(BigDecimal.valueOf(100),RoundingMode.HALF_UP)
                            .multiply(loanOfferDTO.getRate()));
            loanOfferDTO.setTotalAmount(baseAmount);
        } else {
            BigDecimal baseAmount;
            baseAmount = loanOfferDTO.getRequestedAmount()
                    .add(loanOfferDTO.getRequestedAmount().divide(BigDecimal.valueOf(100),RoundingMode.HALF_UP)
                            .multiply(loanOfferDTO.getRate()))
                    .add(loanOfferDTO.getRequestedAmount().multiply(BigDecimal.valueOf(0.1)));
            loanOfferDTO.setTotalAmount(baseAmount);
        }
    }

    // Рассчитать месячный платеж
    private void calculateOfferMonthlyPayment(LoanOfferDTO loanOfferDTO) {
        loanOfferDTO.setMonthlyPayment(loanOfferDTO.getTotalAmount().divide(BigDecimal.valueOf(loanOfferDTO.getTerm()),RoundingMode.HALF_UP));
    }


    /*
    conveyor/calculation
    */


    public CreditDTO creditDTO(ScoringDataDTO scoringDataDTO) {

        CreditDTO creditDTO = new CreditDTO();
        creditDTO.setTerm(scoringDataDTO.getTerm());
        creditDTO.setRate(calculateCreditRate(scoringDataDTO));
        creditDTO.setIsInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled());
        creditDTO.setIsSalaryClient(scoringDataDTO.getIsSalaryClient());
        creditDTO.setAmount(scoringDataDTO.getAmount());
        creditDTO.setPsk(calculatePsk(creditDTO));
        creditDTO.setMonthlyPayment(calculateMonthlyPayment(creditDTO));
        creditDTO.setPaymentSchedule(paymentScheduleElements(creditDTO));

        return creditDTO;
    }

    // Скоринг ставки
    private BigDecimal calculateCreditRate(ScoringDataDTO scoringDataDTO) {

        BigDecimal loanRate = new BigDecimal(String.valueOf(baseLoanRate));

        if (scoringDataDTO.getIsInsuranceEnabled() && scoringDataDTO.getIsInsuranceEnabled()) {
            loanRate = loanRate.subtract(insuranceBonus).subtract(salaryClientBonus);
        } else if (scoringDataDTO.getIsInsuranceEnabled() && !scoringDataDTO.getIsSalaryClient()) {
            loanRate = loanRate.subtract(insuranceBonus);
        } else if (!scoringDataDTO.getIsInsuranceEnabled() && scoringDataDTO.getIsSalaryClient()) {
            loanRate = loanRate.subtract(salaryClientBonus);
        }

        if (scoringDataDTO.getEmployment().getEmploymentStatus().equals(EmploymentStatus.UNEMPLOYED)) {
            loanRate = BigDecimal.valueOf(-1);
            return loanRate;
        } else if (scoringDataDTO.getEmployment().getEmploymentStatus().equals(EmploymentStatus.SELFEMPLOYED)) {
            loanRate = loanRate.add(BigDecimal.valueOf(0.1));
        } else if (scoringDataDTO.getEmployment().getEmploymentStatus().equals(EmploymentStatus.BUSINESSOWNER)) {
            loanRate = loanRate.add(BigDecimal.valueOf(0.3));
        }

        if (scoringDataDTO.getEmployment().getPosition().equals(Position.MIDDLEMAANAGER)) {
            loanRate = loanRate.subtract(BigDecimal.valueOf(0.2));
        } else if (scoringDataDTO.getEmployment().getPosition().equals(Position.TOPMANAGER)) {
            loanRate = loanRate.subtract(BigDecimal.valueOf(0.4));
        }

        if (scoringDataDTO.getAmount().compareTo(scoringDataDTO.getEmployment().getSalary().multiply(BigDecimal.valueOf(20))) > 0) {
            loanRate = BigDecimal.valueOf(-1);
            return loanRate;
        }

        if (scoringDataDTO.getMaritalStatus().equals(MaritalStatus.MARRIED)) {
            loanRate = loanRate.subtract(BigDecimal.valueOf(0.3));
        } else if (scoringDataDTO.getMaritalStatus().equals(MaritalStatus.DIVORCED)) {
            loanRate = loanRate.add(BigDecimal.valueOf(0.1));
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate birthday = scoringDataDTO.getBirthdate();
        Period age = Period.between(birthday, currentDate);
        if (age.getYears() < 20 || age.getYears() > 60) {
            loanRate = BigDecimal.valueOf(-1);
            return loanRate;
        }

        if (scoringDataDTO.getGender().equals(Gender.FEMALE) && age.getYears() > 35 && age.getYears() < 60) {
            loanRate = loanRate.subtract(BigDecimal.valueOf(0.3));
        } else if (scoringDataDTO.getGender().equals(Gender.MALE) && age.getYears() > 30 && age.getYears() < 55) {
            loanRate = loanRate.subtract(BigDecimal.valueOf(0.3));
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12) {
            loanRate = BigDecimal.valueOf(-1);
            return loanRate;
        } else if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3) {
            loanRate = BigDecimal.valueOf(-1);
            return loanRate;
        }

        return loanRate;
    }

    // Полная стоимость кредита
    private BigDecimal calculatePsk(CreditDTO creditDTO) {
        BigDecimal totalAmount = new BigDecimal(String.valueOf(creditDTO.getAmount()));

        if (creditDTO.getIsInsuranceEnabled()) {
            totalAmount = totalAmount.add(creditDTO.getAmount().multiply(BigDecimal.valueOf(0.1)));
        }

        BigDecimal deptAmount = new BigDecimal(String.valueOf(totalAmount.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP).multiply(creditDTO.getRate())));

        return totalAmount.add(deptAmount);
    }

    // Месячный платеж
    private BigDecimal calculateMonthlyPayment(CreditDTO creditDTO) {
        return creditDTO.getPsk().divide(BigDecimal.valueOf(creditDTO.getTerm()), RoundingMode.HALF_UP);
    }

    public List<PaymentScheduleElement> paymentScheduleElements(CreditDTO creditDTO) {

        List<PaymentScheduleElement> paymentScheduleElements = new ArrayList<>();
        BigDecimal remainingDebt = new BigDecimal(String.valueOf(creditDTO.getPsk()));

        for (int i = 1; i < creditDTO.getTerm() + 1; i++) {
            PaymentScheduleElement paymentScheduleElement = new PaymentScheduleElement();
            paymentScheduleElement.setNumber(i);
            paymentScheduleElement.setDate(LocalDate.now().plusMonths(i));
            paymentScheduleElement.setTotalPayment(calculateMonthlyPayment(creditDTO));
            paymentScheduleElement.setInterestPayment(paymentScheduleElement.getTotalPayment().multiply(creditDTO.getRate().multiply(BigDecimal.valueOf(0.1))));
            paymentScheduleElement.setDebtPayment(paymentScheduleElement.getTotalPayment().subtract(paymentScheduleElement.getInterestPayment()));
            remainingDebt = remainingDebt.subtract(paymentScheduleElement.getTotalPayment());
            paymentScheduleElement.setRemainingDebt(remainingDebt);
            paymentScheduleElements.add(paymentScheduleElement);
        }

        return paymentScheduleElements;
    }


}
