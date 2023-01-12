package ru.senya.conveyor.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor
@Builder @AllArgsConstructor
public class LoanApplicationRequestDTO {

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

    @Email
    @Schema(description = "email")
    private String email;

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


}
