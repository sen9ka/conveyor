package ru.senya.conveyor.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;
import ru.senya.conveyor.entity.enums.EmploymentStatus;
import ru.senya.conveyor.entity.enums.Position;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor
public class EmploymentDTO {

    @NotEmpty
    @Schema(description = "Рабочий статус: SELFEMPLOYED | UNEMPLOYED | BUSINESSOWNER")
    private EmploymentStatus employmentStatus;

    @NotEmpty
    @Size(min = 9, max = 9)
    @Schema(description = "ИНН работника")
    private String employerINN;

    @NumberFormat
    @Schema(description = "Зарплата")
    private BigDecimal salary;

    @NotEmpty
    @Schema(description = "Позиция на работе: MIDDLEMANAGER, TOPMANAGER")
    private Position position;

    @NumberFormat
    @Schema(description = "Общий стаж работы")
    private Integer workExperienceTotal;

    @NumberFormat
    @Schema(description = "Настоящий стаж работы")
    private Integer workExperienceCurrent;


}
