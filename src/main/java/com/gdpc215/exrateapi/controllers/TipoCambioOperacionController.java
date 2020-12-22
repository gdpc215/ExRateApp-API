package com.gdpc215.exrateapi.controllers;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gdpc215.exrateapi.objects.TipoCambio;
import com.gdpc215.exrateapi.objects.TipoCambioOperacion;
import com.gdpc215.exrateapi.repositories.TipoCambioOperacionRepository;
import com.gdpc215.exrateapi.repositories.TipoCambioRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class TipoCambioOperacionController {
    private TipoCambioRepository tcRepository;
    private TipoCambioOperacionRepository tcoRepository;

    @Autowired
    public TipoCambioOperacionController(TipoCambioRepository tcRepository, TipoCambioOperacionRepository tcoRepository) {
        this.tcRepository = tcRepository;
        this.tcoRepository = tcoRepository;
    }

    @GetMapping("/operacion/historia")
    Flux<TipoCambioOperacion> getHistoriaSolicitudesTC() {
        return tcoRepository.findAll();
    }
    
    @GetMapping("/operacion/cambio")
    Mono<TipoCambioOperacion> getTipoCambio(
        @RequestParam("monto") BigDecimal monto,
    	@RequestParam("monedaOrigen") String monedaOrigen,
    	@RequestParam("monedaDestino") String monedaDestino
    ) {
    	return tcRepository
    		.getTipoCambioMoneda(monedaOrigen)
    		.zipWith(tcRepository.getTipoCambioMoneda(monedaDestino))
    		.log()
    		.flatMap(tc -> {
    			TipoCambio tcOrigen = tc.getT1();
    			TipoCambio tcDestino = tc.getT2();
    			
    			BigDecimal montoFinal = monto
    					.divide(tcDestino.getCambio(), MathContext.DECIMAL32)
    					.multiply(tcOrigen.getCambio(), MathContext.DECIMAL32)
    					.setScale(4, RoundingMode.HALF_UP);
    			
    			BigDecimal tcUsado = tcDestino.getCambio()
    					.divide(tcOrigen.getCambio(), MathContext.DECIMAL32)
    					.setScale(4, RoundingMode.HALF_UP);
    			
    			TipoCambioOperacion op = new TipoCambioOperacion(monto, montoFinal, monedaOrigen, monedaDestino, tcUsado);
    			return tcoRepository.save(op);
    		});
    }
    
    @PostMapping("/operacion/cambio")
    Mono<TipoCambioOperacion> getTipoCambio(@RequestBody TipoCambioOperacion tco) {
    	BigDecimal monto = tco.getMonto();
    	String monedaOrigen = tco.getMonedaOrigen();
    	String monedaDestino = tco.getMonedaDestino();
    	
    	return tcRepository
    		.getTipoCambioMoneda(monedaOrigen)
    		.zipWith(tcRepository.getTipoCambioMoneda(monedaDestino))
    		.log()
    		.flatMap(tc -> {
    			TipoCambio tcOrigen = tc.getT1();
    			TipoCambio tcDestino = tc.getT2();
    			
    			BigDecimal montoFinal = monto
    					.divide(tcDestino.getCambio(), MathContext.DECIMAL32)
    					.multiply(tcOrigen.getCambio(), MathContext.DECIMAL32)
    					.setScale(4, RoundingMode.HALF_UP);
    			
    			BigDecimal tcUsado = tcDestino.getCambio()
    					.divide(tcOrigen.getCambio(), MathContext.DECIMAL32)
    					.setScale(4, RoundingMode.HALF_UP);
    			
    			TipoCambioOperacion op = new TipoCambioOperacion(monto, montoFinal, monedaOrigen, monedaDestino, tcUsado);
    			return tcoRepository.save(op);
    		});
    }
}
