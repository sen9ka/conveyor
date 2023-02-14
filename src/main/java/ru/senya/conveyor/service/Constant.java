package ru.senya.conveyor.service;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

public class Constant {

    public static final int AGE_20 = 20;
    public static final int AGE_60 = 60;
    public static final int AGE_35 = 35;
    public static final int AGE_30 = 30;
    public static final int AGE_55 = 55;
    public static final int CREDIT_REJECTION_VALUE = -1;
    public static final double SELFEMPLOYMENT_BONUS = 0.1;
    public static final double BUSINESSOWNER_BONUS = 0.3;
    public static final double MIDDLEMANAGER_BONUS = 0.2;
    public static final double TOPMANAGER_BONUS = 0.4;
    public static final double MARRIED_BONUS = 0.3;
    public static final double DIVORCED_FINE = 0.1;
    public static final int MIN_WORKEXPERIENCE_TOTAL = 12;
    public static final int MIN_WORKEXPERIENCE_CURRENT = 3;
    public static final double MIDDLEAGE_FEMALE_BONUS = 0.3;
    public static final double MIDDLEAGE_MALE_BONUS = 0.3;

    @Value("${baseLoanRate}")
    public static BigDecimal BASE_LOAN_RATE;

    @Value("${salaryClientBonus}")
    public static BigDecimal SALARY_CLIENT_BONUS;

    @Value("${insuranceBonus}")
    public static BigDecimal INSURANCE_BONUS;

}
