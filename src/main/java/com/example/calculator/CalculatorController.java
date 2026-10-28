package com.example.calculator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiEndpoints.BASE)
public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping(ApiEndpoints.ADD)
    public CalculationResponse add(@RequestParam double a, @RequestParam double b) {
        return new CalculationResponse("add", a, b, calculatorService.add(a, b));
    }

    @GetMapping(ApiEndpoints.SUBTRACT)
    public CalculationResponse subtract(@RequestParam double a, @RequestParam double b) {
        return new CalculationResponse("subtract", a, b, calculatorService.subtract(a, b));
    }

    @GetMapping(ApiEndpoints.MULTIPLY)
    public CalculationResponse multiply(@RequestParam double a, @RequestParam double b) {
        return new CalculationResponse("multiply", a, b, calculatorService.multiply(a, b));
    }

    @GetMapping(ApiEndpoints.DIVIDE)
    public CalculationResponse divide(@RequestParam double a, @RequestParam double b) {
        return new CalculationResponse("divide", a, b, calculatorService.divide(a, b));
    }
}
