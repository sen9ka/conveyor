package ru.senya.conveyor.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import ru.senya.conveyor.entity.enums.Gender;
import ru.senya.conveyor.entity.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor
@Builder @AllArgsConstructor
public class ScoringDataDTO {

    @NumberFormat
    @DecimalMin("10000")
    @Schema(description = "Запрашиваемая сумма займа")
    private BigDecimal amount;

    @Min(6)
    @Schema(description = "Срок кредита")
    private Integer term;

    @NotEmpty
    @Size(min = 2, max = 30)
    @Schema(description = "Имя")
    private String firstName;

    @NotEmpty
    @Size(min = 2, max = 30)
    @Schema(description = "Фамилия")
    private String lastName;

    @Size(min = 2, max = 30)
    @Schema(description = "Отчество")
    private String middleName;

    @NotEmpty
    @Schema(description = "Гендер: MALE | FEMALE")
    private Gender gender;

    @DateTimeFormat(pattern="yyyy-mm-dd")
    @Schema(description = "Дата рождения")
    private LocalDate birthdate;

    @NumberFormat
    @Size(min = 4, max = 4)
    @Schema(description = "Серия паспорта")
    private String passportSeries;

    @NumberFormat
    @Size(min = 6, max = 6)
    @Schema(description = "Номер паспорта")
    private String passportNumber;

    @DateTimeFormat(pattern="yyyy-mm-dd")
    @Schema(description = "Дата выдачи паспорта")
    private LocalDate passportIssueDate;

    @NotEmpty
    @Schema(description = "Отделение выдачи паспорта")
    private String passportIssueBranch;

    @NotEmpty
    @Schema(description = "Статус отношений: MARRIED | SINGLE | DIVORCED")
    private MaritalStatus maritalStatus;

    @NumberFormat
    @DecimalMin("10000")
    private BigDecimal dependentAmount;

    @NotEmpty
    @Schema(description = "Информация о работе")
    private EmploymentDTO employment;

    @NotEmpty
    @Schema(description = "Информация о работе")
    private String account;


    private Boolean isInsuranceEnabled;

    private Boolean isSalaryClient;

}
