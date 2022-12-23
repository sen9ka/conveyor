package ru.senya.conveyor.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.senya.conveyor.dto.*;
import ru.senya.conveyor.services.ConveyorService;
import java.util.List;

@RestController
@RequestMapping("/conveyor")
@Tag(name = "Кредитный конвеер", description = "расчеты условий кредита")
public class ConveyorController {

    private final ObjectMapper objectMapper;
    private final ConveyorService conveyorService;

    @Autowired
    public ConveyorController(ObjectMapper objectMapper, ConveyorService conveyorService) {
        this.objectMapper = objectMapper;
        this.conveyorService = conveyorService;
    }

    @PostMapping("/offers")
    @Operation(summary = "Прескоринг - 4 кредитных предложения - на основании LoanApplicationRequestDTO")
    public List<LoanOfferDTO> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return conveyorService.offerDTOList(loanApplicationRequestDTO);
    }

    @PostMapping("/calculation")
    @Operation(summary = "Скоринг данных, высчитывание ставки(rate), полной стоимости кредита(psk), размера ежемесячного платежа(monthlyPayment), графика ежемесячных платежей")
    public CreditDTO getCreditDto(@RequestBody ScoringDataDTO scoringDataDTO) {
        return conveyorService.creditDTO(scoringDataDTO);
    }

}
