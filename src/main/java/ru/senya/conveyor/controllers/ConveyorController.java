package ru.senya.conveyor.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.senya.conveyor.dto.*;
import ru.senya.conveyor.services.ConveyorService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/conveyor")
@Tag(name = "Кредитный конвеер", description = "расчеты условий кредита")
public class ConveyorController {

    private final ConveyorService conveyorService;

    @Autowired
    public ConveyorController(ConveyorService conveyorService) {
        this.conveyorService = conveyorService;
    }

    @PostMapping("/offers")
    @Operation(summary = "Прескоринг - 4 кредитных предложения - на основании LoanApplicationRequestDTO")
    public List<LoanOfferDTO> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return conveyorService.offerDTOList(loanApplicationRequestDTO);
    }

    @PostMapping("/calculation")
    @Operation(summary = "Скоринг данных, высчитывание ставки(rate), полной стоимости кредита(psk), размера ежемесячного платежа(monthlyPayment), графика ежемесячных платежей")
    public ResponseEntity<?> getCreditDto(@RequestBody ScoringDataDTO scoringDataDTO) {
        CreditDTO creditDTO = conveyorService.creditDTO(scoringDataDTO);
        if (creditDTO.getRate().equals(BigDecimal.valueOf(-1))) {
            return new ResponseEntity<> ("В кредите отказано", HttpStatus.NOT_ACCEPTABLE);
        } else {
            return new ResponseEntity<>(creditDTO, HttpStatus.OK);
        }
    }

}
