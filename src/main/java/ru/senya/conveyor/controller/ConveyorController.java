package ru.senya.conveyor.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senya.conveyor.entity.dto.CreditDTO;
import ru.senya.conveyor.entity.dto.LoanApplicationRequestDTO;
import ru.senya.conveyor.entity.dto.LoanOfferDTO;
import ru.senya.conveyor.entity.dto.ScoringDataDTO;
import ru.senya.conveyor.service.ConveyorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conveyor")
@Tag(name = "Кредитный конвеер", description = "расчеты условий кредита")
public class ConveyorController {

    private final ConveyorService conveyorService;

    @PostMapping("/offers")
    @Operation(summary = "Прескоринг - 4 кредитных предложения - на основании LoanApplicationRequestDTO")
    public ResponseEntity<Object> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        List<LoanOfferDTO> offerDTOList = conveyorService.getOfferDTOList(loanApplicationRequestDTO);
        return new ResponseEntity<>(offerDTOList, HttpStatus.OK);
    }

    @PostMapping("/calculation")
    @Operation(summary = "Скоринг данных, высчитывание ставки(rate), полной стоимости кредита(psk), размера ежемесячного платежа(monthlyPayment), графика ежемесячных платежей")
    public ResponseEntity<Object> getCreditDto(@RequestBody ScoringDataDTO scoringDataDTO) {
        CreditDTO creditDTO = conveyorService.getCreditDTO(scoringDataDTO);
        return new ResponseEntity<>(creditDTO, HttpStatus.OK);
    }

}
