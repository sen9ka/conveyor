package ru.senya.conveyor.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.senya.conveyor.entity.enums.EmploymentStatus;
import ru.senya.conveyor.entity.enums.Position;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor
public class EmploymentDTO {

    private EmploymentStatus employmentStatus;

    private String employerINN;

    private BigDecimal salary;

    private Position position;

    private Integer workExperienceTotal;

    private Integer workExperienceCurrent;


}
